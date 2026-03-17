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
	String unidadUsuarioLogeado="";
	unidadUsuarioLogeado = usuario.getU_UR()+" - "+usuario.getDescripcion(  );
	
	String cEjercicio=request.getParameter("cEjercicio");
	String cUnidadEjecutora =request.getParameter("cIdUnidadEjecutora");
	int nIdTipoIntegracion=(null==request.getParameter("nTipoIntegracion") || "".equalsIgnoreCase( request.getParameter("nTipoIntegracion") ) ?0:Integer.parseInt(request.getParameter("nTipoIntegracion")));
	int nConsecutivo=(null==request.getParameter("nIdConsecutivo") || "".equalsIgnoreCase( request.getParameter("nIdConsecutivo") ) ?0:Integer.parseInt(request.getParameter("nIdConsecutivo")));
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
        
    <title>My JSP 'IntegraRequiPreseleccion.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script type="text/javascript" charset="utf-8">
		var oTablePresel="",oTableSel="";
		$(document).ready(function() {
			$("#tbs").val(2);
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
				Map botones=nb.getBotones(role,"IntegraRequis","PreseleccionaRequis");
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
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cUnidadEjecutoraRMC", {async: false});
			$("#cEjercicio").val("<%=cEjercicio%>");
			$("#cUnidadEjecutora").val("<%=cUnidadEjecutora%>");
			$("#nIdTipoIntegracion").val("<%=nIdTipoIntegracion%>");
			$("#nConsecutivo").val("<%=nConsecutivo%>");
			queryFormPost("mIntegraRequisRead", {async : false});
			querySelectPost("mCatalogoCapituloReadConsolidado", "cCapitulo", {async : false});
			$("#cCapitulo").val("2");
			showAndHiddenButtons();
			initTablaRequis();
			showTblRequisIntegradas();
			
			//Agrega requi de una en una
			$('#tblSolicitudDispPreSel tr').live('dblclick', function() { 
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTablePresel );
				if (anSelected != "") {
					var aData = oTablePresel.fnGetData(anSelected[0]);
					$("#cIdSolicitud").val(aData[0]);
					if(parseInt($("#nEstatus").val(),10)>1){
						swal("Para agregar el estatus debe de ser \"captura\"",{icon:"info",button: "Cerrar"});
						return;
					}
					queryFormPost("mAgregaRequiIntegracion", {async : false,
						callback : function() 
						{
							$("#cIdSolicitud").val("");
							$('#tblSolicitudPreSel').dataTable().fnClearTable();
							$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
							swal("Requisición agregada correctamente.",{icon:"info",button: "Cerrar"});
							showTblRequis();
							showTblRequisIntegradas();
						},
						error : function(){
							$("#cIdSolicitud").val("");
							$('#tblSolicitudPreSel').dataTable().fnClearTable();
							$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
							showTblRequis();
							showTblRequisIntegradas();
						}
					});
				}
				anSelected="";
				$(this).removeClass('row_selected');
			});
			//Elimina requi de una en una
			$('#tblSolicitudPreSel tr').live('dblclick', function() { 
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableSel );
				if (anSelected != "") {
					var aData = oTableSel.fnGetData(anSelected[0]);
					$("#cIdSolicitud").val(aData[0]);
					if(parseInt($("#nEstatus").val(),10)>1){
						swal("Para eliminar el estatus debe de ser \"captura\"",{icon:"info",button: "Cerrar"});
						return;
					}
					queryFormPost("mDeleteRequiIntegrada", {async : false,
						callback : function() 
						{
							$("#cIdSolicitud").val("");
							$('#tblSolicitudPreSel').dataTable().fnClearTable();
							$('#tblSolicitudDispPreSel').dataTable().fnClearTable();
							swal("Requisición eliminada correctamente.",{icon:"info",button: "Cerrar"});
							showTblRequis();
							showTblRequisIntegradas();
						}
					});
				}
			});
		});//Fin del document ready
		
	</script>

  </head>
  
  <body id="dt_example">
	<form>
		<fieldset>
			<legend>Informaci&oacute;n</legend>
			<table align="left" cellpadding="2" width="100%">    
		    	<tr id="trBotones">						
					<td align="right" colspan="2">
                        <input type="button" class="btnInterfaceAutoriza ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAprobarPreseleccion" 	name="imgAprobarPreseleccion" title="Aprobar"	value="Aprobar"	onclick="aprobarIntegracion();"/>&nbsp;&nbsp;
                        <input type="button" class="btnInterfaceReturn ui-button ui-widget ui-state-default ui-corner-all" 		id="imgDevolverPreseleccion" 	name="imgDevolverPreseleccion" 	value="Devolver"	 onclick="devolverIntegracion();"/>&nbsp;&nbsp;
                        <input type="button" class="btnInterfaceCancelar ui-button ui-widget ui-state-default ui-corner-all" 	id="imgAnular" 	name="imgAnular" 	value="Anular"	onclick="anularIntegracion();"/>&nbsp;&nbsp;
                	</td>
				</tr>
		    	<tr>
			    	<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0; background-color:transparent" id="lblIntegraRequis" name="lblIntegraRequis" readonly />
		    	</tr>
		    	<tr>
		    		<td align="left" colspan="2">
		    			<input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblUnidadEjecutora" id="lblUnidadEjecutora" readonly />
		    		</td>
		    	</tr>
		    	<tr>
		    		<td align="left" colspan="2">
		    			<input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly />
		    		</td>
		    	</tr>
			</table>
		</fieldset>
		<fieldset id="requisDisp">
			<legend>Requisiciones</legend>
			<table id="tblcombosPreseleccion" style="width: 100%">							
					<tr>
						<td>C&aacute;pitulo:</td>										
						<td colspan="2" align="left">
							<select id="cCapitulo" name="cCapitulo"	style="width: 40em;"></select>
						</td>
					</tr>
					<tr>
						<td >Unidad Ejecutora:</td>										
						<td colspan="2" align="left">
							<select id="cUnidadEjecutoraRMC" name="cUnidadEjecutoraRMC" style="width: 40em;">
							<option value="<%=usuario.getU_UR()%>" selected="selected"></select>
						</td>
					</tr>
					<tr>																			
						<td colspan="3" align="left">
							<input type="button" id="btnSolicitudesDisppreselBuscar" name="btnSolicitudesDisppreselBuscar" value="Buscar" onclick="showTblRequis()" class="btnInterfaceBG ui-button ui-corner-all"/>
						</td>
					</tr>
			</table>
			<table id="tblSolicitudDispPreSel" class="display" style="width: 100%">
	            <thead>
	                <tr>
	                	<th>Requisici&oacute;n</th>
	                    <th>Partida</th>
	                    <th>Descripci&oacute;n</th>
	                    <th style="display: none;">nFolioApartado</th>
	                </tr>
	            </thead>
	        </table>
			<table border="0" align="left" style="width: 100%">
				<tr>														
					<td colspan="2" align="left">
						<input type="button" id="btnPreselTodas" name="btnPreselTodas" value="Agregar Todo" onclick="agregarTodo()" class="btnInterfaceBG ui-button ui-corner-all"/>
					</td>						
				</tr>
			</table>
		</fieldset>
		<div>
		<fieldset>
			<legend>Requisiciones Pre-seleccionadas <input name="numSol" id="numSol" type="text" size ="4" style="border: 0px solid black;"></legend>
			<table id="tblSolicitudPreSel" class="display" style="width: 100%">
	            <thead>
	                <tr>
	                	<th>Requisici&oacute;n</th>
	                    <th>Partida</th>
	                    <th>Descripci&oacute;n</th>
	                    <th style="display: none;">nFolioApartado</th>
	                </tr>
	            </thead>
	        </table>
			<table border="0" align="left" style="width: 100%">
				<tr VALIGN=TOP>												
					<td colspan="3" align="left">
						<input type="button" id="btnDesagregarTodas" name="btnDesagregarTodas" value="Desagregar Todo" onclick="eliminarTodo()" class="btnInterfaceBG ui-button ui-corner-all"/>
					</td>						
				</tr>
			</table>
		</fieldset>
		</div>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="-1" />
		<input type="hidden" name="cUnidadEjecutora" id="cUnidadEjecutora" value="-1" />
   		<input type="hidden" name="nIdTipoIntegracion" id="nIdTipoIntegracion" value="-1" />
   		<input type="hidden" name="nConsecutivo" id="nConsecutivo" value="1" />
   		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    	<input type="hidden" name="nIdIntegraRequi" id="nIdIntegraRequi" value="-1" />
    	<input type="hidden" name="cIdSolicitud" id="cIdSolicitud" value="-1" />
    	<input type="hidden" name="nEstatus" id="nEstatus" value="-1" />
    	<input type="hidden" name="cIdTipoSolicitud" id="cIdTipoSolicitud" value="RT" />
    	<input type="hidden" name="existeRequiEnConsolidado" id="existeRequiEnConsolidado" value="-1" />
    	<input type="hidden" name="cIdConsolidado" id="cIdConsolidado" value="" />
    	
	</form>
  </body>
</html>
