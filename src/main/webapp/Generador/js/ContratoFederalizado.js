var oTableCalendario;
var es_MX = {
	sProcessing : "Procesando...",
	sLengthMenu : "Mostrar _MENU_ registros",
	sZeroRecords : "No hay registros a mostrar",
	sEmptyTable : "No hay datos en la tabla",
	sLoadingRecords : "Cargando...",
	sInfo : "Registros _START_ al _END_ de _TOTAL_",
	sInfoEmpty : "Registro 0 al 0 de 0",
	sInfoFiltered : "(filtado de _MAX_ registros)",
	sInfoPostFix : "",
	sInfoThousands : ",",
	sSearch : "Buscar:",
	oPaginate : {
		sFirst : "Primero",
		sPrevious : "Ant.",
		sNext : "Sigte.",
		sLast : "&Uacute;ltimo"
	}
};

var nombreMeses = new Array("enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre");

function init() {
	$(".monto").each(function() {
		$(this).css("text-align", "right");
		$(this).formatCurrency();
		$(this).focus(function() {
			onFocusMonto($(this).attr("id"));
		});
		$(this).blur(function() {
			onBlurMonto($(this).attr("id"));
		});
	});

	$("#nPorcIVAAplicable").change(function() {
		llenaIVA();
		changeMontos(false);
	});

	$("#mObra").blur(function() {
		changeMontos(true);
	});
	$("#mOtrosImpuestos").blur(function() {
		changeMontos(false);
	});
	$("#mIVA").blur(function() {
		changeMontos(false);
	});
}

function changeMontos(actualizaIVA) {
	if (actualizaIVA)
		llenaIVA();

	var montoSinIva = parseFloat(Quitafrmt($("#mObra").val()));
	var otrosImpuestos = parseFloat(Quitafrmt($("#mOtrosImpuestos").val()));
	var mImporteIva = parseFloat(Quitafrmt($("#mIVA").val()));

	var totalAPagar = montoSinIva + otrosImpuestos + mImporteIva;

	$("#mImporte").val(montoSinIva.toFixed(2));
	$("#mEjercicio").val(totalAPagar.toFixed(2));
	$("#mTotal").val(totalAPagar.toFixed(2));
	$("#montoTotalCnt").val(totalAPagar.toFixed(2));

	formateaMontos();
}

function llenaIVA() {
	$("#mIVA").val(calculaImporteIVA().toFixed(2));
	$("#mTotal").val(calculaImporteMasIVA().toFixed(2));
	$("#mEjercicio").val($("#mTotal").val());

}

function calculaImporteMasIVA() {
	var importeSinIva = parseFloat(Quitafrmt($("#mObra").val()));
	var porceIVA = parseFloat($("#nPorcIVAAplicable").val());
	var mImporteTotal = importeSinIva * (1 + porceIVA);

	return mImporteTotal;
}

function calculaImporteIVA() {
	var importeSinIva = parseFloat(Quitafrmt($("#mObra").val()));
	var mImporteTotal = calculaImporteMasIVA();
	var mImporteIVA = mImporteTotal.toFixed(2) - importeSinIva.toFixed(2);

	return parseFloat(mImporteIVA);
}



/**
 * 
 * @param id
 */
function onFocusMonto(id) {
	Sinfrmt($("#" + id)[0]);
	$("#" + id).select();
}

/**
 * Quita el formato de un input para mostrar en él el valor sin formato.
 * 
 * @param fld
 *            Campo a cambiar
 */
function Sinfrmt(fld) {
	var valcol = fld.value;
	valcol = valcol.replace("$", "");
	valcol = valcol.replace(/,/g, "");
	$("#" + fld.id).val(valcol);
	$("#" + fld.id).select();
}

/**
 * Las
 * 
 * @param fldID
 */
function onBlurMonto(fldID) {
	if ($("#" + fldID).val() == "")
		$("#" + fldID).val("0.00");

	$("#" + fldID).formatCurrency();

}

