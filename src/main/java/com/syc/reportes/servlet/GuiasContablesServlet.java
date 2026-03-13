package com.syc.reportes.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.reportes.GuiasContablesBusinessLogic;

public class GuiasContablesServlet extends HttpServlet implements GestionInterface {

	private static final long			serialVersionUID	= 6723127616859732249L;
	private static final Logger			log					= Logger.getLogger(GuiasContablesServlet.class);
	private static String				jniName				= "jdbc/gestion";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// Put your code here
	}
	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession(false);
		if (session == null)
			throw new ServletException("Su session a caducado");

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null)
			throw new ServletException("Su session a caducado");
		
		int res = 0;
		String versionFinal = req.getParameter("generar"); /*Check finalizar*/				
		String msg = "";
		String ruta = getServletContext().getRealPath("Reportes" + File.separator);	 //ruta del jasper			
		String consultar = req.getParameter("consultar"); /*Check consultar*/
		String sinFirmas = req.getParameter("sinFirmar");
		int ejercicioFiscal = Integer.parseInt(req.getParameter("ejercicioFiscal"));
		int nivel = Integer.parseInt(req.getParameter("NUMERO_DIGITOS"));		
		//int idversion = 0;			
		int idGuia = Integer.parseInt(req.getParameter("id_Guia"));
		int idInstructivo = Integer.parseInt(req.getParameter("id_ManejoCuentas"));
		int idVGuia = Integer.parseInt(req.getParameter("id_Guia1"));
		int idVInstructivo = Integer.parseInt(req.getParameter("id_ManejoCuentas1"));
				
		int tipoManual = 0;	
		String tipoFormato = "";
		String tipoReporte = "";
		
		int id_complemento = Integer.parseInt(req.getParameter("id_ComplementoV"));
		
		GuiasContablesBusinessLogic rrs = new GuiasContablesBusinessLogic(jniName);
		
		try{
			if("1".equals(versionFinal)){
				if(id_complemento == 1 || id_complemento == 2 || id_complemento == 3 || id_complemento == 4 || id_complemento == 5 || id_complemento == 6 || id_complemento == 8 || id_complemento == 9 || id_complemento == 10 || id_complemento == 11 || id_complemento == 12 || id_complemento == 13) {
					res = rrs.guardaVersionComplementos(req, resp);					
				}else {
					res = rrs.guardaVersion(req, resp);									
				}
				
				log.info("Registros Insertados: " + res);
				if(res > 0){
					msg = "Se creo exitosamente la version al documento seleccionado";
					session.setAttribute("RESULT", msg);
				}
				else{
					msg = "No se creo la version correctamente, favor de contactar al administrador.";		
					session.setAttribute("RESULT", msg);
				}
			}else if ("0".equals(consultar)){
				//idversion = Integer.parseInt(req.getParameter("id_version"));				
				
				tipoReporte = req.getParameter("TIPO_REPORTE"); //nombre del jasper
				
				if(nivel == 17 && "ManejoDeCuentas.jasper".contentEquals(tipoReporte)) {
					tipoReporte = "ManejoDeCuentasN15.jasper";	//nombre del jasper
				}else if(nivel == 17 && "PlanDecuentas.jasper".contentEquals(tipoReporte)) {			
					tipoReporte = "PlanDecuentasN15.jasper";	//nombre del jasper
				}
				
				if("ComplementoManualIntroduccion.jasper".equals(tipoReporte))
					tipoManual = 1;
				else if("ComplementoManualCicloHacendario.jasper".equals(tipoReporte))
					tipoManual = 2;
				else if("ComplementoManualSCG.jasper".equals(tipoReporte))
					tipoManual = 3;
				else if("ComplementoManualAspectosGenerales.jasper".equals(tipoReporte))
					tipoManual = 4;
				else if("ComplementoManualClasificacionParaestatal.jasper".equals(tipoReporte))
					tipoManual = 5;
				else if("ComplementoManualInterAlcance.jasper".equals(tipoReporte))
					tipoManual = 6;
				else if("ComplementoManualControlIngresoGasto.jasper".equals(tipoReporte))
					tipoManual = 8;
				else if("ComplementoManualSubsidiosApoyosFiscales.jasper".equals(tipoReporte))
					tipoManual = 9;
				else if("ComplementoManualEstimacionCtasIncobrables.jasper".equals(tipoReporte))
					tipoManual = 10;
				else if("ComplementoManualObligacionesLaborales.jasper".equals(tipoReporte))
					tipoManual = 11;
				else if("ComplementoManualArrendamientoFinanciero.jasper".equals(tipoReporte))
					tipoManual = 12;
				else if("ComplementoManualNACG01.jasper".equals(tipoReporte))
					tipoManual = 13;				
				else if("EstadosFinancieros".equals(tipoReporte)){
					tipoFormato = req.getParameter("id_estadoFinanciero1");	

					if(ejercicioFiscal < 2021) {
						tipoFormato = tipoFormato + ".pdf";
					} else if(ejercicioFiscal >= 2021 && ejercicioFiscal <= 2023) {
						tipoFormato = tipoFormato + "_V2.pdf";
					} else if(ejercicioFiscal >= 2024) {
						if("1Notas".equals( tipoFormato )) {
							tipoFormato = tipoFormato + "_V3.pdf";
						} else {
							tipoFormato = tipoFormato + "_V2.pdf";
						}
					}
					
				}
				else if("EstadosPresupuestarios".equals(tipoReporte))
					tipoFormato = req.getParameter("id_estadoPresupuestario1");
				else if("EstadosProgramaticos".equals(tipoReporte))
					tipoFormato = req.getParameter("id_estadoProgramatico1");
				
				if("EstadosFinancieros".equals(tipoReporte) || "EstadosPresupuestarios".equals(tipoReporte) || "EstadosProgramaticos".equals(tipoReporte)){
					rrs.cosultaFormato(req, resp, ruta, tipoFormato);
				}else{
					if(nivel == 17 && "GuiasContabilizadoras.jasper".equals(tipoReporte))
						tipoReporte = "GuiasContabilizadorasN15.jasper";
					
					String reportPath = getServletContext().getRealPath("Reportes" + File.separator + tipoReporte); //ruta y nombre del jasper					
					rrs.consultaVersiones(req, resp, reportPath, ruta, tipoManual, sinFirmas, nivel, idVGuia, idVInstructivo);
				}
			}	
			else {
				String Nomjsaper = req.getParameter("id_archivo");	//nombre del jasper	
				if("ComplementoManualIntroduccion.jasper".equals(Nomjsaper))
					tipoManual = 1;
				else if("ComplementoManualCicloHacendario.jasper".equals(Nomjsaper))
					tipoManual = 2;
				else if("ComplementoManualSCG.jasper".equals(Nomjsaper))
					tipoManual = 3;
				else if("ComplementoManualAspectosGenerales.jasper".equals(Nomjsaper))
					tipoManual = 4;
				else if("ComplementoManualClasificacionParaestatal.jasper".equals(Nomjsaper))
					tipoManual = 5;
				else if("ComplementoManualInterAlcance.jasper".equals(Nomjsaper))
					tipoManual = 6;
				else if("ComplementoManualControlIngresoGasto.jasper".equals(Nomjsaper))
					tipoManual = 8;
				else if("ComplementoManualSubsidiosApoyosFiscales.jasper".equals(Nomjsaper))
					tipoManual = 9;
				else if("ComplementoManualEstimacionCtasIncobrables.jasper".equals(Nomjsaper))
					tipoManual = 10;
				else if("ComplementoManualObligacionesLaborales.jasper".equals(Nomjsaper))
					tipoManual = 11;
				else if("ComplementoManualArrendamientoFinanciero.jasper".equals(Nomjsaper))
					tipoManual = 12;
				else if("ComplementoManualNACG01.jasper".equals(Nomjsaper))
					tipoManual = 13;				
				else if("EstadoFinanciero".equals(Nomjsaper)) {
					tipoFormato = req.getParameter("id_estadoFinanciero");	

				if(ejercicioFiscal < 2021)
					tipoFormato = tipoFormato + ".pdf";
				else if(ejercicioFiscal >= 2021 && ejercicioFiscal <= 2023)
					tipoFormato = tipoFormato + "_V2.pdf";
				else if("1Notas".equals( tipoFormato ) && ejercicioFiscal >= 2024)
					tipoFormato = tipoFormato + "_V3.pdf";
				else if(ejercicioFiscal >= 2024)
					tipoFormato = tipoFormato + "_V2.pdf";
				
				}
				else if("EstadoPresupuestario".equals(Nomjsaper))
					tipoFormato = req.getParameter("id_estadoPresupuestario");
				else if("EstadoProgramatico".equals(Nomjsaper))
					tipoFormato = req.getParameter("id_estadoProgramatico");					
				if("EstadoFinanciero".equals(Nomjsaper) || "EstadoPresupuestario".equals(Nomjsaper) || "EstadoProgramatico".equals(Nomjsaper)){
						rrs.cosultaFormato(req, resp, ruta, tipoFormato);
				}else 
					if(nivel == 17 && "GuiasContabilizadoras.jasper".equals(Nomjsaper))
						Nomjsaper = "GuiasContabilizadorasN15.jasper";
					else if(nivel == 17 && "ManejoDeCuentas.jasper".equals(Nomjsaper))
						Nomjsaper = "ManejoDeCuentasN15.jasper";
					else if(nivel == 17 && "PlanDecuentas.jasper".equals(Nomjsaper))
						Nomjsaper = "PlanDecuentasN15.jasper";
				
					rrs.cosultaTemporal(req, resp, ruta, Nomjsaper, tipoManual, sinFirmas, nivel, idGuia, idInstructivo);				
			}
									
		}catch (Exception e){
			log.error(e, e);
			msg = "Notifique al Administrador. Ocurrio el siguiente error al cargar el archivo: " + e;
			session.setAttribute("RESULT", msg);
			throw new ServletException(e);
		}
		
		resp.sendRedirect("../Generador/GuiasContables.jsp");
		
	}

	@Override
	public void init() throws ServletException {
		// Put your code here
	}
}
