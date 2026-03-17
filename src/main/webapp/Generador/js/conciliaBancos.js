var mx =  {
			sProcessing: "Procesando...",
			sZeroRecords: "No hay registros a mostrar",
			sEmptyTable: "No hay datos en la tabla",
			sLoadingRecords: "Cargando...",
			sSearch: "Buscar:",
			sInfo: "Número de Registros --> _TOTAL_ ",
			sInfoEmpty: "Sin Registros"
		   }
									   
$(function(){
	$( "#dialog-importaCB" ).dialog({
		autoOpen: false,
		height: 610,
		width: 760,
		modal: true
	});
});


function insertarConciliacion() {
	if ($("#hbuscaConciliacion").val()!= ""){
		if ($("#mSaldo").val()!= ""){
			if ($("#mIntereses").val()!= ""){
				queryFormPost("esCtaVigente", {async: false});
				queryFormPost("esCtaDuplicada", {async: false});
				if( $("#esVigente").val() == 1){
					if($("#estaDup").val() == 0){
							Swal.fire({ icon: "info",
										text: "Insertamos los valores..." });																		
						 	queryFormPost("insertaSaldosConciliacion",{async:false, callback:function()
						        {
						 			Swal.fire({ icon: "success",
						 						text: "Proceso terminado"});																	   			
						   		}
						  		});
							
							creaTablaConciliaciones();
							
							}
						else{
							Swal.fire({ icon: "info",
										text: "Esta Cuenta ya se registro para el mes seleccionado..." });																		
						}
				}else{
					Swal.fire({ icon: "warning",
								text: "La cuenta no es VIGENTE para el mes seleccionado..." });																
				 }
			}else{
				Swal.fire({ icon: "warning",
							text: "Falta insertar los intereses..." });															
				$("#mIntereses").focus();
			}
		}else{
			Swal.fire({ icon: "warning",
						text: "Falta insertar el saldo..." });														
			$("#mSaldo").focus();
		}
	}
	else{
		Swal.fire({ icon: "warning",
					text: "Falta selecionar la Cuenta..." });														
		$("#hbuscaConciliacion").focus();
	}
	
	Swal.fire({ icon: "info",
		text: 'Mes:'+ $("#mesSaldo").val()
		+', Cuenta:'+ $("#hbuscaConciliacion").val()
		+', CC:'+ $("#cCCSaldo").val()
		+', Saldo:'+ $("#mSaldo").val()
		+', Inte:'+ $("#mIntereses").val()
		+', nFolioSW:'+$("#nFolioCuenta").val()
		+', Usuario:'+ $("#cUsuarioLogin").val() });
	
	modalInsertar.hide();
}
var texto = "";
function validaDatos() {
	if($("#cPuestoELAB").val() == ""){
		texto = "Falta Ingresar Datos del Firmante que Elabora";
		return texto; 
	}
	if($("#cPuestoVOBO").val() == ""){
		texto = "Falta Ingresar Datos del firmante de Vo Bo";							
		return texto; 
	}
	if($("#cPuestoAUT").val() == ""){ 
		texto = "Falta Ingresar Datos del Firmante Autorizador" ;								
		return texto; 
	}
	
}
function aceptarFirmantes(){
	var mensaje = validaDatos();
	if(mensaje =="" || mensaje === undefined){
			queryFormPost({	queryName : "firmanteBancoExisteRead",async : false});
			
			if($("#firmanteExiste").val() == "Existe"){
				queryFormPost({	queryName : "eliminaFirmantesBanco",async : false});
			}
			
			queryFormPost({	queryName : "firmanteConciliacionElabCreate",async : false});
			queryFormPost({	queryName : "firmanteConciliacionRevCreate",async : false});
			queryFormPost({	queryName : "firmanteConciliacionAutCreate",async : false});
			
			if( $("#chk_esFIEL").attr("checked") )
				$("#esFiel").val("S");		
			else
				$("#esFiel").val("N");
			
			$("#nIdReporte").val("5");
			
			modalFirmantes.hide();
			$("#AuxiliarBanco").submit();
			
	} else {
		Swal.fire("Ocurrio un error:", mensaje, "error");
	}
}

function llenaFirmanteVoBo(){

	$("#nIdtipoFirmante").val("VOBO") 
	querySelectPost("FirmantesPorTipoBanco_Read", "cboVoBo",{async: false });
}


