package com.example.searchapp.network

import com.example.searchapp.model.SearchResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

/**
 * Created by  vanikjain on 19/09/21
 */

interface ISearchApi {
  @GET("/") fun searchApi(@QueryMap params: HashMap<String, String>): Single<SearchResponse>
}