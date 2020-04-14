package me.passin.rxdispose;

import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.internal.disposables.DisposableHelper;
import io.reactivex.plugins.RxJavaPlugins;

/**
 * @author: zbb
 * @date: 2020/4/13 21:42
 * @desc:
 */
@SuppressWarnings("unchecked")
public final class ObservableToSingle<T> extends Single<T> {

    private final ObservableSource<? extends T> source;

    ObservableToSingle(ObservableSource<? extends T> source) {
        this.source = source;
    }

    @Override
    public void subscribeActual(SingleObserver<? super T> t) {
        source.subscribe(new ObservableToSingle.SingleElementObserver(t));
    }

    static final class SingleElementObserver<T> implements Observer<T>, Disposable {

        final SingleObserver<? super T> downstream;

        Disposable upstream;

        boolean done;

        SingleElementObserver(SingleObserver<? super T> actual) {
            this.downstream = actual;
        }

        @Override
        public void onSubscribe(Disposable d) {
            if (DisposableHelper.validate(this.upstream, d)) {
                this.upstream = d;
                downstream.onSubscribe(this);
            }
        }

        @Override
        public void dispose() {
            upstream.dispose();
        }

        @Override
        public boolean isDisposed() {
            return upstream.isDisposed();
        }

        @Override
        public void onNext(T t) {
            if (done) {
                return;
            }
            downstream.onSuccess(t);
        }

        @Override
        public void onError(Throwable t) {
            if (done) {
                RxJavaPlugins.onError(t);
                return;
            }
            done = true;
            downstream.onError(t);
        }

        @Override
        public void onComplete() {
            if (done) {
                return;
            }
            done = true;
            downstream.onSuccess(null);
        }

    }

}