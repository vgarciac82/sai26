package com.syc.contable.servlet;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.contable.MultiReportePagosBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;


public class LayoutMultiReportePagosServlet extends HttpServlet {
	/**
	 * 
	 */
	private String CualArchivo = "";
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor of the object.
	 */
	public LayoutMultiReportePagosServlet() {
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

		//response.setContentType("text/html");
		//PrintWriter out = response.getWriter();
		//out
		//		.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		//out.println("<HTML>");
		//out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		//out.println("  <BODY>");
		//out.print("    This is ");
		//out.print(this.getClass());
		//out.println(", using the GET method");
		//out.println("  </BODY>");
		//out.println("</HTML>");
		//out.flush();
		//out.close();
		
		
		CualArchivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
		//String CualArchivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
        //recupero los campos caNoCompromisos de la lista de los que serán enviados
			
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		
		String szTemp = (request.getParameter("szTemp")!= null)? request.getParameter("szTemp").trim(): "";
		
		CualArchivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
		
		ArrayList<String> arrListPago=null;
		MultiReportePagosBussinessLogic cmpBL =new MultiReportePagosBussinessLogic(GestionInterface.ATT_CONEXION);
		try{
			
			//para dar el nombre del archivo
			SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmm");            
			String sufijo = fecha.format(new Date(System.currentTimeMillis()));

			arrListPago = cmpBL.buscaCompromisos(szTemp);
			
			String layoutPago = "Cadenas" + sufijo.trim() + ".txt";				// para dar el nombre del archivo Pago Directo
			//String layoutDocu = "DOCCOMP" + sufijo.trim() + ".csv";					// para dar el nombre del archivo Documentación Comprobatoria
			String layoutPagoZip = "Cadenas" + sufijo.trim() + ".zip";			// para dar el nombre del archivo Pago Directo ZIP
			
			BufferedWriter out = new BufferedWriter( new FileWriter(layoutPago));  			// Guarda el pago 
			StringBuffer archivoPago = new StringBuffer();
			for(int i=0; i <arrListPago.size(); i++)
			{
				archivoPago.append(arrListPago.get(i));
			}			
			String outTextPago = archivoPago.toString();  
			out.write(outTextPago);  
			out.close();			// fin de guarda pago

			// envío de archivo CSV
			ServletOutputStream outS = null;         
			ByteArrayInputStream byteArrayInputStream = null;         
			BufferedOutputStream bufferedOutputStream = null;    
				try {        
					response.setContentType("text/csv");
				    String disposition = "attachment; fileName=" + layoutPago;
				    response.setHeader("Content-Disposition", disposition);
				    outS = response.getOutputStream();
					byte[] blobData = outTextPago.getBytes();    //setup the input as the blob to write out to the client
				     byteArrayInputStream = new ByteArrayInputStream(blobData);
				     bufferedOutputStream = new BufferedOutputStream(outS);
				     int length = blobData.length; 
				     response.setContentLength(length);
				     //byte[] buff = new byte[length];
				     byte[] buff = new byte[(1024 * 1024) * 2];  
				     //now lets shove the data down 
				     int bytesRead;
				     // Simple read/write loop.
				     while (-1 !=  (bytesRead = byteArrayInputStream.read(buff, 0, buff.length))) { 
				    	 bufferedOutputStream.write(buff, 0, bytesRead);
				     }    
				     //out.flush();
				     out.close();
			     } 
				catch (Exception e) { 
					System.err.println(e); 
					throw e;
				}
				finally { 
					if (out != null)
						out.close(); 
					if (byteArrayInputStream != null) {
						byteArrayInputStream.close(); 
						} 
					if (bufferedOutputStream != null) {
						bufferedOutputStream.close();
						}
				} 
	     
	        File ficheroPag = new File(layoutPago);	ficheroPag.delete();
	        File ficheroPagZip = new File(layoutPagoZip);	ficheroPagZip.delete();

		}		
		catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    	}
		catch(Exception e){
			e.printStackTrace();
		}

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
