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

package me.passin.rxdispose.android;

import static me.passin.rxdispose.RxDispose.bind;
import static me.passin.rxdispose.utils.Preconditions.checkNotNull;

import android.support.annotation.CheckResult;
import android.view.View;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import me.passin.rxdispose.LifecycleTransformer;
import me.passin.rxdispose.OutsideLifecycleException;

public class RxDisposeAndroid {

    private RxDisposeAndroid() {
        throw new AssertionError("No instances");
    }


    /**
     * Binds the given source to an Activity lifecycle.
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. In the case that the lifecycle sequence is in the
     * creation phase (CREATE, START, etc) it will choose the equivalent destructive phase (DESTROY,
     * STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
     * for example, if used in PAUSE, it will unsubscribe in STOP.
     * <p>
     * Due to the differences between the Activity and Fragment lifecycles, this method should only
     * be used for an Activity lifecycle.
     *
     * @param lifecycle the lifecycle sequence of an Activity
     * @return a reusable {@link LifecycleTransformer} that unsubscribes the source during the Activity lifecycle
     */
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindActivity(@NonNull final Observable<String> lifecycle) {
        return bind(lifecycle, ACTIVITY_LIFECYCLE);
    }

    /**
     * Binds the given source to a Fragment lifecycle.
     * <p>
     * This helper automatically determines (based on the lifecycle sequence itself) when the source
     * should stop emitting items. In the case that the lifecycle sequence is in the
     * creation phase (CREATE, START, etc) it will choose the equivalent destructive phase (DESTROY,
     * STOP, etc). If used in the destructive phase, the notifications will cease at the next event;
     * for example, if used in PAUSE, it will unsubscribe in STOP.
     * <p>
     * Due to the differences between the Activity and Fragment lifecycles, this method should only
     * be used for a Fragment lifecycle.
     *
     * @param lifecycle the lifecycle sequence of a Fragment
     * @return a reusable {@link LifecycleTransformer} that unsubscribes the source during the Fragment lifecycle
     */
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindFragment(@NonNull final Observable<String> lifecycle) {
        return bind(lifecycle, FRAGMENT_LIFECYCLE);
    }

    /**
     * Binds the given source to a View lifecycle.
     * <p>
     * Specifically, when the View detaches from the window, the sequence will be completed.
     * <p>
     * Warning: you should make sure to use the returned Transformer on the main thread,
     * since we're binding to a View (which only allows binding on the main thread).
     *
     * @param view the view to bind the source sequence to
     * @return a reusable {@link LifecycleTransformer} that unsubscribes the source during the View lifecycle
     */
    @NonNull
    @CheckResult
    public static <T> LifecycleTransformer<T> bindView(@NonNull final View view) {
        checkNotNull(view, "view == null");
        return bind(Observable.create(new ViewDetachesOnSubscribe(view)));
    }

    // Figures out which corresponding next lifecycle event in which to unsubscribe, for Activities
    private static final Function<String, String> ACTIVITY_LIFECYCLE =
            new Function<String, String>() {
                @Override
                public String apply(String lastEvent) throws Exception {
                    if (ActivityLifecycle.CREATE.equals(lastEvent)) {
                        return ActivityLifecycle.DESTROY;
                    } else if (ActivityLifecycle.START.equals(lastEvent)) {
                        return ActivityLifecycle.STOP;
                    } else if (ActivityLifecycle.RESUME.equals(lastEvent)) {
                        return ActivityLifecycle.PAUSE;
                    } else if (ActivityLifecycle.PAUSE.equals(lastEvent)) {
                        return ActivityLifecycle.STOP;
                    } else if (ActivityLifecycle.STOP.equals(lastEvent)) {
                        return ActivityLifecycle.DESTROY;
                    } else if (ActivityLifecycle.DESTROY.equals(lastEvent)) {
                        throw new OutsideLifecycleException(
                                "Cannot bind to Activity lifecycle when outside of it.");
                    } else {
                        throw new UnsupportedOperationException(
                                "Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };

    // Figures out which corresponding next lifecycle event in which to unsubscribe, for Fragments
    private static final Function<String, String> FRAGMENT_LIFECYCLE =
            new Function<String, String>() {
                @Override
                public String apply(String lastEvent) throws Exception {
                    if (FragmentLifecycle.ATTACH.equals(lastEvent)) {
                        return FragmentLifecycle.DETACH;
                    } else if (FragmentLifecycle.CREATE.equals(lastEvent)) {
                        return ActivityLifecycle.DESTROY;
                    } else if (FragmentLifecycle.CREATE_VIEW.equals(lastEvent)) {
                        return FragmentLifecycle.DESTROY_VIEW;
                    } else if (FragmentLifecycle.START.equals(lastEvent)) {
                        return FragmentLifecycle.STOP;
                    } else if (FragmentLifecycle.RESUME.equals(lastEvent)) {
                        return FragmentLifecycle.PAUSE;
                    } else if (FragmentLifecycle.PAUSE.equals(lastEvent)) {
                        return FragmentLifecycle.STOP;
                    } else if (FragmentLifecycle.STOP.equals(lastEvent)) {
                        return FragmentLifecycle.DESTROY_VIEW;
                    } else if (FragmentLifecycle.DESTROY_VIEW.equals(lastEvent)) {
                        return FragmentLifecycle.DESTROY;
                    } else if (FragmentLifecycle.DESTROY.equals(lastEvent)) {
                        return FragmentLifecycle.DETACH;
                    } else if (FragmentLifecycle.DETACH.equals(lastEvent)) {
                        throw new OutsideLifecycleException(
                                "Cannot bind to Activity lifecycle when outside of it.");
                    } else {
                        throw new UnsupportedOperationException(
                                "Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };

}
