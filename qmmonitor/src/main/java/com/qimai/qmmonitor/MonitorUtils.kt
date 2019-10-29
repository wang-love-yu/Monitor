package com.qimai.qmmonitor

import android.app.Application
import android.util.Log
import com.qimai.qmmonitor.bean.ApiErrorDao
import org.litepal.LitePal

class MonitorUtils private constructor() {
    private  val TAG = "MonitorUtils"
    //添加接口异常
    fun addApiError(
        timeStamp: Long,
        errMsg: String,
        apiUrl: String,
        errorCode: Int,
        isUpload: Boolean = false
    ) {
        //添加数据到数据库
       ApiErrorDao(timeStamp, errMsg, apiUrl, errorCode, isUpload)
            .saveAsync()
    }

    companion object {
        private val instance = MonitorUtils()
        fun init(application: Application) {
            Log.d("MonitorUtils", "init: ")
            LitePal.initialize(application)
        }

        fun getInstance(): MonitorUtils {
            return instance
        }
    }
}