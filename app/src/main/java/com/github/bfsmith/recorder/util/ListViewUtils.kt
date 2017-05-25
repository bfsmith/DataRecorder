package com.github.bfsmith.recorder.util

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class MyListAdapter<T>(val items: List<T>, val getId: (item: T) -> Int, val createView: (item: T) -> View) : BaseAdapter() {
    override fun getView(i: Int, v: View?, parent: ViewGroup?): View {
        val item = getItem(i)
        return with(parent!!.context) {
            createView(item)
        }
    }

    override fun getItem(position: Int): T {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return getId(getItem(position)).toLong()
    }
}