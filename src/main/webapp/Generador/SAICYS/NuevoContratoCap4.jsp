<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="java.text.SimpleDateFormat"%>
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
   	<meta charset="UTF-8">
    <title>'NuevoContratoCap4'</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	
	<script type="text/javascript">
		$(document).ready(function() {
			<%
			    String role="";
			    String roles="";
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role=(String)r.getKey();
					roles += r.getKey().toString()+",";
				}
				Map botones=nb.getBotones(roles,"ContratoCap4","NuevoContratoCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()){
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			initQuerys();
			PlurianualChecked();
			$("#btnGuardarContratoCap4").button().click(function(){
			 	 guardar();
			 });
		});//Fin del document ready
		function initQuerys(){
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			querySelectPost("tipoActividadEconomica", "actEconomContratoCap4", {async: false});
			
		}
		function PlurianualChecked(){
			if($("#isPlurianualCheck").is(':checked')){
				$("#isPlurianual").val(1);
			}else{
				$("#isPlurianual").val(0);
			}
		}
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		function guardar(){
			if(parseInt($("#actEconomContratoCap4").val(),10)==0){
				swal("Falta seleccionar el tipo de contrato","info",{ button: "Cerrar"});
				return;
			}
			if($("#cDescripcion").val()==""){
				swal("Falta agregar la descripción del contrato","info",{ button: "Cerrar"});
				return;
			}
			$.ajax({url: "../../servlet/ContratoCap4Servlet" , type:'post' , async: false
			,data:'operacion=0&cIdUnidadEjecutora='+$("#cIdUnidadEjecutora").val()+'&actEconomContratoCap4='+$("#actEconomContratoCap4").val()
			+'&isPlurianual='+$("#isPlurianual").val()+'&cDescripcion='+$("#cDescripcion").val()+'&cnumCompranet='+$("#cnumCompranet").val()
			, dataType: 'json', success: 
				function(j){
					var mensaje=j[0].MENSAJE;
					var resp=j[0].RESPUESTA;
					swal({
						title: "",
						text: mensaje,
						icon: "info",
						buttons: {
							confirm : "Cerrar"
							},
					}).then((continuar) => {
						if(resp){
							window.location = "ContratoCap4.jsp?tab=2";
						}
					});
				}
			});
		}
	</script>
  </head>
  
  <body>
  	<form id="formNuevoContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Crear Contratos Cap&iacute;tulo 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cIdUnidadEjecutora">Unidad Ejecutora: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" onchange="cambiaCentrocontableUsuario();">
							<option value="<%=usuario.getU_UR()%>" selected="selected">
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="actEconomContratoCap4">Tipo: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="actEconomContratoCap4" name="actEconomContratoCap4" >
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row" >
					<div class="input-group">
						<div class="col-2">		
							<label class="form-check-label" for="isPlurianualCheck">¿Va ser plurianual? </label>
						</div>
						<div class="col-auto">
			  				<input class="form-check-input" type="checkbox" id="isPlurianualCheck" name="isPlurianualCheck" onclick="PlurianualChecked();" >
			  			</div>
		  			</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cDescripcion">Descripci&oacute;n: </label>
						</div>
						<div class="col-6">
							
							<textarea class="form-control" id="cDescripcion" rows="3"  placeholder="Objeto del la contrataci&oacute;n" aria-describedby="basic-addon1" onkeypress="textCounter(this,500);"></textarea>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="cnumCompranet">No. Contrato CNET: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de contrato CNET" aria-label="Objeto del contrato" aria-describedby="basic-addon1"  name="cnumCompranet" id="cnumCompranet"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnGuardarContratoCap4" name="btnGuardarContratoCap4" 	value="Guardar"	 />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    	<input type="hidden"  id="isPlurianual" name="isPlurianual" value="0"/>
    	<input type="hidden" id="isAbierto" name="isAbierto" value="0" />
    	<input type="hidden" name="nIdEstado" id="nIdEstado" value="1"/>
    </form>
  </body>
</html>
