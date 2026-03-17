<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%
//Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

/*if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}*/

//final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();


String mensaje = "";
if (request.getParameter("msg") != null
		&& !"".equals(request.getParameter("msg"))) {
	mensaje = request.getParameter("msg");
	mensaje = mensaje.replace("[", "");
	mensaje = mensaje.replace("]", "");
	mensaje = mensaje.replace(",", "<br>");
}



/*int id_oper = -1;
if (request.getParameter("id_oper") != null)
	id_oper = new Integer(request.getParameter("id_oper")).intValue();
else
	id_oper = c.getCasoOperacion(0).getIdOperacion();*/

Empleado e = new Empleado();
String cEjercicio;
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);


EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);
String UR = usuario.getU_UR();
String uNombre = usuario.getNombre();




AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
cEjercicio = adecProy.obtenEjercicioFiscal();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title></title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<link type="text/css" rel="stylesheet" href="../css/themes/base/jquery.ui.all.css" />
		<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />

		<style type="text/css" title="currentStyle">
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		
		
		<style type="text/css">
			.calendar_1 {					
			background-color: #F5F5f5;
			padding-left: 6px;
			}
			.calendar_2 {				
				text-align: right;
				background-color: #E2E4FF;
				padding-left: 6px;
				padding-right: 6px;
			}
		</style>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.core"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/ui/jquery.ui.accordion.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

	<script type="text/javascript" charset="utf-8">
	$(document).ready(function() {
	
	Unormativa();
	CargaPartida();
	
	var d = new Date();
    var month = d.getMonth()+1;
    $("#Select_Mes").val(String(month));
		

		
    
     });
	
		
	function CargaPartida(){
	
	querySelectPost("PartidasComsocReporte", "Select_Partida",{async : false});
		
	}
	
	
	function Unormativa(){
	$("#Unormativa").val('<%=UR%>');
	$("#ParametroNormativa").val('<%=UR%>');
	
	querySelectPost("catAreaContableReporteComsoc", "Select_UEjecutora", {async : false});
	
	if ('<%=UR%>' != 'A02'){ 
	 $("#Select_UEjecutora").val('<%=UR%>');
	 $("#Select_UEjecutora").attr("disabled",true);
	}
	
	
}



 
	 
	 
  function Imprimir () {
  
    
	   window.open(	"../admin/SeguridadCatalogos?xls=SI"
					 + "&catalogo=REPORTE&accion=run"
					 + "&rn=\ReportePagoAnticipado.jasper"
					 + "&nFolio="+  $("#nPartida").val()+ "&Unormativa="+ $("#Select_UEjecutora").val()+"&mes="+ month ,
					 +"popacuse",
					 +"scrollbars=1, resizable=yes, width=300, height=200"
    			 );
	 
  
   
} 
 	 
 	
	</script>

	</head>
	
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form action="" method="post" name="frm" id="frm">
	
	    <input type="hidden" Id="nPartida" name="nPartida">
	    <input type="hidden" Id="Unormativa" name="Unormativa">
	     <input type="hidden" Id="ParametroNormativa" name="ParametroNormativa">
	       <input type="hidden" Id="NomReporte" name="NomReporte">        
		<div id="container" class="container SyCData">
			<h1 >
				Reportes de Pago Anticipado
			</h1>
			<div id="busqueda" style="display: block;">
			
			  <fieldset>
				       <legend>Capture</legend>
				
				<table id="reAplica_Docto" width="30%" align ="center" border="0" >
				    <tr>
								<td width="50%">Folio:</td>
								<td><select id="selectfolio" name="selectfolio"></select>
								</td>
							
					</tr>
				 	<tr>
								<td width="50%">Oficio No.</td>
								<td ><input type="text" id="NoOficio"></td>
							
					</tr>
					<tr>
							
								<td>Nombre a quien  se Dirige:</td>
								<td>Puesto:</td>
								
					</tr>
					<tr>
								
								<td><textarea id="NomDirige" name="NomDirige"  rows="2" cols="30" ></td>
								<td><textarea id="PuestoDirige" name="PuestoDirige"  rows="3" cols="50" ></td>
								
					</tr>
					<tr>
							
								<td>Nombre del Remitente:</td>
								<td>Puesto:</td>
								
					</tr>
					<tr>
							
								<td><textarea id="NomRemite" name="NomRemite"  rows="2" cols="30" ></td>
								<td><textarea id="PuestoRemite" name="PuestoRemite"  rows="3" cols="50" ></td>
								
					</tr>
			    <tr>
						<td align="center" >
					    <input type="button" id="btnImprimir" name ="btnImprimir" value=" Imprimir " onclick="javascript:Imprimir();" ></td> 
					 	<!-- <input type="button" id="Limpiar" name ="Limpiar" value="Limpiar" onclick="limpiarSesion();"></input>-->
						</td>
				   </tr>
				
				</table>
				</fieldset>
			</div>
			
			
		</div>
	</body>
</html>
