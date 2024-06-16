package com.example.myapplication45.activity.admin

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.myapplication45.R
import com.example.myapplication45.adapter.admin.AdminViewPagerAdapter
import com.example.myapplication45.databinding.ActivityAdminBinding
import androidx.viewpager2.widget.ViewPager2

class AdminActivity : AppCompatActivity() {
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showDialogLogout()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityAdminBinding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(activityAdminBinding.root)
        val adminViewPagerAdapter = AdminViewPagerAdapter(this)
        activityAdminBinding.viewpager2.adapter = adminViewPagerAdapter
        activityAdminBinding.viewpager2.isUserInputEnabled = false

        activityAdminBinding.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        activityAdminBinding.bottomNavigation.menu.findItem(R.id.nav_admin_category).isChecked = true
                        activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_category)
                    }
                    1 -> {
                        activityAdminBinding.bottomNavigation.menu.findItem(R.id.nav_admin_food_drink).isChecked = true
                        activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_food_drink)
                    }
                    2 -> {
                        activityAdminBinding.bottomNavigation.menu.findItem(R.id.nav_admin_movie).isChecked = true
                        activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_movie)
                    }
                    3 -> {
                        activityAdminBinding.bottomNavigation.menu.findItem(R.id.nav_admin_booking).isChecked = true
                        activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_booking)
                    }
                    4 -> {
                        activityAdminBinding.bottomNavigation.menu.findItem(R.id.nav_admin_manage).isChecked = true
                        activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_manage)
                    }
                }
            }
        })
        activityAdminBinding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            when (item.itemId) {
                R.id.nav_admin_category -> {
                    activityAdminBinding.viewpager2.currentItem = 0
                    activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_category)
                }
                R.id.nav_admin_food_drink -> {
                    activityAdminBinding.viewpager2.currentItem = 1
                    activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_food_drink)
                }
                R.id.nav_admin_movie -> {
                    activityAdminBinding.viewpager2.currentItem = 2
                    activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_movie)
                }
                R.id.nav_admin_booking -> {
                    activityAdminBinding.viewpager2.currentItem = 3
                    activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_booking)
                }
                R.id.nav_admin_manage -> {
                    activityAdminBinding.viewpager2.currentItem = 4
                    activityAdminBinding.tvTitle.text = getString(R.string.nav_admin_manage)
                }
            }
            true
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    private fun showDialogLogout() {
        MaterialDialog.Builder(this)
            .title(getString(R.string.app_name))
            .content(getString(R.string.msg_confirm_login_another_device))
            .positiveText(getString(R.string.action_ok))
            .negativeText(getString(R.string.action_cancel))
            .onPositive { dialog: MaterialDialog, _: DialogAction? ->
                dialog.dismiss()
                finishAffinity()
            }
            .onNegative { dialog: MaterialDialog, _: DialogAction? -> dialog.dismiss() }
            .cancelable(true)
            .show()
    }


}