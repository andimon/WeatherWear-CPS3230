package org.weatherwear.weatherdecider;

import org.junit.jupiter.api.*;
import org.weatherwear.clients.LocationClient.ILocationClient;
import org.weatherwear.clients.Models.Location;
import org.weatherwear.clients.Models.Weather;
import org.weatherwear.clients.WeatherClient.IWeatherClient;
import org.mockito.Mockito;

public class DecideCurrentWeatherTest {
    public static Location mockLocation;
    public static ILocationClient locationClientMock; // Renamed for consistency

    public IWeatherClient weatherClientMock; // Renamed for consistency

    @BeforeAll
    public static void setupBeforeAll() throws Exception {
        locationClientMock = Mockito.mock(ILocationClient.class);
        Mockito.when(locationClientMock.getLocation()).thenReturn(mockLocation);
    }

    @BeforeEach
    public void setupBeforeEach() {
        weatherClientMock = Mockito.mock(IWeatherClient.class);
    }

    @Test
    public void decideCurrentWeather_whenTemperatureIsZeroCelsius_isCold() throws Exception {
        double celsius = 0;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(celsius, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather().isCold());
    }

    @Test
    public void decideCurrentWeather_whenTemperatureIsFifteenCelsius_isCold() throws Exception {
        double celsius = 15;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(celsius, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather().isCold());
    }

    @Test
    public void decideCurrentWeather_whenTemperatureSlightlyGreaterThanFifteenCelsius_isWarm() throws Exception {
        double celsius = 15.00001;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(celsius, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather().isCold());
    }

    @Test
    public void decideCurrentWeather_whenTemperatureIsSixteenCelsius_isWarm() throws Exception {
        double celsius = 16;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(celsius, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather().isCold());
    }

    @Test
    public void decideCurrentWeather_whenPrecipitationIsSlightlyLessThanZero_isNotRaining() throws Exception {
        double precipitation = -0.00001;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(0, precipitation));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather().isRaining());
    }

    @Test
    public void decideCurrentWeather_whenPrecipitationIsZero_isNotRaining() throws Exception {
        double precipitation = 0;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(0, precipitation));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather().isRaining());
    }

    @Test
    public void decideCurrentWeather_whenPrecipitationIsSlightlyGreaterThanZero_isRaining() throws Exception {
        double precipitation = 0.00001;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(0, precipitation));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather().isRaining());
    }

    @Test
    public void decideCurrentWeather_whenPrecipitationIsOne_isRaining() throws Exception {
        double precipitation = 1;
        Mockito.when(weatherClientMock.getWeather(mockLocation)).thenReturn(new Weather(0, precipitation));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather().isRaining());
    }
}
