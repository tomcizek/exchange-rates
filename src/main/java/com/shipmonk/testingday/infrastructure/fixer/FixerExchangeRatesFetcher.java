package com.shipmonk.testingday.infrastructure.fixer;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

@Service
public class FixerExchangeRatesFetcher {

    private final HttpClient client;

    public FixerExchangeRatesFetcher(HttpClient client) {
        this.client = client;
    }

    public JSONObject fetchRates(String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return new JSONObject(response.body());
    }

}
