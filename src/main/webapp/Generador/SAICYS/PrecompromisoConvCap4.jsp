<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab1 = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab1 == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuarioTab1.getLogin();
	String role="";
	String roles="";
	Map rol =usuarioTab1.getRoles();
	String cIdContratoDefinitivo = "";
	String nConsecutivoMod="0";
	String nIdContModCap4="0";
	int mesActual=1;
	if (request.getParameter("cIdContratoDefinitivo") != null && request.getParameter("nConsecutivoMod") != null) {
		cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
		nConsecutivoMod=request.getParameter("nConsecutivoMod");
		nIdContModCap4=request.getParameter("nIdContModCap4");
		session.setAttribute(GestionInterface.ATT_ContractConvCap4Definitivo,cIdContratoDefinitivo.toString(  ));
		session.setAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo,request.getParameter("nConsecutivoMod").toString(  ) );
		session.setAttribute(GestionInterface.ATT_nIdContModCap4,request.getParameter("nIdContModCap4").toString(  ) );
	} else {
		cIdContratoDefinitivo = (String) session.getAttribute(GestionInterface.ATT_ContractConvCap4Definitivo);
		nConsecutivoMod=(String) session.getAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo);
		nIdContModCap4=(String) session.getAttribute(GestionInterface.ATT_nIdContModCap4);
	}
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
	<script type="text/javascript" charset="utf-8">
		tabb=4;
		var vEstado;
		$(document).ready(function() {
			showAndHideTabs();
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				Map botones=nb.getBotones(roles,"ConvenioModificatorioCap4","precompromisoConvCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
				
			%>
			roles="<%=roles%>";
			$("#cIdContratoDefinitivo").val("<%= cIdContratoDefinitivo %>");
			$("#nConsecutivoMod").val("<%= nConsecutivoMod %>");
			$("#nIdContModCap4").val("<%= nIdContModCap4 %>");
			infoQuery();
			vEstado=parseInt($("#nEstatus").val(),10);
			cargaPreCompromisoVacio();
			if(vEstado ==3 || vEstado ==4){
				$("#mComprometido").val($("#mImporteTotal").val());
			}
			
			cargaSuficiencias();
			clickHandlers();
			$("#mComprometido").formatCurrency();
			$("#mImporteTotal").formatCurrency();
			$("#difPrecompromiso").formatCurrency();
			hideAndShowButtonDevolverPrecom();
			hideAndShowButtonAutPrecom();
			hideAndShowButtonPrecomprometer();
		});
		
	</script>
</head>
<body>
	<form id="formPrecompromisoConveCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Datos del Convenio Cap 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarpreCompromisoPluriContrato" name="imgAprobarpreCompromisoPluriContrato" 	value="Pre-Comprometer"	onclick="precomprometer();" />
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarCompromisoPluriContrato" name="imgAprobarCompromisoPluriContrato" 	value="Autoriza Compromiso"	onclick="comprometer();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverpreCompromisoPluriContrato" name="imgDevolverpreCompromisoPluriContrato" 	value="Devolver"	onclick="devuelvePrecompromiso();" />
							<input type="button" class="btnInterfaceBackToTop ui-button ui-corner-all float-right" 	id="imgSalir" 	name="imgSalir" value="Salir" onclick="window.location = 'ConveniosCap4.jsp?tab=1';" />
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
						<input type="text" class="form-control transpInput" name="lblProveedor" id="lblProveedor" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblEstadoMod" id="lblEstadoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTipoMod" id="lblTipoMod" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalContratoModificado" id="lblTotalContratoModificado" readonly />
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control transpInput" name="lblTotalPorcentajeMod" id="lblTotalPorcentajeMod" readonly />
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Montos a precomprometer del contrato </legend>
			<div class="form-group" id="trinputsMontos">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<label for="mImporteTotal">Monto del Convenio:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="20"  aria-describedby="basic-addon1"  id="mImporteTotal" name="mImporteTotal" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Compromiso Actual:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="mComprometido" name="mPreComprometer" value="0">
						</div>
						<div class="col">
							<label for="mPreComprometer">Saldo Compromiso:</label>
							<input type="text" class="form-control" readonly="readonly" maxlength="12"  aria-describedby="basic-addon1"  id="difPrecompromiso" name="mPreComprometido" value="0">
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row"  >
					<div class="col-4">
						<h1 style="color: blue">Tabla del Precompromiso</h1>
					</div>
				</div>
				<div class="row" id="trEditarpreCompromisoPlurContrato" >
					<div class="col-4">
						<input class="btnInterfaceBG ui-button ui-corner-all" type="button" name="edit" id="edit" value="Editar">
					</div>
				</div>
				<div class="row" id="tr_dt_preCompromiso">
					<div class="input-group">
						<div class="col">
							<table id="dt_preCompromiso" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th>Estructura Program&aacute;tica</th>
										<th>Clave Interna</th>
										<th>Enero</th>
										<th>Febrero</th>
										<th>Marzo</th>
										<th>Abril</th>
										<th>Mayo</th>
										<th>Junio</th>
										<th>Julio</th>
										<th>Agosto</th>
										<th>Septiembre</th>
										<th>Octubre</th>
										<th>Noviembre</th>
										<th>Diciembre</th>
										<th>Contrato</th>
										<th>EP</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row"  >
					<div class="col-4">
						<h1 style="color: blue">Tabla del Disponible</h1>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="dt_suficiencia" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th>Estructura Program&aacute;tica</th>
										<th>Clave Interna</th>
										<th>Enero</th>
										<th>Febrero</th>
										<th>Marzo</th>
										<th>Abril</th>
										<th>Mayo</th>
										<th>Junio</th>
										<th>Julio</th>
										<th>Agosto</th>
										<th>Septiembre</th>
										<th>Octubre</th>
										<th>Noviembre</th>
										<th>Diciembre</th>
										<th>Anual</th>
										<th>Contrato</th>
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
			
		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%= cIdContratoDefinitivo %>" />
  		<input type="hidden" name="nConsecutivoMod" id="nConsecutivoMod" value="<%= nConsecutivoMod %>" />
  		<input type="hidden" name="nIdContModCap4" id="nIdContModCap4" value="<%= nIdContModCap4 %>" />
  		<input type="hidden" name="tipoOperacion" id="tipoOperacion" value="-1" />
  		<input type="hidden" name="tipoProceso" id="tipoProceso" value="3" />
  		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
  		<input type="hidden" name="mesDisponible" id="mesDisponible" value="<%=mesActual%>"/>
  		<input type="hidden" name="nEstatus" id="nEstatus" value="-1" />
	</form>
</body>
</html>