package org.weatherwear.clients;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.weatherwear.clients.GenericRestClient.HttpRequestMethods;
import org.weatherwear.clients.GenericRestClient.HttpResponse;
import org.weatherwear.clients.GenericRestClient.HttpStatusCodeUnknown;
import org.weatherwear.clients.GenericRestClient.IRestClient;
import org.weatherwear.clients.Models.Location;
import org.weatherwear.clients.Models.Weather;
import org.weatherwear.clients.WeatherClient.WeatherClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.SocketTimeoutException;


public class WeatherClientTest {
    private final HttpResponse GOODWEATHERSERVICERESPONSE = new HttpResponse(200, "{\"latitude\":22.52,\"longitude\":60.69999,\"generationtime_ms\":0.04494190216064453,\"utc_offset_seconds\":3600,\"timezone\":\"Europe/Berlin\",\"timezone_abbreviation\":\"CET\",\"elevation\":38.0,\"daily_units\":{\"time\":\"iso8601\",\"temperature_2m_max\":\"°C\",\"precipitation_sum\":\"%\"},\"daily\":{\"time\":[\"2023-11-03\"],\"temperature_2m_max\":[11.1],\"precipitation_sum\":[88]}}");

    private final HttpResponse GOODFUTUREWEATHERSERVICERESPONSE = new HttpResponse(200, "{\"latitude\":22.52,\"longitude\":60.69999,\"generationtime_ms\":0.04494190216064453,\"utc_offset_seconds\":3600,\"timezone\":\"Europe/Berlin\",\"timezone_abbreviation\":\"CET\",\"elevation\":38.0,\"daily_units\":{\"time\":\"iso8601\",\"temperature_2m_max\":\"°C\",\"precipitation_sum\":\"%\"},\"daily\":{\"time\":[\"2023-11-03\"],\"temperature_2m_max\":[11.1,3.6],\"precipitation_sum\":[88,12.1]}}");
    private IRestClient restClientMock;
    private final Location DUMMYLOCATION = new Location("1.69", "4.20");

    @BeforeEach
    public void setupBeforeEachTest() {
        restClientMock = Mockito.mock(IRestClient.class);
    }

    @Test
    public void WhenGettingCurrentWeatherFromLocation_WeatherServiceReturnsExpectedResponse_ReturnsExpectedWeather() throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://api.open-meteo.com/v1"), ArgumentMatchers.eq("/forecast"), Mockito.any(), Mockito.any())).thenReturn(GOODWEATHERSERVICERESPONSE);
        WeatherClient weatherClient = new WeatherClient(restClientMock);
        //exercise
        Weather weatherUnderTest = weatherClient.getWeather(DUMMYLOCATION);
        //verify
        Assertions.assertAll(
                () -> Assertions.assertEquals(88.0, weatherUnderTest.getPrecipitationProbability()),
                () -> Assertions.assertEquals(11.1, weatherUnderTest.getTempInCelsius())
        );
    }

    //Expected Exceptions Tests
    @Test
    public void getFutureWeather_ValidResponse_returnsExpectedWeather() throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://api.open-meteo.com/v1"), ArgumentMatchers.eq("/forecast"), Mockito.any(), Mockito.any())).thenReturn(GOODFUTUREWEATHERSERVICERESPONSE);

        WeatherClient weatherClient = new WeatherClient(restClientMock);


        //exercise
        Weather weatherUnderTest = weatherClient.getWeather(DUMMYLOCATION, 1);
        //verify
        Assertions.assertAll(
                () -> Assertions.assertEquals(12.1, weatherUnderTest.getPrecipitationProbability()),
                () -> Assertions.assertEquals(3.6, weatherUnderTest.getTempInCelsius())
        );
    }
}