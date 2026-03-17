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
		String roles="";
		Map<String, Role> rol =usuarioTab.getRoles();
		if (usuarioTab == null) {
			response.sendRedirect("../../index.jsp");
			return;
		}	
		String cEjercicio = "";
		String cIdTipoContrato= "";
		String cIdUnidadEjecutora = "";
		String nIdConsecutivo = "";
		if (session.getAttribute(GestionInterface.ATT_ContratoEjercicio) != null) {
			cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
			cIdTipoContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoTipoContrato);
			cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_ContratoUnidadEjec);
			nIdConsecutivo = (String)session.getAttribute(GestionInterface.ATT_ContratoConsecutivo);		
		}else 
			response.sendRedirect("Contratos.jsp?tab=0");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>Partidas</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	
	<script type="text/javascript" charset="utf-8">
		var oTablePartidas;
		var oTableLineas;
		var roles='';	
		cIdTipoContrato="<%=cIdTipoContrato%>";
		$(document).ready(function() {
			$("#tbs").val(2);
			showAndHideTabs();
				<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Contratos","partidasContrato");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
				roles="<%=roles%>";
			//set readonly para deshabilitar backspace			
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblContrato").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;
			//inicializa caratula
			queryFormPost("mContratoCaratulaRead", {async: false});
			queryFormPost("mContratoHeaderRead", {async: false});
			queryFormPost("fnMontoSubtotalReadContrato", {async: false});
			queryFormPost("fnMontoIVAReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto1ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto2ReadContrato", {async: false});
			queryFormPost("fnMontoImpuesto3ReadContrato", {async: false});
			
			if(parseInt($("#nIdEstado").val(),10) > 3)
				$("#btnGuardarPartida").attr("disabled","true");
			if($("#lblImporteImpuesto1").val() == "")
				$("#divImporteImpuesto1").css("display","none");
			if($("#lblImporteImpuesto2").val() == "")
				$("#divImporteImpuesto2").css("display","none");
			if($("#lblImporteImpuesto3").val() == "")
				$("#divImporteImpuesto3").css("display","none");
			queryFormPost("fnMontoTotalContrato", {async: false});
			
			//click handler para mostrar lineas de partidas
			$('#tblPartidas tr').live('dblclick', function() {
				$(oTablePartidas.fnSettings().aoData).each(function(){
					$(this.nTr).removeClass('row_selected');
				});				
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');				
				var anSelected = fnGetSelected( oTablePartidas );				
				var aData = oTablePartidas.fnGetData(anSelected[0]);				
				mostrarLineas(aData[5]);//mostrarLineas(aData[4]);
			});
			
			reloadTablaPartidas();
			reloadLineas();			
			mostrarLineas(-1);
			
			if ($("#cIdTipoProcedimiento").val() == 'PF') {
				$( "#presupuestoContrato" ).attr("disabled", true);
				$( "#preCompromisoContrato" ).attr("disabled", true);
				$( "#pagosContrato" ).attr("disabled", true);
				$( "#plurianualidad" ).attr("disabled", true);
				$( "#pasivo" ).attr("disabled", true);
			}

			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
			if(nIdEstado == 1)
				$("#preCompromisoContrato").css("display", "none");
			else
				$("#preCompromisoContrato").css("display", "block");
			queryFormPost("mUsuarioMismaUE", {async: false   });
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
		
		function reloadTablaPartidas(){
			var func = "'" + $("#cEjercicio").val() + 				
				"','" + $("#cIdUnidadEjecutora").val() +
				"','" + $("#cIdTipoProcedimiento").val() +
				"','" + $("#nIdConsecutivoProcedimiento").val()  +
				"','"+ $("#cIdRFC").val()+"'"+
				","+ $("#nIdConsecutivoAdj").val()+""
				;
				
			var qw = " 1 = 1";
			
			oTablePartidas = $("#tblPartidas").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_PartidasPedidoContrato("+func+")&qw=" + qw,
				bJQueryUI: true,
				aaSorting: [[ 5, "asc" ]] ,
				aoColumns: [
					{sName: "descripcionText"},
					{sName: "descripcionTextAux"},
					{sName: "nCantidad"},
					{sName: "mMontoMinimoUnitario"},
					{sName: "mMontoMinimo"},
					{sName: "nIdLineaConsolidado"},
					{sName: "nConsecutivo", bVisible: false},
					{sName: "cDescripcion", bVisible: false}
				]
			});
		}
		
		function mostrarLineas(partida) {
			var qw = " cIdContrato = '" + $("#cIdContratoMat").val()+
			"' AND nIdLineaConsolidado = '" +partida+"'";	
			
			$("#tblLineas").dataTable().fnClearTable();
			$.getJSON("../../catalogos/SelectJson.jsp", {Tabla: "LINEASPARTIDASCONTRATO", Param: qw, ajax: false},
				function(data)
				{
					arrayCompleto=new Array();
					for(var i=0; i < data.length; i++){
						arrayCompleto [i]=[
							"<div style='text-align:center;'>"+data[i].Col0+"</div>",
							"<div style='text-align:center;'>"+data[i].Col1+"</div>",
							"<div style='text-align:center;'>"+data[i].Col2+"</div>",
							"<div style='text-align:center;'>"+data[i].Col3+"</div>",
							"<div style='text-align:center;'>"+data[i].Col4+"</div>",
							"<div id='precioUnitario"+i+"' style='text-align:center;'>"+data[i].Col5+"</div>",
							"<div style='text-align:center;'>"+data[i].Col6+"</div>",
							"<div id='montoBruto"+i+"' style='text-align:center;'>"+data[i].Col7+"</div>",
							"<div id='montoNeto"+i+"' style='text-align:center;'>"+data[i].Col8+"</div>"
						];
							
						$("#precioUnitario"+i).formatCurrency();
						$("#montoBruto"+i).formatCurrency();
						$("#montoNeto"+i).formatCurrency();
							
					}
					oTableLineas=$('#tblLineas').dataTable().fnAddData(arrayCompleto);
					
				});
				
		}
		
		function reloadLineas(){
			oTableLineas=$("#tblLineas").dataTable({
				    bPaginate: true,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
					sScrollX: "100%",
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
				}			
        	});
		}
		
		function guardaPartidas(){
			if(!(roles.toString().indexOf("ADMIN_RECMAT") >= 0 ) &&roles.indexOf("ANALISTA")<0 &&roles.indexOf("JEFE")<0 && parseInt($("#usuariosMismaUE").val(),10)==0
			&& !($("#U_LOGIN").val() == $("#cIdUsuarioCreacion").val())){
				swal("No tienes Permisos.",{icon:"info",button: "Cerrar"});
				return;
			}
			var aTrs = oTablePartidas.dataTable().fnGetNodes();
			var aData;
			var actualizaciones = 0;
			
			for(var i=0; i<aTrs.length; i++){
				aData = oTablePartidas.fnGetData(aTrs[i]);
				if($("#descripcionPartidaAux"+aData[6]).val() != ""){
					$("#descripcionPartida").val($("#descripcionPartida"+aData[6]).val() );
					$("#descripcionPartidaAdicional").val($("#descripcionPartidaAux"+aData[6]).val());
					$("#numeroPartida").val(aData[5]);
					queryFormPost("updateDescripcionPartida", {async: false});
					$("#tblPartidas").dataTable().fnUpdate($("#descripcionPartida"+aData[6]).val(),i,7);
					actualizaciones++;
				}
			}	
			
			if(actualizaciones > 0){
				guardaBitacora("GUARDA_PARTIDAS",$("#cIdTipoContrato").val()+'-'+$("#cIdUnidadEjecutora").val()
					+'-'+$("#nIdConsecutivo").val()+'/'+$("#cEjercicio").val());
				swal("La actualizacion se realizo correctamente.",{icon:"info",button: "Cerrar"});
			}
			else
			swal("No se realizo ningun cambio.",{icon:"info",button: "Cerrar"});
			return;
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
			<fieldset >
				<legend>Partidas del Contrato</legend>
					<table align="left" cellpadding="2" width="100%">
				    	<tr>
				    		<td align="right" colspan="2">
								<input type="button" class="btnInterfaceBackToTop ui-button ui-widget ui-state-default ui-corner-all" 		id="imgSalir" 	name="imgSalir" 	value="Salir"	  onclick="window.location = 'Contratos.jsp?tab=0';" />
				    		</td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" id="lblUnidadEjecutora" name="lblUnidadEjecutora"  readonly style=""/></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProcedimiento" id="lblProcedimiento" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblDefinitivo" id="lblDefinitivo" readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 700px;border-width:0; background-color:transparent" name="lblContrato" id="lblContrato"  readonly /></td>
				    	</tr>
				    	<tr>
				    		<td align="left" colspan="2"><input type="text" style="width: 600px;border-width:0; background-color:transparent" name="lblProveedor" id="lblProveedor"   readonly /></td>
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
				    </table>
			</fieldset>
					
   			<fieldset >
   				<legend>Partidas</legend>
   				<table id="tblPartidas" class="display" style="width: 100%">
					<thead >
						<tr>														
							<th align="center">DESCRIPCI&Oacute;N</th>
							<th align="center">DESCRIPCI&Oacute;N Adicional</th>
							<th align="center">CANTIDAD</th>
							<th align="center">PRECIO UNITARIO</th>
							<th align="center">MONTO NETO</th>
							<th align="center">LC</th>
							<th align="center">&nbsp;</th>
							<th align="center">&nbsp;</th>
						</tr>										
					</thead>
				</table>
		    	<table width="740px" align="left">
		    		
		    		<tr>
		    			<td align="left">
		    				<input type="button"  name="btnGuardarPartida" id="btnGuardarPartida" value="Guardar" onclick="guardaPartidas()" class="btnInterfaceBG ui-button ui-corner-all" />
		    			</td>
		    		</tr>
		    	</table>
   			</fieldset>
		    		
			<fieldset >
				<legend>L&iacute;neas</legend>
					<table id="tblLineas" class='display'  style="width: 100%">
			     	<thead>
			     		<tr>									        			
			     			<th>REQUISICI&Oacute;N</th>
			     			<th>L&Iacute;NEA</th>
			     			<th>CUCOP</th>
			     			<th>DESCRIPCI&Oacute;N</th>
			     			<th>CANTIDAD</th>
			     			<th>PRECIO UNITARIO</th>
			     			<th>% IVA</th>
			     			<th>MONTO BRUTO</th>
			     			<th>MONTO NETO</th>
			     		</tr>
			     	</thead>
			     </table>
			</fieldset>
	    			        
	        <!-- Hiddens de sesion -->
	        <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>"/>
	        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdSubPartida" id="cIdSubPartida"/>		    
		    <input type="hidden" name="cIdRFC" id="cIdRFC"/>
		    <!--  Auxiliares para consulta -->		    
		    <input type="hidden" name="cIdContratoMat" id="cIdContratoMat"/>
	      	<input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
	       	<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
	       	<input type="hidden" name="nIdLineaConsolidado" id="nIdLineaConsolidado" />
	       	<input type="hidden" name="nIdEstado" id="nIdEstado" />
	       	<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
	       	<input type="hidden" name="descripcionPartida" id="descripcionPartida" />  
	       	<input type="hidden" name="numeroPartida" id="numeroPartida" />
	       	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuarioTab.getLogin() %>"/>
	       	<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
	       	<input type="hidden" name="descripcionPartidaAdicional" id="descripcionPartidaAdicional" />
	       	<input type="hidden" name="usuariosMismaUE" id="usuariosMismaUE" value=""/>
	       	<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%= usuarioTab.getLogin() %>"/>
	       	<input type="hidden" name="cIdDocumento" id="cIdDocumento" value=""/>
	       	<input type="hidden" name="cAccion" id="cAccion" value=""/>
	       	<input type="hidden" name="nIdConsecutivoAdj" id="nIdConsecutivoAdj" value=""/>
	       	
	    </div>
	</form>
  </body>
</html>
