package me.passin.rxdispose.android;

import android.support.annotation.NonNull;
import me.passin.rxdispose.Lifecycleable;

public interface FragmentLifecycleable extends Lifecycleable<FragmentEvent, Object> {

    @Override
    @NonNull
    FragmentEventProvider getEventProvider();

}

