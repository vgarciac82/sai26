<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="java.sql.Connection"%>
<%@page import="com.syc.contable.AccountingEngine"%>
<%@page import="com.syc.ws.inventario.WSManager"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	String mensaje="";
	if (request.getParameter("reAplicaWS") != null && "Si".equals(request.getParameter("reAplicaWS"))) {
		AccountingEngine ae = new AccountingEngine();
		ae.setValidaInsuficienciaDeSaldo(true);		
		Connection conn = ae.getConnection(GestionInterface.ATT_CONEXION);
		String nFolio="";
		try{
			nFolio="4908";
			int status = WSManager.sendAdquistion(conn, nFolio, 0);
			if(status!=0 && status!=-2){
				System.out.println("Error de respuesta del Web Service");
				throw new Exception("Error de respuesta del Web Service para el folio:" + nFolio);
			}
			
			conn.commit();
		}catch (Exception ex) {
				conn.rollback();			
				mensaje = ex.getMessage();
		}finally{
			System.out.println("Termina llamado al WS :"+mensaje);
			if(conn!=null)
				conn.close();
			conn=null;
		}
	
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
   
    
    <title>My JSP 'reAplicaWS.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  	<link rel="stylesheet" type="text/css" href="../css/contable.css"></link>
  	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<link rel="stylesheet" type="text/css" href="../css/interfaz.css">
	<script type="text/javascript" src="../js/datepickercontrol.js"></script>
	<script type="text/javascript" src="../js/jquery-1.2.6.js"></script>
	<script type="text/javascript" src="../js/jsquery.js"></script>
	<script type="text/javascript" src="../js/jsquery-fetch.js"></script>
	<script type="text/javascript" src="../js/masks.js"></script>
	<script type="text/javascript" src="../js/utils/syctools.js"></script>
  	 <script type="text/javascript">
  
     function reAplicaDocWS(){
	      		document.reAplicaWS.submit();
   	 }
  
  </script>
  <body>
   <form id="reAplicaWS" name="reAplicaWS" method="post" action="reAplicaWS.jsp?reAplicaWS=Si">
			<table id="reAplica_Docto" width="50%">
				<tr>
					<td colspan="4" align="center" >Re-Aplicación Web Service </td>
				</tr>
				<tr>
					<td colspan="4" align="center" ><%=mensaje%></td>
				</tr>
				<tr>
					<td align="center" valign="top" colspan="4">
						<input type="button" id="ReAplicaWS" value="ReAplicaWS" onclick="javascript:reAplicaDocWS();"></input>
					</td>
				</tr>
			</table>
		</form>
  </body>
</html>
