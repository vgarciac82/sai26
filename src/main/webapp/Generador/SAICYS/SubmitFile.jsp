<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%
	String cIdContratoDefinitivo=request.getParameter("contratoDef");
	String operacion=request.getParameter("operacion");
	
	
%>    
    
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Submit Files</title>
<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" /> 
		<style type="text/css" title="currentStyle"> 
			@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
	 		@import "../css/demo_table_jui.css"; 
			@import "../css/demo_page.css"; 
			@import "../css/demo_table.css"; 
			@import "../../css/interfaz.css";
		</style>
	
		<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../js/funciones.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript">
		
		$(document).ready(function() {
			$("#submitFrm").ajaxForm({
				dataType: "json",
				success: function(j) { 
			        swal(j[0].MSG,{icon:"info",button: "Cerrar"});
			        $("#esperar").dialog("close");
			        window.opener.cargaTabla();
			        window.close();
			    }
			});
			$("#esperar").dialog({
				autoOpen : false,
				height : 110,
				width : 200,
				modal : true,
				open: function(event, ui){
					$(".ui-dialog-titlebar").hide();
				},
				close : function() {
				}
			});
		});
		function ejecuta(){
			if($("#archivoZip").val()==""){
				swal("Favor de seleccionar un archivo.",{icon:"info",button: "Cerrar"});
			}else{
				$("#enviaArch").hide();
				$("#esperar").dialog("open");
				$("#submitFrm").submit();
			}
			
		}
		function ejecutaAjax(){
			var hayDocumento=false;
			var data = new FormData();
			jQuery.each(jQuery('#archivoZip')[0].files, function(i, file) {
			    data.append('file-'+i, file);
			    hayDocumento=true;
			});
			if(!hayDocumento){
				swal("Favor de seleccionar un archivo.",{icon:"info",button: "Cerrar"});
				return;
			}
			data.append('operacion', $("#operacion").val());
			data.append('cContratoDefinitivo', $("#cContratoDefinitivo").val());
			$("#esperar").dialog("open");
			jQuery.ajax({
			    url: '../../servlet/LeeArchivos',
			    data: data,
			    cache: false,
			    contentType: false,
			    dataType: "json",
			    processData: false,
			    method: 'POST',
			    type: 'POST', // For jQuery < 1.9
			    success: function(j){
			        swal({
			        	title: "",
			        	text: j[0].MSG,
			        	icon: "info",
			        	buttons: {
			        		confirm : "Cerrar"
			        		},
			        	}).then((continuar) => {
			        		$("#esperar").dialog("close");
					        window.opener.cargaTabla();
					        window.close();
			        });
			    }
			});
		}
	</script>
</head>
<body>
	<form id="submitFrm" action="../../servlet/LeeArchivos"  enctype = "multipart/form-data" method = "post" >
		<div id="divFiles">
			<fieldset>
				<legend id="lgndDiv">Enviar Documento.</legend>
				<table align="left" style="width: 100%">
					<tr>
						<td align="right">Archivo *.zip:</td>
						<td align="left"><input type="file" value="" id="archivoZip" name="archivoZip" width="300px" /> 
					</tr>
				</table>
			</fieldset>
		</div>
		<br/><br/><br/>
		<table align="right" style="width: 100%">
			<tr>
				<td align="right">
					<input type="button" value="Enviar Archivo" id="enviaArch" name="enviaArch" onclick="ejecutaAjax()" class="btnInterfaceBG ui-button ui-corner-all"/>
				</td>
			</tr>
		</table>
		<input type="hidden" id="operacion" name="operacion" value="<%=operacion %>"/>
		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo" value="<%=cIdContratoDefinitivo %>"/>
		
	</form>
	<div id="esperar" align="center" title="Espera">
			<fieldset>
				<table>
					<tr>
						<td>Espere por favor.... <img border="0"src="../../imagenes/espera.gif" height="30"></td>
					</tr>
				</table>
			</fieldset>
		</div>
</body>
</html>