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
	Map rol =usuarioTab.getRoles();
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	
	if (request.getParameter("cDefinitivo") != null) {
		cIdContratoDefinitivo = request.getParameter("cDefinitivo");
		cEjercicio=request.getParameter("cEjercicio");
		cIdUnidadEjecutora =request.getParameter("cIdUnidadEjecutora");
		cIdContrato=request.getParameter("cContrato");
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo,cIdContratoDefinitivo.toString());
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioId,cIdContrato.toString());
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio,cEjercicio.toString());
		session.setAttribute(GestionInterface.ATT_ContratoModificatorioUE,cIdUnidadEjecutora.toString());
	} else {
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdUnidadEjecutora=(String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioUE);
	}
	cIdTipoContrato = cIdContrato.split("-")[0];
	if(cIdTipoContrato.equalsIgnoreCase("PLU")){
		cIdTipoContrato=cIdContrato.split("-")[1];
	}
 %>
<!DOCTYPE html>
<html>
  <head>
   <title>Car&aacute;tula Contrato Remanente</title>
   	<meta charset="UTF-8">
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		var roles="",porcentajeTotalAumentado=0;
		$(document).ready(function() {
			tabb=2;
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role+= r.getKey().toString()+",";
				}
				if(role.length()>0){
					role = role.substring(0,role.length()-1);
				}
				Map botones=nb.getBotones(role,"ContratoAnterior","caratulaContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			roles="<%=role%>";
			$.blockUI({message: "Procesando espere ......"});
			showAndHideTabs();
			setFieldsInit();
			mostrarPartidasMods();
			queryFormPost("validaExisteRemanenteContrato", {async : false});
			showAndHideButtons();
			$.unblockUI();
		});//Fin del document ready
		function showAndHideButtons(){
			if(parseInt($("#nIdEstado").val(),10)>1 ){
				document.getElementById('btnRegistrar').style.display = 'none';
			}
			if(parseInt($("#nIdEstado").val(),10)>2 ){
				document.getElementById('imgEliminar').style.display = 'none';
			}
		}
		function setFieldsInit(){
			queryFormPost("mContratoHeaderReadEjerAnt", {async: false,
				callback: function() {
					$("#lblTotalContratoOriginal").val("Monto Contrato Original: $ "+formateaMoneda( parseFloat($("#mimportetotal").val()).toFixed(2)  ));
					$("#lblTotalContratoPagado").val("Monto Pagado Ejercicio Anterior: $ "+formateaMoneda(parseFloat($("#mImportePago").val()).toFixed(2) ));
					$("#lblTotalContratoRemanente").val("Monto Remanente: $ "+formateaMoneda(parseFloat($("#mRemanenteContrato").val()).toFixed(2) ));
					$("#lblEstadoMod").val("Estatus : "+$("#cEstado").val());
				}
			});
		}
		function mostrarPartidasMods(){
			ContratoPartidasMods=$("#tblContratoPartidasMods").dataTable({
				bScrollCollapse: true,
				bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
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
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					aoColumns: [
						{ sName: "nIdLineaConsolidado" },
						{ sName: "cIdSubPartida" },
						{ sName: "cIdCABM" },
						{ sName: "cDescripcion" },
						{ sName: "nCantidad" },
						{ sName: "PrecioUnitario" },
						{ sName: "nPorcentajeIVA" },
						{ sName: "MontoTotalLinea" },
						{ sName: "montoConIVADisp" }
					],
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}			
	       	}); 	
			var zTabla = "REMANENTEEJERCICIOANTERIORPARTIDAS";		
			campos = "'" + $("#cIdUnidadEjecutora").val()+ "','" + $("#cIdRFC").val() + "','" + $("#cContratoDefinitivo").val()+"'";
		    ContratoPartidasMods.fnClearTable();
		    var  elParametro2 ='';		    
			$.getJSON("../../catalogos/SelectJson.jsp",{Tabla: zTabla, Param: elParametro2,Campos:campos, MaxReg: "10", ajax: 'false'}, function(j){		    							
				for (var i = 0; i < j.length; i++){				
					$('#tblContratoPartidasMods').dataTable().fnAddData([ j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col5,j[i].Col6,j[i].Col7,j[i].Col8 ]);											
				}								
			});	
		}
		function registraContrato(){
			document.getElementById('btnRegistrar').style.display = 'none';
			var aDataPar = ContratoPartidasMods.fnGetData();
			var correcto=0,mImporteBruto=0,mImporteIva=0,mImporteIvaAcumulado=0,mImporteTotal=parseFloat(quitaFmt($("#mRemanenteContrato").val())).toFixed(2),mMontoLinea=0,nPorcIVA=0;
			for(var i=0;i<aDataPar.length;i++){
	           	$("#nIdLineaConsolidado").val(aDataPar[i][0]);
	           	$("#cIdSubPartida").val(aDataPar[i][1]);
	           	$("#cIdCABM").val(aDataPar[i][2]);
	           	$("#cDescripcion").val(aDataPar[i][3]);
	           	$("#nCantidad").val(aDataPar[i][4]);
	           	$("#mPrecioUnitario").val(aDataPar[i][5]);
	           	$("#nPorcentajeIVA").val(aDataPar[i][6]);
	           	$("#mMontoNeto").val(aDataPar[i][8]);
	           	queryFormPost({queryName: "mContratoRemanenteEjercicioAnteriorPartidaCreate",async: false,
					callback: function() {
						mMontoLinea= parseFloat(quitaFmt($("#mMontoNeto").val())).toFixed(2);
						nPorcIVA=parseInt($("#nPorcentajeIVA").val());
						mImporteBruto=(mMontoLinea/(1+(nPorcIVA/100))).toFixed(2);
						mImporteIVA=(mMontoLinea-mImporteBruto).toFixed(2);
						mImporteIvaAcumulado=+mImporteIVA;
						correcto++;
					}
	           	});
			}
			mImporteBruto=(mImporteTotal-mImporteIvaAcumulado).toFixed(2);
			$("#mImporteBruto").val(mImporteBruto);
			$("#mImporteIVA").val(mImporteIvaAcumulado);
			if(aDataPar.length==correcto){
				$("#nEstado").val(2);
				queryFormPost({queryName: "mContratoRemanenteEjercicioAnteriorUpdatenEstado",async: false,
					callback: function() {
						swal({
							title: "",
							text: "Contrato Registrado Correctamente",
							icon: "info",
							buttons: {
								confirm : "Cerrar"
								},
							}).then((continuar) => {
								setFieldsInit();
						});
					}
				}); 
			}
		}
		function borraContrato(){
			if(parseInt($("#nIdEstado").val(),10)>=4){
				return;
			}
			swal({
				title: "¿Estas seguro de eliminar el contrato?",
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
					queryFormPost({queryName: "quitaEPSContratoDelete,mDeleteContratoAnteriorPartidas,mDeleteContratoAnterior",async: false,
						callback: function() {
							swal({
								title: "",
								text: "Contrato borrado de forma correcta.",
								icon: "info",
								buttons: {
									confirm : "Cerrar"
									},
								}).then((continuar) => {
									window.location = "ContratoAnterior.jsp?tab=1";
							});
						}
					});
				}
			});
		}
	</script>
  </head>
  
  <body>
	<form id="formCaratCont">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Car&aacute;tula del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
						<input type="button" class="btnInterfaceCancelar ui-button ui-corner-all float-right" 	id="imgEliminar" name="imgEliminar" 	value="Eliminar"	onclick="borraContrato();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ContratoAnterior.jsp?tab=1'" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblDefinitivo" id="lblDefinitivo" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblConcepto" id="lblConcepto" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput font-weight-bold" name="lblEstadoMod" id="lblEstadoMod"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoPagado" id="lblTotalContratoPagado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoRemanente" id="lblTotalContratoRemanente" readonly />
					</div>
				</div>
			</div>
			<div class="form-group" id="trpartidas">
				<div class="row">
					<div class="col">
						<table id="tblContratoPartidasMods" class="table table-striped table-bordered dt-responsive nowrap" >
							<thead >
								<tr>
									<th >L&Iacute;NEA CONS</th>
									<th >SUBPARTIDA</th>
									<th >CUCOP</th>
									<th >DESCRIPCI&Oacute;N</th>
									<th >CANTIDAD</th>
									<th >PRECIO UNITARIO</th>
									<th >% IVA</th>
									<th >M. POR L&Iacute;NEA</th>
									<th >M. Remanente c/IVA</th>
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnRegistrar" name="btnRegistrar" 	value="Registrar"	onclick="registraContrato();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
		    <input type="hidden" id="cIdContratoMat" name="cIdContratoMat" value="<%=cIdContrato%>"/>
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
		    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		    <input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		    <input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		    <input type="hidden" name="tipoMod" id="tipoMod" />
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" >
		    
		    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  			<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
  			<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
		    
		    <input type="hidden" name="totalAnterior" id="totalAnterior" />
		    <input type="hidden" name="totalMod" id="totalMod" />
		    <input type="hidden" name="totalNuevo" id="totalNuevo" />
		    
		    <!-- Resultado de consultas -->
		    <input type="hidden" name="nEstado" id="nEstado" />
		    <input type="hidden" name="cEstado" id="cEstado" />
		    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />	    
		    <!--  Hidden valores auxiliares -->
		    <input type="hidden" name="totalModAct" id="totalModAct" />
		    <input type="hidden" name="totalNuevoAct" id="totalNuevoAct" />
		    
		    <input type="hidden" name="cIdTipoConsolidado" id="cIdTipoConsolidado" />
		    <input type="hidden" name="cIdUEConsolidado" id="cIdUEConsolidado" />
		    <input type="hidden" name="cIdConsecutivoConsolidado" id="cIdConsecutivoConsolidado" />
		    <input type="hidden" name="cIdLineaConsolidado" id="cIdLineaConsolidado" />
		    
		    <input type="hidden" name="cIdSolicitud" id="cIdSolicitud" />
		    <input type="hidden" name="cIdLineaSolicitud" id="cIdLineaSolicitud" />
		    <input type="hidden" name="cEstadoLineaSolicitud" id="cEstadoLineaSolicitud" />
		    
		    <input type="hidden" name="cDescripcion" id="cDescripcion" />
		    <input type="hidden" name="nCantidad" id="nCantidad" />
		    <input type="hidden" name="mMonto" id="mMonto" />
		    
		    <input type="hidden" name="estadoLinea" id="estadoLinea" />
		    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
		    <input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
		    <input type="hidden" name="mMontoNeto" id="mMontoNeto" value="0"/>
		    <input type="hidden" name="mPrecioUnitario" id="mPrecioUnitario" value="0"/>
			<input type="hidden" name="lContratoAbierto" id="lContratoAbierto" value="0" />
			<input type="hidden" name="bEsXTotalPlu" id="bEsXTotalPlu" value="0" />
			<input type="hidden" name="mMontoTotalPlurianual" id="mMontoTotalPlurianual" value="0" />
			<input type="hidden" name="nIdPartidaPresupContMod" id="nIdPartidaPresupContMod" value="0" />
			<input type="hidden" name="isConvEjercicioAnt" id="isConvEjercicioAnt" value="0" />
			
			<input type="hidden" name="mContratoMontoNeto" id="mContratoMontoNeto" >   	
			<input type="hidden" name="mContratoMN" id="mContratoMN" >  
			<input type="hidden" name="cIdRFC" id="cIdRFC" value="0"/>
				
			<input type="hidden" name="mimportetotal" id="mimportetotal" value="0"/>
			<input type="hidden" name="mImporteBruto" id="mImporteBruto" value="0"/>
			<input type="hidden" name="mImporteIVA" id="mImporteIVA" value="0"/>
			
			<input type="hidden" name="mImportePago" id="mImportePago" value="0"/>
			<input type="hidden" name="mRemanenteContrato" id="mRemanenteContrato" value="0"/>
			<input type="hidden" name="cConceptoContrato" id="cConceptoContrato" value="0"/>
			
			<input type="hidden" name="nIdLineaConsolidado" id="nIdLineaConsolidado" >
			<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" >   	
			<input type="hidden" name="cIdCABM" id="cIdCABM" > 
			<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" >  
			<input type="hidden" name="validaExiste" id="validaExiste" > 
			<input type="hidden" name="nIdEstado" id="nIdEstado" value="1">
	</form>
  </body>
</html>