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
	String idRol="0";		
	Map rol =usuarioTab.getRoles();	
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
    <title>Firmantes del Pedido</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	
	<script type="text/javascript" charset="utf-8">
		var oTableCatalogo;
		var oTableFirmantes;
		var validaAgregaFirmantes;
		$(document).ready(function() {
			$("#tbs").val(3);
			showAndHideTabs();
			<%
			    String roles="";
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Pedidos","firmantesPedido");
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
			
			//Click handler para agregar un nuevo firmantes
			$('#tblCatalogoFirmantes tr').live('dblclick', function() {
					if(validaAgregaFirmantes==true){        
						$(oTableCatalogo.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});						
						$(this).addClass('row_selected');   
						var anSelected = fnGetSelected( oTableCatalogo );						
						var aData = oTableCatalogo.fnGetData(anSelected[0]);					
						$("#nIdFirmante").val(aData[1]);		
						//if ($("#nIdEstadoPed").val() == "1" ){
							 //if($("#U_LOGIN").val()==$("#cIdUsuarioCreacion").val() || ($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0)){
								queryFormPost("pa_mAgregaFirmantePedidoCreate", {async: false});
								oTableFirmantes.fnClearTable(oTableFirmantes);
								mostrarFirmantes();
								guardaBitacora("AGREGA_FIRMANTE",$("#cIdTipoPedido").val()+'-'+$("#cIdUnidadEjecutora").val()
										+'-'+$("#nIdConsecutivo").val()+'/'+$("#cEjercicio").val());
							//}	
						//}
					}
					else{
						swal("El suaurio no tiene permisos de realizar esta acción.",{icon:"info",button: "Cerrar"});
					}
			});	
			
			$('#tblFirmantesPedido tr').live('dblclick', function(){  
				//Click handler para quitar un firmante, al momento de dar de baja un firmante se ejecuta un trigger para renumerar
					var roles="<%=roles%>";
					$(oTableFirmantes.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					
					$(this).addClass('row_selected');   
					var anSelected = fnGetSelected( oTableFirmantes );
					var aData = oTableFirmantes.fnGetData(anSelected[0]);
					
					$("#nNumeroFirmante").val(aData[0]);
										
					if ($("#nIdEstadoPed").val() != "4"){ //Cualquier otro estado diferente a capturado
						queryFormPost("mUsuarioMismaUE", {async: false   });
						if($("#U_LOGIN").val()==$("#cIdUsuarioCreacion").val() || roles.indexOf("ADMIN_RECMAT") >=0 
						||roles.indexOf("ANALISTA")>=0 ||roles.indexOf("JEFE")>=0 ||parseInt($("#usuariosMismaUE").val(),10)==1){
							
							queryFormPost("firmantesPedidoDelete", {async: false});					
							oTableFirmantes.fnClearTable(oTableFirmantes);					
							mostrarFirmantes();
							guardaBitacora("BORRA_FIRMANTE",$("#cIdTipoPedido").val()+'-'+$("#cIdUnidadEjecutora").val()
										+'-'+$("#nIdConsecutivo").val()+'/'+$("#cEjercicio").val());
						}else{
							swal("El suaurio no tiene permisos de realizar esta acción.",{icon:"info",button: "Cerrar"});
						}
					}
			});			
			//Lectura de caratula
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("cg_roleRead", {async: false});
			validaAgregaFirmantes=validaEdicionDatos();
			//Agrega el firmante de gerente en automatico
			queryFormPost("hayFirmanteAgregado", {async: false});
			if(parseInt($("#hayFirmante").val(),10)==0){
				//obtiene el nidfirmante de cada ue que sea gerente
				queryFormPost("obtieneNidFirmante", {async: false,callback : function() {
					//Se agrega el firmante
					if($("#nIdFirmante").val()!=0)
						queryFormPost("pa_mAgregaFirmantePedidoCreate", {async: false});
					}
				});
			}
			//Mostrar tablas
			mostrarCatalogo();
			mostrarFirmantes();
			if ($("#cIdTipoProcedimiento").val() == 'PF') {
				$( "#presupuestoPedido" ).attr("disabled", true);
				$( "#preCompromisoPedido" ).attr("disabled", true);
				$( "#pagosPedido" ).attr("disabled", true);
			}

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
		function mostrarCatalogo () {
			var qw = "cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'"; 
			oTableCatalogo = $("#tblCatalogoFirmantes").dataTable({
				bAutoWidth : true,
				bDestroy: true,
				sScrollX: "100%",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_catalogoFirmantesPedido&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" }
				]
        	});
		}
		function mostrarFirmantes () {
			var qw = " cEjercicio = '" + $("#cEjercicio").val() +
			 "' AND cIdTipoPedido = '" + $("#cIdTipoPedido").val() +
			 "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() +
			 "' AND nIdConsecutivo = " + $("#nIdConsecutivo").val();
			oTableFirmantes = $("#tblFirmantesPedido").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
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
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_firmantesPedido&qw=" + qw,
				
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nNumeroFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" },
					{ sName: "cTipoFirmante" }
				]
        	});
		}
		function validaEdicionDatos(){
			var roles="<%=roles%>";
			queryFormPost("mValidaPagosPedidoRead",{async: false});
			queryFormPost("mTotalPagosPedidoRead",{async: false});
			
			if($("#lblEstado").val().toString() == "APROBADO"){
				if(parseInt($("#cValidaPagosPedido").val(),10) == 1){
					if(roles.indexOf("ADMIN_RECMAT") >=0 ){
						if(parseInt($("#cTotalPagosPedido").val(),10) == 0)
							return true;
						else
							return false;
					}
					else
						return true;
				}
				else
					return false;
			}
			return true;
		}
		function guardaBitacora(accion,documento){
			//Bitácora
			$("#cAccion").val(accion);
			$("#cIdDocumento").val(documento);
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
	</script>
  </head>  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container" style="width: 95%">
			
			<fieldset>
				<legend>Información del Pedido</legend>
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
				    		<td align="left" colspan="2"><input type="text" style="width: 100px;border-width:0; background-color:transparent" name="lblEstado" id="lblEstado" readonly /></td>
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
				    </table>
			</fieldset>	
			<fieldset>
				<legend>Firmantes del Pedido</legend>
					<table align="center" id="tblFirmantesPedido" width="100%" class="display">
			        	<thead>
			        		<tr>
			        			<th>#</th>
			        			<th>NOMBRE</th>
			        			<th>PUESTO</th>
			        			<th>CONDICI&Oacute;N</th>									        			
			        		</tr>
			        	</thead>
			        </table>
			</fieldset>
			<fieldset>
				<legend>Cat&aacute;logo de Firmantes</legend>
					<table align="center" id="tblCatalogoFirmantes" width="100%" class="display">
			        	<thead>
			        		<tr>
			        			<th>UNIDAD EJECUTORA</th>
			        			<th>#</th>
			        			<th>NOMBRE</th>
			        			<th>PUESTO</th>
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
		    <input type="hidden" name="nIdEstadoPed" id="nIdEstadoPed" />		     
		    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
	    	<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >	 	
	    	<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
	    	<input type="hidden" name="cPedidoDefinitivo" id="cPedidoDefinitivo" />
	    	<input type="hidden" name="cTotalPagosPedido" id="cTotalPagosPedido"/>
	    	<input type="hidden" name="cValidaPagosPedido" id="cValidaPagosPedido"/>
	    	<input type="hidden" name="hayFirmante" id="hayFirmante"/>
	    	<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
	    	<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
	       	<input type="hidden" name="cIdDocumento" id="cIdDocumento" value=""/>
	       	<input type="hidden" name="cAccion" id="cAccion" value=""/>
	    </div>
	</form>
  </body>
</html>
