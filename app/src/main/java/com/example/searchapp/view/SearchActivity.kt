package com.example.searchapp.view

import android.content.Context
import android.os.Bundle
import android.os.IBinder
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.searchapp.R
import com.example.searchapp.adapter.SearchAdapter
import com.example.searchapp.adapter.SearchItemDecorator
import com.example.searchapp.controller.BaseApplication
import com.example.searchapp.databinding.ActivitySearchBinding
import com.example.searchapp.model.SearchItem
import com.example.searchapp.model.SearchResponse
import com.example.searchapp.network.ISearchApi
import com.example.searchapp.network.RxApiSuccessResponse
import com.example.searchapp.repository.SearchRepository
import com.example.searchapp.utils.BaseUtils.dp
import com.example.searchapp.viewModel.SearchViewModel
import com.example.searchapp.viewModel.SearchViewModelFactory
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by  vanikjain on 19/09/21
 */

class SearchActivity : AppCompatActivity() {

  private var searchItems: ArrayList<SearchItem>? = null
  private lateinit var searchAdapter: SearchAdapter
  private lateinit var mBinder: ActivitySearchBinding
  private var searchItemDecorator: SearchItemDecorator? = null
  private var isGridView = true
  private val searchViewModel: SearchViewModel by viewModels {
    SearchViewModelFactory(
      SearchRepository(
        BaseApplication.instance.getRetrofitObject().create(ISearchApi::class.java)
      )
    )
  }
  private var isLoadMoreCallInProgress = false

  companion object {
    private const val LOAD_MORE_THRESHOLD = 9
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mBinder = ActivitySearchBinding.inflate(layoutInflater)
    setContentView(mBinder.root)
    initToolBar()
    initViews()
  }

  private fun initToolBar() {
    with(mBinder.tbSearch) {
      inflateMenu(R.menu.search_menu)
      val gridOrListMenu = menu?.findItem(R.id.action_grid_or_list)
      setOnMenuItemClickListener {
        if (it.itemId == R.id.action_grid_or_list) {
          isGridView = !isGridView
          gridOrListMenu?.icon = ContextCompat.getDrawable(
            this@SearchActivity, if (isGridView) R.drawable.ic_grid else R.drawable.ic_list
          )
          initRecyclerView()
          invalidateOptionsMenu()
          true
        } else {
          false
        }
      }
    }
  }

  private fun initViews() {
    with(mBinder) {
      tbSearch.setNavigationOnClickListener { onBackPressed() }
      initSearchBox()
    }
  }

  private fun initSearchBox() {
    with(mBinder.etSearchBox) {
      if (searchViewModel.searchTerm.isNotEmpty()) {
        onSearchQuery()
      }
      setOnEditorActionListener { _, actionId, event ->
        if (event != null && event.keyCode == KeyEvent.ACTION_DOWN || actionId == EditorInfo.IME_ACTION_SEARCH) {
          searchViewModel.searchTerm = text.toString().trim()
          onSearchQuery()
        }
        return@setOnEditorActionListener true
      }
    }
  }

  private fun onSearchQuery() {
    hideSoftKeyboard(currentFocus?.windowToken)
    searchViewModel.cancelAllApiCalls()
    searchItems?.clear()
    searchViewModel.mCurrentPage = 1
    showProgressBar()
    fetchSearchData()
  }

  private fun fetchSearchData() {
    searchViewModel.searchApiCall().observe(this, {
      showProgressBar(false)
      showHideLoadMoreProgressBar()
      if (it.isApiCallSuccess) {
        val searchResponse = (it as RxApiSuccessResponse<SearchResponse>).body
        if (searchResponse.response == "True") {
          searchViewModel.totalResults = searchResponse.totalResults?.toInt().orDefaultInt()
          if (searchItems.isNullOrEmpty()) {
            if (searchResponse.searchItem.isNullOrEmpty()) {
              showErrorState()
            } else {
              searchViewModel.mCurrentPage += 1
              showErrorState(false)
              searchItems = searchResponse.searchItem as ArrayList<SearchItem>
              initRecyclerView()
            }
          } else {
            val currentSize = searchItems!!.size
            searchResponse.searchItem?.let { mSearchItems ->
              searchItems!!.addAll(mSearchItems)
              searchAdapter.searchItems.addAll(mSearchItems)
              searchAdapter.notifyItemRangeChanged(currentSize, searchItems!!.size)
            }
          }
        } else {
          showErrorState()
        }
      } else {
        showErrorState()
      }
    })
  }

  private fun initRecyclerView() {
    with(mBinder.rvSearchItems) {
      layoutManager = getListViewGridLayoutManager()
      searchItemDecorator?.let { removeItemDecoration(it) }
      searchItems?.let {
        searchAdapter = SearchAdapter(it, isGridView)
        mBinder.rvSearchItems.adapter = searchAdapter
        searchItemDecorator = SearchItemDecorator(4.dp)
        addItemDecoration(searchItemDecorator!!)
        initScrollListener()
      }
    }
  }

  private fun hideSoftKeyboard(windowToken: IBinder?) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
  }

  private fun getListViewGridLayoutManager(): StaggeredGridLayoutManager {
    val maxSpanCount = if (isGridView) 2
    else 1
    return StaggeredGridLayoutManager(maxSpanCount, StaggeredGridLayoutManager.VERTICAL)
  }

  private fun initScrollListener() {
    mBinder.rvSearchItems.addOnScrollListener(object : RecyclerView.OnScrollListener() {
      override fun onScrolled(recyclerView: RecyclerView, xCoordinate: Int, yCoordinate: Int) {
        super.onScrolled(recyclerView, xCoordinate, yCoordinate)
        val lastVisiblePosition =
          (mBinder.rvSearchItems.layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(
            null
          )
        val mCurrentVisibleItemPosition = lastVisiblePosition.maxOrNull() ?: -1
        if ((mCurrentVisibleItemPosition > (mBinder.rvSearchItems.layoutManager as StaggeredGridLayoutManager).itemCount - LOAD_MORE_THRESHOLD) && (!isLoadMoreCallInProgress) && (!searchItems.isNullOrEmpty() && searchItems!!.size < searchViewModel.totalResults)) {
          showHideLoadMoreProgressBar(true)
          fetchSearchData()
        }
      }
    })
  }

  private fun showHideLoadMoreProgressBar(isVisible: Boolean = false) {
    isLoadMoreCallInProgress = isVisible
    if (::searchAdapter.isInitialized) {
      searchAdapter.isLoadMore = isVisible
      searchItems?.size?.let {
        searchAdapter.notifyItemChanged(it)
      }
    }
  }

  private fun showErrorState(isShow: Boolean = true) {
    with(mBinder) {
      if (isShow) {
        rvSearchItems.visibility = View.GONE
        llEmptyState.root.visibility = View.VISIBLE
      } else {
        rvSearchItems.visibility = View.VISIBLE
        llEmptyState.root.visibility = View.GONE
      }
    }
  }

  private fun showProgressBar(isVisible: Boolean = true) {
    if (isVisible) {
      mBinder.pbCustom.visibility = View.VISIBLE
      window?.setFlags(
        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
      )
    } else {
      mBinder.pbCustom.visibility = View.INVISIBLE
      window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }
  }

  private fun Int?.orDefaultInt(defaultInt: Int? = null) = this ?: defaultInt ?: 0
}