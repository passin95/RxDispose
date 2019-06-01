package me.passin.rxdispose;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.HalfSerializer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: zbb 33775
 * @date: 2019/5/19 22:12
 * @desc:
 */
@SuppressWarnings("unchecked")
public class RxDisposeObservable<T, U> extends Observable<T> {

    final ObservableSource<T> source;
    final ObservableSource<? extends U> other;

    RxDisposeObservable(ObservableSource<T> source, ObservableSource<? extends U> other) {
        this.source = source;
        this.other = other;
    }

    @Override
    public void subscribeActual(Observer<? super T> child) {
        TakeUntilMainObserver<T, U> parent = new TakeUntilMainObserver<>(child);
        child.onSubscribe(parent);

        other.subscribe(parent.otherObserver);
        source.subscribe(parent);
    }

    static final class TakeUntilMainObserver<T, U> extends AtomicInteger
            implements Observer<T>, Disposable {

        final Observer<? super T> downstream;
        final AtomicReference<Disposable> upstream;
        final TakeUntilMainObserver.OtherObserver otherObserver;
        final AtomicThrowable error;
        Disposable downstreamDispose;

        TakeUntilMainObserver(Observer<? super T> downstream) {
            this.downstream = downstream;
            if (downstream instanceof Disposable) {
                downstreamDispose = (Disposable) downstream;
            }
            this.upstream = new AtomicReference<>();
            this.otherObserver = new TakeUntilMainObserver.OtherObserver();
            this.error = new AtomicThrowable();
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(upstream);
            DisposableHelper.dispose(otherObserver);
        }

        @Override
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(upstream.get());
        }

        @Override
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(upstream, d);
        }

        @Override
        public void onNext(T t) {
            HalfSerializer.onNext(downstream, t, this, error);
        }

        @Override
        public void onError(Throwable e) {
            DisposableHelper.dispose(otherObserver);
            HalfSerializer.onError(downstream, e, this, error);
        }

        @Override
        public void onComplete() {
            DisposableHelper.dispose(otherObserver);
            HalfSerializer.onComplete(downstream, this, error);
        }

        void otherError(Throwable e) {
            DisposableHelper.dispose(upstream);
            HalfSerializer.onError(downstream, e, this, error);
        }

        void otherComplete() {
            if (downstreamDispose != null) {
                downstreamDispose.dispose();
            } else {
                dispose();
            }
        }

        final class OtherObserver extends AtomicReference<Disposable> implements Observer<U> {

            @Override
            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            @Override
            public void onNext(U t) {
                otherComplete();
            }

            @Override
            public void onError(Throwable e) {
                otherError(e);
            }

            @Override
            public void onComplete() {
                otherComplete();
            }
        }
    }
}