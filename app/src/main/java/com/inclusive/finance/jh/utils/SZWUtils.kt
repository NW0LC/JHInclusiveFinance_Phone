package com.inclusive.finance.jh.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.model.LatLng
import com.blankj.utilcode.util.*
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterInside
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.inclusive.finance.jh.BuildConfig
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants.Result.Intent_ClassName
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.glide.imageloder.GlideApp
import com.inclusive.finance.jh.observer.SmsContentObserver
import com.inclusive.finance.jh.ui.login.LoginThirdActivity
import com.inclusive.finance.jh.widget.snakebar.TSnackbar
import com.umeng.umcrash.UMCrash
//import io.flutter.FlutterInjector
//import io.flutter.embedding.engine.FlutterEngine
//import io.flutter.embedding.engine.FlutterEngineCache
//import io.flutter.embedding.engine.dart.DartExecutor
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.abs


/**
 * Created by ?????????
 * on 2017/10/17.
 */
object SZWUtils {
    /**
     * ??????????????????????????????
     *
     */
    fun getMd5Pwd(passstr: String): String {
        if (passstr.isNotEmpty()) return EncryptUtils.encryptMD5ToString(passstr[0] + MyApplication.salt + passstr.substring(1))
            .toLowerCase()
        return ""
    }

    /**
     * @param phoneNum ????????????
     * @return ???????????????
     */
    fun hideMidPhone(phoneNum: String): String {

        return if (TextUtils.isEmpty(phoneNum)) "????????????"
        else if (phoneNum.length != 11) phoneNum
        else phoneNum.substring(0, 3) + "****" + phoneNum.substring(phoneNum.length - 4, phoneNum.length)
    }

