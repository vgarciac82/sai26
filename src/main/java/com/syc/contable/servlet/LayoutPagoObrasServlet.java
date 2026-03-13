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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.syc.contable.PagoObrasBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;


public class LayoutPagoObrasServlet extends HttpServlet {
	/**
	 * 
	 */
	private String CualArchivo = "";
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public LayoutPagoObrasServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doDelete method of the servlet. <br>
	 *
	 * This method is called when a HTTP delete request is received.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		
		CualArchivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
		
			
	}

	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		String msgRetorno= "";
		HttpSession session = request.getSession(false);
		Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
		
		String sUsuario = usuario.getLogin();
		String sFolio = (request.getParameter("sDataH")!= null)? request.getParameter("sDataH").trim(): "";
		String sCuentaBancaria = (request.getParameter("sDataHCB")!= null)? request.getParameter("sDataHCB").trim(): "";
		String sFecha = (request.getParameter("sDataHFecha")!= null)? request.getParameter("sDataHFecha").trim(): "";
		String sLeyenda = (request.getParameter("sDataHLeyenda")!= null)? request.getParameter("sDataHLeyenda").trim(): "";
		
		CualArchivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
		
		int valor = sFolio.length() - 1;
		String sFolioQuery = sFolio.substring(0, valor);
		ArrayList<String> arrListPago=null;
		ArrayList<String> arrListDocu=null;
		PagoObrasBussinessLogic cmpBL =new PagoObrasBussinessLogic(GestionInterface.ATT_CONEXION);
		try{
			
			//para dar el nombre del archivo
			SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");            
			String sufijo = fecha.format(new Date(System.currentTimeMillis()));

			//StringBuffer arrListDocumentacion = new StringBuffer();
			arrListPago = cmpBL.buscaCompromisos(sFolioQuery, sCuentaBancaria, sFecha, sLeyenda, sUsuario);
			arrListDocu = cmpBL.ArmaDocumentoComprobatorio(sFolioQuery);
			
			//Long date = System.currentTimeMillis();
			File layoutPago = new File( System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "PAGOCOMPROMISOOBRAS" + sufijo.trim() + ".csv");				// para dar el nombre del archivo Pago Directo
			File layoutDocu = new File( System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "DOCCOMP" + sufijo.trim() + ".csv" );					// para dar el nombre del archivo Documentación Comprobatoria		
			File layoutZip = new File( System.getProperty( "java.io.tmpdir" ) + File.separatorChar + "PAGOCOMPROMISOOBRASTodo" + sufijo + ".zip");
				
			BufferedWriter out = new BufferedWriter( new FileWriter(layoutPago));  			// Guarda el pago 
			StringBuffer archivoPago = new StringBuffer();
			for(int i=0; i <arrListPago.size(); i++)
			{
				archivoPago.append(arrListPago.get(i));
			}			
			String outTextPago = archivoPago.toString();  
			out.write(outTextPago);  
			out.close();			// fin de guarda pago
			
			BufferedWriter outDocu = new BufferedWriter( new FileWriter(layoutDocu));  		// Guarda el documento
			StringBuffer archivoDocu = new StringBuffer();
			for(int i=0; i <arrListDocu.size(); i++)
			{
				archivoDocu.append(arrListDocu.get(i));
			}
			String outTextDocu = archivoDocu.toString();  
			outDocu.write(outTextDocu);  
			outDocu.close();		// fin de guarda documento
			
	        File[] filenames = new File[]{layoutPago, layoutDocu};		// These are the files to include in the ZIP file
        
	        ServletOutputStream ouputStream;
	        byte[] buf = new byte[1024]; 									// Create a buffer for reading the files

	        try 
	        {	   
	        	ouputStream = response.getOutputStream();
	            ZipOutputStream outZIP = new ZipOutputStream(ouputStream);	// Create the ZIP file          
	            for (int i=0; i<filenames.length; i++) 						// Compress the files
	            {
	                FileInputStream in = new FileInputStream(filenames[i]);	                
	                outZIP.putNextEntry(new ZipEntry(filenames[i].getName()));		// Add ZIP entry to output stream.
	                
	                int len;
	                while ((len = in.read(buf)) > 0) 						// Transfer bytes from the file to the ZIP file
	                {
	                	outZIP.write(buf, 0, len);
	                } 
	                outZIP.closeEntry();									// Complete the entry
	                in.close();
	            }	
		        response.setContentType("application/zip");
		        response.setHeader("Content-Disposition", "attachment;filename=\""+ layoutZip.getName() +"\"");
		        outZIP.finish();
	            outZIP.close();												// Complete the ZIP file
	        } 
	        catch (IOException e) {
	        	e.printStackTrace();
	        }
	        
	        layoutPago.delete();
	        layoutDocu.delete();
	        

		}		
		catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    	}
		catch(Exception e){
			e.printStackTrace();
			msgRetorno = "Ocurrio el siguiente error al generar el layout: " + e.getMessage();
		}

		session.setAttribute( "RESULT", msgRetorno );
		ResponseSender.sendClientSimpleMessage( response, false, msgRetorno );
	//	response.sendRedirect( "../Generador/IntegraLayoutPagoObras.jsp" );

	}


	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
	
	
}
