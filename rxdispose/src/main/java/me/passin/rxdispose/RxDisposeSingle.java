package me.passin.rxdispose;

import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: zbb 33775
 * @date: 2019/5/19 23:15
 * @desc:
 */
public class RxDisposeSingle<T, U> extends Single<T> {

    private final SingleSource<T> source;
    private final SingleSource<U> other;

    RxDisposeSingle(SingleSource<T> source, SingleSource<U> other) {
        this.source = source;
        this.other = other;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super T> observer) {
        TakeUntilMainObserver parent = new TakeUntilMainObserver(observer);
        observer.onSubscribe(parent);

        other.subscribe(parent.other);

        source.subscribe(parent);
    }

    final class TakeUntilMainObserver extends AtomicReference<Disposable>
            implements SingleObserver<T>, Disposable {

        final SingleObserver<? super T> downstream;
        final TakeUntilOtherSingle other;
        Disposable downstreamDispose;

        TakeUntilMainObserver(SingleObserver<? super T> downstream) {
            this.downstream = downstream;
            if (downstream instanceof Disposable) {
                downstreamDispose = (Disposable) downstream;
            }
            this.other = new TakeUntilOtherSingle();
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
            DisposableHelper.dispose(other);
        }

        @Override
        public boolean isDisposed() {
            return DisposableHelper.isDisposed(get());
        }

        @Override
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        @Override
        public void onSuccess(T value) {
            DisposableHelper.dispose(other);
            Disposable a = getAndSet(DisposableHelper.DISPOSED);
            if (a != DisposableHelper.DISPOSED) {
                downstream.onSuccess(value);
            }
        }

        @Override
        public void onError(Throwable e) {
            DisposableHelper.dispose(other);
            Disposable a = get();
            if (a != DisposableHelper.DISPOSED) {
                a = getAndSet(DisposableHelper.DISPOSED);
                if (a != DisposableHelper.DISPOSED) {
                    downstream.onError(e);
                    return;
                }
            }
            RxJavaPlugins.onError(e);
        }

        void otherError(Throwable e) {
            Disposable a = get();
            if (a != DisposableHelper.DISPOSED) {
                a = getAndSet(DisposableHelper.DISPOSED);
                if (a != DisposableHelper.DISPOSED) {
                    if (a != null) {
                        a.dispose();
                    }
                    downstream.onError(e);
                    return;
                }
            }
            RxJavaPlugins.onError(e);
        }

        void otherComplete() {
            if (downstreamDispose != null) {
                downstreamDispose.dispose();
            } else {
                DisposableHelper.dispose(this);
            }
        }

        final class TakeUntilOtherSingle extends AtomicReference<Disposable> implements SingleObserver<U> {

            @Override
            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            @Override
            public void onSuccess(U u) {
                otherComplete();
            }

            @Override
            public void onError(Throwable e) {
                otherError(e);
            }

        }

    }

}

