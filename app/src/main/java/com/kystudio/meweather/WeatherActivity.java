package com.kystudio.meweather;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by 20236320 on 2016/11/22.
 */

public class WeatherActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_weather);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull_refresh_scrollview);


        //給刷新頭設置顔色，最多可以設置4個
        swipeRefreshLayout.setColorSchemeResources(android.R.color.white,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        //對刷新的操作
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //不讓刷新頭顯示出來
                        swipeRefreshLayout.setRefreshing(false);
                        //刷新時添加的數據，其中0代表在頭部添加，如果不寫上0，就會添加在尾部
                        Log.d("swipeRefreshLayout", "正在刷新");
                    }
                }, 6000);
            }
        });
    }
}
