package com.draggerco.pdf.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;

public class MergePdf {

	public static void join(List<File> files) throws IOException, DocumentException {
		OutputStream outputStream = new FileOutputStream( new File("/PDF/merge-pdf-result.pdf") );
		Document document = new Document();
		PdfWriter writer = PdfWriter.getInstance(document, outputStream);
		document.open();
		PdfContentByte cb = writer.getDirectContent();

		for (File file : files) {
			PdfReader reader = new PdfReader(new FileInputStream(file));
			for (int i = 1; i <= reader.getNumberOfPages(); i++) {
				document.newPage();
				// import the page from source pdf
				PdfImportedPage page = writer.getImportedPage(reader, i);
				// add the page to the destination pdf
				cb.addTemplate(page, 0, 0);
			}
		}

		outputStream.flush();
		document.close();
		outputStream.close();
	}

	public static void main(String[] args) throws IOException, DocumentException {
		File f = new File("/PDF");
		List<File> files = Arrays.asList((f.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File parent, String file) {
				return file.endsWith(".pdf");
			}
		})));

		MergePdf.join(files);
	}
}