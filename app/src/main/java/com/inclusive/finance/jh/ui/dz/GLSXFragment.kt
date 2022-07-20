package com.inclusive.finance.jh.ui.dz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.google.gson.JsonObject
import com.inclusive.finance.jh.DataCtrlClass
import com.inclusive.finance.jh.adapter.ItemBaseListCardAdapter
import com.inclusive.finance.jh.adapter.ItemBaseTypeAdapter
import com.inclusive.finance.jh.base.BaseActivity
import com.inclusive.finance.jh.base.MyBaseFragment
import com.inclusive.finance.jh.bean.BaseTypeBean
import com.inclusive.finance.jh.bean.model.ApplyModel
import com.inclusive.finance.jh.config.Urls
import com.inclusive.finance.jh.databinding.FragmentGlsxBinding
import com.inclusive.finance.jh.databinding.FragmentSxykhBinding
import com.inclusive.finance.jh.databinding.ItemBaseListCardBinding
import com.inclusive.finance.jh.interfaces.PresenterClick
import com.inclusive.finance.jh.utils.SZWUtils
import org.jetbrains.anko.support.v4.act

/**
 * 关联授信
 * */
class GLSXFragment : MyBaseFragment(), PresenterClick {
    lateinit var adapter: ItemBaseTypeAdapter<BaseTypeBean>
    lateinit var viewModel: ApplyModel
    lateinit var viewBind: FragmentGlsxBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        viewBind = FragmentGlsxBinding.inflate(inflater, container, false).apply {
            presenterClick = this@GLSXFragment
            viewModel = ViewModelProvider(act).get(ApplyModel::class.java)
            data = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return viewBind.root
    }

    var subscribeChildLayoutDrawListener = { adapter: ItemBaseListCardAdapter<JsonObject>,baseTypeBean:BaseTypeBean ->
        adapter.setOnItemChildClickListener { _, view, position ->
            adapter.checkListBean.forEach { it.checked=false }
            adapter.checkListBean[position].checked=true
        };
        { holder: BaseViewHolder, item: JsonObject ->
            val viewBind = DataBindingUtil.getBinding<ItemBaseListCardBinding>(holder.itemView)
            viewBind?.btMore?.visibility = View.GONE
            viewBind?.btChange?.visibility = View.GONE
            viewBind?.btSeeOnly?.visibility = View.GONE
            viewBind?.btPdf?.visibility = View.GONE
        }
    }

    override fun initView() {
        adapter = ItemBaseTypeAdapter(this@GLSXFragment)
        viewBind.mRecyclerView.layoutManager = LinearLayoutManager(act)
        viewBind.mRecyclerView.adapter = adapter
        adapter.subscribeChildLayoutDrawListener = subscribeChildLayoutDrawListener
    }

    override fun initData() {
        DataCtrlClass.KHGLNet.getBaseTypePoPList(
            requireActivity(),
            url = Urls.getSXYKH,
            keyId = viewModel.keyId
        ) {
            if (it != null) {
                SZWUtils.setSeeOnlyMode(viewModel, it)
                adapter.setNewInstance(it)
            }
        }
    }

    override fun saveData() {
        DataCtrlClass.KHGLNet.saveBaseTypePoPList(
            context,
            Urls.saveSXYKH,
            adapter.data,
            keyId = viewModel.keyId
        ) {
            if (it != null) {
                if (isAdded)
                    (activity as BaseActivity).refreshData()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            viewBind.btSave -> {
                saveData()
            }
        }
    }

}