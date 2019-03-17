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
package me.passin.rxdispose;

import static me.passin.rxdispose.utils.Preconditions.checkNotNull;

import io.reactivex.Observable;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;


/**
 * 保留了原来 Rxlifecycle 的使用方式，
 * 想要支持多种“类型”的 Event，泛型 R 需使用同一种类型。
 */
public class RxDispose {

    private RxDispose() {
        throw new AssertionError("No instances");
    }

    @NonNull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bindUntilEvent(@NonNull final Observable<R> lifecycle,
            @NonNull final R... events) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(events, "event == null");
        return bind(takeUntilEvent(lifecycle, events));
    }

    private static <R> Observable<R> takeUntilEvent(final Observable<R> lifecycle, final R... events) {
        return lifecycle.filter(new Predicate<R>() {
            @Override
            public boolean test(R lifecycleEvent) throws Exception {
                for (R event : events) {
                    if (lifecycleEvent.equals(event)) {
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @NonNull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@NonNull Observable<R> lifecycle,
            @NonNull final Function<R, R> correspondingEvents) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(correspondingEvents, "correspondingEvents == null");
        return bind(takeUntilCorrespondingEvent(lifecycle.share(), correspondingEvents));
    }

    @NonNull
    @CheckReturnValue
    public static <T, R> LifecycleTransformer<T> bind(@NonNull final Observable<R> lifecycle) {
        return new LifecycleTransformer<>(lifecycle);
    }

    private static <R> Observable<Boolean> takeUntilCorrespondingEvent(final Observable<R> lifecycle,
            final Function<R, R> correspondingEvents) {
        return Observable.combineLatest(
                lifecycle.take(1).map(correspondingEvents),
                lifecycle.skip(1),
                new BiFunction<R, R, Boolean>() {
                    @Override
                    public Boolean apply(R bindUntilEvent, R lifecycleEvent) throws Exception {
                        return lifecycleEvent.equals(bindUntilEvent);
                    }
                })
                .onErrorReturn(Functions.RESUME_FUNCTION)
                .filter(Functions.SHOULD_COMPLETE);
    }
}