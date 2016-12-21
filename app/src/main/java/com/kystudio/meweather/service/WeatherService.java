package com.kystudio.meweather.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.kystudio.meweather.model.FutureDateWeather;
import com.kystudio.meweather.model.FutureHourWeather;
import com.kystudio.meweather.model.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 20236320 on 2016/12/9.
 */

public class WeatherService extends Service {
    private static final String TAG = "MeWeather";
    private WeatherServiceBinder binder = new WeatherServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    public void test(){
        Log.d(TAG, "test!!!");
    }

    public class WeatherServiceBinder extends Binder {
        public WeatherService getService() {
            return WeatherService.this;
        }
    }

    public Weather parseJson(String jsonData) {
        Weather weather = null;
        try {
            JSONObject json = new JSONObject(jsonData);
            int error_code = json.getInt("error_code");
            if (error_code == 0) {
                JSONObject resultJson = json.getJSONObject("result");
                JSONObject dataJson = resultJson.getJSONObject("data");
                weather = new Weather();

                JSONObject realtimeJson = dataJson.getJSONObject("realtime");
                weather.setCity(realtimeJson.getString("city_name"));
                weather.setRelease(realtimeJson.getString("date") + " " + realtimeJson.getString("time"));
                weather.setWeather_desp(realtimeJson.getJSONObject("weather").getString("info"));
                weather.setNow_temp(realtimeJson.getJSONObject("weather").getString("temperature"));
                weather.setHumidity(realtimeJson.getJSONObject("weather").getString("humidity"));
                weather.setWind(realtimeJson.getJSONObject("wind").getString("direct") + " " + realtimeJson.getJSONObject("wind").getString("power"));

                JSONObject lifeJson = dataJson.getJSONObject("life");
                JSONArray chuanyiInfos = lifeJson.getJSONObject("info").getJSONArray("chuanyi");
                JSONArray ziwaixianInfos = lifeJson.getJSONObject("info").getJSONArray("ziwaixian");
                weather.setDressing_index(chuanyiInfos.get(0).toString());
                weather.setUv_index(ziwaixianInfos.get(0).toString());

                List<FutureDateWeather> futureDateList = getFutureDateWeathers(dataJson);
                weather.setFutureDateList(futureDateList);

                JSONObject f3hJson = dataJson.getJSONObject("f3h");
                List<FutureHourWeather> futureHourList = getFutureHourWeathers(f3hJson);
                weather.setFutureHourList(futureHourList);

                JSONObject pm25Json = dataJson.getJSONObject("pm25");
                weather.setAqi(pm25Json.getJSONObject("pm25").getString("curPm"));
                weather.setQuality(pm25Json.getJSONObject("pm25").getString("quality"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return weather;
    }

    @NonNull
    private List<FutureHourWeather> getFutureHourWeathers(JSONObject f3hJson) throws JSONException {
        JSONArray futureHourArray = f3hJson.getJSONArray("temperature");
        List<FutureHourWeather> futureHourList = new ArrayList<>();
        for (int i = 0; i < futureHourArray.length(); i++) {
            JSONObject futureHourJson = futureHourArray.getJSONObject(i);
            FutureHourWeather futureHourWeather = new FutureHourWeather();
            String time = futureHourJson.getString("jg");
            String hour = time.substring(8, 10);
            futureHourWeather.setHour(hour + ":00");
            futureHourWeather.setTemp(futureHourJson.getString("jb"));
            futureHourList.add(futureHourWeather);
        }
        return futureHourList;
    }

    @NonNull
    private List<FutureDateWeather> getFutureDateWeathers(JSONObject dataJson) throws JSONException {
        JSONArray futureDateArray = dataJson.getJSONArray("weather");
        List<FutureDateWeather> futureDateList = new ArrayList<>();
        for (int i = 0; i < futureDateArray.length(); i++) {
            JSONObject futureJson = futureDateArray.getJSONObject(i);
            FutureDateWeather futureDateWeather = new FutureDateWeather();
            futureDateWeather.setDate(futureJson.getString("date"));
            futureDateWeather.setWeek(futureJson.getString("week"));
            JSONArray dayInfos = futureJson.getJSONObject("info").getJSONArray("day");
            JSONArray nightInfos = futureJson.getJSONObject("info").getJSONArray("night");
            futureDateWeather.setTemp_max(dayInfos.get(2).toString());
            futureDateWeather.setTemp_min(nightInfos.get(2).toString());
            futureDateList.add(futureDateWeather);
        }
        return futureDateList;
    }
}
