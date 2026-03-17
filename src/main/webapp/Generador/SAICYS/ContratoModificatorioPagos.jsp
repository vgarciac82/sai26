<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();	
	Map rol =usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}	
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";		
	if (session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null) {
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		
		cIdTipoContrato = cIdContrato.split("-")[0];
		cIdUnidadEjecutora = cIdContrato.split("-")[1];
		nIdConsecutivo = cIdContrato.split("-")[2];		
	}else 
		response.sendRedirect("ContratosModificatorio.jsp?tab=0");	
%>
<!DOCTYPE html>
<html>
  <head>    
    <title>Pagos del Contrato</title>    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">	
	
	<script type="text/javascript" charset="utf-8">
		var oTablePagos;	
		$(document).ready(function() {
			tabb=6;
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
				Map botones=nb.getBotones(roles,"ContratosModificado","pagosContratoModificado");
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
			document.getElementById("lblContrato").style.readonly = true;
			document.getElementById("lblProveedor").style.readonly = true;
			document.getElementById("lblEstadoMod").style.readonly = true;
			document.getElementById("lblTotalAnterior").style.readonly = true;
			document.getElementById("lblTotalModificado").style.readonly = true;
			document.getElementById("lblTotal").style.readonly = true;
						
			$('#tblPagos').on('dblclick','tr', function() {        
				$(oTablePagos.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
			});
			
			queryFormPost("mContratoHeaderRead", {async: false});
			queryFormPost("mContratoModificadoTotales", {async: false});
			mostrarPagos();
			showHidePestanas();
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
			var qw = " cIdDocumento = '" + $("#cContratoDefinitivo").val()+ "'";
			oTablePagos = $("#tblPagos").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "El contrato no tiene pagos registrados",
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
  <body >
	  <form id="formPayments">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Datos del Contrato Modificado</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoModificatorio.jsp?tab=1';" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblProcedimiento" id="lblProcedimiento" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						<input type="text" class="form-control transpInput" name="lblContrato" id="lblContrato" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput font-weight-bold" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalAnterior" id="lblTotalAnterior" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotalModificado" id="lblTotalModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col-4">
						<input type="text" class="form-control transpInput" name="lblTotal" id="lblTotal" readonly />
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Pagos</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblPagos" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
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
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
	    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />			
	    <input type="hidden" name="nIdEstado" id="nIdEstado" />
	    <input type="hidden" name="tipoMod" id="tipoMod" />
	    
	    <input type="hidden" name="cIdUsuarioAprobacion" id="cIdUsuarioAprobacion" value="<%=usuarioTab.getLogin()%>"/>
	    <input type="hidden" name="cIdUsuarioAnulacion" id="cIdUsuarioAnulacion" value="<%=usuarioTab.getLogin()%>"/>	
	</form>
  </body>
</html>
