<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="utf-8"%>
<%@page import="java.util.*"%>
<%@page import="com.syc.gestion.core.*"%>
<%@page import="com.syc.gestion.servlet.*"%>
<%@page import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.ControlAccesoBusinessLogic"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    Calendar c1 = Calendar.getInstance(); // today
    String today = sdf.format(c1.getTime());

    ControlAccesoBusinessLogic caBL = new ControlAccesoBusinessLogic(GestionInterface.ATT_CONEXION);
    ArrayList unidades = caBL.getUnidadesResponsables();
    String cCentroContable = "";
    String cUR = "";
    String cUR2 = "";
    String cRamo = "";
    String login = "";

    String cAplicaDocto = "No";
    if (request.getParameter("aplicaDocto") != null
		    && request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
    }

    boolean bAplicadoCont = false;

    Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
    Usuario usuario = (Usuario) session
		    .getAttribute(GestionInterface.ATT_USER);

    CasoBusinessLogic cbl = new CasoBusinessLogic(
		    GestionInterface.ATT_CONEXION);

    String msj="";
	msj = (String)session.getAttribute("mensaje");
	
	if (msj != null)
		session.removeAttribute("mensaje");
	else 
		msj = "";
    Empleado e = new Empleado();
    EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
		    GestionInterface.ATT_CONEXION);
    e.setClaveUsuario(usuario.getLogin());
    e = ebl.getEmpleado(e);
    EmpleadoArea ea = new EmpleadoArea();
    ea.setId(e.getClaveArea());
    ea = ebl.getEmpleadoArea(ea);

    //Valida Centro de Costos
    if (usuario.getPropiedades() != null
		    && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE")
	.getValor();
    }

    cUR = usuario.getU_UR();
    cRamo = usuario.getU_Ramo();
    login = usuario.getLogin();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Control de Accesos</title>

<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>		
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>

<script type="text/javascript" charset="utf-8">
			$(document).ready(function(){
					$("#fFechaIni").datepicker({
						showOn:"button",
						dateFormat:"dd/mm/yy",
						buttonImage:"../Generador/images/calendar.gif",
						buttonImageOnly:true
					});
	
					$("#fFechaFin").datepicker({
						showOn:"button",
						dateFormat:"dd/mm/yy",
						buttonImage:"../Generador/images/calendar.gif",
						buttonImageOnly:true
					});
					
			$("#prender").button();
			$("#apagar").button();
			$("#Limpiar").button();
					
	//Crea el DataTable 
		$('#dt_userdesactivados').dataTable({
			        "bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "100%",
					"sScrollXInner": "110%",
					"bScrollCollapse": true,	
					"bServerSide": true,   
			        sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vimx_usuario_grupo_cerrado",
			        aoColumns   : [
						{ sName: "tramite" },
						{ sName: "Unidad_Responsable" },
						{ sName: "CentroContable"},
						{ sName: "usuarios_desactivados"}
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

	});
			
			
			
	function limpiarSesion()
		{
		window.location.href="controlAcceso.jsp?id=<%=request.getParameter("id")%>";
	}

	function cargaCC() {
	$("#usuarios tbody tr").remove(); //limpia la tabla
		var UR = $("#UR").val();
		if (UR != "-1") {
			querySelectPost("readCargaCatalogoCC", "cc", {
				async : false
			});
		}
	}

	function cargaUsuariosCC() {
		$("#usuarios tbody tr").remove(); //limpia la tabla
		$ .ajax({
					url : '../gstnmngr/ControlAcceso',
					dataType : 'json',
					data : {
						"listarUsuariosCC" : "listarUsuariosCC",
						"cc" : $("#cc").val(),
						"TC" : $("#TC").val()
					},
					async : false,
					success : function(json) {
						r = json.data_1;

						var trText = "";
						var tdText = "";
						
						var i = 0;
						var j = 0;
						while (i<r.length){
							while( j  < 7 ){
								if( r[i] )
									tdText = tdText +"<td><input type=\"checkbox\" id=\"" + r[i].data + "\" name=\"checks\" value=\"" + r[i].data + "\"  >" + r[i].data + " </td>";
								else
									tdText = tdText + "<td>&nbsp;</td>";
								j++;
								i++;
							}
							$('#usuarios > tbody:first').append("<tr>"+ tdText + "</tr>");
							j=0;
							tdText ="";
						}	
					
											
					},
					error : function(xhr, textStatus, errorThrown) {
						alert("Advertencia: " + xhr.responseText
								+ "\nEstatus: " + textStatus + "\n"
								+ errorThrown);
						r = true;
					}
				});

	}
</script>
</head>
<br/>
<body id="dt_example">
	<form id="tramite" name="tramite" method="POST" action="../gstnmngr/ControlAcceso">
		<div id="container" class="container" style="width: 90%">
			<div class="card-header"> <h3> Control de Acceso </h3> </div>
			<hr class="mt-3"/>
			
			<div id="mensaje" style="color:red;font-size: 14px;">
				<%=msj%>
			</div>
			
			<h5 align="right"> Activación/Desactivación de trámites </h5>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">										
					<label for="TC" class="form-label"> Tr&aacute;mite: </label>
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<select name="TC" id="TC" class="form-select form-select-sm">
						<option value="-1" selected="selected">Todos los
							trámites</option>
						<option value="-2">Todos los Pagos</option>
						<option value="-3">Recursos Materiales Apartado</option>
						<option value="-4">Recursos Materiales Precompromiso</option>
						<%
							Map m = cbl.getAllTipoCaso();
							for (Iterator iter = m.keySet().iterator(); iter.hasNext();) {
								String name = (String) iter.next();
								TipoCaso tc = (TipoCaso) m.get(name);
						%>
						<option value="<%=tc.getIdTC()%>"><%=name%></option>
						<%
							}
						%>
					</select>
				</div>				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">										
					<label for="UR" class="form-label"> Unidad Responsable: </label>
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<select name="UR" id="UR" onchange="cargaCC()" class="form-select form-select-sm">
						<option value="-1" selected="selected">Todas</option>
						<%
							for (int i = 0; i < unidades.size(); i++) {
								ArrayList<String> a = (ArrayList<String>) unidades.get(i);
						%>
						<option value="<%=a.get(0)%>"><%=a.get(1)%></option>
						<%
							}
						%>
					</select>
				</div>				
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">										
					<label for="cc" class="form-label"> Centro Contable: </label>
				</div>
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
					<select name="cc" id="cc" onchange="cargaUsuariosCC()" class="form-select form-select-sm">
						<option value="-1" selected="selected">Todos</option>
					</select>
				</div>				
			</div>
						
			<div class="row">
				<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1 justify-content-right">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1 justify-content-right">										
					<input type="submit" value="Activar" name="prender" id="prender" class="btn-secondary btn-sm"/>&nbsp;&nbsp;
					<input type="submit" value="Desactivar" name="apagar" id="apagar" class="btn-secondary btn-sm"/>&nbsp;&nbsp;
					<input type="button" id="Limpiar" name="Limpiar" value="Limpiar" onclick="limpiarSesion();" class="btn-secondary btn-sm"/>
				</div>			
			</div>		

			<div id="dv">
				<table id="dt_userdesactivados" class="table table-striped" >
					<thead>
						<tr>
							<th>Tr&aacute;mite</th>
							<th>Unidad Responsable</th>
							<th>Centro Contable</th>
							<th>Usuario desactivado</th>
						</tr>
					</thead>

				</table>
			</div>
		</div>
	</form>
</body>
</html>
