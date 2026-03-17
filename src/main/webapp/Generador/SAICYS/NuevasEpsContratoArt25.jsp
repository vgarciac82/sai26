<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.util.*" %>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();
	String cIdContratoDefinitivo="";
	if(request.getParameter("cIdContratoDefinitivo")!=null){
		cIdContratoDefinitivo=request.getParameter("cIdContratoDefinitivo");
		session.setAttribute(GestionInterface.ATT_ContratArt25Definitivo, cIdContratoDefinitivo);
	}else{
		cIdContratoDefinitivo=(String)session.getAttribute(GestionInterface.ATT_ContratArt25Definitivo);
	}
	//System.out.print(cIdContratoDefinitivo);
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Nuevas Ep'S</title>
	<script type="text/javascript" charset="utf-8">
		var roles;
		tabb=5;
		$(document).ready(function() {
			showAndHideTabs();
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(roles,"ContratosArt25","PresupuestoContratoArt25");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			roles="<%=roles%>";
			$("#tipoOperacion").val(2);
			$("#cIdContratoDefinitivo").val("<%=cIdContratoDefinitivo%>");
			consultaDatosMigrados();
			loadClavesPresupuestalesContrato();
		});//fin del document ready
		
	</script>
</head>
<body>
	<form id="formNuevasEpsContArt25">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Datos del Contrato</legend>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDefinitivo" id="lblDefinitivo"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cConceptoContrato" id="cConceptoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTipoContrato" id="lblTipoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cEstado" id="cEstado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalIVA" id="lblTotalIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNeto" id="lblTotalNeto"  readonly/>
					</div>
				</div>
				<div class="row" id="divTotalMax">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNetoMax" id="lblTotalNetoMax"  readonly/>
					</div>
				</div>
				
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2">Estructuras presupuestales</legend>
			<div class="form-group">
				<div class="row" id="divRadicado" style="display: none;">
					<div class="col-auto">		
						<label class="form-check-label" for="checkDispRadicado">¿Es con recurso radicado? </label>
					</div>
					<div class="col-auto">
		  				<input class="form-check-input" type="checkbox" id="checkDispRadicado" name="checkDispRadicado" value="0" onclick="muestraDispRadicado()">
		  			</div>
				</div>
				<div class="row">
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
						<input type="button" id="AgregarEPContrato" name="Add2" value="Agregar" onclick="fnClickAddRowC(9)" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start"/>
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
		<input type="hidden" id="Partida" name="Partida" value="2"/>
		<input type="hidden" id="Partida2" name="Partida2" value="3"/>
		<input type="hidden" id="Partida3" name="Partida3" value="5"/>
		<input type="hidden" id="Partida1" name="Partida1" value="1"/>
  	</form>
</body>
</html>