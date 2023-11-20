package org.weatherwear;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import org.weatherwear.clients.GenericRestClient.RestClient;
import org.weatherwear.clients.GenericRestClient.IRestClient;
import org.weatherwear.clients.LocationClient.LocationClient;
import org.weatherwear.clients.LocationClient.ILocationClient;
import org.weatherwear.clients.Models.Location;
import org.weatherwear.clients.WeatherClient.WeatherClient;
import org.weatherwear.clients.WeatherClient.IWeatherClient;
import org.weatherwear.menu.Menu;
import org.weatherwear.clothesrecommender.RecommendClothing;
import org.weatherwear.weatherdecider.WeatherDecider;

import java.util.concurrent.TimeUnit;
public class Main {
    public static void main(String[] args) throws Exception {
        //Link all the components together
        Client client = ClientBuilder.newBuilder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .build();
        IRestClient IRestClient = new RestClient(client);
        //Setup Location and Weather Clients using Generic Rest Client
        ILocationClient ILocationClient;
        ILocationClient = new LocationClient(IRestClient);
        IWeatherClient IWeatherClient = new WeatherClient(IRestClient);
        //Set up weather decider
        WeatherDecider weatherDecider = new WeatherDecider(ILocationClient, IWeatherClient);
        //Set up clothing recommender
        RecommendClothing recommendClothing = new RecommendClothing(weatherDecider);
        //Boot menu
        new Menu(System.in, System.out, recommendClothing).start();
    }
}