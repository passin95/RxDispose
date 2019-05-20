package me.passin.rxdispose.sample.view.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;
import me.passin.rxdispose.android.ActivityEvent;
import me.passin.rxdispose.sample.R;
import me.passin.rxdispose.sample.utils.RxDisposeUtils;
import me.passin.rxdispose.sample.way.RxActivity;

/**
 * @author: passin
 * @date: 2019/3/15 16:43
 * @desc: 支持自定义 Event 去取消订阅。
 */
public class CustomEventActivity extends RxActivity {

    public static final String TAG = "CustomTest";
    private static final String EXAMPLE_EVENT = "exceptionEvent";
    private static final String CLICK_EVENT = "clickEvent";

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, CustomEventActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_event);

        Single.timer(10, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "Unsubscribing trainingInRotationUntilExceptionEvent");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindToLifecycle(this))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        Log.i(TAG, "Unsubscribing trainingInRotationUntilExceptionEvent");
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "Unsubscribing trainingInRotationUntilExceptionEvent");
                    }
                });
    }


    /**
     * 开启轮训直到 onDestroy() 或者 ExceptionEvent
     */
    @SuppressLint("CheckResult")
    public void trainingInRotationUntilExceptionEvent(View view) {
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "Unsubscribing trainingInRotationUntilExceptionEvent");
                    }
                })
                .compose(
                        RxDisposeUtils.<Long>bindUntilEvent(this, ActivityEvent.DESTROY, EXAMPLE_EVENT))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG,
                                "trainingInRotationUntilExceptionEvent Started , running until onDestroy() or "
                                        + "EXCEPTION_EVENT: " + num);
                    }
                });
    }

    /**
     * 开启轮训直到 onStop() 或者 ClickEvent
     */
    @SuppressLint("CheckResult")
    public void trainingInRotationUntilClickEvent(View view) {
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "Unsubscribing trainingInRotationUntilClickEvent");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindUntilEvent(this, ActivityEvent.PAUSE, CLICK_EVENT))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG,
                                "trainingInRotationUntilClickEvent Started , running until onStop() or "
                                        + "CLICK_EVENT: " + num);
                    }
                });
    }

    public void triggerExceptionEvent(View view) {
        try {
            throw new Exception("this is a excepation");
        } catch (Exception e) {
            provideEventProvider().sendCostomEvent(EXAMPLE_EVENT);
        }
    }


    public void triggerClickEvent(View view) {
        provideEventProvider().sendCostomEvent(CLICK_EVENT);
    }
}
