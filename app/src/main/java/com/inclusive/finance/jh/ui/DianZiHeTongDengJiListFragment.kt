package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.Clrlist
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentDianzihetongdnegjiBinding
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.ui.filter.Filter
import com.inclusive.finance.jh.ui.filter.FiltersFragment
import com.inclusive.finance.jh.ui.filter.SearchViewModel
import com.inclusive.finance.jh.ui.filter.Tag
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.anko.support.v4.act


/**
 * 电子合同登记列表
 * */
@AndroidEntryPoint
class DianZiHeTongDengJiListFragment : MyBaseFragment(), OnRefreshLoadMoreListener, OnItemChildClickListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentDianzihetongdnegjiBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    private var getListUrl = ""
    private var getEditUrl = ""
    private var saveUrl = ""
    private var deleteUrl = ""
    private var businessType: Int = ApplyModel.BUSINESS_TYPE_DIANZIHETONGDENGJI
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]
        viewBind = FragmentDianzihetongdnegjiBinding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        businessType = arguments?.getInt("businessType", ApplyModel.BUSINESS_TYPE_APPLY)
            ?: businessType
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.toolbar.apply {
            inflateMenu(R.menu.add_search_menu)
            setOnMenuItemClickListener {menu->
                when (menu.itemId) {
                    R.id.action_open_filters -> {
                        findFiltersFragment()?.showFiltersSheet()
                        true
                    }
                    R.id.action_add -> {
                        ApplyCheckPop(
                            context, this@DianZiHeTongDengJiListFragment, businessType = businessType
                        ) { str ->
                            refreshData()
                            IRouter.goF(
                                viewBind.root, R.id.action_to_applyActivity, str, businessType, false
                            )
                        }.show(childFragmentManager, this.javaClass.name)
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
        mAdapter = ItemBaseListCardAdapter(this)
        mAdapter.setOnItemChildClickListener(this) //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)


        //        val mainData = SZWUtils.getJson(context, "listData.json")
        //        val data = Gson().fromJson<BaseListBean>(mainData, BaseListBean::class.java)
        //        adapter.titleList = data.titleList
        //        adapter.setNewInstance(data.list)

        //                val mainData = SZWUtils.getJson(context, "待办事项.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //                mAdapter.titleList = list[0].listBean?.titleList
        //                mAdapter.setNewInstance(list[0].listBean?.list)
    }

    val listMenuDatas = mutableListOf<String>().apply {
        add("人脸识别")
        add("影像资料")
        add("开通线上签约")
        add("取消合同")
        add("合同生效")
        add("合同预览")
    }

    override fun initEvent() {
        when (businessType) {
            ApplyModel.BUSINESS_TYPE_DIANZIHETONGDENGJI -> {
                getListUrl = Urls.getList_HTDJ
                getEditUrl = Urls.getEdit_HTDJ
                saveUrl = Urls.save_HTDJ
                deleteUrl = Urls.delete_HTDJ
                viewBind.actionBarCustom.mTitle.text = "授信申请"
            }
        }

        val dataList: MutableList<Filter> //        val elements_1 = Filter.SearchFilter( //            Tag( //                "9",
        //                "客户姓名",
        //                hint = "请输入客户姓名或身份证号",
        //                valueName = "0",
        //                keyName = "0"
        //            )
        //        )
        val elements1 = Filter.TagFilter(Tag("1", "processStatus", "合同状态", valueName = "未生效", keyName = "未生效", isSingleCheck = true))
        val elements2 = Filter.TagFilter(Tag("2", "processStatus", "合同状态", valueName = "已生效", keyName = "已生效", isSingleCheck = true))
        val elements3 = Filter.TagFilter(Tag("3", "processStatus", "合同状态", valueName = "已失效", keyName = "已失效", isSingleCheck = true))
        dataList = mutableListOf(elements1, elements2, elements3)
        val viewModel = ViewModelProvider(this@DianZiHeTongDengJiListFragment)[SearchViewModel::class.java]
        lifecycleScope.launch {
            delay(500)
            viewModel.setSupportedFilters(dataList)
//            viewModel.toggleFilter(elements0, enabled = true, singleCheck = true)
        }

    }

    private var searchStr = ""
    private var processStatus = ""
    var resultCountCallBack: ((count: String) -> Unit)? = null
    override fun initData() {

        DataCtrlClass.HTNet.getHTDJList(
            requireActivity(), url = getListUrl, currentPage, idenNo = searchStr,  processStatus = processStatus) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.setListData(bean = it, list = it.list)
                } else {
                    mAdapter.addData(it.list)

                }
                if (it.list.isNotEmpty()) {
                    viewBind.mRefreshLayout.finishLoadMore()
                    currentPage++
                } else {
                    viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
                }
            } else {
                viewBind.mRefreshLayout.finishLoadMoreWithNoMoreData()
            }
            resultCountCallBack?.invoke(mAdapter.data.size.toString())
        }
    }

    override fun refreshData(type: Int?) {
        viewBind.mRefreshLayout.autoRefresh()
    }

    override fun onRefresh(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_REFRESH
        currentPage = 1
        viewBind.root.postDelayed({ initData() }, 200)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val jsonObjectString = SZWUtils.getJsonObjectString(jsonObject, "flag")
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "取消申请" -> {
                            when {
                                jsonObjectString.contains("0") -> context?.let {
                                    SZWUtils.showSnakeBarMsg(
                                        "该流程状态下无法操作"
                                    )
                                }
                                else -> ConfirmPop(context, "确定取消申请?") { confirm ->
                                    if (confirm) {
                                        val bean = Clrlist.ConfirmNodeBean().apply {
                                            relationid = SZWUtils.getJsonObjectString(jsonObject, "id")
                                            bz = "客户经理自主取消"
                                            czlx = "reject"
                                            type = "0"
                                        }
                                        DataCtrlClass.SXSQNet.deleteApply(requireActivity(), bean) {
                                            viewBind.mRefreshLayout.autoRefresh()
                                        }
                                    }
                                }.show(childFragmentManager, this.javaClass.name)
                            }

                        }
                        "签批进度" -> {
                            CheckProgressPop(
                                context, SZWUtils.getJsonObjectString(jsonObject, "id"), type = "1", businessType = businessType
                            ).show(childFragmentManager, this.javaClass.name)
                        }
                        "查看进度" -> {
                            CheckProgressPop(
                                context, SZWUtils.getJsonObjectString(jsonObject, "id"), type = SZWUtils.getBusinessType(businessType), businessType = businessType
                            ).show(childFragmentManager, this.javaClass.name)
                        }
                        "提交" -> {
                            when {
                                jsonObjectString.contains("0") -> context?.let {
                                    SZWUtils.showSnakeBarMsg(
                                        "该流程状态下无法操作"
                                    )
                                }
                                else -> DataCtrlClass.SXSPNet.getSXSPById(
                                    requireActivity(), keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = businessType, type = SZWUtils.getBusinessType(businessType)
                                ) { configurationBean ->
                                    if (configurationBean != null) {
                                        ProcessProcessingPop(
                                            context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = businessType
                                        ) {
                                            viewBind.mRefreshLayout.autoRefresh()
                                        }.show(childFragmentManager, this.javaClass.name)
                                    }
                                }
                            }

                        }
                        else -> {}
                    }
                }.showPopupWindow(view)
            }
            R.id.bt_seeOnly -> {
                IRouter.goF(
                    view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), businessType, true
                )
            }
            R.id.bt_change -> {
                when {
                    jsonObjectString.contains("2") -> IRouter.goF(
                        view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), businessType, false
                    )
                    else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                }

            }
        }
    }


    /**
    返回后刷新数据，
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [com.hwangjr.rxbus.annotation.Tag(Constants.BusAction.Bus_Refresh_List)])
    fun backRefresh(str: String) {
        viewBind.mRefreshLayout.autoRefresh()
    }

    private var filtersFragment: FiltersFragment? = null
    private fun findFiltersFragment(): FiltersFragment? { //        if (filtersFragment == null) {

        filtersFragment = FiltersFragment(this) { list, str, c ->
            resultCountCallBack = c
            searchStr = str
            processStatus = ""
            list.forEach {
                val tagFilter = it.filter as Filter.TagFilter
                when (tagFilter.tag.categoryId) {
                    "processStatus" -> {
                        processStatus = tagFilter.tag.keyName
                    }
                }
            }
            refreshData()
        } //        }

        filtersFragment?.show(childFragmentManager, this.javaClass.name)
        return filtersFragment
    }
}