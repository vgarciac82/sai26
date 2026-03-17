/**
 * 
 */

	var grupo  = '';
	var condiciones = '';
	var sumaRetenciones = '';
	var restaep;

	var SUMmISRHonorarios = 0;
	var SUMmISRArrenda = 0;
	var SUMmImporteFlete4 = 0;
	var SUMmIMDT = 0;
	var SUMmCNIC = 0;
	var SUM5alM = 0;
	var SUMPenal = 0;
	var SUMCedular = 0;
	var SUMISRLaudos = 0;
	var SUMISROtros = 0;
	var SUMIVA6 = 0;
	var SUMISRRESICO = 0;
	var SUMmImporteFlete23 = 0;
	
	var DECmImporteFlete23 = 0;
	var DECmISRHonorarios = 0;
	var DECmISRArrenda = 0;
	var DECmImporteFlete4 = 0;
	var Dec5alM = 0;
	var DecCedular = 0;
	var DecPenal = 0;
	var DecISRLaudos = 0;
	var DecISROtros = 0;
	var TotalDEC = 0;
	var DecIva6 = 0;
	var DecISRRESICO = 0;


function ActivarRetSICOP(){
	if ($("#chk_LSICOP").prop("checked")){
		$("#RetSICOP").val("1");
		queryFormPost("bitacoraCheckRetSICOPCreate",{async: false });	
	} else {
		$("#RetSICOP").val("0");
		queryFormPost("bitacoraCheckRetSICOPCreate",{async: false });
	} 
}

/**
 * Funcion que se llama al aplicar el tramite.
 */
