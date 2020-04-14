package me.passin.rxdispose;

import io.reactivex.Observable;

/**
 * @author: passin
 * @date: 2019/3/27 17:38
 * @desc:
 */
public interface EventProvider<T, R> {

    void sendLifecycleEvent(T t);

    Observable<R> getObservable();

}
