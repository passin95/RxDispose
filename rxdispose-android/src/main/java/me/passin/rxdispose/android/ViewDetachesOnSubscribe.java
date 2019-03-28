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

package me.passin.rxdispose.android;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

import android.view.View;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;

final class ViewDetachesOnSubscribe implements ObservableOnSubscribe<Object> {

    static final String VIEW_DETACHE = ViewDetachesOnSubscribe.class.getCanonicalName() + "VIEW_DETACHE";

    final View view;

    public ViewDetachesOnSubscribe(View view) {
        this.view = view;
    }

    @Override
    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
        verifyMainThread();
        EmitterListener listener = new EmitterListener(emitter);
        emitter.setDisposable(listener);
        view.addOnAttachStateChangeListener(listener);
    }

    class EmitterListener extends MainThreadDisposable implements View.OnAttachStateChangeListener {

        final ObservableEmitter<Object> emitter;

        public EmitterListener(ObservableEmitter<Object> emitter) {
            this.emitter = emitter;
        }

        @Override
        public void onViewAttachedToWindow(View view) {
            // Do nothing
        }

        @Override
        public void onViewDetachedFromWindow(View view) {
            emitter.onNext(VIEW_DETACHE);
        }

        @Override
        protected void onDispose() {
            view.removeOnAttachStateChangeListener(this);
        }
    }

}