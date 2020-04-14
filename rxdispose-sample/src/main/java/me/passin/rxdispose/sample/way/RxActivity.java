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
import me.passin.rxdispose.android.ActivityEvent;
import me.passin.rxdispose.android.ActivityEventProvider;
import me.passin.rxdispose.android.ActivityLifecycleable;

public class RxActivity extends AppCompatActivity implements ActivityLifecycleable {

    private final ActivityEventProvider mCostomEventProvide = ActivityEventProvider.create();

    @NonNull
    @Override
    public ActivityEventProvider getEventProvider() {
        return mCostomEventProvide;
    }

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCostomEventProvide.sendLifecycleEvent(ActivityEvent.CREATE);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        mCostomEventProvide.sendLifecycleEvent(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        mCostomEventProvide.sendLifecycleEvent(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        mCostomEventProvide.sendLifecycleEvent(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        mCostomEventProvide.sendLifecycleEvent(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        mCostomEventProvide.sendLifecycleEvent(ActivityEvent.DESTROY);
        super.onDestroy();
    }
}
