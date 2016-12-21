package com.kystudio.meweather;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kystudio.meweather.model.FutureHourWeather;
import com.kystudio.meweather.model.FutureDateWeather;
import com.kystudio.meweather.model.Weather;
import com.kystudio.meweather.service.WeatherService;
import com.thinkland.sdk.android.DataCallBack;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 20236320 on 2016/11/22.
 */

public class WeatherActivity extends AppCompatActivity {
    private Context mContext;
    private WeatherService mWeatherService;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RelativeLayout rl_city;
    private TextView tv_city,
            tv_release,
            tv_now_weather,
            tv_today_temp,
            tv_now_temp,
            tv_aqi,
            tv_quality;
    private TextView tv_next_three_hour,
            tv_next_six_hour,
            tv_next_nine_hour,
            tv_next_twelve_hour,
            tv_next_fifteen_hour;
    private TextView tv_next_three_temp,
            tv_next_six_temp,
            tv_next_nine_temp,
            tv_next_twelve_temp,
            tv_next_fifteen_temp;
    private TextView tv_tomorrow,
            tv_third,
            tv_fourth;
    private TextView tv_today_temp_max,
            tv_today_temp_min,
            tv_tomorrow_temp_max,
            tv_tomorrow_temp_min,
            tv_third_temp_max,
            tv_third_temp_min,
            tv_fourth_temp_max,
            tv_fourth_temp_min;
    private TextView tv_felt_air_temp,
            tv_humidity,
            tv_wind,
            tv_uv_index,
            tv_dressing_index;

    private ImageView iv_now_weather,
            iv_today_weather,
            iv_tomorrow,
            iv_third_weather,
            iv_fourth_weather;
    private ImageView iv_next_three,
            iv_next_six,
            iv_next_nine,
            iv_next_twelve,
            iv_next_fifteen;
    private Weather weather;

