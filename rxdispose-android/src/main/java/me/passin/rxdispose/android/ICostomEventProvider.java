package me.passin.rxdispose.android;

import me.passin.rxdispose.EventProvider;

/**
 * @author: passin
 * @date: 2019/3/27 17:44
 * @desc:
 */
public interface ICostomEventProvider extends EventProvider<String> {

    void sendCostomEvent(String event);

}
