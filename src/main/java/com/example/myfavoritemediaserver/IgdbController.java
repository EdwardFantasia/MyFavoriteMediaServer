package com.example.myfavoritemediaserver;
import jakarta.annotation.PostConstruct;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.json.*;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;

@RestController
@RequestMapping("/igdbController")
public class IgdbController {
    private final RestClient igdbRestClient;

    public IgdbController(RestClient igdbRestClient) { //NOTE: igdbRestClient(@Bean) from IgdbConfig(@Config) is injected into the IgdbController
        this.igdbRestClient = igdbRestClient;
    }

    @GetMapping("/searchGames")
    public String searchGames(@RequestParam(value = "searchQuery", defaultValue = "") String searchQuery) {
        System.out.print("searchQuery: " + searchQuery + "\n");
        String fieldsString = "fields id, name, cover.url, artworks.url";
        String whereString = "where (name ~ *\"%s\"* | summary ~ *\"%s\"* | alternative_names.name ~ *\"%s\"*)".formatted(searchQuery, searchQuery, searchQuery); //TODO: include game_localizations.name
        String gameTypeString = "(0, 2, 4, 6, 8, 10)";
        String sortParam = "total_rating_count";
        int limitNum = 100;
        String searchString = "%s;%s & version_parent = null & game_type = %s; sort %s desc; limit %d;".formatted(fieldsString, whereString, gameTypeString, sortParam, limitNum);
        String resp = this.igdbRestClient
                .post()
                .uri("/games")
                .body(searchString)
                .retrieve()
                .body(String.class);
        //System.out.println("resp: " + resp);
        return resp;
    }

    @GetMapping("/searchGameById")
    public String searchGameById(@RequestParam(value = "id", defaultValue = "") String id) {
        try {
            System.out.print("id: " + id + "\n");
            String fieldsString = "fields *, age_ratings.rating_cover_url, age_ratings.synopsis, artworks.url, collections.name, franchises.name, external_games.name, " +
                    "game_engines.name, game_localizations.name, game_modes.name, game_type.type, genres.name, " +
                    "involved_companies.company.name, involved_companies.developer, involved_companies.porting, " +
                    "involved_companies.publisher, involved_companies.supporting, keywords.name, " +
                    "language_supports.language.name, platforms.name, player_perspectives.name, ports.name, themes.name," +
                    "involved_companies.company.logo.image_id, remakes.name, remakes.cover.url, screenshots.image_id," +
                    "screenshots.url, similar_games.name, similar_games.cover.url, videos.video_id, websites.url," +
                    "bundles.name, dlcs.name, expanded_games.name, expansions.name, multiplayer_modes.platform.name, " +
                    "multiplayer_modes.campaigncoop, multiplayer_modes.dropin, multiplayer_modes.lancoop, multiplayer_modes.offlinecoop," +
                    "multiplayer_modes.offlinecoopmax, multiplayer_modes.onlinemax, multiplayer_modes.splitscreen, multiplayer_modes.splitscreenonline";
            String whereString = "where id = %s".formatted(id);
            String searchString = "%s;%s;".formatted(fieldsString, whereString);
            System.out.println("searchString: " + searchString);
            String resp = this.igdbRestClient
                    .post()
                    .uri("/games")
                    .body(searchString)
                    .retrieve()
                    .body(String.class);
            //System.out.println("resp: " + resp);
            return resp;
        } catch (Exception e) {
            System.out.println("/searchGameById error: " + e);
            throw new RuntimeException(e);
        }
    }

    //TODO: include POST with a custom search

    @GetMapping("/api/greet")
    public String greetUser(@RequestParam(value = "name", defaultValue = "Guest") String name) {
        return "Welcome, " + name + "!";
    }
}