package com.syc.contable.servlet;

import java.io.IOException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.syc.contable.BeneficiarioBussinessLogic;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
/**
 * @author Martha Aurora Sánchez Valdivieso
 * para SYC Constructores de Sistemas
 * desarrollo gestion_conagua_sif
 * México D.F. 16/02/2012
 *
 */
@SuppressWarnings("serial")
public class LayoutBeneficiariosServlet extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public LayoutBeneficiariosServlet() {
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
		BeneficiarioBussinessLogic benBL =new BeneficiarioBussinessLogic(GestionInterface.ATT_CONEXION);
		//recupero los campos caNoCompromisos de la lista de los que serán enviados
		String archivo = (request.getParameter("archivo")!= null)? request.getParameter("archivo").trim(): "";
		String beneficiarios = (request.getParameter("envio")!= null)? request.getParameter("envio").trim(): "";
		int valor = beneficiarios.length() - 1;
		String beneficiariosQuery = beneficiarios.substring(0, valor);
		if(archivo == null){
			throw new ServletException("El archivo no debe ir nulo");
		}
		
		//para dar el nombre del archivo
		SimpleDateFormat fecha = new SimpleDateFormat("yyyyMMddhhmmsss");
		String sufijo = fecha.format(new Date(System.currentTimeMillis()));

		StringBuffer archivoBen = new StringBuffer();
		StringBuffer archivoBenDocCom = new StringBuffer();
		StringBuffer archivoRDocCom = new StringBuffer();
		StringBuffer archivoBCB = new StringBuffer();
		
		if(archivo.equals("1")){
			try{
				//Se acumula el resultado del SELECT en un StringBuffer
				archivoBen = benBL.buscaBeneficiarios(beneficiariosQuery);
				
				String layoutB = "ben_" + sufijo + ".csv";
				//Preparar la descarga como attachment para archivo 1
				response.setContentType("aplication/download");
				response.setHeader("Content-Disposition", "attachment;filename=\""+ layoutB +"\"");
				
				//se calcular el tamaño del archivo
				BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(archivoBen.toString().getBytes()));
				int size = bis.available();
				
				//se avisa el tamaño del archivo al browser
				response.setContentLength(size);
				OutputStream out = response.getOutputStream();
					
				int bytesread;
				do{
					byte[] ben= new byte[10240];
					bytesread = bis.read(ben, 0, size);
					if(bytesread > -1){
						out.write(ben, 0, bytesread);
					}
				}while(bytesread > -1);
				
				bis.close();
				out.flush();
				out.close();
			}
		    catch (FileNotFoundException ex) {
		    	ex.printStackTrace();
		    }
			
