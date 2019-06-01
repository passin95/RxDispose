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

import io.reactivex.Maybe;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import org.junit.Before;
import org.junit.Test;

public class UntilEventTransformerMaybeTest {

    PublishSubject<String> subject;
    Maybe<String> maybe;
    BehaviorSubject<String> lifecycle;

    @Before
    public void setup() {
        subject = PublishSubject.create();
        maybe = subject.firstElement();
        lifecycle = BehaviorSubject.create();
    }

    @Test
    public void noEvents() {
        TestObserver<String> testObserver = maybe
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop"))
                .test();

        subject.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void oneWrongEvent() {
        TestObserver<String> testObserver = maybe
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop"))
                .test();

        lifecycle.onNext("keep going");
        subject.onNext("1");
        testObserver.assertValue("1");
        testObserver.assertComplete();
    }

    @Test
    public void twoEvents() {
        TestObserver<String> testObserver = maybe
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop"))
                .test();

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");

        subject.onNext("1");
        testObserver.assertNoValues();
        assert testObserver.isCancelled();
    }

    @Test
    public void bindTrueEvent1() {
        TestObserver<String> testObserver = maybe
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop", "event"))
                .test();

        lifecycle.onNext("keep going");
        lifecycle.onNext("stop");

        subject.onNext("1");
        testObserver.assertNoValues();
        assert testObserver.isCancelled();
    }

    @Test
    public void bindTrueEvent2() {
        TestObserver<String> testObserver = maybe
                .compose(RxDispose.<String, String>bindUntilEvent(lifecycle, "stop", "event"))
                .test();

        lifecycle.onNext("keep going");
        lifecycle.onNext("event");

        subject.onNext("1");
        testObserver.assertNoValues();
        assert testObserver.isCancelled();
    }
}