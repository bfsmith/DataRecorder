package com.github.bfsmith.recorder

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.bfsmith.recorder.util.MyListAdapter
import com.github.bfsmith.recorder.util.TemplateRenderer
import com.github.bfsmith.recorder.util.toString
import org.jetbrains.anko.*

class TagViewActivity : AppCompatActivity(), AnkoLogger {
    lateinit var records: List<Record>
    lateinit var tag: Tag

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val tagId = intent.extras.getInt("TagId")
        if (tagId == 0) {
            warn("No TagId extra given, ending TagViewActivity.")
            finish()
            return
        }

        val findTag = database.getTag(tagId)

        if (findTag == null) {
            val knownIds = database.tags.map { it.id.toString() }.fold("") { acc, s -> "$acc, $s" }
            warn("Couldn't find a tag with id $tagId in ($knownIds), ending TagViewActivity.")
            finish()
            return
        }

        tag = findTag
        records = database.getRecordsForTag(tagId)

        TagViewActivityUi().setContentView(this)
    }
}

class TagViewActivityUi : AnkoComponent<TagViewActivity> {
    private val dateFormat = "yyyy-MM-dd HH:mm:ss"

    override fun createView(ui: AnkoContext<TagViewActivity>): View = with(ui) {
        val listAdapter = MyListAdapter<Record>(owner.records, { it.id }) {
            record ->
            TemplateRenderer(owner) {
                with(it) {
                    linearLayout {
                        textView {
                            text = record.date.toString(dateFormat)
                            textSize = 12f
                        }

                        textView {
                            text = record.value.toString()
                            textSize = 12f
                            horizontalPadding = dip(16)
                        }
                    }
                }
            }
        }

        verticalLayout {
            padding = dip(8)

            textView {
                text = "Records for ${owner.tag.tag}"
                textSize = 16f
            }

            listView {
                adapter = listAdapter
            }.lparams {
                margin = dip(8)
                width = matchParent
                height = matchParent
            }
        }
    }
}
