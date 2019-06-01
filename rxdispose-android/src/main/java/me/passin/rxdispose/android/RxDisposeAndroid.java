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

    @NonNull
    public static <T> LifecycleTransformer<T> bindActivity(@NonNull final Observable<ActivityEvent> lifecycle) {
        return bind(lifecycle, ACTIVITY_LIFECYCLE);
    }

    @NonNull
    public static <T> LifecycleTransformer<T> bindFragment(@NonNull final Observable<FragmentEvent> lifecycle) {
        return bind(lifecycle, FRAGMENT_LIFECYCLE);
    }

    @NonNull
    public static <T> LifecycleTransformer<T> bindView(@NonNull final View view) {
        checkNotNull(view, "view == null");
        return bind(Observable.create(new ViewDetachesOnSubscribe(view)));
    }

    private static final Function<ActivityEvent, ActivityEvent> ACTIVITY_LIFECYCLE =
            new Function<ActivityEvent, ActivityEvent>() {
                @Override
                public ActivityEvent apply(ActivityEvent lastEvent) throws Exception {
                    switch (lastEvent) {
                        case CREATE:
                            return ActivityEvent.DESTROY;
                        case START:
                            return ActivityEvent.STOP;
                        case RESUME:
                            return ActivityEvent.PAUSE;
                        case PAUSE:
                            return ActivityEvent.STOP;
                        case STOP:
                            return ActivityEvent.DESTROY;
                        case DESTROY:
                            throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                        default:
                            throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };

    private static final Function<FragmentEvent, FragmentEvent> FRAGMENT_LIFECYCLE =
            new Function<FragmentEvent, FragmentEvent>() {
                @Override
                public FragmentEvent apply(FragmentEvent lastEvent) throws Exception {
                    switch (lastEvent) {
                        case ATTACH:
                            return FragmentEvent.DETACH;
                        case CREATE:
                            return FragmentEvent.DESTROY;
                        case CREATE_VIEW:
                            return FragmentEvent.DESTROY_VIEW;
                        case START:
                            return FragmentEvent.STOP;
                        case RESUME:
                            return FragmentEvent.PAUSE;
                        case PAUSE:
                            return FragmentEvent.STOP;
                        case STOP:
                            return FragmentEvent.DESTROY_VIEW;
                        case DESTROY_VIEW:
                            return FragmentEvent.DESTROY;
                        case DESTROY:
                            return FragmentEvent.DETACH;
                        case DETACH:
                            throw new OutsideLifecycleException(
                                    "Cannot bind to Fragment lifecycle when outside of it.");
                        default:
                            throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                    }
                }
            };
}
