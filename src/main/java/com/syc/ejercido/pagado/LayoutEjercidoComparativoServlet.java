package com.syc.ejercido.pagado;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.gestion.servlet.GestionInterface;

public class LayoutEjercidoComparativoServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LayoutEjercidoComparativoServlet() {
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

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out
				.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the GET method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
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
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		ArrayList<String> arrListDocu=null;
		PagosNominaBussinessLogic cmpBL = new PagosNominaBussinessLogic(GestionInterface.ATT_CONEXION);
		
		try{
			
			String layoutComparativo = "";
			String layoutPagoZip = "";
			String tipoConsulta = request.getParameter("tipoConsulta");
			
			if(tipoConsulta.equals("consultaEjercidoComparativo")){
				
				layoutComparativo = "EjercidoPagadoComparativo.csv";
				layoutPagoZip = "EjercidoPagadoComparativo.zip";
				arrListDocu = cmpBL.buscaEjercidoComparativo(tipoConsulta);
			
			}else{
				
				layoutComparativo = "EjercidoPagadoComparativoCapitulo.csv";
				layoutPagoZip = "EjercidoPagadoComparativoCapitulo.zip";
				arrListDocu = cmpBL.buscaEjercidoComparativo(tipoConsulta);
			}
			
			
			BufferedWriter out = new BufferedWriter( new FileWriter(layoutComparativo)); 
			StringBuffer archivoPago = new StringBuffer();
			
			for(int i=0; i <arrListDocu.size(); i++)
			{
				archivoPago.append(arrListDocu.get(i));
			}			
			String outTextPago = archivoPago.toString();  
			out.write(outTextPago);  
			out.close();
			
			byte[] bufPag = new byte[2048]; 	
			
			try {	
	        	
	            ZipOutputStream outPag = new ZipOutputStream(new FileOutputStream(layoutPagoZip)); 
	            FileInputStream inPag = new FileInputStream(layoutComparativo);
	            outPag.putNextEntry(new ZipEntry(layoutComparativo));
	            
	            int lenPag;
                while ((lenPag = inPag.read(bufPag)) > 0) {
                	outPag.write(bufPag, 0, lenPag);
                } 

                outPag.closeEntry();
                inPag.close();
                outPag.close();
	        } catch (IOException e) {
	        
	        }
			
	        doDownload(response, layoutComparativo, layoutComparativo );
	        
	        File ficheroPag = new File(layoutComparativo);	ficheroPag.delete();
				
		}catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    		
    	}catch(Exception e){
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
	
	private void doDownload(HttpServletResponse resp, String filename, String original_filename) throws IOException {

		int length = 0;
		File f = new File(filename);
		ServletOutputStream out = resp.getOutputStream();
		ServletContext context = getServletConfig().getServletContext();
		String mimetype = context.getMimeType(original_filename);

		resp.setContentType((mimetype != null) ? mimetype : "application/octet-stream");
		resp.setContentLength((int) f.length());
		//resp.addHeader("Content-Disposition", "attachment; filename=\"" + original_filename + "\";");
		//resp.addHeader("Content-Disposition", "attachement; filename=\"" + original_filename + "\";");
		resp.addHeader("Content-Disposition", "inline; filename=\"" + original_filename + "\";");
		byte[] bbuf = new byte[5 * 1024]; // 5K buffer
		DataInputStream in = new DataInputStream(new FileInputStream(f));
		while ((in != null) && ((length = in.read(bbuf)) != -1)) {
			out.write(bbuf, 0, length);
		}

		in.close();
		out.flush();
		out.close();

	}
	
}
