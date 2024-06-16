package com.example.myapplication45.activity

import android.animation.ObjectAnimator
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelection
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.example.myapplication45.MyApplication
import com.example.myapplication45.R
import com.example.myapplication45.constant.ConstantKey
import com.example.myapplication45.constant.GlobalFunction
import com.example.myapplication45.databinding.ActivityMovieDetailBinding
import com.example.myapplication45.model.Movie
import com.example.myapplication45.util.DateTimeUtils
import com.example.myapplication45.util.GlideUtils
import com.example.myapplication45.util.StringUtil


import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MovieDetailActivity : AppCompatActivity() {

    private var mActivityMovieDetailBinding: ActivityMovieDetailBinding? = null
    private var mMovie: Movie? = null
    private var mMediaSource: ExtractorMediaSource? = null
    private var mPlayer: SimpleExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMovieDetailBinding = ActivityMovieDetailBinding.inflate(layoutInflater)
        setContentView(mActivityMovieDetailBinding!!.root)
        getDataIntent()
    }

    private fun getDataIntent() {
        val bundle = intent.extras ?: return
        val movie = bundle[ConstantKey.KEY_INTENT_MOVIE_OBJECT] as Movie?
        getMovieInformation(movie!!.id)
    }

    private fun getMovieInformation(movieId: Long) {
        MyApplication[this].getMovieDatabaseReference().child(movieId.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    mMovie = snapshot.getValue(Movie::class.java)
                    displayDataMovie()
                    initListener()
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun displayDataMovie() {
        if (mMovie == null) {
            return
        }
        GlideUtils.loadUrl(mMovie?.image, mActivityMovieDetailBinding!!.imgMovie)
        mActivityMovieDetailBinding!!.tvTitleMovie.text = mMovie?.name
        mActivityMovieDetailBinding!!.tvCategoryName.text = mMovie?.categoryName
        mActivityMovieDetailBinding!!.tvDateMovie.text = mMovie?.date
        val strPrice = mMovie?.price.toString() + ConstantKey.UNIT_CURRENCY_MOVIE
        mActivityMovieDetailBinding!!.tvPriceMovie.text = strPrice
        mActivityMovieDetailBinding!!.tvDescriptionMovie.text = mMovie?.description
        if (!StringUtil.isEmpty(mMovie?.url)) {
            initExoPlayer()
        }
    }

    private fun initListener() {
        mActivityMovieDetailBinding!!.imgBack.setOnClickListener { onBackPressed() }
        mActivityMovieDetailBinding!!.btnWatchTrailer.setOnClickListener { scrollToLayoutTrailer() }
        mActivityMovieDetailBinding!!.imgPlayMovie.setOnClickListener { startVideo() }
        mActivityMovieDetailBinding!!.btnBooking.setOnClickListener { onClickGoToConfirmBooking() }
    }

    private fun onClickGoToConfirmBooking() {
        if (mMovie == null) {
            return
        }
        if (DateTimeUtils.convertDateToTimeStamp(mMovie?.date) < DateTimeUtils.getLongCurrentTimeStamp()) {
            Toast.makeText(this, getString(R.string.msg_movie_date_invalid), Toast.LENGTH_SHORT).show()
            return
        }
        val bundle = Bundle()
        bundle.putSerializable(ConstantKey.KEY_INTENT_MOVIE_OBJECT, mMovie)
        GlobalFunction.startActivity(this, ConfirmBookingActivity::class.java, bundle)
    }

    private fun scrollToLayoutTrailer() {
        val dulation: Long = 500
        Handler(Looper.getMainLooper()).postDelayed({
            val y = mActivityMovieDetailBinding!!.labelMovieTrailer.y
            val sv = mActivityMovieDetailBinding!!.scrollView
            val objectAnimator = ObjectAnimator.ofInt(sv, "scrollY", 0, y.toInt())
            objectAnimator.start()
            startVideo()
        }, dulation)
    }

    private fun initExoPlayer() {
        val mExoPlayerView = mActivityMovieDetailBinding!!.exoplayer
        if (mPlayer != null) {
            return
        }
        val userAgent = Util.getUserAgent(this, this.applicationInfo.packageName)
        val httpDataSourceFactory = DefaultHttpDataSourceFactory(userAgent,
            null, DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
            DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true)
        val dataSourceFactory = DefaultDataSourceFactory(this,
            null, httpDataSourceFactory)
        mMediaSource = ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(mMovie?.url))
        val bandwidthMeter: BandwidthMeter = DefaultBandwidthMeter()
        val videoTrackSelectionFactory: TrackSelection.Factory = AdaptiveTrackSelection.Factory(bandwidthMeter)
        val trackSelector: TrackSelector = DefaultTrackSelector(videoTrackSelectionFactory)
        val loadControl: LoadControl = DefaultLoadControl()
        mPlayer = ExoPlayerFactory.newSimpleInstance(DefaultRenderersFactory(this),
            trackSelector, loadControl)
        // Set player
        mExoPlayerView.player = mPlayer
        mExoPlayerView.hideController()
    }

    private fun startVideo() {
        mActivityMovieDetailBinding!!.imgPlayMovie.visibility = View.GONE
        if (mPlayer != null) {
            // Prepare video source
            mPlayer!!.prepare(mMediaSource)
            // Set play video
            mPlayer!!.playWhenReady = true
        }
    }
}