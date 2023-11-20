package org.weatherwear.weatherdecider;

import org.weatherwear.clients.Models.Location;
import org.weatherwear.clients.LocationClient.ILocationClient;
import org.weatherwear.clients.Models.Weather;
import org.weatherwear.clients.WeatherClient.IWeatherClient;

public class WeatherDecider implements IWeatherDecider {

    //LocationClient and WeatherClient are 2 required dependencies hence we pass them to constructor
    private final ILocationClient locationClientInterface;
    private final IWeatherClient weatherClientInterface;
    public WeatherDecider(ILocationClient locationClientInterface, IWeatherClient weatherClientInterface){
        this.locationClientInterface = locationClientInterface;
        this.weatherClientInterface = weatherClientInterface;
    }

    private WeatherPossibility getWeatherPossibilityOutOfTemperatureInCelsiusAndPrecipitation(Weather weather){
        boolean isRaining = weather.getPrecipitationProbability()>0;
        boolean isCold = weather.getTempInCelsius()<=15;
        return new WeatherPossibility(isRaining, isCold);
    }

    public WeatherPossibility decideWeather() throws Exception {
        Location location = locationClientInterface.getLocation();
        Weather weather = weatherClientInterface.getWeather(location);
        return getWeatherPossibilityOutOfTemperatureInCelsiusAndPrecipitation(weather);
    }
    public WeatherPossibility decideWeather(String IATA, int day) throws Exception {
        Location location =  locationClientInterface.getLocation(IATA);
        Weather weather = weatherClientInterface.getWeather(location,day);
        return  getWeatherPossibilityOutOfTemperatureInCelsiusAndPrecipitation(weather);
    }
}
