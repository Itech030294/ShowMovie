package com.showmovie.moviedb.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.showmovie.moviedb.R;
import com.showmovie.moviedb.databinding.ItemListFooterBinding;
import com.showmovie.moviedb.databinding.RowMovieBinding;
import com.showmovie.moviedb.enums.State;
import com.showmovie.moviedb.model.Movie;
import com.showmovie.moviedb.ui.activity.mainActivity.MainViewModel;
import com.showmovie.moviedb.viewholder.ListFooterViewHolder;
import com.showmovie.moviedb.viewholder.MovieItemViewHolder;

public class MovieItemAdapter extends PagedListAdapter<Movie, RecyclerView.ViewHolder> {

    private static DiffUtil.ItemCallback<Movie> DIFF_CALLBACK = new DiffUtil.ItemCallback<Movie>() {
        @Override
        public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.id.equals(newItem.id);
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
            return oldItem.equals(newItem);
        }
    };
    private MainViewModel viewModel;
    private int DATA_VIEW_TYPE = 1;
    private int FOOTER_VIEW_TYPE = 2;
    private State state = State.LOADING;

    public MovieItemAdapter() {
        super(DIFF_CALLBACK);
    }

    public void setViewModel(MainViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_VIEW_TYPE) {
//            Log.d("MMM123","333");
            RowMovieBinding rowMovieBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.row_movie,
                    parent, false);
            return new MovieItemViewHolder(rowMovieBinding);
        } else {
//            Log.d("MMM123","444");
            ItemListFooterBinding itemListFooterBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_list_footer,
                    parent, false);
            itemListFooterBinding.setViewModel(viewModel);
            itemListFooterBinding.txtError.setOnClickListener(view -> viewModel.retry());
            return new ListFooterViewHolder(itemListFooterBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == DATA_VIEW_TYPE) {
            ((MovieItemViewHolder) holder).binding.setMovie(getItem(position));
            ((MovieItemViewHolder) holder).binding.executePendingBindings();
        } else {
            ((ListFooterViewHolder) holder).binding.setState(state);
            ((ListFooterViewHolder) holder).binding.executePendingBindings();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position < super.getItemCount()) ? DATA_VIEW_TYPE : FOOTER_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return super.getItemCount() + (hasFooter() ? 1 : 0);
    }

    private boolean hasFooter() {
        return super.getItemCount() != 0 && (state == State.LOADING || state == State.ERROR);
    }

    public void setState(State state) {
        this.state = state;
        notifyItemChanged(super.getItemCount());
    }

}
