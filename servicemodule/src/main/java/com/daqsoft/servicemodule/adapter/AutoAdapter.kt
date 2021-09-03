package com.daqsoft.servicemodule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.daqsoft.android.scenic.servicemodule.R
import com.daqsoft.servicemodule.bean.InputTipsBean
import java.util.*
import kotlin.collections.ArrayList

/**
 * desc :模糊搜索适配器
 * @author 江云仙
 * @date 2020/4/10 20:22
 */
class AutoAdapter(var mContext: Context, var mResource: Int, var mList: List<InputTipsBean>?) : BaseAdapter(), Filterable {
    var data: MutableList<InputTipsBean>? = ArrayList()
    override fun getCount(): Int {
        return if (mList == null) 0 else mList!!.size
    }

    override fun getItem(position: Int): Any {
        return mList!![position]
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(mContext)
        @SuppressLint("ViewHolder") val view = inflater.inflate(mResource, null)
        val tvAuto = view.findViewById<TextView>(R.id.tv_auto)
        val tvHint = view.findViewById<TextView>(R.id.tv_district)
        tvAuto.text = mList!![position].name
        tvHint.text = mList!![position].district
        return view
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val results = FilterResults()
                val list: MutableList<String> = ArrayList()
                if (constraint != null) {
                    for (s in mList!!) {
                        list.add(s.name)
                    }
                }
                results.count = list.size
                results.values = list
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults) {
                if (!TextUtils.isEmpty(results.values.toString())){
                    data =  results.values as MutableList<InputTipsBean>
                    notifyDataSetChanged()
                }


            }
        }
    }


}