function llenaFirmanteElab(){

	$("#nIdtipoFirmante").val("ELAB") 
	querySelectPost("FirmantesPorTipoBanco_Read", "cboElabora",{async: false });
}


function llenaFirmanteAut(){
	
	$("#nIdtipoFirmante").val("AUT") 
	querySelectPost("FirmantesPorTipoBanco_Read", "cboAutoriza",{async: false });
}
	
function infoEmpleado( tipoFirmante ){
	
	
	$("#nIdtipoFirmante").val( tipoFirmante );
	
	var numeroEmpleado = -1;
	var postFijo = ""
	
	if( "VOBO" == tipoFirmante){
		
		numeroEmpleado = $("#cboVoBo").val();
		$("#nIdtipoFirmante").val(tipoFirmante );
		$("#nNumEmpleadoVoBo").val( numeroEmpleado );
		
	}else if( "AUT" == tipoFirmante){
		numeroEmpleado = $("#cboAutoriza").val();
		$("#nIdtipoFirmante").val( tipoFirmante );
		$("#nNumEmpleadoAut").val( numeroEmpleado );
	
	}else if( "ELAB" == tipoFirmante){
		numeroEmpleado = $("#cboElabora").val();
		$("#nIdtipoFirmante").val( tipoFirmante );
		$("#nNumEmpleadoElab").val( numeroEmpleado );
	}
	
	if( parseInt( numeroEmpleado, 10 ) > 0 ){
		$("#nNumEmpleadoBusqueda").val( numeroEmpleado );		
		queryFormPost({ queryName:"infoPuestoFirmanteBancoRead", 
		                    async:false,
		                    callback:function(){		                    	
		                    	$("#cPuesto" + tipoFirmante).val( $("#cPuestoEmpleado").val() );
		                    }
		              });
	} 
}
	function importaSubmit(){
 		 	document.getElementById("ver").disabled = false;
			
 		 	const data = new FormData($("#ExportarForm")[0]);
 		 	var exito = false;
			var logErrores = "";
			$.ajax({
					url : "../servlet/ConciliaBancosServlet",
					type : 'POST',
					async : false,
					enctype: "multipart/form-data",
					data : data,
					beforeSend: function () {
						$.blockUI({message: "Procesando espere ......"});
					},
					processData: false,
				    contentType: false,
					success : function(j) {
						exito = j.success;
						$.unblockUI();
						if (exito) {
							Swal.fire("OK","Se importo el estado de cuenta, abre la conciliacion para continuar.", "success");
							
						} else {
							logErrores = j.errorList[0];
						}
					},
					error : function(errorThrown) {
						$.unblockUI();
						logErrores = errorThrown.errorList[0];
					}
				});
			
			if (!exito){
				$.unblockUI();
				Swal.fire("Ocurrio el siguiente error:", logErrores, "error")
				
			}
		}
	
	
	function conciliaAutomatica(){
			document.getElementById("Procesa").disabled = false;
			
			$.ajax({
					async: false,
					cache: false,
					type: 'GET',
					dataType: 'json',
					url: "../servlet/ConciliaAutomaticaServlet",
					data: "Param="+ $("#cBan").val()+","+$("#nMes").val()+"," + $("#nConciliacion").val(), 
					success: function(resp){
						if ( !resp.ERROR ){
							
							var arrData = resp.DATA;
							
							$("#cBan").val(arrData[0]); //cuenta 
							$("#nMes").val(arrData[1]); //mes
							$("#nConciliacion").val(arrData[2]); //conciliacion
							$("#cuenta").val(arrData[0]);
							$("#fecha").val(arrData[1]);
														
							document.getElementById("contConciliacion").style.visibility= "visible"; // Div - Importación estado de cuenta
							document.getElementById("ConciliacionAut").style.visibility= "hidden";   // Div - Procesar conciliación automática	
							document.getElementById("contDatos").style.visibility= "visible";        // Div - Ver, guardar y finalizar conciliación
							$("#seImporto").val("N");
							Swal.fire({})
							alert ("Se proceso la conciliacion automatica.");
						}
						else{
							alert(resp.ERROR);
						}
					},
					 error: function(errorThrown) 
				      {
						 Swal.fire({ icon: "error",
									 text: "error: " + errorThrown.ERROR });				        
				      }
				});
		}
	
	
	function abreConciliacion(numero, nMes, cve, nFinal){
	
		$("#cuenta").focus();
		 document.getElementById("contConciliacion").style.visibility= "visible"; // Div - Importación estado de cuenta
		 document.getElementById("newConciliacion").style.visibility= "visible";  // SubDiv - Importación estado de cuenta
		 document.getElementById("contDatos").style.visibility= "visible";        // Div - Ver, guardar y finalizar conciliación
		 
		 nconciliacion = numero;		 		 
		 $("#nConciliacion").val(numero);
		 
		 cConciliacion = cve;
		 $("#cuenta").val(cve);
		 $("#cBan").val(cve);
		 
		 fConciliacion = nMes;
		 $("#fecha").val(nMes);
		 $("#nMes").val(nMes);
		 
		if ( nFinal == "1"){
			document.getElementById("esFinal").checked= true;
			document.getElementById("final").disabled = true;
			document.getElementById("iniciar").disabled = true;
		}
		else
		{
			document.getElementById("esFinal").checked= false;
			document.getElementById("iniciar").disabled = false;
		}
		
		document.getElementById("ntabla1").style.visibility= "hidden";
		document.getElementById("ntabla2").style.visibility= "hidden";
		document.getElementById("reportes").style.visibility= "hidden"; 
		
		document.getElementById("cargaArchivo").disabled = true;
		document.getElementById("importa").disabled = true;
		$("#divImportar").hide();
		
		document.getElementById("ver").disabled = false;
		
		queryFormPost("readEstatusConciliacion", {async: false});
		
		if ($("#seImporto").val() == "S"){
			document.getElementById("ConciliacionAut").style.visibility= "visible"; // Div - Procesar conciliación automática
			document.getElementById("contDatos").style.visibility= "hidden";        // Div - Ver, guardar y finalizar conciliación
			document.getElementById("Procesa").disabled = false;
		}else{
			document.getElementById("ConciliacionAut").style.visibility= "hidden";  // Div - Procesar conciliación automática
			document.getElementById("Procesa").disabled = true;
		}
				
	}
		
	
	function nuevaConcilia(){
		document.getElementById("TIPO").disabled = false;
		document.getElementById("contConciliacion").style.visibility= "visible"; // Div - Importación estado de cuenta
		document.getElementById("newConciliacion").style.visibility= "visible";  // SubDiv - Importación estado de cuenta
		document.getElementById("contDatos").style.visibility= "hidden";         // Div - Ver, guardar y finalizar conciliación 
		document.getElementById("ntabla1").style.visibility= "hidden";  		 // Div - Cargos
		document.getElementById("ntabla2").style.visibility= "hidden";  	 	 // Div - Abonos
		document.getElementById("reportes").style.visibility= "hidden"; 		 // Div - Repoertes
		document.getElementById("ver").disabled = true;
		
		 nconciliacion = "";		 		 
		 $("#nConciliacion").val("");
		 
		 cConciliacion = "";
		 $("#cuenta").val("");
		 $("#cBan").val("");
		 
		 fConciliacion = "";
		 $("#fecha").val("");
		 $("#nMes").val("");
		 $("#TIPO").focus();
	}	 
	
	function insertaSaldosConciliacion(){
		$("#mesSaldo").val(1);
		$("#hbuscaConciliacion").val("");
		$("#mSaldo").val("");
		$("#mIntereses").val("");
		modalInsertar.show();
		
		
	}
	
	
	function eliminaConciliacion(numeroConciliacion){
		$("#numeroConciliacionElimina").val(numeroConciliacion);
		
		Swal.fire({
			  title: 'Desea continuar?',
			  text: "Se elmiminará la conciliación número "+numeroConciliacion,
			  icon: 'warning',
			  showCancelButton: true,
			  confirmButtonColor: '#288BA8',
			  cancelButtonColor: '#e6e6e6',
			  confirmButtonText: 'Aceptar',
			  cancelButtonText: 'Cancelar'
			}).then((result) => {
			  if (result.isConfirmed) {
				  		
				  	queryFormPost("eliminaConciliacion", {async:false});
				  	$("#seImporto").val("N");
					window.location = "ConciliaBancos.jsp?importado=N";
			  } 
			})
			
			
	}
		
	
	function habilita(){
		if ( $("#TIPO").prop("checked")) {
		 document.getElementById("cargaArchivo").disabled = false;
		 document.getElementById("importa").disabled = false;
		 }
	}	
	
	
	function OpenDialogAdjunta(cuenta, mes, tipo){
		
		 $("#nMes").val(mes);
		 $("#cBan").val(cuenta);
		
		$('#uploadConciliacionFrm').attr('src', "CargaCB.jsp?ctaBan="+cuenta+"&mes="+mes+"&cTipoDoc="+tipo);
		
		$("#dialog-importaCB").dialog( "open" );
		
	}
		 
	function seleccionaCARGOS(){
		if ( $("#sTCargos").prop("checked")) {
		 	$('#dt_vNoConciliadosC input').each(function(idx, elm) {
				$(this).prop('checked', true);
			});
		 }
		 else {
		 	$('#dt_vNoConciliadosC input').each(function(idx, elm) {
				$(this).prop('checked', false);
			});
		 }
	}
	
	function seleccionaABONOS(){
		if ( $("#sTAbonos").prop("checked")) {
		 	$('#dt_vNoConciliadosA input').each(function(idx, elm) {
				$(this).prop('checked', true);
			});
		 }
		 else{
		 	$('#dt_vNoConciliadosA input').each(function(idx, elm) {
				$(this).prop('checked', false);
			});
		 }
	}
	
	function habiliat_boton(){
			if ( $("#report").val()!= "00") {
		 		document.getElementById("exporta").disabled = false;
		 		document.getElementById("firmantes").disabled = false;
		 	}
		 	else{
		 		document.getElementById("exporta").disabled = true;
		 		document.getElementById("firmantes").disabled = true;
		 	}
		}
	
	function ActualizaDatos(){
		$("#cuenta").val($("#cBan").val());
		$("#fecha").val($("#nMes").val());
		
		if ( $("#nFinal").val() == "1"){
			document.getElementById("esFinal").checked= true;
		}
		else
		{
			document.getElementById("esFinal").checked= false;
		}
		
	}
	
	function aceptarCarga(){
		$( "#dialog-importaCB" ).dialog("close");
		
		queryFormPost("updateSaldoPDF", {async: false});
		
		creaTablaConciliaciones();
	}
		
	function cambia_Mes(){
		
		creaTablaConciliaciones();
		document.getElementById("contConciliacion").style.visibility= "hidden";
		document.getElementById("newConciliacion").style.visibility= "hidden";
		document.getElementById("contDatos").style.visibility= "hidden";
		
		document.getElementById("ntabla1").style.visibility= "hidden";
		document.getElementById("ntabla2").style.visibility= "hidden";
		document.getElementById("reportes").style.visibility= "hidden"; 
		
		document.getElementById("cargaArchivo").disabled = true;
		document.getElementById("importa").disabled = true;
		document.getElementById("TIPO").checked = false;
		document.getElementById("TIPO").disabled = true;
		document.getElementById("ver").disabled = false;
		
	}
		
	function conciliacion(){
				
				document.getElementById("ntabla1").style.visibility= "visible";  // Div - Cargos
				document.getElementById("ntabla2").style.visibility= "visible";  // Div - Abonos
				document.getElementById("reportes").style.visibility= "visible"; // Div - Reportes
				
				$("#Cargos").val("0");
				$("#Abonos").val("0");
				$("#sAbonos").val("0");
				$("#sCargos").val("0");
				creaTablaVistaC();
				creaTablaVistaA();
				
				queryFormPost("conciliacionFirmaElectronica", {async: false});
				
				if ($("#cEsFirmaElectronica").val() != 'S'){
					$("#firmantes").show();
				} else {
					$("#firmantes").hide();
				}
		 }
		  
	function guarda_info()
		{	
			sumas();
			
			if($("#guarda").prop("disabled")){				
				Swal.fire({ icon: "warning",
							text: "Seleccion no cuadra"});				
			}
			else {
				Swal.fire({ icon: "success",
							text: "Se guarda y actualiza la conciliación"});					
				guarda_valores();
			}
		}
		
	function exporta_archivo(){
	
		switch ($("#report").val()) {
		case "01":
				Swal.fire({ icon: "info",
							text: "Genera Movimientos en Conciliación"});				
				$("#AuxiliarBanco").submit();
			break;
		case "02":
				Swal.fire({ icon: "info",
							text: "Genera Movimientos Conciliados"});								
			break;
		case "03":
				Swal.fire({ icon: "info",
							text: "Exporta Estado de Cuenta"});				
				$("#AuxiliarBanco").submit();
			break;
		case "04":
				Swal.fire({ icon: "info",
							text: "Exporta Auxiliar 11121"});				
				$("#AuxiliarBanco").submit();
			break;
		default:
			break;
		}
	}	
		
	
	function finaliza(){
		var fin = 0;
		Swal.fire({ icon: "info",
					text: "Se finalizara la conciliación"});
				
		var cAux="";
		var cEdo = "";
		var cNoC = "";
			
		var cSuma = $("#dt_vNoConciliadosC").dataTable().fnGetNodes();
		var aSuma = $("#dt_vNoConciliadosA").dataTable().fnGetNodes();
		var ren;
		
		for (i = 0; i < cSuma.length; i ++)
			{
			ren = oTable1.fnGetData(cSuma[i]);
				if ( ren[3] == "Contabilidad"){
					if ( ren[1] == $("#nConciliacion").val()){
						cAux += ren[11]+',';					
						}
					else {
						cNoC += ren[11]+',';
					}	
				}else{
				
					if ( ren[1] == $("#nConciliacion").val()){
							cEdo += ren[11]+',';
						}
					else{
							cNoC += ren[11]+',';
					}
				}
			}
		
		for (j = 0; j < aSuma.length; j ++)
			{
			ren = oTable2.fnGetData(aSuma[j]);
				if ( ren[3] == "Contabilidad"){
					if ( ren[1] == $("#nConciliacion").val()){
						cAux += ren[11]+',';					
						}
					else {
						cNoC += ren[11]+',';
					}	
				}else{
					if ( ren[1] == $("#nConciliacion").val()){
						cEdo += ren[11]+',';
					}
					else
					{
						cNoC += ren[11]+',';
					}
				}
			}
		
		cAux   = "'" + cAux.substring(0, cAux.length - 1) + "'";
		cNoC   = "'" + cNoC.substring(0, cNoC.length - 1) + "'";
		cEdo   = "'" + cEdo.substring(0, cEdo.length - 1) + "'";
				
		$("#numConciliacionf").val($("#nConciliacion").val());
		$("#cadenaAuxiliarf").val(cAux);
		$("#cadenaEdoCtaf").val(cEdo);
		$("#cadenaNoConf").val(cNoC);
		
		
		queryFormPost("validaFinal", {async: false});	
		
		fin = $("#validaFinal").val();
			
		if ( fin == 0 ){
							
		queryFormPost("finConciliacion",{async:false, callback:function()
	        {
				alert("Proceso terminado ");
			}
			});
		
		document.getElementById("guarda").disabled = true;
		document.getElementById("final").disabled = true;
		document.getElementById("esFinal").checked= true;
		
		document.getElementById("ntabla1").style.visibility= "hidden";
		document.getElementById("ntabla2").style.visibility= "hidden";
		
		document.getElementById("reportes").style.visibility= "visible";
		}
		else
		{
			Swal.fire({ icon: "warnign",
						text: "Ya existe una conciliación finalizada para este mes..."});			
		}
		
	}
	
	
	function guarda_valores(){
		
		var cAuxiliar="";
		var cEdoCta = "";
		var cNoCon = "";
			
		var cargosSuma = $("#dt_vNoConciliadosC").dataTable().fnGetData();
		var abonosSuma = $("#dt_vNoConciliadosA").dataTable().fnGetData();
		var renglon;
		
		$('#dt_vNoConciliadosC input:checked').each(function(idx, elm) {
			renglon = cargosSuma[oTable1.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					if ( renglon[1] == $("#nConciliacion").val()){
						cAuxiliar += renglon[11]+',';					
						}
					else {
						cNoCon += renglon[11]+',';
					}	
				}else{
					if ( renglon[1] == $("#nConciliacion").val()){
						cEdoCta += renglon[11]+',';
						}
					else {
						cNoCon += renglon[11]+',';
					}
				}
			});
			
		$('#dt_vNoConciliadosA input:checked').each(function(idx, elm) {
			renglon = abonosSuma[oTable2.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					if ( renglon[1] == $("#nConciliacion").val()){
						cAuxiliar += renglon[11]+',';
						}
					else {
						cNoCon += renglon[11]+',';
					}	
				}else{
					if ( renglon[1] == $("#nConciliacion").val()){
						cEdoCta += renglon[11]+',';
						}
					else {
						cNoCon += renglon[11]+',';
					}
				}
			});
		
			cAuxiliar = cAuxiliar.substring(0, cAuxiliar.length - 1 );
			cNoCon	  = cNoCon.substring(0, cNoCon.length - 1);
			cEdoCta   = cEdoCta.substring(0, cEdoCta.length - 1) ;		
					
			$("#numConciliacion").val($("#nConciliacion").val());
			$("#cadenaAuxiliar").val(cAuxiliar);
			$("#cadenaEdoCta").val(cEdoCta);
			$("#cadenaNoCon").val(cNoCon);
							
		queryFormPost("guardaConciliacionManual",{async : false, callback : function() 
				{
					Swal.fire({ icon: "success",
								text: "Proceso terminado."});					
				}
				});
				
				creaTablaVistaC();
				creaTablaVistaA();		
	}
	
	
	function sumas(){
		var cargos = 0;
		var abonos = 0;
		
		$("#sCargos").val(0);
		$("#sAbonos").val(0);
		sumaCargos();
		sumaAbonos();
		cargos = parseFloat($("#sCargos").val()).toFixed(2) ;
		abonos = parseFloat($("#sAbonos").val()).toFixed(2) ;
		
		$("#Cargos").val(cargos);
		$("#Abonos").val(abonos);
		
		if (cargos == abonos){
			document.getElementById("guarda").disabled = false;
			document.getElementById("final").disabled = false;
		}
		else{
			document.getElementById("guarda").disabled = true;
			document.getElementById("final").disabled = true;
			document.getElementById("final").checked = false;
		}		
	}
	
	function sumaCargos(){
			var cargosSuma = $("#dt_vNoConciliadosC").dataTable().fnGetData();
			var renglon;
			var banco = 0;
			var conta = 0;

			$('#dt_vNoConciliadosC input:checked').each(function(idx, elm) {
			renglon = cargosSuma[oTable1.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					conta += Math.round(parseFloat(renglon[9])*100)/100;
				}else{
					banco += Math.round(parseFloat(renglon[9])*100)/100;
				}
			});
			
			$("#sCargos").val((conta - banco).toFixed(2));		
	}
	
	function sumaAbonos(){	
			var abonosSuma = $("#dt_vNoConciliadosA").dataTable().fnGetData();
			var renglon;
			var banco = 0;
			var conta = 0;
			
			$('#dt_vNoConciliadosA input:checked').each(function(idx, elm) {
			renglon = abonosSuma[oTable2.fnGetPosition($(this).closest('tr')[0])];
				if ( renglon[3] == "Contabilidad"){
					conta += Math.round(parseFloat(renglon[9])*100)/100;
				}else{
					banco += Math.round(parseFloat(renglon[9])*100)/100;
				}
			});
			
			$("#sAbonos").val( (conta - banco).toFixed(2));	
		}
	
	function fixDecimals(oObj)
        {
			var valor = parseFloat(oObj.aData[oObj.iDataColumn]);
			return valor.toFixed(2);
		}
		
		
	
	function creaTablaVistaC() {

		oTable1 = $("#dt_vNoConciliadosC").dataTable(
						{	
							bAutoWidth : false,
							oLanguage: mx,
							bPaginate : false,
							bServerSide : true,
							bFilter : true,
							bSort : true,
							bRetrive : true,
							bDestroy : true,
							bJQueryUI : true,
							aaSorting: [[ 2, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//"+ window.location.host + "/"+ window.location.pathname.split("/")[1]
									+ "/crud?rt=t&ql=fn_l_noCOnciliados("+$("#nConciliacion").val()+","+$("#nMes").val()+",'"+$("#cBan").val()+"')&qw="
									+ " cTipo = 'C' ",
							aoColumns : [ { sName : "Id"},
							              { sName : "nConciliacion"},
							              { sName : "nFolioOrigen"},
							              { sName : "cOrigen"},
							              { sName : "fFecha"},
							              { sName : "cReferencia"},
							              { sName : "cCheque"},
							              { sName : "cDescripcion"},
							              { sName : "cTipo"},
							              { 
							              	sName : "mMonto",
							              	fnRender: fixDecimals
							              },
							              { sName : "Conciliado", bVisible : false},
							              { sName : "idDetalle", bVisible : false}]
						});		
						
						document.getElementById("sTCargos").disabled = false;
						
		}
		
		function creaTablaVistaA(){				

		oTable2 = $("#dt_vNoConciliadosA").dataTable(
						{
							bAutoWidth : false,
							oLanguage: mx,
							bPaginate : false,
							bServerSide : true,
							bFilter : true,
							bSort : true,
							bRetrive : true,
							bDestroy : true,
							bJQueryUI : true,
							aaSorting: [[ 2, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]
									+ "/crud?rt=t&ql=fn_l_noCOnciliados("+$("#nConciliacion").val()+","+$("#nMes").val()+",'"+$("#cBan").val()+"')&qw="
									+ " cTipo = 'A' ", 
							aoColumns : [ {	sName : "Id" },
										  {	sName : "nConciliacion"},
										  {	sName : "nFolioOrigen"},
										  { sName : "cOrigen"},
										  { sName : "fFecha"},
										  { sName : "cReferencia"},
										  { sName : "cCheque"},
										  { sName : "cDescripcion"},
										  { sName : "cTipo"},
										  { 
							              	sName : "mMonto",
							              	fnRender: fixDecimals
							              },
										  { sName : "Conciliado", bVisible : false},
										  { sName : "idDetalle", bVisible : false}]
							}
						);
						
						document.getElementById("sTAbonos").disabled = false;

}


function creaTablaConciliaciones() {

		oTable3 = $("#dt_Conciliaciones").dataTable(
						{
							bAutoWidth : false,
							oLanguage: mx,
							bPaginate : false,
							bFilter : true,
							bSort : true,    							
							bRetrive : true,
							bDestroy : true,
							bJQueryUI : true,
							bServerSide : true,
							aaSorting: [[ 1, "asc" ]] ,
							sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1]	+ "/crud?rt=t&ql=fn_l_Conciliaciones("+$("#mesMostrado").val()+",'"+$("#cCC").val()+"')",
								aoColumns: [
											{ sName: "nConciliacion" },
											{ sName: "nCban"   },
											{ sName: "nMes" },
											{ sName: "cDescripcion"	},
											{ sName: "Final"},
											{ sName: "cCentroContable"	},
											{ sName: "SaldoTXT"	},
											{ sName: "SaldoPDF"	},
											{ sName: "Intereses"},
											{ sName: "Contabilidad"	},
											{ sName: "PDF"	},
											{ sName: "Firmas"},
											{ sName: "abreConciliacion"},
											{ sName: "eliminaConciliacion"} ]
						}
					);
		}
		
		function openCenteredWindow(url, name, height, width, parms) {
				var left = Math.floor((screen.width - width) / 2);
				var top = Math.floor((screen.height - height) / 2);
				var winParms = "top=" + top + ",left=" + left + ",height=" + height + ",width=" + width + ",scrollbar=yes,status=yes";
				if (parms) {
					winParms += "," + parms;
				}
				var win = window.open(url, name, winParms);
				if (parseInt(navigator.appVersion) >= 4) {
					win.window.focus();
				}
				return win;
			}
		function abreReporte(folioReporte){
				openCenteredWindow("../firma/MuestraReporte?T=R&documento=CONCILIABANCOS&f="+folioReporte, "CONCILIABANCOS", 800, 1024, "scrollbars=yes,resizable=yes");
			}
			
			function abreAcuse(folioReporte){
				openCenteredWindow("../firma/MuestraReporte?T=A&documento=CONCILIABANCOS&f="+folioReporte, "CONCILIABANCOS", 800, 1024, "scrollbars=yes,resizable=yes");
			}
				
	
	function DescargaConciliacion(idDocumento){
		url = "../SAIFilestore?select="+idDocumento;
	    window.open(url, "popacuse","scrollbars=1, resizable=yes, width=500, height=700");
	}
	
	
	function DescargaConciliacionFiel(idDocumento){
		var guardado = false;
		logErrores = "";	
		
			$.ajax({
				url : "../firma/MuestraReporte",
				type : 'post',
				async : false,
				data : $("#ProcesaForm").serialize(),
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
				Swal.fire("Error al mostrar el reporte", logErrores, "error")
			
			return guardado;
			
	}
	
	function muestraFirmantes(){

		llenaFirmanteElab();
		llenaFirmanteVoBo();
		llenaFirmanteAut();
		modalFirmantes.show();

	}