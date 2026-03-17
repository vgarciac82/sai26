var modalFirmantes;
var pagosOperaciones = [ "PAGOFEDERALIZADO", "PAGOOBRA", "CAJA", "PAGODIVERSO", "COMSINVIATICOS", "OPERAJENAS", "RELACIONGASTOS" ];
var firmanteUpdate;
var bEsConsulta = false;
var bpermitePagoSinFIEL = false;

$(document).ready(
	function() {
		iniciaDialogosFirmantes();
	});

function contieneElemento(strBusqueda) {
	var encontrado = false;
	for (i = 0; (i < pagosOperaciones.length && !encontrado); i++) {
		if (pagosOperaciones[i] == strBusqueda)
			encontrado = true;
	}
	return encontrado;
}

function iniciaDialogosFirmantes() {
	if ("CAJA" == $("#cDocumento").val()) {
		$("#elaboraDIV").css("display", "none");
		$("#elaboraDIVUpdate").css("display", "none");
		
	}

	if (contieneElemento($("#cDocumento").val())) {
		bEsConsulta = esConsulta;
		bpermitePagoSinFIEL = permitePagoSinFIEL;
	}

	firmanteUpdate = "t" + $("#cDocumento").val() + "EncabezadoFirmante_Update";
	/*VGC27032018 Para que se seleccione el tipo de delegatorio, por suplencia o delego.*/
	querySelectPost("catTipoSuplenciaRead", "tipoSuplencia", {
		async : false
	});
	querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo", {
		async : false
	});
	querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBoUpdate", {
		async : false
	});
	querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaUpdate", {
		async : false
	});

	/*FIN VGC27032018 Para que se seleccione el tipo de delegatorio, por suplencia o delego.*/

	$("#oficioDelegatorioCaptura").hide();
	$("#oficioDelegatorioCapturaUpdate").hide();
	$("#dFechaOficio").datepicker({
		dateFormat: "dd/mm/yy",
		autoclose: true,
		changeYear: true,
		changeMonth: true
	});

	$("#dFechaOficioUpdate").datepicker({
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

	$("#dFechaOficioVoBoUpdate").datepicker({
		dateFormat: "dd/mm/yy",
		autoclose: true,
		changeYear: true,
		changeMonth: true
	});

	$("#oficioDelegatorioVoBo").hide();
	$("#oficioDelegatorioVoBoUpdate").hide();
	
	modalFirmantes = new bootstrap.Modal(document.getElementById('dialog-firmantes'), 'data-bs-backdrop');

	$("#dialog-firmantesUpdate").dialog({
		autoOpen : false,
		height: 620,
		width: 500,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				if ($.trim($("#cNombreVoBoUpdate").val()) == "") {
					alert("Falta Ingresar Nombre en Datos Vº Bº"); return;
				} else if ($.trim($("#cPaternoVoBoUpdate").val()) == "") {
					alert("Falta Ingresar Apellido Paterno en Datos Vº Bº"); return;
				} else if ($.trim($("#cMaternoVoBoUpdate").val()) == "") {
					alert("Falta Ingresar Apellido Materno en Datos Vº Bº"); return;
				} else if ($.trim($("#cPuestoVoBoUpdate").val()) == "") {
					alert("Falta Ingresar Puesto en Datos Vº Bº"); return;
				}

				if ($.trim($("#cNombreAutUpdate").val()) == "") {
					alert("Falta Ingresar Nombre en Datos Autorizar"); return;
				} else if ($.trim($("#cPaternoAutUpdate").val()) == "") {
					alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return;
				} else if ($.trim($("#cMaternoAutUpdate").val()) == "") {
					alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return;
				} else if ($.trim($("#cPuestoAutUpdate").val()) == "") {
					alert("Falta Ingresar Puesto en Datos Autorizar"); return;
				}

				if ("CAJA" != $("#cDocumento").val()) {
					if ($.trim($("#cNombreElaUpdate").val()) == "") {
						alert("Falta Ingresar Nombre en Datos Elabora"); return;
					} else if ($.trim($("#cPaternoElaUpdate").val()) == "") {
						alert("Falta Ingresar Apellido Paterno en Datos Elabora"); return;
					} else if ($.trim($("#cMaternoElaUpdate").val()) == "") {
						alert("Falta Ingresar Apellido Materno en Datos Elabora"); return;
					} else if ($.trim($("#cPuestoElaUpdate").val()) == "") {
						alert("Falta Ingresar Puesto en Datos Elabora"); return;
					}
				}

				$("#cNombreVo").val($("#cNombreVoBoUpdate").val());
				$("#cPaternoVo").val($("#cPaternoVoBoUpdate").val());
				$("#cMaternoVo").val($("#cMaternoVoBoUpdate").val());
				$("#cPuestoVo").val($("#cPuestoVoBoUpdate").val());

				$("#cNombreA").val($("#cNombreAutUpdate").val());
				$("#cPaternoA").val($("#cPaternoAutUpdate").val());
				$("#cMaternoA").val($("#cMaternoAutUpdate").val());
				$("#cPuestoA").val($("#cPuestoAutUpdate").val());

				$("#cNombreE").val($("#cNombreElaUpdate").val());
				$("#cPaternoE").val($("#cPaternoElaUpdate").val());
				$("#cMaternoE").val($("#cMaternoElaUpdate").val());
				$("#cPuestoE").val($("#cPuestoElaUpdate").val());

				$("#firmanteVoBo").val($("#cNombreVo").val() + " " + $("#cPaternoVo").val() + " " + $("#cMaternoVo").val());
				$("#firmanteAut").val($("#cNombreA").val() + " " + $("#cPaternoA").val() + " " + $("#cMaternoA").val());
				$("#firmanteEla").val($("#cNombreE").val() + " " + $("#cPaternoE").val() + " " + $("#cMaternoE").val());

				try {
					if (confirm("¿Desea actualizar los datos de los firmantes con la informacion capturada?")) {
						queryFormPost(firmanteUpdate, {
							async : false
						});
						alert("Firmantes Actualizados Correctamente.");

						$("#cNombreVoBoUpdate").val("");
						$("#cPaternoVoBoUpdate").val("");
						$("#cMaternoVoBoUpdate").val("");
						$("#cPuestoVoBoUpdate").val("");

						$("#cNombreAutUpdate").val("");
						$("#cPaternoAutUpdate").val("");
						$("#cMaternoAutUpdate").val("");
						$("#cPuestoAutUpdate").val("");
						;

						$("#cNombreElaUpdate").val("");
						$("#cPaternoElaUpdate").val("");
						$("#cMaternoElaUpdate").val("");
						$("#cPuestoElaUpdate").val("");

						if ($("#oficioDelegatorioUpdate").prop("checked")) {

							var msn = "";

							if ($("#cFolioOficioUpdate").val() == "") {
								alert("Falta Ingresar el folio de Oficio."); return;
							} else if ($("#dFechaOficioUpdate").val() == "") {
								alert("Falta Ingresar la fecha del Oficio."); return;
							} else if ($("#cNombreTitularUpdate").val() == "") {
								alert("Falta Ingresar Nombre del Titular."); return;
							} else if ($("#cApellidoPaternoTitularUpdate").val() == "") {
								alert("Falta Ingresar Apellido Paterno del Titular."); return;
							} else if ($("#cApellidoMaternoTitularUpdate").val() == "") {
								alert("Falta Ingresar Apellido Materno del Titular."); return;
							} else if ($("#cPuestoTitularUpdate").val() == "") {
								alert("Falta Ingresar Puesto del Titular."); return;
							}

							$("#cFolioOficioAux").val($("#cFolioOficioUpdate").val());
							$("#dFechaOficioAux").val($("#dFechaOficioUpdate").val());
							$("#cNombreTitularAux").val($("#cNombreTitularUpdate").val());
							$("#cApellidoPaternoTitularAux").val($("#cApellidoPaternoTitularUpdate").val());
							$("#cApellidoMaternoTitularAux").val($("#cApellidoMaternoTitularUpdate").val());
							$("#cPuestoTitularAux").val($("#cPuestoTitularUpdate").val());
							$("#tipoSuplenciaAux").val($("#tipoSuplenciaUpdate").val());

							if ($("#firmanteOficioExiste").val() == "Existe") {
								queryFormPost({
									queryName : "tPagoFirmanteDelagatorioUpdate",
									async : false,
									callback : function() {
										msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
									}
								});
							} else {
								queryFormPost({
									queryName : "tPagoFirmanteDelagatorioCreate",
									async : false,
									callback : function() {
										msn = "Firmantes Oficio Delegatorio guardado correctamente.";
									}
								});
							}

							alert(msn);

							$("#cFolioOficioUpdate").val("");
							$("#dFechaOficioUpdate").val("");
							$("#cNombreTitularUpdate").val("");
							$("#cApellidoPaternoTitularUpdate").val("");
							$("#cApellidoMaternoTitularUpdate").val("");
							$("#cPuestoTitularUpdate").val("");

						}

						if ($("#oficioDeleVoBoUpdate").prop("checked")) {

							var msn = "";

							if ($("#cFolioOficioVoBoUpdate").val() == "") {
								alert("Falta Ingresar el folio de Oficio."); return;
							} else if ($("#dFechaOficioVoBoUpdate").val() == "") {
								alert("Falta Ingresar la fecha del Oficio."); return;
							} else if ($("#cNombreTitularVoBoUpdate").val() == "") {
								alert("Falta Ingresar Nombre del Titular."); return;
							} else if ($("#cApellidoPaternoTitularVoBoUpdate").val() == "") {
								alert("Falta Ingresar Apellido Paterno del Titular."); return;
							} else if ($("#cApellidoMaternoTitularVoBoUpdate").val() == "") {
								alert("Falta Ingresar Apellido Materno del Titular."); return;
							} else if ($("#cPuestoTitularVoBoUpdate").val() == "") {
								alert("Falta Ingresar Puesto del Titular."); return;
							}

							$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBoUpdate").val());
							$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBoUpdate").val());
							$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBoUpdate").val());
							$("#cApellidoPaternoTitularVoBoAux").val($("#cApellidoPaternoTitularVoBoUpdate").val());
							$("#cApellidoMaternoTitularVoBoAux").val($("#cApellidoMaternoTitularVoBoUpdate").val());
							$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBoUpdate").val());
							$("#tipoSuplenciaVoBoAux").val($("#tipoSuplenciaVoBoUpdate").val());

							if ($("#firmanteOficioVoBoExiste").val() == "Existe") {
								queryFormPost({
									queryName : "tPagoFirmanteDelegatorioVoBoUpdate",
									async : false,
									callback : function() {

										msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
									}
								});
							} else {
								queryFormPost({
									queryName : "tPagoFirmanteDelegatorioVoBoCreate",
									async : false,
									callback : function() {

										msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
									}
								});
							}

							alert(msn);

							$("#cFolioOficioVoBoUpdate").val("");
							$("#dFechaOficioVoBoUpdate").val("");
							$("#cNombreTitularVoBoUpdate").val("");
							$("#cApellidoPaternoTitularVoBoUpdate").val("");
							$("#cApellidoMaternoTitularVoBoUpdate").val("");
							$("#cPuestoTitularVoBoUpdate").val("");

						}
					}
				} catch (e) {
					alert("No se pudo actualizar los firmantes, intente mas tarde.");
				}
				$(this).dialog("close");
			},

			"Cancelar" : function() {
				$(this).dialog("close");
			}
		},
		close : function() {},
		open : function() {
			if (cNombreElabora)
				$("#cNombreEla").val(cNombreElabora);
			if (cPaternoEla)
				$("#cPaternoEla").val(cApellidoPaternoElabora);
			if (cMaternoEla)
				$("#cMaternoEla").val(cApellidoMaternoElabora);
			if (cPuestoEla)
				$("#cPuestoEla").val(cPuestoElabora);
		}
	});
}

