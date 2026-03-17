<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String cUR = "";
	String cIniciaEstatus = "NO";
	String cDevuelveConsolidada = "NO";

	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	String cLogin = "";

	cUR = usuario.getU_UR();
	cLogin = usuario.getLogin();
	
	String msg = "";
	
	if( session.getAttribute("RESULT") != null ){
		msg = (String)session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("INICIAESTATUS")) {
		cIniciaEstatus = usuario.getPropiedad("INICIAESTATUS").getValor();
	}

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("DEVUELVECONSOLIDADA")) {
		cDevuelveConsolidada = usuario.getPropiedad("DEVUELVECONSOLIDADA").getValor();
	}

	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean esSAIAlterno = "true".equals(cabl.getSystemSetting("SAI_AMBIENTAL"))
			|| "true".equals(cabl.getSystemSetting("SAI_FONDEN"));
	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	String aEjercicioFiscal = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Layout Relación de Gastos</title>

		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
			
		<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

		<script type="text/javascript" charset="utf-8">
		
		var esSAIAlterno = <%=esSAIAlterno%>;
		var msg = "<%=msg%>";
		var modalCuentaBancaria;
		var modalCuentaBancariaUEjecutora;
		var modalFecha;
		var modalFechaUEjecutora;
		var modalLeyenda;
		var modalLeyendaUEjecutora;
		var modalFolioSICOP;
				
			function inicio(){
				if ("<%=cIniciaEstatus%>" == "NO") {
					$("#pbStatusInicial").attr('disabled', true);
					$("#pbStatusInicial").hide();
					//pbStatusInicial.style.visibility="hidden";
				}
				
				if ("<%=cDevuelveConsolidada%>" == "NO") {
					$("#btnDevuelveConsolidada").attr('disabled', true);
					$("#btnDevuelveConsolidada").hide();
				}
										
				document.getElementById('archivo').value = "";
				document.getElementById('sDataH').value = "";
				document.getElementById('sDataHCB').value = "";
				document.getElementById('sDataHFecha').value = "";
				document.getElementById('sDataFolios').value = "";
				document.getElementById('sDataHComp').value = "";
				//alert("Selecciona el tipo de Integración para mostrar las RG disponibles");
				
			}
			
			function formatCurrency(num) {
				num = num.toString().replace(/\$|\,/g, '');
				if (isNaN(num)) num = "0";
				sign = (num == (num = Math.abs(num)));
				num = Math.floor(num * 100 + 0.50000000001);
				cents = num % 100;
				num = Math.floor(num / 100).toString();
				if (cents < 10) cents = "0" + cents;
				for (var i = 0; i < Math.floor((num.length - (1 + i)) / 3); i++)
				num = num.substring(0, num.length - (4 * i + 3)) + ',' + num.substring(num.length - (4 * i + 3));
				//return (((sign) ? '' : '-') + '$' + num + '.' + cents);
				return (((sign) ? '' : '-') + num + '.' + cents);
			}
			
			$(document).ready(function(){
			
				$("#divTipoPresupuesto").hide();
				
				$("#tabs").tabs( {
					"show" : function(event, ui) {
								var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
								if ( oTable.length > 0 ) {
									oTable.fnAdjustColumnSizing();
								}
								
								if( ui.index == 1 ){
									//muestraBotonesIntegracion();
									$("#pbEnvia").hide();
									$("#btnLayoutBan").hide();	
									$("#btnLayoutCmp").css("visibility","visible");
								}
							},
							
				/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/			
					
				"select" : function(event, ui) {
							
								if( ui.index == 1 ){
									return enviar();// && validaTotalesGeneraSICOP();
								}
								
							}
				} );

				$("#btnDevuelveConsolidada").button();
				$("#pbStatusInicial").button();
				$("#pbEnvia").button();	
				$("#btnLayoutBan").button();	
				$("#btnLayoutCmp").button();		
				$("#generaLayoutIntBtn").button().click(
					function(){
						generaLayoutIntCompromiso();
						if( msg != ""){
							Swal.fire({ icon: "error",
										text: msg});							
						}
					}
				);
				
				$("#generaLayoutCompromiso").button().click(
					function(){
						reimprimeLayoutCompromiso();
						if( msg != ""){
							Swal.fire({ icon: "error",
										text: msg});
						}	
					}
				);
				
				$("#generaLayoutBancoCompromiso").button().click(
					function(){
						generaLayoutBancarioRGCompromiso();
						if( msg != ""){
							Swal.fire({ icon: "error",
										text: msg});
						}
					}
				);
				
				
				
				
				
				if( msg != ""){
					Swal.fire({ icon: "error",
								text: msg});
				}
				querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora", {async: false });
				querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora1", {async: false });
				querySelectPost("U_EJECUTORARead", "cIdUnidadEjecutora2", {async: false });

				querySelectPost("rCatalogoLeyendaRead", "cIdLeyenda", {async: false });
				querySelectPost("rCatalogoLeyendaRead", "cIdLeyendaUEjecutora", {async: false });
				queryFormPost("FechaInicialRead", {async: false });
				queryFormPost("FechaFinalRead", {async: false });

				$("#FechaInicial").val(moment().format('yyyy-01-01'));
				$("#FechaFinal").val(moment().format('yyyy-MM-DD'));
				$("#FechaProgramada").val(moment().format('yyyy-MM-DD'));

				modalCuentaBancaria = new bootstrap.Modal(document.getElementById('dialog-form'), 'data-bs-backdrop');
				modalCuentaBancariaUEjecutora = new bootstrap.Modal(document.getElementById('dialog-formUEjecutora'), 'data-bs-backdrop');
				modalFecha = new bootstrap.Modal(document.getElementById('dialog-formFecha'), 'data-bs-backdrop');
				modalFechaUEjecutora = new bootstrap.Modal(document.getElementById('dialog-formFechaUEjecutora'), 'data-bs-backdrop');
				modalLeyenda = new bootstrap.Modal(document.getElementById('dialog-formLeyenda'), 'data-bs-backdrop');
				modalLeyendaUEjecutora = new bootstrap.Modal(document.getElementById('dialog-formLeyendaUEjecutora'), 'data-bs-backdrop');
				modalFolioSICOP = new bootstrap.Modal(document.getElementById('dialog-CapturaFolio'), 'data-bs-backdrop');

				$( "#cIdUnidadEjecutora" ).val('<%=cUR%>');
			
				if(esSAIAlterno){
					querySelectPost("cUnidadEjecutoraVistasTesoreria", "cIdUnidadEjecutora", {async : false});
					$( "#cIdUnidadEjecutora" ).attr("disabled", false);
				}else{
					$( "#cIdUnidadEjecutora" ).attr("disabled", true);
				}
				
				$("#FechaProgramada").val($("#FechaInicial").val());
				
				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'),tipoIntegra, radicado);
				$("#FechaInicial1").val( $("#FechaInicial").val()); 
				$("#FechaFinal1").val( $("#FechaFinal").val() );
				
				$("#FechaInicial2").val( $("#FechaInicial").val());
				$("#FechaFinal2").val( $("#FechaFinal").val() );
				
				fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val().split('-').reverse().join('/'), $("#FechaFinal1").val().split('-').reverse().join('/'));			
				fnGridCompromisoLayout($("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/'));
				
				$("#tblCuentasBancarias tbody").click(function(event) {
						$(oTableCB.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('table-primary');
						});
						$(event.target.parentNode).addClass('table-primary');
						
					});							
				
				$( "#cIdUnidadEjecutora" ).change(function() 
				{
					tipoIntegra="";
					$("#chkIntegra").prop("disabled", true);
					$("#checkAll").prop("disabled", true);				
					//Desactiva el RadioButton seleccionado
					if ($("#integraprosub").is(':checked'))
						$("#integraprosub").attr("checked", false);
					else if ($("#integranomina").is(':checked'))
						$("#integranomina").attr("checked", false);
					else if ($("#integraempleado").is(':checked'))
						$("#integraempleado").attr("checked", false);

					fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'),tipoIntegra, radicado);
					
					//alert("Selecciona el tipo de Integración para mostrar las RG disponibles");	
					
				});
				
				$( "#cIdUnidadEjecutora1" ).change(function(){
					fnGridConLayout($("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val().split('-').reverse().join('/'), $("#FechaFinal1").val().split('-').reverse().join('/'));
				});
				
				$( "#cIdUnidadEjecutora2" ).change(function(){
					fnGridCompromisoLayout($("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/'));
				});
				
				
				$( "#chkIntegra" )
					.change(function() {
						var table = document.getElementById('dt_generados');
						var aTrs = $('#dt_generados').dataTable().fnGetNodes();
						
						if ($('#chkIntegra').is(':checked')) 
						{
							Swal.fire("¡ AVISO !", "La Integración suma los montos y aplica la transferencia a las cuentas de la Unidad Ejecutora.", "info" );
							
							$('#dt_paraEnvio').dataTable().fnSetColumnVis( 2, false );
							$('#dt_paraEnvio').dataTable().fnSetColumnVis( 3, false );
							$('#dt_paraEnvio').dataTable().fnSetColumnVis( 6, false );
							$('#dt_paraEnvio').dataTable().fnSetColumnVis( 8, false );
							$('#dt_paraEnvio').dataTable().fnSetColumnVis( 9, false );
							for ( var i=0; i<aTrs.length;  i++ )     
							{         
								var row= table.rows[i];
								
								table.rows[i+1].cells[6].innerHTML ="--";  
								table.rows[i+1].cells[8].innerHTML ="--";
								table.rows[i+1].cells[9].innerHTML ="--";
							}
						} else 
						{
							location.reload(true);
							
						}
					});	
					
					
					$("#checkAll").change(
						function(){
							
							var table = document.getElementById('dt_generados');
							var aTrs = $('#dt_generados').dataTable().fnGetNodes();
							var todos = false;
							
							if ($('#checkAll').is(':checked')){
								todos = true;						
								$("input:checkbox").attr('checked', 'checked');
							}else{
								$("input:checkbox").removeAttr('checked');
								$("#chkIntegra").attr('checked', 'checked');
							}
							
							/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/
							actualizaTotales();
								
						});
			});
			
			function Inicializa(){
				var vacio = true;
				try {
									
					var table = document.getElementById('dt_CuentaConLayout');
					var aTrs = $('#dt_CuentaConLayout').dataTable().fnGetNodes();
					var cCOLUMNALLAVE = 3;
					
					//$('#chkRFCValido').is(':checked')
					$('#sDataFolios').val("");
					for ( var i=1; i<=aTrs.length;  i++ )     
					{   
						var row= table.rows[i];
						var chkbox = row.cells[0].childNodes[0];
						//if ( $(aTrs[i]).hasClass('row_selected') )
						//alert(aTrs.length);
						if(null != chkbox && true == chkbox.checked)
						{ 							
							//var nTr = $('#dt_generados').dataTable().fnGetData(aTrs[i]);
							//alert("dentro del if");
							var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
							
							$('#sDataFolios').val($('#sDataFolios').val() + " " + caNoFolio + " ,");
														
							vacio = false;
						} 
					}
					
				if(!vacio){
					$.ajax({
						url: '../gstnmngr/RelacionGastosConLayout',
						type: 'post',
						dataType: 'json',
						data: { sDataFolios : $("#sDataFolios").val() },
						success: function(data) {
							Swal.fire({ icon: "success",
						 				text: "Se regresaron exitosamente."}).then (function() { location.reload();});
						}
					});
				}
					
				}catch(e) {
					Swal.fire({ icon: "error",
		 						text: e});
				}
				if(vacio){
					Swal.fire({ icon: "warning",
		 						text: "Debe marcar al menos una fila."});
					location.reload(true);
				}
			}
			
			function devuelveConsolidada(){
				var cont = 0;
				try {
					var table = document.getElementById('dt_CuentaConLayout');
					var aTrs = $('#dt_CuentaConLayout').dataTable().fnGetNodes();
					var columna= 11; //folio integracion

					$('#sDataFolios').val("");
					for ( var i=1; i<=aTrs.length; i++){
						var row= table.rows[i];
						var chkbox = row.cells[0].childNodes[0];
						if(null != chkbox && true == chkbox.checked){
							$('#sDataFolios').val(row.cells[columna].innerHTML);
							//$('#sDataFolios').val(caNoFolio);													
							cont++;
						}
					}
					if (cont!=1){
						if (cont==0)
							Swal.fire({ icon: "warning",
		 								text: "Debe seleccionar un registro."});							
						else
							Swal.fire({ icon: "warning",
 										text: "Para esta opción debe seleccionar un solo registro."});							
						return;
					}else{
						Swal.fire({
							  title: '¿Desea continuar?',
							  text: "Se regresara la Integracion: " + $('#sDataFolios').val(),
							  icon: 'warning',
							  showCancelButton: true,
							  confirmButtonColor: '#288BA8',
							  cancelButtonColor: '#e6e6e6',
							  confirmButtonText: 'Aceptar',
							  cancelButtonText: 'Cancelar'
							}).then((result) => {
							  if (result.isConfirmed) {
								  queryFormPost("updateDesconsolidar", {async: false });
									queryFormPost("estatusLayoutRGHeader_Update", {async: false });
									Swal.fire({ icon: "success",
												text: "Se ha devuelto la Integracion "+$('#sDataFolios').val()});							
									location.reload(true);
							  } 
							})						
					}
				}catch(e) {
					Swal.fire({ icon: "error",
								text: e});
				}
			}
			
			function fnClickAddRow(row) {
				var table2;
				try
				{
					if ($('#chkIntegra').is(':checked'))
					{
						var bExiste = false;
						var szUEjecutora = '';
						var table = document.getElementById('dt_paraEnvio');
						var aTrs = $('#dt_paraEnvio').dataTable().fnGetNodes();
						for ( var i=0; i<aTrs.length;  i++ )     
						{         				
							szUEjecutora = $.trim(table.rows[i+1].cells[0].innerHTML) ;
							if ($.trim(row.cells[1].innerHTML) == szUEjecutora)
							{
								bExiste = true;
								break;
							}
						}			
						if (bExiste)
						{
							var Monto = parseFloat($.trim(table.rows[i+1].cells[1].innerHTML).replace(/,/g,""));
							Monto = Monto + parseFloat(row.cells[5].innerHTML.replace(/,/g,""));
							
							
							table.rows[i+1].cells[1].innerHTML = formatCurrency(Monto);
						}
						else
						{
							table2 = $('#dt_paraEnvio').dataTable().fnAddData( [	
								row.cells[2].innerHTML,
								row.cells[3].innerHTML,
								row.cells[4].innerHTML,
								row.cells[5].innerHTML,
								row.cells[6].innerHTML,
								row.cells[7].innerHTML,
								row.cells[8].innerHTML,
								row.cells[9].innerHTML,
								0,
								row.cells[10].innerHTML
								] );		
						}
					}
					else
					{
						table2 = $('#dt_paraEnvio').dataTable().fnAddData( [	
								row.cells[2].innerHTML,
								row.cells[3].innerHTML,
								row.cells[4].innerHTML,
								row.cells[5].innerHTML,
								row.cells[6].innerHTML,
								row.cells[7].innerHTML,
								row.cells[8].innerHTML,
								row.cells[9].innerHTML,
								0,
								row.cells[10].innerHTML
								] );
					}
				}
				catch (ex)
				{Swal.fire({ icon: "error",
							 text: ex.message});
				}
			}
			
			function fnClickDellRows(){
				var table2 = $('#dt_paraEnvio').dataTable();
				table2.fnClearTable();
			}
			
			function enviar(){
				//validaTotalesGeneraSICOP();
				var vacio = true;
				/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/
				try {
				
					fnClickDellRows();
					var table = document.getElementById('dt_generados');
					var aTrs = $('#dt_generados').dataTable().fnGetNodes();
					var cCOLUMNALLAVE = 3;
					var cCUENTABANCARIA = 6;
					var cFECHAPROG = 8;
					var cLeyenda = 9;
					var cRFC = 4;
					
					document.getElementById('sDataH').value = " ";
					document.getElementById('sDataHCB').value = " ";
					document.getElementById('sDataHFecha').value = " ";
					document.getElementById('sDataHLeyenda').value = " ";
					document.getElementById('sDataHComp').value = " ";
					
					for ( var i=1; i<=aTrs.length;  i++ )     
						{   
							var row= table.rows[i];
							var chkbox = row.cells[0].childNodes[0];
							
							if(null != chkbox && true == chkbox.checked)
							{
								if (row.cells[cCUENTABANCARIA].innerHTML == "--" )
								{
									Swal.fire({ icon: "warning",
										 		text: "El RFC : " + row.cells[cRFC].innerHTML + " no tiene asignada Cuenta Bancaria"});									
									//location.reload(true);
									return false;
								}
								if (row.cells[cFECHAPROG].innerHTML == "--")
								{
									Swal.fire({ icon: "warning",
								 				text: "La Unidad : " + row.cells[1].innerHTML + " no tiene asignada Fecha Programada"});									
									return false;
								}
								if (row.cells[cLeyenda].innerHTML == "--")
								{
									Swal.fire({ icon: "warning",
						 						text: "La Unidad : " + row.cells[1].innerHTML + " no tiene asignada Leyenda"});									
									return false;
								}
								$("#pbEnvia").css("visibility","visible");
								$("#pbEnviaDocumentacion").css("visibility","visible");
								$("#pbEnviaDoc").css("visibility","visible");
								$("#btnLayoutBan").css("visibility","visible");

								var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; // el 11 corresponde al folio
								var caNoCuentaBancaria = row.cells[6].innerHTML; // el 11 corresponde al folio
								var caNoFecha = row.cells[8].innerHTML; // el 11 corresponde al folio
								var caNoLeyenda = row.cells[9].innerHTML;
								var cxp = row.cells[10].innerHTML;
								
								document.getElementById('sDataH').value += " " + caNoFolio + " ," ;
								document.getElementById('sDataHCB').value += " " + caNoCuentaBancaria + " ," ;
								document.getElementById('sDataHFecha').value += " " + caNoFecha + " ," ;
								document.getElementById('sDataHLeyenda').value += " " + caNoLeyenda + " ," ;
								document.getElementById('sDataHComp').value += " " + cxp + " ," ;
								
								vacio = false;
								fnClickAddRow(row);
							} 
						}
					
					//muestraBotonesIntegracion();
					$("#pbEnvia").hide();
					$("#btnLayoutBan").hide();	
					$("#btnLayoutCmp").css("visibility","visible");
						
				}catch(e) {					
					Swal.fire({ icon: "error",
 								text: e});
					return false;
				}
				
				if(vacio){
					Swal.fire({ icon: "warning",
								text: "Debe marcar al menos una fila."});					
					return false;
				}
				/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/
				return true;
				
			}
			
			function fnGuardaUE(){
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				for ( var i=0; i<aTrs.length;  i++ )     
				{         				
					var szValues = "'" + $.trim(table.rows[i+1].cells[2].innerHTML) + "', "; 
					szValues += " " + $.trim(table.rows[i+1].cells[3].innerHTML) + ", ";
					szValues += "'" + $.trim(table.rows[i+1].cells[4].innerHTML) + "', ";
					szValues += " " + $.trim(table.rows[i+1].cells[5].innerHTML).replace(",","") + ", ";
					szValues += "'" + $.trim(table.rows[i+1].cells[6].innerHTML) + "', ";
					szValues += "'" + $.trim(table.rows[i+1].cells[8].innerHTML) + "', ";
					szValues += " " + $.trim(table.rows[i+1].cells[9].innerHTML) + ", ";
					szValues += "'" + $.trim(table.rows[i+1].cells[10].innerHTML) + "', ";
					szValues += "'', ";		// strFolioInterno
					szValues += "0";
					
					var szTabla = "M_RGINTEGRACION";                                                                                         
					$.getJSON("../catalogos/InsertJson.jsp",{Tabla: szTabla, Param: szValues, MaxReg: 20, ajax: 'false'}, 
						function(j)
						{     

					});   					
				}			
			}

			function generar(){
				try 
				{
					Swal.fire({
						  title: '¿Ya genero el layout Bancario?',
						  text: "En caso afirmativo de click en Aceptar.\nCaso contrario de click en Cancelar.",
						  icon: 'warning',
						  showCancelButton: true,
						  confirmButtonColor: '#288BA8',
						  cancelButtonColor: '#e6e6e6',
						  confirmButtonText: 'Aceptar',
						  cancelButtonText: 'Cancelar'
						}).then((result) => {
						  if (result.isConfirmed) {
								var table = document.getElementById('dt_generados');
								var table2 = document.getElementById('dt_paraEnvio');
								var rowCount = table.rows.length;
								var rowCount2 = table2.rows.length;
								
								if (rowCount2 == 0)
								{
									Swal.fire({ icon: "warning",
												text: "Debe seleccionar al menos un pago."});							
									return;
								}
												
								if ($('#chkIntegra').is(':checked'))
								{
									fnGuardaUE();
									document.getElementById('archivo').value = "2";
								}
								else
								{
									document.getElementById('archivo').value = "1";
								}
								
								document.location.href='../gstnmngr/generaLayoutRelacionGastos';
								document.envioSICOP.submit();
								
								fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'),tipoIntegra, radicado);
								
								fnClickDellRows();	
								
								$("#pbEnvia").css("visibility","hidden");
								$("#btnLayoutBan").css("visibility","hidden");
						  } 
						})
					
				}
				catch(e) {
					Swal.fire({ icon: "error",
								text: e});
				}
			}
			
			function generaLayoutCompromiso(){
				try {
					Swal.fire({
						  title: '¿Esta seguro de integrar los folios seleccionados?',
						  icon: 'warning',
						  showCancelButton: true,
						  confirmButtonColor: '#288BA8',
						  cancelButtonColor: '#e6e6e6',
						  confirmButtonText: 'Aceptar',
						  cancelButtonText: 'Cancelar'
						}).then((result) => {
						  if (result.isConfirmed) {
							  var elementosIntegrar = document.getElementById('dt_paraEnvio');
								var totalIntegrados = elementosIntegrar.rows.length;
								
								if (totalIntegrados == 0)
								{
									Swal.fire({ icon: "warning",
												text: "Debe seleccionar al menos un pago"});							
									return false;
								}
												
								document.envioSICOP.action='../gstnmngr/generaLayoutRelacionGastosCompromiso';
								document.envioSICOP.method='POST';
								document.envioSICOP.submit();
								
								fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'),tipoIntegra, radicado);
								fnClickDellRows();
						  } 
						})					
				}
				catch(e) {
					Swal.fire({ icon: "error",
								text: e});
				}
			}
			
			function generarDocumentacion(){
				try {
					var table = document.getElementById('dt_generados');
					var table2 = document.getElementById('dt_paraEnvio');
					var rowCount = table.rows.length;
					var rowCount2 = table2.rows.length;
					
					if (rowCount2 == 0)
					{
						Swal.fire({ icon: "warning",
									text: "Debe seleccionar al menos un pago"});						
						return;
					}
					
					for(var i=0; i<rowCount; i++) {
						var row = table.rows[i];
						var chkbox = row.cells[0].childNodes[0];
						
						if(null != chkbox && true == chkbox.checked) {
							//quitando de la lista de compromisos los registros enviados a SICOP...
							table.deleteRow(i);
							// Dubois no puede borrar indices iguales de tablas diferentes, por eso lo comente
							//table2.deleteRow(i);
							rowCount--;
							i--;
							
							//table.deleteRow(i);
							//rowCount--;
							//alert(rowCount + "psc");
						}
					}
					document.getElementById('archivo').value = "2";
					document.location.href='../gstnmngr/generaLayoutRelacionGastos?archivo=2';
					document.envioSICOP.submit();
					
					//document.envioSICOP.submit();
					
				}catch(e) {
					Swal.fire({ icon: "error",
		  						text: e}); 
					
				}
			}
			
			function toggleReactivar(status) {
				$("input:checkbox").each( 
					function() {
						$(this).attr("checked",status.checked);
					}
				);
			}
			function convertirAFecha(string) {
				var date = new Date();
				mes = parseInt(string.substring(3, 5), 10);
				date.setMonth(mes - 1); //en javascript los meses van de 0 a 11
				date.setDate(string.substring(0, 2));
				date.setYear(string.substring(6, 10));
				return date;
			}
			
			function valFecha(object1) {
				var FechaIni = document.getElementById("FechaInicial").value;
				var FechaFin = document.getElementById("FechaFinal").value;
				
				if (object1.value != "") 
				{
				if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) 
				{
					Swal.fire({ icon: "warning",
		 						text: "La fecha incial no puede ser mayor a la fecha final."});    						
					document.getElementById("FechaFinal").value = document.getElementById("FechaInicial").value;
				}
				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val(), $("#FechaFinal").val(),tipoIntegra, radicado);
			}
			}
			
			function valFecha1(object1) {
				var FechaIni = document.getElementById("FechaInicial1").value;
				var FechaFin = document.getElementById("FechaFinal1").value;
				
				if (object1.value != "") {
				if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) {					
					Swal.fire({ icon: "warning",
 								text: "La fecha incial no puede ser mayor a la fecha final."});
					document.getElementById("FechaFinal1").value = document.getElementById("FechaInicial1").value;
				}
				
				fnGridConLayout( $("#cIdUnidadEjecutora1").val(), $("#FechaInicial1").val().split('-').reverse().join('/'), $("#FechaFinal1").val().split('-').reverse().join('/') );
				
			}
			}
			
			function valFecha2(object1) {
				var FechaIni = document.getElementById("FechaInicial2").value;
				var FechaFin = document.getElementById("FechaFinal2").value;
				
				if (object1.value != "") {
				if (convertirAFecha(FechaIni) > convertirAFecha(FechaFin)) {
					Swal.fire({ icon: "warning",
								text: "La fecha incial no puede ser mayor a la fecha final."});								
					document.getElementById("FechaFinal2").value = document.getElementById("FechaInicial2").value;
				}
				
				fnGridCompromisoLayout( $("#cIdUnidadEjecutora2").val(), $("#FechaInicial2").val().split('-').reverse().join('/'), $("#FechaFinal2").val().split('-').reverse().join('/') );
				
			}
			}
			
			function Verifica_Fecha(pstrFecha){
				// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				// :: Proposito : Verifica si pstrFecha, contiene un formato y fecha ::
				// ::             correcta, del tipo dd/mm/aaaa                      ::
				// :: Entradas  : pstrFecha, string a validar                        ::
				// ::                                                                ::
				// :: :
				// ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
				//alert('va');
				var strCharCorrectos = "0123456789/";
				var Fecha = pstrFecha;
				if (Fecha == "")
					return false;
			
				for (i=0; i < Fecha.length;i++)
					{   var car = Fecha.substr(i,1);
						if (strCharCorrectos.indexOf(car)==-1)
							{
							return false;
							}
					}
				
				/* ::::::::::::::::::::::::::::::::::::::::::::::
				Creamos un arreglo con los datos de la fecha 
				separados por la diagonal
				::::::::::::::::::::::::::::::::::::::::::::::
				*/ dd=0;
				mm=1;
				aaaa=2;
				ArrayFecha = Fecha.split("/");
				if(ArrayFecha.length!=3)
					return false;
			
				var Dia  = Number(ArrayFecha[0]);
				var Mes  = Number(ArrayFecha[1]);
				var Anno = Number(ArrayFecha[2]);
				var TemAno =String(ArrayFecha[2]);
				
				
				if (TemAno.length != 4 )  return false;
				if (Dia > 31 || Dia < 1 ) return false;
				if (Mes >12 || Mes < 1 )  return false;
				if (Mes == 4 || Mes == 6 || Mes == 9 || Mes == 11)
					{
					if (Dia > 30 ) return false;
					}	
				if ( Mes == 2 )
					{
					if ((Anno % 4)==0 )  /* Se verifica si el Anno es biciesto **/
						{ 
						if (Dia > 29 ) return false;				
						}
					else{
						if (Dia > 28) return false;
						}
					}
			
				return true;
		
			}
		
			function toggle(status) {
				$("input:checkbox").each( 
					function() {
						$(this).attr("checked",status.checked);
					}
				);
			}
			
			$(function() {													
							
			$( "#pbLeyenda" )
				.button()
				.click(function() {
					if (!fnValidaRowSel()){
						Swal.fire({ icon: "warning",
									text: "Debe primero seleccionar un renglon."});						
						return;
					}
					if ($('#chkIntegra').is(':checked'))
	           		{
						modalLeyendaUEjecutora.show();
	           		}					
					else{
						modalLeyenda.show();
					}
				});
				
			$( "#pbFechaProgramada" )
				.button()
				.click(function() {
					
					if (!fnValidaRowSel()){
						Swal.fire({ icon: "warning",
									text: "Debe primero seleccionar un renglon."});
						return;
					}
					
					if ($('#chkIntegra').is(':checked')){
						modalFechaUEjecutora.show();
					}
					else{
						modalFecha.show();
					}
					
				});
			
			function fnValidaRowSel(){
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var bSel = false;
				
				for ( var i=0; i<aTrs.length;  i++ )     
				{         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') )         
					{
						bSel = true; 
						break;
					}  						
				}
				return bSel;	
			}
				
			$( "#pbCuentasBancarias" )
				.button()
				.click(function() {
					
					if (!fnValidaRowSel())
					{						
						Swal.fire({ icon: "warning",
									text: "Debe primero seleccionar un renglon."});
						return;
					}								
					
					var oTableCBUE = $("#tblCuentasBancariasUEjecutora").dataTable({
						bPaginate : false,
						bInfo : false,
						bAutoWidth : true,
						bJQueryUI: true,
						bFilter : false,
						bSort : false,
						bInfo : false,
						bRetrive: true,
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
							oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
						},
						aaSorting: [[ 2, "asc" ]] ,
						aoColumns: [
							{ sName: "strUnidadEjecutora",	bSearchable: false,	bSortable: false },
							{ sName: "strRFC" },
							{ sName: "strNombreBeneficiario" },
							{ sName: "strClabe" },
							{ sName: "strTipoCuenta" }
						]
						
		        	});
					
					$("#tblCuentasBancariasUEjecutora tbody").click(function(event) {
						$(oTableCBUE.fnSettings().aoData).each(function (){
							$(this.nTr).removeClass('table-primary');
						});
						$(event.target.parentNode).addClass('table-primary');
						
					});
					
					if ($('#chkIntegra').is(':checked'))
					{			
						var table = document.getElementById('dt_generados');
						var aTrs = $('#dt_generados').dataTable().fnGetNodes();
						var strUE = "";
						strUE=$("#cIdUnidadEjecutora").val(); //URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO PARA MANDARLA A LA CONSULTA DE LAS CUENTAS BANCARIAS AL SER INTEGRACION
						$('#tblCuentasBancariasUEjecutora').dataTable().fnClearTable();
						
						if (strUE == "")
						{
							Swal.fire({ icon: "warning",
										text: "Debe primero seleccionar un renglon."});
							return;
						}	
						var szWhere = " strUnidadEjecutora = '" + strUE + "' "; 
						var szTabla = "UECUENTASBANCARIAS_LAYOUTRG";                                                                                         
						$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
							function(j)
							{     
								for (var i = 0; i < j.length; i++) 
								{
									$('#tblCuentasBancariasUEjecutora').dataTable().fnAddData([ 
										j[i].Col0 , 
										j[i].Col1 , 
										j[i].Col2 ,
										"<input type='text' id='strClabe" + i + "' name='strClabe' value='" + j[i].Col3 + "' readonly style='border-width:0; background-color:transparent'/>",
										j[i].Col4 
										]); 
								}
						});   				
						
						modalCuentaBancariaUEjecutora.show();
					}
					else
					{
						var table = document.getElementById('dt_generados');
						var aTrs = $('#dt_generados').dataTable().fnGetNodes();
						var cRFC = 4;
						var strRFC = "";
							
						for ( var i=0; i<aTrs.length;  i++ )     
						{         
							var row= table.rows[i];
							if ( $(aTrs[i]).hasClass('table-primary') )         
							{
								var nTr = $('#dt_generados').dataTable().fnGetData(aTrs[i]); 
								strRFC = $.trim(nTr[4]); // el 11 corresponde al folio
								break;
							}  						
						}
						$('#tblCuentasBancarias').dataTable().fnClearTable();
							
						var szWhere = " dRFC = '" + strRFC + "' "; 
						var szTabla = "BENEFICIARIOCUENTASBANCARIAS";                                                                                         
						$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: szWhere, MaxReg: 20, ajax: 'false'}, 
							function(j)
							{    
								for (var i = 0; i < j.length; i++) 
								{
									$('#tblCuentasBancarias').dataTable().fnAddData([ 
										"<input type='text' id='cBanco" + i + "' name='cBanco' value='" + j[i].Col1 + "'  style='width:55px; border-width:0; background-color:transparent'/>", 
										"<input type='text' id='dBanco' name='dBanco' value='" + j[i].Col6 + "' readonly style='width:130px; border-width:0; background-color:transparent'/>", 
										"<input type='text' id='cPlaza" + i + "' name='cPlaza' value='" + j[i].Col2 + "' readonly style='width:38px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='dCuentaBancaria" + i + "' name='dCuentaBancaria' value='" + j[i].Col3 + "' readonly style='width:100px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='dDigitoVerificador" + i + "' name='dDigitoVerificador' value='" + j[i].Col4 + "' readonly style='width:35px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='dSucursal' name='dSucursal' value='" + j[i].Col7 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='cStatusCuenta' name='cStatusCuenta' value='" + j[i].Col5 + "' readonly style='width:60px; border-width:0; background-color:transparent'/>",
										"<input type='text' id='dStatusCuenta' name='dStatusCuenta' value='" + ((j[i].Col5==1)? "Activo":"Inactivo") + "' readonly style='width:60px; border-width:0; background-color:transparent'/>"
										]); 
								}
						});   					
							
						modalCuentaBancaria.show();
					}
				});
			});
			
			var tipoIntegra="";//RN,RE,RG,RP,SU";//para que al caargar muestre todas las RG existentes o no muestre nada sin seleccionar antes el tipo de integracion
			var radicado = "N";
			
			function tipointegracion(){
				if	($("#chkIntegra").prop("disabled", true)){
					$("#chkIntegra").prop("disabled", false);
					$("#checkAll").prop("disabled", false);
					$("#chkIntegra").click();
					$("#chkIntegra").change();
				}
				tipoIntegra=$("input[name='rTipoIntegracion']:checked").val();
				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'),tipoIntegra, radicado);
			}
			
			function tipopresupuesto(){
				radicado = $("input[name='rTipoPresupuesto']:checked").val();
				fnGrid($("#cIdUnidadEjecutora").val(), $("#FechaInicial").val().split('-').reverse().join('/'), $("#FechaFinal").val().split('-').reverse().join('/'),tipoIntegra, radicado);
			}
			
			function generaLayoutBancario(){
				var folios = "";
				var ctaBancaria = "";
				var table = document.getElementById('dt_generados');			
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var cCOLUMNALLAVE = 3;
				var cCUENTABANCARIA = 6; 
				var j = 1;				
					
				for ( var i=1; i<=aTrs.length;  i++ ){   
					var row= table.rows[i];
					var chkbox = row.cells[0].childNodes[0];									
					
					if(null != chkbox && true == chkbox.checked){
						var caNoFolio = row.cells[cCOLUMNALLAVE].innerHTML; 
						var caNoCuentaBancaria = row.cells[6].innerHTML;					
						$("#folioRelGasto").val(caNoFolio);
						queryFormPost("esRelGastosLayoutBancarioRead", {async:false});
						
						if($("#esPagoLayoutBan").val() == "SI"){
											
							if(j==1){
								folios += caNoFolio ;
							}else{
								folios += ", " + caNoFolio ;
							}
							ctaBancaria = caNoCuentaBancaria;
							j++;
							$("#esPagoLayoutBan").val("");
						}									
					}	
					
				}
				
				if(folios == ""){
					Swal.fire({ icon: "info",
								text: "Los folios seleccionados no aplican para generar Layout."});
				}else{
					$("#esIntCompromiso").val("NO");
					$("#nFoliosLayout").val(folios);
					$("#sTipoLayout").val("RG");
					$("#sCuentaLayout").val(ctaBancaria);
					$("#layoutBanco").submit();
				}
			}
			
			/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/
			function actualizaTotales(){
				var suma = 0.00;
				$(".RG_SEL").each(
					function(){
						if( $(this).attr("checked") ){
							var sumando = buscaTotal( $(this).attr('id') );
							suma += sumando;
						}
				});
				$("#totalIntegrado").val( suma.toFixed(2) );
				$("#totalIntegrado").formatCurrency();
				
				validaTotalesCaptura();
			}
			
			/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/
			function buscaTotal( idInput ){
				var matrizVal = $("#dt_generados").dataTable().fnGetData();
				var val = 0.00;
				
				for( cnt = 0; cnt < matrizVal.length; cnt++){
					if( matrizVal[cnt][3] == idInput ){
						val = parseFloat( quitaFrmt( matrizVal[cnt][5] ) );
					}
				}
				return val;
			}
			
			/**
			 * Retira el formato monetario de una cadena;
			 * 
			 */
			function quitaFrmt(fld) {
				var valcol = fld.toString();
				valcol = valcol.replace(/[$]/g, "");
				valcol = valcol.replace(/,/g, "");
				return valcol;
			}
			
			/*VGC20180119 Cambios para generar layout de compromiso cuando la integradora suma mas de 300 UMAS*/
			function validaTotalesCaptura(){
			
				queryFormPost( {
					queryName:"montoMaximoPagoDirectoRead",
					async:false,
					callback:function(){
						var montoMaximo = parseFloat( $("#montoMaximo").val() );
						var montoTotal = parseFloat( quitaFrmt( $("#totalIntegrado").val() ) );
						
						if( montoTotal > montoMaximo ){
							$("#totalIntegrado").removeClass('black');
							$("#totalIntegrado").addClass('red');
						}else{
							$("#totalIntegrado").removeClass('red');
							$("#totalIntegrado").addClass('black');
						}
					}
				});
			}
			
			function validaTotalesGeneraSICOP(){
				var resultado = true;
				if(!esSAIAlterno){
				
					queryFormPost( {
						queryName:"montoMaximoPagoDirectoRead",
						async:false,
						callback:function(){
							var montoMaximo = parseFloat( $("#montoMaximo").val() );
							var montoTotal = parseFloat( quitaFrmt( $("#totalIntegrado").val() ) );
							
							if( montoTotal > montoMaximo ){
								Swal.fire({
									  title: '¿Desea continuar?',
									  html: "El monto total de las integraciones: " + $("#totalIntegrado").val() + " supera el limite para un pago sin compromiso.<br><br>Precione Aceptar para continuar generando compromiso.<br>Precione Cancelar para editar la integracion por un monto menor.",
									  icon: 'info',
									  showCancelButton: true,
									  confirmButtonColor: '#288BA8',
									  cancelButtonColor: '#e6e6e6',
									  confirmButtonText: 'Aceptar',
									  cancelButtonText: 'Cancelar'
									}).then((result) => {
									  if (result.isConfirmed) {
										  resultado = true;
									  } else {
										  resultado = false;
									  }									  								 
									  
									})
								//resultado =  confirm("El monto total de las integraciones: " + $("#totalIntegrado").val() + " supera el limite para un pago sin compromiso.\n\nPrecione Aceptar para continuar generando compromiso.\nPrecione Cancelar para editar la integracion por un monto menor." );	
							}
						}
					});
				}
				return resultado;
			}
			
			function muestraBotonesIntegracion(){
				
				queryFormPost( {
					queryName:"montoMaximoPagoDirectoRead",
					async:false,
					callback:function(){
						var montoMaximo = parseFloat( $("#montoMaximo").val() );
						var montoTotal = parseFloat( quitaFrmt( $("#totalIntegrado").val() ) );
						
						if( montoTotal > montoMaximo ){
							$("#pbEnvia").hide();
							$("#btnLayoutBan").hide();	
							$("#btnLayoutCmp").css("visibility","visible");
						}else{
							$("#pbEnvia").hide();
							$("#btnLayoutBan").hide();	
							$("#btnLayoutCmp").css("visibility","visible");
						} 
					}
				});
			}
			
			function generaLayoutBancarioRGCompromiso(){
			
				if(seleccionado<0){
					Swal.fire({ icon: "warning",
								text: "Debe seleccionar un renglon para generar su Layout bancario."});
					return false;
				}
						
				var selData = oTableCompRGLayout.fnGetData(seleccionado);
				var caNoCompromiso = selData[6];
				var ctaBancaria = selData[2];
				
				$("#caNoCompromisoTest").val(caNoCompromiso);
				queryFormPost("esRelGastosCompLayoutBancarioRead", {async:false});
				if( $("#esPagoLayoutBan").val() == "NO" ){
					queryFormPost("noAplicaRGLayoutBancario_Read", {async:false});
					//noAplicaRGLayoutBan					
					Swal.fire({ icon: "warning",
								text: "Las solicitudes por pagar: " + $("#noAplicaRGLayoutBan").val() + ". De la integracion seleccionada no aplican para generar Layout. Verifique ó devuelva la integracion. "});
					return;
				}
				
				$("#sTipoLayout").val("RG");
				$("#esIntCompromiso").val("SI");
				$("#caNoCompromisoRG").val(caNoCompromiso);
				$("#sCuentaLayout").val(ctaBancaria);
				$("#layoutBanco").submit();
			}
			
			function actualizar(){
				location.reload();
			}

			function aceptarFechaProgramada(){
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var cFechaProgramada = 8;
					
				for ( var i=0; i<aTrs.length;  i++ ){         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('table-primary') ){
							table.rows[i+1].cells[cFechaProgramada].innerHTML = $("#FechaProgramada").val().split('-').reverse().join('/');
							break;
						}	 							 						
					}
				modalFecha.hide();
			}
			
			function aceptarLeyenda(){
				var bExiste = false;
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				for ( var i=0; i<aTrs.length;  i++ ){         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') ){
						bExiste = true;
						break;
					}  						
				}
				if (! bExiste){
					Swal.fire({ icon: "warning",
								text: "Debe primero seleccionar un renglon."});
					return;
				}
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var cLeyenda = 9;
					
				for ( var i=0; i<aTrs.length;  i++ ){         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('table-primary') ){
							table.rows[i+1].cells[cLeyenda].innerHTML = $("#cIdLeyenda").val();
							break;
						} 	 						
					}
				modalLeyenda.hide();
			}
			
			function aceptarLeyendaUEjecutora(){
				var bExiste = false;
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				for ( var i=0; i<aTrs.length;  i++ )     
				{         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') )         
					{
						bExiste = true;
						break;
					}  						
				}
				if (! bExiste)
				{
					Swal.fire({ icon: "warning",
								text: "Debe primero seleccionar un renglon."});
					return;
				}
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var UnidadEjec = "";
				
				//URVP.12082014. SE COMENTARIZA YA QUE SE NECESITA LA UR QUE ESTA HACIENDO LA INTEGRACION Y NO LA DE LA RG SELECCIONADA
				UnidadEjec=$("#cIdUnidadEjecutora").val();//URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO PARA MANDARLA A LA CONSULTA DE LAS CUENTAS BANCARIAS
				
				for ( var i=0; i<aTrs.length;  i++ )     
					{         
							table.rows[i+1].cells[9].innerHTML = $("#cIdLeyendaUEjecutora").val(); 						
					}

				modalLeyendaUEjecutora.hide();	
			}
			
			function aceptarFechaProgramadaUEjecutora(){
				var strFechaProg = $('#FechaProgramadaUEjecutora').val() ;

				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var UnidadEjec = "";
				
				/* for ( var i=0; i<aTrs.length;  i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							UnidadEjec = table.rows[i+1].cells[1].innerHTML;
							break;
						}  						
					} */
				//URVP.12082014. SE COMENTARIZA YA QUE SE NECESITA LA UR DE LA UR QUE ESTA HACIENDO LA INTEGRACION Y NO LA DE LA RG SELECCIONADA
				UnidadEjec=$("#cIdUnidadEjecutora").val();//URVP.12082014. SE OBTIENE LA UR QUE ESTA EN EL COMBO
					
				for ( var i=0; i<aTrs.length;  i++ )     
					{         
						//if(table.rows[i+1].cells[1].innerHTML == UnidadEjec){ //URVP.12082014.SE COMENTARIZA YA QUE SE NECESITA TENER LA MISMA FECHA PROGRAMADA EN TODAS LAS RG INTEGRADAS
							table.rows[i+1].cells[8].innerHTML = $("#FechaProgramadaUEjecutora").val(); //}  						
					}
				
				modalFechaUEjecutora.hide();	
			}
			
			function aceptarCuentaBancaria(){
				var table = document.getElementById('tblCuentasBancarias');
				var aTrs = $('#tblCuentasBancarias').dataTable().fnGetNodes();
				var cCuentaBancaria = 4;
				var strCuentaBancaria = "";
				var bSel = false;

				for ( var i=0; i<aTrs.length;  i++ )     
				{         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') )         
					{
						var nTr = $('#tblCuentasBancarias').dataTable().fnGetData(aTrs[i]);
						
						strCuentaBancaria = $("#cBanco"+i).val();
						strCuentaBancaria += $("#cPlaza"+i).val();
						strCuentaBancaria += $("#dCuentaBancaria"+i).val();
						strCuentaBancaria += $("#dDigitoVerificador"+i).val();
						bSel = true;
						break;
					} 	 						
				}
				if (!bSel)
				{
					Swal.fire({ icon: "warning",
								text: "Advertencia...!\r\rNo ha seleccionado ninguna Cuenta Bancaria."});
					return;
				}
				
				// escribir la cuenta bancaria
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var cCuentaBancaria = 6;
					
				for ( var i=0; i<aTrs.length;  i++ )     
					{         
						var row= table.rows[i];
						
						if ( $(aTrs[i]).hasClass('table-primary') )         
						{
							table.rows[i+1].cells[6].innerHTML =strCuentaBancaria;
							break;
						}  						
					}
				
				modalCuentaBancaria.hide();
				
			}
			
			function aceptarCuentaBancariaUEjecutora(){
				var table = document.getElementById('tblCuentasBancariasUEjecutora');
				var aTrs = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetNodes();
				var cCuentaBancaria = 4;
				var strCuentaBancaria = "";
				var bSel = false;

				for ( var i=0; i<aTrs.length;  i++ )     
				{         
					var row= table.rows[i];
					
					if ( $(aTrs[i]).hasClass('table-primary') )         
					{
						var nTr = $('#tblCuentasBancariasUEjecutora').dataTable().fnGetData(aTrs[i]);
						
						strCuentaBancaria = $("#strClabe"+i).val();
						bSel = true;
						break;
					} 	 						
				}
				if (!bSel)
				{
					Swal.fire({ icon: "warning",
								text: "Advertencia...!\r\rNo ha seleccionado ninguna Cuenta Bancaria."});							
					return;
				}
				
				// escribir la cuenta bancaria
				var table = document.getElementById('dt_generados');
				var aTrs = $('#dt_generados').dataTable().fnGetNodes();
				var UnidadEjec = "";
				
			/* 	for ( var i=0; i<aTrs.length;  i++ )     
					{         
						if ( $(aTrs[i]).hasClass('row_selected') )         
						{
							UnidadEjec = table.rows[i+1].cells[1].innerHTML;
							break;
						}  						
					} */
			//URVP.12082014. SE COMENTARIZA YA QUE EN ESTE CASO NO SE NECESITA OBTENER LA UR A LA CUAL SE APLICARA LA CUENTA BANCARIA
					
				for ( var i=0; i<aTrs.length;  i++ )     
					{         
						//if(table.rows[i+1].cells[1].innerHTML == UnidadEjec){  //URVP.12082014. SE COMENTARIZA YA QUE NO NECESITA VALIDAR RENGLON POR UR, POR QUE TODA LA INTEGRACION TENDRA LA MISMA CUENTA BANCARIA
							table.rows[i+1].cells[6].innerHTML =strCuentaBancaria;//}  						
					}
					
				modalCuentaBancariaUEjecutora.hide();
					
			}

		</script>
	</head>
	<br/>
  	<body id="dt_example" onLoad="inicio();" >  	
		<div id="container" class="ms-5" class="container" style="width: 90%"><!--Inicia div container-->
			<form method="post" id="layoutBanco" name="layoutBanco" action="../gstnmngr/generaLayoutBancoRG">
				<input id="esIntCompromiso" name="esIntCompromiso" type="hidden" value="NO"/>
				<input id="nFoliosLayout" name="nFoliosLayout" type="hidden" value=""/>
				<input id="sTipoLayout" name="sTipoLayout" type="hidden" value=""/>
				<input id="sCuentaLayout" name="sCuentaLayout" type="hidden" value=""/>
				<input id="montoMaximo" type="hidden" value="0"/>
				<input id="cEjercicio" name="cEjercicio" type="hidden" value="<%=aEjercicioFiscal%>"/>
				<input id="caNoCompromisoTest" name="caNoCompromisoTest" type="hidden" value=""/>
				<input id="caNoCompromisoRG" name="caNoCompromisoRG" type="hidden" value=""/>
			</form>		
			
			<div class="card-header"> <h3> Layout Relación de Gastos </h3> </div>
			<hr class="mt-3"/>	                           
	            
			<div class="row d-flex justify-content-center"><!--Inicio Div row para iniciar los tabs-->
								
				<ul class="nav nav-tabs" id="list-opciones">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="tabs-1" onClick="actualizar();" data-bs-toggle="tab" data-bs-target="#tabs-1-layout" type="button" role="tab" aria-controls="tabs-layout" aria-selected="true">Pendientes de generar Layout</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-2" onClick="enviar();" data-bs-toggle="tab" data-bs-target="#tabs-2-enviar" type="button" role="tab" aria-controls="tabs-enviar" aria-selected="false">Pagos en proceso SICOP</button>
					</li>								     
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-3" data-bs-toggle="tab" data-bs-target="#tabs-3-generados" type="button" role="tab" aria-controls="tabs-generados" aria-selected="false">Layouts generados</button>
					</li>								     
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-4" data-bs-toggle="tab" data-bs-target="#tabs-4-compromiso" type="button" role="tab" aria-controls="tabs-compromiso" aria-selected="false">Layouts Compromisos RG</button>
					</li>								            
				</ul>
				 
				<div class="tab-content mt-3" id="tabContent"><!--Inicio div contenido tabs-->		
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout"><!--Inicio tab-1-->						
						<form><!--Inicio form tab-1-->
							<input id="u_Login" name="u_Login" type="hidden" value="<%=cLogin%>"> 
							<input type="hidden" id="esPagoLayoutBan" name="esPagoLayoutBan" />
							<input type="hidden" id="folioRelGasto" name="folioRelGasto" />
							<input type="hidden" id="cUResp" name="cUResp" class="paso01" value="<%=cUR%>"/>
							<input type="hidden" id="noAplicaRGLayoutBan" name="noAplicaRGLayoutBan" value=""/>
							
							<div class="row d-flex">								
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
									<label for="cIdUnidadEjecutora" class="form-label"> U. Ejecutora: </label>
									<select id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" class="form-select form-select-sm">
					            		<option value="<%=cUR%>"></option>
						            </select>
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
									<label for="FechaInicial" class="form-label"> Fecha Inicio: </label>
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
										<input onchange="valFecha(this)" name="FechaInicial" type="date" id="FechaInicial" class="form-control form-control-sm" size="10" />
									</div>						            
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
									<label for="FechaFinal" class="form-label"> Fecha Fin: </label>
									<div class="input-group">
										<span class="input-group date"><i class="datepicker1"></i></span>
										<input onchange="valFecha(this)" name="FechaFinal" type="date" id="FechaFinal" class="form-control form-control-sm" size="10" />
									</div>						            
								</div>
								
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									<br><input type="button" id="pbCuentasBancarias" style="visibility: hidden" value="Cuenta Bancaria" class="btn btn-primary" data-toggle="modal" data-target="#dialog-form"/>
								</div>							
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									<br><input type="button" id="pbFechaProgramada" style="visibility: hidden" value="Fecha Programada" class="btn btn-secondary" data-toggle="modal" data-target="#dialog-formFecha"/> 
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12">
									<br><input type="button" id="pbLeyenda" style="visibility: hidden" value="Leyenda" class="btn btn-dark data-toggle="modal" data-target="#dialog-formLeyenda"/>  
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12">	
									<label for="totalIntegrado" class="form-label"> Total Integrado: </label>
									<input name="totalIntegrado" type="text" id="totalIntegrado" class="form-control form-control-sm" readonly/>
								</div>							
							</div>

							<div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">									
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="checkbox" id="chkIntegra" name="chkIntegra" class="form-check-input" disabled/>&nbsp;<b>Aplicar Integración </b>
								</div>			
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">									
								</div>				
								
							</div>	

					        <h5> Tipo de Integración </h5>
							<hr class="mt-3"/>
					        
					        <div class="row d-flex">								
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="integraprosub" name="rTipoIntegracion" class="form-check-input" value="CPRP" onclick="tipointegracion()"> Proveedor
								</div>
								<!-- <div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="integranomina" name=rTipoIntegracion class="form-check-input" value="NORN" onclick="tipointegracion()"> Nomina 
								</div> -->
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="integraotros" name="rTipoIntegracion" class="form-check-input" value="Otros" onclick="tipointegracion()"> Otros
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="integraLaudosRetSICOP" name=rTipoIntegracion class="form-check-input" value="LaudosRetSICOP" onclick="tipointegracion()"> Laudos Ret. SICOP
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="integraLaudosCompleta" name="rTipoIntegracion" class="form-check-input" value="LaudosComp" onclick="tipointegracion()"> Laudos Completo
								</div>
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="radio" id="integraComprobaciones" name=rTipoIntegracion class="form-check-input" value="Comprobacion" onclick="tipointegracion()"> Comprobaciones
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="integraISNQ" name=rTipoIntegracion class="form-check-input" value="2NRQ" onclick="tipointegracion()"> ISN Queretaro
								</div>
								<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
									<input type="radio" id="integraIVA" name=rTipoIntegracion class="form-check-input" value="RIVA" onclick="tipointegracion()"> Ret. IVA
								</div>
							</div>
							
							<div id="divTipoPresupuesto">
								<h5 class="mt-3"> Tipo de Presupuesto </h5>
								<hr class="mt-3"/>
								
								<div class="row d-flex">						
									<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						        	</div>		
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<input type="radio" id="presupuestoDisp" name="rTipoPresupuesto" class="form-check-input" value="N" onclick="tipopresupuesto()" checked> Disponible Neto
									</div>
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<input type="radio" id="presupuestoRad" name=rTipoPresupuesto class="form-check-input" value="S" onclick="tipopresupuesto()"> Disponible Radicado
									</div>
								</div>
							</div>

							<div class="row d-flex">								
								<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
									<input type="checkbox" id="checkAll" name="checkAll" class="form-check-input" value="CPRP" disabled> Todos
								</div>
							</div>

							<jsp:include page="listaRelacionGasto.jsp"></jsp:include>
							
						</form><!--Form form tab-1-->	
					</div><!--Fin tab-1-->
					
					<div class="tab-pane fade show" id="tabs-2-enviar" role="tabpanel" aria-labelledby="tabs-enviar"><!--Inicio tab-2-->
						<h5> Integraci&oacute;n del layout para ser enviado a SICOP </h5>
						<hr class="mt-3"/>
							
						<jsp:include page="listaRelacionGastosEnviados.jsp"></jsp:include>
						
					</div><!--Fin tab-2-->

					<div class="tab-pane fade show" id="tabs-3-generados" role="tabpanel" aria-labelledby="tabs-generados"><!--Inicio tab-3-->
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">	
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
								<input type="hidden" id="cUResp1" name="cUResp1" class="paso01" value="<%=cUR%>"/>
								<label for="cIdUnidadEjecutora1" class="form-label"> U. Ejecutora: </label>
								<select id="cIdUnidadEjecutora1" name="cIdUnidadEjecutora1" class="form-select form-select-sm">
				            		<option value="<%=cUR%>"></option>
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<label for="FechaInicial1" class="form-label"> Fecha Inicio: </label>
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha1(this)" name="FechaInicial1" type="date" id="FechaInicial1" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<label for="FechaFinal1" class="form-label"> Fecha Final: </label>
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha1(this)" name="FechaFinal1" type="date" id="FechaFinal1" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>								
						</div>
												
						<br/>

						<div class="row d-flex">															
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<input type="button" id="pbStatusInicial" name="pbStatusInicial" onClick="Inicializa();" value="Devolver" class="btn btn-secondary btn-sm"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="btnDevuelveConsolidada" name="btnDevuelveConsolidada" onClick="devuelveConsolidada();" value="Devolver Integraci&oacute;n" class="btn btn-secondary btn-sm"/>
							</div>
						</div>
						 	
						<h5> Pagos enviados a SICOP </h5>
						<hr class="mt-3"/>

						<jsp:include page="listaRelacionGastosConLayout.jsp"></jsp:include>

					</div><!--Fin tab-3-->
										
					<div class="tab-pane fade show" id="tabs-4-compromiso" role="tabpanel" aria-labelledby="tabs-compromiso"><!--Inicio tab-4-->
						<div class="row d-flex">															
							
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">	
								<input type="hidden" id="cUResp2" name="cUResp2" class="paso04" value="<%=cUR%>"/>
								<label for="cIdUnidadEjecutora2" class="form-label"> U. Ejecutora: </label>
								<select id="cIdUnidadEjecutora2" name="cIdUnidadEjecutora2" class="form-select form-select-sm">
				            		<option value="<%=cUR%>"></option>
					            </select>
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<label for="FechaInicial2" class="form-label"> Fecha Inicio: </label>
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha2(this)" name="FechaInicial2" type="date" id="FechaInicial2" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
								<label for="FechaFinal2" class="form-label"> Fecha Final: </label>
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
									<input onchange="valFecha2(this)" name="FechaFinal2" type="date" id="FechaFinal2" class="form-control form-control-sm" size="10"/>
								</div>						            
							</div>								
						</div>	
						<div class="row d-flex">															
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="generaLayoutIntBtn" name="generaLayoutIntBtn" style="visibility: hidden" value="Generar Layout" class="btn btn-primary"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="generaLayoutCompromiso" name="generaLayoutCompromiso" value="Regenerar Layout Compromiso" class="btn btn-secondary"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="generaLayoutBancoCompromiso" name="generaLayoutBancoCompromiso" value="Regenerar Layout Bancario" class="btn btn-dark"/>
							</div>
						</div>
				
						<jsp:include page="listaCompromisoRGConLayout.jsp"></jsp:include>
				
					</div><!--Fin tab-4-->
				</div><!--Fin div contenido tabs-->
			</div><!--Fin Div row para iniciar los tabs-->

			
		</div><!--Fin div container-->
		
		<div class="modal fade" id="dialog-form" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
			<div class="modal-dialog"> <!-- Caja de dialogo -->
				<div class="modal-content"> <!-- Contenido de la caja -->
					  <div class="modal-header"> <!-- Encabezado de la caja -->
						<h5 class="modal-title">Selección de Cuenta Bancaria</h5>
						<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
					  </div>
					<div class="modal-body"> <!-- Cuerpo de la caja -->
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<table id="tblCuentasBancarias" class="table table-striped table-bordered">
									<thead>
										<tr>
											<th>Clave</th>
											<th>Banco</th>
											<th>Plaza</th>
											<th>No.Cuenta</th>
											<th>Dígito</th>
											<th>Sucursal</th>
											<th>cStatus</th>
											<th>Status</th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarCuenta" class="btn btn-primary btn-sm" onclick="aceptarCuentaBancaria();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
					</div>
				</div>
			  </div>		
		</div>
		
		<div class="modal fade" id="dialog-formUEjecutora" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
			<div class="modal-dialog modal-dialog modal-lg"> <!-- Caja de dialogo -->
				<div class="modal-content"> <!-- Contenido de la caja -->
					  <div class="modal-header"> <!-- Encabezado de la caja -->
						<h5 class="modal-title">Selección de Cuenta Bancaria por Unidad Ejecutora</h5>
						<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
					  </div>
					<div class="modal-body"> <!-- Cuerpo de la caja -->
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<table id="tblCuentasBancariasUEjecutora" class="table table-striped table-bordered">
									<thead>
										<tr>
											<th>UE</th>
											<th>RFC</th>
											<th>Nombre Beneficiario</th>
											<th>No.Cuenta</th>
											<th>Tipo Cuenta</th>
										</tr>
									</thead>
								</table>
							</div>
						</div>
					</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarCuentaUE" class="btn btn-primary btn-sm" onclick="aceptarCuentaBancariaUEjecutora();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
					</div>
				</div>
			  </div>		
		</div>		
		
		<div class="modal fade" id="dialog-formFecha" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
			<div class="modal-dialog"> <!-- Caja de dialogo -->
				<div class="modal-content"> <!-- Contenido de la caja -->
					  <div class="modal-header"> <!-- Encabezado de la caja -->
						<h5 class="modal-title">Selección de Fecha</h5>
						<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
					  </div>
					<div class="modal-body"> <!-- Cuerpo de la caja -->
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-4 col-md-3 col-sm-12 p-1">
								<label for="FechaProgramada" class="form-label"> Fecha Programada: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
										<input  name="FechaProgramada" type="date" id="FechaProgramada" class="form-control form-control-sm" />
								</div>						            
							</div>
						</div>
					</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarFecha" class="btn btn-primary btn-sm" onclick="aceptarFechaProgramada();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
					</div>
				</div>
			  </div>		
		</div>
		
		<div class="modal fade" id="dialog-formFechaUEjecutora" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
			<div class="modal-dialog"> <!-- Caja de dialogo -->
				<div class="modal-content"> <!-- Contenido de la caja -->
					  <div class="modal-header"> <!-- Encabezado de la caja -->
						<h5 class="modal-title">Selección de Fecha por Unidad Ejecutora</h5>
						<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
					  </div>
					<div class="modal-body"> <!-- Cuerpo de la caja -->
						<div class="row d-flex">								
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<label for="FechaProgramadaUEjecutora" class="form-label"> Fecha Programada: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">	
								<div class="input-group">
									<span class="input-group date"><i class="datepicker1"></i></span>
										<input  name="FechaProgramadaUEjecutora" type="date" id="FechaProgramadaUEjecutora" class="form-control form-control-sm" />
								</div>						            
							</div>
						</div>
					</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarFechaUE" class="btn btn-primary btn-sm" onclick="aceptarFechaProgramadaUEjecutora();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
					</div>
				</div>
			</div>		
		</div>
		
			
			<div class="modal fade" id="dialog-formLeyenda" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						<div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de Leyenda</h5>
							<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
						</div>
					<div class="modal-body"> <!-- Cuerpo de la caja -->
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<select id="cIdLeyenda" name="cIdLeyenda" class="form-select form-select-sm">
									<option value="Z1:"></option>
									<option value="Y1:"></option>
									<option value="X1:"></option>
									<option value="xx1" selected> -Leyenda- </option>
								</select>		            
							</div>
						</div>
					</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarLeyenda" class="btn btn-primary btn-sm" onclick="aceptarLeyenda();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
					</div>
					</div>
				</div>
			</div>
			
			<div class="modal fade" id="dialog-formLeyendaUEjecutora" tabindex="-1" aria-hidden="true" aria-labelledby="label-modal-1">
				<div class="modal-dialog"> <!-- Caja de dialogo -->
					<div class="modal-content"> <!-- Contenido de la caja -->
						<div class="modal-header"> <!-- Encabezado de la caja -->
							<h5 class="modal-title">Selección de Leyenda por Unidad Ejecutora</h5>
							<button class="btn-close" data-bs-dismiss="modal" aria-label="Cerrar"></button>			          		
						</div>
					<div class="modal-body"> <!-- Cuerpo de la caja -->
						<div class="row d-flex">								
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
								<select id="cIdLeyendaUEjecutora" name="cIdLeyendaUEjecutora" class="form-select form-select-sm">
									<option value="Z1:"></option>
									<option value="Y1:"></option>
									<option value="X1:"></option>
									<option value="xx1" selected> -Leyenda- </option>
								</select>		            
							</div>
						</div>
					</div>
					<div class="modal-footer"> <!-- Pie de pagina de la caja -->
						<button type="button" id="aceptarLeyendaUE" class="btn btn-primary btn-sm" onclick="aceptarLeyendaUEjecutora();" >Aceptar</button>
						<button type="button" class="btn btn-secondary btn-sm" data-bs-dismiss="modal">Cancelar</button>										    
					</div>
					</div>
				</div>
			</div>
						
		</div>
	</body>
			
</html>
