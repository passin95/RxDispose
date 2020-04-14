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
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import me.passin.rxdispose.android.FragmentEvent;
import me.passin.rxdispose.android.FragmentEventProvider;
import me.passin.rxdispose.android.FragmentLifecycleable;

/**
 * @author : passin
 * @date: 2019/3/15 16:26
 */
public class FragmentLifecycleForRxDispose extends FragmentManager.FragmentLifecycleCallbacks {

    public FragmentLifecycleForRxDispose() {
    }

    @Override
    public void onFragmentAttached(@NonNull FragmentManager fm, Fragment f, Context context) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.ATTACH);
        }
    }

    @Override
    public void onFragmentCreated(@NonNull FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.CREATE);
        }
    }

    @Override
    public void onFragmentViewCreated(@NonNull FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.CREATE_VIEW);
        }
    }

    @Override
    public void onFragmentStarted(@NonNull FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.START);
        }
    }

    @Override
    public void onFragmentResumed(@NonNull FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.RESUME);
        }
    }

    @Override
    public void onFragmentPaused(@NonNull FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.PAUSE);
        }
    }

    @Override
    public void onFragmentStopped(@NonNull FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.STOP);
        }
    }

    @Override
    public void onFragmentViewDestroyed(@NonNull FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.DESTROY_VIEW);
        }
    }

    @Override
    public void onFragmentDestroyed(@NonNull FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.DESTROY);
        }
    }

    @Override
    public void onFragmentDetached(@NonNull FragmentManager fm, Fragment f) {
        if (f instanceof FragmentLifecycleable) {
            obtainSubject(f).sendLifecycleEvent(FragmentEvent.DETACH);
        }
    }

    private FragmentEventProvider obtainSubject(Fragment fragment) {
        return ((FragmentLifecycleable) fragment).getEventProvider();
    }
}
