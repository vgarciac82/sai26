function muestraDetallePAAS() {
	$('#tblCucop tr').live('dblclick', function() {
		if ($(this).hasClass('row_selected')) {
			$(this).removeClass('row_selected');
		} else {
			$(this).addClass('row_selected');
		}
		var anSelected = fnGetSelected(oTableCucop);
		if (anSelected != "") {
			var aData = oTableCucop.fnGetData(anSelected[0]);
			$("#cIdCABM").val(aData[0]);
			$("#cDescripcion").val(aData[1]);
			$("#cIdSubPartida").val(aData[3]);
			$(this).removeClass('row_selected');
			mostrar();
		}
	});
}
function fnGetSelected(oTableLocal) {
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	for (var i = 0; i < aTrs.length; i++) {
		if ($(aTrs[i]).hasClass('row_selected')) {
			aReturn.push(aTrs[i]);
		}
	}
	return aReturn;
}
function mostrar() {
	oTableCAMBs2 = $("#tblRequisicionMeses2").dataTable({
		bScrollCollapse : true,
		bDestroy : true,
		"iDisplayLength" : 12,
		oLanguage : {
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
		},
		bServerSide : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/"
			+ window.location.pathname.split("/")[1] +
			"/crud?rt=t&ql=fn_Calendario2('" + $("#cEjercicio").val() + "', '" +
			$("#cIdUnidadEjecutora").val() + "', '" + $("#cIdCABM").val() + "' , '" +
			$("#cIdSubPartida").val() + "','RC')", //"&qw=" + qw,
		bProcessing : true,
		sPaginationType : "full_numbers",
		bJQueryUI : true,
		aaSorting : [ [ 0, "asc" ] ],
		aoColumns : [
			{
				sName : "nIdPeriodo",
				bVisible : false
			},
			{
				sName : "mes"
			},
			{
				sName : "cIdCABM"
			},
			{
				sName : "nCantidadDisponibilidad"
			},
			{
				sName : "mMontoDisponibilidad"
			},
			{
				sName : "nPorcentajeIVA",
				bVisible : false
			},
			{
				sName : "cCABM",
				bVisible : false
			},
			{
				sName : "CantAgregar"
			},
			{
				sName : "mPrecioUnitario",
				bVisible : false
			}

		]
	});
}
function agregarLineasAgrupadas() {
	//validar campos
	if (!validaCamposVaciosPAAAS()) {
		return;
	}
	var arrayTab = arrayTabla();
	if (arrayTab != null) {
		if (arrayTab.length > 0) {
			llamadaAjax(1, arrayTab);
		} else {
			Swal.fire("Capture","Falta agregar cantidades en la segunda tabla", "warning");
		}

	}
}
function validaCamposVaciosPAAAS() {
	var msg = "";
	var token = "";
	var resp = true;
	if (parseInt($("#cIdCapitulo").val(), 10) <= 0) {
		msg = "Debe seleccionar un capítulo del gasto."
		token = "\n";
	}
	if ($("#cIdSubPartida").val() == "") {
		msg = msg + token + "Debe seleccionar una partida."
		token = "\n";
	}
	if ($("#cIdAlmacenEntrega").val() == "") {
		msg = msg + token + "Debe seleccionar un almacen."
		token = "\n";
	}
	if ($("#fechaInicio").val() == "") {
		msg = msg + token + "Debe seleccionar una fecha de inicio."
		token = "\n";
	}
	if ($("#fechaFin").val() == "") {
		msg = msg + token + "Debe seleccionar una fecha fin o de entrega."
		token = "\n";
	}
	if (msg != "") {
		Swal.fire('Capture',msg,'info');
		resp = false;
	}
	return resp;
}
function arrayTabla() {
	var arregloTmp = null;
	var arrayFila = null;
	var aTrs = oTableCAMBs2.dataTable().fnGetNodes();
	var valor = "";
	var token = "";
	var nTr;
	var jqInputs;
	if (aTrs.length > 0) {
		arregloTmp = new Array();
		for (var i = 0; i < aTrs.length; i++) {
			nTr = oTableCAMBs2.dataTable().fnGetData(aTrs[i]);
			valor = "";
			arrayFila = new Object();
			jqInputs = $('input,select', aTrs[i]);
			if (parseInt(document.getElementById('' + jqInputs[0].id).value, 10) <= 0) {
				continue;
			}
			if (parseInt(document.getElementById('' + jqInputs[0].id).value, 10) > parseInt(nTr[3])) {
				Swal.fire("Revise","No hay cantidad disponible en el mes de " + nTr[1], "warning");
				arregloTmp = null;
				break;
			}
			valor = nTr[0] + "," + nTr[2] + "," + nTr[5] + "," + (nTr[8].replace(',', '')) + "," + document.getElementById('' + jqInputs[0].id).value; //idmes,cucop,iva,pu,cantidad
			arrayFila = [ valor, "|" ];
			arregloTmp.push(arrayFila);
			arrayFila = null;
		}
	} else {
		Swal.fire("Capture","Seleccionar un CUCOP de la primer tabla.", "info");
	}
	return arregloTmp;
}

