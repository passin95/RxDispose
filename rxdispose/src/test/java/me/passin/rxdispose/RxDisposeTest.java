package me.passin.rxdispose;

import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class RxDisposeTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().hide();
    }

    @Test
    public void testBindLifecycle() {
        BehaviorSubject<Object> lifecycle = BehaviorSubject.create();
        TestObserver<Object> testObserver = observable.compose(RxDispose.bind(lifecycle)).test();
        assert !testObserver.isCancelled();
        lifecycle.onNext(new Object());
        assert testObserver.isCancelled();
    }

    @Test
    public void testBindLifecycleOtherObject() {
        BehaviorSubject<String> lifecycle = BehaviorSubject.create();
        TestObserver<Object> testObserver = observable.compose(RxDispose.bind(lifecycle)).test();
        assert !testObserver.isCancelled();
        lifecycle.onNext("");
        assert testObserver.isCancelled();
    }

    @Test(expected = NullPointerException.class)
    public void testBindThrowsOnNullLifecycle() {
        RxDispose.bind((Observable) null);
    }

    @Test(expected = NullPointerException.class)
    public void testBindUntilThrowsOnNullLifecycle() {
        RxDispose.bindUntilEvent(null, new Object());
    }

}
