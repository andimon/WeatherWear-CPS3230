package org.weatherwear.clients.WeatherClient;

import org.weatherwear.clients.Models.Location;
import org.weatherwear.clients.Models.Weather;

public interface IWeatherClient {
        Weather getWeather(Location location) throws Exception;
        Weather getWeather(Location location, int day) throws Exception;
}
