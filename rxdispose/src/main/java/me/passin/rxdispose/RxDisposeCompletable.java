package me.passin.rxdispose;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.CompletableSource;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author: zbb 33775
 * @date: 2019/5/19 23:35
 * @desc:
 */
public class RxDisposeCompletable extends Completable {

    final Completable source;
    final CompletableSource other;

    RxDisposeCompletable(Completable source, CompletableSource other) {
        this.source = source;
        this.other = other;
    }

    @Override
    protected void subscribeActual(CompletableObserver observer) {
        TakeUntilMainObserver parent = new TakeUntilMainObserver(observer);
        observer.onSubscribe(parent);

        other.subscribe(parent.other);
        source.subscribe(parent);
    }

    static final class TakeUntilMainObserver extends AtomicReference<Disposable>
            implements CompletableObserver, Disposable {

        final CompletableObserver downstream;
        final TakeUntilMainObserver.OtherObserver other;
        final AtomicBoolean once;
        Disposable downstreamDispose;

        TakeUntilMainObserver(CompletableObserver downstream) {
            this.downstream = downstream;
            if (downstream instanceof Disposable) {
                downstreamDispose = (Disposable) downstream;
            }
            this.other = new TakeUntilMainObserver.OtherObserver();
            this.once = new AtomicBoolean();
        }

        @Override
        public void dispose() {
            if (once.compareAndSet(false, true)) {
                DisposableHelper.dispose(this);
                DisposableHelper.dispose(other);
            }
        }

        @Override
        public boolean isDisposed() {
            return once.get();
        }

        @Override
        public void onSubscribe(Disposable d) {
            DisposableHelper.setOnce(this, d);
        }

        @Override
        public void onComplete() {
            if (once.compareAndSet(false, true)) {
                DisposableHelper.dispose(other);
                downstream.onComplete();
            }
        }

        @Override
        public void onError(Throwable e) {
            if (once.compareAndSet(false, true)) {
                DisposableHelper.dispose(other);
                downstream.onError(e);
            } else {
                RxJavaPlugins.onError(e);
            }
        }

        void otherError(Throwable e) {
            if (once.compareAndSet(false, true)) {
                DisposableHelper.dispose(this);
                downstream.onError(e);
            } else {
                RxJavaPlugins.onError(e);
            }
        }

        void otherComplete() {
            if (downstreamDispose != null) {
                downstreamDispose.dispose();
            } else {
                dispose();
            }
        }

        final class OtherObserver extends AtomicReference<Disposable> implements CompletableObserver {

            @Override
            public void onSubscribe(Disposable d) {
                DisposableHelper.setOnce(this, d);
            }

            @Override
            public void onComplete() {
                otherComplete();
            }

            @Override
            public void onError(Throwable e) {
                otherError(e);
            }
        }
    }
}

