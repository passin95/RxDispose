# RxDispose

该库基于 [RxLifecycle](https://github.com/trello/RxLifecycle) 改造而来，解决了 RxLifecycle 作者所陈述的 [绝大部分问题](https://www.jianshu.com/p/6627e97eba8d)，并新增了以下特性：

1. 通过重写所有观察者和被观察者，能达到真正意义上取消订阅的效果，不再是模拟取消订阅，不会在取消订阅的同时回调 onCompleted 或者 onError(CancellationException)，能获取到正确的 Disposable 状态。
2. 功能更强大，支持同时绑定生命周期事件和自定义其他事件，能够在任何时间和地点去控制取消订阅的时机（本质上是基于观察者模式的应用）。
3. 不需要继承任何基类，只需实现相应的接口，发送相应的事件即可。
4. 搭配单元测试，保证稳定性。

## 使用方式

更多使用方式请参考 rxlifecycle-sample（使用前请必看一下 Demo，建议参考 Demo 的使用方式使用）。

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

## 注意事项

- 请把 `compose(RxDisposeUtils.bindUntilEvent(lifecycleable, ActivityEvent.DESTROY, EXAMPLE_EVENT))` 放置在 `subscribe()` 前一行（流的末尾），否则会有 BUG，且无法获取正确的 Disposable 状态。
- 在明确取消订阅时机的情况下，优先使用 **bindUntilEvent** 而非 **bindToLifecycle**，这样不仅性能更高，而且对于不同的代码维护者来说“所见即所得”。

## 安装

```gradle
implementation 'me.passin:rxdispose:2.1.0'

// 应用在 Android 上
implementation 'me.passin:rxdispose-android:2.1.0'
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
