package org.weatherwear.weatherdecider;

import org.junit.jupiter.api.*;
import org.weatherwear.clients.LocationClient.ILocationClient;
import org.weatherwear.clients.Models.Location;
import org.weatherwear.clients.Models.Weather;
import org.weatherwear.clients.WeatherClient.IWeatherClient;
import org.mockito.Mockito;
import org.weatherwear.clients.WeatherClient.WeatherClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DecideFutureWeatherTest {
    private   final String IATA = "MLA";
    private Location dummyLocation;
    private ILocationClient locationClientMock;

    private IWeatherClient weatherClientMock;

    private static final  double IS_COLD_THRESHOLD = 15;
    private static final  double NOT_RAINING_THRESHOLD = 0;
    private static final int day = 2;



    @BeforeAll
    public void setupBeforeAll() throws Exception {
        dummyLocation = new Location("20","21") ;
        locationClientMock = Mockito.mock(ILocationClient.class);
        Mockito.when(locationClientMock.getLocation()).thenReturn(dummyLocation);
        Mockito.when(locationClientMock.getLocation(Mockito.anyString())).thenReturn(dummyLocation);
    }

    @BeforeEach
    public void setupBeforeEach() {
        weatherClientMock = Mockito.mock(WeatherClient.class);
    }

    @Test
    public void decideWhatIsFutureWeather_temperatureZeroCelsius_temperatureIsDeemedAsCold() throws Exception {
        double celsius = 0;
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(celsius, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather(IATA,day).isCold());
    }

    @Test
    public void decideWhatIsFutureWeather_temperatureFifteenCelsius_temperatureIsDeemedAsCold() throws Exception {
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(IS_COLD_THRESHOLD, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather(IATA,day).isCold());
    }

    @Test
    public void decideWhatIsFutureWeather_slightlyBiggerThanFifteenCelsius_temperatureIsDeemedAsWarm() throws Exception {
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(IS_COLD_THRESHOLD +0.00001, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather(IATA,day).isCold());
    }

    @Test
    public void decideWhatIsFutureWeather_sixteenCelsius_temperatureIsDeemedAsWarm() throws Exception {
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(IS_COLD_THRESHOLD +1, 0));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather(IATA,day).isCold());
    }

    @Test
    public void decideWhatIsFutureWeather_slightSmallerThanZero_consideredNotRaining() throws Exception {
        double precipitation = -0.00001;
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(0, precipitation));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather(IATA,day).isRaining());
    }

    @Test
    public void decideWhatIsFutureWeather_precipitationZero_consideredNotRaining() throws Exception {
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(0, NOT_RAINING_THRESHOLD));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertFalse(weatherDecider.decideWeather(IATA,day).isRaining());
    }

    @Test
    public void decideWhatIsFutureWeather_precipitationSlightlyBiggerThanZero_consideredRaining() throws Exception {
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(0, NOT_RAINING_THRESHOLD +0.00001));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather(IATA,day).isRaining());
    }

    @Test
    public void decideWhatIsFutureWeather_precipitationOne_consideredRaining() throws Exception {
        double precipitation = 1;
        Mockito.when(weatherClientMock.getWeather(dummyLocation,day)).thenReturn(new Weather(0, precipitation));
        WeatherDecider weatherDecider = new WeatherDecider(locationClientMock, weatherClientMock);
        Assertions.assertTrue(weatherDecider.decideWeather(IATA,day).isRaining());
    }
}
