<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	Map rol =usuario.getRoles();

 %>
<!DOCTYPE html>
<html>
	<head>
		<title>My JSP 'LayoutApartadoAutorizados.jsp' starting page</title>
		<script type="text/javascript" charset="utf-8">
			var oTableConsulta="";
			$(document).ready(function() {
				<%
				    String role="";
				    String roles="";
					NegativaPestana NegPestana=new NegativaPestana();
					NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
					NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
					
					Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						role=(String)r.getKey();
						roles += r.getKey().toString()+",";
					}
					Map botones=nb.getBotones(role,"LayoutRequisicion","PorGenerar");
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
				$("#nPestana").val(4);
				initTablaAutorizados();
			}); //fin document ready
		</script>
	</head>
	<body>
		<form action="">
	  		<fieldset class="form-group border p-3">
		 		<legend class="w-auto px-2">Apartado/Suficiencia Autorizados por SICOP</legend>
		 		<div class="form-group" >
					<div class="row">
						<div class="col">
							<table id="dtAutorizados" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th>Integraci&oacute;n</th>
										<th>Folios</th>
										<th>Requisiciones</th>
										<th>Tipo de Documento</th>
										<th>Estatus</th>
									</tr>									
								</thead>
							</table>
						</div>
					</div>
				</div>
		 	</fieldset>
		</form>
	</body>
</html>