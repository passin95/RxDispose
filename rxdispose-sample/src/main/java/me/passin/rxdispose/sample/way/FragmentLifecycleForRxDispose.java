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

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import io.reactivex.subjects.Subject;
import me.passin.rxdispose.LifecycleProvider;
import me.passin.rxdispose.android.FragmentLifecycle;

/**
 * <pre>
 * @author : passin
 * @Date: 2019/3/15 16:26
 * </pre>
 */
public class FragmentLifecycleForRxDispose extends FragmentManager.FragmentLifecycleCallbacks {


    public FragmentLifecycleForRxDispose() {
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.ATTACH);
        }
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.CREATE);
        }
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.CREATE_VIEW);
        }
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.START);
        }
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.RESUME);
        }
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.PAUSE);
        }
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.STOP);
        }
    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.DESTROY_VIEW);
        }
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.DESTROY);
        }
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycle) {
            obtainSubject(f).onNext(FragmentLifecycle.DETACH);
        }
    }

    private Subject<String> obtainSubject(Fragment fragment) {
        return ((LifecycleProvider) fragment).provideLifecycleSubject();
    }
}
