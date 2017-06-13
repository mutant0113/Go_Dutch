package com.mutant.godutch

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import java.util.*

/**
 * Created by evanfang102 on 2017/3/30.
 */

class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager) {

    internal var mFragments: MutableList<Fragment> = ArrayList()

    override fun getCount(): Int {
        return mFragments.size
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    fun addFragment(fragment: Fragment) {
        mFragments.add(fragment)
    }
}
