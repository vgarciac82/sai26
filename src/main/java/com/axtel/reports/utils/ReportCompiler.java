package com.axtel.reports.utils;

import java.io.File;
import java.io.FileFilter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.syc.gestion.util.Util;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

public class ReportCompiler {

	private static final Logger log = LogManager.getLogger( ReportCompiler.class );
	public static void main(String[] args) throws Exception {

		compileFile( new File("C:\\Axtel\\Clientes\\CONAFOR\\Workspaces\\Desarrollo\\sai\\WebContent\\Reportes\\InvoiceDetail.jrxml") );
//		for (String f : args) {
//			File param = new File(f);
//			if (param.isDirectory()) {
//				compile(param);
//			} else
//				compileFile(param);
//		}
	}

	private static void compileFile(File param) throws JRException {
		JasperCompileManager.compileReportToFile(param.getAbsolutePath(),
				Util.getFileWithoutExtencion(param.getAbsolutePath()) + ".jasper");
			 
	}

	public static void compile(File dir)   {
		if (!dir.isDirectory() || !dir.exists())
			throw new RuntimeException("El archivo de entrada debe ser un directorio y debe existir");

		System.out.println("Procesando directorio: " + dir.getAbsolutePath());
		File[] files = dir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File pathname) {
				if (pathname.isDirectory() || pathname.getName().endsWith(".jrxml"))
					return true;
				else
					return false;
			}
		});

		for (File f : files) {
			if (f.isDirectory())
				compile(f);
			else {
				System.out.println("Compilando archivo: " + f.getAbsolutePath());
				try {
				JasperCompileManager.compileReportToFile(f.getAbsolutePath(),
						Util.getFileWithoutExtencion(f.getAbsolutePath()) + ".jasper");
				} catch (JRException e) {
					System.err.println("============================================== ERROR!!!! ===============================================");
					
					log.error("Error compilando: " + f.getAbsolutePath());
					System.err.println("========================================================================================================");
				}
			}
		}
	}
}
