package org.weatherwear.clients;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.weatherwear.clients.GenericRestClient.HttpRequestMethods;
import org.weatherwear.clients.GenericRestClient.HttpResponse;
import org.weatherwear.clients.GenericRestClient.HttpStatusCodeUnknown;
import org.weatherwear.clients.GenericRestClient.IRestClient;
import org.weatherwear.clients.LocationClient.LocationClient;
import org.weatherwear.clients.Models.Location;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;

public class LocationClientTest {
    private IRestClient restClientMock;
    private final HttpResponse GOODLOCATONCLIENTSERVICERESPONSE = new HttpResponse(200, "{\"lat\":20.69,\"lon\":4.20}\n");
    private final HttpResponse GOODLOCAITONBACKUPCLIENTSERVIERESPONSE = new HttpResponse(200, "{\"latitude\": 694.29, \"longitude\": 3.69}");

    private final String VALID_IATA = "MLA";
    private final HttpResponse GOODLOCATONFROMIATACLIENTSERVICERESPONSE = new HttpResponse(200, "{\"latitude\":\"35.857498\",\"longitude\":\"14.4775\",\"code\":\"MLA\",\"name\":\"Malta International Airport\"}");
    private final HttpResponse GOODLOCATONFROMIATACLIENTBACKUPSERVICERESPONSE = new HttpResponse(200, "{\"iata\":\"MLA\",\"icao\":\"LMML\",\"name\":\"Malta International Airport (Luqa Airport)\",\"location\":\"Luqa, Malta\",\"street_number\":\"\",\"street\":\"\",\"city\":\"Luqa\",\"county\":\"\",\"state\":\"\",\"country_iso\":\"MT\",\"country\":\"Malta\",\"postal_code\":\"LQA 4000\",\"phone\":\"+356 2124 9600\",\"latitude\":35.854115,\"longitude\":14.483279,\"uct\":120,\"website\":\"https://www.maltairport.com/\"}");
    private final HttpResponse UNEXCPECTEDRESPONSE = new HttpResponse(200, "{\"error\": \"unexpected\"}");

    private final HttpResponse MALFORMEDRESPONSE = new HttpResponse(200, "WTF THIS IS NOT JSON OR IS IT ?");
    @BeforeEach
    public void setupBeforeEachTest() {
        restClientMock = Mockito.mock(IRestClient.class);
    }

