<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	String cUR = "";
	String cRamo = "";
	String cCentroContable="";

	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	boolean mntoCuentas=false;
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		mntoCuentas = "10".equals(usuario.getPropiedad("CCENTROCONTABLE").getValor());
		cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
	String u_login = usuario.getLogin();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Consulta de Ingresos / Egresos</title>

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
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
		<script type="text/javascript" src="js/jquery.ui.core.js"></script>
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
 		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

		<script type="text/javascript" charset="utf-8">
		var oTable;
		var breturnVal = false;
			
		$(document).ready(function() {
			
			$("#fechaCarga").datepicker({
				minDate: new Date(2012, 0, 1), maxDate: "+1Y", changeMonth: true, changeYear: true, showOn:"button",
				dateFormat : "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
			
			$("#fAutorizar").datepicker({
				minDate: new Date(2012, 0, 1), maxDate: "+1Y", changeMonth: true, changeYear: true, showOn:"button",
				dateFormat : "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
			
			$("#fAplicacion").datepicker({
				minDate: new Date(2012, 0, 1), maxDate: "+1Y", changeMonth: true, changeYear: true, showOn:"button",
				dateFormat : "dd/mm/yy",
				buttonImage: "images/calendar.gif",
				buttonImageOnly: true
			});
			
			$("input.AyudaSyC").subIniciaDlg(); 
    		$("input.autoCompletaSyC").subIniciaAutoCompleta();
    		   		
			$("#tBancosRDB").change(function(){ 
				querySelectPost("cuentasBancariasRBD", "cuentaBancaria", {async: false });  
			});
	
    		//querySelectPost("UnidadresponsableRead","cIdUnidadAdministrativa", {async: false });
			
    		queryFormPost("cEjercicioRead",{async: false });
    		
    		$("#cUnidadResponsable").val( "<%=cUR%>" );

			$("#tblIngresosEgresos tbody").click(function(event) {
					$(oTable.fnSettings().aoData).each(function (){
						$(this.nTr).removeClass('row_selected');
					});
					$(event.target.parentNode).addClass('row_selected');
				});

			$("#tblAct").dataTable({
					"iDisplayLength": 500,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false 
        	});
			
			oTable = $("#tblIngresosEgresos").dataTable({
				bAutoWidth : true,
				sScrollX: "100%",
				sScrollY: "500",
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
				bRetrive: true,
				bDestroy: true,
				bServerSide: true,
				bPaginate: false,
				bJQueryUI: true
        	});
			
		$(this).ajaxForm({
				dataType:  "json",
				success: formSubmited
			});
			
			$('#pbBuscar').button().click( function() {
					
					var szTemp = " ";

					var fecha = $("#fechaCarga").val();
					
					if(fecha != ""){ szTemp += "fechaCarga = '" + fecha + "'"} 
					
					if($("#tBancosRDB").val() != ""){ szTemp += " AND banco = '" + $("#tBancosRDB").val() + "'"}
					
					if($("#cuentaBancaria").val() != null && $("#cuentaBancaria").val() != "" ){  szTemp += " AND cuentaBancaria = '" + $("#cuentaBancaria").val() + "'"; }
					
					if ($("#cMovimiento").val() != "SELECCIONE"){ szTemp += " AND tipoMovimiento = '" + $("#cMovimiento").val() + "'" };
					
					$("#szTemp").val(szTemp);
					
 					if (szTemp != '') szTemp = "&qw=" + szTemp;
    		
    				$("#tblIngresosEgresos").dataTable({
    					bAutoWidth : true,
						sScrollX: "100%",
						sScrollY: "500",
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
						bRetrive: true,
						bDestroy: true,
						bServerSide: true,
						bProcessing: true,
						bJQueryUI: true,
						bPaginate: false,
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_RdbAutorizaRDB" + szTemp ,
						aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
							{ sName: "Sel", sClass: "alignCenter" },
							{ sName: "folioSai", sClass: "alignCenter" },
							{ sName: "nFolio", sClass: "alignCenter" },
							{ sName: "banco",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"   },
							{ sName: "cuentaBancaria" },
							{ sName: "sBeneficiario" },
							{ sName: "tipoMovimiento" },
							{ sName: "fechaCarga", sClass: "alignCenter" },
							{ sName: "cMedioPago", sClass: "alignCenter" },
							{ sName: "cConcepto", sClass: "alignCenter" },
							{ sName: "mImporte", sClass: "alignRight"	},
							{ sName: "ID_CASO",	bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   },
							{ sName: "mImporteSinF", bSearchable: false, bSortable: false, bVisible: false, sClass: "alignCenter"   },
							{ sName: "cEvento", bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   },
							{ sName: "idCargaArchivoRDB", bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   },
							{ sName: "cuentaOrigen", bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   }
						]
					});
				} );
			
			$("#pbLimpiar").button().click(function() {

					$("#tblAct").dataTable().fnClearTable();
					
					$("#tBancosRDB").val(""); 
					$("#tBancosRDB").change(); 
					
					$("#cuentaBancaria").val(""); 
					$("#cMovimiento").val("EGRESOS");
					$("#szTemp").val( "" );
					
					$("#tblIngresosEgresos").dataTable({
    					bAutoWidth : true,
						sScrollX: "100%",
						sScrollY: "500",
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
						bRetrive: true,
						bDestroy: true,
						bServerSide: true,
						bProcessing: true,
						bPaginate: false,
		    			bJQueryUI: true,						
		    			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_RdbAutorizaRDB&qw=banco = 'abc'"  ,
						aaSorting: [[ 1, "asc" ]] ,
						aoColumns: [
							{ sName: "Sel", sClass: "alignCenter" },
							{ sName: "folioSai", sClass: "alignCenter" },
							{ sName: "nFolio", sClass: "alignCenter" },
							{ sName: "banco",	bSearchable: true,	bSortable: true, bVisible: true, sClass: "alignCenter"   },
							{ sName: "cuentaBancaria" },
							{ sName: "sBeneficiario" },
							{ sName: "tipoMovimiento" },
							{ sName: "fechaCarga", sClass: "alignCenter" },
							{ sName: "cMedioPago", sClass: "alignCenter" },
							{ sName: "cConcepto", sClass: "alignCenter" },
							{ sName: "mImporte", sClass: "alignRight"	},
							{ sName: "ID_CASO",	bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   },
							{ sName: "mImporteSinF", bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   },
							{ sName: "cEvento", bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"   },
							{ sName: "idCargaArchivoRDB", bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   },
							{ sName: "cuentaOrigen", bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"   }
						]
					});
					
			});			
			
			$("#pbAutorizar").button().click(function() {
				
				var fAutorizar = $("#fAutorizar").val();
				$( "#dialog-carga-aut" ).dialog( "open" );
				if(confirm("Esta Seguro de Autorizar los Movimientos con Fecha "+fAutorizar)){
					
						
						var otbl = $("#tblIngresosEgresos").dataTable();
						var aInput = otbl.fnGetNodes();
						var aData = otbl.fnGetData();
						$("#nDocRenglon").val(0);
						
						if(aData.length == 0){
							
							alert("No Selecciono Registro Para Autorizar");
							return;
						}
						
						for(var i=0; i<aData.length; i++){
							
							if ( $('input', aInput[i] )[0].checked ){
								$("#tblAct").dataTable().fnAddData( [ '<input type="text" name="id_caso" id="id_caso" value="' + aData[i][11] + '">' ]  );
								
								guardarEncabezadoDetalle(aData[i][4],aData[i][5],aData[i][12],aData[i][13],aData[i][14],aData[i][6]);
								queryFormPost("tActualizaCASOInEgUpdate", {async: false });
								
								if(aData[i][13] == "800_50_27C"  ){
								/////// Poner aData[i][15] por /////// Poner aData[i][4]
									guardarEncabezadoDetalle(aData[i][15],aData[i][5],aData[i][12],"800_50_27A",aData[i][14],aData[i][6]);
									//queryFormPost("tRdbDetalle_Create", {async: false });
									
								}/*else if(aData[i][13] == "800_50_27A"){
									/////// Poner aData[i][15] por /////// Poner aData[i][5]
									guardarEncabezadoDetalle(aData[i][4],aData[i][5],aData[i][12],"",aData[i][14],aData[i][6]);
									//queryFormPost("tRdbDetalle_Create", {async: false });
								}*/
								
							}
						}
						
						//$("#pbLimpiar").click();
						alert("Movimientos Autorizados Correctamente");
						location.reload();
				}else{
					$( "#dialog-carga-aut" ).dialog( "close" );
				}
			});	
	
			
			$("#pbSwitch").button().click(function() {
				
					if( $( this ).html() == "<SPAN class=ui-button-text>DesMarcar</SPAN>" ){
						$( this ).html("<SPAN class=ui-button-text>Marcar</SPAN>");
						$( ".sel" ).attr('checked', false);
					}else{
						$( this ).html("<SPAN class=ui-button-text>DesMarcar</SPAN>");
						$( ".sel" ).attr('checked', true);
					}
			});	
	
			$("#pbPdf").button().click(function() {
				
					if ( $("#szTemp").val() != "" ){
						window.open(
							"../admin/SeguridadCatalogos?"
								+ "catalogo=ANEXO"
								+ "&accion=run"
								+ "&rn=RepConsultaIngresosEgresos.jasper"
								+ "&whereFolio=" + $("#szTemp").val(),  
							"Anexo",
							"scrollbars=1, resizable=yes, width=1024, height=768");
					}

				});	
			
			$("#pbCerrar").button().click(function() {
					
					if(confirm("Seguro que Desea Hacer el Cierre de Dia "+$("#fAplicacion").val())){
						
						cierre();
						
					}
					
					//queryFormPost("tActualizaCASOInEgUpdate", {async: false });
					
			});	
			
$( "#dialog-carga" ).dialog({
				
				autoOpen: false,
	    		modal: true,
	    		resizable: false,
				width: 230,
	  			heigth: 135,
	    		title: 'Guardando Informacion',
	    		show: "blind",
	    		hide: "scale",
	    		closeOnEscape: false,
				overlay: { backgroundColor: '#FFF',
				   opacity: 6.5   
				},beforeClose: function( event, ui ) {
							return false;			
				},
				
				open: function() {
				
					var f = $("#fAplicacion").val().split("/");
					var fecha = "'"+f[2]+"-"+f[1]+"-"+f[0]+"'";
					var tipo = "aplicarMotor";
					var caNoContrarrecibo = $("#caNoRDB").val();
					var campo = "nFolioRDB";
					var tablaEnc = "tRDBEncabezado";
					var campoCondicion = " caNoRDB "; 
					var tablaDet = "tRDBDetalle"; 
					var tipoAplicar = "RDB";
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					
					$.ajax({
							url:'./cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo,campo:campo,tablaEnc:tablaEnc,campoCondicion:campoCondicion,tablaDet:tablaDet,tipoAplicar:tipoAplicar},
							success:function(data){
								
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
											
										alert("Documento Aplicado Correctamente: "+ $("#caNoRDB").val() ) ;
										guardarSaldo();
										breturnVal = true;
										location.reload();
									}else{
										
										breturnVal = false;
										alert( "El Documento No Se Aplico: "+$("#caNoRDB").val() + " - " + data.estatus ) ;		
									}
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-carga" ).dialog( "close" );
							}
					});
				
				},
				close: function() {										
				}				
			});
			
			$('#dialog-carga-aut').dialog({
			    autoOpen: false,
			    modal: true,
			    resizable: false,
			    width: 230,
			    heigth: 135,
			    title: 'Guardando Informacion',
			    show: "blind",
			    hide: "scale",
			    closeOnEscape: false,
			   	beforeClose: function( event, ui ) {
							return false;			
				},
			    overlay: { backgroundColor: '#FFF',
						   opacity: 6.5   
				}
			});	
			
			
			
			
});


		
		function formSubmited() {
                alert("Beneficiario enviado!");
            }
		
		
		function fnGetSelected( oTableLocal )
			{
				var aReturn = new Array();
				var aTrs = oTableLocal.fnGetNodes();
				
				for ( var i=0 ; i<aTrs.length ; i++ )
				{
					if ( $(aTrs[i]).hasClass('gradeA') )
					{
						aReturn.push( aTrs[i] );
					}
				}
				return aReturn;
			}

			function foco(elemento) {
			 elemento.style.border = "1px solid #FF0000";
			 }
			
			 function no_foco(elemento) {
			 elemento.style.border = "1px solid #CCCCCC";
			 }
		
function cierre(){
	
	//**Buscar si existe la Fecha tRDBEncabezado**//
	
	var cTipoPoliza = ($("#cMovimiento").val() == "EGRESO")? "DI" : "IN" ;
	
	var f = $("#fAplicacion").val().split("/");
	var fecha = "'"+f[2]+"-"+f[1]+"-"+f[0]+"'";
	
	// *** Para Generar Poliza Dependiendo si es DI IN *** //
	//var camposWhere = " WHERE fAplicacion = "+fecha+" and cTipoPoliza = '"+cTipoPoliza+"' and cDocumentoHAplicado is null GROUP BY tE.fAplicacion, tE.nFolioRDB, tE.caNoRDB ";
	
	// *** Para Guardar Una Poliza *** //
	
	var camposWhere = " WHERE fAplicacion = "+fecha+" and cDocumentoHAplicado is null GROUP BY tE.fAplicacion, tE.nFolioRDB, tE.caNoRDB ";
	var param = "";
	$("#cMes").val(f[1]);
	
	$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"tRDBEncabezadoExiste", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
											
				$("#mImporteNegativo").val($("#mImporteSinFormato").val() * -1);
				
				if(j.length == 0){
					
					alert("No Existen Datos Para el Cierre");
					return;
					
				}else{
					
					$("#caNoRDB").val(j[0].Col3);
					$( "#dialog-carga" ).dialog( "open" );
					return breturnVal;
					
				}
													
	});	
	
}

function guardarSaldo(){
				 
				 var fe = $("#fAplicacion").val().split("/");
				 var fecha = fe[2]+"-"+fe[1]+"-"+fe[0];
				 $("#fechCarga").val(fecha);
				 
				 /*** Suma Cierre dia ***/
				 
				 queryFormPost({
						queryName : "tRdbCargaArchivoSumaCierre_RDB",
						async : false,
						callback : function(){
					 			
					 			// Verifica si ya se ingreso informacion a tRdbCargaArchivoRDBSaldo
					 			
					 			 var zTabla = "TRDBCARGAARCHIVOSUMACIERRE_RDB";
								 var camposWhere = " WHERE dFecha = '" +fecha+"'"; 
								 var param = "";
								 var datos = "sinDatos";
											
								/*$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
																				
										for(var i = 0; i < j.length; i++ ){
														
												datos = "datos"; 
														
										}
								*/				
										// Buscar saldo de ultimo ingreso, egreso y diferencia
														
										queryFormPost({
												queryName : "tRdbCargaArchivoRDBSaldo_READ",
												async : false,
												callback : function(){
															
														var ingreso = ($("#mIngreso").val() != "")?  $("#mIngreso").val() : 0.00;
														var egreso = ($("#mEgreso").val() != "")?  $("#mEgreso").val() : 0.00;
														var importeTotal = ($("#mImporteTotal").val() != "")?  $("#mImporteTotal").val() : 0.00;
														
														var ingresoSaldo = ($("#mIngresoSaldo").val() != "")?  $("#mIngresoSaldo").val() : 0.00;
														var egresoSaldo = ($("#mEgresoSaldo").val() != "")?  $("#mEgresoSaldo").val() : 0.00;
														var importeSaldo = ($("#mImporteSaldo").val() != "")?  $("#mImporteSaldo").val() : 0.00;
														
														$("#mIngresoSaldo").val(parseFloat(ingreso));		
														$("#mEgresoSaldo").val(parseFloat(egreso));
														$("#mImporteSaldo").val(parseFloat(importeTotal) + parseFloat(importeSaldo));
														
														
														//var nuevoImporte = parseFloat(importeSaldo) + parseFloat(importeTotal);
														//$("#importeTotal").val(nuevoImporte);
															
														//if(datos == "datos"){
																	
															   // Actualizar informacion cierre
															   /*queryFormPost({
																		queryName : "tRdbCargaArchivoERDBSaldo_Update",
																		async : false,
																		callback : function() {
																						
																   				guardarSaldo();
																				alert("Actualizado Correctamente Cierre");
																							
																		}		
																});*/
														//}else{
																					
																// Guardar informacion cierre
																queryFormPost({
																		queryName : "tRdbCargaArchivoERDBSaldo_Create",
																		async : false,
																		callback : function() {
																									
																				alert("Guardado Correctamente El Cierre de "+$("#fAplicacion").val());
																				queryFormPost("tRdbCargatEncabezadoRDB_Update", {
																					async: false,
																					callback : function() { 
																					
																						queryFormPost("tRDBIdCargaArchivoRDBSaldo_READ", {async: false});
																						imprimir();
																					}
																				});
																				
																				return;						
																		}		
																});
																					
														//}
													}		
											});
							//});
					 	}
				});
		}
			 
function guardarEncabezadoDetalle(sCuentaBancaria,beneficiario,importe,cEvento,idCargaArchivoRDB,tipoMovi){
	
	var f = $("#fAutorizar").val().split("/");
	var fecha = f[2]+"-"+f[1]+"-"+f[0];
	var cTipoPoliza = ($("#cMovimiento").val() == "EGRESO")? "DI" : "IN" ;
	$("#cTipoPoliza").val(cTipoPoliza);
	
	// Guardar Polizas IN DI
	
	//var camposWhere = " WHERE fAplicacion = "+fecha+" and cTipoPoliza = '"+cTipoPoliza+"' and cDocumentoHAplicado is null GROUP BY tE.fAplicacion, tE.nFolioRDB, tE.caNoRDB ";
	
	var camposWhere = " WHERE fAplicacion = "+fecha+" and cDocumentoHAplicado is null GROUP BY tE.fAplicacion, tE.nFolioRDB, tE.caNoRDB ";
	var param = "";
	$("#cMes").val(f[1]);
	var res = 0;
	//$.getJSON("../catalogos/SelectJson.jsp",{Tabla:"tRDBEncabezadoExiste", Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true", async:false }, function(j){ 
		$("#fechaToday").val(fecha);
		/*** Trae informacion de  ***/
		queryFormPost("tRDBEncabezadoExiste_READ", {async: false });
	
				$("#sCuentaBancaria").val(sCuentaBancaria);
				$("#sBeneficiario").val(beneficiario);
				$("#mImporteSinFormato").val(importe);
				$("#mImporteNegativo").val(importe * -1);
				$("#cEvento").val(cEvento);
				$("#idCargaArchivoRDB").val(idCargaArchivoRDB);
				$("#tipoMovi").val(tipoMovi);
								
				$("#mImporteNegativo").val($("#mImporteSinFormato").val() * -1);
				
				if($("#nDocRenglon").val() == 0 || $("#nDocRenglon").val() == "" ){
					
					getNextSequenceVal({seqName: "REGISTRO_DIARIO_BANCO_RDB" , async: false, callback: setSequenceValca});
					$("#nDocRenglon").val(1);
					queryFormPost("tRdbEncabezado_Create", {async: false });
					queryFormPost("tRdbDetalle_Create", {async: false });
					
				}else{
					
					//alert("renglon: "+j[0].Col2);
					//$("#nFolioRDB").val(j[0].Col1);
					//$("#nDocRenglon").val(j[0].Col2);
					queryFormPost("tRdbDetalle_Create", {async: false });
					//$("#nDocRenglon").val($("#nDocRenglon").val()++);
				}
													
	//});
	
}

function setSequenceValca(seqValuee) 
{		
		seqValue = "000000" + seqValuee;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = seqValue;
		$("#nFolioRDB").val( seqValuee );
		
		seqValue = "<%=cCentroContable%>" + "RD" + $("#cEjercicio").val() + seqValue;
		$("#caNoRDB").val( seqValue );
}

function imprimir(){
	
	window.open(	"../admin/SeguridadCatalogos?"
					+ "catalogo=Rdb"
					+ "&accion=run"
					+ "&rn=RegistroDiarioBancoCierre.jasper"
					+ "&idRdbCargaArchivoRDBSaldo=" + $("#idRdbCargaArchivoRDBSaldo").val(),
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768"
			   );
}
		
/*
 function guardarSaldo(){
				 
				 var msn = "";
				 if($("#tBancosRDB").val() == ""){
					 
					 msn += "- Banco \n";
					 
				 }if($("#cuentaBancaria").val() == ""){
					
					 msn += "- Cuenta Bancaria \n";
					
				 }if($("#fechaCarga").val() == ""){
					 
					  msn += "- Fecha \n";
					 
				 }
				 if(msn != ""){
					 
					 msnT = "Campo Obligatorio para Cierre: \n " ;
					 alert(msnT+msn);
					 return;
				 }
				 
				 var banco = $("#tBancosRDB").val();
				 var cuentaBancaria = $("#cuentaBancaria").val();
				 var tipoMovi = $("#cMovimiento").val();
				 var fAplicacion = $("#fAplicacion").val();
				 
				 $("#banco").val("");
				 $("#cuentaBanca").val("");
				 $("#fechCarga").val("");
				 $("#importeTotal").val("");
				 $("#datos").val("sinDatos");
				 
				 var fe = $("#fAplicacion").val().split("/");
				 var fecha = fe[2]+"-"+fe[1]+"-"+fe[0];
				 $("#fechCarga").val(fecha);
				 
				 // Suma Cierre dia
				 queryFormPost({
						queryName : "tRdbCargaArchivoSumaCierre_RDB",
						async : false,
						callback : function(){
					 			
					 			// Verifica si ya se ingreso informacion a tRdbCargaArchivoRDBSaldo
					 			 var zTabla = "TRDBCARGAARCHIVOSUMACIERRE_RDB";
								 var camposWhere = " WHERE sBanco = '"+banco+"' AND sCuentaBancaria = '"+cuentaBancaria+"' AND dFecha = '" +fecha+"'"; 
								 var param = "";
								 var datos = "sinDatos";
											
								$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, MaxReg: "5", ajax:"true" }, function(j){ 
																				
										for(var i = 0; i < j.length; i++ ){
														
												datos = "datos"; 
														
										}
														
										// Buscar saldo de ultimo ingreso 
														
										queryFormPost({
												queryName : "tRdbCargaArchivoRDBSaldo_READ",
												async : false,
												callback : function(){
																			
														var importeSaldo = ($("#importeSaldo").val() != "")?  $("#importeSaldo").val() : 0.00;
														var importeTotal = ($("#importeTotal").val() != "")?  $("#importeTotal").val() : 0.00;
																			
														var nuevoImporte = parseFloat(importeSaldo) + parseFloat(importeTotal);
														$("#importeTotal").val(nuevoImporte);
															
														if(datos == "datos"){
																	
															   // Actualizar informacion cierre
															   queryFormPost({
																		queryName : "tRdbCargaArchivoERDBSaldo_Update",
																		async : false,
																		callback : function() {
																						
																				alert("Actualizado Correctamente Cierre");
																							
																		}		
																});
														}else{
																					
																// Guardar informacion cierre
																queryFormPost({
																		queryName : "tRdbCargaArchivoERDBSaldo_Create",
																		async : false,
																		callback : function() {
																									
																				alert("Guardado Correctamente");
																										
																		}		
																});
																					
														}
													}		
											});
							});
					 	}
				});
		}
 */
</script>
</head>
<body id="dt_example" >
	<form id="ExportarForm" name="ExportarForm" action="../gstnmngr/LayoutGeneral" method="post"> 
		<div id="container" style="width:1300px; padding-left:100px" class="SyCData">	
			<input type="hidden" name="szTemp" id="szTemp">
			<input type="hidden" name="cDocumento" id="cDocumento" value="TODOS">
			<input type="hidden" name="cBanco" id="cBanco" value="">
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="">
			<input type="hidden" name="tipoConsulta" id="tipoConsulta" value="ConsultaIngresosEgreso">
			<input type="hidden" name="cOperacion" id="cOperacion" value="CONSULTA_RDB">
			
			<input type="hidden" name="banco" id="banco" />
			<input type="hidden" name="cuentaBanca" id="cuentaBanca" />
			<input type="hidden" name="fechCarga" id="fechCarga" />
			
			<input type="hidden" name="mIngreso" id="mIngreso" value="0.00" />
			<input type="hidden" name="mEgreso" id="mEgreso" value="0.00" />
			<input type="hidden" name="mImporteTotal" id="mImporteTotal" value="0.00" />
			
			<input type="hidden" name="mIngresoSaldo" id="mIngresoSaldo" value="0.00" />
			<input type="hidden" name="mEgresoSaldo" id="mEgresoSaldo" value="0.00" />
			<input type="hidden" name="mImporteSaldo" id="mImporteSaldo" value="0.00" />
			
			<input type="hidden" name="datos" id="datos" value="sinDatos"/>
			
			<!-- Datos Para Guardar Encabezado Detalle -->
			
			<input type="hidden" name="fechaToday" id="fechaToday" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>" />
			<input type="hidden" id="cRamo" name="cRamo" value="16" />
			<input type="hidden" name="caNoRDB" id="caNoRDB" />
			<input type="hidden" name="cTipoPoliza" id="cTipoPoliza" />
			<input type="hidden" name="cEjercicio" id="cEjercicio"  />
			<input type="hidden" name="cUnidadResponsableContable" id="cUnidadResponsableContable" value="RHQ" />
			<input type="hidden" name="loginUsuarioCaptura" id="loginUsuarioCaptura" value="<%=u_login%>" />
			
			<input type="hidden" name="nDocRenglon" id="nDocRenglon" value="0" />
			<input type="hidden" name="sCuentaBancaria" id="sCuentaBancaria" />
			<input type="hidden" name="sBeneficiario" id="sBeneficiario" />
			<input type="hidden" name="cEvento" id="cEvento" />
			<input type="hidden" name="mImporteSinFormato" id="mImporteSinFormato" />
			<input type="hidden" name="mImporteNegativo" id="mImporteNegativo" />
			<input type="hidden" name="cMes" id="cMes" />
			<input type="hidden" name="idCargaArchivoRDB" id="idCargaArchivoRDB" />
			<input type="hidden" name="nFolioRDB" id="nFolioRDB" />
			<input type="hidden" name="tipoMovi" id="tipoMovi" />
			
			<input type="hidden" name="idRdbCargaArchivoRDBSaldo" id="idRdbCargaArchivoRDBSaldo" />				
			<input type="hidden" id="btnGuardar" value="imprimir" onclick="imprimir()" />												
			<h1>Autorizacion de Ingresos / Egresos</h1>	
			<fieldset style="width:1300px;">		
					<table id="clvcont" border="1" align="center">
						<tr>
							<td align="right">Banco: </td>
							<td>
								<input type="text" class="AyudaSyC desahabilitado" maxlength="20" size="20" name="tBancosRDB" id="tBancosRDB" readonly >
							</td>
							<td align="right">Cuenta: </td>
							<td>
								<select name="cuentaBancaria" id="cuentaBancaria" style="width: 15em;" class="desahabilitado">
								</select>
							</td>
							<td valign="top" align="left"> 
								<select name="cMovimiento" id="cMovimiento" style="width: 10em;" class="desahabilitado">
									<option value="SELECCIONE">SELECCIONE</option>
									<option value="EGRESO">EGRESO</option>
									<option value="INGRESO">INGRESO</option>
								</select>
							</td>
							<td>Fecha Carga</td><td><input type="text" id="fechaCarga" name="fechaCarga" size="15" readonly='readonly' value="<%=today%>"/></td>
							<td>Fecha Autorizar</td><td><input type="text" id="fAutorizar" name="fAutorizar" size="15"  readonly='readonly' value="<%=today%>"/></td>
							<td>Fecha Aplicacion</td><td><input type="text" id="fAplicacion" name="fAplicacion" size="15"  readonly='readonly' value="<%=today%>"/></td>	
						</tr>
					</table>
					<table>
						<tr>
						<td>&nbsp;</td>
						</tr>
					</table>
					<table align="center">
						<tr>
							<td>
								<input type="button" id="pbBuscar" value="Buscar"/>
							</td>
							<td>
								<input type="button" id="pbLimpiar" value="Limpiar"/>
							</td>
							<td>
								<input type="button" id="pbSwitch" value="DesMarcar" />
							</td>
							<td>
								<input type="button" id="pbAutorizar" value="Autorizar"/>
							</td>
							<td>
								<input type="button" id="pbCerrar" value="Cierre Dia"/>
							</td>
						</tr>
					</table>
		</fieldset>
			<table>
				<tr>
				<td>&nbsp;</td>
				</tr>
			</table>
			<table id="tblIngresosEgresos" class="display"  >
	            <thead>
	                <tr>
	                	<th>Sel.</th>
	                	<th>Folio Registro</th>
	                	<th>Folio Sai</th>
	                	<th>Banco</th>
	                    <th>Número de Cuenta</th>
	                    <th>Beneficiario</th>
						<th>Movimiento</th>
	                    <th>Fecha Carga</th>
						<th>Medio de Pago</th>
						<th>Concepto</th>
						<th>Importe</th> 
						<th>&nbsp;</th> 
						<th>&nbsp;</th> 
						<th>&nbsp;</th>
						<th>&nbsp;</th> 
	                </tr>
	            </thead>
	        </table>
			<table id="tblAct" class="display" style="visibility: hidden" >
	            <thead>
	                <tr>
						<th>ID_CASO</th> 
	                </tr>
	            </thead>
	        </table>
	       <div id="dialog-carga">
				<div id="esperar" align="center">Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
			<div id="dialog-carga-aut">
				<div id="esperar" align="center">Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
			<br/>
		</div>
	</form>
	</body>
</html>