package com.syc.contable.servlet;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.syc.contable.PagosDiversosBussinessLogic;
import com.syc.gestion.servlet.GestionInterface;

public class LayoutPagosDiversosConLayoutServlet extends HttpServlet {
	private static final long	serialVersionUID	= 1L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sFolio = (request.getParameter("sDataFolios") != null) ? request.getParameter("sDataFolios").trim() : "";

		int valor = sFolio.length();
		String sFolioQuery = sFolio.substring(0, valor);
		PagosDiversosBussinessLogic cmpBL = new PagosDiversosBussinessLogic(GestionInterface.ATT_CONEXION);

		try {
			// Actualizamos el status de los folios seleccionados
			cmpBL.ActualizaStatus(sFolioQuery);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}