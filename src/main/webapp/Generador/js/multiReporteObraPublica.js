/**
//IRD 20131121	RO-0009
 * DataTable que almacena los resultados de la busqueda.
 */
var dTable;

/**
 * Folio para el Pago de Pasivo.
 */
var folioSAI = "";

/**
 * Parametros de lenguaje
 */
var lengParams = {
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

/**
 * Inicializa los campos de la pantalla.
 */
function init2() {

//	setDblClck();
	$("input.AyudaSyC").subIniciaDlg();
	$("input.autoCompletaSyC").subIniciaAutoCompleta();

/*	$("#searachButton").button().click(function() {
		search();
	});
*/

	/*$("#yes")
			.button()
			.click(
					function() {
						$.blockUI({
							message : "<h1>Espere ...</h1>"
						});
						$("#folioSAI").val(folioSAI);
						queryFormPost({
							queryName : 'readExistePagoPasivoObraPublica',
							async : false,
							callback : function() {
								var operAct = $("#OperacionActual").val();
								if (operAct != "0") {
									alert("Ya existe un pago de pasivo del contrato seleccionado.")
									$.unblockUI();
								} else {
									$("#mainForm").submit();
								}
							}
						});
					});*/



	$("#cveContrato").focus();
	//$(".tabs").tabs();

	/*$(function() {
		$("#fConvocatoria").datepicker( {
			minDate: "-6M -1Y", maxDate: "+0M +1M", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fAclaracion").datepicker( {
			minDate: "-6M -1Y", maxDate: "+0M +1M", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fRecepProp").datepicker( {
			minDate: "-6M -1Y", maxDate: "+0M +1M", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fRecepFallo").datepicker( {
			minDate: "-6M -1Y", maxDate: "+0M +1M", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			
			});
		});
	
	$(function() {
		$("#fInicioCom").datepicker( {
			minDate: '01/01/2012', maxDate: "+0M +1Y", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fFinCom").datepicker( {
			minDate: '01/01/2012', maxDate: "+0M +5Y", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
			
	$(function() {
		$("#fFinConv").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	$(function() {
		$("#fSuspencion").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fReactiva").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fPrecom").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fAdicionales").datepicker( {
			minDate: '01/01/2012', maxDate: "+0M +1Y", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fEntregaVentanilla").datepicker( {
			minDate: '01/01/2012', maxDate: "+0M +1Y", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
		
	$(function() {
		$("#fperiodoEstimacionIni").datepicker( {
			minDate: '01/01/2012', maxDate: "+0M +1Y", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	//MLR 1411/2013	R0-0007	Agregar campos a estimaciones
	$(function() {
		$("#fperiodoEstimacionFin").datepicker( {
			minDate: '01/01/2012', maxDate: "+0M +1Y", changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});		
	  	
	$(function() {
		$("#fPago").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fInicioFisico").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
	
	$(function() {
		$("#fFinFisico").datepicker( {
			showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
		//MLR 1411/2013	R0-0007	Agregar campos a estimaciones
	  	$(function() {
		$("#pEstimacionInicial").datepicker( {
			changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
		
		$(function() {
		$("#pEstimacionfinal").datepicker( {
			changeMonth: true, changeYear: true, showOn : "button",
			dateFormat : "dd/mm/yy",
			buttonImage : "images/calendar.gif",
			buttonImageOnly : true
			});
		});
		
		$(function() {
			$("#fRecepcion").datepicker( {
				minDate: '01/01/2012', maxDate: "+0M +1D", changeMonth: true, changeYear: true, showOn : "button",
				dateFormat : "dd/mm/yy",
				buttonImage : "images/calendar.gif",
				buttonImageOnly : true
			});
		});
		
		readCarterasProimpro();
		$("#cCarteraProyec").change(function() {
			$("#cCarteraProyec_2").val($("#cCarteraProyec").val());
			clearSelect( [ "cOLI", "epSel" ]);
			readOLIs();
		});
*/
		querySelectPost("tCatalogoTipoObraReadBusqueda", "cIdTObra", {
			async : false
		});
		querySelectPost("tCatalogoTipoRecursosReadBusqueda", "cIdTipRec", {
			async : false
		});

/*		querySelectPost("tCatalogoTipoArticuloReadBusqueda", "cIdTipoAdjudica", {
			async : false
		});
	*/	
		querySelectPost("CatalogoTipoContratoObraReadBusqueda", "cIdTipoContratoObra", {
			async : false
		});
		querySelectPost("CatalogoClaseContratoObraReadBusqueda", "cIdClaseContratoObra", {
			async : false
		});
		querySelectPost("UnidadresponsableRead", "cIdUnidadAdministrativa", {
			async : false
		});
		querySelectPost("tCatalogoAdicionalesRead", "cIdAdicionales", {
			async : false
		});

		querySelectPost("CatalogoEsquemaPrecioReadBusqueda", "esquemaPrecios", {
			async : false
		});

		querySelectPost("catEntFedRead", "entidadobra", {
			async : false
		});

		$('#Link04').hide();

		$("#cCarteraProyec_2").hide();
		$("#cOLI_2").hide();
		

}

function Grid()
{
	window.open('MultiReporteGrid.jsp?id=10&cCartera=&cPartida=&cPrograma=', 'MultiReporteGrid', 'status=1, width=900px, height=700px');
if ($.trim(document.getElementById("ep").value)!=""){
		//window.alert($.trim(document.getElementById("ep").value));
		rellenaCampos();
	}
	//window.location.href="MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>";
 	return false;
}

function readCarterasProimpro(){
	$.ajax({
	url : '../proimpro/readInfo',
	dataType : 'json',
	data : {
		"accion" : "READ_CARTERA"
	},
	async : false,
	success : function(RS) {
		var exito = RS.success;
		if(exito=="true"){
			var carteras = RS.data_1;
			clearSelect( ["cCarteraProyec"] );
			for( var i = 0; i<carteras.length; i++)
				$("#cCarteraProyec").append( '<option value="' + carteras[i].cartera + '">' + carteras[i].cartera + '</option>');
		}else{
			var msg = RS.data_1.result;
			alert("Ocurrio el siguiente problema al cargar las carteras:\n"+msg);
		}
	},
	error : function(xhr, textStatus, errorThrown) {
		alert("Advertencia: " + xhr.responseText + "\nEstatus: "
				+ textStatus + "\n" + errorThrown);
		r = true;
	}
});
}

function readOLIs(){
	clearSelect( ["cOLI"] );
	if ($("#cCarteraProyec").val() != "-1")
		{
	$.ajax({
		url : '../proimpro/readInfo',
		dataType : 'json',
		data : {
			"accion" : "READ_OLIS",
			"cartera" : $("#cCarteraProyec").val()
		},
		async : false,
		success : function(RS) {
			var exito = RS.success;
			if(exito=="true"){
				var olis = RS.data_1;
				for( var i = 0; i<olis.length; i++)
					$("#cOLI").append( '<option value="' + olis[i].oli + '">' + olis[i].oli + '</option>');
			}else{
				var msg = RS.data_1.result;
				alert("Ocurrio el siguiente problema al cargar las carteras:\n"+msg);
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			r = true;
		}
	});
	}
}

function cargaAreas(){
	$("#cU_UE").val($("#cUnidadEjecutora").val())
	if( $("#cU_UE").val() != '' )
		querySelectPost("obraPublicaCatalogoAreas", "id_area", { async : false });
}

/**
 * Busca contratos en base al numero.
 */
function search() {
	folioSAI = "";
	$("#OperacionActual").val("");
	if ($("#cveContrato").val() == "") {
		alert("Debe ingresar el n\u00FAmero de contrato");
		$("#cveContrato").focus();
		return;
	} else {
		$
				.blockUI({
					message : "<p><img src='imagenes/wait24trans.gif' />&nbsp;&nbsp;Buscando. Por favor espere...</p>"
				});
		var condition = " ccvecontrato LIKE '%" + $("#cveContrato").val() + "%'";
		var tableName = "BUSCA_CONTRATO_PAGO_PASIVO";
		$.getJSON("../catalogos/SelectJson.jsp", {
			Tabla : tableName,
			Param : condition,
			MaxReg : "",
			ajax : 'false'
		}, function(data) {
			dTable.fnClearTable();
			for ( var i = 0; i < data.length; i++) {
				dTable.fnAddData([ data[i].Col0, data[i].Col1, data[i].Col2,
						data[i].Col3,data[i].Col4 ]);
			}
			$.unblockUI();
			if (i == 0)
				alert("No se encontraron coincidencias para el contrato: "
						+ $("#cveContrato").val());
		});
	}

}

/**
 * Funcion general que limpia el contenido de un select agregando una opcion por
 * default con valor -1
 */
function clearSelect(idSel) {
	for ( var i = 0; i < idSel.length; i++)
		$('#' + idSel[i]).find('option').remove().end().append(
				'<option value="-1"></option>');
}
function capitaliseFirstLetter(string)
{
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
}

function toTitleCase(str) {
    return str.toLowerCase().replace(/(?:^|\s)\w/g, function(match) {
        return match.toUpperCase();
    });
}

function formSubmited() {
    alert("Beneficiario enviado!");
}


function insertactasPresupuestales()
{
	var	zona2 = document.getElementById('zonaInsercion') ;
	querySelectPost("catalogoCtasPresupuestalesRead","ctasPresupuestales", {async: false });
	ctasPresupuestales = document.getElementById("ctasPresupuestales");
	zona = "<table border=\"0\" width=\"1028\"  height=\"10\">";
	zona+= "<tr>";
	zona+="<td align=\"right\">Presupuesto:</td>";
	for(i=1;i<ctasPresupuestales.options.length+1;i++) {
//		zona+="<td><input type=\"checkbox\" id=\""+ctasPresupuestales.options[i-1].value.replace(" ","").toLowerCase()+"\" name=\"CuentasPresup\" value=\""+ctasPresupuestales.options[i-1].text+"\"/>"+toTitleCase(ctasPresupuestales.options[i-1].value)+"<br></td>";
		zona+="<td><input type=\"checkbox\" id=\""+ctasPresupuestales.options[i-1].value.replace(" ","").toLowerCase()+"\" name=\""+ctasPresupuestales.options[i-1].value.replace(" ","").toLowerCase()+"\" value=\""+ctasPresupuestales.options[i-1].text+"\"/>"+toTitleCase(ctasPresupuestales.options[i-1].value)+"<br></td>";
		if ((i % 5)==0){
			zona+="</tr>";
			zona+="<tr><td></td>";
		} 
	}		
	zona+="</tr>";
	zona+="</table>";
	zona2.innerHTML=zona;
}

function fnSeleccionaMes()
{
	querySelectPost("catalogoMesesRead","InfoRegMes", {async: false });
	InfoRegMes = document.getElementById("InfoRegMes") ;
	$("#InfoRegMes").val( InfoRegMes.options.length ).attr('selected',true);
}
