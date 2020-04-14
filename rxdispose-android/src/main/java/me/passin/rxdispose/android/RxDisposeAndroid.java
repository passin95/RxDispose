package me.passin.rxdispose.android;

import android.view.View;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import me.passin.rxdispose.LifecycleTransformer;
import me.passin.rxdispose.OutsideLifecycleException;
import static me.passin.rxdispose.RxDispose.bind;
import static me.passin.rxdispose.utils.Preconditions.checkNotNull;

public class RxDisposeAndroid {

    private RxDisposeAndroid() {
        throw new AssertionError("No instances");
    }

    @NonNull
    public static <T> LifecycleTransformer<T> bindActivity(@NonNull final Observable<Object> lifecycle, Object... extraEvents) {
        return bind(lifecycle, ACTIVITY_LIFECYCLE, extraEvents);
    }

    @NonNull
    public static <T> LifecycleTransformer<T> bindFragment(@NonNull final Observable<Object> lifecycle, Object... extraEvents) {
        return bind(lifecycle, FRAGMENT_LIFECYCLE, extraEvents);
    }

    @NonNull
    public static <T> LifecycleTransformer<T> bindView(@NonNull final View view) {
        checkNotNull(view, "view == null");
        return bind(Observable.create(new ViewDetachesOnSubscribe(view)));
    }

    @NonNull
    public static <T> LifecycleTransformer<T> bindView(@NonNull final View view, @NonNull final Observable<Object> lifecycle,
            Object... extraEvents) {
        checkNotNull(view, "view == null");
        return bind(new ViewDetachesAndTakeUntilEventsObservable(lifecycle, view, extraEvents));
    }

    private static final Function<Object, Object> ACTIVITY_LIFECYCLE =
            new Function<Object, Object>() {
                @Override
                public Object apply(Object lastEvent) throws Exception {
                    if (ActivityEvent.CREATE.equals(lastEvent)) {
                        return ActivityEvent.DESTROY;
                    } else if (ActivityEvent.START.equals(lastEvent)) {
                        return ActivityEvent.STOP;
                    } else if (ActivityEvent.RESUME.equals(lastEvent)) {
                        return ActivityEvent.PAUSE;
                    } else if (ActivityEvent.PAUSE.equals(lastEvent)) {
                        return ActivityEvent.STOP;
                    } else if (ActivityEvent.STOP.equals(lastEvent)) {
                        return ActivityEvent.DESTROY;
                    } else if (ActivityEvent.DESTROY.equals(lastEvent)) {
                        throw new OutsideLifecycleException("Cannot bind to Activity lifecycle when outside of it.");
                    }
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                }
            };

    private static final Function<Object, Object> FRAGMENT_LIFECYCLE =
            new Function<Object, Object>() {
                @Override
                public Object apply(Object lastEvent) throws Exception {
                    if (FragmentEvent.ATTACH.equals(lastEvent)) {
                        return FragmentEvent.DETACH;
                    } else if (FragmentEvent.CREATE.equals(lastEvent)) {
                        return FragmentEvent.DESTROY;
                    } else if (FragmentEvent.CREATE_VIEW.equals(lastEvent)) {
                        return FragmentEvent.DESTROY_VIEW;
                    } else if (FragmentEvent.START.equals(lastEvent)) {
                        return FragmentEvent.STOP;
                    } else if (FragmentEvent.RESUME.equals(lastEvent)) {
                        return FragmentEvent.PAUSE;
                    } else if (FragmentEvent.PAUSE.equals(lastEvent)) {
                        return FragmentEvent.STOP;
                    } else if (FragmentEvent.STOP.equals(lastEvent)) {
                        return FragmentEvent.DESTROY_VIEW;
                    } else if (FragmentEvent.DESTROY_VIEW.equals(lastEvent)) {
                        return FragmentEvent.DESTROY;
                    } else if (FragmentEvent.DESTROY.equals(lastEvent)) {
                        return FragmentEvent.DETACH;
                    } else if (FragmentEvent.DETACH.equals(lastEvent)) {
                        throw new OutsideLifecycleException(
                                "Cannot bind to Fragment lifecycle when outside of it.");
                    }
                    throw new UnsupportedOperationException("Binding to " + lastEvent + " not yet implemented");
                }
            };

}
