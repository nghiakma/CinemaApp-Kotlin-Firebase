package com.example.myapplication45.fragment.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication45.activity.ChangePasswordActivity
import com.example.myapplication45.activity.SignInActivity
import com.example.myapplication45.activity.admin.AdminRevenueActivity
import com.example.myapplication45.databinding.FragmentAdminManageBinding
import com.example.myapplication45.prefs.DataStoreManager
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication45.constant.GlobalFunction.startActivity
class AdminManageFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentAdminManageBinding = FragmentAdminManageBinding.inflate(inflater, container, false)
        fragmentAdminManageBinding.tvEmail.text = DataStoreManager.getUser()?.email
        fragmentAdminManageBinding.layoutReport.setOnClickListener { onClickReport() }
        fragmentAdminManageBinding.layoutSignOut.setOnClickListener { onClickSignOut() }
        fragmentAdminManageBinding.layoutChangePassword.setOnClickListener { onClickChangePassword() }
        return fragmentAdminManageBinding.root
    }

    private fun onClickReport() {
        startActivity(activity, AdminRevenueActivity::class.java)
    }

    private fun onClickChangePassword() {
        startActivity(activity, ChangePasswordActivity::class.java)
    }

    private fun onClickSignOut() {
        if (activity == null) {
            return
        }
        FirebaseAuth.getInstance().signOut()
        DataStoreManager.setUser(null)
        startActivity(activity, SignInActivity::class.java)
        requireActivity().finishAffinity()
    }
}