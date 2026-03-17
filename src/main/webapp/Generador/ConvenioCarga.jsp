<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	String ur = usuario.getU_UR();
	String login = usuario.getLogin();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());
	
	String mensaje = (request.getParameter("mensaje") == null) ? "No hay datos cargados todavía..." : request.getParameter("mensaje") ;
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>Carga de Convenios</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Carga Convenios Desembolso">
	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />	
	<style type="text/css" title="currentStyle">
			@import "css/demo_page.css";
			@import "css/demo_table_jui.css";
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>

	<!-- style>
		table, td, th
		{
			border:1px solid silver;
		}
		th
		{
			background-color:4D8EB9;
			/*background-color:#D8D8D8;*/
			color:424242;
			font-size: 15px;
		}
	</style -->
<!-- link rel="stylesheet" type="text/css" href="css/tcal.css" / -->
	
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	
<script type="text/javascript" src="js/styletable.jquery.plugin.js"></script>  
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>

<script type="text/javascript" charset="utf-8">
$(document).ready(function(){

		$("#btnCargarConvenio").show();
		
		$("#txtAreaMensaje").val("<%=mensaje%>");
		//NOMBRE DEL BOTON
		$("#btnCargarConvenio").click(function (){ 
		
			$("#frmConvenioCargaDesembolso").attr("action","../gstnmngr/ConvenioDesemServlet");
			$("#frmConvenioCargaDesembolso").attr("enctype","multipart/form-data");
			$("#frmConvenioCargaDesembolso").submit();
		
		 });
		 
	});

</script>
</head>
<body id="dt_example">

<form method="post" id="frmConvenioCargaDesembolso" name="frmConvenioCargaDesembolso" >
<center>
	
	<div id="container" class="container" style="width:1100px">
			<h1><img id="imgPlayStop" style="visibility: hidden" src="imagenes/wait24trans.gif">Convenios Modificatorios <label id="lbOperacion" style="font-size: 8pt"></label></h1>


			<h2> Carga de Archivo CSV para Importar información de Convenios </h2>
			
			<table border="0" align="center" width="100%"  cellspacing="1" cellpadding="1">
				<tr>
					<td style = "hidden" >
					</td>
					<td style = "hidden" >
					</td>
					<td style = "hidden" >
					</td>
				</tr>			
			</table>
  		
  			<fieldset>

			<table border="0" align="center" width="800px">
				<tr>
					<td>Ruta y Nombre de Archivo</td>
					<td align="center"><input type="file" id="flComprometido" name="flComprometido" size=40 /></td>
					<td colspan="1" align="center"><input type="button" id="btnCargarConvenio" name="btnCargarConvenio"  value="Cargar" /></td>
				</tr>
			</table>
			</fieldset>
  
  
  			<br>
  
  		<div id="dialog-form-Busqueda" title="Mensaje de Retroalimentación" style="width:100%;overflow-x:hidden;overflow-y:hidden">
				<textarea id="txtAreaMensaje" name="txtAreaMensaje"  rows="10"style="width:100%">
				</textarea>
		</div>
  
  
			<div align="center">
					<label id="esperar" style="visibility: hidden">	Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
					</label>
			</div>	
				<input type="hidden" name="mensaje" id="mensaje" value="" />
				<input type="hidden" name="tipoArchivo" id="tipoArchivo" value="cargaArchivo"/>				
		</div>
</center>
</form>			
 </body>
</html>