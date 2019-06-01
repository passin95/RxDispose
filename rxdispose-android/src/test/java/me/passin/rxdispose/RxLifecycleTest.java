/**
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import android.app.Activity;
import android.view.View;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import java.util.concurrent.CopyOnWriteArrayList;
import me.passin.rxdispose.android.ActivityEvent;
import me.passin.rxdispose.android.FragmentEvent;
import me.passin.rxdispose.android.RxDisposeAndroid;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class RxLifecycleTest {

    private Observable<Object> observable;

    @Before
    public void setup() {
        observable = PublishSubject.create().hide();
    }

    @Test
    public void testBindActivityLifecycle() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(ActivityEvent.CREATE);
        TestObserver<Object> createObserver = observable.compose(RxDisposeAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.START);
        assert !createObserver.isCancelled();
        TestObserver<Object> startObserver = observable.compose(RxDisposeAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.RESUME);
        assert !createObserver.isCancelled();
        assert !startObserver.isCancelled();
        TestObserver<Object> resumeObserver = observable.compose(RxDisposeAndroid.bindActivity(lifecycle)).test();

        lifecycle.onNext(ActivityEvent.PAUSE);
        assert !createObserver.isCancelled();
        assert !startObserver.isCancelled();
        assert resumeObserver.isCancelled();

        TestObserver<Object> pauseObserver = observable.compose(RxDisposeAndroid.bindActivity(lifecycle)).test();
        lifecycle.onNext(ActivityEvent.STOP);
        assert !createObserver.isCancelled();
        assert startObserver.isCancelled();
        assert pauseObserver.isCancelled();

        TestObserver<Object> stopObserver = observable.compose(RxDisposeAndroid.bindActivity(lifecycle)).test();
        lifecycle.onNext(ActivityEvent.DESTROY);
        assert createObserver.isCancelled();
        assert stopObserver.isCancelled();

        TestObserver<Object> destroyObserver = observable.compose(RxDisposeAndroid.bindActivity(lifecycle)).test();
        assert destroyObserver.isCancelled();
    }

    @Test
    public void testBindUntilActivityEvent() {
        BehaviorSubject<ActivityEvent> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
                observable.compose(RxDispose.bindUntilEvent(lifecycle, ActivityEvent.STOP)).test();

        lifecycle.onNext(ActivityEvent.CREATE);
        assert !testObserver.isCancelled();
        lifecycle.onNext(ActivityEvent.START);
        assert !testObserver.isCancelled();
        lifecycle.onNext(ActivityEvent.RESUME);
        assert !testObserver.isCancelled();
        lifecycle.onNext(ActivityEvent.PAUSE);
        assert !testObserver.isCancelled();
        lifecycle.onNext(ActivityEvent.STOP);
        assert testObserver.isCancelled();
        lifecycle.onNext(ActivityEvent.DESTROY);
        assert testObserver.isCancelled();
    }


    @Test
    public void testBindFragmentLifecycle() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        lifecycle.onNext(FragmentEvent.ATTACH);
        TestObserver<Object> attachObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.CREATE);
        assert !attachObserver.isCancelled();
        TestObserver<Object> createObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assert !attachObserver.isCancelled();
        assert !createObserver.isCancelled();
        TestObserver<Object> createViewObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.START);
        assert !attachObserver.isCancelled();
        assert !createObserver.isCancelled();
        assert !createViewObserver.isCancelled();
        TestObserver<Object> startObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.RESUME);
        assert !attachObserver.isCancelled();
        assert !createObserver.isCancelled();
        assert !createViewObserver.isCancelled();
        assert !startObserver.isCancelled();
        TestObserver<Object> resumeObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.PAUSE);
        assert !attachObserver.isCancelled();
        assert !createObserver.isCancelled();
        assert !createViewObserver.isCancelled();
        assert !startObserver.isCancelled();
        assert resumeObserver.isCancelled();
        TestObserver<Object> pauseObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.STOP);
        assert !attachObserver.isCancelled();
        assert !createObserver.isCancelled();
        assert !createViewObserver.isCancelled();
        assert startObserver.isCancelled();
        assert pauseObserver.isCancelled();
        TestObserver<Object> stopObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.DESTROY_VIEW);
        assert !attachObserver.isCancelled();
        assert !createObserver.isCancelled();
        assert createViewObserver.isCancelled();
        assert stopObserver.isCancelled();
        TestObserver<Object> destroyViewObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.DESTROY);
        assert !attachObserver.isCancelled();
        assert createObserver.isCancelled();
        assert destroyViewObserver.isCancelled();
        TestObserver<Object> destroyObserver = observable.compose(RxDisposeAndroid.bindFragment(lifecycle)).test();

        lifecycle.onNext(FragmentEvent.DETACH);
        assert attachObserver.isCancelled();
        assert destroyObserver.isCancelled();
    }

    @Test
    public void testBindUntilFragmentEvent() {
        BehaviorSubject<FragmentEvent> lifecycle = BehaviorSubject.create();

        TestObserver<Object> testObserver =
                observable.compose(RxDispose.bindUntilEvent(lifecycle, FragmentEvent.STOP)).test();

        lifecycle.onNext(FragmentEvent.ATTACH);
        assert !testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.CREATE);
        assert !testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.CREATE_VIEW);
        assert !testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.START);
        assert !testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.RESUME);
        assert !testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.PAUSE);
        assert !testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.STOP);
        assert testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.DESTROY);
        assert testObserver.isCancelled();
        lifecycle.onNext(FragmentEvent.DETACH);
        assert testObserver.isCancelled();
    }


    @Test
    public void testBindView() {
        Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        View view = new View(activity);
        CopyOnWriteArrayList<View.OnAttachStateChangeListener> listeners = TestUtil.getAttachStateChangeListeners(view);

        // Do the attach notification
        if (listeners != null) {
            for (View.OnAttachStateChangeListener listener : listeners) {
                listener.onViewAttachedToWindow(view);
            }
        }

        // Subscribe
        TestObserver<Object> viewAttachObserver = observable.compose(RxDisposeAndroid.bindView(view)).test();
        assert !viewAttachObserver.isCancelled();
        listeners = TestUtil.getAttachStateChangeListeners(view);
        assertNotNull(listeners);
        assertFalse(listeners.isEmpty());

        // Now detach
        for (View.OnAttachStateChangeListener listener : listeners) {
            listener.onViewDetachedFromWindow(view);
        }
        assert viewAttachObserver.isCancelled();
    }

    @Test(expected = NullPointerException.class)
    public void testBindFragmentThrowsOnNull() {
        //noinspection ResourceType
        RxDisposeAndroid.bindFragment(null);
    }

    @Test(expected = NullPointerException.class)
    public void testBindActivityThrowsOnNull() {
        //noinspection ResourceType
        RxDisposeAndroid.bindActivity(null);
    }

    @Test(expected = NullPointerException.class)
    public void testBindViewThrowsOnNullView() {
        //noinspection ResourceType
        RxDisposeAndroid.bindView(null);
    }
}
