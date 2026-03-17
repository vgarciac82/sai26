<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.Calendar" %>
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
	int cEjercicio = 0;
	cEjercicio = Calendar.getInstance().get(Calendar.YEAR);
		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Nueva Modificación al Pedido</title>
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

	var cEjercicio;
	var oTable;
		$(document).ready(function() {
			// se deshabilitan las pestañas de presupuesto y precompromiso plurianual
			deshabilitaTabs();
			oTable=$("#tblPedidosAprobados").dataTable({
				    bPaginate: true,
        			bLengthChange: false,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: true,
        			sScrollY : "100%",
					sScrollX: "200%",
					sScrollXInner: "100%",
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
					aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdConsecutivo" },
					{ sName: "cIdPedido" },
					{ sName: "cIdPedidoDefinitivo" },
					{ sName: "cIdRFC" },
					{ sName: "cConceptoPedido" },
					{ sName: "mMontoBruto" },
					{ sName: "mMontoNeto" }
					],
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}			
        	});
		
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map botones=nb.getBotones(role,"Modificacion Pedidos","nuevaPedidoModificatorio");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>		
			
			queryFormPost("mSistema_cEjercicioRead", {async: false});
			queryFormPost("mPedidoModicadoChecaRolUsuario", {async: false});
			
			$('#tblPedidosAprobados tr').live('dblclick', function() { 
				$(oTable.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
				
				var anSelected = fnGetSelected( oTable );
				var aData = oTable.fnGetData(anSelected[0]);
				$('#pedido').val(aData[2]);
				$('#pedidoDefinitivo').val(aData[3]);

				if ($("#usuarioLoginRole").val() != 'ADMIN_RECMAT') { 
					$("#usuarioCreacionOriginal").val('');
					queryFormPost("mPedidoModicadoUsuarioCreacionOriginalRead",{async: false });
					if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
						alert("No tiene permisos para realizar esta acción");
						return;
					} 
				}
				
				$('#cIdTipoModNot').val(1);
				if ($('#cIdTipoMod').val() == 1) {
					$('#cIdTipoModNot').val(0);	
				}
				$('#pedidoPendiente').val('');
				queryFormPost("mPedidoModificadoPendiente",{async: false });
				if ($('#pedidoPendiente').val() == 'PENDIENTE'){
					alert("No es posible generar un modificatorio a este pedido ya que hay modificatorios pendientes o el tipo de modificación no esta permitido");
					return;
				}
				
		  		queryFormPost("totalPedidoModificadoAnterior",{async: false });
		  		if ($('#totalAnterior').val() != ''){
		  			$('#consecutivoMod').val(parseInt($('#consecutivoMod').val(),10) + 1);
		  			queryFormPost("mPedidoModificadoCreate",{async: false });
		  			alert("Modificacion " + $('#consecutivoMod').val() + " creada existosamente");
		  		}
		  		else {
		  			$('#totalAnterior').val(aData[7]);
		  			$('#consecutivoMod').val(1);
		  			queryFormPost("mPedidoModificadoCreate",{async: false });
		  			alert("Primera modificación creada existosamente");
		  		}
		  		
		  		var pMod = $('#consecutivoMod').val();
		  		var pDefinitivo = $('#pedidoDefinitivo').val();
		  		var pId = $('#pedido').val();
		  		var pEjercicio = $('#cEjercicio').val();
			  	habilitaTabs();
			  	window.location = 'PedidoModificatorio.jsp?tab=2' + '&mod=' + pMod + '&pDefinitivo=' + pDefinitivo + '&tipoArchivo=pedidoMod'  + '&pId=' + pId + '&pEjercicio=' + pEjercicio;
			});
			
			//Llenado de tabla y combo box por medio del CRUD
			querySelectPost("UnidadBusca2", "cIdUnidadEjecutora", {async: false });
		});		
		
		function mostrar() {
			var qw = " cEstado='APROBADO' ";  
			if($("#cIdUnidadEjecutora").val() != 0){
				qw += " and cIdUnidadEjecutora='" + $("#cIdUnidadEjecutora").val() + "'";
			}
	        if($("#cIdDefinitivo").val() != ""){
		        qw+=" and  cIdPedidoDefinitivo LIKE '%25" + $("#cIdDefinitivo").val() + "%25'";
		    }
		    if($("#cDescripcion").val() != ""){
		          qw += " and  cConceptoPedido LIKE '%25" + $("#cDescripcion").val() + "%25'";
		    }
		    if($("#cPedido").val()!=""){
		          qw += " and  cIdPedido LIKE '%25" + $("#cPedido").val() + "%25'";
		    }
		    if($("#cIdRFC").val()!=""){
		          qw += " and  cIdRFC LIKE '%25" + $("#cIdRFC").val() + "%25'";
		    }

			oTable = $("#tblPedidosAprobados").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_consultaPedidos&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ], [ 1, "asc" ]] ,
				aoColumns: [
				   	{ sName: "cIdUnidadEjecutora" },
				   	{ sName: "nIdConsecutivo" },
					{ sName: "cIdPedido" },
					{ sName: "cIdPedidoDefinitivo" },
					{ sName: "cIdRFC" },
					{ sName: "cConceptoPedido" },
					{ sName: "mPedidoMontoBruto" },
					{ sName: "mPedidoMontoNeto" }
					]
        	});	
		}
		
		function deshabilitaTabs(){
			$( "#caratulaPedidoMod" ).attr("disabled", true);
			$( "#observacionesPedidoMod" ).attr("disabled", true);
			$( "#presupuestoPedidoMod" ).attr("disabled", true);
		}
		
		function habilitaTabs(){
			$( "#caratulaPedidoMod" ).attr("disabled", false);
			$( "#observacionesPedidoMod" ).attr("disabled", false);
			$( "#presupuestoPedidoMod" ).attr("disabled", false);
		}
		
		function fnGetSelected( oTableLocal ) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ ) {
				if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}	
	</script>
