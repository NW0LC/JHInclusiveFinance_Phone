package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.IRouter
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.databinding.FragmentShoujianList2Binding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
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
import java.util.*


/**
 * 首检列表
 * */
@AndroidEntryPoint
class ShouJianList2Fragment : MyBaseFragment(), OnItemChildClickListener,
    OnRefreshLoadMoreListener {
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentShoujianList2Binding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListCardAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act)[MainActivityModel::class.java]

        viewBind = FragmentShoujianList2Binding.inflate(inflater, container, false).apply {
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    var subscribeChildLayoutDrawListener: (holder: BaseViewHolder, item: JsonObject) -> Unit = { holder, _ ->
        val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
        viewBind?.btMore?.visibility = View.VISIBLE
        viewBind?.btSeeOnly?.visibility = View.GONE
        viewBind?.btChange?.visibility = View.GONE

    }
    var dataList: MutableList<Filter>? = mutableListOf()
    override fun initView() {

        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "首检"
        mAdapter = ItemBaseListCardAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)

        // 当数据不满一页时，是否继续自动加载（默认为true）
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        mAdapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
        viewBind.mRecyclerView.adapter = mAdapter
        mAdapter.setOnItemChildClickListener(this)
        viewBind.mRefreshLayout.setEnableAutoLoadMore(true)
        viewBind.mRefreshLayout.setEnableOverScrollBounce(true)

        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
        viewBind.actionBarCustom.toolbar.apply {
            inflateMenu(R.menu.add_menu)
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.action_open_filters -> {
                        val mFemant = findFiltersFragment()
                        mFemant?.showFiltersSheet()
                        true
                    }
                    else -> {
                        false
                    }
                }
            }
        }
