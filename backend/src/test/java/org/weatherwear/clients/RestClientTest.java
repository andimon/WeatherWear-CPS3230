package org.weatherwear.clients;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatchers;
import org.weatherwear.clients.GenericRestClient.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;

public class RestClientTest {
    private final String validJsonResponse = "{\"hello\": \"This is a JSON response\"}";
    private Response responseMock;

    private Client clientMock;

    private WebTarget webTargetMock;

    private Invocation.Builder builderMock;

    private String unkownStatusCodeExceptionMessage(int code){
        return "Code status "+code+" is unhandled by this implementation of REST CLIENT";
    }

    @BeforeEach
    public void setupBeforeEach() {
        responseMock = Mockito.mock(Response.class);
        clientMock = Mockito.mock(Client.class);
        webTargetMock = Mockito.mock(WebTarget.class);
        builderMock = Mockito.mock(Invocation.Builder.class);
        Mockito.when(webTargetMock.request()).thenReturn(builderMock);
        Mockito.when(builderMock.headers(ArgumentMatchers.any())).thenReturn(builderMock);
        Mockito.when(clientMock.target(Mockito.anyString())).thenReturn(webTargetMock);
        Mockito.when(clientMock.target(Mockito.anyString()).path(Mockito.anyString())).thenReturn(webTargetMock);
        Mockito.when(webTargetMock.request().headers(ArgumentMatchers.any()).get()).thenReturn(responseMock);//get fixed response from any resource
        Mockito.when(clientMock.target(Mockito.anyString()).path(Mockito.anyString())).thenReturn(webTargetMock);

    }

