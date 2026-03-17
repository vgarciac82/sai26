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
		cEjercicio = Calendar.getInstance().get(Calendar.YEAR);//(String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
		
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Nuevo Pasivo Contrato</title>
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
		deshabilitaTabs();
		oTable=$("#tblNuevoPasivo").dataTable({
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
					{ sName: "cIdPedidoDefinitivo" },
					{ sName: "cIdRFC" },
					{ sName: "cConceptoPedido" },
					{ sName: "mMontoBruto" },
					{ sName: "cIdPedido" }
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
			     int tabla=0;
				int consulta=0;
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map botones=nb.getBotones(role,"PasivoPedidos","nuevoPasivoPedido");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>		
			var consulta='<%=consulta%>';
			var tabla='<%=tabla%>';

			$('#tblNuevoPasivo tr').live('dblclick', function() { 
				if (tabla==0){
					if ($(this).hasClass('row_selected'))             
						$(this).removeClass('row_selected');         
					else            
						$(this).addClass('row_selected');
					var anSelected = fnGetSelected( oTable );
					var aData = oTable.fnGetData(anSelected[0]);
					$('#contratoDefinitivo').val(aData[1]);
			  		$('#cIdContrato').val(aData[6]);
			  		//$('#cIdSubPartida').val(aData[2]);
			  		$('#cUnidadEjecutoraPasivo').val(aData[0]);
			  		queryFormPost("creaPasivosContratoPedido",{async: false });
			  		if(aData!= ""){
			  		habilitaTabs()
			  		window.location = 'PasivosPedido.jsp?tab=1';
					}
			  	}
			});
			
		//Llenado de tabla y combo box por medio del CRUD
			querySelectPost("UnidadBusca2", "cIdUnidadEjecutora", {async: false });
		//	if (consulta==0){
				mostrar();
		//	}
		});		
		
		function mostrar() {

		 var qw = " 1=1 ";  
			if($("#cIdUnidadEjecutora").val()!=0){
				qw += " and cIdUnidadEjecutora='" + $("#cIdUnidadEjecutora").val()+"'";
			}
			
			/*
			if($("#cIdSubpartida").val()!=0){
			qw += " and cIdSubPartida='" + $("#cIdSubpartida").val()+"'";
			}*/
			
			
	        if($("#cIdDefinitivo").val()!=""){
		        qw+=" and  cIdPedidoDefinitivo LIKE '%25"+$("#cIdDefinitivo").val()+"%25'";
		    }
		    if($("#cDescripcion").val()!=""){
		          qw += " and  cConceptoPedido LIKE '%25"+$("#cDescripcion").val()+"%25'";
		    }
		    if($("#cContrato").val()!=""){
		          qw += " and  cIdPedido LIKE '%25"+$("#cContrato").val()+"%25'";
		    }
		    if($("#cIdRFC").val()!=""){
		          qw += " and  cIdRFC LIKE '%25"+$("#cIdRFC").val()+"%25'";
		    }

			oTable = $("#tblNuevoPasivo").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mPasivosPosiblesPedido('" + <%=cEjercicio%> + "')&qw="+ qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
				   	{ sName: "cIdUnidadEjecutora" },
					{ sName: "cIdPedidoDefinitivo" },
					//{ sName: "cIdSubPartida" },
					{ sName: "cIdRFC" },
					{ sName: "cConceptoPedido" },
					{ sName: "mMontoBruto" },
					{ sName: "mMontoNeto" },
					{ sName: "cIdPedido" },
					{ sName: "mMontoPorPagar" }
					
					]
        	});


			
		}
		function editarPorcentajePlurianualidad(cEjercicio, cIdContratoDefinitivo){
			if(confirm("\xBFEstas seguro de actualizar los datos?")){
				$("#cEjercicioUpdate").val(cEjercicio);
				$("#cIdContratoUpdate").val(cIdContratoDefinitivo);
               	$("#nPorcentajeIVA").val($("#"+cIdContratoDefinitivo+"-"+cEjercicio).val());
				queryFormPost("actualizaPorcentajePlurianualidad", {async: false});
			}
		}
		
		
				
		       function deshabilitaTabs(){
		     
						$( "#presupuestoPasivo" ).attr("disabled", true);
						$( "#precompromisoPasivo" ).attr("disabled", true);
						
				}
				
				function habilitaTabs(){
				 		$( "#presupuestoPasivo" ).attr("disabled", false);
						$( "#precompromisoPasivo" ).attr("disabled", false);
						
					
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
	</script>
</head>

  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
  		<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" />
  		<input type="hidden" name="cEjercicioUpdate" id="cEjercicioUpdate" />
  		<input type="hidden" name="cIdContratoUpdate" id="cIdContratoUpdate" />
  		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" />
		<input type="hidden" name="cIdUnidadEjecutora1" id="cIdUnidadEjecutora1" value="<%=usuarioTab1.getU_UR()%>" />
  		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
  		<input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" />
  		<input type="hidden" name="cIdContrato" id="cIdContrato" />
  		<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" />
  		<input type="hidden" name="cUnidadEjecutoraPasivo" id="cUnidadEjecutoraPasivo" />
  		
  		
  		<fieldset >
  			<legend>Nuevo Pasivo Pedido</legend>
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
											<input type="text" name="cContrato" id="cContrato" style="width: 50em;" />	
										</td>
									</tr>
									<tr >
										<td>Definitivo</td>
										<td>
										   <input type="text" name="cIdDefinitivo" id="cIdDefinitivo" style="width: 50em;" />	
										</td>
									</tr>
									<tr >
										<td>Descripci&oacute;n</td>
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
							    		<td colspan="2" align="center"><input type="button"  name="btnBuscarPlurianualidad" id="btnBuscarPlurianualidad" value="Buscar"  onclick="mostrar();"/></td>
							    	</tr>
							    </table> 
				    		</td>
				    	</tr>
				    	
				    	<tr  >
				    		<td style="width: 740px; height: 300px" >
							     <table align="left" id="tblNuevoPasivo" width="740px" class="display">
						        	<thead>
						        		<tr> 
						        		
				
						        		    <th>UNIDAD EJECUTORA</th>
						        			<th>PEDIDO</th>
						        			<th>PROVEEDOR</th>
						        			<th>CONCEPTO</th>			
						        			<th>MONTO BRUTO</th>
						        			<th>MONTO NETO</th>
						        			<th>CONSECUTIVO EJERCICIO ANTERIOR</th>
						        			<th>MONTO POR PAGAR</th>
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
