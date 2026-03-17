<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);

if (usuario == null) {
	response.sendRedirect("../index.jsp");
	return;
}
String usu = usuario.getLogin();
String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
 %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
        
    <title>Genera Ejercido Pagado Integrado</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">

	<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "css/demo_table_jui.css";
		@import "css/demo_page.css";
	</style>
	<style media="all" type="text/css">     
		.alignRight { text-align: right; } 
		.alignCenter { text-align: center; }
	</style> 
	
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>	

  </head>
<script type="text/javascript">
$(document).ready(function () {

	$("#btnAceptar").click(function () { aceptar(); });
	$("#btnValidar").click(function () { validar(); });
	$("#btnAplicar").click(function () { aplicarCXPIntegracion(); });
	$("#tblText").hide();
	$("#btnNuevo").click(function () {location.reload();});
	$("#btnAplicarPagado").click(function () { aplicarPagado(); });
	$("#btnAplicarPagado").attr("disabled","disabled");
	$("#btnAplicar").attr("disabled","disabled");
	$("#btnValidar").attr("disabled","disabled");
	
	// dataTable CXP Enc
	
	var TableMovCXP = $('#movimientoCXP').dataTable({         
			
			bSortClasses: false,
			ScrollY: "500px",
			sScrollX: "1300px",
			bPaginate: false,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: true,
        	bInfo: false,
        	bAutoWidth: false,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			bScrollCollapse: true,
			//sScrollXInner: "100%",
			aaSorting: [[ 1, "asc" ]] ,
			bRetrive: true,
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
						sSearch: "Buscar:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
				},  
				aoColumnDefs:[{"bVisible":true, "aTargets":[1]},
								{"aTargets":[6], "sClass":"right"},
								{"sClass": "dt-center", "aTargets": ['dt-center']}
				
					//"width":"20px"
					//"sWidth":"20px"
				]
		});


	// dataTable SICOP		
		var TableMovSICOP = $('#movimientoSicop').dataTable({         
			ScrollY: "500px",
			sScrollX: "1300px",
			bPaginate: true,
        	bLengthChange: false,
        	bFilter: false,
        	bSort: false,
        	bInfo: false,
        	bAutoWidth: true,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			aaSorting: [[ 1, "asc" ]] ,
			sPaginationType: "full_numbers",
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
						sSearch: "Buscar:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
				}
			
		});
	
		$("#movimientoCXP tbody").dblclick(function(event) {
			if(confirm("¿ Desea Eliminar Cuenta Por Pagar ?")){
				var aPos = TableMovCXP.fnGetPosition( event.target.parentNode );
		     	var aData = TableMovCXP.fnGetData( aPos );
		     	TableMovCXP.fnDeleteRow( aPos);
		     }	
		});

});

