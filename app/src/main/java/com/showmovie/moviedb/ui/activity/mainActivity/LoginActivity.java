package com.showmovie.moviedb.ui.activity.mainActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.showmovie.moviedb.R;
import com.google.android.material.textfield.TextInputLayout;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;


public class LoginActivity extends AppCompatActivity {

    public static LoginActivity INSTANT = null;

    //TextInput Layout0
//    @BindView(R.id.text_input_email)
    TextInputLayout text_input_email;
//    @BindView(R.id.text_input_password)
    TextInputLayout text_input_password;

    // EditText
//    @BindView(R.id.edt_email)
    EditText edt_email;
//    @BindView(R.id.edt_password)
    EditText edt_password;

    //Button
//    @BindView(R.id.btnLogin)
    Button btnLogin;

    //Subscription that represents a group of Subscriptions that are unsubscribed together.
    private CompositeSubscription mCompositeSubscription;

    private Observable<CharSequence> mEmailObservable, mPasswordObservable;

    public static LoginActivity getInstant() {
        if (INSTANT == null) {
            INSTANT = new LoginActivity();
        }
        return INSTANT;
    }


    /**
     * init edittext observable
     */
    private void initEditTextObservable() {
        mEmailObservable = RxTextView.textChanges(edt_email);
        mPasswordObservable = RxTextView.textChanges(edt_password);
    }

    private void initSubscriber() {

        //=========== For Email Validation
        Subscription mEmailSubscription = mEmailObservable.doOnNext(new Action1<CharSequence>() {
            @Override
            public void call(CharSequence charSequence) {
                emailEditTextError(1); // disable email error
            }
        })
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return !TextUtils.isEmpty(charSequence); // check if not null
                    }
                }).observeOn(AndroidSchedulers.mainThread()) // Main UI Thread
                .subscribe(new Subscriber<CharSequence>() {
                    @Override
                    public void onCompleted() {
                        // on Completed
                    }

                    @Override
                    public void onError(Throwable e) {
                        // Error
                        Log.e("mEmailSubscription",e.getMessage());
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        // Check every user input for valid email address
                        if (!isUserInputValid(charSequence.toString(), 1)) {
                            emailEditTextError(2); // show error for invalid email
                        } else {
                            emailEditTextError(1); // hide error on valid email
                        }
                    }
                });
        mCompositeSubscription.add(mEmailSubscription); // Add email subscriber in composite subscription


        //=========== For Password Validation
        Subscription mPasswordSubscription = mPasswordObservable
                .doOnNext(new Action1<CharSequence>() {
                    @Override
                    public void call(CharSequence charSequence) {
                        passwordEditTextError(1);
                    }
                })
                .debounce(500, TimeUnit.MILLISECONDS)
                .filter(new Func1<CharSequence, Boolean>() {
                    @Override
                    public Boolean call(CharSequence charSequence) {
                        return !TextUtils.isEmpty(charSequence);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<CharSequence>() {
                    @Override
                    public void onCompleted() {
                        // On Completed
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("mPasswordSubscription",e.getMessage());
                    }

                    @Override
                    public void onNext(CharSequence charSequence) {
                        if (!isUserInputValid(charSequence.toString(), 2)) {
                            passwordEditTextError(2);
                        } else {
                            passwordEditTextError(1);
                        }
                    }
                });

        mCompositeSubscription.add(mPasswordSubscription);


        // Check Both user input ( email and password)

        Subscription allFieldsSubscription=Observable.combineLatest(mEmailObservable, mPasswordObservable, new Func2<CharSequence, CharSequence, Boolean>() {
            @Override
            public Boolean call(CharSequence mEmail, CharSequence mPassword) {
                return isUserInputValid(mEmail.toString(),1) && isUserInputValid(mPassword.toString(),2) ;
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("allFieldsSubscription",e.getMessage());
                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if(aBoolean)
                        {
                            signInButtonState(1); // enable login button
                        }
                        else{
                            signInButtonState(2); // disable login button
                        }
                    }
                });

        mCompositeSubscription.add(allFieldsSubscription);

    }

    private void emailEditTextError(int whichCase) {
        switch (whichCase) {
            case 1: // for hide error
                if (text_input_email.getChildCount() == 2) {
                    text_input_email.getChildAt(1).setVisibility(View.GONE);
                }
                text_input_email.setError(null);
                break;
            case 2: // for show error
                if (text_input_email.getChildCount() == 2) {
                    text_input_email.getChildAt(1).setVisibility(View.VISIBLE);
                }
                text_input_email.setError(getString(R.string.str_enter_valid_email));
                break;
        }
    }

    private void passwordEditTextError(int whichCase) {
        switch (whichCase) {
            case 1: // for hide error
                if (text_input_password.getChildCount() == 2) {
                    text_input_password.getChildAt(1).setVisibility(View.GONE);
                }
                text_input_password.setError(null);
                break;
            case 2: // for show error
                if (text_input_password.getChildCount() == 2) {
                    text_input_password.getChildAt(1).setVisibility(View.VISIBLE);
                }
                text_input_password.setError(getString(R.string.str_enter_password));
                break;
        }
    }

    private void signInButtonState(int whichCase) {
        switch (whichCase) {
            case 1: // enable button
                btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
                btnLogin.setEnabled(true);
                btnLogin.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                break;
            case 2: // disable button
                btnLogin.setBackgroundColor(ContextCompat.getColor(this, R.color.color_disable));
                btnLogin.setEnabled(false);
                btnLogin.setTextColor(ContextCompat.getColor(this, R.color.color_disable_text));
                break;
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.login);
            mCompositeSubscription = new CompositeSubscription();
            init();
            initEditTextObservable();
            initSubscriber();

            btnLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                    Intent in = new Intent(LoginActivity.this, ShowMovie.class);
                    startActivity(in);
                }
            });
        }catch (Exception e)
        {

        }
    }

    private void init() {
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        text_input_email = findViewById(R.id.text_input_email);
        text_input_password = findViewById(R.id.text_input_password);
        btnLogin = findViewById(R.id.btnLogin);


    }

    /**
     * Validate user details for email and password
     */

    private boolean isUserInputValid(String userInput, int whichCase) {
        switch (whichCase) {
            case 1: // check email input
                return !TextUtils.isEmpty(userInput) && Patterns.EMAIL_ADDRESS.matcher(userInput).matches();
            case 2: // check password input
                return userInput.length() >= 6;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeSubscription.unsubscribe();
    }
}