/**
 * Debido a que pueden existir contratos con IVA variable (exceento y 16 por
 * ciento) esta funcion permite al usuario modificar el monto calculado del IVA
 * siempre hacia abajo. Es decir no puede exceder el monto calculado del IVA.
 */
function editaIVA() {
	$("#mIVA").removeClass("notEditable");
	document.getElementById("mIVA").readOnly = false;
	$("#mIVA").focus();
	$("#mIVA").select();
}

/**
 * Funcion para evitar la captura de letras. Permite que se caputuren solo
 * monntos numericos.
 * 
 * @param evt
 *            Evento de keypress
 * @returns {Boolean} si y solo si la tecla presionada esta en 0123456789.
 */
function onlyNumbers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 47) {
		return false;
	}
	return !(keyPressed > 31 && (keyPressed < 46 || keyPressed > 57));
}

/**
 * Funcion que se llama al terminar la captura del IVA editado. El monto del IVA
 * editado no puede ser mayor al calculo del IVA del importe bruto. El monto del
 * IVA no deberia ser 0 si se especifico en el combo que el IVA es del 16% <br/>
 * <b>NOTA:</b> Se hace el primer intento de tener un lugar centralizado de
 * calculo de impuestos y retenciones, para tener unificado el calculo y que sea
 * consistente, valorar si hacer el calculo en servidor es demasiado costoso en
 * red se creara una libreria de JS que se importara a cada tipo de pago
 */
function onBlurIVA() {
	if ($("#mIVA").val() == "")
		$("#mIVA").val("0.00");else {

		var mImporteSinIVA = Quitafrmt($("#mObra").val());
		var mImporteIVA = parseFloat(Quitafrmt($("#mIVA").val()));
		var porcentajeIVA = parseFloat($("#nPorcIVAAplicable").val());
		var ivaCalculado = calculaImporteIVA().toFixed(2);

		if (mImporteSinIVA == "" || parseFloat(mImporteSinIVA) == 0) {
			Swal.fire("Capturar","Debe capturar primero el Monto Convenio Anexo", "info");
			$("#mIVA").val("0.00");
			$("#mObra").focus();
			document.getElementById("mIVA").readOnly = true;
			$("#mIVA").addClass("notEditable");

		} else if ((parseFloat(ivaCalculado) < mImporteIVA) && porcentajeIVA > 0) {
			Swal.fire("Validacion","El importe capturado [" + mImporteIVA + "] excede al IVA calculado[" + ivaCalculado + "] Por favor verifique.", "info");
			$("#mIVA").val("0.00");
			$("#mIVA").focus();
		} else {
			document.getElementById("mIVA").readOnly = true;
			$("#mIVA").addClass("notEditable");
		}

	}

}

/**
 * Retira el formato monetario de una cadena
 * 
 * @param fld
 * @returns
 */
function Quitafrmt(fld) {
	fld = fld.replace("$", "");
	fld = fld.replace(/,/g, "");
	return fld;
}


function formateaMontos() {
	$(".monto").each(function() {
		$(this).formatCurrency();
	});
}

