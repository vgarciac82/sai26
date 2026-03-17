<%@page import="org.apache.commons.lang.StringUtils"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>
<%
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String action = request.getParameter("a");
	int folio = Integer.parseInt(StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));
	String cTipoPago = request.getParameter("d");
	
	Map<?, ?> rol = null;
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	rol = usuario.getRoles();
	String tipoAutorizacion = ( "VoBoPago".equals(action) ? "VOBO" : ( "AutPago".equals(action) ? "AUT": "") );
	
	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	String numeroEmpleado = usuario.getNumeroEmpleado();
	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));
	boolean mostrarResultado = !StringUtils.isBlank(result);
	
	
	String fielMsg = "";
	boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null && ((Boolean)session.getAttribute("EXPIRING_SOON")); 
	if(fielExpiringSoon){
		fielMsg = (String) session.getAttribute("EXPIRING_MSG");
		session.removeAttribute("EXPIRING_SOON");
		session.removeAttribute("EXPIRING_MSG");
	}
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comisiones de Viáticos Sin Comprobación</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/demo_page.css"></link>
<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"	href="css/jquery-ui.min.css"></link>


<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="js/bootstrap.min.js"></script>
<script type="text/javascript" src="js/Moment.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.min.js"></script> 
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../plantillasCasos/ComponentesPago/js/EgresoFirmantes.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/validaciones.js"></script>
<script type="text/javascript" src="js/funciones.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript">

	
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
	   }
	var roles="";
		var cUR = "<%=cUR%>";
		var cLogin = "<%=cLogin%>";
		var RFC = "<%=RFCUsuario%>";
		var numeroEmpleado = "<%=numeroEmpleado%>";
		var mostrarResultado = <%=mostrarResultado%>;
		var mensaje = "<%=msg%>";
		var tipoAutorizacion = "<%=tipoAutorizacion%>";
		var folioComision = 0;
		let modalRechazo;
		let modalFiel;
		
	$(document).ready(function() {
				
		folioComision = $("#nFolioComision").val("<%=folio%>");
		modalRechazo = new bootstrap.Modal(document.getElementById('dlgMotivoRechazo'), 'data-bs-backdrop');
		modalFiel = new bootstrap.Modal(document.getElementById('dlg-FIEL'), 'data-bs-backdrop');
	
		cargarDatosComision();	
		creaDialogoLog();
		
		$("#cTipoPago").val("<%=cTipoPago%>");
			
		$("#rechazaPago").button().click(function(){
			rechazaPago();
		});
		
		$("#autorizaPago").button().click(function(){
			autorizaPago();
		});
		
		queryFormPost("estausAutorizacionPago_Read", {async:false});
			
		
		init();
		
		if( !mostrarResultado ){
				
				if( "VOBO" == tipoAutorizacion ){
					if( parseInt(  $("#nenviadosicop").val(), 10  ) == -2 ){
						$("#operacionesDiv").css("display","block");
					}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -2 ){
						Swal.fire("NOTA","Usted ya ha firmado este documento.","info");
					}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -2 ){
						Swal.fire("Estatus","El tramite se encuentra en estatus " + $("#cEstatus").val() ,"info");
					}
				}else if( "AUT" == tipoAutorizacion ){
					if( parseInt(  $("#nenviadosicop").val(), 10  ) == -1 ){
						$("#operacionesDiv").css("display","block");
					}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -1 ){
						Swal.fire("Nota","Usted ya ha firmado este documento.","info");
					}else if( parseInt(  $("#nenviadosicop").val(), 10  ) > -1 ){
						Swal.fire("Estatus","El tramite se encuentra en estatus " + $("#cEstatus").val() ,"info");
					}
				}
			}
			
			dialogoProcesar();
			
			if( mensaje != "" )
				Swal.fire("Error",mensaje,"error");
			
			$('#passwordLlave').prop('readonly', false);
			$('#cerFile').prop('readonly', false);
			$('#keyFile').prop('readonly', false);
			
			$('#passwordLlave').css('background-color', "#FFFFFF");
			
	});
	
	
	function init(){
		$("#cTipoPago").val("<%=cTipoPago%>");
	}
	
	function rechazaPago(){
		modalRechazo.show();

	} 	
	function onPostSubmit(id_oper) {
		var bRegresa = true;
		
		parent.execResponsable();
		parent.execOperacion();
			
		return bRegresa;
	}
	

	function onLoadPlantilla(id_oper) {
	 
		cargarDatosComision();	
	}

	
	function creaDialogoLog() {
		if( mostrarResultado )
			$("#logTable").css("display", "block");
	
		$("#dlg-Msg").dialog({
			autoOpen : mostrarResultado,
			height : 600,
			width : 800,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});
		
	}	
	
	function cargarDatosComision(){
		queryFormPost("vwComisionesSinComprobacionRead", {async:false});
		cargaDtBoletaje();
	}
	
	function cancelaSolComision(){
	
		if( $("#motivoRechazo").val() == "" ){
			Swal.fire("Capture","El motivo de rechazo es requerido.","info");
			return false;
		}
		
		
		Swal.fire({
			  title: '¿Desea continuar?',
			  text: "Se cancelará la comisión sin viáticos.",
			  icon: 'warning',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {	
						$.ajax({
							url : '../comisiones/CancelaComision',
							dataType : 'json',
							type:"post",
							data : {
								"chk_comision" : $("#nFolioComision").val(),
								"responseType" : "JSON",
								"reason":$("#motivoRechazo").val(),
								"autType":"FIEL"
							},
							async : false,
							success : function(json) {
								var exito = json.success;
								if( exito == "true" ){
								
									Swal.fire("OK","Solictud cancelada exitosamente", "success");
									modalRechazo.hide();
									$("#operacionesDiv").css("display", "none");
									
								}else{
								
									var msg = json.data_1.result;
									Swal.fire("Revise", "No fue posible cancelar la solicitud debido al error:  " +  msg, "error");
									
								}	
							},
							error : function(xhr, textStatus, errorThrown) {
								Swal.fire("Advertencia: ",  xhr.responseText + "\nEstatus: "
										+ textStatus + "\n" + errorThrown, "error");
							}
						});
			
				}else{
					$("#motivoRechazo").val("");
					modalRechazo.hide();
				}
			})
	}

  	
	 var dtBoletaje;
	
	 function cargaDtBoletaje(){
	 	dtBoletaje = 
	 	     $('#dt_Boletaje').dataTable({
			    "bPaginate": false,   
			    "bFilter": false,  
			    "bSort": true,
	        	"bInfo": false,  
	        	"bAutoWidth": false,  
	        	"bJQueryUI": true,
				"bRetrive" : true,  
				"bDestroy" : true,  
				"sPaginationType": "full_numbers",
				"sScrollX": "100%",
				"sScrollY": "100%",  
				"bScrollCollapse": true,  
				"bServerSide": true,   
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vw_ComisionesSinComprobacion&qw=nFolioComision="+folioComision.val(),
				aoColumns: [
					{ sName: "nFolioComision", "bVisible": false},
					{ sName: "cBoleto", "bVisible": true},
					{ sName: "mImporteBoleto", "bVisible": true},
					{ sName: "cPartida", "bVisible": true},
					{ sName: "cPartida as eliminar", "bVisible": false},
					{ sName: "RFCVuelo as RFC", "bVisible": false},
					{ sName: "cNombreRFC as Nombre", "bVisible": false},
					{ sName: "cPartida as Ruta", "bVisible": false}				
				],
				oLanguage: es_mx
		});				
	 }
	 
	
	 function cancelarDlg() {
			$("#nFolioComision").val("");
			modalFiel.hide();
			$(".dlgFielInpt").each(function() {
				$(this).val("");
			});
		}
		
		function aceptarDlg() {
			var msgValidaciones = validaCamposCompletos();
			if( "" == msgValidaciones ) {
				var msg = "Esta a punto de aceptar el proceso de los pagos con folio: " + $("#nFolioComision").val();
				if( confirm(msg) ) {
					$("#tipoPagoSeleccionado").val( $("#cTipoPago").val() );
					$("#autorizaLayouts").submit();
					modalFiel.hide();
					$.blockUI({
						message : "<h1>Espere ...</h1>"
					});
				}
			} else {
				Swal.fire("Revise", msgValidaciones, "info");
			}
		}
		
		function validaCamposCompletos() {
			var msg = "";
			var token = "";
		
			if( $("#cerFile").val() == "" ) {
				msg = "Es necesario que adjunte su certificado.";
				token = "\n";
			} else if( !fileValidation(".cer", $("#cerFile").val()) ) {
				msg = msg + token + "El certificado debe tener una extencion .cer";
				token = "\n";
			}
		
			if( $("#keyFile").val() == "" ) {
				msg = msg + token + "Es necesario que adjunte su llave privada.";
				token = "\n";
			} else if( !fileValidation(".key", $("#keyFile").val()) ) {
				msg = msg + token + "La llave privada debe tener una extencion .key";
				token = "\n";
			}
		
			if( $("#passwordLlave").val() == "" )
				msg = msg + token + "El password de su llave privada es requerido";
		
			return msg;
		}
		
	function muestraLog() {
			$("#dlg-Msg").dialog("open");
		} 
	
		
		function dialogoProcesar(){
			$('#dialog-procesar').dialog({
			    autoOpen: false,
			    modal: true,
			    resizable: false,
			    width: 350,
			    heigth: 200,
			    title: 'Procesando firma electronica',
			    show: "blind",
			    hide: "scale",
			    closeOnEscape: false,
				beforeClose: function( event, ui ) {
					return true;			
				},
			    overlay: { backgroundColor: '#FFF',
						   opacity: 6.5   
				}
			});
		}
		
		function autorizaPago(){
			$("#nFolios").val( $("#nFolioPago").val() );
			modalFiel.show();
			
			$(".dlgFielInpt").each(function() {
				$(this).val("");
			});
			
		}
		
		function fileValidation(extencionesPermitidas, filePath) {
			var allowedExtensions = eval("/(" + extencionesPermitidas + ")$/i");
			if( allowedExtensions.exec(filePath) )
				return true;
			else
				return false;
		}
		
