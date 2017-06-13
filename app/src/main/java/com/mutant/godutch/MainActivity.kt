package com.mutant.godutch

import android.support.design.widget.BottomNavigationView
import com.mutant.godutch.fragment.GroupsFragment
import com.mutant.godutch.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

// TODO 登入系統
// TODO 建立個人Group
// TODO 加入他人Group
// TODO 輸入花費
// TODO 輸出清單以及分帳總結
class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun findViews() {
    }

    override fun setup() {
        setupViewPager()
        setupBottomNavigationView()
    }

    private fun setupViewPager() {
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
        viewPagerAdapter.addFragment(GroupsFragment())
        viewPagerAdapter.addFragment(FriendsFragment())
        viewPagerAdapter.addFragment(SettingsFragment())
        viewpager.adapter = viewPagerAdapter
    }

    private fun setupBottomNavigationView() {
        bottom_navigation.setOnNavigationItemSelectedListener(BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.action_groups -> {
                    viewpager.currentItem = VIEW_PAGER_GROUPS
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_friends -> {
                    viewpager.currentItem = VIEW_PAGER_FRIENDS
                    return@OnNavigationItemSelectedListener true
                }
                R.id.action_settings -> {
                    viewpager.currentItem = VIEW_PAGER_SETTINGS
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })
    }

    companion object {

        val VIEW_PAGER_GROUPS = 0
        val VIEW_PAGER_FRIENDS = VIEW_PAGER_GROUPS + 1
        val VIEW_PAGER_SETTINGS = VIEW_PAGER_FRIENDS + 1
    }

}
