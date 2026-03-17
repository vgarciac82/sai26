
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
<%@page import="com.syc.pasivoscontingentes.PasivosContingentesBusinessLogic"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.gestion.core.Usuario"%>

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
	String cSubcuenta = "";

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	
	String reportPath = getServletContext().getRealPath("Reportes" + File.separator);
	
	PasivosContingentesBusinessLogic pcbl = new PasivosContingentesBusinessLogic(GestionInterface.ATT_CONEXION);
	cSubcuenta = pcbl.readSubcuenta();
	
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
	
%>

<!DOCTYPE html>
<html lang="es">
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

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>

<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
	$(document).ready(function() {	
		
		var msg = "<%=StringUtils.isEmpty( mensaje )?"":mensaje.replaceAll("\n", "").replaceAll("\r", "")%>";		

		$("#cCentroContable").val( "<%=cCentroContable%>");
		$("#usuario").val("<%=algo%>");
		$("input.AyudaSyC").subIniciaDlg();
		$("input.autoCompletaSyC").subIniciaAutoCompleta();			
		
		$("#guardar").button();
		$("#limpiar").button();
		$("#apoderado").hide();
		$("#interes").hide();
		$("#aRaz").hide();
		$("#aNom").show();
		$("#aPat").show();
		$("#aMat").show();
		$("#aaFechaCap").hide();				
		
		if( msg != "" )
			Swal.fire({ icon: "info",
						text: msg });
		
		querySelectPost("juntaConciliacion", "cIdJunta", {async: false });
		
	});		

	function onLoadPlantilla(){
		$("#cSubcuenta").val("<%=cSubcuenta%>");
	}
	
	function existenBeneficiarios(){				
		if ($("#cRFC").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Captura el RFC para hacer la busqueda."});
		} else {
			queryFormPost("buscarPasivoContingenteBeneficiarios", {async : false});
			var existe = $("#existeBeneficiario").val();
					
			if ($("#existeBeneficiario").val() == "S"){
				if ($("#tipoPersonaBEN").val() == "M"){
					$("#cRazon").val($("#cRazonSocialBEN").val());
					
					cRazon.readOnly = true;
					
					$("#apoderado").show();
					$("#aRaz").show();		
				} else if ($("#tipoPersonaBEN").val() == "F"){
					$("#cNombre").val($("#nombreBEN").val());
					$("#cApellidoPaterno").val($("#apPatBEN").val());
					$("#cApellidoMaterno").val($("#apMatBEN").val());
										
					cNombre.readOnly = true;
					cApellidoPaterno.readOnly = true;
					cApellidoMaterno.readOnly = true;
				}
													
				document.getElementById('guardar').disabled = false;
			} else if ($("#existeBeneficiario").val() == "") {
				document.getElementById('guardar').disabled = false;
				Swal.fire({ icon: "info",
							text: "El RFC no exitse en el catalogo de Beneficiarios puedes seguir capturando los datos."});			
			}
		}
	}
	
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
			
	function setEsPatrimonial(){
		if( $("#nesPatrimonial").attr("checked") ){
			$("#nesPatrimonial").val("1");
			$("#interes").show();
		}else{
			$("#nesPatrimonial").val("0");
		}
	}
	
	function setEsLaboral(){
		if( $("#nesLaboral").attr("checked") ){
			$("#nesLaboral").val("0");
			$("#interes").hide();
		}else{
		}
	}
	
	function datosAyuda(){
		$("#fecha_demanda").val($("#fefechaDchaf").val().split('-').reverse().join('/'));
		$("#fecha_oficio").val($("#fechaO").val().split('-').reverse().join('/'));
		
		if( $("#nesPatrimonial").val() == "0" )
			$("#nesLaboral").attr("checked", "checked");
		else if( $("#nesPatrimonial").val() == "1" )
			$("#nesPatrimonial").attr("checked", "checked");
		$("#cIdTipoPersonaRFC").change();		
	}
	
	function guardaPasivo() {
		var ok = 1;
		var fecha_aplicacion = "";
		
		var day = "";
				
		$("#fecha_demanda").val($("#fechaD").val().split('-').reverse().join('/'));
		$("#fecha_oficio").val($("#fechaO").val().split('-').reverse().join('/'));
								
		if ($("#cRFC").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo RFC es obligatorio."});			
			ok = 0;
		}
		else if($("#nExpediente").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo No de Expediente es obligatorio."});
			ok = 0;
		}
		else if($("#fecha_demanda").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo de Fecha Inicio de demanda es obligatorio."});
			ok = 0;
		}
		else if($("#nOficio").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo No de Oficio es obligatorio."});
			ok = 0;
		}
		else if($("#fecha_oficio").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Campo de Fecha de Oficio es obligatorio."});
			ok = 0;
		}		
		else if($("#cIdTipoPersonaRFC").val() == "2"){
			if ($("#cNombre").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Nombre es obligatorio."});				
			}
			else if($("#cApellidoPaterno").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Apellido Paterno es obligatorio."});
			}
			else if($("#cApellidoMaterno").val() == ""){
				Swal.fire({ icon: "warning",
							text: "Campo Apellido Materno es obligatorio."});
			}
		}
		else if ( $("#nesPatrimonial").prop("checked")) {				
			if ( $("#nMonto").val() == "" ){
				Swal.fire({ icon: "warning",
							text: "El pasivo es patrimonial, debe de capturar el monto estimado."});	
				ok = 0;
			}
		}
		else if ( $("#cIdJunta").val() == -1) {						
			Swal.fire({ icon: "warning",
						text: "Debes seleccionar una Junta de Conciliación."});	
			ok = 0;		
		}
		
		if ( $("#nesPatrimonial").prop("checked")) {
			$("#esPatrimonial").val(1);					
		}
		else {
			$("#esPatrimonial").val(0);			
		}
		
		if( $("#nesExtranjero").prop("checked")) {
			$("#esExtranjero").val(1);			
		}
		else {
			$("#esExtranjero").val(0);			
		}
		
		const date1 = new Date($("#fechaO").val());
		const year = new Date(date1).getFullYear();
		const month = new Date(date1).getMonth()+1;		
		
		day = lastDayOfMonth(year,month).getDate();
		
		fecha_aplicacion = year + "/" + (month < 10 ? "0" + month : month) + "/" + day;
		                
		$("#fecha_alta").val(fecha_aplicacion);		
		
		if (ok == 1){							
			Swal.fire({				  
				  text: "¿Esta seguro de desea continuar para guardar y generar la poliza del alta del pasivo contingente?",
				  icon: "warning",
				  showCancelButton: true,
				  confirmButtonColor: '#7066E0',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
			}).then((result) => {
				if (result.isConfirmed) {
					queryFormPost("tAltaPasivoContingenteCreate", {async : true});
												
					document.TempForm.action="../reportes/AltaPasivosContingentes";
					document.TempForm.method="POST";
					document.TempForm.submit();	
										
					if( msg != "" )
						Swal.fire({ icon: "warning",
									text: msg});																								
				}
			})					
		}
				
	}
	
	function  lastDayOfMonth(year, month){
    	return  new Date(year, month, 0);//.getDate();           
   	}
	
	function toDate( strDate ){
		var dateComp = strDate.split("/");
		var year = parseInt(dateComp[0], 10);
		var month = parseInt(dateComp[1], 10);
		var day = parseInt(dateComp[2], 10);
		var datef = new Date(year, month-1, day);
		return datef;
	}
	
	function limpiaCampos(){
		$("#cRFC").val("");
		$("#nesPatrimonial").removeAttr("checked");
		$("#cIdTipoPersonaRFC").val("2");
		$("#apoderado").hide();
		$("#aRaz").hide();
		$("#cRazon").val("");
		$("#cNombre").val("");
		$("#cApellidoPaterno").val("");
		$("#cApellidoMaterno").val("");
		$("#nesExtranjeroChk").removeAttr("checked");
		$("#nExpediente").val("");
		$("#fecha_demanda").val("");
		$("#nOficio").val("");
		$("#fecha_oficio").val("");
		$("#nMonto").val("$");
		$("#nesPatrimonialChk").val("");		
		$("#fecha_alta").val("");
	}
		
	function upperCase(e) {		
		e.value = e.value.toUpperCase();
	}
		
