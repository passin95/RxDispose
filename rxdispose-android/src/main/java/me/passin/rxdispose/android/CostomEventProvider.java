package me.passin.rxdispose.android;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;

/**
 * @author: passin
 * @date: 2019/3/27 17:25
 * @desc:
 */
public class CostomEventProvider implements ICostomEventProvider {

    private final BehaviorSubject<String> mEventSubject = BehaviorSubject.create();

    private String lastLifecycleEvent;

    public static CostomEventProvider create() {
        return new CostomEventProvider();
    }

    public synchronized void sendCostomEvent(String event) {
        mEventSubject.onNext(event);
        if (lastLifecycleEvent != null) {
            mEventSubject.onNext(lastLifecycleEvent);
        }
    }

    public synchronized void sendLifecycleEvent(String event) {
        mEventSubject.onNext(event);
        lastLifecycleEvent = event;
    }

    @Override
    public Observable getObservable() {
        return mEventSubject;
    }

}