function aceptarFirmantes() {
				queryFormPost("tPagoFirmanteDelagatorioRead"); 
				queryFormPost("tPagoFirmanteDelegatorioVoBoRead");

				if ($("#cNombreVoBo").val() == "") {
					alert("Falta Ingresar Nombre en Datos Vº Bº"); return;
				 
				} else if ($("#cPuestoVoBo").val() == "") {
					alert("Falta Ingresar Puesto en Datos Vº Bº"); return;
				}

				if ($("#cNombreAut").val() == "") {
					alert("Falta Ingresar Nombre en Datos Autorizar"); return;
				 
				} else if ($("#cPuestoAut").val() == "") {
					alert("Falta Ingresar Puesto en Datos Autorizar"); return;
				}

				if ("CAJA" != $("#cDocumento").val()) {
					if ($("#cNombreEla").val() == "") {
						alert("Falta Ingresar Nombre en Datos Elabora"); return;
					} else if ($("#cPaternoEla").val() == "") {
						alert("Falta Ingresar Apellido Paterno en Datos Elabora"); return;
					} else if ($("#cMaternoEla").val() == "") {
						alert("Falta Ingresar Apellido Materno en Datos Elabora"); return;
					} else if ($("#cPuestoEla").val() == "") {
						alert("Falta Ingresar Puesto en Datos Elabora"); return;
					}
				}

				$("#cNombreVo").val($("#cNombreVoBo").val());
				$("#cPaternoVo").val($("#cPaternoVoBo").val());
				$("#cMaternoVo").val($("#cMaternoVoBo").val());
				$("#cPuestoVo").val($("#cPuestoVoBo").val());

				$("#cNombreA").val($("#cNombreAut").val());
				$("#cPaternoA").val($("#cPaternoAut").val());
				$("#cMaternoA").val($("#cMaternoAut").val());
				$("#cPuestoA").val($("#cPuestoAut").val());

				$("#cNombreE").val($("#cNombreEla").val());
				$("#cPaternoE").val($("#cPaternoEla").val());
				$("#cMaternoE").val($("#cMaternoEla").val());
				$("#cPuestoE").val($("#cPuestoEla").val());

				$("#firmanteVoBo").val($("#cNombreVoBo").val() + " " + $("#cPaternoVoBo").val() + " " + $("#cMaternoVoBo").val());
				$("#firmanteAut").val($("#cNombreAut").val() + " " + $("#cPaternoAut").val() + " " + $("#cMaternoAut").val());
				$("#firmanteEla").val($("#cNombreEla").val() + " " + $("#cPaternoEla").val() + " " + $("#cMaternoEla").val());

				var msn = "No Se Guardo Correctamente Informacion de Firmantes";
				let updated = false
				queryFormPost({ queryName: firmanteUpdate, 
					async : false,
					callback:function() {
						updated=true
					}
				});

				if(!updated){
					alert(msn);
					return;
				}
				
				updated = false;

				if ($("#oficioDelegatorio").prop("checked")) {

					if ($("#cFolioOficio").val() == "") {
						alert("Falta Ingresar el folio de Oficio."); return;
					} else if ($("#dFechaOficio").val() == "") {
						alert("Falta Ingresar la fecha del Oficio."); return;
					} else if ($("#cNombreTitular").val() == "") {
						alert("Falta Ingresar Nombre del Titular."); return;
					 
					} else if ($("#cPuestoTitular").val() == "") {
						alert("Falta Ingresar Puesto del Titular."); return;
					}

					$("#cFolioOficioAux").val($("#cFolioOficio").val());
					$("#dFechaOficioAux").val($("#dFechaOficio").val());
					$("#cNombreTitularAux").val($("#cNombreTitular").val());
					$("#cApellidoPaternoTitularAux").val($("#cApellidoPaternoTitular").val());
					$("#cApellidoMaternoTitularAux").val($("#cApellidoMaternoTitular").val());
					$("#cPuestoTitularAux").val($("#cPuestoTitular").val());
					$("#tipoSuplenciaAux").val($("#tipoSuplencia").val());

					if ($("#numeroEmpleadoAutoriza"))
						$("#numeroEmpleadoAutoriza").val($("#cboSuplenteAut").val());

					if ($("#firmanteOficioExiste").val() == "Existe") {
						queryFormPost({
							queryName : "tPagoFirmanteDelagatorioUpdate",
							async : false,
							callback : function() {
								updated = true;
								msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
							}
						});
						
						if(!updated){
							alert("No se logro actualizar los firmantes de oficio delegatorio");
							return;
						}
						updated = false;

					} else {
						queryFormPost({
							queryName : "tPagoFirmanteDelagatorioCreate",
							async : false,
							callback : function() {
								msn = "Firmantes Oficio Delegatorio guardado correctamente.";
								updated = true;
							}
						});
						if(!updated){
							alert("No se logro actualizar los firmantes de oficio delegatorio");
							return;
						}
						updated = false;
					}

					alert(msn);

				}

				if ($("#oficioDeleVoBo").prop("checked")) {
					
					updated = false;

					if ($("#cFolioOficioVoBo").val() == "") {
						alert("Falta Ingresar el folio de Oficio."); return;
					} else if ($("#dFechaOficioVoBo").val() == "") {
						alert("Falta Ingresar la fecha del Oficio."); return;
					} else if ($("#cNombreTitularVoBo").val() == "") {
						alert("Falta Ingresar Nombre del Titular."); return;

					} else if ($("#cPuestoTitularVoBo").val() == "") {
						alert("Falta Ingresar Puesto del Titular."); return;
					}

					$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBo").val());
					$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBo").val());
					$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBo").val());
					$("#cApellidoPaternoTitularVoBoAux").val($("#cApellidoPaternoTitularVoBo").val());
					$("#cApellidoMaternoTitularVoBoAux").val($("#cApellidoMaternoTitularVoBo").val());
					$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBo").val());
					$("#tipoSuplenciaVoBoAux").val($("#tipoSuplenciaVoBo").val());
					if ($("#numeroEmpleadoVoBo"))
						$("#numeroEmpleadoVoBo").val($("#cboSuplenteVoBo").val());

					if ($("#firmanteOficioVoBoExiste").val() == "Existe") {
						queryFormPost({
							queryName : "tPagoFirmanteDelegatorioVoBoUpdate",
							async : false,
							callback : function() {
								updated = true;
								msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
							}
						});
						if(!updated){
							alert("No se logro actualizar los firmantes de oficio delegatorio");
							return;
						}
						updated = false;
					} else {

						queryFormPost({
							queryName : "tPagoFirmanteDelegatorioVoBoCreate",
							async : false,
							callback : function() {
								msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
								updated = true;
							}
						});
						if(!updated){
							alert("No se logro actualizar los firmantes de oficio delegatorio");
							return;
						}
						updated = false;

					}

					alert(msn);

				}

				if ("OPERAJENAS" == $("#cDocumento").val()) {
					updated = false;

					if (!esConsulta) {

						parent.document.getElementById("pb_save").disabled = true;

						getNextSequenceVal({
							seqName : cxpPrefijo + "-" + $("#cCentroContable").val(),
							async : false,
							callback : setSequenceVal
						});

						queryFormPost("tOperAjenasCreate", {
							async : false
						});
						
						guardatabladetalle();
						GuardaContrarecibo();

						if ($("#chk_LSICOP").prop("checked"))
							GenerarPasivoDiferido();

						queryFormPost({
							queryName : "operAjenaUpdate",
							async : false,
							callback : function() {
								if ($("#chk_IP").prop("checked"))
									queryFormPost("operAjenaUpdateIP", {
										async : false
									});
								else if ($("#chk_radicado").prop("checked"))
									queryFormPost("operAjenaUpdateRadicado", {
										async : false
									});
								parent.document.getElementById("pb_send").style.visibility = 'visible';
								parent.document.getElementById("pb_send").disabled = false;
								cmdImprimir('PolizaPago');
								parent.document.getElementById("pb_send").click();
							}
						});
					}
				}

				if ("CAJA" == $("#cDocumento").val() || "COMSINVIATICOS" == $("#cDocumento").val()) {
					/*Si no es consulta es la primera vez que se genera el reporte y debe "Aplicar" el tramite.*/
					if (!bEsConsulta) {

						cont = true;
						firmante = true;
						reImprime = "SI";

						if ("CAJA" == $("#cDocumento").val())
							//$("#guardar").click();
							polizaCaja();
						else {

							cmdImprimir();

						}
					}
					
				} else {
					if (!bEsConsulta) {
						if (procesar()) {
							parent.document.getElementById("pb_save").disabled = false;
						}
					}
					
				}
				
			modalFirmantes.hide();

}
function tipoFirmantes() {
	if ("PAGOFEDERALIZADO" == $("#cDocumento").val() || "PAGOOBRA" == $("#cDocumento").val() || "CAJA" == $("#cDocumento").val() || "PAGODIVERSO" == $("#cDocumento").val() 
	|| "RELACIONGASTOS" == $("#cDocumento").val() || "COMSINVIATICOS" == $("#cDocumento").val() || "REINTEGRO" == $("#cDocumento").val() ) {

		$("#tblVoBo").hide();
		$("#tblAut").hide();
		$("#tblSuplenteAut").hide();
		$("#tblSuplenteVoBo").hide();

		llenaFirmanteVoBo();
		llenaFirmanteAut();
		llenaSuplenteVoBo();
		llenaSuplenteAut();
		
		abrirDlg();
		
	} else {

		$("#tblcboVoBo").hide();
		$("#tblcboAut").hide();
		$("#tblcboSuplenteAut").hide();
		$("#tblcboSuplenteVoBo").hide();
		$("#tblAutorizaFIEL").hide();

		$("#autorizadoPorFiel").val(true);
		
		abrirDlg();
		
		queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async : false	}); // voBo - autoriza
		queryFormPost("tPagoFirmanteDelagatorioRead", {	async : false	}); // Para los firmantes de oficio delegatorio en caso de que existan.
		queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {	async : false	}); // Para los firmantes de oficio delegatorio VoBo en caso de que existan.
	}
}

