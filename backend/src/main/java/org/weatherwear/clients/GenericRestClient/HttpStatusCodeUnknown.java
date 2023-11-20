package org.weatherwear.clients.GenericRestClient;

public class HttpStatusCodeUnknown extends Exception {
    public HttpStatusCodeUnknown(String errorMessage) {
        super(errorMessage);
    }
}