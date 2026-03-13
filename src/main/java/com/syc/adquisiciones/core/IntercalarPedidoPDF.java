/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.syc.adquisiciones.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Victor
 */
public class IntercalarPedidoPDF {

    private String rutaFilePDF1;

    private String rutaFilePDF2;

    private String rutaFileIntercalado;

    private Boolean peidoModificado = Boolean.FALSE;

    private static Logger log = LoggerFactory.getLogger(IntercalarPedidoPDF.class);

    public IntercalarPedidoPDF(String rutaFilePDF1, String rutaFilePDF2, String rutaFileIntercalado) {
        this.rutaFilePDF1 = rutaFilePDF1;
        this.rutaFilePDF2 = rutaFilePDF2;
        this.rutaFileIntercalado = rutaFileIntercalado;
    }

    public void setRutaPDF1(String rutaFilePDF1) {
        this.rutaFilePDF1 = rutaFilePDF1;
    }

    public String getRutaPDF1() {
        return this.rutaFilePDF1;
    }

    public void setRutaPDF2(String rutaFilePDF2) {
        this.rutaFilePDF2 = rutaFilePDF2;
    }

    public String getRutaPDF2() {
        return this.rutaFilePDF2;
    }

    public void setRutaFileIntercalado(String rutaFileIntercalado) {
        this.rutaFileIntercalado = rutaFileIntercalado;
    }

    public String getRutaFileIntercalado() {
        return this.rutaFileIntercalado;
    }

    public Boolean getPeidoModificado() {
        return peidoModificado;
    }

    public void setPeidoModificado(Boolean peidoModificado) {
        this.peidoModificado = peidoModificado;
    }

    /*Verifica que existan los archivos PDF que se van a intercalar e INICIA LA OPERACION
     * @archivoPDF1 = ruta del archivo pdf que contiene los detalles del pedido
     * @archivoPDF2 = ruta del archivo pdf que contiene el clausulado y los firmantes
     * @pdfIntercalado = ruta donde se guardara temporalmente el pdfIntercalado
     */
    public void execute() {
        try {
            List<InputStream> pdf1 = new ArrayList<InputStream>();
            List<InputStream> pdf2 = new ArrayList<InputStream>();
            deleteFile(getRutaFileIntercalado());
            if (new File(getRutaPDF1()).exists()) {
                pdf1.add(new FileInputStream(getRutaPDF1()));
                if (new File(getRutaPDF2()).exists()) {
                    pdf2.add(new FileInputStream(getRutaPDF2()));
                    OutputStream output = new FileOutputStream(getRutaFileIntercalado());
                    intercalarPDFs(pdf1, pdf2, output, true);
                } else {
                    log.info("El archivo " + getRutaPDF2() + " no existe");
                }
            } else {
                log.info("El archivo " + getRutaPDF1() + " no existe");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
      * Inicia el intercalado de los archivos PDF
      */
    protected void intercalarPDFs(List<InputStream> isPDF1, List<InputStream> isPDF2, OutputStream pdfUnido, boolean paginate) {
        Document document = new Document();
        int paginasPDF = 0, paginasPDF1 = 0, paginasPDF2 = 0;
        String archivo = "PDF1";
        boolean terminate = false;
        try {
            List<PdfReader> readersPDF1 = getReaders(isPDF1.iterator());
            List<PdfReader> readersPDF2 = getReaders(isPDF2.iterator());
            PdfWriter writer = PdfWriter.getInstance(document, pdfUnido);
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfImportedPage page;
            Iterator<PdfReader> iteratorPDF1Reader = readersPDF1.iterator();
            Iterator<PdfReader> iteratorPDF2Reader = readersPDF2.iterator();
            PdfReader pdfReader = null, pdfReader1 = iteratorPDF1Reader.next(), pdfReader2 = iteratorPDF2Reader.next();
            do {
                if (archivo.toUpperCase().equals("PDF1")) {
                    pdfReader = pdfReader1;
                    if (paginasPDF1 < pdfReader1.getNumberOfPages()) {
                        paginasPDF1++;
                        paginasPDF = paginasPDF1;
                        archivo = "PDF2";
                    } else {
                        break;
                    }
                } else if (archivo.toUpperCase().equals("PDF2")) {
                    pdfReader = pdfReader2;
                    if (paginasPDF1 == pdfReader1.getNumberOfPages()) {
                        paginasPDF2 = 2;
                    } else /*if(paginasPDF1 == 1 || this.peidoModificado){//Imprime Clausulado
                    	log.debug("mostrando representantes");
                        //paginasPDF2 = 1;
                        paginasPDF2 = 2;
                    }*/
                    {
                        //Imprime Firmantes
                        //paginasPDF2 = 2;
                        paginasPDF2 = 1;
                    }
                    archivo = "PDF1";
                    paginasPDF = paginasPDF2;
                }
                Rectangle rectangle = pdfReader.getPageSizeWithRotation(1);
                document.setPageSize(rectangle);
                document.newPage();
                page = writer.getImportedPage(pdfReader, paginasPDF);
                switch(rectangle.getRotation()) {
                    case 0:
                        cb.addTemplate(page, 1f, 0, 0, 1f, 0, 0);
                        break;
                    case 90:
                        cb.addTemplate(page, 0, -1f, 1f, 0, 0, pdfReader.getPageSizeWithRotation(1).getHeight());
                        break;
                    case 180:
                        cb.addTemplate(page, -1f, 0, 0, -1f, 0, 0);
                        break;
                    case 270:
                        cb.addTemplate(page, 0, 1.0F, -1.0F, 0, pdfReader.getPageSizeWithRotation(1).getWidth(), 0);
                        break;
                    default:
                        break;
                }
                if (paginate) {
                    cb.beginText();
                    cb.getPdfDocument().getPageSize();
                    cb.endText();
                }
            } while (!terminate);
            //writer.flush();
            pdfUnido.flush();
            //writer.close();
            document.close();
            pdfUnido.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (document.isOpen())
                document.close();
            try {
                if (pdfUnido != null)
                    pdfUnido.close();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    /*
     * Elimina el archivo de una ruta especificada
     * @rutaFile = ruta del archivo a eliminar
     */
    public Boolean deleteFile(String rutaFile) {
        try {
            if (new File(rutaFile).exists()) {
                if (new File(rutaFile).delete()) {
                    log.info("El archivo " + rutaFile + " se elimino correctamente");
                }
            }
            return Boolean.TRUE;
        } catch (Exception e) {
            log.error("Ocurrio un error al borrar el archivo " + rutaFile);
            e.printStackTrace();
            return Boolean.FALSE;
        }
    }

    /* 
     */
    protected List<PdfReader> getReaders(Iterator<InputStream> iteratorPDF) {
        List<PdfReader> readers = new ArrayList<PdfReader>();
        try {
            while (iteratorPDF.hasNext()) {
                InputStream pdf = iteratorPDF.next();
                PdfReader pdfReader = new PdfReader(pdf);
                readers.add(pdfReader);
            }
        } catch (IOException io) {
            io.printStackTrace();
        }
        return readers;
    }
}
