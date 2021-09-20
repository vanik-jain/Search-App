package com.example.searchapp.network

import io.reactivex.rxjava3.subjects.PublishSubject

/**
 * Created by  vanikjain on 19/09/21
 */

class RxBaseErrorResponse<T>(val throwable: Throwable?, val publishSubject: PublishSubject<Int>)