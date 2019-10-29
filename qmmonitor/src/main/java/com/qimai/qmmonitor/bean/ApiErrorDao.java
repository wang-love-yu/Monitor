package com.qimai.qmmonitor.bean;

import androidx.annotation.NonNull;

import org.litepal.crud.LitePalSupport;

/***
 *
 *数据库名字apierrordao
 *
 *  **/
public class ApiErrorDao extends LitePalSupport {
    private long timeStamp;
    private boolean isUpload;
    private String msg;
    private String apiUrl;

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    private int errorCode;

    public ApiErrorDao(long timeStamp, String msg, String apiUrl, int errorCode, boolean isUpload) {
        this.timeStamp = timeStamp;
        this.isUpload = isUpload;
        this.msg = msg;
        this.apiUrl = apiUrl;
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return "ApiErrorBean{" +
                "timeStamp=" + timeStamp +
                ", isUpload=" + isUpload +
                ", msg='" + msg + '\'' +
                ", apiUrl='" + apiUrl + '\'' +
                ", errorCode=" + errorCode +
                '}';
    }
}
