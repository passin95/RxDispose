package me.passin.rxdispose.sample.view.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import me.passin.rxdispose.sample.R;
import me.passin.rxdispose.sample.view.custom.CustomEventActivity;
import me.passin.rxdispose.sample.view.error.ErrorSampleActivity;
import me.passin.rxdispose.sample.view.sample.SampleActivity;

/**
 * <pre>
 * @author : passin
 * @Date: 2019/3/15 16:43
 * </pre>
 */
public class MainActivity extends AppCompatActivity {

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

    public void jumpToErrorSampleActivity(View view) {
        ErrorSampleActivity.startActivity(this);
    }
}
