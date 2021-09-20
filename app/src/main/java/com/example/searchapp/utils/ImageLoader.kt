package com.example.searchapp.utils

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.RequestOptions

/**
 * Created by  vanikjain on 19/09/21
 */

class ImageLoader {
  companion object {
    fun loadImageWithImpl(
      imageUrl: String, placeholder: Drawable, imageView: ImageView
    ) {
      val options = RequestOptions().dontTransform().diskCacheStrategy(DiskCacheStrategy.DATA)
        .placeholder(placeholder)
      if (null != imageView.context && !TextUtils.isEmpty(imageUrl)) {
        Glide.with(imageView.context).load(GlideUrl(imageUrl)).apply(options).into(imageView)
      }
    }
  }
}