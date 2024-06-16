package com.example.myapplication45.activity.admin

import android.os.Bundle
import android.widget.Toast
import com.example.myapplication45.MyApplication
import com.example.myapplication45.R
import com.example.myapplication45.activity.BaseActivity
import com.example.myapplication45.constant.ConstantKey
import com.example.myapplication45.constant.GlobalFunction
import com.example.myapplication45.databinding.ActivityAddCategoryBinding
import com.example.myapplication45.model.Category
import com.example.myapplication45.util.StringUtil
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference


class AddCategoryActivity : BaseActivity() {

    private var mActivityAddCategoryBinding: ActivityAddCategoryBinding? = null
    private var isUpdate = false
    private var mCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddCategoryBinding = ActivityAddCategoryBinding.inflate(layoutInflater)
        setContentView(mActivityAddCategoryBinding!!.root)
        val bundleReceived = intent.extras
        if (bundleReceived != null) {
            isUpdate = true
            mCategory = bundleReceived[ConstantKey.KEY_INTENT_CATEGORY_OBJECT] as Category?
        }
        initView()
        mActivityAddCategoryBinding!!.imgBack.setOnClickListener { onBackPressed() }
        mActivityAddCategoryBinding!!.btnAddOrEdit.setOnClickListener { addOrEditCategory() }
    }

    private fun initView() {
        if (isUpdate) {
            mActivityAddCategoryBinding!!.tvTitle.text = getString(R.string.edit_category_title)
            mActivityAddCategoryBinding!!.btnAddOrEdit.text = getString(R.string.action_edit)
            mActivityAddCategoryBinding!!.edtName.setText(mCategory?.name)
            mActivityAddCategoryBinding!!.edtImage.setText(mCategory?.image)
        } else {
            mActivityAddCategoryBinding!!.tvTitle.text = getString(R.string.add_category_title)
            mActivityAddCategoryBinding!!.btnAddOrEdit.text = getString(R.string.action_add)
        }
    }

    private fun addOrEditCategory() {
        val strName = mActivityAddCategoryBinding!!.edtName.text.toString().trim { it <= ' ' }
        val strImage = mActivityAddCategoryBinding!!.edtImage.text.toString().trim { it <= ' ' }
        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_category_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_category_require), Toast.LENGTH_SHORT).show()
            return
        }

        // Update category
        if (isUpdate) {
            showProgressDialog(true)
            val map: MutableMap<String, Any> = HashMap()
            map["name"] = strName
            map["image"] = strImage
            MyApplication[this].getCategoryDatabaseReference()
                .child(mCategory?.id.toString()).updateChildren(map) { _: DatabaseError?, _: DatabaseReference? ->
                    showProgressDialog(false)
                    Toast.makeText(this@AddCategoryActivity,
                        getString(R.string.msg_edit_category_successfully), Toast.LENGTH_SHORT).show()
                    GlobalFunction.hideSoftKeyboard(this@AddCategoryActivity)
                }
            return
        }

        // Add category
        showProgressDialog(true)
        val categoryId = System.currentTimeMillis()
        val category = Category(categoryId, strName, strImage)
        MyApplication[this].getCategoryDatabaseReference().child(categoryId.toString())
            .setValue(category) { _: DatabaseError?, _: DatabaseReference? ->
                showProgressDialog(false)
                mActivityAddCategoryBinding!!.edtName.setText("")
                mActivityAddCategoryBinding!!.edtImage.setText("")
                GlobalFunction.hideSoftKeyboard(this)
                Toast.makeText(this, getString(R.string.msg_add_category_successfully),
                    Toast.LENGTH_SHORT).show()
            }
    }
}