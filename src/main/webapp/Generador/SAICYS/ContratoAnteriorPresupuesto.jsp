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
	Map rol =usuarioTab.getRoles();
	
	String name_user=usuarioTab.getLogin();
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String isConvEjercicioAnt="";
	int aprobar=0;
	int devolver=0;
	if (session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null) {
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		isConvEjercicioAnt = (String)session.getAttribute(GestionInterface.ATT_ContratoIsModificatorioEjercicioAnt);
		
		cIdTipoContrato = cIdContrato.split("-")[0];
		if(cIdTipoContrato.equalsIgnoreCase("PLU")){
			cIdTipoContrato=cIdContrato.split("-")[1];
			cIdUnidadEjecutora = cIdContrato.split("-")[2];
			nIdConsecutivo = cIdContrato.split("-")[3];
		}else{
			cIdUnidadEjecutora = cIdContrato.split("-")[1];
			nIdConsecutivo = cIdContrato.split("-")[2];
		}	
			nIdConsecutivo = nIdConsecutivo.split("/")[0];
			cIdContrato=cIdTipoContrato+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo;
	}else 
		response.sendRedirect("ContratoModificatorio.jsp?tab=1");
	
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
		var oTableClaves;
		var roles="";
		$(document).ready(function() {
			tabb=3;
			<%
			    String roles="";
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				int imgAnular=0;
				int imgAprobar=0;
				int imgDevolver=0;
				int imgPdf=0;
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"RemanenteEjercicioAnterior","presupuestoEjercicioAnterior");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
						Map.Entry b = (Map.Entry)btn.next();%>
						$("#<%=b.getValue()%>").attr("disabled", true);<%
						String img=(String) b.getValue();
						
					}
	
			%>
			roles="<%=roles%>";
			showAndHideTabs();
			headerQuery();
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			loadClavesPresupuestalesContrato();
			$('#dt_clavepresup').on('dblclick', 'tr',function(){
				var aTrs = oTableClaves.fnGetNodes();
				if(aTrs.length==0){
					return;
				}
				$(this).addClass('row_selected');   
				var anSelected = fnGetSelected( oTableClaves );						
				var aData = oTableClaves.fnGetData(anSelected[0]);
				$("#epAUX").val(aData[0]);
				deleteEP();
			});
		});//Fin del document ready	
		function deleteEP(){
			if(parseInt($("#nIdEstado").val(),10)<4 ){
				queryFormPost("quitaEP_Temp_ContratoDelete", {async: false,
					callback: function() {
						$("#epAUX").val("");
						loadClavesPresupuestalesContrato();
					}
				});
			}
		}
		function headerQuery(){
			var cIdContratoDef=$("#cContratoDefinitivo").val();
			queryFormPost("mContratoHeaderReadEjerAnt", {async: false,
				callback: function() {
					$("#lblTotalContratoOriginal").val("Monto Contrato Original: $ "+formateaMoneda( parseFloat($("#mimportetotal").val()).toFixed(2)  ));
					$("#lblTotalContratoPagado").val("Monto Pagado Ejercicio Anterior: $ "+formateaMoneda(parseFloat($("#mImportePago").val()).toFixed(2) ));
					$("#lblTotalContratoRemanente").val("Monto Remanente: $ "+formateaMoneda(parseFloat($("#mRemanenteContrato").val()).toFixed(2) ));
					$("#lblEstadoMod").val("Estatus : "+$("#cEstado").val());
					hideButtons();
				}
			});
		}
		function hideButtons(){
			if(parseInt($("#nIdEstado").val(),10) >=4){
				$("#AgregarEPContrato").hide();
				$("#nIdClaveEgresosXContrato").hide();
				$("#imgAprobarPresupuestoCont").hide();
			}else{
				if(roles.indexOf("JEFE")>=0 || roles.toString().indexOf("ADMIN_RECMAT") >= 0 ){
					$("#imgAprobarPresupuestoCont").show();
					$("#msgAutorizar").hide();
				}else{
					$("#imgAprobarPresupuestoCont").hide();
					$("#msgAutorizar").show();
				}
			}
		}
		function apruebaContrato(){
			if(parseInt($("#nIdEstado").val(),10)<2 ){
				swal({
					title: "",
					text: "El estatus del contrato no permite aprobarlo.",
					icon: "info",
					buttons: {
						confirm : "Cerrar"
					},
				}).then((continuar) => {
					return;
				});
			}else{
				var aTrs = $('#dt_clavepresup').dataTable().fnGetNodes();
				if(aTrs.length<=0){
					swal({
						title: "",
						text: "Favor de capturar al menos una estructura presupuestal",
						icon: "warning",
						buttons: {
							confirm : "Cerrar"
							},
						}).then((continuar) => {
							return;
					});
				}else{
					var object=llenaObject();
					$.ajax({url: "../../ContratacionFormalizadaServlet" , type:'post' , async: false
						,data:object
						,dataType: 'json', success: 
							function(j){
								showAndHideTabs();
								headerQuery();
								swal(j[0].MENSAJE,"info",{ button: "Cerrar"});
						}, error: function( jqXHR, textStatus, errorThrown ) {
							swal("Error.",{icon:"warning",button: "Cerrar"});
						}
					});	
				}	
				
			}			
		}
		function llenaObject(){
			var data0= {
				tipoProceso:$("#tipoProceso").val(),
				tipoOperacion:$("#tipoOperacion").val(),
				cContratoDefinitivo:$("#cContratoDefinitivo").val(),
				rfc:$("#cIdRFC").val(),
				cEjercicio:$("#cEjercicio").val(),
				cUnidadEjecutora:$("#cIdUnidadEjecutora").val()
			};
			return data0;
		}
		function buscaClavePresupuestal(){
			pp = window.open('../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=' + $('#U_LOGIN').val() + '&cIdDocumento=' + $('#cContratoDefinitivo').val() 
					+ '&cIdRFC=' + $('#cIdRFC').val() + '&cIdProcedimiento=' + $('#cIdProcedimiento').val()+ '&cuentaDisponible=' + $('#cuentaDisponible').val()
					+ '&isPlurianual=0', 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');	
		}
		function fnClickAddRowC() {
			if(parseInt($("#nIdEstado").val(),10) >=4){
				return;
			}
			rowCount = $('#dt_clavepresup tr').length;
			var aTrs = $('#dt_clavepresup').dataTable().fnGetNodes(); 
			var vep = $('#ep').val();
			if (vep == '') 
				return ;
			for(var i=0;i<aTrs.length;i++){
				queryFormPost("epsReadContratoPlurianual", {async: false});
				if(parseInt($("#epsConsulta").val(),10)>0){
					swal({
						title: "",
						text: "Ya existe esa clave ingrese otra..",
						icon: "info",
						buttons: {
							confirm : "Cerrar"
						},
					}).then((continuar) => {
						return;
					});
				}
			}
			var tmp = vep.lastIndexOf( "\." );
			var uEje= vep.substring( tmp - 3, tmp);
			var cint = vep.substring( tmp -3 );
			$("#cIdUnidadEjecutoraEP").val(uEje);
			$("#unidadEjecutoraCA").val(uEje);
			queryFormPost("readCCentroContableUE", {async: false,
				callback: function() {	
					$("#cIdEntidadContableTbl").val($("#cCentroContable").val());
				}
			});
			$("#ClaveInterna").val(cint);
			queryFormPost({
				queryName: "agregaEPContratoCreatePasivoPlurianual",
				async: false,
				callback: function() {
					loadClavesPresupuestalesContrato();
					$('#ep').val("");
				}
			});
		}
		function loadClavesPresupuestalesContrato(){
			var qw = " cEjercicio = '" + $("#cEjercicio").val() +
 			"' AND cIdTipoContrato = '" + $("#cIdTipoContrato").val() +
 			"' AND cIdContrato = '" + $("#cIdContratoMat").val()+"'";
			oTableClaves=$('#dt_clavepresup').dataTable({
				bPaginate: false,
				bLengthChange: false,
				bFilter: false,
				bInfo: false,
				bAutoWidth: false,
				sScrollY: 100,
				bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,   
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tContratoEP_TMP&qw="+qw,
				aoColumns: [
					{ sName: "nIdClaveEgresos"},
					{ sName: "ClaveInterna" }
				]
			}) ;
		}
	</script>
  </head>
		  
  <body>
	<form id="formPresupCont">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Presupuesto</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarPresupuestoCont" name="imgAprobarPresupuestoCont" 	value="Autorizar"	onclick="apruebaContrato();" />
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
				<div class="row" id="msgAutorizar" style="display: none;">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="inputMsgAutoriza" id="inputMsgAutoriza" value="Solicita a tú Jefe la autorización del contrato." readonly style="color: red;"/>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Claves</legend>
			<div class="form-group">
				<div class="row">
					<div class="col-md-6">
						<label for="ep">E.P.</label>
						<input type="text" class="form-control" placeholder="Seleccione la nueva estructura presupuestal que desea agregar."  
						id="ep" name="ep" readonly>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="nIdClaveEgresosXContrato" name="nIdClaveEgresosXContrato" value="..." onclick="buscaClavePresupuestal()" 
						class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start" title="Dar clic para mostrar las estructuras presupuestales con recurso disponible."/>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="AgregarEPContrato" name="AgregarEPContrato" value="Agregar" onclick="fnClickAddRowC()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="dt_clavepresup" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr align="left">
									<th>C&oacute;digo SAI</th> 
									<th>Clave SHCP</th> 
							 	</tr> 
							</thead>
						</table>
					</div>
					
				</div>
			</div>
		</fieldset>
        <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
        <input type="hidden" name="cEjercicioTbl" id="cEjercicioTbl" value="<%=cEjercicio%>"/>
	    <input type="hidden" name="nIdConsecutivo" id="nIdConsecutivo" value="<%=nIdConsecutivo%>"/>
	    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
	    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
	    <input type="hidden" name="cTContratoTbl" id="cTContratoTbl" value="<%=cIdTipoContrato%>" />
	    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
	    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" />
	    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="cIdContratoTbl" id="cIdContratoTbl" value="<%=cIdContratoDefinitivo%>" />
	    <input type="hidden" name="cConsecutivoMod" id="cConsecutivoMod" value="<%=cIdConsecutivoMod%>" />
	    <input type="hidden" name="cIdContratoDefinitivoMod" id="cIdContratoDefinitivoMod" />
	    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento"/>
	    <input type="hidden" name="tipoMod" id="tipoMod" />
        <input type="hidden" name="totalAnterior" id="totalAnterior" />
	    <input type="hidden" name="totalMod" id="totalMod" />
	    <input type="hidden" name="totalNuevo" id="totalNuevo" />
	    <input type="hidden" name="nIdEstado" id="nIdEstado" />
	    <input type="hidden" name="nTipoPago" id="nTipoPago" />
		<!-- Para unir apartado con precompromiso -->
	    <input type="hidden" name="nApartadosUsados" id="nApartadosUsados" />
	    <input type="hidden" name="mApartadoReal" id="mApartadoReal" />
		<input type="hidden" name="cIdProcedimiento" id="cIdProcedimiento" />
	    <!--  Auxiliares para consulta -->		    
       	<input type="hidden" name="epAUX" id="epAUX" />
       	<input type="hidden" name="cIdUnidadEjecutoraEP" id="cIdUnidadEjecutoraEP" />
	    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
	    <input type="hidden" name="epsConsulta" id="epsConsulta" />
	    
	    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
 			<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
 			<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
	    <input type="hidden" name="lContratoAbierto" id="lContratoAbierto" value="0" />
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
		<input type="hidden" name="cEstado" id="cEstado" />
		
		<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />
		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
		
		<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>

	    <input type="hidden" id="cIdContratoMat" name="cIdContratoMat" value="<%=cIdContrato%>"/>
	    <input type="hidden" id="ClaveInterna" name="ClaveInterna" value=""/>
	    <input type="hidden" id="cCentroContable" name="cCentroContable" value=""/>
	    
	    <input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" value=""/>
	    <input type="hidden" id="cIdEntidadContableTbl" name="cIdEntidadContableTbl" value=""/>
	    <input type="hidden" id="unidadEjecutoraCA" name="unidadEjecutoraCA" value=""/>
	    <input type="hidden" id="tipoProceso" name="tipoProceso" value="2"/>
	    <input type="hidden" id="tipoOperacion" name="tipoOperacion" value="1"/>
	</form>
  </body>
</html>		
			