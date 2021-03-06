package com.inclusive.finance.jh.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.alibaba.android.arouter.facade.annotation.Autowired
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.tabs.TabLayout
import com.hwangjr.rxbus.RxBus
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.base.permissionCameraWithPermissionCheck
import com.inclusive.finance.jh.bean.BaseTypeBean.Enum12
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.ActivityApplyBinding
import com.inclusive.finance.jh.service.ZipService
import com.inclusive.finance.jh.ui.ApplyActivity.Companion.ClassType.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil

@Route(path = "/com/ApplyActivity")
class ApplyActivity : BaseActivity() {


    @Autowired
    @JvmField
    var creditId: String? = ""

    @Autowired
    @JvmField
    var dhId: String? = ""

    @Autowired
    @JvmField
    var zfId: String? = ""

    @Autowired
    @JvmField
    var ysxId: String? = ""

    @Autowired
    @JvmField
    var keyId: String? = ""

    @Autowired
    @JvmField
    var jsonObject: String? = ""

    @Autowired
    @JvmField
    var seeOnly: Boolean? = false

    @Autowired
    @JvmField
    var businessType: Int = ApplyModel.BUSINESS_TYPE_APPLY

    lateinit var viewModel: ApplyModel
    lateinit var viewBind: ActivityApplyBinding
    var menuBind: ViewDataBinding? = null
    override fun initToolbar() {
        permissionCameraWithPermissionCheck(null, 200, false) {}
        StatusBarUtil.darkMode(this)
        StatusBarUtil.setPaddingSmart(this, viewBind.actionBarCustom.appBar)
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }

        })
    }

    override fun setInflateBinding() {
        viewBind =
            DataBindingUtil.setContentView<ActivityApplyBinding>(this, R.layout.activity_apply)
                .apply {
                    viewModel = ViewModelProvider(this@ApplyActivity).get(ApplyModel::class.java)
                    data = viewModel
                    lifecycleOwner = this@ApplyActivity
                }
    }

    companion object {
        enum class ClassType {
            XXGK, GSXX, JTCY, SXSQD, WHYW, FXTC, ZXGL, CSJG, NSWD, SDDC, LLSR, XJL, SXYKH, ZCFZ, SYJB, CWJB, DBQK, PJZB, YXZL, DCBG, DCBG_SIMPLE, DCJL, SXPY, SJBG, RCBG, YXJC, ZFXX
        }

        /**??????*/
        enum class ClassType_DZ {
            GLSX, HTXX, DBXX, LLCS, RLSB, YXZL
        }

        fun getMenuList(businessType: Int): ArrayList<Enum12> {
            val list = arrayListOf<Enum12>()
            when (businessType) {
                ApplyModel.BUSINESS_TYPE_APPLY -> {
                    list.add(Enum12(XXGK, "????????????"))
                    list.add(Enum12(GSXX, "????????????"))
                    list.add(Enum12(JTCY, "????????????"))
                    list.add(Enum12(SXSQD, "???????????????"))
                    list.add(Enum12(WHYW, "????????????"))
                    list.add(Enum12(XJL, "?????????"))
                    list.add(Enum12(FXTC, "????????????"))
                    list.add(Enum12(ZXGL, "????????????"))
                    list.add(Enum12(DBQK, "????????????"))
                    list.add(Enum12(YXZL, "????????????"))
                    list.add(Enum12(CSJG, "????????????"))
                }
                ApplyModel.BUSINESS_TYPE_INVESTIGATE, ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE, ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE -> {
                    list.add(Enum12(XXGK, "????????????"))
                    list.add(Enum12(GSXX, "????????????"))
                    list.add(Enum12(JTCY, "????????????"))
                    list.add(Enum12(SXSQD, "???????????????"))
                    list.add(Enum12(WHYW, "????????????"))
                    list.add(Enum12(FXTC, "????????????"))
                    list.add(Enum12(ZXGL, "????????????"))
                    list.add(Enum12(CSJG, "????????????"))
//                    list.add(Enum12(NSWD, "????????????"))
                    list.add(Enum12(NSWD, "????????????"))
                    list.add(Enum12(SDDC, "????????????"))
                    list.add(Enum12(LLSR, "????????????"))
//                    list.add(Enum12(SXYKH, "???????????????"))
                    list.add(Enum12(XJL, "?????????"))
                    list.add(Enum12(ZCFZ, "????????????"))
                    list.add(Enum12(SYJB, "????????????"))
                    list.add(Enum12(CWJB, "????????????"))
                    list.add(Enum12(DBQK, "????????????"))
                    list.add(Enum12(PJZB, "????????????"))
                    list.add(Enum12(YXZL, "????????????"))
                    list.add(Enum12(DCJL, "????????????"))
                    list.add(Enum12(DCBG, "????????????"))
                }
                ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE -> {
                    list.add(Enum12(XXGK, "????????????"))
                    list.add(Enum12(GSXX, "????????????"))
                    list.add(Enum12(JTCY, "????????????"))
                    list.add(Enum12(SXSQD, "???????????????"))
                    list.add(Enum12(WHYW, "????????????"))
                    list.add(Enum12(FXTC, "????????????"))
                    list.add(Enum12(ZXGL, "????????????"))
                    list.add(Enum12(CSJG, "????????????"))
//                    list.add(Enum12(NSWD, "????????????"))
                    list.add(Enum12(NSWD, "????????????"))
                    list.add(Enum12(SDDC, "????????????"))
                    list.add(Enum12(XJL, "?????????"))
                    list.add(Enum12(DBQK, "????????????"))
                    list.add(Enum12(PJZB, "????????????"))
                    list.add(Enum12(YXZL, "????????????"))
                    list.add(Enum12(DCJL, "????????????"))
                    list.add(Enum12(DCBG_SIMPLE, "????????????"))

                }
                ApplyModel.BUSINESS_TYPE_SJ -> {
                    list.add(Enum12(SJBG, "????????????"))
                }
                ApplyModel.BUSINESS_TYPE_SJ2 -> {
                    list.add(Enum12(SJBG, "????????????"))
                }
                ApplyModel.BUSINESS_TYPE_RC -> {
                    list.add(Enum12(RCBG, "????????????"))
                }
                ApplyModel.BUSINESS_TYPE_RC2 -> {
                    list.add(Enum12(RCBG, "????????????"))
                }
                ApplyModel.BUSINESS_TYPE_JNJ_YX -> {
//                    list.add(Enum12(ZXGL, "????????????"))
                    list.add(Enum12(YXJC, "????????????"))
                }
                ApplyModel.BUSINESS_TYPE_FUPIN -> {
                    list.add(Enum12(XXGK, "?????????"))
                    list.add(Enum12(JTCY, "????????????"))
                    list.add(Enum12(ZFXX, "????????????"))

                }
                ApplyModel.BUSINESS_TYPE_DIANZIHETONGDENGJI -> {
                    list.add(Enum12(ClassType_DZ.GLSX, "????????????"))
                    list.add(Enum12(ClassType_DZ.HTXX, "????????????"))
                    list.add(Enum12(ClassType_DZ.DBXX, "????????????"))
                    list.add(Enum12(ClassType_DZ.LLCS, "????????????"))
                    list.add(Enum12(ClassType_DZ.RLSB, "????????????"))
                    list.add(Enum12(ClassType_DZ.YXZL, "????????????"))

                }
            }
            return list
        }
    }

    override fun init() {
        viewModel.creditId = creditId
        viewModel.dhId = dhId
        viewModel.zfId = zfId
        viewModel.ysxId = ysxId
        viewModel.keyId = keyId
        viewModel.seeOnly = seeOnly
        viewModel.businessType = businessType
        viewModel.jsonObject = jsonObject
        //        if (viewModel.creditId.isNullOrEmpty() && !ToolApplication.appDebug) {
        //            toast("?????????????????????????????????")
        //            ActivityUtils.finishActivity(this)
        //        }
        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ), 101
            )
        }