</script>
</head>
<body id="dt_example">
	
	<form>
		<div id="container" class="container">
			<input type="hidden" id="cEstatus" name="cEstatus" value="">
			<input type="hidden" id="nenviadosicop" name="nenviadosicop" value="">
			<input type="hidden" id="cTipoPago" name="cTipoPago" value="">
			<input type="hidden" id="esBoletoVigente" name="esBoletoVigente" value="N">
			<input type="hidden" id="comision" name="comision">
			<input type="hidden" id="cTipoRfc" name="cTipoRfc" size=15 value="">
			<input type="hidden" id="cEsFirmaElectronica" name="cEsFirmaElectronica" value="N" />
			<input type="hidden" id="boleto" name="boleto">
			<input type="hidden" id="impteBoleto" name="impteBoleto">
			<input type="hidden" id="partida" name="partida">
			<input type="hidden" id="cRuta" name="cRuta">
			<input type="hidden" id="existeFolio" name="existeFolio" value="0" />
			<input type="hidden" id="aEjercicioFiscal" value=""	name="aEjercicioFiscal" />
			
			<input type="hidden" id="RFC" name="RFC" value="">
			<input type="hidden" id="cNombre" name="cNombre" value="">
			<input type="hidden" id="cReferencia" name="cReferencia" value="">
			<input type="hidden" id="mTotal" name="mTotal" value="">
			<input type="hidden" id="statusSel" name="statusSel" value="">
			
			<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=folio%>">
			<input type="hidden" name="momentoGuarda" id="momentoGuarda" value="0" />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste" size="14">
					
			<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
			<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
			<input type="hidden" id="tipoFirmante" name="tipoFirmante" size=15 value="PAGO_VOBO">
		
			<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
			<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
			<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
			<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
			<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
			<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" size=15 value="PAGO_AUT">
			<input type="hidden" name="id_caso"  id="id_caso" maxlength="40" value ="<%=request.getParameter("folio")%>">
			<!-- hidden para la captura de oficio delegatorio -->
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">		
			<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
			<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value="">
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
			<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
			<!-- hidden para la captura de los datos de quien elaboro -->
			<input type="hidden" id="cNombreE" name="cNombreE" size=40>
			<input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
			<input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
			<input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
			<input type="hidden" id="firmanteEla" name="firmanteEla" size=40>
			<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" >
			<input type="hidden" id="nNumEmpleadoBusqueda" name="nNumEmpleadoBusqueda" >
			<!-- hidden para la captura de oficio delegatorio VoBo-->
			<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
			<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
			<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
			<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
			<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
			<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
			<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">
			
			<h1>Comisiones sin Comprobación</h1>
				<div class="row">
					<div class="col-3">
						Folio:
						<input type="text" id="nFolioComision" name="nFolioComision" size="12" class="form-control" value="<%=folio%>" readonly/>
						Vuelo Vigente
						<input type="checkbox" id="chkVueloVigente" name="chkVueloVigente" align="left" readonly/>
					</div>
					
					<div class="col-3">
						RFC:<input type="text" id="cRFC" name="cRFC" size="15" readonly class="form-control"/>
					</div>
					<div class="col-6">
						Nombre: <input type="text" id="cNOMRFC" name="cNOMRFC" size="96" readonly class="form-control" />
					</div>
				</div>
			<div class="mt-2" id="dlgDatosComision">
				<h1>Datos de Comisión</h1>
				<div class="row">
					<div class="col-3">
						Folio:<input type="text" name="nIdComision" id="nIdComision" size="10" readonly class="form-control"/>
					</div>
					<div class="col-8">
						Comisión: <input type="text" name="cConcepto_Comision" id="cConcepto_Comision" size="100" readonly class="form-control"/>
					</div>
				</div>
				<div class="row">
					<div class="col-3">
						Fecha Inicio:
						<input type="text" id="fInicio" name="fInicio" size="10" readonly class="form-control"/>
					</div>
					<div class="col-3">
						Fecha Fin:
						<input type="text" id="fFin" name="fFin" size="10" readonly class="form-control"/>
					</div>
					<div class="col-3">
						# Acompañantes:
						<input type="text" style="text-align:right;" id="nAcompanantes" name="nAcompanantes" size="15" class="form-control" readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col-12">
						Informe de Comisión
						<textarea rows=6 id="cInformeComision" name="cInformeComision" class="form-control" readonly></textarea>
					</div>
				</div>
				
			</div>
									
			<!-- Dialogo que mostrara la informacion de las comisiones creadas -->
			<div class="mt-2" id="dlgComisiones">
					<h1> Información de Boletos de Avión. </h1>		
					<table id="dt_Boletaje"  class="display" cellspacing="0" cellpadding="0" align="center">
						<thead>
							<tr>
								<th> Núm. Renglon </th>
								<th> Núm. Boleto </th>
								<th> Importe </th>
								<th> Partida </th>
								<th> Eliminar </th>
								<th> RFC </th>
								<th> Nombre </th>
								<th> Ruta </th>												
							</tr>
						</thead>
						<tbody>
						</tbody>
					</table>
			</div>
			<div class="mt-2" id="operacionesDiv" style="display: none">
					<h1>Operaciones:</h1>
					<table style="width: 98%">
						<tr style="width: 100%">
							<td><input title="Rechazar Comisi&oacute;n" type="button" value="Rechazar Comisi&oacute;n" id="rechazaPago" class="btn btn-primary"/></td>
							<td align="right"><input title="Autorizar Comisi&oacute;n" type="button" value="Autorizar pago" id="autorizaPago" class="btn btn-secondary"/></td>
						</tr>
					</table>
			</div>
		</div>
	</form>
	<div id="dlg-Msg">
			<h1>Resultado de la operacion</h1>
			<%if( fielMsg != null){ %>
				<div id="fielWarning" class="col-12 col-lg-12 col-md-12 col-sm-12">
					<fieldset>
						<legend>Firma a punto de Expirar</legend>
						<div><p id="msgWarning" style="font-weight: bold;"><%=fielMsg%></p></div>
					</fieldset>
				</div>
				<%} %>
				<div>
					<table id="mnLogTbl" align="center" border="1">
						<thead>
							<tr>
								<th>Log</th>
							</tr>
						</thead>
						<tbody>
							<%=result%>
						</tbody>
					</table>
				</div>
	</div>
		