		    catch(Exception e){
		    	e.printStackTrace();	
		    }	
		}
		
		if(archivo.equals("2")){
			try{
				//Se acumula el resultado del SELECT en un ArrayList<String>
				archivoBenDocCom = benBL.BuscaBenDocCom(beneficiariosQuery);
				
				String layoutBDC = "bendoccom_" + sufijo + ".csv";
				//Preparar la descarga como attachment para archivo 2
				response.setContentType("aplication/download");
				response.setHeader("Content-Disposition", "attachment;filename=\""+ layoutBDC +"\"");
				
				//se calcular el tamaño del archivo
				BufferedInputStream bis2 = new BufferedInputStream(new ByteArrayInputStream(archivoBenDocCom.toString().getBytes()));
				int size2 = bis2.available();
			
				//se avisa el tamaño del archivo al browser
				response.setContentLength(size2);
				OutputStream out2 = response.getOutputStream();
				
				int bytesread2;
			    do{
			    	byte[] ben2 = new byte[1024*256];
			    	bytesread2 = bis2.read(ben2, 0, size2);
			    	if(bytesread2 > -1){
			    		out2.write(ben2, 0, bytesread2);
			    	}
			    }while(bytesread2 > -1);
			
				bis2.close();
				out2.flush();
				out2.close();
				
			}
		    catch (FileNotFoundException ex) {
		    	ex.printStackTrace();
		    }
			
		    catch(Exception e){
		    	e.printStackTrace();	
		    }	
		}
		
		
		if(archivo.equals("3")){
			try{
				//Se acumula el resultado del SELECT en un ArrayList<String>
				archivoRDocCom = benBL.BuscaRDocCom(beneficiariosQuery);
			
				String layoutBRDC = "benrdoccom_" + sufijo + ".csv";
				//Preparar la descarga como attachment para archivo 3
				response.setContentType("aplication/download");
				response.setHeader("Content-Disposition", "attachment;filename=\""+ layoutBRDC +"\"");
				
				//se calcular el tamaño del archivo
				BufferedInputStream bis3 = new BufferedInputStream(new ByteArrayInputStream(archivoRDocCom.toString().getBytes()));
				int size3 = bis3.available();

				//se avisa el tamaño del archivo al browser
				response.setContentLength(size3);
				OutputStream out3 = response.getOutputStream();

				int bytesread3;
				do{
					byte[] ben3 = new byte[1024*256];
					bytesread3 = bis3.read(ben3, 0, size3);
					if(bytesread3 > -1){
						out3.write(ben3, 0, bytesread3);
					}
				}while(bytesread3 > -1);

				bis3.close();
				out3.flush();
				out3.close();
			}
			catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			
			catch(Exception e){
				e.printStackTrace();	
			}	
		}

		if(archivo.equals("4")){
			try{
				//Se acumula el resultado del SELECT en un ArrayList<String>
				archivoBCB = benBL.BuscaBCB(beneficiariosQuery);

				String layoutBCB = "rfc_clabe_" + sufijo + ".csv";

				//Preparar la descarga como attachment para archivo 4
				response.setContentType("aplication/download");
				response.setHeader("Content-Disposition", "attachment;filename=\""+ layoutBCB +"\"");

				//se calcular el tamaño del archivo
				BufferedInputStream bis4 = new BufferedInputStream(new ByteArrayInputStream(archivoBCB.toString().getBytes()));
				int size4 = bis4.available();

				//se avisa el tamaño del archivo al browser
				response.setContentLength(size4);
				OutputStream out4 = response.getOutputStream();

				int bytesread4;
				do{
					byte[] ben4= new byte[1024*256];
					bytesread4 = bis4.read(ben4, 0, size4);
					if(bytesread4 > -1){
						out4.write(ben4, 0, bytesread4);
					}
				}while(bytesread4 > -1);
				bis4.close();
				out4.flush();
				out4.close();
			}
			catch (FileNotFoundException ex) {
				ex.printStackTrace();
			}
			
			catch(Exception e){
				e.printStackTrace();	
			}	
		}
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
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		JSONObject jsonObj=new JSONObject();;
		JSONArray arrayObj=new JSONArray();;
		PrintWriter out = response.getWriter();;
		String mensaje="";
		BeneficiarioBussinessLogic benBL =new BeneficiarioBussinessLogic(GestionInterface.ATT_CONEXION);
		boolean actualizado = false;
		String beneficiarios = request.getParameter("envioP");
		String cFolios = request.getParameter("cFolios");
		Usuario u=(Usuario) session.getAttribute(GestionInterface.ATT_USER);
		if (beneficiarios == null){
			throw new ServletException("envio no debe ir nulo");
		}

		beneficiarios = beneficiarios.trim();
		cFolios=cFolios.trim();
		
		if (beneficiarios.lastIndexOf(",") != -1){
			beneficiarios = beneficiarios.substring(0, beneficiarios.length() - 1);
		}
		if (cFolios.lastIndexOf(",") != -1){
			cFolios = cFolios.substring(0, cFolios.length() - 1);
		}
		try{
			actualizado = benBL.UpdateStatusBen(beneficiarios,cFolios,u.getLogin());
			if(actualizado){
				//System.out.println("Registros actualizados");
				mensaje="Proceso finalizado correctamente.";
			}else{
				mensaje="Error al finalizar el proceso./n No se actualizo el estatus.";
			}
		}
		catch(Exception e){
			mensaje=(null==e.getMessage()?"Error":e.getMessage().toString());
			actualizado=false;
			throw new ServletException(e);
		}finally {
			try {
				jsonObj.put("RESPUESTA", actualizado);
				jsonObj.put("MENSAJE", mensaje);
				String destino = new String(arrayObj.put(jsonObj).toString().getBytes("UTF-8"),"ISO-8859-1") ;
				out.println(destino);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
