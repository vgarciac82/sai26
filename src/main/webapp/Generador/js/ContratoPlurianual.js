	
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
	
	};
	
	function setFechas(){
		$("#fInicio").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true, 
			changeMonth: true,
			onSelect: function(dateText, inst) {
		 		llenaFechaFin();
		 	}	
		});
			
		$( "#fFin" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true, 
			changeMonth: true,
			onSelect: function(dateText, inst) {
		 		validarFechas();
		 	}     
		});
	}
	
	function aplicarContratoPlurianual(valor){
		
		$("#cDocumentoHaplicado").val(valor);
		
		if(valor == "S"){
			queryFormPost("folioMASCPContratoPlurianualUpdate", {async:false});
		}
		
		queryFormPost("aplicarRechazarContratoPlurianualUpdate", {async:false});
	}
	
	function cmdAvanzar(){
		var bRegresa = true;
		
		var mMinimo = Number(quitaFmt($("#mMontoMinimo").val()));
		var mMaximo = Number(quitaFmt($("#mMontoMaximo").val()));		
		var montoContrato = Number( quitaFmt($("#mTotalContrato").val()) );
					
		mMinimo = mMinimo.toFixed(2);
		mMaximo = mMaximo.toFixed(2);		
		montoContrato = montoContrato.toFixed(2);
		
		$("#cDescripcionProyecto").val($("#cDescProyecto").val());
		$("#cJustifSolicitud").val($("#cJustificaSolicitud").val());
		$("#cFundamentoMotivacion").val($("#cFundamentoMotiv").val());
		$("#cEspecificacion").val($("#cEspecif").val());
		$("#cJustificacionEconomica").val($("#cJustifEconomica").val());
		$("#cJustificacionPlazo").val($("#cJustifPlazo").val());
		
		bRegresa = validaCampos();
		if(Number(montoContrato) == 0){
			Swal.fire("Error","El Monto Total por Contrato no puede ser Cero.","error");
			bRegresa = false;
			return;
		}
		
		if($("#rdoAbierto").prop("checked")){
			if(Number(mMinimo) == 0){
				Swal.fire("Revise","El Monto Minimo no puede ser Cero.","error");
				bRegresa = false;
				return;
			}else if(Number(mMaximo) == 0){
				Swal.fire("Revise","El Monto Maximo no puede ser Cero.","error");
				bRegresa = false;
				return;
			}else if (Number(mMinimo) > Number(mMaximo)){
				Swal.fire("Verifique!!","El Monto Minimo no puede ser mayor al Monto Maximo.","error");
				bRegresa = false;
				return;
			}else if (Number(mMinimo) > Number(montoContrato)){
				Swal.fire("Verifique!!","El Monto Minimo no puede ser mayor al Monto Total del Contrato.","error");
				bRegresa = false;
				return;
			}
		}
			
		if($("#rdoCerrado").prop("checked")){
			$("#mMontoMinimo").val( quitaFmt($("#mTotalContrato").val()));
			$("#mMontoMaximo").val(quitaFmt($("#mTotalContrato").val()));
		}
		
		if(bRegresa){
			llenaDatos();
			guardaEncabezado();
			
		}
		
		$("#montoTotal").val(quitaFmt($("#mTotalContrato").val()));
		//Valida los totales por si es un rechazo y ya se hubieran capturado los importes de las ep y claves
		validaTotales();
		
	}
	
	function validaCampos() {
		var bRegresa = true;
		
		if(isEmpty("cDescripcionProyecto")){
			Swal.fire("Verifique!", "La Descripcion del Proyecto no debe quedar vacia. ","info");
			bRegresa = false;
			return;
		}else if(isEmpty("cJustifSolicitud")){
			Swal.fire("Verifique!","La Justificacion de la Solicitud no debe quedar vacia.", "info");
			bRegresa = false;
			return;
		}else if($("#fInicio").val() == ""){
			Swal.fire("Capture","Favor de capturar la Fecha Inicio.","info");
			bRegresa = false;
			return;
		}else if($("#fFin").val() == ""){
			Swal.fire("Capture","Favor de capturar la Fecha Fin.","info");
			bRegresa = false;
			return;
		}else if(!$("#rdoAbierto").prop("checked") && !$("#rdoCerrado").prop("checked")){
			Swal.fire("Capture","Favor de seleccionar el Tipo de Contrato.","info");
			bRegresa = false;
			return;
		}else if($("#cboSolicitud").val() == "0"){
			Swal.fire("Capture","Favor de seleccionar una Solicitud.","info");
			bRegresa = false;
			return;
		}else if($("#cboEspecificacion").val() == "0"){
			Swal.fire("Capture","Favor de seleccionar una Especificación.","info");
			bRegresa = false;
			return;
		}else if($("#cboTipoMoneda").val() == "0"){
			Swal.fire("Capture","Favor de seleccionar un Tipo de Moneda.","info");
			bRegresa = false;
			return;		
		}else if(!validaMontoTotalContrato()){
			bRegresa = false;
			return;
		
		}else if(isEmpty("cEspecificacion")){
			Swal.fire("Verifique!","Especificación no debe quedar vacio. ","info");
			bRegresa = false;
			return;
		}else if(isEmpty("cJustificacionEconomica")){
			Swal.fire("Verifique!","Justificación Ventajas Economicas no debe quedar vacio.","info");
			bRegresa = false;
			return;
		}else if(isEmpty("cJustificacionPlazo")){
			Swal.fire("Verifique!","Justificación del Plazo no debe quedar vacio.","info");
			bRegresa = false;
			return;
		}
		
		
		var descripcion = $("#cDescProyecto").val()
		if(descripcion.length > 4000){
			Swal.fire("Reduzca el campo por favor!","La Descripcion del Proyecto no ser mayor a 4000 caracteres.","info");
			bRegresa = false;
			return;
		}
		
		var justPlazo = $("#cJustificacionPlazo").val()
		if(justPlazo.length > 8000){
			Swal.fire("Reduzca el campo por favor!","La Justificación del plazo no ser mayor a 8000 caracteres.", "info");
			bRegresa = false;
			return;
		}
		
		var justEcon = $("#cJustificacionEconomica").val()
		if(justEcon.length > 8000){
			Swal.fire("Reduzca el campo por favor!","La Justificación económica no ser mayor a 8000 caracteres.","info");
			bRegresa = false;
			return;
		}
		
		var especificacion = $("#cEspecif").val()
		if(especificacion.length > 8000){
			Swal.fire("Reduzca el campo por favor!","La Especificación no ser mayor a 8000 caracteres.","info");
			bRegresa = false;
			return;
		}
		
		var fundamento = $("#cFundamentoMotiv").val()
		if(fundamento.length > 8000){
			Swal.fire("Reduzca el campo por favor!","El fundamento y motivación no ser mayor a 8000 caracteres.","info");
			bRegresa = false;
			return;
		}
		
		return bRegresa
	
	}
	
	function guardaEncabezado() {
			
			queryFormPost ("existeFolioContratoPlurianual_Read", {async: false});
			
			if ($("#existeFolio").val() == 0 ) {
					queryFormPost({ queryName : "tContratoPlurianualEncabezadoCreate",
							async : false})
			} else {
					let correcto = updateFolioContratoPLU();
					if(correcto) {	
						var fInicio = $("#fInicio").val();  
						var fFin = $("#fFin").val();
						var iYearInicio = parseInt(fInicio.substr(6,4), 10);
						var iYearFin = parseInt(fFin.substr(6,4), 10);
						cargarEjercicioFiscal(iYearInicio, iYearFin);
						cargarEjercicioMontos(iYearInicio, iYearFin);
						$("#btnImprimirRpt").show();
						$("#btnExportarSol").show();
						$("#btnFirmantes").show();
						
					} else {
						throw "Ocurrio un error al guardar los campos. Favor de consultarlo con el administrador."; 
					}
			} 
			
			$(".pasoDos").show();
			bloqueaBotones();
			
			Swal.fire("Ok","Se guardo el encabezado del Contrato Plurianual","success");
			$("#tabImporte").click();
		
	}
	
	function bloqueaBotones() {
		
			document.getElementById("cDescProyecto").readOnly = true;
			document.getElementById("cJustificaSolicitud").readOnly = true;
			document.getElementById("folioMASCP").readOnly = true;
			document.getElementById("oficioDG").readOnly = true;
			document.getElementById("fInicio").readOnly = true;
			document.getElementById("fFin").readOnly = true;
			document.getElementById("cboSolicitud").readOnly = true;
			document.getElementById("cboEspecificacion").readOnly = true;
			document.getElementById("cboTipoMoneda").readOnly = true;
			document.getElementById("mMontoMinimo").readOnly = true;
			document.getElementById("mMontoMaximo").readOnly = true;
			document.getElementById("cFundamentoMotiv").readOnly = true;
			document.getElementById("cEspecif").readOnly = true;
			document.getElementById("cJustifEconomica").readOnly = true;
			document.getElementById("cJustifPlazo").readOnly = true;
	}
	
	function cmdGuardar(){
		
		var bRegresa = true;	
		
		 if(!validarCapturaImportesEjer()){
			bRegresa = false;
			return;
		}
			
		var dtClaves = $("#dt_Claves").dataTable();
	    var iRow = $(dtClaves.fnGetNodes()).length;	
	    
	    if(iRow == 0){
	    	Swal.fire("Verifique!!","Debe Capturar al menos una Clave Presupuestal.","info");
			bRegresa = false;
			return;
	    }	
		
		if(bRegresa){
			llenaDatos();
		}
		
		$(".cardBtn").show();
		$("#btnImprimirRpt").show();
		
		return bRegresa;
		
	}
	
	function updateFolioContratoPLU(){
		let correcto = false;
		
			$("#mMontoMinUpdate").val(quitaFmt($("#mMontoMinimo").val()));
			$("#mMontoMaxUpdate").val(quitaFmt($("#mMontoMaximo").val()));	
			$("#mTotalUpdate").val(quitaFmt($("#mTotalContrato").val()));
					
			$("#cDescripcionProyecto").val($("#cDescProyecto").val());
			$("#cJustifSolicitud").val($("#cJustificaSolicitud").val());
			$("#cFundamentoMotivacion").val($("#cFundamentoMotiv").val());
			$("#cEspecificacion").val($("#cEspecif").val());
			$("#cJustificacionEconomica").val($("#cJustifEconomica").val());
			$("#cJustificacionPlazo").val($("#cJustifPlazo").val());
			
			$("#fIniUpdate").val(quitaFmt($("#fInicio").val()));	
			$("#fFinUpdate").val(quitaFmt($("#fFin").val()));
			
			queryFormPost({
							queryName : "folioEncContratoPLU_Update",
							async : false,
							callback:function(){
								correcto=true;
							}
						});
		
		return correcto;
		
	}
	
	function llenaDatos(){
	
		$("#nSolicitud").val($("#cboSolicitud").val());
		$("#nEspecificacion").val($("#cboEspecificacion").val());
		
		if($("#rdoAbierto").prop("checked")){
			$("#nTipoContrato").val("1");	
		}else if($("#rdoCerrado").prop("checked")){
			$("#nTipoContrato").val("2");
		}
	}	
	
	function isEmpty(idCampo){
        var val = $("#"+idCampo).val();
        val = val.replace(/\s/g, "" );
        
        if( val == "" )
               return true;
        else 
               return false;
    }
    
    function revisaValidacion() {
		if($("#rdoValida").prop("checked"))
			valida=true;
		else 
			valida=false;
	}
    
    function revisaRechazo() {
		if($("#rdoAutoriza").prop("checked")){
			rechazo=false;
			//marcar como aplicado el folio.
		}else{ 
			rechazo=true;
			capturaRechazo();
		}
	}
	
	function sumaMeses( fld ){
		var importeCap = "0";
		importeCap = parseFloat( quitaFmt($("#enero").val())) + parseFloat( quitaFmt($("#febrero").val())) +parseFloat( quitaFmt($("#marzo").val()))+parseFloat( quitaFmt($("#abril").val()))
		+parseFloat( quitaFmt($("#mayo").val())) +parseFloat(quitaFmt( $("#junio").val()))+parseFloat( quitaFmt($("#julio").val()))+parseFloat(quitaFmt( $("#agosto").val()))
		+parseFloat( quitaFmt($("#septiembre").val()))+parseFloat(quitaFmt( $("#octubre").val()))+ parseFloat( quitaFmt($("#noviembre").val()))+parseFloat( quitaFmt($("#diciembre").val()));
	   //importeCap = parseFloat(importeCap) + parseFloat( $("#" + fld.id).val());
	   $("#importeCapturado").val(addCommas(importeCap));
	   
	   
	}
	
	function creaDlgMotivoRechazo() { 
      	$("#capturaRechazo_DIV").dialog({
	        title:"Motivo de Rechazo",
	        autoOpen : false,
	        height : 300,
	        width : 490,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
                       insertMotivoRechazo(); 
                       parent.document.getElementById("pb_save").click();
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                	
                }
         	}
       });
 
	}
	
	function aceptarImporte() {
		if(validaMontoCapturado()){
			agregarClave();
			cargarClaves();
        	modalPartidas.hide();
		}  
	}
	/*
	function creaDlgImportes() { 
		
      	$("#capturaImportes").dialog({
	        title:"Importes Mes Clave Presupuestal ",
	        autoOpen : false,
	        height : 450,
	        width : 540,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
            		if(validaMontoCapturado()){
            			agregarClave();
            			cargarClaves();
                    	$(this).dialog("close");
            		}                     
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                }
         	},
         	open : function() {
				iniciaDivImportes();
			}
       });
       
         
	}
	*/
	
	function iniciaDivImportes(){
		
		$("#clavePresup").val($("#clavePresupuestal").val());
		document.getElementById("ejercicioFiscal").disabled = true;
		$("#importeEjerEP").focus();
		$("#ejercicioFiscal").val("0");
		$("#ejercicioFiscal").val($("#cboEjerFiscal").val());
		$("#importeCapturado").val("0");
		$("#enero").val("0");
		$("#febrero").val("0");
		$("#marzo").val("0");
		$("#abril").val("0");
		$("#mayo").val("0");
		$("#junio").val("0");
		$("#julio").val("0");
		$("#agosto").val("0");
		$("#septiembre").val("0");
		$("#octubre").val("0");
		$("#noviembre").val("0");
		$("#diciembre").val("0");

	}
	

	function agregarImportes(){
		
		importeCap = 0;
		$("#aEjercicioMonto").val($("#cboEjerFiscal").val());
		$("#ejercicioFiscal").val($("#cboEjerFiscal").val());
		queryFormPost("consultaMontoEjercicioPlurianual", {asyn:false});
		queryFormPost("consultaMontoAcumulado", {async:false})
				
		iniciaDivImportes();
		modalPartidas.show();
		
		//Valida los totales por si es un rechazo y ya se hubieran capturado los importes de las ep y claves
		queryFormPost("consultaTotalesContrato_read", {async: false})
		var mTotalEP = Number($("#mTotalEP").val());	
		var montoContrato = Number( quitaFmt($("#mTotalContrato").val()) );
		montoContrato = montoContrato.toFixed(2);
		
		if(montoContrato == mTotalEP) {
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
		}
	}
	
	function validaMontoCapturado(){		
		var importeAcum = 0;
		var bRegresa = true;
		//folio = $("#FOLIO").val();
		queryFormPost("consultaMontoAcumulado", {async:false});
		$("#aEjercicioMonto").val($("#cboEjerFiscal").val());				

		if ($("#importeAcumulado").val() > 0) {
			importeAcum = $("#importeAcumulado").val();
		}
		
		var mEjercicio = Number(quitaFmt($("#importeEjerEP").val())).toFixed(2);
		
		var mes1 = $("#enero").val(); 	mes1 = parseFloat(Math.round( quitaFmt(mes1)*100));
		var mes2 = $("#febrero").val(); mes2 = parseFloat(Math.round( quitaFmt(mes2)*100) );
		var mes3 = $("#marzo").val(); 	mes3 = parseFloat(Math.round( quitaFmt(mes3)*100));
		var mes4 = $("#abril").val(); 	mes4 = parseFloat(Math.round( quitaFmt(mes4)*100) );
		var	mes5 = $("#mayo").val(); 	mes5 = parseFloat(Math.round( quitaFmt(mes5)*100) );
		var mes6 = $("#junio").val(); 	mes6 = parseFloat(Math.round( quitaFmt(mes6)*100) );
		var mes7 = $("#julio").val(); 	mes7 = parseFloat(Math.round( quitaFmt(mes7)*100) );
		var mes8 = $("#agosto").val(); 	mes8 = parseFloat(Math.round( quitaFmt(mes8)*100) ); 
		var	mes9 = $("#septiembre").val(); 	mes9 = 	parseFloat(Math.round( quitaFmt(mes9)*100) );
		var mes10 = $("#octubre").val(); 	mes10 = parseFloat(Math.round( quitaFmt(mes10)*100));
		var mes11 = $("#noviembre").val(); 	mes11 = parseFloat(Math.round( quitaFmt(mes11)*100) );
		var mes12 = $("#diciembre").val(); 	mes12 = parseFloat(Math.round( quitaFmt(mes12)*100) );
		
		
		var impteCapturado = 0.00;		
		impteCapturado =(mes1 + mes2 + mes3 + mes4 + mes5 + mes6 + mes7 + mes8 +mes9 + mes10 + mes11 + mes12 ) / 100;
		
		$("#importeCapturado").val(impteCapturado);	
		cambiafrmt("importeCapturado");
		
		importeAcum = Number(importeAcum) + Number(impteCapturado);
		importeAcum = Number(importeAcum).toFixed(2);
		
		if( impteCapturado > Number (mEjercicio)){
			Swal.fire("Verifique!!","La suma del importe capturado no puede revasar el importe del Ejercicio: " + $("#ejercicioFiscal").val() + ". ","info");
			bRegresa = false;			
		}			
		//Valida que no se pase del importe total del contrato
		if(importeAcum > totalContrato){
			Swal.fire("Verifique!!","La suma de los importes que se han capturado no pueden ser mayor al importe total del Contrato.","info");
			bRegresa = false;			
		}	else if (importeAcum == totalContrato) {
				parent.document.getElementById("pb_save").disabled=false;
		}						
		
		return bRegresa;
	}
	var renglonClave = 0;
	
	function agregarClave(){
		
		var impteClaves = $("#mImporteClaves").val();
		impteClaves = quitaFmt( impteClaves );
		var clave = $("#clavePresupuestal").val();		
		var EP = $("#EP").val();
		var impteTotal = quitaFmt( $("#importeCapturado").val());
		var ejerFiscalClave = $("#ejercicioFiscal").val();
		var mes1 = $("#enero").val(); mes1 = quitaFmt(mes1);
		var mes2 = $("#febrero").val(); mes2 = quitaFmt(mes2); 
		var mes3 = $("#marzo").val(); mes3 = quitaFmt(mes3); 
		var mes4 = $("#abril").val(); mes4 = quitaFmt(mes4); 
		var	mes5 = $("#mayo").val(); mes5 = quitaFmt(mes5); 
		var mes6 = $("#junio").val(); mes6 = quitaFmt(mes6); 
		var mes7 = $("#julio").val(); mes7 = quitaFmt(mes7); 
		var mes8 = $("#agosto").val(); mes8 = quitaFmt(mes8); 
		var	mes9 = $("#septiembre").val(); mes9 = quitaFmt(mes9); 
		var mes10 = $("#octubre").val(); mes10 = quitaFmt(mes10); 
		var mes11 = $("#noviembre").val(); mes11 = quitaFmt(mes11);
		var mes12 = $("#diciembre").val(); mes12 = quitaFmt(mes12);
	
		++renglonClave;
		$("#renglon").val(renglonClave);		
		
		cargaDatosDetalle( mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8, mes9, mes10, mes11, mes12, impteTotal);
	    
	    queryFormPost("creaClavesContratoPluri", {async:false});

		$("#clavePresupuestal").val("");
		
	}
	
	function validaTotales() {
		
		//Valida los totales por si es un rechazo y ya se hubieran capturado los importes de las ep y claves
		queryFormPost("consultaTotalesContrato_read", {async: false});
		var mTotalA = Number($("#mTotalAnio").val());	
		var mTotalEP = Number($("#mTotalEP").val());
		
		var montoContrato = Number( quitaFmt($("#mTotalContrato").val()) );
		montoContrato = montoContrato.toFixed(2);
		
		if(montoContrato == mTotalEP && montoContrato == mTotalA) {
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
		}
	}
		
	function capturaRechazo(){
		$("#capturaRechazo_DIV").dialog("open");		
	}
	
	function insertMotivoRechazo(){	
		if($("#motivoRechazo").val()==""){
			Swal.fire("Capture","Favor de capturar el motivo del Rechazo.","info");
			return;			
		}
	
		$("#cMotivoRechazo").val( $("#motivoRechazo").val() );
		queryFormPost("motivoRechazoContratoPlurianualUpdate", {async:false});
		$("#capturaRechazo_DIV").dialog("close");		
		
	}
	
	function cargaInfoFolio(){
	
		queryFormPost("tContratosPlurianualFolio_Read", {async:false});
		
		var nTipoCopntrato = $("#nTipoContrato").val();
		var nSolicitud =  $("#nSolicitud").val();
		var nEspecificacion = $("#nEspecificacion").val();
		
		if(nTipoCopntrato == 1){
			$("#rdoAbierto").prop("checked", true);
			$("#rdoCerrado").prop("checked", false);
			$("#lblmMinimo").show();
			$("#mMontoMinimo").show();
			$("#lblmMaximo").show();
			$("#mMontoMaximo").show();
			$("#lblmMin").show();
			$("#mMontoMin").show();
			$("#lblmMax").show();
			$("#mMontoMax").show();
		}else if(nTipoCopntrato == 2){
			$("#rdoCerrado").prop("checked", true);
			$("#rdoAbierto").prop("checked", false);
		}
		
		$("#cboSolicitud").val(nSolicitud);
		$("#cboEspecificacion").val(nEspecificacion);	
		$("#cDescripcionProyecto").val($("#cDescProyecto").val());
		$("#cJustifSolicitud").val($("#cJustificaSolicitud").val());
		$("#cFundamentoMotiv").val($("#cFundamentoMotivacion").val());
		$("#cEspecif").val($("#cEspecificacion").val());
		$("#cJustifEconomica").val($("#cJustificacionEconomica").val());
		$("#cJustifPlazo").val($("#cJustificacionPlazo").val());
		
		queryFormPost("existeEjercicioPluri", {async:false});
		
		if (idoper > 1 ){
			habilitarCampos(true);
			$("#btnImprimirRpt").show();
			$("#btnExportarSol").show();
			$(".pasoDos").show();
		
		} else{	
			
			if($("#bExiste").val()> 0) {
				totalContrato = Number(quitaFmt($("#mTotalContrato").val()));
				llenaFechaFin();
				$(".pasoDos").show();

			 } 
			
			document.getElementById("fInicio").readOnly = true;
			document.getElementById("fFin").readOnly = true;
			
		}
				
		cargarClaves();	
		cargaDetEjerciciosMontos();
	}
	
	function  habilitarCamposAlValidar(valor){
		document.getElementById("cJustificaSolicitud").disabled = valor;
		document.getElementById("cDescProyecto").disabled = valor;
		document.getElementById("cFundamentoMotiv").disabled = valor;
		document.getElementById("cEspecif").disabled = valor;
		document.getElementById("cJustifEconomica").disabled = valor;
		document.getElementById("cJustifPlazo").disabled = valor;
		document.getElementById("mMontoMinimo").disabled = valor;
		document.getElementById("mMontoMaximo").disabled = valor;
		document.getElementById("mTotalContrato").disabled = valor;
		
	}
	function habilitarCampos(valor){
		document.getElementById("folioSAI").disabled = valor;
		document.getElementById("cDescProyecto").disabled = valor;
		document.getElementById("cJustificaSolicitud").disabled = valor;
		document.getElementById("presupuesto").disabled = valor;
		document.getElementById("fInicio").disabled = valor;
		document.getElementById("fFin").disabled = valor;
		document.getElementById("rdoAbierto").disabled = valor;
		document.getElementById("rdoCerrado").disabled = valor;
		document.getElementById("cboSolicitud").disabled = valor;
		document.getElementById("cboEspecificacion").disabled = valor;
		document.getElementById("cboTipoMoneda").disabled = valor;
		document.getElementById("mMontoMinimo").disabled = valor;
		document.getElementById("mMontoMaximo").disabled = valor;
		document.getElementById("mTotalContrato").disabled = valor;	
		document.getElementById("cFundamentoMotiv").disabled = valor;
		document.getElementById("cEspecif").disabled = valor;
		document.getElementById("cJustifEconomica").disabled = valor;
		document.getElementById("cJustifPlazo").disabled = valor;
		
		if (idoper == 3 || idoper == 2 )
			parent.document.getElementById("pb_save").disabled=false;
	}
	
	function creaDataTableClaves() {
		
		$('#dt_Claves').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : false,
			"bFilter" : false,
			"bSort" : true,
			"bInfo" : false,
			"bAutoWidth" : false,
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"bServerSide" : true,
			"sPaginationType": "full_numbers",
			aoColumns : 
			[ 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true },
				{ bVisible : false }, 
				{ bVisible : false },
				{ bVisible : true }
			]
			
		});

		
	}
	
	var oTableClaves;
	function cargarClaves() {
		var conds = " Folio = " + $("#FOLIO").val();

		if (idoper == 1) {
			oTableClaves = $('#dt_Claves').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vw_ContratosPlurianualDetalle&qw="
						+ conds,
				aoColumns : [ 
								{ sName : "EPCorta" },
								{ sName : "iEjercicioClave", sClass : "centerCls" }, 
								{ sName : "montoTotalClave", sClass : "rightCls" },
								{ sName : "montoEnero", sClass : "rightCls" }, 
								{ sName : "montoFebrero", sClass : "rightCls" }, 
								{ sName : "montoMarzo", sClass : "rightCls" }, 
								{ sName : "montoAbril", sClass : "rightCls" }, 
								{ sName : "montoMayo", sClass : "rightCls" }, 
								{ sName : "montoJunio", sClass : "rightCls" },
								{ sName : "montoJulio", sClass : "rightCls" }, 
								{ sName : "montoAgosto", sClass : "rightCls" }, 
								{ sName : "montoSeptiembre", sClass : "rightCls" },
								{ sName : "montoOctubre", sClass : "rightCls" }, 
								{ sName : "montoNoviembre", sClass : "rightCls" }, 
								{ sName : "montoDiciembre", sClass : "rightCls" },
								{ sName : "EP", bVisible : false }, 
								{ sName : "nDocRenglon", bVisible : false },
								{ sName : "eliminar", bVisible : true }
							],
				oLanguage : es_mx
			});
			} else {
			 
			oTableClaves = $('#dt_Claves').dataTable(
			{
				"bPaginate" : false,
				"bLengthChange" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vw_ContratosPlurianualDetalle&qw="
						+ conds,
				aoColumns : [ 
								{ sName : "EPCorta" },
								{ sName : "iEjercicioClave", sClass : "centerCls" }, 
								{ sName : "montoTotalClave", sClass : "rightCls" },
								{ sName : "montoEnero", sClass : "rightCls" }, 
								{ sName : "montoFebrero", sClass : "rightCls" }, 
								{ sName : "montoMarzo", sClass : "rightCls" }, 
								{ sName : "montoAbril", sClass : "rightCls" }, 
								{ sName : "montoMayo", sClass : "rightCls" }, 
								{ sName : "montoJunio", sClass : "rightCls" },
								{ sName : "montoJulio", sClass : "rightCls" }, 
								{ sName : "montoAgosto", sClass : "rightCls" }, 
								{ sName : "montoSeptiembre", sClass : "rightCls" },
								{ sName : "montoOctubre", sClass : "rightCls" }, 
								{ sName : "montoNoviembre", sClass : "rightCls" }, 
								{ sName : "montoDiciembre", sClass : "rightCls" },
								{ sName : "EP", bVisible : false }, 
								{ sName : "nDocRenglon", bVisible : false },
								{ sName : "eliminar", bVisible : false }
							],
				oLanguage : es_mx
			});
			 
		 }
	}
	
	function BuscarClavePresupuestal(){
		$("#EP").val("");
		$("#clavePresupuestal").val("");
		
		var ejerFiscal = "";
		
		ejerFiscal = $("#cboEjerFiscal").val();
		
		if(ejerFiscal == "0"){
			Swal.fire("Capture","Debe seleccionar un Año para continuar.","info");
		}else{
			window.open("AyudaClavesPresupuestal.jsp?ejercicioFiscal="+ ejerFiscal, 'AyudaClavesPresupuestal', 'status=1, width=900px, height=530px, left=100px');
		}		
						
	}		
	
	function cargaDatosDetalle( mes1, mes2, mes3, mes4, mes5, mes6, mes7, mes8, mes9, mes10, mes11, mes12, importeTotal){
		$("#mImporteTotal").val("");
		$("#mMes1").val(""); $("#mMes2").val(""); $("#mMes3").val(""); $("#mMes4").val("");
		$("#mMes5").val(""); $("#mMes6").val(""); $("#mMes7").val(""); $("#mMes8").val("");
		$("#mMes9").val(""); $("#mMes10").val(""); $("#mMes11").val(""); $("#mMes12").val("");
		
		$("#mImporteTotal").val(importeTotal);
		$("#mMes1").val(mes1); $("#mMes2").val(mes2); $("#mMes3").val(mes3); $("#mMes4").val(mes4);
		$("#mMes5").val(mes5); $("#mMes6").val(mes6); $("#mMes7").val(mes7); $("#mMes8").val(mes8);
		$("#mMes9").val(mes9); $("#mMes10").val(mes10); $("#mMes11").val(mes11); $("#mMes12").val(mes12);
		
	}
	
	function validarFechas(){
		var bRegresa = true;	
		var fInicio = $("#fInicio").val();  
		var fFin = $("#fFin").val();
		
		var iAnioInicio = parseInt(fInicio.substr(6,4), 10);
		var iAnioFin = parseInt(fFin.substr(6,4), 10);
		
		if(iAnioFin <= iAnioInicio){
			Swal.fire("Verifique!!","El Año de la Fecha Fin no puede ser menor o igual la Año de la Fecha Inicio. ","info");
			var sFechaFin = "01/01/" + (iAnioInicio+1);
			$("#fFin").val(sFechaFin);
			iAnioFin = parseInt(sFechaFin.substr(6,4), 10);
			cargarEjercicioFiscal(iAnioInicio, iAnioFin);
			cargarEjercicioMontos(iAnioInicio, iAnioFin);			
			bRegresa = false;
		}else{
			cargarEjercicioFiscal(iAnioInicio, iAnioFin);
			cargarEjercicioMontos(iAnioInicio, iAnioFin);
		}
		
		return bRegresa;		
	}
	
	function cargarEjercicioFiscal(inicio, fin){
		var iCont = fin - inicio;
	   	var ejerFiscal = 0;
	   	
	   	removeSelectBox(document.getElementById("cboEjerFiscal"));
	   	
	   	$("#cboEjerFiscal").append("<option value=" + ejerFiscal + ">--Seleccione--</option>");
	   	
	   	ejerFiscal = inicio;
	   	
		for(var i=0; i<=iCont; i++){ 
    		$("#cboEjerFiscal").append("<option value=" + ejerFiscal + ">" + ejerFiscal + "</option>");
    		ejerFiscal++;
 		}
	}
	
	function cargarEjercicioMontos(inicio, fin){
		var iCont = fin - inicio;
	   	var ejerFiscal = 0;
	   	
	   	removeSelectBox(document.getElementById("cboEjercicioMontos"));
	   	
	   	$("#cboEjercicioMontos").append("<option value=" + ejerFiscal + ">--Seleccione--</option>");
	   	
	   	ejerFiscal = inicio;
	   	
		for(var i=0; i<=iCont; i++){ 
    		$("#cboEjercicioMontos").append("<option value=" + ejerFiscal + ">" + ejerFiscal + "</option>");
    		ejerFiscal++;
 		}
	}
	
	function removeSelectBox(selectbox)
	{
	    var i;
	    for(i = selectbox.options.length - 1 ; i >= 0 ; i--)
	    {
	        selectbox.remove(i);
	    }
	}
	
	function llenaFechaFin(){		
				
		var fInicio = $("#fInicio").val();  
		var fFin = $("#fFin").val();
		
		var iYearInicio = 0;
		var iYearFin = 0;
		
		if(isEmpty("fFin")){
			var iYearFecha = parseInt(fInicio.substr(6,4), 10) + 1;
			var sFechaFin = "01/01/" + iYearFecha;
			$("#fFin").val(sFechaFin);
			fFin = $("#fFin").val();
			
			iYearInicio = parseInt(fInicio.substr(6,4), 10);
			iYearFin = parseInt(fFin.substr(6,4), 10);
			
			$("#anioInicial").val(iYearInicio);
			queryFormPost("consultaJustificacion", {async:false});
			
			cargarEjercicioFiscal(iYearInicio, iYearFin);
			cargarEjercicioMontos(iYearInicio, iYearFin);
		}else{
			iYearInicio = parseInt(fInicio.substr(6,4), 10);
			iYearFin = parseInt(fFin.substr(6,4), 10);
			
			$("#anioInicial").val(iYearInicio);
			queryFormPost("consultaJustificacion", {async:false});
			
			if(iYearInicio >= iYearFin){
				Swal.fire("Verifique!!","El Año de la Fecha Inicio no puede ser Mayor o Igual al Año de la Fecha Fin.","info");
				var sFechaInicio = "01/01/" + (iYearFin-1);
				$("#fInicio").val(sFechaInicio);
				iYearInicio = parseInt(sFechaInicio.substr(6,4), 10);
				cargarEjercicioFiscal(iYearInicio, iYearFin);
				cargarEjercicioMontos(iYearInicio, iYearFin);
			}else{
				cargarEjercicioFiscal(iYearInicio, iYearFin);
				cargarEjercicioMontos(iYearInicio, iYearFin);
			}
		}	
						
	}
	
	
	function validaMontoTotalContrato(){	
		var bRegresa = true;
		
		totalContrato = parseFloat( quitaFmt( $("#mTotalContrato").val()) );
		totalContrato = totalContrato.toFixed(2);
		var mMontoMinimoContrato = Number( quitaFmt($("#mMontoMinimo").val()) );
		mMontoMinimoContrato =  mMontoMinimoContrato.toFixed(2);		
		
		var porcentaje = 0.00;
		porcentaje = (mMontoMinimoContrato / totalContrato );		
		porcentaje = porcentaje.toFixed(2);
		
		if($("#rdoAbierto").prop("checked")){
			if(parseFloat(porcentaje) < 0.40){
				Swal.fire("Verifique!!","El Monto minimo del Contrato no puede ser menor al 40% del total del Contrato. ", "info");
				bRegresa = false;			
			}
		}
						
		return bRegresa;
	}	
	
	var renglon = 1;
	var montoTotal = 0;
	
	function agregarImporteEjercicio(){
		
		totalContrato = Number( quitaFmt( $("#mTotalContrato").val() ));
		totalContrato = parseFloat(Math.round(totalContrato *100) / 100);
		var montoEjercicio = Number(quitaFmt($("#mMontoEjercicio").val()));
		var montoMax =Number( quitaFmt( $("#mMontoMax").val()));
		var montoMin = Number( quitaFmt($("#mMontoMin").val()));
		
		montoEjercicio = parseFloat(Math.round(montoEjercicio *100) / 100);
		montoMax = parseFloat(Math.round( montoMax  *100) / 100);
		montoMin = parseFloat(Math.round( montoMin  *100) / 100);
		
		if(parseFloat(montoEjercicio) <= 0){
			Swal.fire("Capture","Es necesario capturar el Importe para continuar.","info");
			return;
		}
				
		$("#aEjercicioMonto").val($("#cboEjercicioMontos").val());
		queryFormPost("consultaEjercicioPluri", {async:false});
		queryFormPost("consultaMontoTotalPluri", {async:false});
		
		if ($("#montoTotal").val() > 0) {
			montoTotal = Number( quitaFmt( $("#montoTotal").val() ));
			montoTotal = parseFloat(Math.round( montoEjercicio  *100) / 100) + parseFloat(Math.round( montoTotal  *100) / 100);
			montoTotal = Number (montoTotal.toFixed(2))
		} 
		
		if ((montoTotal  > totalContrato) || (montoEjercicio > totalContrato)){
			Swal.fire("Verifique!!","El monto del Ejercicio sobregira el monto Total del Contrato. ","info");
			return;
		}
		
		if($("#bExisteEjer").val() > 0){
			Swal.fire("Verifique!!","El Ejercicio seleccionado ya esta capturado.","info");
			return;
		}else{	
				renglon = ++ renglon;
				$("#renglon").val(renglon);
				queryFormPost("tContratosPlurianual_MontosEjerciciosCreate", {async:false});
				$("#mMontoEjercicio").val("0");
				$("#mMontoMax").val("0");
				$("#mMontoMin").val("0");
				cargaDetEjerciciosMontos();
				queryFormPost("consultaMontoTotalPluri", {async:false});
		}
		
		if(montoTotal.toFixed(2) == parseFloat(totalContrato).toFixed(2)) {
			$("#tabDesglose").click();
		} 
				
	}
	
	var oTableEjerMontos;
	function cargaEjerciciosMontosTabla() {
	 	$('#dt_MontosEjercicios').dataTable(
		{
			"bPaginate": false,
   			"bLengthChange": false,
   			"bFilter": false,
   			"bSort": false,
   			"bInfo": false,
   			"bAutoWidth": true,
   			"sScrollX": 100,
			"sScrollY": 100,
			"bScrollCollapse" : true,
			"bJQueryUI": true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType": "full_numbers",
			aoColumns : 
			[ 
				{ bVisible : true },
				{ bVisible : true }, 
				{ bVisible : false }, 
				{ bVisible : true }, 
				{ bVisible : true }, 
				{ bVisible : true }
			]
		});		
	}
	
	function cargaDetEjerciciosMontos() {
		var conds = " Folio = " + $("#FOLIO").val();

	if (idoper == 1 ) {
		oTableEjerMontos = $('#dt_MontosEjercicios').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth": true,
   			"sScrollX": "100%",
			"sScrollY": "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			"processing": true,
			sAjaxSource : window.location.protocol + "//"
					+ window.location.host + "/"
					+ window.location.pathname.split("/")[1]
					+ "/crud?rt=t&ql=vw_ContratosPlurianual_MontosEjercicios&qw="
					+ conds,
			aoColumns : [ 
							{ sName : "aEjercicio", sClass : "centerCls" },
							{ sName : "montoEjercicio", sClass : "leftCls" }, 
							{ sName : "nDocRenglon", bVisible : false },
							{ sName : "mMontoMin", sClass : "leftCls" }, 
							{ sName : "mMontoMax", sClass : "leftCls" },
							{ sName : "eliminar",sClass : "leftCls" }  
						],
			oLanguage : es_mx
		})		
	
	} else if (idoper > 1 ) {
			oTableEjerMontos = $('#dt_MontosEjercicios').dataTable(
		{
			"bPaginate" : false,
			"bLengthChange" : true,
			"bFilter" : true,
			"bSort" : true,
			"bInfo" : true,
			"bAutoWidth": true,
   			"sScrollX": "100%",
			"sScrollY": "100%",
			"bJQueryUI" : true,
			"bRetrive" : true,
			"bDestroy" : true,
			"sPaginationType" : "full_numbers",
			"bScrollCollapse" : true,
			"bServerSide" : true,
			"processing": true,
			sAjaxSource : window.location.protocol + "//"
					+ window.location.host + "/"
					+ window.location.pathname.split("/")[1]
					+ "/crud?rt=t&ql=vw_ContratosPlurianual_MontosEjercicios&qw="
					+ conds,
			aoColumns : [ 
							{ sName : "aEjercicio", sClass : "centerCls" },
							{ sName : "montoEjercicio", sClass : "leftCls" }, 
							{ sName : "nDocRenglon", bVisible : false },
							{ sName : "mMontoMin", sClass : "leftCls" }, 
							{ sName : "mMontoMax", sClass : "leftCls" },
							{ sName : "eliminar",sClass : "leftCls", bVisible : false  }  
						],
			oLanguage : es_mx
		})		
			
		}
			
	}
	
	function updateFirmantes(){
		queryFormPost({
			queryName:"existeFirmanteSolContratoPLU_Read", 
			async : false, 
			callback:function(){
				if (idoper == 1 && $("#existeFirmanteSol").val()=="SIEXISTE" ){
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
				}else
					parent.document.getElementById("pb_save").disabled=true;
					modalFirmantes.show();
					
			}
		});
	}
	
	function aceptarFirmante() {
		if($.trim($("#cNombreSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Nombre en Datos Solicita","warning"); return; } 
			else if($.trim($("#cApPaternoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Apellido Paterno en Datos Solicita","warning"); return; }
			else if($.trim($("#cApMaternoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Apellido Materno en Datos Solicita","warning"); return; }
			else if($.trim($("#cPuestoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Puesto en Datos Solicita","warning"); return; }	
								
			$("#cNombreS").val($("#cNombreSol").val());
			$("#cPaternoS").val($("#cApPaternoSol").val());
			$("#cMaternoS").val($("#cApMaternoSol").val());
			$("#cPuestoS").val($("#cPuestoSol").val());
			
			$("#firmanteSol").val($("#cNombreS").val()+" "+$("#cPaternoS").val()+" "+$("#cMaternoS").val());
			
			try{						
				queryFormPost("firmanteSolicitaContratoPlurianual_Update", {async: false });
				
				$("#cNombreSol").val("");
				$("#cApPaternoSol").val("");
				$("#cApMaternoSol").val("");
				$("#cPuestoSol").val("");													
				
				cmdImprimir();
				exportarSolicitudExcel();
				
				Swal.fire("Ok!","Solicitud de Contrato Plurianual generada correctamente.","success");
				
				if(parent.document.getElementById("pb_send"))
					parent.document.getElementById("pb_send").disabled=false;
				
			}catch(e){
				if(parent.document.getElementById("pb_save"))
					parent.document.getElementById("pb_save").disabled=false;
				Swal.fire("Verifique!","No se pudo actualizar los firmantes, intente mas tarde.","info");
			}
					
			modalFirmantes.hide();
	}
	/*
	function creaDlgFirmanteSolicita() { 
      	$("#dlgFirmanteSol").dialog({
	        title:"Datos de Firmante Solicita.",
	        autoOpen : false,
	        height : 420,
	        width : 500,
	        modal : true,
	        buttons : {
            	"Aceptar" : function() {
                    					
					if($.trim($("#cNombreSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Nombre en Datos Solicita","warning"); return; } 
					else if($.trim($("#cApPaternoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Apellido Paterno en Datos Solicita","warning"); return; }
					else if($.trim($("#cApMaternoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Apellido Materno en Datos Solicita","warning"); return; }
					else if($.trim($("#cPuestoSol").val()) == ""){ Swal.fire("Capture","Falta Ingresar Puesto en Datos Solicita","warning"); return; }	
										
					$("#cNombreS").val($("#cNombreSol").val());
					$("#cPaternoS").val($("#cApPaternoSol").val());
					$("#cMaternoS").val($("#cApMaternoSol").val());
					$("#cPuestoS").val($("#cPuestoSol").val());
					
					$("#firmanteSol").val($("#cNombreS").val()+" "+$("#cPaternoS").val()+" "+$("#cMaternoS").val());
					
					try{						
						queryFormPost("firmanteSolicitaContratoPlurianual_Update", {async: false });
						
						$("#cNombreSol").val("");
						$("#cApPaternoSol").val("");
						$("#cApMaternoSol").val("");
						$("#cPuestoSol").val("");													
						
						//anexo();
						cmdImprimir();
						exportarSolicitudExcel();
						
						Swal.fire("Ok!","Solicitud de Contrato Plurianual generada correctamente.","success");
						if(parent.document.getElementById("pb_send"))
							parent.document.getElementById("pb_send").disabled=false;
						
					}catch(e){
						if(parent.document.getElementById("pb_save"))
							parent.document.getElementById("pb_save").disabled=false;
						Swal.fire("Verifique!","No se pudo actualizar los firmantes, intente mas tarde.","info");
					}
					$(this).dialog("close");  
             	},
              	"Cancelar" : function() {
                	$(this).dialog("close");
                }
         	}
       });   
 
	}
	*/
	
	function cmdImprimir(){
		//Se imprime la solicitud de contrato plurianual
			window.open("../plurianuales/SolicitudPlurianual?folio=" + $("#FOLIO").val() + "&cEsModificado=0&nFolioContratoPlurianual=0&esOriginal=1", "_blank", "toolbar=no,scrollbars=no,resizable=yes,top=800,left=800,width=250,height=250");
	}	
	
	function exportarSolicitudExcel() {
			var cFolio = $("#FOLIO").val();
			
			$.blockUI();
			try {
				createInput( 'rptExcelForm', 'generaExcel', "1" );
				createInput( 'rptExcelForm', 'FOLIO', cFolio );
				
				$( "#rptExcelForm" ).submit();
				$.unblockUI();
				
			} catch( e ) {
				$.unblockUI();
				alert( e );
			}
		}

		function borraElementos() {
			$( '.remove' ).remove();
		}
		
		function createInput( form, name, value ) {
			$( '<input>' ).attr( {
			type : 'hidden',
			name : name,
			value : value
			} ).addClass( 'remove' ).appendTo( '#' + form );
		}
		
		function fnGetSelected( oTableLocal ) {
		
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			
			for ( var i=0 ; i<aTrs.length ; i++ )
			{				
				aReturn.push( aTrs[i] );
				editRow ( oTableLocal, aReturn );
				aReturn.shift();				
			}
			return aReturn;
		}
	
		function onlyNumbersPositivo(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '0123456789.';
		
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
		
			return true ;
		}
		
		function validarCapturaImportesEjer(){
			var bRegresa = true;
						
			var aniosClave = $("#cAniosCapturados").val();  
			var aniosEjercicio = $("#cNumEjercicio").val();
			
			if(aniosClave < aniosEjercicio){
				Swal.fire("Verifique!!","No se han capturado todos los Importes por Ejercicio. ","warning");
				bRegresa = false;
			}
			
			return bRegresa;		
		}
		
		function eliminaRegistro(row) {
			Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se eliminará el importe del Ejercicio.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $("#renglon").val(row);
						//Consulta año de los ejercicios a borrar
						queryFormPost("consultaAnioPluri_read", {async:false});
			    		    		
			    		//Borra el ejercicio
			    		queryFormPost("delete_contatoPluri_ejercicio", {async:false});
			    	    
			    	    //Elimina las claves de ese año que se borro
			    	    queryFormPost("delete_contatoPluri_detAnio", {async:false});
			    	    
			    	     //Consulta el importe que quedo
			    	    queryFormPost("consultaMontoTotalPluri", {async:false});
			    	    
			    	    //Carga la tabla de nuevo
			    	    cargaDetEjerciciosMontos();
			    	    
			    	     //Deshabilita el guardar
			    	    parent.document.getElementById("pb_save").disabled=true;
			    	    parent.document.getElementById("pb_send").disabled=true;				  
			    	} 
				})
			    
		}

	var folio = 0;
	function eliminaRegistroCve(row) {
		
		Swal.fire({
			  title: 'Desea continuar?',
			  text: "Se eliminará el registro seleccionado",
			  icon: 'warning',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
				//Borra el registro
					$("#renglon").val(row);
		    		queryFormPost("delete_contatoPluri_detalle", {async:false});
		    		    	    
		    	    //modifica los acumulados
					queryFormPost("consultaMontoAcumulado", {asyn:false});
		    		
		    		//vuelve a recargar las tablas
		    	    cargarClaves();
		    	    //Deshabilita el boton de guardar
		    		parent.document.getElementById("pb_save").disabled=true;	
			  } 
			})
						
		}