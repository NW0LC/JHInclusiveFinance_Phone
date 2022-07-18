package com.inclusive.finance.jh.ui.apply

import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.alibaba.android.arouter.launcher.ARouter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.listener.OnLoadMoreListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.app.MyApplication
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.User
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentApplyCreditManagerBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.glide.GlideEngine
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.widget.MyWebActivity
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.style.PictureParameterStyle
import com.mabeijianxi.smallvideorecord2.Log
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshListener
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import org.jetbrains.anko.support.v4.act


/**
 * 征信管理
 * */
class CreditManagerFragment : MyBaseFragment(), PresenterClick, OnLoadMoreListener,
    OnRefreshListener, OnItemChildClickListener {
    private val REQUEST_MANAGER_PERMISSION: Int=1000
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentApplyCreditManagerBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    private var getListUrl = Urls.getList_CreditManager
    private var getUrl = Urls.getEdit_CreditManager
    private var saveUrl = Urls.save_CreditManager
    private var deleteUrl = Urls.delete_CreditManager
    private var submitUrl = Urls.creditAnalysisAdd
    private var enumUrl = Urls.getCreditManagerCyList
    var businessType = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentApplyCreditManagerBinding.inflate(inflater, container, false).apply {
            presenterClick = this@CreditManagerFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.mRefreshLayout.setOnRefreshListener(this)
        mAdapter = ItemBaseListCardAdapter(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        mAdapter.setOnItemChildClickListener(this)
        viewBind.mRecyclerView.adapter = mAdapter
    }
    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btSeeOnly?.visibility = View.GONE
        if(viewModel?.seeOnly == true){
            viewBind?.btChange?.visibility = View.GONE
        }

    }

    override fun initData() {
//        requestManagerPermission()
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
            ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
            ApplyModel.BUSINESS_TYPE_ZXSP,
            -> {
                getListUrl = Urls.getList_CreditManager
                getUrl = Urls.getEdit_CreditManager
                saveUrl = Urls.save_CreditManager
                deleteUrl = Urls.delete_CreditManager
                submitUrl = Urls.creditAnalysisAdd
                enumUrl = Urls.getCreditManagerCyList
            }
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_COMPANY,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL,
            ApplyModel.BUSINESS_TYPE_SJ_COMPANY,
            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL,
            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY,
            -> {
                getListUrl = Urls.get_jnj_cj_personal_zxgl_list
                getUrl = Urls.getEdit_CreditManager
                saveUrl = Urls.save_CreditManager
                deleteUrl = Urls.delete_CreditManager
                submitUrl = Urls.submit_jnj_cj_personal_zxgl
                enumUrl = Urls.getCreditManagerCyList
            }
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL,
            -> {
                getListUrl = Urls.get_creditManager_list
                getUrl = Urls.get_creditManager_edit
                saveUrl = Urls.save_creditManager
                deleteUrl = Urls.delete_creditManager
                submitUrl = Urls.creditAnalysisAdd
                enumUrl = Urls.getCreditManagerCyList
            }
            ApplyModel.BUSINESS_TYPE_JNJ_YX,
            -> {
                getListUrl = Urls.get_jnj_yx_zxgl_list
                getUrl = Urls.getEdit_CreditManager
                saveUrl = Urls.save_CreditManager
                deleteUrl = Urls.delete_CreditManager
                submitUrl = Urls.submit_jnj_cj_personal_zxgl
                enumUrl = Urls.getCreditManagerCyList
            }
            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY,
            -> {
                getListUrl = Urls.getList_sunshine_CreditManager
                getUrl = Urls.getEdit_sunshine_CreditManager
                saveUrl = Urls.save_sunshine_CreditManager
                deleteUrl = Urls.delete_sunshine_CreditManager
                submitUrl = Urls.submit_sunshine_CreditAnalysisAdd
                enumUrl = Urls.getCreditManager_sunshine_CyList
            }

        }
        businessType = SZWUtils.getBusinessType(viewModel.businessType)
        DataCtrlClass.ApplyNet.getCreditManagerList(requireActivity(), getListUrl, keyId = viewModel.keyId, businessType = businessType) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    viewBind.mRefreshLayout.setNoMoreData(false)
                    mAdapter.setListData(bean = it, list = it.list)
                } else {
                    mAdapter.addData(it.list)

                }
                if (!it.list.isNullOrEmpty()) {
                    mAdapter.loadMoreModule.loadMoreComplete()
                    currentPage++
                } else {
                    mAdapter.loadMoreModule.loadMoreEnd()
                }
            } else {
                mAdapter.loadMoreModule.loadMoreFail()
            }

        }
    }


    override fun refreshData(type: Int?) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        super.refreshData(type)
    }

    override fun onLoadMore() {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshData()
    }

    override fun onClick(v: View?) {
            CreditManagerPop(context, this, "新增", getUrl = getUrl, saveUrl = saveUrl, enumUrl = enumUrl, keyId = viewModel.keyId) {
                onRefresh(viewBind.mRefreshLayout)
                if (isAdded) (activity as BaseActivity).refreshData()
            }.show(childFragmentManager, this.javaClass.name)
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("征信授权")
        add("查看征信PDF")
        add("查看征信解析")
        add("征信二维码")
        add("授权书二维码")
        add("提交")
        add("删除")
    }

    private fun requestManagerPermission() {
        //当系统在11及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 没文件管理权限时申请权限
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.setData(Uri.parse("package:" + context?.getPackageName()))
                startActivityForResult(intent, REQUEST_MANAGER_PERMISSION)
            }
        }
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, v: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        when (v.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "征信二维码" ->{
                            DataCtrlClass.ApplyNet.creditErweiMa(context, Urls.createAuthQR, SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                val medias: MutableList<LocalMedia> = ArrayList()
                                val localMedia = LocalMedia()
                                localMedia.path = SZWUtils.getIntactUrl(it)
                                medias.add(localMedia)
                                val pictureParameterStyle = PictureParameterStyle() //                            pictureParameterStyle.pictureExternalPreviewGonePreviewDelete = !viewModel.getSeeOnly()
                                if (medias.size > 0) PictureSelector.create(this)
                                    .themeStyle(R.style.picture_default_style)
                                    .setPictureStyle(pictureParameterStyle)
                                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)
                                    .isNotPreviewDownload(true)
                                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                                    .openExternalPreview( 0,medias)
                            }
                        }
                        "授权书二维码" ->{
                            DataCtrlClass.ApplyNet.creditErweiMa(context, Urls.createSqsQR, SZWUtils.getJsonObjectString(jsonObject, "id")) {
                                val medias: MutableList<LocalMedia> = ArrayList()
                                val localMedia = LocalMedia()
                                localMedia.path = SZWUtils.getIntactUrl(it)
                                medias.add(localMedia)
                                val pictureParameterStyle = PictureParameterStyle() //                            pictureParameterStyle.pictureExternalPreviewGonePreviewDelete = !viewModel.getSeeOnly()
                                if (medias.size > 0) PictureSelector.create(this)
                                    .themeStyle(R.style.picture_default_style)
                                    .setPictureStyle(pictureParameterStyle)
                                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT)
                                    .isNotPreviewDownload(true)
                                    .imageEngine(GlideEngine.createGlideEngine()) // 请参考Demo GlideEngine.java
                                    .openExternalPreview( 0,medias)
                            }
                        }
                        "征信授权" -> {
                            when {
                                viewModel.businessType < 50 || viewModel.businessType == ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL || viewModel.businessType == ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
                                    IRouter.goF(v, R.id.action_to_navActivity, "征信授权", viewModel.creditId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                                }
                                else -> {
                                    IRouter.goF(v, R.id.action_to_navActivity, "征信授权-资料上传", viewModel.dhId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                                }
                            }

                        }
                        "查看征信PDF" -> {
                            Log.e("pdfdizhi",""+SZWUtils.getIntactUrl("zx/zx/queryZxPdf?id=${SZWUtils.getJsonObjectString(jsonObject, "id")}" + "&userName=${(if (MyApplication.user == null) User() else MyApplication.user as User).userInfo?.username}"))
                            ARouter.getInstance()
                                .build("/com/MyWebActivity") //                        .withString(Intent_WebUrl, "http://192.168.3.32:8081/onlinePreview?url=http%3A%2F%2F212.129.130.163%3A3000%2Ftscepdf.pdf&officePreviewType=pdf")
                                //                        .withString(Intent_WebUrl, "http://debugtbs.qq.com")
                                .withString(MyWebActivity.Intent_WebUrl, SZWUtils.getIntactUrl("zx/zx/queryZxPdf?id=${SZWUtils.getJsonObjectString(jsonObject, "id")}" + "&userName=${(if (MyApplication.user == null) User() else MyApplication.user as User).userInfo?.username}"))
                                .withBoolean("isPDF", true)
                                .withString(MyWebActivity.Intent_WebTitle, "征信PDF").navigation()
                        }
                        "查看征信解析" -> {
                            IRouter.goF(v, R.id.action_to_navActivity, "查看征信解析", viewModel.keyId, jsonObject, viewModel.businessType, viewModel.seeOnly)
                        }
                        "提交" -> {

                            when (viewModel.businessType) {
                                ApplyModel.BUSINESS_TYPE_APPLY,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
                                ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE,
                                -> {
//                                    if(SZWUtils.getJsonObjectString(jsonObject, "state")=="500"){
                                        DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, Gson().toJson(jsonObject), dhId = viewModel.dhId, businessType = businessType) {
                                            refreshData()
                                        }
//                                    }else {
//                                        GHQYJPop(context, jsonObject, ApplyModel.BUSINESS_TYPE_APPLY) {
//                                             jsonObject.addProperty("sprgh", ""+it)
//                                             DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, Gson().toJson(jsonObject), dhId = viewModel.dhId, businessType = businessType) {
//                                                refreshData()
//                                             }
//                                        }.show(childFragmentManager, this.javaClass.name)
//                                    }

                                }
                                else -> {
                                    DataCtrlClass.ApplyNet.creditAnalysisAdd(context, submitUrl, viewModel.creditId, Gson().toJson(jsonObject), dhId = viewModel.dhId, businessType = businessType) {
                                        refreshData()
                                    }
                                }
                            }


                        }
                        "删除" -> {
                            ConfirmPop(context, "确定删除?") {
                                if (it) DataCtrlClass.ApplyNet.applyDBDeleteById(context, deleteUrl, SZWUtils.getJsonObjectString(jsonObject, "id"), keyId = viewModel.keyId) {
                                    onRefresh(viewBind.mRefreshLayout)
                                    if (isAdded) (activity as BaseActivity).refreshData()
                                }
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        else -> {
                        }
                    } as Unit?
                }.showPopupWindow(v)
            }
            R.id.bt_seeOnly -> {

                CreditManagerPop(context, this, "查看", getUrl = getUrl, enumUrl = enumUrl, keyId = viewModel.keyId, json = jsonObject) {
                    onRefresh(viewBind.mRefreshLayout)
                    if (isAdded) (activity as BaseActivity).refreshData()
                }.show(childFragmentManager, this.javaClass.name)
            }
            R.id.bt_change -> {
                CreditManagerPop(context, this, "修改", getUrl = getUrl, saveUrl = saveUrl, enumUrl = enumUrl, keyId = viewModel.keyId, json = jsonObject) {
                    onRefresh(viewBind.mRefreshLayout)
                    if (isAdded) (activity as BaseActivity).refreshData()
                }.show(childFragmentManager, this.javaClass.name)

            }
        }
    }
}