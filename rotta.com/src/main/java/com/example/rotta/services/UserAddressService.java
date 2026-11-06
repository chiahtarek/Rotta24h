/*package com.example.rotta.services;

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
}*/


package com.example.rotta.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class UserAddressService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    private final ConcurrentMap<String, CachedAddress> cache =
            new ConcurrentHashMap<>();

    private static final long CACHE_TTL = 30 * 60 * 1000;
    private static final long FAILURE_TTL = 2 * 60 * 1000;

    private volatile long rateLimitUntil = 0;

    public UserAddressService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .baseUrl("https://nominatim.openstreetmap.org")
                .defaultHeader(
                        "User-Agent",
                        "Rotta24h/1.0 (chiahtarek@gmail.com)"
                )
                .build();
    }

    public String getUserAddress(double lat, double lng) {

        String key = createCacheKey(lat, lng);

        CachedAddress cached = cache.get(key);

        if (cached != null && !cached.isExpired()) {
            return cached.address();
        }

        if (cached != null) {
            cache.remove(key, cached);
        }

        // Não consultar a API durante o período de rate limit.
        if (System.currentTimeMillis() < rateLimitUntil) {
            return null;
        }

        try {

            String body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reverse")
                            .queryParam("format", "json")
                            .queryParam("lat", lat)
                            .queryParam("lon", lng)
                            .queryParam("addressdetails", 1)
                            .queryParam("accept-language", "pt-BR")
                            .build())
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 429,
                            (request, response) -> {
                                rateLimitUntil =
                                        System.currentTimeMillis()
                                        + 60_000;
                            }
                    )
                    .body(String.class);

            if (body == null || body.isBlank()) {
                cacheFailure(key);
                return null;
            }

            JsonNode node = objectMapper.readTree(body);

            String address = node.path("display_name")
                    .asText(null);

            if (address == null || address.isBlank()) {
                cacheFailure(key);
                return null;
            }

            cache.put(
                    key,
                    new CachedAddress(
                            address,
                            System.currentTimeMillis() + CACHE_TTL
                    )
            );

            return address;

        } catch (RestClientException e) {

            cacheFailure(key);

            return null;

        } catch (Exception e) {

            cacheFailure(key);

            return null;
        }
    }

    private void cacheFailure(String key) {

        cache.put(
                key,
                new CachedAddress(
                        null,
                        System.currentTimeMillis() + FAILURE_TTL
                )
        );
    }

    private String createCacheKey(double lat, double lng) {

        // Aproximação de aproximadamente 11 metros.
        return String.format(
                java.util.Locale.ROOT,
                "%.4f:%.4f",
                lat,
                lng
        );
    }

    private record CachedAddress(
            String address,
            long expiresAt
    ) {

        boolean isExpired() {
            return System.currentTimeMillis() >= expiresAt;
        }
    }
}

