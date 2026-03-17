var tblBoletoStruct = [ "<td>N&uacute;mero de Boleto</td>", "<td><input type=\"text\" name=\"numeroBoleto\" size=\"20\"></td>", "<td>Importe:</td>", "<td><input type=\"text\" name=\"importeBoleto\" size=\"10\" onKeyPress=\"return valFmt(this,19)\" onfocus=\"Sinfrmt(this)\" onblur=\"cambiafrmt(this);\"></td>", "<td class=\"eliminar\"><img alt=\"Eliminar\" src=\"../images/minus.gif\">Eliminar</td>" ];
var errorCarga = false;
var es_mx = {
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
		sFirst: "Primero",
		sPrevious: "Ant.",
		sNext: "Sigte.",
		sLast: "&Uacute;ltimo"
	}
};


function validaEFO() {
	
	var esEFO = false;
	 $("#esEFO").val("");
	$("#rfcValidar").val($("#cIdRFC_RelacionGasto").val());
	
	queryFormPost({
		queryName : "validaEFO",
		async : false,
		callback : function() {
			esEFO = $("#esEFO").val() == "S";
		}
	});
	
	return esEFO;
}

function autorizadoPorFielAction(){
	
	if( $("#autorizadoPorFielChk").attr("checked") )
		$("#autorizadoPorFiel").val(true);
	else
		$("#autorizadoPorFiel").val(false);

}



function revisaTipoFacturas(){
	queryFormPost("readEsPPD", {async : false});
	
		if( $("#esPPD").val() != "0" ){
			Swal.fire ('IMPORTANTE', 'La factura adjunta tiene el metodo de pago PPD por lo que es indispensable posteriormente solicitar al proveedor el CFDI de complemento de pago.', 'warning');
			queryFormPost("readRFCPPD", {async : false});
			$("#cIDRFC").val($("#cRFCFactura").val());
			$("#EditaCorreoE").css('visibility', 'visible');
			modalCorreo.show();
			mostrarCorreo();
		} 
}

function revisaFacturaConRetenciones(){
	queryFormPost("readMesFacturas", {async : false});
	
		if( $("#cantMeses").val() > 1 ){
			Swal.fire ('El pago tiene facturas generadas en diferentes meses con retenciones.', 'Favor de realizar pagos/comprobaciones por separado, una por cada mes de las facturas.', 'error');
			return false;
		}  else
			return true;
}

function validaCapturaCorreo(){
	let mensaje = "";
	if( $("#esPPD").val() != "0" &&  $("#correoActual").val() ==""){
		mensaje = "No se capturo el correo para seguimiento y la(s) factura(s) son PPD"
	}
	
	return mensaje;
}

function abrirDialogCorreo(){
	modalCorreo.show();
}

function mostrarCorreo(){
	queryFormPost("mostrarCorreoRead",{async: false});
}
 
