package org.weatherwear.clients.GenericRestClient;

import jakarta.ws.rs.core.MultivaluedMap;

import java.net.SocketTimeoutException;

public interface IRestClient {
    HttpResponse request(HttpRequestMethods verb, String target, String path) throws SocketTimeoutException, HttpStatusCodeUnknown;

    HttpResponse request(HttpRequestMethods verb, String target, String path, MultivaluedMap<String, Object> headers, MultivaluedMap<String, Object> queryParams) throws SocketTimeoutException, HttpStatusCodeUnknown;
}
