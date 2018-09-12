package com.zain.practice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;


import com.jakewharton.rxbinding.view.RxView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;

public class BufferOperatorActivity extends AppCompatActivity {
    private static final String TAG = BufferOperatorActivity.class.getSimpleName();

    @BindView(R.id.tap_result)
    TextView txtTapResult;

    @BindView(R.id.tap_result_max_count)
    TextView txtTapResultMax;

    @BindView(R.id.layout_tap_area)
    Button btnTapArea;

    private Disposable disposable;
    private Unbinder unbinder;
    private int maxTaps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer_operator);
        unbinder = ButterKnife.bind(this);


        RxView.clicks(btnTapArea)
                .map(new Func1<Object, String>() {
                    @Override
                    public String call(Object o) {
                        return "0";
                    }
                })
                .buffer(3, TimeUnit.SECONDS)
                .observeOn(rx.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(new rx.Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<String> integers) {
                        Log.e(TAG, "onNext: " + integers.size() + " taps received!");
                        if (integers.size() > 0) {
                            maxTaps = integers.size() > maxTaps ? integers.size() : maxTaps;
                            txtTapResult.setText(String.format("Received %d taps in 3 secs", integers.size()));
                            txtTapResultMax.setText(String.format("Maximum of %d taps received in this session", maxTaps));
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        disposable.dispose();
    }

}