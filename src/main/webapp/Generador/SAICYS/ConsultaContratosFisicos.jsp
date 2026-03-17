<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@page import="com.syc.gestion.core.NegativaPestana"%>
<%@page import="java.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
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
    
    <title>My JSP 'ConsultaContratosFisicos.jsp' starting page</title>
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

	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/moment.js/2.22.2/moment.min.js"></script>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/js/tempusdominus-bootstrap-4.min.js"></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/tempusdominus-bootstrap-4/5.0.1/css/tempusdominus-bootstrap-4.min.css" />
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.3.1/css/bootstrap.min.css">
	<link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css" rel="stylesheet"/>
	<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.blockUI/2.70/jquery.blockUI.js"></script>
	
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../js/sweetalert/sw/sweetalert.min.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/funciones.js"></script>
	<link href="../../css/reportesGRM.css" rel="stylesheet" type="text/css" />
	
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
				Map botones=nb.getBotones(role,"RecepcionMaterial","ConsultaRecepcion");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}

				%>
	  		roles="<%=roles%>";
			if (roles.indexOf("ADMIN_RECMAT") >= 0 || roles.indexOf("ANALISTA") >= 0 ||roles.indexOf("JEFES") >= 0){
				$("#isAdmin").val(0);
				querySelectPost("UnidadBusca2", "cIdUnidadEjecutora", {async: false });
			}else{
				querySelectPost("tCatalogoUnidadEjecutoraReadVistas", "cIdUnidadEjecutora", {async: false});
			}
			agregaDatePickerFechas();
			initTabla();
			
			
		});//Fin del document ready
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
		function  initTabla(){
			var qw=" fFormalizacion BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";
			if($("#cIdUnidadEjecutora").val()!='0'){
				qw=qw+" and cIdUnidadEjecutora='"+$("#cIdUnidadEjecutora").val()+"'";
			}
			oTableConsulta = $("#tblConsulta").dataTable({
				sScrollX: "100%",
				bScrollCollapse: true,
				bDestroy: true,
				bAutoWidth: true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay Contratos",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mConsultaContratoFisico&qw="+qw,
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "lArchivoContCargado",bVisible: false},
					{sName: "cIdUnidadEjecutora"},
					{sName: "cIdContratoDefinitivo"},
					{sName: "cIdRFC"},
					{sName: "cNoContratoCNET"},
					{sName: "fFechaCarga"},
					{sName: "cUsuarioCargaDoc"},
					{sName: "estatusDoc"},
					{sName: "descagarDoc"}
				]
			});
		
		}
		function openXLSX(ext){
			var ext="xlsx";		
			var where=" and fFormalizacion BETWEEN CONVERT(date,'"+$("#fInicio").val()+"') and CONVERT(date,'"+$("#fFin").val()+"') ";
			if($("#cIdUnidadEjecutora").val()!='0'){
				where=where+" and cont.cIdUnidadEjecutora='"+$("#cIdUnidadEjecutora").val()+"'";
			}
			var myWindow=window.open("../../servlet/ReportesGRM?"
				+"nTipoReporte="+$("#nTipoReporte").val()
				+"&operacion="+$("#operacion").val()
				+"&fechaInicio="+$("#fInicio").val()
				+"&fechaFin="+$("#fFin").val()
				+"&reporteNombre="+$("#cPlantilla").val()+"."+ext
				+"&formato="+ext
				+"&where=" + where, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
		}
		function downloadFile(cFolio,contratoSAI,contratoCNET){//,contratoSAI,contratoCNET
			//alert(idCaso+"\n"+contratoSAI+"\n"+contratoCNET)
			var object= {
				operacion:4,
				nTypeFile:1,
				nIdCaso:0,
				cFolio:cFolio,
				cContratoDefinitivo:contratoSAI,
				cContratoCNET:contratoCNET
			}
			var myWindow=window.open("../../servlet/ReportesGRM?"
				+"&operacion=4"
				+"&nTypeFile=1"
				+"&cFolio="+cFolio
				+"&cContratoDefinitivo="+contratoSAI
				+"&cContratoCNET="+contratoCNET
				, 'Procesando', 'status=1, width=400px, height=200px, left=150px');
			
		}
	</script>
  </head>
  
  <body id="dt_example">
  	<form>
		<div class="container-fluid">
			<div class="col-md-12 col-lg-12 col-sm-12">
	 			<fieldset class="form-group border p-3">
	 				<legend class="w-auto px-2"> Consulta Contratos F&iacute;sicos</legend>
	 				<div class="form-group">
	 					<div class="form-group row">
	 						<div class="form-group col-md-4">
		 						<label for="cIdUnidadEjecutora">Unidad Ejecutora</label>
								<select class="custom-select" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" > 
									<option value="<%=usuario.getU_UR()%>" selected="selected"> </option>
								</select>
							</div> 
	 					</div>
	 				</div>
	 				<div class="form-group row">
						<div class="form-group col-md-2">
							<label for="fInicial">Fecha Inicio</label>
							<div class="input-group date" id="fInicial" data-target-input="nearest">
					          <input type="text" class="form-control datetimepicker-input" data-target="#fInicial" title="Fecha Inicial" id="fInicio" name="fInicio" value="<%=todayAnt %>"/>
					          <div class="input-group-append" data-target="#fInicial" data-toggle="datetimepicker" title="Fecha Inicial">
					            <div class="input-group-text"><i class="fa fa-calendar"></i></div>
					          </div>
					        </div>
				        </div>
				    </div>
					<div class="form-group row">
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
					<div class="form-group row">
					    <div class="col">
					      <input type="button" class="btnInterfaceBG ui-button ui-widget ui-state-default ui-corner-all float-start" 	id="btnBuscarConsultaDocContratoFisico" name="btnBuscarConsultaDocContratoFisico" 	value="Buscar"	onclick="initTabla();" />
					    </div>
					    <div class="col">
					      <input type="button" class="btnInterfaceXLS ui-button ui-widget ui-state-default ui-corner-all float-right" 	id="cmdxlsReporteProg" name="cmdxlsReporteProg" 	value="Excel"	onclick="openXLSX('xlsx');" />
						</div>
					</div>
					
        
					<div class="form-group row" >
						<div class="col">
							<table id="tblConsulta" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
								<thead >
									<tr>
										<th style="display: none;"></th>
										<th align="center">Unidad<br />Ejecutora</th>
										<th align="center">Pedido/Contrato</th>
										<th align="center">RFC</th>
										<th align="center">Contrato CNET</th>
										<th align="center">Fecha de <br/>Carga</th>
										<th align="center">Usuario<br/> Carga Documento</th>
										<th align="center">Estatus<br/> Documento</th>
										<th align="center">Descarga<br/>de Documentos</th>
									</tr>										
								</thead>
							</table>
							
						</div>
					</div>
	 			</fieldset>
	 		</div>
	 	</div>
		<input type="hidden" name="isAdmin" id="isAdmin" value="1" />
    	<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%= usuario.getU_UR() %>" />
    	<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%= usuario.getLogin() %>" />
    	<input type="hidden" name="modulo" id="modulo" value="MATERIALES" />
    	<input type="hidden" name="operacion" id="operacion" value="3" />
    	<input type="hidden" name="nTipoReporte" id="nTipoReporte" value="1" />
    	<input type="hidden" name="cPlantilla" id="cPlantilla" value="PlantillaContratosFisicos" />
	 </form>
  </body>
</html>
