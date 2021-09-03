//package com.daqsoft.provider.xpopup
//
//import android.content.Context
//import android.graphics.Rect
//import android.view.View
//import android.widget.TextView
//import androidx.recyclerview.widget.GridLayoutManager
//import androidx.recyclerview.widget.RecyclerView
//import com.daqsoft.provider.R
//import com.daqsoft.provider.adapter.PlayTypeChooseAdapter
//import com.daqsoft.provider.bean.PlayChooseType
//import com.daqsoft.provider.dp
//import com.daqsoft.provider.view.BasePopupWindow
//
///**
// * 游玩选择框
// */
//class PlayTypeChoosePopup  : BasePopupWindow {
//
//
//    constructor(context: Context) : super(context)
//
//    private var topdatas = mutableListOf<PlayChooseType>()
//
//    private var bottomdatas = mutableListOf<PlayChooseType>()
//
//    private val listTopAdapter : PlayTypeChooseAdapter by lazy { PlayTypeChooseAdapter() }
//
//    private val listBottomAdapter : PlayTypeChooseAdapter by lazy { PlayTypeChooseAdapter() }
//
//    private var topBean: PlayChooseType?=null
//
//    private var buttopmBean: PlayChooseType?=null
//
//    private var title:String?=null
//
//    private var topTitle:String?=null
//
//    private var  bottomTitle:String?=null
//
//    override fun getMaxHeight(): Int {
//        return (XPopupUtils.getScreenHeight(context) * .65f).toInt()
//    }
//
//    fun setData(
//        topdatas: MutableList<PlayChooseType>,
//        bottomdatas: MutableList<PlayChooseType>,
//        leaveState: PlayChooseType?,
//        PlayChooseType: PlayChooseType?
//    ){
//        this.topdatas = topdatas
//        this.bottomdatas = bottomdatas
//        if(leaveState==null){
//            setSelectedPositionTop(topdatas[0])
//        }else{
//            setSelectedPositionTop(leaveState)
//        }
//        if(PlayChooseType==null){
//            setSelectedPosition(bottomdatas[0])
//        }else{
//            setSelectedPosition(PlayChooseType)
//        }
//
//    }
//
//    override fun getImplLayoutId(): Int {
//        return R.layout.layout_play_choose
//    }
//
//    override fun initPopupContent() {
//        super.initPopupContent()
//
//        val tv_cancel = findViewById<TextView>(R.id.tv_cancel)
//
//        val tv_ensure = findViewById<TextView>(R.id.tv_ensure)
//
//
//        if (!title.isNullOrBlank()) {
//            findViewById<TextView>(R.id.tv_title).text = title
//        }
//        if (!topTitle.isNullOrBlank()) {
//            findViewById<TextView>(R.id.tv_title1).text = topTitle
//        }
//        if (!bottomTitle.isNullOrBlank()) {
//            findViewById<TextView>(R.id.tv_title2).text = bottomTitle
//        }
//
//        tv_cancel.setOnClickListener {
//            dismiss()
//        }
//
//        tv_ensure.setOnClickListener {
//            dismiss()
//        }
//
//        val topRecycleView = findViewById<RecyclerView>(R.id.recycler_view1)
//        topRecycleView.apply {
//            layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
//            adapter = listTopAdapter.apply {
//                addItemDecoration(object : RecyclerView.ItemDecoration() {
//                    override fun getItemOffsets(
//                        outRect: Rect,
//                        view: View,
//                        parent: RecyclerView,
//                        state: RecyclerView.State
//                    ) {
//                        outRect.bottom = 12.dp
//                    }
//                })
//                setNewData(topdatas)
//                setItemOnClickListener(object : PlayTypeChooseAdapter.ItemOnClickListener {
//                    override fun onClick(position: Int, bean: PlayChooseType) {
//                        topBean = bean
//                    }
//                })
//            }
//        }
//        val topRecycleView1 = findViewById<RecyclerView>(R.id.recycler_view2)
//        topRecycleView1.apply {
//            layoutManager = GridLayoutManager(context!!, 3, GridLayoutManager.VERTICAL, false)
//            adapter = listBottomAdapter.apply {
//                addItemDecoration(object : RecyclerView.ItemDecoration() {
//                    override fun getItemOffsets(
//                        outRect: Rect,
//                        view: View,
//                        parent: RecyclerView,
//                        state: RecyclerView.State
//                    ) {
//                        outRect.bottom = 12.dp
//                    }
//                })
//                setNewData(bottomdatas)
//                setItemOnClickListener(object : PlayTypeChooseAdapter.ItemOnClickListener {
//                    override fun onClick(position: Int, bean: PlayChooseType) {
//                        buttopmBean = bean
//                    }
//                })
//            }
//        }
//    }
//
//    /**
//     * 设置选中位置
//     */
//    fun setSelectedPositionTop(bean: PlayChooseType) {
//        if (bean == null && topdatas.size > 0) {
//            listBottomAdapter?.setSelectedPosition(bottomdatas[0])
//        }
//        topdatas?.forEachIndexed { index, s ->
//            if (bean?.id == s.id) {
//                listTopAdapter.setSelectedPosition(bean)
//            }
//        }
//    }
//
//    fun setSelectedPosition(bean: PlayChooseType?) {
//        if (bean == null && bottomdatas.size > 0) {
//            listBottomAdapter?.setSelectedPosition(bottomdatas[0])
//        }
//        bottomdatas?.forEachIndexed { index, s ->
//            if (bean?.id == s.id) {
//                listBottomAdapter.setSelectedPosition(bean)
//            }
//        }
//    }
//
//
//    private var itemOnClickListener : ItemOnClickListener? = null
//
//    fun setItemOnClickListener(listener: ItemOnClickListener){
//        this.itemOnClickListener = listener
//    }
//
//    interface ItemOnClickListener{
//        fun onClick(topBean: PlayChooseType?, bean2: PlayChooseType?)
//    }
//
//
//}