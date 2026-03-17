<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab1.getLogin();
	String roles="";
	Map rol =usuarioTab1.getRoles();
	if (usuarioTab1 == null) {
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
		response.sendRedirect("Contratos.jsp?tab=0");
		
		
		int cEjercicioPlurianual=0;
		cEjercicioPlurianual = Calendar.getInstance().get(Calendar.YEAR);//(String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Plurianualidad Pedido</title>
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
	<script type="text/javascript" charset="utf-8"><!--

	var cEjercicio;
	//var cIdTipoContrato;
	var oTable;
		$(document).ready(function() {
			<%
				int agregaPlurianulidad=0;
		        int eliminaPlurianulidad=0;
		        int guardarPlurianulidad=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb = new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				
				Map botones=nb.getBotones(roles,"Pedidos","plurianualidad");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%	
					String img=(String) b.getValue();
					if ("imgEliminarPlurianualidadPedido".equals(img)){  
						%>$("#<%=img%>").attr("disabled", false);<%
						eliminaPlurianulidad=1;
					}
					if ("imgGuardarPlurianualidadPedido".equals(img)){// actualiza
						%>$("#<%=img%>").attr("disabled", false);<%
						guardarPlurianulidad=1; 
					}
					if ("imgAgregarPlurianualidadPedido".equals(img)){
						%>$("#<%=img%>").attr("disabled", false);<%
						agregaPlurianulidad=1; 
					}
				} 
				%>   
			setFieldsInit();
			
			
			
			$('#tblConsultaPlurianualidad tr').live('dblclick', function() { 
				//if (tabla==0){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );
					var aData = oTable.fnGetData(anSelected[0]);
					if(aData!= "")
						window.location = aData[0];
			//	}
			});
			

				queryFormPost("mPedidoHeaderRead", {async: false});
				queryFormPost("contratoDefinitivoPlurianualPedido", {async: false}); 
				queryFormPost("contratoDefinitivoContratoPedido", {async: false}); 
				querySelectPost("UnidadPedidosRead", "cIdUnidadEjecutora", {async: false });
				querySelectPost("estadoPedidoRead", "nIdEstado", {async: false });
				queryFormPost("mSistema_cEjercicioRead", {async: false});
				querySelectPost("llenaTipoCambioCotizaciones","cboCambioCotizacion",{async:false});
				queryFormPost("mContratoPagadoReadPedido", {async: false});
				queryFormPost("mContratoPorPagarReadPedido", {async: false});
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
					
				//	if (consulta==0){
						mostrar();
				//	}
				var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
				
				if(nIdEstado == 1)
					$("#preCompromisoPedido").css("display", "none");
				else
					$("#preCompromisoPedido").css("display", "block");
		});
		
	
//-------------------------------------------------------------------------------------------------------------------------------
		// Agrega una nueva plurianualidad
	function agregarNuevaPluri(){
		var agregaPlurianulidad='<%=agregaPlurianulidad%>';
		if (agregaPlurianulidad==0){
			if ($("#tblConsultaPlurianualidad").dataTable().fnGetNodes().length == 0){
				$("#cejercicioPlurianual").val(parseInt($("#cEjercicioPlurianualidad").val(),10));
				$("#montoBruto").val('0');
				$("#porcentajeIva").val('0');
				queryFormPost("sp_plurianualidadInsertaPedido", {async: false});
				mostrar();		
			}else{
						queryFormPost("checa_maximo_pedido", {async: false});
						$("#cejercicioPlurianual").val(parseInt($("#maximoPlurianual").val(),10)+1);
						$("#montoBruto").val('0');
						$("#porcentajeIva").val('0');
						queryFormPost("sp_plurianualidadInsertaPedido", {async: false});
						mostrar();
			}
		}else{
			alert("No tiene permiso para realizar esta accion, contacte a su administrador");
		}
	}
		
		
			
		function eliminaPlurianualidad(){
			var eliminaPlurianulidad='<%=eliminaPlurianulidad%>';
			if(eliminaPlurianulidad==0){
				if ($("#tblConsultaPlurianualidad").dataTable().fnGetNodes().length != 0){
			  		queryFormPost("checa_maximo_pedido", {async: false});
					queryFormPost("borraLineaPlurianualidadPedido", {async: false});
					mostrar();
				}
			}else{
				alert("No tiene permisos para realizar esta accion, contacte a su administrador");
			}
			
		}
		
		
		//  Actualiza el valor de %IVA y MontoNeto de una plurianualidad
		function actualizaPlurianualidad(){
		   var actualizaPlurianulidad='<%=guardarPlurianulidad%>';
		   if (actualizaPlurianulidad==0){
			    var aTrs=$("#tblConsultaPlurianualidad").dataTable().fnGetNodes();
				if (aTrs.length != 0){
					 for(var i=0; i <aTrs.length; i++){
						aData=oTable.fnGetData(aTrs[i])
						if(parseFloat($("#nPorcentajeIva_"+aData[5]+"").val())> 100 || parseFloat($("#nPorcentajeIva_"+aData[5]+"").val()) < 0 ){
				          alert("El Iva no puede ser Mayor a 100 o Menor a 0");
				          return;
				        }
			          	$("#cejercicioPlurianual").val(aData[0])
						$("#montoBruto").val($("#mMontoBruto_"+aData[5]+"").val());	
						$("#porcentajeIva").val(parseInt($("#nPorcentajeIva_"+aData[5]+"").val(),10));
						queryFormPost("sp_plurianualidadInsertaPedido", {async: false});
					}
				}
				mostrar();
		   }else{
			   alert("No tiene permisos para realizar esta accion, contacte a su administrador");
		   }
	      
		}
