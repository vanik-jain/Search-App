package com.example.searchapp.viewModel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.searchapp.repository.SearchRepository

/**
 * Created by  vanikjain on 19/09/21
 */

class SearchViewModelFactory(private val searchRepository: SearchRepository)  : ViewModelProvider.Factory {
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return SearchViewModel(searchRepository) as T
  }

}