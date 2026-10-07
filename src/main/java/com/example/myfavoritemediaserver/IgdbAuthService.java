package com.example.myfavoritemediaserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Instant;

@Service
public class IgdbAuthService { //NOTE: instantiated on startup as a singleton
    private final RestClient authClient;
    private final String clientId;
    private final String clientSecret;
    private final String authUrl;
    private String bearerToken;
    private Instant tokenExpiration;

    public IgdbAuthService(
            @Value("${IGDB_CLIENT_ID}") String clientId,
            @Value("${IGDB_CLIENT_SECRET}") String clientSecret,
            @Value("${IGDB_AUTH_URL}") String authUrl)
    {
        this.authClient = RestClient.create();
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.authUrl = authUrl;
    }

    public synchronized String getAccessToken() {
        if (bearerToken != null && tokenExpiration != null && Instant.now().isBefore(tokenExpiration.minusSeconds(60))) {
            return bearerToken;
        }

        TwitchTokenResponse response = authClient.post()
                .uri(authUrl + "?client_id={id}&client_secret={secret}&grant_type=client_credentials",
                        clientId, clientSecret)
                .retrieve()
                .body(TwitchTokenResponse.class);

        if (response != null) {
            this.bearerToken = response.accessToken();
            this.tokenExpiration = Instant.now().plusSeconds(response.expiresIn());
            System.out.print("Bearer token: " + bearerToken + "\n"); //TODO: remove this print
            return bearerToken;
        }

        throw new IllegalStateException("Failed to retrieve access token from Twitch OAuth");
    }

    private record TwitchTokenResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("expires_in") long expiresIn,
            @JsonProperty("token_type") String tokenType
    ) {}
}
