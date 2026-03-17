<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cCentroContable="";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
		String cIdRecepMat = "";
		String cIdpedContDef = "";
		String cUnidadEjecutora = "";
		String nIdEstadoRecepMat = "";
		String estatus="";
		String nIdConsecutivoRecepMat="";
		String cTipoDocRecepMat="";
		String cIdAlmacen="";
		String nIdEntraAlmacen="0";
	
		cIdRecepMat = request.getParameter("cIdRecepMat");
		cIdpedContDef = request.getParameter("cIdpedContDef");
		cUnidadEjecutora =request.getParameter("cUnidadEjecutora");
		nIdEstadoRecepMat =request.getParameter("nIdEstadoRecepMat");
		estatus =request.getParameter("estatus");
		nIdConsecutivoRecepMat=request.getParameter("nIdConsecutivoRecepM");
		cTipoDocRecepMat=request.getParameter("tipoDoc");
		cIdAlmacen=request.getParameter("cIdAlmacen");
		nIdEntraAlmacen=request.getParameter("nIdEntraAlmacen");
 %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
   
    
    <title>CaratulaRecepcion</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript" charset="utf-8">
		var oTableLineas="";
		var oTablePartidas="";
		var roles="";
		var partidaRestringida=false;
		$(document).ready(function() {
			$("#tbs").val(2);
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"RecepcionMaterial","CaratulaRecepcion");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
			roles="<%=roles%>";
			$("#cIdContrato").val("<%=cIdpedContDef%>");
			$("#nIdConsecutivoRecepMat").val("<%=nIdConsecutivoRecepMat%>");
			$("#nIdEstadoRecepMat").val("<%=nIdEstadoRecepMat%>");
			$("#cIdRecepMat").val("<%=cIdRecepMat%>");
			$("#cIdAlmacen").val("<%=cIdAlmacen%>");	    	
			$("#esAlmacenCentral").val("<%=nIdEntraAlmacen%>");
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			} 
			querySelectPost("tCatalogoAlmacenesReadVistas", "cIdAlmacenEntrega", {async: false});
			$("#cIdAlmacenEntrega").val($("#cIdAlmacen").val());
			
			queryFormPost("obtieneDatosPedCont", {async: false, 
				callback : function() 
				{
					if($("#cTipoDocRecepMat").val()=='RM'){
						$("#divTblLineasAnticipo").hide();
						if($("#esDescentralizado").val() =='1'){
							$("#cIdUnidadEjecutora").val($("#cIdUnidadEjecutoraDesc").val());//
							queryFormPost("esCucopDeGasolinaDesc",  {async : false, 
								callback : function() 
								{
									if($("#cIdTipoProcedimiento").val()=='PC' &&  $("#esCucopGasolina").val()==''){
										$("#divTblPartidas").css("display", "");
										$("#divTblPartidasServicios").css("display", "none");
										muestraDatos();
									}else{
										$("#divTblPartidas").css("display", "none");
										$("#divTblPartidasServicios").css("display", "");
										muestraDatosServicios();
									}
								}
							});
						}else{
							queryFormPost("esCucopDeGasolina",  {async : false, 
								callback : function() 
								{
									if($("#cIdTipoProcedimiento").val()=='PC' &&  $("#esCucopGasolina").val()==''){
										$("#divTblPartidas").css("display", "");
										$("#divTblPartidasServicios").css("display", "none");
										muestraDatos();
									}else{
										$("#divTblPartidas").css("display", "none");
										$("#divTblPartidasServicios").css("display", "");
										muestraDatosServicios();
									}
								}
							});
						}
						muestraLineas();
					}else{
						$("#trcIdAlmacen").css("display", "none");
						$("#divtblLineas").hide();
						$("#fieldsetPartidasPedCont").css("display", "none");
						$("#trmConIVA").css("display", "none");
						$("#trmSinIVA").css("display", "none");
						$("#trmIVA").css("display", "none");
						if(parseInt($("#hayRecep").val(),10)!=0){
							$("#CancelarSolAbaste").hide();
						}
						muestraDatosAnticipo();
						
						
					}
					queryFormPost("mRecepMat_montosTtales",{async: false});
					
					
				}
			});
			desabilitar();
			muestraBotonEnviar();
			partidaRestringida=elContratoTienePartidaRestringida();
		});
		function agregaLineas(){
			var cadenaLineaConsCant="";
			var token="";
			//Para los ped/contratos de bienes
			if($("#cIdTipoProcedimiento").val()=='PC'){
				var aTrs = $('#tblPartidas').dataTable().fnGetNodes();
				for ( var i=0 ; i<aTrs.length; i++ )     
				{  
					var nTr = $('#tblPartidas').dataTable().fnGetData(aTrs[i]);
					if(parseInt($("#cantAgregar_"+nTr[0]).val(),10)<=parseInt(nTr[5],10) && parseInt($("#cantAgregar_"+nTr[0]).val(),10)>0){
						cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[0]+'-'+$("#cantAgregar_"+nTr[0]).val();
						token=",";
					}else{
						if(parseInt($("#cantAgregar_"+nTr[0]).val(),10)>0)
							swal("En la línea "+nTr[0]+" sobrepasaste la cantidad disponible.",{icon:"info",button: "Cerrar"});
					}
					
				}
				
				$("#cadenaLineaCantidad").val(cadenaLineaConsCant);
				if($("#esDescentralizado").val() =='1'){
					queryFormPost("creaActualizaRecepMatDesc",  {async : false, 
						callback : function() 
						{
							//Guarda en la Bitácora
							guardaBitacora("ACTUALIZA RECEPCIÓN PEDCONT DESCENTRALIZADO");
							muestraDatos();
							muestraLineas();
						}
					});
				}else{
					queryFormPost("creaActualizaRecepMat",  {async : false, 
						callback : function() 
						{
							//Guarda en la Bitácora
							guardaBitacora("ACTUALIZA RECEPCIÓN PEDCONT CENTRALIZADO");
							muestraDatos();
							muestraLineas();
						}
					});
				}
					
			}else{
			//Cuando son servicios
			
			}
		}
		function EliminaLinea(nlineaConsolidado){
			var aTrs = $('#tblLineas').dataTable().fnGetNodes();
			if(aTrs.length<=1){
				swal("No Puedes eliminar todas las líneas.",{icon:"info",button: "Cerrar"});
				return;
			}
			$("#nlineaConsolidado").val(nlineaConsolidado);
			queryFormPost("mEliminaLineaRecepMat",  {async : false, 
				callback : function() 
				{
					
					if($("#cIdTipoProcedimiento").val()=='PC'  && $("#esCucopGasolina").val()==''){
						muestraDatos();
					}else{
						muestraDatosServicios();
					}
					muestraLineas();
					queryFormPost("mRecepMat_montosTtales",{async: false});
					swal("Línea Eliminada.",{icon:"info",button: "Cerrar"});
				}
			});
			//Guarda en la Bitácora
			guardaBitacora("SE ELIMINA LINEA DE RECEPCIÓN");
		}
		function desabilitar(){
			if($("#nIdEstadoRecepMat").val()!="1"){
				$("#fieldsetPartidasPedCont").hide();
				$("#EnviarSolAbasteCaratRM").hide();
			}
			if(parseInt($("#nIdEstadoRecepMat").val(),10)>2 ){
				$("#CancelarSolAbaste").hide();
			}
			$("#EnviarSolAbasteCaratRM").hide();
		}
		function cancelaSolAbaste(){
			
			if($("#cIdRecepMat").val()!='' && $("#cIdContrato").val()!=''){
				swal({
					title: "Esta seguro de Cancelar el documento "+$("#cIdRecepMat").val()+"?",
					text: "",
					icon: "info",
					buttons: {
						confirm : "Aceptar",
						cancel: "Cancelar"
						},
					}).then((continuar) => {
						if (!continuar) {
							return;
					}else{
						if($("#cTipoDocRecepMat").val()=='RM'){
							var totalConIVA=0;
							var totalSinIVA=0;
							var totalIVA=0;	
							var aTrs = $('#tblLineas').dataTable().fnGetNodes();
							
							for(i=0; i<aTrs.length;i++){
							//4,5,6
								var nTr = $('#tblLineas').dataTable().fnGetData(aTrs[i]);
								totalConIVA=parseFloat(totalConIVA)+parseFloat(quitaFmt($("#mmontoConIVA_"+nTr[1]).val() ));
								totalSinIVA=parseFloat(totalSinIVA)+parseFloat(quitaFmt(nTr[5]));
								totalIVA=parseFloat(totalIVA)+parseFloat(quitaFmt(nTr[6]));
							}
							
							totalConIVA=totalConIVA.toFixed(2);
							totalSinIVA=totalSinIVA.toFixed(2);
							totalIVA=totalIVA.toFixed(2);
							$("#textMontoConIVA").val(quitaFmt2("textMontoConIVA"));
							$("#textMontoSinIVA").val(quitaFmt2("textMontoSinIVA"));
							$("#textMontoIVA").val(quitaFmt2("textMontoIVA"));
							
							queryFormPost("mMontoRemanenteAnticipo",{async: false, 
								callback : function() 
								{
									$("#montoConIvaDev").val(parseFloat($("#montoRemanenteConIVA").val())+ (totalConIVA-$("#textMontoConIVA").val()) );
									$("#montoSinIvaDev").val(parseFloat($("#montoRemanenteSinIVA").val())+(totalSinIVA-$("#textMontoSinIVA").val()));
									$("#montoIvaDev").val(parseFloat($("#montoRemanenteIVA").val())+(totalIVA-$("#textMontoIVA").val()));
								}
							});
							$("#estatusEnviado").val($("#estatusCancelada").val());
							queryFormPost("mDevuelveAnticipoRecepMat,mUpdateRecepMat",  {async : false, 
								callback : function() 
								{
									//Guarda en la Bitácora
									guardaBitacora("RECEPCIÓN CANCELADA");
									swal("Solicitud de Abastecimiento Cancelada.",{icon:"info",button: "Cerrar"});
									window.location = "RecepcionMaterial.jsp?tab=1";
								}
							});
						}else{
							$("#estatusEnviado").val($("#estatusCancelada").val());
							if(parseInt($("#hayRecep").val(),10)!=0){
								swal("No puedes cancelar esté anticipo por que hay recepciones creadas.",{icon:"info",button: "Cerrar"});
								return;
							}
							queryFormPost("mMontoActualAnticipo",  {async : false, 
								callback : function() 
								{
									$("#nPorcAsignacion").val($("#nPorcentajeAnticipo").val());
									queryFormPost("mUpdateRecepMat,tContratoAnticipoDivUpdate",  {async : false, 
										callback : function() 
										{
											//Guarda en la Bitácora
											guardaBitacora("ANTICIPO CANCELADO");
											swal("Solicitud de Anticipo Cancelada.",{icon:"info",button: "Cerrar"});
											window.location = "RecepcionMaterial.jsp?tab=1";
										}
									});
								}
							});
						}
					}
				});
			}else{
				swal("No hay nada que cancelar.",{icon:"info",button: "Cerrar"});
			}
		}
		function quitaFmt(valor) {
			var val=valor;
			
			val = val.replace("$", "");
			val = val.replace(/,/g, "");
			if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
			 }
			 return val;
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
		function quitaFmt3(dlt) {
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
		
		function muestraDatosAnticipo(){
			var qw="1=1 and cIdpedContDef='"+$('#cIdContrato').val()+"' and cIdRecepMat='"+$('#cIdRecepMat').val()+"'" ;
			oTableLienasAnticipo = $("#tblLineasAnticipo").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
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
		function enviar(){
			if($("#cTipoDocRecepMat").val()=='RA'){
				enviarAnticipo();
			}else{
				enviaSolAbaste();
			}
		}
		function enviarAnticipo(){
			
			var aTrs = $('#tblLineasAnticipo').dataTable().fnGetNodes();
			if($("#cIdRecepMat").val()!='' && $("#cIdContrato").val()!=''&& aTrs.length>0){
				if( requiereAutorizacion() ){
					capturaNota();
				}else{
					queryFormPost("mUpdateRecepMat",  {async : false, 
						callback : function() 
						{
							//Guarda en la Bitácora
							guardaBitacora("ANTICIPO EMITIDO PARA SU PAGO");
							swal("Ya se puede hacer el pago del Anticipo.",{icon:"info",button: "Cerrar"});
							window.location = "RecepcionMaterial.jsp?tab=1";
						}
					});
				}
			}else{
				swal("No hay nada que enviar.",{icon:"info",button: "Cerrar"});
			}
		}
		function onlyNum(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			return (keyPressed >= 48 && keyPressed <= 57 || keyPressed==46);
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
		function calcualOtrosImp(){
			var oImpGuardar=unFormatCurrency($("#otrosImpGuardar").val());
			var oImpRemanente=unFormatCurrency($("#otrosImpRemanente").val());
			if(oImpGuardar>oImpRemanente){
				$("#otrosImpGuardar").val(0);
			}
		}
		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		function frmt(dlt){
			$("#"+dlt.id).formatCurrency();
		}
		function unFormatCurrency(str){
			str = str.replace("$","");
			str = str.replace(/\,/g,'');
			return parseFloat(str);
		}
		function muestraBotonEnviar(){
			$("#divNotas").hide();
			$("#divNotasTermAnt").hide();
			$("#divNotaAnticipo").hide();
			if(esSAIAlterno){
				document.getElementById("agregarPartCaratRecepMat").disabled = false;
				document.getElementById("EnviarSolAbasteCaratRM").disabled = false;
				if(parseInt($("#nTerminacionAnticipada").val(),10)>=1){
					if( parseInt($("#permiteHacerRecep").val(),10)==0){
						document.getElementById("agregarPartCaratRecepMat").disabled = true;
						document.getElementById("EnviarSolAbasteCaratRM").disabled = true;	
					}
					$("#divNotasTermAnt").show();
				}
				if(parseInt($("#nOtorgaAnticipo").val(),10)==1 && parseInt($("#nFacturaGlobalCargada").val(),10)==0){
					$("#divNotaAnticipo").show();
					document.getElementById("agregarPartCaratRecepMat").disabled = true;
					document.getElementById("EnviarSolAbasteCaratRM").disabled = true;
				}
			}else{
				document.getElementById("agregarPartCaratRecepMat").disabled = false;
				document.getElementById("EnviarSolAbasteCaratRM").disabled = false;
				if(parseInt(($("#nTerminacionAnticipada").val(),10)>=1 && parseInt($("#permiteHacerRecep").val(),10)==0) 
						|| (parseInt($("#nFolioAutSICOP").val(),10)!=-1 && parseInt($("#nFolioAutSICOP").val(),10)<1) 
						|| parseInt($("#lArchivoContCargado").val(),10)==0 
						|| (parseInt($("#nOtorgaAnticipo").val(),10)==1 && parseInt($("#nFacturaGlobalCargada").val(),10)==0 ) 
				){
					document.getElementById("agregarPartCaratRecepMat").disabled = true;
					document.getElementById("EnviarSolAbasteCaratRM").disabled = true;
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
	</script>
  </head>
  
  <body>
  <form id="formNuevaRecep">
    	<div id="container" class="container" style="width: 90%;">
    		<div class= "card" id="fieldsetPartidasPedCont">
				<div class="card-header">
				    <h5>Partidas Pedido/Contrato</h5>
				</div>
				<div class="card-body">
					<div id="divNotas" align="left" class="row" style="display: none;">
	    				<div class="col-12" id="notas">
   							<span id="notaContFisico" style="color: red;">Todos los contratos cargados en SAI a partir del 01/09/2020 sera necesario la carga del contrato físico para poder hacer recepciones.</span>
	    				</div>
    				</div>
    				<div id="divNotaAnticipo" align="left" class="row" style="display: none;">
	    				<div class="col-12" id="notaAnticipo">
	    					<span id="notaContAnticipo" style="color: red;">Contratos que se les otorg&oacute; anticipo, ser&aacute; necesario la carga de factura global en el m&oacute;dulo de contratos para poder hacer recepciones.</span>
	    				</div>
	    			</div>
	    			<div id="divNotasTermAnt" align="left" class="row" style="display: none;">
	    				<div id="notasTermAnt" style="width: 100%" class="col-12">
   							<input id="cTerminacionAnticipada" name="cTerminacionAnticipada" style="width: 100%; border: 0px none;background:#FEFEFE; color:red" value="" readonly />
	    				</div>
	    			</div>
	    			<div id="trOtrosImp" style="display: block;">
						<div class="row" >
							<div class="col-3">
								Remanente Otros Impuestos 
							</div>
							<div class="col-2">
								<input type="text" name="otrosImpRemanente" id="otrosImpRemanente" value="$ 0.0" class="form-control"  readonly/>
							</div>
							<div class="col-3">
								Captura Monto Otros Impuestos
							</div>
							<div class="col-2">
								<input type="text" name="otrosImpGuardar" id="otrosImpGuardar" value="$0.0" onkeypress="return onlyMoney(event)" onfocus="quitaFmt3(this)" onblur="calcualOtrosImp();frmt(this);" class="form-control" />
							</div>
						</div>
					</div>
					<div id="divTblPartidas" style="display: none;" class="mt-2" >
		    			<table id="tblPartidas" class="display" >
							<thead >
								<tr>
									<th align="center">Linea</th>
									<th align="center">Partida</th>
									<th align="center">CUCOP</th>
									<th align="center">Descripción</th>
									<th align="center">Cant.<br />Total</th>
									<th align="center">Cant.<br />Disponible</th>
									<th align="center">Monto <br />Total</th>
									<th align="center">Cant.<br />A <br />Agregar</th>
									<th align="center">Monto<br />Descuento<br />Sin IVA </th>
									<th align="center">Unidad Medida</th>
									<th align="center">IVA</th>
									<th align="center">PrecioU</th>
								</tr>										
							</thead>
						</table>
					</div>
					<div id="divTblPartidasServicios" style="display: none;" class="mt-2">
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
									<th align="center">Unidad de Medida</th>
									<th align="center">IVA</th>
									<th align="center">PrecioU</th>
								</tr>										
							</thead>
						</table>
					</div>
					<div class="row mt-2">
						<div class="d-flex justify-content-end">
							<input type="button" id="agregarPartCaratRecepMat" name="agregarPartCaratRecepMat" value="Agregar" onclick="crearSolAbastecimiento();" class="btn btn-secondary"/>
						</div>
					</div>
    				<br/>
				</div>
			</div>
    		<div class= "card">
    			<div class= "card-body">
    			<table align="left">
    				<tr align="left">
    					<td>
    					IdRecepci&oacute;n :
    					<input type="text" id="textcidRecepcion" name="textcidRecepcion" value="<%=cIdRecepMat %>" style="border-width:0; background-color:transparent;"/> 
    					</td>
    					
    				</tr>
    				<tr align="left">
    					<td>
    					Estatus :<input type="text" id="textEstatus" name="textEstatus" value="<%=estatus %>" style="border-width:0; background-color:transparent; font-weight: bold;"/> 
    					</td>
    					
    				</tr>
    				<tr align="left">
    					<td>
    					Monto Con IVA :<input type="text" id="textMontoConIVA" readonly name="textMontoConIVA" value="" style="border-width:0; background-color:transparent;"/> 
    					</td>
    				</tr>
    				<tr align="left">
    					<td>
    					Monto Sin IVA :<input type="text" id="textMontoSinIVA" readonly name="textMontoSinIVA" value="" style="border-width:0; background-color:transparent;"/> 
    					</td>
    				</tr>
    				<tr align="left">
    					<td>
    					Monto IVA :<input type="text" id="textMontoIVA" readonly name="textMontoIVA" value="" style="border-width:0; background-color:transparent;"/> 
    					</td>
    				</tr>
    				<tr align="left">
    					<td>
    					Descuento Con IVA :<input type="text" id="textDescuentoConIVA" readonly name="textDescuentoConIVA" value="" style="border-width:0; background-color:transparent;"/> 
    					</td>
    				</tr>
    				<tr align="left">
    					<td>
    					Descuento Sin IVA :<input type="text" id="textDescuentoSinIVA" readonly name="textDescuentoSinIVA" value="" style="border-width:0; background-color:transparent;"/> 
    					</td>
    				</tr>
    				<tr align="left">
    					<td>
    					Descuento IVA :<input type="text" id="textDescuentoIVA" readonly name="textDescuentoIVA" value="" style="border-width:0; background-color:transparent;"/> 
    					</td>
    				</tr>
    				<tr align="left">
    					<td>
    					Otros Imp :<input type="text" id="textMontoOtrosImp" readonly name="textMontoOtrosImp" value="" style="border-width:0; background-color:transparent;"/> 
    					</td>
    				</tr>
    			</table>
    			<div class="row"  id="trcIdAlmacen">
    				<div class="col-2">
    					Lugar de Entrega:
    				</div>
    				<div class="col-8">
    					<select name="cIdAlmacenEntrega" id="cIdAlmacenEntrega" style="width: 600px" class="form-select"></select>
    				</div>
    			</div>
    			
    			<div id="divtblLineas">
	    			<table id="tblLineas" class="display">
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
				<div id="divTblLineasAnticipo" >
	    			<table id="tblLineasAnticipo" class="display">
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
				<div class="row">
					<div class="d-flex justify-content-end">
						<input type="button" id="EnviarSolAbasteCaratRM" name="EnviarSolAbasteCaratRM" value="Enviar"  onclick="enviar()" class="btn btn-secondary"/>
						<input type="button" id="CancelarSolAbaste" name="CancelarSolAbaste" value="Cancelar" onclick="cancelaSolAbaste()" class="btn btn-secondary"/>
					</div>
				</div>
			</div>
		</div>
		
				
    		
    	</div>
    	<input type="hidden" id="pedidoContratoCompromiso" name="pedidoContratoCompromiso" value="<%=cIdpedContDef %>"/>
    	<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=cUnidadEjecutora %>"/>
    	<input type="hidden" id="cIdUnidadEjecutoraDesc" name="cIdUnidadEjecutoraDesc" value="<%=cUnidadEjecutora %>"/>
    	<input type="hidden" name="estatusCancelada" id="estatusCancelada" value="4" />
    	<input type="hidden" name="cTipoDocRecepMat" id="cTipoDocRecepMat" value="<%=cTipoDocRecepMat%>" />
    	<input type="hidden" name="montoConIvaDev" id="montoConIvaDev" value="0" />
    	<input type="hidden" name="montoSinIvaDev" id="montoSinIvaDev" value="0" />
    	<input type="hidden" name="montoIvaDev" id="montoIvaDev" value="0" />
    	<input type="hidden" name="montoRemanenteConIVA" id="montoRemanenteConIVA" value="0" />
    	<input type="hidden" name="montoRemanenteSinIVA" id="montoRemanenteSinIVA" value="0" />
    	<input type="hidden" name="montoRemanenteIVA" id="montoRemanenteIVA" value="0" />
    	<input type="hidden" name="nPorcentajeAnticipo" id="nPorcentajeAnticipo" value="0" />
    	<input type="hidden" name="mImporteAnticipo" id="mImporteAnticipo" value="0" />
    	<input type="hidden" name="mImporteAnticipoIVA" id="mImporteAnticipoIVA" value="0" />
    	<input type="hidden" name="mTotalAnticipo" id="mTotalAnticipo" value="0" />
   		<input type="hidden" id="nPorcAsignacion" name="nPorcAsignacion" value="0" />
   		<input type="hidden" id="esAlmacenCentral" name="esAlmacenCentral" value="0" />

    </form>
    <script type="text/javascript">
    function requiereAutorizacion(){
		var requerido = true;
		queryFormPost({
			queryName: "rmRequiereNotaCaratulaRead",
			async:false,
			callback:function(){
				requeridoVl = $("#requiereNota").val() == ""?"0":$("#requiereNota").val();
				requerido = ( parseInt(requeridoVl,10) > 0 );
			}
		});
		return requerido;
	}
    </script>
  </body>
</html>
