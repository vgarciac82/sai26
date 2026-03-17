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
    <title>Responsables Almacén</title>    
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
		var oTableSolicitudes;
		$(document).ready(function() {	
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
				Map botones=nb.getBotones(roles,"Pedidos","responsablesAlmacen");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>		
			
			//set ReadOnly
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
			document.getElementById("lblTotal").style.readonly=true;
			//Lectura de caratula
			queryFormPost("mPedidoCaratulaRead", {async: false});
			queryFormPost("mPedidoHeaderRead", {async: false});
			queryFormPost("fnMontoSubtotalRead", {async: false});
			queryFormPost("fnMontoIVARead", {async: false});
			queryFormPost("fnMontoImpuesto1Read", {async: false});
			queryFormPost("fnMontoImpuesto2Read", {async: false});
			queryFormPost("fnMontoImpuesto3Read", {async: false});
			
			//almacen
			querySelectPost("mCatalogoAlmacenEntregaRead", "cIdAlmacen", {async: false});
			
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
			
			//Click handler para cargar las lineas de las partidas
			$('#tblSolicitudes tr').live('dblclick', function() {
				$(oTableSolicitudes.fnSettings().aoData).each(function(){
					$(this.nTr).removeClass('row_selected');
				});
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableSolicitudes );				
				var aData = oTableSolicitudes.fnGetData(anSelected[0]);
				document.getElementById("almacen").style.display="block";
				//almacen
				
				if($("#cIdAlmacenEntrega_"+aData[0]).val()=='NO APLICA'){
					//$("#cIdAlmacenEntrega").val('.');
					$("#cIdAlmacenEntrega").val(aData[1]);
				}else if ($("#cIdAlmacenEntrega_"+aData[0]).val()=='SEGUN ANEXO'){
				//	$("#cIdAlmacenEntrega").val('..');
					$("#cIdAlmacenEntrega").val(aData[1]);
				}else{
				//	$("#cIdAlmacen").val($("#cIdAlmacenEntrega_"+aData[2]).val());
					$("#cIdAlmacenEntrega").val(aData[1]);
				}
				$("#cIdAlmacen").val($("#cIdAlmacenEntrega_"+aData[1]).val());
			//	$("#cIdAlmacen").val($("#cIdAlmacenEntrega_"+aData[2]).val());
				$("#cIdSolicitud").val(aData[0]);
				
				// responsable	
				$("#responsableAlmacen").val($("#nombreResponsable_"+aData[0]).val());
				//facturar
				$("#facturar").val(aData[3]);
			});			
			mostrarSolicitudes();		
			
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
			
			//Guarda almacen
			$("#guardaAlmacen").button().click(function(){
				queryFormPost("responsableAlmacen", {async: false});
				alert("Datos guardados");
				mostrarSolicitudes();
			});
			
			//mostrar lugar de entrega
			
		//	$("#nuevoAlmacen").button().click(function(){
				//queryFormPost("responsableAlmacen", {async: false});
				//alert("Datos guardados");
				//mostrarSolicitudes();
		//		document.getElementById("entrega").style.display="block";
	//		});
			
			//guardar lugar de entrega			
		/* 	$("#nuevaDireccion").button().click(function(){
			if($("#cIdAlmacenEntrega").val()=='.'){
				$("#cIdAlmacenEntrega").val("CNA-");
			}else if($("#cIdAlmacenEntrega").val()=='..'){
				$("#cIdAlmacenEntrega").val("CNA-");
			}else{
				$("#cIdAlmacenEntrega").val($("#cIdAlmacenEntrega").val()+'.');
			}
			$("#cIdAlmacenEntrega").val($("#cIdAlmacenEntrega").val()+'.');
			alert($("#cIdAlmacenEntrega").val());
				queryFormPost("guardaAlmacen", {async: false});
				alert("Datos guardados");
				//almacen
				querySelectPost("mCatalogoAlmacenEntregaRead", "cIdAlmacen", {async: false});
				$("#cIdAlmacen").val(aData[1]);
			}); */
	
						
		});
		
		
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
		function mostrarSolicitudes() {
			
			oTableSolicitudes = $("#tblSolicitudes").dataTable({
				sScrollX: "150%",
				sScrollXInner: "150%",
				bScrollCollapse: true,
				bDestroy: true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mResponsablesRequisicion('"+$("#cIdPedido").val()+"')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdSolicitud" },
					{ sName: "cIdAlmacen" },
			//		{ sName: "cIdAlmacen" },
					{ sName: "nombre" },
					{ sName: "facturar" }
					]
			});
		}
		
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
	</script>
