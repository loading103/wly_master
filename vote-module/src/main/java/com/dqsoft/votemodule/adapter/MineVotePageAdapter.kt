package com.dqsoft.votemodule.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * @Description
 * @ClassName   MineVotePageAdapter
 * @Author      luoyi
 * @Time        2020/11/19 14:06
 */
class MineVotePageAdapter : FragmentPagerAdapter {

    private var datas: MutableList<Fragment> = mutableListOf()

    constructor(data: MutableList<Fragment>, manager: FragmentManager) : super(manager) {
        datas.clear()
        datas.addAll(data)
    }

    override fun getItem(position: Int): Fragment {
        return datas[position]
    }

    override fun getCount(): Int {
        return datas.size
    }
}