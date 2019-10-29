package com.qimai.qmmonitor.bean

object ApiErrorCode {

    /**
     * 未知错误
     */
    val UNKNOWN = 1000
    /**
     * 解析错误
     */
    val PARSE_ERROR = 1001
    /**
     * 网络错误
     */
    val NETWORD_ERROR = 1002
    /**
     * 协议出错
     */
    val HTTP_ERROR = 1003
    //连接异常
    val CONNECT_ERROR = 1004
    /**
     * 证书出错
     */
    val SSL_ERROR = 1005
    //请求超时
    val CONNECT_TIME_OUT = 1006
    val API_RESPONSE_FALSE = 2000

    val UNAUTHORIZED = 401
    val FORBIDDEN = 403
    val NOT_FOUND = 404
    val REQUEST_TIMEOUT = 408
    val INTERNAL_SERVER_ERROR = 500
    val BAD_GATEWAY = 502
    val SERVICE_UNAVAILABLE = 503
    val GATEWAY_TIMEOUT = 504

}