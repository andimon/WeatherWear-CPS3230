package org.weatherwear.clothesrecommender;

import org.junit.jupiter.api.BeforeEach;
import org.weatherwear.weatherdecider.WeatherDecider;
import org.weatherwear.weatherdecider.IWeatherDecider;
import org.weatherwear.weatherdecider.WeatherPossibility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class RecommendClothingForCurrentWeatherTest {
    private final String RECOMMEND_LIGHT_CLOTHING_AND_NO_UMBRELLA = "It is warm so you should wear light clothing.\nIt is not raining so you don't need an umbrella.";


    private IWeatherDecider weatherDeciderMock;

    @BeforeEach
    public  void setupBeforeEach(){
        weatherDeciderMock = Mockito.mock((IWeatherDecider.class));
    }


    @Test
    public void RecommendClothing_WarmAndNotRaining_RecommendLightClothingAndNoUmbrella() throws Exception {
        //setup
        boolean isRaining = false;
        boolean isCold = false;
        WeatherPossibility weatherPossibility = new WeatherPossibility(isRaining,isCold);
        Mockito.when(weatherDeciderMock.decideWeather()).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDeciderMock);
        //exercise
        String message = recommendClothing.recommendClothing();
        //verify
        Assertions.assertEquals(RECOMMEND_LIGHT_CLOTHING_AND_NO_UMBRELLA,message);
    }

    @Test
    public void RecommendClothing_WarmAndRaining_RecommendLightClothingAndUmbrella() throws Exception {
        //setup
        boolean isRaining = true;
        boolean isCold = false;
        WeatherPossibility weatherPossibility = new WeatherPossibility(isRaining,isCold);
        Mockito.when(weatherDeciderMock.decideWeather()).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDeciderMock);
        //exercise
        String message = recommendClothing.recommendClothing();
        //verify
        Assertions.assertEquals("It is warm so you should wear light clothing.\nIt is currently raining so you do need an umbrella.",message);
    }
    @Test
    public void RecommendClothing_ColdAndNotRaining_RecommendWarmClothingAndNoUmbrella() throws Exception {
        //setup
        WeatherPossibility weatherPossibility = new WeatherPossibility(false,true);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather()).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider);
        //exercise
        String message = recommendClothing.recommendClothing();
        //verify
        Assertions.assertEquals("It is cold so you should wear warm clothing.\nIt is not raining so you don't need an umbrella.",message);
    }
    @Test
    public void RecommendClothing_ColdAndRaining_RecommendWarmClothingAndUmbrella() throws Exception {
        //setup
        WeatherPossibility weatherPossibility = new WeatherPossibility(true,true);
        WeatherDecider weatherDecider = Mockito.mock(WeatherDecider.class);
        Mockito.when(weatherDecider.decideWeather()).thenReturn(weatherPossibility);
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider);
        //exercise
        String message = recommendClothing.recommendClothing();
        //verify
        Assertions.assertEquals("It is cold so you should wear warm clothing.\nIt is currently raining so you do need an umbrella.",message);
    }

}
