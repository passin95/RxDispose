# RxDispose

该库基于 [RxLifecycle](https://github.com/trello/RxLifecycle) 改造得来，支持同时绑定和自定义多种生命周期事件。
自定义事件的触发可以是一次点击、一个异常或者任何你想触发的地方。

## Usage

更多使用方式请参考 rxlifecycle-sample（使用前请必看一下 Demo）。

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

根据不同的需求，可以实现 `LifecycleProvider` 去提供不同的 Subject，但一般情况下建议使用 `PublishSubject`，
不同 Subject 的区别请自行查阅。

绝大多数情况下，建议把 `compose(RxDisposeUtils.bindUntilEvent(lifecycleable, ActivityEvent.DESTROY, EXAMPLE_EVENT))` 放置在 `subscribe()` 前一行，避免出现异步，导致取消订阅不及时。

## Unsubscription

RxDispose 实际上并没有取消订阅序列。 相反，它终止序列的方式为：

- `Observable`, `Flowable` and `Maybe` - emits `onCompleted()`
- `Single` and `Completable` - emits `onError(CancellationException)`

如果想真正取消订阅 `Subscription.unsubscribe()`，那么建议您手动调用 `unsubscribe()` 处理。

## Installation

```gradle
implementation 'com.passin.rxdispose:0.1.0'

// 如果应用在 Android 上
implementation 'com.passin.rxdispose-android:0.1.0'
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
