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
	String nConsecutivoMod="";
	String nIdContModCap4="";
	//System.out.println("***********"+request.getParameter("cIdContratoDefinitivo") +"*****"+request.getParameter("nConsecutivoMod")+"****"+request.getParameter("nIdContModCap4"));
	if (request.getParameter("cIdContratoDefinitivo") != null && request.getParameter("nConsecutivoMod") != null) {
		cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
		nConsecutivoMod=request.getParameter("nConsecutivoMod") ;
		nIdContModCap4= request.getParameter("nIdContModCap4") ;
		session.setAttribute(GestionInterface.ATT_ContractConvCap4Definitivo,cIdContratoDefinitivo.toString(  ));
		session.setAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo,request.getParameter("nConsecutivoMod").toString(  ) );
		session.setAttribute(GestionInterface.ATT_nIdContModCap4,request.getParameter("nIdContModCap4").toString(  ) );
	} else {
		cIdContratoDefinitivo = (String) session.getAttribute(GestionInterface.ATT_ContractConvCap4Definitivo);
		nConsecutivoMod=(String) session.getAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo);
		nIdContModCap4=(String) session.getAttribute(GestionInterface.ATT_nIdContModCap4);
	}
	//System.out.println("***********"+cIdContratoDefinitivo +"*****"+nConsecutivoMod+"****"+nIdContModCap4);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
	<script type="text/javascript" charset="utf-8">
		tabb=3;
		var oTableClaves;
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
				Map botones=nb.getBotones(roles,"ConvenioModificatorioCap4","presupuestoConvCap4");
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
			agregaDatePickerFechas();
			infoQuery();
			loadClavesPresupuestalesContratoMod();
			hideAndShowDates();
			hideAndShowButtonAprobar();
			hideAndShowButtonDevuelvePresup();
			disabledenabledEdicionConvenio();
			
			//Delete EP
			$('#dt_clavepresup').on('dblclick', 'tr',function(){
				$(oTableClaves.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
				if($("#nEstatus").val()!=1){
					swal("El estatus del convenio no permite la eliminación de EP´S.",{icon:"info",button: "Cerrar"});
					return;
				}
				var anSelected = fnGetSelected( oTableClaves );
				var aData = oTableClaves.fnGetData(anSelected[0]);
				deleteEP(aData[0]);
				
			});
		});
	</script>
