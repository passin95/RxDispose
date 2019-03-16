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

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;
import me.passin.rxdispose.LifecycleProvider;
import me.passin.rxdispose.OutsideLifecycleException;
import me.passin.rxdispose.android.ActivityLifecycle;
import me.passin.rxdispose.android.FragmentLifecycle;

public final class AndroidLifecycle implements LifecycleProvider<String>, LifecycleObserver {

    public static LifecycleProvider<String> createLifecycleProvider(LifecycleOwner owner) {
        return new AndroidLifecycle(owner);
    }

    private final PublishSubject<String> lifecycleSubject = PublishSubject.create();

    private AndroidLifecycle(LifecycleOwner owner) {
        owner.getLifecycle().addObserver(this);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    void onEvent(LifecycleOwner owner, Lifecycle.Event event) {
        switch (event) {
            case ON_CREATE:
                if (owner instanceof Activity) {
                    lifecycleSubject.onNext(ActivityLifecycle.DESTROY);
                } else if (owner instanceof Fragment) {
                    lifecycleSubject.onNext(FragmentLifecycle.DESTROY_VIEW);
                }
                break;
            case ON_START:
                if (owner instanceof Activity) {
                    lifecycleSubject.onNext(ActivityLifecycle.STOP);
                } else if (owner instanceof Fragment) {
                    lifecycleSubject.onNext(FragmentLifecycle.STOP);
                }
                break;
            case ON_RESUME:
                if (owner instanceof Activity) {
                    lifecycleSubject.onNext(ActivityLifecycle.PAUSE);
                } else if (owner instanceof Fragment) {
                    lifecycleSubject.onNext(FragmentLifecycle.PAUSE);
                }
                break;
            case ON_PAUSE:
                if (owner instanceof Activity) {
                    lifecycleSubject.onNext(ActivityLifecycle.STOP);
                } else if (owner instanceof Fragment) {
                    lifecycleSubject.onNext(FragmentLifecycle.STOP);
                }
                break;
            case ON_STOP:
                if (owner instanceof Activity) {
                    lifecycleSubject.onNext(ActivityLifecycle.DESTROY);
                } else if (owner instanceof Fragment) {
                    lifecycleSubject.onNext(FragmentLifecycle.DESTROY_VIEW);
                }
                break;
            case ON_DESTROY:
                throw new OutsideLifecycleException(
                        "Cannot bind to Activity lifecycle when outside of it.");
            default:
                break;
        }
        if (event == Lifecycle.Event.ON_DESTROY) {
            owner.getLifecycle().removeObserver(this);
        }
    }

    @NonNull
    @Override
    public Subject<String> provideLifecycleSubject() {
        return lifecycleSubject;
    }
}
