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
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);

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
	
	CargaFolios();
	
   
     });
	
		
	function CargaFolios(){
	
	querySelectPost("ReporteConsultaPlurianual", "Select_NumeroContrato",{async : false});
		
	}
	
	






function BuscarFolio(){

 //alert("BuscarFolio");
   $("#nFolioPurianual").val($("#Select_NumeroContrato").val());
   var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=\ReporteContratoPlurianual.jasper&folioPlurianual="+  $("#nFolioPurianual").val();
   
  // $.blockUI( {
	//					message : "Generando Reporte Plurianual (" +  $("#nFolioPurianual").val() + ")........"
		//			});


 
 $.blockUI({ message: "Generando Reporte Plurianual (" +  $("#nFolioPurianual").val() + ")........" });

        setTimeout($.unblockUI, 2000);
   window.open(url, "popacuse","scrollbars=1, resizable=yes, width=300, height=200");	

}
	</script>

	</head>
	
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form action="" method="post" name="frm" id="frm">
	
	 <input type="hidden" Id="nFolioPurianual" name="nFolioPurianual"> 
		<div id="container" class="container SyCData">
			<h1 >
				Reporte Plurianual
			</h1>
			<div id="busqueda" style="display: block;">
				<table id="reAplica_Docto" width="30%" align ="center" border="0" >
					<tr>
						<td align="right">Folio: </td> 
				     	
						<td align="left"><select  style="width: 70px" id="Select_NumeroContrato"
							name="Select_NumeroContrato">
						</select>
						</td>
					</tr>
					<tr>
						<td align="center"  colspan="3">
					    <input type="button" id="btnBuscar" name ="btnBuscar" value="Imprimir" onclick="javascript:BuscarFolio();" ></input>
						<!-- <input type="button" id="Limpiar" name ="Limpiar" value="Limpiar" onclick="limpiarSesion();"></input>-->
						
						</td>
				   </tr>
				
				</table>
			</div>
			
			
		</div>
	</body>
</html>
