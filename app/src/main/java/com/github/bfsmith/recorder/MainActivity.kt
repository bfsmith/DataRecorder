package bfsmith.github.com.recorder

import android.content.Context
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.InputType.TYPE_CLASS_TEXT
import android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import com.github.bfsmith.recorder.DataService
import com.github.bfsmith.recorder.Tag
import com.github.bfsmith.recorder.util.MyListAdapter
import com.github.bfsmith.recorder.util.TemplateRenderer
import com.github.bfsmith.recorder.util.render
import org.jetbrains.anko.*
import org.jetbrains.anko.design.coordinatorLayout
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.design.floatingActionButton
import org.jetbrains.anko.sdk25.coroutines.onItemClick

class MainActivity : AppCompatActivity() {
    lateinit var ds: DataService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ds = DataService()

        MainActivityUi().setContentView(this)
    }

    fun tagSelected(ui: AnkoContext<MainActivity>, tag: Tag) {
        ui.doAsync {
            activityUiThreadWithContext {
                toast("You selected tag ${tag.tag}.")
            }
        }
    }

    fun tryLogin(ui: AnkoContext<MainActivity>, name: CharSequence?, password: CharSequence?) {
        ui.doAsync {
            Thread.sleep(500)

            activityUiThreadWithContext {
                if (checkCredentials(name.toString(), password.toString())) {
                    toast("Logged in! :)")
//                    startActivity<CountriesActivity>()
                } else {
                    toast("Wrong password :( Enter user:password")
                }
            }
        }
    }

    private fun checkCredentials(name: String, password: String) = name == "user" && password == "password"
}

class MainActivityUi : AnkoComponent<MainActivity> {
    private val customStyle = { v: Any ->
        when (v) {
            is Button -> v.textSize = 26f
            is EditText -> v.textSize = 24f
        }
    }

    override fun createView(ui: AnkoContext<MainActivity>): View = with(ui) {

        val listAdapter = MyListAdapter<Tag>(owner.ds.tags, { it.id }) {
            tag ->
            TemplateRenderer(owner) {
                with(it) {
                    textView {
                        text = tag.tag
                        textSize = 26f
                    }
                }
            }
        }

        coordinatorLayout {
            verticalLayout {
                padding = dip(32)

                render(TemplateRenderer(owner) {
                    with(it) {
                        textView {
                            text = "Template Renderer Worked"
                            textSize = 26f
                        }
                    }
                })

                listView {
                    adapter = listAdapter
                    onItemClick { adapterView, view, index, id ->
                        val tag = owner.ds.tags.find { it.id == id.toInt() }
                        toast("You selected ${tag?.tag ?: "Unknown"}")
                    }
                }.lparams {
                    margin = dip(8)
                    width = matchParent
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
                                        toast("You would have added ${tag.text}")
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
