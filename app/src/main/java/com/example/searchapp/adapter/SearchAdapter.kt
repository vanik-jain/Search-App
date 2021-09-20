package com.example.searchapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.searchapp.R
import com.example.searchapp.databinding.LoadMoreItemBinding
import com.example.searchapp.databinding.SearchGridItemBinding
import com.example.searchapp.databinding.SearchListItemBinding
import com.example.searchapp.model.SearchItem
import com.example.searchapp.utils.ImageLoader

/**
 * Created by  vanikjain on 19/09/21
 */

class SearchAdapter(
   val searchItems: ArrayList<SearchItem>, private val isGrid: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  var isLoadMore = false

  companion object {
    private const val GRID = 1
    private const val LIST = 2
    private const val LOAD_MORE = 3
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
    val layoutInflater = LayoutInflater.from(parent.context)
    return when (viewType) {
      GRID -> {
        SearchGridViewHolder(
          SearchGridItemBinding.inflate(
            layoutInflater, parent, false
          )
        )
      }
      LIST -> {
        SearchListViewHolder(
          SearchListItemBinding.inflate(
            layoutInflater, parent, false
          )
        )
      }

      LOAD_MORE -> {
        LoadMoreViewHolder(
          LoadMoreItemBinding.inflate(
            layoutInflater, parent, false
          )
        )
      }
      else -> {
        SearchListViewHolder(
          SearchListItemBinding.inflate(
            layoutInflater, parent, false
          )
        )
      }
    }
  }

  override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
    when (holder) {
      is SearchGridViewHolder -> {
        searchItems.getOrNull(position)?.let {
          holder.bind(it)
        }
      }
      is SearchListViewHolder -> {
        searchItems.getOrNull(position)?.let {
          holder.bind(it)
        }
      }
      is LoadMoreViewHolder -> {
        holder.bind()
      }
    }
  }

  override fun getItemCount(): Int = searchItems.size + 1

  override fun getItemViewType(position: Int): Int = when {
    position == searchItems.size -> {
      LOAD_MORE
    }
    isGrid -> {
      GRID
    }
    else -> {
      LIST
    }
  }

  inner class SearchGridViewHolder(private val searchGridItemBinding: SearchGridItemBinding) :
    RecyclerView.ViewHolder(searchGridItemBinding.root) {
    fun bind(searchItem: SearchItem) {
      with(searchGridItemBinding) {
        tvMovieTitle.text = searchItem.title.orEmpty()
        tvMovieDesc.text = searchItem.imdbID.orEmpty()
        tvReleaseDate.text = searchItem.year.orEmpty()
        tvRating.text = searchItem.rating.toString()
        ContextCompat.getDrawable(ivMovie.context, R.drawable.ic_search)?.let {
          ImageLoader.loadImageWithImpl(searchItem.poster.orEmpty(), it, ivMovie)
        }
      }
    }
  }

  inner class SearchListViewHolder(private val searchListItemBinding: SearchListItemBinding) :
    RecyclerView.ViewHolder(searchListItemBinding.root) {
    fun bind(searchItem: SearchItem) {
      with(searchListItemBinding) {
        tvMovieTitle.text = searchItem.title.orEmpty()
        tvMovieDesc.text = searchItem.imdbID.orEmpty()
        tvReleaseDate.text = searchItem.year.orEmpty()
        tvRating.text = searchItem.rating.toString()
        ContextCompat.getDrawable(ivMovie.context, R.drawable.ic_search)?.let {
          ImageLoader.loadImageWithImpl(searchItem.poster.orEmpty(), it, ivMovie)
        }
      }
    }
  }

  inner class LoadMoreViewHolder(private val loadMoreItemBinding: LoadMoreItemBinding) :
    RecyclerView.ViewHolder(loadMoreItemBinding.root) {
    fun bind() {
      loadMoreItemBinding.cpbLoadMore.visibility = if (isLoadMore) {
        View.VISIBLE
      } else {
        View.GONE
      }
    }
  }
}