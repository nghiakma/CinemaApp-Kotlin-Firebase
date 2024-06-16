package com.example.myapplication45.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.example.myapplication45.R

abstract class BaseActivity : AppCompatActivity() {
    private var progressDialog: MaterialDialog? = null
    private var alertDialog: MaterialDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createProgressDialog()
    }
    private fun createProgressDialog() {
        progressDialog = MaterialDialog.Builder(this)
            .content(R.string.waiting_message)
            .progress(true, 0)
            .build()
    }
    fun showProgressDialog(value: Boolean) {
        if (value) {
            if (progressDialog != null && !progressDialog!!.isShowing) {
                progressDialog!!.show()
                progressDialog!!.setCancelable(false)
            }
        } else {
            if (progressDialog != null && progressDialog!!.isShowing) {
                progressDialog!!.dismiss()
            }
        }
    }




}