function muestraDatosCxp(){
			
			$("#tblText").val("");
			$("#tblText").hide();
			
			var tipoDoc = $("#tipoDoc").val();
			var idCXP =  $("#bDatosCXP").val();
			var campos = "";
			var elParametro = "";	
			var szTabla = "";
			
			if((tipoDoc == "seleccione" || idCXP == "") && tipoDoc != "r_gastos"){
				
				$("#flcxp").val("");
				$("#flsicop").val("");
				$('#movimientoCXP').dataTable().fnClearTable();
				alert("Error. Obligatorio El Tipo de Documento y Folio Cuenta Por Pagar.");
				return;
				
			}else if(tipoDoc == "r_gastos" && $("#selectRelacion").val() != "seleccione" && ($("#tdRelacionGastosIntegracion").val() != "" || $("#tdRelacionGastosDiferentes").val() || $("#tdCXP").val() )){
				
				alert("Error. Falta Informacion Para Hacer Busqueda Relacion de Gastos");
				return;
				
			}else{
				
				$("#flcxp").val("");
				var datos = "sinDatos";
				
				if(tipoDoc == "p_directo"){
					
					campos = "caNoContrarrecibo,nFolioPagoDirecto,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fRecepcion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1),CONVERT(varchar,CONVERT(money,mImporteIVA),1),isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.040') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1),nFolioPolizaCancelacion";
					szTabla = "tPagoDirectoEncabezado";
					elParametro = "caNoContrarrecibo = '"+idCXP+"'";
					documento = "p_directo";
					cTipoPago = "PAGODIRECTO";
					
				}
				if(tipoDoc == "r_gastos"){
										
					campos = " caNoContrarrecibo,nFolioRELACIONGASTOS,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fRecepcion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteMasIva),1) AS importeIVA,'0.00' as Retenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion ";
					szTabla = "tRELACIONGASTOSEncabezado";
					elParametro = "caNoContrarrecibo = '"+idCXP+"'";
					documento = "r_gastos";
					cTipoPago = "RELACIONGASTOS";
										
				}if(tipoDoc == "p_diverso"){
					
					campos = "caNoContrarrecibo,nFolioPAGODIVERSO,(SELECT dNombre FROM tBeneficiario WHERE dRFC = RFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' as fRecepcion,CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIVA),1) AS importeIVA,isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.00') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion ";
					szTabla = "tPAGODIVERSOEncabezado";
					elParametro = "caNoContrarrecibo = '"+idCXP+"'";
					documento = "p_diverso";
					cTipoPago = "PAGODIVERSO";
					
				}if(tipoDoc == "p_obra"){
					
					campos = "caNoContrarrecibo,nFolioPAGOOBRA,(SELECT dNombre FROM tBeneficiario WHERE dRFC = RFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' AS fRecepcion, CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIVA),1) AS importeIVA,isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.00') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion ";
					szTabla = "tPAGOOBRAEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
					documento = "p_obra";
					cTipoPago = "PAGOOBRA";
					
				}if(tipoDoc == "nomina"){
					
					campos = "caNoContrarreciboCLC, nFolioNOMINACLC,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fAplicacion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1),'-','-',CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto, 0 nFolioPolizaCancelacion, 'S' AS sinAplicar "
					szTabla = "tNOMINACLCEncabezado";
					elParametro = "caNoContrarreciboCLC ='"+idCXP+"'";
					documento = "nomina";
					cTipoPago = "NOMINA";
					
				}if(tipoDoc == "federalizado"){
					
					campos = "caNoContrarrecibo,nFolioPAGOFEDERALIZADO,(SELECT dNombre FROM tBeneficiario WHERE dRFC = RFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' AS fRecepcion, CONVERT(varchar,CONVERT(money,mImporteBruto),1) AS importeBruto,CONVERT(varchar,CONVERT(money,mImporteIVA),1) AS importeIVA,isnull(CONVERT(varchar,CONVERT(money, CONVERT(money,mImporteRetencion) + (select mImportePenalizacion from tContrarrecibo where caNoContrarrecibo = '"+idCXP+"')),1),'0.00') as totalRetenciones,CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion";
					szTabla = "tPAGOFEDERALIZADOEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
					documento = "federalizado";
					cTipoPago = "FEDERALIZADO";
					
				}if(tipoDoc == "o_ajenas"){
					
					campos = "caNoContrarrecibo,nFolioOperAjenas,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIDRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),'-' AS fRecepcion, '-' AS importeBruto, '-' AS importeIVA, '-' AS totalRetenciones,CONVERT(varchar,CONVERT(money,mImportes),1) AS importeNeto,nFolioPolizaCancelacion ";
					szTabla = "tOperAjenasEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
					documento = "o_ajenas";
					cTipoPago = "AJENAS";
					
				}/*if(tipoDoc == "nomina"){
					
					campos = "caNoContrarrecibo,nFolioNOMINA,(SELECT dNombre FROM tBeneficiario WHERE dRFC = cIdRFC) AS Beneficiaro,CONVERT(varchar,fAplicacion,103),CONVERT(varchar,fRecepcion,103),CONVERT(varchar,CONVERT(money,mImporteBruto),1),'-','-',CONVERT(varchar,CONVERT(money,mImporteNeto),1) AS importeNeto,nFolioPolizaCancelacion";
					szTabla = "tNominaEncabezado";
					elParametro = "caNoContrarrecibo ='"+idCXP+"'";
					documento = "nomina";
					cTipoPago = "NOMINA";
					
				}*/
			}	
			
			//Verifica Si Existe En dataTable
			
			var existeCxp = existeEnDataTable();
			
			if(!existeCxp){
			
				//Verifica Si Existe CXP
				
				var nFolioPagado = "";
					$.ajax({
						
							url: './ejercidoPagadoValidar.jsp',
							type: 'post',
							async: 'true',
							// data:{tipo:'existe',nomCampoId:'caNoContrarrecibo',idCab:idCXP,tabla:'tEjercidoEncabezado'},
							data:{tipo:'existePagado',nomCampoId:'caNoContrarrecibo',idCab:idCXP,tabla:'tEjercidoEncabezado'},
							success:function(data){
								
								if(data.sinSesion == 'sinSesion'){
									
									location.href = "../index.jsp";
									
								}else if(data.estatus == "Pagado" ){
					            	
									alert("El Documento Con el Folio "+idCXP+" Esta Ejercido y Pagado");
									return;
									
					            }else{
					            	
					            	if(data.estatus == "Ejercido" ){
					            		
					            		var cxp = $("#bDatosCXP").val();
					            		
					            		if($("#estatusDocumento").val() == "nuevo"){
					            			
					            			alert("No se Pueden Agregar Cuentas Por Pagar Que Ya Estan Ejercidas");
					            			return;
					            		}else{
					            			$("#estatusDocumento").val(data.estatus);	
					            		}
										
					            		var zTabla = "TPAGADOENCABEZADO";
										var camposWhere = " WHERE cTipoPago = '" +cTipoPago+ "' AND caNoContrarrecibo IN ('"+cxp+"')"; 
										var param = "";
										var nFolioAplicarPagado = "";
										
										$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"TPAGADOENCABEZADO", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"false"}, function(j){ 
											
											for(var i = 0; i < j.length; i++ ){
												
												if($("#bDatos").val() == "" || $("#bDatos").val() == j[i].Col0 ){
													
													nFolioPagado = j[i].Col1;
													$("#bDatos").val(j[i].Col0);
													//nFolioAplicarPagado += j[i].Col1;
													//nFolioAplicarPagado += "/";
													
															$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParametro, MaxReg: "5", ajax: 'true'}, function(j){
																
																	for (var i = 0; i < j.length; i++) {
																										
																			$("#nFolio").val(j[i].Col1);
																			$("#fAplicacion").val(j[i].Col3);
																			datos = "datos";
																			
																			if(j[i].Col9 > 0){
																					alert("Documento Cancelado. Intente con otra Cuenta Por Pagar");
																					$('#movimientoCXP').dataTable().fnClearTable();
																			}
																			
																			$("#movimientoCXP").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8, 
																										'<td><input type="" name="documento" id="documento" value="'+documento+'" size=5 style="background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:center" readonly="readonly" ></td> ',
																										'<td><input type="" name="nFolioPagado" id="nFolioPagado" value="'+nFolioPagado+'" size=5 style="background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:center" readonly="readonly" ></td>'
																			]);
																	}
																				
																	if(datos == "sinDatos"){
																				
																			alert("El Documento Con el Folio "+idCXP+" No Existe.");
																			$("#flcxp").val("");
																			$("#btnValidar").removeAttr("disabled");
																			$("#btnAplicar").attr("disabled","disabled");
																			
																	}else{
																		
																			$("#btnValidar").removeAttr("disabled");
																			$("#btnAplicar").attr("disabled","disabled");
																			$("#nFolioAplicarPagado").val(nFolioAplicarPagado);
																			$("#btnAplicar").attr("disabled","disabled");
																			$("#btnValidar").attr("disabled","disabled");
																			$("#btnAplicarPagado").attr("disabled","disabled");
																			$("#bDatos").attr("disabled","disabled");
																			
																			alert("El Documento Con el Folio "+idCXP+" Esta Ejercido \n  Aplicara Pagado Con la CLC SICOP "+ $("#bDatos").val());
																			$("#btnValidar").attr("disabled","disabled");
																	}
															});
												}else{
													
													alert("La CLC Sicop No Es Igual a Las Otras Cuentas Por Pagar");
													return;
												}
											}
										});
										
					            	 }else{
					            	
					            		if($("#estatusDocumento").val() != "Ejercido" ){
					            			
					            				$("#estatusDocumento").val("nuevo");
					            				
								            	$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParametro, MaxReg: "5", ajax: 'true'}, function(j){
																
														for (var i = 0; i < j.length; i++) {
																						
																datos = "datos";
																$("#nFolio").val(j[i].Col1);
																$("#fAplicacion").val(j[i].Col3);
																													
																if(j[i].Col9 > 0){
																		alert("Documento Cancelado. Intente con otra Cuenta Por Pagar");
																		return;
																}
																
																$("#movimientoCXP").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8, 
																							'<td><input type="" name="documento" id="documento" value="'+documento+'" size=5 style="background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:center" readonly="readonly" ></td> ', 
																							'<td><input type="" name="nFolioPagado" id="nFolioPagado" value="'+nFolioPagado+'" size=5 style="background-color:#E0E0F8; border:1px solid #E0E0F8; text-align:center" readonly="readonly" ></td>' 
																]);
														}
																	
														if(datos == "sinDatos"){
																	
																alert("El Documento Con el Folio "+idCXP+" No Existe.");
																							
																$("#flcxp").val("");
																$("#btnValidar").removeAttr("disabled");
																$("#btnAplicar").attr("disabled","disabled");
														}else{
															
																$("#btnValidar").removeAttr("disabled");
																$("#btnAplicar").attr("disabled","disabled");
														}
												});
								         }else{
								        	 alert("Solo Cuentas Por Pagar Para Aplicar Pagado");
								        	 return;
								         }
						           }
					            	
								}
							}
					});	
			}else{
				alert("La Cuenta Por Pagar Ya Existe");
				return;
			}
}

