<%@page language="java" import="java.util.*"contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.core.AnteProyectoAut"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%

boolean cGrupoUSR=false;
ArrayList<ArrayList<String>> ues; //ntp
String DATE_FORMAT = "dd/MM/yyyy";
SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
Calendar c1 = Calendar.getInstance(); // today
String today = sdf.format(c1.getTime());

Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
if (c == null) {
	response.sendRedirect("../index.jsp");
	return;
}
Empleado e = new Empleado();
EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
e.setClaveUsuario(usuario.getLogin());
e = ebl.getEmpleado(e);
EmpleadoArea ea = new EmpleadoArea();
ea.setId(e.getClaveArea());
ea = ebl.getEmpleadoArea(ea);

CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
AdecuacionBusinessLogic abl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
AnteProyectoBusinessLogic apbl = new AnteProyectoBusinessLogic(GestionInterface.ATT_CONEXION);

ues = apbl.getUnidadesEjecutoras();

int id_oper = -1;
if (request.getParameter("id_oper") != null)
	id_oper = new Integer(request.getParameter("id_oper")).intValue();
else
	id_oper = c.getCasoOperacion(0).getIdOperacion();

String mensaje = "";
if (request.getParameter("msg") != null
		&& !"".equals(request.getParameter("msg"))) {
	mensaje = request.getParameter("msg");
	mensaje = mensaje.replace("[", "");
	mensaje = mensaje.replace("]", "");
	mensaje = mensaje.replace(",", "<br>");
}

String cCentroContable = "";
String UR = "";

if (usuario.getPropiedades() != null) {
    if(usuario.getPropiedades().containsKey("CCENTROCONTABLE"))
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
    if(usuario.getU_UR()!=null)
		UR = usuario.getU_UR();
}

session.setAttribute("UR",UR);
if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
	mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
}

cGrupoUSR=(usuario.getGrupos() != null && usuario.getGrupos().containsKey("AUTORIZADOR_ANTEPROYECTO")) ? true : false;


