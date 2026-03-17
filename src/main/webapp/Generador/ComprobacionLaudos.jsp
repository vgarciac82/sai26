<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.Date"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.core.Empleado"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>

<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String operador = usuario.getNombre();
	String cUR =usuario.getU_UR();
	int idTipoCaso = c.getIdTC();
	String today = Util.getTodayESMX();
	String fAplicacion = "";
	String uLogin =usuario.getLogin();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();	
	String folio = c.getFolio();
	int idCaso = c.getIdCaso();
	int id_oper = -1;
	
	String numeroEmpleado = usuario.getNumeroEmpleado();
	
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	String msg;
	
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	/*FAVV20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic( GestionInterface.ATT_CONEXION );
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null?"CP":cabl.getSystemSetting("CXP_PREFIJO");
	boolean esRadicado = "S".equalsIgnoreCase( cabl.getSystemSetting("MUESTRA_RADICADO_LAUDOS") );
	
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION );
	String ef = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
	
	if( ef.equalsIgnoreCase( String.valueOf( ( new GregorianCalendar()  ).get(Calendar.YEAR) ) ))
		fAplicacion = today;
	else
		fAplicacion = "31/12/" + ef;
		
	String nombreElabora = e.getNombre();
	String aPaternoElabora = e.getApellidoPaterno();
	String aMaternoElabora= e.getApellidoMaterno();
	String puestoElabora = e.getCargo();
	
	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comprobacion de Laudos</title>

<!-- Estilos estandar para los controles JQuery -->
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/Firmantes.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"> </script>
	
	
	<script type="text/javascript" src="js/ComprobacionLaudos.js"></script>
	<script type="text/javascript" src="js/ValidaMesContable.js"></script>
	<script type="text/javascript" src="js/ActualizaFIEL.js" charset="UTF-8"></script>

<script type="text/javascript">
	var esConsulta = <%=esConsulta%>;
	//--------INICIO Variables Globales---------------------------------------------------------------------------------------------------
	var tips = $(".validateTips");
	var tablaSolicitudesCaja;
	var  nfolioCaja;
	var operacionActual = <%=c.getCasoOperacion(0).getOperacion().getIdOperacion()%>;
	var operador = "<%=operador%>";
	var cURUsuario = "<%=cUR%>";
	var esRadicado = <%=esRadicado%>;
	var breturnVal = false, bCOMSOC = false;
	var cNombreElabora = "<%=nombreElabora%>";
	var cApellidoPaternoElabora  = "<%=aPaternoElabora%>";
	var cApellidoMaternoElabora  = "<%=aMaternoElabora%>";
	var cPuestoElabora = "<%=puestoElabora%>";
	
	/*FAV20171019 Se guarda en base el prefijo de CxP*/
	var cxpPrefijo = "<%=cxpPrefijo%>";
	//-------FIN Variables Globales-------------------------------------------------------------------------------------------------------------
	$(document).ready(function() {
		init();
		$("#botonCambia").button();
		$("#btnBeneficiario").button();
		$("#nIdClaveEgresos2").button();
		$("#agrega2").button();
		$("#Agregar").button();
		$("#Limpia").button();
	});
