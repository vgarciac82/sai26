package com.draggerco.pdf.test;

import java.io.File;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import java.util.Base64;

public class InsertingImage {

    public static void main(String[] args) throws Exception {
        //Loading an existing document
        File file = new File("C:\\Vicente\\Solicitud de Pago Firmada.pdf");
        PDDocument doc = PDDocument.load(file);
        //Retrieving the page
        PDPage page = doc.getPage(0);
        //Creating PDImageXObject object
        PDImageXObject pdImage = PDImageXObject.createFromFile("C:\\Vicente\\censura.jpg", doc);
        pdImage.setHeight(20);
        pdImage.setWidth(100);
        //creating the PDPageContentStream object
        PDPageContentStream contents = new PDPageContentStream(doc, page, true, true);
        //Drawing the image in the PDF document
        contents.drawImage(pdImage, 70, 522);
        System.out.println("Image inserted");
        //Closing the PDPageContentStream object
        contents.close();
        //Saving the document
        doc.save("C:\\Vicente\\Solicitud de Pago Firmada.pdf");
        //Closing the document
        doc.close();
    }
}
