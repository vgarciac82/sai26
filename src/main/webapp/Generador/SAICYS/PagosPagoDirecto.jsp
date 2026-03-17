<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map<String, Role> rol =usuario.getRoles();
    String cIdDocumento = "";
    String cEjercicio = "";
    String cCentroContable = "";
    String cIdUnidadEjecutora = "";
    
     if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		
	if (session.getAttribute(GestionInterface.ATT_PagoDirectoFolio) != null) {
		cIdDocumento = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoFolio);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoEjercicio);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoUE);
	}else 
		response.sendRedirect("PagoDirecto.jsp?tab=1");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>Pagos Pago Directo</title>
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
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker-es.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/validaciones.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
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
				Map botones=nb.getBotones(roles,"PagosDirectos","PagosPagoDirecto");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
		});
		
		function init(){
			$("#cIdFolio").val("<%=cIdDocumento%>");
			$("#cCentroContable").val("<%=cCentroContable%>");
			$("#cIdUnidadEjecutora").val("<%=cIdUnidadEjecutora%>");
			queryFormPost("mPagoDirectoRead",{async:false});
			queryFormPost("mPagoDirectoMontos",{async:false});
			
			if($("#lblMotivoRechazo").val() != ""){
				$("#trMovitoRechazo").css("display","");
				$("#divMotivoRechazo").html($("#lblMotivoRechazo").val());
			}
			else
				$("#trMovitoRechazo").css("display","none");
			$("#lblImporteBruto").val($("#Imp_Bruto").val());
			$("#lblIVA").val($("#Imp_Iva").val());
			$("#lblImporteNeto").val($("#Imp_Neto").val());
			$("#lblImporteBruto").formatCurrency();
			$("#lblIVA").formatCurrency();
			$("#lblImporteNeto").formatCurrency();
			mostrarPagos();
		}
		
		
		function mostrarPagos () {
			var qw = " cIdDocumento='"+$("#cIdFolio").val()+"' "+ " AND cUnidadEjecutora='" +$("#cIdUnidadEjecutora").val() +"' AND tipoDocto='Pago Directo'";
		
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
<body>
<form>
	<table width="100%" align="left">
		<tr>
			<td>
				<fieldset>
					<legend>Datos Pago Directo</legend>
					<table align="left" cellpadding="2" width="100%">
						<tr align="left">
							<td>
								<input type="text" id="lblUnidadEjecutora" name="lblUnidadEjecutora" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								No. Folio:&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="lblFolio" id="lblFolio" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="text" id="lblEstado" name="lblEstado" style="border-width:0; background-color:transparent;width: 400px" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Fecha Recepci&oacute;n:<input type="text" id="lblFechaRecepcion" name="lblFechaRecepcion" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe Bruto:<input type="text" id="lblImporteBruto" name="lblImporteBruto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								IVA:<input type="text" id="lblIVA" name="lblIVA" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe Neto:<input type="text" id="lblImporteNeto" name="lblImporteNeto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
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
	<input type="hidden" id="cCentroContable" name="cCentroContable" />
	<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=cEjercicio %>"/>
	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
	<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>" />
	<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion"/>
	<input type="hidden" id="cIdFolio" name="cIdFolio" />
	<input type="hidden" id="Imp_Bruto" name="Imp_Bruto" />
	<input type="hidden" id="Imp_Iva" name="Imp_Iva" />
	<input type="hidden" id="Imp_Neto" name="Imp_Neto" />
</form>
</body>
</html>
