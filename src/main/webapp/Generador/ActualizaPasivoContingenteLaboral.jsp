
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "yyyy/MM/dd";//"dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";

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
	
	String msg = "";
	if (session.getAttribute("RESULT") != null) {
		msg = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Pasivos Contingentes</title>

<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" /> 

<link rel="stylesheet" href="css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="css/bootstrap-datetimepicker.min.css"></link>
<script src="js/bootstrap.bundle.min.js"></script>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

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
	
	var msg = "<%=msg%>";
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	$("#usuario").val("<%=algo%>");		
		
	$("#guardar").button();
	$("#limpiar").button();
	$("#apoderado").hide();
	$("#aRaz").hide();
	$("#aNom").show();
	$("#aPat").show();
	$("#aMat").show();
	$("#dTrimestral").hide();
	$("#dbuscaPasivo").show();	
	$("input.AyudaSyC").subIniciaDlg();
	

	if (msg != "")
		$("#msgDialog").dialog("open");
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
	
	function enviaConsulta() {			
		if (validaExisteImporte()){
			Swal.fire({				  
				  text: "¿Esta seguro de modificar el importe nuevamente?",
				  icon: "warning",
				  showCancelButton: true,
				  confirmButtonColor: '#7066E0',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
			}).then((result) => {
				if (result.isConfirmed) {						
					queryFormPost("tActualizaImporteM",
						{ 
							async : false,
							callback:function(){
								Swal.fire({ icon: "success",
											text: "Se modifico el importe exitosamente" });															
							}						
						});
				}
			})
		}		
		else{
			Swal.fire({ icon: "warning",
						text: "Solo se permite modificar un pasivo cuando no se ha subio el càlculo mensual por layout" });			
		}								
	}
	
	function actualizaBeneficiario() {					
		Swal.fire({				  
			  text: "¿Esta seguro de agregar el Beneficiario al Pasivo Contingente?",
			  icon: "warning",
			  showCancelButton: true,
			  confirmButtonColor: '#7066E0',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
		}).then((result) => {
			if (result.isConfirmed) {						
				queryFormPost("ActualizaBeneficiario",
					{ 
						async : false,
						callback:function(){
							Swal.fire({ icon: "success",
										text: "Se guardo exitosamente el beneficiario" });															
						}						
					});
			}
		})
									
	}
	
	function actualizaInfo(){
		if( $("#nesPatrimonial").val() == "0" )
			$("#nesLaboral").attr("checked", "checked");
		else if( $("#nesPatrimonial").val() == "1" )
			$("#nesPatrimonial").attr("checked", "checked");
			
		if( $("#nesExtranjero").val() == "1")
			$("#nesExtranjeroChk").attr("checked", "checked");
		else 
			$("#nesExtranjeroChk").removeAttr("checked");
			
		$("#cIdTipoPersonaRFC").change();
	}
	
	function limpiarConsulta(){
		$("#cRFCLaboral").val("");
		$("#cSubcuenta").val("");
		$("#nesPatrimonialChk").removeAttr("checked");
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
		$("#nMonto").val("");
		$("#nesPatrimonialChk").val("");		
		$("#fecha_captura").val(<%= today %>);
		$("#mImporte").val("");	
		$("#cMes").val("1");	
	}
	
	function validaExisteImporte(){					
		queryFormPost("tImporteMRead", {async : false});	
			if( Number( $("#contador").val() ) == 0)
				return false;
			else
				return true;		
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
		<input type="hidden" name="esPatrimonial" id="esPatrimonial" value="0" />
		<input type="hidden" name="esExtranjero" id="esExtranjero" value="0" />	
		<input type="hidden" name="idPasivoContingente" id="idPasivoContingente" value="" />	
		<input type="hidden" name="contador" id="contador" value="0" />	

		<div id="container" style="width: 60%" class="container">
			
			<div class="card-header"> <h3> Actualización Pasivo Contingente Laboral </h3> </div>
			<hr class="mt-3"/>
															
			<div id="dbuscaPasvio">					
				<div class="row">	
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>				
					<div class="col-12 col-lg-5 col-md-5 col-sm-12" id="prfcP">									
						<label for="cRFCLaboral" class="form-label">RFC</label>
						<div class="input-group">
							<input type="text" name="cRFCLaboral" id="cRFCLaboral" class="form-control AyudaSyC form-control-sm" onchange="actualizaInfo()" readonly/>															
						</div>
					</div>		
					<div class="col-12 col-lg-5 col-md-5 col-sm-12" id="Extranjero">
					<div class="form-check">
							<input type="checkbox" name="nesExtranjeroChk" id="nesExtranjeroChk" class="form-check-input" value = "0" disabled="disabled"/>
							<label for="Extranjero" class="form-check-label">Es Extranjero</label>
						</div>
					</div>					
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12">
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

					<div class="col-12 col-lg-5 col-md-5 col-sm-12">
						<label class="form-label">Tipo Persona</label>
						<select name="cIdTipoPersonaRFC" id="cIdTipoPersonaRFC" class="form-select form-select-sm" onChange="tipoPersonaChange(this)" disabled="disabled">
							<option id="1" value="1">PERSONA MORAL</option>
							<option id="2" value="2">PERSONA FISICA</option>																		
						</select>							
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
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12" id="aPat">
						<label for="apPaterno" class="form-label">Apellido Paterno</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" name="cApellidoPaterno" id="cApellidoPaterno" class="form-control form-control-sm"readonly/>
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
						<input type="text" name="cNombreBen" id="cNombreBen" class="form-control form-control-sm"/>
					</div>							
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<button type="button" id="actualiza" name="actualiza" class="btn btn-secondary btn-sm" onclick="actualizaBeneficiario()"> Actualizar Beneficiario </button>												
				</div>
			</div>
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label for="cApPaternoBen" class="form-label">Apellido Paterno Beneficiario</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cApPaternoBen" id="cApPaternoBen" class="form-control form-control-sm"/>
					</div>							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label for="cApMaternoBen" class="form-label">Apellido Materno Beneficiario</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" name="cApMaternoBen" id="cApMaternoBen" class="form-control form-control-sm"/>
					</div>							
				</div>
			</div>	
			
			<br/>
			<br/>										
			
			<div id="dMensual">		
				<h5> "Se guadara la modificaciòn del importe mensual en el registro seleccionado" </h5>
				<hr class="mt-3"/>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">										
						<label for="cMes" class="form-label"> Mes: </label>											
						<select id="cMes" name="cMes" class="form-select form-select-sm" style="width: 12em;">
							<option value="1">Enero</option>
							<option value="2">Febrero</option>
							<option value="3">Marzo</option>
							<option value="4">Abril</option>
							<option value="5">Mayo</option>
							<option value="6">Junio</option>
							<option value="7">Julio</option>
							<option value="8">Agosto</option>
							<option value="9">Septiembre</option>
							<option value="10">Octubre</option>
							<option value="11">Noviembre</option>
							<option value="12">Diciembre</option>
						</select>																													
					</div>																																										
				</div>
				
				<div class="row d-flex justify-content-center">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">							
						<label for="mImporte" class="form-label">Importe:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>					
							<input  type="text" id="mImporte" name="mImporte" class="form-control form-control-sm" placeholder="0.00" onfocus="quitaFmt(this.id)" onkeypress="valFmt(this,9)" onblur="moneyFrmt(this.id)"/>
						</div>																
					</div>
				</div>															
			</div>
				
			<br/>
			
			<div class="row">
				<div class="col-12 d-flex justify-content-center">
					<div>
						<button type="button" id="guardar" name="guardar" class="btn btn-secondary btn-sm" onclick="enviaConsulta()"> Guardar </button>
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
