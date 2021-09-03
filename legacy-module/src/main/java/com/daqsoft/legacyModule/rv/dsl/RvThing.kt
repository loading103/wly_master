package com.daqsoft.legacyModule.rv.dsl

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.daqsoft.legacyModule.rv.flow.SpecLayoutManager
import com.daqsoft.legacyModule.rv.flow.SpecSpaceItemDecoration


private const val LINEAR_LAYOUT = 0
private const val GRID_LAYOUT = 1
private const val STAGGERED_LAYOUT = 2
private const val FLOW_LAYOUT = 3

/**
 * This function is used to create a Linear list.
 *
 *@param clear If true, all old items will be cleared and new items will be re-created.
 *             Otherwise, it will continue to add new items to the original data
 *
 *@param block Item dsl
 */
internal fun RecyclerView.linear(clear: Boolean = true, block: RvDsl.() -> Unit) {
    initDsl(this, clear, LINEAR_LAYOUT, block)
}

/**
 * This function is used to create a Grid list.
 *
 *@param clear If true, all old items will be cleared and new items will be re-created.
 *             Otherwise, it will continue to add new items to the original data
 *
 *@param block Item dsl
 */
internal fun RecyclerView.grid(clear: Boolean = true, block: RvDsl.() -> Unit) {
    initDsl(this, clear, GRID_LAYOUT, block)
}

/**
 * This function is used to create a Stagger list.
 *
 *@param clear If true, all old items will be cleared and new items will be re-created.
 *             Otherwise, it will continue to add new items to the original data
 *
 *@param block Item dsl
 */
internal fun RecyclerView.stagger(clear: Boolean = true, block: RvDsl.() -> Unit) {
    initDsl(this, clear, STAGGERED_LAYOUT, block)
}


/**
 * SpaceItemDecoration's spaceRow dp value
 */
private var spaceRow = 5


/**
 *
 *SpaceItemDecoration's spaceClo dp value
 */
private var spaceClo = 5

/**
 * This function is used to create a flow list.
 *
 *@param clear If true, all old items will be cleared and new items will be re-created.
 *             Otherwise, it will continue to add new items to the original data
 *
 *@param spaceRow SpaceItemDecoration's spaceRow dp value
 *
 *@param spaceClo SpaceItemDecoration's spaceClo dp value
 *
 *@param block Item dsl
 */
internal fun RecyclerView.flow(clear: Boolean = true, spaceRow: Int = 5, spaceClo: Int = 5, block: RvDsl .() -> Unit) {
    initDsl(this, clear, FLOW_LAYOUT, block)
}

private fun initDsl(target: RecyclerView, clear: Boolean, type: Int, block: RvDsl.() -> Unit) {

    checkAdapter(target)

    val adapter = target.adapter as RvDslAdapter

    val dsl = if (clear) {
        RvDsl(mutableListOf())
    } else {
        RvDsl(adapter.data)
    }

    dsl.block()

    initLayoutManager(target, dsl, type)
    adapter.submitList(dsl.dataSet)
}

private fun checkAdapter(target: RecyclerView) {
    if (target.adapter == null) {
        target.adapter = RvDslAdapter()
    }

    if (target.adapter !is RvDslAdapter) {
        throw IllegalStateException("Adapter must be RvDslAdapter")
    }
}

private fun initLayoutManager(target: RecyclerView, dsl: RvDsl, type: Int) {
    var needNew = true
    val source = target.layoutManager
    if (source != null) {
        needNew = checkNeedNew(source, dsl)
    }

    if (needNew) {
        val layoutManager = newLayoutManager(type, target, dsl)
        configureLayoutManager(layoutManager, dsl)
        target.layoutManager = layoutManager
    }
}

private fun newLayoutManager(type: Int, target: RecyclerView, dsl: RvDsl): RecyclerView.LayoutManager {

    while (true) {
        if (target.itemDecorationCount > 0)
            target.removeItemDecorationAt(0)
        else
            break
    }

    return when (type) {
        LINEAR_LAYOUT -> LinearLayoutManager(target.context, dsl.orientation, dsl.reverse)
        GRID_LAYOUT -> GridLayoutManager(target.context, dsl.spanCount, dsl.orientation, dsl.reverse)
        STAGGERED_LAYOUT -> StaggeredGridLayoutManager(dsl.spanCount, dsl.orientation)
        FLOW_LAYOUT -> {
            target.addItemDecoration(SpecSpaceItemDecoration(target.context, spaceRow, spaceClo))
            SpecLayoutManager()
        }
        else -> throw IllegalStateException("This should never happen!")
    }
}

private fun configureLayoutManager(layoutManager: RecyclerView.LayoutManager, dsl: RvDsl) {
    if (layoutManager is GridLayoutManager) {
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return dsl.dataSet[position].gridSpanSize()
            }
        }
    }
}

private fun checkNeedNew(source: RecyclerView.LayoutManager, dsl: RvDsl): Boolean {
    return when (source) {
        is GridLayoutManager -> dsl.checkGrid(source)            //Grid must check before Linear
        is LinearLayoutManager -> dsl.checkLinear(source)
        is StaggeredGridLayoutManager -> dsl.checkStagger(source)
        is SpecLayoutManager -> true
        else -> throw  IllegalStateException("This should never happen!")
    }
}
