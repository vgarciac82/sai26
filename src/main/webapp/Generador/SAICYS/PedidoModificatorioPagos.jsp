<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();	
	Map rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}	
	String cIdPedido = "";
	String cIdPedidoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoPedido= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";		
	if (session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo) != null) {
		cIdPedidoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioConsecutivo);
		cIdPedido = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PedidoModificatorioEjercicio);
		
		cIdTipoPedido = cIdPedido.split("-")[0];
		cIdUnidadEjecutora = cIdPedido.split("-")[1];
		nIdConsecutivo = cIdPedido.split("-")[2];		
	}else 
		response.sendRedirect("PedidosModificatorio.jsp?tab=0");	
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>    
    <title>Pagos del Pedido</title>    
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
		var oTablePagos;	
		$(document).ready(function() {	
			<%
			    String roles="";
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int cmdPdfContratoAnexo1=0;
				int cmdPdfContratoAnexo2=0;
			
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"PedidosModificado","pagosPedidoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%   
				}
				%>		
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly = true;
			document.getElementById("lblProcedimiento").style.readonly = true;
			document.getElementById("lblDefinitivo").style.readonly = true;
			document.getElementById("lblPedido").style.readonly = true;
			document.getElementById("lblProveedor").style.readonly = true;
			document.getElementById("lblEstadoMod").style.readonly = true;
			document.getElementById("lblTotalAnterior").style.readonly = true;
			document.getElementById("lblTotalModificado").style.readonly = true;
			document.getElementById("lblTotal").style.readonly = true;
						
			$('#tblPagos tr').live('dblclick', function() {        
				$(oTablePagos.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
			});
			
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("mPedidoModificadoTotales", {async: false});
			mostrarPagos();
			if ($("#tipoMod").val() != 1){
				document.getElementById("reduccionPedidoMod").disabled = true;
			}

			if(parseInt($("#nIdEstado").val(),10) == 1){
				document.getElementById("precompromisoPedidoMod").disabled = true;
			}
			
		});
		
		/* Get the rows which are currently selected */
		function fnGetSelected( oTableLocal ) {
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
			
		function mostrarPagos () {
			var qw = " cIdDocumento = '" + $("#cPedidoDefinitivo").val() + "%23M" + $("#cConsecutivoMod").val() + "'";
			oTablePagos = $("#tblPagos").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "440%",
				//sScrollXInner: "200%",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "El Documento no tiene pagos Registrados",
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
				bServerSide: true,
				//lista pagos pero falta validar que sea el Query Correcto
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_pagosSai&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					 { sName: "folioTipoDocto" },
					{ sName: "tipoDocto" },
					{ sName: "cIdDocumento" },
					{ sName: "caNoContrarrecibo" },
					{ sName: "cIdRFC" },
					{ sName: "Nombre" },
					{ sName: "cConcepto" },
					{ sName: "[mImporteBruto(Total Pedido/Contrato)]" },
					{ sName: "mImporteIVA" },
					{ sName: "[mImporteNeto(Total Pedido/Contrato)]" },
					{ sName: "EP" },
					{ sName: "NetoPorEp" },
					{ sName: "cMes" },
					{ sName: "cPartida" },
					{ sName: "cUnidadEjecutora" },
					{ sName: "fAplicacion" },
					{ sName: "fProgramadaPago" },
					{ sName: "totalSinRetenciones" }
					
				]
		
        	});		
		}
	</script>
  </head>  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container">
			<table width="94%" align="left">
				<tr>
					<td>
						<fieldset>
							<legend>Pagos del Pedido Modificado</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer"  onclick="window.location = 'PedidoModificatorio.jsp?tab=1';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblProcedimiento" name="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" id="lblDefinitivo" name="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblPedido" id="lblPedido" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 300px" name="lblEstadoMod" id="lblEstadoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTipoMod" id="lblTipoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalAnterior" id="lblTotalAnterior" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalModificado" id="lblTotalModificado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    </table>
						</fieldset>	
						<fieldset>
							<legend>Pagos</legend>
								<table width="97%" align="left">
									<tr>
										<td width="740px">
											<table align="center" id="tblPagos" width="740px" class="display">
									        	<thead>
									        		<tr>
									        		<th>Folio Tipo Documento</th>
									        		<th>Tipo Documento</th>
									        		<th>Folio Definitivo</th>
									        		<th>Número de contrarecibo</th>
									        		<th>RFC Proveedor</th>
									        	    <th>Proveedor </th>
									        		<th>Concepto</th>
									      			<th>Importe Bruto</th>
									      			<th>% Iva</th>	
									        		<th>Importe Neto</th>
									        		<th>EP</th>  
									        		<th>Neto Por EP</th> 
									        		<th>Mes</th>
									        		<th>Partida</th>
									        		<th>Unidad Ejecutora</th>
									        		<th>Fecha Aplicacion</th>
									        		<th>Fecha Programada Pago</th>
									        		<th>Total sin Retenciones(Bruto+iva)</th>
									        		</tr>
									        	</thead>
									        </table>
										</td>
									</tr>
								</table>
						</fieldset>
					</td>
				</tr>
			</table>
			<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		    <input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="cPedido" id="cPedido" value="<%=cIdPedido%>" />
		    <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" value="<%=cIdPedidoDefinitivo%>" />
		    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />			
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />
		    
		    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="tipoMod" id="tipoMod" />	
	    </div>
	</form>
  </body>
</html>
