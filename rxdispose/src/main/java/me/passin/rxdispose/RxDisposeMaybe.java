package me.passin.rxdispose;

import io.reactivex.Maybe;
import io.reactivex.MaybeObserver;
import io.reactivex.MaybeSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: zbb 33775
 * @date: 2019/5/19 23:24
 * @desc:
 */
public class RxDisposeMaybe<T, U> extends Maybe<T> {

    final MaybeSource<T> source;
    final MaybeSource<U> other;

    public RxDisposeMaybe(MaybeSource<T> source, MaybeSource<U> other) {
        this.source = source;
        this.other = other;
    }

    @Override
    protected void subscribeActual(MaybeObserver<? super T> observer) {
        TakeUntilMainMaybeObserver<T, U> parent =
                new TakeUntilMainMaybeObserver<>(
                        observer);
        observer.onSubscribe(parent);

        other.subscribe(parent.other);

        source.subscribe(parent);
    }

    static final class TakeUntilMainMaybeObserver<T, U>
            extends AtomicReference<Disposable> implements MaybeObserver<T>, Disposable {

        final MaybeObserver<? super T> downstream;

        final TakeUntilMainMaybeObserver.TakeUntilOtherMaybeObserver<U> other;

        TakeUntilMainMaybeObserver(MaybeObserver<? super T> downstream) {
            this.downstream = downstream;
            this.other = new TakeUntilMainMaybeObserver.TakeUntilOtherMaybeObserver<>(this);
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
            if (getAndSet(DisposableHelper.DISPOSED) != DisposableHelper.DISPOSED) {
                downstream.onSuccess(value);
            }
        }

        @Override
        public void onError(Throwable e) {
            DisposableHelper.dispose(other);
            if (getAndSet(DisposableHelper.DISPOSED) != DisposableHelper.DISPOSED) {
                downstream.onError(e);
            } else {
                RxJavaPlugins.onError(e);
            }
        }

        @Override
        public void onComplete() {
            DisposableHelper.dispose(other);
            if (getAndSet(DisposableHelper.DISPOSED) != DisposableHelper.DISPOSED) {
                downstream.onComplete();
            }
        }

        void otherError(Throwable e) {
            if (DisposableHelper.dispose(this)) {
                downstream.onError(e);
            } else {
                RxJavaPlugins.onError(e);
            }
        }

        void otherComplete() {
            DisposableHelper.dispose(this);
        }

        static final class TakeUntilOtherMaybeObserver<U>
                extends AtomicReference<Disposable> implements MaybeObserver<U> {

            final TakeUntilMainMaybeObserver<?, U> parent;

            TakeUntilOtherMaybeObserver(TakeUntilMainMaybeObserver<?, U> parent) {
                this.parent = parent;
            }

            @Override
            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            @Override
            public void onSuccess(Object value) {
                parent.otherComplete();
            }

            @Override
            public void onError(Throwable e) {
                parent.otherError(e);
            }

            @Override
            public void onComplete() {
                parent.otherComplete();
            }
        }
    }

}
