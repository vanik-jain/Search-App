package com.example.searchapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.searchapp.model.SearchResponse
import com.example.searchapp.network.ISearchApi
import com.example.searchapp.network.RxApiResponse
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.subjects.PublishSubject
import io.reactivex.rxjava3.functions.Function
import io.reactivex.rxjava3.schedulers.Schedulers

/**
 * Created by  vanikjain on 19/09/21
 */

class SearchRepository(private val iSearchApi: ISearchApi) {
  private val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

  fun searchApi(
    params: HashMap<String, String>
  ): LiveData<RxApiResponse<SearchResponse>> {
    val liveData = MutableLiveData<RxApiResponse<SearchResponse>>()
    mCompositeDisposable.add(
      iSearchApi.searchApi(
        params
      ).subscribeOn(
        Schedulers.io()
      ).observeOn(AndroidSchedulers.mainThread()).retryWhen(retryFunctionRxJava3(liveData))
        .subscribe({
          liveData.value = RxApiResponse.create(it)
        }, { // No Implementation required
        })
    )
    return liveData
  }

  private fun retryFunctionRxJava3(
    liveData: MutableLiveData<RxApiResponse<SearchResponse>>
  ): Function<Flowable<out Throwable>, Flowable<*>> {
    return Function { observable ->
      observable.flatMap {
        val subject = PublishSubject.create<Int>()
        liveData.postValue(RxApiResponse.create(it, subject))
        subject.toFlowable(BackpressureStrategy.LATEST)
      }
    }
  }

  fun cancelAllApiCalls() {
    mCompositeDisposable.clear()
  }
}