function Guardar() {
	if ($("#nPorcAsignacion").val() == '0' || $("#nPorcAsignacion").val() == ''
		|| $("#nPorcAsignacion").val() == '0.0'
		|| $("#nPorcAsignacion").val() == '0.00') {
		document.FormContrato.LHaySaldoAnticipo.value = 0;
	} else {
		document.FormContrato.LHaySaldoAnticipo.value = 1;
	}

	if (rowCount < 2) {
		Swal.fire("Capturar","Por favor agregue al menos una Clave Presupuestal", "warning");
		return false;
	}

	if ($('#cIdClaseContratoObra').val() == '0') {
		Swal.fire("Seleccione...","Seleccione la Clase de Contrato (Convenio o Anexo Técnico)", "warning");
		return false;
	}

	if ($('#id_esquema').val() == '0') {
		Swal.fire("Seleccione...","Seleccione el Esquema de Precio", "info");
		return false;
	}

	
		Generales();
		
		
		
		return true;
	 
}


	
function Generales() {
	if (activo == 1) {
		if (Number($("#mAmortizado").val()) > 0
			|| Number($("#nPorcAsignacion").val()) == 0) {
			document.FormContrato.LHaySaldoAnticipo.value = 0;
		} else {
			document.FormContrato.LHaySaldoAnticipo.value = 1;
		}
		
		queryFormPost({
			
			queryName: "ContratoFederalizadoCreate", 
			async : false,
			callback : function() {
					queryFormPost({
						queryName: "ContratoFederalizadoAnticipoCreate,ContratoEPCreate",
						async : false
					});
					iniciaCapturaRetenciones();
					parent.document.getElementById("pb_send").style.visibility = 'visible';
					Swal.fire({ title:"Contrato Federalizado Guardado!", text: " Folio de Contrato: " + $("#cIdContrato").val() , icon: "success"
						, footer: '<a href="">Presione el boton cerrar y continue en el modulo de Compromisos</a>'});
				/*		
				queryFormPost({
					queryName: "ContratoFederalizadoAnticipoCreate,ContratoEPCreate",
					async : false,
					callback : function() {
						if (esContratoDirecto()) {
							deshabilitaCaptura();
							iniciaCapturaCalendario();
						} else {		
							parent.document.getElementById("pb_send").style.visibility = 'visible';
							Swal.fire({ title:"Contrato Federalizado Guardado!", text: " Folio de Contrato: " + $("#cIdContrato").val() , icon: "success"
							, footer: '<a href="">Presione el boton cerrar y continue en el modulo de Compromisos</a>'});
						}
					}
				}); */
			}
		});
		
		activo = 0;
	}
}




function onSubmit(id_oper) {
	parent.document.getElementById("pb_save").disabled = true;
	parent.document.getElementById("pb_save").style.visibility = 'hidden';

	var p = window.parent;
	var valida_campos = true;

	if (operacion == 1) {
		/*if (esContratoGuardado()) {
			return true;
		}*/

		if ($.trim($("#cIdContrato").val()) == "") {
			Swal.fire("Capturar","Favor de capturar Numero de Contrato","info");
			parent.document.getElementById("pb_save").disabled = false;
			parent.document.getElementById("pb_save").style.visibility = 'visible';
			return false;
		} else {
			queryFormPost("validaContratoFed", {
				async : false
			});
			if (Number($("#existeCont").val()) > 0) {
				Swal.fire("Contrato duplicado","Ese Numero de Contrato ya existe, favor de capturar uno diferente", "error");
				parent.document.getElementById("pb_save").disabled = false;
				parent.document.getElementById("pb_save").style.visibility = 'visible';
				return false;
			}
		}

		if ($.trim($("#cIDRFC_contrato").val()) == "") {
			Swal.fire("Seleccione","Favor de Seleccionar un Beneficiario","info");
			parent.document.getElementById("pb_save").disabled = false;
			parent.document.getElementById("pb_save").style.visibility = 'visible';
			return false;
		}

		if ($("#cIdClaseContratoObra").val() == "0") {
			Swal.fire("Capturar","Favor de Capturar el tipo de Documento","info");
			parent.document.getElementById("pb_save").disabled = false;
			parent.document.getElementById("pb_save").style.visibility = 'visible';
			return false;
		}
		if ($.trim($("#cObjetoContrato").val()) == "") {
			Swal.fire("Capturar","Favor de Capturar el concepto del contrato","info");
			parent.document.getElementById("pb_save").disabled = false;
			parent.document.getElementById("pb_save").style.visibility = 'visible';
			return false;
		}

		if (Number(Quitafrmt($("#mObra").val())) == 0) {
			Swal.fire("Capturar","El importe no puede ser 0","warning");
			parent.document.getElementById("pb_save").disabled = false;
			parent.document.getElementById("pb_save").style.visibility = 'visible';
			return false;
		}

		if (!validaFechaTermino()) {
			parent.document.getElementById("pb_save").disabled = false;
			parent.document.getElementById("pb_save").style.visibility = 'visible';
			return false;
		}
	}

	try {
		p.gestion.setFolio(folio);
		p.gestion.setOperador($("#OPERADOR").val());
		p.gestion.setFechaDocumento($("#FECHA_CARGA").val());
		p.gestion.setEjercicioFiscal($("#cEjercicio").val());
		p.gestion.setConceptoMov("Aplicación de Federalizados (Subsidio)");
		p.gestion.setMoneda("MXP");
		p.gestion.setFechaApCont($("#FECHA_CARGA").val());
		p.gestion.setAplicadoCont("false");

		valida_campos = Guardar();

	} catch (e) {
		window.alert("onSubmit: Error: " + e.message);
		parent.document.getElementById("pb_save").disabled = false;
		parent.document.getElementById("pb_save").style.visibility = 'visible';
		return false;
	}

	if (!valida_campos) {
		parent.document.getElementById("pb_save").disabled = false;
		parent.document.getElementById("pb_save").style.visibility = 'visible';
	}

	return valida_campos;
}

