	
	function init(){
		
		$.ajax({
			url : '../expediente/CreaExpediente',
			dataType : 'json',
			type : "POST",
			data : {
				"folio" : $("#nFolioReintegrocaja").val()
			},
			async : true,
			success : function(json) {
				var exito = json.success;
	
				if( exito != "true" ) {
					alert(json.data_1.result);
					throw json.data_1.result;
				} else {
					if( "" != json.data_1.result )
						$("#idGabinete").val(json.data_1.result);
				}
			},
			error : function(xhr, textStatus, errorThrown) {
				Swal.fire({ icon: 'warning',
							text: "Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown });
				r = true;
			}
		});
	
		querySelectPost("readUnidadResponsable", "ue", {async: false });
		
		hideButtonsDetail();			
		
		$("#nNumEmpleadoElab").val(nNumEmpleado);
		$("#cNombreEla").val(nombreElaboro);
		$("#cPaternoEla").val(aPaternoElaboro);
		$("#cMaternoEla").val(aMaternoElaboro);
		$("#cPuestoEla").val(puestoElaboro);
		$("#cEsFirmaElectronica").val("N");
		$("#cIdUsuarioCaptura").val(cIdUsuarioCaptura);						
		
		$("#ReImprimirBtn").button();
		$("#Limpiar").button();	
		$("#operaciones").hide();
		$("#operacionesAutoriza").hide();
		$("#enviar").button();
		$("#CausaRegreso").hide();		
		$("#btnRechazo").hide();
						
		if(esConsulta){
			$("#ReImprimir").show();	
		}
						
		 $("input.AyudaSyC").subIniciaDlg();	
					
	}
		
	function onSubmit(id_oper){//VALIDACIONES PARA EL BOTON GUARDAR
  		var p = window.parent;
  		var valida_campos = true;
		
		try{
			if (id_oper==1){
				if($("#existe").val() != "SI"){
					//validaciones de la forma
					if ($('#ue').val() == "0" ){
						Swal.fire({ icon: 'warning',
									text: "Debes seleccionar la Unidad Ejecutora" });
						$("#ue").focus();
						return false;
					}
					if ($('#cEventoOrigen').val() == "" ){
						Swal.fire({ icon: 'warning',
									text: "Debes seleccionar el evento origen" });
						$("#cEventoOrigen").focus();
						return false;
					}
					if ($('#cEventoDestino').val() == "" ){
						Swal.fire({ icon: 'warning',
									text: "Debes seleccionar el evento destino" });
						$("#cEventoDestino").focus();
						return false;
					}
					if ($('#cConcepto').val() == "" ){
						Swal.fire({ icon: 'warning',
									text: "Debes capturar el conepto del tramite" });
						$("#cConcepto").focus();
						return false;
					}			
					if	($('#mMonto').val() == "0" || $('#mMonto').val() == "" || $('#mMonto').val() == "0.00"){
						Swal.fire({ icon: 'warning',
									text: "El monto debe ser diferente de $0.00" });
						$("#mMonto").focus();
						return false;
					}
					
					//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
		
					//Guardado de los campos correspondientes a cada variable de caso
					p.gestion.setFolio( $("#cFolio").val() );
					p.gestion.setOperador( $("#operador").val() );
					p.gestion.setFechaDocumento( $("#fechaCaptura").val() );
					p.gestion.setEjercicioFiscal( $("#ejercicioFiscal").val() );
					p.gestion.setConceptoMov("Aplicacion Reintegros Caja");
					p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
					p.gestion.setAplicadoCont("false");            
		
					$.blockUI({message: "Guardando datos espere ......"});
				
					$.ajax({
						url: '../contabilidad/ReintegrosCaja',
						type: 'post',
						async:false,
						dataType: 'json',
						data : $("#FormReintegroCaja").serialize(),
						error: function(data) {
							Swal.fire({ icon: 'error',
										text: "Ocurrio un error en el Insert. ["+ data.data_1.result +"]" });
						},
						success: function(data){
							var exito = data.success;	
							if (exito == "true") {
								Swal.fire({ icon: 'success',
											text: "Se guardo correctamente" });
								valida_campos = true;
								parent.document.getElementById("pb_save").disabled=true;
								parent.document.getElementById("pb_send").disabled=false;
								parent.document.getElementById("pb_cancel").disabled=false;
								$.unblockUI();
							} else {
								Swal.fire({ icon: 'error',
											text: "Ocurrio un error al guardar. ["+ data.data_1.result +"]" });
								parent.document.getElementById("pb_save").disabled=false;
								parent.document.getElementById("pb_send").disabled=true;
								parent.document.getElementById("pb_cancel").disabled=false;	
								$.unblockUI();						
							}
						}
					});									
								
					return true;					
				} 
			}

			if(id_oper == 2){ 						
				//AplicarTramite();				
				
			}
			
			if(id_oper == 3){
				$("#fechaAplicacion").val($("#fechaAplicacion").val().split('-').reverse().join('/'));
				
				if($('input:radio[name=grpAutorizarAut]:checked').val()=="Si"){
					$("#rechazaAutorizador").val("SI");
				}
				if($('input:radio[name=grpAutorizarAut]:checked').val()=="No")	{
					if( confirm("¿Esta seguro que desea rechazar el tramite?") ) {
						$("#rechazaAutorizador").val("NO");
					} else { // ! solo se confirmó que no y hay que dejar pasar el flujo
						return false;
					}			
				}
							
					$.ajax({
						url: '../contabilidad/ReintegrosCaja',
						type: 'post',
						async:false,
						dataType: 'json',
						data : $("#FormReintegroCaja").serialize(),
						error: function(data) {
							Swal.fire({ icon: 'error',
										text: "Ocurrio un error en el Insert. ["+ data.data_1.result +"]" });
						},
						success: function (data){
							Swal.fire({ icon: 'success',
										text: "Se aplicó la solicitud correctamente" });
							$.unblockUI();
						},
						error: function(data) {
							Swal.fire({ icon: 'error',
										text: "Ocurrio un error al aplicar la solicitud. " + "<%=mensaje%>" });
							$.unblockUI();
						}
						
					});
					
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
					parent.document.getElementById("pb_cancel").disabled=true;		
				
			}
			//Control de estado de botones
		}
		catch (e) {
			$("#esperaDialog").dialog("close");
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		$("#esperaDialog").dialog("close");
		return valida_campos;
  	}

	function AplicarTramite(){ //BOTON APLCIAR TRAMITE
		if($('input:radio[name=grpAutorizar]:checked').val()=="Si"){
			if (validaFirmantes()) { //VALIDA QUE SE HAYAN CAPTURADO LOS FIRMANTES
				if ($("#firmaElectronica").prop("checked")){
					$("#cEsFirmaElectronica").val("S");	
				}
				
				var id_oper = $("#id_oper").val();
				
				//Enviar para aplicar la primer poliza
				$("#dialog").dialog("close");				
				if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {			
					return false;			
				}
			   	
		   		$.blockUI({message: "Procesando espere ......"});
				
				$.ajax({
					datatype: 'json',
					type: "POST",
					url: '../contabilidad/ReintegrosCaja',
					data: $("#FormReintegroCaja").serialize(),
					error: function(data) {
							Swal.fire({ icon: 'error',
										text: "Ocurrio un error en el Insert. ["+ data.data_1.result +"]" });
						},
					success: function (data){
						Swal.fire({ icon: 'success',
									text: "Se aplicó la solicitud correctamente" });
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=false;
						parent.document.getElementById("pb_cancel").disabled=true;
						$.unblockUI();
					},
					error: function(data) {
						Swal.fire({ icon: 'error',
									text: "Ocurrio un error al aplicar la solicitud. " + "<%=mensaje%>" });
						parent.document.getElementById("pb_save").disabled=false;
						parent.document.getElementById("pb_send").disabled=true;
						parent.document.getElementById("pb_cancel").disabled=true;
						$.unblockUI();						
					}
				});
				
				actualizaFirmantes();								
				
				$("#co_responsable").val(ResponsableSiguiente(id_oper));
				$("#id_oper").val(Id_OperacionSiguiente(id_oper));
											
				$("#tdAutorizar").hide();
				$("#operaciones").hide();
				$("#operacionesAutoriza").hide();
				$("#enviar").hide();
				$("#pb_save", parent.window.document).click();
							
				if($("#cEsFirmaElectronica").val() != "S") {
					muestraEditaFirmantes();
					$("#operaciones").show();
					$("#operacionesAutoriza").hide();
					$("#tdAutorizar").hide();
					$("#ReImprimir").show();
				} else {
					var exito = false;
						$.ajax({
							beforeSend : function() {
								$.blockUI({
									message : "Enviando. Espere ..."
								});
							},
							complete : function() {
								$.unblockUI();
							},
							type : "POST",
							dataType : 'json',
							url : "..//FIEL/solicitaFirmaElectronica",
							cache : false,
							async : false,
							data : $("#FormContrato").serialize(),
							error : function(xhr, textStatus, errorThrown) {
								$.unblockUI();
								Swal.fire({ icon: 'error',
											text: "No se pudo completar la operacion debido al error: " + errorThrown });
							},
							success : function(RS) {
								$.unblockUI();
								var success = RS.success;
								if( "true" == success ){
									Swal.fire({ icon: 'success',
												text:  RS.data_1.result });
									parent.frmLeave.submit();
								}else{
									Swal.fire({ icon: 'error',
												text:  RS.data_1.result });
								}
							}
						});
					muestraResumenFirmas();
				}	
							
				return true;		
			}											
		}
		
		if($('input:radio[name=grpAutorizar]:checked').val()=="No")	{
			if( confirm("¿Esta seguro que desea rechazar el tramite?") ) {
				$("#CausaRegreso").dialog("open");			
			} else { // ! solo se confirmó que no y hay que dejar pasar el flujo
				return false;
			}			
		}
		
		$("#pb_save", parent.window.document).click();
	}
	
	function EnviarFirma(){ //BOTON APLCIAR TRAMITE
		if($('input:radio[name=grpAutorizar]:checked').val()=="Si"){
			if (validaFirmantes()) { //VALIDA QUE SE HAYAN CAPTURADO LOS FIRMANTES
				if ($("#firmaElectronica").prop("checked")){
					$("#cEsFirmaElectronica").val("S");	
				}
				
				var id_oper = $("#id_oper").val();
				
				//Enviar para aplicar la primer poliza
				$("#dialog").dialog("close");				
				if(!confirm("Se enviara el Documento a Firmas.  \n \n  ¿desea continuar?")) {			
					return false;			
				}
			   	
		   		$.blockUI({message: "Procesando espere ......"});
				
				actualizaFirmantes();								
				
				$("#co_responsable").val(ResponsableSiguiente(id_oper));
				$("#id_oper").val(Id_OperacionSiguiente(id_oper));
											
				$("#tdAutorizar").hide();
				$("#operaciones").hide();
				$("#operacionesAutoriza").hide();
				$("#enviar").hide();
				$("#pb_save", parent.window.document).click();
							
				if($("#cEsFirmaElectronica").val() != "S") {
					muestraEditaFirmantes();
					$("#operaciones").show();
					$("#operacionesAutoriza").hide();
					$("#tdAutorizar").hide();
					$("#ReImprimir").show();
					cmdReImprime();
				} else {
					var exito = false;
						$.ajax({
							beforeSend : function() {
								$.blockUI({
									message : "Enviando. Espere ..."
								});
							},
							complete : function() {
								$.unblockUI();
							},
							type : "POST",
							dataType : 'json',
							url : "..//FIEL/solicitaFirmaElectronica",
							cache : false,
							async : false,
							data : $("#FormContrato").serialize(),
							error : function(xhr, textStatus, errorThrown) {
								$.unblockUI();
								Swal.fire({ icon: 'error',
											text: "No se pudo completar la operacion debido al error: " + errorThrown });
							},
							success : function(RS) {
								$.unblockUI();
								var success = RS.success;
								if( "true" == success ){
									Swal.fire({ icon: 'success',
												text:  RS.data_1.result });
									parent.frmLeave.submit();
								}else{
									Swal.fire({ icon: 'error',
												text:  RS.data_1.result });
								}
							}
						});
					muestraResumenFirmas();
				}	
				$.unblockUI();				
				return true;		
			}											
		}
		
		if($('input:radio[name=grpAutorizar]:checked').val()=="No")	{
			if( confirm("¿Esta seguro que desea regresar el tramite para descartar?") ) {
				$("#CausaRegreso").dialog("open");			
			} else { // ! solo se confirmó que no y hay que dejar pasar el flujo
				return false;
			}			
		}
		
		$("#pb_save", parent.window.document).click();
	}
			
	function onPostSubmit(id_oper){//VALIDACIONES PARA EL BOTON ENVIAR
		$("#fechaAplicacion").val($("#fechaAplicacion").val().split('-').reverse().join('/'));
		
		if(id_oper == 2){ 			
			if($("#cEsFirmaElectronica").val() != "S"){					
				//SE VALIDA QUE LA SOLICITUD FIRMADA ESTE ADJUNTA CUANDO NO ES CON FIEL
				if ($("#longitudDocumento").val() == 0){
					Swal.fire({ icon: 'warning',
								text: "Favor de adjuntar la solicitud firmada." });
					return;					
				} else {
					Aplicar(id_oper);
				}
			}
		}
		
		return true; 
	}
	
	function Aplicar(id_oper){ //BOTON APLCIAR TRAMITE		
		
		//Enviar para aplicar la primer poliza
		$("#dialog").dialog("close");				
		if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {			
			return false;			
		}
	   	
		$.blockUI({message: "Procesando espere ......"});
		
		$.ajax({
			datatype: 'json',
			type: "POST",
			url: '../contabilidad/ReintegrosCaja',
			data: $("#FormReintegroCaja").serialize(),
			error: function(data) {
					Swal.fire({ icon: 'error',
								text: "Ocurrio un error en el Insert. ["+ data.data_1.result +"]" });
				},
			success: function (data){
				Swal.fire({ icon: 'success',
							text: "Se aplicó la solicitud correctamente" });
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=false;
				parent.document.getElementById("pb_cancel").disabled=true;
				$.unblockUI();
			},
			error: function(data) {
				Swal.fire({ icon: 'error',
							text: "Ocurrio un error al aplicar la solicitud. " + "<%=mensaje%>" });
				parent.document.getElementById("pb_save").disabled=false;
				parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_cancel").disabled=true;
				$.unblockUI();						
			}
		});
		
	}
	
	function onPostDisplay(id_oper){
		
	}
	
	function cambioEvento(){
		var subGurpo = $("#cEventoOrigen").val().split("_")[1];
		$("#cidsubgrupoevento").val("5_" + subGurpo);	
	}	
	
	function validaEventoOrigen(){
		if($("#cEventoOrigen").val() == ""){
			Swal.fire({ icon: 'warning',
						text: "Primero debe seleccionar el evento origen." })
			return false;
		}
	}
			
	function showButtonsDetail(){
		var sOrder = ""; 
	    var param =""; 
	    var zTabla = "R_CARGA_EVENTO_RECA";	     
		var camposWhere = " where EVT.Evento ='"+$("#cEventoDestino").val()+"' and r.cUR='"+$("#ue").val()+"' and cSubCuenta <> '' order by nDocRenglon ";  
	    			
		$.getJSON("../catalogos/SelectJson.jsp",{Tabla:zTabla, Campos:camposWhere, Param:param, Order:sOrder, MaxReg: "5", ajax:"true"}, function(j)
		{
			$("#trCTABAN").hide();
			$("#trhbuscabeneficiario").hide();	
			$("#trctabBene").hide();
			$("#trFFM").hide();
			$("#ctaBeneficiario").hide();
			
			for (var i = 0; i < j.length; i++){
				if($.trim(j[i].Col8)=="RFC"){
					$("#trhbuscabeneficiario").show();	
					$("#trctabBene").show();					
				}
				if($.trim(j[i].Col8)=="CTAB"){
					$("#trCTABAN").show();					
				}
				if($.trim(j[i].Col8)=="FFM"){
					$("#trFFM").show();
				}								
			}
		});
	}
	
	function ayudaBeneficiarios(){		
		window.open('../Generador/CatalogoBeneficiariosRG.jsp?formName=FormReintegroCaja&inputRFCTarget=hbuscabeneficiario&inputDRFCTarget=cNombre', 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');		
	}
	
	function cat_CuentaBeneficiario(){
		try{
			var done=false;
			
			querySelectPost("readCuentaBeneficiario","ctabBeneficiario",{
			asyc:true,
			callback : function(){	
							queryFormPost("readNoCuentaBeneficiario","NoCuentasB",  {async:false});	
							done=true;
						}
			});
		
		}	
		catch(ex){
			Swal.fire({ icon: 'error',
						text: "Error 00CTAB_RFCjs.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });
		}
	}//fin catalogo beneficiario
	
	function cat_FFM(){		
		window.open('../Generador/CatalogoFFM.jsp?formName=FormReintegroCaja&inputFFMTarget=FFM&inputDFFMTarget=cnombre&nombreArchivoPadre=REINTEGROCAJA', 'SubCuentas_Fondo', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');			
	}
	
	function limpiarDatos(){				
		$("#cEventoOrigen").val("");
		$("#cEventoDestino").val("");
		$("#dEvento").val("");
		$("#dEventoDestino").val("");
		$("#nidgrupoevento").val("");
		$("#nidgrupoeventoDestino").val("");
		$("#cidsubgrupoevento").val("");		
		$("#cConcepto").val("");
		$("#CTABAN_SNP").val("");
		$("#hbuscabeneficiario").val("");
		$("#cNombre").val("");		
		$("#ctabBeneficiario").val("");
		$("#id").val("");
		$("#FFM").val("");
		$("#mMonto").val("");
				
		hideButtonsDetail();		
	}
	
	function hideButtonsDetail(){
		$("#trCTABAN").hide();
		$("#trhbuscabeneficiario").hide();	
		$("#trctabBene").hide();
		$("#trFFM").hide();			
	}		
		
	function ResponsableSiguiente(id_oper){
		if(id_oper==1)
			return "REVISOR_REINTEGROCAJA";  		
   		
		if(id_oper==2){
			if($('input:radio[name=grpAutorizar]:checked').val()=="Si"){				
				return "AUTORIZA_REINTEGROCAJA";				
			}
			else			
				return "CAPTURA_REINTEGROCAJA";
		}				
		
		if(id_oper==3)
			 return "CONSULTA_REINTEGROCAJA";
  	}
  	  	
	function OperacionSiguiente(id_oper){
  		if(id_oper==1)
			 return "revisor_reintegrocaja";				
		
		if(id_oper==2){			 
			if($('input:radio[name=grpAutorizar]:checked').val()=="Si")
				return "autoriza_reintegrocaja";
			else							
				return "captura_reintegrocaja";		
		}
		
		if(id_oper==3)
			 return "consulta_reintegrocaja";
  	}

	function Id_OperacionSiguiente(id_oper) {			
		if($('input:radio[name=grpAutorizar]:checked').val()=="Si")
			return "3";
		else
			return "1";		
	}

	function onPostDisplay(){
	
	}
	
	function cmdImprimir(elFormato){					
		window.open(
			"../admin/SeguridadCatalogos?"
				+ "catalogo=CONTRARECIBO"
				+ "&accion=run"
				+ "&rn=PolizaReintegroCaja.jasper"
				+ "&whereFolio= AND Ce.nFolioReintegrocaja=" + $("#nFolioReintegrocaja").val(),				
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");					
	}
	
	function cmdReImprime() {	
		try{
			cmdImprimir('PolizaPago');
		}catch(ex){
			Swal.fire({ icon: 'error',
						text: "Error 0023js.\r\r" + ex.message + "\r\rFavor de reportarlo al Administrador del Sistema." });
		}
	}
	
	function mostrarMotivoRechazo()
	{
		$("#CausaRegreso").dialog("open");
	}

	