package com.github.bfsmith.recorder

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType.*
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ListAdapter
import com.github.bfsmith.recorder.util.MyListAdapter
import com.github.bfsmith.recorder.util.TemplateRenderer
import com.github.bfsmith.recorder.util.showKeyboardOnFocus
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.sdk25.coroutines.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainActivityUi().setContentView(this)
    }

    fun tagSelected(ui: AnkoContext<MainActivity>, tag: Tag) {
        ui.doAsync {
            activityUiThreadWithContext {
                toast("You selected tag ${tag.tag}.")
                startActivity<TagViewActivity>("TagId" to tag.id)
            }
        }
    }
}

class MainActivityUi : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {
        fun addValue(tagId: Int, value: Double?): Boolean {
            if (value != null) {
                ui.owner.database.addRecordForTag(tagId, value)
                toast("Recorded ${value}")
                return true
            }
            return false
        }
        var listAdapter: MyListAdapter<Tag>? = null
        fun removeTag(tagId: Int) {
            ui.owner.database.removeTag(tagId)
            listAdapter?.notifyDataSetChanged()
        }

        listAdapter = MyListAdapter(owner.database.tags, { it.id }) {
            tag ->
            TemplateRenderer(owner) {
                with(it) {
                    relativeLayout {
                        //                        backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                        val buttonContainer = linearLayout {
                            id = R.id.buttonContainer
                            // Add value button
                            imageButton(android.R.drawable.ic_input_add) {
                                isFocusable = false
                                onClick {
                                    var valueField: EditText? = null
                                    val alertDialog = alert {
                                        customView {
                                            verticalLayout {
                                                toolbar {
                                                    lparams(width = matchParent, height = wrapContent)
                                                    backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                                                    title = "Add a value"
                                                    setTitleTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                                                }
                                                valueField = editText {
                                                    hint = "Value"
                                                    padding = dip(20)
                                                    inputType = TYPE_CLASS_NUMBER or TYPE_NUMBER_FLAG_DECIMAL
//                                                    isFocusable = true
//                                                    isFocusableInTouchMode = true
                                                    showKeyboardOnFocus(ctx)
                                                    imeOptions = EditorInfo.IME_ACTION_DONE

                                                }
                                                positiveButton("Add") {
                                                    val value = valueField?.text.toString().toDoubleOrNull()
                                                    if (addValue(tag.id, value)) {
                                                        it.dismiss()
                                                    }
                                                }
                                                cancelButton { }
                                            }
                                        }
                                    }.show()
                                    valueField?.onEditorAction { v, actionId, event ->
                                        if (actionId == EditorInfo.IME_ACTION_DONE
                                                && addValue(tag.id, valueField?.text.toString().toDoubleOrNull())) {
                                            alertDialog.dismiss()
                                        }
                                    }
                                }
                            }
                            imageButton(android.R.drawable.ic_delete) {
                                isFocusable = false
                                onClick {
                                    alert {
                                        customView {
                                            verticalLayout {
                                                toolbar {
                                                    lparams(width = matchParent, height = wrapContent)
                                                    backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                                                    title = "Delete '${tag.tag}'?"
                                                    setTitleTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                                                }
                                                positiveButton("Yes") {
                                                    removeTag(tag.id)
                                                    it.dismiss()
                                                }
                                                negativeButton("No") {}
                                            }
                                        }
                                    }.show()
                                }
                            }
                        }.lparams {
                            width = wrapContent
                            height = wrapContent
                            alignParentRight()
                            alignParentTop()
                        }
                        textView {
                            text = tag.tag
                            textSize = 32f
//                            background = ContextCompat.getDrawable(ctx, R.drawable.border)
//                            onClick {
//                                ui.owner.tagSelected(ui, tag)
//                            }
                        }.lparams {
                            alignParentLeft()
                            alignParentTop()
                            leftOf(buttonContainer)
                        }
                    }

                }
            }
        }

        coordinatorLayout {
            verticalLayout {
                padding = dip(8)

                listView {
                    adapter = listAdapter
                    onItemClick { _, _, _, id ->
                        val tag = owner.database.tags.find { it.id == id.toInt() }
                        if (tag != null) {
                            owner.tagSelected(ui, tag)
                        }
                    }
                }.lparams {
                    margin = dip(8)
                    width = matchParent
                    height = matchParent
                }
            }.lparams {
                width = matchParent
                height = matchParent
            }

            floatingActionButton {
                imageResource = android.R.drawable.ic_input_add
                onClick {
                    fun addTag(tag: String?): Boolean {
                        if (!tag.isNullOrEmpty()) {
                            owner.database.addTag(tag!!)
                            listAdapter?.notifyDataSetChanged()
                            return true
                        }
                        return false
                    }

                    var tagField: EditText? = null
                    val alertDialog = alert {
                        customView {
                            verticalLayout {
                                toolbar {
                                    lparams(width = matchParent, height = wrapContent)
                                    backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                                    title = "Add a Tag"
                                    setTitleTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                                }
                                tagField = editText {
                                    hint = "Tag"
                                    padding = dip(20)
                                    showKeyboardOnFocus(ctx)
                                    inputType = TYPE_CLASS_TEXT or TYPE_TEXT_FLAG_CAP_WORDS
                                    imeOptions = EditorInfo.IME_ACTION_DONE
                                }
                                positiveButton("Add") {
                                    if (addTag(tagField?.text.toString())) {
                                        it.dismiss()
                                    }
                                }
                                cancelButton { }
                            }
                        }
                    }.show()
                    tagField?.onEditorAction { v, actionId, event ->
                        if (actionId == EditorInfo.IME_ACTION_DONE
                                && addTag(tagField?.text.toString())) {
                            alertDialog.dismiss()
                        }
                    }
                }
            }.lparams {
                margin = dip(10)
                gravity = Gravity.BOTTOM or Gravity.END
            }
        }
    }
}
