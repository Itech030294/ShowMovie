package com.showmovie.moviedb.datasource;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.showmovie.moviedb.enums.State;
import com.showmovie.moviedb.model.Movie;
import com.showmovie.moviedb.model.MovieResponse;
import com.showmovie.moviedb.repository.AllApi;
import com.showmovie.moviedb.repository.RetrofitClient;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MovieDataSource extends PageKeyedDataSource<Integer, Movie> {

    public static final int PAGE_SIZE = 20;
    private static final String TAG = "MovieDataSource";
    private static final int FIRST_PAGE = 1;

    private CompositeDisposable compositeDisposable;

    private MutableLiveData<State> stateMutableLiveData = new MutableLiveData<>();
    private Completable retryCompletable = null;

    MovieDataSource(CompositeDisposable compositeDisposable) {
        this.compositeDisposable = compositeDisposable;
    }

    @Override
    public void loadInitial(@NonNull final LoadInitialParams<Integer> params, @NonNull final LoadInitialCallback<Integer, Movie> callback) {
        updateState(State.LOADING);

        compositeDisposable.add(
                RetrofitClient.getInterface().getTopRatedMovies(FIRST_PAGE, AllApi.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                            @Override
                            public void onSuccess(MovieResponse movieResponse) {
                                if (movieResponse != null) {
                                    Log.d(TAG, "onSuccess: " + movieResponse.movies.size());
                                    updateState(State.DONE);
                                    callback.onResult(movieResponse.movies, null, FIRST_PAGE + 1);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                                updateState(State.ERROR);
                                setRetry(() -> loadInitial(params, callback));
                            }
                        }));
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {
        updateState(State.LOADING);

        compositeDisposable.add(
                RetrofitClient.getInterface().getTopRatedMovies(params.key, AllApi.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                            @Override
                            public void onSuccess(MovieResponse movieResponse) {
                                if (movieResponse != null) {
                                    Log.d(TAG, "onSuccess: " + movieResponse.movies.size());
                                    updateState(State.DONE);
                                    Integer adjacentKey = (params.key > 1) ? params.key - 1 : null;
                                    callback.onResult(movieResponse.movies, adjacentKey);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                                updateState(State.ERROR);
                                setRetry(() -> loadBefore(params, callback));
                            }
                        }));
    }

    @Override
    public void loadAfter(@NonNull LoadParams<Integer> params, @NonNull LoadCallback<Integer, Movie> callback) {
        updateState(State.LOADING);

        compositeDisposable.add(
                RetrofitClient.getInterface().getTopRatedMovies(params.key, AllApi.API_KEY)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<MovieResponse>() {
                            @Override
                            public void onSuccess(MovieResponse movieResponse) {
                                if (movieResponse != null) {
                                    Log.d(TAG, "onSuccess: " + movieResponse.movies.size());
                                    updateState(State.DONE);
                                    Integer key = movieResponse.movies.size() > 0 ? params.key + 1 : null;
                                    callback.onResult(movieResponse.movies, key);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.d(TAG, "onError: " + e.getMessage());
                                updateState(State.ERROR);
                                setRetry(() -> loadAfter(params, callback));
                            }
                        }));
    }

    private void updateState(State state) {
        this.stateMutableLiveData.postValue(state);
    }

    public void retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }
    }

    private void setRetry(Action action) {
        retryCompletable = action == null ? null : Completable.fromAction(action);
    }

    public MutableLiveData<State> getStateMutableLiveData() {
        return stateMutableLiveData;
    }
}
