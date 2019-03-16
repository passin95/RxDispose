/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.passin.rxdispose.sample.view.error;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;
import me.passin.rxdispose.android.ActivityLifecycle;
import me.passin.rxdispose.sample.R;
import me.passin.rxdispose.sample.utils.RxDisposeUtils;
import me.passin.rxdispose.sample.way.RxActivity;

/**
 * <pre>
 * @author : passin
 * @Date: 2019/3/15 10:49
 * </pre>
 * ------------------------------
 * 主要展示一些错误的使用方式。
 * 正确使用的关键点在于：PublishSubject 不会发送订阅前的所接收到的数据（Event）。
 */
public class ErrorSampleActivity extends RxActivity {

    public static final String TAG = "ErrorSampleTest";

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, ErrorSampleActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_sample);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "SampleActivity Unsubscribing subscription from next onStop()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindUntilEvent(this, ActivityLifecycle.STOP))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "SampleActivity Started after super.onStop(),"
                                + " running until next onStop(): " + num);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        // 这是一个正确的实例，可以取消订阅。
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "SampleActivity Unsubscribing subscription from onDestroy()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindUntilEvent(this, ActivityLifecycle.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "SampleActivity Started before super.onDestroy(),"
                                + " running until in onDestroy(): " + num);
                    }
                });

        super.onDestroy();
        Log.d(TAG, "onDestroy()");

        // 这是一个错误的实例，将无法取消订阅。
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "SampleActivity Unsubscribing subscription from next onDestroy()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindUntilEvent(this, ActivityLifecycle.DESTROY))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG,
                                "SampleActivity Started in after super.onDestroy(),"
                                        + "unable to cancel subscription :" + num);
                    }
                });
    }
}
