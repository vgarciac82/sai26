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
	session.setAttribute(GestionInterface.ATT_ContractConvCap4Definitivo,"");
	session.setAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo,-1);
	session.setAttribute(GestionInterface.ATT_nIdContModCap4,-1);
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
	<script type="text/javascript" charset="utf-8">
		var oTableContratosAprovados="";
		tabb=0;
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
				Map botones=nb.getBotones(roles,"ConvenioModificatorioCap4","nuevoConvCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
	
			%>		
			roles="<%=roles%>";
			consultaNuevoConv();
			$('#tblContratosAprobados').on('dblclick', 'tr',function(){
				$(oTableContratosAprovados.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
				
				var anSelected = fnGetSelected( oTableContratosAprovados );
				var aData = oTableContratosAprovados.fnGetData(anSelected[0]);
				$("#cIdContratoDefinitivo").val(aData[1]);
				if ((roles.toString().indexOf("ADMIN_RECMAT") < 0) && (roles.toString().indexOf("ANALISTA") < 0) && (roles.toString().indexOf("JEFES") < 0)) { 
					swal("No tiene permisos para realizar esta acción.",{icon:"info",button: "Cerrar"});
					return;
				}
				addNewConvenio();
			});
		});
		
		
	</script>
</head>
<body>
	<form id="formNuevoConveCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Crea Convenios Cap 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdDefinitivo">Contrato SAI: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CF-A04-1/2022" aria-label="Número de Contrato SAI, Ejemplo CF-A04-1/2022" aria-describedby="basic-addon1"  name="cIdDefinitivo" id="cIdDefinitivo"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cDescripcion">Concepto: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Objeto del contrato" aria-label="Objeto del contrato" aria-describedby="basic-addon1"  name="cDescripcion" id="cDescripcion"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdRFC">R.F.C: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="RFC del Proveedor" aria-label="RFC del Proveedor" aria-describedby="basic-addon1"  name="cIdRFC" id="cIdRFC"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnBuscarContratos" name="btnBuscarContratos" 	value="Buscar"	onclick="searchContracts();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos de captura para la generación del convenio modificatorio</legend>
			<div class="form-group">
				<div class="row" >
					<div class="input-group">
						<div class="col-2">		
							<label class="form-check-label" for="esPorTotalPlurianual">¿Es por el total plurianual el convenio modificatorio? </label>
						</div>
						<div class="col-auto">
			  				<input class="form-check-input" type="checkbox" id="esPorTotalPlurianual" name="esPorTotalPlurianual" >
			  			</div>
		  			</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdTipoMod">Tipo de Modificaci&oacute;n: </label>
						</div>
						<div class="col-2">
							<select class="custom-select" id="cIdTipoMod" name="cIdTipoMod" >
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row"  >
					<div class="col-6">
						<h1 style="color: blue">Dar doble clic en el contrato que se requiere hacer el convenio.</h1>
					</div>
				</div>
				<div class="row">
					<div class="input-group">
						<div class="col">
							<table id="tblContratosAprobados" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th >UNIDAD EJECUTORA</th>
						  				<th >CONTRATO SAI</th>
						  				<th >RFC</th>
						  				<th >PROVEEDOR</th>
						  				<th >MONTO BRUTO</th>
						  				<th >MONTO NETO</th>
						  				<th >CONCEPTO</th>			
						  				
									</tr>										
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="cIdUnidadEjecutora1" id="cIdUnidadEjecutora1" value="<%=usuarioTab1.getU_UR()%>" />
  		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
  		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
  		<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
  		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
  		<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab1.getLogin()%>"/>
  		<input type="hidden" name="tipoOperacion" id="tipoOperacion" value="1" />
  		<input type="hidden" name="tipoProceso" id="tipoProceso" value="3" />
  		<input type="hidden" name="bEsXTotalPlu" id="bEsXTotalPlu" value="0" />
  		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="" />
  		
	</form>
</body>
</html>