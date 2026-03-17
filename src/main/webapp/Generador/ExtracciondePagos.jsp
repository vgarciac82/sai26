<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%
	String cUR = "";
	String cRamo = "";
	String cCentroContable="";

	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
		cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();

	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Extractor de Pagos</title>

		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">

		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />

		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";
		</style>
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script type="text/javascript" charset="utf-8">
			
	$(document).ready(function() {
		$(function() {
			$( "#fEmisionInc" ).datepicker({
				showOn: "button",
				showAnim:"slideDown",
				dateFormat: "yy-mm-dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});	
		$(function() {
			$( "#fEmisionFin" ).datepicker({
				showOn: "button",
				showAnim:"slideDown",
				dateFormat: "yy-mm-dd",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
		});	


		querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {async: false });

		
		$(this).ajaxForm({
			dataType:  "json",
			success: formSubmited
		});
					
		$("#pbExtraccion")
			.button()
			.click(function() {
				var szTemp = "";
				
				if ( $("#DESTINO_GASTO").val() != "" ){
					szTemp = " Destino = '" + $("#DESTINO_GASTO").val() + "'";  
				}

				if ( $("#fEmisionInc").val() == "" || $("#fEmisionFin").val() == "" ){
					alert("Debe Capturar un Rango de Fechas para Realizar la Extracción de la Información");
					return;
				}

				if ( $("#fEmisionInc").val() != "" && $("#fEmisionFin").val() != "" ){
					if (szTemp != '') szTemp = szTemp + " AND ";
					szTemp = szTemp + " Convert(date, fAplicacion) BETWEEN '" + $("#fEmisionInc").val() + "' AND '" + $("#fEmisionFin").val() + "'";  
				}
				
				$("#szTemp").val( " WHERE " + szTemp );
				document.ExportarForm.submit();

		});	
	
	});
		
		function formSubmited() {
                alert("Beneficiario enviado!");
        }
		
		

		
</script>
</head>
	<body id="dt_example" >
		<form id="ExportarForm" name="ExportarForm" action="../gstnmngr/generaLayoutOAServlet" method="post"> 
			<div id="container" style="width:1000px; padding-left:100px" class="SyCData">	
				<input type="hidden" name="szTemp" id="szTemp"/>
				<input type="hidden" name="szTabla" id="szTabla" value="ExtractorPagos"/>
				<input type="hidden" name="cDocumento" id="cDocumento" value="TODOS"/>
				<h1>Extractor de Pagos</h1>	
				<fieldset style="background-color:#CCCCCC" >
				</fieldset>	
				<fieldset>
					<table id="tblExtractor" align="center" >
						<tr align="center">
							<td>&nbsp;</td>
						</tr>
						<tr align="left">
							<td valign="top" >Tipo Destino:</td>
							<td valign="top" colspan="3">
								<select id="DESTINO_GASTO" name="DESTINO_GASTO"></select>
							</td>
						</tr>
						<tr align="left">
								<td>&nbsp; &nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
						</tr>
						<tr align="left">
							<td align="right">Fecha desde:</td>
							<td >
								<input style="background-color:#F5F5F5;" type="text" size="8" name="fEmisionInc" id="fEmisionInc" readonly />
							</td>
							<td align="right">Hasta:</td>
							<td>
								<input style="background-color:#F5F5F5;" type="text" size="8" name="fEmisionFin" id="fEmisionFin" readonly />
							</td>
						</tr>
						<tr align="center">
							<td>&nbsp;</td>
						</tr>
					</table>
					
				</fieldset>
				<fieldset>
						<table align="center">
							<tr align="center">
								<td>&nbsp;</td>
							</tr>
							<tr align="center">
								<td>
									<input type="button" id="pbExtraccion" value="Extracción"/>
								</td>
							</tr>
							<tr align="center">
								<td>&nbsp;</td>
							</tr>
						</table>
				</fieldset>
				<fieldset style="background-color:#CCCCCC" >
				</fieldset>		
			</div>
		</form>
	</body>
</html>