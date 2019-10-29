package com.qimai.qmmonitor.interceptor

/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.text.TextUtils
import android.util.Log

import com.qimai.qmmonitor.MonitorUtils
import com.qimai.qmmonitor.ThrowWrapper
import com.qimai.qmmonitor.bean.ApiErrorCode
import com.qimai.qmmonitor.bean.ApiErrorCode.BAD_GATEWAY
import com.qimai.qmmonitor.bean.ApiErrorCode.CONNECT_ERROR
import com.qimai.qmmonitor.bean.ApiErrorCode.CONNECT_TIME_OUT
import com.qimai.qmmonitor.bean.ApiErrorCode.FORBIDDEN
import com.qimai.qmmonitor.bean.ApiErrorCode.GATEWAY_TIMEOUT
import com.qimai.qmmonitor.bean.ApiErrorCode.HTTP_ERROR
import com.qimai.qmmonitor.bean.ApiErrorCode.INTERNAL_SERVER_ERROR
import com.qimai.qmmonitor.bean.ApiErrorCode.NOT_FOUND
import com.qimai.qmmonitor.bean.ApiErrorCode.REQUEST_TIMEOUT
import com.qimai.qmmonitor.bean.ApiErrorCode.SERVICE_UNAVAILABLE
import com.qimai.qmmonitor.bean.ApiErrorCode.SSL_ERROR
import com.qimai.qmmonitor.bean.ApiErrorCode.UNAUTHORIZED
import com.qimai.qmmonitor.bean.ApiErrorCode.UNKNOWN

import org.json.JSONException
import org.json.JSONObject

import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

import okhttp3.Connection
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.http.HttpHeaders
import okhttp3.internal.platform.Platform
import okio.Buffer
import okio.BufferedSource
import retrofit2.HttpException

import okhttp3.internal.platform.Platform.INFO
import java.net.ConnectException
import java.util.concurrent.TimeoutException
import javax.net.ssl.SSLHandshakeException

/**
 * An OkHttp interceptor which logs request and response information. Can be applied as an
 * [application interceptor][OkHttpClient.interceptors] or as a [ ][OkHttpClient.networkInterceptors].
 *
 * The format of the logs created by
 * this class should not be considered stable and may change slightly between releases. If you need
 * a stable logging format, use your own interceptor.
 */
abstract class ApiErrInterceptor : Interceptor {

    var logMessage = StringBuilder()

    private val logger = object : ApiErrInterceptor.Logger {


        override fun log(message: String) {
            Platform.get().log(INFO, message, null)
            Log.d("httpLog", message)
            logMessage.append(message + "\n")
        }
    }

    @Volatile
    private var level: ApiErrInterceptor.Level = Level.BODY

    enum class Level {
        /**
         * No logs.
         */
        NONE,
        /**
         * Logs request and response lines.
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1 (3-byte body)
         *
         * <-- 200 OK (22ms, 6-byte body)
        `</pre> *
         */
        BASIC,
        /**
         * Logs request and response lines and their respective headers.
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         * <-- END HTTP
        `</pre> *
         */
        HEADERS,
        /**
         * Logs request and response lines and their respective headers and bodies (if present).
         *
         *
         * Example:
         * <pre>`--> POST /greeting http/1.1
         * Host: example.com
         * Content-Type: plain/text
         * Content-Length: 3
         *
         * Hi?
         * --> END POST
         *
         * <-- 200 OK (22ms)
         * Content-Type: plain/text
         * Content-Length: 6
         *
         * Hello!
         * <-- END HTTP
        `</pre> *
         */
        BODY
    }

    interface Logger {
        fun log(message: String)

        /**
         * A [ApiErrInterceptor.Logger] defaults output appropriate for the current platform.
         */
        // ApiErrInterceptor.
    }

    /**
     * Change the level at which this interceptor logs.
     *
     * @return
     */
    fun setLevel(level: ApiErrInterceptor.Level?): ApiErrInterceptor {
        if (level == null) throw NullPointerException("level == null. Use Level.NONE instead.")
        this.level = level
        return this
    }

