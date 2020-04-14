package me.passin.rxdispose.android;

import io.reactivex.Observable;
import io.reactivex.subjects.BehaviorSubject;
import me.passin.rxdispose.EventProvider;

/**
 * @author: passin
 * @date: 2019/3/27 17:25
 * @desc:
 */
public class FragmentEventProvider implements EventProvider<FragmentEvent, Object> {

    private final BehaviorSubject<Object> mEventSubject = BehaviorSubject.create();

    private Object lastLifecycleEvent;

    public static FragmentEventProvider create() {
        return new FragmentEventProvider();
    }

    public synchronized void sendCostomEvent(Object event) {
        mEventSubject.onNext(event);
        if (lastLifecycleEvent != null) {
            mEventSubject.onNext(lastLifecycleEvent);
        }
    }

    @Override
    public synchronized void sendLifecycleEvent(FragmentEvent event) {
        mEventSubject.onNext(event);
        lastLifecycleEvent = event;
    }

    @Override
    public Observable<Object> getObservable() {
        return mEventSubject;
    }

}

