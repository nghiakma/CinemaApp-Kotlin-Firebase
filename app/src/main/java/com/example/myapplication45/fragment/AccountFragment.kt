package com.example.myapplication45.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication45.activity.ChangePasswordActivity
import com.example.myapplication45.activity.SignInActivity
import com.example.myapplication45.databinding.FragmentAccountBinding
import com.example.myapplication45.prefs.DataStoreManager
import com.google.firebase.auth.FirebaseAuth
import com.example.myapplication45.constant.GlobalFunction.startActivity
class AccountFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val fragmentAccountBinding = FragmentAccountBinding.inflate(inflater, container, false)
        fragmentAccountBinding.tvEmail.text = DataStoreManager.getUser()?.email
        fragmentAccountBinding.layoutSignOut.setOnClickListener { onClickSignOut() }
        fragmentAccountBinding.layoutChangePassword.setOnClickListener { onClickChangePassword() }
        return fragmentAccountBinding.root
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