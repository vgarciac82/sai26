<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	int id_oper=c.getCasoOperacion(0).getIdOperacion();

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>My JSP 'tramite1.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript">
	 	function get(name) {
			return document.getElementById(name).value;
		}
		function OperacionSiguiente(id_oper){
			var opSig = false;
			
			if(id_oper==1){
				opSig='VALIDA';
			}
			if(id_oper==2){
				opSig='AUTORIZA';
			}			
			if(id_oper==3){
				opSig='PROCESANDO';
			}
			if(id_oper==4){
				opSig='CONSULTA';
			}
			if(id_oper==5){
				opSig='TERMINAR';
			}					
			return opSig;
		}
		function ResponsableSiguiente(id_oper){
			var resSig = false;
			
			if(id_oper==1){
				resSig='SUPERVISOR_TRAMITE1';
			}			
			if(id_oper==2){
				resSig='GERENCIA_TRAMITE1';
			}
			if(id_oper==3){
				resSig='PROCESANDO_TRAMITE1';
			}
			if(id_oper==4){
				resSig='CONSULTA_TRAMITE1';
			}			
			if(id_oper==5){
				opSig='TERMINAR';
			}		
			return resSig;
		}
		function onSubmit(id_oper){
			var p = window.parent;//siempre
			var resultado = false;
			
			p.gestion.setFolio(get("FOLIO"));
			p.gestion.setOperador(get("OPERADOR"));
			p.gestion.setFechaDocumento(get("FECHA_DOCUMENTO")); //en la variable de caso dice FECHA_DOCUMENTO se quita el underscore y se capitalizan las primeras letras
			p.gestion.setEjercicioFiscal(get("EJERCICIO_FISCAL"));//EJRECICIO_FISCAL lo mismo que arriba
			p.gestion.setMoneda(get("MONEDA"));
			
			//validaciones de la jsp
			//TODO: van todas las validaciones de la jsp, si todo cumple regresamos true
			resultado=true;	
			if(resultado){
				parent.document.getElementById("pb_send").disabled=false;
			}		
			
			return resultado;
		}
		function onLoadPlantilla(id_oper){
			alert('todo: onload plantilla');
		}
		function onPostSubmit(id_oper){
			alert('todo: postsubmit');
			return true;		
		}
		function onPostDisplay(id_oper){
			alert('todo: postdisplay');
		}
	</script>
  </head>
  
  <body>
	<table>
	<tr><td>FOLIO:</td><td><input type="text" id="FOLIO" name="FOLIO" value="<%=c.getFolio() %>"></td></tr>
	<tr><td>EF:</td><td><input type="text" id="EJERCICIO_FISCAL" name="EJERCICIO_FISCAL"></td></tr>
	<tr><td>FECHA:</td><td><input type="text" id="FECHA_DOCUMENTO" name="FECHA_DOCUMENTO"></td></tr>
	<tr><td>OPERADOR:</td><td><input type="text" id="OPERADOR" name="OPERADOR" VALUE="<%=usuario.getNombre() %>"></td></tr>
	<tr><td>MONEDA:</td><td><input type="text" id="MONEDA" name="MONEDA" VALUE=""></td></tr>
	</table>

  </body>
</html>
