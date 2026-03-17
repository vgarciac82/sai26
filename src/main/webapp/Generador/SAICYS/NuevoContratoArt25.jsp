<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
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
	int cEjercicio = 0;
	cEjercicio = Calendar.getInstance().get(Calendar.YEAR);//(String)session.getAttribute(GestionInterface.ATT_ContratoEjercicio);
	
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Nuevo</title>
	<script type="text/javascript" charset="utf-8">
		var cEjercicio;
		var oTable;
		var roles;
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
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(role,"ContratosArt25","NuevoContratoArt25");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
	
			%>	
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false,
				callback : function() {
					$("#cIdUnidadEjecutora").val("<%=usuarioTab1.getU_UR()%>");
					mostrar();
				}
			});
			$('#tblNuevoContratoArt25').on('dblclick', 'tr',function(){
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
			  	var anSelected=fnGetSelected(oTable);
			  	var aData=oTable.fnGetData(anSelected[0]);
			  	
			  	if(aData!= ""){
			  		$("#cIdContratoDefinitivo").val(aData[1]);
			  		$("#cDB").val(aData[6]);
			  		queryFormPost("sp_migraContratoArt25", {async: false,//Aqui me quede
			  			callback : function() 
						{
					  		swal({
					  			title: "",
					  			text: "Contrato Migrado.",
					  			icon: "info",
					  			buttons: {
					  				confirm : "Cerrar"
					  				},
					  			}).then((continuar) => {
					  				window.location = 'ContratosArt25.jsp?tab=1';
					  		});
			  			}
			  		});
			  		
		  		}
				
			});
		});//fin del document ready
		
	</script>
</head>
<body>
	<form id="formNuevo">
  		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Nuevo Contrato Art. 25</legend>
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
							<label for="cIdDefinitivo">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CS-A04-1/2023" aria-label="Número de Contrato SAI, Ejemplo CS-A04-1/2023" aria-describedby="basic-addon1"  name="cIdDefinitivo" id="cIdDefinitivo"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cDescripcion">Objeto del contrato: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Concepto de la contratación" aria-label="Concepto de la contratación" aria-describedby="basic-addon1"  name="cDescripcion" id="cDescripcion"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdRFC">RFC: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="RFC del proveedor" aria-label="RFC del proveedor" aria-describedby="basic-addon1"  name="cIdRFC" id="cIdRFC"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnBuscaContArt25" name="btnBuscaContArt25" 	value="Buscar"	onclick="mostrar();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblNuevoContratoArt25" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr> 
				        		    <th>UNIDAD<BR/> EJECUTORA</th>
				        			<th>CONTRATO SAI</th>
				        			<th>CONTRATO CNET</th>
				        			<th>PROVEEDOR</th>
				        			<th>RAZ&Oacute;N<BR/> SOCIAL</th>
				        			<th>CONCEPTO</th>			
				        			<th style="display: none;">DB</th>
				        		</tr>										
							</thead>
						</table>
					</div>
					
				</div>
			</div>
		</fieldset>
  		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
  		<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuarioTab1.getU_UR() %>" />
  		<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuarioTab1.getLogin()%>"/>	
  		<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
  		
  		<input type="hidden" name="cDB" id="cDB" value="" />
  		<input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>" />
  		<input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="" />
	</form>
</body>
</html>