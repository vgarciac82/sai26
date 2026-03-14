package com.draggerco.pdf.test;

import java.io.FileOutputStream;
import java.io.IOException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import java.util.Base64;

public class PDFDraw {

    public static void main(String[] args) throws Exception {
        PDFDraw drawer = new PDFDraw();
        drawer.drawRect("c:\\Vicente\\Solicitud de Pago Firmada.pdf");
    }

    public void drawRect(String fileIn) throws DocumentException, IOException {
        PdfReader reader = new PdfReader(fileIn);
        Rectangle pagesize = reader.getPageSizeWithRotation(1);
        // step 1
        Document document = new Document(pagesize);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("c:\\Vicente\\salida.pdf"));
        // step 3
        document.open();
        // step 4
        PdfContentByte canvas = writer.getDirectContent();
        PdfImportedPage page = writer.getImportedPage(reader, 1);
        canvas.addTemplate(page, 0, 0);
        // adding the same page 16 times with a different offset
        drawRectangle(canvas, 20, 100);
        // step 4
        document.close();
        reader.close();
        /*PdfReader reader = new PdfReader( new FileInputStream( fileIn ) );
		PdfStamper stamper = new PdfStamper( reader, new FileOutputStream( "c:\\Vicente\\salida.pdf" ) );
		PdfContentByte canvas = stamper.getOverContent( 1 );
		
		stamper.close();
		reader.close();
*/
    }

    public static void drawRectangle(PdfContentByte content, float width, float height) {
        content.saveState();
        PdfGState state = new PdfGState();
        state.setFillOpacity(0.6f);
        content.setGState(state);
        content.setRGBColorFill(0xFF, 0xFF, 0xFF);
        content.setLineWidth(3);
        content.rectangle(0, 0, width, height);
        content.fillStroke();
        content.restoreState();
    }
}
