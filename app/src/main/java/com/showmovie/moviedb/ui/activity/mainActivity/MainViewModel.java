package com.showmovie.moviedb.ui.activity.mainActivity;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.showmovie.moviedb.adapter.MovieItemAdapter;
import com.showmovie.moviedb.datasource.MovieDataSource;
import com.showmovie.moviedb.datasource.MovieDataSourceFactory;
import com.showmovie.moviedb.enums.State;
import com.showmovie.moviedb.model.Movie;

import io.reactivex.disposables.CompositeDisposable;

public class MainViewModel extends AndroidViewModel {

    final LiveData<PagedList<Movie>> moviePagedList;
    final LiveData<State> stateObserver;
    public final MovieItemAdapter adapter;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MovieDataSourceFactory movieDataSourceFactory;

    public MainViewModel(@NonNull Application application) {
        super(application);
        movieDataSourceFactory = new MovieDataSourceFactory(compositeDisposable);
        adapter = new MovieItemAdapter();
        Log.d("MMM123","333");
        PagedList.Config pagedListConfig =
                (new PagedList.Config.Builder())
                        .setEnablePlaceholders(false)
                        .setPageSize(MovieDataSource.PAGE_SIZE)
                        .build();
        Log.d("MMM123","444");
        moviePagedList = (new LivePagedListBuilder(movieDataSourceFactory, pagedListConfig)).build();
        stateObserver = Transformations.switchMap(movieDataSourceFactory.getMovieDataSourceMutableLiveData(), MovieDataSource::getStateMutableLiveData);
        Log.d("MMM123","555");
    }


    public void retry() {
        if(movieDataSourceFactory.getMovieDataSourceMutableLiveData().getValue() != null){
            movieDataSourceFactory.getMovieDataSourceMutableLiveData().getValue().retry();
        }
    }

    public boolean listEmpty() {
        if (moviePagedList.getValue() != null) {
            return moviePagedList.getValue().isEmpty();
        } else {
            return true;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
