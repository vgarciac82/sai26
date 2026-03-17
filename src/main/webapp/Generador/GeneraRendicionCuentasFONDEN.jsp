<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>
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

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	
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
	
	boolean success = false;
	if (session.getAttribute("success") != null) {
		success = "true".equals(  (String) session.getAttribute("success") );
		session.removeAttribute("RESULT");
	}
	
	
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	System.out.println("0: Fecha de Aplicacion "+fAplicacion[0]);
	System.out.println("1: Fecha Minima "+fAplicacion[1]);
	System.out.println("2: Fecha Maxima "+fAplicacion[2]);	

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Cedula y Poliza</title>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="-1" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />

<style type="text/css" title="currentStyle">
	@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
	@import "css/demo_table_jui.css";
	@import "css/demo_page.css";
</style>

<style>
.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}
</style>
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
<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	
	var msg = "<%=StringUtils.isEmpty( mensaje )?"":mensaje.replaceAll("\n", "").replaceAll("\r", "")%>";
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	$("#usuario").val("<%=algo%>");
	
	$( ".datepicker" ).datepicker({
		showOn: "button",			
		buttonImage: "../images/calendar.gif",
		buttonImageOnly: true,
		changeYear: true,
		changeMonth: true,
		minDate:new Date(<%=fAplicacion[1]%>),
		maxDate:new Date(<%=fAplicacion[2]%>)
		
	});		
	
	$("#generaPoliza").button();
	$("#cedula").button();	
	
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
	
	$("#generaPoliza").button().click(function() {
		if( $("#fecha_aplicacion").val() != "" && validaMesCaptura() ){		
			document.TempForm.action="../rendicioncuentasFONDEN/RendicionCuentasFONDEN";
			document.TempForm.method="POST";
			document.TempForm.submit();
		}
	});
		
// 	document.getElementById("dpoliza").style.display = "none";	
	if( msg != "" )
		alert( msg );
	
});
		
	function validaMesCaptura(){
		var continuar = true;
		var valFecha = $("#fecha_aplicacion").val();
		
		$("#polizaAutorizada").val("");
		
		if( valFecha != "" ){
			var mes = valFecha.split("/")[1];
			$("#nMes").val(mes);
			queryFormPost( {
				queryName:"existePolizaFONDEN",
				async:false,
				callback:function(){
					if( $("#polizaAutorizada").val() != "0" ){
						alert("El mes ya contiene una poliza registrada de redicion de cuentas de FONDEN");
						document.getElementById("generaPoliza").style.display = "none";						
						continuar = false;
					}else {
						document.getElementById("generaPoliza").style.display = "block";
					}
				}
			} );
		}
		return continuar;	
	}

	function extraeCedula(){
		document.TempForm.action="../rendicioncuentasFONDEN/RendicionCuentasFONDEN";
		document.TempForm.method="GET";
		document.TempForm.target="_blank";
		document.TempForm.submit();
	}			

</script>

</head>
<body id="dt_example">
<br/>
<br/>
	<form id="TempForm" name="TempForm" >		

	<div id=container style="width: 40%" class="container SyCData">		
		<input type="hidden" id="nMes" name="nMes" />
		<input type="hidden" id="polizaAutorizada" name="polizaAutorizada" value="0"/>
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/> 				
		<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />	
		<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="<%=efa%>"/>	

		<fieldset>					
			<h1>Póliza Rendición de Cuentas FONDEN</h1>			
																	
			<div id="dcedula">															
				<table align = "center">											
					<tr>
						<td align="center">
							Fecha Aplicación:
						</td>
						<td>
							<input type="text" name="fecha_aplicacion" id="fecha_aplicacion" size="12" onchange="validaMesCaptura()" value="<%= fAplicacion[0] %>" class="notEditable datepicker" />							
						</td>																
						<td align="center">
							<input align = "right" id="cedula" name="cedula" value="Vista Previa" type="button" onclick="extraeCedula()" />
						</td>							
						<td align = "center">
							<input id="generaPoliza" name="generaPoliza" value="Genera Póliza" type="button" />
						</td>						
					</tr>																																											
				</table>
				<br>													
			</div>										
										
		</fieldset>	
	</div>																					
</form>

</body>
</html>


