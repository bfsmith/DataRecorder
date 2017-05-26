package bfsmith.github.com.recorder

import android.app.Dialog
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.InputType.TYPE_CLASS_NUMBER
import android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import com.github.bfsmith.recorder.DataService
import com.github.bfsmith.recorder.Tag
import com.github.bfsmith.recorder.TagViewActivity
import com.github.bfsmith.recorder.database
import com.github.bfsmith.recorder.util.MyListAdapter
import com.github.bfsmith.recorder.util.TemplateRenderer
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.sdk25.coroutines.onItemClick

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

//    fun tryLogin(ui: AnkoContext<MainActivity>, name: CharSequence?, password: CharSequence?) {
//        ui.doAsync {
//            Thread.sleep(500)
//
//            activityUiThreadWithContext {
//                if (checkCredentials(name.toString(), password.toString())) {
//                    toast("Logged in! :)")
////                    startActivity<CountriesActivity>()
//                } else {
//                    toast("Wrong password :( Enter user:password")
//                }
//            }
//        }
//    }
//
//    private fun checkCredentials(name: String, password: String) = name == "user" && password == "password"
}

class MainActivityUi : AnkoComponent<MainActivity> {
    private val customStyle = { v: Any ->
        when (v) {
            is Button -> v.textSize = 26f
            is EditText -> v.textSize = 24f
        }
    }

    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {

        val listAdapter = MyListAdapter<Tag>(owner.database.tags, { it.id }) {
            tag ->
            TemplateRenderer(owner) {
                with(it) {
                    relativeLayout {
                        textView {
                            text = tag.tag
                            textSize = 32f
                            onClick {
                                ui.owner.tagSelected(ui, tag)
                            }
                        }.lparams {
                            alignParentLeft()
                        }
                        linearLayout {
                            imageButton(android.R.drawable.ic_input_add) {
                                onClick {
                                    alert {
                                        customView {
                                            verticalLayout {
                                                toolbar {
                                                    lparams(width = matchParent, height = wrapContent)
                                                    backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                                                    title = "Add a value"
                                                    setTitleTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                                                }
                                                val valueField = editText {
                                                    hint = "Value"
                                                    padding = dip(20)
                                                    inputType = TYPE_CLASS_NUMBER + TYPE_NUMBER_FLAG_DECIMAL
                                                }
                                                positiveButton("Add") {
                                                    val value = valueField.text.toString().toDoubleOrNull()
                                                    if (value != null) {
                                                        ui.owner.database.addRecordForTag(tag.id, value)
                                                        it.dismiss()
                                                    }
                                                }
                                                negativeButton("Cancel") {}
                                            }
                                        }
                                    }.show()
                                }
                            }
                            imageButton(android.R.drawable.ic_delete) {
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
                                                    ui.owner.database.removeTag(tag.id)
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
//                        toast("You selected ${tag?.tag ?: "Unknown"}")
                    }
                }.lparams {
                    margin = dip(8)
                    width = matchParent
                    height = matchParent
                }

//                imageView(android.R.drawable.ic_menu_manage).lparams {
//                    width = matchParent
//                    height = wrapContent
//                    margin = dip(16)
//                    gravity = Gravity.CENTER
//                }
//
//                val name = editText {
//                    hintResource = R.string.name
//                    width = matchParent
////                    height = wrapContent
//                }
//                val password = editText {
//                    hintResource = R.string.password
//                    width = matchParent
////                    height = wrapContent
//                    inputType = TYPE_CLASS_TEXT or TYPE_TEXT_VARIATION_PASSWORD
//                }
//
//                button("Log in") {
//                    onClick {
//                        ui.owner.tryLogin(ui, name.text, password.text)
//                    }
//                }
            }.lparams {
                width = matchParent
                height = matchParent
            }

            floatingActionButton {
                imageResource = android.R.drawable.ic_input_add
                onClick {
                    alert {
                        customView {
                            verticalLayout {
                                //Dialog Title
                                toolbar {
                                    lparams(width = matchParent, height = wrapContent)
                                    backgroundColor = ContextCompat.getColor(ctx, R.color.colorAccent)
                                    title = "Add a Tag"
                                    setTitleTextColor(ContextCompat.getColor(ctx, android.R.color.white))
                                }
                                val tag = editText {
                                    hint = "Tag"
                                    padding = dip(20)
                                }
                                positiveButton("Add") {
                                    if (tag.text.toString().isEmpty()) {
                                        toast("Oops!! Your task says nothing!")
                                    } else {
                                        // Add Tag to database and refresh list
                                        owner.database.addTag(tag.text.toString())
                                        listAdapter.notifyDataSetChanged()
                                        toast("You have added ${tag.text}")
                                        it.dismiss()
                                    }
                                }
                                negativeButton("Cancel") {
                                    toast("Add Tag canceled.")
                                }
                            }
                        }
                    }.show()
                }
            }.lparams {
                //setting button to bottom right of the screen
                margin = dip(10)
//                alignParentBottom()
//                alignParentEnd()
//                alignParentRight()
                gravity = Gravity.BOTTOM or Gravity.END
            }

//            myRichView()
        }.applyRecursively(customStyle)
    }
}
