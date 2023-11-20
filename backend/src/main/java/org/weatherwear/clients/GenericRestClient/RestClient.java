package org.weatherwear.clients.GenericRestClient;


import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;


public class RestClient implements IRestClient {
    private final Client client;

    public RestClient(Client client) {
        this.client = client;
    }

    public HttpResponse request(HttpRequestMethods verb, String target, String path) throws HttpStatusCodeUnknown {
        MultivaluedMap<String, Object> emptyMap = new MultivaluedHashMap<>();
        return request(verb, target, path, emptyMap, emptyMap);
    }

    private void  checkIfResponseStatusIsIdentified(int status) throws HttpStatusCodeUnknown {
        //the system only handles responses having code 200
        //others are unknown to our system
        if(status!=200){
            throw new HttpStatusCodeUnknown("Code status "+status+" is unhandled by this implementation of REST CLIENT");

        }
    }

    public HttpResponse request(HttpRequestMethods verb, String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> queryParams) throws HttpStatusCodeUnknown {
        WebTarget webTarget = client.target(target).path(path);
        for (String queryParam : queryParams.keySet()) {
            webTarget = webTarget.queryParam(queryParam, queryParams.getFirst(queryParam));
        }
        Response response = webTarget.request().headers(headers).get();
        checkIfResponseStatusIsIdentified(response.getStatus());
        HttpResponse httpResponse = new HttpResponse(response.getStatus(), response.readEntity(String.class));
        response.getDate();
        return httpResponse;
    }

}
