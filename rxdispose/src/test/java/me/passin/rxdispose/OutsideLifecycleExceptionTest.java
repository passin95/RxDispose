package me.passin.rxdispose;

import io.reactivex.functions.Function;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;

public class OutsideLifecycleExceptionTest {

    @Test
    public void eventOutOfLifecycle() {
        PublishSubject<String> stream = PublishSubject.create();
        BehaviorSubject<String> lifecycle = BehaviorSubject.create();

        TestObserver<String> testObserver = stream
                .compose(RxDispose.<String, String>bind(lifecycle, CORRESPONDING_EVENTS))
                .test();

        lifecycle.onNext("destroy");
        stream.onNext("1");

        testObserver.assertNoValues();
        assert testObserver.isCancelled();
    }

    private static final Function<String, String> CORRESPONDING_EVENTS = new Function<String, String>() {
        @Override
        public String apply(String s) throws Exception {
            if (s.equals("destroy")) {
                throw new OutsideLifecycleException("");
            }

            throw new IllegalArgumentException("Cannot handle: " + s);
        }
    };
}
