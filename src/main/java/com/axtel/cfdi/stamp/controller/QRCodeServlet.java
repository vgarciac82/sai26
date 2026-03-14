package com.axtel.cfdi.stamp.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

@WebServlet("/invoice/generateQRCode")
public class QRCodeServlet extends HttpServlet {

    private static final long serialVersionUID = 9191361535677960155L;

    private static final Logger log = LoggerFactory.getLogger(QRCodeServlet.class.getName());

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uuid = request.getParameter("uuid");
        String rfcEmisor = request.getParameter("rfcEmisor");
        String rfcReceptor = request.getParameter("rfcReceptor");
        String totalParam = request.getParameter("total");
        String sello = request.getParameter("sello");
        log.info("Received request to generate QR code with parameters: ");
        log.info("Object: {}", "UUID: " + uuid);
        log.info("Object: {}", "RFC Emisor: " + rfcEmisor);
        log.info("Object: {}", "RFC Receptor: " + rfcReceptor);
        log.info("Object: {}", "Total: " + totalParam);
        log.info("Object: {}", "Sello: " + sello);
        try {
            BigDecimal total = new BigDecimal(totalParam);
            String qrContent = generateQRContent(uuid, rfcEmisor, rfcReceptor, total, sello);
            log.info("Object: {}", "Generated QR content: " + qrContent);
            BitMatrix qrCodeMatrix = generateQRCodeMatrix(qrContent);
            log.info("QR code matrix generated successfully.");
            response.setContentType("image/png");
            try (ServletOutputStream outputStream = response.getOutputStream()) {
                MatrixToImageWriter.writeToStream(qrCodeMatrix, "PNG", outputStream);
                log.info("QR code image written to response output stream successfully.");
            }
        } catch (Exception e) {
            log.error("Error occurred", "Error generating QR code: " + e.getMessage());
            throw new ServletException("Error generating QR code", e);
        }
    }

    private String generateQRContent(String uuid, String rfcEmisor, String rfcReceptor, BigDecimal total, String sello) {
        String totalFormatted = String.format("%.2f", total);
        String selloFormatted = sello.length() > 8 ? sello.substring(0, 8) : sello;
        return String.format("https://verificacfdi.facturaelectronica.sat.gob.mx/default.aspx?id=%s&re=%s&rr=%s&tt=%s&fe=%s", uuid, rfcEmisor, rfcReceptor, totalFormatted, selloFormatted);
    }

    private BitMatrix generateQRCodeMatrix(String qrContent) throws Exception {
        int width = 250;
        int height = 250;
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, width, height, hints);
    }
}
