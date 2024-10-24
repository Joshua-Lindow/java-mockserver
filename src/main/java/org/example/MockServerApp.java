package org.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockserver.integration.ClientAndServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.model.Parameter.param;

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
                        .withPath("/api/example/{key}")
                        .withPathParameters(param("key", "[a-zA-Z0-9\\-]+"))
                )
                .respond(request -> {
                        String key = request.getFirstPathParameter("key");
                        String value = memory.get(key);
                        if (value != null) {
                            return response()
                                    .withStatusCode(200)
                                    .withBody("Found: " + key + ":" + value);
                        } else {
                            return response()
                                    .withStatusCode(404)
                                    .withReasonPhrase("Not Found")
                                    .withBody("Did not find value for " + key);
                        }
                    }
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