function abrirDlg() {
	$("#autorizadoPorFiel").val(true);
		$("#autorizadoPorFielChk").prop("checked", true);
		queryFormPost("tPagoFirmanteDelagatorioRead"); 
		queryFormPost("tPagoFirmanteDelegatorioVoBoRead"); 
		
		if ($("#firmanteOficioVoBoExiste").val() == "Existe") {
			$("#oficioDeleVoBo").attr("checked", "checked");
			showDivOficioVoBo(false);
		}
	
		if ($("#firmanteOficioExiste").val() == "Existe") {
			$("#oficioDelegatorio").attr("checked", "checked");
			showDivOficio(false);
		}
	
		if (bpermitePagoSinFIEL) {
			autorizadoPorFielAction();
		} else {
			$("#autorizadoPorFiel").val("true");
		}
	
		if (cNombreElabora)
			$("#cNombreEla").val(cNombreElabora);
		if (cPaternoEla)
			$("#cPaternoEla").val(cApellidoPaternoElabora);
		if (cMaternoEla)
			$("#cMaternoEla").val(cApellidoMaternoElabora);
		if (cPuestoEla)
			$("#cPuestoEla").val(cPuestoElabora);
			
		modalFirmantes.show();	
}

function updateFirmantes() {
	if ("CAJA" == $("#cDocumento").val()) {
		queryFormPost("readcDocumentoHaplicadoCaja", {
			async : false
		});
	} else if ("CONPLURIANUAL" == $("#cDocumento").val()) {
		queryFormPost("readcDocumentoHaplicadoContrato", {			async : false		});
		
	}else {
		queryFormPost("readcDocumentoHaplicadoPago", {
			async : false
		});
	}

	if ($("#cEstatusPago").val() != "C") {


		if ("PAGOFEDERALIZADO" == $("#cDocumento").val() || "PAGOOBRA" == $("#cDocumento").val() || "CAJA" == $("#cDocumento").val()|| "RELACIONGASTOS" == $("#cDocumento").val()
			|| "PAGODIVERSO" == $("#cDocumento").val() || "COMSINVIATICOS" == $("#cDocumento").val() || "REINTEGRO" == $("#cDocumento").val()) {

			$("#tblVoBo").hide();
			$("#tblAut").hide();
			$("#tblSuplenteAut").hide();
			$("#tblSuplenteVoBo").hide();

			llenaFirmanteVoBo();
			llenaFirmanteAut();
			llenaSuplenteVoBo();
			llenaSuplenteAut();
			$("#dialog-firmantes").dialog("open");
		} else {

			$("#dialog-firmantesUpdate").dialog("open");

			$("#tblcboVoBoUpdate").hide();
			$("#tblcboAutUpdate").hide();
			$("#tblcboSuplenteAutUpdate").hide();
			$("#tblcboSuplenteVoBoUpdate").hide();
		}

		queryFormPost("tPagoFirmanteDelagatorioActualizaRead", {
			async : false
		});
		queryFormPost("tPagoFirmanteDelegatorioActualizaVoBoRead", {
			async : false
		});

	} else {
		alert("El pago ya est\u00E1 en estatus de Cancelado, por lo que ya no se puede cambia la firma.");
	}
}