//        Watermark.instance?.show(this, "Fantasy BlogDemo")
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            finish()
        }

        val tabsStr: ArrayList<Enum12> = getMenuList(viewModel.businessType)
        when (viewModel.businessType) {
            ApplyModel.BUSINESS_TYPE_APPLY -> {
                viewBind.actionBarCustom.toolbar.title = "????????????"
                getMenuList(viewModel.businessType)
            }
            ApplyModel.BUSINESS_TYPE_INVESTIGATE, ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE, ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE, ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE -> {
                viewBind.actionBarCustom.toolbar.title = "????????????"
            }
            ApplyModel.BUSINESS_TYPE_SJ -> {
                viewBind.actionBarCustom.toolbar.title = "??????"
            }
            ApplyModel.BUSINESS_TYPE_SJ2 -> {
                viewBind.actionBarCustom.toolbar.title = "??????"
            }
            ApplyModel.BUSINESS_TYPE_RC -> {
                viewBind.actionBarCustom.toolbar.title = "????????????"
            }
            ApplyModel.BUSINESS_TYPE_RC2 -> {
                viewBind.actionBarCustom.toolbar.title = "????????????"
            }
            ApplyModel.BUSINESS_TYPE_JNJ_YX -> {
                viewBind.actionBarCustom.toolbar.title = "?????????"
            }
            ApplyModel.BUSINESS_TYPE_DIANZIHETONGDENGJI -> {
                viewBind.actionBarCustom.toolbar.title = "??????????????????"
            }
        }
        tabsStr.forEach {
            viewBind.tabLay.addTab(viewBind.tabLay.newTab().apply {
                text = it.valueName
                tag = it
            })
        }
        viewBind.tabLay.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                onClick((tab?.tag as Enum12).id)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                onClick((tab?.tag as Enum12).id)
            }

        })