</head>  
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container">
			<table align="left" width="750px">
				<tr>
					<td>
						<fieldset style="width:750px" align="left">
							<legend>Solicitudes del pedido</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
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
							    		<td align="left" colspan="2"><input type="text" style="width: 700px" name="lblPedido" id="lblPedido" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 500px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblSubtotal" id="lblSubtotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr id="divImporteIVA">
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblImporteIVA" id="lblImporteIVA" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr id="divImporteImpuesto1">
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblImporteImpuesto1" id="lblImporteImpuesto1" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr id="divImporteImpuesto2">
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblImporteImpuesto2" id="lblImporteImpuesto2" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr id="divImporteImpuesto3">
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblImporteImpuesto3" id="lblImporteImpuesto3" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>								    	
							    </table>
						</fieldset>
					</td>
				</tr>	
		    	<tr>
		    		<td>
		    			<fieldset style="width:750px" align="left">
		    				<legend>Solicitudes</legend>
						    	<table width="740px" align="left">
						    		<tr>
						    			<td style="width: 710px">
						    				<table id="tblSolicitudes" class="display" width="740px">
												<thead >
													<tr>														
														<th align="center" width="10px">Solicitud</th>
														<th align="center" width="100px">Almacén</th>
														<!-- <th align="center">Almacén</th> -->
														<th align="center" width="100px">Responsable</th>
														<th align="center" width="100px">Facturar</th>
													</tr>										
												</thead>
											</table>
						    			</td>
						    		</tr>
						    	</table>
		    			</fieldset>
		    		</td>
		    	</tr>
		    	<!-- datos del almacen -->
		    	<tr>
		    		<td>
		    			<div id="almacen" style="display: none">
		    				<table>
			    				<tr>
			    					<!-- <td align="left">Lugar de Entrega<select name="cIdAlmacen" id="cIdAlmacen" style="width: 550px"></select></td> -->
							    <!--	<td align="left"><select name="cIdAlmacen" id="cIdAlmacen" style="width: 550px"></select></td> 
							    	<td align="left"><input type="button" id="nuevoAlmacen" name="nuevoAlmacen" value="Nuevo"/></td>
							   </td>-->
							    <td align="left">Lugar de Entrega: <input type="text" id="cIdAlmacen" name="cIdAlmacen" style="width: 550px" onkeypress="textCounter(this,350);" />
			    				</tr>
			    				<!-- <tr>
			    					<td>
				    					<div id="entrega" style="display: none">
				    						<table>
				    						<tr>
						    					<td>Direccion<input type="text" id="direccion" name="direccion" style="width: 550px" /></td>
										    	<td><input type="button" id="nuevaDireccion" name="nuevaDireccion" value="Guardar"/></td>	
									    	</tr>
									    	</table>
								    	</div>	
							    	</td>
			    				</tr> -->
			    				<tr>
			    					<td align="left">Responsable: <input type="text" id="responsableAlmacen" name="responsableAlmacen" style="width: 550px"  onkeypress="textCounter(this,200);"/></td>
							    	<!--  --><td align="left"></td>
			    				</tr>
			    				<tr>
			    					<td align="left">Facturar a: <input type="text" id="facturar" name="facturar" style="width: 550px" onkeypress="textCounter(this,100);" /></td>
							    	<!--  --><td align="left"></td>
			    				</tr>
		    				</table>
		    			</div>
		    		</td>
		    	</tr>
		    	
	    		<tr>
	    			<td>
	    				<input type="button" id="guardaAlmacen" name="guardaAlmacen" value="Guardar"/>
	    			</td>
	    		</tr>
	        </table>
	        <!-- Hiddens de sesion -->
	        <input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>"/>
	        <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="hidden" name="cIdPedido" id="cIdPedido"/>
		    
		    
		    <!-- almacen -->
		    <input type="hidden" name="cIdSolicitud" id="cIdSolicitud"/>
		    <input type="hidden" name="cIdAlmacenEntrega" id="cIdAlmacenEntrega"/>
		    
		    <!--  Auxiliares para consulta 		    
	      	<input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
	       	<input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
	       	<input type="hidden" name="nIdLineaConsolidado" id="nIdLineaConsolidado" />-->
	    </div>
	</form>
  </body>
</html>
