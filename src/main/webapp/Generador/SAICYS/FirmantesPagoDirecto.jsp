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
    Map rol =usuario.getRoles();
    String cIdDocumento = "";
    String cEjercicio = "";
    String cCentroContableUsr = "";
    String cIdUnidadEjecutora = "";
    Calendar c = Calendar.getInstance();
  	int mesActual = c.get(Calendar.MONTH)+1;
    
     if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContableUsr = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
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
    <title></title>
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
			var  oTableReq;
			var oTable;
			
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
				Map botones=nb.getBotones(roles,"PagosDirectos","FirmantesPagoDirecto");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
			var roles ="<%=roles%>";
			mostrarFirmantes();
			mostrarCatalogo();
			$('#tblFirmantesPagoDirecto tr').live('dblclick', function() {         
				var oTableFir=$('#tblFirmantesPagoDirecto').dataTable();
				if ($("#nIdEstado").val() != "2" && $("#nIdEstado").val() != "3" &&  $("#nIdEstado").val() != "4" && $("#nIdEstado").val() != "5" &&
						(roles.indexOf("ADMIN_RECMAT")>= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
					$(this).addClass('row_selected');   
					alert(oTableFir);
					var anSelected = fnGetSelected(oTableFir);
					var aData = oTableFir.fnGetData(anSelected[0]);
					$("#nNumeroFirmante").val(aData[5]);
					//alert(aData[5]);
					$(oTableFir.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					//queryFormPost("mSolicitudFirmanteDelete", { async:false });
					queryFormPost("pa_mPagoDirectoFirmantesBorrarYRenumerar", {async: false});
					//oTable.fnClearTable(oTable);
					oTableFir.fnClearTable(oTableFir);
					//mostrarCatalogo();
					mostrarFirmantes();
				}
			});
			
			$('#tblCatalogoFirmantes tr').live('dblclick', function() {
				var oTable=$('#tblCatalogoFirmantes').dataTable();
				if ($("#nIdEstado").val() != "2" && $("#nIdEstado").val() != "3" &&  $("#nIdEstado").val() != "4" && $("#nIdEstado").val() != "5" &&
						(roles.indexOf("ADMIN_RECMAT")>= 0 || $("#cIdUnidadEjecutoraUsuario").val() == $("#cIdUnidadEjecutora").val())) {
						//se checa numero de firmantes
					queryFormPost("mPagoDirectoFirmantes", {async: false});
					if($("#nFirmantes").val() < 2) {        
						$(this).addClass('row_selected');   
						var anSelected = fnGetSelected( oTable );
						var aData = oTable.fnGetData(anSelected[0]);
						$(oTable.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('row_selected');
						});
						$("#nIdFirmante").val(aData[1]);
						queryFormPost("mPagoDirectoConsecutivo", {async: false});
						queryFormPost("mPagoDirectoFirmantesCreate", {async: false});
						//oTableReq.fnClearTable(oTableReq);
						mostrarFirmantes();
					} 
				}
			});
		});
			
		function init(){
			queryFormPost("EjercicioFiscalActvRead",{async:false});
			$("#cIdFolio").val("<%=cIdDocumento%>");
			$("#cIdUnidadEjecutora").val("<%=cIdUnidadEjecutora%>");			
			queryFormPost("mPagoDirectoRead",{async:false});
			queryFormPost("mPagoDirectoMontos",{async:false});
			queryFormPost("apartadoPagoDirectoAplicado",{async:false});
			queryFormPost("maximoPagoDirectoRead",{async:false});
			
			queryFormPost("mPagoDirectoFacturas",{async:false});
			
				$("#lblImporteBruto").val($("#Imp_Bruto").val());
				$("#lblIVA").val($("#Imp_Iva").val());
				$("#lblImporteNeto").val($("#Imp_Neto").val());
				$("#lblImporteBruto").formatCurrency();
				$("#lblIVA").formatCurrency();
				$("#lblImporteNeto").formatCurrency();
			
			if($("#nIdEstado").val()==5){
				//actualizar el usuario
				queryFormPost("updateOperadorCaso", {async:false});
			}
			
			$("#lblImporteFactura").formatCurrency();
			$("#lblImporteIvaFactura").formatCurrency();
			$("#lblImporteNetoFactura").formatCurrency();
			
			/* var importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
			var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));

			Imp_Neto=Imp_Neto.toFixed(2);
			importeNetoFactura=importeNetoFactura.toFixed(2);
			
			umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
		
			 if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
				$("#apartadoPagoDirecto").css("display", "block");
			}  else{
				
				$("#apartadoPagoDirecto").css("display", "none");				
			} */
			
			validaPestana(); 
		}

		function mostrarFirmantes () {
			var qw = " cEjercicio = '" + $("#cEjercicio").val() + "' AND cIdDocumento= '" + $("#cIdFolio").val() + "' AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val()+"'";
			oTableReq = $("#tblFirmantesPagoDirecto").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mPagoDirectoFirmantes&qw=" + qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 5, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdUnidadEjecutora" },
					{ sName: "nIdFirmante" },
					{ sName: "cNombre" },
					{ sName: "cPuesto" },
					{ sName: "cTipoFirmante" },
					{ sName: "nNumeroFirmante", bVisible:false }
				]
        	});
		}
		
		function mostrarCatalogo () {
			var qw = " cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'"; // = "(nIdFirmante NOT IN (SELECT nIdFirmante FROM dbo.mSolicitudFirmantes WHERE (cEjercicio = '" + $("#cEjercicio").val() + "') AND (cIdTipoSolicitud = '" + $("#cIdTipoSolicitud").val() +  "') AND (cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "') AND (nIdConsecutivo = " + $("#nIdConsecutivo").val() + "))) AND cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val() + "'";
			oTable = $("#tblCatalogoFirmantes").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCatalogoFirmantes&qw=" + qw,
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
		
		function validaPestana(){
			importeTotalDocumento=parseFloat(quitaFmt($("#lblImporteNetoFactura").val()));
			
			importeNetoFactura=Number(quitaFmt($("#lblImporteNetoFactura").val())); 
			var Imp_Neto=Number(quitaFmt($("#lblImporteNeto").val()));
	
			Imp_Neto=Imp_Neto.toFixed(2);
			importeNetoFactura=importeNetoFactura.toFixed(2);
			umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
	
			$("#folioPago").val($("#nFolioPagoDirecto").val());
			queryFormPost("obtenSubTotalAcumulado", {async:false});
			
			var mSubTotalAcumulado=Number(quitaFmt($("#mSubTotalAcumulado").val()));
			mSubTotalAcumulado=mSubTotalAcumulado.toFixed(2);
			umbral1 = Math.abs(parseFloat(quitaFmt($("#mSubTotalAcumulado").val())) - parseFloat(quitaFmt($("#lblImporteNetoFactura").val())));
			if(Number(mSubTotalAcumulado)==Number(importeNetoFactura) || umbral1 < .03 ){
			//	$("#apartadoPagoDirecto").css("display", "block");
			    $("#pagosPagoDirecto").css("display", "block");	
				$("#retencionesPagoDirecto").css("display", "block");
				$("#partidasPagoDirecto").css("display", "block");	
				if(Number(Imp_Neto)==Number(importeNetoFactura) || umbral < .03 ){
					$("#apartadoPagoDirecto").css("display", "block");
				}else{
					$("#apartadoPagoDirecto").css("display", "none");				
				} 
			}else{
				$("#apartadoPagoDirecto").css("display", "none");	
				$("#partidasPagoDirecto").css("display", "none");	
				$("#retencionesPagoDirecto").css("display", "none");
				$("#pagosPagoDirecto").css("display", "none");			
			}
			$("#mSubTotalAcumulado").formatCurrency();
		}
		
	</script>
</head> 
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0"	topmargin="0">
<form>
<div id="container" class="container" style="text-align: left">
	<table>
		<tr>
			<td>
				<fieldset style="width: 750px">
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
								Importe Bruto en facturas:<input type="text" id="lblImporteFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe IVA en facturas:<input type="text" id="lblImporteIvaFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr> 
						<tr align="left">
							<td>
								Importe Neto en facturas:<input type="text" id="lblImporteNetoFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblImporteBruto" name="lblImporteBruto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblIVA" name="lblIVA" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblImporteNeto" name="lblImporteNeto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td>
				<fieldset>
					<legend>Firmantes de la Requisici&oacute;n</legend>
						<table width="97%" align="left">
							<tr>
								<td>
									<table align="center" id="tblFirmantesPagoDirecto" width="100%" class="display">
							        	<thead>
							        		<tr>
							        			<th>Unidad Ejecutora</th>
							        			<th>#</th>
							        			<th>Nombre</th>
							        			<th>Responsabilidades</th>
							        			<th>Tipo</th>
							        			<th></th>
							        		</tr>
							        	</thead>
							        </table>
								</td>
							</tr>
						</table>
				</fieldset>
				<fieldset>
					<legend>Cat&aacute;logo de Firmantes</legend>
						<table width="97%" align="left">
							<tr>
								<td>
									<table align="center" id="tblCatalogoFirmantes" width="100%" class="display">
							        	<thead>
							        		<tr>
							        			<th>Unidad Ejecutora</th>
							        			<th>#</th>
							        			<th>Nombre</th>
							        			<th>Responsabilidades</th>
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
</div>

<!-- Campos configurables del modulo -->
<input type="hidden" id="modulo" name="modulo"  value="Pagos Directos"/>
<input type="hidden" id="TIPO_DOCTO" name="TIPO_DOCTO"  value="PAGODIRECTO"/>

<!-- Campos principales -->
<input type="hidden" id="nFolioPagoDirecto" name="nFolioPagoDirecto" />
<input type="hidden" id="cEjercicio" name="cEjercicio" />
<input type="hidden" id="cIdFolio" name="cIdFolio" />
<input type="hidden" id="Imp_Bruto" name="Imp_Bruto" />
<input type="hidden" id="Imp_Iva" name="Imp_Iva" />
<input type="hidden" id="Imp_Neto" name="Imp_Neto" />
<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
<input type="hidden" id="cCentroContableUsr" name="cCentroContableUsr" value="<%=cCentroContableUsr%>"/>
<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>"/>
<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuario.getU_UR()%>" />
<input type="hidden" id="nIdEstado" name="nIdEstado" />

<input type="hidden" id="nNumeroFirmante" name="nNumeroFirmante" />
<input type="hidden" id="nFirmantes" name="nFirmantes" />
<input type="hidden" id="nIdFirmante" name="nIdFirmante" />
<input type="hidden" id="folioPago" name="folioPago" />

<input type="hidden" id="mSubTotalAcumulado" name="mSubTotalAcumulado" />

</form>
</body>
</html>
