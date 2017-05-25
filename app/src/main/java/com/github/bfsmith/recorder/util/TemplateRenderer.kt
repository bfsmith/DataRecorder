package com.github.bfsmith.recorder.util

import android.content.Context
import android.view.View
import android.view.ViewManager
import android.widget.RelativeLayout
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.AnkoViewDslMarker
import org.jetbrains.anko.custom.ankoView

class TemplateRenderer(context: Context?, render: (AnkoContext<*>) -> View) : RelativeLayout(context) {
    init {
        render(AnkoContext.createDelegate(this))
    }
}

inline fun ViewManager.render(view: android.view.View): android.view.View {
    return ankoView({ ctx: Context -> view }, theme = 0) {}
}
inline fun ViewManager.render(view: android.view.View, init: (@AnkoViewDslMarker android.view.View).() -> Unit ): android.view.View {
    return ankoView({ ctx: Context -> view }, theme = 0) {
        init()
    }
}
