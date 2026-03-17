<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab.getLogin();	
	Map<String, Role> rol =usuarioTab.getRoles();
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
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>Pagos del Pedido</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
		
	<script type="text/javascript" charset="utf-8">
		var oTablePagos;	
		$(document).ready(function() {
			$("#tbs").val(6);
			showAndHideTabs();
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
				Map<String, String> botones=nb.getBotones(roles,"Pedidos","pagosPedido");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%   
				}
				%>		
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;			
			$('#tblPagos tr').live('dblclick', function() {        
				$(this).addClass('row_selected');   
				var anSelected = fnGetSelected( oTablePagos );						
				var aData = oTablePagos.fnGetData(anSelected[0]);
				$(this.nTr).removeClass('row_selected');
			});
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("contratoDefinitivoPedido", {async: false}); 
			queryFormPost("mContratoPagadoReadPedido", {async: false});
			$("#cIdDefinitivo").val($("#cPedidoDefinitivo").val())
			//obtenemos cIdProcedimiento y RFC
			queryFormPost("obtieneRFCProcedimiento", {async: false});
			//se checa si es contrato abierto
			queryFormPost("esPedidoAbierto", {async: false});
			
			if($("#esAbierto").val() == "1"){
			queryFormPost("mPedidoTotalMaximoAmpliacion", {async: false});
			queryFormPost("mPedidoPorPagarReadMaximoAmpliacion", {async: false});
			
			}else
			queryFormPost("mContratoPorPagarReadPedido", {async: false});
			
									
			mostrarPagos();
			queryFormPost("fnMontoSubtotalRead", {async: false});
			queryFormPost("fnMontoIVARead", {async: false});
			queryFormPost("fnMontoImpuesto1Read", {async: false});
			queryFormPost("fnMontoImpuesto2Read", {async: false});
			queryFormPost("fnMontoImpuesto3Read", {async: false});

			if($("#lblImporteImpuesto1").val() == ""){
				$("#divImporteImpuesto1").css("display","none");
			}
			if($("#lblImporteImpuesto2").val() == ""){
				$("#divImporteImpuesto2").css("display","none");
			}
			if($("#lblImporteImpuesto3").val() == ""){
				$("#divImporteImpuesto3").css("display","none");
			}
			queryFormPost("fnMontoTotalPedido", {async: false});

			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
			if(nIdEstado == 1)
				$("#preCompromisoPedido").css("display", "none");
			else
				$("#preCompromisoPedido").css("display", "block");
		});	
		function formateaMoneda(importe){
			var importeSeparado = importe.toString().split("\.");
			var importeParte1 = importeSeparado[0];
			var cont=0;
			var tem="";
			
			for(var i=importeParte1.length; i>0; i--){
				if(cont == 3){
					tem = ","+tem;
					cont=0;
				}
				tem = importeParte1.substring(i-1,i)+tem;
				cont++;
			}
			if(importe.toString().indexOf("\.")>0){
				for(var i=importeSeparado[1].length; i<2; i++){
					importeSeparado[1]+="0";
				}
				return tem+"."+importeSeparado[1];
			}
			else{
				return tem+".00";
			}
		}	
		/* Get the rows which are currently selected */
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
		function mostrarPagos () {
			
			/*var qw = "aEjercicioFiscal = '" + $("#cEjercicio").val() + "'"+
			" AND cNoFactura='"+$("#cPedidoDefinitivo")+"'";
			*/
		
		var qw = " cIdDocumento='"+$("#cPedidoDefinitivo").val()+"' AND tipoDocto='Pago Diverso'";
		
			oTablePagos = $("#tblPagos").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "100%",
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
		<div id="container" class="container" style="width: 95%">
			<fieldset>
				<legend>Pagos del Pedido</legend>
					<table align="left" cellpadding="2" width="100%">
				    	<tr>
				    		<td align="right" colspan="2">
								<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Pedidos.jsp?tab=0';" />
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento" id="lblProcedimiento" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 400px;border-width:0; background-color:transparent" name="lblPedido" id="lblPedido" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 500px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblSubtotal" id="lblSubtotal" readonly /></td>
				    	</tr>
				    	<tr id="divImporteIVA">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteIVA" id="lblImporteIVA" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto1">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto1" id="lblImporteImpuesto1" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto2">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto2" id="lblImporteImpuesto2" readonly /></td>
				    	</tr>
				    	<tr id="divImporteImpuesto3">
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblImporteImpuesto3" id="lblImporteImpuesto3" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotal" id="lblTotal" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblTotalMaximo" id="lblTotalMaximo" readonly /></td>
				    	</tr>	
				    	<tr>
						<td align="left" colspan="2">Total Pagado:$<input type="text" style="width:600px;border-width:0; background-color:transparent" name="lblTotalPagado" id="lblTotalPagado" readonly /></td>
					 </tr>
					 <tr>  
						<td align="left" colspan="2">Total Por Pagar:$<input type="text" style="width:500px;border-width:0; background-color:transparent" name="lblTotalPorPagar" id="lblTotalPorPagar" readonly /></td> 	   
					 </tr>
				</table>
			</fieldset>	
			<fieldset>
				<legend>Pagos</legend>
				<table align="center" id="tblPagos" width="100%" class="display">
		        	<thead>
		        		<tr>
		        		<th>Folio Tipo Documento</th>
		        		<th>Tipo Documento</th>
		        		<th>Folio Definitivo</th>
		        		<th>Número de contrarecibo</th>
		        		<th>RFC Proveedor</th>
		        	    <th>Proveedor</th>
		        		<th>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Concepto&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
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
			</fieldset>
					
			<input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
			<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
			<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" />
			<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>" />			
			<input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
	    	<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
			<input type="hidden" name="nIdFirmante" id="nIdFirmante" />	
			<input type="hidden" name="nNumeroFirmante" id="nNumeroFirmante" />		
			<input type="hidden" name="nIdEstadoReq" id="nIdEstadoReq" />		    
		    <input type="hidden" name="fAprobacion" id="fAprobacion" />
		    <input type="hidden" name="fAnulacion" id="fAnulacion" />
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />		     
		    <input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" />
		    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>	
		    <input type="hidden" name="cIdDefinitivo" id="cIdDefinitivo" />
		    <input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
		    <input type="hidden" name="RFC" id="RFC" />
		    <input type="hidden" name="esAbierto" id="esAbierto" />
	    </div>
	</form>
  </body>
</html>