    @Test
    public void WhenGettingCurrentLocation_WithGoodLocationServiceAPIResponse_ReturnsExpectedLocation() throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "http://ip-api.com", "/json")).thenReturn(GOODLOCATONCLIENTSERVICERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        Location locationUnderTest = locationClient.getLocation();
        Assertions.assertAll(() -> Assertions.assertEquals("20.69", locationUnderTest.getLatitude()), () -> Assertions.assertEquals("4.2", locationUnderTest.getLongitude()));
    }

    @Test
    public void WhenGettingCurrentLocationFromBackupService_locationFixedResponse_ReturnsExpectedLocation() throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenReturn(GOODLOCAITONBACKUPCLIENTSERVIERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        Location locationUnderTest = locationClient.getLocationBackup();
        Assertions.assertAll(() -> Assertions.assertEquals("694.29", locationUnderTest.getLatitude()), () -> Assertions.assertEquals("3.69", locationUnderTest.getLongitude()));
    }

    @Test
    public void WhenGettingLocationFromIATA_LocationFixedResponse_ReturnsExpectedLocation() throws JsonProcessingException, SocketTimeoutException, HttpStatusCodeUnknown {
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://www.iatageo.com", "/getLatLng/" + VALID_IATA)).thenReturn(GOODLOCATONFROMIATACLIENTSERVICERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        Location locationUnderTest = locationClient.getLocation(VALID_IATA);
        Assertions.assertAll(() -> Assertions.assertEquals("35.857498", locationUnderTest.getLatitude()), () -> Assertions.assertEquals("14.4775", locationUnderTest.getLongitude()));
    }

    @Test
    public void WhenGettingCurrentLocation_WithSocketTimeOutWithFirstLocationService_CallBackUpServiceOnce() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "http://ip-api.com", "/json")).thenThrow(SocketTimeoutException.class);
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenReturn(GOODLOCAITONBACKUPCLIENTSERVIERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //exercise
        locationClient.getLocation();
        //assert
        Mockito.verify(restClientMock, Mockito.times(1)).request(HttpRequestMethods.GET, "https://ipapi.co", "/json");
    }

    @Test
    public void WhenGettingCurrentLocation_WithMalformedResponse_CallBackUpServiceOnce() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "http://ip-api.com", "/json")).thenReturn(MALFORMEDRESPONSE);
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenReturn(GOODLOCAITONBACKUPCLIENTSERVIERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //exercise
        locationClient.getLocation();
        //assert
        Mockito.verify(restClientMock, Mockito.times(1)).request(HttpRequestMethods.GET, "https://ipapi.co", "/json");
    }

    @Test
    public void WhenGettingCurrentLocation_WithUnexpectedResponse_CallBackUpServiceOnce() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "http://ip-api.com", "/json")).thenReturn(UNEXCPECTEDRESPONSE);
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenReturn(GOODLOCAITONBACKUPCLIENTSERVIERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //exercise
        locationClient.getLocation();
        //assert
        Mockito.verify(restClientMock, Mockito.times(1)).request(HttpRequestMethods.GET, "https://ipapi.co", "/json");
    }


    @Test
    public void WhenGettingLocationFromIATA_WithSocketTimeOutWithFirstLocationService_CallBackUpServiceOnce() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://www.iatageo.com", "/getLatLng/" + VALID_IATA)).thenThrow(SocketTimeoutException.class);
        Mockito.when(restClientMock.request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://airport-info.p.rapidapi.com"), ArgumentMatchers.eq("/airport"), Mockito.any(), Mockito.any())).thenReturn(GOODLOCATONFROMIATACLIENTBACKUPSERVICERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //exercise
        locationClient.getLocation(VALID_IATA);
        //assert
        Mockito.verify(restClientMock, Mockito.times(1)).request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://airport-info.p.rapidapi.com"), ArgumentMatchers.eq("/airport"), Mockito.any(), Mockito.any());
    }

    @Test
    public void WhenGettingLocationFromIATA_WithMalformedResponse_CallBackUpServiceOnce() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://www.iatageo.com", "/getLatLng/" + VALID_IATA)).thenReturn(MALFORMEDRESPONSE);
        Mockito.when(restClientMock.request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://airport-info.p.rapidapi.com"), ArgumentMatchers.eq("/airport"), Mockito.any(), Mockito.any())).thenReturn(GOODLOCATONFROMIATACLIENTBACKUPSERVICERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //exercise
        locationClient.getLocation(VALID_IATA);
        //verify
        Mockito.verify(restClientMock, Mockito.times(1)).request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://airport-info.p.rapidapi.com"), ArgumentMatchers.eq("/airport"), Mockito.any(), Mockito.any());
    }

    @Test
    public void WhenGettingLocationFromIATA_WithUnexpectedResponse_CallBackUpServiceOnce() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://www.iatageo.com", "/getLatLng/" + VALID_IATA)).thenReturn(UNEXCPECTEDRESPONSE);
        Mockito.when(restClientMock.request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://airport-info.p.rapidapi.com"), ArgumentMatchers.eq("/airport"), Mockito.any(), Mockito.any())).thenReturn(GOODLOCATONFROMIATACLIENTBACKUPSERVICERESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //exercise
        locationClient.getLocation(VALID_IATA);
        //verify
        Mockito.verify(restClientMock, Mockito.times(1)).request(ArgumentMatchers.eq(HttpRequestMethods.GET), ArgumentMatchers.eq("https://airport-info.p.rapidapi.com"), ArgumentMatchers.eq("/airport"), Mockito.any(), Mockito.any());
    }

    @Test
    public void WhenGettingCurrentLocationFromBackupService_WithSocketTimeOut_ExpectedSocketTimeOutException() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenThrow(SocketTimeoutException.class);
        LocationClient locationClient = new LocationClient(restClientMock);
        //assert and verify
        Assertions.assertThrows(SocketTimeoutException.class, locationClient::getLocationBackup, "SocketTimeoutException is expected");
    }

    @Test
    public void WhenGettingLocationFromIATAFromBackupService_WithSocketTimeOut_ExpectedSocketTimeOutException() throws SocketTimeoutException, JsonProcessingException, HttpStatusCodeUnknown {
       //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenThrow(SocketTimeoutException.class);
        LocationClient locationClient = new LocationClient(restClientMock);
        //assert and verify
        Assertions.assertThrows(SocketTimeoutException.class, locationClient::getLocationBackup, "SocketTimeoutException is expected");
    }

    @Test
    public void WhenGettingCurrentLocationFromBackupService_NotExpectedJsonResponse_ThrowsNullPointerException() throws SocketTimeoutException, HttpStatusCodeUnknown {
       //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenReturn(UNEXCPECTEDRESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //assert and verify
        Assertions.assertThrows(NullPointerException.class, locationClient::getLocationBackup, "NullPointerException is expected");
    }

    @Test
    public void WhenGettingCurrentLocationFromBackupService_MalformedResponse_ThrowsNullPointerException() throws JsonParseException, SocketTimeoutException, HttpStatusCodeUnknown {
        //setup
        Mockito.when(restClientMock.request(HttpRequestMethods.GET, "https://ipapi.co", "/json")).thenReturn(MALFORMEDRESPONSE);
        LocationClient locationClient = new LocationClient(restClientMock);
        //assert and verify
        Assertions.assertThrows(JsonParseException.class, locationClient::getLocationBackup, "JsonParseException is expected");
    }
}