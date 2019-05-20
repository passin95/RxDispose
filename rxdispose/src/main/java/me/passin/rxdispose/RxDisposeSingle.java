package me.passin.rxdispose;

import io.reactivex.FlowableSubscriber;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscription;

/**
 * @author: zbb 33775
 * @date: 2019/5/19 23:15
 * @desc:
 */
public class RxDisposeSingle<T, U> extends Single<T> {

    final SingleSource<T> source;
    final Publisher<U> other;

    public RxDisposeSingle(SingleSource<T> source, Publisher<U> other) {
        this.source = source;
        this.other = other;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super T> observer) {
        TakeUntilMainObserver<T> parent = new TakeUntilMainObserver<>(observer);
        observer.onSubscribe(parent);

        other.subscribe(parent.other);

        source.subscribe(parent);
    }

    static final class TakeUntilMainObserver<T>
            extends AtomicReference<Disposable>
            implements SingleObserver<T>, Disposable {

        final SingleObserver<? super T> downstream;

        final TakeUntilOtherSubscriber other;

        TakeUntilMainObserver(SingleObserver<? super T> downstream) {
            this.downstream = downstream;
            this.other = new TakeUntilOtherSubscriber(this);
        }

        @Override
        public void dispose() {
            DisposableHelper.dispose(this);
            other.dispose();
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
            other.dispose();

            Disposable a = getAndSet(DisposableHelper.DISPOSED);
            if (a != DisposableHelper.DISPOSED) {
                downstream.onSuccess(value);
            }
        }

        @Override
        public void onError(Throwable e) {
            other.dispose();

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
            if (e instanceof CancellationException) {
                DisposableHelper.dispose(this);
                return;
            }
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
    }

    static final class TakeUntilOtherSubscriber extends AtomicReference<Subscription> implements
            FlowableSubscriber<Object> {

        final TakeUntilMainObserver<?> parent;

        TakeUntilOtherSubscriber(TakeUntilMainObserver<?> parent) {
            this.parent = parent;
        }

        @Override
        public void onSubscribe(Subscription s) {
            SubscriptionHelper.setOnce(this, s, Long.MAX_VALUE);
        }

        @Override
        public void onNext(Object t) {
            if (SubscriptionHelper.cancel(this)) {
                parent.otherError(new CancellationException());
            }
        }

        @Override
        public void onError(Throwable t) {
            parent.otherError(t);
        }

        @Override
        public void onComplete() {
            if (get() != SubscriptionHelper.CANCELLED) {
                lazySet(SubscriptionHelper.CANCELLED);
                parent.otherError(new CancellationException());
            }
        }

        public void dispose() {
            SubscriptionHelper.cancel(this);
        }
    }
}