</script>

</head>
<body>	
		
	<div class="container" style="width: 60%" id="container">

		<form action="" id="TempForm" name="TempForm">
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
			<input type="hidden" id="cCentroContable" name="cCentroContable"/> 				
			<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />
			<input type="hidden" name="esPatrimonial" id="esPatrimonial" value="0" />
			<input type="hidden" name="esExtranjero" id="esExtranjero" value="0" />		
			<input type="hidden" name="fecha_aplicacion" id="fecha_aplicacion" value="<%=today%>" /> 
			<input type="hidden" name="fecha_captura" id="fecha_captura" value="<%=today%>" />
			<input type="hidden" name="fecha_demanda" id="fecha_demanda" />
			<input type="hidden" name="fecha_oficio" id="fecha_oficio" />
			<input type="hidden" name="existeBeneficiario" id="existeBeneficiario" value=""/>
			<input type="hidden" name="cRazonSocialBEN" id="cRazonSocialBEN" value=""/>
			<input type="hidden" name="nombreBEN" id="nombreBEN" value=""/>
			<input type="hidden" name="apPatBEN" id="apPatBEN" value=""/>
			<input type="hidden" name="apMatBEN" id="apMatBEN" value=""/>
			<input type="hidden" name="tipoPersonaBEN" id="tipoPersonaBEN" value=""/>
			<input type="hidden" name="reportPath" id="reportPath" value="<%=reportPath%>" />
						
		<br/>
			<div class="card-header"> <h3> Alta y Busqueda de Pasivo Contingente </h3> </div>
			<hr class="mt-3">
							
			<h5> Datos Generales </h5>
			<hr class="mt-3">

			<div class="row">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12">
					<p class="text-warning bg-dark"> Antes de dar de alta se hace una busqueda en el catalogo de Beneficiarios, si existe se llenan en automatico el Nombres y los Apellidos o Razon Social si es persona Moral.</p>
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">									
					<label for="cRFC" class="form-label">RFC</label>
					<input type="text" name="cRFC" id="cRFC" class="form-control form-control-sm" placeholder="AABC220101ABC" onkeyup="upperCase(this);">													 																
				</div>				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label for="pc" class="form-label">Subcuenta</label>
					<input type="text" name="cSubcuenta" id="cSubcuenta" class="form-control form-control-sm" style="width: 10em;" maxlength="7" onblur="ChangeCase(this);" value="<%=cSubcuenta%>" readonly/>
				</div>				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label class="form-label">Tipo Pasivo Contingente</label>	
					<div class="form-check">								
						<input type="radio" name="tipoPasivoChk" id="nesLaboral" class="form-check-input" value = "0" onclick="setEsLaboral()" checked/>
						<label for="laboral" class="form-check-label">Laboral</label>
					</div>
					<div class="form-check">
						<input type="radio" name="tipoPasivoChk" id="nesPatrimonial" class="form-check-input" value = "1" onclick="setEsPatrimonial()"/>								
						<label for="patrimonial" class="form-check-label">Patrimonial</label>
					</div>							
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					<button type="button" id="existe" name="existe" class="btn btn-warning btn-sm" onclick="existenBeneficiarios()">¿Existe?</button>
				</div>
			</div>
			
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>				
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label class="form-label">Tipo Persona</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<select name="cIdTipoPersonaRFC" id="cIdTipoPersonaRFC" class="form-select form-select-sm" style="width: 12em;" onChange="tipoPersonaChange(this)">
							<option id="1" value="1">PERSONA MORAL</option>
							<option id="2" value="2" selected>PERSONA FISICA</option>																		
						</select>							
					</div>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<div class="form-check">
						<input type="checkbox" name="nesExtranjero" id="nesExtranjero" class="form-check-input" value = "0">
						<label for="Extranjero" class="form-check-label">Es Extranjero</label>
					</div>
				</div>
			</div>
			
			<br/>
			
			<div class="row" id="apoderado">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12">
					<label for="rz" class="form-label">Razón Social</label>				
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-building"></i></span>				
						<input type="text" name="cRazon" id="cRazon" class="form-control form-control-sm" maxlength="200" onkeyup="upperCase(this);">
					</div>
				</div>
			</div>

			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label for="nombre" class="form-label">Nombre</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cNombre" id="cNombre" class="form-control form-control-sm" style="width: 20em;" maxlength="200" onkeyup="upperCase(this);"/>
					</div>							
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label for="apPaterno" class="form-label">Apellido Paterno</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cApellidoPaterno" id="cApellidoPaterno" class="form-control form-control-sm" style="width: 20em;" maxlength="200" onkeyup="upperCase(this);"/>
					</div>							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label for="apMaterno" class="form-label">Apellido Materno</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cApellidoMaterno" id="cApellidoMaterno" class="form-control form-control-sm" style="width: 20em;" maxlength="200" onkeyup="upperCase(this);"/>
					</div>							
				</div>
			</div>
			
		<br/>
		
		<h5> Beneficiario </h5>
		<hr class="mt-3">
		
		<div class="row">
			<div class="col-12 col-lg-1 col-md-1 col-sm-12">
			</div>
			<div class="col-12 col-lg-3 col-md-3 col-sm-12">
				<label for="cRFCBen" class="form-label">RFC Beneficiario</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person-circle"></i></span>
					<input type="text" name="cRFCBen" id="cRFCBen" class="form-control form-control-sm" placeholder="AABC220101ABC" onkeyup="upperCase(this);"/>
				</div>							
			</div>
		</div>		
		<div class="row">
			<div class="col-12 col-lg-1 col-md-1 col-sm-12">
			</div>
			<div class="col-12 col-lg-5 col-md-5 col-sm-12">
				<label for="cNombreBen" class="form-label">Nombre Beneficiario</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" name="cNombreBen" id="cNombreBen" class="form-control form-control-sm" style="width: 20em;" maxlength="200" onkeyup="upperCase(this);"/>
				</div>							
			</div>
		</div>
		<div class="row">
			<div class="col-12 col-lg-1 col-md-1 col-sm-12">
			</div>
			<div class="col-12 col-lg-5 col-md-5 col-sm-12">
				<label for="cApPaternoBen" class="form-label">Apellido Paterno Beneficiario</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" name="cApPaternoBen" id="cApPaternoBen" class="form-control form-control-sm" style="width: 20em;" maxlength="200" onkeyup="upperCase(this);"/>
				</div>							
			</div>
			<div class="col-12 col-lg-5 col-md-5 col-sm-12">
				<label for="cApMaternoBen" class="form-label">Apellido Materno Beneficiario</label>
				<div class="input-group">								
					<span class="input-group-text"><i class="bi bi-person"></i></span>
					<input type="text" name="cApMaternoBen" id="cApMaternoBen" class="form-control form-control-sm" style="width: 20em;" maxlength="200" onkeyup="upperCase(this);"/>
				</div>							
			</div>
		</div>
			
		<br/>
			
			<h5> Expediente </h5>
			<hr class="mt-3">

			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<label for="expediente" class="form-label">No. Expediente</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-folder"></i></span>
						<input type="text" name="nExpediente" id="nExpediente" class="form-control form-control-sm" onkeyup="upperCase(this);"/>
					</div>							
				</div>
				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
					<div class="form-group">
						<label for="fechaD">Fecha Inicio Demanda:</label>
	                    <div class="input-group date" id="datepicker1">
	                    	<input type="date" class="form-control form-control-sm" id="fechaD" name="fechaD"/>                                    
	                    </div>
	                </div>										
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="cIdJunta" class="form-label">Junta Conciliacion</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-building"></i></span>						
						<select id="cIdJunta" name="cIdJunta" class="form-select form-select-sm">
		            		<option value="-1">Selecciona una Junta de Conciliacion...</option>
			            </select>
					</div>							
				</div>
			</div>

			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-2">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<label for="oficio" class="form-label">No. oficio</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-file-earmark-text"></i></span>
						<input type="text" name="nOficio" id="nOficio" class="form-control form-control-sm" onkeyup="upperCase(this);"/>
					</div>							
				</div>
				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<div class="form-group">
						<label for="fechaO">Fecha Oficio:</label>
	                    <div class="input-group date" id="datepicker1">
	                    	<input type="date" class="form-control form-control-sm" id="fechaO" name="fechaO"/>                                    
	                    </div>
	                </div>										
				</div>
			</div>

			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<label for="montoEstimado" class="form-label">Monto Estimado</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>
						<input type="text" name="nMonto" id="nMonto" class="form-control form-control-sm" placeholder="0.00"/>
					</div>							
				</div>
				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<div class="row" id="interes">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12">
							<label class="form-label">Interes</label>							
							<select name="nInteres" id="nInteres" class="form-select form-select-sm" style="width: 12em;">
								<option id="1" value="0.09">9% Anual</option>
								<option id="2" value="0.0098">0.98% Mensual</option>																		
							</select>																		
						</div>
					</div>
				</div>

				<div class="col-12 col-md-6 mb-3" id="aaFechaCap">
					<label for="fechaCaptura" class="form-label">Registro en Contabilidad</label>
					<input type="text" name="fecha_alta" id="fecha_alta" class="form-control form-control-sm" style="width: 15em;" data-date-format="mm/dd/yyyy"/>							
				</div>
			</div>
			
		<br/>
		
			<div class="row">
				<div class="col-12 d-flex justify-content-center">
					<div>
						<button type="button" id="guardar" name="guardar" class="btn btn-secondary btn-sm" onclick="guardaPasivo()" disabled="disabled">Guardar Alta</button>
						<button type="button" id="limpiar" name="limpiar" class="btn btn-outline-primary btn-sm" onclick="limpiaCampos()">Limpiar</button>
					</div>
				</div>
				<br/>									
			</div>
			<br/>											
		</form>
			
	</div>		
	
</body>
</html>
