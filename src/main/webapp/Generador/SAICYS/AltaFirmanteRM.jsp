<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="org.slf4j.Logger"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%!Logger log = LoggerFactory.getLogger( "AltaFirmanteRM.jsp" ); %>
<%

	if( session == null ){
		log.debug("Sin usuario en session. Se redirige a inicio. ");
		response.sendRedirect( "../../index.jsp" );
		return;
	}

	Usuario usuario = (Usuario)session.getAttribute( GestionInterface.ATT_USER );

	if( usuario == null ){
		log.debug("Sin usuario en session. Se redirige a inicio. ");
		response.sendRedirect( "../../index.jsp" );
		return;
	}

	log.info("Usuario: ".concat(usuario.getNombre()  ).concat( " inicia proceso de Firmantes de RM" ) );
	
	boolean esAdmin = usuario.getRole( "ADMIN_RECMAT" ) != null;
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>Registro de firmantes de Recepcion de Material.</title>
	<!-- Estilos estandar para los controles JQuery -->
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
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
  	<script type="text/javascript" src="../../Generador/js/AltaFirmanteRM.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
  	<script type="text/javascript" src="../../js/jquery.blockUI-2.4.2.js"></script>
  	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../../Generador/js/jquery.formatCurrency.all.js"></script>
	
	
	<script type="text/javascript">
		$(document).ready(function() {
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
		    
		    $("#cIdUnidadEjecutora").change(function(){createDTFirmantes();})
		    
		    querySelectPost("tCatalogoUnidadEjecutoraReadFRM", "cIdUnidadEjecutora", {async: false,
		    	callback:function(){
		    		$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
		    		createDTFirmantes();
		    	}
		    });
		    
		    $("#isAdmin").val( "<%=esAdmin?0:1%>");
		    
		    $("#btnCrear").button().click(function(){
		    	agregaFirmante();
		    });
		    $(window).bind('resize', function (){
				resizeDt();
			});
		});
		
	</script>

</head>

<body id="dt_example">
	<form action="" method="post" id="">
		<div id="container" class="container" >
		
			<h1>Registro de Firmantes de Recepcion de Material.</h1>
			
			<div>
				<fieldset>
					<legend>Captura de Firmante.</legend>
					<table align="left" >
			   			<tr>
				    		<td align="right" nowrap="nowrap">
				    			Unidad Ejecutora: 
				    		</td>
				    		<td align="left">
				    			<select name='cIdUnidadEjecutora' id='cIdUnidadEjecutora' style='width: 600px' ></select>
				    		</td>
				    	</tr>
				    	<tr>
							<td align="right">
								No. Empleado: 
							</td>
							<td align="left">
								<input type="text" class="AyudaSyC obligatorio desahabilitado"  name="NumEmpleado" id="NumEmpleado" readonly onkeydown="return(desactivaBackspace(event))" style='width: 570px' />
							</td>
						</tr>
						<tr>
							<td align="right">
								Nombre:
							</td>
							<td align="left">
								<input type="text" id="nombreEmpleado" name="nombreEmpleado" value=""  class="desahabilitado" readonly style='width: 600px'/>
							</td>
						</tr>
						<tr>
							<td align="right">
								Apellido Paterno: 
							</td>
							<td align="left">
								<input type="text" id="apellidoPatEmpleado" name="apellidoPatEmpleado" value=""  class="desahabilitado" readonly style='width: 600px'/>
							</td>
						</tr>
						<tr>
							<td align="right">
								Apellido Materno: 
							</td>
							<td align="left">
								<input type="text" id="apellidoMatEmpleado" name="apellidoMatEmpleado" value=""  class="desahabilitado" readonly style='width: 600px'/>
							</td>
						</tr>
						<tr>
							<td align="right">
								Puesto:
							</td>
							<td align="left">
								<input type="text" id="puestoEmpleado" name="puestoEmpleado" value=""  class="desahabilitado" readonly style="width: 600px"/>
							</td>
						</tr>
						
						<tr>
							<td align="center" colspan="2">
								<input type="button" id="btnCrear" value="Agregar Firmante" class="btnInterfaceBG ui-button ui-corner-all"/>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
			
			<div>
				<fieldset>
					<legend>Firmantes Capturados</legend>
					<table id="tblFirmantes" class="display"  >
						<thead >
							<tr>
								<th align="center">Unidad/Gerencia</th>
								<th align="center">Nombre</th>
								<th align="center">Apellido <br />Paterno  </th>
								<th align="center">Apellido <br />Materno</th>
								<th align="center">Puesto</th>
								<th align="center">Estatus</th>
							</tr>										
						</thead>
					</table>
				</fieldset>
			</div>
		</div>
		
		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
		<input type="hidden" name="nIdModulo" id="nIdModulo" value="1" />
		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuario.getLogin()%>" />
		<input type="hidden" name="existe" id="existe" value="<%=usuario.getLogin()%>" />
	</form>
</body>

</html>