function muestraDatosSicop(){
	
			var idclc = $("#bDatos").val();
			var datos = "sinDatos";
			var tipoDoc = $("#tipoDoc").val();
			var szTabla = "";
			var campos = "";
			
			if(tipoDoc == "nomina"){
				szTabla = "datossicop_nomina";
				campos = " AND NCLC_43 = '"+idclc+"' GROUP BY SICOP.NCLC_43,SIAFF.BENEFICIARIO,FECHA_APL,SICOP.FOLIO_SIAFF_112,FECHA_APLICACION,FECHA_PAGO,SICOP.TOTAL_DIVISA_104, SIAFF.ESTATUS_CLC, SIAFF.APLICACION_CONTABLE ";
			}else{
				szTabla = "datossicop";
				campos = " NCLC_43 = '"+idclc+"' GROUP BY SICOP.NCLC_43,SIAFF.BENEFICIARIO,FECHA_APL,SICOP.FOLIO_SIAFF_112,FECHA_APLICACION,FECHA_PAGO,SICOP.TOTAL_DIVISA_104,RETENCION.IMP_RETE_49, SIAFF.ESTATUS_CLC, SIAFF.APLICACION_CONTABLE";
			}
			var elParametro = "";
			$("#flsicop").val("");
			$('#movimientoSicop').dataTable().fnClearTable();
						
			//Verifica Si Existe
			
			$.ajax({
					url: './ejercidoPagadoValidar.jsp',
					type: 'post',
					data:{tipo:'existe',nomCampoId:'nFolioSICOP',idCab:idclc,tabla:'tEjercidoEncabezado'},
					success:function(data){
						if((data == true || data == 'true') && $("#nFolioAplicarPagado").val() == ""){
							alert("El Documento Con el CLC "+idclc+" Ya Fue Aplicado.");
							return;
						}else{
								$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Campos:campos, Param: elParametro, MaxReg: "5",ajax: 'true'}, function(j){
									
									for (var i = 0; i < j.length; i++) {
											
											datos = "datos";
											$("#fPago").val(j[i].Col5);
											$("#estatus_clc").val(j[i].Col9);
											$("#aplicacion_contable").val(j[i].Col10);
											
											aoColumns: [
												
												   { sName: j[i].Col0,	bSearchable: true,	bSortable: false, bVisible: false},
												   { sName: j[i].Col1,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col2,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col3,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col4,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col5,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col6,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col7,	bSearchable: false,	bSortable: false, bVisible: false},
												   { sName: j[i].Col8,	bSearchable: false,	bSortable: false, bVisible: false}
												   
											]
											
											$("#flsicop").val(j[i].Col0);
											$("#montoTotalSicop").val(quitaFmt(j[i].Col6));
											
											$("#movimientoSicop").dataTable().fnAddData( [j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col8]);
										}
										if(datos == "sinDatos"){
											
											alert("El Documento Con el Folio "+idclc+" No Existe.");
											$('#movimientoSicop').dataTable().fnClearTable();
											$('#movimientoSICOPDetalles').dataTable().fnClearTable();
											$("#flsicop").val("");
											$("#btnValidar").removeAttr("disabled");
											$("#btnAplicar").attr("disabled","disable");
											$("#btnAplicarPagado").attr("disabled","disabled");
											
										}else{
											
											if($("#nFolioAplicarPagado").val() == ""){
												
												$("#btnValidar").removeAttr("disabled");
												
											}else{
												
												$("#btnValidar").attr("disabled","disabled");
												
												if($("#flcxp").val() != "" && $("#flsicop").val() != "" && $("#estatusDocumento").val() == "Ejercido" ){
													
													$("#btnAplicarPagado").removeAttr("disabled","");
													
												}
											}
											$("#btnAplicar").attr("disabled","disabled");
										}		
									});
						}
					}	
				});	
}

