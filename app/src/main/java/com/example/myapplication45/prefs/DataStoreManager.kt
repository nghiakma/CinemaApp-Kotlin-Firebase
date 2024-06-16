package com.example.myapplication45.prefs

import android.content.Context
import com.example.myapplication45.model.User
import com.example.myapplication45.util.StringUtil.isEmpty
import com.google.gson.Gson


class DataStoreManager {
    private var sharedPreferences: MySharedPreferences? = null

    companion object{
        private const val PREF_USER_INFOR = "PREF_USER_INFOR"
        private var instance: DataStoreManager? = null

        fun init(context: Context?) {
            instance = DataStoreManager()
            instance!!.sharedPreferences = MySharedPreferences(context)
        }

        private fun getInstance(): DataStoreManager? {
            return if (instance != null) {
                instance
            } else {
                throw IllegalStateException("Not initialized")
            }
        }

        fun setUser(user: User?) {
            var jsonUser = ""
            if (user != null) {
                jsonUser = user.toJSon()
            }
            getInstance()!!.sharedPreferences!!.putStringValue(PREF_USER_INFOR, jsonUser)
        }

        fun getUser(): User? {
            val jsonUser = getInstance()!!.sharedPreferences!!.getStringValue(PREF_USER_INFOR)
            return if (!isEmpty(jsonUser)) {
                Gson().fromJson(jsonUser, User::class.java)
            } else User()
        }

    }


}