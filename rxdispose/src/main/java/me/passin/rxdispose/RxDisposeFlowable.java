package me.passin.rxdispose;

import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.subscriptions.SubscriptionHelper;
import io.reactivex.internal.util.AtomicThrowable;
import io.reactivex.internal.util.HalfSerializer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @author: zbb 33775
 * @date: 2019/5/19 23:04
 * @desc:
 */
@SuppressWarnings("unchecked")
public class RxDisposeFlowable<T, U> extends Flowable<T> {

    final Flowable<T> source;
    final Publisher<? extends U> other;

    RxDisposeFlowable(Flowable<T> source, Publisher<? extends U> other) {
        this.source = source;
        this.other = other;
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> child) {
        TakeUntilMainSubscriber<T> parent = new TakeUntilMainSubscriber<>(child);
        child.onSubscribe(parent);

        other.subscribe(parent.other);

        source.subscribe(parent);
    }

    static final class TakeUntilMainSubscriber<T> extends AtomicInteger implements FlowableSubscriber<T>, Subscription {

        final Subscriber<? super T> downstream;
        final AtomicLong requested;
        final AtomicReference<Subscription> upstream;
        final AtomicThrowable error;
        final TakeUntilMainSubscriber.OtherSubscriber other;
        Disposable downstreamDispose;

        TakeUntilMainSubscriber(Subscriber<? super T> downstream) {
            this.downstream = downstream;
            if (downstream instanceof Disposable) {
                downstreamDispose = (Disposable) downstream;
            }
            this.requested = new AtomicLong();
            this.upstream = new AtomicReference<>();
            this.other = new TakeUntilMainSubscriber.OtherSubscriber();
            this.error = new AtomicThrowable();
        }

        @Override
        public void onSubscribe(Subscription s) {
            SubscriptionHelper.deferredSetOnce(this.upstream, requested, s);
        }

        @Override
        public void onNext(T t) {
            HalfSerializer.onNext(downstream, t, this, error);
        }

        @Override
        public void onError(Throwable t) {
            SubscriptionHelper.cancel(other);
            HalfSerializer.onError(downstream, t, this, error);
        }

        @Override
        public void onComplete() {
            SubscriptionHelper.cancel(other);
            HalfSerializer.onComplete(downstream, this, error);
        }

        @Override
        public void request(long n) {
            SubscriptionHelper.deferredRequest(upstream, requested, n);
        }

        @Override
        public void cancel() {
            SubscriptionHelper.cancel(upstream);
            SubscriptionHelper.cancel(other);
        }

        void otherError(Throwable t) {
            SubscriptionHelper.cancel(upstream);
            HalfSerializer.onError(downstream, t, TakeUntilMainSubscriber.this, error);
        }

        void otherComplete() {
            if (downstreamDispose != null) {
                downstreamDispose.dispose();
            } else {
                cancel();
            }
        }

        final class OtherSubscriber extends AtomicReference<Subscription> implements FlowableSubscriber<Object> {

            @Override
            public void onSubscribe(Subscription s) {
                SubscriptionHelper.setOnce(this, s, Long.MAX_VALUE);
            }

            @Override
            public void onNext(Object t) {
                otherComplete();
            }

            @Override
            public void onError(Throwable t) {
                otherError(t);
            }

            @Override
            public void onComplete() {
                otherComplete();
            }
        }
    }
}