function onPostDisplay(id_oper) {

		
}

function aceptarBtn(){
	
		var guardado = false;
		var objParams = {};
		objParams['cIdEntidadContable'] = $("#cIdEntidadContable").val();
		objParams['cEjercicio'] = $("#cEjercicio").val();
		objParams['cIdContrato'] = $("#cIdContrato").val();

		Swal.fire({
				  title: 'Se autorizara el contrato',
				  text: " ¿Desea continuar? ",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
						guardado = false
						$.ajax({
							url : '../contratosFederalizados/CreaCompromiso',
							data : objParams,
							dataType : 'json',
							type : 'post',
							async : false,
							success : function(json) {
								r = json.success;
								if (r == "true") {
									guardado = true;
								} else {
									var msg = json.data_1.result;
									alert(msg);
								}
			
							},
							error : function(xhr, textStatus, errorThrown) {
								alert("Advertencia: " + xhr.responseText + "\nEstatus: "
									+ textStatus + "\n" + errorThrown);
							}
						});
						
						if (guardado) {
							
							Swal.fire("Se genero y autorizo el compromiso para el contrato.", "Ahora puede realizar el pago.", "success");
							$.blockUI({
								message : "Procesando espere ......"
							});
							parent.document.getElementById("pb_send").click();	
						}
						
					} else {
						return guardado;
					}
					
				});
	
}

//SE LLAMA SII FUE CORRECTO EL LLAMADO EN EL BOTON, POR ESO SIEMPRE RETORNA TRUE;
function onPostSubmit(id_oper) {
	
	window.parent.execResponsable();
	window.parent.execOperacion();
	
	return true;	
		
}

function esContratoGuardado() {
	var bEsContratoGuardado = false;

	$("#esContratoGuardado").val("");
	queryFormPost({
		queryName : "esContratoGuardadoRead",
		async : false,
		callback : function() {
			var contratoGuardado = $("#esContratoGuardado").val();
			bEsContratoGuardado = (contratoGuardado === 'true');
		}
	});

	return bEsContratoGuardado;
}

function esContratoComprometido() {
	var bEsContratoComprometido = false;
	$("#compromisos").val("0");
	queryFormPost({
		queryName : "contratoFedConCompromiso",
		async : false,
		callback : function() {
			var contratoComprometido = ($("#compromisos").val() == "" ? false : parseInt($("#compromisos").val(), 10) > 0);
			bEsContratoComprometido = contratoComprometido;
		}
	});

	return bEsContratoComprometido;
}

