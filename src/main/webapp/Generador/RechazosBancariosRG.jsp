<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>

<%	
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String cUR = "";
	cUR = usuario.getU_UR();	

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(	GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	String nombreElabora = e.getNombre();
	String aPaternoElabora = e.getApellidoPaterno();
	String aMaternoElabora= e.getApellidoMaterno();
	String puestoElabora = e.getCargo();
	
	CatalogoURFIELBusinessLogic curbl = new CatalogoURFIELBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean permitePagoSinFIEL = curbl.permitePagoSinFiel(cUR);

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Solicitudes por Rechazo Bancario</title>

		<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		<link rel="stylesheet" href="css/bootstrap.min.css">
		<script src="js/bootstrap.bundle.min.js"></script>
		<style type="text/css" title="currentStyle">
			@import "themes/smoothness/jquery-ui-1.8.4.custom.css";
			@import "css/demo_table_jui.css";
			@import "css/demo_page.css";		
		</style>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
		
		<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>			
		<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
		<script type="text/javascript" src="js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		
		<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

		<script type="text/javascript" charset="utf-8">
		
		var oTable;
		var sIdRFC;	
		var condicion_UR;
		var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
		var esConsulta = true;
		
		$(document).ready(function() {
			
			condicion_UR = "= '" + $("#uUR").val() + "'";	
			reloadtable();
			setFechas(); 
			$("#autorizadoPorFielChk").prop("checked", true);
			
			querySelectPost("catTipoSuplenciaRead", "tipoSuplencia",{async: false });
			querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo",{async: false });
			
			$("#divSolicitud").hide();
			$("#oficioDelegatorioCaptura").hide();
			
			$("#oficioDelegatorioVoBo").hide();
			
			if( permitePagoSinFIEL ) {
				$("#AutorizaConFielTD").css("display","block");
			}else{
				$("#autorizadoPorFiel ").val("true");
				
			}
									
			$("#btnGuardar").button().click(function(){    	
				Actualizar();
		    });	
			
		});		
		
		function setFechas(){
			$("#dFechaOficio").datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true
			});
			
			$("#dFechaOficioVoBo").datepicker({
				dateFormat: "dd/mm/yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true
			});
		}
			
		function tblDblClick(event){
	         var aPos;
	         var aData;
	         aPos = oTable.fnGetPosition(event.target.parentNode); /*Toma la posición del renglón doble click*/
	         aData = oTable.fnGetData(aPos); /*Toma los datos de acuerdo a la posición y genera arreglo*/
	         var id= aData[0]; /*Set de dato requerido*/
	         $("#divSolicitud").show();		        
	         
	          $("#nFolioPago"	 ).val( aData[0]);
	          $("#nCXP"	         ).val( aData[1]);
	          $("#nRFC"	         ).val( aData[2]);
	          $("#nCTAB"	     ).val( aData[3]);
	          $("#nDescripcion"	 ).val( aData[4]);
	          $("#nUR"	         ).val( aData[5]);
		 	  $("#nImporte"	     ).val( aData[6]);
		 	  $("#DESTINO_GASTO" ).val( aData[8]);
		 	  $("#cEsFIEL"	     ).val( aData[9]);
		 	  $("#nFirmaVoBo"    ).val( aData[10]);
		 	  $("#nPuestoVoBo"   ).val( aData[11]);		 	 
		 	  $("#nFirmaAut"     ).val( aData[12]);
		 	  $("#nPuestoAut"    ).val( aData[13]);
		 	  $("#nOficioVoBo"   ).val( aData[14]);
		 	  $("#nOficioAut"    ).val( aData[15]);
		 	  $("#nNombre"		 ).val( aData[16]);	 	  
        }
        	
        function reloadtable(){
        	oTable = $("#tblSolicitudes").dataTable({
		        		"bLengthChange" : true,
						"bFilter" : true,
						"bSort" : true,
						"bInfo" : true,
						"bPaginate" : true,
						"bAutoWidth" : true,
						"bScrollCollapse" : true,
						"sScrollXInner": "100%", 
						"sScrollX": "100%",
						"sPaginationType" : "full_numbers",
						"bJQueryUI" : true,
						"bRetrive" : true,
						"bDestroy" : true,
						"bServerSide": true,
						"fnInitComplete": function() {    
							oTable.fnAdjustColumnSizing();
							},
						"iDisplayLength": 25,  
                       oLanguage : {
                              sProcessing : "Procesando...",
                              sLengthMenu : "Mostrar _MENU_ registros",
                              sZeroRecords : "No hay registros a mostrar",
                              sEmptyTable : "No se encontraron resultados", 
                              sLoadingRecords : "Cargando...",
                              sInfo : "Registros _START_ al _END_ de _TOTAL_",
                              sInfoEmpty : "Registro 0 al 0 de 0",
                              sInfoFiltered : "(filtered from _MAX_ total entries)",
                              sInfoPostFix : "",
                              sInfoThousands : ",",
                              sSearch : "Buscar:",
                              oPaginate : {
                                    sFirst : "Primero",
                                    sPrevious : "Ant.",
                                    sNext : "Sigte.",
                                    sLast : "&Uacute;ltimo"
                              }
                       },
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_solicitudes_rechazo_bancario&qw=" + encodeURIComponent( "cUnidadEjecutora"+condicion_UR),
				sPaginationType: "full_numbers",
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [					
					{ sName: "nFolioPago" },
					{ sName: "canocontrarrecibo" },
					{ sName: "RFC" },
					{ sName: "CTAB", bVisible : false},
					{ sName: "cdescripcionpoliza" },
					{ sName: "cunidadejecutora"	},
					{ sName: "mMontoSolicitud", bVisible : false},					
					{ sName: "ID_DESTINO_GASTO", bVisible : false},
					{ sName: "cEsFirmaElectronica", bVisible : false},
					{ sName: "VoBoFirma", bVisible : false},
					{ sName: "VoBoPuesto", bVisible : false},
					{ sName: "AutFirma", bVisible : false},
					{ sName: "AutPuesto", bVisible : false},
					{ sName: "cFolioOficioVoBo", bVisible : false},
					{ sName: "cFolioOficioAut", bVisible : false},
					{ sName: "cNombre", bVisible : false}				
				]
	       	});	 
        	
        	  /*Alta de funcionalidad de doble clic*/
        	$("#tblSolicitudes tbody").unbind('dblclick');
            $("#tblSolicitudes tbody").dblclick( function( e ) {                	                	
                 $(oTable.fnSettings().aoData).each(
                        function (){
                              $(this.nTr).removeClass('row_selected');
                 });
                 $(e.target.parentNode).addClass('row_selected');
                 tblDblClick(e);	                 
            });
	       	
	       	$( "#dialog-firmantes" ).dialog({
						
				autoOpen: false,
				height: 490,
				width: 480,
				modal: true,
				buttons: {
						"Aceptar": function() {														
							$("#FOLIO").val($("#nFolioPago").val());
							
							Swal.fire({				  
								  text: "¿Esta seguro de actualizar la cuenta bancaria?",
								  icon: "warning",
								  showCancelButton: true,
								  confirmButtonColor: '#7066E0',
								  cancelButtonColor: '#e6e6e6',
								  confirmButtonText: 'Aceptar',
								  cancelButtonText: 'Cancelar'
							}).then((result) => {
								if (result.isConfirmed) {														
									if( $("#cNombreVoBo").val() == "" ){ 
										Swal.fire({ icon: "warning",
													text: "Falta Ingresar Nombre en Datos Vº Bº"})									
										return; 
									} 
									else if($("#cPuestoVoBo").val() == ""){ 
										Swal.fire({ icon: "warning",
													text: "Falta Ingresar Puesto en Datos Vº Bº"})									
										return;
									}
									
									if($("#cNombreAut").val() == ""){ 
										Swal.fire({ icon: "warning",
													text: "Falta Ingresar Nombre en Datos Autorizar"})									
										return;									
									} 
									else if($("#cPuestoAut").val() == ""){ 
										Swal.fire({ icon: "warning",
													text: "Falta Ingresar Puesto en Datos Autorizar"})									
										return;
									}
									
									if($("#cNombreEla").val() == ""){ 
										Swal.fire({ icon: "warning",
													text: "Falta Ingresar Nombre en Datos Elabora"})									
										return;
									} 
									else if($("#cPuestoEla").val() == ""){ 
										Swal.fire({ icon: "warning",
													text: "Falta Ingresar Puesto en Datos Elabora"})									
										return;
									}
									
									$("#cNombreVo").val($("#cNombreVoBo").val());
									$("#cPaternoVo").val($("#cPaternoVoBo").val());
									$("#cMaternoVo").val($("#cMaternoVoBo").val());
									$("#cPuestoVo").val($("#cPuestoVoBo").val());
									$("#cEmpleadoVo").val($("#cboVoBo").val());
									
									$("#cNombreA").val($("#cNombreAut").val());
									$("#cPaternoA").val($("#cPaternoAut").val());
									$("#cMaternoA").val($("#cMaternoAut").val());
									$("#cPuestoA").val($("#cPuestoAut").val());
									$("#cEmpleadoA").val($("#cboAutoriza").val());
									
									$("#cNombreE").val($("#cNombreEla").val());
									$("#cPaternoE").val($("#cPaternoEla").val());
									$("#cMaternoE").val($("#cMaternoEla").val());
									$("#cPuestoE").val($("#cPuestoEla").val());
									
									$("#firmanteVoBo").val($("#cNombreVoBo").val()+" "+$("#cPaternoVoBo").val()+" "+$("#cMaternoVoBo").val() );
									$("#firmanteAut").val($("#cNombreAut").val()+" "+$("#cPaternoAut").val()+" "+$("#cMaternoAut").val());
									$("#firmanteEla").val($("#cNombreEla").val()+" "+$("#cPaternoEla").val()+" "+$("#cMaternoEla").val());
									
									var msn = "No Se Guardo Correctamente Informacion de Firmantes"; 
									
									queryFormPost("tRELACIONGASTOSEncabezadoFirmante_Update", {async: false });											
									
									if ($("#oficioDelegatorio").prop("checked")){
									
										if($("#cFolioOficio").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar el folio de Oficio."})									
											return;
										} 
										else if($("#dFechaOficio").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar la fecha del Oficio."})									
											return;
										}
										else if($("#cNombreTitular").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar Nombre del Titular."})									
											return;
										}
										else if($("#cPuestoTitular").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar Puesto del Titular."})									
											return;
										}
										
										$("#cFolioOficioAux").val($("#cFolioOficio").val());
										$("#dFechaOficioAux").val($("#dFechaOficio").val());
										$("#cNombreTitularAux").val($("#cNombreTitular").val());
										$("#cApellidoPaternoTitularAux").val($("#cPaternoTitular").val());
										$("#cApellidoMaternoTitularAux").val($("#cMaternoTitular").val());
										$("#cPuestoTitularAux").val($("#cPuestoTitular").val());
										$("#tipoSuplenciaAux").val($("#tipoSuplencia").val());
										$("#numeroEmpleadoAutoriza").val( $("#cboSuplenteAut").val() )
										
										if($("#firmanteOficioExiste").val() == "Existe"){
											queryFormPost({
												queryName : "tPagoFirmanteDelagatorioUpdate", 
												    async : false, 
												 callback : function(){ 
												    		msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
											      		  } 
											});									
										}else{
											queryFormPost({	
												queryName : "tPagoFirmanteDelagatorioCreate", 
												    async : false, 
												 callback : function(){ 
															msn = "Firmantes Oficio Delegatorio guardado correctamente.";
														  } 
											});
										}
										Swal.fire({ icon: "success",
													text: msn})										
									}
									
									if ($("#oficioDeleVoBo").prop("checked")){
									
										if($("#cFolioOficioVoBo").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar el folio de Oficio."})									
											return;
										} 
										else if($("#dFechaOficioVoBo").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar la fecha del Oficio."})									
											return;
										}
										else if($("#cNombreTitularVoBo").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar Nombre del Titular."})									
											return; 
										}
										else if($("#cPuestoTitularVoBo").val() == ""){ 
											Swal.fire({ icon: "warning",
														text: "Falta Ingresar Puesto del Titular."})									
											return;
										}
										
										$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBo").val());
										$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBo").val());
										$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBo").val());
										$("#cApellidoPaternoTitularVoBoAux").val($("#cPaternoTitularVoBo").val());
										$("#cApellidoMaternoTitularVoBoAux").val($("#cMaternoTitularVoBo").val());
										$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBo").val());
										$("#tipoSuplenciaVoBoAux").val($("#tipoSuplenciaVoBo").val());
										$("#numeroEmpleadoVoBo").val( $("#cboSuplenteVoBo").val() );
										
										if( $("#firmanteOficioVoBoExiste").val() == "Existe" ){
											queryFormPost({
												queryName : "tPagoFirmanteDelegatorioVoBoUpdate", 
												    async : false, 
												 callback : function(){ 
																msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
															} 
											});									
										}else{
										
											queryFormPost({ 
												queryName : "tPagoFirmanteDelegatorioVoBoCreate", 
												    async : false, 
												 callback : function(){ 
																msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
															} 
											});
												  
										}
										
										Swal.fire({ icon: "success",
													text: msn})
									}													
																	
									queryFormPost({ queryName : "ActualizaCTAB_RB", 
													async : false , 
													callback: function (){
														if ($("#autorizadoPorFielChk").prop("checked")){
															queryFormPost({ 
																			queryName : "esFIEL", 
																			    async : false, 
																			 callback : function(){ 
																							msn = "Pago enviado a Firma con FIEL";
																						} 
																		});													
															$("#RechazoBancario").submit()	
														} else{
															cmdImprimir("PolizaPago");
															queryFormPost({ 
																			queryName : "EnviadoSICOPManual", 
																			    async : false, 
																			 callback : function(){ 
																							msn = "Pago enviado a Pagado Manual";
																						} 
																		});
														}
													} 
												});								
									
									reloadtable();
									$("#divSolicitud").hide();
									$( this ).dialog( "close" );
								}
							})								
						},
						
						"Borrar Oficios": function() {
							$("#nOficioVoBo").val("Sin Delego");
							$("#nOficioAut").val("Sin Delego");
							
							queryFormPost("tPagoFirmanteDelagatorioRead", {
							async : false
							}); 
							
							queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {
								async : false
							}); 
							
							if( $("#firmanteOficioVoBoExiste").val() == "Existe" ){
								queryFormPost({
												queryName : "borrarOficioDelegatorioVoBo", 
												    async : false, 
												 callback : function(){ 
																msn = "Firmantes Oficio Delegatorio VoBo eliminado correctamente";
															} 
											});								
							}
							
							if( $("#firmanteOficioExiste").val() == "Existe" ){
								queryFormPost({
												queryName : "borrarOficioDelegatorio", 
												    async : false, 
												 callback : function(){ 
																msn = "Firmantes Oficio Delegatorio eliminado correctamente.";
															} 
											});
							}									
							reloadtable(); 	
							Actualizar();
						}, 
							
						"Cancelar": function() {
							reloadtable();
							$("#divSolicitud").hide();
							$( this ).dialog( "close" );														
						}
																				
					},
				close: function() {
						
						parent.document.getElementById("pb_save").disabled=false;
						
				},
				open: function(){
						queryFormPost("tPagoFirmanteDelagatorioRead", {
							async : false
						}); 
						
						queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {
							async : false
						}); 
						
						if( $("#firmanteOficioVoBoExiste").val() == "Existe" ){
							$("#oficioDeleVoBo").attr( "checked", "checked" );
							showDivOficioVoBo( false );
						}
						
						if( $("#firmanteOficioExiste").val() == "Existe" ){							
							$("#oficioDelegatorio").attr( "checked", "checked" );
							showDivOficio(false);
						}
						
				}							
			});
        }	 
				
		function cat_CTASbeneficiario(){					
			DestinoGasto = $("#DESTINO_GASTO").val();					
			tipoRelacion = DestinoGasto.substring(2,4);
			rfc = $("#nRFC").val();
			
			if (((tipoRelacion=="RG" || tipoRelacion=="RP" || tipoRelacion=="SU") && ($("#DESTINO_GASTO").val()!="GCRG") && $("#DESTINO_GASTO").val()!="NARG" && $("#DESTINO_GASTO").val()!="2NRP" && $("#DESTINO_GASTO").val()!="2NFA") )
				$("#cTipoRfc").val("1,2");//PERSONA FISICA Y MORAL
			else if (tipoRelacion == "RE" && $("#DESTINO_GASTO").val() == "RCRE")
				$("#cTipoRfc").val("3"); //EMPLEADO CNF
			else if (tipoRelacion == "RE")
				$("#cTipoRfc").val("3"); //EMPLEADO CNF
			else if ($("#DESTINO_GASTO").val()=="GCRG")
				$("#cTipoRfc").val("0"); //CNF
			else if ($("#DESTINO_GASTO").val()=="NORN")
				$("#cTipoRfc").val("0,3"); //CNF
			else if ($("#DESTINO_GASTO").val()=="CBRB" || $("#DESTINO_GASTO").val()=="2NRP" || $("#DESTINO_GASTO").val()=="2NFA") //2% / Nomina
				$("#cTipoRfc").val("7"); //CNF
			else if($("#DESTINO_GASTO").val()=="NARG")
				$("#cTipoRfc").val("1,2,3"); 
					
			window.open('CatalogoCuentas.jsp?DESTINO_GASTO=' + DestinoGasto + '&formName=RechazoBancario' + '&RFC=' + rfc,   'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
		}
		
		function showDivOficio(esUpdate){
			var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
			var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
			if( $("#" + cmpName ).is(":checked") )
				$("#"+divName).show();
			else
				$("#"+divName).hide();
		}

		function showDivOficioVoBo(esUpdate){
			var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
			var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
			if( $("#" + cmpName ).is(":checked") )
				$("#"+divName).show();
			else
				$("#"+divName).hide();
		}
		
		function infoEmpleado( tipoFirmante ){
		
			$("#cNombreEmpleado").val(); 
			$("#cPaternoEmpleado").val();
			$("#cMaternoEmpleado").val();
			$("#cPuestoEmpleado").val(); 
			$("#cTipoFirmante").val( tipoFirmante )
			
			var numeroEmpleado = -1;
			var postFijo = ""
			
			if( "VOBO" == tipoFirmante){
				numeroEmpleado = $("#cboVoBo").val();
				postFijo = "VoBo";
			}else if( "AUT" == tipoFirmante){
				numeroEmpleado = $("#cboAutoriza").val();
				postFijo = "Aut";
			}else if( "SUPAUT" == tipoFirmante){
				numeroEmpleado = $("#cboSuplenteAut").val();
				postFijo = "Titular";
			}else if( "SUPVOBO" == tipoFirmante){
				numeroEmpleado = $("#cboSuplenteVoBo").val();
				postFijo = "TitularVoBo";
			}
			
			limpiaFirmante(postFijo);
			
			if( parseInt( numeroEmpleado, 10 ) > 0 ){
				$("#nNumEmpleadoBusqueda").val( numeroEmpleado );		
				queryFormPost({ queryName:"infoComplementariaFirmanteRead", 
				                    async:false,
				                    callback:function(){
				                    	$("#cNombre" + postFijo).val( $("#cNombreEmpleado").val() );
				                    	$("#cPaterno" + postFijo).val( $("#cPaternoEmpleado").val() );
				                    	$("#cMaterno" + postFijo).val( $("#cMaternoEmpleado").val() );
				                    	$("#cPuesto" + postFijo).val( $("#cPuestoEmpleado").val() );
				                    }
				              });
			} 
	
		}
		
		function llenaFirmanteVoBo(){
			$("#cTipoFirmante").val("VOBO"); 
			querySelectPost("FirmantesPorTipo_Read", "cboVoBo",{async: false });
		}
		
		function llenaFirmanteAut(){
			$("#cTipoFirmante").val("AUT");
			querySelectPost("FirmantesPorTipo_Read", "cboAutoriza",{async: false });
		}
		
		function llenaSuplenteVoBo(){
			$("#cTipoFirmante").val("SUPVOBO");
			querySelectPost("FirmantesPorTipo_Read", "cboSuplenteVoBo",{async: false });			
		}
		
		function llenaSuplenteAut(){		
			$("#cTipoFirmante").val("SUPAUT");
			querySelectPost("FirmantesPorTipo_Read", "cboSuplenteAut",{async: false });
		}
		
		function Actualizar(){						
			$("#id_caso").val($("#nFolioPago").val());			
			$("#cFolioOficio").val("");
			$("#cFolioOficioVoBo").val("");			
			$("#dFechaOficio").val("");
			$("#dFechaOficioVoBo").val("");
			document.getElementById("oficioDelegatorio").checked = false;
			document.getElementById("oficioDeleVoBo").checked = false;
			$("#oficioDelegatorioCaptura").hide();
			$("#oficioDelegatorioVoBo").hide();
			
			llenaFirmanteVoBo();
			llenaFirmanteAut();
			llenaSuplenteVoBo();
			llenaSuplenteAut();
			$("#dialog-firmantes").dialog("open");									
		}
		
		function limpiaFirmante(postFijo){
			$("#cNombre" + postFijo).val( "" );
	        $("#cPaterno" + postFijo).val( "" );
	        $("#cMaterno" + postFijo).val( "" );
	        $("#cPuesto" + postFijo).val( "" );
		}
		
		function cmdImprimir(elFormato) {
			var swhere = "&folio=" + $("#nCXP").val();
			
			window.open("../admin/SeguridadCatalogos?" 
				+ "catalogo=CONTRARECIBO"
				+ "&accion=run" 
				+ "&rn=" + elFormato + ".jasper" 
				+ swhere,
				"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		
		}
															
		</script>

</head>

<body id="dt_example" >
	<form id="RechazoBancario" method="post" action="../fiel/SolicitaRefirma" > 
		<input type="hidden" name="DESTINO_GASTO" id="DESTINO_GASTO" value = ""/>
		<input type="hidden" name="cTipoRfc" id="cTipoRfc" value = "" />
		<input type="hidden" name="uUR" id="uUR" value = "<%=cUR %>" />	
		
		<input type="hidden" name="tipoTramite" id="tipoTramite" value = "11" />
		<input type="hidden" name="id_caso" id="id_caso" value = "" />
		<input type="hidden" name="FOLIO" id="FOLIO" value = "" />
		<input type="hidden" id="cDocumento" name="cDocumento" value="RELACIONGASTOS">
		<input type="hidden" id="DOCUMENT" name="DOCUMENT" value="RELACIONGASTOS">
		<input type="hidden" id="cTipoPago" name="cTipoPago" value="RELACIONGASTOS">
		
		<input type="hidden" id="cNombreEmpleado" name="cNombreEmpleado" value="">
		<input type="hidden" id="cPaternoEmpleado" name="cPaternoEmpleado" value="">
		<input type="hidden" id="cMaternoEmpleado" name="cMaternoEmpleado" value="">
		<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value="">
		<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value="">
		
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value = "<%=cUR %>" />
		<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value="">
		<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
		
		<input type="hidden" id="numeroEmpleadoVoBo" name="numeroEmpleadoVoBo" value="">
		<input type="hidden" id="numeroEmpleadoAutoriza" name="numeroEmpleadoAutoriza" value="">
		
		<!-- hidden para la captura de los datos de quien elaboro -->
		<input type="hidden" id="cNombreE" name="cNombreE" size=40>
		<input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
		<input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
		<input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
		<input type="hidden" id="firmanteEla" name="firmanteEla" size=40>
		
		<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
		<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
		<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
		<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
		<input type="hidden" id="cEmpleadoVo" name="cEmpleadoVo" size=40>
		<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
		
		<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
		<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
		<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
		<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
		<input type="hidden" id="cEmpleadoA" name="cEmpleadoA" size=40>
		<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
		
		<!-- hidden para los firmantes del VoBo y Autoriza -->
		<input type="hidden" name="cNombreVoBo" id="cNombreVoBo" value=""/>
		<input type="hidden" name="cPaternoVoBo" id="cPaternoVoBo" value=""/>
		<input type="hidden" name="cMaternoVoBo" id="cMaternoVoBo" value=""/>
		<input type="hidden" name="cPuestoVoBo" id="cPuestoVoBo" value=""/>
		<input type="hidden" name="cNombreAut" id="cNombreAut" value=""/>
		<input type="hidden" name="cPaternoAut" id="cPaternoAut" value=""/>
		<input type="hidden" name="cMaternoAut" id="cMaternoAut" value=""/>
		<input type="hidden" name="cPuestoAut" id="cPuestoAut" value=""/>
		
		<!-- hidden para la captura de oficio delegatorio VoBo-->
		<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
		<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
		<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
		<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
		<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
		<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
		<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">
		
		<!-- hidden para la captura de oficio delegatorio -->
		<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
		<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">
		<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
		<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
		<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
		<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
		<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
		<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
		<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value="">
		
		<!-- hidden para los firmantes de suplencia de VoBo y Autoriza --> 
		<input type="hidden" name="cNombreTitular" id="cNombreTitular" value=""/>
		<input type="hidden" name="cPaternoTitular" id="cPaternoTitular" value=""/>
		<input type="hidden" name="cMaternoTitular" id="cMaternoTitular" value=""/>
		<input type="hidden" name="cPuestoTitular" id="cPuestoTitular" value=""/>
		<input type="hidden" name="cNombreTitularVoBo" id="cNombreTitularVoBo" value=""/>
		<input type="hidden" name="cPaternoTitularVoBo" id="cPaternoTitularVoBo" value=""/>
		<input type="hidden" name="cMaternoTitularVoBo" id="cMaternoTitularVoBo" value=""/>
		<input type="hidden" name="cPuestoTitularVoBo" id="cPuestoTitularVoBo" value=""/>

		<div id="container" class="container" style="width: 65%" >		
		<br/>			
			<div class="card-header"><h3> Solicitudes para editar cuenta bancaria </h3></div>
			<hr class="mt-3"/>
											
			<table id="tblSolicitudes" class="table table-striped table-sm">
	            <thead>
	                <tr>
	                	<th>Folio</th>
	                	<th>CXP</th>
	                	<th>RFC</th>
	                	<th>Cuenta Bancaria</th>
	                	<th>Descripción</th>
	                    <th>Unidad</th>
	                    <th>Importe</th>
	                    <th>Destino Gasto</th>
	                    <th>FIEL</th>
	                    <th>Firma VoBo</th>
	                    <th>Puesto VoBo</th>
	                    <th>Firma Aut</th>
	                    <th>Puesto Aut</th>
	                    <th>Oficio Delga VoBo</th>
	                    <th>Oficio Delga Aut</th>	           
	                    <th>Nombre</th>
	                </tr>
	            </thead>
	        </table>	        
			<br/>
		
		</div>
	
		<div id="divSolicitud" title="Solicitud" style="width: 65%" class="container">						
			<h4> Editar </h4> <h5>Hacer clic en el boton de tres puntos para cambiar la cuenta bancaria</h5>
			<hr class="mt-3"/>
		
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">																						
					<label for="nFolioPago" class="form-label">Folio:&nbsp; </label>																												
				</div>
				<div class="col-2">																																	
				</div>						
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<label for="nCXP" class="form-label">CXP:&nbsp; </label>									
				</div>					
			</div>						
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-file-earmark-text"></i></span>																								
						<input type="text" name="nFolioPago" id="nFolioPago" class="form-control form-control-sm" readonly/>
					</div>
				</div>
				<div class="col-2">																																	
				</div>						
				<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-file-earmark-text"></i></span>									
						<input type="text" name="nCXP" id="nCXP" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="input-group">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">																
						<label for="nDescripcion" class="form-label">Descripcion:&nbsp; </label>																												
					</div>																																	
				</div>			
			</div>			
			<div class="row">
				<div class="input-group">						
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">																									
						<textarea name="nDescripcion" id="nDescripcion" class="form-control form-control-sm" readonly></textarea>
					</div>																												
				</div>
			</div>
						
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">																
					<label for="nUR" class="form-label">Unidad Responsable:&nbsp; </label>																												
				</div>	
				<div class="col-2">
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<label for="nImporte" class="form-label">Importe:&nbsp; </label>									
				</div>																								
			</div>				
			<div class="row">			
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-bookmark"></i></span>																									
						<input type="text" name="nUR" id="nUR" class="form-control form-control-sm" readonly/>
					</div>
				</div>				
				<div class="col-2">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">		
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>							
						<input type="text" name="nImporte" id="nImporte" class="form-control form-control-sm" readonly/>
					</div>
				</div>																										
			</div>
									
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">																
					<label for="nRFC" class="form-label">RFC:&nbsp; </label>																												
				</div>	
				<div class="col-2">
				</div>		
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">
					<label for="nCTAB" class="form-label">Cuenta Bancaria:&nbsp; </label>									
				</div>
			</div>				
			<div class="row">									
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">		
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>																																
						<input type="text" name="nRFC" id="nRFC" class="form-control AyudaSyC form-control-sm" readonly/>						
					</div>
				</div>	
				<div class="col-2">
				</div>								
				<div class="col-12 col-lg-3 col-md-2 col-sm-12">		
					<div class="input-group">							
						<span class="input-group-text"><i class="bi bi-bank"></i></span>	
						<input type="text" name="nCTAB" id="nCTAB" class="form-control form-control-sm" readonly/>
						<input type="button" name="btnCTA" id="btnCTA" value="..." size="5" onclick="cat_CTASbeneficiario()">
					</div>
				</div>
			</div>		
			
			<div class="row">				
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">																
					<label for="nRFC" class="form-label">Nombre:&nbsp; </label>																												
				</div>
			</div>
			<div class="row">									
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">						
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>			
						<input type="text" name="nNombre" id="nNombre" class="form-control form-control-sm" readonly/>
					</div>				
				</div>
			</div>
			
			<br/>
			
			<h5> Firmas </h5>
			<hr class="mt-3"/>
			
			<div class="row">				
				<div class="col-12 col-lg-4 col-md-4 col-sm-12">																
					<label for="cEsFIEL" class="form-label">Es Firma Electronica:&nbsp; </label>																												
				</div>				
			</div>
			<div class="row">													
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">									
					<input type="text" name="cEsFIEL" id="cEsFIEL" class="form-control form-control-sm" readonly/>
				</div>
			</div>
			
			<div class="row">				
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">								
					<label for="nRFC" class="form-label"> Firma VoBo:&nbsp; </label> 							 							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">								
					<label for="nRFC" class="form-label"> Puesto VoBo:&nbsp; </label> 															
				</div>
			</div>
			<div class="row">				
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">	
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>									
						<input type="text" name="nFirmaVoBo" id="nFirmaVoBo" class="form-control form-control-sm" readonly/>
					</div> 							 							
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">	
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>							
						<input type="text" name="nPuestoVoBo" id="nPuestoVoBo" class="form-control form-control-sm" readonly/>
					</div> 															
				</div>
			</div>
					
			<div class="row">				
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<label for="nOficioVoBo" class="form-label">Oficio Delegatorio VoBo:&nbsp; </label>									
				</div>																										
			</div>
			<div class="row">								
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
					<input type="text" name="nOficioVoBo" id="nOficioVoBo" class="form-control form-control-sm" readonly/>
				</div>																										
			</div>
			
			<div class="row">	
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<label for="nFirmaAut" class="form-label">Firma Aut:&nbsp; </label>									
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">									
					<label for="nPuestoAut" class="form-label">Puesto Aut:&nbsp; </label>						
				</div>																								
			</div>
			<div class="row">	
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-person"></i></span>		
						<input type="text" name="nFirmaAut" id="nFirmaAut" class="form-control form-control-sm" readonly/>
					</div>									
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">		
					<div class="input-group">
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>								
						<input type="text" name="nPuestoAut" id="nPuestoAut" class="form-control form-control-sm" readonly/>
					</div>
				</div>																								
			</div>

			<div class="row">			
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">
					<label for="nOficioAut" class="form-label">Oficio Delegatorio Aut:&nbsp; </label>									
				</div>																					
			</div>
			<div class="row">							
				<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
					<input type="text" name="nOficioAut" id="nOficioAut" class="form-control form-control-sm" readonly/>
				</div>																										
			</div>
		
			
			<br/>
			
			<div class="row" id="Actualizar">
				<div class="col-12">													
					<button type="button" id="btnGuardar" name="btnGuardar" class="btn btn-secondary btn-sm"> Guardar </button>						
				</div>
			</div>
		
			<br/>		
			
			<div id="dialog-firmantes" title="Firmantes">						
				<div class="row" id="AutorizaConFielTD">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">
						<div class="form-check">
							<input type="checkbox" name="autorizadoPorFielChk" id="autorizadoPorFielChk" class="form-check-input">
							<label for="autorizadoPorFielChk" class="form-check-label">Autorizar con Firma Electronica</label>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">
						<div class="form-check">
							<input type="checkbox" name="oficioDelegatorio" id="oficioDelegatorio" class="form-check-input" onclick="showDivOficio(false);">
							<label for="oficioDelegatorio" class="form-check-label">Oficio Delegatorio Autoriza</label>
						</div>
					</div>

					<div class="col-12 col-lg-6 col-md-6 col-sm-12">
						<div class="form-check">
							<input type="checkbox" name="oficioDeleVoBo" id="oficioDeleVoBo" class="form-check-input" onclick="showDivOficioVoBo(false);">
							<label for="oficioDeleVoBo" class="form-check-label">Oficio Delegatorio VoBo</label>
						</div>
					</div>
				</div>					
							
				<h2> Datos VºBº </h2> 
				<hr class="mt-1">	
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="cboVoBo" class="form-label">VoBo:&nbsp; </label>	
					</div>
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<div class="form-check">
							<select id="cboVoBo" name="cboVoBo" class="form-select form-select-sm" onchange="infoEmpleado('VOBO');"> </select>
						</div>
					</div>
				</div>					
				
				<h2> Datos Autoriza </h2>				
				<hr class="mt-1">				
					
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						<label for="cboAutoriza" class="form-label">Autoriza:&nbsp; </label>	
					</div>
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<div class="form-check">
							<select id="cboAutoriza" name="cboAutoriza" class="form-select form-select-sm" onchange="infoEmpleado('AUT');"> </select>									
						</div>
					</div>
				</div>	
			
				<h2> Datos Elabora </h2>				
				<hr class="mt-1">
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label for="cNombreEla" class="form-label">Nombre:&nbsp; </label>	
					</div>
					<div class="col-12 col-lg-9 col-md-9 col-sm-12">
						<div class="form-check">
							<input type="text" name="cNombreEla" id="cNombreEla" class="form-control form-control-sm" value="<%=nombreElabora%>"/>									
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label for="cPaternoEla" class="form-label">Ap. Paterno:&nbsp; </label>	
					</div>
					<div class="col-12 col-lg-9 col-md-9 col-sm-12">
						<div class="form-check">
							<input type="text" name="cPaternoEla" id="cPaternoEla" class="form-control form-control-sm" value="<%=aPaternoElabora%>"/>									
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label for="cMaternoEla" class="form-label">Ap. Materno:&nbsp; </label>	
					</div>
					<div class="col-12 col-lg-9 col-md-9 col-sm-12">
						<div class="form-check">
							<input type="text" name="cMaternoEla" id="cMaternoEla" class="form-control form-control-sm" value="<%=aMaternoElabora%>"/>									
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label for="cPuestoEla" class="form-label">Puesto:&nbsp; </label>	
					</div>
					<div class="col-12 col-lg-9 col-md-9 col-sm-12">
						<div class="form-check">
							<input type="text" name="cPuestoEla" id="cPuestoEla" class="form-control form-control-sm" value="<%=puestoElabora%>"/>									
						</div>
					</div>
				</div>								
				
				<div id="oficioDelegatorioCaptura">
					<h2> Datos del Suplente </h2>				
					<hr class="mt-1">
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							<label for="cFolioOficio" class="form-label">No. Oficio:&nbsp; </label>	
						</div>
						<div class="col-12 col-lg-9 col-md-9 col-sm-12">
							<div class="form-check">
								<input type="text" name="cFolioOficio" id="cFolioOficio" class="form-control form-control-sm"/>									
							</div>
						</div>
					</div>
					
					<div class="row">		
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">
							<label for="dFechaOficio" class="form-label">Fecha Oficio:&nbsp; </label>	
						</div>			
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">										            	
	                       	<input type="text"	id="dFechaOficio" name="dFechaOficio" class="form-control form-control-sm" readonly>	                        	                               
						</div>
					</div>		
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							<label for="tipoSuplencia" class="form-label">Tipo de Suplencia:&nbsp; </label>	
						</div>
						<div class="col-12 col-lg-6 col-md-6 col-sm-12">
							<div class="form-check">
								<select id="tipoSuplencia" name="tipoSuplencia" class="form-select form-select-sm"> </select>									
							</div>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							<label for="cboSuplenteAut" class="form-label">Suplente Autoriza:&nbsp; </label>	
						</div>
						<div class="col-12 col-lg-9 col-md-9 col-sm-12">
							<div class="form-check">
								<select id="cboSuplenteAut" name="cboSuplenteAut" class="form-select form-select-sm" onchange="infoEmpleado('SUPAUT');"> </select>									
							</div>
						</div>
					</div>																							
				</div>
				
				<div id="oficioDelegatorioVoBo">
					<h2> Datos del Suplente VoBo</h2>				
					<hr class="mt-1">
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							<label for="cFolioOficioVoBo" class="form-label">No. Oficio:&nbsp; </label>	
						</div>
						<div class="col-12 col-lg-6 col-md-6 col-sm-12">
							<div class="form-check">
								<input type="text" name="cFolioOficioVoBo" id="cFolioOficioVoBo" class="form-control form-control-sm"/>									
							</div>
						</div>
					</div>
					
					<div class="row">		
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">
							<label for="dFechaOficioVoBo" class="form-label">Fecha Oficio:&nbsp; </label>	
						</div>			
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">
							<input type="text"	id="dFechaOficioVoBo" name="dFechaOficioVoBo" class="form-control form-control-sm" readonly>
						</div>
					</div>		
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							<label for="tipoSuplenciaVoBo" class="form-label">Tipo de Suplencia:&nbsp; </label>	
						</div>
						<div class="col-12 col-lg-6 col-md-6 col-sm-12">
							<div class="form-check">
								<select id="tipoSuplenciaVoBo" name="tipoSuplenciaVoBo" class="form-select form-select-sm"> </select>									
							</div>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
							<label for="cboSuplenteVoBo" class="form-label">Suplente Autoriza:&nbsp; </label>	
						</div>
						<div class="col-12 col-lg-9 col-md-9 col-sm-12">
							<div class="form-check">
								<select id="cboSuplenteVoBo" name="cboSuplenteVoBo" class="form-select form-select-sm" onchange="infoEmpleado('SUPVOBO');"> </select>									
							</div>
						</div>
					</div>																							
				</div>						
			</div>			
		</div>														
	</form>	
	
</body>
</html>