    /**
     * @param mContext ?????????
     * @param intent   ??????
     * @return true??????
     */
    fun checkLogin(mContext: androidx.fragment.app.Fragment, intent: Intent = Intent(), clazzName: String = ""): Boolean {
        return if (!MyApplication.checkUserLogin()) {
            val login = Intent(mContext.context, LoginThirdActivity::class.java)
            if (clazzName.isNotEmpty()) {
                login.putExtra(Intent_ClassName, clazzName)
            }
            login.putExtras(intent)
            mContext.startActivityForResult(login, 0xc8)
            mContext.activity?.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fade_out)
            false
        } else {
            try {
                mContext.startActivityForResult(intent, 0xc8)
            } catch (e: Exception) {
            }
            true
        }
    }

    /**
     * @param mContext ?????????
     * @param intent   ??????
     * @return true??????
     */
    fun checkLogin(mContext: Activity, intent: Intent? = null, clazzName: String = ""): Boolean {
        return if (!MyApplication.checkUserLogin()) {
            val login = Intent(mContext, LoginThirdActivity::class.java)
            if (clazzName.isNotEmpty()) {
                login.putExtra(Intent_ClassName, clazzName)
            }
            if (intent != null) login.putExtras(intent)
            mContext.startActivityForResult(login, 0xc8)
            mContext.overridePendingTransition(R.anim.slide_in_bottom, R.anim.fade_out)
            false
        } else if (intent != null) {
            try {
                mContext.startActivityForResult(intent, 0xc8)
            } catch (e: Exception) {
            }
            true
        } else true
    }

    /**
     * ??????EditText???????????????????????????????????????????????????????????????????????????
     */

    fun isShouldHideKeyboard(v: View?, event: MotionEvent): Boolean {
        if (v != null && (v is EditText)) {
            val l = intArrayOf(0, 0)
            v.getLocationInWindow(l)
            val left = l[0]
            val top = l[1]
            val bottom = top + v.getHeight()
            val right = left + v.getWidth()
            return !(event.x > left && event.x < right && event.y > top && event.y < bottom)
        }
        return false
    }

    /**
     * ?????????????????????
     */
    fun setMargin(view: View, size: Float) {
        val lp = view.layoutParams
        if (lp is ViewGroup.MarginLayoutParams) {
            lp.topMargin += SizeUtils.dp2px(size)
        }

        view.layoutParams = lp

    }

    /**
     * ?????????????????????
     */
    fun setPaddingSmart(view: View, size: Float) {
        val lp = view.layoutParams
        if (lp != null && lp.height > 0) {
            lp.height += SizeUtils.dp2px(size)
        }
        view.setPadding(view.paddingLeft, view.paddingTop + SizeUtils.dp2px(size), view.paddingRight, view.paddingBottom)

    }

    /**
     * ?????????????????????
     */
    fun minusMargin(view: View, size: Float) {
        val lp = view.layoutParams
        if (lp is ViewGroup.MarginLayoutParams) {
            lp.topMargin -= SizeUtils.dp2px(size)
        }

        view.layoutParams = lp

    }

    /**
     * ?????????????????????
     */
    fun minusPaddingSmart(view: View, size: Float) {
        val lp = view.layoutParams
        if (lp != null && lp.height > 0) {
            lp.height -= SizeUtils.dp2px(size)
        }
        view.setPadding(view.paddingLeft, view.paddingTop - SizeUtils.dp2px(size), view.paddingRight, view.paddingBottom)

    }

    //    /**
    //     * ??????????????????yellow ??????
    //     *
    //     * @param b true grey  ; false yellow
    //     */
    //    fun setGreyOrYellow(context: Context?, view: RadioButton, b: Boolean) {
    //        if (context != null)
    //            if (b) {
    //                view.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.selector_tab_triangle_grey), null)
    //                view.setTextColor(ContextCompat.getColor(context, R.color.MaterialGrey600))
    //            } else {
    //                view.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(context, R.drawable.selector_tab_triangle_red), null)
    //                view.setTextColor(ContextCompat.getColor(context, R.color.color_main_blue))
    //            }
    //    }

    /**
     * ??????assets??????json
     */
    fun getJson(context: Context?, fileName: String): String {

        val stringBuilder = StringBuilder()
        try {
            val assetManager = context?.assets
            val bf = BufferedReader(InputStreamReader(assetManager?.open(fileName)))
            var b = true
            while (b) {
                val line = bf.readLine()
                if (line != null) {
                    stringBuilder.append(line)
                } else {
                    b = false
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }

    /**
     * ??????????????????observer
     *
     * @param context  ?????????
     * @param mHandler ??????
     * @return observer
     */
    fun registerSMS(context: Context, mHandler: Handler): SmsContentObserver { //?????????????????????????????????
        val smsContentObserver = SmsContentObserver(context, mHandler) // ???????????????????????? ???????????????????????????????????????Uri -----> content://sms // ?????????????????????Uri ????????? content://sms/outbox
        val smsUri = Uri.parse("content://sms")
        context.contentResolver.registerContentObserver(smsUri, true, smsContentObserver)
        return smsContentObserver
    }

    /**
     * @param mContext ?????????
     * @param textView ??????????????????textView
     * @return ?????????handler
     */
    fun patternCode(mContext: Context, textView: TextView, length: Int): Handler =
        MyHandler(mContext, textView, length)

    class MyHandler constructor(internal var mContext: Context, private var textView: TextView, private var length: Int) :
        Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val outbox = msg.obj as String //            edCode.setText(outbox); //            Toast.makeText(mContext, outbox, Toast.LENGTH_SHORT).show();
            textView.text = SZWUtils.patternCode(outbox, length)
        }
    }

    /**
     * ?????????????????????????????????
     * @param body ????????????
     * @param length  ?????????????????? ??????6?????????4???
     * @return ????????????????????????
     */
    fun patternCode(body: String, length: Int): String? { // ??????([a-zA-Z0-9]{length})????????????????????????????????????????????????
        // (?<![a-zA-Z0-9])????????????([0-9]{length})?????????????????????
        // (?![a-zA-Z0-9])??????([0-9]{length})???????????????????????????


        //  ????????????????????????
        //    Pattern p = Pattern   .compile("(?<![a-zA-Z0-9])([a-zA-Z0-9]{" + YZMLENGTH + "})(?![a-zA-Z0-9])");

        //  ???????????????
        val p = Pattern.compile("(?<![0-9])([0-9]{$length})(?![0-9])")

        val m = p.matcher(body)
        if (m.find()) {
            println(m.group())
            return m.group(0)
        }
        return null
    }


    /**
     * JsonObject?????????????????????null
     */
    fun getJsonObjectString(jsonObject: JsonObject?, key: String): String {
        return if (jsonObject?.get(key) == null || jsonObject.get(key).isJsonNull) "" else jsonObject.get(key).asString
    }

    /**
     * JsonObject?????????????????????null  ??????
     */
    fun getJsonObjectStringList(jsonObjects: List<JsonObject>?, key: String): List<String> {
        return jsonObjects?.map { if (it.get(key) == null || it.get(key).isJsonNull) "" else it.get(key).asString }
            ?: arrayListOf()
    }

    /**
     * JsonObject?????????JsonArray
     */
    fun getJsonObjectArray(jsonObject: JsonObject?, key: String): JsonArray {
        return if (jsonObject?.get(key) == null || jsonObject.getAsJsonArray(key).isJsonNull) JsonArray() else jsonObject.get(key).asJsonArray
    }

    /**
     * JsonObject?????????boolean ???null
     */
    fun getJsonObjectBoolean(jsonObject: JsonObject, key: String): Boolean {
        return if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull) false else jsonObject.get(key).asBoolean
    }

    /**
     * JsonObject?????????Int ???null
     */
    fun getJsonObjectInt(jsonObject: JsonObject, key: String): Int {
        return if (jsonObject.get(key) == null || jsonObject.get(key).isJsonNull) -1 else jsonObject.get(key).asInt
    }

    /**
     * ???????????????????????????????????????Calender ????????????????????????
     */
    fun getCalender(timeStr: String, format: String? = "yyyy-MM-dd"): Calendar {
        return Calendar.getInstance().apply {
            try {
                time = SimpleDateFormat(format ?: "", Locale.CHINA).parse(timeStr)
                    ?: TimeUtils.getNowDate()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * ??????????????????????????????
     */
    fun getIntactUrl(picUrl: String?): String {
        return when {
            picUrl?.contains("http://") == true || picUrl?.contains("https://") == true -> return picUrl
            picUrl.isNullOrEmpty() -> ""
            else -> (Urls.url + picUrl)
        }
    }

    /**
     * ???????????????????????????????????????
     */
    fun getJsonObjectBeanFromList(data: List<JsonObject>?, msg: String = "?????????????????????", listener: (JsonObject) -> Unit) {
        val jsonObject = data?.firstOrNull { b -> getJsonObjectBoolean(b, "isCheck") }
        if (jsonObject == null) {
            showSnakeBarMsg(msg)
        } else {
            listener.invoke(jsonObject)
        }
    }

    /**
     * ???????????????????????????????????????
     */
    fun getJsonObjectBeanListFromList(data: List<JsonObject>?, msg: String = "???????????????????????????", listener: (List<JsonObject>) -> Unit) {
        val jsonObjects = data?.filter { b -> getJsonObjectBoolean(b, "isCheck") }
        if (jsonObjects.isNullOrEmpty()) {
            showSnakeBarMsg(msg)
        } else {
            listener.invoke(jsonObjects)
        }
    }

    fun isSingleCheck(list: List<JsonObject>, msg: String = "????????????????????????"): Boolean {
        if (list.size > 1) {
            showSnakeBarMsg(msg)
        }
        return list.size <= 1
    }

    /**
     *???????????????????????????
     */
    fun setSeeOnlyMode(viewModel: ApplyModel, it: List<BaseTypeBean>) {
        if (viewModel.seeOnly == true) {
            it.forEach { baseTypeBean ->
                baseTypeBean.editable = false
                if (baseTypeBean.layoutType == BaseTypeBean.TYPE_4) {
                    baseTypeBean.listBean?.saveUrl = ""
                }
            }
        }
    }

    /**
     *????????????
     */
    fun loadPhotoImg(mContext: Context?, url: String? = "", imgView: ImageView?, radius: Int? = 1, error: Int? = null, placeholder: Int? = null) {
        if (mContext != null) {
            try {
                val with = GlideApp.with(mContext.applicationContext)
                if (url?.contains(".gif") == true) with.asGif()
                val options = RequestOptions().transform(CenterInside(), RoundedCorners(radius
                    ?: 1))
                    .error(ContextCompat.getDrawable(mContext.applicationContext, if (url.isNullOrEmpty()) R.mipmap.icon_photo_bg else error
                        ?: R.mipmap.icon_error))

                val apply = with.load(getIntactUrl(url)).apply(options)
                if (!url.isNullOrEmpty()) apply.thumbnail(Glide.with(imgView
                    ?: ImageView(mContext.applicationContext))
                    .load(placeholder ?: R.drawable.loading))

                //                .placeholder(ContextCompat.getDrawable(mContext, placeholder
                //                    ?: R.drawable.loading))
                apply.into(imgView ?: ImageView(mContext.applicationContext))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /**
     * ??????
     * */
    fun showSnakeBarMsg(msg: String) {
        showSnakeBarMsg(null, msg = msg)
    }

    fun showSnakeBarMsg(contentView: View?, msg: String) {
        try {
            val topActivity = ActivityUtils.getTopActivity()
            val rootView = topActivity?.window?.decorView?.findViewById<View>(android.R.id.content)
            var make = contentView?.let { TSnackbar.make(it, msg, 2000) }
            if (make == null) {
                make = rootView?.let { TSnackbar.make(it, msg, 2000) }
            }
            make?.setIconLeft(R.drawable.ic_baseline_warning_24, 24f, Color.WHITE)
            make?.setIconPadding(8)
            StatusBarUtil.setPaddingSmart(topActivity, make?.view)
            val textView = make?.view?.findViewById<TextView>(R.id.snackbar_text)
            textView?.setTextColor(Color.WHITE)
            make?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun showSnakeBarError(msg: String) {
        showSnakeBarError(null, msg)
    }

    fun showSnakeBarError(contentView: View?, msg: String) {
        try {
            val topActivity = ActivityUtils.getTopActivity()
            val rootView = topActivity?.window?.decorView?.findViewById<View>(android.R.id.content)
            var make = contentView?.let { TSnackbar.make(it, msg, 2000) }
            if (make == null) {
                make = rootView?.let { TSnackbar.make(it, msg, 2000) }
            }
            make?.view?.setBackgroundColor(Color.parseColor("#ff4339"))
            make?.setIconLeft(R.drawable.ic_baseline_error_outline_24, 24f, Color.WHITE)
            make?.setIconPadding(8)
            StatusBarUtil.setPaddingSmart(topActivity, make?.view)
            val textView = make?.view?.findViewById<TextView>(R.id.snackbar_text)
            textView?.setTextColor(Color.WHITE)
            make?.show()

        } catch (e: Exception) {
            UMCrash.generateCustomLog(e, "UmengException")
            e.printStackTrace()
        }
    }

    fun showSnakeBarSuccess(msg: String) {
        showSnakeBarSuccess(null, msg = msg)
    }

    fun showSnakeBarSuccess(contentView: View?, msg: String) {
        try {
            val topActivity = ActivityUtils.getTopActivity()
            val rootView = topActivity?.window?.decorView?.findViewById<View>(android.R.id.content)
            var make = contentView?.let { TSnackbar.make(it, msg, 2000) }
            if (make == null) {
                make = rootView?.let { TSnackbar.make(it, msg, 2000) }
            }
            make?.view?.setBackgroundColor(Color.parseColor("#18dc7e"))
            make?.setIconRight(R.drawable.ic_baseline_check_24, 24f, Color.WHITE)
            make?.setIconPadding(8)
            StatusBarUtil.setPaddingSmart(topActivity, make?.view)
            val textView = make?.view?.findViewById<TextView>(R.id.snackbar_text)
            textView?.setTextColor(Color.WHITE)
            make?.show()
        } catch (e: Exception) {
            UMCrash.generateCustomLog(e, "UmengException")
            e.printStackTrace()
        }
    }

    fun <T:BaseTypeBean> setCalculateCount(it: ArrayList<T>, s: String, lastYearAll: BigDecimal) {
        it.firstOrNull { item -> item.dataKey == s }?.valueName = lastYearAll.setScale(2, BigDecimal.ROUND_HALF_UP)
            .toString()
    }

    fun <T:BaseTypeBean> getCalculateCount(it: ArrayList<T>, s: String) =
        it.firstOrNull { item -> item.dataKey == s && item.valueName.isNotEmpty() }?.valueName?.toDoubleOrNull()
            ?: 0.00

    /**
     *
     * ????????????
     * */
    fun getMonthSpace(endDate: String, startDate: String): Int {
        var result: Int
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        val c1: Calendar = Calendar.getInstance()
        val c2: Calendar = Calendar.getInstance()
        c1.time = sdf.parse(endDate) ?: Date()
        c2.time = sdf.parse(startDate) ?: Date()
        val y1: Int = c1.get(1)
        val y2: Int = c2.get(1)
        val m1: Int = c1.get(2)
        val m2: Int = c2.get(2)
        val d1: Int = c1.get(5)
        val d2: Int = c2.get(5)
        result = (y1 - y2) * 12 + (m1 - m2)
        if (d1 > d2) {
            result++
        }
        return if (result == 0) 1 else abs(result)
    }

    /**
     *
     * ???????????????businessType
     * */
    fun getBusinessType(type: Int) = when (type) {
        ApplyModel.BUSINESS_TYPE_SXSP,
        ApplyModel.BUSINESS_TYPE_APPLY,
        -> {
            "0"
        }
        ApplyModel.BUSINESS_TYPE_QPLC,
        -> {
            "1"
        }
        ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
        ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
        ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
        ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
        ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
        -> {
            "03"
        }
        ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
        ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
        -> {
            "01"
        }
        ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
        ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
        ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
        -> {
            "02"
        }
        ApplyModel.BUSINESS_TYPE_VISIT_NEW,
        ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
        -> {
            "ZF"
        }
        ApplyModel.BUSINESS_TYPE_PRECREDIT,
        -> {
            "YSX"
        }
        ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER,
        ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK,
        ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL,
        -> {
            "YX"
        }

        ApplyModel.BUSINESS_TYPE_SJ,
        -> {
            "4"
        }
        ApplyModel.BUSINESS_TYPE_RC,
        -> {
            "5"
        }
        ApplyModel.BUSINESS_TYPE_JNJ_YX,
        -> {
            "8"
        }
        ApplyModel.BUSINESS_TYPE_INFORMATION_OFFICER,
        -> {
            "9"
        }
        ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE,
        -> {
            "10"
        }
        ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW,
        -> {
            "11"
        }
        ApplyModel.BUSINESS_TYPE_COMPARISON_OF_QUOTAS,
        -> {
            "12"
        }
        ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
        ApplyModel.BUSINESS_TYPE_SUNSHINE_QPLC,
        -> {
            "13"
        }
        ApplyModel.BUSINESS_TYPE_FUPIN,
        -> {
            "600"
        }
        else -> {
            ""
        }
    }

    /**
     *
     * ????????????
     * */
    fun setNumColorRed(str: String): SpannableStringBuilder? {
        val style = SpannableStringBuilder(str)
        for (i in str.indices) {
            val a = str[i]
            if (a in '0'..'9') {
                style.setSpan(ForegroundColorSpan(Color.RED), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                style.setSpan(RelativeSizeSpan(1.0f), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return style
    }
    /**
     *
     * ?????????????????????
     * */
    fun setRequiredColorRed(str: String="",b:Boolean): SpannableStringBuilder? {
        var newStr=str
        if (b) {
            newStr+="*"
        }
        val style = SpannableStringBuilder(newStr)
        for (i in newStr.indices) {
            val a = newStr[i]
            if (a =='*') {
                style.setSpan(ForegroundColorSpan(Color.RED), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                style.setSpan(RelativeSizeSpan(1.0f), i, i + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return style
    }

    fun compareLatLng(mMap: BaiduMap, latLng: LatLng): Boolean {
        val leftPoint = mMap.projection.fromScreenLocation(Point(0, 0))
        val rightPoint = mMap.projection.fromScreenLocation(Point(ScreenUtils.getScreenWidth(), ScreenUtils.getScreenHeight()))
        return rightPoint.latitude < latLng.latitude && latLng.latitude < leftPoint.latitude && leftPoint.longitude < latLng.longitude && latLng.longitude < rightPoint.longitude
    }


    /**
     * ?????????????????????????????????
     *
     * @return
     */
    fun createCustomCameraOutPath(context: Context?): String {
        val customFile: File
        val externalFilesDir = context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        customFile = File(externalFilesDir?.absolutePath, BuildConfig.APPLICATION_ID)
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.absolutePath + File.separator
    }

    /**
     * ???????????????s??????????????????
     *
     * @return
     */
    fun createCustomMoviesOutPath(context: Context?): String {
        val customFile: File
        val externalFilesDir = context?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        customFile = File(externalFilesDir?.absolutePath, BuildConfig.APPLICATION_ID)
        if (!customFile.exists()) {
            customFile.mkdirs()
        }
        return customFile.path + File.separator
    }


    /**
     * flutterEngine ??????
     *
     * @return
     */
//    fun flutterEngine(context: Context, engineId: String, entryPoint: String): FlutterEngine {
//        var engine = FlutterEngineCache.getInstance().get(engineId)
//        if (engine == null) { //??????????????????????????????????????????
//            val app = context.applicationContext as ToolApplication
//            val dartEntrypoint = DartExecutor.DartEntrypoint(FlutterInjector.instance().flutterLoader().findAppBundlePath(), entryPoint)
//            engine = app.engineGroup.createAndRunEngine(context, dartEntrypoint)
//            FlutterEngineCache.getInstance().put(engineId, engine)
//        }
//        return engine!!
//    }
}