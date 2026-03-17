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
<title>Partidas de contrato</title>
	<script type="text/javascript" charset="utf-8">
		var roles;
		tabb=2;
		var tipo;
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
				Map botones=nb.getBotones(roles,"ContratosArt25","PartidasContratoArt25");
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
			hideDivs();
			showTables();
		});//fin del document ready
		
	</script>
</head>
<body>
	<form id="formPartidasContArt25">
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
			<legend class="w-auto px-2"> Partidas del contrato</legend>
			<div class="form-group" id="divLineasServ">
				<div class="row">
					<div class="col">
						<table id="tblLineas-serv" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" style="display: none;">Cantidad <br />M&aacute;xima </th>
									<th align="center" >Precio Unitrio</th>
									<th align="center" style="display: none;"></th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto</th>
									<th style="display: none;"></th>	
									<th style="display: none;"></th>
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divLineasBienes">
				<div class="row">
					<div class="col">
						<table id="tblLineas-bienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" style="display: none;">Cantidad <br />M&aacute;xima </th>
									<th align="center" >Precio Unitrio</th>
									<th align="center" style="display: none;"></th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto</th>
									<th style="display: none;"></th>	
									<th style="display: none;"></th>
									
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divLineasBienes-ContAbierto">
				<div class="row">
					<div class="col">
						<table id="tblLineas-bienesContAbierto" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" >Cantidad <br />M&aacute;xima </th>
									<th align="center" >Precio Unitrio</th>
									<th align="center" style="display: none;"></th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto</th>
									<th style="display: none;"></th>	
									<th>Monto Neto<br/> M&aacute;ximo</th>
									
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divLineasServ-ContAbierto">
				<div class="row">
					<div class="col">
						<table id="tblLineas-servContAbierto" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th style="display: none;"></th>
									<th align="center">Unidad Requi</th>
									<th align="center">Partida <br/> Contrato</th>
									<th align="center">Partida</th>
									<th align="center">Cucop</th>
									
									<th align="center">Descripci&oacute;n <br/> Adicional</th>
									<th align="center">Cantidad</th>
									<th align="center" style="display: none;" >Cantidad <br />M&aacute;xima </th>
									<th align="center" >Monto M&iacute;nimo</th>
									<th align="center" >Monto M&aacute;ximo</th>
									<th align="center">IVA</th>
									<th align="center" >Monto Neto <br/>M&iacute;nimo</th>
									<th style="display: none;"></th>	
									<th align="center">Monto Neto<br/>M&aacute;ximo</th>
									
				        		</tr>										
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" style="display: none;">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="gurdar" name="gurdar" 	value="Guardar"	 />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" name="esCucopGasolina" id="esCucopGasolina" value="" />
		
  	</form>
</body>
</html>