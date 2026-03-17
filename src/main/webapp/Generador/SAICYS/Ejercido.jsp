<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab1.getLogin();
	String role="";
	Map rol =usuarioTab1.getRoles();
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Consulta Ejercido</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" charset="utf-8"><!--
	
		
			$(document).ready(function() {
			querySelectPost("UnidadBusca2", "cIdUnidadEjecutora", {async: false });
			querySelectPost("mCatalogoSubPartidaCMBReadEjercido", "cIdSubPartida", {async: false});
			
			$( "#dialog-form" ).dialog({
				autoOpen: false,
				height: 210,
				width: 330,
				modal: true,
				close: function() {
		
				}
			});
			
			
				
				});	

		
	
		
			function ejecutaReporte() { 
			
			divAplica.innerHTML = "Procesando, por favor espere.";
			$( "#dialog-form" ).dialog( "open" );
			setTimeout('fnGeneraReporte()', 2000);
			}
				
			function 	fnGeneraReporte(){
			

				var qw = "  where 1=1 ";
				
					if($("#cIdUnidadEjecutora").val()!='0'){
						//qw += " and cUnidadEjecutora='" +$.trim( $("#cIdUnidadEjecutora").val())+"'";
						qw += " and cUnidadEjecutora_P='" +$.trim( $("#cIdUnidadEjecutora").val())+"'";
					}
					  if($("#cIdSubPartida").val()!='0'){
					     qw+=" and  cPartida_P LIKE '%25"+$.trim($("#cIdSubPartida").val())+"%25'";
					    }
					    				    
					     if($("#rfc").val()!=""){
					     qw+=" and  cIdRFC_P LIKE '%25"+$.trim($("#rfc").val())+"%25'";
					    }
					    				     
					     if($("#folioTd").val()!=""){
					     qw+=" and  folioTipoDocto LIKE '%25"+$.trim($("#folioTd").val())+"%25'";
					    }
					    				    
					     if($("#tipoPago").val()!='0'){
					     qw+=" and  foliotipo LIKE '%25"+$.trim($("#tipoPago").val())+"%25'";
					    }
					    
					     if($("#cIdDocumento").val()!=""){
					     qw+=" and  cIdDocumento LIKE '%25"+$.trim($("#cIdDocumento").val())+"%25'";
					    }
					
						divAplica.innerHTML ='<iframe id="ifAplica" src="../../servlet/CatalogosCSV?rn=rpt_mEjercido&filter='+qw+'"></iframe>';
					
									
				}
				
		
	        
	</script>
</head>

  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
  	  		<fieldset >
  			<legend>Consulta Ejercido</legend>
		  		<div id="container" class="container">	
				    <table align="left" width="90%">
				    	<tr>
				    		<td>
				    			<table align="left" cellpadding="2" width="750px">
									<tr>	
										<td>Unidad Ejecutora</td>
										<td>
											<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"style="width: 50em;">
												<option value="<%=usuarioTab1.getU_UR()%>" selected="selected">
											</select>	
										</td>
									</tr>	
									<tr>
						    		<td align="left">Partida</td>
						    		<td align="left"><select name="cIdSubPartida" id="cIdSubPartida" style="width: 550px"></select></td>
						    	    </tr>
											
									<tr >
										<td>RFC</td>
										<td>
											<input type="text" name="rfc" id="rfc" style="width: 50em;" />	
										</td>
									</tr>
									
									<tr >
										<td>Folio Tipo Documento</td>
										<td>
											<input type="text" name="folioTd" id="folioTd" style="width: 50em;" />	
										</td>
									</tr>
									
									<tr>	
										<td>Tipo de Pago</td>
										<td>
											<select id="tipoPago" name="tipoPago"style="width: 50em;">
											<option value="0">*</option>
											<option value="PDIR">Pago Directo</option>
											<option value="RELG">Relacion de Gastos</option>
											<option value="NOMI">Pago Nomina</option>
											<option value="POBR">Pago Obra</option>
											<option value="PDIV">Pago Diverso</option>
											</select>	
										
										</td>
									</tr>	
									
									<tr >
										<td>Documento</td>
										<td>
											<input type="text" name="cIdDocumento" id="cIdDocumento" style="width: 50em;" />	
										</td>
									</tr>
									
									<tr>
							    		<td colspan="2" align="center"><input type="button"  name="btnBuscar" id="btnBuscar" value="Buscar"  onclick="ejecutaReporte();"/></td>
							    	</tr>
							    </table> 
				    		</td>
				    	</tr>
						
				    </table>
			    </div>
  		</fieldset>
  		
  		<div id="dialog-form" title="Procesando el Reporte del Ejercido">	
		<div id="divAplica" >				
			<iframe id="ifAplica" src="about:blank"></iframe>
		</div>
	</div>
  		
  		
  		
	</form>
  </body>
  
</html>
