<%@page import="java.io.File"%>
<%@page import="com.syc.egresos.firmante.servlet.Firmante"%>
<%@page import="com.syc.egresos.firmante.FirmanteBussinessLogic"%>
<%@page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page language="java" contentType="application/json"%>
<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.ejercido.pagado.EjercidoPagadoValidar"%>
<%@page import="com.syc.ejercido.pagado.EjercidoPagadoValidarVarios"%>
<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="com.syc.gestion.custom.FolioGeneratorInterface"%>
<%
	// Para Generar Caso Tramite
	InitialContext ic = new InitialContext();
	String folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");
	final Logger log = Logger.getLogger( "ejercidoPagadoValidar" );
	String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
%>
<%
	// Para Generar Caso Tramite
Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
Caso cRein=null;
FolioGeneratorInterface fg = null;
try {
	ClassLoader cl = getClass().getClassLoader();
	Class<?> clase = cl.loadClass(folioGenerator);
	fg = (FolioGeneratorInterface) clase.newInstance();
} catch (ClassNotFoundException exc) {
	throw new ServletException(exc);
} catch (InstantiationException exc) {
	throw new ServletException(exc);
} catch (IllegalAccessException exc) {
	throw new ServletException(exc);
}

/*if (usuario != null) {
	response.sendRedirect("../index.jsp");
	return;
}
*/