function showDivOficioVoBo(esUpdate) {
	var cmpName = "oficioDeleVoBo" + (esUpdate ? "Update" : "");
	var divName = "oficioDelegatorioVoBo" + (esUpdate ? "Update" : "");
	if ($("#" + cmpName).is(":checked"))
		$("#" + divName).show();
	else
		$("#" + divName).hide();
}

function showDivOficio(esUpdate) {
	var cmpName = "oficioDelegatorio" + (esUpdate ? "Update" : "");
	var divName = "oficioDelegatorioCaptura" + (esUpdate ? "Update" : "");
	if ($("#" + cmpName).is(":checked"))
		$("#" + divName).show();
	else
		$("#" + divName).hide();
}

function llenaFirmanteVoBo() {

	$("#cTipoFirmante").val("VOBO")
	querySelectPost("FirmantesPorTipo_Read", "cboVoBo", {
		async : false
	});
}


function llenaFirmanteAut() {

	$("#cTipoFirmante").val("AUT")
	querySelectPost("FirmantesPorTipo_Read", "cboAutoriza", {
		async : false
	});
}

function llenaSuplenteVoBo() {

	$("#cTipoFirmante").val("SUPVOBO")
	querySelectPost("FirmantesPorTipo_Read", "cboSuplenteVoBo", {
		async : false
	});
}

