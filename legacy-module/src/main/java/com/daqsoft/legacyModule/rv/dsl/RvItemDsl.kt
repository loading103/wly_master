package com.daqsoft.legacyModule.rv.dsl

import android.view.View

internal class RvItemDsl {
    private var resId: Int = 0
    private var renderBlock: (view: View) -> Unit = {}
    private var renderBlockX: (position: Int, view: View) -> Unit = { _: Int, _: View -> }
    private var renderBlockWithAdapter: (position: Int, view: View, adapter: RvDslAdapter) -> Unit = { _: Int, _: View, _: RvDslAdapter -> }

    private var gridSpanSize = 1
    private var staggerFullSpan = false

    /**
     * Set item xml layout resource
     */
    fun xml(res: Int) {
        this.resId = res
    }

    /**
     * Render item
     *
     * @param block  Call when item render
     */
    fun render(block: (view: View) -> Unit) {
        this.renderBlock = block
    }

    /**
     * Render item
     *
     * @param block  Call when item render
     */
    fun renderX(block: (position: Int, view: View) -> Unit) {
        this.renderBlockX = block
    }


    /**
     * Render item
     *
     * @param block  Call when item render
     */
    fun renderWithAdapter(block: (position: Int, view: View, adapter: RvDslAdapter) -> Unit) {
        this.renderBlockWithAdapter = block
    }

    /**
     * Only work for Grid, set the span size of this item
     *
     * @param spanSize spanSize
     */
    fun gridSpanSize(spanSize: Int) {
        this.gridSpanSize = spanSize
    }

    /**
     * Only work for Stagger, set the fullSpan of this item
     *
     * @param fullSpan True or false
     */
    fun staggerFullSpan(fullSpan: Boolean) {
        this.staggerFullSpan = fullSpan
    }

    internal fun internal(): RvItem {
        return object : RvItem {
            override fun render(position: Int, view: View, adapter: RvDslAdapter) {
                renderBlock(view)
                renderBlockX(position, view)
                renderBlockWithAdapter(position, view, adapter)
            }

            override fun xml(): Int {
                return resId
            }

            override fun gridSpanSize(): Int {
                return gridSpanSize
            }

            override fun staggerFullSpan(): Boolean {
                return staggerFullSpan
            }
        }
    }
}