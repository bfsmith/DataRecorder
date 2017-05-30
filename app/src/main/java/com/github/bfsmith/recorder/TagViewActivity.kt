package com.github.bfsmith.recorder

import android.graphics.Color
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.github.bfsmith.recorder.util.MyListAdapter
import com.github.bfsmith.recorder.util.TemplateRenderer
import com.github.bfsmith.recorder.util.lineChart
import com.github.bfsmith.recorder.util.toString
import org.jetbrains.anko.*

import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TagViewActivity : AppCompatActivity(), AnkoLogger {
    lateinit var records: List<Record>
    lateinit var tag: Tag
    lateinit var data: List<Double>

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

    fun createLineDataSet(records: List<Record>): LineDataSet {
        val time = Date().time / 1000
        val lineDataSet = LineDataSet(records.mapIndexed {
            index, r ->
            Entry((time - (86_400 * index)).toFloat(), r.value.toFloat()) //
        }.reversed(), "Data")
        lineDataSet.circleRadius = 3f
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.valueTextSize = 12f
        lineDataSet.lineWidth = 2f
        lineDataSet.color = Color.RED

        return lineDataSet
    }

    override fun createView(ui: AnkoContext<TagViewActivity>): View = with(ui) {


//        val listAdapter = MyListAdapter(owner.records, { it.id }) {
//            record ->
//            TemplateRenderer(owner) {
//                with(it) {
//                    linearLayout {
//                        textView {
//                            text = record.date.toString(dateFormat)
//                            textSize = 12f
//                        }
//
//                        textView {
//                            text = record.value.toString()
//                            textSize = 12f
//                            horizontalPadding = dip(16)
//                        }
//                    }
//                }
//            }
//        }

        verticalLayout {
            padding = dip(8)

            textView {
                text = "Records for ${owner.tag.tag}"
                textSize = 16f
            }

//            listView {
//                adapter = listAdapter
//            }.lparams {
//                margin = dip(8)
//                width = matchParent
//                height = wrapContent
//            }

            lineChart {
                description.isEnabled = false
                setTouchEnabled(true)
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                data = LineData(createLineDataSet(owner.records))
                xAxis.valueFormatter = IAxisValueFormatter { value, axis ->
                    SimpleDateFormat("MMM d").format(Date(value.toLong()*1000))
                }
            }.lparams {
                height = matchParent
                width = matchParent
            }
        }
    }
}
