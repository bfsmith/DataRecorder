package com.github.bfsmith.recorder.util

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import org.jetbrains.anko.sdk25.coroutines.onFocusChange

fun View.showKeyboardOnFocus(ctx: Context) {
    this.onFocusChange { v, hasFocus ->
        val inputMethodManager = ctx.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (hasFocus) {
            inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
        } else {
            inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}
