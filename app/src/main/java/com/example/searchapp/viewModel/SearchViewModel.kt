package com.example.searchapp.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.searchapp.BuildConfig
import com.example.searchapp.model.SearchResponse
import com.example.searchapp.network.RxApiResponse
import com.example.searchapp.repository.SearchRepository

/**
 * Created by  vanikjain on 19/09/21
 */

class SearchViewModel(private val searchRepository: SearchRepository) : ViewModel() {
  var mCurrentPage = 1
  var searchTerm = "Naruto"
  var totalResults = 0

  fun searchApiCall(): LiveData<RxApiResponse<SearchResponse>> {
    val queryParameters = HashMap<String, String>()
    queryParameters["apikey"] = BuildConfig.SearchApiKey
    queryParameters["s"] = searchTerm
    queryParameters["page"] = mCurrentPage.toString()
    return searchRepository.searchApi(queryParameters)
  }

  fun cancelAllApiCalls() {
    searchRepository.cancelAllApiCalls()
  }

  override fun onCleared() {
    cancelAllApiCalls()
    super.onCleared()
  }
}