function cambiaDoc(tipo){
	
		$("#flcxp").val();
		
		//Muestra tipo relacion de gastos.
		
		if(tipo == "r_gastos"){
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").hide();
			$("#selectRelacion").show();
		}else{
			$("#tdRelacionGastosIntegracion").hide();
			$("#tdRelacionGastosDiferentes").hide();
			$("#tdCXP").show();
			$("#selectRelacion").hide();
		}
}

function aceptar(){
	
	if(confirm("Agregar Todas Las Cuentas Por Pagar ?")){
		
			var table = document.getElementById("movimientoCXP");
			var count = table.rows.length;
			
			var caNoContrarrecibo = "";
			var nFolio = "";
			var importe = 0;
			var documentos = "";
			var row = 0;
			var montoTotalCXP = 0;
			var nFolioAplicarPagado = "";
			
			for(var i = 1; i < count; i++){
		
				row = table.rows[i];
				caNoContrarrecibo += row.cells[0].childNodes[0].toString() + "/";
				nFolio += row.cells[1].childNodes[0].toString() + "/";
				importe = row.cells[8].childNodes[0].toString();
				documentos += row.cells[9].childNodes[0].value + "/";
				nFolioAplicarPagado += row.cells[10].childNodes[0].value + "/";
				
				montoTotalCXP = Number(montoTotalCXP) + Number(quitaFmt(importe)) ;
				$("#cuantosRecibos").val(i);	
				
			}
			
			var tableSicop = document.getElementById("movimientoSicop");
			var countSicop = table.rows.length;
			var importeSicop = 0;
			
			for(var ii = 1; ii < count; ii++){
		
				row = table.rows[ii];
				importeSicop = row.cells[6].childNodes[0];
								
			}
			
				$("#flcxp").val(caNoContrarrecibo);
				$("#nFolioRecibos").val(nFolio);
				$("#tipoDocumentos").val(documentos);
				$("#montoTotalCXP").val(montoTotalCXP);
				$("#nFolioAplicarPagado").val(nFolioAplicarPagado);
				
			if( $("#flcxp").val() != "" && $("#flsicop").val() != "" && $("#estatusDocumento").val() == "Ejercido" ){
				
				$("#btnAplicarPagado").removeAttr("disabled","");
				
			}
		
	}else{
		$("#flcxp").val("");
		$("#montoTotalCXP").val("");
	}
}

