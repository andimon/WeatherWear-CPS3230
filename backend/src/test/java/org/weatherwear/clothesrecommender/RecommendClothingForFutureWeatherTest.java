package org.weatherwear.clothesrecommender;

import org.junit.jupiter.api.*;
import org.weatherwear.weatherdecider.WeatherDecider;
import org.weatherwear.weatherdecider.WeatherPossibility;
import org.mockito.Mockito;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.SplittableRandom;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RecommendClothingForFutureWeatherTest {
    private Clock clock;

    private static final String validIATA = "MLA";
    private static final String expectedWarmAndNotRainingClothingRecommendation = "It is warm so you should wear light clothing.\nIt is not raining so you don't need an umbrella.";

    private WeatherDecider weatherDecider;

    @BeforeAll
    public void setupBeforeAll() {
        clock = Clock.fixed(Instant.parse(("2023-01-01T00:00:00.00Z")), ZoneId.of("UTC"));
    }

    @BeforeEach
    public void setupBeforeEach() {
        weatherDecider = Mockito.mock(WeatherDecider.class);
    }

    @Test
    public void RecommendClothing_WarmAndNotRaining_RecommendLightClothingAndNoUmbrellaMessage() throws Exception {
        //setup
        boolean isRaining = false;
        boolean isCold = false;
        WeatherPossibility weatherPossibility = new WeatherPossibility(isRaining, isCold);
        Mockito.when(weatherDecider.decideWeather(validIATA, 0)).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //exercise
        String message = recommendClothing.recommendClothing(validIATA, "2023-01-01");
        //verify
        Assertions.assertEquals(expectedWarmAndNotRainingClothingRecommendation, message);
    }

    @Test
    public void RecommendClothingForFutureWeather_WarmAndRaining_RecommendLightClothingAndUmbrella() throws Exception {
        //setup
        String IATA = "MLA";
        WeatherPossibility weatherPossibility = new WeatherPossibility(true, false);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather(IATA, 0)).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //exercise
        String message = recommendClothing.recommendClothing(IATA, "2023-01-01");
        //verify
        Assertions.assertEquals("It is warm so you should wear light clothing.\nIt is currently raining so you do need an umbrella.", message);
    }

    @Test
    public void RecommendClothing_ColdAndNotRaining_RecommendWarmClothingAndNoUmbrella() throws Exception {
        //setup
        String IATA = "MLA";
        WeatherPossibility weatherPossibility = new WeatherPossibility(false, true);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather(IATA, 0)).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //exercise
        String message = recommendClothing.recommendClothing(IATA, "2023-01-01");
        //verify
        Assertions.assertEquals("It is cold so you should wear warm clothing.\nIt is not raining so you don't need an umbrella.", message);
    }

    @Test
    public void RecommendClothing_ColdAndRaining_RecommendWarmClothingAndUmbrella() throws Exception {
        //setup
        String IATA = "MLA";
        WeatherPossibility weatherPossibility = new WeatherPossibility(true, true);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather(IATA, 0)).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //exercise
        String message = recommendClothing.recommendClothing(IATA, "2023-01-01");
        //verify
        Assertions.assertEquals("It is cold so you should wear warm clothing.\nIt is currently raining so you do need an umbrella.", message);
    }

    @Test
    public void RecommendClothing_DateIsInThePast_ExpectedDateTimeException() throws Exception {
        //setup
        String invalidDate = "2022-12-31";
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //verify
        DateTimeException exception = Assertions.assertThrows(DateTimeException.class, () -> {
            recommendClothing.recommendClothing(validIATA, invalidDate);
        }, "DateTimeException is expected");
        Assertions.assertEquals("Expected date to be between 0 and 10 days in the future", exception.getMessage());
    }

    @Test
    public void RecommendClothing_ElevenDaysFromNow_ExpectedDateTimeException() throws Exception {
        //setup
        String IATA = "MLA";
        WeatherPossibility weatherPossibility = new WeatherPossibility(true, true);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather(IATA, 0)).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //exercise
        DateTimeException exception = Assertions.assertThrows(DateTimeException.class, () -> {
            recommendClothing.recommendClothing(IATA, "2023-01-12");
        }, "DateTimeException is expected");
        //verify
        Assertions.assertEquals("Expected date to be between 0 and 10 days in the future", exception.getMessage());

    }

    @Test
    public void RecommendClothing_TenDaysFromNow_NoExceptionThrown() throws Exception {
        //setup
        String IATA = "MLA";
        WeatherPossibility weatherPossibility = new WeatherPossibility(false, true);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather(IATA, 0)).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //exercise
        Assertions.assertDoesNotThrow(() -> recommendClothing.recommendClothing(IATA, "2023-01-01"));
    }

    @Test
    public void RecommendClothing_invalid_ExpectedIllegalArgumentException() throws Exception {
        //setup
        String invalidIATA = "mla";
        WeatherPossibility weatherPossibility = new WeatherPossibility(true, true);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather(invalidIATA, 0)).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider, clock);
        //exercise
        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            recommendClothing.recommendClothing(invalidIATA, "2023-01-01");
        }, "Illegal argument is expected");
        //verify
        Assertions.assertEquals("IATA is invalid", exception.getMessage());

    }


}