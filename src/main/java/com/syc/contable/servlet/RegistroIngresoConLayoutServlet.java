package com.syc.contable.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.syc.gestion.servlet.GestionInterface;
import com.syc.registroingresos.RegistroIngresosBussinesLogic;

public class RegistroIngresoConLayoutServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(RegistroIngresoConLayoutServlet.class);
	private String	folioGenerator;

	public RegistroIngresoConLayoutServlet() { 
		super();
	}

	public void destroy() {
		super.destroy(); 
	}

	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{

		String sFolio = request.getParameter("sDataFolios");
		String u_login = request.getParameter("u_login");
		
		int valor = sFolio.length();// - 1;
		String sFolioQuery = sFolio.substring(0, valor);

		RegistroIngresosBussinesLogic RegIngBL = new RegistroIngresosBussinesLogic(GestionInterface.ATT_CONEXION);
		try{
			RegIngBL.ActualizaStatus(sFolioQuery, u_login);
		}
		catch (FileNotFoundException ex) {
    		ex.printStackTrace();
    	}
		catch(Exception e){
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