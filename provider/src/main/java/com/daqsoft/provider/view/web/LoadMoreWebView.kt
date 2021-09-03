package com.daqsoft.provider.view.web

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Picture
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.daqsoft.baselib.base.BaseApplication
import com.daqsoft.baselib.extend.dp
import com.daqsoft.baselib.utils.StringUtil
import com.daqsoft.baselib.utils.Utils
import com.daqsoft.baselib.widgets.click.onNoDoubleClick
import com.daqsoft.provider.R
import com.daqsoft.provider.databinding.WebviewLoadMoreBinding
import timber.log.Timber

/**
 * @Description 加载更多webview
 * @ClassName   LoadMoreWebView
 * @Author      luoyi
 * @Time        2020/5/16 15:00
 */
class LoadMoreWebView : FrameLayout {

    var mContext: Context? = null
    var binding: WebviewLoadMoreBinding? = null
    private var webViewHeight: Int = 0
    var maxWebViewHeight: Int = 0


    /**
     * 内容高度
     */
    var contentHeight = 0

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        var defaultHeight = Utils.dip2px(BaseApplication.context, 180f).toInt()
        var ta = context?.obtainStyledAttributes(attrs, R.styleable.loadMoreWebView)
        var maxHeight = ta?.getDimension(
            R.styleable.loadMoreWebView_maxLoadHeight,
            defaultHeight.toFloat()
        )
        ta?.recycle()
        maxWebViewHeight = maxHeight!!.toInt()
        initView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),
            R.layout.webview_load_more,
            this,
            false
        )
        addView(binding!!.root)
        initWebLoadMore()

        initViewEvent()
    }

    private fun initViewEvent() {
//        binding?.webLoadMore?.setCallBack {
//
//            Timber.e("getContentHeight  setCallBack 高度${binding?.webLoadMore?.contentHeight}")
//
//            webViewHeight = it
//            if (it > maxWebViewHeight) {
//                binding?.webLoadMore?.layoutParams =
//                    LayoutParams(LayoutParams.MATCH_PARENT, maxWebViewHeight)
//                binding?.vWebLoadMore?.visibility = View.VISIBLE
//            } else if (it < maxWebViewHeight) {
//                binding?.webLoadMore?.layoutParams =
//                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//            }
//        }
//
//        binding?.vWebLoadMore?.setOnClickListener {
//            if (binding?.imgLoadMore!!.isSelected) {
//                binding?.imgLoadMore!!.isSelected = false
//                binding?.imgLoadMore!!.setImageResource(R.mipmap.provider_arrow_down)
//                if (webViewHeight >= maxWebViewHeight) {
//                    binding?.webLoadMore?.layoutParams =
//                        LayoutParams(LayoutParams.MATCH_PARENT, maxWebViewHeight)
//                    webViewHeight = maxWebViewHeight
//                }
//            } else {
//                binding?.imgLoadMore!!.isSelected = true
//                binding?.imgLoadMore!!.setImageResource(R.mipmap.provider_arrow_up)
//                binding?.webLoadMore?.layoutParams =
//                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
//                webViewHeight += 100
//            }
//
//        }

        binding?.vWebLoadMore?.onNoDoubleClick {
            if (binding?.imgLoadMore!!.isSelected) {
                binding?.imgLoadMore!!.isSelected = false
                binding?.imgLoadMore!!.setImageResource(R.mipmap.provider_arrow_down)

                binding?.webLoadMore?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, maxWebViewHeight)

            } else {
                binding?.imgLoadMore!!.isSelected = true
                binding?.imgLoadMore!!.setImageResource(R.mipmap.provider_arrow_up)
                binding?.webLoadMore?.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

            }

        }
    }

    private fun initWebLoadMore() {

        binding?.vWebLoadMore?.visibility = View.GONE

        binding?.webLoadMore?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        binding?.webLoadMore?.settings?.defaultTextEncodingName = "utf-8"
        binding?.webLoadMore?.settings?.javaScriptEnabled = true
        binding?.webLoadMore?.isScrollContainer = false
        binding?.webLoadMore?.isVerticalScrollBarEnabled = false
        binding?.webLoadMore?.isHorizontalScrollBarEnabled = false
        binding?.imgLoadMore?.isSelected = false

        binding?.webLoadMore?.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                contentHeight =  view.contentHeight
                if (contentHeight > maxWebViewHeight){
                    binding?.webLoadMore?.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, maxWebViewHeight)
                    binding?.vWebLoadMore?.visibility = View.VISIBLE
                }


            }
        }

    }

    /**
     * 加载文本内容
     */
    fun loadContent(context: String) {
        binding?.webLoadMore?.loadDataWithBaseURL(
            null, StringUtil.getHtml(context),
            "text/html", "utf-8", null
        )
    }

}