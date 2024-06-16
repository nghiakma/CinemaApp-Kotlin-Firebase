package com.example.myapplication45.activity.admin
import android.os.Bundle
import android.widget.Toast
import com.example.myapplication45.MyApplication
import com.example.myapplication45.R
import com.example.myapplication45.activity.BaseActivity
import com.example.myapplication45.constant.ConstantKey
import com.example.myapplication45.constant.GlobalFunction
import com.example.myapplication45.databinding.ActivityAddFoodBinding
import com.example.myapplication45.model.Food

import com.example.myapplication45.util.StringUtil
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
class AddFoodActivity : BaseActivity() {

    private var mActivityAddFoodBinding: ActivityAddFoodBinding? = null
    private var isUpdate = false
    private var mFood: Food? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityAddFoodBinding = ActivityAddFoodBinding.inflate(layoutInflater)
        setContentView(mActivityAddFoodBinding!!.root)
        val bundleReceived = intent.extras
        if (bundleReceived != null) {
            isUpdate = true
            mFood = bundleReceived[ConstantKey.KEY_INTENT_FOOD_OBJECT] as Food?
        }
        initView()
        mActivityAddFoodBinding!!.imgBack.setOnClickListener { onBackPressed() }
        mActivityAddFoodBinding!!.btnAddOrEdit.setOnClickListener { addOrEditFood() }
    }

    private fun initView() {
        if (isUpdate) {
            mActivityAddFoodBinding!!.tvTitle.text = getString(R.string.edit_food_title)
            mActivityAddFoodBinding!!.btnAddOrEdit.text = getString(R.string.action_edit)
            mActivityAddFoodBinding!!.edtName.setText(mFood?.name)
            mActivityAddFoodBinding!!.edtPrice.setText(mFood?.price.toString())
            mActivityAddFoodBinding!!.edtQuantity.setText(mFood?.quantity.toString())
        } else {
            mActivityAddFoodBinding!!.tvTitle.text = getString(R.string.add_food_title)
            mActivityAddFoodBinding!!.btnAddOrEdit.text = getString(R.string.action_add)
        }
    }

    private fun addOrEditFood() {
        val strName = mActivityAddFoodBinding!!.edtName.text.toString().trim { it <= ' ' }
        val strPrice = mActivityAddFoodBinding!!.edtPrice.text.toString().trim { it <= ' ' }
        val strQuantity = mActivityAddFoodBinding!!.edtQuantity.text.toString().trim { it <= ' ' }
        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_food_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (StringUtil.isEmpty(strPrice)) {
            Toast.makeText(this, getString(R.string.msg_price_food_require), Toast.LENGTH_SHORT).show()
            return
        }
        if (StringUtil.isEmpty(strQuantity)) {
            Toast.makeText(this, getString(R.string.msg_quantity_food_require), Toast.LENGTH_SHORT).show()
            return
        }

        // Update food
        if (isUpdate) {
            showProgressDialog(true)
            val map: MutableMap<String, Any> = HashMap()
            map["name"] = strName
            map["price"] = strPrice.toInt()
            map["quantity"] = strQuantity.toInt()
            MyApplication[this].getFoodDatabaseReference()
                .child(mFood?.id.toString()).updateChildren(map) { _: DatabaseError?, _: DatabaseReference? ->
                    showProgressDialog(false)
                    Toast.makeText(this@AddFoodActivity,
                        getString(R.string.msg_edit_food_successfully), Toast.LENGTH_SHORT).show()
                    GlobalFunction.hideSoftKeyboard(this@AddFoodActivity)
                }
            return
        }

        // Add food
        showProgressDialog(true)
        val foodId = System.currentTimeMillis()
        val food = Food(foodId, strName, strPrice.toInt(), strQuantity.toInt())
        MyApplication[this].getFoodDatabaseReference().child(foodId.toString())
            .setValue(food) { _: DatabaseError?, _: DatabaseReference? ->
                showProgressDialog(false)
                mActivityAddFoodBinding!!.edtName.setText("")
                mActivityAddFoodBinding!!.edtPrice.setText("")
                mActivityAddFoodBinding!!.edtQuantity.setText("")
                GlobalFunction.hideSoftKeyboard(this)
                Toast.makeText(this, getString(R.string.msg_add_food_successfully),
                    Toast.LENGTH_SHORT).show()
            }
    }
}