function validarCorreo(){
        
    emailRegex = /^(([^<>()[\]\.,;:\s@\"]+(\.[^<>()[\]\.,;:\s@\"]+)*)|(\".+\"))@(([^<>()[\]\.,;:\s@\"]+\.)+[^<>()[\]\.,;:\s@\"]{2,})$/i;
    
    if (emailRegex.test($("#correoActual").val())) {
		$("#correo").val($("#correoActual").val());
		
		//Actualizar datos y cerrar
		if($("#nombreCorreo").val()==""){
			Swal.fire("Capturar Nombre", "Debe capturar el nombre a la cual se dirigirá el correo", "warning")
		} else {
			$("#paternoCorreo").val($("#aPCorreo").val());
			$("#maternoCorreo").val($("#aMCorreo").val());
			$("#nCorreo").val($("#nombreCorreo").val());
			$("#cCargo").val($("#cargo").val());
			queryFormPost("updateCorreoActualizado",{async: false});
			$('#dialog-actualizaCorreo').modal('hide');
			//$("#dialog-actualizaCorreo").dialog("close");
		}
		
	} else {
     	Swal.fire("Correo Invalido", "El correo no es valido, intente de nuevo", "error");
     	return;
    }
}


function revisaRetenciones() {
	if($("#mImporteRetencion").val() == "" ){
		$("#mImporteRetencion").val(0)
	}
	
	if ($("#mImporteRetencion").val() > 0 ) {
		queryFormPost("retencionesGuardadasRead", {async: false});
		if( Number($("#sumaRetenciones").val()).toFixed(2) == Number($("#mImporteRetencion").val()).toFixed(2)){
			return true
		} else {
			return false
		}
	} else {
		return true;
	}
}


function creaDTEPs() {
	var diaUsarMesSiguiente;
	var dia = $("#fAplicacion").val().split("/")[0];
	var mes = $("#fAplicacion").val().split("/")[1];
	var folio = $("#nFolioPago").val();
	var login = $("#usur").val();
	var fuenteFinanciamiento = $("#cTipoFuente").val();
	//var cidcontrato = $("#cIdContrato").val();
	var destinoGasto = $("#DESTINO_GASTO").val();
	queryFormPost({
		queryName: "diaUsarMesSiguienteRead",
		async: false,
		callback: function() {
			if ($("#gp_valor").val() && !isNaN(Number($("#gp_valor").val()))) {
				diaUsarMesSiguiente = Number($("#gp_valor").val());
			}
		}
	});

	
	if (Number(dia) > diaUsarMesSiguiente ) {
		mes = Number(mes) + 1;
	}
	var funcionSQL = "fn_SaldoDisponibleIP (" + mes + "," + folio + "," + "'" + login + "'" + "," + fuenteFinanciamiento + ",'" + destinoGasto + "')";
	/*
	if (fuenteFinanciamiento == 1 ) {
		funcionSQL = "fn_SaldoCompromisoIF (" + mes + "," + folio + ", '" + login + "' ," + fuenteFinanciamiento + ",'"  +  cidcontrato + "','" + destinoGasto +  "')";
	} 
	*/
	oTableEPs = $('#tblEP').dataTable(
			{
				"bPaginate" : true,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : true,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				"sPaginationType": "full_numbers",
				sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=" + funcionSQL,
				aoColumns : [ {
					sName : "ep"
				}, {
					sName : "mSaldoEnero"
				}, {
					sName : "mSaldoFebrero"
				}, {
					sName : "mSaldoMarzo"
				}, {
					sName : "mSaldoAbril"
				}, {
					sName : "mSaldoMayo"
				},{
					sName : "mSaldoJunio"
				},{
					sName : "mSaldoJulio"
				},{
					sName : "mSaldoAgosto"
				},{
					sName : "mSaldoSeptiembre"
				},{
					sName : "mSaldoOctubre"
				},{
					sName : "mSaldoNoviembre"
				},{
					sName : "mSaldoDiciembre"
				}],
				oLanguage : es_mx
			});		

	$("#tblEP tbody").click(function(event) {

		$(oTableEPs.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblEP tbody").dblclick(function(event) {

		$(oTableEPs.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTableEPs.fnGetPosition(event.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var rowData = $("#tblEP").dataTable().fnGetData()[currIndex];
		var ep = $("#tblEP").dataTable().fnGetData()[currIndex][0];
		$("#epShowRG").val(ep);
		$("#epConsulta").val(ep);
		$("#mesConsulta").val(mes);
		//queryFormPost("saldoDispobleAcumRead", {async : false});
		var total = 0;
		
		for (var i = 1; i <= mes; i++) {
		    var valor = Number(quitaFmt(rowData[i]));  
		    if (!isNaN(valor)) {
		        total += valor;
		    }
		}

		$("#disponibleEPTotalRG").val(total);
		
		if(Number(quitaFmt($("#mMontoPorEjercer").val())) > 0 ) {
			myModal.show();
			$("#montoEjercer").val("0.00");
			$("#montoEjercer").focus();
		} else {
			Swal.fire("Terminado", "El total ya fue calendarizado, no se pueden agregar mas claves. Presione el botón AVANZAR.", "info");
		}
	});
}

function validaTipoRetencion(){
	/* Valida el tipo de retencion que tienen las facturas */
	queryFormPost("validaResico",{async: false });
	queryFormPost("tieneRetencionesXML_RG_Read",{async: false });
		
	if ($("#tieneRetenXML").val() == 1 && $("#chk_tieneRete").prop("checked") === false){
		Swal.fire("Tiene retenciones diferentes a Resico","Debe selecccionar la opcion de mencionada para señalar la retención correspondiente" )
		return false;
	}	
	
	return true;
}

function solicitudesSinRetencion() {
		/* Revisa las solicitudes y los regimenes para que tengan la retencion RESICO si corresponde */
		queryFormPost("consultaSolicitudesSinRetencion",{async: false });
		msg = $("#listaSol").val();
		
		if(msg != ""){
			Swal.fire("No tiene retenciones","La factura no tiene retenciones y el regimen es Resico (PERSONA FISICA):" + msg,"error");
			return false;
		}
		
		return true;
}

function validaCalculoResico() {
		/* Revisa que el calculo del impuesto RESICO */
		queryFormPost("validaImporteResico",{async: false });
		msg = $("#msgRF").val();
		
		if(msg != ""){
			Swal.fire("Revise las facturas", msg,"error")
			return false;
		}
		
		return true;
}

function validaRegimenFacturasRG() {
			
			queryFormPost("tieneRetencionesCapturadas",{async: false });
			queryFormPost("importeRetencionRG",{async: false });
			
			if($("#mImporteRetencion").val()=="" || $("#mImporteRetencion").val()== "0"){
				$("#mImporteRetencion").val("0.00");
				$("#mImporteBrutoEP").val($("#mImporteNetoEP").val());
				//$("#mImporteBruto").val(Number(Number($("#mImporteNeto").val())));
			}
				
			$("#mImporteBrutoEP").val(parseFloat(Number(quitaFmt($("#mImporteNeto").val())) + Number($("#mImporteRetencion").val())).toFixed(2));
			if($("#importeEdicion").val() != "0.00") {
				$("#mImporteBruto").val(parseFloat(Number(quitaFmt($("#importeEdicion").val())) + Number($("#mImporteRetencion").val()) + Number(quitaFmt($("#importenoComprobable").val()))).toFixed(2));						
			}		

			 if (validacionesRetencion() == false) {
				Swal.fire("Verifique la Factura", "El calculo de las retenciones no corresponde con la factura", "error")
		        return false;
		    }
		
		    return true;

}


function validacionesRetencion(){
		let diferencia;
		if ($("#chk_tieneRete").prop("checked")) {
			
			if (Number($("#cOtraRetencion").val())== 0 && Number($("#cTipoRetencion").val())  == 0) {
				Swal.fire("Seleccione", "Debe seleccionar el tipo de Otras Retenciones para continuar", "info");
				return false;
			
			} else {
				//Otras retenciones
				if (Number($("#mFacturaOtros").val()) > 0 ) {
							
							diferencia = Number($("#mOtras").val()) - Number($("#mFacturaOtros").val())
							
							if (diferencia > 0.01 || diferencia < -0.01 ){
								Swal.fire("Verifique la Factura", "El calculo de las Otras retenciones no corresponde con la factura", "error");
								return false;	
							} else {
								queryFormPost("insertaOtrasRetenciones", {async: false});
								
							} 
				}
					
				//Retenciones IVA e ISR
				 if ( Number($("#mIvaFactura").val()) > 0  ||  Number($("#mISRFactura").val()) > 0 ) {
						
						diferencia = parseFloat( Number($("#mImporteIva").val())  + Number($("#mImporteISR").val()) - Number($("#mIvaFactura").val()) -Number($("#mISRFactura").val())).toFixed(2);
						
						if (Number(diferencia) > 0.01 || Number(diferencia) < -0.01 ){
							//Eliminar otras retenciones
							queryFormPost("eliminaRetenciones", {async: false});
							Swal.fire("Verifique la Factura", "El calculo de las retenciones no corresponde con la factura", "error");
							return false;	
						} else {
							if(!agregaRetenciones()){
								return false;
							}
						}
				
				} else {
					if($("#tieneRetenXML").val() == "1" && $("#retencionesCapturadas").val() > 0 ){
						return true;
						
					} else if($("#tieneRetenXML").val() == "1"){
						
						Swal.fire("No se puede continuar"," Debido a que se encontraron retenciones diferentes al ISR en el XML de la Factura.","error");
							if($("#esResico").val() == "0")
								Swal.fire("No se puede continuar","Debido a que la factura tiene retenciones y el proveedor no es RESICO.","error");
						return false;
					} 
				}
			}		
		} else {	
					if($("#tieneRetenXML").val() == "1" && $("#retencionesCapturadas").val() > 0 ){
						return true;
						
					} else  if ($("#tieneRetenXML").val() == "1"){
						Swal.fire("No se puede continuar"," Debido a que se encontraron retenciones diferentes al ISR en el XML de la Factura.","error");
							if($("#esResico").val() == "0")
								Swal.fire("No se puede continuar","Debido a que la factura tiene retenciones y el proveedor no es RESICO.","error");
						return false;
							
					} else {
						$("#cTipoPago").val($("#cDocumento").val());
						
						$("#msgRF").val();
						queryFormPost("validaResicoRG",{async: false });
						if ($("#msgRF").val()!=""){
							Swal.fire("Factura(s) erronea(s)","La(s) factura(s) " + $("#msgRF").val() + " tienen otro regimen fiscal diferente a RESICO.","error");
							return false;
						}
						
						queryFormPost("validaRESICOPersonaMoral",{async: false });
						if ($("#msgRF").val()!=""){
							Swal.fire("No se puede continuar",$("#msgRF").val(),"error");
							return false;
						}
					} 
		}
		return true;
}


function agregaRetenciones() {
	var guardado = false;
		logErrores = "";
			
				$.ajax({
					url : "../viaticos/agregaRetenciones",
					type : 'post',
					async : false,
					data : {folioPago : 	$("#nFolioPago").val(),
							mIvaFactura: 	$("#mIvaFactura").val(),
							mISRFactura: 	$("#mISRFactura").val(),
							aejercicioFiscal: $("#aEjercicioFiscal").val(),
							cTipoPago: 		$("#cTipoPago").val(),
							tipoRetIVA: 	$("#tipoRetIVA").val(),
							tipoRetISR: 	$("#tipoRetISR").val(),
							concepto:		$("#cTipoRetencion").val()
					},
					dataType : 'json',
					success : function(j) {
						var exito = j.success;
			
						if (exito) {
							guardado = true;
						} else {
							var errores = j.errorList;
							var cnt = 0;
							for (cnt = 0; cnt < errores.length; cnt++) {
								logErrores = logErrores + errores[cnt] + "\n";
							}
						}
					},
					error : function(errorThrown) {
						logErrores = errorThrown.ERROR;
					}
				});
			
				if (!guardado)
					Swal.fire("Error al agregar las retenciones", logErrores, "error")
					
				return guardado;
}

function createDTCalendario() {
	var cWhere = " nFolioPago=" + $("#nFolioPago").val() + " AND cTipoPago='" + $("#cTipoPago").val() + "'";

	oTableSaldos = $("#tblCalendarioRG").dataTable(
		{
			"bDestroy" : true,
			fnDrawCallback : function() {},
			//bAutoWidth : true,
			oLanguage : es_mx,
			bServerSide : true,
			"bInfo" : false,
			"bFilter" : false,
			"bSort" : true,
			"bPaginate" : false,
			"sScrollX" : "100%",
			"sScrollY" : "100px",
			sAjaxSource : window.location.protocol + "//"
				+ window.location.host + "/"
				+ window.location.pathname.split("/")[1]
				+ "/crud?rt=t&ql=vImportesCalendario&qw=" + cWhere,
			bProcessing : true,
			sPaginationType : "full_numbers",
			bJQueryUI : true,
			aoColumns : [ {
				sName : "EP"
			}, {
				sName : "mImporteBruto",
				sClass : "money"
			}, {
				sName : "mImporteRetencion",
				sClass : "money"
			}, {
				sName : "tipoConcepto"
			}, {
				sName : "tipoMovimiento"
			} ]
		});

	$("#tblCalendarioRG tbody").click(function(event) {

		$(oTableSaldos.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

	});

	$("#tblCalendarioRG tbody").dblclick(function(event) {

		$(oTableSaldos.fnSettings().aoData).each(function() {
			$(this.nTr).removeClass('row_selected');
		});
		$(event.target.parentNode).addClass('row_selected');

		var aPos = oTableSaldos.fnGetPosition(event.target.parentNode);

		if (aPos instanceof Array)
			currIndex = aPos[0];
		else
			currIndex = aPos;

		var ep = $("#tblCalendarioRG").dataTable().fnGetData(event.target.parentNode)[0];
		
		$("#epBorrar").val(ep);

		if (confirm("Esta seguro de eliminar la clave\n" + ep + "\n del calendario?")) {
			queryFormPost({
				queryName : "eliminaEPCalendario",
				async : false,
				callback : function() {
					creaDTEPs();
					createDTCalendario();
					
    						
				}
			});
		}

	});

	leeTotalCalendarizadoRG();
}


function leeTotalCalendarizadoRG() {
	let montoTotalEjercer;
	queryFormPost({
		queryName : "montoCalendarizadoRead",
		async : false,
		callback : function() {
			if ($("#DESTINO_GASTO").val() == "CCRE" || $("#DESTINO_GASTO").val() == "CERE" ||$("#DESTINO_GASTO").val() == "CPRP"
			 || $("#DESTINO_GASTO").val() == "GCRE" || $("#DESTINO_GASTO").val() == "RCRE") 
			{
				montoTotalEjercer =Number(quitaFmt($("#mImporteBruto").val())) + Number( $("#mImporteRetencion").val());	
			} else {
				montoTotalEjercer =Number(quitaFmt($("#mImporteNeto").val()));
			}
							
			let montoCalendario = parseFloat(quitaFmt($("#mMontoCalendarizado").val()));
			let montoRestante = (montoTotalEjercer - montoCalendario).toFixed(2);
			montoRet= parseFloat(quitaFmt($("#mMontoRetenciones").val()));
			montoRetFact= parseFloat(quitaFmt($("#mMontoRetFact").val()));
			montoRetXCapturar= (montoRetFact - montoRet).toFixed(2);;

			montoTotalEjercer = currencyFormatter({currency:'USD', value: montoTotalEjercer });
			$("#mMontoEjercer").val(montoTotalEjercer);	
			
			montoCalendario = currencyFormatter({currency:'USD', value: montoCalendario });
			$("#mMontoCalendarizado").val(montoCalendario);
			
			montoRestante = currencyFormatter({currency:'USD', value: montoRestante });
			$("#mMontoPorEjercer").val(montoRestante);
			
			montoRet = currencyFormatter({currency:'USD', value: montoRet });
			$("#mMontoRetenciones").val(montoRet);	
			
			montoRetFact = currencyFormatter({currency:'USD', value: montoRetFact });
			$("#mMontoRetFact").val(montoRetFact);		
			
			montoRetXCapturar = currencyFormatter({currency:'USD', value: montoRetXCapturar });
			$("#mMontoRetPendientes").val(montoRetXCapturar);		
			}
	});
}

function validaRetenciones(){
	if ($("#montoRetencionRG").val() == "")
		$("#montoRetencionRG").val("0.00");
	
	let retCapturadas = parseFloat(quitaFmt($("#montoRetencionRG").val()));	
	if (montoRetFact < retCapturadas ){
		Swal.fire("Error","Las retenciones capturadas no puede ser mayor al importe de retenciones en facturas.","error");
		return false;
	}
}

/*
function consultaSuficiencias() {
	//Cambiar el RFC del contrato de acuerdo a lo establecido por Tesoreria
			if ($("#DESTINO_GASTO").val()=="CCRE" || $("#DESTINO_GASTO").val()=="GCRG" || $("#DESTINO_GASTO").val()=="CERE" 
			 || $("#DESTINO_GASTO").val()=="GCRE" || $("#DESTINO_GASTO").val()=="NORN" || $("#DESTINO_GASTO").val()=="RCRE"  ){
				$("#rfcContrato").val("CNF010405EG1");
			} else if ( $("#DESTINO_GASTO").val()=="CERG"  ) {
				$("#rfcContrato").val("TESOFE");
			} else {
				$("#rfcContrato").val($("#cIdRFC_RelacionGasto").val());
			}
			
			querySelectPost("consultaSuficienciasRG", "cFolioSuficiencia",{async: false });
			
}

function actualizaContrato (){
	$("#cIdContrato").val($("#cFolioSuficiencia").val());
}

*/
function agregarDetalle() {
	var guardado = false;
		logErrores = "";
			
				$.ajax({
					url : "../viaticos/agregaDetalle",
					type : 'post',
					async : false,
					data : $("#formPagos").serialize(),
					dataType : 'json',
					success : function(j) {
						var exito = j.success;
			
						if (exito) {
							guardado = true;
						} else {
							var errores = j.errorList;
							var cnt = 0;
							for (cnt = 0; cnt < errores.length; cnt++) {
								logErrores = logErrores + errores[cnt] + "\n";
							}
						}
					},
					error : function(errorThrown) {
						logErrores = errorThrown.ERROR;
					}
				});
			
				if (!guardado)
					Swal.fire("Error al agregar el detalle", logErrores, "error")
				else {
					queryFormPost("CambioStatusRELGASTOUpdate", {async: false });		
				    parent.document.getElementById("pb_send").disabled=false;
					$(".paso03").attr('disabled', true);
					$(".pasoTres").show();
					$("#numPaso").val("6");
					datosResumen();
					
					$("#divImprimePoliza").show();
		 		 	$("#divImprimeAnexo").show();
		 		 	$("#L04").click();
				}	
				return guardado;
}

function datosResumen() {
	$("#DCD_IMP_BRUTO").val(Number($("#mImporteBruto").val()) + Number($("#importenoComprobable").val()) );
	$("#DCD_RETENCION").val(Number($("#mImporteRetencion").val()));
	
	if ($("#cTipoRetencion").val() == 0){
		$("#DCD_ISR").val(Number($("#mImporteRetencion").val()));
	} else {
		if ($("#cOtraRetencion").val() != 0){
			$("#DCD_IVA").val(Number($("#mImporteIva").val()));
			$("#DCD_ISR").val(Number($("#mImporteISR").val()));
		} else if ($("#cOtraRetencion").val() == 8 || $("#cOtraRetencion").val() == 9 || $("#cOtraRetencion").val() == 19 ){
			$("#DCD_OTRAS_RET").val(Number($("#mOtras").val()));
		} else if ($("#cOtraRetencion").val() == 3 ) {
			$("#DCD_MIL5").val(Number($("#mOtras").val()));
		}
	}
}
