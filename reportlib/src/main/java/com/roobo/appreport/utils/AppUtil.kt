@file:JvmName("AppUtil")

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import java.io.File

/**
 * @author xiongmingcai
 * @date 2020/12/18
 * @function App信息工具类
 * @Description
 */

private const val UNKOWN = "unKnown"

/**
 * 获取应用版本名称，默认为本应用
 * @return 失败时返回unKnown
 */
fun Context.getAppVersionName(packageName: String = this.packageName): String {
    return try {
        if (packageName.isBlank()) {
            return UNKOWN
        } else {
            val pi = packageManager.getPackageInfo(packageName, 0)
            pi?.versionName ?: UNKOWN
        }
    } catch (e: PackageManager.NameNotFoundException) {
        UNKOWN
    }
}

/**
 * 获取应用版本号，默认为本应用
 * @return 失败时返回-1
 */
fun Context.getAppVersionCode(packageName: String = this.packageName): Int {
    return try {
        if (packageName.isBlank()) {
            -1
        } else {
            val pi = packageManager.getPackageInfo(packageName, 0)
            pi?.versionCode ?: -1
        }
    } catch (e: PackageManager.NameNotFoundException) {
        -1
    }
}

/**
 * 获取应用大小，单位为b，默认为本应用
 * @return 失败时返回-1
 */
fun Context.getAppSize(packageName: String = this.packageName): Long {
    return try {
        if (packageName.isBlank()) {
            -1
        } else {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            File(applicationInfo.sourceDir).length()
        }
    } catch (e: PackageManager.NameNotFoundException) {
        -1
    }
}

/**
 * 获取应用图标，默认为本应用
 * @return 失败时返回null
 */
fun Context.getAppIcon(packageName: String = this.packageName): Drawable? {
    return try {
        if (packageName.isBlank()) {
            null
        } else {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            applicationInfo.loadIcon(packageManager)
        }
    } catch (e: PackageManager.NameNotFoundException) {
        null
    }
}


/**
 * 共享元素
 */
private fun getOptionsBundle(activity: Activity, shareElements: Array<out View>): Bundle? {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val len = shareElements.size
        val pairs = arrayOfNulls<Pair<View, String>>(len)
        for (i in shareElements.indices) {
            pairs[i] = Pair.create(shareElements[i], shareElements[i].transitionName)
        }
        return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, *pairs).toBundle()
    }
    return ActivityOptionsCompat.makeSceneTransitionAnimation(activity, null, null).toBundle()
}

// 获取渠道工具函数
fun getChannelName(ctx: Context?): String? {
    if (ctx == null) {
        return null
    }
    var channelName: String? = null
    try {
        val packageManager = ctx.packageManager
        if (packageManager != null) {
            //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是activity标签中，所以用ApplicationInfo
            val applicationInfo: ApplicationInfo =
                packageManager.getApplicationInfo(ctx.packageName, PackageManager.GET_META_DATA)
            if (applicationInfo != null) {
                if (applicationInfo.metaData != null) {
                    channelName = applicationInfo.metaData.get("market_channel").toString() + ""
                }
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    if (TextUtils.isEmpty(channelName) || TextUtils.equals("null", channelName)) {
        channelName = "neibukaifaceshi"
    } else {
        if (TextUtils.equals("10000", channelName)) {
            return "guanwang"
        } else if (TextUtils.equals("10001", channelName)) {
            return "yingyongbao"
        } else if (TextUtils.equals("10002", channelName)) {
            return "baidu"
        } else if (TextUtils.equals("10003", channelName)) {
            return "baidu91"
        } else if (TextUtils.equals("10005", channelName)) {
            return "xiaomi"
        } else if (TextUtils.equals("10006", channelName)) {
            return "360"
        } else if (TextUtils.equals("10007", channelName)) {
            return "wandoujia"
        } else if (TextUtils.equals("10008", channelName)) {
            return "oppo"
        } else if (TextUtils.equals("10009", channelName)) {
            return "vivo"
        } else {
            channelName = "umeng"
        }
    }
    return channelName
}

fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
fun Double.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()
fun Double.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()