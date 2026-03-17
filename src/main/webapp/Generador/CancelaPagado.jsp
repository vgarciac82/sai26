<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.model.GeneradorPolizaManualBusinessLogic"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%
	String folioPagado = request.getParameter("folioPagado");
	String cUE = request.getParameter("cUE");
	String mensaje = "";
	if( !StringUtils.isEmpty( folioPagado ) && !StringUtils.isEmpty( cUE ) ){
		GeneradorPolizaManualBusinessLogic gpmbl = new GeneradorPolizaManualBusinessLogic( GestionInterface.ATT_CONEXION );
		gpmbl.generaPolizaCancelaPago(folioPagado, cUE);
	}
		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Cancela Pagado</title>

  </head>
  
  <body>
    Cancela Pagado.
    <form action="CancelaPagado.jsp">
    	<table>
    		<tr>
    			<td>
    				Folio Pagado:
    			</td>
    		</tr>
    		<tr>
    			<td>
    				<input type="text" id="folioPagado" name="folioPagado" value="">
    			</td>
    		</tr>
    		<tr>
    			<td>
    				Unidad Ejecutora:
    			</td>
    		</tr>
    		<tr>
    			<td>
    				<input type="text" id="cUE" name="cUE" value="">
    			</td>
    		</tr>
    		<tr>
    			<td>
    				<input type="submit" value="Enviar">
    			</td>
    		</tr>
    	</table>
    </form>
  </body>
</html>
