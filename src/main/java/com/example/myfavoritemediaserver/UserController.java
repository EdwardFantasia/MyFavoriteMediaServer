package com.example.myfavoritemediaserver;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RestController
@RequestMapping("/userController")
public class UserController {
    HttpClient client = HttpClient.newHttpClient();

    @GetMapping("/user")
    public String getUser(@RequestParam(value = "id") String id) {
        return "Welcome, " + id + "!";
    }

    @GetMapping("/")
    public String getBase() {
        try {
            return "Hello world!";
        } catch (Exception e) {
            return "World exploded";
        }
    }
}
