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

package me.passin.rxdispose.android;

import me.passin.rxdispose.Lifecycleable;

/**
 * Lifecycle events that can be emitted by Fragments.
 */
public interface FragmentLifecycle extends Lifecycleable {

    ICostomEventProvider provideEventProvider();

    String GROUP = FragmentLifecycle.class.getCanonicalName();

    String ATTACH = GROUP + "ATTACH";
    String CREATE = GROUP + "CREATE";
    String CREATE_VIEW = GROUP + "CREATE_VIEW";
    String START = GROUP + "START";
    String RESUME = GROUP + "RESUME";
    String PAUSE = GROUP + "PAUSE";
    String STOP = GROUP + "STOP";
    String DESTROY_VIEW = GROUP + "DESTROY_VIEW";
    String DESTROY = GROUP + "DESTROY";
    String DETACH = GROUP + "DETACH";
}
