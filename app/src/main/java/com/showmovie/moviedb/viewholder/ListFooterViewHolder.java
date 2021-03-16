package com.showmovie.moviedb.viewholder;

import androidx.recyclerview.widget.RecyclerView;

import com.showmovie.moviedb.databinding.ItemListFooterBinding;

public class ListFooterViewHolder extends RecyclerView.ViewHolder {

    public ItemListFooterBinding binding;

    public ListFooterViewHolder(ItemListFooterBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
