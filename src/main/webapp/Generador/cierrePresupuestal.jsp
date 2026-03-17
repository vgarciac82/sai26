<%@page import="org.slf4j.Logger"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.ejercido.pagado.CierrePresupuestal"%>
<%@page import="com.syc.ejercido.pagado.SubirArchivo"%>
<%@page import="org.json.JSONObject"%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.util.Date"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.BufferedInputStream"%>
<%@page import="java.io.BufferedOutputStream"%>
<%@page import="java.io.ByteArrayInputStream"%>
<%@page import="org.slf4j.LoggerFactory"%>

<%!private Logger log = LoggerFactory.getLogger(getClass());%>
<%
String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
String u_login = usuario.getLogin();
String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
String cUnidadResponsableAplica = usuario.getU_UR();

if (usuario != null) {
	
	try{

		String strTipo = (request.getParameter("tipo") != "") ? request.getParameter("tipo") : "";
		String tipoDocumento = (request.getParameter("tipoDocumento") != "") ? request.getParameter("tipoDocumento") : "" ;
		String numPagoAMF = (request.getParameter("numPagoAMF") != "") ? request.getParameter("numPagoAMF") : "" ;
		String numFolioAMF = (request.getParameter("numFolioAMF") != "") ? request.getParameter("numFolioAMF") : "" ;
		String nClaveAMF = (request.getParameter("nClaveAMF") != "" ) ? request.getParameter("nClaveAMF") : "" ;
		String rfcAMF = (request.getParameter("rfcAMF") != "") ?  request.getParameter("rfcAMF") : "" ;
		String aEjercicioFiscal = (request.getParameter("aEjercicioFiscal") != "") ?  request.getParameter("aEjercicioFiscal") : "" ;
		String cUnidadResponsable = (request.getParameter("cUnidadResponsable") != "") ? request.getParameter("cUnidadResponsable") : "";
		String fCaptura = (request.getParameter("fCaptura") != "") ? request.getParameter("fCaptura") : "";
		String fPago = (request.getParameter("fPago") != "") ? request.getParameter("fPago") : "";
		//String importeNetoCxp = (request.getParameter("importeNetoCxp") != "") ? request.getParameter("importeNetoCxp") : "";
		//String cCentroContable = (request.getParameter("cCentroContable") != "") ? request.getParameter("cCentroContable") : "";
		//String u_login = (request.getParameter("u_login") != "") ? request.getParameter("u_login") : "";
		String idCierreCXP = (request.getParameter("idCierreCXP") != "") ? request.getParameter("idCierreCXP") : "";
		String caNoContrarrecibo = (request.getParameter("caNoContrarrecibo") != "") ? request.getParameter("caNoContrarrecibo") : "";
		String nFolioAdefa = (request.getParameter("nFolioAdefa") != "" ) ? request.getParameter("nFolioAdefa") : "" ;
		String strDocumento = (request.getParameter("documento") != "" ) ? request.getParameter("documento") : "" ;
		
		String empleadoCaptura = StringUtils.trimToEmpty( request.getParameter("empleadoCaptura" ) );
		String empleadoVoBo = StringUtils.trimToEmpty( request.getParameter("empleadoVoBo" ) );
		String empleadoAutoriza = StringUtils.trimToEmpty( request.getParameter("empleadoAutoriza" ) );
		
		CierrePresupuestal cpAMF = new CierrePresupuestal(GestionInterface.ATT_CONEXION);
		cpAMF.setReportPath(reportPath);
		cpAMF.setUsuario( usuario );
		
		if(strTipo.equals("guardarPagoAMF")){
		
			JSONObject resJson = cpAMF.guardarPagoAMFCierre(tipoDocumento, numPagoAMF, numFolioAMF, nClaveAMF, rfcAMF, aEjercicioFiscal, fCaptura, fPago, cCentroContable, u_login, caNoContrarrecibo, cUnidadResponsable, cUnidadResponsableAplica);
			log.debug("respuesta:"+resJson);
			out.println(resJson);
		
		}else if(strTipo.equals("guardarCierreCuentasPorPagar")){
			JSONObject resJson = cpAMF.guardarCierreCuentasPorPagar(tipoDocumento,caNoContrarrecibo,idCierreCXP,u_login );
			out.println(resJson);
			
		}else if(strTipo.equals("guardarAdefa")){
			JSONObject resJson = cpAMF.guardarAdefa(tipoDocumento,caNoContrarrecibo,nFolioAdefa,u_login);
			out.println(resJson);
		}else if(strTipo.equals("cancelaAdefa")){
			JSONObject resJson = cpAMF.cancelaAdefa(nFolioAdefa);
			out.println(resJson);
			
		}else if(strTipo.equals("cancelaEjercidoPagado")){
			JSONObject resJson = cpAMF.cancelaEjercidoPagado(nFolioAdefa, tipoDocumento);
			out.println(resJson);
			
		}else if(strTipo.equals("aplicarCompromisoCapituloMil")){
			
			JSONObject resJson = cpAMF.aplicarCompromisoCapituloMil(caNoContrarrecibo);
			out.println(resJson);
		}else if(strTipo.equals("aplicarCierreCompromiso")){
			
			JSONObject resJson = cpAMF.aplicarCancelacionCompromiso(caNoContrarrecibo);
			out.println(resJson);
		}else if(strTipo.equals("aplicarNominaCapituloMil")){
			JSONObject resJson = cpAMF.aplicarNominaCapituloMil(caNoContrarrecibo);
			out.println(resJson);
			
		}else if(strTipo.equals("aplicarNominaCapMilDev")){
			JSONObject resJson = cpAMF.aplicarNominaCapMilDev(caNoContrarrecibo);
			out.println(resJson);
			
		}else if(strTipo.equals("totalAdefa")){
			JSONObject resJon = cpAMF.totalAdefa();
			out.println(resJon);
			
		}else if(strTipo.equals("validarArchivoCapituloMil")){
			JSONObject resJon = cpAMF.validarArchivo(caNoContrarrecibo,strDocumento);
			out.println(resJon);
			
		}else if(strTipo.equals("subirArchivo")){
			
			String nombreArchivo = (request.getParameter("nombreArchivo") != "" ) ? request.getParameter("nombreArchivo") : "" ;
			
			SubirArchivo archivo = new SubirArchivo();
			JSONObject resJon = archivo.envioArchivo(nombreArchivo);
			out.println(resJon);
			
		}else if(strTipo.equals("subirArchivoFTP")){
			
			String nombreArchivo = (request.getParameter("nombreArchivo") != "" ) ? request.getParameter("nombreArchivo") : "" ;
			
			SubirArchivo archivo = new SubirArchivo();
			JSONObject resJon = archivo.envioArchivoFTP(nombreArchivo);
			out.println(resJon);
			
		}else if(strTipo.equals("capituloMilDevCompromiso")){
			
			String caNoCompromiso = (request.getParameter("caNoCompromiso") != "" ) ? request.getParameter("caNoCompromiso") : "" ;
			String campo = "nFolioCompromisoNomina";
			String tablaEnc = "tCompromisoNominaEncabezado";
			String campoCondicion = "caNoCompromiso";
			String tablaDet = "tCompromisoNominaDetalle";
			String tipoAplicar = "COMPROMISO";
			String fAplicar = "";
			
			JSONObject resJson = cpAMF.aplicarMotor(caNoCompromiso, campo, tablaEnc, campoCondicion, tablaDet, tipoAplicar, fAplicar);
			out.println(resJson);
		}else if(strTipo.equals("aplicarMotor")){
			
			String caNoCompromiso = (request.getParameter("caNoContrarrecibo") != "" ) ? request.getParameter("caNoContrarrecibo") : "" ;
			String campo = (request.getParameter("campo") != "" ) ? request.getParameter("campo") : "" ; //"nFolioCompromisoNomina";
			String tablaEnc = (request.getParameter("tablaEnc") != "" ) ? request.getParameter("tablaEnc") : "" ; //"tCompromisoNominaEncabezado";
			String campoCondicion = (request.getParameter("campoCondicion") != "" ) ? request.getParameter("campoCondicion") : "" ; //"caNoCompromiso";
			String tablaDet = (request.getParameter("tablaDet") != "" ) ? request.getParameter("tablaDet") : "" ; //"tCompromisoNominaDetalle";
			String tipoAplicar = (request.getParameter("tipoAplicar") != "" ) ? request.getParameter("tipoAplicar") : "" ; //"COMPROMISO";
			String fAplicar = (request.getParameter("fAplicar") != "" ) ? request.getParameter("fAplicar") : "" ; //"fAplicacion";
			boolean autorizadoPorFiel = "true".equalsIgnoreCase( request.getParameter("autorizadoPorFiel") ); //autorizadoPorFiel
			
			cpAMF.setEmpleadoVoBo( empleadoVoBo );
			cpAMF.setEmpleadoCaptura( empleadoCaptura );
			cpAMF.setEmpleadoAutoriza( empleadoAutoriza );
			cpAMF.setAutorizadoPorFiel( autorizadoPorFiel );
			
			JSONObject resJson = cpAMF.aplicarMotor(caNoCompromiso, campo, tablaEnc, campoCondicion, tablaDet, tipoAplicar, fAplicar);
			
			out.println(resJson);
			 
		}else if(strTipo.equals("aplicarMotorPoliza")){				
			
			
			String nFolioDocPoliza =(request.getParameter("nFolioDocPoliza") != "" ) ? request.getParameter("nFolioDocPoliza") : "" ; 
			String campo = (request.getParameter("campo") != "" ) ? request.getParameter("campo") : "" ; //nfoliodocpoliza/cancel
			String tablaEnc = (request.getParameter("tablaEnc") != "" ) ? request.getParameter("tablaEnc") : "" ; 
			String tablaDet = (request.getParameter("tablaDet") != "" ) ? request.getParameter("tablaDet") : "" ; 
			String tipoAplicar = (request.getParameter("tipoAplicar") != "" ) ? request.getParameter("tipoAplicar") : "" ; //"DOCPOLIZA/CANCEL";
			log.debug(nFolioDocPoliza+" "+campo+" "+tablaEnc+" "+tablaDet+" "+tipoAplicar);
			
			boolean autorizadoPorFiel = "true".equalsIgnoreCase( request.getParameter("firmaElectronica") ); //autorizadoPorFiel
			empleadoCaptura = StringUtils.trimToEmpty( request.getParameter("empleadoCaptura" ) );
		  	empleadoVoBo = StringUtils.trimToEmpty( request.getParameter("nombreVoBo" ) );
			empleadoAutoriza = StringUtils.trimToEmpty( request.getParameter("nombreAut" ) );
		
			cpAMF.setEmpleadoVoBo( empleadoVoBo );
			cpAMF.setEmpleadoCaptura( empleadoCaptura );
			cpAMF.setEmpleadoAutoriza( empleadoAutoriza );
			cpAMF.setAutorizadoPorFiel( autorizadoPorFiel );
			
			JSONObject resJson = cpAMF.aplicarMotorPoliza(nFolioDocPoliza, campo, tablaEnc, tablaDet, tipoAplicar);
			out.println(resJson);
			
		}else if(strTipo.equals("aplicarMotorPolizaCancelacion")){				
			
			
			String cFolioDocumento =(request.getParameter("cFolioDocumento") != "" ) ? request.getParameter("cFolioDocumento") : "" ; 
			String Fecha = (request.getParameter("Fecha") != "" ) ? request.getParameter("Fecha") : "" ; 
			String Usuario = (request.getParameter("Usuario") != "" ) ? request.getParameter("Usuario") : "" ; 
			String mesAbierto= (request.getParameter("mesAbierto") != "" ) ? request.getParameter("mesAbierto") : "" ; 
			boolean autorizadoPorFiel = "true".equalsIgnoreCase( request.getParameter("autorizadoPorFiel") );
			
			JSONObject resJson = cpAMF.aplicarCancelacionMotorPoliza( cFolioDocumento, Fecha, Usuario,mesAbierto,autorizadoPorFiel);
			out.println(resJson);
			
		}
		else if(strTipo.equals("cancelaDevengado")){
			
			String tipoDoc = request.getParameter("tipoDocumento");
			String nFolio = request.getParameter("nFolio");
			boolean autorizadoPorFiel = "true".equalsIgnoreCase( request.getParameter("autorizadoPorFiel") ); //autorizadoPorFiel
			
			JSONObject resJson = cpAMF.cancelaDevengado( tipoDoc, nFolio, usuario, autorizadoPorFiel);
			out.println(resJson);
			
		}
		else if(strTipo.equals("aplicarDisminucionDevengado")){
			
			String tipoDoc = request.getParameter("tipoDocumento");
			String campo = request.getParameter("campo");
			String nFolio = request.getParameter("nFolio");
			String tablaEnc = request.getParameter("tablaEnc");
			String tablaDet = request.getParameter("tablaDet");
			
			JSONObject resJson = cpAMF.aplicarDisminucionDev( tipoDoc, campo, nFolio, tablaEnc, tablaDet);
			out.println(resJson);
			
		}else if(strTipo.equals("cancelaDocumento")){
			
			String tipoDoc = request.getParameter("tipoDocumento");
			String nFolio = request.getParameter("nFolio");
			
			JSONObject resJson = cpAMF.cancelaDocumento( tipoDoc, nFolio, usuario);
			out.println(resJson);
			
		}else if(strTipo.equals("cancelaCompromiso")){
			
			String tipoDoc = request.getParameter("tipoDocumento");
			String nFolio = request.getParameter("nFolio");
			String contrato = request.getParameter("contrato");
			
			JSONObject resJson = cpAMF.cancelaContrato( tipoDoc, nFolio, usuario, contrato);
			out.println(resJson);
			
		}	
	}catch(Exception e){
		log.error("catch_" +e,e);
		out.println("false");
	}

} else{
	out.println("{\"sinSesion\":\"sinSesion\"}");
}
%>


