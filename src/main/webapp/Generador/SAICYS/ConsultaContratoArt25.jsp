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
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today= sdf.format(c1.getTime());
	c1.add(Calendar.MONTH, -1);
	String todayAnt= sdf.format(c1.getTime());
 %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Consulta contratos Art 25</title>
	<script type="text/javascript" charset="utf-8">
		var roles;
		var oTableConsultaContratos;
		tabb=1;
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
				Map botones=nb.getBotones(roles,"ContratosArt25","ConsultaContratoArt25");
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
			$("#tipoOperacion").val(1);
			pestanaConsulta();
			pestanaConsultaDatePickerFechas();
			$('#tblConsulta').on('dblclick', 'tr',function(){
				if ($(this).hasClass('row_selected'))             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
			  	var anSelected=fnGetSelected(oTableConsultaContratos);
			  	var aData=oTableConsultaContratos.fnGetData(anSelected[0]);
			  	if(aData!= ""){
			  		window.location = 'ContratosArt25.jsp?tab=2&cIdContratoDefinitivo='+aData[2];
		  		}
				
			});
		});//fin del document ready
		
	</script>
</head>
<body>
	<form id="formConsultaCont">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Consulta Contratos Art. 25</legend>
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
							<label for="contratoArt25">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo CR-A04-1/2023" aria-label="Número de Contrato SAI, Ejemplo CR-A04-1/2023" aria-describedby="basic-addon1"  
							name="contratoArt25" id="contratoArt25"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="estatusContratoArt25">Estatus: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="estatusContratoArt25" name="estatusContratoArt25" >
							
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="catTipoContratoArt25">Tipo: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="catTipoContratoArt25" name="catTipoContratoArt25" >
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group row">
				<div class="form-group col-md-2">
					<label for="fInicial">Fecha Inicio</label>
					<div class="input-group date" id="fInicial" data-target-input="nearest">
			          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha Inicial" id="fInicio" name="fInicio" value="<%=todayAnt %>" class="desahabilitado"/>
			          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha Inicial">
			            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
			          </div>
			        </div>
		        </div>
		        <div class="form-group col-md-2">
					<label for="fFinal">Fecha Fin</label>
					<div class="input-group date col-xs-2" id="fFinal" data-target-input="nearest" >
						<input type="text" class="form-control datetimepicker-input" data-target="#fFinal" id="fFin" name="fFin" title="Fecha Final" value="<%=today %>"  />
						<div class="input-group-append" data-target="#fFinal" data-toggle="datetimepicker" title="Fecha Final">
						  <div class="input-group-text"><i class="fa fa-calendar"></i></div>
						</div>
					</div>
				</div>
		    </div>
		    <div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="buscarContratoArt25" name="buscarContratoArt25" 	value="Buscar"	onclick="searchContracts();" />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="col">
						<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">url</th>
									<th >Unidad Ejecutora</th>
									<th >Contrato Definitivo</th>
									<th >Tipo Contrato</th>
									<th >Descripci&oacute;n</th>
									<th >Usuario Creador</th>
									<th >Estatus</th>
									<th >RFC</th>
									<th >Raz&oacute;n social</th>
									
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
		</fieldset>
    	<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuario.getLogin()%>"/>
    	
    </form>
	
</body>
</html>