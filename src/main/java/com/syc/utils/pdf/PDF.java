package com.syc.utils.pdf;

import java.io.File;
import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.util.Base64;

public class PDF {

    PDDocument pdDocument;

    private static Log log = LogFactory.getLog(PDF.class);

    public PDF() {
    }

    public PDF load(String path) throws IOException {
        pdDocument = PDDocument.load(new File(path));
        return this;
    }

    public String extractText(int startPage, int endPage) throws IOException {
        PDFTextStripper stripper = new PDFTextStripper();
        stripper.setStartPage(startPage);
        stripper.setEndPage(endPage);
        String text = stripper.getText(pdDocument);
        return text;
    }

    public void close() {
        try {
            pdDocument.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
