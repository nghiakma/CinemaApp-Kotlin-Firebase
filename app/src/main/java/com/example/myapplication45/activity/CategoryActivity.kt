package com.example.myapplication45.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication45.MyApplication
import com.example.myapplication45.R
import com.example.myapplication45.adapter.MovieAdapter
import com.example.myapplication45.constant.ConstantKey
import com.example.myapplication45.constant.GlobalFunction
import com.example.myapplication45.databinding.ActivityCategoryBinding
import com.example.myapplication45.model.Category
import com.example.myapplication45.model.Movie
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class CategoryActivity : AppCompatActivity() {

    private var mActivityCategoryBinding: ActivityCategoryBinding? = null
    private var mListMovies: MutableList<Movie>? = null
    private var mCategory: Category? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityCategoryBinding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(mActivityCategoryBinding!!.root)
        getDataIntent()
        initListener()
        getListMovies()
    }

    private fun getDataIntent() {
        val bundleReceived = intent.extras
        if (bundleReceived != null) {
            mCategory = bundleReceived[ConstantKey.KEY_INTENT_CATEGORY_OBJECT] as Category?
            mActivityCategoryBinding!!.tvTitle.text = mCategory?.name
        }
    }

    private fun initListener() {
        mActivityCategoryBinding!!.imgBack.setOnClickListener { onBackPressed() }
    }

    private fun getListMovies() {
        MyApplication[this].getMovieDatabaseReference().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mListMovies != null) {
                    mListMovies!!.clear()
                } else {
                    mListMovies = ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val movie = dataSnapshot.getValue(Movie::class.java)
                    if (movie != null && mCategory?.id == movie.categoryId) {
                        mListMovies!!.add(0, movie)
                    }
                }
                displayListMovies()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayListMovies() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        mActivityCategoryBinding!!.rcvData.layoutManager = gridLayoutManager
        val movieAdapter = MovieAdapter(mListMovies, object : MovieAdapter.IManagerMovieListener {
            override fun clickItemMovie(movie: Movie?) {
                GlobalFunction.goToMovieDetail(this@CategoryActivity, movie)
            }
        })
        mActivityCategoryBinding!!.rcvData.adapter = movieAdapter
    }
}