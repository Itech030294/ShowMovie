package com.showmovie.moviedb.bindingadapter;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class SetAdapterBindingAdapter {

    @BindingAdapter("setAdapter")
    public static void setAdapter(RecyclerView view, RecyclerView.Adapter adapter) {
        view.setAdapter(adapter);
    }
}