function validar(){
	
	var centavo = 1.00;
	var montoTotalCXP = $("#montoTotalCXP").val();
	var montoTotalSicop = quitaFmt($("#montoTotalSicop").val());
		
	var montoTotalCXPMayor = Number(montoTotalCXP) + Number(centavo);
	var montoTotalCXPMenor = Number(montoTotalCXP) - Number(centavo);
	
	if( Number(montoTotalCXPMayor) >= Number(montoTotalSicop) && Number(montoTotalCXPMenor) <=  Number(montoTotalSicop) ){
		
		validarDatos();
		
	}else{
		alert("Encabezados incorrectos");
	}
}

function validarDatos(){
	
		$("#tblText").hide();
		$("#tblText").val("");
		
		var id_CXP = $("#flcxp").val();
		var id_CLC = $("#flsicop").val();
		var nFolios = $("#nFolioRecibos").val();
		var tipoDoc = $("#tipoDocumentos").val();
		
		var nomTabla = "", nomTablaDet = "";
		var nomCampos = "", nomCamposDet = "";
		var nomIdEnc = "", nomIdDet;
		
		var numCXP = $("#numCXP").val();
		var numSICOP = $("#numSICOP").val();
		//var tipoCxp = "";
		
		if($("#aplicacion_contable").val() == 0  ){
			alert("Este Documento No Puede Aplica Como Ejercido y/o Pagado Porque Esta Pendiente De Aplicacion En SIAFF");
			return
		}
		
		if(id_CXP != "" && id_CLC != ""){
				
				$("#esperar").attr("style","visibility=visible");
					
				getNextSequenceVal({seqName: "EJERCIDO_INTEGRACION" , async: false, callback: setSequenceValI});
				
				var nFolioIntegracion = $("#nFolioIntegracion").val();
				//Para las cuentas por pagar diferentes de relacion gastos integracion o diferentes.
				$.ajax({
						url: './ejercidoPagadoValidar.jsp',
						type: 'post',
						dataType: 'json',
						data: {
							tipo:'validarCXPIntegracion',
							idCXP:id_CXP, 
							idCLC:id_CLC, 
							nFolios:nFolios,
							tipoDoc:tipoDoc,
							nFolioIntegracion:nFolioIntegracion
						},
						success: function(data){
								
								document.getElementById("esperar").style.visibility = 'hidden';
								
								if(data.encaFalso == "encaFalso"){
									alert("Encabezados Incorrectos.");
								}else if(data.validacionCorrecta == "validacionCorrecta"){
									alert("La Validacion Es Correcta.");
									$("#btnAplicar").removeAttr("disabled");
									$("#btnValidar").attr("disabled","disabled");
									$("#tipoDoc").attr("disabled","disabled");
								}else if(data.diferenciaCentavo == "diferenciaCentavo"){
									alert("Diferencia De Un Centavo, Puede Aplicar");
									$("#btnAplicar").removeAttr("disabled");
									$("#btnValidar").attr("disabled","disabled");
									$("#tipoDoc").attr("disabled","disabled");
								}else if(data.error == "error"){
									alert("Error: no se hizo la comparacion, intente de nuevo");
								}else{
									alert("Detalles Incorrectos.");
									
									$("#txtDif").val(data.errorDetalles);
									$("#tblText").show();
								}
					 	}
			  	 	});
		
		}else{
			alert("Error: Falta Folio CXP o Folio Sicop para Validar");
			document.getElementById("esperar").style.visibility = 'hidden';
		}
}

