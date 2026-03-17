<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%
	String tipoPago = request.getParameter("tipo_pago");
	String rfc = request.getParameter("RFC");
	String contratoVales = StringUtils.isBlank(  request.getParameter("contratoVales") )? "false": request.getParameter("contratoVales");
	String msg = "";
	
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	
		<!-- Estilos estandar para los controles JQuery -->
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
		<link rel="stylesheet" type="text/css" href="../css/interfaz.css"/>
		<style type="text/css">
			#dt_example .container {
				width: 500px;
			}
		</style>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript">
			var tipoPago = '<%=tipoPago%>';
			var rfc = '<%=rfc%>';
			var msg = "<%=msg%>";
			var contratoVales = "<%=contratoVales%>";
			
			$(document).ready(
				function(){
					$("#contratoVales").val(contratoVales);
					$("#tipo_pago").val(tipoPago);
					$("#btnAceptar").click(function(){
						window.parent.frames['content-iframe'].location.href = 'ReemplazaFactura.jsp';
						}
					);
					 
						$("#msgDialog").show();
				}
			);
			
			  
		</script>

	</head>
	<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
		<div id="container" class="container SyCData">
			<h1>Resultado de Reemplazo:</h1>
				<input type="hidden" id="contratoVales" name="contratoVales" value="">
				<input type="hidden" id="tipo_pago" name="tipo_pago" value="">
				<input type="hidden" id="cIdRFC" name="cIdRFC" value="">
				
				<div id="msgDialog" title="Resultado de Carga">
					<fieldset>
						<legend>Resultado de carga.</legend>
							<table align="center">
								<tr>
									<td align="center">
										<textarea rows="10" cols="40" id="msgTxt"><%=msg.replaceAll("<br>", "\n*")%></textarea>
									</td>
								</tr>
								<tr>
									<td align="right">
										<input type="button" id="btnAceptar" value="Aceptar" class="btnInterfaceBG"/>
									</td>
								</tr>
							</table>
					</fieldset>
				</div>
		</div>
	</body>
</html>