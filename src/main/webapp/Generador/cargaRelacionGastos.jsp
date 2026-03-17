<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.obrapublica.EjercicioFiscal"%>
<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);
	EjercicioFiscal aEjercicioFiscal = efbl.getEjercicioFiscalActivo();
	String ejercicioFiscal = aEjercicioFiscal.getaEjercicioFiscal();
	
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	String ur = usuario.getU_UR();
	String login = usuario.getLogin();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat fe = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = fe.format(c1.getTime());

	String msgOperation = "";
	String mensaje = (request.getParameter("mensaje") == null)
			? "vacio"
			: request.getParameter("mensaje");
	System.out.println("mensaje_" + mensaje);
	
	String msgUpdate = "";
	String msgUpdateSICOP = "";
	
	if( session.getAttribute("MSG_RESP") != null ){
		msgUpdate = (String)session.getAttribute("MSG_RESP");
		session.removeAttribute("MSG_RESP");
	}
	
	if( session.getAttribute("MSG_RESP_SICOP") != null ){
		msgUpdateSICOP = (String)session.getAttribute("MSG_RESP_SICOP");
		session.removeAttribute("MSG_RESP_SICOP");
	}
	
	if( session.getAttribute("msg") != null ){
		msgOperation = (String)session.getAttribute("msg");
		session.removeAttribute("msg");
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>

<title>Carga Layout Relacion Gastos</title>

	
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
	<link rel="stylesheet" type="text/css" href="../plantillasCasos/ComponentesPago/CSS/EgresoFirmantes.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		
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
	<script type="text/javascript" src="../plantillasCasos/ComponentesPago/js/EgresoFirmantes.js"></script>
	<script type="text/javascript" src="js/validaciones.js"></script>
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>

<script type="text/javascript">
	var urUsuario = "<%=ur%>";
	var loginUsuario = "<%=login%>";
	var oTableCargas;
	var oTableCargasPendientesDoc;
	var oTableDetalle;
	var oTableDetallePendientesDoc;
	var oTableCargasPendientes;
	var oTableDetallePendientes;
	var nombreElaboro = "<%=e.getNombre()%>";
	var aPaternoElaboro = "<%=e.getApellidoPaterno()%>";
	var aMaternoElaboro = "<%=e.getApellidoMaterno()%>";
	var puestoElaboro = "<%=e.getCargo()%>";
	var msgUpdate = "<%=msgUpdate%>";
	var msgUpdateSICOP = "<%=msgUpdateSICOP%>";
	
	
	var es_mx = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
		sLoadingRecords : "Cargando...",
		sInfo : "Registros _START_ al _END_ de _TOTAL_",
		sInfoEmpty : "Registro 0 al 0 de 0",
		sInfoFiltered : "(filtered from _MAX_ total entries)",
		sInfoPostFix : "",
		sInfoThousands : ",",
		sSearch : "Filtro:",
		oPaginate : {
			sFirst : "Primero",
			sPrevious : "Ant.",
			sNext : "Sigte.",
			sLast : "&Uacute;ltimo"
		}
	};
	
	$(document).ready(function(){
		
		$("#btnExportPendientes").button();
		$("#btnRechazaMasivo").button();
		$("#btnEnviarSICOPMasivo").button();
		$("#btnEnviarSICOPMasivo").hide();
		
		$("#cNombreEla").val(nombreElaboro);
		$("#cPaternoEla").val(aPaternoElaboro);
		$("#cMaternoEla").val(aMaternoElaboro);
		$("#cPuestoEla").val(puestoElaboro);
		querySelectPost("catTipoSuplenciaRead", "tipoSuplencia",{async: false });
		var mensaje = "<%=mensaje%>";

		$( "#btnAplicar" ).hide();
		$(".fiel").each(function(){
			$( this ).hide();
		});

		if( mensaje != "vacio" ) {

			var msgOriginal = mensaje;
			var ar = mensaje.split( ":" );
			mensaje = ar[ 0 ];
			var folio = ar[ 1 ];

			if( mensaje == "Error" ) {

				Swal.fire( "Revise","Verificar el Archivo Cargado : " + msgOriginal , "info");

			} else if( mensaje == "Detalle" ) {

				Swal.fire("Revise", "El Archivo Se cargo con algunos Detalles al momento de su Validacion \n Corregir y Cargar " + folio , "info");
				informacionCargadaTemp( folio );

			} else {

				Swal.fire("OK", "Archivo Cargado Correctamente con Folio " + folio , "success");
				informacionCargadaTemp( folio );
				$( "#btnAplicar" ).show();
				$(".fiel").each(function(){
					$( this ).show();
				});
			}

			mensaje = "vacio";
			$( "#folioTempGral" ).val( folio );
			$( "#dialog-procesar" ).dialog( "close" );

		}

		$("#esFIEL").click(function(){
			habilitaFirmantes();
		});
		
		$( "#btnEnviarLayout" ).button().click( function() {
			cargarInformacion( this.form.flRelacionGastos.value );
		} );

		$( "#btnAplicar" ).button().click( function() {
			if( !$("#esFIEL").attr("checked") ){
				Swal.fire({
					  title: 'Desea continuar?',
					  text: "Se firmará con firma AUTOGRAFA",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						  $.blockUI();
							aplicarInformacion();
					  } 
					})
			}else{
				if( validaFirmantes() ){
					$.blockUI();
					aplicarInformacion();
				}
			}
			
		} );
		
		$(function() {
			$( "#FechaFinal" ).datepicker({
				dateFormat: "yy/mm/dd",
				autoclose: true,
				changeYear: true,
				changeMonth: true	
			});
		});

		$( "#tabs" ).tabs( {} );
		$("#tabs-5").hide();
		
		if (urUsuario == "A02" )
			$("#tabs-5").show();
		
		querySelectPost( "tEjercicioRead", "aEjercicioFiscal", {
			async : false
		} );
		
		queryFormPost("FechaFinalRead", {async: false });
		
		$("#FechaFinal").val(moment().format('yyyy-MM-DD'));
		$("#fAplicacion").val(moment().format('yyyy-01-01'));
	
		$( '#tblCargaDatos' ).dataTable( {
			iDisplayLength : 20,
			ScrollY : "900px",
			sScrollX : "980px",
			bPaginate : false,
			//bLengthChange : false,
			bFilter : true,
			bSort : true,
			bInfo : true,
			bAutoWidth : false,
			bJQueryUI : true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType : "full_numbers",
			oLanguage : es_mx
		} );

		$( '#dialog-procesar' ).dialog( {
			autoOpen : false,
			modal : true,
			resizable : true,
			width : 500,
			heigth : 900,
			title : 'Procesando',
			show : "blind",
			hide : "scale",
			closeOnEscape : true,
			overlay : {
				backgroundColor : '#FFF',
				opacity : 6.5
			}

		} );

		creaDTCargasMasivas();
		creaDTCargasMasivasDoc();
		creaDTCargasMasivasPendientes();

		$( "#dialog-firmantes" ).dialog( {
			autoOpen : false,
			height : 490,
			width : 700,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					if( validaCapturaFirmantes() ){
						Swal.fire({
									  title: '¿Desea continuar?',
									  text: "Se actualizarán los firmantes de la carga masiva.",
									  icon: 'warning',
									  showCancelButton: true,
									  confirmButtonColor: '#288BA8',
									  cancelButtonColor: '#e6e6e6',
									  confirmButtonText: 'Aceptar',
									  cancelButtonText: 'Cancelar'
									}).then((result) => {
									  if (result.isConfirmed) {
										  setValoresFirmantes();
										  $.blockUI({message: "Procesando espere ......"});
											$("#frmLayoutRG").submit();
									  } 
									})
					}
				},
				"Cancelar" : function() {
					$( this ).dialog( "close" );
				}
			},
			close : function() {}
		} );

		$("#oficioDelegatorioCaptura").hide();
		llenaFirmanteVoBo();
		llenaFirmanteAut();
		llenaSuplenteAut();
		 
		if( msgUpdate != "" ){
			$( "#tabs-4" ).click();
			alert( msgUpdate );
		}
		
		if( msgUpdateSICOP != "" ){
			$( "#tabs-5" ).click();
			alert( msgUpdateSICOP );
		}
		
		
		
		// Radio Buttons
		$("#rdoBecas").prop("checked", false);
		$("#rdoBrigadistas").prop("checked", false);
		$("#rdoCertificado").prop("checked", false);
		$("#rdoLaudos").prop("checked", false);
		$("#rdoComprobacionGasto").prop("checked", false);
		$("#rdoBoxLunch").prop("checked", false);
		
		$("#rdoBecas").change(function() {
			if ($("#rdoBecas").prop("checked")) {
				$("#tipoCarga").val("1");
			}
		});
		
		$("#rdoLaudos").change(function() {
			if ($("#rdoLaudos").prop("checked")) {
				$("#tipoCarga").val("5");
			}
		});
		
		
		$("#rdoBrigadistas").change(function() {
			if ($("#rdoBrigadistas").prop("checked")) {
				$("#tipoCarga").val("2");
			}
		});
		
		$("#rdoCertificado").change(function() {
			if ($("#rdoCertificado").prop("checked")) {
				$("#tipoCarga").val("3");
			}
		});
		
		$("#rdoComprobacionGasto").change(function() {
			if ($("#rdoComprobacionGasto").prop("checked")) {
				$("#tipoCarga").val("6");
			}
		});	
		
		$("#rdoBoxLunch").change(function() {
			if ($("#rdoBoxLunch").prop("checked")) {
				$("#tipoCarga").val("7");
			}
		});	
		
		<%if(StringUtils.isNotBlank( msgOperation )){%>
			Swal.fire('Nota', "<%=msgOperation%>", 'info');
		<%}%>
				
	} );

	function cargarInformacion( archivo ) {
		$("#fAplicacion").val($("#FechaFinal").val().split('-').reverse().join('/'));
		
		if(validaSeleccionOpciones()){
			
			var ext = new Array( ".csv" );
			var correcto = false;
			var extension = ( archivo.substring( archivo.lastIndexOf( "." ) ) ).toLowerCase();
	
			if( ext[ 0 ] == extension ) {
				correcto = true;
			}
	
			if( !correcto ) {
	
				Swal.fire( "Revise extension","Comprueba la extensión del archivo a subir." , "info");
				return;
	
			} else {
						Swal.fire({
							  title: 'Desea continuar?',
							  text: "Se cargará el archivo seleccionado.",
							  icon: 'warning',
							  showCancelButton: true,
							  confirmButtonColor: '#288BA8',
							  cancelButtonColor: '#e6e6e6',
							  confirmButtonText: 'Aceptar',
							  cancelButtonText: 'Cancelar'
							}).then((result) => {
							  if (result.isConfirmed) {
								  $.blockUI();
									$( "#frmLayoutRG" ).attr( "action", "../gstnmngr/SubirArchivoRelacionGastosServlet" );
									$( "#frmLayoutRG" ).attr( "enctype", "multipart/form-data" );
					
									$( "#dialog-procesar" ).dialog( "open" );
									$( "#frmLayoutRG" ).submit();
							  } 
							})
			}	
				
		}
	}

	function informacionCargadaTemp( folio ) {

		$( '#tblCargaDatos' ).dataTable().fnClearTable();

		var szTabla = "tRELACIONGASTOSEncabezado_temp";
		var campos = " folioTempGral = " + folio;
		var elParametro = "";
		var order = "";

		$.getJSON( "../catalogos/SelectJson.jsp", {
		Tabla : szTabla,
		Campos : campos,
		Param : elParametro,
		MaxReg : "10",
		Order : order,
		ajax : 'true'
		}, function( j ) {

			for( var i = 0; i < j.length; i++ ) {

				$( "#tblCargaDatos" ).dataTable().fnAddData( [ j[ i ].Col0, j[ i ].Col1, j[ i ].Col2, j[ i ].Col3, j[ i ].Col4 ] );

			}
		} );

	}

	function aplicarInformacion() {

		$( "#dialog-procesar" ).dialog( "open" );

		$.ajax( {
			url : './ejercidoPagadoValidar.jsp',
			type : 'post',
			async : true,
			data : $("#frmLayoutRG").serialize(),
			success : function( data ) {
			$.unblockUI();
			var detalle = data.estatus.split( ":" );
			if( detalle[ 0 ] == "correcto" ) {

				alert( "Aplicado Correctamente" );
				$( '#tblCargaDatos' ).dataTable().fnClearTable();
				$( "#folioTempGral" ).val( "" );
				$( "#btnAplicar" ).hide();
				 
				$( "#dialog-procesar" ).dialog( "close" );
				if( !$("#esFIEL").attr("checked") ){
					muestraCanoContrarrecibo( detalle[ 1 ] );
					$( "#tabs-3" ).click();
				}else{
					$(".fiel").each(function(){
						$( this ).hide();
					});
					$("#firmantesDiv").hide();
				}

			} else {

				$( "#dialog-procesar" ).dialog( "close" );

				if( detalle.length == 2 ) {

					alert( detalle[ 1 ] );

				} else if( detalle.length == 3 ) {

					alert( detalle[ 2 ] );

				} else {

					alert( data.estatus );
				}
			}
		}

		} );

	}

	function muestraCanoContrarrecibo( folios ) {

		$( "#txtDif" ).val( "" );
		$( "#txtDif" ).val( folios );

	}

	function creaDTCargasMasivasDoc(){
		
		var cond = "cunidadresponsable IN ( SELECT ur FROM dbo.tVistasUR WHERE usuario = '" + loginUsuario + "' AND modulo = 'TESORERIA' )";
		
		oTableCargasPendientesDoc = $( '#dtCargasMasivasPendientesDoc' ).dataTable( {
		"bPaginate" : false,
		//"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=v_CargaMasivaRG_PendientesDoc&qw=" + cond,
		aoColumns : [ {
			sName : "FolioCargaMasiva"
		}, {
			sName : "fechaAplicacion"
		}, {
			sName : "fechaCarga"
		}, {
			sName : "cunidadresponsable"
		}, {
			sName : "importeCarga"
		}, {
			sName : "totalCargadas"
		} ],
		oLanguage : es_mx,
		"fnInitComplete": function(oSettings, json) {
							$("#nFolioCargaMasiva").val("");
						  },
		"fnDrawCallback": function( oSettings ) {
							$("#nFolioCargaMasiva").val("");
				   		  }
			  					 
		 });
		
		$( "#dtCargasMasivasPendientesDoc tbody" ).click( function( event ) {

			$( oTableCargasPendientesDoc.fnSettings().aoData ).each( function() {
				$( this.nTr ).removeClass( 'row_selected' );
			} );

			$( event.target.parentNode ).addClass( 'row_selected' );

			var aPos = oTableCargasPendientesDoc.fnGetPosition( event.target.parentNode );
			var aData = oTableCargasPendientesDoc.fnGetData( aPos );

			var nFolioRG = aData[ 0 ];
			$("#nFolioCargaMasiva").val(nFolioRG);
			creaDTDetalleCargasMasivasPendientesDoc( nFolioRG );
			
		} );

		$("#btnGeneraArchivos").button().click(function(){
			generarArchivos();
		});
		creaDTDetalleCargasMasivasPendientesDoc( -1 );

	 
	}
	function generarArchivos(){
		
		if(  $("#nFolioCargaMasiva").val() == "" )
			Swal.fire('Atencion', "Debe seleccionar una carga masiva para generar sus archivos.", 'info');
		if( confirm("Desea generar los documentos para firma electronica?") ){	
			$( "#frmLayoutRG" ).attr( "action", "../fiel/generateMasiveDocs" );
			$.blockUI();
			$( "#frmLayoutRG" ).submit();
		}
	}
	
	/*VGC20160825 Se crea data table que muestra las cargas masivas para editar los firmantes.*/
	function creaDTCargasMasivas() {

		var cond = "cunidadresponsable IN ( SELECT ur FROM dbo.tVistasUR WHERE usuario = '" + loginUsuario + "' AND modulo = 'TESORERIA' ) AND cEsFirmaElectronica = 'N'";
		oTableCargas = $( '#dtCargasMasivas' ).dataTable( {
		"bPaginate" : false,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=v_CargaMasivaRG&qw=" + cond,
		aoColumns : [ {
			sName : "FolioCargaMasiva"
		}, {
			sName : "fechaAplicacion"
		}, {
			sName : "fechaCarga"
		}, {
			sName : "cunidadresponsable"
		}, {
			sName : "importeCarga"
		}, {
			sName : "totalCargadas"
		} ],
		oLanguage : es_mx,
		"fnInitComplete": function(oSettings, json) {
							$("#nFolioCargaMasiva").val("");
						  },
		"fnDrawCallback": function( oSettings ) {
							$("#nFolioCargaMasiva").val("");
				   		  }
			  					 
		 });
		
		$( "#dtCargasMasivas tbody" ).click( function( event ) {

			$( oTableCargas.fnSettings().aoData ).each( function() {
				$( this.nTr ).removeClass( 'row_selected' );
			} );

			$( event.target.parentNode ).addClass( 'row_selected' );

			var aPos = oTableCargas.fnGetPosition( event.target.parentNode );
			var aData = oTableCargas.fnGetData( aPos );

			var nFolioRG = aData[ 0 ];
			$("#nFolioCargaMasiva").val(nFolioRG);
			creaDTDetalleCargasMasivas( nFolioRG );
			
		} );

		creaDTDetalleCargasMasivas( -1 );

		$( "#btnFirmantes" ).button().click( function() {
			muestraDLGFirmantes();
		} );
		$( "#btnExport" ).button().click( function() {
			exportaSolicitudes();
		} );
				
	}
	
	/*ARLA12052022 se crea la tabla para ver cargs masivas pendientes de enviar a SICOP.*/
	function creaDTCargasMasivasPendientes() {
		
		oTableCargasPendientes = $( '#dtCargasMasivasPendientes' ).dataTable( {
		"bPaginate" : false,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=v_CargaMasivaRGPendienteSICOP",
		aoColumns : [ {
			sName : "FolioCargaMasiva"
		}, {
			sName : "fechaAplicacion"
		}, {
			sName : "fechaCarga"
		}, {
			sName : "cunidadresponsable"
		}, {
			sName : "importeCarga"
		}, {
			sName : "totalCargadas"
		} ],
		oLanguage : es_mx,
		"fnInitComplete": function(oSettings, json) {
							$("#nFolioCargaMasiva").val("");
						  },
		"fnDrawCallback": function( oSettings ) {
							$("#nFolioCargaMasiva").val("");
				   		  }
			  					 
		 });
		
		$( "#dtCargasMasivasPendientes tbody" ).click( function( event ) {

			$( oTableCargasPendientes.fnSettings().aoData ).each( function() {
				$( this.nTr ).removeClass( 'row_selected' );
			} );

			$( event.target.parentNode ).addClass( 'row_selected' );

			var aPos = oTableCargasPendientes.fnGetPosition( event.target.parentNode );
			var aData = oTableCargasPendientes.fnGetData( aPos );

			var nFolioRG = aData[ 0 ];
			$("#nFolioCargaMasiva").val(nFolioRG);			
			creaDTDetalleCargasMasivasPendientes( nFolioRG );
			
			queryFormPost("existeAdjuntoAlimentacion", {async: false });		
			
			if ($("#existeAdjunto").val() == "0")
				$("#btnEnviarSICOPMasivo").hide();
			else
				$("#btnEnviarSICOPMasivo").show();
			
		} );
	
	}

	
	function creaDTDetalleCargasMasivasPendientesDoc( idCargaMasiva ) {

		var condDTDetalle = " folio = " + idCargaMasiva;

		oTableDetallePendientesDoc = $( "#dtDetalleCargaPendientesDoc" ).dataTable( {
		"bPaginate" : false,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=v_CargaMasivaDetalle&qw=" + condDTDetalle,
		aoColumns : [ {
			sName : "cidrelacion"
		}, {
			sName : "cidrfc"
		}, {
			sName : "cnombre"
		}, {
			sName : "cconcepto"
		}, {
			sName : "mimportemasiva"
		} ],
		oLanguage : es_mx
		} );

	}
	
	function creaDTDetalleCargasMasivas( idCargaMasiva ) {

		var condDTDetalle = " folio = " + idCargaMasiva;

		oTableDetalle = $( "#dtDetalleCarga" ).dataTable( {
		"bPaginate" : false,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bScrollCollapse" : true,
		"bServerSide" : true,
		"scrollX" : true,
		"sScrollX" : "1024",
		"sScrollY" : "300",
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=v_CargaMasivaDetalle&qw=" + condDTDetalle,
		aoColumns : [ {
			sName : "cidrelacion"
		}, {
			sName : "cidrfc"
		}, {
			sName : "cnombre"
		}, {
			sName : "cconcepto"
		}, {
			sName : "mimportemasiva"
		} ],
		oLanguage : es_mx
		} );

	}
	
	function creaDTDetalleCargasMasivasPendientes( idCargaMasiva ) {

		var condDTDetalle = " folio = " + idCargaMasiva;

		oTableDetallePendientes = $( "#dtDetalleCargaPendientes" ).dataTable( {
		"bPaginate" : false,
		//"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"sPaginationType" : "full_numbers",
		"bScrollCollapse" : true,
		"bServerSide" : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split( "/" )[ 1 ] + "/crud?rt=t&ql=v_CargaMasivaDetallePendienteSICOP&qw=" + condDTDetalle,
		aoColumns : [ {
			sName : "caNoContrarrecibo"
		},{
			sName : "FolioRG"
		},{
			sName : "cidrfc"
		}, {
			sName : "cnombre"
		}, {
			sName : "cconcepto"
		}, {
			sName : "CTAB"
		}, {
			sName : "EP"
		}, {
			sName : "mimportemasiva"
		}, {
			sName : "link"
		} ],
		oLanguage : es_mx
		} );
									
	}
	
	function muestraDLGFirmantes(){
		$("#dialog-firmantes").dialog("open");
	}
	
	function showDivOficio(esUpdate){
		var cmpName = "oficioDelegatorio";
		var divName = "oficioDelegatorioCaptura";
		
		if( $("#" + cmpName ).is(":checked") ){
			$("#"+divName).show();
			$("#esOficioDelegatorio").val("true");
		}else{
			$("#"+divName).hide();
			$("#esOficioDelegatorio").val("false");
		}
			
	}
	
	function validaCapturaFirmantes() {
		if( $("#nFolioCargaMasiva").val() == "" ){
			Swal.fire("Seleccione", "Debe seleccionar una carga masiva para definir los firmantes." , "info");
			return false;
		} 
		if( $( "#cNombreVoBo" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Nombre en Datos Vº Bº" ,"info");
			return false;
		} else if( $( "#cPaternoVoBo" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Apellido Paterno en Datos Vº Bº" , "info");
			return false;
		} else if( $( "#cMaternoVoBo" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Apellido Materno en Datos Vº Bº", "info" );
			return false;
		} else if( $( "#cPuestoVoBo" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Puesto en Datos Vº Bº", "info" );
			return false;
		}

		if( $( "#cNombreAut" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Nombre en Datos Autorizar", "info" );
			return false;
		} else if( $( "#cPaternoAut" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Apellido Paterno en Datos Autorizar" , "info");
			return false;
		} else if( $( "#cMaternoAut" ).val() == "" ) {
			Swal.fire( "Capture", "Falta Ingresar Apellido Materno en Datos Autorizar" , "info");
			return false;
		} else if( $( "#cPuestoAut" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Puesto en Datos Autorizar" , "info");
			return false;
		}

		if( $( "#cNombreEla" ).val() == "" ) {
			Swal.fire( "Capture", "Falta Ingresar Nombre en Datos Elabora" , "info");
			return false;
		} else if( $( "#cPaternoEla" ).val() == "" ) {
			Swal.fire( "Capture","Falta Ingresar Apellido Paterno en Datos Elabora", "info" );
			return false;
		} else if( $( "#cMaternoEla" ).val() == "" ) {
			Swal.fire( "Capture", "Falta Ingresar Apellido Materno en Datos Elabora", "info" );
			return false;
		} else if( $( "#cPuestoEla" ).val() == "" ) {
			Swal.fire( "Capture", "Falta Ingresar Puesto en Datos Elabora", "info" );
			return false;
		}

		if( $( "#oficioDelegatorio" ).prop( "checked" ) ) {

			if( $( "#cFolioOficio" ).val() == "" ) {
				Swal.fire( "Capture", "Falta Ingresar el folio de Oficio.","info" );
				return false;
			} else if( $( "#dFechaOficio" ).val() == "" ) {
				Swal.fire( "Capture","Falta Ingresar la fecha del Oficio.", "info" );
				return false;
			} else if( $( "#cNombreTitular" ).val() == "" ) {
				Swal.fire( "Capture", "Falta Ingresar Nombre del Titular." , "info");
				return false;
			} else if( $( "#cApellidoPaternoTitular" ).val() == "" ) {
				Swal.fire( "Capture","Falta Ingresar Apellido Paterno del Titular." , "info");
				return false;
			} else if( $( "#cApellidoMaternoTitular" ).val() == "" ) {
				Swal.fire( "Capture","Falta Ingresar Apellido Materno del Titular." ,"info");
				return false;
			} else if( $( "#cPuestoTitular" ).val() == "" ) {
				Swal.fire( "Capture", "Falta Ingresar Puesto del Titular.", "info" );
				return false;
			}

		}
		
		return true;
		
	}
	
	function setValoresFirmantes() {

		$( "#cNombreVo" ).val( $( "#cNombreVoBo" ).val() );
		$( "#cPaternoVo" ).val( $( "#cPaternoVoBo" ).val() );
		$( "#cMaternoVo" ).val( $( "#cMaternoVoBo" ).val() );
		$( "#cPuestoVo" ).val( $( "#cPuestoVoBo" ).val() );

		$( "#cNombreA" ).val( $( "#cNombreAut" ).val() );
		$( "#cPaternoA" ).val( $( "#cPaternoAut" ).val() );
		$( "#cMaternoA" ).val( $( "#cMaternoAut" ).val() );
		$( "#cPuestoA" ).val( $( "#cPuestoAut" ).val() );

		$( "#cNombreE" ).val( $( "#cNombreEla" ).val() );
		$( "#cPaternoE" ).val( $( "#cPaternoEla" ).val() );
		$( "#cMaternoE" ).val( $( "#cMaternoEla" ).val() );
		$( "#cPuestoE" ).val( $( "#cPuestoEla" ).val() );

		$( "#cFolioOficioAux" ).val( $( "#cFolioOficio" ).val() );
		$( "#dFechaOficioAux" ).val( $( "#dFechaOficio" ).val().split('-').reverse().join('/') );
		$( "#cNombreTitularAux" ).val( $( "#cNombreTitular" ).val() );
		$( "#cApellidoPaternoTitularAux" ).val( $( "#cApellidoPaternoTitular" ).val() );
		$( "#cApellidoMaternoTitularAux" ).val( $( "#cApellidoMaternoTitular" ).val() );
		$( "#cPuestoTitularAux" ).val( $( "#cPuestoTitular" ).val() );
		$( "#tipoSuplenciaAux" ).val( $( "#tipoSuplencia" ).val() );

	}
	
	function exportaSolicitudes(){
		if( $("#nFolioCargaMasiva").val() == "" ){
			Swal.fire("Seleccione", "Debe seleccionar una carga masiva para exportar" ,"info");
			return;
		}else{
			window.open("../ActualizaFirmantesMasivo?nFolioCargaMasiva=" + $("#nFolioCargaMasiva").val(), "_blank", "toolbar=no,scrollbars=no,resizable=no,top=500,left=500,width=150,height=150");
		}
	}
	
	function exportaSolicitudesPendientes(){		
		var bandera = 1;
		$("#pendientesSICOP").val(bandera); //bandera para identificar layout de pendientes de envio a SICOP
		
		if( $("#nFolioCargaMasiva").val() == "" ){
			Swal.fire("Seleccione", "Debe seleccionar una carga masiva para exportar" ,"info");
			return;
		}else{
			$("#frmLayoutRG").submit();
		}
	}
	
	function rechazoMasivoSolicitudes(){
		var bandera = 1;
		var tipoDocumento = "";
		var nFolio = "";	
		var data = $('#dtDetalleCargaPendientes').dataTable().fnGetNodes();
		var oTable = $('#dtDetalleCargaPendientes').dataTable();
		var aData = oTable.fnGetData();
		
		$("#rechazoSICOP").val(bandera); //bandera para identificar layout de pendientes de envio a SICOP
		
		if( $("#nFolioCargaMasiva").val() == "" ){
			Swal.fire("Seleccione", "Debe seleccionar una carga masiva para enviar solicitudes a SICOP" ,"info");
			return;
		} else {
						
			for(var i=0; i < data.length; i++){
				tipoDocumento += "RelacionGastos/";
				nFolio += aData[i][1] + "/";
			}
			
			$("#tipoDocumento").val(tipoDocumento);
			$("#nFolio").val(nFolio);
			
			if( confirm("Esta seguro de rechazar y cancelar el devengado de la carga masiva?") ){				
				$.blockUI({message: "Procesando espere ......"});
				$("#frmLayoutRG").submit();
			}					
		}
	}
	
	function enviarSolicitudesPendientesSICOP(){
		var bandera = 1;
		$("#envioSICOP").val(bandera); //bandera para identificar layout de pendientes de envio a SICOP
		
		if( $("#nFolioCargaMasiva").val() == "" ){
			Swal.fire("Seleccione", "Debe seleccionar una carga masiva para enviar solicitudes a SICOP" ,"info");
			return;
		} else {
			if( confirm("Esta seguro de enviar a Generar LayOut SICOP la carga masiva?") ){
				$.blockUI({message: "Procesando espere ......"});
				$("#frmLayoutRG").submit();
			}					
		}
	}
	
	
	function validaSeleccionOpciones(){
		var bReturn = true;
		
		if (!$("#rdoBecas").prop("checked") && !$("#rdoBrigadistas").prop("checked") && !$("#rdoCertificado").prop("checked") 
				&& !$("#rdoLaudos").prop("checked") && !$("#rdoComprobacionGasto").prop("checked") && !$("#rdoBoxLunch").prop("checked")){
			bReturn = false;
			Swal.fire("Seleccione", "Debe seleccionar una opcion: Becas, Alimentación a Brigadistas, etc..." ,"info");
			
		}
		
		return bReturn;
	}
	function llenaFirmanteVoBo(){
		$("#cTipoFirmante").val("VOBO") 
		querySelectPost("FirmantesPorTipo_Read", "cboVoBo",{async: false });
	}
	
	function llenaFirmanteAut(){
		$("#cTipoFirmante").val("AUT") 
		querySelectPost("FirmantesPorTipo_Read", "cboAutoriza",{async: false });
	}
	
	function llenaSuplenteAut(){
		$("#cTipoFirmante").val("SUPAUT") 
		querySelectPost("FirmantesPorTipo_Read", "cboSuplenteAut",{async: false });
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
	
	function limpiaFirmante(postFijo){
		$("#cNombre" + postFijo).val( "" );
        $("#cPaterno" + postFijo).val( "" );
        $("#cMaterno" + postFijo).val( "" );
        $("#cPuesto" + postFijo).val( "" );
	}
	
	/*VGC20200812 Cambios para agregar firma electronica*/
	function habilitaFirmantes(){
	
		if( $("#esFIEL").attr("checked") ){
		
			$("#firmaElectronica").attr("checked", true);
			$("#firmantesDiv").show();
			
			muestraFirmantes();
			muestraEditaFirmantes();
			
			$(".firmaElectronica").each(function() {
				$(this).show();
			});
			
			$("#firmaElectronica").hide();
			$("#firmaElectronicaLbl").hide();
			
			if( $("#nombreElabora").val() == "" ){
				$("#nombreElabora").val( $("#cNombreEla").val() + ' ' 
				                      +  $("#cPaternoEla").val() + ' ' 
				                      +  $("#cMaternoEla").val() ); 
				$("#puestoElabora").val($("#cPuestoEla").val());
			}
		}else{
			$("#firmantesDiv").hide();
		}
	}
	
	function DescargaAdjunto(idDocumento){
		url = "../SAIFilestore?select="+idDocumento;
	    window.open(url, "popacuse","scrollbars=1, resizable=yes, width=500, height=700");
	}
	
</script>
</head>
<br/>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<div id="container" class="ms-5" class="container" style="width: 90%"><!--Inicia div container-->		
		<form method="post" id="frmLayoutRG" name="frmLayoutRG" action="../ActualizaFirmantesMasivo">
			<input type="hidden" id="tabla" name="tabla" value="tRELACIONGASTOS"> 
			<input type="hidden" id="tipo" name="tipo" value="aplicarMasivo">
			<input type="hidden" id="cTipoPago" name="cTipoPago" value="RELACIONGASTOS">
			<input type="hidden" id="nFolioPago" name="nFolioPago" value="-1">
			<input type="hidden" id="cNombreEmpleado" name="cNombreEmpleado" value="">
			<input type="hidden" id="cPaternoEmpleado" name="cPaternoEmpleado" value="">
			<input type="hidden" id="cMaternoEmpleado" name="cMaternoEmpleado" value="">
			<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value="">
			<input type="hidden" name="cTipoFirmante" id="cTipoFirmante" value="false" />
			<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
			<input type="hidden" name="aEjercicioFiscal" id="aEjercicioFiscal" value = "<%=ejercicioFiscal%>"/>
			<input type="hidden" name="esOficioDelegatorio" id="esOficioDelegatorio" value="false" />
			<input type="hidden" name="nFolioCargaMasiva" id="nFolioCargaMasiva" value="" />
			<input type="hidden" name="cDocumento" id="cDocumento" value="RELACIONGASTOS" />
			<input type="hidden" name="mensaje" id="mensaje" value="JAJAJ" />
			<input type="hidden" name="cUnidadResponsable" id="cUnidadResponsable" value="<%=ur%>" />
			<input type="hidden" name="cUnidadResponsableContable" id="cUnidadResponsableContable" value="RHQ" />
			<input type="hidden" name="cCentroContable" id="cCentroContable" value="<%= cCentroContable%>" />
			<input type="hidden" name="cRamo" id="cRamo" value="16" />
			<input type="hidden" name="login" id="login" value="<%=login%>" />
			<input type="hidden" name="folioTempGral" id="folioTempGral" />
			<input type="hidden" name="tipoCarga" id="tipoCarga" value="0"/>
			<input type="hidden" name="pendientesSICOP" id="pendientesSICOP" value="0"/>
			<input type="hidden" name="envioSICOP" id="envioSICOP" value="0"/>
			<input type="hidden" name="rechazoSICOP" id="rechazoSICOP" value="0"/>
			<input type="hidden" name="tipoDocumento" id="tipoDocumento" value=""/>
			<input type="hidden" name="nFolio" id="nFolio" value=""/>
			<input type="hidden" name="existeAdjunto" id="existeAdjunto" />
			
			<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
			<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
			<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
			<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
			<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
			<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
			<input type="hidden" id="cNombreE" name="cNombreE" size=40>
			<input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
			<input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
			<input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
			<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
			<input type="hidden" name="fAplicacion" id="fAplicacion" value="">			
	
			<div class="card-header"> <h3> Carga Layout Relacion de Gastos </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row d-flex justify-content-center"><!--Inicio Div row para iniciar los tabs-->			
				<ul class="nav nav-tabs" id="list-opciones">
					<li class="nav-item" role="presentation">
						<button class="nav-link active" id="tabs-1" data-bs-toggle="tab" data-bs-target="#tabs-1-layout" type="button" role="tab" aria-controls="tabs-layout" aria-selected="true">Layout Relacion Gastos ( .csv )</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-2" data-bs-toggle="tab" data-bs-target="#tabs-2-pendiente" type="button" role="tab" aria-controls="tabs-pendiente" aria-selected="false">Carga Masiva Pendiente</button>
					</li>
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-3" data-bs-toggle="tab" data-bs-target="#tabs-3-cxp" type="button" role="tab" aria-controls="tabs-cxp" aria-selected="false">Cuentas Por Pagar</button>
					</li>								     
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-4" data-bs-toggle="tab" data-bs-target="#tabs-4-firmantes" type="button" role="tab" aria-controls="tabs-firmantes" aria-selected="false">Firmantes</button>
					</li>								     
					<li class="nav-item" role="presentation">
						<button class="nav-link" id="tabs-5" data-bs-toggle="tab" data-bs-target="#tabs-5-envio" type="button" role="tab" aria-controls="tabs-envio" aria-selected="false">Envio SICOP Masivo</button>
					</li>								     													            
				</ul>

				<div>
					<label id="esperar" style="visibility: hidden"> Espere por favor.... <img border="0" src="../imagenes/espera.gif" height="30"> </label>
				</div>
				
				<div class="tab-content mt-3" id="tabContent"><!--Inicio div contenido tabs-->		
					<div class="tab-pane fade show active" id="tabs-1-layout" role="tabpanel" aria-labelledby="tabs-layout"><!--Inicio tab-1-->
						<div class= "card">
							<div class="card-body"> 
								<div id="tblOpciones" class="row">
									<div class= "col-auto">
										<div class="form-check">
											<input type="radio" name="rdoBecas" id="rdoBecas" class="form-check-input" > Becas
										</div>
									</div>
									<div class= "col-auto">
										<div class="form-check">
											<input type="radio" name="rdoBecas" id="rdoBrigadistas" class="form-check-input" > Alimentacion a Brigadistas
										</div>
									</div>
									<div class= "col-auto">
										<div class="form-check">
										<input type="radio" name="rdoBecas" id="rdoCertificado" class="form-check-input" > Certificado de Transito
										</div>
									</div>
									<div class= "col-auto">
										<div class="form-check">
										<input type="radio" name="rdoBecas" id="rdoLaudos" class="form-check-input" > Comprobacion de Laudos
										</div>
									</div>
									<div class= "col-auto">
										<div class="form-check">
										<input type="radio" name="rdoBecas" id="rdoComprobacionGasto" class="form-check-input" > Comprobación de Gastos Extraordinarios
										</div>
									</div>
									<div class= "col-auto">
										<div class="form-check">
										<input type="radio" name="rdoBecas" id="rdoBoxLunch" class="form-check-input" > Juegos Deportivos
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class= "card">
							<div class="card-body"> 						
								<div id="tblCarga">
									<div class="row" >
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
											<label for="FechaFinal" class="form-label"> Fecha: </label>
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
											<div class="input-group">
												<span class="input-group date"><i class="datepicker1"></i></span>
												<input name="FechaFinal" type="date" id="FechaFinal" class="form-control form-control-sm" size="10" value="<%= today%>"/>
											</div>						            
										</div>										
										<div class="col-auto">
											Layout Relacion Gastos:
										</div>
										<div class="col-6">
											<input type="file" id="flRelacionGastos" name="flRelacionGastos" class="form-control" />
										</div>
									</div>
									<div class="row mt-2" >
										<div class="col-2">
											<input type="button" id="btnEnviarLayout" class="btn btn-secondary btn-sm" name="btnEnviarLayout" value="Cargar" />
										</div>
										<div class="col-2">
											<input type="button" id="btnAplicar" class="btn btn-secondary btn-sm" name="btnAplicar" value="Aplicar" />
										</div>
										<div class="col-4">
											<input type="checkbox" id="esFIEL" name="esFIEL" value="S" class="fiel"/>
											<label for"esFIEL" class="fiel">Firma Electronica</label>
										</div>
									</div>
								</div>
							</div>
						</div>
						<br>
							<h6>Informacion Validada</h6>
							<table id="tblCargaDatos" class="table table-striped">
								<thead>
									<tr align="center">
										<th>Relacion</th>
										<th>Importe</th>
										<th>RFC</th>
										<th>Fecha</th>
										<th>Estatus</th>
	
									</tr>
								</thead>
							</table>
					</div>
		
					<!-- SE muestran las cargas masivas que por error no se generaron sus archivos de firma electronica.  -->
					<div class="tab-pane fade show" id="tabs-2-pendiente" role="tabpanel" aria-labelledby="tabs-pendiente"><!--Inicio tab-2-->
						<div class="row">
							<div class="d-flex flex-row-reverse">
									<input type="button" id="btnGeneraArchivos"  class="btn btn-secondary btn-sm" value="Generar Archivos" alt="Generar Archivos"/>
							</div>
						</div>
						
						<h6>Cargas Masivas Pendientes de Envio a Firma.</h6>
						<table id="dtCargasMasivasPendientesDoc" class="table table-striped">
							<thead>
								<tr>
									<th>Folio Carga</th>
									<th>Fecha Aplicacion</th>
									<th>Fecha de Carga</th>
									<th>Unidad Ejecutora</th>
									<th>Importe Total</th>
									<th>Relaciones de Gastos</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
						
						<br>
						<h6>Detalle de Carga Masiva</h6>
						<table id="dtDetalleCargaPendientesDoc" class="table table-striped">
							<thead>
								<tr>
									<th>ID Relaci&oacute;n</th>
									<th>RFC</th>
									<th>Nombre</th>
									<th>Concepto</th>
									<th>Importe Total</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
					
					<div class="tab-pane fade show" id="tabs-3-cxp" role="tabpanel" aria-labelledby="tabs-cxp"><!--Inicio tab-3-->
						<div id="tblText" class="row">
							<div class="d-flex justify-content-center">
								<textarea rows="14" cols="80" id="txtDif" name="txtDif" readonly="readonly"></textarea>
							</div>
						</div>
					</div>
					
					<div class="tab-pane fade show" id="tabs-4-firmantes" role="tabpanel" aria-labelledby="tabs-firmantes"><!--Inicio tab-4-->
						<div class="row">
							<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" id="btnExport" class="btn btn-secondary btn-sm" value="Exportar Solicitudes" alt="Exporta en un zip las solicitudes de pago."/>									
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
								<input type="button" id="btnFirmantes"  class="btn btn-secondary btn-sm" value="Actualizar Firmantes" alt="Actualizar Firmantes"/>
							</div>
						</div>
						
							<h6>Cargas Masivas</h6>
							<table id="dtCargasMasivas" class="table table-striped">
								<thead>
									<tr>
										<th>Folio Carga</th>
										<th>Fecha Aplicacion</th>
										<th>Fecha de Carga</th>
										<th>Unidad Ejecutora</th>
										<th>Importe Total</th>
										<th>Relaciones de Gastos</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
						
						<br>
							<h6>Detalle de Carga Masiva</h6>
							<table id="dtDetalleCarga" class="table table-striped">
								<thead>
									<tr>
										<th>ID Relaci&oacute;n</th>
										<th>RFC</th>
										<th>Nombre</th>
										<th>Concepto</th>
										<th>Importe Total</th>
									</tr>
								</thead>
								<tbody></tbody>
							</table>
					
					</div>
					
					<div class="tab-pane fade show" id="tabs-5-envio" role="tabpanel" aria-labelledby="tabs-envio"><!--Inicio tab-5-->
						<div class="row">
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">															
								<input type="button" id="btnExportPendientes" class="btn btn-secondary btn-sm" value="Exportar Solicitudes" onclick="exportaSolicitudesPendientes()"/>
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
								<input type="button" id="btnEnviarSICOPMasivo" class="btn btn-secondary btn-sm" value="Enviar SICOP" onclick="enviarSolicitudesPendientesSICOP()"/>								
							</div>
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
								<input type="button" id="btnRechazaMasivo" class="btn btn-secondary btn-sm" value="Rechazar" onclick="rechazoMasivoSolicitudes()"/>									
							</div>							
						</div>
						<h6>Pendientes Envio SICOP</h6>
						<table id="dtCargasMasivasPendientes" class="table table-striped">
							<thead>
								<tr>
									<th>Folio Carga</th>
									<th>Fecha Aplicacion</th>
									<th>Fecha de Carga</th>
									<th>Unidad Ejecutora</th>
									<th>Importe Total</th>
									<th>Relaciones de Gastos</th>
								</tr>
							</thead>
							<tbody></tbody>
						</table>
						<br>
						<h6>Detalle de Carga Masiva</h6>
						<table id="dtDetalleCargaPendientes" class="table table-striped">
							<thead>
								<tr>
									<th>CxP</th>
									<th>Folio RG</th>
									<th>RFC</th>
									<th>Nombre</th>
									<th>Concepto</th>
									<th>Cuenta Bancaria</th>
									<th>EP</th>
									<th>Importe Total</th>						
									<th>Link</th>						
								</tr>
							</thead>
							<tbody></tbody>
						</table>
					</div>
				</div>
			</div>
			
			<div id="dialog-firmantes" title="Firmantes">
				<div class= "card">
					<div class="card-body">
						<h6>Datos VºBº</h6>
						<table>
							<tr>
								<td align="left"><input type="checkbox" class="form-check-input" id="oficioDelegatorio" name="oficioDelegatorio" onclick="showDivOficio(false)" value="true"/>Oficio delegatorio</td>
							</tr>
						</table>
						
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cboVoBo"> Firmante: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<select name="cboVoBo" id="cboVoBo" class="form-select form-select-sm" onchange="infoEmpleado('VOBO');">
								</select> 															
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cNombreVoBo"> Nombre: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cNombreVoBo" name="cNombreVoBo" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cPaternoVoBo"> Apellido Paterno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cPaternoVoBo" name="cPaternoVoBo" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cMaternoVoBo"> Apellido Materno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cMaternoVoBo" name="cMaternoVoBo" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cPuestoVoBo"> Puesto: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cPuestoVoBo" name="cPuestoVoBo" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>						
					</div>
				</div>
				<div class= "card">
					<div class="card-body">
						<h6>Datos Autoriza</h6>
						
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cboAutoriza"> Puesto: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<select name="cboAutoriza" id="cboAutoriza" class="form-select form-select-sm" onchange="infoEmpleado('AUT');">
							</select> 	
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cNombreAut"> Nombre: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cNombreAut" name="cNombreAut" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cPaternoAut"> Apellido Paterno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cPaternoAut" name="cPaternoAut" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cMaternoAut"> Apellido Materno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cMaternoAut" name="cMaternoAut" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cPuestoAut"> Puesto: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cPuestoAut" name="cPuestoAut" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
					</div>
				</div>
				<div class= "card">
					<div class="card-body">
						<h6>Datos Elabora</h6>
					
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cNombreEla"> Nombre: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cNombreEla" name="cNombreEla" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cPaternoEla"> Apellido Paterno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cPaternoEla" name="cPaternoEla" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cMaternoEla"> Apellido Materno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cMaternoEla" name="cMaternoEla" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cPuestoEla"> Puesto: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cPuestoEla" name="cPuestoEla" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
					</div>
				</div>
				
				<div id="oficioDelegatorioCaptura">
					<div class= "card">
						<div class="card-body">
						<h6>Datos del Suplente</h6>
						
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cFolioOficio"> No. de Oficio: </label> 							 							
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<input type="text" id="cFolioOficio" name="cFolioOficio" class="form-control form-control-sm"/>	
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cboAutoriza"> Fecha de Oficio: </label> 				 	
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">									
								<input name="dFechaOficio" type="date" id="dFechaOficio" class="form-control form-control-sm" size="10"/>															
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="tipoSuplencia"> Tipo de Suplencia: </label> 							 							
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<select id="tipoSuplencia" name="tipoSuplencia" class="form-select form-select-sm"></select>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cboAutoriza"> Suplente Autoriza: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<select name="cboAutoriza" id="cboAutoriza" class="form-select form-select-sm" onchange="infoEmpleado('SUPAUT');">
							</select> 	
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cNombreTitular"> Nombre Suplente: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cNombreTitular" name="cNombreTitular" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cApellidoPaternoTitular"> Apellido Paterno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cApellidoPaternoTitular" name="cApellidoPaternoTitular" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cApellidoMaternoTitular"> Apellido Materno: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cApellidoMaternoTitular" name="cApellidoMaternoTitular" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
						<div class="row">						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">								
								<label for="cPuestoTitular"> Puesto: </label> 							 							
							</div>
							<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
								<input type="text" id="cPuestoTitular" name="cPuestoTitular" class="form-control form-control-sm" size=40 maxlength="70"/>
							</div>
						</div>
					</div>
					</div>
				</div>
			</div>
			<br/>
			<div id="firmantesDiv" style="display:none">
				<div class= "card">
					<div class="card-body">
						<h5> Seleccione los Firmantes </h5>
						<hr class="mt-3">
						
						<jsp:include page="../plantillasCasos/ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
					</div>
				</div>
			</div>
		</form>
	</div>
	
</body>
</html>
