package com.example.searchapp.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by  vanikjain on 19/09/21
 */
class SearchItemDecorator(private val mItemOffset: Int) : RecyclerView.ItemDecoration() {

  override fun getItemOffsets(
    outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
  ) {
    outRect.left = mItemOffset
    outRect.right = mItemOffset
    outRect.bottom = mItemOffset
    outRect.top = mItemOffset
  }
}