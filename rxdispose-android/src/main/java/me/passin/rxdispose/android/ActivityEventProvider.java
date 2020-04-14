package me.passin.rxdispose.android;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import me.passin.rxdispose.EventProvider;

/**
 * @author: passin
 * @date: 2019/3/27 17:44
 * @desc:
 */
public class ActivityEventProvider implements EventProvider<ActivityEvent, Object> {

    private final BehaviorSubject<Object> mEventSubject = BehaviorSubject.create();

    private Object lastLifecycleEvent;

    public static ActivityEventProvider create() {
        return new ActivityEventProvider();
    }

    public synchronized void sendCostomEvent(Object event) {
        mEventSubject.onNext(event);
        if (lastLifecycleEvent != null) {
            mEventSubject.onNext(lastLifecycleEvent);
        }
    }

    @Override
    public synchronized void sendLifecycleEvent(ActivityEvent event) {
        mEventSubject.onNext(event);
        lastLifecycleEvent = event;
    }

    @Override
    public Observable<Object> getObservable() {
        return mEventSubject;
    }

}