//        when (viewModel.businessType) {
//            ApplyModel.BUSINESS_TYPE_APPLY, ApplyModel.BUSINESS_TYPE_INVESTIGATE, ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE, ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE -> {
//                viewBind.actionBarCustom.toolbar.title = if (viewModel.businessType == ApplyModel.BUSINESS_TYPE_APPLY) "????????????" else "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_credit
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeCreditBinding>(inflateView)
////                val activityApplyIncludeCreditBinding = viewBind.root.findViewById<ViewStub>(generateViewId).binding as ActivityApplyIncludeCreditBinding
////                bind?.flow?.referencedIds = when (viewModel.businessType) {
////                    ApplyModel.BUSINESS_TYPE_APPLY -> intArrayOf(R.id.constraintLay1, R.id.constraintLay1_01, R.id.constraintLay2, R.id.constraintLay3, R.id.constraintLay4????????????, R.id.constraintLay14?????????, R.id.constraintLay5????????????, R.id.constraintLay6????????????, R.id.constraintLay7????????????, R.id.constraintLay8????????????, R.id.constraintLay9????????????)
////                    ApplyModel.BUSINESS_TYPE_INVESTIGATE,
////                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_OPERATINGMODE,
////                    ApplyModel.BUSINESS_TYPE_INVESTIGATE_CONSUMPTIONMODE-> intArrayOf(R.id.constraintLay1, R.id.constraintLay1_01, R.id.constraintLay2, R.id.constraintLay3, R.id.constraintLay4, R.id.constraintLay5????????????, R.id.constraintLay6, R.id.constraintLay9, R.id.constraintLay10????????????, R.id.constraintLay11????????????, R.id.constraintLay12????????????, R.id.constraintLay13???????????????, R.id.constraintLay14?????????, R.id.constraintLay15????????????, R.id.constraintLay16????????????, R.id.constraintLay17????????????, R.id.constraintLay7, R.id.constraintLay18????????????, R.id.constraintLay8, R.id.constraintLay19, R.id.constraintLay20)
////                    else -> null
////                }
////                bind?.data = viewModel
//
//            }
////            ApplyModel.BUSINESS_TYPE_INVESTIGATE_SIMPLEMODE -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_investigate_simplemode
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeInvestigateSimplemodeBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_JNJ_CJ_PERSONAL -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_cj_personal
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeCjPersonalBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_JNJ_CJ_COMPANY -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_cj_company
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeCjCompanyBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_JNJ_JC_OFF_SITE_PERSONAL -> {
////                viewBind.actionBarCustom.toolbar.title = "???????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_jc_offsite_personal
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeJcOffsitePersonalBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_JNJ_JC_ON_SITE_PERSONAL -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_jc_onsite_personal
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeJcOnsitePersonalBindingImpl>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_JNJ_YX -> {
////                viewBind.actionBarCustom.toolbar.title = "?????????-??????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_jc_yx
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeJcYxBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_SJ -> {
////                viewBind.actionBarCustom.toolbar.title = "??????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_sj
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeSjBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_SJ_PERSONAL -> {
////                viewBind.actionBarCustom.toolbar.title = "??????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_sj_personal
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeSjPersonalBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_SJ_COMPANY -> {
////                viewBind.actionBarCustom.toolbar.title = "??????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_sj_company
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeSjCompanyBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////
////            ApplyModel.BUSINESS_TYPE_RC -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_rc
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeRcBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_RC_OFF_SITE_PERSONAL -> {
////                viewBind.actionBarCustom.toolbar.title = "???????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_rc_offsite_personal
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeRcOffsitePersonalBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_PERSONAL -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_rc_onsite_personal
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeRcOnsitePersonalBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_RC_ON_SITE_COMPANY -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_rc_onsite_company
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeRcOnsiteCompanyBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_VISIT_NEW -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_visit_new
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeVisitNewBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////
////            ApplyModel.BUSINESS_TYPE_VISIT_EDIT -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_visit_edit
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeVisitEditBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////
////            ApplyModel.BUSINESS_TYPE_PRECREDIT -> {
////                viewBind.actionBarCustom.toolbar.title = "?????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_visit_precredit
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeVisitPrecreditBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_credit_manager
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeCreditManagerBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_wjdc
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeWjdcBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_sxpy
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeSxpyBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_COMPARISON_OF_QUOTAS -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_edbd
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeEdbdBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            ApplyModel.BUSINESS_TYPE_SUNSHINE_APPLY -> {
////                viewBind.actionBarCustom.toolbar.title = "????????????"
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_sunshine_credit
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeSunshineCreditBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
////            else -> {
////                viewBind.root.findViewById<ViewStub>(generateViewId).layoutResource = R.layout.activity_apply_include_cj_personal
////                inflateView = viewBind.root.findViewById<ViewStub>(generateViewId).inflate()
////                val bind = DataBindingUtil.bind<ActivityApplyIncludeCjPersonalBinding>(inflateView)
////                bind?.data = viewModel
////
////            }
//        }
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.apply_nav_host_fragment) as NavHostFragment
        val navGraph: NavGraph =
            navHostFragment.navController.navInflater.inflate(R.navigation.nav_graph_apply)
        navGraph.setStartDestination(getActionByType(viewModel.businessType))
        navHostFragment.navController.graph = navGraph
//        viewModel.applyCheckBean?.xxgkCheck = true
    }

    private fun getActionByType(index: Int): Int = when (index) {
        ApplyModel.BUSINESS_TYPE_VISIT_EDIT,
        ApplyModel.BUSINESS_TYPE_VISIT_NEW,
        ApplyModel.BUSINESS_TYPE_PRECREDIT,
        -> {
            viewModel.applyCheckBean?.jbxxCheck = true
            R.id.BaseInfoFragment
        }
        ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER,
        -> {
            viewModel.applyCheckBean?.zxglCheck = true
            viewModel.businessType = ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL
            R.id.creditManagerFragment
        }
//        ApplyModel.BUSINESS_TYPE_JNJ_YX -> {//?????????-??????
//            viewModel.applyCheckBean?.zxglCheck = true
//            R.id.creditManagerFragment
//        }
        ApplyModel.BUSINESS_TYPE_QUESTIONNAIRE,
        ApplyModel.BUSINESS_TYPE_CREDIT_REVIEW,
        -> {
            viewModel.applyCheckBean?.xxyzpCheck = true
            R.id.XinXiYuanZhaoPianFragment
        }
        ApplyModel.BUSINESS_TYPE_COMPARISON_OF_QUOTAS,
        -> {
            viewModel.applyCheckBean?.edbdCheck = true
            R.id.WenJuanHuiZongFragment
        }
        else -> {
            viewModel.applyCheckBean?.xxgkCheck = true
            R.id.customerInfoFragment
        }
    }

    override fun refreshData(type: Int?) {
        initData()
    }

    override fun initData() {
        //        val mainData = SZWUtils.getJson(this, "checkData.json")
        //        val data = Gson().fromJson(mainData, ComleteCheckBean::class.java)
        //        viewModel.applyCheckBean?.completeCheckBean = data
        if ((viewModel.creditId + viewModel.dhId + viewModel.zfId).isNotEmpty()) DataCtrlClass.ApplyNet.getApplyCheck(
            this,
            viewModel.creditId,
            viewModel.dhId,
            viewModel.zfId
        ) {
            if (it != null) {
                viewModel.applyCheckBean?.completeCheckBean = it
            }
        }

    }

    /**
    ????????????????????????
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_LoginSuccess)])
    fun loginSuccess(str: String) {
        ((supportFragmentManager.findFragmentById(R.id.apply_nav_host_fragment) as NavHostFragment?)?.childFragmentManager?.primaryNavigationFragment as? MyBaseFragment)?.refreshData()
    }

    override fun onSupportNavigateUp() =
        Navigation.findNavController(this, R.id.apply_nav_host_fragment)
            .navigateUp() || super.onSupportNavigateUp()


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (SZWUtils.isShouldHideKeyboard(v, ev)) {
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                viewBind.viewOverlay.isFocusable = true
                viewBind.viewOverlay.isFocusableInTouchMode = true
                viewBind.viewOverlay.requestFocus()
                (v as EditText).clearFocus()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun onClick(id: Enum<*>) {
        val findNavController = Navigation.findNavController(this, R.id.apply_nav_host_fragment)
        viewModel.applyCheckBean?.resetCheck()
        viewModel.jsonObject = null
        //        val bind= when (viewModel.businessType) {
        //            ApplyModel.BUSINESS_TYPE_APPLY -> {
        //                (viewBind.titleMenuViewStub.binding as ActivityApplyIncludeCreditBinding)
        //            }
        //            ApplyModel.BUSINESS_TYPE_INVESTIGATE-> {
        //                (viewBind.titleMenuViewStub.binding as ActivityApplyIncludeCreditBinding)
        //            }
        //            else -> {
        //                (viewBind.titleMenuViewStub.binding as ActivityApplyIncludeCjPersonalBinding)
        //            }
        //        }
        val fragmentId = when (id) {
            XXGK/* "????????????" */, SJBG/* "????????????" */, RCBG/* "????????????" */, YXJC/* "????????????" */ -> {
                viewModel.applyCheckBean?.xxgkCheck = true
                R.id.customerInfoFragment
            }
            GSXX/*"????????????"*/ -> {
                viewModel.applyCheckBean?.gsxxCheck = true
                R.id.GSXXFragment
            }
            JTCY/*"????????????"*/ -> {
                viewModel.applyCheckBean?.jtcyCheck = true
                R.id.familyListFragment
            }
            SXSQD/*"???????????????"*/ -> {
                viewModel.applyCheckBean?.sxsqdCheck = true
                R.id.creditApplyFormFragment
            }
            WHYW /*"????????????"*/ -> {
                viewModel.applyCheckBean?.whywCheck = true
                R.id.bankDataFragment
            }
            FXTC/*"????????????"*/ -> {
                viewModel.applyCheckBean?.fxtcCheck = true
                R.id.riskFragment
            }

            ZXGL/*"????????????"*/ -> {
                viewModel.applyCheckBean?.zxglCheck = true
                R.id.creditManagerFragment
            }
            DBQK/*"????????????"*/ -> {
                viewModel.applyCheckBean?.dbdyCheck = true
                R.id.guaranteeFragment
            }
            YXZL/*"????????????"*/ -> {
                viewModel.applyCheckBean?.yxzlCheck = true
                R.id.picListFragment
            }
            CSJG/*"????????????"*/ -> {
                viewModel.applyCheckBean?.csjgCheck = true
                R.id.trialResultFragment
            }
            NSWD/*"????????????/????????????"*/ -> {
                viewModel.applyCheckBean?.nswdCheck = true
                R.id.vodFragment
            }
            SDDC/*"????????????"*/ -> {
                viewModel.applyCheckBean?.sddcCheck = true
                R.id.SDDCFragment
            }
            LLSR/*"????????????"*/ -> {
                viewModel.applyCheckBean?.llsrCheck = true
                R.id.LLSRFragment
            }
            SXYKH/*"???????????????"*/ -> {
                viewModel.applyCheckBean?.sxykhCheck = true
                R.id.SXYKHFragment
            }
            XJL/*"?????????"*/ -> {
                viewModel.applyCheckBean?.xjlCheck = true
                R.id.XJLFragment
            }
            ZCFZ/*"????????????"*/ -> {
                viewModel.applyCheckBean?.zcfzCheck = true
                R.id.ZCFZFragment
            }
            SYJB/*"????????????"*/ -> {
                viewModel.applyCheckBean?.syjbCheck = true
                R.id.SYJBFragment
            }
            CWJB/*"????????????"*/ -> {
                viewModel.applyCheckBean?.cwjbCheck = true
                R.id.CWJBFragment
            }
            PJZB/*"????????????"*/ -> {
                viewModel.applyCheckBean?.pjzbCheck = true
                R.id.PJZBFragment
            }
            DCBG/*"????????????"*/ -> {
                viewModel.applyCheckBean?.dcbgCheck = true
                R.id.ReportLineFragment
            }

            DCJL/*"????????????"*/ -> {
                viewModel.applyCheckBean?.dcjlCheck = true
                R.id.DCJLFragment
            }
            ZFXX/*"????????????"*/ -> {
                viewModel.applyCheckBean?.jtcyCheck = true
                R.id.ZFInfoFragment
            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay21) -> {
//                viewModel.applyCheckBean?.jbxxCheck = true
//                R.id.BaseInfoFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay22) -> {
//                viewModel.applyCheckBean?.zjlxCheck = true
//                R.id.EmptyFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay23) -> {
//                viewModel.applyCheckBean?.qyydCheck = true
//                R.id.CompanyElectricFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay24) -> {
//                viewModel.applyCheckBean?.qyxxcjCheck = true
//                R.id.CompanyInfoCollectFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay25) -> {
//                viewModel.applyCheckBean?.qyygrsCheck = true
//                R.id.CompanyPersonnelFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay26) -> {
//                viewModel.applyCheckBean?.qyygzCheck = true
//                R.id.CompanyMonthPayFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay27) -> {
//                viewModel.applyCheckBean?.frjgjcyCheck = true
//                R.id.CompanyKeyMembersFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay28) -> {
//                viewModel.applyCheckBean?.jyjlCheck = true
//                R.id.JianYanJieLunFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay29) -> {
//                viewModel.applyCheckBean?.xcjyyyCheck = true
//                R.id.EmptyFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay30) -> {
//                viewModel.applyCheckBean?.xcjyybCheck = true
//                R.id.XianChangJianYanBiaoFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay31) -> {
//                viewModel.applyCheckBean?.qywqdcbCheck = true
//                R.id.CompanyWenJuanDiaoChaBiaoFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay32) -> {
//                viewModel.applyCheckBean?.jyxxCheck = true
//                R.id.JYXXFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay33) -> {
//                viewModel.applyCheckBean?.xyqkCheck = true
//                R.id.XYQKFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay34) -> {
//                viewModel.applyCheckBean?.edcsCheck = true
//                R.id.EDCSFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay35) -> {
//                viewModel.applyCheckBean?.lldjCheck = true
//                R.id.EmptyFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay36) -> {
//                viewModel.applyCheckBean?.htqyCheck = true
//                R.id.SigningContractListFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay37) -> {
//                viewModel.applyCheckBean?.lylxCheck = true
//                R.id.CreditVodListFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay38) -> {
//                viewModel.applyCheckBean?.lgfkCheck = true
//                R.id.LGFKFragment
//            }
////            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay39) -> {
////                viewModel.applyCheckBean?.jcqkCheck = true
////                R.id.CheckInfoFragment
////            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay40) -> {
//                viewModel.applyCheckBean?.xxyzpCheck = true
//                R.id.XinXiYuanZhaoPianFragment
//            }
            SXPY /*????????????*/ -> {
                viewModel.applyCheckBean?.wjdcCheck = true
                R.id.WenJuanDiaoChaFragment
            }
////            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay42) -> {
////                viewModel.applyCheckBean?.wjhzCheck = true
////                R.id.WenJuanHuiZongFragment
////            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay43) -> {
//                viewModel.applyCheckBean?.pyqzCheck = true
//                R.id.PingYiQianZiFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay44) -> {
//                viewModel.applyCheckBean?.edbdCheck = true
//                R.id.WenJuanHuiZongFragment
//            }
//            menuBind?.root?.findViewById<ConstraintLayout>(R.id.constraintLay45) -> {
//                viewModel.applyCheckBean?.bdqzCheck = true
//                R.id.PingYiQianZiFragment
//            }
            DCBG_SIMPLE/*"????????????-?????????"*/ -> {
                viewModel.applyCheckBean?.dcbgCheck = true
                R.id.ReportLineFragment
            }
            ClassType_DZ.GLSX/*"????????????-????????????"*/ -> {
                viewModel.applyCheckBean?.glsxCheck = true
                R.id.GLSXFragment
            }
            ClassType_DZ.HTXX/*"????????????-????????????"*/ -> {
                viewModel.applyCheckBean?.htxxCheck = true
                R.id.ReportLineFragment
            }
            ClassType_DZ.DBXX/*"????????????-??????"*/ -> {
                viewModel.applyCheckBean?.dbdyCheck = true
                R.id.ReportLineFragment
            }
            ClassType_DZ.LLCS/*"????????????-????????????"*/ -> {
                viewModel.applyCheckBean?.llcsCheck = true
                R.id.ReportLineFragment
            }
            ClassType_DZ.RLSB/*"????????????-????????????"*/ -> {
                viewModel.applyCheckBean?.rlsbCheck = true
                R.id.ReportLineFragment
            }
            ClassType_DZ.YXZL/*"????????????-????????????"*/ -> {
                viewModel.applyCheckBean?.yxzlCheck = true
                R.id.ReportLineFragment
            }
            else -> {
                R.id.EmptyFragment
            }
        }
        when (businessType) {
            ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER -> {
                when (fragmentId) {
                    R.id.LGFKFragment -> {
                        viewModel.businessType = ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_LGFK
                    }
                    R.id.creditManagerFragment -> {
                        viewModel.businessType = ApplyModel.BUSINESS_TYPE_CREDIT_MANAGER_ZXGL
                    }
                    else -> {
                        viewModel.businessType = businessType
                    }
                }
            }
        }
        val build = NavOptions.Builder().setPopUpTo(fragmentId, true).build()
        findNavController.navigate(fragmentId, null, build)
    }

    override fun onDestroy() {
        RxBus.get().post(Constants.BusAction.Bus_Refresh_List, Constants.BusAction.Bus_Refresh_List)
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            213 -> {
                ZipService.bindService(applicationContext, object : ServiceConnection {
                    override fun onServiceConnected(name: ComponentName, service: IBinder) {
                        val binder = (service as ZipService.ZipBinder)
                        binder.start(
                            this@ApplyActivity, data?.data ?: Uri.EMPTY, viewModel.creditId
                                ?: "", viewModel.businessType
                        )
                    }

                    override fun onServiceDisconnected(name: ComponentName) {}
                })
            }
            else -> {
            }
        }

    }

}
