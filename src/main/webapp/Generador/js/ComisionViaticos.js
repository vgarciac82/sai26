//	let transporte = 0;
	jQuery.browser = {};
	let fInicio ="";
	let fFin = ""; 

(function () {
    jQuery.browser.msie = false;
    jQuery.browser.version = 0;
    if (navigator.userAgent.match(/MSIE ([0-9]+)\./)) {
        jQuery.browser.msie = true;
        jQuery.browser.version = RegExp.$1;
    }
})();
	
	const cuota = 1700;

	
	
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
		});
		
		$("#fFin").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
		});
		
	}
	
	function validaFechaAnterior(fecha) {
		let hoy = moment();
		let arrFecha= $(fecha).val().split("/");
		let fechaInicial=(arrFecha[2]+"-"+arrFecha[1]+"-"+arrFecha[0]);
		
		let diasDif  = hoy.diff(moment(fechaInicial), 'days');
		
		if (validaRFCCapturado()){
			if(diasDif > 20 && $("#tieneExcepcionMA").val() == 0 ){
				Swal.fire("Verifique","No se puede hacer una comision con una antiguedad mayor a 15 días. Consulte con el área de Tesoreria", "info");
				$(fecha).val("");
				return false;
			}
				
		}
	}
	
	function verificaFechas(){
		if(!validarFechas()){
			Swal.fire("VERIFICAR FECHAS","La fecha de Inicio no puede ser mayor a la fecha Fin", "error");
			$("#fFin").val("");
		} else 	if (!validarFechasExistentes()) {
			Swal.fire("Validar fechas con formatos anteriores","En esas fechas ya existen Viaticos previamente capturados.", "error");
			$("#fFin").val("");	
		} else if (!validarFechasIncidencias()) {
			Swal.fire("Validar fechas con incidencias","Ya existen Incidencias en esta fecha no se puede capturar, favor de contactar a RH.", "error");
			$("#fFin").val("");	
		}		
		
		queryFormPost("leeEjercicioAgenda", {async: false});
		if($("#agendaDifAnio").val() != 0){
			Swal.fire("Captura en distinto año","El año del ejercicio fiscal es diferente al año de la comision capturada","error");
			return false;
		}		
			
	}
	
	function validaRFCCapturado() {
		if ($("#cIdRFC").val() =="") {
			Swal.fire("Capture RFC","Debe capturarse primero el RFC del Empleado", "error");
			$("#fInicio").val("");
			return false
		}	else {
			queryFormPost("esExcepcionMesesAnteriores", {async: false});
		}	
		
		return true;
	}
	
	function validaCampos(){
		
		if($("#fInicio").val() == ""){
			Swal.fire("Favor de capturar","La fecha de inicio debe tener un valor para poder generar la comisión.", "warning");
			return false;
		}else if($("#fFin").val() == ""){
			Swal.fire("Favor de capturar","La fecha de fin debe tener un valor para poder generar la comisión.", "warning");
			return false;
		}else if(!validarFechas()){
			Swal.fire("VERIFICAR FECHAS","La fecha de Inicio no puede ser mayor a la fecha Fin", "error");
			return false;
		}else if(!validarFechas()){
			Swal.fire("Verifique las fechas!","En esas fechas ya existen Viaticos previamente capturados.", "error");
			return false;
		}else if (document.getElementById("cPais").value == 0){
			Swal.fire("Seleccione", "Seleccione el pais para poder generar la comisión.","warning");
			return false;
		}else if (document.getElementById("estadocombo").value == 0){
			Swal.fire("Seleccione","Seleccione un estado para poder generar la comisión.","warning");
			return false;
		}else if (document.getElementById("estadocombo").value == 9999){
			Swal.fire("Favor de agregar estado",'Presionar el boton "Agregar" para que agregue el nuevo Estado.', "warning");
			return false;
		}else if (document.getElementById("municipiocombo").value == 0){
			Swal.fire("Capturar",'Favor de capturar un condado o municipio para poder generar la comisión.', "warning");
			return false;
		}else if (document.getElementById("municipiocombo").value == 9999){
			Swal.fire("Favor de agregar ciudad",'Presionar el boton "Agregar" para que agregue el nuevo Municipio/Ciudad.', "warning");
			return false;
		}else if ($("#cConcepto").val() == "" ){
			Swal.fire("Favor de capturar",'Capture el motivo de la comisión',"warning");
			return false;			
		}else if ($("#idNombre").val() == "0"  && $("#operacion").val()!="EDITAR" ){
			Swal.fire("Favor de capturar",'Seleccione el nombre de la comisión para continuar.',"warning");
			return false;
		}else if (document.getElementById("chk_homologa").checked == true && $("#nivelHomologa").val() == '0' ){
			Swal.fire("Seleccione",'Debe seleccionar el nivel de homologación de la comisión',"warning");
			return false;
		}else if (document.getElementById("chk_paquete").checked == true && $("#paqueteCombo").val() == '0' ){
			Swal.fire("Seleccione",'Debe seleccionar el paquete de la comisión',"warning");
			return false;
		} else if ( $("#ctaBancaria").val() == "Seleccione cuenta bancaria" || $("#operacion").val()!="EDITAR" && $("#ctaBancaria").val() == null){
			Swal.fire("Seleccione","Debe seleccionar la cuenta bancaria del empleado","warning");
			return false;	
		} else if ($("#cMoneda").val() != 1 && $("#tipoCambio").val() == 1) {
			Swal.fire("Capture el tipo de cambio","El tipo de moneda es diferente de MXN y el tipo de cambio no fue actualizado. Favor de capturarlo.","warning");
			return false;
		} else if ($("#cPais").val() != 146 && $("#cMoneda").val() == 1){
			Swal.fire("Comision Internacional", "Es una comisión internacional y el tipo de moneda sigue en MXN. Favor de actualizarla.","warning");
			return false;
		}
		
		if ($("#cMoneda").val() == 1){
			$("#tipoCambio").val(1);
		}
		
		
		if ($("#operacion").val()!="EDITAR") {
			queryFormPost("registrosAgenda", {async: false});
			if($("#registros").val() > 0 ) {
				
					queryFormPost("consultaFechaFinAnterior", {async: false});
					var arrFecha=$("#fInicio").val().split("/");
				    var fechaInicial=(arrFecha[2]+"-"+arrFecha[1]+"-"+arrFecha[0]);
				    
					if ($("#fFinAnterior").val() != fechaInicial){
						Swal.fire("Fechas no enlazadas", "La fecha inicial debe ser igual a la fecha final del renglón anterior","info");
						return false;
					}
				}	
		} else if ( $("#operacion").val() =="EDITAR"){
			queryFormPost("registrosAgendaEdicion", {async: false});
			
			if($("#registros").val() > 0 ) {
					queryFormPost("consultaFechaFinAnteriorEditar", {async: false});
					var arrFecha=$("#fInicio").val().split("/");
				    var fechaInicial=(arrFecha[2]+"-"+arrFecha[1]+"-"+arrFecha[0]);
				    
					if ($("#fFinAnterior").val() != fechaInicial){
						Swal.fire("Fechas no enlazadas", "La fecha inicial debe ser igual a la fecha final del renglón anterior","info");
						return false;
					}	
			}	
		}
			
			
		return true;
	}
	
	function save(){
		if (validaCampos()){
			 if(validaDatosPrevios()){
				if(agregaAgenda()) 
					limpiarValores();
					$("#btnBeneficiario").hide();
			}
		}	
	}
	
	function limpiarValores() {
		$("#fInicio").val("");
		$("#fFin").val("");
		$("#cPais").val(146);
		$("#estadocombo").val(0);
		$("#municipiocombo").val(0);
		$("#cLocalidad").val("");
		$("#cConcepto").val("");
		$("#cActividades").val("");
		$("#chk_homologa").prop("checked", false);
		$("#nivelHomologa").val(0);
		$("#plazaHomologa").val("");
		$("#plazaJustifica").val("");
		$("#chk_paquete").prop("checked", false);
		$("#paqueteCombo").val(0);
		$("#hasTicket").val(0);
		$("#nPorcentaje").val("");
		$(".inputHomologa").css('display','none');
		$(".inputPaquete").css('display', 'none');
		cargaEstados();
		cargaCuota();
	}
	
	function validaDatosPrevios() {
		let valida = true;
		
		//Valida fecha inicio
		$("#dateTemp").val($("#fInicio").val())
		queryFormPost("validaFechasPrevias", {async: false});
		
		if($("#dateExist").val() != 0){
			Swal.fire("Verifique la fecha de inicio","Ya hay una Agenda agregada con entre esa fecha en este tramite","error");
			return false;
		}
		
		//Valida fecha fin
		$("#dateTemp").val($("#fFin").val())
		queryFormPost("validaFechasPrevias", {async: false});
		if($("#dateExist").val() != 0){
			Swal.fire("Verifique la fecha de fin","Ya hay una Agenda agregada con entre esa fecha en este tramite","error");
			return false;
		}
		
		if(!validarFechasExistentes()){
			Swal.fire("Verifique las fechas","Ya hay formatos previos guardados con esas fechas","error");
			return false;
		}
		
		if (!validarFechasIncidencias()) {
			Swal.fire("Validar fechas con incidencias","Ya existen Incidencias en esta fecha no se puede capturar, favor de contactar a RH.", "error");
				$("#fInicio").val("");
				$("#fFin").val("");
			}	
		
		return valida;
	}
	
	
	function avanzar(){
		queryFormPost("registrosAgenda", {async: false});
		
		if($("#registros").val() > 0 ) {
				Swal.fire({
					  title: 'Desea continuar?',
					  text: " Ya no podrá agregar más agendas a este trámite",
					  icon: 'warning',
					  showCancelButton: true,
					  confirmButtonColor: '#288BA8',
					  cancelButtonColor: '#e6e6e6',
					  confirmButtonText: 'Aceptar',
					  cancelButtonText: 'Cancelar'
					}).then((result) => {
					  if (result.isConfirmed) {
						    $('[href="#transporte"]').tab().show();
							$('#viaticos-list a[href="#transporte"]').tab('show');
							$("#encabezadoAgenda").hide();
							$("#divTablaAgenda").hide();
							createTableAgendaConsulta();
							$("#divTablaAgendaConsulta").show();
							Swal.fire("OK!","Agendas guardadas exitosamente", "success");
					  } 
					})
		} else {
			Swal.fire("Agregar agenda","Antes de avanzar debe agregar la Agenda","warning");
		}
	}
	
	function avanzaTransporte(){
		$("#encabezadoAgenda").hide();
		
		Swal.fire({
				  title: 'Desea continuar?',
				  text: " Ya no podra capturar mas tipos de transporte.",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  	$('[href="#resumen"]').tab().show();
						$('#viaticos-list a[href="#resumen"]').tab('show');
						$("#transporteEncabezado").hide();
						Swal.fire("OK!","Transportes guardados exitosamente", "success");
						
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					  Swal.fire("Recuerde:","Debe dar click en el boton Agregar para que se guarde el Transporte Local","info");
				  }
			})
	
			queryFormPost("calcularTotales", {async:false});
			queryFormPost("actualizarTotales", {async:false});
			queryFormPost("calculaDiasNacional", {async:false});
			queryFormPost("calculaDiasInternac", {async:false});
			
			let total = addCommas($("#totalGeneral").val());
			$("#totalGeneral").val(total);
			
			let transporte = addCommas($("#totalTransporte").val());
			$("#totalTransporte").val(transporte);
			
			let totAgenda = addCommas($("#totalAgenda").val());
			$("#totalAgenda").val(totAgenda);
			
			let totalDias = parseFloat($("#diasNacional").val()) + parseFloat($("#diasInternacional").val())
				+ parseFloat($("#totalDias").val())
				
			$("#totalAcDias").val(totalDias);
			
			parent.document.getElementById("pb_save").disabled = false;
	}
	
	function cargaPaises(){
		
		querySelectPost("tCatalogoPaisesRead", "cPais", {async:false});
		$("#cPais").val(146);
		
	}
	
	function cargaEstados(){
		$("#pais").val(document.getElementById("cPais").value);
		querySelectPost("cat_EstadosRead", "estadocombo", {async:false});
		
	}
	
	function cargaMunicipios(){
		$("#estado").val(document.getElementById("estadocombo").value);
	
		if ($("#estado").val() == 9999) {
			$("#dlgAgregar").css('display', 'block');
		}  else {
			$("#dlgAgregar").css('display', 'none');
		}
				
		querySelectPost("cat_MunicipiosRead", "municipiocombo", {async:false});
		
		$("#municipio").val(document.getElementById("municipiocombo").value);
	}
	
	function validaMunicipio(){
		$("#municipio").val(document.getElementById("municipiocombo").value);
	
		if ($("#municipio").val() == 9999) {
			$("#dlgAgregarMpio").css('display', 'block');
		}  else {
			$("#dlgAgregarMpio").css('display', 'none');
		}
		
	}
	

	function cargaTransporte(){
		$("#tipoTransp").val(document.getElementById("transporteCombo").value);
		querySelectPost("tTransporteRead", "transporteCombo", {async:false});
		
	} 
	
	function quitaFrmt(fld) {
		var valcol = fld.toString();
		valcol = valcol.replace(/[$]/g, "");
		valcol = valcol.replace(/,/g, "");
		return valcol;
	}
	
	function agregarEstado(){
	
		if ($('#nombre_estado').val() ==""){
			Swal.fire("Captura el estado","Debe capturar el nombre del estado para darlo de alta", "info");
		} else {
			//Se agrega el nuevo estado
			queryFormPost("nuevoEstado", {async:false});
			
			//Se consulta el estado y se actualiza en el combo
			queryFormPost("cat_EstadosNuevoRead", {async:false});
			querySelectPost("cat_EstadosRead", "estadocombo", {async:false});
			$('#estadocombo').val($('#estado').val());
			
			//Se ocultan los controles de agregar
			$("#dlgAgregar").css('display', 'none');
		}
	}
	
	function agregarMunicipio(){
	
		if ($('#nombre_municipio').val() ==""){
			Swal.fire("Captura el condado/ciudad","Debe capturar el nombre de la ciudad/municipio para darlo de alta", "info");
		} else {
			//Se agrega el nuevo municipio / ciudad
			queryFormPost("nuevoMunicipio", {async:false});			
			
			// Se consulta el municipio agregado
			queryFormPost("cat_MunicipioNuevoRead", {async:false});
			querySelectPost("cat_MunicipiosRead", "municipiocombo", {async:false});
			$('#municipiocombo').val($('#municipio').val());
			
			//Se ocultan el dlg de agregar
			$("#dlgAgregarMpio").css('display', 'none');
		}
	}

	function cargaNiveles() {
		$("#niveles").val(document.getElementById("nivelHomologa").value);
		$("#nivel1").val($("#cNivel").val().charAt(0));
		querySelectPost("cat_TiposNivelesRead", "nivelHomologa", {async:false});	
		queryFormPost("consultaTiposNivelesRead", {async:false});
		
		if(document.querySelector("#chk_paquete").checked){
						//cargaCuota();
						calculaCuotaPorDia();
		}
	}
	
	function cargaCuota() {
		//Dolares
		if ($("#cMoneda").val() == 2) {
			if (document.querySelector("#chk_homologa").checked) {
				queryFormPost("consultaTiposNivelMERead", {async:false});
			} else {
				queryFormPost("consultaCuotaEmpleadoDll", {async:false});		
			}
		//Euros
		} else if ($("#cMoneda").val() == 3)  {
			if (document.querySelector("#chk_homologa").checked) {
				queryFormPost("consultaTiposNivelMERead", {async:false});
			} else {
				queryFormPost("consultaCuotaEmpleadoEU", {async:false});		
			}
		//Pesos
		} else {
			if (document.querySelector("#chk_homologa").checked) {
				queryFormPost("consultaTiposNivelesRead", {async:false});
			} else {
				queryFormPost("consultaCuotaEmpleado", {async:false});		
			}
		}
		
	}

	function creaDT( conds ){
		if( !conds )
			conds = "1<>1";
		
		oTable = $('#comisionesDT').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : true,
				"bAutoWidth" : true,
				"sScrollY" : 270,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType" : "full_numbers",
				"bScrollCollapse" : true,
				"bServerSide" : true,
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_ViaticosComisiones&qw=" + conds,
				aoColumns : [ {
					sName : "nIdComision"
				}, {
					sName : "cConcepto_Comision"
				}, {
					sName : "fInicio"
				}, {
					sName : "fFin"
				}, {
					sName : "Ubicacion"
				}, {
					sName : "cEstatusDesc"
				}],
				oLanguage : es_mx
			});			
			
	}
	
	function leerNombreComision() {
		queryFormPost("leeNombreComision", {async:false});	
	}
	
	function agregaAgenda(){
		var guardado = false;
		var logErrores = "";
		$('#actividades').val($('#cActividades').val());
		$('#concepto').val($('#cConcepto').val());
		$("#cuentaBancaria").show();
		$("#cuentaBancaria").val($("#ctaBancaria").val());
		$("#ctaBancaria").hide();
	
		$.ajax({
			url : "../viaticos/guardarAgenda",
			type : 'post',
			async : false,
			data : $("#formViaticos").serialize(),
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					guardado = true;
					creaTableAgenda();
					$("#hasPackage").val(0);
					$("#hasSameRate").val(0);
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
			Swal.fire("Error guardando agenda", logErrores, "error")
			
		return guardado;
	}
	
	function eliminarAgenda(idAgenda) {
		$("#folioA").val(idAgenda);
		var eliminado = false;
		var logErrores = "";
		
		if ($("#tipoOperacion").val()=="EDICION_SIN_FIRMAS") {
			$("#idComision").val($("#cComision").val());
		}
		
		if ($("#operacion").val()=="EDITAR") {
			$("#idAgenda").val(idAgenda);
			$("#id").val($("#idComision").val());
		}
	
		$.ajax({
			url : "../viaticos/eliminarAgenda",
			type : 'post',
			async : false,
			data : {
				folioA 	 : $("#folioA").val(),
				idAgenda : $("#idAgenda").val(),
				id 		 : $("#id").val(),
				idComision :$("#idComision").val(),
				tipoOperacion: $("#operacion").val(),
				operacion: $("#tipoOperacion").val()
			},
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					eliminado = true;
					creaTableAgenda();
					if ($("#operacion").val()=="EDITAR") {
						limpiar();
						creaTableAgendaEditar();
					}
				} else {
					var errores = j.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						logErrores = logErrores + errores[cnt] + "\n";
					}
				}
			},
			error : function(errorThrown) {
				logErrores =  errorThrown.ERROR;
			}
		});
	
		if (!eliminado)
			Swal.fire("Error eliminando agenda", logErrores, "error")
	}
	
	function eliminarTransporte(idAgenda) {
		$("#folioA").val(idAgenda);
		var eliminado = false;
		var logErrores = "";
		
		if ($("#tipoOperacion").val()=="EDICION_SIN_FIRMAS") {
			$("#idComision").val($("#cComision").val());
		}	
		
		if ($("#operacion").val()=="EDITAR") {
			$("#idAgenda").val(idAgenda);
			$("#id").val($("#idComision").val());
		}
		
		$.ajax({
			url : "../viaticos/eliminarTransporte",
			type : 'post',
			async : false,
			data : {
				folioA 	 : $("#folioA").val(),
				idAgenda : $("#idAgenda").val(),
				id 		 : $("#id").val() ,
				idComision :$("#idComision").val()
			},
			dataType : 'json',
			success : function(j) {
				var exito = j.success;
	
				if (exito) {
					eliminado = true;
					creaTableTransporte();
					if ($("#operacion").val()=="EDITAR") {
						creaTableTransporteEditar();
						limpiarTransporte();
					}		
				} else {
					var errores = j.errorList;
					var cnt = 0;
					for (cnt = 0; cnt < errores.length; cnt++) {
						logErrores = logErrores + errores[cnt] + "\n";
					}
				}
			},
			error : function(errorThrown) {
				logErrores =  errorThrown.ERROR;
			}
		});
	
		if (!eliminado)
			Swal.fire("Error eliminando Tranporte", logErrores, "error")
	}
	
        
	function onLoadPlantilla(){
  	  	
  	}
  	
  	function revisaValores(){
		queryFormPost("existeComisionGenerada", {async: false});
	  		
	  		if($("#existeComision").val() > 0){
	  			$("#btnBeneficiario").hide();
	  			$("#radiosEmpleado").hide();
	  			
	  			queryFormPost("datosEmpleadoViatico", {async:false});
	  			queryFormPost("consultaAgendas", {async:false});
	  			
	  			if ($("#tieneAgenda").val() > 0) {
		  			$("#encabezadoAgenda").hide();
		  			$('[href="#transporte"]').tab().show();
					$('#viaticos-list a[href="#transporte"]').tab('show');
					$("#ctaBancaria").hide();
					$("#cuentaBancaria").show();
					
					createTableAgendaConsulta();
					$("#divTablaAgenda").hide();
					$("#divTablaAgendaConsulta").show();
				} else {
					$("#divTablaAgenda").show();
					$("#divTablaAgendaConsulta").hide();
					$('[href="#transporte"]').tab().hide();
					$('[href="#resumen"]').tab().hide();
				}
	  		} else {
				$("#divTablaAgenda").show();
				$("#divTablaAgendaConsulta").hide();
				$('[href="#transporte"]').tab().hide();
				$('[href="#resumen"]').tab().hide();
			}
	  		
	  		queryFormPost("existeTransporteGuardado", {async: false});
	  		
	  		if($("#existeTransporte").val() > 0){
				$('[href="#resumen"]').tab().show();
				$('#viaticos-list a[href="#resumen"]').tab('show');
				parent.document.getElementById("pb_save").disabled = false;
				
				queryFormPost("calculaDiasNacional", {async: false});
				queryFormPost("calculaDiasInternac", {async: false});
				queryFormPost("calcularTotales", {async:false});
				queryFormPost("actualizarTotales", {async:false});
				
				let total = addCommas($("#totalGeneral").val());
				$("#totalGeneral").val(total);
				
				let transporte = addCommas($("#totalTransporte").val());
				$("#totalTransporte").val(transporte);
				
				let totAgenda = addCommas($("#totalAgenda").val());
				$("#totalAgenda").val(totAgenda);
				
				let totalDias = parseFloat($("#diasNacional").val()) + parseFloat($("#diasInternacional").val())
					+ parseFloat($("#totalDias").val())
					
				$("#totalAcDias").val(totalDias);
				
				creaTableTransporteConsulta();
				$("#divTransporte").hide();
				$("#divTransporteConsulta").show();
				
				$("#transporteEncabezado").hide();
	  		} else {
				$("#divTransporte").show();
				$("#divTransporteConsulta").hide();
			}
		
	}
	
  	function guardarTramite(){
		var guardado = false;
		var logErrores = "";
		var totAgenda = $("#totalAgenda").val();
		var totTransporte = $("#totalTransporte").val();
		
		$("#totalAgenda").val(quitaFrmt(totAgenda));
		$("#totalTransporte").val(quitaFrmt(totTransporte));
		
		if (validaJustificacion()) {
			$.ajax({
				url : "../viaticos/guardarTramite",
				type : 'post',
				async : false,
				data : $("#formViaticos").serialize(),
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
				Swal.fire("Error guardando agenda", logErrores, "error")
				
		}	
		return guardado;
	}
	
	function validaJustificacion() {
		if ($("#chkBoletosAvion").prop("checked")){
			if ($("#justificaBoleto").val() =="" ) {
				Swal.fire("Boleto sin justificacion", "Se debe capturar el detalle para solicitar el boleto de avión", "error")
				return false;
			} else {
				$("#jBoleto").val($("#justificaBoleto").val());
				return true;
			}
		} else {
			$("#hasTicket").val(0);
			return true;
		}
			
	}

	function creaTableAgenda(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableAgenda = $('#tablaAgenda').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				//"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				"order": [[0, "asc"]],
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_AgendaViaticos&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nidAgenda"
				}, {
					sName : "fInicio"
				}, {
					sName : "fFin"
				},  {
					sName : "destino"
				}, {
					sName : "cMotivoComision"
				}, {
					sName : "mCuotaPorDia"
				}, {
					sName : "dias"
				}, {
					sName : "Importe"
				}, {
					sName : "nPorcentaje"
				}, {
					sName : "eliminar",
					
				}],
				oLanguage : es_mx
			});			
			
			
		$("#tablaAgenda tbody").dblclick(function(event) {

			$(oTableAgenda.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

			var aPos = oTableAgenda.fnGetPosition(event.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			let id = $("#tablaAgenda").dataTable().fnGetData()[currIndex][0];
		
			$("#idAgenda").val(id);
			$("#btnEditarAgenda").show();
			$("#btnAgregaAgenda").hide();
			
			queryFormPost({
				queryName : "llenaValoresEditar",
				async : false,
				callback : function() {
					$("#pais").val(document.getElementById("cPais").value);
					cargaEstados();
					$("#estadocombo").val(document.getElementById("estado").value);
					cargaMunicipios();
					$("#municipiocombo").val($("#nidMunicipio").val());
					if($("#cTienePaquete").val()==1){
						$("#hasPackage").val(1);
						$("#chk_paquete").prop('checked', true);
						$(".inputPaquete").css('display', 'block');
						cargaCuota();
						calculaCuotaPorDia();
					} else {
						cargaCuota();	
						$("#hasPackage").val(0);
						$("#chk_paquete").prop('checked', false);
						$(".inputPaquete").css('display', 'none');
					}
					
					if($("#cTieneHomologacion").val()==1){
						$("#hasSameRate").val(1);
						$("#chk_homologa").prop('checked', true);
						$(".inputHomologa").css('display','block');
						cargaNiveles();
					}
				}
			});

		});
	}
	
	function createTableAgendaConsulta(){
		sWhere ="nIdComision =" + $("#idComision").val();
		$('#tablaAgendaConsulta').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrieve" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				//"sScrollY" : "100%",
				"order": [[0, "asc"]],
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=v_AgendaViaticos&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nidAgenda"
				}, {
					sName : "fInicio"
				}, {
					sName : "fFin"
				}, {
					sName : "destino"
				}, {
					sName : "cMotivoComision"
				}, {
					sName : "mCuotaPorDia"
				}, {
					sName : "dias"
				}, {
					sName : "Importe"
				} , {
					sName : "nPorcentaje"
				}],
				oLanguage : es_mx
			});			
			
	}
	function creaTableTransporte(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableTransporte = $('#tablaTransporte').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				"order": [[0, "asc"]],
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vTransporteLocal&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nIdTransporte"
				}, {
					sName : "nIdTipo"
				}, {
					sName : "cOrigen"
				}, {
					sName : "mMonto"
				}, {
					sName : "mKm"
				}, {
					sName : "cNumEconomico"
				}, {
					sName : "cTieneVales"
				}, {
					sName : "eliminar",
					sClass : "alignCenter"
				}],
				oLanguage : es_mx
			});			
		
		$("#tablaTransporte tbody").dblclick(function(event) {

			$(oTableTransporte.fnSettings().aoData).each(function() {
				$(this.nTr).removeClass('row_selected');
			});
			$(event.target.parentNode).addClass('row_selected');

			var aPos = oTableTransporte.fnGetPosition(event.target.parentNode);

			if (aPos instanceof Array)
				currIndex = aPos[0];
			else
				currIndex = aPos;

			let idTipo = $("#tablaTransporte").dataTable().fnGetData()[currIndex][1];
			let origen = $("#tablaTransporte").dataTable().fnGetData()[currIndex][2];
			let monto = $("#tablaTransporte").dataTable().fnGetData()[currIndex][3];
			let km = $("#tablaTransporte").dataTable().fnGetData()[currIndex][4];
			let numEco = $("#tablaTransporte").dataTable().fnGetData()[currIndex][5];
			let tieneVales = $("#tablaTransporte").dataTable().fnGetData()[currIndex][6];
		
			monto = quitaFrmt(monto);
			
			$("#transporteCombo").val(idTipo);
			$("#descripcion").val(origen);
			$("#importeT").val(monto);
			$("#km").val(km);
			$("#nEconomico").val(numEco);
			$("#chk_tieneVales").val(tieneVales);
			
			if(document.getElementById("transporteCombo").value== 3 || document.getElementById("transporteCombo").value== 4 ){
				$("#transporteDet").show();
			} else {
				$("#transporteDet").hide();
			
			if (document.getElementById("transporteCombo").value==  4) {
				$("#lblnEconomico").attr("style", "visibility: visible");
				$("#nEconomico").attr("style", "visibility: visible");
			} else {
				$("#lblnEconomico").attr("style", "visibility: hidden");
				$("#nEconomico").attr("style", "visibility: hidden");
			}
		} 
			$("#btnEditarTrans").show();
			$("#btnAgregarTrans").hide();
			
		});	
	}
	
	function creaTableTransporteConsulta(){
		sWhere ="nIdComision =" + $("#idComision").val();
		oTableTransporteC = $('#tablaTransporteConsulta').dataTable(
			{
				"bPaginate" : false,
				"bFilter" : false,
				"bSort" : true,
				"bInfo" : false,
				"bAutoWidth" : false,
				"bJQueryUI" : true,
				"bRetrieve" : true,
				"bDestroy" : true,
				"bServerSide" : true,
				//"sScrollY" : "100%",
				"order": [[0, "asc"]],
				sAjaxSource : window.location.protocol + "//"
						+ window.location.host + "/"
						+ window.location.pathname.split("/")[1]
						+ "/crud?rt=t&ql=vTransporteLocal&qw=" + sWhere ,
				aoColumns : [ {
					sName : "nIdTransporte"
				}, {
					sName : "dTransporte"
				}, {
					sName : "cOrigen"
				}, {
					sName : "mMonto"
				}, {
					sName : "mKm"
				}, {
					sName : "cNumEconomico"
				}, {
					sName : "cTieneVales"
				}],
				oLanguage : es_mx
			});			
	}
	
	function activarBotones(){
		if (document.querySelector("#chk_homologa").checked) {
			$("#hasSameRate").val(1);
			$(".inputHomologa").css('display','block');
			cargaNiveles();
		} else {
			$("#hasSameRate").val(0);
			$(".inputHomologa").css('display','none');
			$("#nivelHomologa").val(0);
			$("#plazaHomologa").val("");
			$("#plazaJustifica").val("");
			cargaCuota();
			calculaCuotaPorDia();
		}
		
		if(document.querySelector("#chk_paquete").checked){
			cargaCuota();
			calculaCuotaPorDia();
		}
	}

	
	function validarFechas(){
		var bRegresa = true;
        fInicio = document.getElementById('fInicio').value.split("/");
        fFin  = document.getElementById('fFin').value.split("/");
	      
        fInicio = new Date( fInicio[2], fInicio[1]-1, fInicio[0], 0, 0, 0, 0 );
        fFin = new Date( fFin[2], fFin[1]-1, fFin[0], 0, 0, 0, 0);

        if(fInicio > fFin){
        	bRegresa = false;        	
        }
        
        return bRegresa;
  	}
  	
	function validarFechasExistentes(){
		var bRegresa = true;
        var fInicio = document.getElementById('fInicio').value.split("/");
        var fFin  = document.getElementById('fFin').value.split("/");
	      
	    fInicio = fInicio[2] + '-' + fInicio[1] + '-' + fInicio[0]
	    fFin = fFin[2] + '-' + fFin[1] + '-' + fFin[0]
	      
		$("#fechaInicio").val(fInicio);
		$("#fechaFin").val(fFin);
        queryFormPost("consultaOtrosFormatosInicio", {async: false});
        queryFormPost("consultaOtrosFormatosFin", {async: false});
        queryFormPost("consultaOtrosFormatosEntreFechas", {async: false});
        
        if( $("#nComisionAnterior").val() > 0 || $("#nComisionAnteriorF").val() > 0 || $("#nComisionAnterior2").val() > 0) {
		 	bRegresa = false;
		}
        
        return bRegresa;
  }
  
  function validarFechasIncidencias(){
		var bRegresa = true;
        var fInicio = document.getElementById('fInicio').value.split("/");
        var fFin  = document.getElementById('fFin').value.split("/");
	      
	    fInicio = fInicio[2] + '-' + fInicio[1] + '-' + fInicio[0]
	    fFin = fFin[2] + '-' + fFin[1] + '-' + fFin[0]
	      
		$("#fechaInicio").val(fInicio);
		$("#fechaFin").val(fFin);
        queryFormPost("consultaInicidenciaInicio", {async: false});
        queryFormPost("consultaInicidenciaFin", {async: false});
        
        if( $("#tieneInicidencia").val() > 0 || $("#tieneInicidenciaF").val() > 0) {
		 	bRegresa = false;
		}
        
        return bRegresa;
  }

  	function activarPaquete(){
  		if(document.querySelector("#chk_paquete").checked) {
			$("#hasPackage").val(1);
  			$(".inputPaquete").css('display', 'block');
  		} else {
  			$("#hasPackage").val(0);
  			$("#paqueteCombo").val(0);
  			$("#nPorcentaje").val(1);
  			$(".inputPaquete").css('display', 'none');
  			cargaCuota();
			calculaCuotaPorDia();
  		}
  		
  		if(document.querySelector("#chk_paquete").checked){
			cargaCuota();
			calculaCuotaPorDia();
		}
  	}

	
  	function cargaPaquetes(){
  		$("#paquetes").val(document.getElementById("paqueteCombo").value);
		querySelectPost("cat_TiposPaqueteRead", "paqueteCombo", {async:false});
		queryFormPost("cat_FiltroPaquetes", {async: false});
		
		cargaCuota();
		calculaCuotaPorDia();
		
  	}
  	
  	function calculaCuotaPorDia() {
		if($("#nPorcentaje").val() == 0 ) {
			$("#cCuota").val(0);
		} else if($("#nPorcentaje").val() != "") {
			let cuota = parseFloat(quitaFrmt($("#cCuota").val()));
			let porcentaje = parseFloat($("#nPorcentaje").val());
			let cuotaPorDia = ( cuota * porcentaje) ;
			$("#cCuota").val(cuotaPorDia);
		}
	}
	
	function cat_beneficiario(){
		$("#cTipoRfc").val("3");
		window.open('CatalogoBeneficiarios.jsp','Beneficiarios', 'status=1, width=900px, height=430px, left=100px, resizable=yes');
		$("#fInicio").val("");
	}
	
	function cargaCtaBancariaRFC(){
		$("#campoRFC").val($("#cIdRFC").val());
		querySelectPost("cargaCtaBancariasRFC", "ctaBancaria",{async: false });
		
	}
	
	function leerTipoMoneda() {
		queryFormPost("leerTipoMoneda", {async: false});
		
		if($("#cMoneda").val() ==1) {
			cargaCuota();
		}	
	}
	
	function agregarEmp(){
				let tblResumenRef = document.getElementById("tablaEmpleados");
				let newRowRef = tblResumenRef.insertRow(-1);
				
				let newCellRef = newRowRef.insertCell(0);
				newCellRef.textContent = document.getElementById("nEmpleado").value;
				
				newCellRef = newRowRef.insertCell(1);
				newCellRef.textContent = document.getElementById("cnombre").value;
				
				newCellRef = newRowRef.insertCell(2);
				newCellRef.textContent = document.getElementById("ctaBancaria").value;
				
				newCellRef = newRowRef.insertCell(3);
				let deleteButton= document.createElement("button");
				deleteButton.textContent="Eliminar";
				deleteButton.classList.add("btn");
				deleteButton.classList.add("btn-secondary");
				newCellRef.appendChild(deleteButton);

				deleteButton.addEventListener("click", (event) =>{
					event.target.parentNode.parentNode.remove();
				})
				
				document.getElementById("ctaBancaria").value='Seleccione cuenta bancaria';

		}	
	
	function validaBotones(){
		if($("#radio1").is(':checked')){
			$("#divTablaEmpleados").hide();
			$("#divDatosEmpleado").show();
			$("#btnAgregarEmp").attr("style", "visibility: hidden");
		} else {
			$("#divTablaEmpleados").show();
			$("#divDatosEmpleado").hide();
			$("#btnAgregarEmp").attr("style", "visibility: visible");
		}
		 
	}
	
	function validaTransporte(){
		if(document.getElementById("transporteCombo").value== 1){
			$("#transporteDet").hide();
			$("#descripcion").val("CASA - AEROPUERTO - CASA");
		} else if(document.getElementById("transporteCombo").value==2 || document.getElementById("transporteCombo").value==5 || document.getElementById("transporteCombo").value==6 ) {
			$("#transporteDet").hide();
			$("#descripcion").val("");
		} else {
			$("#transporteDet").show();
			$("#descripcion").val("");
			if (document.getElementById("transporteCombo").value==  4) {
				$("#lblnEconomico").attr("style", "visibility: visible");
				$("#nEconomico").attr("style", "visibility: visible");
			} else {
				$("#lblnEconomico").attr("style", "visibility: hidden");
				$("#nEconomico").attr("style", "visibility: hidden");
			}
		} 
			 
	}
	
	function validarDatosTransporte(){
		$("#idTransporte").val($("#transporteCombo").val());
		
		if ($("#idTransporte").val() == 0 ) {
			Swal.fire("Capture tipo Transporte","Debe seleccionar el tipo de transporte para continuar.", "warning");
				return false;
		} else {
		
				if (document.querySelector("#chk_tieneVales").checked) {
					$("#tieneVales").val(1);
					//Si tiene vales el importe es cero
					$("#importeT").val(0);
				} else {
					$("#tieneVales").val(0);
					//Si no tiene vales tiene que tener importe
					if($("#importeT").val() =="" || $("#importeT").val() =="0"){
						Swal.fire("Capturar importe","El importe del transporte NO puede ser cero, sino quiere agregar transporte únicamente presione el botón Avanzar", "warning");
						return false;
					}
				}
				
				if($("#km").val()=="")
					$("#km").val(0);
				
				if($("#nEconomico").val()=="0")
					$("#nEconomico").val()=="";
				
				if(($("#transporteCombo").val()==3 || $("#transporteCombo").val()==4) && $("#km").val() == 0 ) {
					Swal.fire("Captura KM","Debe capturar el Kilometraje del vehiculo para continuar","warning");
					
				} else if ($("#transporteCombo").val()==4 && $("#nEconomico").val()=="" ){
					Swal.fire("Captura No. Económico","Debe capturar el Número Económico del Vehiculo para continuar","warning");
				}
			
		}
		return true;
	}
	
	function limpiaTransporte(){
		$("#transporteCombo").val(0);
		$("#descripcion").val("");
		$("#km").val("");
		$("#importeT").val(0);
		$("#nEconomico").val()=="";
		$("#chk_tieneVales").prop("checked", false);
	}
	
	function agregarTransporte(){
		var guardado = false;
		var logErrores = "";
		
		if (validarDatosTransporte()) {
				$.ajax({
					url : "../viaticos/guardarTransporte",
					type : 'post',
					async : false,
					data : $("#formViaticos").serialize(),
					dataType : 'json',
					success : function(j) {
						var exito = j.success;
			
						if (exito) {
							guardado = true;
							creaTableTransporte();
						} else {
							var errores = j.errorList;
							var cnt = 0;
							for (cnt = 0; cnt < errores.length; cnt++) {
								logErrores = logErrores + errores[cnt] + "\n";
							}
						}
					},
					error : function(errorThrown) {
						logErrores =  errorThrown.ERROR;
					}
				});
			
				if (!guardado)
					Swal.fire("Error guardando transporte", logErrores, "error")
		}	
	}
	