function llenaSuplenteAut() {
	
	$("#cTipoFirmante").val("SUPAUT")
	querySelectPost("FirmantesPorTipo_Read", "cboSuplenteAut", {
		async : false
	});
}

function llenaFirmanteVoBoUpdate() {
	querySelectPost("cboFirmanteModuloVoBo_Read", "cboVoBoUpdate", {
		async : false
	});
	cargaFirmanteVoBoUpdate();
}

function cargaFirmanteVoBoUpdate() {
	$("#nNumEmpleadoVoBo").val($("#cboVoBoUpdate").val());
	queryFormPost("firmantesModuloVoBoUpdate_Read", {
		async : false
	});
}

function llenaFirmanteAutUpdate() {
	querySelectPost("cboFirmanteModuloAut_Read", "cboAutorizaUpdate", {
		async : false
	});
	cargaFirmanteAutUpdate();
}

function cargaFirmanteAutUpdate() {
	$("#nNumEmpleadoAut").val($("#cboAutorizaUpdate").val());
	queryFormPost("firmantesModuloAutUpdate_Read", {
		async : false
	});
}

function llenaSuplenteVoBoUpdate() {
	querySelectPost("cboFirmanteModuloVoBoSuplente_Read", "cboSuplenteVoBoUpdate", {
		async : false
	});
	cargaSuplenteVoBoUpdate();
}

