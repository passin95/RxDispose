/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.passin.rxdispose;


import static me.passin.rxdispose.utils.Preconditions.checkNotNull;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;
import io.reactivex.internal.operators.single.SingleToFlowable;
import io.reactivex.plugins.RxJavaPlugins;
import me.passin.rxdispose.utils.Preconditions;
import org.reactivestreams.Publisher;

/**
 * Transformer that continues a subscription until a second Observable emits an event.
 */
public final class LifecycleTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>, SingleTransformer<T, T>, MaybeTransformer<T, T>,
        CompletableTransformer {

    final Observable<?> observable;

    LifecycleTransformer(Observable<?> observable) {
        checkNotNull(observable, "observable == null");
        this.observable = observable;
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        Preconditions.checkNotNull(upstream, "upstream is null");
        return new RxDisposeObservable<>(upstream, observable);
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        Preconditions.checkNotNull(upstream, "upstream is null");
        return new RxDisposeFlowable<>(upstream, observable.toFlowable(BackpressureStrategy.LATEST));
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        Preconditions.checkNotNull(upstream, "upstream is null");
        return RxJavaPlugins.onAssembly(new RxDisposeSingle<>(upstream, new SingleToFlowable<>(observable.firstOrError())));
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        Preconditions.checkNotNull(upstream, "upstream is null");
        return new RxDisposeMaybe<>(upstream, observable.firstElement());
    }

    @Override
    public CompletableSource apply(Completable upstream) {
        Preconditions.checkNotNull(upstream, "upstream is null");
        return new RxDisposeCompletable(upstream, observable.flatMapCompletable(Functions.CANCEL_COMPLETABLE));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LifecycleTransformer<?> that = (LifecycleTransformer<?>) o;

        return observable.equals(that.observable);
    }

    @Override
    public int hashCode() {
        return observable.hashCode();
    }

    @Override
    public String toString() {
        return "LifecycleTransformer{" +
                "observable=" + observable +
                '}';
    }
}
