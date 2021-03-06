package com.inclusive.finance.jh.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import com.hwangjr.rxbus.annotation.Subscribe
import com.hwangjr.rxbus.annotation.Tag
import com.hwangjr.rxbus.thread.EventThread
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.R
import com.inclusive.finance.jh.adapter.ItemBaseListAdapter
import com.inclusive.finance.jh.adapter.ItemTextAdapter
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.bean.model.MainActivityModel
import com.inclusive.finance.jh.config.Constants
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentShouliListBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.pop.*
import com.inclusive.finance.jh.utils.SZWUtils
import com.inclusive.finance.jh.utils.StatusBarUtil
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.listener.OnRefreshLoadMoreListener
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import org.jetbrains.anko.support.v4.act
import java.util.*


/**
 * ๅ็ๅ่กจ
 * */
class ShouLiListFragment : MyBaseFragment(), PresenterClick, OnRefreshLoadMoreListener {
    private var statue: String = "0"
    lateinit var viewModel: MainActivityModel
    lateinit var viewBind: FragmentShouliListBinding
    private var refreshState = Constants.RefreshState.STATE_REFRESH
    private var currentPage = 1
    lateinit var mAdapter: ItemBaseListAdapter<JsonObject>
    var event: Lifecycle.Event? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewModel = ViewModelProvider(act).get(MainActivityModel::class.java)

        viewBind = FragmentShouliListBinding.inflate(inflater, container, false).apply {
            data = viewModel
            presenterClick = this@ShouLiListFragment
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    override fun initView() {
        viewBind.actionBarCustom.toolbar.setNavigationOnClickListener {
            Navigation.findNavController(act, R.id.my_nav_host_fragment).navigateUp()
        }
        StatusBarUtil.setPaddingSmart(act, viewBind.actionBarCustom.appBar)
        viewBind.actionBarCustom.mTitle.text = "ไบ็ปด็?ๅ็"
        mAdapter = ItemBaseListAdapter(this)
        //        mAdapter.loadMoreModule.setOnLoadMoreListener(this)
        // ๅฝๆฐๆฎไธๆปกไธ้กตๆถ๏ผๆฏๅฆ็ปง็ปญ่ชๅจๅ?่ฝฝ๏ผ้ป่ฎคไธบtrue๏ผ
        //        mAdapter.loadMoreModule.isEnableLoadMoreIfNotFullPage = false
        viewBind.layoutBaseList.mRecyclerView.adapter = mAdapter
        viewBind.mRefreshLayout.setOnRefreshLoadMoreListener(this)
//        mAdapter.itemRecyclerViewBackGroundColor={ item, holder ->
//            when {
//                SZWUtils.getJsonObjectBoolean(item, "isCheck") -> R.color.color_main_orangeAlpha
//                SZWUtils.getJsonObjectString(item, "status")=="ๅทฒ่ถๆถ" -> R.color.color_main_redAlpha
//                holder.adapterPosition % 2 == 0 -> R.color.white
//                else -> R.color.line2
//            }
//        }
        mAdapter.textListItemConfig = { item, adapterPosition ->
            ItemTextAdapter.TextListItemConfig().apply {
                if (SZWUtils.getJsonObjectString(item, "status") == "ๅทฒ่ถๆถ") {
                    textDefaultColor = R.color.colorPrimary
                }
            }

        }
        initStatusView()
        //        val mainData = SZWUtils.getJson(context, "listData.json")
        //        val data = Gson().fromJson<BaseListBean>(mainData, BaseListBean::class.java)
        //        adapter.titleList = data.titleList
        //        adapter.setNewInstance(data.list)

        //                val mainData = SZWUtils.getJson(context, "ๅพๅไบ้กน.json")
        //                val list = Gson().fromJson<MutableList<BaseTypeBean>>(
        //                    mainData,
        //                    object : TypeToken<ArrayList<BaseTypeBean>>() {}.type
        //                )
        //                mAdapter.titleList = list[0].listBean?.titleList
        //                mAdapter.setNewInstance(list[0].listBean?.list)
    }
    override fun initData() {
            DataCtrlClass.YXNet.getShouliList(requireActivity(), currentPage, viewBind.etSearch.text.toString(),viewBind.etJl.text.toString(),processStatus) {
            viewBind.mRefreshLayout.finishRefresh()
            if (it != null) {
                if (refreshState == Constants.RefreshState.STATE_REFRESH) {
                    mAdapter.initTitleLay(context, viewBind.layoutBaseList.root, it) {
                        mAdapter.setNewInstance(it.list)
                    }
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

        }
    }
    private fun initStatusView() {
        val dataList = ArrayList<BaseTypeBean.Enum12>()
        viewBind.tvStatus.isClickable = true
        dataList.clear()
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "ๅพๅค็"
            keyName = "0"
        })
        dataList.add(BaseTypeBean.Enum12().apply {
            valueName = "ๅทฒๅค็"
            keyName = "1"

        })

        viewBind.tvStatus.setOnClickListener {
            DownPop(context, enums12 = dataList, checkedTextView = it as AppCompatCheckedTextView, isSingleChecked = true) { k, v, p ->
                processStatus = k
            }.showPopupWindow(it)
        }
    }

    var processStatus = "0"

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

    private var mLastClickTime: Long = 0
    private val TIME_INTERVAL = 500L
    override fun onClick(v: View?) {

        //        PictureSelector.create(this@ApplyListFragment).externalPictureVideo("http://7xjmzj.com1.z0.glb.clouddn.com/20171026175005_JObCxCE2.mp4");

        if (v == viewBind.btSearch) {
            if (System.currentTimeMillis() - mLastClickTime > TIME_INTERVAL) {
                viewBind.mRefreshLayout.autoRefresh()
                mLastClickTime = System.currentTimeMillis()
            }
            return
        }
        if (v != null) SZWUtils.getJsonObjectBeanFromList(mAdapter.data) { jsonObject ->
            val flag = SZWUtils.getJsonObjectString(jsonObject, "flag")
            val mtime = SZWUtils.getJsonObjectString(jsonObject, "handleDate")
            val mid = SZWUtils.getJsonObjectString(jsonObject, "id")

            when (v) {
                viewBind.chipYj -> {
                    when (flag) {
                        "1" -> {
                            GHQYJPop(context,json=jsonObject,businessType = ApplyModel.BUSINESS_TYPE_SHOLI){
                                refreshData()
                            }.show(childFragmentManager, this.javaClass.name)
                        }
                        else -> context?.let { SZWUtils.showSnakeBarMsg("่ฏฅๆต็จ็ถๆไธๆ?ๆณๆไฝ") }
                    }
                }

                viewBind.chipYwsl->{
                    ConfirmPop(mContext, "ๆฏๅฆๅค็๏ผ") { confirm ->
                        if (confirm) {
                            DataCtrlClass.YXNet.updateTime(context, Urls.save_update_time,mid){
                                refreshData()
                            }
                        }
                    }.show(childFragmentManager, this.javaClass.name)
                }
            }
        }

    }

    /**
    ่ฟๅๅๅทๆฐๆฐๆฎ๏ผ
     */
    @Subscribe(thread = EventThread.MAIN_THREAD, tags = [Tag(Constants.BusAction.Bus_Refresh_List)])
    fun backRefresh(str: String) {
        viewBind.mRefreshLayout.autoRefresh()
    }


}