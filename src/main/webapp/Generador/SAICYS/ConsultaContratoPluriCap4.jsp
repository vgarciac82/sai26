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
   <title>Consulta Contrato Pluri Cap4</title>
    <meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<script type="text/javascript">
		var oTableConsulta="";
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
				Map botones=nb.getBotones(roles,"ContratoPlurianualCap4","ConsultaContratoPluriCap4");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			tabb=1;	
			roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0){
				$("#isAdmin").val(0);
			}
			showAndHideTabs();
			agregaDatePickerFechas();
			initQuerys();
			initTabla();
			
			$('#tblConsulta').on('dblclick','tr', function() { 
				        
				$(this).addClass('row_selected');
				var anSelected = fnGetSelected( oTableConsulta );
				if (anSelected != "") {
					var aData = oTableConsulta.fnGetData(anSelected[0]);
					window.location = aData[0];
				}
				
			});
		});//Fin del document ready
		function initQuerys(){
			querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			querySelectPost("estadoContratoRead", "estatusContratoPluriCap4", {async: false});
			querySelectPost("tipoActividadEconomica", "actEconomContratoPluriCap4", {async: false});
		}
		function  initTabla(){
			var qw=" cIdContratoDefinitivo LIKE'%25" +$("#contratoPluriCap4").val()+ "%25' ";
			if ($("#cIdUnidadEjecutora").val() != "0"){
				qw+=" and cIdUnidadEjecutora='"+$("#cIdUnidadEjecutora").val()+"' ";
			}
			if ($("#estatusContratoPluriCap4").val() != "0"){
				qw+=" and nIdEstado='"+$("#estatusContratoPluriCap4").val()+"' ";
			}
			if ($("#actEconomContratoPluriCap4").val() != "0"){
				qw+=" and nIdTipoActividadEconomica='"+$("#actEconomContratoPluriCap4").val()+"' ";
			}
			var rangoFechas=" and fCreacion BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";	
			
			qw+=rangoFechas;
			oTableConsulta = $("#tblConsulta").dataTable({
				bScrollCollapse: true,
				bInfo: false,
				sScrollX: "100%",
				bAutoWith: true,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtrado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				},
				bServerSide: true,
				bProcessing: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mConsultaContratoPluriCap4&qw="+qw,
				sPaginationType: "full_numbers",
					aaSorting: [[ 0, "asc" ],[2, "asc"]] ,
					aoColumns: [
						{sName: "cVinculo",bVisible: false},
						{sName: "cIdUnidadEjecutora"},
						{sName: "cIdContratoDefinitivo"},
						{sName: "descripActEconom"},
						{sName: "cConceptoContrato"},
						{sName: "cIdUsuarioCreacion"},
						{sName: "cEstado"},
						{sName: "cIdRFC"},
						{sName: "cRazonSocial"}
						
					]
				});
		}
		function agregaDatePickerFechas(){
			$("#fInicial").datetimepicker({
				format: 'DD/MM/YYYY',
				altField: "#actualDate",
			 	currentText: "Now",
				changeYear: true
				
			});
			$("#fFinal").datetimepicker({
				format: 'DD/MM/YYYY',
				altField: "#actualDate",
			 	currentText: "Now",
				changeYear: true
				
			});
		}
	</script>
  </head>
  
  <body>
  	<form id="formConsultaCont">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Consulta Contratos Plurianuales Cap&iacute;tulo 4000</legend>
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
							<label for="contratoPluriCap4">Contrato Definitivo: </label>
						</div>
						<div class="col-6">
							<input type="text" class="form-control" placeholder="Número de Contrato SAI, Ejemplo PLU-CF-A04-1/2023" aria-label="Número de Contrato SAI, Ejemplo PLU-CF-A04-1/2023" aria-describedby="basic-addon1"  
							name="contratoPluriCap4" id="contratoPluriCap4"  />
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="estatusContratoPluriCap4">Estatus: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="estatusContratoPluriCap4" name="estatusContratoPluriCap4" >
							
							</select>
						</div>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-2">
							<label for="actEconomContratoPluriCap4">Tipo: </label>
						</div>
						<div class="col-6">
							<select class="custom-select" id="actEconomContratoPluriCap4" name="actEconomContratoPluriCap4" >
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
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="buscarContratoCap4" name="buscarContratoCap4" 	value="Buscar"	onclick="initTabla();" />
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
    	<input type="hidden" id="isAbierto" name="isAbierto" value="0" />
    	<input type="hidden" name="nIdEstado" id="nIdEstado" value="1"/>
    </form>
  </body>
</html>
