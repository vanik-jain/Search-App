package com.example.searchapp.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize data class SearchResponse(
  @field:SerializedName("Response") val response: String? = null,
  @field:SerializedName("totalResults") val totalResults: String? = null,
  @field:SerializedName("Search") val searchItem: List<SearchItem>? = null
) : Parcelable
