package com.showmovie.moviedb.bindingadapter;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.showmovie.moviedb.utils.decoration.ItemOffsetDecoration;

public class ItemDecorationBindingAdapter {

    @BindingAdapter("itemDecorationVertical")
    public static void itemDecorationVertical(RecyclerView view, int resId) {
        view.addItemDecoration(new ItemOffsetDecoration(view.getContext(), resId));
    }

}
