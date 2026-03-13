package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.ejercido.pagado.Anexo1BusinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class LayoutAnexo1Servlet extends HttpServlet {
	
	private String				CualArchivo			= "";
	private static String		folioGenerator;
	private static final long	serialVersionUID	= 1L;
	private static final Logger log = Logger.getLogger(LayoutAnexo1Servlet.class);

	public LayoutAnexo1Servlet() {
		super();
	}

	public void destroy() {
		super.destroy(); 
	}

	public void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		CualArchivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String archivo = (request.getParameter("archivo") != null) ? request.getParameter("archivo").trim() : "";
		if (archivo == null) {
			throw new ServletException("El archivo no debe ir nulo");
		}

		HttpSession session = request.getSession(false);
		Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

		//String sUsuario = usuario.getLogin();

		String sFolio = (request.getParameter("sDataH") != null) ? request.getParameter("sDataH").trim() : "";
		String sCuentaBancaria = (request.getParameter("sDataHCB") != null) ? request.getParameter("sDataHCB").trim() : "";
		String sFecha = (request.getParameter("sDataHFecha") != null) ? request.getParameter("sDataHFecha").trim() : "";
		String sLeyenda = (request.getParameter("sDataHLeyenda") != null) ? request.getParameter("sDataHLeyenda").trim() : "";

		int valor = sFolio.length() - 1;
		String sFolioQuery = sFolio.substring(0, valor);
		ArrayList<String> arrListPago = null;
		ArrayList<String> arrListDocu = null;
		Anexo1BusinessLogic anexoBL = new Anexo1BusinessLogic(GestionInterface.ATT_CONEXION, folioGenerator);
		
		try {
			try {
				SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");
				String sufijo = fecha.format(new Date(System.currentTimeMillis()));

				String[] arrCuentasBancarias = sCuentaBancaria.split(",");
				String sREFERENCIA1_107 = new java.text.SimpleDateFormat("yyMMddhhmmss").format(new java.util.Date());

				sREFERENCIA1_107 = "A_" + anexoBL.getUE(arrCuentasBancarias[0].trim()) + sREFERENCIA1_107;

				arrListPago = anexoBL.buscaAnexo1Integrados(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, usuario, sREFERENCIA1_107);//SP
				arrListDocu = anexoBL.ArmaDocumentoComprobatorio(sFolioQuery, sREFERENCIA1_107, archivo.equals("1"));//DC

				//Long date = System.currentTimeMillis();
				String layoutPago 	 = "SP_ANX_" 	 + sufijo.trim() + ".csv"; // para dar el nombre del archivo Pago Directo
				String layoutPagoZip = "SP_ANX_" 	 + sufijo.trim() + ".zip"; // para dar el nombre del archivo Pago Directo ZIP
				String layoutDocu 	 = "DC_ANX_" 	 + sufijo.trim() + ".csv"; // para dar el nombre del archivo Documentación Comprobatoria
				String layoutDocuZip = "DC_ANX_" 	 + sufijo.trim() + ".zip"; // para dar el nombre del archivo Documentación Comprobatoria ZIP
				String layoutZip 	 = "ANXTodo_" + sufijo 		 + ".zip";

				BufferedWriter out = new BufferedWriter(new FileWriter(layoutPago)); // Guarda el pago
				StringBuffer archivoPago = new StringBuffer();
				for (int i = 0; i < arrListPago.size(); i++) {
					archivoPago.append(arrListPago.get(i));
				}
				String outTextPago = archivoPago.toString();
				out.write(outTextPago);
				out.close(); // fin de guarda pago

				BufferedWriter outDocu = new BufferedWriter(new FileWriter(layoutDocu)); // Guarda el documento
				StringBuffer archivoDocu = new StringBuffer();
				for (int i = 0; i < arrListDocu.size(); i++) {
					archivoDocu.append(arrListDocu.get(i));
				}
				String outTextDocu = archivoDocu.toString();
				outDocu.write(outTextDocu);
				outDocu.close(); // fin de guarda documento

				byte[] bufPag = new byte[1024]; // Guarda el pago Zip
				try {
					ZipOutputStream outPag = new ZipOutputStream(new FileOutputStream(layoutPagoZip));
					FileInputStream inPag = new FileInputStream(layoutPago);
					outPag.putNextEntry(new ZipEntry(layoutPago));

					int lenPag;
					while ((lenPag = inPag.read(bufPag)) > 0) {
						outPag.write(bufPag, 0, lenPag);
					}

					outPag.closeEntry();
					inPag.close();
					outPag.close();
				} catch (IOException e) {
				}

				byte[] bufDoc = new byte[1024]; // Guarda el Doc Zip
				try {
					ZipOutputStream outDoc = new ZipOutputStream(new FileOutputStream(layoutDocuZip));
					FileInputStream inDoc = new FileInputStream(layoutDocu);
					outDoc.putNextEntry(new ZipEntry(layoutDocu));

					int lenPag;
					while ((lenPag = inDoc.read(bufDoc)) > 0) {
						outDoc.write(bufDoc, 0, lenPag);
					}

					outDoc.closeEntry();
					inDoc.close();
					outDoc.close();
				} catch (IOException e) {
				}

				String[] filenames = new String[] { layoutPago, layoutDocu }; // These are the files to include in the ZIP file

				ServletOutputStream ouputStream;
				byte[] buf = new byte[1024]; // Create a buffer for reading the files

				try {
					ouputStream = response.getOutputStream();
					ZipOutputStream outZIP = new ZipOutputStream(ouputStream); // Create the ZIP file
					for (int i = 0; i < filenames.length; i++){ // Compress the files
						FileInputStream in = new FileInputStream(filenames[i]);
						outZIP.putNextEntry(new ZipEntry(filenames[i])); // Add ZIP entry to output stream.

						int len;
						while ((len = in.read(buf)) > 0) // Transfer bytes from the file to the ZIP file
						{
							outZIP.write(buf, 0, len);
						}
						outZIP.closeEntry(); // Complete the entry
						in.close();
					}
					response.setContentType("application/zip");
					response.setHeader("Content-Disposition", "attachment;filename=\"" + layoutZip + "\"");
					outZIP.finish();
					outZIP.close(); // Complete the ZIP file
				} catch (IOException e) {
					e.printStackTrace();
				}

				File ficheroPag = new File(layoutPago);
				ficheroPag.delete();
				File ficheroDoc = new File(layoutDocu);
				ficheroDoc.delete();
				File ficheroPagZip = new File(layoutPagoZip);
				ficheroPagZip.delete();
				File ficheroDocZip = new File(layoutDocuZip);
				ficheroDocZip.delete();

			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
		}

		catch (FileNotFoundException ex) {
			ex.printStackTrace();
		}

		catch (Exception e) {
			e.printStackTrace();

		}

	}

	
	
	@Override
	public void init(ServletConfig config) throws ServletException {
		try {
			InitialContext ic = new InitialContext();
			folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");

			if (folioGenerator == null) {
				folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
				log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
			} else
				log.info("folioGeneratorInterface=" + folioGenerator);
		} catch (NamingException exc) {
			folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
			log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
		}
	}

}