//        mAdapter.itemRecyclerViewBackGroundColor={ item, holder ->
//            when {
//                SZWUtils.getJsonObjectBoolean(item, "isCheck") -> R.color.color_main_orangeAlpha
//                SZWUtils.getJsonObjectString(item, "color")=="1" -> R.color.color_main_redAlpha
//                holder.adapterPosition % 2 == 0 -> R.color.white
//                else -> R.color.line2
//            }
//        }
//        mAdapter.textListItemConfig = { item, adapterPosition ->
//            ItemTextAdapter.TextListItemConfig().apply {
//                when {
//                    SZWUtils.getJsonObjectString(item, "color") == "1" -> {
//                        textDefaultColor = R.color.colorPrimary
//                    }
//                    SZWUtils.getJsonObjectString(item, "color") == "2" -> {
//                        textDefaultColor = R.color.color_main_orange_1
//                    }
//                }
//            }
//
//        }
        initStatusView()
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
        DataCtrlClass.SJNet.getJGMCList(context) {
            if (it != null) {
                var index = 0
                jgList = arrayListOf()
                it.forEach { jsonObject ->
                    jgList.add(BaseTypeBean.Enum12().apply {
                        keyName = SZWUtils.getJsonObjectString(jsonObject, "orgCode")
                        valueName = SZWUtils.getJsonObjectString(jsonObject, "departName")
                        val elements0 = Filter.TagFilter(Tag("1$index", "orgCode", "机构", valueName = "" + valueName, keyName = "0$keyName", isSingleCheck = true))
                        dataList?.add(elements0)
                        index++
                    })
                }

                if (jgList.size > 0) {
                    orgCode = jgList[0].keyName
                }
                val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]
                lifecycleScope.launch {
                    delay(500)
                    dataList?.let { it1 -> viewModel.setSupportedFilters(it1) }
                }
                viewBind.mRefreshLayout.autoRefresh()
            }
        }
    }

    var jgList = ArrayList<BaseTypeBean.Enum12>()

    val listMenuDatas = mutableListOf<String>().apply {
        add("检查")
        add("查看检查")
        add("提交")
        add("查看进度")

    }

    private fun initStatusView() {

        val elements1 = Filter.TagFilter(Tag("0", "checkStatus", "检验状态", valueName = "检验中", keyName = "01", isSingleCheck = true))
        val elements2 = Filter.TagFilter(Tag("2", "checkStatus", "检验状态", valueName = "异常处理中", keyName = "02", isSingleCheck = true))
        val elements3 = Filter.TagFilter(Tag("3", "checkStatus", "检验状态", valueName = "流程中", keyName = "03", isSingleCheck = true))
        val elements4 = Filter.TagFilter(Tag("4", "checkStatus", "检验状态", valueName = "完成", keyName = "04", isSingleCheck = true))

        val elements5 = Filter.TagFilter(Tag("5", "processStatus", "流程状态", valueName = "全部", keyName = "", isSingleCheck = true))
        val elements6 = Filter.TagFilter(Tag("6", "processStatus", "流程状态", valueName = "异常", keyName = "02", isSingleCheck = true))
        val elements7 = Filter.TagFilter(Tag("7", "processStatus", "流程状态", valueName = "正常", keyName = "01", isSingleCheck = true))
//        val elements8 = Filter.TagFilter(Tag("8", "dbztStatus", "是否待办", valueName = "是", keyName = "1", isSingleCheck = true))
//        val elements9= Filter.TagFilter(Tag("9", "dbztStatus", "是否待办", valueName = "否", keyName = "0", isSingleCheck = true))
//        dataList?.add(elements1)
        dataList?.add(elements2)
        dataList?.add(elements3)
        dataList?.add(elements4)
        dataList?.add(elements5)
        dataList?.add(elements6)
        dataList?.add(elements7)
//        dataList?.add(elements8)
//        dataList?.add(elements9)

    }

    private var searchStr = ""
    private var checkStatus = "02"
    private var processStatus = ""
    private var dbztStatus = ""

    private var orgCode = ""
    private var resultCountCallBack: ((count: String) -> Unit)?=null
    override fun initData() {

        DataCtrlClass.SJNet.getSJList(
            context = requireActivity(), pageNum = currentPage, searchStr = searchStr, orgCode = orgCode, status = processStatus, checkResult = checkStatus,daibanstatus = dbztStatus
        ) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.setListData(bean = it, list = it.list)
                } else {
                    mAdapter.addData(it.list)

                }
                if (!it.list.isNullOrEmpty()) {
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
        viewBind.root.postDelayed({ initData() }, 0)
    }

    override fun onLoadMore(refreshLayout: RefreshLayout) {
        refreshState = Constants.RefreshState.STATE_LOAD_MORE
        initData()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        val jsonObject = mAdapter.data[position]
        val checkFlag = SZWUtils.getJsonObjectString(jsonObject, "checkFlag")
        val flag = SZWUtils.getJsonObjectString(jsonObject, "flag")
        when (view.id) {
            R.id.bt_more -> {
                BaseListMenuPop(requireActivity(), listMenuDatas) {
                    when (listMenuDatas[it]) {
                        "检查" -> {
                            when {
                                checkFlag.contains("1") -> IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_SJ, false)
                                else -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                            }

                        }
                        "查看检查" -> {
                            IRouter.goF(view, R.id.action_to_applyActivity, SZWUtils.getJsonObjectString(jsonObject, "id"), ApplyModel.BUSINESS_TYPE_SJ, true)
                        }
                        "提交" -> {
                            when {
                                !flag.contains("1") -> context?.let { SZWUtils.showSnakeBarMsg("该流程状态下无法操作") }
                                else -> DataCtrlClass.SXSPNet.getSXSPById(context, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_SJ_PERSONAL, type = "4") { configurationBean ->
                                    if (configurationBean != null) {
                                        ProcessProcessingPop(context, configurationBean, keyId = SZWUtils.getJsonObjectString(jsonObject, "id"), businessType = ApplyModel.BUSINESS_TYPE_SJ_PERSONAL) {
                                            viewBind.mRefreshLayout.autoRefresh()
                                        }.show(childFragmentManager, this.javaClass.name)
                                    }
                                }
                            }
                        }
                        "查看进度" -> {
                            CheckProgressPop(context, SZWUtils.getJsonObjectString(jsonObject, "id"), type = "4", businessType = ApplyModel.BUSINESS_TYPE_SJ).show(childFragmentManager, this.javaClass.name)

                        }
                        else -> {
                        }
                    }
                }.showPopupWindow(view)
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

        filtersFragment = FiltersFragment(this) { list, str ,c->
            resultCountCallBack=c
            searchStr = str
            checkStatus = ""
            processStatus = ""
            dbztStatus = ""
            orgCode = ""
            list.forEach {
                val tagFilter = it.filter as Filter.TagFilter
                when (tagFilter.tag.categoryId) {
                    "checkStatus" -> {
                        checkStatus = tagFilter.tag.keyName
                    }
                    "processStatus" -> {
                        processStatus = tagFilter.tag.keyName
                    }
                    "orgCode" -> {
                        orgCode = tagFilter.tag.keyName
                    }
                    "dbztStatus" -> {
                        dbztStatus = tagFilter.tag.keyName
                    }
                }
            }
            refreshData()
        } //        }
        filtersFragment?.show(childFragmentManager, this.javaClass.name)

        return filtersFragment
    }

}