    @Test
    void testRestClient_withSomeApiReturn_returnExpectedHttpResponse() throws SocketTimeoutException, HttpStatusCodeUnknown {
        Mockito.when(responseMock.getStatus()).thenReturn(200);
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        RestClient restClient = new org.weatherwear.clients.GenericRestClient.RestClient(clientMock);
        HttpResponse httpResponse = restClient.request(HttpRequestMethods.GET, "example.com", "/json");
        Assertions.assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    void testRestClient_requestWithEmptyHeadersAndNotEmptyQueries_returnExpectedHttpResponse() throws SocketTimeoutException, HttpStatusCodeUnknown {
        //setup
        MultivaluedMap<String, Object> emptyMap = new MultivaluedHashMap<>(); //no headers
        MultivaluedMap<String, Object> queries = new MultivaluedHashMap<>();
        queries.add("QUERY", "AWESOME-QUERY");
        Mockito.when(webTargetMock.queryParam("QUERY","AWESOME-QUERY")).thenReturn(webTargetMock);
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        Mockito.when(responseMock.getStatus()).thenReturn(200);
        RestClient restClient = new org.weatherwear.clients.GenericRestClient.RestClient(clientMock);
        //exercise
        HttpResponse httpResponse = restClient.request(HttpRequestMethods.GET, "example.com", "/json", emptyMap, queries);
        //verify
        Assertions.assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    void testRestClient_requestWithEmptyHeadersAndEmptyQueries_returnExpectedHttpResponse() throws SocketTimeoutException, HttpStatusCodeUnknown {
        //setup
        MultivaluedMap<String, Object> emptyMap = new MultivaluedHashMap<>(); //no headers
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        Mockito.when(responseMock.getStatus()).thenReturn(200);
        RestClient restClient = new RestClient(clientMock);
        //exercise
        HttpResponse httpResponse = restClient.request(HttpRequestMethods.GET, "example.com", "/json", emptyMap, emptyMap);
        //verify
        Assertions.assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    void testRestClient_requestWithNonEmptyHeadersAndEmptyQueries_returnExpectedHttpResponse() throws SocketTimeoutException, HttpStatusCodeUnknown {
        //setup
        MultivaluedMap<String, Object> emptyMap = new MultivaluedHashMap<>(); //no headers
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("api-key", "1234567");
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        Mockito.when(responseMock.getStatus()).thenReturn(200);
        RestClient restClient = new RestClient(clientMock);
        //exercise
        HttpResponse httpResponse = restClient.request(HttpRequestMethods.GET, "example.com", "/json", headers, emptyMap);
        //verify
        Assertions.assertEquals(200, httpResponse.getStatusCode());
    }

    @Test
    void testRestClient_requestWithNonEmptyHeadersAndNonEmptyQueries_returnExpectedHttpResponse() throws SocketTimeoutException, HttpStatusCodeUnknown {
        //setup
        MultivaluedMap<String, Object> queries = new MultivaluedHashMap<>(); //no headers
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("api-key", "1234567");
        queries.add("QUERY1", "AWESOME-QUERY");
        queries.add("QUERY2", "AWESOME-QUERY");
        Mockito.when(webTargetMock.queryParam("QUERY1","AWESOME-QUERY")).thenReturn(webTargetMock);
        Mockito.when(webTargetMock.queryParam("QUERY2","AWESOME-QUERY")).thenReturn(webTargetMock);
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        Mockito.when(responseMock.getStatus()).thenReturn(200);
        RestClient restClient = new RestClient(clientMock);
        //exercise
        HttpResponse httpResponse = restClient.request(HttpRequestMethods.GET, "example.com", "/json", headers, queries);
        //verify
        Assertions.assertEquals(200, httpResponse.getStatusCode());
    }


    //any code that is not 200 is unrecognised by our RestClient
    @Test
    void testRestClient_statusCode199_throwHttpStatusCodeUnknownException() throws HttpStatusCodeUnknown {
        //setup
        int unrecognisedStatusCode = 199;
        Mockito.when(responseMock.getStatus()).thenReturn(unrecognisedStatusCode);
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        RestClient restClient = new RestClient(clientMock);
        //exercise + verify
        HttpStatusCodeUnknown exception = Assertions.assertThrows(HttpStatusCodeUnknown.class, () -> {restClient.request(HttpRequestMethods.GET, "example.com", "/json");});
        Assertions.assertEquals(unkownStatusCodeExceptionMessage(unrecognisedStatusCode), exception.getMessage());
    }

    @Test
    void testRestClient_statusCode201_throwHttpStatusCodeUnknownException() throws HttpStatusCodeUnknown {
        //setup
        int unrecognisedStatusCode = 201;
        Mockito.when(responseMock.getStatus()).thenReturn(unrecognisedStatusCode);
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        RestClient restClient = new RestClient(clientMock);
        //exercise + verify
        HttpStatusCodeUnknown exception = Assertions.assertThrows(HttpStatusCodeUnknown.class, () -> {restClient.request(HttpRequestMethods.GET, "example.com", "/json");});
        Assertions.assertEquals(unkownStatusCodeExceptionMessage(unrecognisedStatusCode), exception.getMessage());
    }

    @Test
    void testRestClient_requestIsMade_verifyInteractionsWithClientService() throws SocketTimeoutException, HttpStatusCodeUnknown {
        // setup
        Mockito.when(responseMock.getStatus()).thenReturn(200);
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        RestClient restClient = new RestClient(clientMock);
        // assert
        restClient.request(HttpRequestMethods.GET, "example.com", "/json");
        // verify
        Assertions.assertAll(
                () -> Mockito.verify(clientMock).target("example.com"),
                () -> Mockito.verify(webTargetMock).path("/json"),
                () -> Mockito.verify(builderMock).get()
        );
    }

    @Test
    void testRestClient_requestWithHeadersAndQueries_verifyInteractionsWithClientService() throws SocketTimeoutException, HttpStatusCodeUnknown {
        // Arrange
        MultivaluedMap<String, Object> headers = new MultivaluedHashMap<>();
        headers.add("api-key", "1234567");
        MultivaluedMap<String, Object> queries = new MultivaluedHashMap<>();
        queries.add("QUERY1", "AWESOME-QUERY-1");
        queries.add("QUERY2", "AWESOME-QUERY-2");
        Mockito.when(webTargetMock.queryParam("QUERY1","AWESOME-QUERY-1")).thenReturn(webTargetMock);
        Mockito.when(webTargetMock.queryParam("QUERY2","AWESOME-QUERY-2")).thenReturn(webTargetMock);
        Mockito.when(responseMock.getStatus()).thenReturn(200);
        Mockito.when(responseMock.readEntity(String.class)).thenReturn(validJsonResponse);
        RestClient restClient = new RestClient(clientMock);
        // Act
        restClient.request(HttpRequestMethods.GET, "example.com", "/json", headers, queries);
        // Assert
        Assertions.assertAll(
                () -> Mockito.verify(clientMock).target("example.com"),
                () -> Mockito.verify(webTargetMock).path("/json"),
                // Verify that the query parameters were added
                () -> Mockito.verify(webTargetMock).queryParam("QUERY1", "AWESOME-QUERY-1"),
                () -> Mockito.verify(webTargetMock).queryParam("QUERY2", "AWESOME-QUERY-2"),
                // Verify that headers were passed correctly
                () -> Mockito.verify(builderMock).headers(headers),
                // Verify the final get request
                () -> Mockito.verify(builderMock).get()
        );
    }

}
