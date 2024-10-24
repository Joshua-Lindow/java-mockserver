package org.example;

import org.mockserver.integration.ClientAndServer;

import java.util.HashMap;
import java.util.Map;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerApp {
    private static ClientAndServer mockServer;
    private Map<String, String> memory = new HashMap<>();

    public static void main(String[] args) {
        mockServer = ClientAndServer.startClientAndServer(1080);
        setupExpectations();
    }

    private static void setupExpectations() {
        mockServer
                .when(
                        request()
                                .withMethod("GET")
                                .withPath("/api/example")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{\"message\": \"Hello, Mock Server!\"}")
                                .withHeader("Content-Type", "application/json")
                );

        mockServer
                .when(
                        request()
                                .withMethod("POST")
                                .withPath("/api/example")
                                .withBody("{\"key\":\"value\"}")
                )
                .respond(
                        response()
                                .withStatusCode(200)
                                .withBody("{\"message\": \"Received: {\\\"key\\\":\\\"value\\\"}\"}")
                                .withHeader("Content-Type", "application/json")
                );

    }
}
