
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();
	String roles="";
	Map rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String cEjercicio = "";
	String cIdTipoPedido= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_PedidoEjercicio) != null) {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoEjercicio);
		cIdTipoPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoTipoPedido);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PedidoUnidadEjec);
		nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_PedidoConsecutivo);		
	}else 
		response.sendRedirect("Pedidos.jsp?tab=0");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Pedido de propuesta conjunta</title>
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
	<script type="text/javascript" charset="utf-8">	
		$(document).ready(function() {
			$("#usuarioLogin").val('<%=usuario.getLogin()%>');
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("obtieneRFCPedido",{async:false});
			mostrarTablaProveedores();
			mostrarTablaProveedoresPedido();
			
			/////////// busca proveedores///////
			$( "#btnBuscarProveedorProcedimiento" ).button().click(function() {
					mostrarTablaProveedores();				
			});
			
			/////EVENTO CLICK EN EL RENGLON DE LA TABLA DE PROVEEDORES DISPONIBLES
		$('#tblProveedoresDisponibles tr').live('dblclick', function() {
		    	if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else  
				{         
					$(this).addClass('row_selected');
					var aTrs = $('#tblProveedoresDisponibles').dataTable().fnGetNodes();
					for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							var nTr = $('#tblProveedoresDisponibles').dataTable().fnGetData(aTrs[i]);   
							//Obtengo el campo de la tabla
							$("#IdRFCProveedor").val(nTr[0]);
							$("#razonSocial").val(nTr[1]);
					
							oTableMP.dataTable().fnDeleteRow( i );
							alert("Registro insertado");
							queryFormPost("agregaProveedoresPropuesta",{async:false});
							$("#btnBuscarProveedorProcedimiento").click();
							 mostrarTablaProveedoresPedido();
						}
					}
				}
				
	    	});
	    	
	    	//elimina proveedores
	    	
	    	$('#tblPedidoProveedores tr').live('dblclick', function() {
		    	if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else  
				{         
					$(this).addClass('row_selected');
					var aTrs = $('#tblProveedoresDisponibles').dataTable().fnGetNodes();
					for ( var i=aTrs.length ; i>=0; i-- )     
					{  
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							var nTr = $('#tblProveedoresDisponibles').dataTable().fnGetData(aTrs[i]);   
							//Obtengo el campo de la tabla
							$("#IdRFCProveedor").val(nTr[0]);
							$("#razonSocial").val(nTr[1]);
					
							oTableMP.dataTable().fnDeleteRow( i );
							alert("Registro insertado");
							queryFormPost("agregaProveedoresPropuesta",{async:false});
							$("#btnBuscarProveedorProcedimiento").click();
							 mostrarTablaProveedoresPedido();
						}
					}
				}
				
	    	});
		});
		
		function mostrarTablaProveedores(){
			var consulta="qw= 1=1";
			if (($.trim($("#rfcProveedor").val()))!="")
			{
				consulta += " AND cIdRFC LIKE '%25" + $.trim($("#rfcProveedor").val())+"%25'";
				consulta=consulta.replace("\&","%26");
 				
			}
			if(($.trim($("#rSocialProveedor").val()))!="" )
			{
				consulta += " AND cRazonSocial LIKE '%25" + $.trim($("#rSocialProveedor").val())+"%25'";
				consulta=consulta.replace("\&","%26");
			}
			
			var parametro="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoPedido").val()+"','"+$("#cIdUnidadEjecutora").val()+"',"+$("#nIdConsecutivo").val();
			consulta+= " AND  CIDRFC NOT IN (select cIdRFC from fn_mProveedorPedido("+parametro+"))";
			
			oTableMP=$('#tblProveedoresDisponibles').dataTable({
			"bProcessing": true,
			"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
			"bFilter" : false,
			"bDestroy" : true,
			"bJQueryUI": true,
			"bAutoWidth" : false,
			"iDisplayLength": 5,
			"sPaginationType": "full_numbers",
			//"sScrollY": 250,
					"oLanguage": {
					sProcessing: "Procesando...",
					//sLengthMenu: "Mostrar _MENU_ registros",
					//sLengthMenu: "<h2><b>PROVEEDORES DISPONIBLES</b></h2><h5>Click para agregar el proveedor</h5>",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				"bServerSide": true,
				"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_GetMostrarProveedoresProcedimiento()&"+consulta,
				"aaSorting": [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial"   }
				]
			});
			
		}
		function mostrarTablaProveedoresPedido(){
				
			
			
			var consulta="'"+$("#cEjercicio").val()+"','"+$("#cIdTipoPedido").val()+"','"+$("#cIdUnidadEjecutora").val()+"',"+$("#nIdConsecutivo").val();
			
			oTablePP=$('#tblPedidoProveedores').dataTable({
			"bProcessing": true,
			"bLengthChange" : true,//Habilitar el que dice mostrar 10, 25 etc
			"bFilter" : false,
			"bDestroy" : true,
			"bJQueryUI": true,
			"bAutoWidth" : false,
			"iDisplayLength": 5,
			"sPaginationType": "full_numbers",
			//"sScrollY": 250,
					"oLanguage": {
					sProcessing: "Procesando...",
					//sLengthMenu: "Mostrar _MENU_ registros",
				//	sLengthMenu: "<h2><b>PROVEEDORES DISPONIBLES</b></h2><h5>Click para agregar el proveedor</h5>",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				"bServerSide": true,
				"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mProveedorPedido("+consulta+")&",
				"aaSorting": [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdRFC" },
					{ sName: "cRazonSocial"},
					{ sName: "boton"}
				]
			});
			}
			
	function validaModificarPedido(){
		if(($('#cIdUsuarioCreacion').val()== $("#usuarioLogin").val())||( $('#usuarioRolePedido').val().toString().indexOf('ADMIN_RECMAT') >= 0)){
			return true;
		}
		else{
			alert("El usuario no tiene permiso para realizar esta acci\xF3n.");
			return false;
		}
	}
		
		
	function  eliminaProveedorRFC(indice){
		$("#rfcElimina").val(indice);
		queryFormPost("eliminaRFCPropuesta",{async:false});
		mostrarTablaProveedoresPedido();
	}	
	</script>
  </head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form > 
		<div id="container" class="container">
		<table width="94%" align="left">
				<tr>
					<td>
						<fieldset>
							<legend>Pedido de propuesta conjunta</legend>
								<table align="left" cellpadding="2" width="100%" >
							    	<tr>
							    		<td align="right" colspan="2">
							    			<img id="imgBorrar" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="borrar();"/>&nbsp;Anular
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="window.location = 'Pedidos.jsp?tab=0';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProcedimiento" id="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblDefinitivo" id="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblPedido" id="lblPedido" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 500px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td width="33%" align="right">&nbsp;</td>
							    	</tr>
							   		<tr>
							   			<td align="left" style="width:10px">RFC:</td>
										<td><input type="text" id="rfcProveedor" name="rfcProveedor"style="width: 30em;" /></td>
									</tr>
									<tr><td align="left">Raz&oacute;n Social:</td>
										<td><input type="text" id="rSocialProveedor" name="rSocialProveedor"style="width: 30em;" /></td>
									</tr>
									<tr>
										<td width="33%" align="center" colspan="2"><button id="btnBuscarProveedorProcedimiento">BUSCAR</button></td>
									</tr>
									<tr>
										<td colspan="2" >
											<table id="tblProveedoresDisponibles" class="display">
										       <thead>
										          <tr>
										             <th>RFC</th>
										             <th>Raz&oacute;n Social</th>
										          </tr>
										        </thead>
										     </table>
										</td>						
								    </tr>
							</table>
						</fieldset>
						<fieldset>
						<legend>Proveedores del pedido</legend>
							<table id="tblPedidoProveedores" class="display">
						       <thead>
						          <tr>
						             <th>RFC</th>
						             <th>Raz&oacute;n Social</th>
						             <th>Elimina</th>
						          </tr>
						        </thead>
						     </table>
						</fieldset>
					</td>
				</tr>
			</table>
		</div>
		 <!-- Sesion  -->
	    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
	    <input type="hidden" name="cIdTipoDocumento" id="cIdTipoDocumento" value="<%=cIdTipoPedido%>" />
	    <input type="hidden" name="cIdPedido" id="cIdPedido" value="<%=cIdTipoPedido%>-<%=cIdUnidadEjecutora%>-<%=nIdConsecutivo%>" />
	     <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
	    <input type="hidden" name="rfcPedido" id="rfcPedido" />		
	    <input type="hidden" name="IdRFCProveedor" id="IdRFCProveedor" />
	    <input type="hidden" name="razonSocial" id="razonSocial" />	    
	    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
	    <input type="hidden" name="rfcElimina" id="rfcElimina" />	
	    
	</form>
	
  </body>
</html>