</head>
<body>
	<form id="formPresupuestoConveCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Datos del Convenio Cap 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceAutoriza ui-button ui-corner-all float-right" 	id="imgAprobarPresupuestoContMod" name="imgAprobarPresupuestoContMod" 	value="Aprobar"	onclick="apruebaContModCap4();" />
							<input type="button" class="btnInterfaceReturn ui-button ui-corner-all float-right" 	id="imgDevolverPresupuestoContMod" name="imgDevolverPresupuestoContMod" 	value="Devolver"	onclick="devuelveContModCap4();" />
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
		<fieldset class="form-group border p-3" id="fechas">
			<legend class="w-auto px-2">Edici&oacute;n del Contrato Modificado</legend>
			
			<div class="form-group">
				<div class="row" id="trObjetoConvenio">
					
					<div class="col-md-3"  id="trffor">
						<label for="fechaFormaliza">Fecha de Formalizaci&oacute;n</label>
						<div class="input-group date" id="fechaFormaliza" data-target-input="nearest">
				          <input type="text" class="form-control datetimepicker-input" data-target="#fechaFormaliza" title="Fecha de Formalización del convenio modificatorio" id="fechaFormalizacion" name="fechaFormalizacion" value=""/>
				          <div class="input-group-append" data-target="#fechaFormaliza" data-toggle="datetimepicker" title="Fecha de Formalización del convenio modificatorio">
				            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
				          </div>
				        </div>
			        </div>
			        <div class="col-md-3" id="trfini" style="display: none;">
						<label for="fInicial">Fecha Inicio</label>
						<div class="input-group date" id="fInicial" data-target-input="nearest">
				          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha inicio del convenio modificatorio" id="fechaInicio" name="fechaInicio" value=""/>
				          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha inicio del convenio modificatorio">
				            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
				          </div>
				        </div>
			        </div>
			        <div class="form-group col-md-3"  id="trffin" style="display: none;">
						<label for="fFinal">Fecha Fin</label>
						<div class="input-group date col-xs-2" id="fFinal" data-target-input="nearest" >
							<input type="text" class="form-control datetimepicker-input" data-target="#fFinal" id="fechaFin" name="fechaFin" title="Fecha final del convenio modificatorio" value=""  />
							<div class="input-group-append" data-target="#fFinal" data-toggle="datetimepicker" title="Fecha final del convenio modificatorio">
							  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							</div>
						</div>
					</div>
					 <div class="form-group col-md-3"  id="trfentrega" style="display: none;">
						<label for="fEntrega">Fecha de Entrega</label>
						<div class="input-group date col-xs-2" id="fEntrega" data-target-input="nearest" >
							<input type="text" class="form-control datetimepicker-input" data-target="#fEntrega" id="fechaEntrega" name="fechaEntrega" title="Fecha entrega de los bienes" value=""  />
							<div class="input-group-append" data-target="#fEntrega" data-toggle="datetimepicker" title="Fecha entrega de los bienes">
							  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
							</div>
						</div>
					</div>
				</div>
				<div class="row" id="trNumConvenio">
					<div class="col-md-6">
						<label for="objConv">Objeto Convenio</label>
						 <textarea class="form-control" id="objConv" rows="3"  placeholder="Objeto del convenio modificatorio." aria-describedby="basic-addon1"></textarea>
					</div>
					<div class="col-md-6">
						<label for="cNoConvenio">N&uacute;mero. de Convenio</label>
						<input type="text" class="form-control" placeholder="Número de convenio modificatorio CNET" aria-label="Número de convenio modificatorio CNET" aria-describedby="basic-addon1"  id="cNoConvenio" name="cNoConvenio">
					</div>
				</div>
				
			</div>
			
		</fieldset>
		<fieldset class="form-group border p-3" id="fieldsetClves">
			<legend class="w-auto px-2">Estructuras presupuestales "EP"</legend>
			<div class="form-group">
				
				<div class="row" id="divAddEP">
					<div class="col-md-6" >
						<label for="ep">E.P.</label>
						<input type="text" class="form-control" placeholder="Seleccione la nueva estructura presupuestal que desea agregar."
						id="ep" name="ep" readonly>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="nIdClaveEgresosXContrato" name="nIdClaveEgresosXContrato" value="..." onclick="buscaClavePresupuestal()" 
						class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"  title="Dar clic para mostrar las estructuras presupuestales con recurso disponible."/>
					</div>
					<div class="col-auto">
						<br>
						<input type="button" id="AgregarEPContrato" name="Add2" value="Agregar" onclick="fnClickAddRowC()" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<table id="dt_clavepresup" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th>Estructura Presupuestal "EP"</th> 
									<th>Clave SHCP</th> 
								</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%= cIdContratoDefinitivo %>" />
  		<input type="hidden" name="nConsecutivoMod" id="nConsecutivoMod" value="<%= nConsecutivoMod %>" />
  		<input type="hidden" name="nIdContModCap4" id="nIdContModCap4" value="<%= nIdContModCap4 %>" />
  		<input type="hidden" name="tipoOperacion" id="tipoOperacion" value="-1" />
  		<input type="hidden" name="tipoProceso" id="tipoProceso" value="3" />
  		<input type="hidden" name="nEstatus" id="nEstatus" value="-1" />
  		<input type="hidden" name="cIdUnidadMedida" id="cIdUnidadMedida" value="" />
  		<input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" value="" />
  		<input type="hidden" name="cIdRFC" id="cIdRFC" value="" />
  		<input type="hidden" name="cuentaDisponible" id="cuentaDisponible" value="82106" />
  		<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
		<input type="hidden" id="cEjercicio" name="cEjercicio" value="2022"/>
		<input type="hidden" id="cIdUnidadEjecutoraEP" name="cIdUnidadEjecutoraEP" value=""/>
  		<input type="hidden" id="cIdContratoMat" name="cIdContratoMat" value=""/>
  		<input type="hidden" id="cContratoDefinitivo" name="cContratoDefinitivo" value=""/>
  		<input type="hidden" id="cIdTipoContrato" name="cIdTipoContrato" value="CF"/>
  		<input type="hidden" id="ClaveInterna" name="ClaveInterna" value=""/>
  		<input type="hidden" id="nIdClaveEgresos" name="nIdClaveEgresos" value=""/>
  		<input type="hidden" id="U_LOGIN" name="U_LOGIN" value="<%= usuarioTab1.getLogin() %>"/>
  		
	</form>
		
</body>
</html>