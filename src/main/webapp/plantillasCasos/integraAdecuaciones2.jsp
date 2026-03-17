
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page language="java" import="java.util.*"contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>

<%
	int nFolio = 0;		
	int nConsecutivoSICOP = 0;
	int id_oper = -1;
	int cTipoExportascion = 0;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cAnioFiscal = "";
	boolean bIntegraGuardado=false;
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	String cUR = usuario.getU_UR();
	
	nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	//nConsecutivoSICOP = adecua.buscaConsecutivoSICOP(nFolio);
		
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();
			
	System.out.println ("" + id_oper);
 %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Integracion de adecuaciones</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>

<style type="text/css">
.notEditable {
	background-color: #CCCCCC;
	color: #000000;
}
</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<script type="text/javascript">
	function createInput(form, name, value){
		$('<input>').attr({
			type: 'hidden',
			name: name,
			value: value
		}).addClass('remove').appendTo('#' + form);
	}
	
	function creaCondicion(){
		var token = "";
		
		var datos = $("#grdIntegraAdecuacion").dataTable().fnGetData();
		var cond = "";
		
		if( datos && datos.length > 0 ){
		
			for( cnt = 0; cnt < datos.length; cnt++ ){
				cond += token + "'" + datos[cnt][0] + "'";
				token = ", ";
			}
			return "(" + cond + ")";
			
		}else
			return "";
	}
	
	$(document).ready(
			
			function() {
			
				querySelectPost("readCatUEIADE", "cUnidadResponsable", {
					async : false
				});
				
				$('input').each(function() {
					var readonly = $(this).attr("readonly");
					if (readonly && readonly.toLowerCase() !== 'false') {
						$(this).addClass("notEditable");
					}
				});				
						
				oTablePA = $('#grdIntegraAdecuacion').dataTable( );				
			
				$("#grdIntegraAdecuacion").dataTable(
					{
						"bPaginate": false,
						"bLengthChange": false,
						"bFilter": true,
						"bSort": false,
						"bInfo": false,
						"bAutoWidth": false,
						"sScrollY": 300,
						"bScrollCollapse": true,	
						"bJQueryUI": true,
						"bRetrive" : true,
						"bDestroy" : true,
						"sPaginationType": "full_numbers",
						oLanguage: {
							sProcessing: "Procesando...",
							sLengthMenu: "Mostrar _MENU_ registros",
							sZeroRecords: "No hay registros a mostrar",
							sEmptyTable: "No hay datos en la tabla",
							sLoadingRecords: "Cargando...",
							sInfo: "Registros _START_ al _END_ de _TOTAL_",
							sInfoEmpty: "Registro 0 al 0 de 0",
							sInfoFiltered: "(filtered from _MAX_ total entries)",
							sInfoPostFix: "",
							sInfoThousands: ",",
							sSearch: "Filtro:",
							oPaginate: {
								sFirst:    "Primero",
								sPrevious: "Ant.",
								sNext:     "Sigte.",
								sLast:     "&Uacute;ltimo"
							}
						}
					} );

				creaTablaBusqueda();

				$("#buscarDialog").dialog({
					autoOpen : true,
					width : 1200,
					heigth : 500,
					modal : true
				});
				
				$("#bBusca").button().click(function() {
					buscaAIntegrar();
				});

				$("#bIntegrar").button().click(function() {
					fnIntegrar();
				});
				
				$("#bExportar").button().click(function(){
					exportaIntegracion();	
					alert("entro para comparar si en consecutivo de sicop es en cero");					
					recarga();
					alert("Folio Layout SICOP" + nConsecutivoSICOP);								
				});
				
				$("#DPC_fFechaSicop").datepicker({
					showOn:"button",
					dateFormat:"dd/mm/yy",
					buttonImage:"../Generador/images/calendar.gif",
					buttonImageOnly:true
				});
		
				$("#DPC_fFechaMAP").datepicker({
					showOn:"button",
					dateFormat:"dd/mm/yy",
					buttonImage:"../Generador/images/calendar.gif",
					buttonImageOnly:true
				});
			});
						
			function muestraAdec(){
				creaTablaBusqueda();
				$("#buscarDialog").dialog({height:500, width:1200}).dialog('open');																
				parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_save").disabled=true;					
				$("#grdBuscaIntegracion").dataTable().fnClearTable();							
			}				
			
			function buscaAIntegrar(){
			 	var cDataQuery="";
			 	var cconcatenador=" AND ";
				if ($("#nivelAdecu").val() == "" && $("#tipoAdecu").val() == "" && $("#cUnidadResponsable").val() == "" ){
					alert("Es nesesario indicar alguno de los siguientes datos:Nivel, Tipo o Unidad.");
					return;
				} 
				if ($("#nivelAdecu").val() != ""){
					cDataQuery=" nNivel = '"+$("#nivelAdecu").val()+"' ";
				} 
				if ($("#tipoAdecu").val() != ""){
					if ($("#nivelAdecu").val() != "" ){
						cDataQuery+=cconcatenador;
					}
					cDataQuery+=" cTipoAdecuacion = '"+$("#tipoAdecu").val()+"' ";
				} 
				if ($("#cUnidadResponsable").val() != "" ){
					if ( ($("#nivelAdecu").val() != "" ) || ($("#tipoAdecu").val() != "")){
						cDataQuery+=cconcatenador;
					}
					cDataQuery+=" cUnidadResponsable = '"+$("#cUnidadResponsable").val()+"'";
				}
				
				creaTablaBusqueda(cDataQuery);
			
			}			
			
			function fnClickAddRowB(nFolioAdec, tipoAdec, nivelAdec, monto, usCreador, fAplicacion, unidadUsuario, descarta) {
				$('#grdIntegraAdecuacion').dataTable().fnAddData( [nFolioAdec, tipoAdec, nivelAdec, monto, usCreador, fAplicacion, unidadUsuario, descarta] );
			}
			
			function descartar(row){				
				var index = row.index();
				aTrs = oTablePA.fnGetNodes();
				index = row.index();
				oTablePA.fnDeleteRow( index );
				$(row).remove();
				return false;
			}
		
			function fnIntegrar(){
				//VGC Agregando el input con el folio de integracion, para envio al Servlet
				createInput("insertarAdecuaciones", "folioConsolidacion", "<%=nFolio%>");
				$( "#insertarAdecuaciones input" ).empty();
				//VGC Itera sobre los folios seleccionados para agregarlos al formulario y enviarlos al servlet para su insercion				
				$('#grdBuscaIntegracion input:checked').each(
					function(idx, elm){
					    //var A = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						var nFolioAdec = $(this).parent('td').parent('tr').find('td:eq(1)').html();
						var tipoAdec = $(this).parent('td').parent('tr').find('td:eq(2)').html();
					 	var nivelAdec = $(this).parent('td').parent('tr').find('td:eq(3)').html();
					  	var monto = $(this).parent('td').parent('tr').find('td:eq(4)').html();
					  	var usCreador = $(this).parent('td').parent('tr').find('td:eq(5)').html();
					  	var fAplicacion = $(this).parent('td').parent('tr').find('td:eq(6)').html();
					  	var unidadUsuario = $(this).parent('td').parent('tr').find('td:eq(7)').html();				  	
					  	var descarta = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Descartar Secuencia\" onClick=\"descartar($(this).parent('td').parent('tr'));\">";
					  	fnClickAddRowB(nFolioAdec,tipoAdec,nivelAdec,monto,usCreador,fAplicacion,unidadUsuario,descarta);
					  	
					  	createInput("insertarAdecuaciones", "folioAdecuacion", nFolioAdec);
					  	
				});
			
				//VGC Envia mediante ajax el formulario para insertar el detalle.
				$.ajax({
					type : "POST",
					url : "../adecuaciones/IADE",
					cache : false,
					async : false,
					data : $("#insertarAdecuaciones").serialize(),
					error : function(xhr, textStatus, errorThrown) {
						alert("Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown);
					},
					success : function(RS) {
						
						var exito = RS.success;
						if( "true" == exito){
							
							var nInsertados = parseInt( RS.data_1.result, 10 );
							
							$("#buscarDialog").dialog('close');
							$("#exportarDiv").css("visibility", "visible");
							parent.document.getElementById("pb_save").disabled=false;
							
							alert("Se insertaron exitosamente: " + nInsertados + " adecuaciones." );	
						}else
							alert( RS.data_1.result );
					}
				});
				
			}																				
				
			function onLoadPlantilla(){
				return true;
			}		
			
			function creaTablaBusqueda(condicion){
				if( !condicion )
					condicion = "1=2";
				
				var condicionFolios = creaCondicion();
				if( condicionFolios != "" )
					condicion += " AND nFolioAdecuacion NOT IN " + condicionFolios;
					
				$("#grdBuscaIntegracion").dataTable({
					"bProcessing": true,
					"bServerSide": true,
					"bDestroy": true,
					"bSort": true, 
					"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vAdecuacion&qw="+condicion,
					"bJQueryUI": true,
					"sScrollX": "1150px",
					//"sScrollXInner": "100%",
					"sScrollY": "190px",
					"bPaginate": false,
					"bAutoWidth": true,
					"bInfo": true,
					"aoColumns": [
						{ sName: "cDescartar"},
						{ sName: "nFolioAdecuacion"},
						{ sName: "cTipoAdecuacion" },
						{ sName: "nNivel" },
						{ sName: "mImporte"},
						{ sName: "cUsuarioCreador"},
						{ sName: "fAplicacion"},
						{ sName: "cUnidadResponsable"}
	    			]
				});			
			}
			
			function exportaIntegracion(){
				var cTipoExportascion=$("#exporta").val();
				var cyaEjecutado=$("#iEjecutoAntes").val();
				
				<% 
					if(session.getAttribute("objConsecutivoSICOP")!=null)					
						nConsecutivoSICOP = (Integer) session.getAttribute("objConsecutivoSICOP");
					System.out.println("Folio Layout SICOP" + nConsecutivoSICOP );
				%>
								
				if (<%=nConsecutivoSICOP%> == 0  && cTipoExportascion != 2){
					alert("Tipo de archivo: " + cTipoExportascion);
					if (cyaEjecutado == 0  && cTipoExportascion != 2){
						alert("Requiere Generar Primero el Layout SICOP.");
						return -1;
					}
				}
				
				document.getElementById("iEjecutoAntes").value = "1";
				if (cTipoExportascion== ""){					
					alert("Seleccione el tipo de Exportación");
					return -1;
				}
				
				document.forms.ExportaExcel.action="../gstnmngr/IntegraAdecuaLayoutSicop?exporta="+cTipoExportascion+"&nFolioIntegracion=" + $("#nFolioIntegracion").val()+ "&nConsecutivoSICOP="+$("#nConsecutivoSICOP").val() ;
				document.ExportaExcel.submit();								
				parent.document.getElementById("pb_cancel").disabled=false;
				parent.document.getElementById("pb_send").disabled=true;
				
			}						
		
			function recarga(){
				//alert("Se busdca el consecutivo");				
				<%
				//if (nConsecutivoSICOP == 0 && nFolio > 0){
					nConsecutivoSICOP = adecua.buscaConsecutivoSICOP(nFolio);
					System.out.println("Folio" + nConsecutivoSICOP);
				//}
				%>
				
				alert("Se busca el consecutivo" + <%=nConsecutivoSICOP%>);
				//return nConsecutivoSICOP;
				
			}
			
			function onPostSubmit(id_oper){
				return true;
			}
			
			function onPostSubmit(id_oper){
				var p = window.parent;
				var valida_campos = true;
				try{
					if (id_oper==1){
						parent.document.getElementById("pb_save").disabled=true;
						p.gestion.setFolio(get("nFolioIntegracion"));
						p.gestion.setOperador(get("OPERADOR"));
						p.gestion.setFechaDocumento("<%=today %>");
						p.gestion.setEjercicioFiscal("<%=cAnioFiscal %>");
						p.gestion.setConceptoMov("Adecuacion Presupuestal");
						p.gestion.setMoneda("MXP");
					}
					aplicaCont();
				}catch (e) {
					window.alert("onSubmit: Error: " + e.message);
					return false;
				}
				return valida_campos;
			}
</script>

</head>
<body id="dt_example">
	<form id="insertarAdecuaciones" action="../adecuaciones/IADE" method="post">
	</form>
	<form id="ExportaExcel" name="ExportaExcel" action="../gstnmngr/IntegraAdecuaLayoutSicop" method="POST" target="_blank"></form>
		<form id="formIntegracion" name="formIntegracion" method="post" action="integraAdecuaciones2.jsp?AgregaDatos=SI" >
			<input type="hidden" name="iEjecutoAntes" id="iEjecutoAntes" value="0" maxlength="2" size="2"/>
				<div id="container" class="container" style="width: 90%">
					<h1>Integración de Adecuaciones Presupuestarias</h1>											
						<table id="adecuaPresup" width="100%">
							<tr>
								<td width="50%" align="left">
									Folio Integración 
									<input type="text" name="nFolioIntegracion" id="nFolioIntegracion" value="<%=c.getFolio()%>" readonly="readonly" /> 
								</td>
								<td width="50%" align="right">
									No. Lauy Out SICOP 								
									<input type="text" id="nConsecutivoSICOP" value="<%=nConsecutivoSICOP %>" readonly="readonly" maxlength="10" size="6"/>								
								</td>							</tr>
						</table>				
	
					
					<div id="exportarDiv" style="visibility:hidden;"><!-- hidden/true ****************************-->
						<table align="center">
							<tr>
								<td>
									<select name="exporta" id="exporta">
										<option value="0"></option>
										<option value="1">Archivo Excel</option>
										<option value="2">Layout SICOP</option>
										<option value="3">Formato FAP01</option>
										<option value="4">Adecuaciones Integradas</option>
										<option value="5">Adecuaciones Integradas PDF</option>
									</select> 
									<input name="bExportar" id="bExportar" value="Exportar"  type="button"/>								
								</td>
							</tr>
						</table>
					</div>
					<div id="datosSicopMap" style="visibility:hidden;"><!-- hidden/true ****************************-->
						<table align="center">
							<tr>
								<td>
									N&uacute;mero de Autorizaci&oacute;n SICOP: 
									<input id="nNumSicop" name="nNumSicop" value="" maxlength="20" size="20" onChange="" />
								
									Fecha SICOP: 
									<input type="text" id="DPC_fFechaSicop" name="DPC_fFechaSicop" readonly="readonly" maxlength="10"  size="10" onchange="actualizaFechaSICOPMAPIADE()" />
								</td>
							</tr>
							<tr>
								<td>
									N&uacute;mero de Autorizaci&oacute;n MAP: 
									<input id="nNumMAP"  name="nNumMAP" value="" maxlength="20" size="20" onChange="" />
								
									Fecha MAP: 
									<input type="text" id="DPC_fFechaMAP"  name="DPC_fFechaMAP" readonly="readonly" maxlength="10" size="10"  onKeyDown="return false" onchange="actualizaFechaSICOPMAPIADE()" />
								</td>
							</tr>
							<tr>
								
								<td><input type="checkbox" name="cAutorizar" id="cAutorizar" value="Autorizar" onclick="marcaAutoriza();">Autorizar</td>
								<td><input type="checkbox" name="cCancelar"  id="cCancelar"  value="Cancelar"  onclick="marcaCancela();">Des-Integrar</td>
								
							</tr>
						</table>
					</div>
		
					<input name="bBuscaAdec" id="bBuscaAdec" value="Buscar Adecuaciones" type="button" onclick="muestraAdec()">
					<div id="divIntegraAdecuacion">
						<table cellpadding="2" cellspacing="0" id="grdIntegraAdecuacion" class="display" align="center">
							<thead>
								<tr>
									<!-- <th>Sec.</th> -->
									<th>Folio Adecuacón</th>
									<th>Tipo</th>
									<th>Nivel</th>
									<th>$ Monto</th>
									<th>Usuario Creador</th>
									<th>Fecha Aplicacion</th>
									<th>Unidad Usuario</th>
									<th style="text-align: center;">Descartar Secuencia</th>
								</tr>
							</thead>
						</table>
					</div>
				</div>
				<div id="buscarDialog" title="Buscar Adecuaciones">
					<fieldset>
						<legend>B&uacute;squeda de Adecuaciones para integrar</legend>
						<table align="center">
							<tr>
								<td align="right">Nivel de la adecuación:</td>
								<td align="left">
									<select id="nivelAdecu" name="nivelAdecu">
										<option value=""></option>
										<option value="1">Interna</option>
										<option value="2">Interna SICOP</option>
										<option value="3">Interna SHCP</option>
										<option value="4">Externa SHCP sin restricci&oacute;n</option>
										<option value="5">Externa SHCP con restricci&oacute;n</option>
									</select>
								    &nbsp;Tipo de Adecuación:
									<select id="tipoAdecu" name="tipoAdecu">
										<option value=""></option>
										<option value="Ampliación">Ampliación</option>
										<option value="Reducción">Reducción</option>
										<option value="Transferencia">Transferencia</option>
										<option value="Calendario">Calendario</option>
									</select>
								</td>
							</tr>
							<tr>
								<td align="right">Unidad Creadora:</td>
								<td align="left">
									<select id="cUnidadResponsable" name="cUnidadResponsable">
										
									</select></td>
								<td colspan="2">&nbsp;</td>
							</tr>
							<tr>
								<td colspan="4" align="center">
									<input name="bBusca" id="bBusca" value="Buscar" type="button"> 
									<input name="bIntegrar" id="bIntegrar" value="Integrar" type="button">
								</td>
							</tr>
						</table>
					</fieldset>
					<fieldset>
						<legend>Selecci&oacute;n de Adecuaciones</legend>
						<table id="grdBuscaIntegracion">
							<thead>
								<tr>
									<th>Sec.</th>
									<th>Folio Adecuacón</th>
									<th>Tipo</th>
									<th>Nivel</th>
									<th>$ Monto</th>
									<th>Usuario Creador</th>
									<th>Fecha Aplicacion</th>
									<th>Unidad Usuario</th>									
								</tr>
							</thead>
						</table>
					</fieldset>
				</div>
		</form>
</body>
</html>