package org.weatherwear.clients.LocationClient;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.weatherwear.clients.GenericRestClient.*;
import org.weatherwear.clients.Models.Location;

import java.net.SocketTimeoutException;

public class LocationClient implements ILocationClient {
    private final Dotenv dotenv = Dotenv.load();

    private MultivaluedMap<String, Object> getHeaders() {
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("X-RapidAPI-Key", dotenv.get("X-RapidAPI-Key"));
        headers.add("X-RapidAPI-Host", "airport-info.p.rapidapi.com");
        return headers;
    }

    private MultivaluedMap<String, Object> getQueryParam(String iata) {
        MultivaluedMap<String, Object> queryParam = new MultivaluedHashMap<>();
        queryParam.add("iata", iata);
        return queryParam;
    }

    private final IRestClient restClientInterface;

    public LocationClient(IRestClient restClientInterface) {
        this.restClientInterface = restClientInterface;
    }

    public Location getLocation() throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        try {
            HttpResponse response = restClientInterface.request(HttpRequestMethods.GET, "http://ip-api.com", "/json");
            JsonNode node = new ObjectMapper().readTree(response.getResponseBody());
            return new Location(node.get("lat").asText(), node.get("lon").asText());
        } catch (SocketTimeoutException | JsonParseException | NullPointerException e) {
            return getLocationBackup();
        }
    }

    public Location getLocation(String IATA) throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        try {
            HttpResponse response = restClientInterface.request(HttpRequestMethods.GET, "https://www.iatageo.com", "/getLatLng/" + IATA);
            JsonNode node = new ObjectMapper().readTree(response.getResponseBody());
            return new Location(node.get("latitude").asText(), node.get("longitude").asText());
        } catch (SocketTimeoutException | JsonParseException | NullPointerException e) {
            return getLocationBackup(IATA);
        }
    }

    public Location getLocationBackup() throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        HttpResponse httpResponse = restClientInterface.request(HttpRequestMethods.GET, "https://ipapi.co", "/json");
        JsonNode node = new ObjectMapper().readTree(httpResponse.getResponseBody());
        return new Location(node.get("latitude").toString(), node.get("longitude").toString());

    }

    public Location getLocationBackup(String IATA) throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        HttpResponse httpResponse = restClientInterface.request(HttpRequestMethods.GET, "https://airport-info.p.rapidapi.com", "/airport", getHeaders(), getQueryParam(IATA));
        JsonNode node = new ObjectMapper().readTree(httpResponse.getResponseBody());
        return new Location(node.get("latitude").toString(), node.get("longitude").toString());
    }
}
