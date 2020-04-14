package me.passin.rxdispose;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: zbb
 * @date: 2020/4/11 14:30
 * @desc:
 */
public class TakeUntilEventsObservable<T> extends Observable<T> {

    private final ObservableSource<T> source;
    private final Function<T, T> correspondingEvents;
    private final T[] extraEvents;

    TakeUntilEventsObservable(ObservableSource<T> source, Function<T, T> correspondingEvents, T[] extraEvents) {
        this.source = source;
        this.correspondingEvents = correspondingEvents;
        this.extraEvents = extraEvents;
    }

    @Override
    protected void subscribeActual(Observer<? super T> observer) {
        source.subscribe(new HandleTakeUtilEventObserver(observer));
    }

    final class HandleTakeUtilEventObserver implements Observer<T>, Disposable {

        final Observer<? super T> downstream;
        boolean done;
        T correspondingEvent;
        final AtomicReference<Disposable> s = new AtomicReference<>();

        HandleTakeUtilEventObserver(Observer<? super T> actual) {
            this.downstream = actual;
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this.s, d)) {
                downstream.onSubscribe(this);
            }
        }

        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }

            if (correspondingEvent != null) {
                if (correspondingEvent.equals(t)) {
                    downstream.onNext(t);
                } else {
                    checkExtraEvents(t);
                }
            } else {
                try {
                    correspondingEvent = TakeUntilEventsObservable.this.correspondingEvents.apply(t);
                } catch (Exception e) {
                    if (e instanceof OutsideLifecycleException) {
                        correspondingEvent = t;
                        downstream.onNext(t);
                    } else {
                        checkExtraEvents(t);
                    }
                }
            }
        }

        private void checkExtraEvents(T t) {
            if (extraEvents == null) {
                return;
            }
            for (T extraEvent : extraEvents) {
                if (extraEvent != null && extraEvent.equals(t)) {
                    downstream.onNext(t);
                }
            }
        }

        @Override
        public void onError(Throwable t) {
            if (done) {
                RxJavaPlugins.onError(t);
                return;
            }

            done = true;
            DisposableHelper.dispose(s);
            downstream.onError(t);
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            done = true;
            DisposableHelper.dispose(s);
            downstream.onComplete();
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(s);
        }

        @Override
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(s.get());
        }

    }

}
