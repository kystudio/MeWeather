package com.kystudio.meweather.model;

/**
 * Created by 20236320 on 2016/12/1.
 */

public class FutureHourWeather {
    private String weather_id;
    private String temp;
    private String hour;

    public String getWeather_id() {
        return weather_id;
    }

    public void setWeather_id(String weather_id) {
        this.weather_id = weather_id;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }
}
