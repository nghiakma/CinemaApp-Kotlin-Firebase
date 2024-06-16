package com.example.myapplication45.adapter.admin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.myapplication45.fragment.admin.AdminBookingFragment
import com.example.myapplication45.fragment.admin.AdminCategoryFragment
import com.example.myapplication45.fragment.admin.AdminFoodFragment
import com.example.myapplication45.fragment.admin.AdminHomeFragment
import com.example.myapplication45.fragment.admin.AdminManageFragment



class AdminViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> AdminFoodFragment()
            2 -> AdminHomeFragment()
            3 -> AdminBookingFragment()
            4 -> AdminManageFragment()
            else -> AdminCategoryFragment()
        }
    }

    override fun getItemCount(): Int {
        return 5
    }
}