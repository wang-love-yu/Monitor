package com.qimai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qimai.qmmonitor.MonitorUtils

import kotlinx.android.synthetic.main.main.*
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import java.security.SecureRandom
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData


class MainActivity : AppCompatActivity() {

    private  val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        val description = Description()
        lc_chart.setDrawBorders(true)
        lc_chart.setBorderColor(Color.GRAY)
        lc_chart.setBorderWidth(1F)
        lc_chart.setDrawGridBackground(false)
//设置chart是否可以触摸
        lc_chart.setTouchEnabled(true)
//设置是否可以拖拽
        lc_chart.setDragEnabled(true)
//设置是否可以缩放 x和y，默认true
        lc_chart.setScaleEnabled(false)
//设置是否可以通过双击屏幕放大图表。默认是true
        lc_chart.setDoubleTapToZoomEnabled(false)


        var entries = ArrayList<Entry>()
        for (i in 0..7) {
            entries.add(Entry(i.toFloat(), SecureRandom().nextInt(500).toFloat()))
        }
        var lineDataSet = LineDataSet(entries, "昨天")
        lineDataSet.color = Color.parseColor("#FD7841")
        lineDataSet.setCircleColor(Color.parseColor("#FD7841"))
        //lineDataSet.circleHoleRadius = 5f
        lineDataSet.circleRadius = 2f
        lineDataSet.setDrawFilled(true)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
       // lineDataSet.fillColor = Color.parseColor("#FD7841")
        lineDataSet.fillAlpha = 10
       lineDataSet.fillDrawable = ContextCompat.getDrawable(this,R.drawable.layer_1)
        // lineDataSet.setFillFormatter()
        lineDataSet.lineWidth = 2f


        var entries2 = ArrayList<Entry>()
        for (i in 0..7) {
            entries2.add(Entry(i.toFloat(), SecureRandom().nextInt(600).toFloat()))
        }

        var lineDataSet2 = LineDataSet(entries2, "今天")
        lineDataSet2.color = Color.parseColor("#3CCA6F")
        lineDataSet2.setCircleColor(Color.parseColor("#3CCA6F"))
        lineDataSet2.lineWidth = 2f
        lineDataSet2.fillAlpha = 10

        lineDataSet2.setDrawFilled(true)
        lineDataSet2.fillDrawable = ContextCompat.getDrawable(this,R.drawable.layer_2)
        lineDataSet2.mode = LineDataSet.Mode.CUBIC_BEZIER
        //lineDataSet2.fillColor = Color.parseColor("#3CCA6F")
        var lineData = LineData(lineDataSet, lineDataSet2)
        lc_chart.data = lineData
        lc_chart.invalidate()
        bt.setOnClickListener {
            /* val builder = SuitLines.LineBuilder()

             val lines = ArrayList<LineUnit>()
             for (i in 0..24 step 2) {
                 lines.add(
                     LineUnit(
                         SecureRandom().nextInt(128).toFloat(),
                         //0f,
                         "" + i, LineUnit.YESTERDAY
                     )
                 )
             }
             builder.add(lines, *intArrayOf(ContextCompat.getColor(this, R.color.line1)))
             val lines2 = ArrayList<LineUnit>()
             for (i in 0..24 step 2) {
                 lines2.add(
                     LineUnit(
                         if (i > 14) 0.toFloat() else SecureRandom().nextInt(128).toFloat(),
                         //0f,
                         "" + i, LineUnit.TODAY
                     )
                 )
             }
             builder.add(lines2, *intArrayOf(ContextCompat.getColor(this, R.color.colorAccent)))
             builder.build(suitlines, false)*/


        }
        MonitorUtils.getInstance().addApiError(System.currentTimeMillis(), "", "", 1, false)


        //  suitlines.feed(lines)

    }


}