    final public static int REQUEST_CODE_ASK_READ_PHONE_STATE = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 动态权限
        if (Build.VERSION.SDK_INT >= 23) {
            int checkPhonePermission = ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE);
            if (checkPhonePermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CODE_ASK_READ_PHONE_STATE);
                return;
            }
        }

        setContentView(R.layout.activity_weather);

        mContext = this;

        init();
    }

    private void updateWeather(String cityName) {
        String url = "http://op.juhe.cn/onebox/weather/query";//请求接口地址
        try {
            String myCity = URLEncoder.encode(tv_city.getText().toString(), "UTF-8");
            Log.d("abc", myCity);
            Log.d("abc", URLDecoder.decode(myCity, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 请不要添加key参数.
         */
        Parameters params = new Parameters();
        //params.add("key", APPKEY);
        //params.add("cityname", myCity);
        params.add("cityname", cityName);
        params.add("dtype", "");
        /**
         * 请求的方法 参数: 第一个参数 当前请求的context;
         * 				  第二个参数 接口id;
         * 				  第三个参数 接口请求的url;
         * 				  第四个参数 接口请求的方式;
         * 				  第五个参数 接口请求的参数,键值对com.thinkland.sdk.android.Parameters类型;
         * 				  第六个参数请求的回调方法,com.thinkland.sdk.android.DataCallBack;
         *
         */
        JuheData.executeWithAPI(mContext, 73, url, JuheData.GET, params, new DataCallBack() {
            /**
             * 请求成功时调用的方法 statusCode为http状态码,responseString为请求返回数据.
             */
            @Override
            public void onSuccess(int statusCode, String responseString) {
                Toast.makeText(getApplicationContext(), "--->success", Toast.LENGTH_SHORT).show();

                //Log.d("abc", statusCode + "");
                //Log.d("abc", responseString);
                if (statusCode == 200) {
                    weather = mWeatherService.parseJson(responseString);
                    if (weather != null) {
                        setViews();
                    }
                }
            }

            /**
             * 请求完成时调用的方法,无论成功或者失败都会调用.
             */
            @Override
            public void onFinish() {
                //Toast.makeText(getApplicationContext(), "--->finish",Toast.LENGTH_SHORT).show();
            }

            /**
             * 请求失败时调用的方法,statusCode为http状态码,throwable为捕获到的异常
             * statusCode:30002 没有检测到当前网络.
             * 			  30003 没有进行初始化.
             * 			  0 未明异常,具体查看Throwable信息.
             * 			  其他异常请参照http状态码.
             */
            @Override
            public void onFailure(int statusCode,
                                  String responseString, Throwable throwable) {
                Toast.makeText(getApplicationContext(), "--->failure", Toast.LENGTH_SHORT).show();
                Log.d("abcd", statusCode + "");
                Log.d("abcd", responseString);
            }
        });
    }

    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mWeatherService = ((WeatherService.WeatherServiceBinder) service).getService();
            mWeatherService.test();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void initService() {
        Intent intent = new Intent(mContext, WeatherService.class);
        startService(intent);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**
         * 关闭当前页面正在进行中的请求.
         */
        JuheData.cancelRequests(mContext);
        unbindService(conn);
    }

    private void init() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.pull_refresh_scrollview);
        rl_city = (RelativeLayout) findViewById(R.id.rl_city);

        rl_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        findTextView();

        findImageView();

        initService();

        updateWeather(tv_city.getText().toString());

        //给刷新头设置颜色，最多可以设置4个
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.d("swipeRefreshLayout", "正在刷新");
                        updateWeather(tv_city.getText().toString());
                    }
                }, 3000);
            }
        });
    }

    private void findImageView() {
        iv_now_weather = (ImageView) findViewById(R.id.iv_now_weather);
        iv_today_weather = (ImageView) findViewById(R.id.iv_today_weather);
        iv_tomorrow = (ImageView) findViewById(R.id.iv_tomorrow);
        iv_third_weather = (ImageView) findViewById(R.id.iv_third_weather);
        iv_fourth_weather = (ImageView) findViewById(R.id.iv_fourth_weather);

        iv_next_three = (ImageView) findViewById(R.id.iv_next_three);
        iv_next_six = (ImageView) findViewById(R.id.iv_next_six);
        iv_next_nine = (ImageView) findViewById(R.id.iv_next_nine);
        iv_next_twelve = (ImageView) findViewById(R.id.iv_next_twelve);
        iv_next_fifteen = (ImageView) findViewById(R.id.iv_next_fifteen);
    }

    private void findTextView() {
        tv_city = (TextView) findViewById(R.id.tv_city);
        tv_city.setText("东莞");
        tv_release = (TextView) findViewById(R.id.tv_release);
        tv_now_weather = (TextView) findViewById(R.id.tv_now_weather);
        //tv_today_temp = (TextView) findViewById(R.id.tv_today_temp);
        tv_now_temp = (TextView) findViewById(R.id.tv_now_temp);
        tv_aqi = (TextView) findViewById(R.id.tv_aqi);
        tv_quality = (TextView) findViewById(R.id.tv_quality);

        tv_next_three_hour = (TextView) findViewById(R.id.tv_next_three_hour);
        tv_next_six_hour = (TextView) findViewById(R.id.tv_next_six_hour);
        tv_next_nine_hour = (TextView) findViewById(R.id.tv_next_nine_hour);
        tv_next_twelve_hour = (TextView) findViewById(R.id.tv_next_twelve_hour);
        tv_next_fifteen_hour = (TextView) findViewById(R.id.tv_next_fifteen_hour);

        tv_next_three_temp = (TextView) findViewById(R.id.tv_next_three_temp);
        tv_next_six_temp = (TextView) findViewById(R.id.tv_next_six_temp);
        tv_next_nine_temp = (TextView) findViewById(R.id.tv_next_nine_temp);
        tv_next_twelve_temp = (TextView) findViewById(R.id.tv_next_twelve_temp);
        tv_next_fifteen_temp = (TextView) findViewById(R.id.tv_next_fifteen_temp);

        tv_tomorrow = (TextView) findViewById(R.id.tv_tomorrow);
        tv_third = (TextView) findViewById(R.id.tv_third);
        tv_fourth = (TextView) findViewById(R.id.tv_fourth);

        tv_today_temp_max = (TextView) findViewById(R.id.tv_today_temp_max);
        tv_today_temp_min = (TextView) findViewById(R.id.tv_today_temp_min);
        tv_tomorrow_temp_max = (TextView) findViewById(R.id.tv_tomorrow_temp_max);
        tv_tomorrow_temp_min = (TextView) findViewById(R.id.tv_tomorrow_temp_min);
        tv_third_temp_max = (TextView) findViewById(R.id.tv_third_temp_max);
        tv_third_temp_min = (TextView) findViewById(R.id.tv_third_temp_min);
        tv_fourth_temp_max = (TextView) findViewById(R.id.tv_fourth_temp_max);
        tv_fourth_temp_min = (TextView) findViewById(R.id.tv_fourth_temp_min);

        //tv_felt_air_temp = (TextView) findViewById(R.id.tv_felt_air_temp);
        tv_humidity = (TextView) findViewById(R.id.tv_humidity);
        tv_wind = (TextView) findViewById(R.id.tv_wind);
        tv_uv_index = (TextView) findViewById(R.id.tv_uv_index);
        tv_dressing_index = (TextView) findViewById(R.id.tv_dressing_index);
    }

    private void setViews() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_release.setText(weather.getRelease() + " 发布");
                tv_now_weather.setText(weather.getWeather_desp());
                tv_now_temp.setText(weather.getNow_temp() + " ℃");
                tv_aqi.setText(weather.getAqi());
                tv_quality.setText(weather.getQuality());

                //预报
                //未来3小时
                List<FutureHourWeather> futureHourList = weather.getFutureHourList();
                tv_next_three_hour.setText(futureHourList.get(0).getHour());
                tv_next_three_temp.setText(futureHourList.get(0).getTemp() + " ℃");

                tv_next_six_hour.setText(futureHourList.get(1).getHour());
                tv_next_six_temp.setText(futureHourList.get(1).getTemp() + " ℃");

                tv_next_nine_hour.setText(futureHourList.get(2).getHour());
                tv_next_nine_temp.setText(futureHourList.get(2).getTemp() + " ℃");

                tv_next_twelve_hour.setText(futureHourList.get(3).getHour());
                tv_next_twelve_temp.setText(futureHourList.get(3).getTemp() + " ℃");

                tv_next_fifteen_hour.setText(futureHourList.get(4).getHour());
                tv_next_fifteen_temp.setText(futureHourList.get(4).getTemp() + " ℃");

                //未来三天
                List<FutureDateWeather> futureDateList = weather.getFutureDateList();
                tv_today_temp_max.setText(futureDateList.get(0).getTemp_max() + " ℃");
                tv_today_temp_min.setText(futureDateList.get(0).getTemp_min() + " ℃");

                tv_tomorrow.setText(futureDateList.get(1).getDate());
                tv_tomorrow_temp_max.setText(futureDateList.get(1).getTemp_max() + " ℃");
                tv_tomorrow_temp_min.setText(futureDateList.get(1).getTemp_min() + " ℃");

                tv_third.setText(futureDateList.get(2).getDate());
                tv_third_temp_max.setText(futureDateList.get(2).getTemp_max() + " ℃");
                tv_third_temp_min.setText(futureDateList.get(2).getTemp_min() + " ℃");

                tv_fourth.setText(futureDateList.get(3).getDate());
                tv_fourth_temp_max.setText(futureDateList.get(3).getTemp_max() + " ℃");
                tv_fourth_temp_min.setText(futureDateList.get(3).getTemp_min() + " ℃");

                // 详细信息
                tv_humidity.setText(weather.getHumidity() + "%");
                tv_wind.setText(weather.getWind());
                tv_uv_index.setText(weather.getUv_index());
                tv_dressing_index.setText(weather.getDressing_index());
            }
        });
    }

}
