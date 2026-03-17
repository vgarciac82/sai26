
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.io.File"%>

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

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	
	String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
	
	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);	

	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	String mensaje = null;
	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
		return;
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	algo = usuario.getLogin();
	
	if (session.getAttribute("RESULT") != null) {
		mensaje = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	if (cbl == null || usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	System.out.println("0: Fecha de Aplicacion "+fAplicacion[0]);
	System.out.println("1: Fecha Minima "+fAplicacion[1]);
	System.out.println("2: Fecha Maxima "+fAplicacion[2]);	

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Pasivos Contingentes</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" /> 

<link rel="stylesheet" href="css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="css/bootstrap-datetimepicker.min.css"></link>
<script src="js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css"></link>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	
	var msg = "<%=StringUtils.isEmpty( mensaje )?"":mensaje.replaceAll("\n", "").replaceAll("\r", "")%>";
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	$("#usuario").val("<%=algo%>");		
		
	$("#guardarBaja").button();
	$("#limpiar").button();
	$("#apoderado").hide();
	$("#aRaz").hide();
	$("#aNom").show();
	$("#aPat").show();
	$("#aMat").show();
	$("#dTrimestral").hide();
	$("#dbuscaPasivo").show();
	$("input.AyudaSyC").subIniciaDlg();
	
	$("#msgDialog").dialog({
		autoOpen : false,
		height : 250,
		width : 400,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				$(this).dialog("close");
			}
		}
	});
	
	$("#guardarBaja").button().click(function() {
		if ($("#cRFC").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo RFC es obligatorio."});			
			ok = 0;
		} else {
			document.TempForm.action="../reportes/BajaPasivosContingentes";
			document.TempForm.method="POST";
			document.TempForm.submit();
		}
	});
		
	if( msg != "" )
		Swal.fire({ icon: "success",
					text: msg });		
		
});		
	
	function tipoPersonaChange() {//muestra opciones segun sea el tipo de persona
		var tipo = $("#cIdTipoPersonaRFC").val();
		if (tipo == 1) {//MORAL
			$("#apoderado").show();
			$("#aRaz").show();			
		} else {//FISICA
			$("#apoderado").hide();
			$("#aRaz").hide();			
		}		
	}		
	
	function actualizaInfo(){
		if( $("#nesPatrimonial").val() == "0" )
			$("#nesLaboral").attr("checked", "checked");				
		else if( $("#nesPatrimonial").val() == "1" ){
			$("#nesPatrimonial").attr("checked", "checked");
			$("#tipoPasivo").val("P");
		}
		if( $("#nesExtranjero").val() == "1")
			$("#nesExtranjeroChk").attr("checked", "checked");
		else 
			$("#nesExtranjeroChk").removeAttr("checked");
			
		$("#cIdTipoPersonaRFC").change();
	}
	
	function limpiarConsulta(){
		$("#cRFC").val("");
		$("#cSubcuenta").val("");
		$("#nesPatrimonial").removeAttr("checked");		
		$("#cIdTipoPersonaRFC").val("2");
		$("#apoderado").hide();
		$("#aRaz").hide();
		$("#cRazon").val("");
		$("#cNombre").val("");
		$("#cApellidoPaterno").val("");
		$("#cApellidoMaterno").val("");
		$("#nesExtranjeroChk").removeAttr("checked");

	}
</script>

