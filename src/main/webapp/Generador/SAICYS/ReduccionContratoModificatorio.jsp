<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab.getLogin();
	String role="";
	String cCentroContable = "";
	Map rol =usuarioTab.getRoles();
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	if (session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null) {
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		
		cIdTipoContrato = cIdContrato.split("-")[0];
		cIdUnidadEjecutora = cIdContrato.split("-")[1];
		nIdConsecutivo = cIdContrato.split("-")[2];		
	}else 
		response.sendRedirect("ContratoModificatorio.jsp?tab=1");
	if (usuarioTab.getPropiedades() != null && usuarioTab.getPropiedades().containsKey("CCENTROCONTABLE"))
		cCentroContable = usuarioTab.getPropiedad("CCENTROCONTABLE").getValor();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Reduccion Contrato</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" charset="utf-8">	
		$(document).ready(function() {
			var compromiso;
			var compromisoContrato;
			var globalCentroCon;
			<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgDevolver=0;
				//botones
				//NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
				}
				Map botones=nb.getBotones(role,"ContratoModificatorio","reduccionContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					if ("imgAprobarpreCompromisoRedCon".equals(img)){  
						imgDevolver=1;
					}
				}
				
			%>
			
			setFieldsInit();
			cargarCompromisos();
			cargarCompromisosSeleccionados();

		});

		$('#tblCompromisos tr').live('dblclick', function() {
			$(compromiso.fnSettings().aoData).each(function (){
				$(this.nTr).removeClass('row_selected');
			});						
			$(this).addClass('row_selected');

			if(parseFloat($("#cTotalModificacion").val()) == 0){
				alert("No se puede agregar ninguna compromiso por que el importe de la modificaci\xF3n es 0.");
				return;	
			}

			if(!validaReduccionesAplicadas()){
				alert("No se puede agregar ningun compromiso por que las reducciones ya fueron aprobadas.");
				return;
			}

			var aTrs1 = $("#tblCompromisos").dataTable().fnGetNodes();
			var aTrs2 = $('#tblCompromisoSeleccionado').dataTable().fnGetNodes();
			
			if(aTrs1.length > 0){
				for ( var i=aTrs1.length-1 ; i>=0; i-- ){
					var nTr1 = $("#tblCompromisos").dataTable().fnGetData(aTrs1[i]);
					
					if(aTrs2.length > 0){
						if ($(aTrs1[i]).hasClass('row_selected')){
							for ( var l=aTrs2.length-1 ; l>=0; l-- ){
								var nTr2 = $("#tblCompromisoSeleccionado").dataTable().fnGetData(aTrs2[l]);
								if(nTr1[1].toString() == nTr2[1].toString() && parseInt(nTr1[5].toString(),10) == parseInt(nTr2[6].toString(),10)){
									alert("La EP ya esta agregada, favor de seleccionar otra.");
									return;
								}
							}
						}
					}
				}
			}

			if(aTrs1.length > 0){
				for ( var i=aTrs1.length ; i>=0; i-- ){
					if ( $(aTrs1[i]).hasClass('row_selected') ){
						var nTr1 = compromiso.dataTable().fnGetData(aTrs1[i]);
						$("#cEPTem").val(nTr1[1]);
						$("#nIdMesTem").val(nTr1[5]);
						$("#cCentroContableTem").val(nTr1[6]);
						queryFormPost("insertReduccionContrato",{async: false})
					}					
				}
			}
			
			cargarCompromisosSeleccionados();
		});

		function cargarCompromisosSeleccionados(){
			compromisoContrato=$("#tblCompromisoSeleccionado").dataTable({
				sScrollY: "200px",
				sScrollX: "100%",
				sScrollXInner: "200%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mReduccionesContratoEP('" + $("#cContratoDefinitivo").val() + "')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cColumn" },
					{ sName: "EP" },
					{ sName: "cMes" },
					{ sName: "cImporte" },
					{ sName: "cEstado" },
					{ sName: "cEliminar" },
					{ sName: "nIdMes", bVisible: false },
					{ sName: "cIdContrato", bVisible: false },
					{ sName: "nConsecutivoModificacion", bVisible: false },
					{ sName: "cCentroContable", bVisible: false },
					{ sName: "cAplicada", bVisible: false }
				]
			});
			
		}

		function cargarCompromisos(){ 
			compromiso=$("#tblCompromisos").dataTable({
				sScrollY: "200px",
				sScrollX: "100%",
				sScrollXInner: "200%",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mCompromisosDocumento('" + $("#cContratoDefinitivo").val() + "')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdContrato" },
					{ sName: "EP" },
					{ sName: "cMes" },
					{ sName: "mImporte" },
					{ sName: "totalPagos" },
					{ sName: "nIdMes", bVisible: false },
					{ sName: "cCentroContable", bVisible: false }
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
			document.getElementById("lblTotalAnterior").style.readonly = true;
			document.getElementById("lblTotalModificado").style.readonly = true;
			document.getElementById("lblTotal").style.readonly = true;
						
			//Carga de cabecera
			queryFormPost("mContratoHeaderRead", {async: false});
			queryFormPost("mContratoModificadoTotales", {async: false});
			queryFormPost("mReduccionesContratoRead", {async: false});

			if(parseInt($("#cReducciones").val(),10) > 0){
				$("#btnGuardarReduccionesContMod").attr("disabled","true");
			}
			
			if ($("#tipoMod").val() == 1){
				document.getElementById("presupuestoContratoMod").disabled = true;
				document.getElementById("precompromisoContratoMod").disabled = true;
				document.getElementById("pagosContratoMod").disabled = true;
			}
		}
		
		function fnGetSelected( oTableLocal ) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}

		function eliminarReduccion(ep, cIdContrato, nIdMes, nConsecutivoModificacion){
			if(confirm("\xBFEst\xE1s seguro eliminar la reducci\xF3n?")){
				$("#cEPTem").val(ep);
				$("#nIdMesTem").val(nIdMes);
				queryFormPost("deleteReduccionContratoModificatorio", {async: false});
				cargarCompromisosSeleccionados();
				
			}
		}
		function guardaReducciones(){
			var aTrs = $('#tblCompromisoSeleccionado').dataTable().fnGetNodes();

			if(validaImportesReduccion(aTrs)){
				if(aTrs.length > 0){
					for ( var i=aTrs.length-1 ; i>=0; i-- ){
						var nTr = $('#tblCompromisoSeleccionado').dataTable().fnGetData(aTrs[i]);
						$("#cEPTem").val(nTr[1]);
						$("#nIdMesTem").val(nTr[6]);
						$("#cImporteReduccionTem").val($("#cImporteReduccion_"+nTr[0]).val());
						queryFormPost("updateImporteReduccionContratoEP", {async: false});
					}
					alert("Los datos se guardaron correctamente.");
					cargarCompromisosSeleccionados();
				}
			}
		}
		function validaImportesReduccion(aTrs){
			var inputMontos = $('input','#dtblCompromisoSeleccionado');
			var importeMod=parseFloat($("#cTotalModificacion").val())*-1;
			var total=0;
			var objMonto;
			var saldo=0;

			if(aTrs.length > 0){
				for ( var i=aTrs.length-1 ; i>=0; i-- ){
					var nTr = $('#tblCompromisoSeleccionado').dataTable().fnGetData(aTrs[i]);
					objMonto=$("#cImporteReduccion_"+nTr[0]).val().replace("$","").replace(",","");

					var aTrs2=$('#tblCompromisos').dataTable().fnGetNodes();

					for(var a=aTrs2.length-1 ; a>=0; a--){
						var nTr2 = $('#tblCompromisos').dataTable().fnGetData(aTrs2[a]);

						if(nTr[1].toString() == nTr2[1].toString() && parseInt(nTr[6].toString(),10) == parseInt(nTr2[5].toString(),10)){
							saldo = parseFloat(nTr2[3].toString().replace("$","").replace(",","")) - parseFloat(nTr2[4].toString().replace("$","").replace(",",""));

							if(parseFloat(objMonto) > parseFloat(saldo)){
								alert("El importe de la reducci\xF3n para la EP "+nTr[1].toString()+" no puede ser mayor a su saldo.");
								return false;
							}
						}
					}
					
					if(objMonto == ""){
						alert("Debe insertar todos los importes.");
						return false;
					}
					else{
						if(parseFloat(objMonto) < 0){
							alert("Los importes deben ser positivos.");
							return false;
						}
						else{
							total=total+parseFloat(objMonto); 
						}
					}
					
				}
			}

			if(total<importeMod || total>importeMod){
				alert("Los montos de las reducciones no coincide con el total de la reduccion.");
				return false;
			}
			return true;
		}
		
		function formateaImporte(obj){
			obj.value=obj.value.replace("$","").replace(",","");
		}

		function apruebaReduccion(){
			$("#centralizado").val("CENTRALIZADO");
			$("#cCetrosContables").val("");
			var proc="";
			var centrosContable;
			var folios;
			var unidades;

			if(document.getElementById("btnGuardarReduccionesContMod").disabled){
				alert("No existe ninguna reducci\xF3n por aprobar.");
				return;
			}

			if(confirm("\xBFEst\xE1s seguro de aprobar la reducci\xF3n?")){
				if(validaImportesReduccion($('#tblCompromisoSeleccionado').dataTable().fnGetNodes())){
					if($("#centralizado").val().toUpperCase() == "DESCENTRALIZADO"){
						queryFormPost("mCentrosContablesReduccionContrato", {async: false});
						unidades = $("#cCetrosContables").val().split("|");

						for(var i=0;i<unidades.length;i++){
							centrosContable = unidades[i].split("-");
							globalCentroCon=centrosContable[1];
							getNextSequenceVal({seqName: "CO-" + centrosContable[1], async: false, callback: setSequenceVal});
						}
						$("#cFoliosCompromiso").val($("#cFoliosCompromiso").val().substring(0,$("#cFoliosCompromiso").val().length-1));
					}
					else{
						$("#cCetrosContables").val("<%=cCentroContable%>");
						globalCentroCon=$("#cCetrosContables").val();
						getNextSequenceVal({seqName: "CO-" + globalCentroCon, async: false, callback: setSequenceVal});
						$("#cFoliosCompromiso").val($("#cFoliosCompromiso").val().substring(0,$("#cFoliosCompromiso").val().length-1));
					}
					
					//Llama a AmpliacionesServlet para llamar el stored procedure que aprueba el Contrato
					$.getJSON("../../servlet/ReduccionesServlet?"+new Date().getTime()+"&cEjercicio="+$("#cEjercicio").val()+"&cIdDocumentoDefinitivo="+$("#cContratoDefinitivo").val()+"&cConsecutivoMod="+$("#cConsecutivoMod").val()+"&centralizado="+$("#centralizado").val()+"&tipo=CONTRATO&centrosContables="+$("#cCetrosContables").val()+"&numCompromisos="+$("#cFoliosCompromiso").val() ,{Tabla: "", Param: proc, MaxReg: "", ajax: 'false'}, function(j){
						for(var i = 0; i < j.length; i++)
							var col=parseInt(j[i].Col1.toString(),10);
							switch(col){
								case 0: 
									alert("DOCUMENTO DE PRECOMPROMISO GENERADO CORRECTAMENTE.");
									$("#nIdEstado").val(3);
									queryFormPost("mContratoModificadoPrecompromisoDesUpdate", {async: false });
									queryFormPost("mContratoHeaderRead", {async: false});
									queryFormPost("mReduccionesContratoRead", {async: false});
									cargarCompromisosSeleccionados();
									
									if(parseInt($("#cReducciones").val(),10) > 0){
										$("#btnGuardarReduccionesContMod").attr("disabled","true");
									}
								break;
								case 1: 
									alert("Error al crear el encabezado del precompromiso.");
								break;
								case 10:  
									alert("Error al buscar los datos para crear el contrato diverso.");
								break;
								case 11:  
									alert("Error al buscar los datos para crear el contrato diverso.");
								break;
								case 12:  
									alert("Error al buscar los datos para crear el contrato diverso.");
								break;
								case 13:
									alert("Error al guardar las EP's en la tabla temporal.");
								break;
								case 14:
									alert("Error al guardar los datos del contrato diverso.");
								break;
								case 15:
									alert("Error al guardar las EP's del contrato diverso.");
								break;
								case 16:
									alert("Error al guardar el detalle del precompromiso.");
								break;
								case 17:
									alert("Error al actualizar los datos de la reducci\xF3n.");
								break;
								case 18:
									alert("Error al actualizar los datos de la reducci\xF3n.");
								break;
								case 27:  
									alert("Error al buscar los datos para crear el contrato diverso.");
								break;
								case 28:  
									alert("Error al buscar los datos para crear el contrato diverso.");
								break;
								case 29:  
									alert("Error al buscar los datos para crear el contrato diverso.");
								break;
								case 30:
									alert("Error al guardar las EP's en la tabla temporal.");
								break;
								case 31:
									alert("Error al guardar los datos del contrato diverso.");
								break;
								case 32:
									alert("Error al guardar las EP's del contrato diverso.");
								break;
								case 33:
									alert("Error al guardar el detalle del precompromiso.");
								break;
								case 34:
									alert("Error al actualizar los datos de la reducci\xF3n.");
								break;
								default:
									alert("Error inesperado.");
							}
						});
				}
			}
		}

		function validaReduccionesAplicadas(){
			var aTrs = $('#tblCompromisoSeleccionado').dataTable().fnGetNodes();
			
			if(aTrs.length > 0){
				for ( var i=aTrs.length-1 ; i>=0; i-- ){
					var nTr = $('#tblCompromisoSeleccionado').dataTable().fnGetData(aTrs[i]);

					if(nTr[10] == 1){
						$("#btnGuardarReduccionesContMod").attr("disabled","true");
						return false;
					}
				}
			}
			return true;
		}

		function setSequenceVal(seqValue) {
			seqValue = 100000 + parseInt(seqValue,10);
			//seqValue = seqValue.substr(seqValue.length - 6);
			vcaNoCompromiso = globalCentroCon + "CO" + $("#cEjercicio").val() + seqValue + "|";
			$("#cFoliosCompromiso").val($("#cFoliosCompromiso").val() + vcaNoCompromiso);
		}
	</script>
  </head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form > 
		<div id="container" class="container" >
			<table width="94%" align="left">
				<tr>
					<td width="750px" >
						<fieldset>&nbsp; 
							<legend>Car&aacute;tula del Contrato Modificado</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="window.location = 'ContratoModificatorio.jsp?tab=1';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblProcedimiento" name="lblProcedimiento" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" id="lblDefinitivo" name="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" name="lblContrato" id="lblContrato" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 300px" name="lblEstadoMod" id="lblEstadoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTipoMod" id="lblTipoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalAnterior" id="lblTotalAnterior" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalModificado" id="lblTotalModificado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotal" id="lblTotal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblPorcentajeMod" id="lblPorcentajeMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>				    	
							    </table>
						</fieldset>
					</td>
				</tr>
			
				<tr>
					<td style="width: 750px;" >
						<fieldset>&nbsp; 
						<legend>Reducciones</legend>
							<table width="100%">
								<tr>
									<td style="width: 740px; text-align:right;">
										<img id="imgAprobarAmpliacion" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="apruebaReduccion();" />
										&nbsp;Aprobar
									</td>
								</tr>
								<tr>
									<td>
										&nbsp;
									</td>
								</tr>
								<tr>
									<td style="width: 740px;">
										<table align="left" id="tblCompromisos" width="740px" class="display">
											<thead>
												<tr>
													<th width="15%">Contrato</th>
													<th width="15%">EP</th>
													<th width="15%">Mes</th>
													<th width="15%">Importe</th>
													<th width="15%">Pagos</th>
													<th width="15%">nIdMes</th>
													<th width="15%">cCentroContable</th>
												</tr>
											</thead> 
										</table>
									</td>
								</tr>
								<tr>
									<td>
										&nbsp;
									</td>
								</tr>
								<tr id="ampliTr" >
									<td style="width: 740px;" >
										<div id="dtblCompromisoSeleccionado">
											<table align="left"  id="tblCompromisoSeleccionado" width="740px" class="display">
												<thead>
													<tr>
														<th>&nbsp;</th>
														<th width="10%" >EP</th>
														<th width="10%" >Mes</th>
														<th width="10%" >Importe</th>
														<th width="10%" >Estado</th>
														<th width="11%" >&nbsp;</th>
														<th width="15%" >nIdMes</th>
														<th width="15%" >cIdPedido</th>
														<th width="15%" >nConsecutivoModificacion</th>
														<th width="15%" >cCentroContable</th>
														<th width="15%" >cAplicada</th>
													</tr>
												</thead> 
											</table>
										</div>
										<br/>
									</td>
								</tr>
								<tr>
									<td colspan="2" style="text-align:left;">
										<button name="btnGuardarReduccionesContMod" id="btnGuardarReduccionesContMod" onClick="guardaReducciones();" >Guardar</button>
									</td>
								</tr>
							</table>
							
						</fieldset>
					</td>
				</tr>
				
			</table>
		    <!-- Hidden's -->
		    <!-- Sesion  -->
		    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="text" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
		    <input type="text" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		    <input type="text" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
		    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
		    <input type="hidden" name="tipoMod" id="tipoMod" />
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
		    <input type="hidden" name="nIdEstado" id="nIdEstado" />
		    
		    <!-- Datos para insertar las ep de la reduccion -->
		    <input type="hidden" name="cEPTem" id="cEPTem" >
		    <input type="hidden" name="nIdMesTem" id="nIdMesTem" >
		    <input type="hidden" name="cCentroContableTem" id="cCentroContableTem" >
		    <input type="hidden" name="cImporteReduccionTem" id="cImporteReduccionTem" >
		    <input type="hidden" name="cTotalModificacion" id="cTotalModificacion" >
		    <input type="hidden" name="cReducciones" id="cReducciones" >
		    
		    <input type="hidden" name="centralizado" id="centralizado" >
		    <input type="hidden" name="cCetrosContables" id="cCetrosContables" >
		    <input type="hidden" name="cFoliosCompromiso" id="cFoliosCompromiso" >
		    
	    </div>
	</form>
  </body>
</html>
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         