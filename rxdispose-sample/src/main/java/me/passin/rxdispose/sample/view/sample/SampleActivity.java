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

package me.passin.rxdispose.sample.view.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;
import me.passin.rxdispose.LifecycleProvider;
import me.passin.rxdispose.android.ActivityLifecycle;
import me.passin.rxdispose.sample.R;
import me.passin.rxdispose.sample.way.RxActivity;
import me.passin.rxdispose.sample.utils.RxDisposeUtils;

/**
 * @author : passin
 * @date: 2019/3/15 10:49
 */
public class SampleActivity extends RxActivity implements IView {

    public static final String TAG = "SampleTest";

    private SamplePresenter mPresenter;

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SampleActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");

        setContentView(R.layout.activity_sample);
        mPresenter = new SamplePresenter(this);

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "SampleActivity Unsubscribing subscription from onCreate()");
                    }
                })
                .compose(
                        RxDisposeUtils.<Long>bindUntilEvent((LifecycleProvider<String>) this, ActivityLifecycle.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "SampleActivity Started in onCreate(), running until onPause(): " + num);
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart()");

        mPresenter.onStart();

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "SampleActivity Unsubscribing subscription from onStart()");
                    }
                })
                .compose(
                        RxDisposeUtils.<Long>bindUntilEvent((LifecycleProvider<String>) this, ActivityLifecycle.PAUSE))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "SampleActivity Started in onStart(), running until in onStop(): " + num);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        mPresenter.onResume();

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "SampleActivity Unsubscribing subscription from onResume()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindToLifecycle((LifecycleProvider<String>) this))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "SampleActivity Started in onResume(), running until in onPause(): " + num);
                    }
                });
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        Log.i(TAG, "SampleActivity Unsubscribing subscription from onPause()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindToLifecycle((LifecycleProvider<String>) this))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        Log.i(TAG, "SampleActivity Started in onResume(), running until in onStop(): " + num);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
