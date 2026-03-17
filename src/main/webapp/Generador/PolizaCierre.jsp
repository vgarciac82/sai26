
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

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
			
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	

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
<title>Poliza Cierre</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	
	var msg = "<%=msg%>";
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	$("#usuario").val("<%=algo%>");
	
	
	$(function() {
		$("#fecha_aplicacion").datepicker({
			showOn : "button",
			dateFormat : "yy/mm/dd",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
		});
	});
	
	$("#guardaVersion").button();
	$("#polizaTemporal").button();
	document.getElementById("guardaVersion").style.display = "none";	
	
	
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
		
	$("#guardaVersion").button().click(function() {
		document.TempForm.action="../reportes/PolizaCierre";
		document.TempForm.method="POST";
		document.TempForm.submit();
	});

	if (msg != "")
		$("#msgDialog").dialog("open");
});
		
	function extraeTemporal(){
		document.TempForm.action="../reportes/PolizaCierre";
		document.TempForm.method="GET";
		document.TempForm.target="_blank";
		document.TempForm.submit();
	}	
	
	function habilita(){		
		if($("#versionFinal").prop("checked")){
			document.getElementById("guardaVersion").style.display = "block";
		}
		else {
			document.getElementById("guardaVersion").style.display = "none";	
		}
		
	}			

</script>

</head>
<body id="dt_example">
<br/>
<br/>
<br/>

	<form id="TempForm" name="TempForm" >
	
		<div id="temp" style="width: 50%" class="container" >
			
			<div class="card-header"> <h3> Genera Póliza de Cierre </h3> </div>		
				<h1>
				</h1>
				<div class="mt-2 row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
						<input align = "right" id="polizaTemporal" name="polizaTemporal" value="Consulta movimientos" type="button" onclick="extraeTemporal()" class="btn btn-secondary"/>
					</div>
				</div>	
				<div class="mt-2 row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
						Finalizar
						<input type="checkbox" id="versionFinal" name="versionFinal" onclick="habilita()" />
					</div>
				</div>	
		
				<div class="mt-2 row d-flex justify-content-center">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
						<input id="guardaVersion" name="guardaVersion" value=" Final  " type="button"  class="btn btn-secondary"/>
					</div>
				</div>	
		</div>
		
		<div id="msgDialog" title="Resultado de Carga">
			<fieldset>
				<!-- <legend>Resultado de carga.</legend> -->
				<table align="center">
					<tr>
						<td align="center"><textarea rows="10" cols="40" id="msgTxt"><%=msg.replaceAll("<br>", "\n*")%></textarea>
						</td>
					</tr>
				</table>
			</fieldset>
		</div>

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 				
		<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />		
		
	</form>

</body>
</html>


