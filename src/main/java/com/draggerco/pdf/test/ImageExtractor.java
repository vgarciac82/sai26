package com.draggerco.pdf.test;

import java.awt.image.BufferedImage;
import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import java.util.Base64;

public class ImageExtractor {

    /**
     * @param args
     */
    public static void main(String[] args) {
        ImageExtractor ie = new ImageExtractor();
        ie.extraImagen("c:\\Vicente\\Solicitud de Pago Firmada.pdf", "c:\\Vicente\\solpago.png");
    }

    public void extraImagen(String pdfPath, String imageOutput) {
        try {
            System.out.println("Iniciando");
            PDDocument document = PDDocument.load(new File(pdfPath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            BufferedImage image = pdfRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
            ImageIOUtil.writeImage(image, imageOutput, 300);
            System.out.println("Terminado.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
