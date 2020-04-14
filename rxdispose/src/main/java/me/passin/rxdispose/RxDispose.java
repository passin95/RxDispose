package me.passin.rxdispose;

import io.reactivex.Observable;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import static me.passin.rxdispose.utils.Preconditions.checkNotNull;

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
    @SafeVarargs
    public static <T, E> LifecycleTransformer<T> bindUntilEvent(@NonNull final Observable<E> lifecycle,
            @NonNull final E... events) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(events, "events == null");
        return bind(lifecycle.filter(new Predicate<E>() {
            @Override
            public boolean test(E lifecycleEvent) {
                for (E event : events) {
                    if (lifecycleEvent.equals(event)) {
                        return true;
                    }
                }
                return false;
            }
        }));
    }

    @NonNull
    @CheckReturnValue
    @SafeVarargs
    public static <T, E> LifecycleTransformer<T> bind(@NonNull Observable<E> lifecycle,
            @NonNull final Function<E, E> correspondingEvents, @Nullable final E... extraEvents) {
        checkNotNull(lifecycle, "lifecycle == null");
        checkNotNull(correspondingEvents, "correspondingEvents == null");
        return bind(new TakeUntilEventsObservable<>(lifecycle, correspondingEvents, extraEvents));
    }

    @NonNull
    public static <T, E> LifecycleTransformer<T> bind(@NonNull final Observable<E> lifecycle) {
        checkNotNull(lifecycle, "lifecycle == null");
        return new LifecycleTransformer<>(lifecycle);
    }

}