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
import android.app.Application;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import me.passin.rxdispose.android.ActivityEvent;
import me.passin.rxdispose.android.ActivityLifecycleable;
import me.passin.rxdispose.android.ICostomEventProvider;

/**
 * @author : passin
 * @date: 2019/3/15 16:23
 */
public class ActivityLifecycleForRxDispose implements Application.ActivityLifecycleCallbacks {

    FragmentLifecycleForRxDispose mFragmentLifecycle;

    public ActivityLifecycleForRxDispose(FragmentLifecycleForRxDispose fragmentLifecycleForRxLifecycle) {
        mFragmentLifecycle = fragmentLifecycleForRxLifecycle;
    }

    /**
     * 通过桥梁对象 {@code BehaviorSubject<ActivityEvent> mLifecycleSubject}
     * 在每个 Activity 的生命周期中发出对应的生命周期事件
     */
    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        if (activity instanceof ActivityLifecycleable) {
            obtainEventProvider(activity).sendLifecycleEvent(ActivityEvent.CREATE);
            if (activity instanceof FragmentActivity) {
                ((FragmentActivity) activity).getSupportFragmentManager()
                        .registerFragmentLifecycleCallbacks(mFragmentLifecycle, true);
            }
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainEventProvider(activity).sendLifecycleEvent(ActivityEvent.START);
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainEventProvider(activity).sendLifecycleEvent(ActivityEvent.RESUME);
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainEventProvider(activity).sendLifecycleEvent(ActivityEvent.PAUSE);
        }
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainEventProvider(activity).sendLifecycleEvent(ActivityEvent.STOP);
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if (activity instanceof ActivityLifecycleable) {
            obtainEventProvider(activity).sendLifecycleEvent(ActivityEvent.DESTROY);
        }
    }

    private ICostomEventProvider obtainEventProvider(Activity activity) {
        return ((ActivityLifecycleable) activity).provideEventProvider();
    }
}
