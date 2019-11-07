package com.qimai

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.qimai.qmmonitor.MonitorUtils



class MainActivity : AppCompatActivity() {

    private  val TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        MonitorUtils.getInstance().addApiError(System.currentTimeMillis(), "", "", 1, false)


        //  suitlines.feed(lines)

    }


}