//-------------------------------------------------------------------------------------------------------------------------------		
		
		function setFieldsInit(){
			//set readonly para deshabilitar backspace			
			document.getElementById("lblUnidadEjecutora").style.readonly=true;
			document.getElementById("lblProcedimiento").style.readonly=true;
			document.getElementById("lblDefinitivo").style.readonly=true;
			document.getElementById("lblPedido").style.readonly=true;
			document.getElementById("lblProveedor").style.readonly=true;
			document.getElementById("lblEstado").style.readonly=true;
		}
		
		function mostrar() {
			 var qw = "cIdPedidoDefinitivo LIKE'%25" +$("#cIdDefinitivo").val()+"%25'";
			
			oTable = $("#tblConsultaPlurianualidad").dataTable({
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
					sEmptyTable: "No hay Plurianualidades",
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
				
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_plurianualidadEditaPedido&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cEjercicio" },
					{ sName: "mMontoBruto1" },
					{ sName: "nPorcentajeIVA1" },
					{ sName: "mMontoIVA" },
					{ sName: "mMontoNeto" },
					{ sName: "identificador" ,bVisible:false}
					]
        	});
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


	--></script>
</head>

<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
	<form>
	<div id="container" class="container">
		<table align="left" width="750px">
			<tr>
				<td>
	 
		<fieldset style="width:750px" align="left"> 
			<legend>Plurianualidad</legend>
			<table align="left" cellpadding="2" width="100%"> 
			<tr>
  				<td align="right" colspan="2">  
					<img id="imgAgregarPlurianualidadPedido" src="../../imagenes/iconos/aceptar.png" style="cursor: pointer"  onclick="agregarNuevaPluri();"/>&nbsp;Agregar

					<img id="imgGuardarPlurianualidadPedido" src="../../imagenes/iconos/guardar.png" style="cursor: pointer"  onclick="actualizaPlurianualidad();"/>&nbsp;Guardar

					<img id="imgEliminarPlurianualidadPedido" src="../../imagenes/iconos/rechazar.png" style="cursor: pointer"  onclick="eliminaPlurianualidad();"/>&nbsp;Eliminar
				</td>
			 </tr>
			 <tr>
					<td align="left" colspan="2">
						<input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora"  readonly style="border-width:0; background-color:transparent"/>
					</td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProcedimiento" id="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblDefinitivo" id="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 700px" name="lblPedido" id="lblPedido"  readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor"   readonly style="border-width:0; background-color:transparent"/></td>
			 </tr>
			 <tr>
					<td align="left" colspan="2"><input type="text" style="width: 100px" name="lblEstado" id="lblEstado" readonly style="border-width:0; background-color:transparent"/></td>
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
				<td style="width: 740px; height: 300px" >
					<table align="left" id="tblConsultaPlurianualidad" width="740px" class="display">
						<thead>
							<tr>
							    <th>EJERCICIO</th>
							    <th>MONTO BRUTO</th>
							    <th>% DE IVA</th>			
							    <th>MONTO IVA</th>
							    <th>MONTO NETO</th>	
							    <th></th>		        								        			
							 </tr>
						</thead> 
					</table> 
				</td>
			</tr>
			
			<tr>
				<td>
					<table align="left" width="80%">			
						<td><input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" /></td>
						<td><input type="hidden" name="cEjercicioPlurianualidad" id="cEjercicioPlurianualidad" value="<%=cEjercicioPlurianual%>" /></td>
						<input type="hidden" name="cIdTipoPedido" id="cIdTipoPedido" value="<%=cIdTipoPedido%>" />
						<input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>" />
						<input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>" />
						<input type="hidden" name="montoBruto" id="montoBruto" />
						<input type="hidden" name="porcentajeIva" id="porcentajeIva" />
						<input type="hidden" name="cejercicioPlurianual" id="cejercicioPlurianual" />
						<input type="hidden" name="maximoPlurianual" id="maximoPlurianual" />
						<input type="hidden" name="cIdDefinitivo" id="cIdDefinitivo" />
						<input type="hidden" name="cIdDefinitivoPedido" id="cIdDefinitivoPedido" />
						<input type="hidden" name="cIdDefinitivoPlurianual" id="cIdDefinitivoPlurianual" />
						
						<tr>
							<td><input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/></td>
						</tr>
					</table>
				</td>
			</tr>
			
		</table>
	</div>
</form>
</body>
</html>

