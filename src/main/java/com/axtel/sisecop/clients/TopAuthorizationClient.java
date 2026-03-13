package com.axtel.sisecop.clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import org.apache.log4j.LogManager;
import com.axtel.sisecop.dto.TopAuthorizationDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TopAuthorizationClient {

    private String baseURL;

    private static final Logger log = LogManager.getLogger(TopAuthorizationClient.class);

    public TopAuthorizationClient(String baseURL) {
        this.baseURL = baseURL;
        log.info("TopAuthorizationClient initialized with base URL: " + baseURL);
    }

    public List<TopAuthorizationDTO> getTopAuthorizations(String unitName) {
        log.debug("Entering getTopAuthorizations with unitName: " + unitName);
        try {
            HttpURLConnection connection = createConnection(unitName);
            log.info("Connection created successfully for unit: " + unitName);
            List<TopAuthorizationDTO> result = parseResponse(connection);
            log.debug("Response parsed successfully for unit: " + unitName);
            return result;
        } catch (IOException e) {
            log.error("Error consuming the service: " + e.getMessage(), e);
            throw new RuntimeException("Error consuming the service: " + e.getMessage(), e);
        }
    }

    private HttpURLConnection createConnection(String unitName) throws IOException {
        log.trace("Creating connection for unit: " + unitName);
        URL url = new URL(baseURL + unitName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        int responseCode = connection.getResponseCode();
        log.debug("HTTP response code received: " + responseCode);
        if (responseCode != HttpURLConnection.HTTP_OK) {
            log.error("Failed: HTTP error code: " + responseCode);
            throw new RuntimeException("Failed: HTTP error code: " + responseCode);
        }
        return connection;
    }

    private List<TopAuthorizationDTO> parseResponse(HttpURLConnection connection) throws IOException {
        log.trace("Parsing response from connection: " + connection);
        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
                log.trace("Reading line: " + line);
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
