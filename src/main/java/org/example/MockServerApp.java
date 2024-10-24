package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.integration.ClientAndServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

public class MockServerApp {
    private static ClientAndServer mockServer;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static Map<String, String> memory = new HashMap<>();


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
                )
                .respond( request -> {
                        try {
                            JsonNode jsonNode = objectMapper.readTree(request.getBodyAsString());
                            jsonNode.fieldNames().forEachRemaining(field -> {
                                String value = jsonNode.get(field).asText();
                                memory.put(field, value);
                            });
                            System.out.println(memory.toString());
                            return response()
                                    .withStatusCode(200)
                                    .withBody("Added to memory");
                        } catch (IOException e){
                            return response()
                                    .withStatusCode(500)
                                    .withBody("Failed with errorMessage: " + e.getMessage());
                        }
                    }
                );

    }
}
