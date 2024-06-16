package com.example.myapplication45.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myapplication45.MyApplication
import com.example.myapplication45.R
import com.example.myapplication45.adapter.MovieAdapter
import com.example.myapplication45.constant.GlobalFunction
import com.example.myapplication45.databinding.ActivitySearchBinding
import com.example.myapplication45.model.Category
import com.example.myapplication45.model.Movie
import com.example.myapplication45.util.StringUtil
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.wefika.flowlayout.FlowLayout
import java.util.ArrayList
import java.util.Locale

class SearchActivity : AppCompatActivity(), View.OnClickListener {

    private var mActivitySearchBinding: ActivitySearchBinding? = null
    private var mListCategory: MutableList<Category>? = null
    private var mCategorySelected: Category? = null
    private var mListMovies: MutableList<Movie>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivitySearchBinding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(mActivitySearchBinding!!.root)
        initListener()
        getListCategory()
    }

    private fun initListener() {
        mActivitySearchBinding!!.imageBack.setOnClickListener {
            GlobalFunction.hideSoftKeyboard(this@SearchActivity)
            onBackPressed()
        }
        mActivitySearchBinding!!.imageDelete.setOnClickListener { mActivitySearchBinding!!.edtKeyword.setText("") }
        mActivitySearchBinding!!.edtKeyword.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchMovie()
                return@setOnEditorActionListener true
            }
            false
        }
        mActivitySearchBinding!!.edtKeyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                    mActivitySearchBinding!!.imageDelete.visibility = View.VISIBLE
                } else {
                    mActivitySearchBinding!!.imageDelete.visibility = View.GONE
                    searchMovie()
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun getListCategory() {
        MyApplication[this].getCategoryDatabaseReference().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                mActivitySearchBinding!!.tvCategoryTitle.visibility = View.VISIBLE
                mActivitySearchBinding!!.layoutCategory.visibility = View.VISIBLE
                if (mListCategory != null) {
                    mListCategory!!.clear()
                } else {
                    mListCategory = ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(Category::class.java)
                    if (category != null) {
                        mListCategory!!.add(0, category)
                    }
                }
                mCategorySelected = Category(0, getString(R.string.label_all), "")
                mListCategory!!.add(0, mCategorySelected!!)
                initLayoutCategory("")
            }

            override fun onCancelled(error: DatabaseError) {
                mActivitySearchBinding!!.tvCategoryTitle.visibility = View.GONE
                mActivitySearchBinding!!.layoutCategory.visibility = View.GONE
            }
        })
    }

    private fun initLayoutCategory(tag: String) {
        mActivitySearchBinding!!.layoutCategory.removeAllViews()
        if (mListCategory != null && mListCategory!!.isNotEmpty()) {
            for (i in mListCategory!!.indices) {
                val category = mListCategory!![i]
                val params = FlowLayout.LayoutParams(
                    FlowLayout.LayoutParams.WRAP_CONTENT,
                    FlowLayout.LayoutParams.WRAP_CONTENT)
                val textView = TextView(this@SearchActivity)
                params.setMargins(0, 10, 20, 10)
                textView.layoutParams = params
                textView.setPadding(30, 10, 30, 10)
                textView.tag = category.id.toString()
                textView.text = category.name
                if (tag == category.id.toString()) {
                    mCategorySelected = category
                    textView.setBackgroundResource(R.drawable.bg_white_shape_round_corner_border_red)
                    textView.setTextColor(resources.getColor(R.color.red))
                    searchMovie()
                } else {
                    textView.setBackgroundResource(R.drawable.bg_white_shape_round_corner_border_grey)
                    textView.setTextColor(resources.getColor(R.color.colorPrimary))
                }
                textView.textSize = resources.getDimension(R.dimen.text_size_small).toInt() /
                        resources.displayMetrics.density
                textView.setOnClickListener(this@SearchActivity)
                mActivitySearchBinding!!.layoutCategory.addView(textView)
            }
        }
    }

    private fun searchMovie() {
        MyApplication[this].getMovieDatabaseReference().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mListMovies != null) {
                    mListMovies!!.clear()
                } else {
                    mListMovies = ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val movie = dataSnapshot.getValue(Movie::class.java)!!
                    if (isMovieResult(movie)) {
                        mListMovies!!.add(0, movie)
                    }
                }
                displayListMoviesResult()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayListMoviesResult() {
        val gridLayoutManager = GridLayoutManager(this, 3)
        mActivitySearchBinding!!.rcvData.layoutManager = gridLayoutManager
        val movieAdapter = MovieAdapter(mListMovies, object : MovieAdapter.IManagerMovieListener {
            override fun clickItemMovie(movie: Movie?) {
                GlobalFunction.goToMovieDetail(this@SearchActivity, movie)
            }
        })
        mActivitySearchBinding!!.rcvData.adapter = movieAdapter
    }

    private fun isMovieResult(movie: Movie?): Boolean {
        if (movie == null) {
            return false
        }
        val key = mActivitySearchBinding!!.edtKeyword.text.toString().trim { it <= ' ' }
        val categoryId = mCategorySelected?.id
        return if (StringUtil.isEmpty(key)) {
            if (categoryId == 0L) {
                true
            } else movie.categoryId == categoryId
        } else {
            val isMatch = GlobalFunction.getTextSearch(movie.name).toLowerCase(Locale.getDefault()).trim { it <= ' ' }
                .contains(GlobalFunction.getTextSearch(key).toLowerCase(Locale.getDefault()).trim { it <= ' ' })
            if (categoryId == 0L) {
                isMatch
            } else isMatch && movie.categoryId == categoryId
        }
    }

    override fun onClick(v: View) {
        initLayoutCategory(v.tag.toString())
    }
}