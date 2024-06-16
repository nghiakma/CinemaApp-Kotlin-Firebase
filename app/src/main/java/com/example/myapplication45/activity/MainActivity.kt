package com.example.myapplication45.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.afollestad.materialdialogs.DialogAction
import com.afollestad.materialdialogs.MaterialDialog
import com.example.myapplication45.R
import com.example.myapplication45.adapter.MyViewPagerAdapter
import com.example.myapplication45.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            showDialogLogout()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        val myViewPagerAdapter = MyViewPagerAdapter(this)
        activityMainBinding.viewpager2.adapter = myViewPagerAdapter
        activityMainBinding.viewpager2.isUserInputEnabled = false
        activityMainBinding.viewpager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        activityMainBinding.bottomNavigation.menu.findItem(R.id.nav_home).isChecked = true
                        activityMainBinding.tvTitle.text = getString(R.string.nav_home)
                    }
                    1 -> {
                        activityMainBinding.bottomNavigation.menu.findItem(R.id.nav_booking).isChecked = true
                        activityMainBinding.tvTitle.text = getString(R.string.nav_booking)
                    }
                    2 -> {
                        activityMainBinding.bottomNavigation.menu.findItem(R.id.nav_user).isChecked = true
                        activityMainBinding.tvTitle.text = getString(R.string.nav_user)
                    }
                }
            }
        })
        activityMainBinding.bottomNavigation.setOnItemSelectedListener { item: MenuItem ->
            val id = item.itemId
            if (id == R.id.nav_home) {
                activityMainBinding.viewpager2.currentItem = 0
                activityMainBinding.tvTitle.text = getString(R.string.nav_home)
            } else if (id == R.id.nav_booking) {
                activityMainBinding.viewpager2.currentItem = 1
                activityMainBinding.tvTitle.text = getString(R.string.nav_booking)
            } else if (id == R.id.nav_user) {
                activityMainBinding.viewpager2.currentItem = 2
                activityMainBinding.tvTitle.text = getString(R.string.nav_user)
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