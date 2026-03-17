
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
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";
	System.out.println(today);	
	
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
			
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	
	
	String finicial = "01/01/" + efa;
	System.out.println(finicial);	
	
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
	//finicial = usuario.getPropiedad("aEjercicioFiscal").getValor();
	
	
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();		
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}
	
	//Se agrega boton para limpieza de las cedulas
	boolean usrLimpiaCedula = ( usuario.getPropiedad("BORRA_CEDULA") != null && "SI".equalsIgnoreCase( usuario.getPropiedad("BORRA_CEDULA").getValor() )  );

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
	
	String msg = "";
	boolean esRespuesta=false;
	if (session.getAttribute("msg") != null) {
		msg = (String) session.getAttribute("msg");
		session.removeAttribute("msg");
		esRespuesta = true;
	}		
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte Acreedores y Deudores</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

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
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
	 var usrLimpiaCedula = <%=usrLimpiaCedula%>;
	 var msg = "<%=msg%>";
	 var esRespuesta = <%=esRespuesta%>;
	 						
	$(document).ready(function() {	
		
		document.getElementById("AcUnidad").style.display = "block";
		
		if( usrLimpiaCedula )
			$("#limpiarDiv").show();
			
		$("#cCentroContable").val( "<%=cCentroContable%>");	
	    $("#limpiarBtn").button();
	    
	    $("#fechaI").val(moment().format('yyyy-01-01'));
		$("#fechaF").val(moment().format('yyyy-MM-DD'));
		
	    $("#cmdReporteDevengado").button().click(function(){
	    	
	    	extraeDev();
	    });
	    
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
		
		$("#cmdxlsReporteProg").button();
		
		if(esRespuesta){
			$("#msgDialog").dialog("open");
		}
	});	
	
	function extrae(){	
		if($("#fechaI").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Debes seleccinar una fecha de Inicio."});
			return false;
		} else if($("#fechaF").val() ==""){
			Swal.fire({ icon: "warning",
						text: "Debes seleccinar una fecha de Fin."});
			return false;
		}
		
		$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
		$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
		document.getElementById("TIPO").value = "";		
		document.ExportarForm.submit();				
	}		
	
	function extraeDev(){								
		$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
		$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
		document.getElementById("TIPO").value = "Devengado";
		document.ExportarForm.submit();		
	
	}		
	
	function limpiarCedula(){
		if($("#fechaF").val() ==""){
			Swal.fire({ icon: "warning",
						text: "Debes seleccinar una fecha de Fin."});
			return false;
		}		
		$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
		document.getElementById("limpiar").checked=true;		
		document.ExportarForm.submit();		
	}
	
	function mostrarFComision(){
		$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
		$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));
		if ( $("#TIPO_REPORTE2").prop("checked")) {				
			document.getElementById("fComision").style.display = "block";
			document.getElementById("AcUnidad").style.display = "none";			
		}else if ( $("#TIPO_REPORTE1").prop("checked")){
			document.getElementById("fComision").style.display = "none";
			document.getElementById("AcUnidad").style.display = "block";
			$("#fechaComision").prop("checked", false);  
		}
	}

</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteAcreedoresDeudores" method="get">	

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/>
		<input type="hidden" id="TIPO" name="TIPO"/> 
		<input type="hidden" id="xls" name="xls" value='xls' /> 		
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="fecha_fin" name="fecha_fin"/>
		
		<div id="container" style="width: 60%" class="container">
			
			<div class="card-header"> <h3> Cedula de Acreedores y Deudores </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<input type="radio" name="TIPO_REPORTE" id="TIPO_REPORTE1" onclick="mostrarFComision()" value="Acreedores" class="form-check-input" checked/>
					<label for="TIPO_REPORTE1" class="form-check-label">Acreedores</label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<input type="radio" name="TIPO_REPORTE" id="TIPO_REPORTE2" onclick="mostrarFComision()" value="Deudores" class="form-check-input"/>
					<label for="TIPO_REPORTE2" class="form-check-label">Deudores</label>
				</div>
			</div>
						
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">
                       <label for="fecha_inicio">Desde:</label>
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI" value="<%=finicial%>"/>                                    
                       </div>
                	</div>										
				</div>			        				
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">
                       <label for="fecha_fin">Hasta:</label>
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                    
                       </div>
                	</div>										
				</div>			        				
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="analitica" class="form-check-label">Analítica</label>
					<input type="checkbox" name="analitica" value="analitica" class="form-check-input"/>						
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="cmdxlsReporteProg" name="cmdxlsReporteProg" value="Extraer" class="btn btn-secondary" onclick="extrae()"/>
				</div>
			</div>
					
			<div style="display:none;" id="fComision">
				<div class="row d-flex justify-content-center">
					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						Con fecha de comisión<input type="checkbox" id="fechaComision" name="fechaComision" class="form-check-input" value="1"/>
					</div>
				</div>					
			</div>					
			
			<div style="display:none;" id="AcUnidad">
				<div class="row d-flex justify-content-center">
					
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
						Con Unidad Ejecutora<input type="checkbox" id="conUnidad" name="conUnidad" class="form-check-input" value="1"/>
					</div>
				</div>					
			</div>	
			
			<br/>
					
			<div>
				<h6> Devengados </h6>
				<hr class="mt-3"/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
						<input type="button" id="cmdReporteDevengado" name="cmdReporteDevengado" value="Extraer Reporte Devengados" class="btn btn-primary" onclick="extraeDev()"/>
					</div>
				</div>	
			</div>	
			
			<br/>
				
			<div style="display:none;" id="limpiarDiv">			
				<h6> Operaciones de Administracion </h6> 
				<hr class="mt-3"/>
				
				<div class="row d-flex">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
						<input type="button" id="btnInterfaceBG" name="btnInterfaceBG" value="Limpiar Cedula" class="btn btn-dark" alt="Elimina la informacion actual para reemplazarla." onclick="limpiarCedula()"/>
						<input type="radio" name="limpiar" id="limpiar" value="limpiar" style="visibility: hidden"/>
					</div>
				</div>		
			</div>							
		</div>		
			
		<div id="msgDialog" title="Resultado" class="container">
			<fieldset>
				<legend>Resultado de carga.</legend>
				<table align="center">
					<tr>
						<td align="center"><textarea rows="5" cols="30" id="msgTxt"><%=msg.replaceAll("<br>", "\n*")%></textarea>
						</td>
					</tr>
				</table>
			</fieldset>
		</div>											
	</form>		
</body>
</html>


