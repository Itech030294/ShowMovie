package com.showmovie.moviedb.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.showmovie.moviedb.databinding.RowMovieBinding;

public class MovieItemViewHolder extends RecyclerView.ViewHolder {

    public RowMovieBinding binding;

    public MovieItemViewHolder(RowMovieBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
