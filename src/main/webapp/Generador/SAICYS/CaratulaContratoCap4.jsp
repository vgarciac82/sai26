<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user = usuario.getLogin();
	Map<String, Role> rol = usuario.getRoles();
	String cIdContratoDefinitivo = "";
	String nEstatus="";
	if (request.getParameter("cIdContratoDefinitivo") != null) {
		cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
		nEstatus=request.getParameter("nIdEstatus");
		session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo,cIdContratoDefinitivo);
		session.setAttribute(GestionInterface.ATT_EstatusContratCap4,nEstatus);
	} else {
		cIdContratoDefinitivo = (String) session
				.getAttribute(GestionInterface.ATT_ContratCap4Definitivo);
	}
%>

<!DOCTYPE html>
<html>
<head>


<title>'CaratulaContratoCap4'</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="Consolidado">

<script type="text/javascript">
	var oTableMP = "";
	var myModal;
	$(document).ready(function() {
			<%
				
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(roles,"ContratoCap4","CaratulaContratoCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			
			%>
			initQuerys();
			myModal = new bootstrap.Modal(document.getElementById('modalProveedoresCap4'), {
				  keyboard: false
			});
			////EVENTO CLICK EN EL RENGLON DE LA TABLA DE PROVEEDORES DISPONIBLES
			$('#tblProveedoresDisponibles').on('dblclick','tr', function() {
				$(oTableMP.fnSettings().aoData).each(function() {
					$(this.nTr).removeClass('row_selected');
				});
				$(this).addClass('row_selected');
				var aTrs = $('#tblProveedoresDisponibles')
						.dataTable().fnGetNodes();
				for ( var i = aTrs.length; i >= 0; i--) {
					if ($(aTrs[i]).hasClass('row_selected')) {
						var nTr = $('#tblProveedoresDisponibles')
								.dataTable().fnGetData(aTrs[i]);
						$("#rfcSeleccionado").val("[[ " + nTr[0] + " ]] " + nTr[1]);
						$("#cIdRfcSeleccionado").val(nTr[0]);
						
						$("#lblProveedor").val("[[ " + nTr[0] + " ]] " + nTr[1]);
						$("#cIdRfParaGuardar").val(nTr[0]);
						myModal.hide();
					}
				}
			});
			iniciaRadioBtn();
			showButtons();
	});//Fin del document ready
	function initQuerys() {
		queryFormPost("obtieneDatosContCap4", {
			async : false,
			callback : function() {
				querySelectPost("CategoriaReadContCap4", "cboCategoriaCaratula", {
					async : false,
					callback : function() {
						$("#cboCategoriaCaratula")
								.val($("#nIdCategoria").val());
						querySelectPost("FundamentoLegRead",
								"cboFundamentoLeg", {
									async : false,
									callback : function() {
										$("#cboFundamentoLeg").val(
												$("#nIdFundamentoLeg").val());
									}
								});
					}
				});
				validaPlurianual();
				muestraFechas();
			}
		});

	}
	function cambiaFundamentoLeg() {
		querySelectPost("FundamentoLegRead", "cboFundamentoLeg", {
			async : false
		});
		muestraFechas();
	}
	function PlurianualChecked() {
		if ($("#isPlurianualCheck").is(':checked')) {
			$("#isPlurianual").val(1);
			$("#tdMontoTotalPluri").show();
			$("#tdFolioMASCP").show();
			$("#OficioDG").show();
		} else {
			$("#isPlurianual").val(0);
			$("#montoTotalPluri").val(0.00);
			$("#tdMontoTotalPluri").hide();
			$("#tdFolioMASCP").hide();
			$("#OficioDG").hide();
		}
	}
	function validaPlurianual() {
		if (parseInt($("#isPlurianual").val(), 10) == 1) {
			document.getElementById("isPlurianualCheck").checked = true;
			$("#tdMontoTotalPluri").show();
			$("#tdFolioMASCP").show();
			$("#tdOficioDG").show();
		} else {
			document.getElementById("isPlurianualCheck").checked = false;
			$("#montoTotalPluri").val(0.00);
			$("#tdMontoTotalPluri").hide();
			$("#tdFolioMASCP").hide();
			$("#tdOficioDG").hide();
		}

	}
	function frmt(dlt) {
		$("#" + dlt.id).formatCurrency();
	}
	function muestraFechas() {
		$("#esServicio").val(0);
		$("#cboCategoria").val($("#cboCategoriaCaratula").val());
		if (parseInt($("#nIdTipoActividadEconomica").val(), 10) == 1) {
			$("#esServicio").val(1);
		}
		queryFormPost("fechasProcedimientoCuentaRead", {
			async : false
		}); //Cuantas fechas hay en la bd
		var intentos = 0;
		do {
			queryInnerDivPost("fn_mFechasContratoCap4", {
				async : false
			}); //Trae el html con los inputs
			var inputDateCreados = $('input', '#tablaFechasProcedimiento').length; //Cuenta los inputs creados
			intentos++;
		} while (inputDateCreados < $("#fechasProcedimiento").val()
				&& intentos < 3);

		if (inputDateCreados < $("#fechasProcedimiento").val()) {
			swal("Error al cargar las fechas necesarias. Contacte a soporte SAI.",{icon:"warning",button: "Cerrar"});
		}
	}
	function saveFormPage() {
		manejaRadioBtn();
		var cadFechas = cadenaFechas();
		if (cadFechas == null) {
			return;
		}
		if ($("#cIdRfParaGuardar").val() == "") {
			swal("Falta seleccionar el Proveedor.",{icon:"warning",button: "Cerrar"});
			return;
		}
		if ($("#isPlurianual").val() == 1 && $("#montoTotalPluri").val() <= 0.00) {
			swal("El monto total plurianual no puede ser menor o igual a $ 0.00.",{icon:"warning",button: "Cerrar"});
			return;
		}
		$.ajax({
			url : "../../servlet/ContratoCap4Servlet",
			type : 'post',
			async : false,
			data : 'operacion=1&cnumCompranet=' + $("#cNoContratoCNET").val()
					+ '&isPlurianual=' + $("#isPlurianual").val()
					+ '&montoTotalPluri=' + $("#montoTotalPluri").val()
					+ '&nIdCategoria=' + $("#cboCategoriaCaratula").val()
					+ '&nIdFundamentoLeg=' + $("#cboFundamentoLeg").val()
					+ '&cadenaFechas=' + cadFechas + '&cIdContratoDefinitivo='
					+ $("#cIdContratoDefinitivo").val()
					+ '&cIdUnidadEjecutora=' + $("#cIdUnidadEjecutora").val()
					+ '&cDescripcion=' + $("#cConceptoContrato").val()
					+ '&cIdRFC=' + $("#cIdRfParaGuardar").val()
					+ '&nEsDescentralizado=' + $("#esDescentralizado").val()
					+ '&cOficioDG=' + $("#cOficioDG").val()
					+ '&cFolioMASCP=' + $("#cFolioMASCP").val()
					+ '&cnumProcedimientoCompranet=' + $("#cNoProcedimientoCNET").val()
					+ '&nCodContratoCNET=' + $("#nCodContratoCNET").val()
					+ '&nCodExpedienteCNET=' + $("#nCodExpedienteCNET").val()
					+ '&actEconomContratoCap4='
					+ $("#nIdTipoActividadEconomica").val(),
			dataType : 'json',
			success : function(j) {
				var mensaje = j[0].MENSAJE;
				var resp = j[0].RESPUESTA;
				swal({
					title: "",
					text: mensaje,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						if (resp) {
							window.location = "ContratoCap4.jsp?tab=2";
						}
				});
			}
		});
	}
	function cadenaFechas() {
		var inputTablaFechasProcedimiento = $('input',
				'#tablaFechasProcedimiento');
		if (inputTablaFechasProcedimiento.length < $("#fechasProcedimiento").val()) {
			swal("Error al cargar las fechas necesarias. Contacte a su soporte.",{icon:"warning",button: "Cerrar"});
			return null;
		}
		var numFecha = "";
		var cadenaFechas = '';
		var cantidadFechas = inputTablaFechasProcedimiento.length;
		//validación para fechas bacías 
		for ( var i = 0; i < inputTablaFechasProcedimiento.length; i++) {
			if (inputTablaFechasProcedimiento[i].type == "date"
					|| inputTablaFechasProcedimiento[i].type == "date") {
				if (inputTablaFechasProcedimiento[i].id
						.indexOf("fechaProcedimiento") >= 0) {
					numFecha = parseInt(inputTablaFechasProcedimiento[i].id
							.replace("fechaProcedimiento", ""), 10);
					if (i > 0) {
						cadenaFechas = cadenaFechas + ",";
						if (inputTablaFechasProcedimiento[i].value == "") {
							swal("Falta insertar algunas fechas",{icon:"warning",button: "Cerrar"});
							return null;
						}
					}
					cadenaFechas += numFecha + "-"
							+ ((inputTablaFechasProcedimiento[i].value).replace("-","/")).replace("-","/");
				}
			}
		}
		return cadenaFechas;
	}
	///Desabilita sabados y domingos del datepicker
	function nonWorkingDates(date) {
		var day = date.getDay(), Sunday = 0, Monday = 1, Tuesday = 2, Wednesday = 3, Thursday = 4, Friday = 5, Saturday = 6;
		//var closedDates = [[7, 29, 2009], [8, 25, 2010]];
		//var closedDays = [ [ Sunday ], [ Saturday ] ];
		var closedDays = [];
		for ( var i = 0; i < closedDays.length; i++) {
			if (day == closedDays[i][0]) {
				return [ false ];
			}
		}
		return [ true ];
	}
	function buscaProveedor() {
		myModal.show();
		mostrarTablaProveedores();
	}
	function mostrarTablaProveedores() {
		//$("#tblProveedoresDisponibles").css("display", "");
		var consulta = "qw= 1=1";
		if (($.trim($("#rfcProveedor").val())) != "") {
			consulta += " AND cIdRFC LIKE '%25"
					+ $.trim($("#rfcProveedor").val()) + "%25'";
			consulta = consulta.replace("\&", "%26");

		}
		if (($.trim($("#rSocialProveedor").val())) != "") {
			consulta += " AND cRazonSocial LIKE '%25"
					+ $.trim($("#rSocialProveedor").val()) + "%25'";
			consulta = consulta.replace("\&", "%26");
		}
		oTableMP = $('#tblProveedoresDisponibles').dataTable(
		{
			bScrollCollapse: true,
			bInfo: false,
			sScrollX: "100%",
			bAutoWith: true,
			bJQueryUI: true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType: "full_numbers",
			oLanguage: {
				sProcessing: "Procesando...",
				sLengthMenu: "<h5>Doble click para seleccionar el proveedor</h5>",
				sZeroRecords: "No hay registros a mostrar",
				sEmptyTable: "No hay datos",
				sLoadingRecords: "Cargando...",
				sInfo: "Registros _START_ al _END_ de _TOTAL_",
				sInfoEmpty: "Registro 0 al 0 de 0",
				sInfoFiltered: "(filtrado de _MAX_ registros)",
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
			bProcessing: true,
			"sAjaxSource" : window.location.protocol
					+ "//"
					+ window.location.host
					+ "/"
					+ window.location.pathname.split("/")[1]
					+ "/crud?rt=t&ql=fn_GetMostrarProveedoresProcedimiento()&"
					+ consulta,
			"aaSorting" : [ [ 0, "asc" ] ],
			aoColumns : [ 
				{sName : "cIdRFC"}, 
				{sName : "cRazonSocial"} 
			]
			,fnInitComplete: function() {
				if($('#tblProveedoresDisponibles >tbody >tr').length>0){
					oTableMP.fnAdjustColumnSizing();
				}
			}
		});
	}
	function manejaRadioBtn() {
		if ($("#centralizado").is(':checked')) {
			//hidden 'esDescentralizado' = 0
			$("#esDescentralizado").val(0);
		} else {
			//hidden 'esDescentralizado' = 1
			$("#esDescentralizado").val(1);
		}
	}
	function iniciaRadioBtn() {
		if ($("#esDescentralizado").val() == 1) {
			$("#descentralizado").attr("checked", true);
		} else {
			$("#centralizado").attr("checked", true);
		}
	}
	function showButtons() {
		if (parseInt($("#nIdEstado").val(), 10) >= 2) {
			$("#agregaProveedor").hide();
			$("#btnGuardar").hide();
		} else {
			$("#agregaProveedor").show();
			$("#btnGuardar").show();
		}
		//$("#agregaProveedor").show();
	}
	function deleteContrato() {
		if (parseInt($("#nIdEstado").val(), 10) > 1) {
			swal("No puede eliminar el contrato hasta que esté en estatus de captura.",{icon:"warning",button: "Cerrar"});
			return;
		}
		swal({
			title: "¿Está seguro que desea eliminar el contrato?",
			text: "Una vez confirmado, no podrá deshacer los cambios!",
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
			}else{
				queryFormPost("mDeleteContratoCap4", {
					async : false,
					callback : function() {
						guardaBitacora("Elimina_Contrato",$('#cIdContratoDefinitivo').val());
						window.location = "ContratoCap4.jsp?tab=1";
					}
				});
			}
		});
		
	}
	function guardaBitacora(accion,documento){
		//Bitácora
		$("#cAccion").val(accion);
		$("#cIdDocumento").val(documento);
		queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
	}
</script>
</head>

<body>
	<form id="formCaratulaContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Car&aacute;tula del Contrato Cap&iacute;tulo 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceCancelar ui-button ui-corner-all float-right" 	id="imgEliminar" name="imgEliminar" 	value="Eliminar"	onclick="deleteContrato();" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDefinitivo" id="lblDefinitivo"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cConceptoContrato" id="cConceptoContrato"  readonly/>
					</div>
				</div>
				
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTipoContrato" id="lblTipoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cEstado" id="cEstado"  readonly/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Edici&oacute;n del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-3">
							<label for="agregaProveedor">Agregar/Actualizar Proveedor </label>
						</div>
						<div class="col-auto">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="agregaProveedor" name="agregaProveedor" 	value="Buscar Proveedor"	 onclick="buscaProveedor()" >
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" style="display: none;">
					<div class="col-3">
						<div class="form-check">
					  		<input class="form-check-input" type="radio" name="grupoTipoPago" id="centralizado" value="0" checked="checked">
					  		<label class="form-check-label" for="centralizado">
					    		Centralizado
					  		</label>
						</div>	
					</div>
					<div class="col-4">
						<div class="form-check">
							<input class="form-check-input" type="radio" name="grupoTipoPago" id="descentralizado" value="1">
							<label class="form-check-label" for="flexRadioDefault2">
								Descentralizado
							</label>
					  	</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-3">
							<label for="cNoContratoCNET">No. de Contrato Compranet: </label>
						</div>
						<div class="col-4">
							<input type="text" class="form-control" id="cNoContratoCNET" name="cNoContratoCNET" >
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-3">
							<label for="cNoProcedimientoCNET">No. de Procedimiento Compranet: </label>
						</div>
						<div class="col-4">
							<input type="text" class="form-control" id="cNoProcedimientoCNET" name="cNoProcedimientoCNET" >
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-3">
							<label for="nCodExpedienteCNET">C&oacute;digo de Expediente Compranet: </label>
						</div>
						<div class="col-4">
							<input type="text" class="form-control" id="nCodExpedienteCNET" name="nCodExpedienteCNET" >
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-3">
							<label for="nCodContratoCNET">C&oacute;digo de Contrato Compranet: </label>
						</div>
						<div class="col-4">
							<input type="text" class="form-control" id="nCodContratoCNET" name="nCodContratoCNET">
						</div>
					</div>
				</div>
			</div>
			<div class="form-group form-check  ">
				<div class="row" >
					<div class="form-check input-group">
						<div class="col-3">		
							<label class="form-check-label" for="isPlurianualCheck">Es plurianual </label>
						</div>
						<div class="form-check  col-auto">
			  				<input class="form-check-input" type="checkbox" id="isPlurianualCheck" name="isPlurianualCheck" disabled="disabled">
			  			</div>
		  			</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" style="display: none;" id="tdMontoTotalPluri">
					<div class="input-group">
						<div class="col-3">
							<label for="montoTotalPluri">Total Plurianual Neto: </label>
						</div>
						<div class="col-4">
							<input type="text" class="form-control" id="montoTotalPluri" name="montoTotalPluri" onkeypress="return onlyDoubles(event);" placeholder="0.00">
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" style="display: none;" id="tdOficioDG">
					<div class="input-group">
						<div class="col-3">
							<label for="cOficioDG">Oficio DG: </label>
						</div>
						<div class="col-4">
							<input type="text" class="form-control" id="cOficioDG" name="cOficioDG" >
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" style="display: none;" id="tdFolioMASCP">
					<div class="input-group">
						<div class="col-3">
							<label for="cFolioMASCP">Folio MASCP: </label>
						</div>
						<div class="col-4">
							<input type="text" class="form-control" id="cFolioMASCP" name="cFolioMASCP" >
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" >
					<div class="input-group">
						<div class="col-3">
							<label for="cboCategoriaCaratula">Tipo de Procedimiento: </label>
						</div>
						<div class="col-4">
							<select class="custom-select" id="cboCategoriaCaratula" name="cboCategoriaCaratula" 	onchange="cambiaFundamentoLeg()">
								<option value="" selected="selected"></option>
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" >
					<div class="input-group">
						<div class="col-3">
							<label for="cboFundamentoLeg">Fundamento Legal: </label>
						</div>
						<div class="col-4">
							<select class="custom-select" id="cboFundamentoLeg" name="cboFundamentoLeg" 	onchange="cambiaFundamentoLeg()">
								<option value="" selected="selected"></option>
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group" id="tablaFechasProcedimiento" >
				
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnGuardar" name="btnGuardar" 	value="Guardar"	onclick="saveFormPage();" />
						</div>
					</div>
				</div>
			</div>
			<!-- Modal -->
			<div class="modal fade bd-example-modal-lg" id="modalProveedoresCap4" tabindex="-1" aria-labelledby="modalLabel" aria-hidden="true" >
			  <div class="modal-dialog modal-lg">
			    <div class="modal-content">
			      <div class="modal-header">
			        <h5 class="modal-title" id="exampleModalLabel">PROVEEDORES DISPONIBLES</h5>
			        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
			      </div>
			      <div class="modal-body">
			        	<div class="form-group row" >
		        			<div class="col-sm-3">
								<label for="rfcProveedor" class="form-label">RFC</label>
								<input type="text" class="form-control" id="rfcProveedor" name="rfcProveedor" >
		        			</div>
		        			<div class="col-sm-6">
								<label for="nCantidad" class="form-label">Raz&oacute;n Social</label>
								<input type="text" class="form-control" id="rSocialProveedor" name="rSocialProveedor" >
		        			</div>
		        			
			        	</div>
			        	<div class="form-group row" >
			        		<div class="col-3 col-sm-4">
								<input type="button" id="searchProveedor" value="Buscar" class="btn btn-primary" onclick="mostrarTablaProveedores();"/>
		        			</div>
			        	</div>
			        	<div class="form-group">
							<div class="row">
								<div class="col">
									<table id="tblProveedoresDisponibles" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
										<thead>
											<tr>
												<th>RFC</th>
												<th>Raz&oacute;n Social</th>
											</tr>										
										</thead>
									</table>
								</div>
							</div>
						</div>
			      </div>
			    </div>
			  </div>
			</div>
		</fieldset>
		<input type="hidden" name="nIdProcedimientoNuevo" id="nIdProcedimientoNuevo" /> 
		<input type="hidden" name="nIdFechaProcedimiento" id="nIdFechaProcedimiento" /> 
		<input type="hidden" name="nFechaProcedimiento" id="nFechaProcedimiento" />
		<input type="hidden" id="cIdContratoDefinitivo"	name="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		<input type="hidden" id="cboCategoria" name="cboCategoria" value="" />
		<input type="hidden" id="nIdTipoActividadEconomica"	name="nIdTipoActividadEconomica" value="1" /> 
		<input type="hidden" id="esServicio" name="esServicio" value="0" /> 
		<input type="hidden" id="fechasProcedimiento" name="fechasProcedimiento" value="0" /> 
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="" /> 
		<input type="hidden" id="cIdRfcSeleccionado" name="cIdRfcSeleccionado" value="" /> 
		<input type="hidden" id="rfcSeleccionado" name="rfcSeleccionado" value="" /> 
		<input type="hidden" id="cIdRfParaGuardar" name="cIdRfParaGuardar" value="" />
		<input type="hidden" name="esDescentralizado" id="esDescentralizado" />
		<input type="hidden" name="nIdCategoria" id="nIdCategoria" /> 
		<input type="hidden" name="nIdFundamentoLeg" id="nIdFundamentoLeg" /> 
		<input type="hidden" id="isPlurianual" name="isPlurianual" value="0" /> 
		<input type="hidden" id="nIdEstado" name="nIdEstado" value="0" />
		<input type="hidden" id="cAccion" name="cAccion" value=""/>
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%= usuario.getLogin() %>"/>
	</form>
</body>
</html>
