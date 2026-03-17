function onLoadPlantilla(id_oper) {//Carga Plantilla
	$.blockUI({message: "Procesando espere ......"});
	idOper=id_oper;
	if(id_oper>1 && id_oper<5 ){
		parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
	}
	var resp=true;
	
	switch (parseInt(id_oper,10)){
		case 1://Captura
			ocultaCampos();
			consultaInfo(id_oper);
			hideAndShowXtipePerson();
			chechExtranjero();
			esExtranjero();
			break;
		case 2://Captura Cuenta Bancaria
			parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
			consultaInfo(id_oper);
			hideAndShowXtipePerson();
			chechExtranjero();
			esExtranjero();
			cargaDataTable();
			ocultaCampos();
			if($("#bEsActCta").val() == 1){
				actualizaCuentasBancarias=true;
				soloLectura();

				if($("#idRegimenFiscal").val() == 0){
					$("#idRegimenFiscal").attr("disabled",false);
					
				}

				if($("#nIdOperAnt").val()==5 && $("#bLiberaCaso").val()==1){
					$("#botonLibera").show();
				}else{
					$("#botonLibera").hide();
				}
				habilitaInputsRequeridos();
			}
			if("admin"==$("#cIdUsuarioLogeado").val()){
				parent.window.document.getElementById("pb_cancel").style.visibility = "visible";
			}
			if($("#cDocumentoHaplicado").val() == 'R'){
				swal("Devolución de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val(),{icon:"info",closeOnClickOutside: false,closeOnClickOutside: false,button: "Cerrar"});
				$("#btnObservaciones").show();
				rechazoAutorizacion =true;
			}else if(($("#cDocumentoHaplicado").val() == 'D') || $("#cDocumentoHaplicado").val() == 'I'){
				swal("Devolución de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val(),{icon:"info",closeOnClickOutside: false,closeOnClickOutside: false,button: "Cerrar"});
				$("#btnObservaciones").show();
				$("#cDocumentoHaplicado").val("D");
			}else{
				if("PROVEEDOR"!=$("#tipoPB").val()){
					$("#cDocumentoHaplicado").val("V");
				}
				if("admin"==$("#cIdUsuarioLogeado").val() && $("#nIdOperAnt").val()==1){
					parent.window.document.getElementById("pb_cancel").style.visibility = "visible";
				}
			}			
			break;
		case 3://validacion
			document.getElementById("autorizaSi").checked = false;
			document.getElementById("autorizaNo").checked = false;
			consultaInfo(id_oper);
			hideAndShowXtipePerson();
			chechExtranjero();
			esExtranjero();
			cargaDataTable();
			nameEncabezado(id_oper);
			$("#fieldAutoriza").show();
			$("#botonExtrae").show();
			if($("#cDocumentoHaplicado").val() == 'R'|| $("#cDocumentoHaplicado").val() == 'D' || $("#cDocumentoHaplicado").val() == 'I'){
				swal("Devolucion de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val(),{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				$("#btnObservaciones").show();
			}else if($("#cDocumentoHaplicado").val() == 'P'){
				swal("MODIFICACION DE TIPO PERSONA:"+$("#cObservaciones").val(),{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				$("#btnObservaciones").show();
			}
			break;
		case 4://Autorizacion
			document.getElementById("autorizaSi").checked = false;
			document.getElementById("autorizaNo").checked = false;			
			$("#trCBEN").show();
			consultaInfo(id_oper);
			hideAndShowXtipePerson();
			chechExtranjero();
			esExtranjero();
			cargaDataTable();
			nameEncabezado(id_oper);
			$("#fieldAutoriza").show();
			$("#botonExtrae").show();
			if($("#cDocumentoHaplicado").val() == 'R'|| $("#cDocumentoHaplicado").val() == 'D'){
				swal("Devolucion de Alta de Proveedor\nObservaciones: "+$("#cObservaciones").val(),{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				$("#btnObservaciones").show();
			}else if($("#cDocumentoHaplicado").val() == 'P'){
				swal("MODIFICACION DE TIPO PERSONA:"+$("#cObservaciones").val(),{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				$("#btnObservaciones").show();
			}
			break;
		case 5://Consulta
			$("#trCBEN").show();
			nameEncabezado(id_oper);
			consultaInfo(id_oper);
			hideAndShowXtipePerson();
			chechExtranjero();
			esExtranjero();
			cargaDataTable();
			$("#botonExtrae").show();
			break;
		case 6://Modifica
			parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
			consultaInfo(id_oper);
			hideAndShowXtipePerson();
			chechExtranjero();
			esExtranjero();
			readOnlyRFC();
			nameEncabezado(id_oper);
			if($("#nIdOperAnt").val()==5 && $("#bLiberaCaso").val()==1){
				$("#botonLibera").show();
			}else{
				$("#botonLibera").hide();
			}
			actualizacion=true;
			$("#cActualizacion").val("1");
			$("#cDocumentoHaplicado").val("P");
			break;
		default:
			resp=false;
			break;
	}
	$.unblockUI();
	return resp;
}
function onSubmit(id_oper) {//clic boton Guardar
	var resp=true;
	
	$.blockUI({message: "Procesando espere ......"});
	switch (parseInt(id_oper,10)){
		case 1://Captura
			var p = window.parent;
			if($("#cIdRFC1").val()==""){
				swal("El RFC no puede ir vacío.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				resp=false;
			}
			if(resp &&!validaHomoclave()){
				resp= false;
			}
			if(resp && guardaDatos(id_oper,3)){
				try{
					p.gestion.setFolio($("#cFolio").val());
					p.gestion.setOperador($("#OPERADOR").val());
					p.gestion.setFechaDocumento($("#FECHA_DOCUMENTO").val()); 
					p.gestion.setEjercicioFiscal($("#cEjercicio").val());
					if(consultaDoctos(id_oper)){
						parent.document.getElementById("pb_send").disabled = false;
					}
					resp=true;
				} catch (e) {
					swal("Error: "+ e.message,{icon:"error",closeOnClickOutside: false,button: "Cerrar"});
                	parent.document.getElementById("pb_send").disabled = true;
                	resp=false;
				}finally{
					$.unblockUI();
				}
			}else{
				parent.document.getElementById("pb_send").disabled = true;
				resp=false;
			}
			break;
		case 2://Captura Cuenta Bancaria
			if($("#cIdRFC1").val()==""){
				swal("El RFC no puede ir vacío.",{icon:"warm",closeOnClickOutside: false,button: "Cerrar"});
				resp=false;
			}
			if(resp && !validaHomoclave()){
				resp=false;
			}
			if(resp && guardaDatos(id_oper,3)){
				$("#botonLibera").hide();
				parent.document.getElementById("pb_send").disabled = false;
				resp=true;
			}else{
				resp=false;
			}
			break;
		case 3://validacion
			if(document.getElementById("autorizaNo").checked){
				if($("#cDocumentoHaplicado").val()!='P'){
					$("#cDocumentoHaplicado").val("I");
				}
				document.getElementById("cObservaciones").readOnly = true;
				document.getElementById("cObservaciones").className = "notEditable";
				devolucion=true;
			}else if(document.getElementById("autorizaSi").checked){
				if($("#cDocumentoHaplicado").val()!="R" && $("#cDocumentoHaplicado").val()!="D" && $("#cDocumentoHaplicado").val()!="P")
					$("#cDocumentoHaplicado").val("V");
			}else{
				swal("Debe seleccionar una opci\u00f3n de Autorizaci\u00f3n.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				resp=false;
			}
			if(resp){
				parent.document.getElementById("pb_send").disabled = false;
				document.getElementById("autorizaSi").disabled = true;
				document.getElementById("autorizaNo").disabled = true;
			}
			
			break;
		case 4://Autorizacion
			document.getElementById("autorizaSi").disabled = true;
			document.getElementById("autorizaNo").disabled = true;
			parent.document.getElementById("pb_send").disabled = false;
			if(document.getElementById("autorizaNo").checked){
				if($("#cDocumentoHaplicado").val()!="P"){
					$("#cDocumentoHaplicado").val("R");
				}
				devolucion=true;
			}else if(document.getElementById("autorizaSi").checked){
				$("#cDocumentoHaplicado").val("S");
			}else{
				swal("Debe seleccionar una opci\u00f3n de Autorizaci\u00f3n.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				document.getElementById("autorizaSi").disabled = false;
				document.getElementById("autorizaNo").disabled = false;
				parent.document.getElementById("pb_send").disabled = true;
				resp=false;
			}
			break;
		case 5://Consulta
			break;
		case 6://Modifica
			var p = window.parent;
			if(resp && !validaHomoclave()){
				resp=false;
			}
			if(resp && $("#nIdOperAnt").val()==5 && $("#bLiberaCaso").val()==1){
				swal({
					title: "",
					text: "Una vez guardado los datos no se podra liberar el tramite, segur@ que quieres modificar el tramite",
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
					},
				}).then((continuar) => {
					if (!continuar) {
						resp=false;
					}else{
						resp=true;
					}
				});
			}
			if(resp && guardaDatos(id_oper,9)){
				try{
					p.gestion.setFolio($("#cFolio").val());
					p.gestion.setOperador($("#OPERADOR").val());
					p.gestion.setFechaDocumento($("#FECHA_DOCUMENTO").val()); 
					p.gestion.setEjercicioFiscal($("#cEjercicio").val());
					$("#botonLibera").hide();
					parent.document.getElementById("pb_send").disabled = false;
				} catch (e) {
					swal("onSubmit: Error: " + e.message,{icon:"error",closeOnClickOutside: false,button: "Cerrar"});
                	parent.document.getElementById("pb_send").disabled = true;
                	resp= false;
				}
			}else{
				resp=false;
			}
			break;
		default:
			resp=false;
			break;
	}
	$.unblockUI();
	return resp;
}// fin onSubmit
function ResponsableSiguiente(id_oper) {
	var respSiguiente="";
	switch (parseInt(id_oper,10)){
	case 1:// Captura
		respSiguiente= "CAPTURA_PROVEEDOR";
		break;
	case 2:// Captura Cuenta Bancaria
		if(actualizaCuentasBancarias){
			if(sincambios)
				respSiguiente= "CONSULTA_PROVEEDOR";
			else
				respSiguiente= "AUTORIZA_PROVEEDOR";
		}else{
			if("PROVEEDOR"==$("#tipoPB").val()){
				respSiguiente= "VALIDA_PROVEEDOR";
			}else{
				respSiguiente= "AUTORIZA_PROVEEDOR";
			}
		}
		break;
	case 3:// Validacion
		if(devolucion){
			respSiguiente= "CAPTURA_PROVEEDOR";
		}else{
			respSiguiente= "AUTORIZA_PROVEEDOR";	
		}
		break;
	case 4:// Autorizacion
		if(devolucion){
			if($("#cDocumentoHaplicado").val()=="P"){
				respSiguiente= "MODIFICA_PROVEEDOR";
			}else{
				respSiguiente= "CAPTURA_PROVEEDOR";
			}
		}else
			respSiguiente= "CONSULTA_PROVEEDOR";
		break;
	case 5: //Consulta
		respSiguiente= "CONSULTA_PROVEEDOR";
		break;
	case 6://mofidicaciones
		if($("#cDocumentoHaplicado").val()=='P'){
			if("PROVEEDOR"==$("#tipoPB").val()){
				respSiguiente= "VALIDA_PROVEEDOR";
			}else{
				respSiguiente= "AUTORIZA_PROVEEDOR";
			}
		}else
			respSiguiente= "CONSULTA_PROVEEDOR";
		break;
	default:
		respSiguiente= "CONSULTA_PROVEEDOR";
		break;
	}// fin switch
	return respSiguiente;
}// fin ResponsableSiguiente

function OperacionSiguiente(id_oper) {
	var operSiguiente="";
	switch (parseInt(id_oper,10)){
	case 1://Captura
		operSiguiente= "captura_cuentabancaria_proveedor";
		break;
	case 2: //Captura Cuenta Bancaria
		if(actualizaCuentasBancarias){
			if(sincambios)
				operSiguiente= "consulta_proveedor";
			else
				operSiguiente= "autoriza_proveedor";
			
		}else{
			if("PROVEEDOR"==$("#tipoPB").val()){
				operSiguiente= "valida_proveedor";
			}else{
				operSiguiente= "autoriza_proveedor";
			}
		}
		break;
	case 3://Validacion
		if(devolucion){
			if($("#cDocumentoHaplicado").val()=='P'){
				operSiguiente= "modifica_proveedor";
			}else{
				operSiguiente= "captura_cuentabancaria_proveedor";
			}
		}else
			operSiguiente= "autoriza_proveedor";
		break;
	case 4://Autorizacion
		if(devolucion){
			if($("#cDocumentoHaplicado").val()=='P'){
				operSiguiente= "modifica_proveedor";
			}else{
				operSiguiente= "captura_cuentabancaria_proveedor";
			}
		}else
			operSiguiente= "consulta_proveedor";
		break;
	case 5://consulta
		operSiguiente= "consulta_proveedor";
		break;
	case 6://modificacion
		if($("#cDocumentoHaplicado").val()=='P'){
			if("PROVEEDOR"==$("#tipoPB").val()){
				operSiguiente= "valida_proveedor";
			}else{
				operSiguiente= "autoriza_proveedor";
			}
		}else{
			operSiguiente= "consulta_proveedor";
		}
		break;
	default:
		operSiguiente= "consulta_proveedor";
		break;
	}// fin switch
	return operSiguiente;
}// fin OperacionSiguiente
function onPostDisplay(id_oper) {
		return true;
}// fin onPostDisplay
function onPostSubmit(id_oper) {// clic boton enviar
	//guardar ultimo movimiento
	$.blockUI({message: "Procesando espere ......"});
	var resp=true;
	parent.window.document.getElementById("pb_save").style.visibility = "hidden";
	parent.window.document.getElementById("pb_send").style.visibility = "hidden";
	switch (parseInt(id_oper,10)){
		case 1://Captura
			parent.window.document.getElementById("pb_cancel").style.visibility = "hidden";
			if(consultaDoctos(id_oper)){
				resp=false;
				if(confirm("Esta seguro de registrar el RFC: "+$("#cIdRFC").val()) ){//confirm("Esta seguro de registrar el RFC: "+$("#cIdRFC").val())
					resp=validarDatos(id_oper,10);
	    		}else{
					resp= false;
					parent.window.document.getElementById("pb_save").style.visibility = "visible";
					parent.window.document.getElementById("pb_send").style.visibility = "visible";
					parent.window.document.getElementById("pb_cancel").style.visibility = "visible";
				}
			}
			
			break;
		case 2://Captura Cuenta Bancaria
			resp=validarDatos(id_oper,5);
			break;
		case 3://validacion
			if(devolucion){
				resp=validarDatos(id_oper,10);
			}else{
				resp=validarDatos(id_oper,7);
				if(!resp){
					parent.window.document.getElementById("pb_save").style.visibility = "visible";
					parent.window.document.getElementById("pb_send").style.visibility = "visible";
				}
			}
			break;
		case 4://Autorizacion
			if(devolucion){
				swal("El documento regresa a captura para ser modificado",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
			}
			resp=validarDatos(id_oper,8);
			if(!resp){
				parent.window.document.getElementById("pb_save").style.visibility = "visible";
				parent.window.document.getElementById("pb_send").style.visibility = "visible";
			}
			break;
		case 5://Consulta
			resp=true;
			break;
		case 6://Modifica
			resp=consultaDoctos(id_oper);
			if(resp && (confirm("Esta seguro de enviar a validaci\u00f3n la modificac\u00f3on del RFC: "+$("#cIdRFC").val()))){
				if("PROVEEDOR"==$("#tipoPB").val()){
					resp=validarDatos(id_oper,10);
				}else{
					resp=validarDatos(id_oper,7);
				}
    		}else{
    			parent.window.document.getElementById("pb_save").style.visibility = "visible";
				parent.window.document.getElementById("pb_send").style.visibility = "visible";
				resp= false;
			}
			break;
		default:
			resp=false;
			break;
	}
	$.unblockUI();
	return resp;
}// fin  onPostSubmit
//-------------Funciones de Caso Fin --------------------------------------------------------------------------------------------------------
function ocultaCampos(){
	$("#btnObservaciones").hide();
}
function validarDatos(id_oper,option){
	var resp=false;
	var object=llenaObjectDat(option,id_oper);
	$.ajax({url: "../servlet/AltaProveedoresServlet" , type:'post' , async: false
		,data:object
		,beforeSend : function() {
			$.blockUI({ message: 'Procesando Solicitud...' });
         }
        ,complete: function () {
			$.unblockUI();
        }
		,dataType: 'json', success: 
			function(j){
				var mensaje=j[0].MENSAJE;
				resp=j[0].RESPUESTA;
				swal({
					title: "",
					text: mensaje,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Aceptar"
						},
					}).then((continuar) => {
						return resp;
					});
		}, error: function( jqXHR, textStatus, errorThrown ) {
			parent.window.document.getElementById("pb_save").style.visibility = "visible";
			parent.window.document.getElementById("pb_send").style.visibility = "visible";
		}
	});
	return resp;
}
function consultaInfo(id_oper){
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat(1,id_oper);
	$.ajax({url: "../servlet/AltaProveedoresServlet" , type:'post' , async: false
		,data:object
		,beforeSend : function() {
			$.blockUI({ message: 'Consultando Solicitud...' });
         }
        ,complete: function () {
			$.unblockUI();
        }
		,dataType: 'json', success: 
			function(j){
				llenaCombo(j[0].catEntidadFederativa,"cEstadoFiscal");
				llenaCombo(j[0].catPyme,"nIdPyme");
				llenaCombo(j[0].catTipoPersona,"cIdTipoPersonaRFC");
				llenaCombo(j[0].catMunicipio,"cMunicipioFiscal");
				llenaCombo(j[0].catTipoTelefono,"cTipoTelefono");
				llenaCombo(j[0].idRegimenFiscal,"idRegimenFiscal");
				llenaCombo(j[0].catLocalidad,"cLocalidadFiscal");
				
				vaciarJsonAInputs(j[0].datGuardados);
				vaciarJsonAInputs(j[0].EFOS);
				
				if(2==id_oper){
					llenaCombo(j[0].catBancos,"cbBanco");
					if(j[0].HAYCUENTAS){
						sincambios=false;
					}
				}
				if(4==id_oper || 5==id_oper){
					vaciarJsonAInputs(j[0].datGuardadostBeneficiario);
				}
				$.unblockUI();
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$.unblockUI();
		}
	});
}
function llenaObjectDat(operacion,id_oper){
	var isExt=0;
	var observ="";
	var aut=false;
	var dataCtaBancaria;
	
	if($("#chk_extranjero").prop("checked")){
		isExt=1;
	}
	if($("#cIdTipoPersonaRFC").val()==3){
		$("#tipoPB").val("BENEFICIARIO");
	}
	if(id_oper>2 && id_oper!=6 ){
		observ=$("#cObservaciones").val();
		if(document.getElementById("autorizaSi").checked){
			aut=true;
		}
	}
	var data0= {
			nIdCaso:$("#idCaso").val(),
			cFolio: $("#cFolio").val(),
			cApellidoMat:$("#cApellidoMaterno").val(),
			cApellidoPat:$("#cApellidoPaterno").val(),
			cCalle:$("#cCalle").val(),
			cCodigoPost:$("#cCodigoPostal").val(),
			cColonia:$("#cColonia").val(),
			cCurp:$("#cCURP").val(),
			cEmail:$("#cEmail").val(),
			cGiro:$("#cGiro").val(),
			cNombre:$("#cNombre").val(),
			cNumeroExt:$("#cNumeroExterno").val(),
			cNumeroInt:$("#cNumeroInterno").val(),
			cPaginaWeb:$("#cUrl").val(),
			cPais:$("#cPais").val(),
			cRazonSocial:$("#cRazonSocial").val(),
			cTelefono:$("#cTelefono").val(),
			nTipoTelefono:$("#cTipoTelefono").val(),
			cTipoPB:$("#tipoPB").val(),
			isExtranjero:isExt,
			nEntidadFederativa:$("#cEstadoFiscal").val(),
			nMunicipio:$("#cMunicipioFiscal").val(),
			cLocalidad:$("#cLocalidadFiscal").val(),
			nNumEmpleado:$("#NEmp").val(),
			nPyme:$("#nIdPyme").val(),
			nTipoPersona:$("#cIdTipoPersonaRFC").val(),
			cIdRFC:$("#cIdRFC1").val()+"-"+$("#cIdRFC2").val()+"-"+$("#cIdRFC3").val(),
			cIdRFC1:$("#cIdRFC1").val(),
			cIdRFC2:$("#cIdRFC2").val(),
			cIdRFC3:$("#cIdRFC3").val(),
			cDocumentoHaplicado:$("#cDocumentoHaplicado").val(),
			operacion:operacion,
			observaciones:observ,
			autoriza:aut,
			cTituloAplicacion:$("#flujo").val(),
			nIdOper:id_oper,
			idRegimen:$("#idRegimenFiscal").val(),
			numRepse: $("#cNumeroREPSE").val()
			
		};
	if(id_oper==2 && operacion==12){
		data0.cBanco = $("#dBancoH2").val();
		data0.cClabeInterbancaria="";
		data0.cCuentaBancaria=$("#dCuentaBancariaH").val();
		data0.cNameBanco=$("#cBancoH").val();
		data0.cPlaza=$("#cPlazaH").val();
		data0.nBCBEnviadoSICOP=0;
		data0.nDigitoVerificador=$("#dDigitoVerificadorH").val();
		data0.nEstatusCta=("Inactivo"==$("#cStatus").val())?0:1;
		data0.cEstatusCta=$("#cStatus").val();
		data0.cSucursal=$("#dSucursalH").val();
		data0.cMotivoEliminaCta=$("#cMotivoEliminaCta").val();
		
	}
	return data0;
}
function chechExtranjero(){
	if($("#cExtranjero").val()==1){
		document.getElementById("chk_extranjero").checked = true;
	}else{
		document.getElementById("chk_extranjero").checked = false;
	}
}
function actualizaMunicipio() {
	querySelectPost("MunicipiosRead", "cMunicipioFiscal", {async : false,});
	$('#cLocalidadFiscal').empty();
}
function actualizaLocalidad() {
	if(parseInt($("#cEstadoFiscal").val(),10)<=32){
		querySelectPost("LocalidadesRead", "cLocalidadFiscal", {async : false});
	}else{
		$('#cLocalidadFiscal').empty();
		var $select = $('#cLocalidadFiscal');
		if(parseInt($("#cEstadoFiscal").val(),10)==34){
			$select.append($('<option />', { value: '000000', text: 'SIN DISTRIBUCIÓN GEOGRAFICA' }));
		}else{
			$select.append($('<option />', { value: '000000', text: 'EN EL EXTRANJERO' }));	
		}
	}
	
}
function cargaRegimenFiscal(tipopersona, idRegimen){
	
	$('#idRegimenFiscal').empty();
	
	let queryName = "regimenFiscalPFRead";
	if(tipopersona==1)
		queryName = "regimenFiscalPMRead";
	
	querySelectPost({
		queryName:queryName, 
		targetObjectId: "idRegimenFiscal",
		async : false,
		callback:function(){
			if( idRegimen )
				$("#idRegimenFiscal").val(idRegimenFiscal);
		}
	});
}



function hideAndShowXtipePerson() {//muestra opciones segun sea el tipo de persona
	var tipopersona = $("#cIdTipoPersonaRFC").val();
	$("#trNoEmpleado").hide();
	$("#pMoralRepresentante").hide();
	$("#pMoralRazon").hide();
	$("#lbl_provExtra").show();
	$("#chk_extranjero").show();
	$("#divCheckExtramjero").show();
	
	
	if(tipopersona==1){//Persona moral
		
		document.getElementById('cIdRFC1').maxLength = 3;
		$("#trCurp").hide();
		$("#pMoralRepresentante").show();
		$("#tipoPB").show();
		$("#trGiro").show();
		$("#trPyme").show();
		$("#pMoralRazon").show();
		$("#cCURP").val('');
		document.getElementById("labelCalle").style.visibility = "visible";
		document.getElementById("labelNumero").style.visibility = "visible";
		document.getElementById("labelColonia").style.visibility = "visible";
		document.getElementById("labelCP").style.visibility = "visible";
		document.getElementById("labelCorreo").style.visibility = "visible";
		document.getElementById("labelTel").style.visibility = "visible";
	}else{//Persona fisica y empleado
		document.getElementById('cIdRFC1').maxLength = 4;
		$("#trCurp").show();
		if(tipopersona==3){//solo Empleado CNF
			$("#tipoPB").hide();
			$("#trPyme").hide();
			$("#trdescProv").hide();
		 	$("#trdescBen").show();
			$("#cRazonSocial").val('');	
			$("#lbl_provExtra").hide();
			$("#chk_extranjero").hide();
			$("#divCheckExtramjero").hide();
			document.getElementById("labelCalle").style.visibility = "hidden";
			document.getElementById("labelNumero").style.visibility = "hidden";
			document.getElementById("labelColonia").style.visibility = "hidden";
			document.getElementById("labelCP").style.visibility = "hidden";
			document.getElementById("labelCorreo").style.visibility = "hidden";
			document.getElementById("labelTel").style.visibility = "hidden";
			document.getElementById("labelPaginaWeb").style.visibility = "hidden";
			$("#trGiro").hide();
			$("#trNoEmpleado").show();			
		}else{
			$("#tipoPB").show();
			$("#trGiro").show();
			$("#trPyme").show();
			document.getElementById("labelCalle").style.visibility = "visible";
			document.getElementById("labelNumero").style.visibility = "visible";
			document.getElementById("labelColonia").style.visibility = "visible";
			document.getElementById("labelCP").style.visibility = "visible";
			document.getElementById("labelCorreo").style.visibility = "visible";
			document.getElementById("labelTel").style.visibility = "visible";
		}
	}
}
function hideAndShowXtipePerson2() {//muestra opciones segun sea el tipo de persona
	var tipopersona = $("#cIdTipoPersonaRFC").val();
	/*
	if(idOper!=6){
		$("#cIdRFC1").val("");
		$("#cIdRFC2").val("");
		$("#cIdRFC3").val("");
	}
	*/
	if(tipopersona==3){//solo Empleado CNF
		$("#nIdPyme").val(1);
		$("#cGiro").val("EMPLEADO CONAFOR");
	}else{
		$("#cGiro").val("EMPLEADO CONAFOR");
	}
	hideAndShowXtipePerson();
}
function Change(elem,evt) {//Pasa al siguiente campo al escribir
	var rfc = elem.name;
	if(idOper>1){
		swal("No se puede modificar el RFC.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
		return;
	}
	if (elem.value.length == elem.maxLength) {
		if (rfc == "cIdRFC1") {
			if(onlyNumbers(evt))
				$("#cIdRFC2").select();
		} else
			$("#cIdRFC3").select();
	}
}
function onlyNumbersAndLetters(evt, elem) {
	var keyPressed = (evt.which) ? evt.which : event.keyCode;
	var strCheck = "!@#$%^&*()´+=-[]\\';,./{}|\":<>?";
	var key = String.fromCharCode(keyPressed);
	if (strCheck.indexOf(key) == -1) {
		//fue ingresado un caracter valido
		if (elem.name == "cIdRFC2" || elem.name == "NEmp") {//Solo numeros para esta parte del RFC
			strCheck = '0123456789';
			if (strCheck.indexOf(key) == -1)
				return false;
			else
				return true;
		} else
			return true;
	}
	return false;
}
function classErrorRFC(){
	$("#cIdRFC1").addClass( "ui-state-error" );
	$("#cIdRFC2").addClass( "ui-state-error" );
	$("#cIdRFC3").addClass( "ui-state-error" );
}
function removeClassErrorRFC(){
	$("#cIdRFC1").removeClass( "ui-state-error" );
	$("#cIdRFC2").removeClass( "ui-state-error" );
	$("#cIdRFC3").removeClass( "ui-state-error" );
}
function readOnlyAndClassRFC(){
	document.getElementById("cIdTipoPersonaRFC").disabled = true;
	document.getElementById("idRegimenFiscal").disabled = true;

	document.getElementById("cNumeroREPSE").readOnly = true;
	document.getElementById("cNumeroREPSE").className = "notEditable";
	
	document.getElementById("tipoPB").disabled = true;
	document.getElementById("cIdRFC1").readOnly = true;
	document.getElementById("cIdRFC1").className = "notEditable";
	document.getElementById("cIdRFC2").readOnly = true;
	document.getElementById("cIdRFC2").className = "notEditable";
	document.getElementById("cIdRFC3").readOnly = true;
	document.getElementById("cIdRFC3").className = "notEditable";
}
function existeProveedor() {//valida en mCatalogoProveedor
	var resp=false;
	if(idOper==6){ 
		return;
	}
	parent.document.getElementById("pb_save").disabled = false;
	$("#cIdRFC").val($("#cIdRFC1").val() + "-" + $("#cIdRFC2").val() + "-"+ $("#cIdRFC3").val());
	if($("#cActualizacion").val()!=1){
		var object=llenaObjectDat(2,idOper);
		$("#existeFolio").val('');
		$("#regreso").val('');
		$("#regresoBenef").val('');
		$.ajax({url: "../servlet/AltaProveedoresServlet" , type:'post' , async: false
			,data:object
			,beforeSend : function() {
				$.blockUI({ message: 'Consultando proveedor...' });
	         }
	        ,complete: function () {
				$.unblockUI();
	        }
			,dataType: 'json', success: 
				function(j){
					resp=j[0].RESPUESTA;
					if(resp){
						vaciarJsonAInputs(j[0].existeProvCatalogoProveedor);
						vaciarJsonAInputs(j[0].existeProvAltaEmpleado);
						if($("#existeFolio").val()!="" && $("#existeFolio").val()!=$("#cFolio").val()){
							parent.document.getElementById("pb_save").disabled = true;
							classErrorRFC();
							swal("Este RFC ya esta Capturado en el Documento: "+$("#existeFolio").val(),{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
						}else if ($("#regreso").val() == 'EXISTE') {
							swal({
								title: "",
								text: "Este RFC ya existe en el catalogo de materiales pero aun no ha tenido un proceso de alta, desea Actualizarlo?",
								icon: "info",
								closeOnClickOutside: false,
								buttons: {
									confirm : "Aceptar",
									cancel: "Cancelar"
									},
							}).then((continuar) => {
								if (!continuar) {
									classErrorRFC();
								}else{
									llenaCombo(j[0].catMunicipio,"cMunicipioFiscal");
									vaciarJsonAInputs(j[0].datosCatalogoProveedor);
									hideAndShowXtipePerson();
								}
							});
						}else {
							vaciarJsonAInputs(j[0].existeBeneficiario);
							vaciarJsonAInputs(j[0].existeBeneficiarioSinH);
							if ($("#regresoBenef").val() == 'EXISTE') {
								swal({
									title: "",
									text: "Este RFC ya existe como beneficiario, desea Actualizarlo?",
									icon: "info",
									closeOnClickOutside: false,
									buttons: {
										confirm : "Aceptar",
										cancel: "Cancelar"
										},
								}).then((continuar) => {
									if (!continuar) {
										classErrorRFC();
									}else{
										llenaCombo(j[0].catMunicipio,"cMunicipioFiscal");
										vaciarJsonAInputs(j[0].datosCatalogoBeneficiario);
										hideAndShowXtipePerson();
									}
								});
							}else if($("#regresoSinH").val() == 'EXISTE'){
								swal({
									title: "",
									text: "Este Beneficiario ya existe sin tomar en cuenta la homoclave, desea Actualizarlo?",
									icon: "info",
									closeOnClickOutside: false,
									buttons: {
										confirm : "Aceptar",
										cancel: "Cancelar"
										},
								}).then((continuar) => {
									if (!continuar) {
										classErrorRFC();
									}else{
										llenaCombo(j[0].catMunicipio,"cMunicipioFiscal");
										vaciarJsonAInputs(j[0].datosCatalogoBeneficiarioSinH);
										hideAndShowXtipePerson();
									}
								});
							}else{			
								removeClassErrorRFC();
							}
						}
					}else{
						swal(j[0].MENSAJE,{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
					}
			}
		});
	}
}
function esExtranjero(){
	if ($("#chk_extranjero").prop("checked")){
		$("#cExtranjero").val("1");
		$("#Pais").show();
		document.getElementById("cEstadoFiscal").disabled = true;
		document.getElementById("cMunicipioFiscal").disabled = true;
		$("#nIdPyme").val("4");
		$("#trPyme").hide();
		$("#labelNombre").hide();
		$("#labelPaterno").hide();
		$("#labelMaterno").hide();
		$("#labelCalle").hide();
		$("#labelNumero").hide();
		$("#labelColonia").hide();
		$("#labelCP").hide();
		$("#labelCorreo").hide();
		$("#labelTel").hide();
	}else{
		$("#labelNombre").show();
		$("#labelPaterno").show();
		$("#labelMaterno").show();
		$("#labelCalle").show();
		$("#labelNumero").show();
		$("#labelColonia").show();
		$("#labelCP").show();
		$("#labelCorreo").show();
		$("#labelTel").show();
		$("#cExtranjero").val("0");
		$("#cPais").val("México");
		$("#Pais").hide();
		document.getElementById("cEstadoFiscal").disabled = false;
		document.getElementById("cMunicipioFiscal").disabled = false;
		if(idOper>2 && idOper<6){
			document.getElementById("cEstadoFiscal").disabled = true;
			document.getElementById("cMunicipioFiscal").disabled = true;
		}
		$("#trPyme").show();
	}
	
}
function guardaDatos(id_oper,opcion){
	var resp=false;
	var object=llenaObjectDat(opcion,id_oper);
	$.ajax({url: "../servlet/AltaProveedoresServlet" , type:'post' , async: false
		,data:object
		,beforeSend : function() {
			$.blockUI({ message: 'Guardando datos proveedor...' });
         }
        ,complete: function () {
			$.unblockUI();
        }
		,dataType: 'json', success: 
			function(j){
				resp=j[0].RESPUESTA;
				sincambios=!(j[0].HAYCUENTAS);
				swal({
					title: "",
					text: j[0].MENSAJE,
					icon: "info",
					closeOnClickOutside: false,
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						if(id_oper==1){
							$("#msgEFOS").val(j[0].msgEFOS);
						}
						if(id_oper==2 && j[0].HAYCUENTAS){
							sincambios=false;
						}
				});
			
		}, error: function( jqXHR, textStatus, errorThrown ) {
			resp=false;
		}
	});
	return resp;
}
function validaHomoclave(){
	var resp=true;
	var homoclave=$("#cIdRFC3").val();
	var msg="";
	var token="";
	
	if($("#cIdTipoPersonaRFC").val()!=3){
		if(homoclave=="" || homoclave=="   "){
			msg="La homoclave es un dato requerido.";
			resp=false;
			token="\n";
		}
		if(homoclave=="000"){
			msg+=token+"La homoclave no puede ser 000.";
			resp=false;
			token="\n";
		}
		if(homoclave.length<3){
			resp=false;
			msg+=token+"La homoclave deben ser 3 caracteres.";
		}
	}
	//
	if(!resp){
		swal(msg,{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
	}
	return resp;
}
function tipoPBChange(){
	var tipo = $("#tipoPB").val();
	if(tipo=="PROVEEDOR"){//Proveedor
		 $("#trdescProv").show();
		 $("#trdescBen").hide();
	}else{//Beneficiario
		 $("#trdescBen").show();
		 $("#trdescProv").hide();
	}
}

function validaOrganismoPublico(){

	if(parseInt($("#nIdPyme").val(),10)==6 &&!(roles.indexOf("ADMIN_RECMAT")>=0 || roles.indexOf("ANALISTA")>=0 || roles.indexOf("JEFES") >=0 || roles.indexOf("CAPTURA_ENTE_PUB") >= 0 ) ) {
	
		swal("Los organismos p\u00fablicos solo se pueden dar de alta en el area de adquisiciones de oficinas centrales.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
		parent.document.getElementById("pb_send").disabled = true;
		parent.document.getElementById("pb_save").disabled = true;
		$("#trLegenOrgPub").show();
		
	}else{
	
		parent.document.getElementById("pb_save").disabled = false;
		$("#trLegenOrgPub").hide();
		
	}
}

function checkLength( o, n, min, max ) {//Funcion para Validar la longitud de un campo (max-Min)
	if ( o.val().length > max || o.val().length < min ) {
		o.addClass( "ui-state-error" );
		if (min == max){
			updateTipsDlg( "La longitud de " + n + " debe ser de " + min + " caracteres." );
			parent.document.getElementById("pb_send").disabled = true;
		}else{
			updateTipsDlg( "La longitud de " + n + " debe estar entre " + min + " y " + max + "." );
			parent.document.getElementById("pb_send").disabled = true;
		}
		o.focus();
		return false;
	} else {
		return true;
	}
}
function validarEmail() {
	var email = $("#cEmail").val();
	expr = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if (!expr.test(email)){
		swal("Error: La dirección de correo " + email + " es incorrecta.",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
		return false;
	}else
		return true;
}
function esRequerido( o, n) {//Funcion para validar si un campo es requerido
	var sTemp = $.trim(o.val());
	o.val(sTemp);
	if ( sTemp.length == 0  ) {
		o.addClass( "ui-state-error" );
		updateTipsDlg(  n + " es un dato requerido." );
		parent.document.getElementById("pb_send").disabled = true;
		o[0].readOnly=false;
		parent.document.getElementById("pb_save").disabled = false;
		o.focus();
		return false;
	} else {
		o.removeClass( "ui-state-error" );
		return true;
	}
}
function consultaDoctos(id_oper){
	var resp=true;
	var object=llenaObjectDat(4,id_oper);
	$.ajax({url: "../servlet/AltaProveedoresServlet" , type:'post' , async: false
		,data:object
		,beforeSend : function() {
			$.blockUI({ message: 'Consultando documentos proveedor...' });
         }
        ,complete: function () {
			$.unblockUI();
        }
		,dataType: 'json', success: 
			function(j){
				var mensaje=j[0].MENSAJE;
				resp=j[0].RESPUESTA;
				if(!resp){
					swal(mensaje,{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				}
		}
	});
	return resp;
}
function OpenDialogObservaciones(){
	if($("#cObservaciones").val()!="")
		swal($("#cObservaciones").val(),{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
	else
		swal("No Hay Observaciones",{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
}
function checkRegexp( o, regexp, n ) {//Validar expresiones regulares
	if ( !( regexp.test( o.val() ) ) ) {
		o.addClass( "ui-state-error" );
		updateTipsDlg( n );
		return false;
	} else {
		return true;
	}
}
function cargaDataTable(){
	var rfc= $("#cIdRFC").val().replace("-","");
    rfc=rfc.replace("-","");
    szWhere = " dRFC = '" +rfc + "' ";
    szTabla = "BENEFICIARIOCUENTASBANCARIASTMP";
    $.getJSON("../catalogos/SelectJson.jsp", {
            Tabla: szTabla,
            Param: szWhere,
            MaxReg: 20,
            ajax: 'false'
        },
        function(j) {
            var x,x2;
            for (var i = 0; i < j.length; i++) {
                if(j[i].Col5 == 1)
                	x="Activo";
                else 
                	x="Inactivo";
                if(j[i].Col10 == 1)
                	x2="SI";
                else 
                	x2="NO";
                $('#tblCuentasBancarias').dataTable().fnAddData([
                		j[i].Col1 ,
                    	j[i].Col6 ,
                    	j[i].Col2 ,
                    	j[i].Col3 ,
                    	j[i].Col4 ,
                    	j[i].Col7 ,
                    	j[i].Col5 ,
                    	x,
                    	x2,
                    	"NO",
                    	"<a border=\"0\" href=\"#\" onclick=\"eliminaRegistro('" + j[i].Col3 +"','"+j[i].Col6+"','"+j[i].Col2+"','"+j[i].Col4+"','"+j[i].Col7+"','"+x+"','"+j[i].Col1+"');return false;\"><img border=\"0\" class=\"btnEliminar\" src=\"../imagenes/iconos/rechazar.png\" title=\"Eliminar Registro\"></img></a>" 	                
                     ]);
            }
        });		   
    szTabla = "BENEFICIARIOCUENTASBANCARIAS2";
    $.getJSON("../catalogos/SelectJson.jsp", {
            Tabla: szTabla,
            Param: szWhere,
            MaxReg: 20,
            ajax: 'false'
        },
         function(j) {
            var x,x2;
            for (var i = 0; i < j.length; i++) {
                if(j[i].Col5 == 1)
                	x="Activo";
                else 
                	x="Inactivo";
                if(j[i].Col10 == 1)
                	x2="SI";
                else 
                	x2="NO";
                $('#tblCuentasBancarias').dataTable().fnAddData([
                		j[i].Col1 ,
                    	j[i].Col6 ,
                    	j[i].Col2 ,
                    	j[i].Col3 ,
                    	j[i].Col4 ,
                    	j[i].Col7 ,
                    	j[i].Col5 ,
                    	x,
                    	x2,
                    	"SI",
                    	"<a border=\"0\" href=\"#\" onclick=\"eliminaRegistro('" + j[i].Col3 +"','"+j[i].Col6+"','"+j[i].Col2+"','"+j[i].Col4+"','"+j[i].Col7+"','"+x+"','"+j[i].Col1+"');return false;\"><img border=\"0\" class=\"btnEliminar\" src=\"../imagenes/iconos/rechazar.png\" title=\"Eliminar Registro\"></img></a>" 	                
                     ]);		
            }
        });
}
function soloLectura(){
	//cssDisabledInput();
	$("#msgEFOS").css("background-color", "transparent");
	//readOnlyInput();
	disableSelect();
	disableCheckbox();
}
function habilitaInputsRequeridos(){
	var arreglo=["cCodigoPostal", "cEmail","cTelefono","cColonia","cNumeroExterno","cCalle","cPais","cGiro","cNombre","cApellidoMaterno","cApellidoPaterno","cRazonSocial","cCURP"]
	for(var i = 0; i < arreglo.length; i++){
		if($("#"+arreglo[i]).val()==""){
			habilitaInput(arreglo[i]);
		}
	}
}
function enviaConsulta() {
	document.ExportarForm.submit();
}
function nameEncabezado(id_oper){
	if(id_oper==3){
		if("PROVEEDOR"==$("#tipoPB").val()){
			$("#spanEncabezado").text("Valida Proveedor");
		}else{
			$("#spanEncabezado").text("Valida Beneficiario");
			if(3==$("#cIdTipoPersonaRFC").val()){
				$("#spanEncabezado").text("Valida Empleado");
			}
		}
	}
	if(id_oper==4){
		if("PROVEEDOR"==$("#tipoPB").val()){
			$("#spanEncabezado").text("Autoriza Proveedor");
		}else{
			$("#spanEncabezado").text("Autoriza Beneficiario");
			if(3==$("#cIdTipoPersonaRFC").val()){
				$("#spanEncabezado").text("Autoriza Empleado");
			}
		}
	}
	if(id_oper==5){
		if("PROVEEDOR"==$("#tipoPB").val()){
			$("#spanEncabezado").text("Consulta Proveedor");
		}else{
			$("#spanEncabezado").text("Consulta Beneficiario");
			if(3==$("#cIdTipoPersonaRFC").val()){
				$("#spanEncabezado").text("Consulta Empleado");
			}
		}
	}
	if(id_oper==6){
		if("PROVEEDOR"==$("#tipoPB").val()){
			$("#spanEncabezado").text("Modifica Proveedor");
		}else{
			$("#spanEncabezado").text("Modifica Beneficiario");
			if(3==$("#cIdTipoPersonaRFC").val()){
				$("#spanEncabezado").text("Modifica Empleado");
			}
		}
	}
}
function readOnlyRFC(){
	$("#cIdRFC1").prop('readonly', true);
	$("#cIdRFC1").css("background-color", "#E8E8E8");
	$("#cIdRFC2").prop('readonly', true);
	$("#cIdRFC2").css("background-color", "#E8E8E8");
	$("#cIdRFC3").prop('readonly', true);
	$("#cIdRFC3").css("background-color", "#E8E8E8");
}
function updateTipsDlg( t ) {//Manda mensaje de Error
	tips.text( t );
	swal(t,{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
}
function liberaCaso() {
	$.blockUI({message: "Procesando espere ......"});
	var object=llenaObjectDat(11,idOper);
	formleave=parent.document.getElementById("frmLeave");
	$.ajax({url: "../servlet/AltaProveedoresServlet" , type:'post' , async: false
		,data:object
		,beforeSend : function() {
			$.blockUI({ message: 'Procesando ...' });
         }
        ,complete: function () {
			$.unblockUI();
        }
		,dataType: 'json', success: 
			function(j){
				swal(j[0].MENSAJE,{icon:"info",closeOnClickOutside: false,button: "Cerrar"});
				resp=j[0].RESPUESTA;
				formleave.submit();
				$.unblockUI();
		}, error: function( jqXHR, textStatus, errorThrown ) {
			$.unblockUI();
		}
	});
}