package me.passin.rxdispose.sample.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import me.passin.rxdispose.sample.R;
import me.passin.rxdispose.sample.view.custom.CustomEventActivity;
import me.passin.rxdispose.sample.view.sample.SampleActivity;
import me.passin.rxdispose.sample.way.RxActivity;

/**
 * @author : passin
 * @date: 2019/3/15 16:43
 */
public class MainActivity extends RxActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jumpToSampleActivity(View view) {
        SampleActivity.startActivity(this);
    }

    public void jumpToCustomActivity(View view) {
        CustomEventActivity.startActivity(this);
    }
}