function cmdImprimir() {
	
	window.open(
		"../admin/SeguridadCatalogos?"
		+ "catalogo=CONTRARECIBO"
		+ "&accion=run"
		+ "&rn=PolizaViaticos_FIEL.jasper"
		+ "&whereFolio=" + $("#idComision").val(),
		"popacuse",
		"scrollbars=1, resizable=yes, width=1024, height=768");

}

function limpiarTransporte() {
	$("#transporteCombo").val('');
	$("#descripcion").val('');
	$("#importeT").val(0);
	$("#km").val('');
	$("#nEconomico").val('');
	$("#chk_tieneVales").val(0);	
}

function limpiar() {
	$("#fInicio").val('');
	$("#fFin").val('');
	$("#estadocombo").val('');
	$("#municipiocombo").val('');
	$("#nombre_estado").val('');
	$("#nombre_municipio").val('');
	$("#cLocalidad").val('');
	$("#cConcepto").val('');
	$("#idNombre").val(0);
	$("#chk_homologa").val(0);
	$("#nivelHomologa").val('');
	$("#plazaHomologa").val('');
	$("#plazaJustifica").val('');
	$("#chk_paquete").val(0);	
	$("#paqueteCombo").val(0);
	$("#nPorcentaje").val(0);
	$("#hasTicket").val(0);
	
	if ($("#operacion").val()=="EDITAR") {
			$("#nidAgenda").val('');
		}
	
	cargaCuota();

}

function consultaDatos() {
		$("#fInicio").val('');
		$("#fFin").val('');
		queryFormPost("consultaDatosEmpleado", {async: false});
		
		if ($("#cnombre").val()=="") {
			Swal.fire("RFC Invalido","Favor de buscar nuevamente el RFC","error");
			$("#cIdRFC").val("");
		}
}