</script>
</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0"
	topMargin="0">
	<form id="mainFrm">
		<input type="hidden" name="cEsRadicado" id="cEsRadicado" value="N"/>
		<input type="hidden" name="cEsDevengado" id="cEsDevengado" value="N"/>
		<input type="hidden" name="cEsLiquidacion" id="cEsLiquidacion" value="N"/>
		<input type="hidden" name="FOLIO" id="FOLIO" value="<%=folio%>"/>
		<input type="hidden" name="existe" id="existe" value=" " /> 
		<input type="hidden" value="6" id="cTipoRfc"/> 
		<input type="hidden" id="numPaso"/> 
		<input type="hidden" id="retencionesSICOP"/>  
		<input type="hidden" value="" id="TIPO_CONCEPTO" name="TIPO_CONCEPTO"/> 
		<input type="hidden" value="RELACIONGASTOS" id="cDocumento" name="cDocumento"/>
		<input type="hidden" value="RELACIONGASTOS" id="cTipoPago" name="cTipoPago"/>  
		<input type="hidden" value="<%=request.getParameter("folio")%>" id="nFolioPago" name="nFolioPago"/>
		<input type="hidden" value="NORE" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO"/> 
		<input type="hidden" value="NORE" id="idDestinoGasto" name="idDestinoGasto"/> 
		<input type="hidden" value="NORE" id="DESTINO_GASTO" name="DESTINO_GASTO"/>  
		<input type="hidden" name="cCtaBanc" id="cCtaBanc" value="N/A"/> 
		<input type="hidden" name="dCtaBanc" id="dCtaBanc" value="N/A"/> 
		<input type="hidden" name="TO_TIPO_DOCTO" id="TO_TIPO_DOCTO" value="RELGASTO" /> 
		<input type="hidden" id="cUnidadEjecutora" name="cUnidadEjecutora" value="<%=cUR%>" /> 
		<input type="hidden" id="aEjercicioFiscal" value="" name="aEjercicioFiscal" /> 
		<input type="hidden" value="" id="cEjercicio" name="cEjercicio" />
		<input type="hidden" value="" id="cMeta" name="cMeta" />

		<!-- para funcion revisar si realmente se requieren -->
		<input type="hidden" id="cEvento" name="cEvento" value="CD_AL01" /> 
		<input type="hidden" id="eventoPoliza" name="eventoPoliza" value=""/> 
		<input type="hidden" id="nMes" name="nMes" /> 
		<input type="hidden" id="cMes" name="cMes" /> 
		<input type="hidden" id="altaAlmacen" name="altaAlmacen" value="0" /> 
		<input type="hidden" id="cAnioFactEP" name="cAnioFactEP" value="0"/> 
		<input type="hidden" id="nFacturaEP" name="nFacturaEP" value="0"/> 
		<input type="hidden" id="ALM" name="ALM" value="No Aplica"/> 
		<input type="hidden" id="usur" name="usur" value="<%=uLogin%>" />  
		<input type="hidden" id="id_oper" name="id_oper" value="<%=id_oper%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" /> 
		<input type="hidden" id="vOGT" name="vOGT" value="" /> 
		<input type="hidden" name="cIdCuentaContable" id="cIdCuentaContable" /> 
		<input type="hidden" name="cIdRFC_RelacionGasto2" id="cIdRFC_RelacionGasto2" /> 
		<input type="hidden" id="mImporteNetoEP" name="mImporteNetoEP" value="0"/> 
		<input type="hidden" id="mImporteIvaEP" name="mImporteIvaEP" value="0"/>
		<input type="hidden" id="cOBGT" name="cOBGT" />
		<input type="hidden" id="mImporteISRLaudos" name="mImporteISRLaudos" value="0"/>
		<input type="hidden" id="montoprevio" name="montoprevio"/>
		<input type="hidden" id="nFolioApartado" name="nFolioApartado" value=""/>
		<input type="hidden" id="mImporteMasIva" name="mImporteMasIva" value="0" />
		<input type="hidden" id="noSolicitudCaja" name="noSolicitudCaja" value="0" />
		<input type="hidden" id="cRamo" name="cRamo" value="16" />
		<input type="hidden" id="otrosImpuestos" name="otrosImpuestos" value="0"/>
		<input type="hidden" id="FechaAplAptd" name="FechaAplAptd" value="<%=today%>"/>
		<input type="hidden" id="fAplicacion" name="fAplicacion" value="<%=fAplicacion%>"/>
		<input type="hidden" id="elcontra" name="elcontra" value=""/>
		<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza" value=""/>
		<input type="hidden" id="campo" name="campo" value=""/>
		<input type="hidden" id="cMotivoRechazoCRUD" name="cMotivoRechazoCRUD" />	
		<input type="hidden" id="mImporteHospedaje" name="mImporteHospedaje" value="0" />
		<input type="hidden" id="nIdEstado" name="nIdEstado" value="0"/>
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
		<input type="hidden" id="tablaEnc" name="tablaEnc" value=""/>
		<input type="hidden" id="cRetSICOP" name="cRetSICOP" value=""/>
		<input type="hidden" id="firmanteExiste" name="firmanteExiste" />
		<input type="hidden" id="elcontraTemp" name="elcontraTemp" value=""/>
		<input type="hidden" id="Fecha_Pago" name="Fecha_Pago" value=""/>	
		<input type="hidden" id="campoCondicion" name="campoCondicion" value=""/> 
		<input type="hidden" id="tablaDet" name="tablaDet" value=""/> 
		<input type="hidden" id="tipoAplicar" name="tipoAplicar" value=""/>
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" />
		<input type="hidden" id="txtFolioFact" name="txtFolioFact" value="<%=request.getParameter("folio")%>"/>
		<input type="hidden" id="tipoTramite" name="tipoTramite" value="<%=idTipoCaso %>"/>
		
		<!-- hidden para los firmantes  -->
		<input type="hidden" id="cNombreEmpleado" name="cNombreEmpleado" value=""/>
		<input type="hidden" id="cPaternoEmpleado" name="cPaternoEmpleado" value=""/>
		<input type="hidden" id="cMaternoEmpleado" name="cMaternoEmpleado" value=""/>
		<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value=""/>
		<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value=""/>
		<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
		<input type="hidden" id="numeroEmpleadoVoBo" name="numeroEmpleadoVoBo" value=""/>
		<input type="hidden" id="numeroEmpleadoAutoriza" name="numeroEmpleadoAutoriza" value=""/>
		<input type="hidden" id="numeroEmpleado" name="numeroEmpleado" value="<%=numeroEmpleado%>"/>		
		<input type="hidden" id="cNombreVo" name="cNombreVo" />
		<input type="hidden" id="cPaternoVo" name="cPaternoVo" />
		<input type="hidden" id="cMaternoVo" name="cMaternoVo" />
		<input type="hidden" id="cPuestoVo" name="cPuestoVo" />
		<input type="hidden" id="cEmpleadoVo" name="cEmpleadoVo" />
		<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" />
		<input type="hidden" id="cEmpleadoA" name="cEmpleadoA" />
		<input type="hidden" id="tipoFirmante" name="tipoFirmante" value="PAGO_VOBO"/>
		<input type="hidden" id="cNombreA" name="cNombreA" />
		<input type="hidden" id="cPaternoA" name="cPaternoA"/>
		<input type="hidden" id="cMaternoA" name="cMaternoA" />
		<input type="hidden" id="cPuestoA" name="cPuestoA" />
		<input type="hidden" id="firmanteAut" name="firmanteAut"/>
		<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" value="PAGO_AUT"/>
		
		<!-- hidden para los firmantes del VoBo y Autoriza -->
		<input type="hidden" name="cNombreVoBo" id="cNombreVoBo" value=""/>
		<input type="hidden" name="cPaternoVoBo" id="cPaternoVoBo" value=""/>
		<input type="hidden" name="cMaternoVoBo" id="cMaternoVoBo" value=""/>
		<input type="hidden" name="cPuestoVoBo" id="cPuestoVoBo" value=""/>
		<input type="hidden" name="cNombreAut" id="cNombreAut" value=""/>
		<input type="hidden" name="cPaternoAut" id="cPaternoAut" value=""/>
		<input type="hidden" name="cMaternoAut" id="cMaternoAut" value=""/>
		<input type="hidden" name="cPuestoAut" id="cPuestoAut" value=""/>
		
		<!-- hidden para los firmantes de suplencia de VoBo y Autoriza --> 
		<input type="hidden" name="cNombreTitular" id="cNombreTitular" value=""/>
		<input type="hidden" name="cPaternoTitular" id="cPaternoTitular" value=""/>
		<input type="hidden" name="cMaternoTitular" id="cMaternoTitular" value=""/>
		<input type="hidden" name="cPuestoTitular" id="cPuestoTitular" value=""/>
		<input type="hidden" name="cNombreTitularVoBo" id="cNombreTitularVoBo" value=""/>
		<input type="hidden" name="cPaternoTitularVoBo" id="cPaternoTitularVoBo" value=""/>
		<input type="hidden" name="cMaternoTitularVoBo" id="cMaternoTitularVoBo" value=""/>
		<input type="hidden" name="cPuestoTitularVoBo" id="cPuestoTitularVoBo" value=""/>
		
		<!-- hidden para actualizar los firmantes del VoBo y Autoriza -->
		<input type="hidden" name="cNombreVoBoUpdate" id="cNombreVoBoUpdate" value=""/>
		<input type="hidden" name="cPaternoVoBoUpdate" id="cPaternoVoBoUpdate" value=""/>
		<input type="hidden" name="cMaternoVoBoUpdate" id="cMaternoVoBoUpdate" value=""/>
		<input type="hidden" name="cPuestoVoBoUpdate" id="cPuestoVoBoUpdate" value=""/>
		<input type="hidden" name="cNombreAutUpdate" id="cNombreAutUpdate" value=""/>
		<input type="hidden" name="cPaternoAutUpdate" id="cPaternoAutUpdate" value=""/>
		<input type="hidden" name="cMaternoAutUpdate" id="cMaternoAutUpdate" value=""/>
		<input type="hidden" name="cPuestoAutUpdate" id="cPuestoAutUpdate" value=""/>
		
		<!-- hidden para actualizar los firmantes de suplencia de VoBo y Autoriza -->
		<input type="hidden" name="cNombreTitularUpdate" id="cNombreTitularUpdate" value=""/>
		<input type="hidden" name="cApellidoPaternoTitularUpdate" id="cApellidoPaternoTitularUpdate" value=""/>
		<input type="hidden" name="cApellidoMaternoTitularUpdate" id="cApellidoMaternoTitularUpdate" value=""/>
		<input type="hidden" name="cPuestoTitularUpdate" id="cPuestoTitularUpdate" value=""/>
		<input type="hidden" name="cNombreTitularVoBoUpdate" id="cNombreTitularVoBoUpdate" value=""/>
		<input type="hidden" name="cApellidoPaternoTitularVoBoUpdate" id="cApellidoPaternoTitularVoBoUpdate" value=""/>
		<input type="hidden" name="cApellidoMaternoTitularVoBoUpdate" id="cApellidoMaternoTitularVoBoUpdate" value=""/>
		<input type="hidden" name="cPuestoTitularVoBoUpdate" id="cPuestoTitularVoBoUpdate" value=""/>
	
		<!-- hidden para la captura de los datos de quien elaboro -->
		<input type="hidden" id="cNombreE" 	name="cNombreE" />
		<input type="hidden" id="cPaternoE" name="cPaternoE" />
		<input type="hidden" id="cMaternoE" name="cMaternoE" />
		<input type="hidden" id="cPuestoE" 	name="cPuestoE" />
		<input type="hidden" id="firmanteEla" name="firmanteEla" />
		
		<!-- hidden para la captura de oficio delegatorio -->
		<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value=""/>
		<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value=""/>
		<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value=""/>
		<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value=""/>
		<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value=""/>
		<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value=""/>
		<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value=""/>
		<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value=""/>
		<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value=""/>
		
		<!-- hidden para la captura de oficio delegatorio VoBo-->
		<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value=""/>
		<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value=""/>
		<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value=""/>
		<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value=""/>
		<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value=""/>
		<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value=""/>
		<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value=""/>
		<input type="hidden" name="tipoComprobacion" id="tipoComprobacion" value=""/>		
		<input type="hidden" name="cxpPrefijo" id="cxpPrefijo" value="<%=cxpPrefijo%>"/>
		<input type="hidden" id="cTipoFuente" name="cTipoFuente" value="1"/>
		<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value=""/>
		<input type="hidden" id="cPasivo" name="cPasivo" value=""/>
		<input type="hidden" id="nSiISSSTE" name="nSiISSSTE" value=""/>
		<input type="hidden" id="dRFCCuotas" name="dRFCCuotas" value=""/>		
				
		<input type="hidden" id="autorizadoPorFiel" name="autorizadoPorFiel" value="false"/>

		<div id="container" style="width: 70%" class="container" > 				
			<div class="card-header"> <h3> Comprobaci&oacute;n Laudos </h3> </div>
			<hr class="mt-3"/>
			
			<div id="divImprimePoliza">
				<img src="imagenes/Imprimir.png" width="25" height="21" onClick="cmdImprimir('PolizaPago');">
				Poliza
			</div>
			<span id="EditaFirmas" style="visibility:hidden">
				<a href="#" onclick="updateFirmantes();">Firmas*</a>
			</span>	
			<div id="dialog-form" title="Aplicación Presupuestal/Contable">	
				<div id="divEspera" align="center">Espere por favor....
				  <img border="0" src="../imagenes/espera.gif" height="30">
				</div>
				<div id="divAplica" >				
					<iframe id="ifAplica" src="about:blank"></iframe>
				</div>
			</div>
			<div id="dialog-Procesando" title="Procesando">
	  			<div id="divEsperaProcesando" style="visibility: hidden" align="center">Espere por favor....
				  <img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
			
			<div id="DialogSolCaja">
				<h5> Solicitudes de Gastos Anticipados para Laudos </h5>
				<hr class="mt-3"/>
				
				<div id="laudoDevengado">
					<div class="row">
						<div class="col-12 col-md-6 mb-3 d-flex">	
							<div class="form-check">
								<input type="checkbox" id="chkLaudoDevengado" name ="chkLaudoDevengado" class="form-check-input" onclick="laudoDevengadoClick();"/>
								<label id="laudoDevengadoLbl" class="form-check-label">Solicitud de Laudos Devengada</label>																														
							</div>
						</div>
					</div>										
				</div>
				
				<div id="liquidacionDevengado">
					<div class="row">
						<div class="col-12 col-md-6 mb-3 d-flex">	
							<div class="form-check">
								<input type="checkbox" id="chkLiquidacionDevengado" name ="chkLiquidacionDevengado" class="form-check-input" onclick="liquidacionDevengadoClick();"/>
					<label id="liquidacionDevengadoLbl" class="form-check-label">Solicitud de Liquidación Devengada</label>																													
							</div>
						</div>
					</div>
				</div>
				
				<label style="font-size: 12px; font-weight: bold;">Dar doble clic sobre la solicitud que desea comprobar</label>
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<table id="tblSolicitudesB" class="table table-striped table-bordered">							
							<thead>
								<tr>
									<th>#FolioCaja</th>
									<th>Concepto</th>
									<th>MontoSolicitud</th>
									<th>TipoAnticipo</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
							
			</div>
			
			<div id="solicitudAnticipadaDiv" style="display: block;">	
				<h5> Solicitud de Caja </h5>
				<hr class="mt-3"/>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="nfolioCaja"> Folio Caja: </label>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">														
						<input name="nfolioCaja" type="text" id="nfolioCaja" size="5" maxlength="40" readonly class="form-control form-control-sm" onchange="actualizaFolioCaja();"> 							 							
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="montoSol"> Anticipo: </label>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">														
						<input name="montoSol" type="text" id="montoSol" size="5" maxlength="40" readonly class="form-control form-control-sm"> 							 							
					</div>					
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="cDescripcionCaja"> Descripci&oacute;n: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">													
						<textarea rows="3" cols="47" id="cDescripcionCaja" name="cDescripcionCaja" readonly class="form-control form-control-sm"></textarea> 							 							
					</div>
				</div>				
			</div>
			
			<h5> Cambio tipo de solicitud </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">							
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
					<input id="botonCambia" name="botonCambia" type="button" value="Cambiar" onclick="OpenDialogSolicitudes()" class="btn btn-secondary btn-sm"/>
				</div>
			</div>
			
			<h5> Relacion con Ingreso Fiscal </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">							
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
						<label for="ING_F"> Folio Registro Ingreso: </label>
					</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
					<select id="nIdIntegracion" name="nIdIntegracion" class="form-select form-select-sm"></select> 				
				</div>
			</div>
			
			<h5> ¿Es pago al ISSSTE? </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">
					Activa si es pago de cuotas o aportaciones&nbsp;&nbsp;
					<input type="checkbox" class="form-check-input" id="Si_ISSSTE" name="Si_ISSSTE" onclick="habilita()"/>							
				</div>
			</div>
			
			<div id="divPagoISSSTE">
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">							
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
							<label for="dRFC_Cuotas"> RFC: </label>
						</div>	
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">			
						<select id="dRFC_Cuotas" name="dRFC_Cuotas" class="form-select form-select-sm"></select>
					</div>
				</div>
			</div>
					
			<div id="datosRG">
				<h5> Datos de la Comprobaci&oacute;n </h5>
				<hr class="mt-3"/>
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="cIdRelacion"> Folio Relaci&oacute;n: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
						<input type="text" id="cIdRelacion" name="cIdRelacion" style="text-transform:uppercase" class="form-control form-control-sm"/> 
						<input type="hidden" id="nombre" name="nombre" value="0"/>							 												
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="caNoContrarrecibo"> CxP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">														
						<input type="text" name="caNoContrarrecibo" id="caNoContrarrecibo" size="14" readonly class="form-control form-control-sm" />					
					</div>					
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="id_caso"> Folio: </label>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">													
						<input type="text" name="id_caso" id="id_caso" size="6" maxlength="40" readonly value="<%=request.getParameter("folio")%>" class="form-control form-control-sm"/>							
					</div>					
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="tipo_fuente"> Tipo Fuente: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">														
						<select id="tipo_fuente" name="tipo_fuente" onchange="cargaTipoDestino();" class="form-select form-select-sm">
								<option value="FF">Fondos Fiscales</option>
								<option value="IP">Ingresos Propios</option>
								<option value="CE">Credito Externo</option>
						</select> 							 						
						<input type="hidden" id="chk_retenciones" name="chk_retenciones" onchange="retencionesCambio();"/>	
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="fRecepcion"> F. Aplicacion: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>														
							<input name="fRecepcion" id="fRecepcion" size="10" value="<%=today%>" class="form-control form-control-sm" readonly/>			 
							<input name="fRecepcion2" id="fRecepcion2" type="hidden" value="<%=today%>" readonly />
						</div>							
					</div>					
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
						<label for="cIdRFC_RelacionGasto"> RFC PC: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">													
						<div class="input-group">
							<input type="text" name="cIdRFC_RelacionGasto" id="cIdRFC_RelacionGasto" style='text-transform:uppercase;' maxlength="15" size="13" onchange="obtenTipoClaveBenf()" readonly class="form-control form-control-sm" /> 
							<input type="button" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="cat_beneficiario()" class="btn btn-secondary btn-sm"/> 
						</div>
							<input type="text" name="cnombre" ID="cnombre" value="" maxlength="100" size="75" readonly class="form-control form-control-sm" />																
					</div>				
				</div>			
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex p-1">
					<div id="multitabs" style="width: 910px">
						<div class="row d-flex justify-content-center">	
											
				       		<ul class="nav nav-tabs" id="list-opciones">
					            <li class="nav-item" role="presentation">
					            	<button class="nav-link active" id="L01" data-bs-toggle="tab" data-bs-target="#tabs-1-concepto" type="button" role="tab" aria-controls="tabs-concepto" aria-selected="true">Concepto</button>
					            </li>
					            <li class="nav-item" role="presentation">
					            	<button class="nav-link pasoDos" id="L02" data-bs-toggle="tab" data-bs-target="#tabs-2-movimiento" type="button" role="tab" aria-controls="tabs-movimiento" aria-selected="false">Movimientos</button>
					            </li>
					            <li class="nav-item" role="presentation">
					            	<button class="nav-link pasoTres" id="L03" data-bs-toggle="tab" data-bs-target="#tabs-3-documento" type="button" role="tab" aria-controls="tabs-documento" aria-selected="false">Documentaci&oacute;n</button>
					            </li>
					            <li class="nav-item" role="presentation">
					            	<button class="nav-link pasoULTIMO" id="L04" data-bs-toggle="tab" data-bs-target="#tabs-4-final" type="button" role="tab" aria-controls="tabs-final" aria-selected="false"></button>
					        	</li>				           				           
					    	</ul>
							
							<div class="tab-content mt-3" id="tabContent">								
								<div class="tab-pane fade show active" id="tabs-1-concepto" role="tabpanel" aria-labelledby="tabs-concepto">
									
									<div class="row">										
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="mImporteNeto"> Importe Neto: </label>
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											<input name="mImporteNeto" type="text" id="mImporteNeto" class="form-control form-control-sm" onchange="sumaImpuestos();" onKeyPress="return onlyFloat(event);"/> 												 							 						
										</div>
									</div>	
									
									<div class="row">				
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>						
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="mImporteISRLaudosTotal"> Importe ISR: </label>
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											<input name="mImporteISRLaudosTotal" type="text" id="mImporteISRLaudosTotal" class="form-control form-control-sm" onchange="sumaImpuestos();" onKeyPress="return onlyFloat(event);"/> 												 							 						
										</div>
									</div>						
										
									<div class="row">				
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>						
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="mImporteBruto"> Total Solicitud: </label>
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											<input name="mImporteBruto" type="text" id="mImporteBruto" class="form-control form-control-sm" readonly/> 												 							 						
										</div>
									</div>	
									
									<div class="row">				
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>						
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="cConcepto"> Concepto: </label>
										</div>
										<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1">																			
											<textarea rows="5" cols="47" id="cConcepto" name="cConcepto" class="form-control form-control-sm"></textarea>							 							 						
										</div>
									</div>																			
								</div>
								
								<div class="tab-pane fade" id="tabs-2-movimiento" role="tabpanel" aria-labelledby="tabs-movimiento">
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>				
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<label for="tConcepto"> Tipo de Concepto </label>																		 							 					
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<label for="TIPO_MOVIMIENTO"> Tipo de Movimiento </label>																		 							 					
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<label for="totalsolicitud"> Monto Solicitud </label>																		 							 					
										</div>
									</div>
																		
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>		
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<select id="tConcepto" name="tConcepto" onchange="concepto();" class="form-select form-select-sm"></select> 																		 							 					
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<select id="TIPO_MOVIMIENTO" name="TIPO_MOVIMIENTO" class="form-select form-select-sm"></select> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="totalsolicitud" name="totalsolicitud" class="form-control form-control-sm" readonly/> 																		 							 					
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>				
										<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
											<label for="EP"> Estructura Programática </label>																		 							 					
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<input type="button" value="Agregar" onclick=" FiltroMovimientos();" id="agrega2" class="btn btn-secondary btn-sm" name="agrega2" title="Agrega EP" />&nbsp;
											<input type="button" value="Limpia" onclick="LimpiaEP();" id="Limpia" name="Limpia" class="btn btn-secondary btn-sm" title="Borra las EP's" />																													 							 					
										</div>										
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<label for="mMovimiento"> Monto Movimiento </label>																		 							 					
										</div>
									</div>
																		
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>		
										<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
											<input type="text" id="EP" name="EP" size="61" readonly class="form-control form-control-sm"/> 
											<input name="nClaveCNA1" type="hidden" id="nClaveCNA1" size="7" maxlength="7" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
											<input type="button" id="nIdClaveEgresos2" size="5" value="..." onclick="Grid()" class="btn btn-secondary btn-sm"/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input name="mMovimiento" type="text" id="mMovimiento" class="form-control form-control-sm" onChange="cambioMovmientos()" onkeypress="return onlyFloat(event)"/> 																		 							 					
										</div>
									</div>		
									
									<div class="row">												
										<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
										</div>																				
										<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">								
											<label for="tConcepto"> Monto Acumulado de la Operación: </label>																												 							 				
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input name="acumuladoOperacion" type="text" id="acumuladoOperacion" class="form-control form-control-sm" value="0" readonly/>																		 							 					
										</div>
									</div>							
									
									<div class="row d-flex">								
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
											<table id="grdMovimientos" class="table table-striped table-bordered">															
												<thead>
													<tr>
														<th>#Movto</th>
														<th>Estructura Programática SIAF</th>
														<th>Tipo Concepto</th>
														<th>Almacén</th>
														<th>Alta</th>
														<th>Importe</th>
														<th>Cuenta</th>
														<th>Clabe</th>
													</tr>
												</thead>
											</table>
										</div>
									</div>			
								</div>
								
								<div class="tab-pane fade" id="tabs-3-documento" role="tabpanel" aria-labelledby="tabs-documento">
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>				
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_FACTURA"> No. Factura </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_FECHA_FACTURA"> Fecha Factura </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_TBEN"> Tipo Beneficiario </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_CBEN"> Clave Beneficiario </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_TIPO_OPE"> Tipo Operación </label>																		 							 					
										</div>
									</div>
																		
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>		
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_FACTURA" name="DCD_FACTURA" class="form-control form-control-sm" value="N/A" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_FECHA_FACTURA" name="DCD_FECHA_FACTURA" class="form-control form-control-sm" value="<%=today%>" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_TBEN" name="DCD_TBEN" class="form-control form-control-sm" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_CBEN" name="DCD_CBEN" class="form-control form-control-sm" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<select id="DCD_TIPO_OPE" name="DCD_TIPO_OPE" onchange="concepto();" class="form-select form-select-sm" disabled class="notEditable">
												<option value="85" class="notEditable">85 OTROS</option>
											</select>															 							 					
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>				
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DESCRIPCION20"> Porcentaje IVA </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_IMP_BRUTO"> Importe Total </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_IVADES"> IVA Desglose </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_IVA"> IVA Retención </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_ISR"> ISR </label>																		 							 					
										</div>
									</div>
																		
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>		
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DESCRIPCION20" name="DESCRIPCION20" class="form-control form-control-sm" value="0" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_IMP_BRUTO" name="DCD_IMP_BRUTO" class="form-control form-control-sm" value="0" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_IVADES" name="DCD_IVADES" class="form-control form-control-sm" value="0" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_IVA" name="DCD_IVA" class="form-control form-control-sm" value="0" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_ISR" name="DCD_ISR" class="form-control form-control-sm" value="0" readonly/>		 							 					
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>				
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_MIL5"> Ret. 0.5% </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_MIL2"> Ret. 0.2% </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_CONTRIBUCION"> Beneficio social </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_OTRAS_RET"> Impuesto cedular </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_PENALIZACION"> Penalización </label>																		 							 					
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>		
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_MIL5" name="DCD_MIL5" class="form-control form-control-sm" value="0" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_MIL2" name="DCD_MIL2" class="form-control form-control-sm" value="0" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_CONTRIBUCION" name="DCD_CONTRIBUCION" class="form-control form-control-sm" value="0" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_OTRAS_RET" name="DCD_OTRAS_RET" class="form-control form-control-sm" value="0" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_PENALIZACION" name="DCD_PENALIZACION" class="form-control form-control-sm" value="0" readonly/>		 							 					
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>				
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_SANCION"> Sanci&oacute;n </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_DEVOL"> Devoluci&oacute;n </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_AMORT"> Amort. Anticipo </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_OTRAS_RET"> Retenci&oacute;n </label>																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="DCD_PENALIZACION"> Neto </label>																		 							 					
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>		
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_SANCION" name="DCD_SANCION" class="form-control form-control-sm" value="0" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_DEVOL" name="DCD_DEVOL" class="form-control form-control-sm" value="0" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_AMORT" name="DCD_AMORT" class="form-control form-control-sm" value="0" readonly/> 																		 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_OTRAS_RET" name="DCD_OTRAS_RET" class="form-control form-control-sm" value="0" readonly/>															 							 					
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<input type="text" id="DCD_PENALIZACION" name="DCD_PENALIZACION" class="form-control form-control-sm" value="0" readonly/>		 							 					
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>				
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
											<label for="DCD_CONCEPTO"> Concepto: </label>																																					 							 				
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																																					 							 				
										</div>										
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																		
											<input type="button" id="Agregar" value="Agregar" onclick="fnClickAddRowComp();" class="btn btn-secondary btn-sm" />																			 							 				
										</div>
									</div>
									
									<div class="row">												
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>		
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
											<textarea rows="5" cols="47" id="DCD_CONCEPTO" name="DCD_CONCEPTO" class="form-control form-control-sm" readonly></textarea> 																		 							 					
										</div>
									</div>

									<div class="row d-flex">								
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
											<table id="grdFacturas" class="table table-striped table-bordered">																
												<thead>
													<tr>
														<th nowrap>&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">Descuentos</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
														<th align="left">&nbsp;</th>
													</tr>
													<tr>
														<th nowrap>No. Factura</th>
														<th align="left">Fecha Factura</th>
														<th align="left">Tipo beneficiario</th>
														<th align="left">Cve Benef</th>
														<th align="left">Tipo oper</th>
														<th align="left">%IVA</th>
														<th align="left">Importe Total</th>
														<th align="left">IVA desglose</th>
														<th align="left">IVA</th>
														<th align="left">ISR</th>
														<th align="left">Ret. 0.5</th>
														<th align="left">Ret 0.2</th>
														<th align="left">Beneficio social</th>
														<th align="left">Impuesto cedular</th>
														<th align="left">Penalizaciones</th>
													</tr>
												</thead>
												<tbody>
												</tbody>
												<tfoot>
												</tfoot>
											</table>
										</div>
									</div>
									
								</div>
								
								<div class="tab-pane fade" id="tabs-4-final" role="tabpanel" aria-labelledby="tabs-final">
									<div class="row d-flex">								
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
											<table id="grdCompromisos" class="table table-striped table-bordered">															
												<thead>
													<tr>
														<th>Cuenta</th>
														<th>Subcuenta</th>
														<th>Saldo</th>
													</tr>
												</thead>
												<tbody>
												</tbody>
												<tfoot>
												</tfoot>
											</table>										
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</form>
	<!-- Dialogo Firmantes -->
		<jsp:include page="Firmantes.jsp"></jsp:include>
	<!-- Fin Dialogo Firmantes -->	
</body>
</html>
