package com.roobo.appreport.networklibrary.exception

import android.net.ParseException
import com.google.gson.JsonParseException
import org.apache.http.conn.ConnectTimeoutException
import org.json.JSONException
import retrofit2.HttpException
import java.net.ConnectException
import java.net.SocketTimeoutException
import javax.net.ssl.SSLHandshakeException

//
//static Map<String, String> _error = {
//    '-400': '服务内部错误，内部调用错误',
//    '-401': '请重新登陆',
//    '-406': '您没有使用权限，\n请联系机构管理员',
//    '-422': '参数或者header头必要信息错误',
//    '-429': '验证码请求次数过多，\n请1小时后重试',
//    '-451': '验证码已失效\n请重新获取',
//    '-500': '服务内部错误',
//    '-100': '网络不给力，请检查网络设置或稍后重试',
//};
//
//static Map<String, Function> _errorFunction = {
//    '-401': showLoginDialog,
//    '-406': clearAuth,
//    '-100': showMessage,
//    '-100866': proxyClean,
//};
class ExceptionHandle {
    companion object {
        // 登录授权失效，请重新登录
        const val LOGIN_REQUIRE = -401
        const val REQUIRE_LOGOUT = -422

        private const val UNAUTHORIZED = 401
        private const val FORBIDDEN = 403
        private const val NOT_FOUND = 404
        private const val REQUEST_TIMEOUT = 408
        private const val INTERNAL_SERVER_ERROR = 500
        private const val BAD_GATEWAY = 502
        private const val SERVICE_UNAVAILABLE = 503
        private const val GATEWAY_TIMEOUT = 504

        fun handleException(e: Throwable): Exception {
            val ex: ResponeThrowable
            return if (e is HttpException) {
                ex = ResponeThrowable(e, ERROR.HTTP_ERROR)
                when (e.code()) {
                    UNAUTHORIZED,
                    FORBIDDEN,
                    NOT_FOUND,
                    REQUEST_TIMEOUT,
                    GATEWAY_TIMEOUT,
                    INTERNAL_SERVER_ERROR,
                    BAD_GATEWAY,
                    SERVICE_UNAVAILABLE -> ex.message = ERROR_MESSAGE.ENTWORK_ERROR
                    else -> ex.message = ERROR_MESSAGE.ENTWORK_ERROR
                }
                ex
            } else if (e is ServerException) {
                val resultException: ServerException = e as ServerException
                ex = ResponeThrowable(resultException, resultException.code)
                ex.message = resultException.message
                ex
            } else if (e is NoNetworkException) {
                val resultException: NoNetworkException = e as NoNetworkException
                ex = ResponeThrowable(resultException, ERROR.NOT_NETWORK_ERROR)
                ex.message = resultException.message
                ex
            } else if (e is JsonParseException
                || e is JSONException
                || e is ParseException
            ) {
                ex = ResponeThrowable(e, ERROR.PARSE_ERROR)
                ex.message = ERROR_MESSAGE.PARSE_ERROR
                ex
            } else if (e is ConnectException) {
                ex = ResponeThrowable(e, ERROR.NETWORD_ERROR)
                ex.message = ERROR_MESSAGE.CONNECTION_FAILURE
                ex
            } else if (e is SSLHandshakeException) {
                ex = ResponeThrowable(e, ERROR.SSL_ERROR)
                ex.message = ERROR_MESSAGE.CERTIFICATE_VERIFICATION_FAILED
                ex
            } else if (e is ConnectTimeoutException) {
                ex = ResponeThrowable(e, ERROR.TIMEOUT_ERROR)
                ex.message = ERROR_MESSAGE.CONNECTION_TIMEOUT
                ex
            } else if (e is SocketTimeoutException) {
                ex = ResponeThrowable(e, ERROR.TIMEOUT_ERROR)
                ex.message = ERROR_MESSAGE.CONNECTION_TIMEOUT
                ex
            } else if (e is ResponeThrowable) {
                e
            } else {
                ex = ResponeThrowable(e, ERROR.UNKNOWN)
                ex.message = ERROR_MESSAGE.UNKOWN_ERROR
                ex
            }
        }
    }

    /**
     * 约定异常
     */
    object ERROR {
        /**
         * 未知错误
         */
        const val UNKNOWN = 1000

        /**
         * 解析错误
         */
        const val PARSE_ERROR = 1001

        /**
         * 网络错误
         */
        const val NETWORD_ERROR = 1002

        /**
         * 协议出错
         */
        const val HTTP_ERROR = 1003

        /**
         * 证书出错
         */
        const val SSL_ERROR = 1005

        /**
         * 连接超时
         */
        const val TIMEOUT_ERROR = 1006

        /**
         * 无网络
         */
        const val NOT_NETWORK_ERROR = 1007
    }

    object ERROR_MESSAGE {
        const val ENTWORK_ERROR = "网络错误"
        const val PARSE_ERROR = "解析错误"
        const val CONNECTION_FAILURE = "连接失败"
        const val CERTIFICATE_VERIFICATION_FAILED = "证书验证失败"
        const val CONNECTION_TIMEOUT = "连接超时"
        const val UNKOWN_ERROR = "未知错误"
        const val NOT_NETWORK = "当前无网络"
        const val NOT_NETWORK_WEAK = "您的网络好像不给力，请稍后重试"
    }

    class ResponeThrowable(throwable: Throwable?, var code: Int) : Exception(throwable) {
        override var message: String? = null

    }

    class ServerException(val code: Int, message: String) : RuntimeException(message)

    /**
     * 无网络异常
     */
    class NoNetworkException : RuntimeException() {
        var code = ERROR.NOT_NETWORK_ERROR
        override var message: String? = ERROR_MESSAGE.NOT_NETWORK
    }

}

/**
 * 根据errorcode 内部定义错误内容
 */
fun Int.errorMessage(str: String): String {
    return when (this) {
        -400 -> "服务内部错误，内部调用错误"
        -401 -> "请重新登陆"
        -406 -> "您没有使用权限，\n请联系机构管理员"
        -422 -> "参数或者header头必要信息错误"
        -429 -> "验证码请求次数过多，\n请1小时后重试"
        -451 -> "验证码已失效\n请重新获取"
        -500 -> "服务内部错误"
        -100 -> "网络不给力，请检查网络设置或稍后重试"
        else -> {
            str
        }
    }
}