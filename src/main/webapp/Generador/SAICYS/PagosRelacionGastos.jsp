<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	 String user_login=usuario.getLogin();
    String roles="";
    Map<String, Role> rol =usuario.getRoles();
    String cEjercicio = "";
	String cIdDocumento = "";
	String nIdEstado = "";
	String cCentroContable="";
		
	if (session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio) != null) {
	  	cEjercicio =(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
		cIdDocumento= (String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
		nIdEstado=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEstado);
		}
			
	
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Pagos Relacion de Gastos</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
			@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "../css/demo_table_jui.css";
			@import "../css/demo_page.css";
	</style>
	<link href="../../css/interfaz.css" rel="stylesheet" type="text/css" />
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
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
	var importeTotalDocumento;
		$(document).ready(function() {
		
		
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"Relacion Gastos","PagosRelacionGastos");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
		
		
			queryFormPost("llenaCaratulaRelacionGastos", {async:false});
			queryFormPost("mContratoPagadoReadRelacionGastos", {async: false});
			queryFormPost("mContratoPorPagarReadRelacionGastos", {async: false});
			queryFormPost("checaMontoTotalLineas",{async:false});
			mostrarPagos();
			importeTotalDocumento=parseFloat(quitaFmt($("#mImporteNeto").val()));
			activaDesactivaPestanas();
		
		
		});
		
			function mostrarPagos () {
			
			var qw = " cIdDocumento='"+$("#cIdDocumentoCaratula").val()+"' "+ " AND cUnidadEjecutora='" +$("#cIdUnidadEjecutora").val() +"' AND tipoDocto='Relacion de Gastos'";
		
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
		
		
		
	function activaDesactivaPestanas(){
		var umbral;
	     //verificar que el monto ingresado por linea no sobrepase el total que se ingreso en la caratula
		 queryFormPost("verificaTotalFacturasRelacionGastosInicio", {async:false});
		 queryFormPost("checaMontoCapturadoFacturaDetalleTotal", {async:false});
		 queryFormPost("verificaMontosTotalesPartidasRELG", {async:false});
		  deshabilitaTabs();
		   if(parseFloat(quitaFmt($("#totalFactCapturadoInicio").val())) == parseFloat(importeTotalDocumento)){
		  if(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) == parseFloat(importeTotalDocumento)){
		    $("#partidasRelacionGastos").attr("disabled",false);
		   umbral = Math.abs(parseFloat(quitaFmt($("#mTotalFacturaDetalleTotal").val())) - parseFloat($("#totalSumaPartidasRELG").val()));
		    if(umbral > .03){
				$("#apartadoRelacionGastos").attr("disabled",true);
	            $("#pagosRelacionGastos").attr("disabled",true);
				}else
			    habilitaTabs();
		   
	     }  
		 }
	    
	}
		
	function deshabilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",true);
	$("#partidasRelacionGastos").attr("disabled",true);
	$("#pagosRelacionGastos")	.attr("disabled", true);
	}
		
	function habilitaTabs(){
	$("#apartadoRelacionGastos").attr("disabled",false);
	$("#partidasRelacionGastos").attr("disabled",false);
	$("#pagosRelacionGastos")	.attr("disabled",false);
	}		
		
	   function quitaFmt( val ) {
			if ( val.indexOf( "$" ) >= 0 )
		   		val = val.replace("$", "");
		   	while(val.indexOf( "," ) > 0)
		   		val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}	
		
		
	</script>
</head> 
<body id="dt_example" >
		<form>
			<fieldset>
				<legend>Informaci&oacute;n de la Relaci&oacute;n de Gastos</legend>
				<table border="0" align="center" width="100%">
					
					<tr align="left">
						<td colspan="2">
							<input name="lblcIdDocumento" id="lblcIdDocumento" type="text" style="width: 600px"  style="border: 0px solid black;" readonly="readonly"/>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" style="width: 600px" readonly="readonly" style="border: 0px solid black;"></input>  
						</td>								
					</tr>
					
					<tr align="left">
						<td colspan="2">
							<input name="lblnIdEstado" id="lblnIdEstado" type="text" readonly="readonly" style="border: 0px solid black; width: 45em;"></input>  
						</td>								
					</tr>
					<tr>
							<td align="left">
								Importe Neto Relacion Gastos:
								<input type="text" style="width: 500px" name="lblImporteNeto"
									id="lblImporteNeto" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					<tr>
							<td align="left">
								<!-- Importe Neto Partidas: -->
								<input type="hidden" style="width: 500px" name="montoNetoRELG"
									id="montoNetoRELG" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
								
					<tr>
							<td align="left" colspan="2">Total Pagado:$<input type="text" style="width:600px" name="lblTotalPagado" id="lblTotalPagado" readonly style="border-width:0; background-color:transparent"/></td>
						 </tr>		     
						 	     
						 	   
						 <tr>  
						<td align="left" colspan="2">Total Por Pagar:$<input type="text" style="width:500px" name="lblTotalPorPagar" id="lblTotalPorPagar" readonly style="border-width:0; background-color:transparent"/></td> 	   
						 </tr>	 
			
					
					</table>
				</fieldset>
					
					<br/><br/><br/>
					
					<table width="100%" align="left">
				<tr>
					<td>
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
	   
		<input id="cEjercicio" name="cEjercicio" type="hidden"  value="<%=cEjercicio%>" />
		<input id="cIdDocumentoCaratula" name="cIdDocumentoCaratula" type="hidden"  value="<%=cIdDocumento%>" />
		<input id="nIdEstadoCaratula" name="nIdEstadoCaratula" type="hidden" size="4" value="<%=nIdEstado%>" />
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=usuario.getU_UR() %>" />
		<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
		<input type="hidden" id="montoNetoRELG" name="montoNetoRELG" />
		<input id="cIdFolio" name="cIdFolio" type="hidden"  value="<%=cIdDocumento%>" />
		<input id="mTotalFacturaDetalleTotal" name="mTotalFacturaDetalleTotal" type="hidden"/>
		<input id="totalSumaPartidasRELG" name="totalSumaPartidasRELG" type="hidden"/>
		<input id="totalFactCapturadoInicio" name="totalFactCapturadoInicio" type="hidden"/>
		<input id="nFolioConsecutivoRelacionGastosCaratula" name="nFolioConsecutivoRelacionGastosCaratula" type="hidden"/>
		<input id="mImporteNeto" name="mImporteNeto" type="hidden"/>
		
		
			
		</form>				
	</body>
</html>
