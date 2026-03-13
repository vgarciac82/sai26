package com.draggerco.pdf.test;


import java.io.FileOutputStream;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;


public class InsertImagePDF {

	public static void main( String[] args ) {
		InsertImagePDF insertimg = new InsertImagePDF();
		insertimg.insertImagePDF( "C:\\Vicente\\Solicitud de Pago Firmada.pdf", "C:\\Vicente\\censura.jpg", 100, 300);
	}

	public void insertImagePDF( String urlPDF, String urlImagen, float x, float y ) {
		Document documento = new Document();
		try {
			PdfWriter.getInstance( documento, new FileOutputStream( urlPDF ) );
			documento.open();
			Image imagen = Image.getInstance( urlImagen );
			 imagen.scaleAbsoluteWidth(100f);
			 imagen.scaleAbsoluteHeight(150f);
			imagen.setAbsolutePosition( x, y );

			documento.add( imagen );
			
		} catch ( DocumentException ex ) {
			ex.printStackTrace();
		} catch ( java.io.IOException ex ) {
			ex.printStackTrace();
		} finally {
			if ( documento != null ) {
				documento.close();
			}
			documento = null;
		}
	}

}
