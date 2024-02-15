package com.ukn.edu.weatherstation.weather;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ukn.edu.weatherstation.R;

public class WeatherView extends RelativeLayout {
    public TextView currentWeatherTextView;
    public TextView highLowTemperatureTextView;
    public TextView rainfallChanceTextView;
    public TextView ciTextView;
    public TextView datetimeTextView;
    public WeatherView(Context context) {
        super(context);
        inflate(context, R.layout.view_weather, this);

        initLayout();
    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.view_weather, this);

        initLayout();
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, R.layout.view_weather, this);

        initLayout();
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflate(context, R.layout.view_weather, this);

        initLayout();
    }

    private void initLayout(){
        currentWeatherTextView = findViewById(R.id.currentWeatherTextView);
        highLowTemperatureTextView = findViewById(R.id.highLowTemperatureTextView);
        rainfallChanceTextView = findViewById(R.id.rainfallChance);
        ciTextView = findViewById(R.id.ci);
        datetimeTextView = findViewById(R.id.datetime);
    }
}