function cargaSuplenteVoBoUpdate() {
	$("#nNumEmpleadoVoBoSuplencia").val($("#cboSuplenteVoBoUpdate").val());
	queryFormPost("firmantesModuloVoBoSuplenteUpdate_Read", {
		async : false
	});
}

function llenaSuplenteAutUpdate() {
	querySelectPost("cboFirmanteModuloAutSuplente_Read", "cboSuplenteAutUpdate", {
		async : false
	});
	cargaSuplenteAutUpdate();
}

function cargaSuplenteAutUpdate() {
	$("#nNumEmpleadoAutSuplencia").val($("#cboSuplenteAutUpdate").val());
	queryFormPost("firmantesModuloAutSuplenteUpdate_Read", {
		async : false
	});
}

function autorizadoPorFielAction() {
	if ($("#autorizadoPorFielChk").attr("checked"))
		$("#autorizadoPorFiel").val(true);
	else
		$("#autorizadoPorFiel").val(false);

}

function infoEmpleado(tipoFirmante) {
	$("#cNombreEmpleado").val();
	$("#cPaternoEmpleado").val();
	$("#cMaternoEmpleado").val();
	$("#cPuestoEmpleado").val();
	$("#cTipoFirmante").val(tipoFirmante)

	var numeroEmpleado = -1;
	var postFijo = ""

	if ("VOBO" == tipoFirmante) {
		numeroEmpleado = $("#cboVoBo").val();
		postFijo = "VoBo";
		$("#nNumEmpleadoVoBo").val(numeroEmpleado);
		
		if("RELACIONGASTOS" == $("#cDocumento").val()){
			$("#cNombreVo").val(numeroEmpleado);	
		}
	} else if ("AUT" == tipoFirmante) {
		numeroEmpleado = $("#cboAutoriza").val();
		postFijo = "Aut";
		$("#nNumEmpleadoAut").val(numeroEmpleado);
		
		if("RELACIONGASTOS" == $("#cDocumento").val()){
			$("#cEmpleadoA").val(numeroEmpleado);
		}
	} else if ("SUPAUT" == tipoFirmante) {
		numeroEmpleado = $("#cboSuplenteAut").val();
		postFijo = "Titular";
	} else if ("SUPVOBO" == tipoFirmante) {
		numeroEmpleado = $("#cboSuplenteVoBo").val();
		postFijo = "TitularVoBo";
	}

	limpiaFirmante(postFijo);

	if (parseInt(numeroEmpleado, 10) > 0) {
		$("#nNumEmpleadoBusqueda").val(numeroEmpleado);
		queryFormPost({
			queryName : "infoComplementariaFirmanteRead",
			async : false,
			callback : function() {
				$("#cNombre" + postFijo).val($("#cNombreEmpleado").val());
				$("#cPaterno" + postFijo).val($("#cPaternoEmpleado").val());
				$("#cMaterno" + postFijo).val($("#cMaternoEmpleado").val());
				$("#cPuesto" + postFijo).val($("#cPuestoEmpleado").val());
			}
		});
	}

}

function limpiaFirmante(postFijo) {
	$("#cNombre" + postFijo).val("");
	$("#cPaterno" + postFijo).val("");
	$("#cMaterno" + postFijo).val("");
	$("#cPuesto" + postFijo).val("");
}