function aplicaTramite() {		
	if ($("#chk_LSICOP").prop("checked")){
		if(!confirm("Se encuentra activo Retenc. en SICOP y aplicará el Pasivo Diferido, desea continuar? ")){			
			parent.document.getElementById("pb_send").disabled = true;
			parent.document.getElementById("pb_save").disabled = false;
			return false;
		}				
	}
		
	if (confirm("Esta seguro de enviar a integracion la operacion ajena actual?")) {
		$("#esperaDialog").dialog("open");
		if (validaFirmantes()) {
			if ($("#firmaElectronica").prop("checked"))
				$("#cEsFirmaElectronica").val("S");

			parent.document.getElementById("pb_save").disabled = true;
			getNextSequenceVal({ seqName: cxpPrefijo + "-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal });
			queryFormPost("tOperAjenasCreate", { async: false });
			guardatabladetalle();
			GuardaContrarecibo();
			actualizaFirmantes();
			
			if ($("#chk_LSICOP").prop("checked"))
				GenerarPasivoDiferido();
			
			return true;
		}
	}

	return false;

}


function cmdGuardar() {

	if ($("#caNoContrarrecibo").val() != "") {
		if ($("#cDocumentoHaplicado").val() == "S" && id_oper == 1) {
			parent.document.getElementById("pb_save").disabled = true;
			parent.document.getElementById("pb_send").style.visibility = 'visible';
			parent.document.getElementById("pb_send").disabled = false;
		}
		return 0;
	}

	var hayError = '';

	if ($('#cBeneficiario').val() == 0) {
		Swal.fire("Seleccione"," Tiene que elegir un Grupo de Retenciones", info);
		return -1;
	}

	$('.encabezado').each(function() {
		if ($(this).val() == '') {
			hayError = hayError + this.name + ', ';
		}
	});

	if (hayError == '') {

		if (aplicaTramite())
			return 0;
		else
			return -1;
	} else {
		alert('Debe ingresar los siguientes datos: ' + hayError);
		return -1;
	}
}


function Busqueda() {

	$("#esperaDialog").dialog("open");

	$("#vDetalle").show();

	if ($('#cBeneficiario').val() == 0) {
		alert(" Tiene que elegir un Grupo de Retenciones");
		return;
	}

	initSumas();
	
	
	
	SUMCedular = 0; 

	var nRows = $("#Busqueda1 tr").length - 1;

	if (nRows > 0) {
		var oBusqueda = $("#Busqueda1").dataTable();
		oBusqueda.fnClearTable();
		var oDetalle = $("#Detalle").dataTable();
		oDetalle.fnClearTable();
	}

	var campos = "'" + $("#cGrupo").val() + "','" + $("#fBusquedaDe").val() + "','" + $("#fBusquedaHasta").val() + "','" + $("#cCentroContable").val() + "','" + $("#cCondiciones").val() + "','" + $("#cSumaRetenciones").val() + "','***', " + $("#cBeneficiario").val() + ", '" + $("#DESTINO_GASTO").val() + "'";

	var elParametro2 = '';
	var esIP = $("#chk_IP").prop("checked");
	var ejercido = $("#chk_Ejercido").prop("checked");
	var fFactura = $("#chk_fFactura").prop("checked");
	var esRGconOC = "N";
	if($("#chk_IP").prop("checked")){
		esRGconOC = "S";	
	}
	
	$.ajax({
		dataType: "json",
		url: "../egresos/BuscaRetenciones",

		data: {
			fInicio: $("#fBusquedaDe").val(),
			fFin: $("#fBusquedaHasta").val(),
			idGrupo: $("#cBeneficiario").val(),
			esIP: esIP,
			esRGconOC: esRGconOC,
			centroCont: $("#cCentroContable").val(),
			ejercido: ejercido,
			fechaFactura :fFactura
		},
		async: true,
		success: function(ajxSrc) {
			procesaTabla(ajxSrc);
		},
		error: function(xhr, ajaxOptions, thrownError) {
			alert(xhr.status);
			alert(thrownError);
		}
	});
	
	$(".montoAjena").each(function(){
		$(this).val(0);
	});

}

function procesaTabla(j) {

	if (j.success == "false") {
		alert(j.message);
		return;
	}


	var Nombre = 0;
	valjson = j.resultObj;
	j = j.resultObj;
	var datosTabla = [];
	var datosBusqueda = [];
	var tipoRetencion = $("#cBeneficiario").val();
	
	for (var i = 0; i < j.length; i++) {

		if (j[i].Col5 > 0) {


			var vcheck = "<input type='checkbox' id='" + Nombre + "' name='" + Nombre + "' onclick='restamMonto(" + Nombre + ")' checked='checked' class='desmarcar1'/>";

			datosBusqueda[i] = [vcheck, j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col21, j[i].Col22];
			datosTabla[i] = ['<input type="text" readonly="readonly" size="2" id="d' + Nombre + '" name="d' + Nombre + '" class="desmarcar2" value="1" />', j[i].Col0, j[i].Col1, j[i].Col2, j[i].Col3, j[i].Col4, j[i].Col5, j[i].Col6, j[i].Col7, j[i].Col9, j[i].Col8, j[i].Col10, j[i].Col11, j[i].Col12, j[i].Col13, j[i].Col14, j[i].Col15, j[i].Col16, j[i].Col17, j[i].Col18, j[i].Col22, j[i].Col24, j[i].Col25, j[i].Col26];

			Nombre++;
			
			if(tipoRetencion == 7){
				SUMmImporteFlete23 = SUMmImporteFlete23 + Number(j[i].Col9);//arrendamiento
				SUMmISRHonorarios = SUMmISRHonorarios + Number(j[i].Col8);				
				SUMmImporteFlete4 = SUMmImporteFlete4 + Number(j[i].Col12);					
			}
			else if(tipoRetencion == 8){				
				SUMmISRHonorarios = SUMmISRHonorarios + Number(j[i].Col10);
				SUMmISRArrenda = SUMmISRArrenda + Number(j[i].Col11);
				SUMISRRESICO = SUMISRRESICO + Number(j[i].Col26);
			}
			

			SUMmIMDT = SUMmIMDT + Number(j[i].Col13);
			SUMmCNIC = SUMmCNIC + Number(j[i].Col12);

			SUM5alM = SUM5alM + Number(j[i].Col15);
			SUMPenal = SUMPenal + Number(j[i].Col16);
			SUMCedular = SUMCedular + Number(j[i].Col17);
			SUMISRLaudos = SUMISRLaudos + Number(j[i].Col18);
			SUMISROtros = SUMISROtros + Number(j[i].Col24);
			SUMIVA6 = SUMIVA6 + Number(j[i].Col25);
						
			$("#subtmImportFlete").val(SUMmImporteFlete23.toFixed(2));
			$("#subHonor").val(SUMmISRHonorarios.toFixed(2));
			$("#subARR").val(SUMmISRArrenda.toFixed(2));
			$("#subFlete").val(SUMmImporteFlete4.toFixed(2));

			$("#IMDT").val(SUMmIMDT.toFixed(2));
			$("#CNIC").val(SUMmCNIC.toFixed(2));

			$("#sub5alM").val(SUM5alM.toFixed(2));
			$("#subPenal").val(SUMPenal.toFixed(2));
			$("#subCedular").val(SUMCedular.toFixed(2));

			$("#ISRLAUDOS").val(SUMISRLaudos.toFixed(2));
			$("#ISROTROS").val(SUMISROtros.toFixed(2));
			$("#ImporteIVA6").val(SUMIVA6.toFixed(2));
			$("#ImporteISRRESICO").val(SUMISRRESICO.toFixed(2));
			
			$("#subHonorSinRedondeo").val($("#subHonor").val());
			$("#subArrendaSinRedondeo").val($("#subtmImportFlete").val());
			$("#subFleteSinRedondeo").val($("#subFlete").val());

			DECmImporteFlete23 = 0; //parseFloat( $("#subtmImportFlete").val() ) -  parseInt(  $("#subtmImportFlete").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DECmImporteFlete23.toFixed(2);
			DECmISRHonorarios = 0; //parseFloat( $("#subHonor").val() ) -  parseInt(  $("#subHonor").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DECmISRHonorarios.toFixed(2);
			DECmISRArrenda = 0; //parseFloat( $("#subARR").val() ) -  parseInt(  $("#subARR").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DECmISRArrenda.toFixed(2);
			DECmImporteFlete4 = 0; //parseFloat( $("#subFlete").val() ) -  parseInt(  $("#subFlete").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DECmImporteFlete4.toFixed(2);


			Dec5alM = 0; //parseFloat( $("#sub5alM").val() ) -  parseInt(  $("#sub5alM").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			Dec5alM.toFixed(2);
			DecPenal = 0; //parseFloat( $("#subPenal").val() ) -  parseInt(  $("#subPenal").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DecPenal.toFixed(2);
			DecCedular = 0; //parseFloat( $("#subCedular").val() ) -  parseInt(  $("#subCedular").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DecCedular.toFixed(2);

			DecISRLaudos = 0; //parseFloat( $("#ISRLAUDOS").val() ) -  parseInt(  $("#ISRLAUDOS").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DecISRLaudos.toFixed(2);

			DecISROtros = parseFloat($("#ISROTROS").val()) - parseInt($("#ISROTROS").val()); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DecISROtros.toFixed(2);

			DecIva6 = 0; //parseFloat( $("#ISRLAUDOS").val() ) -  parseInt(  $("#ISRLAUDOS").val() ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
			DecIva6.toFixed(2);
			
			DecISRRESICO = 0; 
			DecISRRESICO.toFixed(2);
			
			if(tipoRetencion != 7){
				var TotalDEC = DECmImporteFlete23 + DECmISRHonorarios + DECmISRArrenda + DECmImporteFlete4 + Dec5alM + DecPenal + DecCedular + DecISRLaudos + DecISROtros + DecIva6 + DecISRRESICO;
				$("#AjusteREdon").val(TotalDEC.toFixed(2));
	
				var moperajena = Number($("#MOperAjena").val()) + Number(j[i].Col5);	
				$("#MOperAjena").val(moperajena.toFixed(2));
	
				var MonT = moperajena - TotalDEC;	
				$("#cTMonto").val(MonT);				
			}

			if (i + 1 == j.length) {
				$('#Busqueda1').dataTable().fnAddData(datosBusqueda);
				$('#Detalle').dataTable().fnAddData(datosTabla);

				$("#subtmImportFlete").formatCurrency();
				$("#subHonor").formatCurrency();
				$("#subFlete").formatCurrency();
				$("#subARR").formatCurrency();

				$("#IMDT").formatCurrency();
				$("#CNIC").formatCurrency();

				$("#sub5alM").formatCurrency();
				$("#subPenal").formatCurrency();
				$("#subCedular").formatCurrency();
				$("#ISRLAUDOS").formatCurrency();
				$("#MOperAjena").formatCurrency();
				$("#cTMonto").formatCurrency();
				$("#AjusteREdon").formatCurrency();
				$("#ImporteIVA6").formatCurrency();
				$("#ImporteISRRESICO").formatCurrency();

				marimpo = $("#subtmImportFlete").val();
				marhonor = $("#subHonor").val();
				marflete = $("#subFlete").val();
				marArre = $("#subARR").val();

				marIMDT = $("#IMDT").val();
				marCNCI = $("#CNIC").val();

				mar5alM = $("#sub5alM").val();
				marPenal = $("#subPenal").val();
				marCedular = $("#subCedular").val();
				marISRLaudos = $("#ISRLAUDOS").val();
				marMOper = $("#MOperAjena").val();
				marcTmonto = $("#cTMonto").val();
				marAjuste = $("#AjusteREdon").val();
				marISROtros = $("#ISROTROS").val();
				marIVA6 = $("#ImporteIVA6").val();
				marISRRESICO = $("#ImporteISRRESICO").val();				
			}
		}
	}
	if(tipoRetencion == 7){
		
		//var Autotransporte = Number(quitaFmt($("#subtmImportFlete").val()) ).toFixed(2);
		//var Honorario = Number(quitaFmt($("#subHonor").val()) ).toFixed(2);
		//var Arrendamiento = Number(quitaFmt($("#subARR").val()) ).toFixed(2);
		
		//var ajuste = (quitaFmt(Autotransporte) - parseInt( quitaFmt(Autotransporte),10 )) + (quitaFmt(Honorario) - parseInt( quitaFmt(Honorario),10 )) + (quitaFmt(Arrendamiento) - parseInt( quitaFmt(Arrendamiento),10 ))
		
		//$("#AjusteREdon").val(ajuste).formatCurrency();
			
		$("#subtmImportFlete").val(Number( quitaFmt($("#subtmImportFlete").val()) ).toFixed(2)).formatCurrency();
		$("#subHonor").val(Number( quitaFmt($("#subHonor").val()) ).toFixed(2)).formatCurrency();
		$("#subARR").val(Number( quitaFmt($("#subARR").val()) ).toFixed(2)).formatCurrency();
		$("#subFlete").val(Number( quitaFmt($("#subFlete").val()) ).toFixed(2)).formatCurrency();
		
		var TotalAutotransporte = Number( quitaFmt($("#subtmImportFlete").val()) ).toFixed(2);
		var TotalHonorario = Number( quitaFmt($("#subHonor").val()) ).toFixed(2);
		var TotalArrendamiento = Number( quitaFmt($("#subARR").val()) ).toFixed(2);
		var TotalFlete = Number( quitaFmt($("#subFlete").val()) ).toFixed(2);
		
		TotalDEC = Number(TotalAutotransporte) + Number(TotalHonorario) + Number(TotalArrendamiento) + Number(TotalFlete) ;
			
		$("#AjusteREdon").val(Number("0.00").toFixed(2));
			
		var moperajena = Number(Number(TotalDEC).toFixed(2));

			$("#MOperAjena").val(Number(moperajena.toFixed(2))).formatCurrency();//suma de los rubros ya redondeados
			$("#cTMonto").val(Number(moperajena.toFixed(2))).formatCurrency();//suma de los rubros ya redondeados
	}
	
	$("#ncheked").val(Nombre);

	$("#esperaDialog").dialog("close");
	parent.document.getElementById("pb_save").disabled = false;
}

	function cmdImprimir(elFormato){			
				
				window.open(
					"../admin/SeguridadCatalogos?"
						+ "catalogo=CONTRARECIBO"
						+ "&accion=run"
						+ "&rn=PolizaOperAjenas.jasper"
						+ "&whereFolio= CR.canocontrarrecibo='" + $("#caNoContrarrecibo").val() + "'",
						//+ "&nombre="   + ""
						//+ "&cargo="    + ""
						//+ "&area="     + "",
					"popacuse",
					"scrollbars=1, resizable=yes, width=1024, height=768");

					
					
}

function initSumas() {
	SUMmISRHonorarios = 0;
	SUMmISRArrenda = 0;
	SUMmImporteFlete4 = 0;
	SUMmIMDT = 0;
	SUMmCNIC = 0;
	SUM5alM = 0;
	SUMPenal = 0;
	SUMCedular = 0;
	SUMISRLaudos = 0;
	SUMISROtros = 0;
	SUMIVA6 = 0;
	SUMISRRESICO = 0;
	SUMmImporteFlete23 = 0;
	$("#MOperAjena").val("0");
}