    fun getLevel(): ApiErrInterceptor.Level {
        return level
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = this.level

        val request = chain.request()
        if (level == ApiErrInterceptor.Level.NONE) {
            return chain.proceed(request)
        }

        val logBody = level == ApiErrInterceptor.Level.BODY
        val logHeaders = logBody || level == ApiErrInterceptor.Level.HEADERS

        val requestBody = request.body()
        val hasRequestBody = requestBody != null

        val connection = chain.connection()
        var requestStartMessage = ("--> "
                + request.method()
                + ' '.toString() + request.url()
                + if (connection != null) " " + connection.protocol() else "")
        if (!logHeaders && hasRequestBody) {
            requestStartMessage += " (" + requestBody!!.contentLength() + "-byte body)"
        }
        //初始化保存日志变量
        logMessage = StringBuilder()
        logger.log(requestStartMessage)

        if (logHeaders) {
            if (hasRequestBody) {
                // Request body headers are only present when installed as a network interceptor. Force
                // them to be included (when available) so there values are known.
                if (requestBody!!.contentType() != null) {
                    logger.log("Content-Type: " + requestBody.contentType()!!)
                }
                if (requestBody.contentLength() != -1L) {
                    logger.log("Content-Length: " + requestBody.contentLength())
                }
            }

            val headers = request.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                val name = headers.name(i)
                // Skip headers from the request body as they are explicitly logged above.
                if (!"Content-Type".equals(
                        name,
                        ignoreCase = true
                    ) && !"Content-Length".equals(name, ignoreCase = true)
                ) {
                    logger.log(name + ": " + headers.value(i))
                }
                i++
            }

            if (!logBody || !hasRequestBody) {
                logger.log("--> END " + request.method())
            } else if (bodyEncoded(request.headers())) {
                logger.log("--> END " + request.method() + " (encoded body omitted)")
            } else {
                val buffer = Buffer()
                requestBody!!.writeTo(buffer)

                var charset: Charset? = UTF8
                val contentType = requestBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }

                logger.log("")
                if (isPlaintext(buffer)) {
                    logger.log(buffer.readString(charset!!))
                    logger.log(
                        "--> END " + request.method()
                                + " (" + requestBody.contentLength() + "-byte body)"
                    )
                } else {
                    logger.log(
                        "--> END " + request.method() + " (binary "
                                + requestBody.contentLength() + "-byte body omitted)"
                    )
                }
            }
        }

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            //logger.log("<-- HTTP FAILED: $e")
            //logMessage.append(e.toString());
            Log.d(TAG, "intercept: logMessage= $logMessage")
            val throwWrapper = handleResponseException(e)
            MonitorUtils.getInstance()
                .addApiError(
                    System.currentTimeMillis(),
                    logMessage.append(throwWrapper.toString()).toString(),
                    request.url().toString(),
                    throwWrapper.erroCode,
                    false
                )
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()
        val contentLength = responseBody!!.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logger.log(
            "<-- "
                    + response.code()
                    + (if (response.message().isEmpty()) "" else ' ' + response.message())
                    + ' '.toString() + response.request().url()
                    + " (" + tookMs + "ms" + (if (!logHeaders) ", $bodySize body" else "") + ')'.toString()
        )

        if (logHeaders) {
            val headers = response.headers()
            var i = 0
            val count = headers.size()
            while (i < count) {
                logger.log(headers.name(i) + ": " + headers.value(i))
                i++
            }

            if (!logBody || !HttpHeaders.hasBody(response)) {
                logger.log("<-- END HTTP")
            } else if (bodyEncoded(response.headers())) {
                logger.log("<-- END HTTP (encoded body omitted)")
            } else {
                val source = responseBody.source()
                source.request(java.lang.Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()

                var charset: Charset? = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    charset = contentType.charset(UTF8)
                }

                if (!isPlaintext(buffer)) {
                    logger.log("")
                    logger.log("<-- END HTTP (binary " + buffer.size() + "-byte body omitted)")
                    return response
                }
                //这里存储接口响应的数据，在这里过滤接口返回的false 存储到数据库
                if (contentLength != 0L) {
                    logger.log("")
                    logger.log(buffer.clone().readString(charset!!))
                    //String respose = buffer.clone().readString(charset);
                    if (!filterResponse(buffer.clone().readString(charset))) {
                        //记录错误日志
                        val errorMessage = logMessage.toString()
                        MonitorUtils.getInstance().addApiError(
                            System.currentTimeMillis(), errorMessage, request.url().toString(),
                            ApiErrorCode.API_RESPONSE_FALSE, false
                        )
                        Log.d(TAG, "intercept: errorMessage = $errorMessage")
                    }

                    // Log.d(TAG, "intercept: " + tempMessage);
                }
                logger.log("<-- END HTTP (" + buffer.size() + "-byte body)")
            }
        }
        return response
    }

    private fun handleResponseException(exception: Exception): ThrowWrapper {
        when (exception) {
            is HttpException -> {
                return ThrowWrapper(HTTP_ERROR, exception.message)
            }
            is ConnectException -> {
                return ThrowWrapper(CONNECT_ERROR, exception.message)
            }
            is SSLHandshakeException -> {
                return ThrowWrapper(SSL_ERROR, exception.message)
            }
            is TimeoutException -> {
                return ThrowWrapper(CONNECT_TIME_OUT, exception.message)
            }
            else -> {
                //子类去处理异常
                val otherThrowable: ThrowWrapper? = handleOtherThrowable()
                return otherThrowable ?: ThrowWrapper(UNKNOWN, exception.message)
            }
        }

    }
    //子类实现方法，处理一些自定义异常或者比较特殊的异常
    abstract fun handleOtherThrowable(): ThrowWrapper?

    private fun filterResponse(readString: String): Boolean {
        return if (!TextUtils.isEmpty(readString)) {
            filterApiRequestFailedRule(readString)
        } else false
    }

    /***
     * 可以重写这个方法自定义接口成功 返回列入false的情况
     */
    fun filterApiRequestFailedRule(readString: String): Boolean {
        try {
            val jsonObject = JSONObject(readString)
            return jsonObject.optBoolean("status")
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return false
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    companion object {
        private val TAG = "ApiErrInterceptor"

        private val UTF8 = Charset.forName("UTF-8")

        /**
         * Returns true if the body in question probably contains human readable text. Uses a small sample
         * of code points to detect unicode control characters commonly used in binary file signatures.
         */
        internal fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size() < 64) buffer.size() else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }

        }
    }
}
