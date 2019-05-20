# RxDispose

该库基于 [RxLifecycle](https://github.com/trello/RxLifecycle) 改造得来，在该库的基础上新增以下特性：
1. 支持同时绑定和自定义多种生命周期事件，能够在任何时间和地点去控制取消订阅的时机。
2. 能达到真正取消订阅的效果，不再是模拟取消订阅，不会在取消订阅的同时回调 onCompleted 或者 onError(CancellationException)。
3. 面向接口编程，不需要继承任何基类，只需现实相应的接口，发送相应的生命周期事件即可。

## 使用方式

更多使用方式请参考 rxlifecycle-sample（使用前请必看一下 Demo，建议参考 Demo的使用方式使用）。

```java
myObservable
    .compose(RxDisposeUtils.bindToLifecycle(lifecycleable)
    .subscribe();
```

或者想绑定至某个特定的 Event，所有 Event 的生效区间为 lifecycle 对象的生命周期。
支持多种“类型”的 Event 以及自定义 Event。

```java
myObservable
    .compose(RxDisposeUtils.bindUntilEvent(lifecycleable, ActivityEvent.DESTROY, EXAMPLE_EVENT))
    .subscribe();
```

自定义 Event 的触发方式：

```java
provideEventProvider().sendCostomEvent(EXAMPLE_EVENT);
```

绝大多数情况下，请把 `compose(RxDisposeUtils.bindUntilEvent(lifecycleable, ActivityEvent.DESTROY, EXAMPLE_EVENT))` 放置在 `subscribe()` 前一行，避免出现异步，导致取消订阅不及时。


## 安装

```gradle
implementation 'me.passin:rxdispose:0.2.0'

// 如果应用在 Android 上
implementation 'me.passin:rxdispose-android:0.2.0'
```

## License

    Copyright (C) 2019 Passin

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