function aplicarCXPIntegracion(){
	
		document.getElementById("btnAplicar").disabled = false;
		$("#esperar").attr("style","visibility=visible");
		
		$("#btnAplicar").attr("disabled","true");
		$("#tipoDoc").removeAttr("disabled");
		var tipoDoc = $("#tipoDocumentos").val();
		var id_CXP = $("#flcxp").val();
		var id_CLC = $("#flsicop").val();
		var nFolios = $("#nFolioRecibos").val();
		var fPago = $("#fPago").val(); //Variable para ingresar en el campo fAplicado y lo aplique en Pagado.
		var usuario = $("#usuario").val();
		var statusSiaff = $("#estatus_clc").val();
		
		var idfolioEjercido = "";
		var idfolioPagado = "";
		
		cuantos = $("#cuantosRecibos").val();
		for(var i = 0; i < cuantos; i++){
			
				getNextSequenceVal({seqName: "EJERCIDO" , async: false, callback: setSequenceValE});
				getNextSequenceVal({seqName: "PAGADO" , async: false, callback: setSequenceValP});
					
				idfolioEjercido += $("#nFolioEjercido").val()+"/";
				idfolioPagado += +$("#nFolioPagado").val()+"/";
		}
		$.ajax({
				url:'./ejercidoPagadoValidar.jsp',
				type: 'post',
				data:{tipo:'aplicarCXPIntegracion',
					  idCXP:id_CXP,
					  idCLC:id_CLC,
					  tipoDoc:tipoDoc,
					  nFolios:nFolios,
					  fPago:fPago,usuario:usuario,
					  idfolioEjercido:idfolioEjercido,
					  idfolioPagado:idfolioPagado,
					  statusSiaff:statusSiaff
					 },
				success: function(data){
					if(data.estatus == "correcto"){
						
						alert("Aplicado Correctamente");
						location.reload();
						
					}else{
						
						alert("No Se Aplico Correctamente");
						document.getElementById("esperar").style.visibility = 'hidden';
						document.getElementById("btnAplicar").disabled = false;
					}
				}
		});	
		
		$("#btnAplicar").removeAttr("disabled");
		
}

function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
}

function setSequenceValE(seqValue){
	
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioEjercido").val( seqValue );
}
function setSequenceValP(seqValue){
	
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioPagado").val( seqValue );
}

function setSequenceValI(seqValue){
	
		seqValue = seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioIntegracion").val( seqValue );
}

