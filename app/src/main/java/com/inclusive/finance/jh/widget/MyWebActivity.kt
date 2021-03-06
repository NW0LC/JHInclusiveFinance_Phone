package com.inclusive.finance.jh.widget

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.blankj.utilcode.util.EncodeUtils
import com.blankj.utilcode.util.RegexUtils
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.hwangjr.rxbus.RxBus
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.permissionWAndRWithPermissionCheck
import com.inclusive.finance.jh.databinding.ActivityMyWebBinding
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureConfig.CHOOSE_REQUEST
import com.luck.picture.lib.config.PictureMimeType
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback
import com.tencent.smtt.export.external.interfaces.JsResult
import com.tencent.smtt.export.external.interfaces.SslError
import com.tencent.smtt.export.external.interfaces.SslErrorHandler
import com.tencent.smtt.sdk.*
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import java.io.File


@RuntimePermissions
@Route(path = "/com/MyWebActivity")
open class MyWebActivity : BaseActivity(), OnPageChangeListener, OnPageErrorListener,
    OnLoadCompleteListener {
    @Autowired
    @JvmField
    var webUrl = ""

    @Autowired
    @JvmField
    var webTitle = ""


    @Autowired
    @JvmField
    var screen_orientation = ""

    @Autowired
    @JvmField
    var isPDF = false
    private var isAnimStart = false
    private var currentProgress: Int = 0
    private var mUploadMessage: ValueCallback<Uri>? = null

    private var mUploadMessageForAndroid5: ValueCallback<Array<Uri>>? = null
    override fun initToolbar() {
        if (screen_orientation == "portrait") {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.toolbar.title = webTitle
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            if (!this.viewBind.mWebView.canGoBack()) {
                this.finish()
            } else {
                viewBind.mWebView.goBack()
            }
        }
    }

    //
    //    override fun setInflateId(): Int {
    //        return R.layout.activity_my_web
    //    }
    lateinit var viewBind: ActivityMyWebBinding
    override fun setInflateBinding() {
        viewBind = DataBindingUtil.setContentView<ActivityMyWebBinding>(this, R.layout.activity_my_web)
            .apply {
                lifecycleOwner = this@MyWebActivity
            }

    }

    var tbsReaderView: TbsReaderView? = null
    var readerCallback = TbsReaderView.ReaderCallback { integer, o, o1 -> }

    override fun onDestroy() {
        viewBind.mWebView.reload()
        //??????
        viewBind.mWebView.clearCache(true)
        viewBind.mWebView.clearFormData()
        viewBind.mWebView.destroy()
        tbsReaderView?.onStop()
        super.onDestroy()
    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
    fun permissionWAndR(listener: Runnable) {
        listener.run()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }


    override fun init() {
        //        mWebView.loadUrl("http://hpb.xzsem.cn/mobile/after_login.aspx?uid=4")
        //        mWebView.postUrl("http://hpb.xzsem.cn/mobile/after_login.aspx",EncodeUtils.base64Decode("uid=4"))
        permissionWAndRWithPermissionCheck(null, 100, false) {
            if (isPDF) {
//                tbsReaderView = TbsReaderView(this, readerCallback)
//
                DataCtrlClass.downloadPDF(this, webUrl) {

                    viewBind.mWebView?.visibility=View.GONE
                viewBind.pdfView?.visibility=View.VISIBLE
                    val pdfFile = File(it)
                    displayFromFile(pdfFile)
//                    if (pdfFile.exists()) {
//                        //pdfView.fromFile(pdfFile).pages(0).load();//???????????????????????????
//                        viewBind.pdfView.fromFile(pdfFile) //??????pdf????????????
//                            .onLoad(object : OnLoadCompleteListener() {
//                                fun loadComplete(nbPages: Int) {
////                                sumPageIndex = pdfView.getPageCount()
////                                currentPageIndex = pdfView.getCurrentPage()
//                                }
//                            })
//                            .onPageChange(object : OnPageChangeListener {
//                                fun onPageChanged(page: Int, pageCount: Int) {
////                                    tvCurrentIndex.setText((page + 1).toString() + "/" + pageCount)
////                                    currentPageIndex = page
//                                }
//                            }) //??????????????????
//                            .onRender(object : OnRenderListener() {
//                                fun onInitiallyRendered(
//                                    nbPages: Int,
//                                    pageWidth: Float,
//                                    pageHeight: Float
//                                ) {
//                                    viewBind.pdfView.fitToWidth()
//                                }
//                            }) //??????????????????????????????????????????????????????
//                            .swipeHorizontal(false) //????????????????????????
//                            .load()
//                    }
                }



//                viewBind.mWebView.loadUrl("file:///android_asset/previewIndex.html?http://www.cztouch.com/upfiles/soft/testpdf.pdf")
//                viewBind.mWebView?.visibility=View.GONE
//                viewBind.pdfView?.visibility=View.VISIBLE
//                viewBind.pdfViewPager.fromFile(File("http://www.cztouch.com/upfiles/soft/testpdf.pdf")).enableSwipe(true) // allows to block changing pages using swipe
//                    .swipeHorizontal(false)
//                    .enableDoubletap(true)
//                    .defaultPage(0).load()

            } else{
                viewBind.mWebView?.visibility=View.VISIBLE
//                viewBind.pdfView?.visibility=View.GONE
                viewBind.mWebView.loadUrl(webUrl)
            }
        }
         //???????????????
        val webSettings = viewBind.mWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.allowFileAccess = true
        webSettings.domStorageEnabled = true
                webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.loadWithOverviewMode = true
        webSettings.useWideViewPort = true
        webSettings?.builtInZoomControls=true
        webSettings?.displayZoomControls=false
//        webSettings.databaseEnabled = true
//        webSettings.javaScriptCanOpenWindowsAutomatically = true
//        webSettings.setGeolocationEnabled(true)
//        webSettings.defaultTextEncodingName = "UTF-8" // ?????????JS??????
//        viewBind.mWebView.addJavascriptInterface(JavascriptInterface(), "backlistner")
        viewBind.mWebView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        viewBind.mWebView.clearHistory() // js????????????
        viewBind.mWebView.clearFormData()
        viewBind.mWebView.clearCache(true)
        viewBind.mWebView.setBackgroundColor(ContextCompat.getColor(this.mContext, R.color.white))
        viewBind.mWebView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {

                viewBind.progressBar.visibility = View.VISIBLE
                viewBind.progressBar.alpha = 1.0f
            }

            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url == null) return false
                try {
                    if (url.startsWith("weixin://") || url.startsWith("alipays://") || url.startsWith("mailto://") || url.startsWith("tel://") || url.startsWith("tel:") || url.startsWith("tbopen://") || url.startsWith("baidumap://")) {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        startActivity(intent)
                        return true
                    } //??????????????????scheme
                } catch (e: Exception) { //??????crash (???????????????????????????????????????scheme?????????url???APP, ?????????crash)
                    return false
                }
                if (url.contains("https://3gimg.qq.com/lightmap/components/locationPicker2/back.html?")) {
                    val intent = intent
                    intent.putExtra("url", url)
                    setResult(Activity.RESULT_OK, intent)
                    if (!getQueryStr(url, "name").contains("????????????")){
                        SZWUtils.showSnakeBarError("???????????? ??????????????????")
                        viewBind.mWebView.goBack()
                    }else{
                        RxBus.get().post("BackUrl", url)
                        finish()
                    }
                    return true
                }else
                //??????http???https?????????url
                view?.loadUrl(url)
                return true
            }

            override fun onReceivedSslError(p0: WebView?, p1: SslErrorHandler?, p2: SslError?) {
                if (p1 != null) {
                    p1.proceed();//????????????????????????????????????????????????????????????????????????
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (TextUtils.isEmpty(webTitle)) viewBind.actionBarCustom.mTitle.text = view?.title

                //??????js???????????????????????????????????????????????????img??????????????????onClick???????????????????????????????????????????????????????????????java???????????????url??????
                viewBind.mWebView.loadUrl("javascript:(function(){" + "var objs = document.getElementsByClassName(\"return_btn\"); " + "for(var i=0;i<objs.length;i++) {" + "    objs[i].onclick=function()  {  " + "        window.backlistner.goBack();  " + "}" + "}" + "var objs1 = document.getElementsByClassName(\"return_btn2\"); " + "for(var i=0;i<objs1.length;i++) {" + "    objs1[i].onclick=function()  {  " + "        window.backlistner.goBack();  " + "}" + "}" + "var objs2 = document.getElementsByClassName(\"back\"); " + "for(var i=0;i<objs2.length;i++) {" + "    objs2[i].onclick=function()  {  " + "        window.backlistner.goBack();  " + "}" + "}" + "})()")
            }
        }
        viewBind.mWebView.webChromeClient = object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(s: String, geolocationPermissionsCallback: GeolocationPermissionsCallback) {
                geolocationPermissionsCallback.invoke(s, true, true)
                super.onGeolocationPermissionsShowPrompt(s, geolocationPermissionsCallback)
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                this@MyWebActivity.currentProgress = this@MyWebActivity.viewBind.progressBar.progress
                if (newProgress >= 100 && !this@MyWebActivity.isAnimStart) {
                    this@MyWebActivity.isAnimStart = true
                    this@MyWebActivity.viewBind.progressBar.progress = newProgress
                    this@MyWebActivity.startDismissAnimation(this@MyWebActivity.viewBind.progressBar.progress)
                } else {
                    this@MyWebActivity.startProgressAnimation(newProgress)
                }


            }

            override fun onJsAlert(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                val builder = AlertDialog.Builder(p0?.context)
                builder.setTitle("??????").setMessage(p2).setPositiveButton("??????", null)
                builder.setCancelable(false)
                val dialog = builder.create()
                dialog.show()
                p3?.confirm()
                return true
            }

            override fun onJsConfirm(p0: WebView?, p1: String?, p2: String?, p3: JsResult?): Boolean {
                val builder = AlertDialog.Builder(p0?.context)
                builder.setTitle("??????").setMessage(p2)
                    .setPositiveButton("??????") { _, _ -> p3?.confirm() }
                    .setNeutralButton("??????") { _, _ -> p3?.cancel() }
                builder.setOnCancelListener { p3?.cancel() }
                // ??????keycode??????84????????????????????????????????????????????????????????????????????????????????????????????????
                builder.setOnKeyListener { _, _, _ -> true }
                // ???????????????back????????????
                // builder.setCancelable(false);
                val dialog = builder.create()
                dialog.show()
                return true
            }

            override fun openFileChooser(p0: ValueCallback<Uri>?, p1: String?, p2: String?) {
                mUploadMessage = p0
                PictureSelector.create(this@MyWebActivity).openGallery(PictureMimeType.ofImage())
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)
                    .selectionMode(PictureConfig.SINGLE).compress(true).enableCrop(true)
                    .withAspectRatio(1, 1)
                    //                            .maxSelectNum(1)
                    //                        .videoMaxSecond(15)
                    //                            .recordVideoSecond(15)
                    .forResult(PictureConfig.CHOOSE_REQUEST) //????????????onActivityResult code
            }

            override fun onShowFileChooser(p0: WebView?, p1: ValueCallback<Array<Uri>>?, p2: FileChooserParams?): Boolean {
                mUploadMessageForAndroid5 = p1
                PictureSelector.create(this@MyWebActivity).openGallery(PictureMimeType.ofImage())
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)
                    .selectionMode(PictureConfig.SINGLE).compress(true).enableCrop(true)
                    .withAspectRatio(1, 1)
                    //                            .maxSelectNum(1)
                    //                        .videoMaxSecond(15)
                    //                            .recordVideoSecond(15)
                    .forResult(PictureConfig.CHOOSE_REQUEST) //????????????onActivityResult code
                return true
            }
        }
    }
    private fun displayFromFile(file: File) {
        try {
            viewBind.pdfView.fromFile(file) //??????pdf????????????
                .defaultPage(0)
                .onPageChange(this)
                .enableSwipe(true)
                .enableAnnotationRendering(true)
                .onLoad(this)
//                .scrollHandle(DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }
    private fun getQueryStr(url: String, str: String): String {
        val matches = RegexUtils.getMatches("(^|)$str=([^&]*)(&|$)", url)
        return try {
            if (matches.size > 0) {
                EncodeUtils.urlDecode(matches[0].split("=")[1].replace("&", ""))
            } else ""
        } catch (e: Exception) {
            "??????????????????????????????"
        }
    }
    inner class JavascriptInterface {

        @android.webkit.JavascriptInterface
        fun goBack() {
            if (!this@MyWebActivity.viewBind.mWebView.canGoBack()) {
                this@MyWebActivity.finish()
            }
        }
    }

    private fun startProgressAnimation(newProgress: Int) {
        val animator = ObjectAnimator.ofInt(this.viewBind.progressBar, "progress", this.currentProgress, newProgress)
        animator.duration = 300L
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    private fun startDismissAnimation(progress: Int) {
        val anim = ObjectAnimator.ofFloat(this.viewBind.progressBar, "alpha", 1.0f, 0.0f)
        anim.duration = 1500L
        anim.interpolator = DecelerateInterpolator()
        anim.addUpdateListener { valueAnimator ->
            val fraction = valueAnimator.animatedFraction
            val offset = 100 - progress
            this@MyWebActivity.viewBind.progressBar.progress = (progress.toFloat() + offset.toFloat() * fraction).toInt()
        }
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                this@MyWebActivity.viewBind.progressBar.progress = 0
                this@MyWebActivity.viewBind.progressBar.visibility = View.GONE
                this@MyWebActivity.isAnimStart = false
            }
        })
        anim.start()
    }


    override fun onBackPressed() {
        if (!this@MyWebActivity.viewBind.mWebView.canGoBack()) {
            this@MyWebActivity.finish()
        } else {
            viewBind.mWebView.goBack()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_REQUEST) {
                // ??????????????????????????????????????????
                val selectList = PictureSelector.obtainMultipleResult(data)
                // ?????? LocalMedia ??????????????????path
                // 1.media.getPath(); ?????????path
                // 2.media.getCutPath();????????????path????????????media.isCut();?????????true  ????????????????????????
                // 3.media.getCompressPath();????????????path????????????media.isCompressed();?????????true  ????????????????????????
                // ????????????????????????????????????????????????????????????????????????????????????
                val path = Uri.parse(if (selectList[0].isCompressed) {
                    selectList[0].compressPath
                } else selectList[0].path)
                when {
                    null != mUploadMessage -> {
                        mUploadMessage?.onReceiveValue(path)
                        mUploadMessage = null

                    }
                    null != mUploadMessageForAndroid5 -> {
                        if (path != null) {
                            mUploadMessageForAndroid5?.onReceiveValue(arrayOf(path))
                        } else {
                            mUploadMessageForAndroid5?.onReceiveValue(arrayOf())
                        }
                        mUploadMessageForAndroid5 = null
                    }
                    else -> return
                }
            }
        } else {
            when {
                null != mUploadMessage -> {
                    mUploadMessage?.onReceiveValue(Uri.EMPTY)
                    mUploadMessage = null

                }
                null != mUploadMessageForAndroid5 -> {
                    mUploadMessageForAndroid5?.onReceiveValue(arrayOf())
                    mUploadMessageForAndroid5 = null
                }
                else -> return
            }
        }

    }

    companion object {
        var Intent_WebUrl = "webUrl"
        var Intent_WebTitle = "webTitle"
    }

    override fun onPageChanged(page: Int, pageCount: Int) {

    }

    override fun onPageError(page: Int, t: Throwable?) {
    }

    override fun loadComplete(nbPages: Int) {
    }
}