</head>
<body id="dt_example">
<br/>
	<form id="TempForm" name="TempForm" >
			
		<input type="hidden" id="nesExtranjero" name="nesExtranjero" value="" />
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 				
		<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />
		<input type="hidden" name="idPasivoContingente" id="idPasivoContingente" value="" />	
		<input type="hidden" name="contador" id="contador" value="0" />
		<input type="hidden" name="tipoPasivo" id="tipoPasivo" value="L" /> 		
		<input type="hidden" name="fecha_aplicacion" id="fecha_aplicacion" value="<%=fAplicacion[0]%>" />	
		<input type="hidden" name="reportPath" id="reportPath" value="<%=reportPath%>" />
		
		<div id="container" style="width: 60%" class="container">
		
		<div class="card-header"> <h3> Baja Pasivos Contingentes </h3> </div>
		<hr class="mt-3"/>
											
			<div id="dbuscaPasvio">						
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12" id="prfcP">									
						<label for="cRFC" class="form-label">RFC</label>
						<div class="input-group">
							<input type="text" name="cRFC" id="cRFC" class="form-control AyudaSyC form-control-sm" onchange="actualizaInfo()" readonly/>													
						</div>
					</div>					
					<div class="col-12 col-lg-3 col-md-3 col-sm-12" id="pCol">
						<label for="pc" class="form-label">Subcuenta</label>
						<input type="text" name="cSubcuenta" id="cSubcuenta" class="form-control form-control-sm" onblur="ChangeCase(this);" readonly/>
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label class="form-label">Tipo Pasivo Contingente</label>	
						<div class="form-check">
							<input type="radio" name="tipoPasivoChk" id="nesPatrimonial" class="form-check-input" value = "1" onclick="setEsPatrimonial()" disabled="disabled"/>								
							<label for="patrimonial" class="form-check-label">Patrimonial</label>
						</div>
						<div class="form-check">								
							<input type="radio" name="tipoPasivoChk" id="nesLaboral" class="form-check-input" value = "0" onclick="setEsLaboral()" disabled="disabled"/>
							<label for="laboral" class="form-check-label">Laboral</label>
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label class="form-label">Tipo Persona</label>
						<select name="cIdTipoPersonaRFC" id="cIdTipoPersonaRFC" class="form-select form-select-sm" onChange="tipoPersonaChange(this)" disabled="disabled">
							<option id="1" value="1">PERSONA MORAL</option>
							<option id="2" value="2">PERSONA FISICA</option>																		
						</select>							
					</div>
					<div class="col-12 col-md-3 mb-3" id="Extranjero">
					<div class="form-check">
							<input type="checkbox" name="nesExtranjeroChk" id="nesExtranjeroChk" class="form-check-input" value = "0" disabled="disabled"/>
							<label for="Extranjero" class="form-check-label">Es Extranjero</label>
						</div>
					</div>	
				</div>
										
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12" id="aRaz">																					
						<label for="cRazon" class="form-label">Raz&oacute;n Social</label>		
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-building"></i></span>									
							<input type="text" name="cRazon" id="cRazon" class="form-control form-control-sm" readonly/>
						</div>								
					</div>
				</div>
					
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12" id="apoderado">						
						<label for="cRazon" class="form-label">Nombre del Representante</label>
					</div>
				</div>

				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12" id="aNom">
						<label for="cNombre" class="form-label">Nombre</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" name="cNombre" id="cNombre" class="form-control form-control-sm" readonly/>
						</div>								
					</div>
				</div>
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12" id="aPat">
						<label for="apPaterno" class="form-label">Apellido Paterno</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" name="cApellidoPaterno" id="cApellidoPaterno" class="form-control form-control-sm" readonly/>
						</div>
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12" id="aMat">
						<label for="apMaterno" class="form-label">Apellido Materno</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" name="cApellidoMaterno" id="cApellidoMaterno" class="form-control form-control-sm" readonly/>
						</div>
					</div>						
				</div>						
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 d-flex justify-content-center">
					<div>
						<button type="button" id="guardarBaja" name="guardarBaja" class="btn btn-secondary btn-sm" > Guardar Baja </button>
						<button type="button" id="limpiar" name="limpiar" class="btn btn-outline-primary btn-sm" onclick="limpiarConsulta()"> Limpiar </button>
					</div>
				</div>
				<br/>									
			</div>
																				
			<br/><br/>									
			
		</div>											
	</form>

</body>
</html>
