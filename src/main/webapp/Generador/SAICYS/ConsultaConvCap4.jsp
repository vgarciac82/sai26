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
	session.setAttribute(GestionInterface.ATT_ContractConvCap4Consecutivo,0);
	session.setAttribute(GestionInterface.ATT_nIdContModCap4,0);
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
	<script type="text/javascript" charset="utf-8">
		var oTableConvenios="";
		tabb=1;
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
				Map botones=nb.getBotones(roles,"ConvenioModificatorioCap4","consultaConvCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
	
			%>		
			roles="<%=roles%>";
			consultaConv();
			$('#tblconsultaConveniosContCap4').on('dblclick', 'tr',function(){
				$(oTableConvenios.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
				
				var anSelected = fnGetSelected( oTableConvenios );
				var aData = oTableConvenios.fnGetData(anSelected[0]);
				$("#cIdContratoDefinitivo").val(aData[2]);
				$("#nConsecutivoMod").val(aData[1]);
				$("#nIdContModCap4").val(aData[7]);
				window.location = 'ConveniosCap4.jsp?tab=2&cIdContratoDefinitivo='+aData[2]+'&nConsecutivoMod='+aData[1]+'&nIdContModCap4='+aData[7];
				
			});
			
		});
	</script>
</head>
<body>
	<form id="formConsultaConv">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Consulta Convenios Cap 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cUnidadEjecutora">Unidad Ejecutora: </label>
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
							<label for="cIdDefinitivo">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CF-A04-1/2022" aria-label="Número de Contrato SAI, Ejemplo CF-A04-1/2022" aria-describedby="basic-addon1"  
							name="cIdDefinitivo" id="cIdDefinitivo"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnConsultaConveniosCap4" name="btnConsultaConveniosCap4" 	value="Buscar"	onclick="searchConvenios();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblconsultaConveniosContCap4" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th>Ejercicio</th>
									<th># Modificaci&oacute;n</th>
									<th>Contrato SAI</th>
									<th>Estatus</th>
				        			<th>Monto Anterior</th>
				        			<th>Monto Modificado</th>
				        			<th>Monto total</th>
				        			<th style="display: none;">idContratoMod</th>
								</tr>										
							</thead>
						</table>
					</div>
					
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="tipoOperacion" id="tipoOperacion" value="3" />
  		<input type="hidden" name="tipoProceso" id="tipoProceso" value="3" />
  		<input type="hidden" name="cIdUnidadResponsableUsuario" id="cIdUnidadResponsableUsuario" value="<%= usuarioTab1.getU_UR() %>"/>
  		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="" />
  		<input type="hidden" name="nConsecutivoMod" id="nConsecutivoMod" value="0" />
  		<input type="hidden" name="nIdContModCap4" id="nIdContModCap4" value="0" />
  		
	</form>
</body>
</html>