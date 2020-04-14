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
package me.passin.rxdispose.sample.utils;

import io.reactivex.annotations.NonNull;
import me.passin.rxdispose.LifecycleTransformer;
import me.passin.rxdispose.Lifecycleable;
import me.passin.rxdispose.RxDispose;
import me.passin.rxdispose.android.ActivityLifecycleable;
import me.passin.rxdispose.android.FragmentLifecycleable;
import me.passin.rxdispose.android.RxDisposeAndroid;
import me.passin.rxdispose.sample.view.sample.IView;

/**
 * @author : passin
 * @date: 2019/3/15 10:27
 */
@SuppressWarnings("unchecked")
public class RxDisposeUtils {

    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
            @NonNull final Object... event) {
        if (view instanceof Lifecycleable) {
            return bindUntilEvent((Lifecycleable) view, event);
        }else {
            throw new IllegalArgumentException("view isn't Lifecycleable");
        }
    }

    public static <T> LifecycleTransformer<T> bindUntilEvent(
            @NonNull final Lifecycleable lifecycleable, @NonNull final Object... event) {
        return RxDispose.bindUntilEvent(lifecycleable.getEventProvider().getObservable(), event);
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IView view, @NonNull final Object... event) {
        if (view instanceof ActivityLifecycleable) {
            return bindToLifecycle((ActivityLifecycleable) view, event);
        } else if (view instanceof FragmentLifecycleable) {
            return bindToLifecycle((FragmentLifecycleable) view, event);
        } else {
            throw new IllegalArgumentException("view isn't Lifecycleable");
        }
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull ActivityLifecycleable lifecycleProvider,
            @NonNull final Object... event) {
        return RxDisposeAndroid.bindActivity(lifecycleProvider.getEventProvider().getObservable(), event);
    }

    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull FragmentLifecycleable lifecycleProvider,
            @NonNull final Object... event) {
        return RxDisposeAndroid.bindFragment(lifecycleProvider.getEventProvider().getObservable(), event);
    }

}