<div class="modal" tabindex="-1" role="dialog" id="dlg-FIEL" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title">Ingrese su firma Electronica</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
				<form id="autorizaLayouts" name="autorizaLayouts" method="POST" action="../firmaSolicitudPago" enctype="multipart/form-data">
					<input id="urlRetorno" name="urlRetorno" type="hidden" value="Generador/ComisionesSinComprobacionFirma.jsp"/>
					<input id="tipoPagoSeleccionado" name="tipoPagoSeleccionado" type="hidden" value=""/>
					<input id="loginFirma" name="loginFirma" type="hidden" value="<%=cLogin%>"/>
					<input id="ueFirma" name="ueFirma" type="hidden" value="<%=cUR%>"/>
					<input id="rfcFirma" name="rfcFirma" type="hidden" value="<%=RFCUsuario%>"/>
					<input id="tipoAutorizacion" name="tipoAutorizacion" type="hidden" value="<%=tipoAutorizacion%>"/>
					<input id="nFolios" name="nFolios" type="hidden" value=""/>
					<div class="row">
						<div class="col-12">
							Archivo *.cer
							<input type="file" size="30" name="cerFile" id="cerFile" class="dlgFielInpt form-control">
						</div>
					</div>
					<div class="row">
						<div class="col-12">
							Archivo *.key
							<input type="file" size="30" name="keyFile" id="keyFile" class="dlgFielInpt form-control">
						</div>
					</div>
					<div class="row">
						<div class="col-6">
							Password
							<input type="password" size="30" name="passwordLlave" id="passwordLlave" class="dlgFielInpt form-control">
						</div>
					</div>	
				</form>
	       </div>
	      <div class="modal-footer">
	        <button type="button" id="btnActualizarTC" onclick="aceptarDlg();" class="btn btn-primary">Aceptar</button>
	        <button type="button" class="btn btn-secondary" onclick="cancelarDlg();">Cerrar</button>
	      </div>
    	</div>
  	</div>
</div>
	

	
<div class="modal" tabindex="-1" role="dialog" id="dlgMotivoRechazo" data-mdb-keyboard="true" data-mdb-backdrop="static">
  	<div class="modal-dialog" role="document">
    	<div class="modal-content">
	      <div class="modal-header">
	        <h5 class="modal-title">Rechaza solicitud</h5>
	        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
	      </div>
	      <div class="modal-body">
				<div class="row">
					<div class="col-12">
						<label for="motivoRechazo">Es requerido indicar el motivo del rechazo</label>
						<textarea rows="5" cols="60" id="motivoRechazo" name="motivoRechazo" class="form-control"></textarea>
					</div>
				</div>
	       </div>
	      <div class="modal-footer">
	        <button type="button" id="btnActualizarTC" onclick="cancelaSolComision();" class="btn btn-primary">Aceptar</button>
	        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
	      </div>
    	</div>
  	</div>
</div>
	
</body>

</html>
