package org.weatherwear.weatherdecider;

public interface IWeatherDecider {
    WeatherPossibility decideWeather() throws Exception;
    WeatherPossibility decideWeather(String IATA, int day) throws Exception;
}
