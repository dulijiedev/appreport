package com.roobo.appreport.utils

import android.app.Application
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream

/**
 * Created by dlj on 2019/9/25.
 */
object UIUtils {


    private var sApplication: Application? = null

    /**
     * 初始化工具类
     *
     * @param app 应用
     */
    fun init(app: Application) {
        sApplication = app
    }

    /**
     * 得到上下文
     *
     * @return
     */
    fun getContext(): Context {
        if (sApplication != null) return sApplication as Application
        throw NullPointerException("请先在全局Application中调用 ResUIUtils.init 初始化！")
    }

    /**
     * view中单位换算 px转dip
     *
     * @param context Context
     * @param pxValue px值
     */
    fun px2dip(pxValue: Float): Int {
        val scale = getResource().displayMetrics.density
        return (pxValue / scale + 0.5f).toInt()
    }

    /**
     * view中单位换算 dip转px
     *
     * @param context  Context
     * @param dipValue dp值
     */
    fun dip2px(dipValue: Float): Int {
        val scale = getResource().displayMetrics.density
        return (dipValue * scale + 0.5f).toInt()
    }

    /**
     * sp转换px
     *
     * @param context
     * @param sp
     */
    fun sp2px(sp: Float): Float {
        val scaledDensity = getContext().resources.displayMetrics.scaledDensity
        return sp * scaledDensity
    }

    /**
     * 得到resources对象
     *
     * @return
     */
    fun getResource(): Resources {
        return getContext().resources
    }

    /**
     * 得到string.xml中的字符串
     *
     * @param resId
     * @return
     */
    fun getString(resId: Int): String {
        return getResource().getString(resId)
    }

    /**
     * 得到string.xml中的字符串，带点位符
     *
     * @return
     */
    fun getString(id: Int, vararg formatArgs: Any): String {
        return getResource().getString(id, *formatArgs)
    }

    /**
     * 得到string.xml中和字符串数组
     *
     * @param resId
     * @return
     */
    fun getStringArr(resId: Int): Array<String> {
        return getResource().getStringArray(resId)
    }

    /**
     * 得到string.xml中图片数组
     *
     * @param resId
     * @return
     */
    fun getTypeArr(resId: Int): TypedArray {
        return getResource().obtainTypedArray(resId)
    }

    /**
     * 得到colors.xml中的颜色
     *
     * @param colorId
     * @return
     */
    fun getColor(colorId: Int): Int {
        return ContextCompat.getColor(getContext(), colorId)
    }


    /**
     * Return the compressed bitmap using quality.
     *
     * @param src         The source of bitmap.
     * @param maxByteSize The maximum size of byte.
     * @param recycle     True to recycle the source of bitmap, false otherwise.
     * @return the compressed bitmap
     */
    fun compressByQuality(
        src: Bitmap,
        maxByteSize: Long,
        recycle: Boolean
    ): Bitmap? {
        if (isEmptyBitmap(src) || maxByteSize <= 0) return null
        val baos = ByteArrayOutputStream()
        src.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val bytes: ByteArray
        if (baos.size() <= maxByteSize) {
            bytes = baos.toByteArray()
        } else {
            baos.reset()
            src.compress(Bitmap.CompressFormat.JPEG, 0, baos)
            if (baos.size() >= maxByteSize) {
                bytes = baos.toByteArray()
            } else {
                // find the best quality using binary search
                var st = 0
                var end = 100
                var mid = 0
                while (st < end) {
                    mid = (st + end) / 2
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, mid, baos)
                    val len = baos.size()
                    if (len.toLong() == maxByteSize) {
                        break
                    } else if (len > maxByteSize) {
                        end = mid - 1
                    } else {
                        st = mid + 1
                    }
                }
                if (end == mid - 1) {
                    baos.reset()
                    src.compress(Bitmap.CompressFormat.JPEG, st, baos)
                }
                bytes = baos.toByteArray()
            }
        }
        if (recycle && !src.isRecycled) src.recycle()
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun isEmptyBitmap(src: Bitmap?): Boolean {
        return src == null || src.width == 0 || src.height == 0
    }

    /**
     * 添加spannbale文本
     *
     * @param tv       TextView
     * @param text     文本内容
     * @param textSize 文本大小
     * @param color    文本颜色
     * @param isBold   是否加粗
     */
    fun appendSpannableText(tv: TextView?, text: String, textSize: Int, color: Int, isBold: Boolean) {
        if (TextUtils.isEmpty(text) || null == tv)
            return
        tv.append(getTextSpannable(text, textSize, color, isBold))
    }

    /**
     * 获取文本spannable
     *
     * @param text     文本内容
     * @param textSize 文本大小
     * @param color    文本颜色
     * @param isBold   是否加粗
     * @return 文本spannable
     */
    fun getTextSpannable(text: String, textSize: Int, color: Int, isBold: Boolean): SpannableString {
        val ss = SpannableString(text)
        val len = text.length
        ss.setSpan(AbsoluteSizeSpan(textSize), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (isBold)
            ss.setSpan(StyleSpan(Typeface.BOLD), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        if (0 != color)
            ss.setSpan(ForegroundColorSpan(color), 0, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ss
    }

    /**
     *
     * 方法描述 隐藏银行卡号中间的字符串（使用*号），显示前四后四
     *
     * @param cardNo
     * @return
     *
     * @author yaomy
     * @date 2018年4月3日 上午10:37:00
     */
    fun hideCardNo(cardNo: String): String {
        val length = cardNo.length
        val beforeLength = 4
        val afterLength = 4
        //替换字符串，当前使用“*”
        val replaceSymbol = "*"
        val sb = StringBuffer()
        for (i in 0 until length) {
            if (i < beforeLength || i >= length - afterLength) {
                sb.append(cardNo[i])
            } else {
                sb.append(replaceSymbol)
            }
        }
        return sb.toString()
    }

    fun hidePhoneNo(phone: String): String {
        var newphone = phone.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
        newphone = newphone.replace("(\\d{3})\\d{4}(\\d{4})".toRegex(), "$1****$2")
        return newphone
    }

    /**
     * 纯数字
     *
     * @param str
     * @return
     */
    fun isNumeric(str: String): Boolean {
        var i = str.length
        while (--i >= 0) {
            if (!Character.isDigit(str[i])) {
                return false
            }
        }
        return true
    }

    /**
     * 纯字母
     *
     * @param data
     * @return
     */
    fun isChar(data: String): Boolean {
        var i = data.length
        while (--i >= 0) {
            val c = data[i]
            return if (c in 'a'..'z' || c in 'A'..'Z') {
                continue
            } else {
                false
            }
        }
        return true
    }

}