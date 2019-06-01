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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import java.util.concurrent.TimeUnit;
import me.passin.rxdispose.Lifecycleable;
import me.passin.rxdispose.sample.R;
import me.passin.rxdispose.sample.utils.LogUtils;
import me.passin.rxdispose.sample.utils.RxDisposeUtils;
import me.passin.rxdispose.sample.way.RxActivity;

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
    @SuppressLint("CheckResult")
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG, "onCreate()");

        setContentView(R.layout.activity_sample);
        mPresenter = new SamplePresenter(this);

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.i(TAG, "取消订阅成功：bindToLifecycle，订阅时间：onCreate()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindToLifecycle((Lifecycleable) this))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        LogUtils.i(TAG, "开始于：onCreate(), 运行至： onDestroy(): " + num);
                    }
                });
    }

    @Override
    @SuppressLint("CheckResult")
    protected void onStart() {
        super.onStart();
        LogUtils.d(TAG, "onStart()");

        mPresenter.onStart();
    }

    @Override
    @SuppressLint("CheckResult")
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.i(TAG, "取消订阅成功：bindToLifecycle，订阅时间：onResume()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindToLifecycle((Lifecycleable) this))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        LogUtils.i(TAG, "开始于：onResume(), 运行至： onPause(): " + num);
                    }
                });
    }

    @Override
    @SuppressLint("CheckResult")
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");

        Observable.interval(1, TimeUnit.SECONDS)
                .doOnDispose(new Action() {
                    @Override
                    public void run() throws Exception {
                        LogUtils.i(TAG, "取消订阅成功：bindToLifecycle，订阅时间：onPause()");
                    }
                })
                .compose(RxDisposeUtils.<Long>bindToLifecycle((Lifecycleable) this))
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long num) throws Exception {
                        LogUtils.i(TAG, "开始于：onPause(), 运行至： onStop(): " + num);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.d(TAG, "onStop()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.d(TAG, "onDestroy()");
    }
}
