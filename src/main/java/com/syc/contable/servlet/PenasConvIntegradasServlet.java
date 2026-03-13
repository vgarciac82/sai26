package com.syc.contable.servlet;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import com.syc.contable.core.PenasConvIntDetalle;
import com.syc.contable.core.PenasConvIntEncabezado;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;

public class PenasConvIntegradasServlet extends HttpServlet implements GestionInterface {

	/**
	
	 */
	private static final long	serialVersionUID	= -6209553370385667777L;
	private static final Logger	log					= Logger.getLogger(PenasConvIntegradasServlet.class);
	private String				jniName;
	
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		HttpSession sesion = req.getSession(false);
		if (sesion == null) {
			return;
		}

		Usuario u = (Usuario) sesion.getAttribute(ATT_USER);
		if (u == null) {
			return;
		}
		
		String cxpAI = req.getParameter("caNoContrarreciboInt").trim();
		String nFolio = req.getParameter("id_caso").trim();
		String fecha = "2015/03/11";
		String cBancaria = req.getParameter("CuentaBancaria").trim();
		String Leyenda = req.getParameter("claveLeyenda").trim();
		String accion = req.getParameter("accion");
		String cBEN = req.getParameter("cIDRFC2");
		
		switch (Integer.parseInt(accion,10)){
			
			case 1:
				
				try{
					
					log.info("Insertando encabezado y detalle de la Integración");
					
					PenasConvIntEncabezado encabezado = PenasConvIntegradasBusinessLogic.instanceHeaderFromRequest(req);
					List<PenasConvIntDetalle> detalle = PenasConvIntegradasBusinessLogic.instanceDetailFromRequest(req);
					PenasConvIntegradasBusinessLogic pcIntBl = new PenasConvIntegradasBusinessLogic(jniName);
					pcIntBl.setEncabezado(encabezado);
					pcIntBl.setDetalle(detalle);
					int r = pcIntBl.insert();
					ResponseSender.sendClientSimpleMessage(resp, true, String.valueOf(r));
					
				}catch (Exception e) {
					log.error(e, e);
					ResponseSender.sendClientSimpleMessage(resp, false, "Ocurrio el siguiente error insertando la informacion: " + e);
				}
				break;
			case 2:
				
				try{
						
					log.info("Generando Layout de la integracion");
					
					ArrayList<String> arrListDocu=null;					
					PenasConvIntegradasBusinessLogic cmpBL = new PenasConvIntegradasBusinessLogic(GestionInterface.ATT_CONEXION);
									
					   try{
										
							SimpleDateFormat fecha1 = new SimpleDateFormat("yyyyMMddhhmm");            
							String sufijo = fecha1.format(new Date(System.currentTimeMillis()));
				
							arrListDocu = cmpBL.buscaPenasConvIntegradas(cxpAI, fecha, cBancaria, Leyenda, nFolio, cBEN);							
														
							File layoutDoc = new File (System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "PENASCONV_"+ sufijo +  ".csv" ) ;
							//String layoutDocu = System.getProperty( "java.io.tmpdir" ) + File.separatorChar  + cxpAI.trim() +  ".csv";					// para dar el nombre del archivo Documentación Comprobatoria
							File layoutZip = new File ( System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "INTEGRACION_PENAS_" +sufijo+ ".zip");

							BufferedWriter out = new BufferedWriter( new FileWriter(layoutDoc));  			// Guarda el pago 
							
							StringBuffer archivoPago = new StringBuffer();
							for(int i=0; i <arrListDocu.size(); i++){
								archivoPago.append(arrListDocu.get(i));
							}			
							
							String outTextPago = archivoPago.toString();  
							out.write(outTextPago);  
							out.close();			// fin de guarda pago
														
										
							File[] filenames = new File[] { layoutDoc }; 
							 
							ServletOutputStream ouputStream;
							byte[] buf = new byte[1024]; 
								
								
							try {
								ouputStream = resp.getOutputStream();
								ZipOutputStream outZIP = new ZipOutputStream(ouputStream); // Create the ZIP file
								for (int i = 0; i < filenames.length; i++){ // Compress the files
									FileInputStream in = new FileInputStream(filenames[i]);
									outZIP.putNextEntry(new ZipEntry(filenames[i].getName())); // Add ZIP entry to output stream.

									int len;
									while ((len = in.read(buf)) > 0){ // Transfer bytes from the file to the ZIP file
										outZIP.write(buf, 0, len);
									}

									outZIP.closeEntry(); // Complete the entry
									in.close();
								}
								
								resp.setContentType("application/zip");
								resp.setHeader("Content-Disposition", "attachment;filename=\"" + layoutZip.getName() + "\"");									
								outZIP.finish();
								outZIP.close(); // Complete the ZIP file
							} catch (IOException e) {
								log.error( e, e );
								e.printStackTrace();
							}
				
							layoutDoc.delete();
							layoutZip.delete();
							
					    }catch(FileNotFoundException ex){
						
						  ex.printStackTrace();
					    }
					    
			   }catch(Exception e){
					e.printStackTrace();
               }
				
			break;
			
			default:
				break;
		}
	}
	
	

}
