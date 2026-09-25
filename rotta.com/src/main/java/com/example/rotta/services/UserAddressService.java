package com.example.rotta.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service 
public class UserAddressService {
    private final RestClient restClient = RestClient.create();

    public String getUserAddress(double lat, double lng) {
        try {
            String body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                    .scheme("https").host("nominatim.openstreetmap.org").path("/reverse")
                    .queryParam("format", "json")
                    .queryParam("lat", lat)
                    .queryParam("lon", lng)
                    .queryParam("addressdetails", 1)
                    .queryParam("accept-language", "pt-BR")
                    .build())
                    .header("User-Agent", "Rotta24h/1.0 (chiahtarek@gmail.com)") // Nominatim REQUIRES this
                    .retrieve()
                    .body(String.class);

            JsonNode node = new ObjectMapper().readTree(body);
            return node.path("display_name").asText(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null; // never let geocoding break location updates
        }
    }
}
