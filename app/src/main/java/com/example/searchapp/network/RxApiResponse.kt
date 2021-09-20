package com.example.searchapp.network

import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * Created by  vanikjain on 19/09/21
 */

sealed class RxApiResponse<T> {
  var isApiCallSuccess: Boolean = true

  companion object {
    /**
     * This method is used in case of api error response.
     * A base error response object is created, which will contain the publish subject and throwable.
     */
    fun <T> create(error: Throwable?, publishSubject: PublishSubject<Int>): RxApiResponse<T> {
      val apiResponse: RxApiResponse<T> = RxApiErrorResponse(
        error, publishSubject
      )
      apiResponse.isApiCallSuccess = false
      return apiResponse
    }

    /**
     * This method is used in case of success response.
     */
    fun <T> create(body: T): RxApiResponse<T> {
      val apiResponse: RxApiResponse<T> = RxApiSuccessResponse(
        body
      )
      apiResponse.isApiCallSuccess = true
      return apiResponse
    }
  }
}

data class RxApiSuccessResponse<T>(val body: T) : RxApiResponse<T>()

data class RxApiErrorResponse<T>(val baseErrorResponse: RxBaseErrorResponse<T>) :
  RxApiResponse<T>() {
  constructor(
    throwable: Throwable?, publishSubject: PublishSubject<Int>
  ) : this( //Convert base error response object from throwable and publish subject
    baseErrorResponse = RxBaseErrorResponse(throwable, publishSubject)
  )
}