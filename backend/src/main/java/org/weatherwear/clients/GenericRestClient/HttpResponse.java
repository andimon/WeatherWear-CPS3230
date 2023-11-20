package org.weatherwear.clients.GenericRestClient;

public class HttpResponse {
    private final int statusCode;
    private final String responseBody;

    public HttpResponse(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getResponseBody() {
        return responseBody;
    }
}