%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Creación de Ante Proyecto</title>
		<meta http-equiv="pragma" content="no-cache">
		<meta http-equiv="cache-control" content="no-cache">
		<meta http-equiv="expires" content="0">
		<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
		<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
		<meta http-equiv="description" content="This is my page">
		<link rel="shortcut icon" type="image/ico" href="imagenes/favicon.ico" />
		<style type="text/css" title="currentStyle"> 
			@import "../Generador/css/demo_page.css";
			@import "../Generador/css/demo_table_jui.css";
			@import "../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css";
		</style>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker-es.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
		<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" charset="utf-8">
			function onSubmit(id_oper){
				var p = window.parent;
				if (id_oper==1){
					p.gestion.setFolio($("#FOLIO").val());
					p.gestion.setOperador($("#OPERADOR").val());
					p.gestion.setFechaDocumento($("#FECHA_SOLICITUD").val());
					p.gestion.setEjercicioFiscal($("#EJERCICIO_FISCAL").val());
					p.gestion.setConceptoMov("Calendario Anteproyecto");
					p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
					parent.document.getElementById("pb_send").disabled=false;
					parent.document.getElementById("pb_save").disabled=true;
				}
				if (id_oper==2 || id_oper==4){
					if(document.datosCorrectos.autorizaRein[1].checked && $("#motivoRechazo").val()==""){
				  		alert("Motivo de rechazo es requerido");
				  		parent.document.getElementById("pb_send").disabled=true;
				  		return false;
			  		}else if(!(document.datosCorrectos.autorizaRein[0].checked || document.datosCorrectos.autorizaRein[1].checked)){
			  			alert("Favor de marcar si los datos son correctos o no");
			  			parent.document.getElementById("pb_save").disabled=false;
			  			return false;
			  		}else{
			  			parent.document.getElementById("pb_send").disabled=false;
			  			p.gestion.setMensaje($("#motivoRechazo").val());
			  		}
				}
				if (id_oper==3 || id_oper==5){
					parent.document.getElementById("pb_send").disabled=false;
				}
				return true;
			}
			
			function onPostSubmit(id_oper){
				return true;
			}
			
			function onPostDisplay(id_oper){}
			
			function onLoadPlantilla(id_oper){
				var p = window.parent;
				if(id_oper==1){
					if(<%=c.getIdGabinete()%>==-1)
		  				parent.document.getElementById("pb_save").click();//esto es para asegurar que se crea el expediente antes de subir el archivo
				}
					
				if(id_oper==2){
					
				}
				if(id_oper==3){
					if(p.gestion.getMensaje()!=null && p.gestion.getMensaje()!="")
						alert(p.gestion.getMensaje());
				}
				if(id_oper==4){
					if(p.gestion.getMensaje()!=null && p.gestion.getMensaje()!="")
						alert(p.gestion.getMensaje());
				}
				if(id_oper==5){
					if(p.gestion.getMensaje()!=null && p.gestion.getMensaje()!="")
						alert(p.gestion.getMensaje());
				}
			}
			
			function ResponsableSiguiente(id_oper){
				if(id_oper==1)
			  		 return "REVISOR_ANTEPROYECTO";
			  	if(id_oper==2){
			  		if(document.datosCorrectos.autorizaRein[1].checked)
			  			return "CAPTURISTA_ANTEPROYECTO";
			  		else
			  		 	return "AUTORIZADOR_ANTEPROYECTO";
			  	}
			  	if(id_oper==3){
					return "REVISOR_ANTEPROYECTO";
				}
			  	if(id_oper==4){
					if(document.datosCorrectos.autorizaRein[1].checked)
			  			return "REVISOR_ANTEPROYECTO";
			  		else
			  		 	return "CONSULTA_ANTEPROYECTO";
				}
			  	if(id_oper==5){
			  		if(document.datosCorrectos.autorizaRein[1].checked)
			  			return "CAPTURISTA_ANTEPROYECTO";
			  		else
			  		 	return "AUTORIZADOR_ANTEPROYECTO";
				}
		  	}
			
			function OperacionSiguiente(id_oper){
				if(id_oper==1)
			  	 	return "revisa_calendario";
			  	if(id_oper==2){
			  		if(document.datosCorrectos.autorizaRein[1].checked)
			  			return "rechazar_calendario";
			  		else
			  	 	 	return "autoriza_calendario";
				}
			  	if(id_oper==3){
					return "revisa_calendario";
				}
			  	if(id_oper==4){
			  		if(document.datosCorrectos.autorizaRein[1].checked)
			  			return "rechazaa_calendario";
			  		else
			  	 	 	return "consulta_calendario";
				}
			  	if(id_oper==5){
					if(document.datosCorrectos.autorizaRein[1].checked)
			  			return "rechazar_calendario";
			  		else
			  	 	 	return "autoriza_calendario";
				}
		  	}
			
			$(document).ready(function(){
				oTable = $('#tblCalendarioAnte').dataTable({
					"bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
        			"bAutoHeight": true,
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : false,
					"sPaginationType": "full_numbers",
					"sScrollX": "100%",
					"sScrollXInner": "300%",
					"bScrollCollapse": true,	
					"bServerSide": true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vtAnteProyectoAutCal&qw=cUnidadResponsable=+'<%= UR%>'",
					aoColumns: [
						{ sName: "nConsecutivo"},
						{ sName: "nFolioAnteProyecto" },
						{ sName: "nFolioAnteProyectoAut" },
						{ sName: "aEjercicioFiscal"},
						{ sName: "cClaveSiaff"},
						{ sName: "cClaveInterna"},
						{ sName: "cCentroContable"},
						{ sName: "mAnualAutorizado"},
						{ sName: "mEnero"},
						{ sName: "mFebrero"},
						{ sName: "mMarzo"},
						{ sName: "mAbril"},
						{ sName: "mMayo"},
						{ sName: "mJunio"},
						{ sName: "mJulio"},
						{ sName: "mAgosto"},
						{ sName: "mSeptiembre"},
						{ sName: "mOctubre"},
						{ sName: "mNoviembre"},
						{ sName: "mDiciembre"}
					],
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay datos en la tabla",
						sLoadingRecords: "Cargando...",
						sInfo: "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty: "Registro 0 al 0 de 0",
						sInfoFiltered: "(filtered from _MAX_ total entries)",
						sInfoPostFix: "",
						sInfoThousands: ",",
						sSearch: "Filtro:",
						oPaginate: {
							sFirst:    "Primero",
							sPrevious: "Ant.",
							sNext:     "Sigte.",
							sLast:     "&Uacute;ltimo"
						}
					}					
				});
			});  //fin del ready
		</script>
	</head>
	<body id="dt_example">
		<div id="container" class="container SyCData">
			<h1>Calendarización de Ante Proyecto</h1>
		
		<%if(id_oper==1 || id_oper==3){ %>	
			<form method="POST" id="generaExcel" action="../gstnmngr/CalendarioAP">
				<%if (cGrupoUSR){ %>
					Unidad Ejecutora:
						<select id="cUniEjecutora" class="paso01" name="cUniEjecutora" style="width: 40em;">
							<option value=""></option>
							<% if (ues.size() > 0) { %>
							 	<% int j = 0;
							 	 while (j<ues.size()) {
							 	    ArrayList<String> ue;
							 	 	ue=(ArrayList<String>)ues.get(j);							 	 	
							 	 %>
								<option value="<%=ue.get(0)%>"><%=ue.get(1) %></option>
								<% 		j++;
								}
							} %>
						</select>
				<%} %>
				<input type="hidden" value="1" name="accion"/>
				<input type="hidden" name="folioc" id="folioc" size="15" value="<%=c.getFolio() %>">
				<br/><input type="submit" value="Genera Excel"/>
			</form>
						
			<form id="importaExcel" name="importaExcel" action="../gstnmngr/CalendarioAP" method="POST">
				<input type="hidden" value="2" name="accion"/>
				<input type="hidden" name="CCENTROCONTABLE" id="CCENTROCONTABLE" value="<%=cCentroContable %>"/>
				<input type="submit" name="importaExcel" id="importaExcel" value="Importar Excel"/>
			</form>
			<%} %>
			
			<form id="datosReintegro" name="datosReintegro" action="">
				<input type="hidden" name="FOLIO" id="FOLIO" size="15" value="<%=c.getFolio() %>">
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo" : c.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
				<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=abl.obtenEjercicioFiscal()%>" />	
				<input type="hidden" name="FECHA_APLICACION_CONTABLE" id="FECHA_APLICACION_CONTABLE" value="<%=today%>" readonly="readonly" maxlength="10" size="10"/>
			</form>
			
			<%if(id_oper==2 || id_oper==4 || id_oper==5){ %>	
			<form id="datosCorrectos" name="datosCorrectos">
				Datos Correctos: &nbsp;&nbsp;&nbsp;
				Si <input type="radio" id="autorizaRein" name="autorizaRein"value="1" />&nbsp;
				No <input type="radio" id="autorizaRein" name="autorizaRein" value="0" /> &nbsp;&nbsp;&nbsp; 
				Motivo:&nbsp; <input type="text" id="motivoRechazo" name="motivoRechazo" size="60" maxlength="200" />
				<input type="hidden" id="motivoR" name="motivoR" />	
			</form>
			<%}%>	
			<%if(id_oper==2 || id_oper==3 || id_oper==4 || id_oper==5){ %>	
				<table border="0" class="display" id="tblCalendarioAnte" style="text-align: center;">
					<thead>
						<tr>
							<th>
								nConsecutivo
							</th>
							<th>
								Folio
							</th>
							<th>
								FolioAut
							</th>
							<th>
								Clave Siaff
							</th>
							<th>
								Clave Interna
							</th>
							<th>
								Ejercicio Fiscal
							</th>
							<th>
								Monto Anual
							</th>
							<th>
								Enero
							</th>
							<th>
								Febrero
							</th>
							<th>
								Marzo
							</th>
							<th>
								Abril
							</th>
							<th>
								Mayo
							</th>
							<th>
								Junio
							</th>
							<th>
								Julio
							</th>
							<th>
								Agosto
							</th>
							<th>
								Septiembre
							</th>
							<th>
								Octubre
							</th>
							<th>
								Noviembre
							</th>
							<th>
								Diciembre
							</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
							<td></td>
						</tr>
					</tbody>
				</table>
			<%}%>			
		</div>
	</body>
</html>
