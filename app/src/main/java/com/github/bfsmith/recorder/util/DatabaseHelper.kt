package com.github.bfsmith.recorder.util

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.github.bfsmith.recorder.RecordTableName
import com.github.bfsmith.recorder.TagTableName
import org.jetbrains.anko.db.*

class DatabaseHelper(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "DataRecorderDb", null, 1) {
    companion object {
        private var instance: DatabaseHelper? = null

        @Synchronized
        fun getInstance(ctx: Context): DatabaseHelper {
            if (instance == null) {
                instance = DatabaseHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable(TagTableName, true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "tag" to TEXT)
        db.createTable(RecordTableName, true,
                "id" to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                "tagId" to INTEGER,
                "date" to INTEGER,
                "value" to REAL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        db?.dropTable("User", true)
    }
}
