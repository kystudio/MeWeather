package com.kystudio.meweather;

import android.app.Application;

import com.thinkland.sdk.android.JuheSDKInitializer;

/**
 * Created by 20236320 on 2016/11/22.
 */

public class MeWeatherApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化聚合SDK
        JuheSDKInitializer.initialize(getApplicationContext());
    }
}
