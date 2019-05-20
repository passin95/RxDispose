package me.passin.rxdispose.android;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author: passin
 * @date: 2019/3/27 17:25
 * @desc:
 */
public class CostomEventProvider implements ICostomEventProvider {

    private final BehaviorSubject<Object> mEventSubject = BehaviorSubject.create();

    private Object lastLifecycleEvent;

    public static CostomEventProvider create() {
        return new CostomEventProvider();
    }

    @Override
    public synchronized void sendCostomEvent(Object event) {
        mEventSubject.onNext(event);
        if (lastLifecycleEvent != null) {
            mEventSubject.onNext(lastLifecycleEvent);
        }
    }

    @Override
    public synchronized void sendLifecycleEvent(Object event) {
        mEventSubject.onNext(event);
        lastLifecycleEvent = event;
    }

    @Override
    public Observable getObservable() {
        return mEventSubject;
    }

}
