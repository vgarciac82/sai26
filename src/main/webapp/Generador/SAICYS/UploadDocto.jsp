<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>My JSP 'UploadDocto.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
		@import "../../css/interfaz.css";
	</style>
	<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			var options = { 
				dataType: 'json', 
				success:function(j) {
					parent.window.document.getElementById("observaciones").value = j[0].MSG;
					//parent.window.document.getElementById("pb_cancel").style.visibility = "visible";
					window.parent.termina();
				},
				error: function(){
					window.parent.error();
				}
			}; 
		  	$('#formSubmitDocto').ajaxForm(options); 
		});
		function onSubmit() {
			//alert($("#descripcionCausa").val()+" operacion: "+$("#operacion").val()+" fechaTermino: "+$("#fechaTermino").val()+" nTipoTerminacionCont: "+$("#nTipoTerminacionCont").val()+" cFolio: "+$("#cFolio").val());
			if($("#uploadfile").val()==""){
				window.parent.faltaArchivo();
				return;
			}
			$("#formSubmitDocto").submit();
		}
		function onSubmitAjax(){
			var hayDocumento=false;
			var data = new FormData();
			jQuery.each(jQuery('#uploadfile')[0].files, function(i, file) {
			    data.append('file-'+i, file);
			    hayDocumento=true;
			});
			if(!hayDocumento){
				window.parent.faltaArchivo();
				return;
			}
			data.append('operacion', $("#operacion").val());
			data.append('descripcionCausa', $("#descripcionCausa").val());
			data.append('fechaTermino', $("#fechaTermino").val());
			data.append('fechaLimitePagoPendiente', $("#fechaLimitePagoPendiente").val());
			data.append('nTipoTerminacionCont', $("#nTipoTerminacionCont").val());
			data.append('cFolio', $("#cFolio").val());
			data.append('cContratoDefinitivo', $("#cContratoDefinitivo").val());
			data.append('fechaNotificacionUAF', $("#fechaNotificacionUAF").val());
			
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
			    	parent.window.document.getElementById("observaciones").value = j[0].MSG;
					window.parent.termina();
			    },error: function(){
			    	window.parent.error();
			    }
			});
		}
	</script>

  </head>
  	<form name="formSubmitDocto" id="formSubmitDocto"   enctype = "multipart/form-data" action="../../servlet/LeeArchivos" method = "post">
		<table align="left" style="width: 92%">
			<tr align="left">
				<td>
					Elegir archivo .zip:
					<input id="uploadfile" name="uploadfile" type="file" />
				</td>
			</tr>
		</table>
		<input type="hidden" id="operacion" name="operacion" value="3"/>
		<input type="hidden" id="descripcionCausa" name="descripcionCausa" value=""/>
		<input type="hidden" id="fechaTermino" name="fechaTermino" value=""/>
		<input type="hidden" id="fechaLimitePagoPendiente" name="fechaLimitePagoPendiente" value=""/>
		<input type="hidden" id="fechaNotificacionUAF" name="fechaNotificacionUAF" value=""/>
		<input type="hidden" id="nTipoTerminacionCont" name="nTipoTerminacionCont" value="1"/>
		<input type="hidden" id="cFolio" name="cFolio" value=""/>
		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo" value=""/>
		
	</form>
  </body>
</html>