//System.out.println("usuario:"+usuario);
//usuario = null;
//System.out.println("usuario2:"+usuario);
System.out.println("usuario: "+usuario );
if (usuario != null) {
	try{
		String strTipo = request.getParameter("tipo");
		String strCampos = request.getParameter("campos");
		String strCamposDet = request.getParameter("camposDet");
		String strTabla = request.getParameter("tabla");
		String strTablaDet = request.getParameter("tablaDet");
		String strCXP = request.getParameter("idCXP");
		String strCLC = request.getParameter("idCLC");
		String strNomIdEnc = request.getParameter("idEnc");
		String strNomIdDet = request.getParameter("idDet");
		String strNomCXP = request.getParameter("nomCXP");
		String strNomDetFolio = request.getParameter("nomDetFolio");
		String strLetratipoP = request.getParameter("tipoPago");
		
			
		EjercidoPagadoValidar ejercido = new EjercidoPagadoValidar();
		ejercido.setReportPath( reportPath );
		//Valida si el encabezado cxp y sus detalles son iguales en clc_sicop
		String strFolioEjercidoV = request.getParameter("idfolioEjercido");
		String strFolioPagadoV = request.getParameter("idfolioPagado");
		if(strTipo.equals("validar")){
			JSONObject valor = ejercido.validar(strCampos,strCamposDet,strTabla,strTablaDet,strCXP,strCLC,strNomIdEnc,strNomIdDet,strFolioEjercidoV,strFolioPagadoV);
			out.println(valor);
		}
		
		/*if(strTipo.equals("validar")){
			boolean valor = ejercido.validar(strCampos,strCamposDet,strTabla,strTablaDet,strCXP,strCLC,strNomIdEnc,strNomIdDet);
			if(valor){
				System.out.println("valor true_"+valor);
				out.println("true");
			}else{
				System.out.println("valor false_"+valor);
				out.println("false");
			}
		}*/
		//Despues de validar correctamente ingresa informacion a tEjercidoEncabezado y tEjercidoDetalle.
		else if(strTipo.equals("aplicar")){
			String strfPago = request.getParameter("fPago");
			String strUsuario = request.getParameter("usuario");
			String strFolioEjercido = request.getParameter("idfolioEjercido");
			String strFolioPagado = request.getParameter("idfolioPagado");
			String strNomCamposDetValidar = request.getParameter("nomCamposDetValidar");
			String strStatusSiaff = request.getParameter("statusSiaff");
			
			String valor2 = ejercido.aplicar(strCampos,strCamposDet,strTabla,strTablaDet,strCXP,strCLC,strNomCXP,strTipo,strNomDetFolio,strLetratipoP,strfPago,strUsuario,strFolioEjercido,strFolioPagado,strNomCamposDetValidar,strStatusSiaff);
						
			if(valor2.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else if(valor2.equals("Ejercido")){
				out.println("{\"estatus\":\"Ejercido\"}");
				
			}else if(valor2.equals("Pagado")){
				out.println("{\"estatus\":\"Pagado\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor2+"\"}");
				//out.println("{\"estatus\":\"incorrecto\"}");
			}
		}
		//Verifica si existe en tEjercidoEncabezado, si existe ya no lo muestra.
		else if(strTipo.equals("existe")){
			String strIdCab = request.getParameter("idCab");
			String strNomCampoId = request.getParameter("nomCampoId");
			boolean existe = ejercido.existe(strTabla,strIdCab,strNomCampoId);
			if(existe){
				System.out.println("true");
				out.println(existe);
			}else{
				System.out.println("false");
				out.println(existe);
			}
		}
		else if(strTipo.equals("existeIntegracion")){
			String strIdCab = request.getParameter("idCab");
			String strNomCampoId = request.getParameter("nomCampoId");
			boolean existeIntegracion = ejercido.existeIntegracion(strTabla,strIdCab,strNomCampoId);
			if(existeIntegracion){
				System.out.println("true");
				out.println(existeIntegracion);
			}else{
				System.out.println("false");
				out.println(existeIntegracion);
			}
		}else if(strTipo.equals("validarIntegracion")){
			String strTipoCxp = request.getParameter("tipoCxp");
			String strFolioEjercidoI = request.getParameter("idfolioEjercido");
			String strFolioPagadoI = request.getParameter("idfolioPagado");
			
			JSONObject valor = ejercido.validarIntegracion(strTabla,strCXP,strCLC,strTipoCxp,strFolioEjercidoI,strFolioPagadoI);
			out.println(valor);
		}else if(strTipo.equals("aplicarIntegracion")){
			String strfPago = request.getParameter("fPago");
			String strUsuario = request.getParameter("usuario");
			String strFolioEjercido = request.getParameter("idfolioEjercido");
			String strFolioPagado = request.getParameter("idfolioPagado");
			String strNomCamposDetValidar = request.getParameter("nomCamposDetValidar");
			String strStatusSiaff = request.getParameter("statusSiaff");
			
			String valor2 = ejercido.aplicarIntegracion(strTabla,strCXP,strCLC,strfPago,strUsuario,strFolioEjercido,strFolioPagado,strNomCamposDetValidar,strStatusSiaff);
			if(valor2.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			}else{
				out.println("{\"estatus\":\"incorrecto\"}");
			}
		}else if(strTipo.equals("aplicarDiferentes")){
			String strfPago = request.getParameter("fPago");
			String strUsuario = request.getParameter("usuario");
			String strFolioEjercido = request.getParameter("idfolioEjercido");
			String strFolioPagado = request.getParameter("idfolioPagado");
			String strNomCamposDetValidar = request.getParameter("nomCamposDetValidar");
			String strStatusSiaff = request.getParameter("statusSiaff");
			
			String valor2 = ejercido.aplicarDiferentes(strTabla,strCXP,strCLC,strfPago,strUsuario,strFolioEjercido,strFolioPagado,strStatusSiaff);
			if(valor2.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			}else{
				out.println("{\"estatus\":\"incorrecto\"}");
			}
		}else if(strTipo.equals("aplicarContable")){
			
			String strTipoAplicacion = request.getParameter("tipoAplicacion");
			String strFolioPago = request.getParameter("nFolio");
			//System.out.println(strTipoAplicacion+" _ "+strFolioPago);
			String valor3 = ejercido.aplicarContable(strTipoAplicacion, strFolioPago);
			
			if(valor3.equals("correcto")){
				out.println("{\"estatus\":\"correcto\"}");
			}else{
				out.println("{\"estatus\":\""+valor3+"\"}");
				//out.println("{\"estatus\":\"incorrecto\"}");
			}
			
		}else if(strTipo.equals("validarCXPIntegracion")){
			
			String strCaNoContrarrecibo = request.getParameter("idCXP");
			String strSicop = request.getParameter("idCLC");
			String strFolios = request.getParameter("nFolios");
			String strTipoDocumentos = request.getParameter("tipoDoc");
			String strFolioIntegracion = request.getParameter("nFolioIntegracion");
			
			JSONObject valor4 = ejercido.validarCXPIntegracion(strCaNoContrarrecibo, strSicop, strFolios, strTipoDocumentos, strFolioIntegracion);
			out.println(valor4);
			
		}else if(strTipo.equals("aplicarCXPIntegracion")){
			
			String strCaNoContrarrecibo = request.getParameter("idCXP");
			String strSicop = request.getParameter("idCLC");
			String strFolios = request.getParameter("nFolios");
			String strTipoDocumentos = request.getParameter("tipoDoc");
			String strfPago = request.getParameter("fPago");
			String strUsuario = request.getParameter("usuario");
			String strFolioEjercido = request.getParameter("idfolioEjercido");
			String strFolioPagado = request.getParameter("idfolioPagado");
			String strStatusSiaff = request.getParameter("statusSiaff");
			
			String valor2 = ejercido.aplicarCXPIntegracion(strCaNoContrarrecibo, strSicop, strFolios, strTipoDocumentos, strfPago, strUsuario, strFolioEjercido, strFolioPagado, strStatusSiaff);
			
			if(valor2.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			}else{
				
				out.println("{\"estatus\":\"incorrecto\"}");
			}
			
		}else if(strTipo.equals("existePagado")){
			
			String strIdCab = request.getParameter("idCab");
			String strNomCampoId = request.getParameter("nomCampoId");
			String existePagado = ejercido.existePagado(strTabla,strIdCab,strNomCampoId);
			
			if(existePagado.equals("Ejercido")){
				
				out.println("{\"estatus\":\"Ejercido\"}");
				
			}if(existePagado.equals("Pagado")){
				
				out.println("{\"estatus\":\"Pagado\"}");
				
			}if(existePagado.equals("noExiste")){
				
				out.println("{\"estatus\":\"noExiste\"}");
			}
		}else if(strTipo.equals("existeIntegracionPagado")){
			
			String strIdCab = request.getParameter("idCab");
			String strNomCampoId = request.getParameter("nomCampoId");
			String existePagadoIntegracion = ejercido.existeIntegracionPagado(strTabla,strIdCab,strNomCampoId);
			
			if(existePagadoIntegracion.equals("Ejercido")){
				
				out.println("{\"estatus\":\"Ejercido\"}");
				
			}if(existePagadoIntegracion.equals("Pagado")){
				
				out.println("{\"estatus\":\"Pagado\"}");
				
			}if(existePagadoIntegracion.equals("noExiste")){
				
				out.println("{\"estatus\":\"noExiste\"}");
			}
		}else if(strTipo.equals("aplicarContableVarios")){
			
			String nFolios = request.getParameter("nFolio");
			String tipoDoc = "PAGADO";
			String fPago = request.getParameter("fPago");
			
			String valor = ejercido.aplicarContablementeVarios(nFolios,tipoDoc,fPago);
			
			if(valor.equals("correcto")){
				out.println("{\"estatus\":\"correcto\"}");
			}else{
				out.println("{\"estatus\":\""+valor+"\"}");
				
			}
		}else if(strTipo.equals("ejercidoPagadoVarios")){
			
			String cuentaPorPagar = request.getParameter("cuentaPorPagar");
			EjercidoPagadoValidarVarios ejercidoPagadoVarios = new EjercidoPagadoValidarVarios(GestionInterface.ATT_CONEXION);
			
			ejercidoPagadoVarios.setReportPath(reportPath);
			JSONObject valor = ejercidoPagadoVarios.EjercidoPagadoVarios(cuentaPorPagar);
			out.println(valor);
			
		}else if(strTipo.equals("aplicarMasivo")){ // Relacion Gastos Masivo CONAFOR
			
			int strFolioTempGral = Integer.parseInt(request.getParameter("folioTempGral"),10);
			boolean esFirmaElectronica = "S".equals(request.getParameter("esFIEL") );
			
			log.info("Iniciando aplicacion de RG Masiva " + 
					(esFirmaElectronica?"Con firma electronica.":"Por firma autografa" ) );						 
			FirmanteBussinessLogic fbl = new FirmanteBussinessLogic( GestionInterface.ATT_CONEXION );
			List<Firmante> firmantes = null;
			
			if( esFirmaElectronica )
				firmantes = fbl.readFromRequest( request );
				
			ejercido.setReportPath( reportPath );
			ejercido.setUsuario(usuario);
				
			String valor2 = ejercido.aplicarMasivoRelacionGastos(strFolioTempGral,strTabla, fg, usuario, esFirmaElectronica, firmantes);
					
			if(valor2.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor2+"\"}");
			}
		}else if(strTipo.equals("aplicacionManual")){ // Aplicacion de Ejercido y Pagado MANUAL
			
			String stridFolio = request.getParameter("idFolio");
			String strfPago = request.getParameter("fPago");
			String strUsuario = request.getParameter("usuario");
			String strCTAB = request.getParameter("ctab");	
			String strAccion = request.getParameter("accion");	
			
			String valor2 = ejercido.aplicarManualmenteRGProveedorIP(strCXP, strTabla, strTablaDet, strfPago, strUsuario, strFolioEjercidoV, strFolioPagadoV, strCTAB, strAccion);
					
			if(valor2.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor2.replaceAll("\n", " ")+"\"}");
				//out.println("{\"estatus\":\"incorrecto\"}");
			}
		}else if(strTipo.equals("aplicacionManualNuevo")){ // Aplicacion de Ejercido y Pagado MANUAL
			String stridFolio = request.getParameter("idFolio");
			String strfPago = request.getParameter("fPago");
			String strUsuario = request.getParameter("usuario");
			String strCTAB = request.getParameter("ctab");	
			
			boolean pagoParcial = "S".equals( request.getParameter("parcial") );
			String valor2 = ejercido.aplicarManualmenteRGProveedorIP_Nuevo(stridFolio, strfPago, strUsuario, strCTAB, pagoParcial);
			
					
			if(valor2.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor2.replaceAll("\n", " ")+"\"}");
				//out.println("{\"estatus\":\"incorrecto\"}");
			}
		}else if(strTipo.equals("aplicarISRLaudos")){ // Aplicacion de Ejercido y Pagado de ISR de Laudos
			
			String strFolios = request.getParameter("cfolios");
			String strfPago = request.getParameter("fPago");
			String strUsuario = request.getParameter("usuario");
			String strctab = request.getParameter("ctab");
			
			String valor = ejercido.aplicacionISRLaudos(strFolios, strfPago, strUsuario, strctab);
					
			if(valor.equals("Insertado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor+"\"}");
			}
		}else if(strTipo.equals("aplicarPagosDiversosRG")){ // Aplicacion de Ejercido y Pagado de Pagos Diversos Integrados
			String strFolios = request.getParameter("cfolios");
			String strfPagado = request.getParameter("fPagado");
			String strUsuario = request.getParameter("usuario");
			String strctab = request.getParameter("ctab");
			String tipoPago = request.getParameter("cTipoPagoRad");
			/*VGC20151223 Cambio para la apliacion parcial*/
			boolean pagoParcial = "S".equals( request.getParameter("parcial") );
			String valor = ejercido.aplicacionPagosDiversosRG(strFolios, strfPagado, strUsuario, strctab, pagoParcial, tipoPago);
			if(valor.equals("Insertado")){
				out.println("{\"estatus\":\"correcto\"}");
			}else{
				out.println("{\"estatus\":\""+valor.replaceAll("\n", " ")+"\"}");
			}
		}else if(strTipo.equals("AplicarPagosFuera")){ // Aplicacion de Ejercido y Pagado de Pagos por Fuera
			
			String strFolio = request.getParameter("nFolio");
			String strTipoPago = request.getParameter("tipoPago");
			String fPago = request.getParameter("fPago");
			String fEjer = request.getParameter("fEjer");
			String strUsuario = request.getParameter("usuario");
			String strfolioSICOP = request.getParameter("folioSICOP");
			String strsolPago = request.getParameter("solPago");
			String strnumProceso = request.getParameter("numProceso");
			String strfolioSIAFF = request.getParameter("folioSIAFF");			
			String strCTABLaudo = request.getParameter("CTAB");
			
			String valor = ejercido.aplicacionPagosFuera(strFolio, strCXP, strTipoPago, fPago, fEjer, strUsuario, strfolioSICOP, strsolPago, strnumProceso, strfolioSIAFF, strCTABLaudo);
					
			if(valor.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor.replaceAll("\n", " ")+"\"}");
			}
		}else if(strTipo.equals("AplicarPagosFueraNomina")){ // Aplicacion de Ejercido y Pagado de Pagos por Fuera
			
			String strFolio = request.getParameter("nFolio");
			String strTipoPago = request.getParameter("tipoPago");
			String fPago = request.getParameter("fPago");
			String fEjer = request.getParameter("fEjer");
			String strUsuario = request.getParameter("usuario");
			String strCtaBancaria = request.getParameter("ctaBancaria");
			String strfolioSICOP = request.getParameter("folioSICOP");
			String strsolPago = request.getParameter("solPago");
			String strnumProceso = request.getParameter("numProceso");
			String strfolioSIAFF = request.getParameter("folioSIAFF");			
			
			String valor = ejercido.aplicacionPagosFueraNomina(strFolio, strCXP, strTipoPago, fPago, fEjer, strUsuario, strCtaBancaria, strfolioSICOP, strsolPago, strnumProceso, strfolioSIAFF);
					
			if(valor.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor.replaceAll("\n", " ")+"\"}");
			}
		}else if(strTipo.equals("AplicarPagosPorReintegro")){ // Aplicacion de Ejercido y Pagado de Pagos por Reintegro
			
			String strFolio = request.getParameter("nFolio");
			String strTipoPago = request.getParameter("tipoPago");
			String fPago = request.getParameter("fPago");
			String fEjer = request.getParameter("fEjer");
			String strUsuario = request.getParameter("usuario");
			String strfolioSICOP = request.getParameter("folioSICOP");
			String strsolPago = request.getParameter("solPago");
			String strnumProceso = request.getParameter("numProceso");
			String strfolioSIAFF = request.getParameter("folioSIAFF");
			String ctaBancarias = request.getParameter("ctaBancarias");			
			
			String valor = ejercido.aplicacionPagosPorReintegro(strFolio, strCXP, strTipoPago, fPago, fEjer, strUsuario, strfolioSICOP, strsolPago, strnumProceso, strfolioSIAFF, ctaBancarias);
					
			if(valor.equals("ingresado")){
				out.println("{\"estatus\":\"correcto\"}");
			
			}else{
				out.println("{\"estatus\":\""+valor.replaceAll("\n", " ")+"\"}");
			}
		}
	}	
	catch(Exception e){
		e.printStackTrace( );
		System.out.println("catch_" +e);
		out.println("false");
	}
}else{
	out.println("{\"sinSesion\":\"sinSesion\"}");
}	
%>
