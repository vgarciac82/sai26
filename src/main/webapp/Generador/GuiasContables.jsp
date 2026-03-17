
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";
	String aEjercicioFiscal = EjercicioFiscalBusinessLogic.getEjercicioFiscal();

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
	
	// ARLA20181105 
	boolean usrGenerarVersion = ( usuario.getPropiedad("GENERA_MANUALCONTABLE") != null && "SI".equalsIgnoreCase( usuario.getPropiedad("GENERA_MANUALCONTABLE").getValor() ) );
	boolean usrConsultaVersion = ( usuario.getPropiedad("CONSULTA_MANUALCONTABLE") != null && "SI".equalsIgnoreCase( usuario.getPropiedad("CONSULTA_MANUALCONTABLE").getValor() ) );	
	
	String msg = "";
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />

<title>Guias Contables</title>

<link rel="stylesheet" type="text/css"  href="css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="ISO-8859-1">	
	var usrGenerarVersion = <%=usrGenerarVersion%>;
	var usrConsultaVersion = <%=usrConsultaVersion%>;
	var msg = "<%=msg%>";	
	var oTable;
			
	$(document).ready(function() {
		
		inittable();
		reloadtable();		
		
		if( usrGenerarVersion ){
			$("#generaVersionDiv").show();
			$("#consultaVersionDiv").show();			
		}
		 else if( usrConsultaVersion ){
				$("#consultaVersionDiv").show();
				$("#generaVersionDiv").hide();						
				document.getElementById("id_Guia1").style.display = "none";
				document.getElementById("id_ManejoCuentas1").style.display = "none";	
				TempForm.getElementById("consultar").checked;			
		}
			
		$("#sinFirmar").val("0");	
	
		$("#cCentroContable").val( "<%=cCentroContable%>");
		$("#usuario").val("<%=algo%>");
		
		querySelectPost("catManejoCuentas", "id_ManejoCuentas", {async: false });
		querySelectPost("catGuiaContable", "id_Guia", {async: false });
		querySelectPost("catManejoCuentas1", "id_ManejoCuentas1", {async: false });
		querySelectPost("catGuiaContable1", "id_Guia1", {async: false });
		querySelectPost("catManejoCuentasV", "id_ManejoCuentasV", {async: false });
		querySelectPost("catGuiaContableV", "id_GuiaV", {async: false });				
		querySelectPost("readEjercicioFiscal", "nEjercicioFiscal", {async: false });
		
		$("#consultaVersion").button();
		$("#guardaVersion").button();
		$("#guiaTemporal").button();
		$("#descarga").button();
		$("#estadoFinanciero").button();
		$("#estadoFinanciero1").button();
		
		document.getElementById("consultaVersionDiv").style.display = "none";
		document.getElementById("creaVersionDiv").style.display = "none";
		document.getElementById("guardaVersion").style.display = "none";	
		document.getElementById("id_Guia").style.display = "none";
		document.getElementById("id_ManejoCuentas").style.display = "none";		
		document.getElementById("id_estadoFinanciero").style.display = "none";		
		document.getElementById("id_estadoPresupuestario").style.display = "none";
		document.getElementById("id_estadoProgramatico").style.display = "none";		
		document.getElementById("estadoFinanciero").style.display = "none";	
		document.getElementById("id_estadoFinanciero1").style.display = "none";	
		document.getElementById("id_estadoPresupuestario1").style.display = "none";
		document.getElementById("id_estadoProgramatico1").style.display = "none";		
		document.getElementById("estadoFinanciero1").style.display = "none";
		document.getElementById("id_Guia1").style.display = "none";
		document.getElementById("id_ManejoCuentas1").style.display = "none";
		document.getElementById("id_GuiaV").style.display = "none";
		document.getElementById("id_ManejoCuentasV").style.display = "none";	
		document.getElementById("tipoGuardar").style.display = "none";		
		document.getElementById("id_GuiaH").style.display = "none";
		document.getElementById("id_ManejoCuentasH").style.display = "none";
		document.getElementById("id_CatCuentasH").style.display = "none";
		document.getElementById("btnEditarH").style.display = "none";
		document.getElementById("id_CatCuentasInstructivoH").style.display = "none";
		document.getElementById("consultaVersionDiv").style.display = "none";
		document.getElementById("historicoNotas").style.display = "none";
			
		$("#guardaVersion").button().click(function() {
			if ($("#fecha").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Debes seleccionar la fecha de creación para la versión."});
				return;
			}
			
			if ($("#id_archivoV").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Debes seleccionar una opción a guardar"});
				return;
			}
			
			$("#id_ComplementoV").val("0");
			$("#fecha_aplicacion").val($("#fecha").val().split('-').reverse().join('/'));
			
			document.TempForm.action="../reportes/GuiasContables";
			document.TempForm.method="POST";
			document.TempForm.submit();
		});
		
		$("#btnGuardarH").button().click(function(){    	
			GuardarNota();
			reloadtable();
	    });	
		
		$("#btnEditarH").button().click(function(){    	
			EditarNota();
			reloadtable();
	    });	
	
		if (msg != "")
			Swal.fire({ icon: "success",
						text: msg });
			
	});
	
	function esEstadoFinanciero(){ //OK
		$("#id_estadoFinanciero").val("");
		$("#id_estadoPresupuestario").val("");
		$("#id_estadoProgramatico").val("");	
		$("#id_Guia").val("-1");
		$("#id_ManejoCuentas").val("-1");
		
		if($("#id_archivo").val() == "EstadoFinanciero" ){			
			document.getElementById("id_estadoFinanciero").style.display = "block";
			document.getElementById("estadoFinanciero").style.display = "block";	
			document.getElementById("id_estadoPresupuestario").style.display = "none";
			document.getElementById("id_estadoProgramatico").style.display = "none";
			document.getElementById("guiaTemporal").style.display = "none";
			document.getElementById("id_Guia").style.display = "none";
			document.getElementById("id_ManejoCuentas").style.display = "none";
		}
		else if($("#id_archivo").val() == "EstadoPresupuestario" ){
			document.getElementById("id_estadoFinanciero").style.display = "none";
			document.getElementById("estadoFinanciero").style.display = "block";	
			document.getElementById("id_estadoPresupuestario").style.display = "block";
			document.getElementById("id_estadoProgramatico").style.display = "none";
			document.getElementById("guiaTemporal").style.display = "none";
			document.getElementById("id_Guia").style.display = "none";
			document.getElementById("id_ManejoCuentas").style.display = "none";
		}
		else if($("#id_archivo").val() == "EstadoProgramatico" ){
			document.getElementById("id_estadoFinanciero").style.display = "none";
			document.getElementById("estadoFinanciero").style.display = "block";	
			document.getElementById("id_estadoPresupuestario").style.display = "none";
			document.getElementById("id_estadoProgramatico").style.display = "block";
			document.getElementById("guiaTemporal").style.display = "none";
			document.getElementById("id_Guia").style.display = "none";
			document.getElementById("id_ManejoCuentas").style.display = "none";
		}
		else if($("#id_archivo").val() == "GuiasContabilizadoras.jasper"){
			document.getElementById("id_Guia").style.display = "block";
			document.getElementById("id_estadoFinanciero").style.display = "none";
			document.getElementById("estadoFinanciero").style.display = "none";	
			document.getElementById("id_estadoPresupuestario").style.display = "none";
			document.getElementById("id_estadoProgramatico").style.display = "none";
			document.getElementById("id_ManejoCuentas").style.display = "none";
			document.getElementById("guiaTemporal").style.display = "block";
		}
		else if($("#id_archivo").val() == "ManejoDeCuentas.jasper"){
			document.getElementById("id_ManejoCuentas").style.display = "block";
			document.getElementById("id_estadoFinanciero").style.display = "none";
			document.getElementById("estadoFinanciero").style.display = "none";	
			document.getElementById("id_estadoPresupuestario").style.display = "none";
			document.getElementById("id_estadoProgramatico").style.display = "none";
			document.getElementById("id_Guia").style.display = "none";
			document.getElementById("guiaTemporal").style.display = "block";
		}else {
			document.getElementById("id_estadoFinanciero").style.display = "none";
			document.getElementById("estadoFinanciero").style.display = "none";	
			document.getElementById("id_estadoPresupuestario").style.display = "none";
			document.getElementById("id_estadoProgramatico").style.display = "none";
			document.getElementById("guiaTemporal").style.display = "block";
			document.getElementById("id_Guia").style.display = "none";
			document.getElementById("id_ManejoCuentas").style.display = "none";
		}			
	}
	
	/*Tab1 Consulta temporal*/
	function extraeTemporal(){ //OK		
		$("#consultar").val("1");
		document.TempForm.action="../reportes/GuiasContables";
		document.TempForm.method="POST";
		document.TempForm.target="_blank";
		document.TempForm.submit();
	}
	
	/*Tab1 Consulta temporal*/
	function extraer(){ //OK
		if($("#id_archivo").val() == "EstadoFinanciero"){
			if($("#id_estadoFinanciero").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Debes de seleccionar un estado financiero."});				
				return false;
			}
		}else if($("#id_archivo").val() == "EstadoPresupuestario"){	
			if($("#id_estadoPresupuestario").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Debes de seleccionar un informe presupuestal."});				
				return false;	
			}		
		}else if($("#id_archivo").val() == "EstadoProgramatico"){
			if($("#id_estadoProgramatico").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Debes de seleccionar un informe programatico."});				
				return false;	
			}
		}
	
		document.TempForm.action="../reportes/GuiasContables";
		document.TempForm.method="POST";
		document.TempForm.target="_blank";
		document.TempForm.submit();
	}	
	
	function extrae(){ //OK								
		document.TempForm.action="../reportes/GuiasContables";
		document.TempForm.method="POST";
		document.TempForm.target="_blank";
		document.TempForm.submit();		
		$("#consultar").val("0");
		document.TempForm.TIPO_REPORTE[0].checked = false;
		document.TempForm.TIPO_REPORTE[1].checked = false;
		document.TempForm.TIPO_REPORTE[2].checked = false;
		document.getElementById("consultaVersionDiv").style.display = "block";						
		document.getElementById("creaVersionDiv").style.display = "none";
		document.getElementById("temp").style.display = "none";					
	}	
	
	function temporal(){ //OK	//tabs-1
		$("#id_archivo").val("");
		esEstadoFinanciero();
		document.getElementById("seleccionaNivel").style.display = "block";
		document.getElementById("temp").style.display = "block";
		document.getElementById("creaVersionDiv").style.display = "none";
		document.getElementById("guardaVersion").style.display = "none";
		document.getElementById("tipoGuardar").style.display = "none";
		document.getElementById("consultaVersionDiv").style.display = "none";
		document.getElementById("historicoNotas").style.display = "none";
		document.getElementById("firmas").style.display = "block";		
	}
	
	function generaVersion(){ //OK	//tabs-2
		$("#fecha_aplicacion").val("");
		$("#id_archivoV").val("");
		seleccionaGrupo();
		
		document.getElementById("seleccionaNivel").style.display = "none";
		document.getElementById("temp").style.display = "none";	
		document.getElementById("creaVersionDiv").style.display = "block";
		document.getElementById("guardaVersion").style.display = "block";
		document.getElementById("tipoGuardar").style.display = "block";				
		document.getElementById("consultaVersionDiv").style.display = "none";
		document.getElementById("historicoNotas").style.display = "none";
		document.getElementById("firmas").style.display = "none";		
		$("#generar").val("1");				
	}
	
	function consultaV(){ //OK	//tabs-3				
		queryFormPost("consultaVersiones", {async: false });
			
		/*if($("#totalVersiones").val() == "0"){		
			Swal.fire({ icon: "info",
						text: "No existen versiones a consultar."});			
			document.getElementById("consultaVersionDiv").style.display = "none";	
			return false;
		}else{*/					
			document.TempForm.TIPO_REPORTE[0].checked = false;
			document.TempForm.TIPO_REPORTE[1].checked = false;
			document.TempForm.TIPO_REPORTE[2].checked = false;				
			
			document.getElementById("seleccionaNivel").style.display = "block";
			document.getElementById("temp").style.display = "none";	
			document.getElementById("creaVersionDiv").style.display = "none";
			document.getElementById("guardaVersion").style.display = "none";
			document.getElementById("tipoGuardar").style.display = "none";				
			document.getElementById("consultaVersionDiv").style.display = "block";
			document.getElementById("historicoNotas").style.display = "none";
			document.getElementById("firmas").style.display = "block";
			$("#consultar").val("0");
		//}				
	}
	
	function historicoNotas(){ //OK	//tabs-4	
		$("#id_archivoH").val("");
		$("#nNivelH").val("4");
		$("#nEjercicioFiscal").val();		
		$("#nNotaH").val("");
		
		document.getElementById("id_archivoH").disabled = false;
		document.getElementById("nNivelH").disabled = false;
		document.getElementById("nEjercicioFiscal").disabled = false;
		document.getElementById("id_GuiaH").disabled = false;
		document.getElementById("id_ManejoCuentasH").disabled = false;
		document.getElementById("id_CatCuentasInstructivoH").disabled = false;
		document.getElementById("id_CatCuentasH").disabled = false;
		
		document.getElementById("id_GuiaH").style.display = "none";		
		document.getElementById("id_CatCuentasH").style.display = "none";		
		document.getElementById("id_CatCuentasInstructivoH").style.display = "none";
		document.getElementById("id_ManejoCuentasH").style.display = "none";
		document.getElementById("id_ManejoCuentasV").style.display = "none";
		document.getElementById("temp").style.display = "none";	
		document.getElementById("creaVersionDiv").style.display = "none";
		document.getElementById("guardaVersion").style.display = "none";
		document.getElementById("tipoGuardar").style.display = "none";				
		document.getElementById("consultaVersionDiv").style.display = "none";
		document.getElementById("historicoNotas").style.display = "block";
		document.getElementById("firmas").style.display = "none";
		document.getElementById("seleccionaNivel").style.display = "none";		
	}
		
	function seleccionaSinFirmas(){ //OK
		if($("#sinFirmas").prop("checked")){
				$("#sinFirmar").val("1");		
			}else {
				$("#sinFirmar").val("0");
			}
	}
	
	function seleccionaGrupo(){ //OK
		$("#id_GuiaV").val("");
		$("#id_ManejoCuentasV").val("");
		$("#id_ComplementoV").val("");
		
		if($("#id_archivoV").val() == "GuiasContabilizadoras.jasper"){
			document.getElementById("id_GuiaV").style.display = "block";
			document.getElementById("id_ManejoCuentasV").style.display = "none";
		}
		else if($("#id_archivoV").val() == "ManejoDeCuentas.jasper"){
			document.getElementById("id_ManejoCuentasV").style.display = "block";		
			document.getElementById("id_GuiaV").style.display = "none";
		}
		else if($("#id_archivoV").val() == "PlanDecuentas.jasper"){
			document.getElementById("id_GuiaV").style.display = "none";
			document.getElementById("id_ManejoCuentasV").style.display = "none";	
			SeleccionaPlanCuentas();
		}
		else if($("#id_archivoV").val() == "ComplementoManualIntroduccion.jasper"){
			$("#id_ComplementoV").val("1");
			queryFormPost("versionSiguienteComplemento", {async: false });
		}
		else if($("#id_archivoV").val() == "ComplementoManualCicloHacendario.jasper"){
			$("#id_ComplementoV").val("2");
			queryFormPost("versionSiguienteComplemento", {async: false });
		}
		else if($("#id_archivoV").val() == "ComplementoManualSCG.jasper"){
			$("#id_ComplementoV").val("3");
			queryFormPost("versionSiguienteComplemento", {async: false });			
		}
		else if($("#id_archivoV").val() == "ComplementoManualInterAlcance.jasper"){
			$("#id_ComplementoV").val("4");
			queryFormPost("versionSiguienteComplemento", {async: false });		
		}
		else if($("#id_archivoV").val() == "ComplementoManualAspectosGenerales.jasper"){
			$("#id_ComplementoV").val("5");
			queryFormPost("versionSiguienteComplemento", {async: false });
		}
		else if($("#id_archivoV").val() == "ComplementoManualClasificacionParaestatal.jasper"){
			$("#id_ComplementoV").val("6");
			queryFormPost("versionSiguienteComplemento", {async: false });
		}
		else {
			document.getElementById("id_ManejoCuentasV").style.display = "none";		
			document.getElementById("id_GuiaV").style.display = "none";			
		}
	}
	
	function SeleccionaGuia(){ //OK
		queryFormPost("versionSiguinteGuia", {async: false });				
	}
	
	function SeleccionaInstructivo(){ //OK
		queryFormPost("versionSiguinteInstructivo", {async: false });		
	}
	
	function SeleccionaPlanCuentas(){ //OK
		queryFormPost("versionSiguintePlanCuentas", {async: false });		
	}
	
	function SeleccionaGuiaFinal(){ //OK
		if($("#id_Guia1").val() == 0)
			querySelectPost("versionesFinalesGuiaGeneral", "id_version", {async: false });
		else
			querySelectPost("versionesFinalesGuia", "id_version", {async: false });	
	}
	
	function SeleccionaInstructivoFinal(){ //OK
		if($("#id_ManejoCuentas1").val() == 0)
			querySelectPost("versionesFinalesInstructivoGeneral", "id_version", {async: false });
		else
			querySelectPost("versionesFinalesInstructivo", "id_version", {async: false });	
	}
	
	function SeleccionaPlanFinal(){ //OK
		querySelectPost("versionesFinalesPlan", "id_version", {async: false });
	}		
	
	function SeleccionaComplementoFinal(v){ //OK
		$("#id_complemento1").val(v);
		querySelectPost("versionesFinalesComplementos", "id_version", {async: false });
	}
	
	function selecciona(val,op){ //OK
		$("#id_estadoFinanciero1").val("");
		$("#id_estadoPresupuestario1").val("");
		$("#id_estadoProgramatico1").val("");
		$("#id_Guia1").val("-1");
		$("#id_ManejoCuentas1").val("-1");		
		$(".clsVers").show();
		
		if($("#PlanCuentas").prop("checked")){
			document.getElementById("id_Guia1").style.display = "none";
			document.getElementById("consultaVersion").style.display = "block";
			document.getElementById("id_estadoFinanciero1").style.display = "none";
			document.getElementById("estadoFinanciero1").style.display = "none";	
			document.getElementById("id_estadoPresupuestario1").style.display = "none";
			document.getElementById("id_estadoProgramatico1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "none";				
			SeleccionaPlanFinal();
		}else if(op!=0){
			document.getElementById("id_Guia1").style.display = "none";
			document.getElementById("consultaVersion").style.display = "block";
			document.getElementById("id_estadoFinanciero1").style.display = "none";
			document.getElementById("estadoFinanciero1").style.display = "none";	
			document.getElementById("id_estadoPresupuestario1").style.display = "none";
			document.getElementById("id_estadoProgramatico1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "none";					
			SeleccionaComplementoFinal(op);		
		}		
		else if($("#EstadoFinanciero").prop("checked")){
			$(".clsVers").hide();					
			document.getElementById("id_estadoFinanciero1").style.display = "block";
			document.getElementById("estadoFinanciero1").style.display = "block";	
			document.getElementById("id_estadoPresupuestario1").style.display = "none";
			document.getElementById("id_estadoProgramatico1").style.display = "none";
			document.getElementById("consultaVersion").style.display = "none";
			document.getElementById("id_Guia1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "none";
		}
		else if($("#EstadoPresupuestal").prop("checked")){				
			$(".clsVers").hide();
			document.getElementById("id_estadoFinanciero1").style.display = "none";
			document.getElementById("estadoFinanciero1").style.display = "block";	
			document.getElementById("id_estadoPresupuestario1").style.display = "block";
			document.getElementById("id_estadoProgramatico1").style.display = "none";
			document.getElementById("consultaVersion").style.display = "none";
			document.getElementById("id_Guia1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "none";
		}
		else if($("#EstadoProgramatico").prop("checked")){		
			$(".clsVers").hide();		
			document.getElementById("id_estadoFinanciero1").style.display = "none";
			document.getElementById("estadoFinanciero1").style.display = "block";	
			document.getElementById("id_estadoPresupuestario1").style.display = "none";
			document.getElementById("id_estadoProgramatico1").style.display = "block";
			document.getElementById("consultaVersion").style.display = "none";
			document.getElementById("id_Guia1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "none";
		}
		else if($("#GuiasContabilizadoras").prop("checked")){
			document.getElementById("id_Guia1").style.display = "block";
			document.getElementById("consultaVersion").style.display = "block";
			document.getElementById("id_estadoFinanciero1").style.display = "none";
			document.getElementById("estadoFinanciero1").style.display = "none";	
			document.getElementById("id_estadoPresupuestario1").style.display = "none";
			document.getElementById("id_estadoProgramatico1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "none";
		}
		else if($("#ManejoDeCuentas").prop("checked")){
			document.getElementById("id_Guia1").style.display = "none";
			document.getElementById("consultaVersion").style.display = "block";
			document.getElementById("id_estadoFinanciero1").style.display = "none";
			document.getElementById("estadoFinanciero1").style.display = "none";	
			document.getElementById("id_estadoPresupuestario1").style.display = "none";
			document.getElementById("id_estadoProgramatico1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "block";
		}else{
			document.getElementById("consultaVersion").style.display = "block";
			document.getElementById("id_estadoFinanciero1").style.display = "none";
			document.getElementById("estadoFinanciero1").style.display = "none";	
			document.getElementById("id_estadoPresupuestario1").style.display = "none";
			document.getElementById("id_estadoProgramatico1").style.display = "none";
			document.getElementById("id_Guia1").style.display = "none";
			document.getElementById("id_ManejoCuentas1").style.display = "none";
		}
	}
		
	function inittable(){
		/*Inicializar tabla*/
		oTable= $("#tblNotas").dataTable({
                   "bLengthChange" : true,
                   "bFilter" : true,
                   "bSort" : true,
                   "bInfo" : true,
                   "bPaginate" : true,
                   "bAutoWidth" : false,
                   "bScrollCollapse" : true,
                   //"sScrollXInner": "100%", 
           		   "sScrollX": "100%",
                   "sPaginationType" : "full_numbers",
                   "bJQueryUI" : true,
                   "bRetrive" : true,
                   "bDestroy" : true,
                   "bServerSide": true,                   
       			   "iDisplayLength": 25,
	       			"fnInitComplete": function() {   
	       				oTable.fnAdjustColumnSizing();
	    			},
                   oLanguage : {
	               	    sProcessing: "Procesando...",
	          			sLengthMenu: "Mostrar _MENU_ registros",
	          			sZeroRecords: "No hay registros a mostrar",
	          			sEmptyTable: "No hay datos en la tabla",
	          			sLoadingRecords: "Cargando...",
	          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
	          			sInfoEmpty: "Registro 0 al 0 de 0",
	          			sInfoFiltered: "(filtado de _MAX_ registros)",
	          			sInfoPostFix: "",
	          			sInfoThousands: ",",
	          			sSearch: "Buscar:",
	          			oPaginate: {
	          				sFirst:    "Primero",
	          				sPrevious: "Ant.",
	          				sNext:     "Sigte.",
	          				sLast:     "&Uacute;ltimo"
                          }
                   }
            });    
		
		
	}
	
	function tblDblClick(event){
        var aPos;
        var aData;
        aPos = oTable.fnGetPosition(event.target.parentNode); /*Toma la posición del renglón doble click*/
        aData = oTable.fnGetData(aPos); /*Toma los datos de acuerdo a la posición y genera arreglo*/
        var id= aData[0]; /*Set de dato requerido*/        		       
        var cbo_archivo = "";
        var SubTipo = "";
        
        $("#idHistorico"   	  ).val(aData[0]);
        $("#id_archivoH"   	  ).val(aData[1]);
        $("#nNivelH"	      ).val(aData[3]);        
        $("#nEjercicioFiscal" ).val(aData[4]);
        $("#nNotaH"   		  ).val(aData[5]);	 
        
        cbo_archivo = aData[6];
        SubTipo = aData[2];
        $("#id_archivoH").val(aData[6]);
        document.getElementById("id_archivoH").disabled = true;
        
        seleccionaGrupoH();
        
        if (cbo_archivo == "1"){        	        	
        	$("#id_GuiaH").val(SubTipo);
        	document.getElementById("id_GuiaH").disabled = true;        	
        }
        else if(cbo_archivo == "2"){   
        	seleccionaCuentaInstructivoH();
        	$("#id_ManejoCuentasH").val(aData[7]);        	
        	$("#id_CatCuentasInstructivoH").val(SubTipo);
        	document.getElementById("id_ManejoCuentasH").disabled = true;        	
        	document.getElementById("id_CatCuentasInstructivoH").disabled = true;
        }
        else if (cbo_archivo == "3"){
        	$("#id_CatCuentasH").val(SubTipo);
        	document.getElementById("id_CatCuentasH").disabled = true;
        }
        
        document.getElementById("nNivelH").disabled = true;
        document.getElementById("nEjercicioFiscal").disabled = true;
        
        document.getElementById("btnGuardarH").style.display = "none";
        document.getElementById("btnEditarH").style.display = "block";
   	}
	
	function reloadtable(){
    	oTable = $("#tblNotas").dataTable({
		    		"bLengthChange" : true,
		            "bFilter" : true,
		            "bSort" : true,
		            "bInfo" : true,
		            "bPaginate" : true,
		            "bAutoWidth" : false,
		            "bScrollCollapse" : true,
		            "sScrollXInner": "100%", 
		    		"sScrollX": "100%",
		            "sPaginationType" : "full_numbers",
		            "bJQueryUI" : true,
		            "bRetrive" : true,
		            "bDestroy" : true,
		            "bServerSide": true,		            
					"iDisplayLength": 25,
					"fnInitComplete": function() {   
						oTable.fnAdjustColumnSizing();
	    			},
                   oLanguage : {
	               	    sProcessing: "Procesando...",
	          			sLengthMenu: "Mostrar _MENU_ registros",
	          			sZeroRecords: "No hay registros a mostrar",
	          			sEmptyTable: "No hay datos en la tabla",
	          			sLoadingRecords: "Cargando...",
	          			sInfo: "Registros _START_ al _END_ de _TOTAL_",
	          			sInfoEmpty: "Registro 0 al 0 de 0",
	          			sInfoFiltered: "(filtado de _MAX_ registros)",
	          			sInfoPostFix: "",
	          			sInfoThousands: ",",
	          			sSearch: "Buscar:",
	          			oPaginate: {
	          				sFirst:    "Primero",
	          				sPrevious: "Ant.",
	          				sNext:     "Sigte.",
	          				sLast:     "&Uacute;ltimo"
                          }
                   },           
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_notas",			
			aaSorting: [[ 0, "asc" ]] ,
			aoColumns: [				
				{ sName: "idHistorico", bVisible : false},
				{ sName: "tipoManual" },
				{ sName: "SubTipo" },
				{ sName: "nNivel" },
				{ sName: "aEjercicioFiscal" },
				{ sName: "cNotaHistorica" },
				{ sName: "idTipo",bVisible : false },
				{ sName: "idSubTipo",bVisible : false }
			]
       	});	
    	
    	 /*Alta de funcionalidad de doble clic*/
		   $("#tblNotas tbody").unbind('dblclick'); 
	       $("#tblNotas tbody").dblclick( function( e ) {                	                	
	            $(oTable.fnSettings().aoData).each(
	                   function (){
	                         $(this.nTr).removeClass('row_selected');
	            });
	            $(e.target.parentNode).addClass('row_selected');
	            tblDblClick(e);	                 
	       });
	}
	
	function seleccionaGrupoH(){				
		if($("#id_archivoH").val() == "1"){					
			document.getElementById("id_GuiaH").style.display = "block";
			document.getElementById("id_ManejoCuentasH").style.display = "none";
			document.getElementById("id_CatCuentasH").style.display = "none";
			document.getElementById("id_CatCuentasInstructivoH").style.display = "none";
			querySelectPost("catGuiaContableV", "id_GuiaH", {async: false });			
		}
		else if($("#id_archivoH").val() == "2"){			
			document.getElementById("id_ManejoCuentasH").style.display = "block";		
			document.getElementById("id_GuiaH").style.display = "none";
			document.getElementById("id_CatCuentasH").style.display = "none";
			document.getElementById("id_CatCuentasInstructivoH").style.display = "block";
			document.getElementById("nNivelH").disabled = false;
			querySelectPost("catManejoCuentasV", "id_ManejoCuentasH", {async: false });
			querySelectPost("catCuentasInstructivoH", "id_CatCuentasInstructivoH", {async: false });				
		}
		else if($("#id_archivoH").val() == "3"){					
			document.getElementById("id_GuiaH").style.display = "none";
			document.getElementById("id_ManejoCuentasH").style.display = "none";		
			document.getElementById("id_CatCuentasH").style.display = "block";
			document.getElementById("id_CatCuentasInstructivoH").style.display = "none";
			document.getElementById("nNivelH").disabled = false;
			querySelectPost("catCuentasH", "id_CatCuentasH", {async: false });	
		}
	}		
	
	function GuardarNota(){
		if($("#id_archivoH").val() == "1"){
			$("#idSubTipo").val($("#id_GuiaH").val());
		} 
		else if($("#id_archivoH").val() == "2"){	
			$("#idSubTipo").val($("#id_CatCuentasInstructivoH").val());
		}
		else if($("#id_archivoH").val() == "3"){
			$("#idSubTipo").val($("#id_CatCuentasH").val());
		}
		queryFormPost({
			queryName : "GuardarNotaHistorica", 
			    async : false, 
			 callback : function(){ 
			    		msn = "La nota fue guardada exitosamente";
		      		  } 
		});		
		
		Swal.fire({ icon: "success",
					text: msn });		
		historicoNotas();
		
		$("#nNivelH").val("4");
		$("#nEjercicioFiscal").val("2015");
		$("#id_archivoH").val("");
		$("#id_GuiaH").val("");
		$("#id_ManejoCuentasH").val("");
		$("#id_CatCuentasInstructivoH").val("-1");
		$("#id_CatCuentasH").val("");
		
	}
	
	function EditarNota(){
		queryFormPost({
			queryName : "EditarNotaHistorica", 
			    async : false, 
			 callback : function(){ 
			    		msn = "La nota fue editada exitosamente";
		      		  } 
		});		
		
		Swal.fire({ icon: "success",
					text: msn });		
		historicoNotas();
	}
	
	function seleccionaCuentaH(){
		querySelectPost("catCuentasH", "id_CatCuentasH", {async: false });			
	}
	
	function seleccionaCuentaInsH(){
		querySelectPost("catCuentasInstructivoH", "id_CatCuentasInstructivoH", {async: false });			
	}
	
	function seleccionaCuentaInstructivoH(){
		document.getElementById("id_CatCuentasInstructivoH").style.display = "block";
		querySelectPost("catCuentasInstructivoH", "id_CatCuentasInstructivoH", {async: false });			
	}
	
	function seleccionaNivelH(){
		querySelectPost("catCuentasH", "id_CatCuentasH", {async: false });
		seleccionaCuentaInsH();			
	}
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="TempForm" name="TempForm" >		
		<input type="hidden" name="totalVersiones" id="totalVersiones" />
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 				
		<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />
		<input type="hidden" name="idversion_sig" id="idversion_sig" maxlength="5" size="5" readonly="readonly" class="notEditable"/>
		<input type="hidden" name="idversion_complemento" id="idversion_complemento" maxlength="5" size="5" readonly="readonly" class="notEditable"/>
		<input type="hidden" name="id_ComplementoV" id="id_ComplementoV" value="0"/>
		<input type="hidden" name="id_complemento1" id="id_complemento1" value="0"/>
		<input type="hidden" name="generar" id="generar" value="0" /> <!-- 1 -->
		<input type="hidden" name="consultar" id="consultar" value="-1" /> <!-- 0 -->
		<input type="hidden" name="idSubTipo" id="idSubTipo" value="-1" /> 
		<input type="hidden" name="idHistorico" id="idHistorico" value="-1" /> 
		<input type="hidden" id="ejercicioFiscal" name="ejercicioFiscal" value="<%=aEjercicioFiscal%>"/>
		<input type="hidden" id="fecha_aplicacion" name="fecha_aplicacion" value=""/>			
		
		<div id="container" style="width: 90%" class="container" > 	
			<div id="tabs" style="width: 90%" class="container" >    	
				<div class="card-header"> <h3> Manuales contables </h3> </div>
				<hr class="mt-3"/>
					
				<div class="row d-flex justify-content-center">	
					
		       		<ul class="nav nav-tabs" id="list-opciones">
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link active" id="tabs-1" onClick="temporal();" data-bs-toggle="tab" data-bs-target="#tabs-1-temp" type="button" role="tab" aria-controls="tabs-temp" aria-selected="true">Consulta Temporal</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="tabs-2" onClick="generaVersion();" data-bs-toggle="tab" data-bs-target="#tabs-2-version" type="button" role="tab" aria-controls="tabs-version" aria-selected="false">Generar Versiones</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="tabs-4" onClick="historicoNotas();" data-bs-toggle="tab" data-bs-target="#tabs-4-notas" type="button" role="tab" aria-controls="tabs-notas" aria-selected="false">Captura de Historico de Notas</button>
			            </li>
			            <li class="nav-item" role="presentation">
			            	<button class="nav-link" id="tabs-3" onClick="consultaV();" data-bs-toggle="tab" data-bs-target="#tabs-3-final" type="button" role="tab" aria-controls="tabs-final" aria-selected="false">Consulta Versiones Finales</button>
			        	</li>				           				           
			    	</ul>
		        
					<div class="tab-content mt-3" id="tabContent">		
						<div class="tab-pane fade show active" id="tabs-1-temp" role="tabpanel" aria-labelledby="tabs-temp">			
							<div id="seleccionaNivel" style="width: 50%" class="container">			
										
								<h5> Presentacion </h5>
								<hr class="mt-3"/>
								
								<div class="row">
									<div class="col-12 d-flex justify-content-center">										
										<div class="form-check">
											<input type="radio" name="NUMERO_DIGITOS" id="TIPO_REPORTE1" class="form-check-input" value = "17" onclick="selecciona(this)" checked/>								
											<label for="patrimonial" class="form-check-label">Manual de Contabilidad (Usuarios SAI)</label>
										</div>						
									</div>
									<div class="col-12 d-flex justify-content-center">
										<div class="form-check">								
											<input type="radio" name="NUMERO_DIGITOS" id="TIPO_REPORTE1" class="form-check-input" value = "4" onclick="selecciona(this)"/>
											<label for="laboral" class="form-check-label">Manual de Contabilidad (CONAC)</label>
										</div>
									</div>
								</div>			
				
							</div> <!-- FIN id="seleccionaNivel" -->
					
							<div style="display:none;" id="generaVersionDiv">								
								<div id="temp" style="width: 90%" class="container">
									
									<h5> Manual temporal </h5>
									<hr class="mt-3"/>
								
									<div class="row">
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">
											<select name="id_archivo" id="id_archivo" class="form-select form-select-sm" style="width: 30em;" onchange="esEstadoFinanciero()">
												<option value= "">Selecciona una opcion...</option>		
												<option value= "ComplementoManualIntroduccion.jasper">Introducción</option>										
												<option value= "ComplementoManualCicloHacendario.jasper">Ciclo Hacendario</option>
												<option value= "ComplementoManualSCG.jasper">Sistema de Contabilidad Gubernamental</option>
												<option value= "ComplementoManualInterAlcance.jasper">Interpretación y Alcance Institucional</option>
												<option value= "ComplementoManualAspectosGenerales.jasper">Aspectos Generales</option>
												<option value= "ComplementoManualClasificacionParaestatal.jasper">Clasificacion Paraestatal</option>
												<option value= "ComplementoManualControlIngresoGasto.jasper">Control Presupuestario de Ingreso y Gasto</option>
												<option value= "ComplementoManualSubsidiosApoyosFiscales.jasper">Subsidios y Apoyos Fiscales</option>
												<option value= "ComplementoManualEstimacionCtasIncobrables.jasper">Estimacion Cuentas Incobrables</option>
												<option value= "ComplementoManualObligacionesLaborales.jasper">Obligaciones Laborales</option>
												<option value= "ComplementoManualArrendamientoFinanciero.jasper">Arrendamiento Financiero</option>
												<option value= "ComplementoManualNACG01.jasper">NACG 01 Norma Archivo Contable Gubernamental</option>
												<option value= "GuiasContabilizadoras.jasper">Guias Contabilizadoras</option>
												<option value= "ManejoDeCuentas.jasper">Instructivo de Manejo de Cuentas</option>
												<option value= "PlanDecuentas.jasper">Plan de Cuentas</option>										
												<option value= "EstadoFinanciero">Estados e Información Contable</option>
												<option value= "EstadoPresupuestario">Estados e Informes Presupuestarios</option>
												<option value= "EstadoProgramatico">Estados e Informes Programáticos</option>																		
											</select>
										</div>
									</div>

									<br/>
									
									<div class="row">
										<div class="col-12 d-flex justify-content-center">
											<select name="id_Guia" id="id_Guia" class="form-select form-select-sm" style="width: 30em;">
											</select>
											
											<select name="id_ManejoCuentas" id="id_ManejoCuentas" class="form-select form-select-sm" style="width: 25em;">
											</select>
											
											<select  name="id_estadoFinanciero" id="id_estadoFinanciero" class="form-select form-select-sm" style="width: 25em;">
												<option value= "">Selecciona un Estado Financiero...</option>
												<option value= "1SituacionFinanciera">Estado de Situación Financiera</option>
												<option value= "1Actividades">Estado de Actividades</option>
												<option value= "1Variacion">Estado de Variacion en la Hacienda Pública</option>
												<option value= "1CambioSituacionFinanciera">Estado de Cambios en la Situación Financiera</option>
												<option value= "1FlujoEfectivo">Estado de Flujos de Efectivo</option>
												<option value= "1AnaliticoActivo">Estado Analitico del Activo</option>
												<option value= "1AnaliticoDeuda">Estado Analitico de la Deuda y Otros Pasicvos</option>
												<option value= "1InformePasivos">Informe sobre Pasivos Contingentes</option>
												<option value= "1Notas">Notas a los Estados Financieros</option>
												<option value= "1Balanza">Balanza de Comprobacion</option>
												<option value= "1ConciliacionEgreso">Conciliacion Egresos Presupuestarios y Contables</option>
												<option value= "1ConciliacionIngreso">Conciliacion Ingresos Presupuestarios y Contables</option>
												<option value= "1AnaliticoNoCirculante">Estado Analitico del Activo No Circulante</option>																								
											</select>
											
											<select  name="id_estadoPresupuestario" id="id_estadoPresupuestario" class="form-select form-select-sm" style="width: 25em;">
												<option value= "">Selecciona un Estado Presupuestario...</option>
												<option value= "2AnaliticoIngreso.pdf">Estado Analitico de Ingresos</option>
												<option value= "2AnaliticoEgreso.pdf">Estado Analitico del Ejercicio del Preupuesto de Egresos</option>																															
											</select>
											
											<select  name="id_estadoProgramatico" id="id_estadoProgramatico" class="form-select form-select-sm" style="width: 25em;">
												<option value= "">Selecciona un Estado Programatico...</option>
												<option value= "3GastoProgramatico.pdf">Gasto por Categoria Programatica</option>																																					
											</select>
											
										</div>
									</div>	
									
									<br/>
									
									<div class="row">
										<div class="col-12 d-flex justify-content-center">													
											<button type="button" id="guiaTemporal" name="guiaTemporal" class="btn btn-secondary btn-sm" onclick="extraeTemporal()"> Genera Temporal </button>
											<button type="button" id="estadoFinanciero" name="estadoFinanciero" class="btn btn-secondary btn-sm" onclick="extraer()"> Extraer Formato </button>
										</div>
									</div>																		
									
								</div> <!-- FIN id="temp" -->		
							</div> <!-- FIN id="generaVersionDiv" -->
						</div> <!-- FIN tabs-1 -->
							
						<div class="tab-pane fade" id="tabs-2-version" role="tabpanel" aria-labelledby="tabs-version">
							<div id="creaVersionDiv" style="width: 90%" class="container">
							
								<h5> Genera Versiones Manuales Contables </h5>
								<hr class="mt-3"/>
									
								<div id="guia">	
									<div class="row">
										<div class="col-12 d-flex justify-content-center">	
											<div class="form-group">
							                	<label for="fecha">Fecha de Autorización:</label>
							                    <div class="input-group date" id="datepicker1">
							                    	<input type="date" class="form-control form-control-sm" id="fecha" name="fecha"/>                                    
							                    </div>
							                </div>																
										</div>											
									</div>
									
									<br/>
									
									<div id="tipoGuardar">
										<div class="row">
											<div class="col-12 d-flex justify-content-center">	
												<label for="fechaAplicacion" class="form-label"> Selecciona la opcion que deseas generar versión </label>
											</div>
										</div>

										<div class="row">
											<div class="col-12 d-flex justify-content-center">
												<select name="id_archivoV" id="id_archivoV" class="form-select form-select-sm" style="width: 20em;" onchange="seleccionaGrupo()">
													<!-- La versionde los complementos se generan en automatico cuando se crea la version de la guia contable -->															
													<option value= "">Selecciona una opcion...</option>
													<option value= "GuiasContabilizadoras.jasper">Guias Contabilizadoras</option>
													<option value= "ManejoDeCuentas.jasper">Instructivo de Manejo de Cuentas</option>
													<option value= "PlanDecuentas.jasper">Plan de Cuentas</option>
												</select>
											</div>
										</div>

										<br/>
										
										<div class="row">
											<div class="col-12 d-flex justify-content-center">
												<select name="id_GuiaV" id="id_GuiaV" class="form-select form-select-sm" style="width: 30em;" onchange="SeleccionaGuia()">
												</select>
												
												<select name="id_ManejoCuentasV" id="id_ManejoCuentasV" class="form-select form-select-sm" style="width: 25em;" onchange="SeleccionaInstructivo()">
												</select>
											</div>
										</div>																														
									</div> <!-- FIN id="tipoGuardar" -->
									
									<br/>
									
									<div class="row">
										<div class="col-12 d-flex justify-content-center">													
											<button type="button" id="guardaVersion" name="guardaVersion" class="btn btn-secondary btn-sm"> Guarda Versión </button>
										</div>
									</div>													
								</div> <!-- FIN id="guia" -->																						
							</div> <!-- FIN id="creaVersionDiv" -->
						</div> <!-- FIN tabs-2 -->
										
						<div class="tab-pane fade" id="tabs-3-final" role="tabpanel" aria-labelledby="tabs-final">
							<div id="seleccionaNivel" style="width: 50%" class="container">			
										
								<h5> Presentacion </h5>
								<hr class="mt-3"/>
								
								<div class="row">
									<div class="col-12 d-flex justify-content-center">										
										<div class="form-check">
											<input type="radio" name="NUMERO_DIGITOS" id="TIPO_REPORTE1" class="form-check-input" value = "17" onclick="selecciona(this)" checked/>								
											<label for="patrimonial" class="form-check-label">Manual de Contabilidad (Usuarios SAI)</label>
										</div>						
									</div>
									<div class="col-12 d-flex justify-content-center">
										<div class="form-check">								
											<input type="radio" name="NUMERO_DIGITOS" id="TIPO_REPORTE1" class="form-check-input" value = "4" onclick="selecciona(this)"/>
											<label for="laboral" class="form-check-label">Manual de Contabilidad (CONAC)</label>
										</div>
									</div>
								</div>			
				
							</div> <!-- FIN id="seleccionaNivel" -->
							
							<div id="consultaVersionDiv" style="width: 90%" class="container">											
								<div id="consulta" style="width: 90%" class="container">		
														
									<h5> Consulta Manuales Contables </h5>
									<hr class="mt-3"/>		
									
									<div class="row d-flex">
										<div class="col-12 col-md-2 mb-3">
										</div>
										<div class="col-12 col-md-5 mb-3">													
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Introducción</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,1)" id="1" value="ComplementoManualIntroduccion.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Ciclo Hacendario</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,2)" id="2" value="ComplementoManualCicloHacendario.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Sistema de Contabilidad Gubernamental</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,3)" id="3" value="ComplementoManualSCG.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Interpretación y Alcance Institucional</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,4)" id="4" value="ComplementoManualInterAlcance.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Aspectos Generales</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,5)" id="5" value="ComplementoManualAspectosGenerales.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Clasificacion Paraestatal</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,6)" id="6" value="ComplementoManualClasificacionParaestatal.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Control Presupuestario de Ingreso y Gasto</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,8)" id="8" value="ComplementoManualControlIngresoGasto.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Subsidios y Apoyos Fiscales</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,9)" id="9" value="ComplementoManualSubsidiosApoyosFiscales.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Estimacion Cuentas Incobrables</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,10)" id="10" value="ComplementoManualEstimacionCtasIncobrables.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Obligaciones Laborales</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,11)" id="11" value="ComplementoManualObligacionesLaborales.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Arrendamiento Financiero</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,12)" id="12" value="ComplementoManualArrendamientoFinanciero.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">NACG 01 Norma Archivo Contable Gubernamental</label>
												<input type="radio" name="TIPO_REPORTE" class="form-check-input" onclick="selecciona(this,13)" id="13" value="ComplementoManualNACG01.jasper" />
											</div>											
										</div>
										<div class="col-12 col-md-5 mb-3">																								
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Guia Contabilizadora</label>
												<input type="radio" name="TIPO_REPORTE" id="GuiasContabilizadoras" class="form-check-input" onclick="selecciona(this,0)" value="GuiasContabilizadoras.jasper" />
												<select name="id_Guia1" id="id_Guia1" class="form-select form-select-sm" style="width: 30em;" onchange="SeleccionaGuiaFinal()"></select>														
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Instructivo de Cuentas</label>
												<input type="radio" name="TIPO_REPORTE" id="ManejoDeCuentas" class="form-check-input" onclick="selecciona(this,0)" value="ManejoDeCuentas.jasper" />
												<select name="id_ManejoCuentas1" id="id_ManejoCuentas1" class="form-select form-select-sm" style="width: 30em;" onchange="SeleccionaInstructivoFinal()"></select>														
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Plan de Cuentas</label>
												<input type="radio" name="TIPO_REPORTE" id="PlanCuentas" class="form-check-input" onclick="selecciona(this,0)" value="PlanDecuentas.jasper" />
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Estados e Informacion Contable</label>
												<input type="radio" name="TIPO_REPORTE" id="EstadoFinanciero" class="form-check-input" onclick="selecciona(this,0)" value="EstadosFinancieros" />
												<select  name="id_estadoFinanciero1" id="id_estadoFinanciero1" class="form-select form-select-sm" style="width: 25em;">
													<option value= "">Selecciona un Estado Financiero...</option>
													<option value= "1SituacionFinanciera">Estado de Situación Financiera</option>
													<option value= "1Actividades">Estado de Actividades</option>
													<option value= "1Variacion">Estado de Variacion en la Hacienda Pública</option>
													<option value= "1CambioSituacionFinanciera">Estado de Cambios en la Situación Financiera</option>
													<option value= "1FlujoEfectivo">Estado de Flujos de Efectivo</option>
													<option value= "1AnaliticoActivo">Estado Analitico del Activo</option>
													<option value= "1AnaliticoDeuda">Estado Analitico de la Deuda y Otros Pasicvos</option>
													<option value= "1InformePasivos">Informe sobre Pasivos Contingentes</option>
													<option value= "1Notas">Notas a los Estados Financieros</option>
													<option value= "1Balanza">Balanza de Comprobacion</option>
													<option value= "1ConciliacionEgreso">Conciliacion Egresos Presupuestarios y Contables</option>
													<option value= "1ConciliacionIngreso">Conciliacion Ingresos Presupuestarios y Contables</option>
													<option value= "1AnaliticoNoCirculante">Estado Analitico del Activo No Circulante</option>																								
												</select>	
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Estados e Informes Presupuestarios</label>
												<input type="radio" name="TIPO_REPORTE" id="EstadoPresupuestal" class="form-check-input" onclick="selecciona(this,0)" value="EstadosPresupuestarios" />
												<select  name="id_estadoPresupuestario1" id="id_estadoPresupuestario1" class="form-select form-select-sm" style="width: 25em;">
													<option value= "">Selecciona un Estado Presupuestario...</option>
													<option value= "2AnaliticoIngreso.pdf">Estado Analitico de Ingresos</option>
													<option value= "2AnaliticoEgreso.pdf">Estado Analitico del Ejercicio del Preupuesto de Egresos</option>																															
												</select>
											</div>
											<div class="form-check">
												<label for="patrimonial" class="form-check-label">Estados e Informes Programaticos</label>
												<input type="radio" name="TIPO_REPORTE" id="EstadoProgramatico" class="form-check-input" onclick="selecciona(this,0)" value="EstadosProgramaticos" />
												<select  name="id_estadoProgramatico1" id="id_estadoProgramatico1" class="form-select form-select-sm" style="width: 25em;">
													<option value= "">Selecciona un Estado Programatico...</option>
													<option value= "3GastoProgramatico.pdf">Gasto por Categoria Programatica</option>																																					
												</select>
											</div>
										</div>
									</div>
																				
									<div class="row">
										<div class="col-12 d-flex justify-content-center">
											<label for="patrimonial" class="form-check-label"><b>Selecciona Versión:</b> </label>
											&nbsp;<select  name="id_version" id="id_version" class="form-select form-select-sm" style="width: 5em;"> </select>
										</div>
									</div>
									
									<br/>
									
									<div class="row">
										<div class="col-12 d-flex justify-content-center">													
											<button type="button" id="consultaVersion" name="consultaVersion" class="btn btn-secondary btn-sm" onclick="extrae()"> Extraer </button>
											<button type="button" id="estadoFinanciero1" name="estadoFinanciero1" class="btn btn-secondary btn-sm" onclick="extraer()"> Extraer Formato </button>
										</div>
									</div>																				
								</div> <!-- FIN id="consulta" -->												
							</div> <!-- FIN id="consultaVersionDiv" -->
						</div> <!-- FIN tabs-3 -->
						
						<div class="tab-pane fade" id="tabs-4-notas" role="tabpanel" aria-labelledby="tabs-notas">
							<div id="historicoNotas" style="width: 99%" class="container">							
								<div id="guardaHistorico" style="width: 95%" class="container">
								
									<h5> Captura de Notas Historicas </h5>
									<hr class="mt-3"/>		
									
									<div class="row">
										<div class="col-12 col-lg-6 col-md-12 col-sm-12">
											<label class="form-label">Nivel: </label>
											<select name="nNivelH" id="nNivelH" class="form-select form-select-sm" style="width: 10em;" onchange="seleccionaNivelH()">
												<option value= "4">4 CONAC</option>
												<option value= "17">15 SAI</option>
											</select>							
										</div>
				
										<div class="col-12 col-lg-6 col-md-12 col-sm-12">
											<label class="form-label">Ejercicio Fiscal: </label>
											<select name="nEjercicioFiscal" id="nEjercicioFiscal" class="form-select form-select-sm" style="width: 10em;">
												<option value=""></option>																		
											</select>							
										</div>
									</div>
									
									<div class="row">
										<div class="col-12 col-lg-6 col-md-12 col-sm-12">
											<label class="form-label">Grupo: </label>
											<select name="id_archivoH" id="id_archivoH" class="form-select form-select-sm" style="width: 20em;"  onchange="seleccionaGrupoH()">
												<option value= "">Selecciona una opcion...</option>			
													<option value= "1">Guias Contabilizadoras</option>
													<option value= "2">Instructivo de Manejo de Cuentas</option>
													<option value= "3">Plan de Cuentas</option>
											</select>							
										</div>
									</div>

									<div class="row">
										<div class="col-12 col-md-6 mb-3 d-flex">
											<select name="id_GuiaH" id="id_GuiaH" class="form-select form-select-sm" style="width: 30em;">
											</select>
											
											<select name="id_ManejoCuentasH" id="id_ManejoCuentasH" class="form-select form-select-sm" style="width: 25em;" onchange="seleccionaCuentaInstructivoH()">
											</select>

											<select name="id_CatCuentasInstructivoH" id="id_CatCuentasInstructivoH" class="form-select form-select-sm" style="width: 25em;">
											</select>

											<select name="id_CatCuentasH" id="id_CatCuentasH" class="form-select form-select-sm" style="width: 25em;" onchange="seleccionaCuentaH()">
											</select>
										</div>
									</div>	
									
									<div class="row">
										<div class="col-12 col-md-6 mb-3 d-flex">
											<label for="nNotaH" class="form-label"> Nota: </label>											
										</div>
									</div>
									<div class="row">
										<div class="col-12 col-md-6 mb-3 d-flex">
											<textarea id="nNotaH" name="nNotaH" rows="5" cols="80" maxlength="1000" class="form-control form-control-sm"></textarea>
										</div>
									</div>

									<div class="row">
										<div class="col-12 col-md-6 mb-3 d-flex">	
											<button type="button" id="btnGuardarH" name="btnGuardarH" class="btn btn-secondary btn-sm"> Guardar </button>
											<button type="button" id="btnEditarH" name="btnEditarH" class="btn btn-secondary btn-sm"> Editar </button>
										</div>
									</div>	
									
									<br/>
										
									<h5> Consulta Notas Historicas </h5>
									<hr class="mt-3"/>
									
									<div id="notas" class="table-responsive">	    
										<table id="tblNotas" class="table table-striped">
								            <thead>
								                <tr>
								                	<th>ID</th>
								                	<th>Tipo</th>
								                	<th>Sub-Tipo</th>
								                	<th>Nivel</th>
								                	<th>Ejercicio Fiscal</th>
								                	<th>Nota</th>
								                	<th>idtipo</th>
								                	<th>idSubtipo</th>						                    
								                </tr>
								            </thead>
								        </table>
									</div>
							        									
								</div> <!-- FIN id="guardaHistorico" -->		
							</div> <!-- FIN id="historicoNotas" -->
						</div> <!-- FIN tabs-4 -->
					</div> <!-- FIN class="tab-content mt-3" -->			
				</div> <!-- FIN class="card" -->
			</div> <!-- FIN class="row" -->					
		</div> <!-- FIN class="container" -->
		
		<div id="firmas" style="width: 70%" class="container">
					
			<h5> Firmas </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-md-6 mb-3 d-flex">	
					<div class="form-check">
						<input type="checkbox" name="sinFirmas" id="sinFirmas" class="form-check-input" onclick="seleccionaSinFirmas()"/>								
						<label for="sinFirmas" class="form-check-label">Sin Firmas</label>
						<input type="hidden" id="sinFirmar" name="sinFirmar" />
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">								
					<label for="nombre1"> Nombre&nbsp; </label> 							 							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divcargo1" title="DivCargo1">								
					<label for="cargo1"> Cargo&nbsp; </label> 															
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divnombre1" title="Firma1">															 
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre1" name="nombre1" class="form-control form-control-sm" maxlength="200" value="M.A.T. ADANELY LÓPEZ GONZÁLEZ" />
					</div> 								
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1" id="divcargo1" title="DivCargo1">															 
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo1" name="cargo1" class="form-control form-control-sm" maxlength="200" value="JEFATURA DE DEPARTAMENTO" />
					</div>								
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="nombre2"> Nombre&nbsp; </label> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="cargo2"> Cargo&nbsp; </label>
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre2" name="nombre2" class="form-control form-control-sm" maxlength="200" value="L.C.P. GABRIELA GÓMEZ RUVALCABA" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo2" name="cargo2" class="form-control form-control-sm" maxlength="200" value="SUBGERENTE DE CONTABILIDAD" />
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="nombre3"> Nombre&nbsp; </label> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<label for="cargo3"> Cargo&nbsp; </label>
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="nombre3" name="nombre3" class="form-control form-control-sm" maxlength="200" value="M.B.A. TANIA ANANÍ LIMÓN MAGAÑA" />
					</div> 
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cargo3" name="cargo3" class="form-control form-control-sm" maxlength="200" value="GERENCIA DE PROGRAMACIÓN Y PRESUPUESTO" />
					</div>
				</div>
			</div>
		</div>					
	</form>

</body>
</html>
