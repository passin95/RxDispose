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

import static me.passin.rxdispose.utils.Preconditions.checkNotNull;

import io.reactivex.annotations.NonNull;
import me.passin.rxdispose.Lifecycleable;
import me.passin.rxdispose.LifecycleTransformer;
import me.passin.rxdispose.RxDispose;
import me.passin.rxdispose.android.ActivityLifecycle;
import me.passin.rxdispose.android.FragmentLifecycle;
import me.passin.rxdispose.android.RxDisposeAndroid;
import me.passin.rxdispose.sample.view.sample.IView;

/**
 * @author : passin
 * @date: 2019/3/15 10:27
 */
public class RxDisposeUtils {

    @SuppressWarnings("unchecked")
    public static <T> LifecycleTransformer<T> bindUntilEvent(@NonNull final IView view,
            @NonNull final String... event) {
        checkNotNull(view, "view == null");
        if (view instanceof Lifecycleable) {
            return bindUntilEvent((Lifecycleable) view, event);
        } else {
            throw new IllegalArgumentException("view isn't LifecycleProvider");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> LifecycleTransformer<T> bindUntilEvent(
            @NonNull final Lifecycleable<String> lifecycleProvider, @NonNull final String... event) {
        checkNotNull(lifecycleProvider, "lifecycleable == null");
        checkNotNull(event, "event == null");
        return RxDispose.bindUntilEvent(lifecycleProvider.provideEventProvider().getObservable(), event);
    }

    @SuppressWarnings("unchecked")
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull IView view) {
        checkNotNull(view, "view == null");
        if (view instanceof Lifecycleable) {
            return bindToLifecycle((Lifecycleable) view);
        } else {
            throw new IllegalArgumentException("view isn't LifecycleProvider");
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> LifecycleTransformer<T> bindToLifecycle(@NonNull Lifecycleable<String> lifecycleProvider) {
        checkNotNull(lifecycleProvider, "lifecycleProvider == null");
        if (lifecycleProvider instanceof ActivityLifecycle) {
            return RxDisposeAndroid.bindActivity(lifecycleProvider.provideEventProvider().getObservable());
        } else if (lifecycleProvider instanceof FragmentLifecycle) {
            return RxDisposeAndroid.bindFragment(lifecycleProvider.provideEventProvider().getObservable());
        } else {
            throw new IllegalArgumentException("lifecycleProvider not match");
        }
    }

}
