package com.github.bfsmith.recorder

import java.util.*

class DataService {
    private val _tags: MutableList<Tag> by lazy {
        mutableListOf(Tag(1, "Gas"), Tag(2, "HighTemp"), Tag(3, "Calories"))
    }
    val tags: List<Tag> get() = _tags

    fun getRecordsForTag(id: Int): List<Record> {
        val random = Random()
        val dateRange = (Date().time - (8.64e7 * 3).toLong()).rangeTo(Date().time)
        return dateRange.map { Record(id, Date(it), random.nextDouble() * 5) }
    }
}