function llamadaAjax(operacion, arregloDatos) {
	$.ajax({
		url : "../servlet/ConsumePAASServlet",
		type : 'post',
		async : false,
		data : 'operacion=' + operacion + '&tablaDatos=' + arregloDatos + '&nFolio=' + $("#nFolioPago").val() + '&capitulo=' + $("#cIdCapitulo").val()
			+ '&partida=' + $("#cIdSubPartida").val() + '&cabm=' + $("#cIdCABM").val() + '&fechaIni=' + $("#fechaInicio").val()
			+ '&fechaFin=' + $("#fechaFin").val() + '&cIdunidadEjecutora=' + $("#cIdUnidadEjecutora").val() + '&cEjercicio=' + $("#cEjercicio").val()
			+ '&almacenEntrega=' + $("#cIdAlmacenEntrega").val() + '&tipoProcedimiento=' + $("#tipoAdjudicacion").val() + '&fundamentoLeg=' + $("#nIdFundamentoLeg").val(),
		dataType : 'json',
		success : function(j) {
			creaDTCucop();
			createDTCalendarioPAAS();
			creaDTLineas();
			datosGuardadosPAAS();
			Swal.fire("OK",j[0].MENSAJE,"success");
		}
	});
}

function onlyIntegers(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	return (keyPressed >= 48 && keyPressed <= 57);
}
function onlyMoney(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	if (keyPressed == 46 || keyPressed == 36)
		return true;
	return (keyPressed >= 48 && keyPressed <= 57);
}
function actualizaLineas() {
	var arrayTab = arrayTabLieas();
	if (arrayTab != null) {
		llamadaAjax(2, arrayTab);
	}
}
function arrayTabLieas() {
	var arregloTmp = null;
	var arrayFila = null;
	var aTrs = oTableLineas.dataTable().fnGetNodes();
	var valor = "";
	var token = "";
	var nTr;
	var jqInputs;
	var pu = 0;
	var neto = 0;
	if (aTrs.length > 0) {
		arregloTmp = new Array();
		arrayFila ="";
		for (var i = 0; i < aTrs.length; i++) {
			nTr = oTableLineas.dataTable().fnGetData(aTrs[i]);
			valor = "";
			
			jqInputs = $('input,select', aTrs[i]);
			pu = document.getElementById('' + jqInputs[1].id).value;
			pu = pu.replace(',', '');
			pu = pu.replace('$', '');
			pu = pu.replace(' ', '');
			
			neto = document.getElementById('' + jqInputs[3].id).value;
			
			neto = neto.replace(/[\$, ]/g, '');
			console.log(neto);

			if (parseFloat(pu) <= 0) {
				Swal.fire("NOTA","El precio unitario no puede ser menor o igual a 0.00","warning");
				arregloTmp = null;
				break;
			}
			valor = nTr[0] + "#" + nTr[1] + "#" + (document.getElementById('' + jqInputs[0].id).value) + "#" + nTr[4] + "#" + pu + "#" + (document.getElementById('' + jqInputs[2].id).value) + "#" + neto + "#|"; //linea,cucop,descripción,Cantidad,precio unitario,idIVA,monto neto,fin de linea
			//alert("valor"+valor);
			arrayFila = arrayFila+token+valor;
			token="#";
		}
		arregloTmp.push(arrayFila);
		arrayFila = null;
	} else {
		Swal.fire("Seleccione","Seleccionar un CUCOP de la primer tabla.","info");
	}
	return arregloTmp;
}
function eliminaLinea(nLinea) {
	var operacion = 3;

	$.ajax({
		url : "../servlet/ConsumePAASServlet",
		type : 'post',
		async : false,
		data : 'operacion=' + operacion + '&nFolio=' + $("#nFolioPago").val() + '&capitulo=' + $("#cIdCapitulo").val()
			+ '&partida=' + $("#cIdSubPartida").val() + '&cabm=' + $("#cIdCABM").val() + '&fechaIni=' + $("#fechaInicio").val()
			+ '&fechaFin=' + $("#fechaFin").val() + '&cIdunidadEjecutora=' + $("#cIdUnidadEjecutora").val() + '&cEjercicio=' + $("#cEjercicio").val()
			+ '&almacenEntrega=' + $("#cIdAlmacenEntrega").val() + '&tipoProcedimiento=' + $("#tipoAdjudicacion").val() + '&fundamentoLeg=' + $("#nIdFundamentoLeg").val()
			+ '&nLinea=' + nLinea,
		dataType : 'json',
		success : function(j) {
			creaDTCucop();
			createDTCalendarioPAAS();
			creaDTLineas();
			datosGuardadosPAAS();
			alert(j[0].MENSAJE);
		}
	});
}
function datosGuardadosPAAS() {
	queryFormPost("datosGuardadosPAAS", {
		async : false
	});
}
function muestraCapitulos() {
	//querySelectPost("mCatalogoCapituloReadPagoDirec", "cIdCapitulo", {async : false});
}
function muestraPartidas() {
	if (parseInt($("#cIdCapitulo").val(), 10) <= 0) {
		Swal.fire("Seleccione...","Primero debe de seleccionar un capitulo.","warning");
		return;
	}
	window.open('../Generador/SAICYS/CatalogoPartida.jsp?cidCapitulo=' + $("#cIdCapitulo").val() + '&nameForm=formPagos', 'Partidas', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
}
function muestraAlmacenes() {
	window.open('../Generador/SAICYS/CatAlmacenes.jsp?nameForm=formPagos', 'Almacenes', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
}
function desactivaBackspace(event) {
	if (window.event && window.event.keyCode == 8) {
		window.event.keyCode = 505;
	}
	if (window.event && window.event.keyCode == 505) {
		return false;
	}
	return true;
}
function initPestanaPAAAS() {
	$("#fechaInicio").val($("#fechaAplicacion").val());
	$("#fechaFin").val($("#fechaAplicacion").val());
	creaDTCucop();
	datosGuardadosPAAS();
	createDTCalendarioPAAS();
	creaDTLineas();
	muestraDetallePAAS();
}

function clearDatosPartida() {
	var partida = $("#partida").val();
	$("#partida").val('');
	$("#cIdSubPartida").val('');
	if (partida != "") {
		creaDTCucop();
	}


}
function showPAAS() {
	window.open('../Generador/SAICYS/ProgramaAnual.jsp?tab=0','PAAS', 'status=1, width=1024px, height=768px, left=0px, scrollbars=Yes,resizable=yes');
}
function creaDTCucop() {
	var qw = "cIdUnidadEjecutora = '" + $("#cIdUnidadEjecutora").val()
		+ "' AND cIdSubPartida = '" + $("#cIdSubPartida").val()
		+ "' AND cEjercicio = '" + $("#cEjercicio").val() + "'";

	oTableCucop = $("#tblCucop").dataTable({
		bScrollCollapse : true,
		bDestroy : true,
		oLanguage : es_MX,
		bServerSide : true,
		bProcessing : true,
		bLengthChange : true,
		iDisplayLength : 10,
		sPaginationType : "full_numbers",
		bJQueryUI : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mCucopsDisponiblesAgrupados&qw=" + qw,
		aaSorting : [ [ 1, "asc" ] ],
		aoColumns : [
			{
				sName : "cidCABM"
			},
			{
				sName : "cCabm"
			},
			{
				sName : "cantDisp"
			},
			{
				sName : "cidSubpartida",
				bVisible : false
			}
		],
		fnInitComplete : function(oSettings, json) {},
		fnDrawCallback : function(oObj) {}
	});
}
function createDTCalendarioPAAS() {
	oTableCAMBs2 = $("#tblRequisicionMeses2").dataTable({
		bScrollCollapse : true,
		bDestroy : true,
		oLanguage : es_MX,
		bServerSide : true,
		bProcessing : true,
		bLengthChange : true,
		iDisplayLength : 12,
		sPaginationType : "full_numbers",
		bJQueryUI : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_Calendario2('2018', 'A02', '21100133' , '21101','RC')",
		aaSorting : [ [ 1, "asc" ] ],
		aoColumns : [
			{
				sName : "nIdPeriodo",
				bVisible : false
			},
			{
				sName : "mes"
			},
			{
				sName : "cIdCABM"
			},
			{
				sName : "nCantidadDisponibilidad"
			},
			{
				sName : "mMontoDisponibilidad"
			},
			{
				sName : "nPorcentajeIVA",
				bVisible : false
			},
			{
				sName : "cCABM",
				bVisible : false
			},
			{
				sName : "CantAgregar"
			},
			{
				sName : "mPrecioUnitario",
				bVisible : false
			}

		],
		fnInitComplete : function(oSettings, json) {},
		fnDrawCallback : function(oObj) {}
	});
}
function creaDTLineas() {
	var qw = "1=1 and cEjercicio='" + $("#cEjercicio").val() + "' and cIdUnidadEjecutora='" + $("#cIdUnidadEjecutora").val() + "' and nFolioPagoDirecto=" + $("#nFolioPago").val();
	oTableLineas = $("#tblLineas").dataTable({
		bScrollCollapse : true,
		bDestroy : true,
		oLanguage : es_MX,
		bServerSide : true,
		bProcessing : true,
		bLengthChange : true,
		iDisplayLength : 10,	
		sPaginationType : "full_numbers",
		bJQueryUI : true,
		sAjaxSource : window.location.protocol + "//" + window.location.host +
			"/" + window.location.pathname.split("/")[1] +
			"/crud?rt=t&ql=v_mLineasPagoDirecto&qw=" + qw,

		aaSorting : [ [ 0, "asc" ] ],
		aoColumns : [
			{
				sName : "nIdLineas"
			},
			{
				sName : "cIdCABM"
			},

			{
				sName : "cDescripcion"
			},
			{
				sName : "cDescripcionAdicional"
			},
			{
				sName : "nCantidad"
			},
			{
				sName : "mPrecioUnitario"
			},
			{
				sName : "nPorcentajeIVA"
			},
			{
				sName : "mImporteNeto"
			},
			{
				sName : "cUnidadMedida"
			},
			{
				sName : "lineaElimina"
			}
		],
		fnInitComplete : function(oSettings, json) {},
		fnDrawCallback : function(oObj) {}
	});
}

/*Oculta e impide modificar el pass capturado*/
function inhabilitaCapturaPAAS() {
}

/**
 * Valida que se haya capturado la informacion minima del PAAS y que no supere las 300 UMAS;
 */
function validacionesPAAS() {
	var continuar = false;
	$("#accion").val("validaPAAS");

	$.ajax({
		url : "../egresos/validaPAAS",
		type : 'get',
		async : false,
		data : $("#formPagos").serialize(),
		dataType : 'json',
		success : function(j) {
			var exito = j.success;
			if (exito) {
				continuar = true;
			} else {
				var errores = j.errorList;
				var logErrores = "";
				var cnt = 0;
				for (cnt = 0; cnt < errores.length; cnt++) {
					logErrores = logErrores + errores[cnt] + "\n";
				}
				alert(logErrores);
			}
		}
	});

	return continuar;
}

/**
 * Funcion de callback se llama al terminar exitosamente de capturar el PAAS. Debe ocultar todo boton que altere el estatus y hacer de solo lectura los componentes que muestran informacion.
 * 
 */
function paasTerminado() {
	return true;
}

function leeImporteBruto() {
	
	var ejecutado = false;

	queryFormPost({
		queryName : "importeBrutoPDRead",
		async : false,
		callback : function() {
			ejecutado = true;
		}
	});
	
	if( !ejecutado )
		throw "No se encontro el importe bruto";
	
}