function aplicarPagado(){
		
		document.getElementById("btnAplicarPagado").disabled = true;
		$("#btnNuevo").attr("disabled","disabled");
		$("#esperar").attr("style","visibility=visible");
		var tipoDoc = $("#tipoDoc").val();
		var caNoContrarrecibo = $("#flcxp").val();
		var clcSicop = $("#flsicop").val();
		var nFolioAplicarPagado = $("#nFolioAplicarPagado").val();
		var fPago = $("#fPago").val(); //Variable para ingresar en el campo fAplicado y lo aplique en Pagado.
		
		if($("#estatus_clc").val() == "Pagada"){
			
				if(confirm("Esta seguro que desea Aplicar Pagado?")){
							
						$.ajax({
								url: './ejercidoPagadoValidar.jsp',
								type: 'post',
								data: {tipo:'aplicarContableVarios',tipoAplicacion:"PAGADO" , nFolio:nFolioAplicarPagado, fPago:fPago },
								success: function(data){
									
										if(data.estatus == "correcto"){
												
											alert("Aplicado Correctamente Pagado");
											location.reload();
													
										}else{
													
											alert("No Se Aplico Correctamente Intente Despues");
											document.getElementById("btnAplicarPagado").disabled = false;
											$("#btnNuevo").removeAttr("disabled","");
											document.getElementById("esperar").style.visibility = 'hidden';
										}
								}
						});
				}else{
					
					document.getElementById("btnAplicarPagado").disabled = false;
					document.getElementById("esperar").style.visibility = 'hidden';
					$("#btnNuevo").removeAttr("disabled","");
					return;
				}		
		}else{
			
			alert("No Esta Pagada En Siaff, Intente Despues");
			document.getElementById("btnAplicarPagado").disabled = false;
			$("#btnNuevo").removeAttr("disabled","");
			document.getElementById("esperar").style.visibility = 'hidden';
			location.reload();
	
		}
}
function existeEnDataTable(){
	
	var valor = false;
	var table = document.getElementById("movimientoCXP");
	var count = table.rows.length;
	var cxp = $("#bDatosCXP").val();
	var doc = $("#tipoDoc").val();
	var row = 0;
		
	for(var i = 1; i < count; i++){
		
		row = table.rows[i];	
		var caNoContrarrecibo = row.cells[0].childNodes[0].toString();
		
		if(cxp == caNoContrarrecibo ){
			
			valor = true;
			
		}
	}
	return valor;
}
</script>  
  <body id="dt_example">
  	<div  id="container">
			<h1>Ejercido Pagado Integracion<label style="font-size: 8pt"></label></h1>			
        	<div id="tabsl">    		
        		<table id="tblEjecidoPagado" align="center" width="1150px">	
        		<tr>
					<td>
						<label id="esperar" style="visibility:hidden">
								<div align="center">Espere por favor....
								  <img border="0" src="../imagenes/espera.gif" height="30">
								</div>
						</label>
						
						<fieldset>
							<table id="tblBoton" align="center" width="1150px">
								<tr>
									
									<td>FOLIO CUENTAS POR PAGAR</td><td><input type="text" id="flcxp" name="flcxp" size="70" readonly="readonly" ></td>
									<td>FOLIO SICOP</td><td><input type="text" id="flsicop" name="flsicop" size="15" readonly="readonly" ></td>
									<td><input type="button" id="btnValidar" name="btnValidar" value="Validar"></td>
									<td><input type="button" id="btnAplicar" name="btnAplicar" value="Aplicar"></td>
									<td><input type="button" id="btnAplicarPagado" name="btnAplicarPagado" value="Aplicar Pagado"></td>
									<td><input type="button" id="btnNuevo" name="btnNuevo" value="Nuevo"></td>
									
									<td><input type="hidden" id="fPago" name="fPago"></td>
									<td><input type="hidden" id="numCXP" name="numCXP"></td>
									<td><input type="hidden" id="numSICOP" name="numSICOP"></td>
									<td><input type="hidden" id="usuario" name="usuario" value="<%=usuario.getLogin()%>"></td>
									<td><input type="hidden" id="nFolioEjercido" name="nFolioEjercido"></td>
									<td><input type="hidden" id="nFolioPagado" name="nFolioPagado"></td>
									<td><input type="hidden" id="cuantosRecibos" name="cuantosRecibos"/></td>
									<td><input type="hidden" id="estatus_clc" name="estatus_clc" /></td>
									<td><input type="hidden" id="aplicacion_contable" name="aplicacion_contable" /></td>
									
									<td><input type="hidden" id="montoTotalCXP" name="montoTotalCXP"></td>
									<td><input type="hidden" id="montoTotalSicop" name="montoTotalSicop"></td>
									<td><input type="hidden" id="tipoDocumentos" name="tipoDocumentos"></td>
									<td><input type="hidden" id="nFolioRecibos" name="nFolioRecibos" value=""></td>
									<td><input type="hidden" id="nFolioIntegracion" name="nFolioIntegracion" value=""></td>
									
									<td><input type="hidden" id="estatusDocumento" name="estatusDocumento"></td>
									<td><input type="hidden" id="nFolioAplicarPagado" name="nFolioAplicarPagado" value="" /></td> 
									<!-- td><input type="text" id="fAplicacion" name="fAplicacion"></td>
									<td><input type="text" id="cEjercicio" name="cEjercicio"></td-->
									
								</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td>
						<fieldset>
							<legend>Cuenta Por Pagar, Detalle No Encontrado En Detalle SICOP</legend>
							<table id="tblText" name="tblText" align="center">
								<td><textarea rows="14" cols="115" id="txtDif" name="txtDif" style="font-size: 9pt; font-family: Arial" readonly="readonly"></textarea></td>
							
							</table>
						</fieldset>
					</td>	
				</tr>
        		<tr>	
					<td>
						<fieldset>
							<legend>Datos Cuentas Por Pagar</legend>
							<DIV>
								<table align="right">
									
									<td align="center">TIPO DE PAGO</td><td><select id="tipoDoc" name="tipoDoc" onchange="cambiaDoc(this.value)">
																<option value="seleccione">Seleccione</option>
																<option value="p_directo">Pago Directo</option>
																<option value="r_gastos">Relacion Gastos</option>
																<option value="p_diverso">Pago Diverso</option>
																<option value="p_obra">Pago de Obra</option>
																<option value="nomina">Nomina</option>
																<option value="federalizado">Federalizado</option>
																<option value="o_ajenas">Operaciones Ajenas</option>
															 </select>
									</td>
									<td id="tdCXP">BUSCAR POR CUENTA POR PAGAR<input type="text" name="bDatosCXP" id="bDatosCXP"></td><td><input type="button" id="btonBuscar" value="Buscar" onClick="muestraDatosCxp()"></td>
									<td><input type="button" name="btnAceptar" id="btnAceptar" value="Aceptar"></td>
								</table>
							</DIV>
								<table id="movimientoCXP" class="display" align="center" width="900px">
								
									<tbody>
										<thead>
											<tr align="center">
												
												<th>No CXP</th>
												<th>Folio</th>	
												<th>Beneficiario</th>
												<th>Fecha Aplicacion</th>
												<th>Fecha Registro</th>	
												<th>Importe Bruto</th>
												<th>Importe Iva</th>
												<th>Importe Retenciones</th>
												<th>Importe Neto</th>
												<th>Documento</th>
												<th>-</th>
											</tr>	
										</thead>							
									</tbody>
										
								</table>
						  </fieldset>	
					</td>
				</tr>
						
				<tr><td>&nbsp;</td></tr><tr><td>&nbsp;</td></tr>
				
				<tr>	
					<td>
						<fieldset>
							<legend>Datos SICOP</legend>
								<table align="right"> 
									<td>BUSCAR POR CLC SICOP</td><td><input type="text" name="bDatos" id="bDatos"><input type="button" id="btonBuscar" value="Buscar" onClick="muestraDatosSicop()"></td>
								</table>
								<table id="movimientoSicop" class="display" align="center">
											<thead>
												<tr align="center">
												    <th>No CLC SICOP</th>
													<th>Beneficiario</th>
													<th>Fecha Aplic. SICOP</th>	
													<th>No CLC SIAFF</th>	
													<th>Fecha Aplic. SIAFF</th>
													<th>Fecha Pago SIAFF</th>															
													<th>Importe Neto</th>
													<th>Retenciones</th>
													<th>Total Ejercido</th>
												</tr>	
											</thead>							
								  </table>
						  </fieldset>
					</td>
				</tr>
				<tr><td>&nbsp;</td></tr>
				
			</table>				
		</div>
	</div>	
  </body>
</html>
