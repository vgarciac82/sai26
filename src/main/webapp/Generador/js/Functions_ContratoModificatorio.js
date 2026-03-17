function mostrarPartidasModificadas(){
	var campos = "'" + $("#cContratoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
	var query="fn_mContratoModificadoPartidasModificadas(" + campos + ")";
	var cIdContratoDef=$("#cContratoDefinitivo").val();
	if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0 && 0==$("#isConvEjercicioAnt").val() ){
		query="fn_mContratoPluModificadoPartidasModificadas(" + campos + ")";
	}
	if(1==$("#isConvEjercicioAnt").val()){
		query="fn_mContratoModificadoEjercAntPartidasModificadas(" + campos + ")";
	}
	PartidasModificadas = $("#tblPartidasMods").dataTable({
		//sScrollY: "200px",
		sScrollX: "100%",
		//sScrollXInner: "200%",
		bScrollCollapse: true,
		bDestroy: true,
		bAutoWidth: true,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay registros",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			
			{ sName: "nIdLineaConsolidado" },
			{ sName: "cIdSolicitud" },
			{ sName: "cIdLineaSolicitud" },
			{ sName: "cIdCABM" },
			{ sName: "cDescripcion" },
			{ sName: "nCantidad" },
			{ sName: "PrecioUnitario" },
			{ sName: "MontoBruto" },
			{ sName: "MontoNeto" },
			{ sName: "PorcentajeMod" },
			{ sName: "cIdTipoConsolidado", bVisible: false },
			{ sName: "cIdUnidadEjecutora", bVisible: false },
			{ sName: "cIdConsecutivoConsolidado", bVisible: false },
			{ sName: "MontoNetoD", bVisible: false }
		]
	});	
}
function mostrarPartidasModificadas_ConvReduccion(){
	var campos = "'" + $("#cContratoDefinitivo").val() + "'," + $("#cConsecutivoMod").val();
	var query="fn_mContratoModificadoPartidasModificadas(" + campos + ")";
	var cIdContratoDef=$("#cContratoDefinitivo").val();
	if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0 && 0==$("#isConvEjercicioAnt").val() ){
		query="fn_mContratoPluModificadoPartidasModificadas(" + campos + ")";
	}
	if(1==$("#isConvEjercicioAnt").val()){
		query="fn_mContratoModificadoEjercAntPartidasModificadas(" + campos + ")";
	}
	PartidasModificadasConvRed = $("#tblPartidasMod_ConvReduccion").dataTable({
		//sScrollY: "200px",
		sScrollX: "100%",
		//sScrollXInner: "200%",
		bScrollCollapse: true,
		bDestroy: true,
		bAutoWidth: true,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay registros",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+query,
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			
			{ sName: "nIdLineaConsolidado" },
			{ sName: "mMontoNetoLineaMaxOriginal" },
			{ sName: "nPocentajeIVA" },
			{ sName: "cIdCABM" },
			{ sName: "Descripcion" },
			{ sName: "nCantidadReduccion" },
			{ sName: "PrecioUnitario" },
			{ sName: "MontoBruto" },
			{ sName: "mMontoNetoReduccion" },
			{ sName: "PorcentajeModPart" },
			{ sName: "eliminarPartida" },
			{ sName: "cIdTipoConsolidado", bVisible: false },
			{ sName: "cIdUnidadEjecutora", bVisible: false },
			{ sName: "cIdConsecutivoConsolidado", bVisible: false },
			{ sName: "MontoNetoD", bVisible: false },
			{ sName: "nCantidadOriginal", bVisible: false },
			{ sName: "mMontoNetoLineaMaxOrig", bVisible: false }
		]
	});	
}
function mostrarReqsMods(idcabm, precio){
	var campos = "'" + idcabm + "'";
	var funcion="fn_mContratoModificadoPartidaRequisiciones";
	if("10"!=$("#cCentroContable").val()){
		campos = "'" + idcabm + "','"+$("#cIdUnidadEjecutora").val()+"'";
		funcion="fn_mContratoModificadoPartidaRequisicionesEst";
	}
	ReqsMods = $("#tblReqsMods").dataTable({
		//sScrollY: "200px",
		sScrollX: "100%",
		//sScrollXInner: "200%",
		bScrollCollapse: true,
		bDestroy: true,
		bRetrive : true,
		bAutoWidth: true,
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay registros",
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
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql="+funcion+" (" + campos + ")",
		bProcessing: true,
		sPaginationType: "full_numbers",
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "cIdSolicitud" },
			{ sName: "nIdLineaSolicitud" },
			{ sName: "cDescripcion" },
			{ sName: "cDescripcionAdicional" },
			{ sName: "nCantidad" },
			{ sName: "mPrecioUnitario" },
			{ sName: "MontoBruto" },
			{ sName: "MontoNeto" }
		]
	});
}
function setFieldsInit(){
	//asigna el valor de true a readonly para los campos que sirven de etiquetas
	document.getElementById("lblUnidadEjecutora").style.readonly = true;
	document.getElementById("lblProcedimiento").style.readonly = true;
	document.getElementById("lblDefinitivo").style.readonly = true;
	document.getElementById("lblContrato").style.readonly = true;
	document.getElementById("lblProveedor").style.readonly = true;
	document.getElementById("lblEstadoMod").style.readonly = true;
	document.getElementById("lblTotalContratoOriginal").style.readonly = true;
	document.getElementById("lblTotalContratoModificado").style.readonly = true;
	document.getElementById("lblTotalPorcentajeMod").style.readonly = true;
	document.getElementById("lblTotalAnterior").style.readonly = true;
	document.getElementById("lblTotalModificado").style.readonly = true;
	document.getElementById("lblTotal").style.readonly = true;
	//deshabilita fechas del contrato original
	
	document.getElementById("trffor").disabled=true;
	document.getElementById("trfini").disabled=true;
	document.getElementById("trffin").disabled=true;
	document.getElementById("trfentrega").disabled=true;
	document.getElementById("fechaFormalizacion").disabled=true;
	document.getElementById("fechaInicio").disabled=true;
	document.getElementById("fechaFin").disabled=true;
	document.getElementById("fechaEntrega").disabled=true;
	
	ocultar();
	
	if(1==$("#isConvEjercicioAnt").val()){
		setInitQueysContEjerAnt();
	}else{
		var cIdContratoDef=$("#cContratoDefinitivo").val();
		//Nunca se Cumplia la validacion de Plurianuales
		//if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())>=0){
		if(cIdContratoDef.indexOf("PLU")>=0&&cIdContratoDef.indexOf("/"+$("#cEjercicio").val())<=0){
			setInitQueysContPlu();
		}else{
			setInitQueys();
		}
	}
		
	mostrarXtipoMod();
	showHidePestanas();
	
	if(parseInt($("#nIdEstado").val(),10) == 1){
		$("#precompromisoContratoMod").hide();
	}
	
	if(parseInt($("#nIdEstado").val(),10) > 1){
		document.getElementById("tblReqsMods").disabled = true;
		$("#agrBtnContratoMod").css("display", "none");
		document.getElementById("bajaDiv").disabled = true;
		$("#devBtnContratoMod").css("display", "none");
		$("#grdBtnContratoMod").css("display", "none");
		document.getElementById("grdBtnContratoModUE").disabled = true;
		$("#imgEliminar").css("display", "none");
		$("#btnpartidaContMod").css("display", "none");
		$("#grdBtnContratoModDeleteAll").css("display", "none");
		$("#grdBtnContratoModRead").css("display", "none");
	}
	if(parseInt($("#nIdEstado").val(),10)==4){
		var cont =$("#cContratoDefinitivo").val();
		$("#cContratoDefinitivo").val($("#cContratoDefinitivo").val()+"#M"+$("#cConsecutivoMod").val());
		queryFormPost("mValidaCompAutSicop", {async: false});
		$("#cContratoDefinitivo").val(cont);
	}
	//queryFormPost("mContratoModicadoChecaRolUsuario", {async: false});
	if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 && roles.indexOf("ANALISTA")< 0 && roles.indexOf("Estatales")< 0 ) { 
		$("#usuarioCreacionOriginal").val('');
		queryFormPost("mContratoModicadoUsuarioCreacionOriginalRead",{async: false });
		if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
			document.getElementById("tblReqsMods").disabled = true;
			$("#agrBtnContratoMod").css("display", "none");
			document.getElementById("bajaDiv").disabled = true;
			$("#devBtnContratoMod").css("display", "none");
			$("#grdBtnContratoMod").css("display", "none");
		} 
	}
}
function showHidePestanas(){
	if ($("#tipoMod").val() == 0){
		$("#compromisoContratoMod").hide();
	}
	else {
		if ($("#tipoMod").val() == 2 || $("#tipoMod").val() == 3 || $("#tipoMod").val() == 4){//Para las fechas, Nuva partida y cambio de UE
			$("#compromisoContratoMod").hide();
		}else if($("#tipoMod").val() == 1){
			$("#precompromisoContratoMod").hide();
			$("#presupuestoContratoMod").show();
			$("#compromisoContratoMod").show();
		}else{
			$("#compromisoContratoMod").hide();
			document.getElementById("tblReqsMods").disabled = true;
			$("#agrBtnContratoMod").css("display", "none");
			$("#presupuestoContratoMod").hide();
			$("#precompromisoContratoMod").hide();
			$("#pagosContratoMod").hide();
		}
	}
}
function showAndHideTabs(){
	if(tabb==0 || tabb==1){
		$("#caratulaContratoMod").hide();
		$("#presupuestoContratoMod").hide();
		$("#precompromisoContratoMod").hide();
		$("#compromisoContratoMod").hide();
		$("#pagosContratoMod").hide();
		$("#imprimeConvenio").hide();
	}else{
//		//validar el tipo de modificación
		$("#caratulaContratoMod").show();
		$("#presupuestoContratoMod").show();
		$("#precompromisoContratoMod").show();
		$("#compromisoContratoMod").show();
		$("#pagosContratoMod").show();
		$("#imprimeConvenio").show();
	}
}
function mostrarXtipoMod(){
	switch(parseInt($("#tipoMod").val())){
		case 0:
			$("#trpartidas").show();
			$("#trrequiDispoMod").show();
			$("#trPartidasMod").show();
			$("#trPartidasMod_ConvReduccion").hide();
			break;
		case 1:
			mostrarPartidasModificadas_ConvReduccion();
			$("#trPartidasMod").hide();
			$("#trPartidasMod_ConvReduccion").show();
			$("#trpartidas").show();
			$("#bajaDiv").css("visibility", "");
			break;
		case 2:
			$("#trPartidasMod_ConvReduccion").hide();
			break;
		case 3:
			$("#trPartidasPresupCont").show();
			$("#trPartidasPresupContNuevas").show();
			if(parseInt($("#nIdEstado").val(),10)==1){
				$("#agregaPartPresupContMod").show();
			}
			mostrarPartidasPresupCont();
			partidasPresupAgregadas();
			$("#trPartidasMod_ConvReduccion").hide();
			break;
		case 4:
			$("#trpartidasUE").show();
			$("#trPartidasMod_ConvReduccion").hide();
			break;
		default:
			swal("Tipo de modificación desconocida.",{icon:"info",button: "Cerrar"});
			break;
	}
}