package com.qimai.qmmonitor.bean;

import org.litepal.crud.LitePalSupport;

public class TestErrorBean extends LitePalSupport {

    private long timeStamp;
    private boolean isUpload;
    private String msg;
    private String  apiUrl;
    private int errorCode;

}
