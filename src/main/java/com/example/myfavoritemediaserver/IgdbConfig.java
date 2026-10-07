package com.example.myfavoritemediaserver;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class IgdbConfig {
    @Bean
    public RestClient igdbRestClient(IgdbAuthService authService, //NOTE: having authService in the constructor of the Bean inside the Configuration flags authService to be instantiated on startup
            @Value("${IGDB_CLIENT_ID}") String clientId,
            @Value("${IGDB_API_URL}") String baseUrl
    ){
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor((request, body, execution) -> {
                    String accessToken = authService.getAccessToken();

                    request.getHeaders().add("Client-ID", clientId);
                    request.getHeaders().setBearerAuth(accessToken);
                    request.getHeaders().add("Accept", "application/json");

                    return execution.execute(request, body);
                })
                .build();
    }
}
