<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();

 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
		var oTableConsulta="";
		$(document).ready(function() {
			$("#tbs").val(1);
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"IntegraRequis","ConsultaIntegracion");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			} 
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			
			
			//selección de valores por default en los dropdownlist
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
			querySelectPost("TipoIntegraRequisRead", "nTipoIntegracion", {async : false});
			initTabla();
			
			$('#tblConsulta tr').live('dblclick', function() { 
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableConsulta );
				if (anSelected != "") {
					var aData = oTableConsulta.fnGetData(anSelected[0]);
					window.location = aData[0];
				}
			});
		});
		
	</script>

  </head>
  
  <body>
    <form id="formConsulta">
    	<fieldset>
    		<legend>Consulta Integraci&oacute;n de Requisiciones</legend>
    		<table align="left">
   				<tr align='left'>
   					<td >
		    			Unidad Ejecutora: 
		    		</td>
		    		<td>
		    			<select name='cIdUnidadEjecutora' id='cIdUnidadEjecutora' style='width: 450px' onchange="cambiaCentrocontableUsuario();"></select>
		    		</td>
   				</tr>
   				<tr>
   					<td align='left'>
		    			Tipo:
		    		</td>
		    		<td align='left'>
		    			<select name='nTipoIntegracion' id='nTipoIntegracion' style='width: 450px'></select>
		    		</td>
   				</tr>
   				<tr>
   					<td align='left'>
		    			Descripci&oacute;n:
		    		</td>
		    		<td align='left'>
		    			<input type="text" id="cDescripcion" name="cDescripcion" value="" style='width: 450px'/>
		    		</td>
   				</tr>
   				<tr>
					<td colspan="2" align="center">
					<input type="button" id="btnBuscar" name="btnBuscar" value="Buscar" onclick="buscarIntegraRequis()" class="btnInterfaceBG ui-button ui-corner-all"/>
					</td>
				</tr>
   			</table>
   			<table id="tblConsulta" class="display" >
				<thead >
					<tr>
						<th align="center" style="display:none;">cVinculo</th>
						<th align="center" style="display:none;">IdIntegraRequi</th>
						<th align="center" style="display:none;">nEstatus</th>
						<th align="center" style="display:none;">nEnviadoSICOP</th>
						<th align="center" style="display:none;">nIdTipoIntegracion</th>
						<th align="center">Tipo</th>
						<th align="center">UE</th>
						<th align="center">No.</th>
						<th align="center">Descripci&oacute;n</th>
						<th align="center">Estatus</th>
						<th align="center">Estatus <br>SICOP</th>
						<th align="center">Folio <br>SICOP</th>													
					</tr>										
				</thead>
			</table>
    	</fieldset>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    </form>
  </body>
</html>
