package com.axtel.sisecop.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.axtel.sisecop.dto.ProductDTO;
import com.axtel.sisecop.entities.ProyectoProducto;
import com.axtel.sisecop.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syc.gestion.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet("/SISECOP/products")
public class ServiceProductController extends HttpServlet {

    private static final long serialVersionUID = 3064440578302135421L;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final Logger log = LoggerFactory.getLogger(ServiceProductController.class);

    private ProductService productService = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        productService = new ProductService();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ProductDTO product = readServicioProducto(request);
        log.trace("Object: {}", "JSON recibido correctamente y mapeado a objeto ServicioProductoDTO. " + product);
        try {
            log.debug("Object: {}", "Saving servicioProducto: " + product);
            ProyectoProducto createdProduct = productService.create(product);
            log.info("Object: {}", "ServicioProducto saved: " + createdProduct);
            Util.sendJSONResponse(response, createdProduct);
        } catch (Exception e) {
            log.error("Error saving servicioProducto: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int projectID = Integer.parseInt(request.getParameter("servicioId"));
            log.debug("Object: {}", "Looking for products in project: " + projectID);
            List<ProyectoProducto> products = productService.readByProjectID(projectID);
            Util.sendJSONResponse(response, products);
        } catch (Exception e) {
            log.error("Error saving servicioProducto: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int servicioProductoId = Integer.parseInt(request.getParameter("servicioProductoId"));
            log.info("Object: {}", "Trying to delete servicioProducto " + servicioProductoId);
            productService.deleteServicioProducto(servicioProductoId);
            log.info("Object: {}", "ServicioProducto " + servicioProductoId + " deleted");
            Map<String, String> result = new HashMap<>();
            result.put("deleted", "true");
            result.put("success", "true");
            Util.sendJSONResponse(response, result);
        } catch (Exception e) {
            log.error("Error deleting servicioProducto: " + e.toString(), e);
            Util.sendJSONError(response, e);
        }
    }

    private ProductDTO readServicioProducto(HttpServletRequest request) throws IOException {
        final StringBuilder jsonRequest = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonRequest.append(line);
            }
        }
        return objectMapper.readValue(jsonRequest.toString(), ProductDTO.class);
    }
}
