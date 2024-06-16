package com.example.myapplication45.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication45.MyApplication
import com.example.myapplication45.activity.CategoryActivity
import com.example.myapplication45.activity.SearchActivity
import com.example.myapplication45.adapter.BannerMovieAdapter
import com.example.myapplication45.adapter.CategoryAdapter
import com.example.myapplication45.adapter.MovieAdapter
import com.example.myapplication45.constant.ConstantKey
import com.example.myapplication45.constant.GlobalFunction.goToMovieDetail
import com.example.myapplication45.databinding.FragmentHomeBinding
import com.example.myapplication45.model.Category
import com.example.myapplication45.model.Movie
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.util.Collections
import com.example.myapplication45.constant.GlobalFunction.startActivity
class HomeFragment : Fragment() {
    private var mFragmentHomeBinding: FragmentHomeBinding? = null
    private var mListMovies: MutableList<Movie>? = null
    private var mListMoviesBanner: MutableList<Movie>? = null
    private var mListCategory: MutableList<Category>? = null
    private val mHandlerBanner = Handler(Looper.getMainLooper())
    private val mRunnableBanner = Runnable {
        if (mListMoviesBanner == null || mListMoviesBanner!!.isEmpty()) {
            return@Runnable
        }
        if (mFragmentHomeBinding!!.viewPager2.currentItem == mListMoviesBanner!!.size - 1) {
            mFragmentHomeBinding!!.viewPager2.currentItem = 0
            return@Runnable
        }
        mFragmentHomeBinding!!.viewPager2.currentItem = mFragmentHomeBinding!!.viewPager2.currentItem + 1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        getListMovies()
        getListCategory()
        initListener()
        return mFragmentHomeBinding!!.root
    }

    private fun initListener() {
        mFragmentHomeBinding!!.layoutSearch.setOnClickListener { startActivity(activity, SearchActivity::class.java) }
    }

    private fun getListMovies() {
        if (activity == null) {
            return
        }
        MyApplication[activity].getMovieDatabaseReference().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mListMovies != null) {
                    mListMovies!!.clear()
                } else {
                    mListMovies = java.util.ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val movie = dataSnapshot.getValue(Movie::class.java)
                    if (movie != null) {
                        mListMovies!!.add(0, movie)
                    }
                }
                displayListBannerMovies()
                displayListAllMovies()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayListBannerMovies() {
        val bannerMovieAdapter = BannerMovieAdapter(getListBannerMovies(), object :
            BannerMovieAdapter.IClickItemListener {
            override fun onClickItem(movie: Movie?) {
                goToMovieDetail(activity, movie)
            }
        })
        mFragmentHomeBinding!!.viewPager2.adapter = bannerMovieAdapter
        mFragmentHomeBinding!!.indicator3.setViewPager(mFragmentHomeBinding!!.viewPager2)
        mFragmentHomeBinding!!.viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                mHandlerBanner.removeCallbacks(mRunnableBanner)
                mHandlerBanner.postDelayed(mRunnableBanner, 3000)
            }
        })
    }

    private fun getListBannerMovies() : List<Movie> {
        if (mListMoviesBanner != null) {
            mListMoviesBanner!!.clear()
        } else {
            mListMoviesBanner = java.util.ArrayList()
        }
        if (mListMovies == null || mListMovies!!.isEmpty()) {
            return mListMoviesBanner!!
        }
        val listClone: List<Movie> = ArrayList(mListMovies!!)
        Collections.sort(listClone) { movie1: Movie, movie2: Movie -> movie2.booked - movie1.booked }
        for (movie in listClone) {
            if (mListMoviesBanner!!.size < MAX_BANNER_SIZE) {
                mListMoviesBanner!!.add(movie)
            }
        }
        return mListMoviesBanner!!
    }

    private fun displayListAllMovies() {
        val gridLayoutManager = GridLayoutManager(activity, 3)
        mFragmentHomeBinding!!.rcvMovie.layoutManager = gridLayoutManager
        val movieAdapter = MovieAdapter(mListMovies, object : MovieAdapter.IManagerMovieListener {
            override fun clickItemMovie(movie: Movie?) {
                goToMovieDetail(activity, movie)
            }
        })
        mFragmentHomeBinding!!.rcvMovie.adapter = movieAdapter
    }

    private fun getListCategory() {
        if (activity == null) {
            return
        }
        MyApplication[activity].getCategoryDatabaseReference().addValueEventListener(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (mListCategory != null) {
                    mListCategory!!.clear()
                } else {
                    mListCategory = java.util.ArrayList()
                }
                for (dataSnapshot in snapshot.children) {
                    val category = dataSnapshot.getValue(Category::class.java)
                    if (category != null) {
                        mListCategory!!.add(0, category)
                    }
                }
                displayListCategories()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun displayListCategories() {

             val linearLayoutManager = LinearLayoutManager(activity,
            LinearLayoutManager.HORIZONTAL, false)
        mFragmentHomeBinding!!.rcvCategory.layoutManager = linearLayoutManager
        val categoryAdapter = CategoryAdapter(mListCategory, object : CategoryAdapter.IManagerCategoryListener {
            override fun clickItemCategory(category: Category?) {
                val bundle = Bundle()
                bundle.putSerializable(ConstantKey.KEY_INTENT_CATEGORY_OBJECT, category)
                startActivity(activity, CategoryActivity::class.java, bundle)
            }
        })
        mFragmentHomeBinding!!.rcvCategory.adapter = categoryAdapter
    }

    companion object {
        private const val MAX_BANNER_SIZE = 3
    }
}