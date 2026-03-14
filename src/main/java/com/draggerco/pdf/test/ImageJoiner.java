package com.draggerco.pdf.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class ImageJoiner {

    private static final Logger log = LoggerFactory.getLogger(ImageJoiner.class);

    public static String joinImageAndText(String filePath, String text) throws Exception {
        BufferedImage imgSrc = null;
        BufferedImage combined = null;
        File fOut = new File(filePath);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fOut);
            imgSrc = ImageIO.read(fis);
            int newWidth = imgSrc.getWidth();
            int newHeight = 235 + imgSrc.getHeight();
            List<String> strConcepto = new LinkedList<String>(Arrays.asList(("Pago por concepto de: " + text).split("\\s+")));
            combined = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = combined.createGraphics();
            g.setBackground(Color.WHITE);
            g.fillRect(0, 0, newWidth, newHeight);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            g.setColor(new Color(50, 50, 50));
            g.drawRect(170, 5, newWidth - 340, 235);
            g.drawRect(169, 5, newWidth - 340, 235);
            g.drawRect(171, 5, newWidth - 340, 235);
            // g.drawImage( image1, 0, 0, Color.WHITE, null );
            g.setColor(new Color(50, 50, 50));
            int xStr = 250;
            int yStr = 50;
            int maxLength = 134;
            int nRenglones = (int) (text.length() / maxLength);
            String cad = "";
            for (int i = 0; i < nRenglones; i++) {
                cad = generaRenglon(cad, strConcepto, maxLength);
                g.drawString(cad, xStr, yStr);
                cad = "";
                yStr += 27;
            }
            if (!strConcepto.isEmpty()) {
                cad = "";
                cad = generaRenglon(cad, strConcepto, maxLength);
                g.drawString(cad, xStr, yStr);
            }
            g.drawImage(imgSrc, 0, 250, Color.WHITE, null);
            ImageIO.write(combined, "jpg", new File(filePath));
            return filePath;
        } finally {
            ImageJoiner.closeInputStream(fis);
            imgSrc = null;
            combined = null;
            fOut = null;
        }
    }

    private static String generaRenglon(String cad, List<String> strConcepto, int maxLength) {
        String str = "";
        while (!strConcepto.isEmpty() && strConcepto.get(0).length() + 1 + str.length() <= maxLength) {
            str = str + (str.length() > 0 ? " " : "") + strConcepto.remove(0);
        }
        return str;
    }

    private static void closeInputStream(InputStream stream) {
        if (stream != null)
            try {
                stream.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                stream = null;
            }
    }
}
