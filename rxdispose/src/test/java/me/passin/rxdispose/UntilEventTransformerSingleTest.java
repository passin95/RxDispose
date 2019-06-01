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

import io.reactivex.Single;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subjects.BehaviorSubject;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;

public class UntilEventTransformerSingleTest {

    BehaviorSubject<String> lifecycle;
    TestScheduler testScheduler;

    @Before
    public void setup() {
        lifecycle = BehaviorSubject.create();
        testScheduler = new TestScheduler();
    }

    @Test
    public void noEvents() {
        TestObserver<String> testObserver = Single.just("1")
                .delay(1, TimeUnit.MILLISECONDS, testScheduler)
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop"))
                .test();

        testObserver.assertNoValues();

        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneWrongEvent() {
        TestObserver<String> testObserver = Single.just("1")
                .delay(1, TimeUnit.MILLISECONDS, testScheduler)
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop"))
                .test();

        testObserver.assertNoValues();

        lifecycle.onNext("keep going");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void twoEvents() {
        TestObserver<String> testObserver = Single.just("1")
                .delay(1, TimeUnit.MILLISECONDS, testScheduler)
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop"))
                .test();

        lifecycle.onNext("keep going");
        testObserver.assertNoErrors();

        lifecycle.onNext("stop");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testObserver.assertNoValues();
        assert testObserver.isCancelled();
    }

    @Test
    public void bindTrueEvent1() {
        TestObserver<String> testObserver = Single.just("1")
                .delay(1, TimeUnit.MILLISECONDS, testScheduler)
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop", "event"))
                .test();

        lifecycle.onNext("keep going");
        testObserver.assertNoErrors();

        lifecycle.onNext("stop");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testObserver.assertNoValues();
        assert testObserver.isCancelled();
    }

    @Test
    public void bindTrueEvent2() {
        TestObserver<String> testObserver = Single.just("1")
                .delay(1, TimeUnit.MILLISECONDS, testScheduler)
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop", "event"))
                .test();

        lifecycle.onNext("keep going");
        testObserver.assertNoErrors();

        lifecycle.onNext("event");
        testScheduler.advanceTimeBy(1, TimeUnit.MILLISECONDS);

        testObserver.assertNoValues();
        assert testObserver.isCancelled();
    }
}