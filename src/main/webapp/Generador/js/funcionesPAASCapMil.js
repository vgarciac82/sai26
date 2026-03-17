var oTable;
var oTable2;
var oTable3;
var oTable4;
function resizeDt(){
	var tab = parseInt($("#tbs").val());
	switch (tab){
		case 0:
			if($('#tblSeleccionaCucop >tbody >tr').length>0){
				oTable.fnAdjustColumnSizing();
			}
			if($('#tblCucops >tbody >tr').length>0){
				oTable2.fnAdjustColumnSizing();
			}
			
		break;
		case 1:
			if($('#tblDisponible >tbody >tr').length>0){
				oTable4.fnAdjustColumnSizing();
			}
			if($('#tblcucopPeriodo >tbody >tr').length>0){
				oTable3.fnAdjustColumnSizing();
			}
			
		break;
		
	}
}
function consultaSelects(){
	$("#esperar").dialog("open");
	$("#opcion").val(2);
	var object=llenaObjectDat();
	$.ajax({url: "../../servlet/ProgramaAnualServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
			llenaCombo(j[0].catalogoUE,"cIdUE");
			llenaCombo(j[0].catalogoPartidaCapMil,"cIdPartidaCapMil");
			llenaCombo(j[0].catalogoCapitulos,"mCatalogoCapitulo");
			llenaCombo(j[0].catalogoPartida,"mCatalogoSubPartida");
			vaciarJsonAInputs(j[0].datosPrincipales);
			vaciarJsonAInputs(j[0].montosPorCapitulo);
			validaPrecarga();
			$("#mMontoC2").formatCurrency();
			$("#mMontoC3").formatCurrency();
			$("#mMontoC5").formatCurrency();
			$("#mMontoTotal").formatCurrency();
			$("#mMontoPartida").formatCurrency();
			$("#mTechoPresupuestal").formatCurrency();
			
			$("#esperar").dialog("close");
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$("#esperar").dialog("close");
		}
	});
}
function validaPrecarga(){
	if($("#tieneUe").val()==-1){
		swal("La unidad ejecutora no tiene un PAAS asignado favor de crearlo.",{icon:"warning",button: "Cerrar"});
		$("#pbGuardaUE").css("visibility","visible");
		$("#trCargaManual").show();
		$("#pbPrecarga").css("visibility","visible");
		
	}else{
		$("#trConsulta").css("visibility","visible");
		$("#trCucopsDisp").css("visibility","visible");
		$("#trCucopsSelec").css("visibility","visible");
	}
}
function llenaObjectDat(){
	var data0= {
		operacion:$("#operacion").val(),
		opcion:$("#opcion").val(),
		cUnidadEjecutora:$("#cIdUE").val(),
		cEjercicio:$("#cEjercicio").val(),
		cCentroContable:$("#centroContablePrecarga").val(),
		cPartidaCapMil:$("#cIdPartidaCapMil").val(),
		cCucopEliminar:$("#borraCIdCABM").val(),
		cIdPartidaEliminar:$("#borraIdSubPartida").val(),
		cIdPartidaCapMilEliminar:$("#borraIdSubPartidaCapMil").val() 
	};
	return data0;
}
function initTbls(){
	oTable2= $("#tblCucops").dataTable({
		bPaginate: true,
  			bLengthChange: true,
  			bFilter: true,
  			bSort: false,
  			bInfo: false,
		sScrollX: "100%",
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
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
		aaSorting: [[ 0, "asc" ]],
		aoColumns: [
			{ sName: "cIdCAMB1" ,sType: "string" },
			{ sName: "cIdSubPartidaCapMil",sType: "string"  },
			{ sName: "cIdSubPartida1",sType: "string"  },					
			{ sName: "cCABM1",sType: "string" 	},
			{ sName: "cUnidadMedida1" },
			{ sName: "nCantidad1" },
			{ sName: "mPrecioUnitario1"},
			{ sName: "nPorcentajeIVA1"},
			{ sName: "mImporteBruto1" },														
			{ sName: "mImporteNeto1" },
		    { sName: "boton1"},
			{ sName: "identificador", bVisible: false },
			{ sName: "cIdCABM", bVisible: false }
		]
	});
	oTable= $("#tblSeleccionaCucop").dataTable({
		bPaginate: true,
		bLengthChange: true,
		bFilter: true,
		bSort: false,
		bInfo: false,
		sScrollX: "100%",
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
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
		aaSorting: [[ 0, "asc" ]],
		aoColumns: [
			{ sName: "cIdCABM" },
			{ sName: "cCABM"},
			{ sName: "cIdSubPartida" },	
			{ sName: "csubpartida" },	
			{ sName: "cUnidadMedida" },
			{ sName: "ctipoproceso" }
		]
	});
}
function cambiaComboPartida(){
	querySelectPost("cambiaCombomCatalogoSubPartidaRead", "mCatalogoSubPartida", {async : false	});
}
function buscar(){
	var qw;
    var tmp;
	qw=" cEjercicio='"+$("#cEjercicio").val()+"' "; 
	if($("#cIdUE").val() != 0){
		qw += " and cIdUnidadEjecutora ='"+$("#cIdUE").val()+"'";
	}
	if($("#mCatalogoCapitulo").val() != 0){
		qw += " and cIdCapitulo='"+$("#mCatalogoCapitulo").val()+"'";
	}
	if($("#mCatalogoSubPartida").val() != 0){
		qw+=" and cIdSubpartida='"+$("#mCatalogoSubPartida").val()+"'";
	}
	tmp=qw;
    if($("#desCucopPa").val()!=""){
    	qw+=" and  cCABM LIKE '%25"+$("#desCucopPa").val()+"%25'";
	}
	if($("#cucopPa").val()!=""){
		qw += " and  cIdCABM LIKE '%25"+$("#cucopPa").val()+"%25'";
	}
     //se actualiza la tabla de cucops
	actualizatablaCucops(qw);
    qw1=" 1=1 "; 
	if($("#mCatalogoCapitulo").val() != 0){
		qw1+=" and cIdCapitulo='"+$("#mCatalogoCapitulo").val()+"'"; 
	}
	if($("#desCucopPa").val()!=""){
		qw1+=" and  cCABM LIKE '%25"+$("#desCucopPa").val()+"%25'";
		tmp+=" and  cCABM LIKE '%25"+$("#desCucopPa").val()+"%25'";
	}
	if($("#cucopPa").val()!=""){
		qw1 += " and  cIdCABM LIKE '%25"+$("#cucopPa").val()+"%25'";
	    tmp+=" and  cIdCABM LIKE '%25"+$("#cucopPa").val()+"%25'";
	}
	if($("#mCatalogoSubPartida").val() != 0){
		qw1+=" and cIdSubpartida='"+$("#mCatalogoSubPartida").val()+"'";
	    tmp+=" and cIdSubpartida='"+$("#mCatalogoSubPartida").val()+"'";
	}
	//para asegurar que no serepitan los cabms que ya estan en el programa anual
	qw1+= "and cidcabm not in(select  cidcabm  from vCucopsGridCapMil where "+tmp+")";
     consultaCucopsDisp(qw1);
}
function actualizatablaCucops(query){
	//se borra la tabla antes de realizar la consulta
	$('#tblCucops').dataTable().fnClearTable();
	//tabla cucop agregado
	oTable2= $("#tblCucops").dataTable({
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bPaginate" : true,
		"bAutoWidth" : true,
		"bScrollCollapse" : true,
		"sScrollX": "100%",
		"sPaginationType" : "full_numbers",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bServerSide": true,
		"fnInitComplete": function() {
			oTable2.fnAdjustColumnSizing();
		},
		"iDisplayLength": 10,
		oLanguage: {
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
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vCucopsGridBDCapMil&qw="+ query,
		bProcessing: true,
	    sPaginationType: "full_numbers",
		bJQueryUI: true,
		aaSorting: [[11, "asc" ]] ,
		aoColumns: [
			{ sName: "cIdCAMB1" ,sType: "string" },
			{ sName: "cIdSubPartidaCapMil",sType: "string"  },
			{ sName: "cIdSubPartida1",sType: "string"  },					
			{ sName: "cCABM1",sType: "string" 	},
			{ sName: "cUnidadMedida1" },
			{ sName: "nCantidad1" },
			{ sName: "mPrecioUnitario1"},
			{ sName: "nPorcentajeIVA1"},
			{ sName: "mImporteBruto1" },														
			{ sName: "mImporteNeto1" },
		    { sName: "boton1"},
			{ sName: "identificador", bVisible: false },
			{ sName: "cIdCABM", bVisible: false }
		],
		fnInitComplete: function() {
			$(".hola1").formatCurrency();
  			$(".hola2").formatCurrency();
            $(".hola3").formatCurrency();
            var aTrs = $('#tblCucops').dataTable().fnGetNodes(); 
          	if(aTrs.length >0 ){
				$("#pbEditarCucop").css("visibility","visible");
				$("#pbBorrarCucop").css("visibility","visible");
			}
			oTable2.fnAdjustColumnSizing();
		}
	});
}
function consultaCucopsDisp(qw1){
	oTable=$('#tblSeleccionaCucop').dataTable({
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
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_agregacucops&qw="+qw1,
		aaSorting: [[0, "asc" ]] ,
		aoColumns: [
			{ sName: "cIdCABM" },
			{ sName: "cCABM"},
			{ sName: "cIdSubPartida" },	
			{ sName: "csubpartida" },	
			{ sName: "cUnidadMedida" },
			{ sName: "ctipoproceso" }
		]
	});
}
function fnGetSelected( oTableLocal ){
	var aReturn = new Array();
	var aTrs = oTableLocal.fnGetNodes();
	for ( var i=0 ; i<aTrs.length ; i++ ){
		if ( $(aTrs[i]).hasClass('row_selected') ){
			aReturn.push( aTrs[i] );
		}
	}
	return aReturn;
}
function agregaUE(){
	$("#opcion").val(1);
	$("#centroContablePrecarga").val(centroContable);
	var object=llenaObjectDat();
	$.ajax({url: "../../servlet/ProgramaAnualServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				swal(j[0].MENSAJE,{icon:"info",button: "Cerrar"});
				if(j[0].RESPUESTA){
					$("#trCucopsDisp").css("visibility","visible");
					$("#trCucopsSelec").css("visibility","visible");
					
					//se pone formato tipo moneda
					$("#mMontoC2").formatCurrency();
					$("#mMontoC3").formatCurrency();
					$("#mMontoC5").formatCurrency();
					$("#mMontoTotal").formatCurrency();		
					//se pone visible formulario de consulta
					$("#trConsulta").css("visibility","visible");
					$("#tieneUe").val(1);
					$("#pbGuardaUE").css("visibility","hidden"); 
					$("#trCargaManual").hide();
					$("#pbPrecarga").css("visibility","hidden");
				}
				
				$("#esperar").dialog("close");
			}, error: function( jqXHR, textStatus, errorThrown ) {
				$("#esperar").dialog("close");
			}
	});
	
}
function actualizaMontosCapitulo(){
	$("#opcion").val(3);
	var object=llenaObjectDat();
	$.ajax({url: "../../servlet/ProgramaAnualServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				vaciarJsonAInputs(j[0].hayEpMonto);
				vaciarJsonAInputs(j[0].montosPorCapitulo);
				vaciarJsonAInputs(j[0].presupuestoPart);
				$("#mMontoC2").formatCurrency();
				$("#mMontoC3").formatCurrency();
				$("#mMontoC5").formatCurrency();
				$("#mMontoTotal").formatCurrency();
				$("#mMontoPartida").formatCurrency();
				$("#mTechoPresupuestal").formatCurrency();
				actualizatablaCucops(" cIdCapitulo=-1");
				consultaCucopsDisp(" cIdCapitulo=-1");
				$("#esperar").dialog("close");
			}, error: function( jqXHR, textStatus, errorThrown ) {
				$("#esperar").dialog("close");
			}
	});
}
function eliminaCucop(indice){
	$("#borraCIdCABM").val($("#cIdCABM_"+indice+"").val());
	$("#borraIdSubPartida").val($("#cIdSubPartida_"+indice+"").val());
	$("#borraIdSubPartidaCapMil").val($("#cIdSubPartidaCapMil_"+indice+"").val());
	$("#opcion").val(4);
	var object=llenaObjectDat();
	swal({
		title: "Est\u00e1 seguro de borrar el cucop "+$("#borraCIdCABM").val()+"?",
		text: "Est\u00e1 acci\u00f3n no se puede revertir!",
		icon: "info",
		buttons: {
			confirm : "Aceptar",
			cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
		}else{
			$.ajax({
				url: '../../servlet/ProgramaAnualServlet',
				dataType: 'json',
				data:object,
				async : false,
				type: 'POST',
				success : function(j) {
					if(j[0].RESPUESTA){
						vaciarJsonAInputs(j[0].hayEpMonto);
						vaciarJsonAInputs(j[0].montosPorCapitulo);
						vaciarJsonAInputs(j[0].presupuestoPart);
						$("#mMontoC2").formatCurrency();
						$("#mMontoC3").formatCurrency();
						$("#mMontoC5").formatCurrency();
						$("#mMontoTotal").formatCurrency();
						$("#mMontoPartida").formatCurrency();
						$("#mTechoPresupuestal").formatCurrency();
						buscar();
					}
					swal(j[0].MENSAJE,{icon:"info",button: "Cerrar"});
				}
			});
		}
	});
}
//Funciones para la pestaña calendario
function initTabl(){
		oTable3=  $("#tblcucopPeriodo").dataTable({
		bPaginate: false,
			bLengthChange: false,
			bFilter: true,
			bSort: false,
			bInfo: false,
		sScrollX: "100%",
		bJQueryUI: true,
		bRetrive : true,
		bDestroy : true,
		sPaginationType: "full_numbers",
		oLanguage: {
			sProcessing: "Procesando...",
			sLengthMenu: "Mostrar _MENU_ registros",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay Datos",
			sLoadingRecords: "Cargando...",
			sInfo: "Registros _START_ al _END_ de _TOTAL_",
			sInfoEmpty: "Registro 0 al 0 de 0",
			sInfoFiltered: "(filtado de _MAX_ registros)",
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
		aaSorting: [[ 1, "desc" ]] ,
		aoColumns: [
			{ sName: "cperiodo",bSortable: false },
			{ sName: "cantDisp",bSortable: false   },
			{ sName: "ncantidad",bSortable: false   },					
			{ sName: "mpreciounitario",bSortable: false	},
			{ sName: "mimportebruto",bSortable: false },
			{ sName: "mimporteneto",bSortable: false },
			{ sName: "ncantidadensolicitudes",bSortable: false },
			{ sName: "ncantidaddisponibilidad", bVisible: false,bSortable: false },
			{ sName: "mMontoNetoEnSolicitudes",bSortable: false },
			{ sName: "mmontodisponibilidad",bSortable: false },
			{ sName: "nIdperiodo", bVisible: false,bSortable: false }
		]
	});
}
function iniTablRemanente(){
	oTable4= $("#tblDisponible").dataTable({
		"bLengthChange" : true,
		"bFilter" : true,
		"bSort" : true,
		"bInfo" : true,
		"bPaginate" : true,
		"bAutoWidth" : true,
		"bScrollCollapse" : true,
		"sScrollX": "100%",
		"sPaginationType" : "full_numbers",
		"bJQueryUI" : true,
		"bRetrive" : true,
		"bDestroy" : true,
		"bServerSide": true,
		"fnInitComplete": function() {
			oTable4.fnAdjustColumnSizing();
		},
		"iDisplayLength": 10,
		oLanguage: {
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
				sFirst:    "Primero",
				sPrevious: "Ant.",
				sNext:     "Sigte.",
				sLast:     "&Uacute;ltimo"
			}
		},
		bServerSide: true,
		sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_SaldosRemanentesPAAAS&qw="+" UE= '"+$("#ue_usuarioEdita").val()+"' AND Partida= '"+$("#editaCIdSubPartidaCapMil").val()+"'",
		bJQueryUI: true,
		aaSorting: [[ 0, "asc" ]] ,
		aoColumns: [
			{ sName: "UE",bSortable: false , sClass : "centerCls" },
			{ sName: "Partida",bSortable: false, sClass : "centerCls"   },
			{ sName: "MontoAnual",bSortable: false , sClass : "centerCls"  },					
			{ sName: "MontoEnero",bSortable: false	, sClass : "centerCls"},
			{ sName: "MontoFebrero",bSortable: false , sClass : "centerCls"},
			{ sName: "MontoMarzo",bSortable: false , sClass : "centerCls"},
			{ sName: "MontoAbril",bSortable: false , sClass : "centerCls"},
			{ sName: "MontoMayo", bSortable: false, sClass : "centerCls"},
			{ sName: "MontoJunio",bSortable: false , sClass : "centerCls"},
			{ sName: "MontoJulio",bSortable: false , sClass : "centerCls"},
			{ sName: "MontoAgosto",bSortable: false, sClass : "centerCls" },
			{ sName: "MontoSeptiembre",bSortable: false , sClass : "centerCls"},
			{ sName: "MontoOctubre",bSortable: false, sClass : "centerCls" },
			{ sName: "MontoNoviembre",bSortable: false, sClass : "centerCls" },
			{ sName: "MontoDiciembre",bSortable: false , sClass : "centerCls"}]
	}); 
}
function consultaDatosCalendario(){
	$("#opcion").val(5);
	var object=llenaObjectDatCalendario();
	$.ajax({url: "../../servlet/ProgramaAnualServlet" , type:'post' , async: false
		,data:object
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catalogoIVA,"ivaEdita");
				llenaCombo(j[0].catalogoTipoAdj,"tipoAdjudicacion");
				llenaCombo(j[0].catalogoProcedencia,"tipoProcedimientoEdita");
				vaciarJsonAInputs(j[0].datosPrincipales);
				vaciarJsonAInputs(j[0].datosguardadosCalendario);
				vaciarJsonAInputs(j[0].datosguardadosTotal);
				vaciarJsonAInputs(j[0].datosOtros);
				$("#totalEdita").formatCurrency();
				datosTabla();
				$("#esperar").dialog("close");
			}, error: function( jqXHR, textStatus, errorThrown ) {
				$("#esperar").dialog("close");
			}
	});
}
function llenaObjectDatCalendario(){
	var data0= {
		operacion:$("#operacion").val(),
		opcion:$("#opcion").val(),
		cUnidadEjecutora:$("#ue_usuarioEdita").val(),
		cEjercicio:$("#cEjercicio").val(),
		cCentroContable:$("#centroContablePrecarga").val(),
		cPartidaCapMil:$("#partidaEditaCapMil").val(),
		cCucopEliminar:$("#cucopEdita").val(),
		cIdPartidaEliminar:$("#partidaEdita").val(),
		cIdPartidaCapMilEliminar:$("#partidaEditaCapMil").val() 
	};
	return data0;
}
function onlyNumbers2(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	var strCheck = '0123456789.';
	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1)
		return false; // Valida que sea numero y punto decimal
	return true;
}
function onlyInteger(evt) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode
	var strCheck = '0123456789';
	var key = String.fromCharCode( keyPressed );
	if (strCheck.indexOf( key ) == -1)
		return false; // Valida que sea numero y punto decimal
	return true;
}
function datosPlurianualidad(){
	if ($('#chk_plurianualidad').is(':checked')) {
		$("#plurianualidad").css("visibility","visible");
		$("#plurianualidadv").css("visibility","visible");
		$("#EP").css("visibility","visible");
		$("#ME").css("visibility","visible");
		$("#anos").css("visibility","visible");
		
		//queryFormPost("obtienePluri", {async: false });
		$("#plurianualidad").val($("#valorplurianualidad").val());
		$("#plurianualidadv").val($("#valorplurianualidadv").val());
		$('#chk_plurianualidad').val(1);
    } else {
        $("#plurianualidad").css("visibility","hidden");
        $("#plurianualidadv").css("visibility","hidden");
		$("#EP").css("visibility","hidden");
        $("#ME").css("visibility","hidden");
		$("#anos").css("visibility","hidden");
        $('#chk_plurianualidad').val(0);
    }
}
//seguir codificando
function guardaCalendario(){
	if(validacionesGuardar()){
		$("#esperar").dialog("open");
		var cadTabla=createArrayTabla("tblcucopPeriodo");
		$("#opcion").val(6);
		if(cadTabla==null ||cadTabla==""){
			$("#esperar").dialog("close");
			return;
		}
		$.ajax({
			url : "../../servlet/ProgramaAnualServlet",
			type : 'post',
			async : false,
			data : 'cadTabla=' +cadTabla+'&operacion='+$("#operacion").val()+'&opcion='+$("#opcion").val()
			+'&cUnidadEjecutora='+$("#ue_usuarioEdita").val()+'&cCentroContable='+$("#centroContablePrecarga").val()+'&cPartidaCapMil='+$("#partidaEditaCapMil").val()
			+'&cCucopEliminar='+$("#cucopEdita").val()+'&cIdPartidaEliminar='+$("#partidaEdita").val()+'&cIdPartidaCapMilEliminar='+$("#partidaEditaCapMil").val()
			+'&ivaEdita='+$("#ivaEdita").val()+'&pymeEdita='+$("#pymeEdita").val()+'&porcentajeEdita='+$("#porcentajeEdita").val()
			+'&tipoProcedimientoEdita='+$("#tipoProcedimientoEdita").val()+'&isPluri='+$("#isPluri").val()+'&nEjerciciosPluris='+$("#plurianualidad").val()
			+'&montoBrutoPluri='+$("#plurianualidadv").val()+'&tipoAdjudicacion='+$("#tipoAdjudicacion").val()+'&cEjercicio='+$("#cEjercicio").val()
			,
			dataType : 'json',
			success : function(j) {
				$('#ivaEdita').empty();
				$('#tipoAdjudicacion').empty();
				$('#tipoProcedimientoEdita').empty();
				swal(j[0].MENSAJE,{icon:"info",button: "Cerrar"});
				llenaCombo(j[0].catalogoIVA,"ivaEdita");
				llenaCombo(j[0].catalogoTipoAdj,"tipoAdjudicacion");
				llenaCombo(j[0].catalogoProcedencia,"tipoProcedimientoEdita");
				vaciarJsonAInputs(j[0].datosPrincipales);
				vaciarJsonAInputs(j[0].datosguardadosCalendario);
				vaciarJsonAInputs(j[0].datosguardadosTotal);
				vaciarJsonAInputs(j[0].datosOtros);
				$("#totalEdita").formatCurrency();
				datosTabla();
				$("#esperar").dialog("close");
			}, error: function() {
				$("#esperar").dialog("close");
			}
		});
	}
}
function validacionesGuardar(){
	var resp=true;
	var msg="";
	var token="";
	$("#isPluri").val(0);
	if ($('#chk_plurianualidad').is(':checked')) {
		$("#isPluri").val(1);
		if(parseFloat($("#plurianualidad").val())<=0){
			msg="El campo Ejercicio de plurianualidad debe ser mayor a cero.";
			token="\n";
		}
		if( parseInt($("#plurianualidadv").val(),10)<= 0){
			msg=msg+token+"El monto sin IVA a ejercer debe ser mayor a cero.";
			token="\n";
		}
	}
	
	if(msg!=""){
		swal(msg,{icon:"info",button: "Cerrar"});
		resp=false;
	}
	return resp;
}
function createArrayTabla(id){
	var arregloTmp=new Array();
	var arrayFila=new Object();
	
	var aTrs = $('#'+id).dataTable().fnGetNodes();
	var nTr;
	var jqInputs;
	for ( var i=0 ; i<aTrs.length; i++ ){
		nTr =  $('#'+id).dataTable().fnGetData(aTrs[i]);
		jqInputs = $('input',aTrs[i] );
		var nCantidadCap=0,precioUCap=0.0,mimporteBruto=0.0,mimporteNeto=0.0;
		var k=i;
		
		nCantidadCap=jqInputs[2].value;
		precioUCap=jqInputs[3].value;
		mimporteBruto=jqInputs[4].value;
		mimporteNeto=jqInputs[5].value;
		precioUCap = quitaFmt(precioUCap);
		mimporteBruto=quitaFmt(mimporteBruto);
		mimporteNeto=quitaFmt(mimporteNeto);
		if(parseInt(nCantidadCap,10)>0 && parseFloat(precioUCap)<=parseFloat(0)){
			swal("El precio no puede ser 0.00 en el mes "+(k+1),{icon:"info",button: "Cerrar"});
			arregloTmp="";
			return;
		}
		arrayFila=[k+1,nCantidadCap,precioUCap,mimporteBruto,mimporteNeto,"|"];
		arregloTmp.push(arrayFila);
	}
	return arregloTmp;
}
function quitaFmt( val ) {
   	val = val.replace("$", "");
   	val = val.replace(/,/g, "");

   	if ( val.indexOf( "(" ) >= 0 ) {
		val = val.replace("(", "");
		val = val.replace(")", "");
		val = "-" + val;
   	}
   	return val
}
function borraDatos(indice){
	var br=$("#mpreciounitario_"+indice+"").val();
	br=br.replace("$","");
	br=br.replace(",","");
	$("#mpreciounitario_"+indice+"").val(br);
}
function cambiafrmt(fld){
	var formato=$("#" + fld.id).val();
	formato=parseFloat(formato).toFixed(2);
	$("#" + fld.id).val(formato);			
}
function addValue(){
	$("#cIdPartidaCapMil").val($("#mCatalogoSubPartida").val());
}