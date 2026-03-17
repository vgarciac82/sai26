/**
 * Memoria para guardar el monto a editar. Si cierran el dialogo (por cancelar o
 * cerrar) suma esto al remanente
 */
var montoEditar = 0.0;
var nDocRenglonEditar = "";
var sicop = 0;
/** Para regresar la solicitud (caNoContrarrecibo.) **/
var solicitud = "";
/**
 * Crea el dialogo para editar el debe decir
 */
function creaDialogoEditar() {	
	
	$("#EdtaRenglon")
			.dialog(
					{
						autoOpen : false,
						height : 500,
						width : 650,
						modal : true,
						buttons : {
							"Agregar" : function() {
								actualizarImportes();
								$(this).dialog("close");      
							},
							"Cancelar" : function() {

								$(this).dialog("close");
							}
						},
						open: function(){
														
						},
						close : function() {
						}
					});
}

function actualizarImportes(){
		var iRow = $("#nDocRenglonR").val();
		var ep = $("#EPR").val();
		var mes = $("#nMesR").val();
		var m2millar = parseFloat(quitaFrmt($("#M2Millar").val()));
		var mObra5 = $("#mObra5").val();
		var mimporteFlete4 = parseFloat(quitaFrmt($("#mImporteFlete4").val()));
		var mIsrHonorarios = parseFloat(quitaFrmt($("#mISRHonorarios").val()));
		var mISRArrenda = parseFloat(quitaFrmt($("#mISRArrenda").val()));
		var mRetImpuestoCedular =  parseFloat(quitaFrmt($("#mRetImpuestoCedular").val()));
		var mimporteIvaHonorarios = parseFloat(quitaFrmt($("#mImporteIvaHonorarios").val()));
		var mImporteIvaArrenda =  parseFloat(quitaFrmt($("#mImporteIvaArrenda").val()));
		var mImporteISRLaudos = parseFloat(quitaFrmt($("#mImporteISRLaudos").val()));
		var mISROtros = parseFloat(quitaFrmt($("#mISROtros").val()));
		var mImporteIVA6 = parseFloat(quitaFrmt($("#mImporteIva6").val()));
		var mImporte = parseFloat(m2millar) + parseFloat(mObra5) +parseFloat(mimporteFlete4) +parseFloat(mIsrHonorarios) +parseFloat(mISRArrenda) + parseFloat(mRetImpuestoCedular) + parseFloat(mimporteIvaHonorarios) +parseFloat(mImporteIvaArrenda)+parseFloat(mImporteISRLaudos)+parseFloat(mISROtros)+parseFloat(mImporteIVA6);
		var rfc = $("#RFC").val();
		var cmdBorrar = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Descartar Renglon\" onClick=\"descartar('" + iRow + ");\" />";
		
		$('#tblPagadoFiltrado').dataTable().fnUpdate( [ iRow,  mes, ep, mImporte, m2millar, mObra5, mimporteFlete4,mIsrHonorarios, mISRArrenda,mimporteIvaHonorarios,mImporteIvaArrenda,mRetImpuestoCedular, mImporteISRLaudos,mISROtros,mImporteIVA6,rfc,  cmdBorrar ], nDocRenglonEditar);
}

function tblPagoDblClick(event) {

	montoEditar = 0.0;
	var aPos = oTable.fnGetPosition(event.target.parentNode);
	var aData = oTable.fnGetData(aPos);

	nDocRenglonEditar = aPos;
	var EP = aData[2];
	var nMes = aData[1];
	
	var renglon = aData[0];
	var rfc = aData[15];
	var disponible = aData[3];

	$("#EPR").val(EP);
	$("#nMesR").val(nMes);
	$("#RFC").val(rfc);
	$("#nDocRenglonR").val(renglon);
	$("#mDisponible").val(disponible);
	$("#EdtaRenglon").dialog("open");	
}

/**
 * Retira el formato monetario de una cadena;
 * 
 */
function quitaFrmt(fld) {
	var valcol = fld.toString();
	valcol = valcol.replace(/[$]/g, "");
	valcol = valcol.replace(/,/g, "");
	return valcol;
}


/**
 * Elimina un renglon del detalle.
 */
function descartar(renglon) {
	if (confirm("Esta seguro de borrar el renglon?")) {
		var info = oTable.fnGetData();

		for (i = 0; i < info.length; i++) {
			var renglonInfo = info[i];
			if (renglonInfo[0] == renglon) {
				oTable.fnDeleteRow(i);
				break;
			}	
		}
		renumeraRenglones();

	}
}	

function renumeraRenglones() {

	for ( var i = 0; i < oTable.fnGetData().length; i++) {
		
		 var arr = oTable.fnGetData()[i]
		 arr[16] = '<img id= "R' + i + '" src="../imagenes/cancelar.gif" width="25" height="21" alt="Eliminar Renglon"  onClick="descartar( ' +(i + 1)  + ');"/>';
		 arr[0] = i+1;
		 oTable.fnUpdate(arr, i);
		
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

function capturaFolioSicop (){
	$("#dialogSolicitud").dialog("open");
}

function creaDialogoCaptura() {	
	
	$("#dialogSolicitud")
			.dialog(
					{
						autoOpen : false,
						height : 200,
						width : 250,
						modal : true,
						buttons : {
							"Agregar" : function() {
								$("#folioSICOP").val($("#cRETE").val());
								guardaValor();
								$(this).dialog("close");      
							},
							"Cancelar" : function() {

								$(this).dialog("close");
							}
							
						},
						open: function(){
														
						},
						close : function() {
						}
					});
}

function guardaValor(){
	 
	queryFormPost("actualizaFolio", {async: false });
	$("#nFolioSICOP").val($("#folioSICOP").val());
}
