package com.axtel.sisecop.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import com.axtel.sisecop.dto.TopAuthorizationDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class TopAuthorizationClient {

    private String baseURL;

    private static final Logger log = LoggerFactory.getLogger(TopAuthorizationClient.class);

    public TopAuthorizationClient(String baseURL) {
        this.baseURL = baseURL;
        log.info("Object: {}", "TopAuthorizationClient initialized with base URL: " + baseURL);
    }

    public List<TopAuthorizationDTO> getTopAuthorizations(String unitName) {
        log.debug("Object: " + String.valueOf("Entering getTopAuthorizations with unitName: " + unitName));
        try {
            HttpURLConnection connection = createConnection(unitName);
            log.info("Object: {}", "Connection created successfully for unit: " + unitName);
            List<TopAuthorizationDTO> result = parseResponse(connection);
            log.debug("Object: " + String.valueOf("Response parsed successfully for unit: " + unitName));
            return result;
        } catch (IOException e) {
            log.error("Error consuming the service: " + e.getMessage(), e);
            throw new RuntimeException("Error consuming the service: " + e.getMessage(), e);
        }
    }

    private HttpURLConnection createConnection(String unitName) throws IOException {
        log.trace("Object: {}", "Creating connection for unit: " + unitName);
        URL url = new URL(baseURL + unitName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        int responseCode = connection.getResponseCode();
        log.debug("Object: " + String.valueOf("HTTP response code received: " + responseCode));
        if (responseCode != HttpURLConnection.HTTP_OK) {
            log.error("Error occurred", "Failed: HTTP error code: " + responseCode);
            throw new RuntimeException("Failed: HTTP error code: " + responseCode);
        }
        return connection;
    }

    private List<TopAuthorizationDTO> parseResponse(HttpURLConnection connection) throws IOException {
        log.trace("Object: {}", "Parsing response from connection: " + connection);
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
                log.trace("Object: {}", "Reading line: " + line);
            }
        } finally {
            connection.disconnect();
            log.debug("Connection disconnected.");
        }
        ObjectMapper objectMapper = new ObjectMapper();
        log.debug("Deserializing response to List<TopAuthorizationDTO>");
        return objectMapper.readValue(response.toString(), new TypeReference<List<TopAuthorizationDTO>>() {
        });
    }
}
