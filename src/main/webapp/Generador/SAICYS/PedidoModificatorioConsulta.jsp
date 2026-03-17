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
    <title>Consulta Pedido</title>
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
		var oTable;
		$(document).ready(function() {
			<%
				int tabla = 0;
				int consulta = 0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map pestanas=ebl.getPestana(role,"PedidoModificatorio");
				Iterator it = pestanas.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry e = (Map.Entry)it.next();%>
					$( "#<%=e.getValue()%>" ).attr("disabled", true);
					<%
					String pestana = (String)e.getValue();
					if ("caratulaPedidoMod".equals(pestana)){
						 tabla=1;
					}
					if ("consultaPedidoMod".equals(pestana)){
						 consulta=1;
					}
				}
				Map botones = nb.getBotones(role, "PedidoModificatorio" , "consultaPedidoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%			
				}

				%>			
			
			//Agrega el click handler para el doble click de cada reglon de la tabla
			$('#tblConsultaPedidosMod tr').live('dblclick', function() { 
				var tabla = '<%=tabla%>';
				if (tabla == 0){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );				
					var aData = oTable.fnGetData(anSelected[0]);
					
					//Construir la ruta para la caratula del pedido modificado
					var pEjercicio = aData[0];
					var pMod = aData[1];
					var pId = aData[2];
					var pDefinitivo = aData[3];
					var pUrl = 'PedidoModificatorio.jsp?tab=2&mod=' + pMod + '&pDefinitivo=' + pDefinitivo + '&tipoArchivo=pedidoMod' + '&pId=' + pId + '&pEjercicio=' + pEjercicio;
					window.location = pUrl;
				}
			});
			
			//Carga los valores para los comboBox
			querySelectPost("UnidadPedidosRead", "cIdUnidadEjecutora", {async: false });
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			mostrar();
	
		});
		
		function mostrar() {
			var consulta=<%=consulta%>;
			if (consulta==0){
				var qw = " cEjercicio = '" + $("#cEjercicio").val() + "'" + 				
				" AND cIdPedidoDefinitivo LIKE '%25" + $("#cIdUnidadEjecutora").val() + "%25'";
				if ($("#cIdDefinitivo").val() != ''){
					qw = qw + " AND cIdPedidoDefinitivo LIKE '%25" + $("#cIdDefinitivo").val() + "%25'";
				}
				
				oTable = $("#tblConsultaPedidosMod").dataTable({
					sScrollY: "300px",
					sScrollX: "100%",
					sScrollXInner: "200%",
					bScrollCollapse: true,
					bDestroy: true,
					bAutoWidth: true,
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Pedidos",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtrado de _MAX_ registros)",
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
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mPedidoModificado&qw=" + qw,
					bProcessing: true,
					sPaginationType: "full_numbers",
					bJQueryUI: true,
					aaSorting: [[ 1, "asc" ]] ,
					aoColumns: [
						{ sName: "cEjercicio" },
						{ sName: "nConsecutivoModificacion" },
						{ sName: "cIdPedido" },
						{ sName: "cIdPedidoDefinitivo" },
						{ sName: "mTotalAnterior" },
						{ sName: "mTotalModificacion" },
						{ sName: "mTotalNuevo" }
					]
	        	});
			}
			
		}
		/* Get the row which are currently selected */
		function fnGetSelected( oTableLocal )
		{
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
	</script>
</head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
  		<fieldset >
  			<legend>Consulta Pedidos Modificados</legend>
		  		<div id="container" class="container" >	
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
									<tr >
										<td>Pedido Definitivo</td>
										<td>
										   <input type="text" name="cIdDefinitivo" id="cIdDefinitivo" style="width: 50em;" />	
										</td>
									</tr>
							    	<tr>
							    		<td colspan="2" align="center"><input type="button"  name="btnBuscarConsultaPed" id="btnBuscarConsultaPed" value="Buscar"  onclick="mostrar();"/></td>
							    	</tr>
							    </table>
				    		</td>
				    	</tr>
				    	<tr  >
				    		<td style="width: 740px; height: 300px" >
							     <table align="left" id="tblConsultaPedidosMod" width="740px" class="display">
						        	<thead>
						        		<tr>
						        			<th>EJERCICIO</th>
						        			<th>MODIFICACION</th>
						        			<th>PEDIDO</th>
						        			<th>PEDIDO DEFINITIVO</th>
						        			<th>MONTO ANTERIOR</th> 
						        			<th>MONTO MODIFICACION</th>
						        			<th>MONTO NUEVO</th>
						        		</tr>
						        	</thead> 
						        </table> 
				    		</td>
				    	</tr>
				    	<tr>
				    		<td>
							    <table align="left" width="80%">
						        	<tr>
						        		<td><input type="hidden" name="cEjercicio" id="cEjercicio"/></td>
						        	</tr>
						        	<tr>
						        		<td><input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/></td>
						        	</tr>
						        </table>
				    		</td>
				    	</tr>
				    </table>
			    </div>
  		</fieldset>
	</form>
  </body>
</html>