function creaDTCalendario() {
	var cEjercicio = $("#cEjercicio").val();
	var cIdEntidadContable = $("#cIdEntidadContable").val();
	var cIdContrato = $("#cIdContrato").val();

	var qw = " cEjercicio = '" + cEjercicio + "' AND cIdEntidadContable='" + cIdEntidadContable + "' AND cIdContrato = '" + cIdContrato + "'";
	oTableCalendario = $("#dtPresupuesto").dataTable(
		{
			//bScrollCollapse : false,
			bInfo : false,
			//sScrollY : 100,
			//sScrollX : 100,
			//bAutoWith : true,
			bJQueryUI : true,
			bRetrive : true,
			bDestroy : true,
			sPaginationType : "full_numbers",
			bPaginate : false,
			oLanguage : es_MX,
			bServerSide : true,
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=vContratoFederalizadoEP&qw=" + qw,
			bJQueryUI : true,
			aaSorting : [ [ 0, "asc" ] ],
			aoColumns : [ {
				sName : "EP"
			}, {
				sName : "importeCalendarizado"
			} ]
		});

	$("#dtPresupuesto tbody").click(function(event) {
		$(oTableCalendario.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});

		$(event.target.parentNode).addClass('row_selected');
	});

	$("#dtPresupuesto tbody").dblclick(
		function(evt) {

			$(oTableCalendario.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});

			$(evt.target.parentNode).addClass('row_selected');

			var aPos = oTableCalendario.fnGetPosition(evt.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			var ep = $("#dtPresupuesto").dataTable().fnGetData()[currIndex][0];
			$("#epDisp").val(ep);
			$("#EPBusqueda").val(ep);
			abrirDialogEP();
			//$("#dialog-form-ep").dialog("open");

		});

}

function iniciaCapturaCalendario() {
	$('[href="#tabRetencion"]').tab().show();
	$('[href="#tabPresupuesto"]').tab().show();
	$('#contrato-list a[href="#tabPresupuesto"]').trigger('click');
	
	$("#cIdTipoRetencion").attr("disabled", false);
	
	$("#btnAceptar").show();
	creaDTCalendario();
	creaTablaRetencion();

	actualizaMontosCalendarizados();
}
function iniciaCapturaRetenciones(){
	$('[href="#tabRetencion"]').tab().show();
	$('#contrato-list a[href="#tabRetencion"]').trigger('click');
	
}
function actualizaMontosCalendarizados() {
	queryFormPost({
		queryName : "montosCalendarioCFRead",
		async : false,
		callback : function() {}
	});
}

function abrirDialogEP() {
			deshabilitaMontos();
			limpiaCapturaCalendario();
			cargaSaldoDisponible();
			cargaSaldoCalendarizado();
			
			modalEP.show();
}



function cargaSaldoDisponible() {
	queryFormPost({
		queryName : "montoDispCalendarioCFRead",
		async : false,
		callback : function() {}
	});
}

/**
 * Deshabilita los campos en que se muestra los montos de la EP seleccionada.
 */
function deshabilitaMontos() {
	$(".monto").each(function() {
		$(this).attr('readonly', 'readonly');
	});
}


function limpiaCapturaCalendario() {
	$(".montoCaptura").each(function() {
		$(this).val("0.00");
		cambiafrmt(this);
	});
	$("#totalCalendarizado").val("0.00");

}

function cargaSaldoCalendarizado() {
	queryFormPost({
		queryName : "montoCalendarizadoCFRead",
		async : false,
		callback : function() {
			$(".montoCaptura").each(function() {
				if ($(this).val() == "")
					$(this).val("0.00");
				cambiafrmt(this);
			});

			if ($("#totalCalendarizado").val() == "")
				$("#totalCalendarizado").val("0.00");
			cambiafrmt($("#totalCalendarizado")[0]);

		}
	});

}
function onFocusMontoCaptura(idInpt) {
	var val = Sinfrmt($("#" + idInpt)[0]);
	$("#" + idInpt).select();
}

