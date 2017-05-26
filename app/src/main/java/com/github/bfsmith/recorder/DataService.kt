package com.github.bfsmith.recorder

import android.content.Context
import com.github.bfsmith.recorder.util.DatabaseHelper
import org.jetbrains.anko.db.*
import java.util.*

val TagTableName = "tags"
val TagColumns = arrayOf("id", "tag")
val TagParser = rowParser {
    id: Int, tag: String ->
    Tag(id, tag)
}
val RecordTableName = "records"
val RecordColumns = arrayOf("id", "tagId", "date", "value")
val RecordParser = rowParser {
    id: Int, tagId: Int, date: Long, value: Double ->
    Record(id, tagId, Date(date), value)
}

class DataService private constructor(val databaseHelper: DatabaseHelper) {
    companion object {
        private var instance: DataService? = null

        @Synchronized
        fun getInstance(ctx: Context): DataService {
            if (instance == null) {
                instance = DataService(DatabaseHelper.getInstance(ctx.applicationContext))
            }
            return instance!!
        }
    }

    private val _tags: MutableList<Tag> by lazy {
        val tags = databaseHelper.use {
            select(TagTableName, *TagColumns)
                    .parseList(TagParser)
        }
        tags.toMutableList()
    }
    val tags: List<Tag> get() = _tags

    fun getTag(id: Int): Tag? = tags.find { it.id == id }
    fun addTag(tag: String): Tag {
        val existingTag = tags.find { it.tag == tag }
        if (existingTag == null) {
            val newId = databaseHelper.use {
                insert(TagTableName,
                        "tag" to tag
                )
            }
            val newTag = Tag(newId.toInt(), tag)
            _tags.add(newTag)
            return newTag
        }
        return existingTag
    }

    fun removeTag(id: Int): DataService {
        databaseHelper.use {
            delete(TagTableName, "id = {id}", "id" to id)
        }
        _tags.removeIf { it.id == id }
        return this
    }

    fun getRecordsForTag(tagId: Int): List<Record> {
        val records = databaseHelper.use {
            select(RecordTableName, *RecordColumns)
                    .whereArgs("tagId = {tagId}", "tagId" to tagId)
                    .parseList(RecordParser)
        }
        return records
    }

    fun addRecordForTag(tagId: Int, value: Double): Record {
        val now = Date()
        val id = databaseHelper.use {
            insert(RecordTableName,
                    "tagId" to tagId,
                    "date" to now.time,
                    "value" to value)
        }
        return Record(id.toInt(), tagId, now, value)
    }

    fun removeRecord(id: Int, tagId: Int): DataService {
        databaseHelper.use {
            delete(TagTableName,
                    "id = {id} and tagId = {tagId}",
                    "id" to id,
                    "tagId" to tagId)
        }
        return this
    }
}

val Context.database: DataService
    get() = DataService.getInstance(applicationContext)
