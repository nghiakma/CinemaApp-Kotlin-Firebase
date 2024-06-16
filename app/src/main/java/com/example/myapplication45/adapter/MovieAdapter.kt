package com.example.myapplication45.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication45.databinding.ItemMovieBinding
import com.example.myapplication45.model.Movie
import com.example.myapplication45.util.GlideUtils

class MovieAdapter(private val mListMovies: List<Movie>?,
                   private val iManagerMovieListener: IManagerMovieListener) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {
    interface IManagerMovieListener {
        fun clickItemMovie(movie: Movie?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemMovieBinding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(itemMovieBinding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = mListMovies!![position]
        GlideUtils.loadUrl(movie.image, holder.mItemMovieBinding.imgMovie)
        holder.mItemMovieBinding.tvName.text = movie.name
        holder.mItemMovieBinding.tvBooked.text = movie.booked.toString()
        holder.mItemMovieBinding.layoutItem.setOnClickListener { iManagerMovieListener.clickItemMovie(movie) }
    }

    override fun getItemCount(): Int {
        return mListMovies?.size ?: 0
    }

    class MovieViewHolder(val mItemMovieBinding: ItemMovieBinding) : RecyclerView.ViewHolder(mItemMovieBinding.root)
}