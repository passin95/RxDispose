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

package me.passin.rxdispose.sample.way;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import me.passin.rxdispose.android.ActivityLifecycle;

public class RxActivity extends AppCompatActivity implements ActivityLifecycle {

    private final PublishSubject<String> lifecycleSubject = PublishSubject.create();

    @NonNull
    @Override
    public Subject<String> provideLifecycleSubject() {
        return lifecycleSubject;
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(ActivityLifecycle.CREATE);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityLifecycle.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityLifecycle.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityLifecycle.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityLifecycle.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityLifecycle.DESTROY);
        super.onDestroy();
    }
}
