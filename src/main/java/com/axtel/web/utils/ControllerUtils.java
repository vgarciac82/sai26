package com.axtel.web.utils;

import java.io.BufferedReader;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;

public class ControllerUtils {

    public static String readJsonFromRequest(HttpServletRequest request) throws IOException {
        request.setCharacterEncoding("UTF-8");
        StringBuilder jsonBuffer = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuffer.append(line);
            }
        }
        return jsonBuffer.toString();
    }
}
