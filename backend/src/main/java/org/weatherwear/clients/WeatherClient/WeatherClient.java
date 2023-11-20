package org.weatherwear.clients.WeatherClient;

import org.weatherwear.clients.GenericRestClient.*;
import org.weatherwear.clients.Models.Location;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.weatherwear.clients.Models.Weather;

import java.net.SocketTimeoutException;

public class WeatherClient implements IWeatherClient {
    private final IRestClient restClientInterface;

    public WeatherClient(IRestClient restClientInterface) {
        this.restClientInterface = restClientInterface;
    }

    private MultivaluedMap<String, Object> getHeaders() {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        return headers;
    }

    private MultivaluedMap<String, Object> getQueryParam(Location location, String days) {
        MultivaluedMap<String, Object> queryParam = new MultivaluedHashMap<>();
        queryParam.add("latitude", location.getLatitude());
        queryParam.add("longitude", location.getLongitude());
        queryParam.add("daily", "temperature_2m_max,precipitation_sum");
        queryParam.add("timezone", "auto");
        queryParam.add("forecast_days", days);
        return queryParam;
    }


    private Weather getWeatherFromService(Location location) throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        return getWeatherFromService(location, "1", 0);
    }

    private Weather getWeatherFromService(Location location, String days, int day) throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        HttpResponse response = restClientInterface.request(HttpRequestMethods.GET, "https://api.open-meteo.com/v1", "/forecast", getHeaders(), getQueryParam(location, days));
        final JsonNode node = new ObjectMapper().readTree(response.getResponseBody());
        double tempInCelsius = node.get("daily").get("temperature_2m_max").get(day).asDouble();
        double precipitationProbability = node.get("daily").get("precipitation_sum").get(day).asDouble();
        return new Weather(tempInCelsius, precipitationProbability);
    }

    public Weather getWeather(Location location) throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        return getWeatherFromService(location);
    }

    public Weather getWeather(Location location, int day) throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        return getWeatherFromService(location, "11", day);
    }
}