function onBlurMontoCaptura(idInpt) {
	var mes = idInpt.replace("Compr", "");
	var mesDisponible = mes + "Disp";

	var monto = parseFloat(Quitafrmt($("#" + idInpt).val()));
	monto = parseFloat(monto.toFixed(2));

	var montoDisponible = parseFloat(Quitafrmt($("#" + mesDisponible).val()));

	if (monto <= montoDisponible) {
		$("#" + idInpt).val(monto);
		var calendarizado = parseFloat(Quitafrmt($("#montoCalendarioCnt").val()));
		var montoTotalCnt = parseFloat(Quitafrmt($("#montoTotalCnt").val()));
		var totalEP = sumaMontoTotalEP();

		if ((totalEP + calendarizado) <= montoTotalCnt) {
			$("#totalCalendarizado").val(totalEP);
			cambiafrmt($("#totalCalendarizado")[0]);
			cambiafrmt($("#" + idInpt)[0]);
			return true;
		} else {
			Swal.fire("Verifique","No puede comprometer mas del importe total del contrato.","error");
			$("#" + idInpt).val("0.00");
			$("#" + idInpt).focus();
			return false;
		}

	} else {
		Swal.fire("Verifique","No puede comprometer mas del importe disponible en el mes de " + mes, "error");
		$("#" + idInpt).val("0.00");
		//$("#" + idInpt).focus();
		return false;
	}
}

function sumaMontoTotalEP() {
	var montoTotal = 0;
	for (cnt = 0; cnt < nombreMeses.length; cnt++) {
		var nombreMes = nombreMeses[cnt];
		var montoCalendario = parseFloat(Quitafrmt($("#" + nombreMes + "Compr").val()));
		montoTotal = montoTotal + montoCalendario;
	}
	return montoTotal;
}

function guardaCalendarioEP() {
	var guardado = false;

	var montoTotal = sumaMontoTotalEP();
	if (montoTotal == 0) {
		Swal.fire("Capturar","No ha capurado calendario en la EP (todos los montos son 0)","info");
		return false;
	}

		Swal.fire({
				  title: 'Se guardara el calendario actual.',
				  text: "¿Desea continuar?",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					   var objParams = {};
						for (cnt = 0; cnt < nombreMeses.length; cnt++) {
							objParams[nombreMeses[cnt]] = Quitafrmt($("#" + nombreMeses[cnt] + "Compr").val());
						}
				
						objParams['cIdEntidadContable'] = $("#cIdEntidadContable").val();
						objParams['cEjercicio'] = $("#cEjercicio").val();
						objParams['cIdContrato'] = $("#cIdContrato").val();
						objParams['ep'] = $("#epDisp").val();
				
						$.ajax({
							url : '../contratosFederalizados/RegistraDetalle',
							dataType : 'json',
							type : 'post',
							data : objParams,
							async : false,
							success : function(json) {
								r = json.success;
								if (r == "true") {
									guardado = true;
								} else {
									var msg = json.data_1.result;
									alert(msg);
								}
				
							},
							error : function(xhr, textStatus, errorThrown) {
								alert("Advertencia: " + xhr.responseText + "\nEstatus: "
									+ textStatus + "\n" + errorThrown);
								r = true;
							}
						});
						
						Swal.fire("OK","Se guardo exitosamente el calendario del contrato.", "success");
						limpiaCapturaCalendario();
						validaMontosCompletos();
						actualizaMontosCalendarizados();
						modalEP.hide();
				}
		});
		
		return guardado;
}

function validaMontosCompletos() {
	if (calendarioCompleto()) {
		Swal.fire("Completo","El calendario se ha capturado por completo. De click en el boton enviar para terminar","success");
		$('#btnAceptar').show();
	}
}

function calendarioCompleto() {
	var calendarioCompleto = false;
	$("#faltanteCalendario").val("-1");

	queryFormPost({
		queryName : "capturaCalCFCompleta",
		async : false,
		callback : function() {
			var faltante = $("#faltanteCalendario").val();
			if (faltante != "")
				faltante = parseInt(faltante, 10);
			else
				faltante = -1;
			calendarioCompleto = (faltante == 0);
		}
	});

	return calendarioCompleto;
}

function soloNumerosPositivo(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	var strCheck = '0123456789.';
	var key = String.fromCharCode(keyPressed);
	if (strCheck.indexOf(key) == -1)
		return false;
	return true;
}

function deshabilitaCaptura() {
	$('.obligatorio').each(function() {

		$(this)[0].readOnly = true;
	});
	$('.obligatorio').css("background", "#f0f0f0");
	$("Select").attr("disabled", true);
	$("textarea").attr("disabled", true);
}