<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	
	String roles="";
	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user=usuario.getLogin();
	String cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	Map rol =usuario.getRoles();

 %>
<!DOCTYPE html>
<html>
	<head>
		<title>Layout de Apartado "Suficiencia Presupuestal"</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		
		
	 	
		<style type="text/css" title="currentStyle"> 
			@import "../../css/custom-theme/jquery-ui-1.8.16.custom.css";
	 		@import "../../css/interfaz.css";
		</style>
		<script src="https://code.jquery.com/jquery-3.5.0.js"></script>
		<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap502/css/bootstrap.css"/>
		<link rel="stylesheet" type="text/css" href="../../Bootstrap/Bootstrap-dataTables/datatables.css"/>
		
		<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.js"></script>
		<script type="text/javascript" src="../../Bootstrap/Bootstrap502/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.js"></script>
		<script type="text/javascript" src="../../Bootstrap/Bootstrap-dataTables/datatables.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
		<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
		<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
		
		<link rel="stylesheet" href="https://code.jquery.com/ui/1.13.2/themes/base/jquery-ui.css">
		<script src="https://code.jquery.com/ui/1.13.2/jquery-ui.js"></script>
		
		<script type="text/javascript" src="../js/crud.js"></script>
		<script type="text/javascript" src="../../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/funciones.js"></script>
		<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/FixedColumns.js"></script>
		<script type="text/javascript" src="../js/LayoutRequisicion.js"></script>
		
		
		<script type="text/javascript">
			var oTableConsulta="";
			$(document).ready(function() {
				<%
					NegativaPestana NegPestana=new NegativaPestana();
					NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
					
					Iterator it1 = rol.entrySet().iterator();
					while (it1.hasNext()) {
						Map.Entry r = (Map.Entry)it1.next();
						roles += r.getKey().toString()+",";
					}
					if(roles.length()>0){
						roles = roles.substring(0,roles.length()-1);
					}
					Map pestanas=ebl.getPestana(roles,"RecepcionMaterial");
					Iterator it = pestanas.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry e = (Map.Entry)it.next();
						System.out.println(e.getValue());
						%>
						
						$( "#<%=e.getValue()%>" ).attr("disabled", true);
						<%
					}
				%>
				$(".tabs").tabs();
				var roles="<%=roles%>";
				if (roles.indexOf("ADMIN_RECMAT") >= 0){
					$("#isAdmin").val(0);
				}
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cboUnidadEjecutora", {async: false});
				$("#cboUnidadEjecutora").val($("#cIdUnidadEjecutoraUsuario").val());
				if($("#isAdmin").val() == "0"){
					$("#cboUnidadEjecutora").prepend("<option value='*'> * - OFICINAS CENTRALES</option>");
					$("#cboUnidadEjecutora").prepend("<option value='**'> ** - GERENCIAS ESTATALES</option>");
				}
				searchApartados();
		  		
		  		$("#layoutPorGenerar").click(function() {
					window.location = "LayoutRequisicion.jsp?tab=" + 0;
				});
				$("#layoutProcesoEnvio").click(function() {
					var folios=getFolios("dtPorGenerar");
					
					if(folios==""){
						alert("No se ha seleccionadao ningun registro.");
						window.location = "LayoutRequisicion.jsp?tab=" + 0;
					} else{
						window.location = "LayoutRequisicion.jsp?tab=" + 1+"&folios="+folios;	
					}
				});
				$("#layoutPorGenerar").click(function() {
					window.location = "LayoutRequisicion.jsp?tab=" + 0;
				});
				$("#layoutEnviados").click(function() {
					window.location = "LayoutRequisicion.jsp?tab=" + 2;
				});
				$("#layoutAutorizados").click(function() {
					window.location = "LayoutRequisicion.jsp?tab=" + 3;
				});
				
				
			});//Fin del document ready
		</script>
	</head> 
	<body>
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
		  		<fieldset class="form-group border p-3">
			 		<legend class="w-auto px-2">Genera Layout Apartado</legend>
			 			<div class="form-group">
			 				<div class="row">
								<div class="input-group col-md-8">
						 			<div class="form-check">
									  <input class="form-check-input" type="radio" name="rdoOriginal" id="rdoOriginal" checked="checked">
									  <label class="form-check-label" for="rdoOriginal">Original</label>
									</div>
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class="row">
								<div class="input-group">
									<div class="col-2">
										<label for="cboUnidadEjecutora">Unidad Ejecutora: </label>
									</div>
									<div class="col-6">
										<select class="custom-select" id="cboUnidadEjecutora" name="cboUnidadEjecutora" onchange="searchDatos();">
											<option value="<%=usuario.getU_UR()%>" selected="selected">
										</select>
									</div>
								</div>
							</div>
						</div>
			 	</fieldset>
			 </div>
			 <div class="col-md-12 col-lg-12 col-sm-12">
		  		<fieldset class="form-group border p-3">
			 		<legend class="w-auto px-2"></legend>
					<div class="tabs" id="tabs">
			 			<ul class="nav nav-tabs">
			 				<li class="nav-item"><a href="#tabs-0" id="layoutPorGenerar" class="nav-link" aria-current="page">Por Generar </a></li>
						    <li class="nav-item"><a href="#tabs-1" id="layoutProcesoEnvio" class="nav-link" aria-current="page">Proceso de Env&iacute;o</a></li>
						    <li class="nav-item"><a href="#tabs-2" id="layoutEnviados" class="nav-link" aria-current="page">Enviados</a></li>
						    <li class="nav-item"><a href="#tabs-3" id="layoutAutorizados" class="nav-link" aria-current="page">Autorizados</a></li>
						    
			 			</ul>
						<div id="tabs-0" align=left>
							<% if (request.getParameter("tab").equals("0")) { %>
								<jsp:include page="LayoutApartadoPorGenerar.jsp" />
								<script type="text/javascript" charset="utf-8">
										var $tabs = $(".tabs").tabs();
										$( "#tabs" ).tabs({ active: 0 });
								</script>
							<% } %>
						</div>
						<div id="tabs-1" align="left" >
							<% if (request.getParameter("tab").equals("1")) { %>
								<jsp:include page="LayoutApartadoProcesoEnvio.jsp" />
								<script type="text/javascript" charset="utf-8">
										var $tabs = $(".tabs").tabs();
										$( "#tabs" ).tabs({ active: 1 });
								</script>
							<% } %>
						</div>
						<div id="tabs-2" align="left">
							<% if (request.getParameter("tab").equals("2")) { %>
								<jsp:include page="LayoutApartadoEnviados.jsp" />
								<script type="text/javascript" charset="utf-8">
										var $tabs = $(".tabs").tabs();
										$( "#tabs" ).tabs({ active: 2 });
								</script>
							<% } %>
						</div>
						<div id="tabs-3" align="left">
							<% if (request.getParameter("tab").equals("3")) { %>
								<jsp:include page="LayoutApartadoAutorizados.jsp" />
								<script type="text/javascript" charset="utf-8">
										var $tabs = $(".tabs").tabs();
										$( "#tabs" ).tabs({ active: 3 });
								</script>
							<% } %>
						</div>
					</div>
			 	</fieldset>
			 </div>
			  <form action="">
		     	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
		    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
		    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
		    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
		    	<input type="hidden" name="nPestana" id="nPestana" value="1" />
		     </form>
		</div>
	</body>
</html>