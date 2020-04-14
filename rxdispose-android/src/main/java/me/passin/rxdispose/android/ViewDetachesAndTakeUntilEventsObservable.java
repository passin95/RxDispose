package me.passin.rxdispose.android;

import android.view.View;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: zbb
 * @date: 2020/4/12 11:23
 * @desc:
 */
public class ViewDetachesAndTakeUntilEventsObservable extends Observable<Object> {

    private final ObservableSource<Object> source;
    private final View view;
    private final Object[] extraEvents;

    ViewDetachesAndTakeUntilEventsObservable(ObservableSource<Object> source, View view, Object[] extraEvents) {
        this.source = source;
        this.view = view;
        this.extraEvents = extraEvents;
    }

    @Override
    protected void subscribeActual(Observer<? super Object> observer) {
        source.subscribe(new ViewDetachesAndTakeUntilEventsObserver(observer));
    }

    class ViewDetachesAndTakeUntilEventsObserver implements Observer<Object>, Disposable, View.OnAttachStateChangeListener {

        final Observer<Object> downstream;
        final AtomicReference<Disposable> s = new AtomicReference<>();
        boolean done;

        ViewDetachesAndTakeUntilEventsObserver(Observer<Object> actual) {
            this.downstream = actual;
            view.addOnAttachStateChangeListener(this);
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.setOnce(this.s, d)) {
                downstream.onSubscribe(this);
            }
        }

        @Override
        public void onNext(Object t) {
            if (done) {
                return;
            }
            for (Object event : extraEvents) {
                if (event != null && event.equals(t)) {
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
            dispose();
            downstream.onError(t);
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            done = true;
            dispose();
            downstream.onComplete();
        }

        @Override
        public void dispose() {
            view.removeOnAttachStateChangeListener(this);
            DisposableHelper.dispose(s);
        }

        @Override
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(s.get());
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            // Do nothing
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            downstream.onNext(ViewDetachesOnSubscribe.SIGNAL);
        }

    }

}