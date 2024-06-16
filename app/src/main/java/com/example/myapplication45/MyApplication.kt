package com.example.myapplication45

import android.app.Application
import android.content.Context
import com.example.myapplication45.prefs.DataStoreManager

import com.google.firebase.FirebaseApp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        DataStoreManager.init(applicationContext)
        FirebaseApp.initializeApp(this)
    }

    fun getFoodDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/food")
    }

    fun getCategoryDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/category")
    }

    fun getMovieDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/movie")
    }

    fun getBookingDatabaseReference(): DatabaseReference {
        return FirebaseDatabase.getInstance(FIREBASE_URL).getReference("/booking")
    }

    fun getQuantityDatabaseReference(foodId: Long): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("/food/$foodId/quantity")
    }

    companion object {
        private const val FIREBASE_URL = "https://cinemaapp-156bd-default-rtdb.firebaseio.com"
        operator fun get(context: Context?): MyApplication {
            return context!!.applicationContext as MyApplication
        }
    }
}