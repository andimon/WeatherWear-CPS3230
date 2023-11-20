package org.weatherwear.clothesrecommender;

import org.weatherwear.utilities.Validation;
import org.weatherwear.weatherdecider.IWeatherDecider;
import org.weatherwear.weatherdecider.WeatherPossibility;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RecommendClothing implements IRecommendClothing {
    private final IWeatherDecider IWeatherDecider;
    private final Clock clock;
    public RecommendClothing(IWeatherDecider IWeatherDecider, Clock clock){
        this.IWeatherDecider = IWeatherDecider;
        this.clock=clock;
    }
    public RecommendClothing(IWeatherDecider IWeatherDecider){
        this.IWeatherDecider = IWeatherDecider;
        this.clock = Clock.systemDefaultZone();
    }
    private String message(boolean isCold, boolean isRaining){
        String coldOrWarm = (isCold) ? "cold" : "warm";
        String warmOrLight = (isCold) ? "warm" : "light";
        String notOrCurrently = (isRaining) ? "currently" : "not";
        String doOrDont = (isRaining) ? "do" : "don't";
        return "It is "+coldOrWarm+" so you should wear "+warmOrLight+" clothing.\n"+"It is "+notOrCurrently+" raining so you "+doOrDont+" need an umbrella.";
    }
    private int getNumberOfDaysFromToday(String date) {
        Validation validation = new Validation();
        String today = LocalDate.now(clock).format(DateTimeFormatter.ISO_LOCAL_DATE);
        long days = validation.dayDifference(today,date);
        if(days<0 || days>10)
            throw new DateTimeException("Expected date to be between 0 and 10 days in the future");
        return (int) days;
    }
    private void validateIATA(String IATA){
        Validation validation = new Validation();
        if(!validation.isIATAValid(IATA)){
            throw new IllegalArgumentException("IATA is invalid");
        }
    }
    @Override
    public String recommendClothing() throws Exception {
       WeatherPossibility weather = IWeatherDecider.decideWeather();
       return message(weather.isCold(), weather.isRaining());
    }
    @Override
    public String recommendClothing(String IATA, String date) throws Exception {
        validateIATA(IATA);
        WeatherPossibility weather = IWeatherDecider.decideWeather(IATA,getNumberOfDaysFromToday(date));
        return message(weather.isCold(), weather.isRaining());
    }
}
