<%@ page language="java" pageEncoding="UTF-8"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Nueva Recepción</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var roles="";
		var partidaRestringida=false;
		$(document).ready(function() {
			$("#tbs").val(0);
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			querySelectPost("tCatalogoAlmacenesReadVistas", "cIdAlmacenEntrega", {async: false});
			querySelectPost("mcatalogoEntraAlmacenCentralRead", "esAlmacenCentral", {async: false});
			//selección de valores por default en los dropdownlist
			$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
			reloadTablas();
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
		    muestraFactorAmort();
		});
		function reloadTablas(){
			oTablePartidas = $("#tblPartidas").dataTable({
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
				aaSorting: [[ 0, "asc" ]]
			});
			oTablePartidasServicios = $("#tblPartidasServicios").dataTable({
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
					aaSorting: [[ 0, "asc" ]]
			});
			oTableLienasAnticipo = $("#tblLineasAnticipo").dataTable({
					bPaginate: true,
        			bLengthChange: true,
        			bFilter: true,
        			bSort: false,
        			bInfo: true,
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
					aaSorting: [[ 0, "asc" ]]
			});
			oTableLineas = $("#tblLineas").dataTable({
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
					aaSorting: [[ 0, "asc" ]]
			});
			
		}
		function consultaDatos(){
			$("#cIdContrato").val($("#pedidoContratoCompromiso").val());
			if($("#pedidoContratoCompromiso").val()==""){
				swal("Seleccionar un contrato.",{icon:"info",button: "Cerrar"});
				return;
			}
			
			document.getElementById("lblEstadoSICOP").style.readonly=true;
			$("#cContratoDefinitivo").val($("#pedidoContratoCompromiso").val());
			queryFormPost("consultaEstadoSICOP", {async : false});
			if($("#cContratoDefinitivo").val().substring(0, 2)=="PE"){
				$("#lblEstadoSICOP").val("");
			}
			queryFormPost("obtieneDatosPedCont,obtieneDatosAnticipoEjerAnt,obtieneDatosAnticipoEjerAntNoAmortizado", {async: false, 
				callback : function() {
					var porcent=$("#nPorcentajeAnticipo").val();
					porcent=porcent.replace("%", "");
					porcent=porcent.replace(/,/g, "");
					if(parseFloat(porcent,10)<50.0 && parseInt($("#hayRecep").val(),10)==0 && ($("#rmaSinPagar").val()==''|| $("#rmaSinPagar").val()==0)){
				    	$("#trCreaAnticipo").show();
				    }
					$("#cIdRecepMat").val('');
					$("#nIdConsecutivoRecepMat").val('');
					if(!$('#checkAnticipo').is(':checked')){
						cambiaTblServicio();
					}
					cambiarTablaLineas();
					document.getElementById("agregarPartRecepMat").disabled = false;
					muestraBotonEnviar();
					partidaRestringida=elContratoTienePartidaRestringida();
					showAndHideAlmacenCentral();
				}
			});
		}
		function muestraBotonEnviar(){
			$("#divNotas").hide();
			$("#divNotasTermAnt").hide();
			$("#divNotaAnticipo").hide();
			if(esSAIAlterno){
				document.getElementById("agregarPartRecepMat").disabled = false;
				document.getElementById("EnviaPartRecepMat").disabled = false;
				if(parseInt($("#nTerminacionAnticipada").val(),10)>=1){
					if( parseInt($("#permiteHacerRecep").val(),10)==0){
						document.getElementById("agregarPartRecepMat").disabled = true;
						document.getElementById("EnviaPartRecepMat").disabled = true;	
					}
					$("#divNotasTermAnt").show();
				}
				if(parseInt($("#nOtorgaAnticipo").val(),10)==1 && parseInt($("#nFacturaGlobalCargada").val(),10)==0 ){
					$("#divNotaAnticipo").show();
					document.getElementById("agregarPartRecepMat").disabled = true;
					document.getElementById("EnviaPartRecepMat").disabled = true;
				}
			}else{
				document.getElementById("agregarPartRecepMat").disabled = false;
				document.getElementById("EnviaPartRecepMat").disabled = false;
				if(parseInt(($("#nTerminacionAnticipada").val(),10)>=1 && parseInt($("#permiteHacerRecep").val(),10)==0) 
						|| (parseInt($("#nFolioAutSICOP").val(),10)!=-1 && parseInt($("#nFolioAutSICOP").val(),10)<1) 
						|| parseInt($("#lArchivoContCargado").val(),10)==0 
						|| (parseInt($("#nOtorgaAnticipo").val(),10)==1 && parseInt($("#nFacturaGlobalCargada").val(),10)==0 ) 
				){
					document.getElementById("agregarPartRecepMat").disabled = true;
					document.getElementById("EnviaPartRecepMat").disabled = true;
				}
				if(parseInt($("#lArchivoContCargado").val(),10)==0){
					$("#divNotas").show();
				}
				if(parseInt($("#nTerminacionAnticipada").val(),10)>=1){
					$("#divNotasTermAnt").show();
				}
				if(parseInt($("#nOtorgaAnticipo").val(),10)==1 && parseInt($("#nFacturaGlobalCargada").val(),10)==0){
					$("#divNotaAnticipo").show();
				}
			}
		}
		
		function muestraDatosAnticipo(){
			var qw="1=1 and cIdpedContDef='"+$('#cIdContrato').val()+"' and cIdRecepMat='"+$('#cIdRecepMat').val()+"'" ;
			oTableLienasAnticipo = $("#tblLineasAnticipo").dataTable({
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRecepcionpMatAnticipo&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{sName: "cIdRecepMat"},
					{sName: "cIdpedContDef"},
					{sName: "mMontoAnticipoConIVA"},
					{sName: "mMontoAnticipoSinIVA"},
					{sName: "mMontoAnticipoIVA"},
					{sName: "nPorcentaje"}
				]
			});
		}
		function agregar(){
			if($('#checkAnticipo').is(':checked')){
				crearAnticipo();
			}else{
				if($("#rmaSinPagar").val()=='EXISTE'){
					swal("Hay anticipos que no se han pagado.\nTienes que pagar el anticipo para poder hacer una recepción de material.",{icon:"info",button: "Cerrar"});
					return;
				}else{
					if(esContratoDeBienes() && $("#esAlmacenCentral").val()==0 &&!partidaRestringida){
						swal("¡Especifica si los bienes se entregarán en el almacén central!",{icon:"warning",button: "Cerrar"});
						return;
					}else{
						crearSolAbastecimiento();	
					}
				}
			}
			$("#trCreaAnticipo").hide();
		}
		function crearAnticipo(){
			$("#textMontoConIVA_Ant").val(quitaFmt2('textMontoConIVA_Ant'));
			if(parseFloat($("#textMontoConIVA_Ant").val())<=0.00){
				swal("No se puede agregar monto 0.00.",{icon:"info",button: "Cerrar"});
				$("#textMontoConIVA_Ant").formatCurrency();
				return;
			}
			if($("#cIdRecepMat").val()==''){
				queryFormPost("mRecepMat_siguienteConsecutivoReadAnticipo", {async: false, 
					callback : function() 
					{
						$("#cIdRecepMat").val('RA-'+$("#cIdUnidadEjecutora").val()+'-'+$("#nIdConsecutivoRecepMat").val());
						agregaAnticipo();
						$("#textcidRecepcion").val($("#cIdRecepMat").val());
					}
				});
				//Guarda en la Bitácora
				guardaBitacora("CREA ANTICIPO");
			}else{
				agregaAnticipo();
				$("#textcidRecepcion").val($("#cIdRecepMat").val());
				//Guarda en la Bitácora
				guardaBitacora("ACTUALIZA ANTICIPO");
			}
		}
		function agregaAnticipo(){
			var porcent=$("#textporcent_Ant").val();
			porcent=porcent.replace("%", "");
			porcent=porcent.replace(/,/g, "");
			$("#cIdContrato").val($("#pedidoContratoCompromiso").val());
			$("#textporcent_Ant").val(porcent);
			$("#textMontoConIVA_Ant").val(quitaFmt2('textMontoConIVA_Ant'));
			$("#textMontoSinIVA_Ant").val(quitaFmt2('textMontoSinIVA_Ant'));
			$("#textMontoIVA_Ant").val(quitaFmt2('textMontoIVA_Ant'));
			queryFormPost("creaActualizaRecepMatAnticipo",  {async : false, 
					callback : function() 
					{
						queryFormPost("mUpdateRecepMatAmort",  {async : false, 
						callback : function() 
						{
							muestraDatosAnticipo();
							queryFormPost("mRecepMat_montosTtales",{async: false});
							queryFormPost("mRecepMat_montosOtrosImp",{async: false});
							$("#textMontoConIVA_Ant").val().formatCurrency();
							$("#textMontoSinIVA_Ant").val().formatCurrency();
							$("#textMontoIVA_Ant").val().formatCurrency();
							$("#textporcent_Ant").val(porcent+' %');
						}
					});
					}
				});
		}
		
		function EliminaLinea(nlineaConsolidado){
			var aTrs = $('#tblLineas').dataTable().fnGetNodes();
			if(aTrs.length<=1){
				swal("No Puedes eliminar todas las líneas",{icon:"info",button: "Cerrar"});
				return;
			}
			$("#nlineaConsolidado").val(nlineaConsolidado);
			queryFormPost("mEliminaLineaRecepMat",  {async : false, 
				callback : function() 
				{
					if(($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()=='') 
							||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='') 
							||( $("#nServicio_A_Bienes").val()==1 ) || $("#cIdTipoProcedimiento").val()=='PT'){
						muestraDatos();
					}else{
						muestraDatosServicios();
					}
					muestraLineas();
					//sumatoria
					queryFormPost("mRecepMat_montosTtales",{async: false});
					swal("Línea Eliminada",{icon:"info",button: "Cerrar"});
				}
			});
			//Guarda en la Bitácora
			guardaBitacora("SE ELIMINA LINEA DE RECEPCIÓN");
		}
		
		/*VGC20210622 Se valida que: Si la unidad del contrato requiere atenta nota para la RM y es un contrato, muestra
		* una pantalla para la captura de la informacion y se envia a firma electronica.
		*/
		function enviar(){
			
			if($('#checkAnticipo').is(':checked')){
				enviarAnticipo();
			}else{
				enviaSolAbaste();
			}
			
		}
		
		function enviarAnticipo(){
			
			
			var aTrs = $('#tblLineasAnticipo').dataTable().fnGetNodes();
			
			if($("#calculosErroneosAnticipo").val()==1){
				swal("Captura el monto correcto del IVA",{icon:"info",button: "Cerrar"});
				return;
			}
			if($("#cIdRecepMat").val()!='' && $("#cIdContrato").val()!=''&& aTrs.length>0){
				
				if( requiereAutorizacion() ){
					capturaNota();
				}else{
					queryFormPost("mUpdateRecepMat",  {async : false, 
						callback : function() 
						{
							//Guarda en la Bitácora
							guardaBitacora("ANTICIPO EMITIDO PARA SU PAGO");
							swal("Ya se puede hacer el pago del Anticipo",{icon:"info",button: "Cerrar"});
							window.location = "RecepcionMaterial.jsp?tab=1";
						}
				
					});
				}
				
			}else{
				swal("No hay nada que enviar.",{icon:"info",button: "Cerrar"});
			}
		}
		function cambiarTablaLineas(){
			if($('#checkAnticipo').is(':checked')){
				$("#divTblPartidas").css("display", "none");
				$("#divTblPartidasServicios").css("display", "none");
				$("#cIdAlmacenEntrega").val('.');
				$("#trcIdAlmacen").css("display", "none");
				$("#divTblLineasAnticipo").show();
				$("#divtblLineas").hide();
				$("#divAnticipo").show();
				$("#GuardaAjusteCentavos").hide();
				muestraDatosAnticipo();
			}else{
				//alert($("#esDescentralizado").val()+"     "+$("#cIdTipoProcedimiento").val())
				if($("#esDescentralizado").val() =='1'){
					queryFormPost("esCucopDeGasolinaDesc",  {async : false, 
						callback : function() 
						{
							if(($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()=='')
									||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='')
									||( $("#nServicio_A_Bienes").val()==1 ) || $("#cIdTipoProcedimiento").val()=='PT'  ){
								$("#divTblPartidas").show();
								$("#divTblPartidasServicios").hide();
								$("#GuardaAjusteCentavos").show();
							}else{
								$("#divTblPartidasServicios").show();
								$("#divTblPartidas").hide();
								$("#GuardaAjusteCentavos").hide();
							}
						}
					});
				}else{
					queryFormPost("esCucopDeGasolina",  {async : false, 
						callback : function() 
						{
							if(($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()=='')
									||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='')
									||( $("#nServicio_A_Bienes").val()==1  ) ||$("#cIdTipoProcedimiento").val()=='PT' ){
								$("#divTblPartidas").show();
								$("#divTblPartidasServicios").hide();
								$("#GuardaAjusteCentavos").show();
							}else{
								$("#divTblPartidasServicios").show();
								$("#divTblPartidas").hide();
								$("#GuardaAjusteCentavos").hide();
							}
						}
					});
				}
				$("#trcIdAlmacen").css("display", "block");
				$("#divtblLineas").show();
				$("#divTblLineasAnticipo").hide();
				$("#divAnticipo").hide();
			}
		}
		function cambiaTblServicio(){
			$('#tblPartidas').dataTable().fnClearTable();
			$('#tblPartidasServicios').dataTable().fnClearTable();
			var cIdContrato=$("#cIdContrato").val();
			muestraRowsAnticiposEjerAnt();
			if($("#esDescentralizado").val() =='1'){
				if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
					queryFormPost("esCucopDeGasolinaContPluriDesc",  {async : false});
				}else{
					queryFormPost("esCucopDeGasolinaDesc",  {async : false});
					if($("#esCucopGasolina").val()==''){
						queryFormPost("esCucopDeGasolina",  {async : false});
					}
				}
			}else{
				if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
					queryFormPost("esCucopDeGasolinaPluri",  {async : false});
				}else{
					if($("#isConvEjercicioAnt").val()==1){
						queryFormPost("esCucopDeGasolina_ContRemanente",  {async : false});
					}else{
						queryFormPost("esCucopDeGasolina",  {async : false});
					}
				}
			}
			$('#tblPartidas').dataTable().fnClearTable();
			$('#tblPartidasServicios').dataTable().fnClearTable();
			if(($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()=='')
					||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='')
					||( $("#nServicio_A_Bienes").val()==1 ) || $("#cIdTipoProcedimiento").val()=='PT'){
				$("#divTblPartidas").show();
				$("#divTblPartidasServicios").hide();
				muestraDatos();
			}else{
				$("#divTblPartidasServicios").show();
				$("#divTblPartidas").hide();
				muestraDatosServicios();
			}
			muestraLineas();
		}
		function frmt(dlt){
			$("#"+dlt.id).formatCurrency();
		}
		function quitaFmt(dlt) {
			var val=$("#"+dlt.id).val();
			val = val.replace("$", "");
			val = val.replace(/,/g, "");
			if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
			 }
			 $("#"+dlt.id).val(val);
		}
		function quitaFmt2(dlt) {
			var val=$("#"+dlt).val();
			val = val.replace("$", "");
			val = val.replace(/,/g, "");
			if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
			 }
			 return val;
		}
		function calculaPorcentajeSinIVA(){
			var subTotalPedCont=0;
			var montoTotalPluri=0
			var porcentGuardado=$("#nPorcentajeAnticipo").val();
			totalPedCont=quitaFmt2("montoBruto");
			montoTotalPluri=quitaFmt2("mMontoTotalPlurianual");
			if(parseFloat(montoTotalPluri)>0){
				totalPedCont=montoTotalPluri;
			}
			totalCap=quitaFmt2("textMontoSinIVA_Ant");
			var totalCapIVA=quitaFmt2("textMontoIVA_Ant");
			if(parseFloat(totalCap)<=0){
				swal("El subtotal no puede ser 0.0",{icon:"info",button: "Cerrar"});
			}
			x=((parseFloat(totalCap)*100)/parseFloat(totalPedCont)) +parseFloat(porcentGuardado);
			//if(x>50 && false){
			if(x>50){
				swal("No puedes dar mas del 50 % de anticipo del subtotal del contrato "+totalPedCont,{icon:"info",button: "Cerrar"});
				$("#textMontoConIVA_Ant").val('$0.00');
				$("#textMontoSinIVA_Ant").val('$0.00');
				$("#textporcent_Ant").val('0.00 %');
			}else{
				x=(parseFloat(totalCap)*100)/parseFloat(totalPedCont);
				$("#textporcent_Ant").val(x.toFixed(2)+' %');
				$("#textMontoConIVA_Ant").val(totalCap+totalCapIVA);
				$("#textMontoConIVA_Ant").formatCurrency();
				document.getElementById("textMontoIVA_Ant").readOnly = "";
			}
		}
		function calculaPorcentaje(){
			var x=0;
			var totalPedCont=0;
			var totalCap=0;
			var montoTotalPluri=0
			var porcentGuardado=$("#nPorcentajeAnticipo").val();
			porcentGuardado=porcentGuardado.replace("%","");
			porcentGuardado = porcentGuardado.replace(/\,/g,'');
			montoTotalPluri=quitaFmt2("mMontoTotalPlurianual");
			totalPedCont=quitaFmt2("montoNeto");
			
			if(parseFloat(montoTotalPluri)>0){
				totalPedCont=montoTotalPluri;
			}
			totalCap=quitaFmt2("textMontoConIVA_Ant");
			var totalCapIVA=quitaFmt2("textMontoIVA_Ant");
			if(parseFloat(totalCap)<=0){
				swal("El Monto con iva no puede ser 0.0",{icon:"info",button: "Cerrar"});
				
			}
			x=((parseFloat(totalCap)*100)/parseFloat(totalPedCont)) +parseFloat(porcentGuardado);
			//if(x>50 && false){
			if(x>50){
				swal("No puedes dar mas del 50 % de anticipo del total del contrato "+totalPedCont,{icon:"info",button: "Cerrar"});
				$("#textMontoConIVA_Ant").val('$0.00');
				$("#textMontoSinIVA_Ant").val('$0.00');
				$("#textporcent_Ant").val('0.00 %');
			}else{
				x=(parseFloat(totalCap)*100)/parseFloat(totalPedCont);
				$("#textporcent_Ant").val(x.toFixed(2)+' %');
				$("#textMontoSinIVA_Ant").val(totalCap-totalCapIVA);
				$("#textMontoSinIVA_Ant").formatCurrency();
				document.getElementById("textMontoIVA_Ant").readOnly = "";
			}
		}
		function calculaMontoSinIVA(){
			var totalCapConIVA=quitaFmt2("textMontoConIVA_Ant");
			var totalCapIVA=quitaFmt2("textMontoIVA_Ant");
			if( (parseFloat(totalCapConIVA) - parseFloat(totalCapIVA)) < 0){
				swal("Tus calculos son erroneos. No puedes pagar mas de iva.",{icon:"info",button: "Cerrar"});
				$("#calculosErroneosAnticipo").val(1);
			}else{
				$("#textMontoSinIVA_Ant").val( parseFloat(totalCapConIVA) - parseFloat(totalCapIVA));
				$("#textMontoSinIVA_Ant").formatCurrency();
			}
		}
		function limpiaClvContrato(){
			$("#pedidoContratoCompromiso").val('');
		}
		function Guarda(){
			var cadenaLineaConsCant="";
			if(($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()=='')
					||($("#cIdTipoProcedimiento").val()=='PR'  && $("#cucopGasolina").val()=='')
					||( $("#nServicio_A_Bienes").val()==1 ) || $("#cIdTipoProcedimiento").val()=='PT'){
				var aTrs = $('#tblLineas').dataTable().fnGetNodes();
				var token="";
				var nTr="";
				if(aTrs.length==0){
					swal("No hay nada que modificar.",{icon:"info",button: "Cerrar"});
					return;
				}
				for ( var i=0 ; i<aTrs.length; i++ )     
				{  
					nTr = $('#tblLineas').dataTable().fnGetData(aTrs[i]);
					cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[1]+'-'+unFormatCurrency($("#mmontoConIVA_"+nTr[1]).val());
					token=",";
				}
				$("#cadenaLineaCantidad").val(cadenaLineaConsCant);
				queryFormPost("actualizaCentRecepMat",  {async : false, 
					callback : function() 
					{
						muestraDatos();
						muestraLineas();
						queryFormPost("mRecepMat_montosTtales",{async: false});
						queryFormPost("mRecepMat_montosOtrosImp",{async: false});
						//Guarda en la Bitácora
						guardaBitacora("AJUSTE DE CENTAVOS");
						swal("Datos Actualizados.",{icon:"info",button: "Cerrar"});
					}
				});
			}
		}
		function calcualOtrosImp(){
			var oImpGuardar=unFormatCurrency($("#otrosImpGuardar").val());
			var oImpRemanente=unFormatCurrency($("#otrosImpRemanente").val());
			if(oImpGuardar>oImpRemanente){
				$("#otrosImpGuardar").val(0);
			}
		}
		function guardaBitacora(accion){
			$("#cAccion").val(accion);
			$("#cIdDocumento").val($("#cIdContrato").val()+", "+$("#cIdRecepMat").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
		}
		function muestraFactorAmort(){
			document.getElementById("checkFactAmort").checked=false;
			$("#factAmort").val(0);
			queryFormPost("esUEParaFactAmort", { async : false,
				callback : function() {
					if($("#existeUEFactAmort").val()=="EXISTE"){
						$("#trFactorAmortizacion").show();
					}else{
						$("#trFactorAmortizacion").hide();
					}
				}	
			});
			$("#existeUEFactAmort").val('');	
		}
		function ischeckedFactorAmort(){
			if($('#checkFactAmort').is(':checked')){
				$("#factAmort").val(1);
			}else{
				$("#factAmort").val(0);
			}
		}
		function desactivaBackspace(event){
			if(window.event && window.event.keyCode == 8){
		     	window.event.keyCode = 505;
	    	}
		    if(window.event && window.event.keyCode == 505){
	    	 	return false;
	    	}
	    	return true;
		}
		function muestraRowsAnticiposEjerAnt(){
			var cIdContrato=$("#cIdContrato").val();
			$("#trAntEjerciciosAnt").hide();
			$("#trMontosAntEjerciciosAnt").hide();
			$("#trAnticipoNoAmortEjerciciosAnt").hide();
			$("#trMontosAnticipoNoAmortEjerciciosAnt").hide();
			if(cIdContrato.indexOf("PLU")>=0 && $("#esProcesoNormPluri").val()==0){
				$("#trAntEjerciciosAnt").show();
				$("#trMontosAntEjerciciosAnt").show();
				$("#trAnticipoNoAmortEjerciciosAnt").show();
				$("#trMontosAnticipoNoAmortEjerciciosAnt").show();
				queryFormPost("anticipoPlurEjerAnt",  {async : false, 
					callback : function() 
					{queryFormPost("anticipoPlurActual",  {async : false, 
							callback : function() 
							{	
								if($("#montoBrutoAnticipoEjerAnt").val()>0){
									remamenteAnticipo=true;
									
								}
								$("#montoBrutoAnticipoEjerAnt").formatCurrency();
								$("#montoAnticipoIVAEjerAnt").formatCurrency();
								$("#montoNetoAnticipoEjerAnt").formatCurrency();
								$("#montoBrutoAnticipo").formatCurrency();
								$("#montoAnticipoIVA").formatCurrency();
								$("#montoNetoAnticipo").formatCurrency();
								queryFormPost("AmortPlurActual",  {async : false, 
									callback : function() 
									{	
										
										var totalAmort=(quitaFmt2("montoNetoAnticipoEjerAnt")-quitaFmt2("montoNetoAmortizadoEjerAnt")).toFixed(2);
										var subtotalAmort=(totalAmort/(1+(0.01*($("#nPorcentajeIVA").val())))).toFixed(2);
										var ivaAmort=totalAmort-subtotalAmort;
										$("#montoBrutoAnticipoNoEjercidoEjerAnt").val(subtotalAmort);
										$("#montoAnticipoIVANoEjercidoEjerAnt").val(ivaAmort);
										$("#montoNetoAnticipoNoEjercidoEjerAnt").val(totalAmort);
										$("#montoBrutoAnticipoNoEjercidoEjerAnt").formatCurrency();
										$("#montoAnticipoIVANoEjercidoEjerAnt").formatCurrency();
										$("#montoNetoAnticipoNoEjercidoEjerAnt").formatCurrency();
										var porcent=$("#nPorcentajeAnticipo").val();
										porcent=porcent.replace("%", "");
										porcent=porcent.replace(/,/g, "");
										if(totalAmort>0){
											$("#isAmortizaEjercAnt").val("1");
										}	
										if(parseFloat(porcent)<50.0 && totalAmort==0  && ($("#rmaSinPagar").val()==''|| $("#rmaSinPagar").val()==0)){
									    	$("#trCreaAnticipo").show();
									    }
									}
								});
							}
						}); 						
					}
				});
			}
		}
		function cambiaCentrocontableUsuario(){
			if($("#cIdUnidadEjecutora").val()!='0'){
				$.ajax({
					url: '../../servlet/CambiaPropiedadesUsuario',
					dataType: 'json',
					data: {"UnidadEjecutora" : $("#cIdUnidadEjecutora").val()},
					async : false,
					success : function(j) {
						if(j[0].error){
							swal("No se hizo el cambio de centro contable y unidad ejecutora",{icon:"info",button: "Cerrar"});
						}else{
							$("#cIdUnidadEjecutoraUsuario").val(j[0].unidadEjecutora);
						}
						
					}
				});
			}
		}
		function showAndHideAlmacenCentral(){
			$("#trSeEntregaAlmacenCentral").show()
			if(!esContratoDeBienes() || partidaRestringida ){
				$("#trSeEntregaAlmacenCentral").hide();
				$("#esAlmacenCentral").val(0);
			}
		}
		
	</script>
  </head>
  <body>
    <form id="formNuevaRecep">
    	<div id="container" class="container" style="width: 90%;">
    		<div class= "card">
				<div class="card-header">
				    <h5>Nueva Recepci&oacute;n de Material</h5>
				</div>
				<div class="card-body">
					<div class="row">
		    			<div class="col-3">
		    				Unidad Ejecutora:
		    			</div>
		    			<div class="col-6">
		    				 <select name='cIdUnidadEjecutora' id='cIdUnidadEjecutora' class="form-select" onchange="limpiaClvContrato(); muestraFactorAmort(); cambiaCentrocontableUsuario();"></select>
		    			</div>
		    		</div>
		    		<div class="row">
		    			<div class="col-3">
		    				Clave de Contrato: 
		    			</div>
		    			<div class="col-6">
		    				<div class="input-group">
		    				 <input type="text" class="AyudaSyC form-control" maxlength="40" size="50" name="pedidoContratoCompromiso" id=pedidoContratoCompromiso readonly onkeydown="return(desactivaBackspace(event))" />
		    				</div>
		    			</div>
		    			<div class="col-3">
		    				<input value="Buscar"  id="bucaDatosRecepMat" name="bucaDatosRecepMat" type="button" onclick="consultaDatos()" class="btn btn-secondary"/>
		    			</div>
		    		</div>
				</div>
			</div>
    		
    		
    		<div class= "card">

				<div class="card-body">
					<div id="divNotas" class="row" style="display: none;">
	    				<div id="notas" class="col-12">
	    					<span id="notaContFisico" style="color: red;">Todos los contratos cargados en SAI a partir del 01/09/2020 sera necesario la carga del contrato físico para poder hacer recepciones.</span>
	    				</div>
	    			</div>
	    			<div id="divNotaAnticipo" class="row" style="display: none;">
	    				<div id="notaAnticipo" class="col-12">
   							<span id="notaContAnticipo" style="color: red;">Contratos que se les otorg&oacute; anticipo, ser&aacute; necesario la carga de factura global en el m&oacute;dulo de contratos para poder hacer recepciones.</span>
	    				</div>
	    			</div>
	    			<div id="divNotasTermAnt" class="row" style="display: none;">
    					<div id="notasTermAnt" class="col-12" style="width: 100%">
    							<input id="cTerminacionAnticipada" name="cTerminacionAnticipada" style="width: 100%; border: 0px none;background:#FEFEFE; color:red" class="form-control" value="" readonly />
    					</div>
    				</div>
    				<div class="row">
    					<div class="col-1"></div>
		    			<div class="col-2">
		    				<input type="text" style="width: 500px; border: 0px none;background:#FEFEFE; color:blue;" name="FolioSicop" id="FolioSicop" readonly />
		    			</div>
		    			<div class="col-3">
		    				<input type="text" style="width: 500px;border: 0px none;background:#FEFEFE; color: blue;" name="lblEstadoSICOP" id="lblEstadoSICOP" readonly />
		    			</div>
		    		</div>
		    		
					<div class="row">
		    			<div class="col-3">
		    				Concepto:
		    			</div>
		    			<div class="col-9">
		    				 <textarea name="descripcionPedCont" rows="3" readonly ID="descripcionPedCont" class="form-control" onkeydown="return(desactivaBackspace(event))"></textarea>
		    			</div>
		    		</div>
		    		<div class="row mt-2">
		    			<div class="col-3">
		    				SubTotal: <input style="text-align:right;" readonly type="text" maxlength="16" size="12" name="montoBruto"  id="montoBruto" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))" />
		    			</div>
		    			<div class="col-3">
		    				Importe IVA: <input style="text-align:right;" readonly type="text" maxlength="16" size="10" name="montoIVA"  id="montoIVA" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))" />
		    			</div>
		    			<div class="col-3">
		    				Otros Imp: <input style="text-align:right;" readonly type="text" maxlength="16" size="10" name="montoOtrosImp"  id="montoOtrosImp" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))" />	
		    			</div>
		    			<div class="col-3">
		    				Total: <input style="text-align:right;" readonly type="text" maxlength="16" size="16" name="montoNeto"  id="montoNeto" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))" />	
		    			</div>
		    		</div>
		    		<div class="row">
		    			<div class="col-12"><b>Anticipo Ejercicio Actual.</b>
		    			</div>
		    		</div>
		    		<div class="row">
		    			<div class="col-3">
		    				SubTotal: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="montoBrutoAnticipo"  id="montoBrutoAnticipo" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))" />
		    			</div>
		    			<div class="col-3">
		    				Importe IVA: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="montoAnticipoIVA"  id="montoAnticipoIVA" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))"/>
		    			</div>
		    			<div class="col-3">
		    				Total: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="montoNetoAnticipo"  id="montoNetoAnticipo" value="$ 0.00" class="form-control"  onkeydown="return(desactivaBackspace(event))" />
		    			</div>
		    			<div class="col-3">
		    				Porcentaje: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="nPorcentajeAnticipo"  id="nPorcentajeAnticipo" value="0.0 %" class="form-control" onkeydown="return(desactivaBackspace(event))" />
		    			</div>
		    		</div>
		    		<div class="row" id="trAntEjerciciosAnt" style="display: none;">
		    			<div class="col-12"><b>Anticipo Ejercicio Años Anteriores.</b>
		    			</div>
		    		</div>
		    		<div id="trMontosAntEjerciciosAnt" style="display: none;">
			    		<div class="row">
			    			<div class="col-3">
			    				SubTotal: <input style="text-align:right;" readonly type="text" maxlength="12" name="montoBrutoAnticipoEjerAnt"  id="montoBrutoAnticipoEjerAnt" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))" />
			    			</div>
			    			<div class="col-3">
			    				Importe IVA: <input style="text-align:right;" readonly type="text" maxlength="12" name="montoAnticipoIVAEjerAnt"  id="montoAnticipoIVAEjerAnt" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))"/>
			    			</div>
			    			<div class="col-3">
			    				Total: <input style="text-align:right;" readonly type="text" maxlength="12" name="montoNetoAnticipoEjerAnt"  id="montoNetoAnticipoEjerAnt" value="$ 0.00" class="form-control"  onkeydown="return(desactivaBackspace(event))" />
			    			</div>
			    			<div class="col-3">
			    				Porcentaje: <input style="text-align:right;" readonly type="text" maxlength="12" name="nPorcentajeAnticipoEjerAnt"  id="nPorcentajeAnticipoEjerAnt" value="0.0 %" class="form-control" onkeydown="return(desactivaBackspace(event))" />		
			    			</div>
			    		</div>
		    		</div>
		    		<div class="row" id="trAnticipoNoAmortEjerciciosAnt" style="display: none;">
		    			<div class="col-12">
		    				<b>Anticipo No Amortizado de Ejercicios Anteriores.</b>
		    			</div>
		    		</div>
		    		<div class="row" id="trMontosAnticipoNoAmortEjerciciosAnt" style="display: none;">
    					<div class="col-3">
    						SubTotal: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="montoBrutoAnticipoNoEjercidoEjerAnt"  id="montoBrutoAnticipoNoEjercidoEjerAnt" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))" />
    					</div>
		    			<div class="col-3">
		    				Importe IVA: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="montoAnticipoIVANoEjercidoEjerAnt"  id="montoAnticipoIVANoEjercidoEjerAnt" value="$ 0.00" class="form-control" onkeydown="return(desactivaBackspace(event))"/>
		    			</div>
		    			<div class="col-3">
		    				Total: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="montoNetoAnticipoNoEjercidoEjerAnt"  id="montoNetoAnticipoNoEjercidoEjerAnt" value="$ 0.00" class="form-control"  onkeydown="return(desactivaBackspace(event))" />
		    			</div>
		    			<div class="col-3">
		    				Porcentaje: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="nPorcentajeAnticipoNoEjercidoEjerAnt"  id="nPorcentajeAnticipoNoEjercidoEjerAnt" value="0.0 %" class="form-control" onkeydown="return(desactivaBackspace(event))" />
		    			</div>
		    		</div>
		    		<br>
		    		<div class="row" id="trSeEntregaAlmacenCentral">
    					<div class="col-3">
    						Se recepcionara en almac&eacute;n central: 
    					</div>
    					<div class="col-3">
    						<select name="esAlmacenCentral" id="esAlmacenCentral" class="form-select"></select>
    					</div>
    				</div>
    				<div id="trcIdAlmacen">
    					<div class="row">
	    					<div class="col-3">
	    						Lugar de Entrega:
	    					</div>
	    					<div class="col-9">
	    						<select name="cIdAlmacenEntrega" id="cIdAlmacenEntrega" class="form-select"></select>
	    					</div>
    					</div>
    				</div>
    				
    				<div id="trOtrosImp" style="display: block;">
	    				<div class="row">
	    					<div class="col-3"></div>
	    					<div class="col-3">
	    						Remanente Otros Impuestos
	    						<input type="text" name="otrosImpRemanente" id="otrosImpRemanente" value="$ 0.0" class="form-control" onkeydown="return(desactivaBackspace(event))" readonly/>
	    					</div>
	    					<div class="col-3">
	    						Captura Monto OtrosImp
	    						<input type="text" name="otrosImpGuardar" id="otrosImpGuardar" value="$0.0" class="form-control" onkeypress="return onlyMoney(event)" onfocus="quitaFmt(this)" onblur="calcualOtrosImp();frmt(this);"/>
	    					</div>
	    				</div>
    				</div>
    				<div class="row">
	    					<div class="col-3" id="trFactorAmortizacion" style="display: block;">
	    						Lleva factor de amortización
	    						<input type="checkbox" id="checkFactAmort" name="checkFactAmort" value="0" onclick="ischeckedFactorAmort();" />
	    					</div>
	    					<div class="col-3" id="trCreaAnticipo" style="display: none;">
	    						Crear Anticipo: 
	    						<input type="checkbox" id="checkAnticipo" name="checkAnticipo" onclick="cambiarTablaLineas()" />
	    					</div>
    				</div>
    				<br>
    				<div id="divTblPartidas" style="display: none;">
		    			<table id="tblPartidas" class="display" >
							<thead >
								<tr>
									<th align="center">Linea</th>
									<th align="center">Partida</th>
									<th align="center">CUCOP</th>
									<th align="center">DESCRIPCI&Oacute;N</th>
									<th align="center">CANTIDAD<br />Total</th>
									<th align="center">CANTIDAD<br />Disponible</th>
									<th align="center">Monto <br />Total</th>
									<th align="center">CANTIDAD<br />A <br />Agregar</th>
									<th align="center">Monto<br />Descuento<br />Sin IVA </th>
									<th align="center">UNIDAD DE MEDIDA</th>
									<th align="center">IVA</th>
									<th align="center">PrecioU</th>
								</tr>										
							</thead>
						</table>
					</div>
					<div id="divTblPartidasServicios" style="display: none;">
			    			<table id="tblPartidasServicios" class="display" >
								<thead >
									<tr>
										<th align="center">Linea</th>
										<th align="center">Partida</th>
										<th align="center">CUCOP</th>
										<th align="center">Descripción</th>
										<th align="center">Monto <br />Total</th>
										<th align="center">Monto <br />Total Disp</th>
										<th align="center">Monto<br />Con <br />IVA</th>
										<th align="center">Monto<br />Descuento<br />Sin IVA </th>
										<th style="display: none;">Monto<br />Sin <br />IVA</th>
										<th style="display: none;">Monto<br />IVA</th>
										<th align="center">Unidad Medida</th>
										<th align="center">IVA</th>
										<th align="center">PrecioU</th>
									</tr>										
								</thead>
							</table>
					</div>
					<div align="right">
						<input type="button" id="agregarPartRecepMat" name="agregarPartRecepMat" value="Agregar" onclick="agregar()" disabled="disabled" class="btn btn-secondary"/>
					</div>
				</div>
			</div>
    		<div class= "card" id="divAnticipo" style="display: none;">
				<div class="card-body">
					<div class="row">
						<div class="col-3">
							Monto Con IVA :
							<input type="text" id="textMontoConIVA_Ant" name="textMontoConIVA_Ant" value="$0.00" onfocus="quitaFmt(this)" onblur="frmt(this);calculaPorcentaje();" class="form-control"/>
						</div>
						<div class="col-3">
							Monto IVA :
							<input type="text" id="textMontoIVA_Ant" name="textMontoIVA_Ant" value="$0.00" onfocus="quitaFmt(this)" onblur="frmt(this);calculaMontoSinIVA()" readonly class="form-control"/>
						</div>
						<div class="col-3">
							Monto Sin IVA :
							<input type="text" id="textMontoSinIVA_Ant" name="textMontoSinIVA_Ant" value="$0.00" onfocus="quitaFmt(this)" onblur="frmt(this);calculaPorcentajeSinIVA();"  readonly class="form-control"/>
						</div>
						<div class="col-3">
							Porcentaje :
							<input type="text" id="textporcent_Ant" name="textporcent_Ant" value="0.00 %" readonly class="form-control"/>
						</div>
					</div>
					
				</div>
			</div>
			<div class= "card">
				<div class="card-header">
				    <h5>Cantidad a Recepcionar</h5>
				</div>
				<div class="card-body">
					<div class="row">
						<div class="col-2">
							IdRecepci&oacute;n :
						</div>
						<div class="col-4">
							<input type="text" id="textcidRecepcion" readonly name="textcidRecepcion" value="" style="border-width:0; background-color:transparent;"/>
						</div>
						<div class="col-2">
							Descuento Con IVA :
						</div>
						<div class="col-4">
							<input type="text" id="textDescuentoConIVA" readonly name="textDescuentoConIVA" value="" style="border-width:0; background-color:transparent;"/>
						</div>
					</div>
					<div class="row">
						<div class="col-2">
							Monto Con IVA :
						</div>
						<div class="col-4">
							<input type="text" id="textMontoConIVA" readonly name="textMontoConIVA" value="" style="border-width:0; background-color:transparent;"/>
						</div>
						<div class="col-2">
							Descuento Sin IVA :
						</div>
						<div class="col-4">
							<input type="text" id="textDescuentoSinIVA" readonly name="textDescuentoSinIVA" value="" style="border-width:0; background-color:transparent;"/>
						</div>
					</div>
					<div class="row">
						<div class="col-2">
							Monto Sin IVA :
						</div>
						<div class="col-4">
							<input type="text" id="textMontoSinIVA" readonly name="textMontoSinIVA" value="" style="border-width:0; background-color:transparent;"/>
						</div>
						<div class="col-2">
							Descuento IVA :
						</div>
						<div class="col-4">
							<input type="text" id="textDescuentoIVA" readonly name="textDescuentoIVA" value="" style="border-width:0; background-color:transparent;"/>
						</div>
					</div>
					<div class="row">
						<div class="col-2">
							Monto IVA :
						</div>
						<div class="col-4">
							<input type="text" id="textMontoIVA" readonly name="textMontoIVA" value="" style="border-width:0; background-color:transparent;"/>
						</div>
						<div class="col-2">
							Otros Imp :
						</div>
						<div class="col-4">
							<input type="text" id="textMontoOtrosImp" readonly name="textMontoOtrosImp" value="" style="border-width:0; background-color:transparent;"/>
						</div>
					</div>
				</div>
			</div>
    		<fieldset>
    			<div id="divtblLineas">
    			<table id="tblLineas" class="display" >
					<thead >
						<tr>
							<th style="display: none;"></th>
							<th align="center">Linea</th>
							<th align="center">IdRecepMat</th>
							<th align="center">Cantidad</th>
							<th align="center">MontoConIVA</th>
							<th align="center">MontoSinIVA</th>
							<th align="center">MontoIVA</th>
							<th align="center">Eliminar</th>
							<th style="display: none;">IdPedCont</th>														
						</tr>										
					</thead>
				</table>
				</div>
				<div id="divTblLineasAnticipo" style="display: none;">
	    			<table id="tblLineasAnticipo" class="display" >
						<thead >
							<tr>
								<th align="center">Anticipo</th>
								<th align="center">Pedido/Contrato</th>
								<th align="center">Monto Con <br />IVA</th>
								<th align="center">Monto Sin<br />IVA</th>
								<th align="center">Monto <br />IVA</th>
								<th align="center">Porcentaje</th>
							</tr>										
						</thead>
					</table>
				</div>
				<br />
				<div align="center">
					<input type="button" style="display: none;" title="Guardar el ajuste de centavos en cada linea" id="GuardaAjusteCentavos" name="GuardaAjusteCentavos" value="Guardar" onclick="Guarda();" class="btn btn-secondary"/>
				</div>
				<div align="right">
					<input type="button" id="EnviaPartRecepMat" name="EnviaPartRecepMat" value="Enviar" onclick="enviar();" class="btn btn-secondary"/>
				</div>
    		</fieldset>
    	</div>
    </form>
  </body>
</html>