</head>

  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
  		<input type="hidden" name="cEjercicio" id="cEjercicio" />
  		<input type="hidden" name="cParametro" id="cParametro" />
  		<input type="hidden" name="pedidoDefinitivo" id="pedidoDefinitivo" />
  		<input type="hidden" name="pedido" id="pedido" />
  		
  		<input type="hidden" name="totalAnterior" id="totalAnterior" value="" />
  		<input type="hidden" name="consecutivoMod" id="consecutivoMod" />
  		<input type="hidden" name="pedidoPendiente" id="pedidoPendiente" value="" />
  		<input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  		<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
  		<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
  		
  		<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" />
  		<input type="hidden" name="cEjercicioUpdate" id="cEjercicioUpdate" />
		<input type="hidden" name="cIdUnidadEjecutora1" id="cIdUnidadEjecutora1" value="<%=usuarioTab1.getU_UR()%>" />
  		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
  		<input type="hidden" name="cIdTipoModNot" id="cIdTipoModNot" />
  		
  		
  		<fieldset >
  			<legend>Nueva Plurianualidad</legend>
		  		<div id="container" class="container">	
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
										<td>Pedido</td>
										<td>
											<input type="text" name="cPedido" id="cPedido" style="width: 50em;" />	
										</td>
									</tr>
									<tr >
										<td>Definitivo</td>
										<td>
										   <input type="text" name="cIdDefinitivo" id="cIdDefinitivo" style="width: 50em;" />	
										</td>
									</tr>
									<tr >
										<td>Concepto</td>
										<td>
										   <input type="text" name="cDescripcion" id="cDescripcion" style="width: 50em;" />	
										</td>
									</tr>
									<tr >
										<td>R.F.C.</td>
										<td>
										   <input type="text" name="cIdRFC" id="cIdRFC" style="width: 50em;" />	
										</td>
									</tr>	
							    	<tr>
							    		<td colspan="2" align="center"><input type="button" name="btnBuscarPedidos" id="btnBuscarPedidos" value="Buscar" onclick="mostrar();" /></td>
							    	</tr>
							    </table> 
				    		</td>
				    	</tr>
				    	
				    	<tr></tr>
				    	<tr></tr>
				    	<tr></tr>
				    	
				    	<tr>
				    		<td align="left" > Tipo de Modificación
				    			<select id="cIdTipoMod" name="cIdTipoMod" style="width: 20em;" >
									<option label="Ampliación" value="0" selected="selected" > </option>
									<option label="Reducción" value="1"  > </option>
								</select>
				    		</td>
				    	</tr>
				    	
				    	<tr>
				    		<td style="width: 740px; height: 300px" >
							     <table align="left" id="tblPedidosAprobados" width="740px" class="display">
						        	<thead>
						        		<tr> 
						        		    <th width="5%" >UNIDAD EJECUTORA</th>
						        		    <th width="5%" >#</th>
						        			<th>PEDIDO</th>
						        			<th>DEFINITIVO</th>
						        			<th>PROVEEDOR</th>
						        			<th>CONCEPTO</th>			
						        			<th>MONTO BRUTO</th>
						        			<th>MONTO NETO</th>
						        		</tr>
						        	</thead> 
						        </table> 
				    		</td>
				    	</tr>
				    	
				    </table>
			    </div>
  		</fieldset>
	</form>
  </body>
  
</html>
