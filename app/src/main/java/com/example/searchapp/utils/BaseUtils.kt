package com.example.searchapp.utils

import com.example.searchapp.controller.BaseApplication

/**
 * Created by  vanikjain on 19/09/21
 */

object BaseUtils {
  val Int.dp: Int
    get() {
      return BaseApplication.instance.applicationContext.resources.displayMetrics.density.toInt() * this
    }
}