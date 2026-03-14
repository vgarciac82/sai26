package com.axtel.web.clients;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import com.axtel.user.entities.Employee;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmployeeClient {

    private static final Logger log = LoggerFactory.getLogger(EmployeeClient.class);

    private final String basicAuth;

    private String password;

    private String serviceURL;

    private String userName;

    public EmployeeClient(String url, String userName, String code) throws UnsupportedEncodingException {
        this.serviceURL = url;
        this.userName = userName;
        this.password = code;
        basicAuth = "Basic " + Base64.getEncoder().encodeToString((getUserName() + ":" + getPassword()).getBytes("UTF-8"));
        log.info("Object: {}", "EmployeeClient inicializado con URL: " + url);
    }

    public Employee fetchEmployee(int employeeId) throws Exception {
        log.trace("Object: {}", "Iniciando fetchEmployee con id: " + employeeId);
        URL url = new URL(getServiceURL() + employeeId);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", basicAuth);
        connection.setRequestProperty("Accept", "application/json");
        int responseCode = connection.getResponseCode();
        log.debug("Object: {}", "Código de respuesta HTTP: " + responseCode);
        if (responseCode == 200) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                String json = response.toString();
                log.trace("Object: {}", "JSON recibido: " + json);
                Gson gson = new Gson();
                Employee employee = gson.fromJson(json, Employee.class);
                log.info("Object: {}", "Empleado recibido: " + employee.getName() + " " + employee.getFirstSurname());
                return employee;
            }
        } else {
            log.error("Error occurred", "Error HTTP al obtener empleado. Código: " + responseCode);
            throw new RuntimeException("Failed : HTTP error code : " + responseCode);
        }
    }

    private String getPassword() {
        return password;
    }

    private String getServiceURL() {
        return serviceURL;
    }

    private String getUserName() {
        return userName;
    }
}
