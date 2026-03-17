<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.core.TipoCaso"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdministracionMensajesBusinessLogic"%>

<%
	AdministracionMensajesBusinessLogic amBL = new AdministracionMensajesBusinessLogic(GestionInterface.ATT_CONEXION);
    ArrayList unidades = amBL.getUnidadesResponsables();

	String mensaje = request.getParameter("mensaje") != null ? request
	.getParameter("mensaje") : "";
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session
	.getAttribute(GestionInterface.ATT_USER);
	String prefixPath = getServletContext().getRealPath(
	"/WEB-INF/mail-bodies/");

	String msj = "";
	msj = (String) session.getAttribute("mensaje");

	if (msj != null)
		session.removeAttribute("mensaje");
	else
		msj = "";
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Administración de Mensajes</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>		
<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>

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
<script type="text/javascript">

	$(document).ready(function() {
		init();

	});

	function init() {

		$("#activar").button().click(function() {
			$("#accion").val("AGREGA_MENSAJE");
			Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se actualizara el mensaje",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $("#FormMensajes").submit();
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					  
				  }
				})
			
		});

		$("#desactivar").button().click(function() {
			$("#accion").val("DESACTIVA_MENSAJE");
			Swal.fire({
				  title: 'Desea continuar?',
				  text: "Se desactivará el mensaje",
				  icon: 'warning',
				  showCancelButton: true,
				  confirmButtonColor: '#288BA8',
				  cancelButtonColor: '#e6e6e6',
				  confirmButtonText: 'Aceptar',
				  cancelButtonText: 'Cancelar'
				}).then((result) => {
				  if (result.isConfirmed) {
					  $("#FormMensajes").submit();
				  } else if (result.dismiss === Swal.DismissReason.cancel) {
					  
				  }
				})
		});

		$("#limpiar").button().click(function() {
			window.location.href = "AdministracionMensajes.jsp";
		});

		$("#cancelar").button().click(function() {
		});

		
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();

		$("#dialog-mensaje").dialog({
			autoOpen : false, // se juega con el true o false para que se muestre o no
			height : 290,
			width : 550,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					$(this).dialog("close");
				}
			}
		});
		
	cargaInicial();

<%if (msj != null && !"".equals(msj)) {%>
	$("#dialog-mensaje").dialog("open");
<%}%>
	}

	function cargaCC() {
		var UR = $("#UR").val();
		if (UR != "-1") {
			querySelectPost("readCargaCatalogoCC", "cc", {
				async : false
			});
		}
	}

	function cargaInicial() {
		queryFormPost({
			queryName : "readPantallaMensajes",
			async : false,
			callback : function() {
				cargaCC();
				$("#cc").val($("#ccSel").val());
				
				if ($("#activaAlertatxt").val() == "S")
					$("#activaAlerta").attr("checked", "checked");
				
				if ($("#cc").val() != "-1") {
					cargaUsuariosCC();

					$(".ck").each(function() {
						$(this).removeAttr("checked");
					});

					selecChecks();
					activaContador();
				}
			}
		});
	}

	function selecChecks() { //selecciona los checks
		$.ajax({
			url : '../gstnmngr/AdministracionMensajes',
			dataType : 'json',
			data : {
				"seleccionarUsuarios" : "seleccionarUsuarios",
				"accion" : "SELECCIONA_USUARIOS"
			},
			async : false,
			success : function(json) {
				r = json.data_1;

				$(".ck").each(function() {
					$(this).removeAttr("checked");
				});

				for ( var c = 0; c < r.length; c++) {
					$("#" + r[c].data).attr('checked', true);

				}
			},
			error : function(xhr, textStatus, errorThrown) {
				alert("Advertencia: " + xhr.responseText + "\nEstatus: "
						+ textStatus + "\n" + errorThrown);
				r = true;
			}
		});
	}

	function cargaUsuariosCC() {
		$("#usuarios tbody tr").remove(); //limpia la tabla
		$.ajax({
					url : '../gstnmngr/AdministracionMensajes',
					dataType : 'json',
					data : {
						"listarUsuariosCC" : "listarUsuariosCC",
						"accion" : "LISTA_USUARIOS",
						"cc" : $("#cc").val(),
						"UR" : $("#UR").val(),
						"mensajeAdmin" : $("#mensajeAdmin").val()
					},
					async : false,
					success : function(json) {
						r = json.data_1;
						var trText = "";
						var tdText = "";
						var i = 0;
						var j = 0;
						tdText = tdText
								+ "<tr><input type=\"checkbox\" checked=\"checked\" id=\"todosUsuarios\" name=\"todosUsuarios\" value=\"todosUsuarios\" class=\"check_todos\" onclick=\"seleccionarTodo()\" />Selecciona Todos los Usuarios</tr>";
						while (i < r.length) {
							while (j < 7) {
								if (r[i])
									tdText = tdText
											+ "<td><input type=\"checkbox\" checked=\"checked\" id=\"" + r[i].data + "\" name=\"checks\" value=\"" + r[i].data + "\" class=\"ck\" onclick=\"activaContador()\" >"
											+ r[i].data + " </td>";
								else
									tdText = tdText + "<td>&nbsp;</td>";
								j++;
								i++;
							}
							$('#usuarios > tbody:first').append(
									"<tr>" + tdText + "</tr>");
							j = 0;
							tdText = "";
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

	function seleccionarTodo() {
		var activo = $("#todosUsuarios").is(":checked");

		$(".ck").each(function() {
			if ($(this).is(":checked") & !activo)
				$(this).removeAttr("checked");
			else if (!$(this).is(":checked") & activo)
				$(this).attr("checked", "checked");
		});
		
		
	}
	
	function activaContador(){
		var nUsuarios = $(".ck").length;
		var nActivados = $("input.ck[checked=true]").length;
		
		if( nUsuarios == nActivados)
			$("#todosUsuarios").attr("checked", "checked");
		else
			$("#todosUsuarios").removeAttr("checked");
		
	}
</script>

</head>
<br/>
<body id="dt_example">
	<form action="../gstnmngr/AdministracionMensajes" id="FormMensajes"
		name="FormMensajes" method="post">
		<input type="hidden" value="AGREGA_MENSAJE" name="accion" id="accion" />
		<input type="hidden" value="" name="ccSel" id="ccSel" />
		<input type="hidden" value="" name="activaAlertatxt" id="activaAlertatxt" />
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3> Administración de Mensajes </h3> </div>
			<hr class="mt-3"/>
			
			<h5> Mensajes </h5>
			<hr class="mt-3"/>
			
			<div class="row">												
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">										
					<div class="form-check">
						<input type="checkbox" name="activaAlerta" id="activaAlerta" class="form-check-input" value = "activaAlerta"/>								
						<label for="activaAlerta" class="form-check-label">Activar Alerta</label>
					</div>						
				</div>			
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																						
					<label for="mensajeAdmin"  class="form-label">Mensaje:</label>											
				</div>			
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex p-1">
					<textarea name="mensajeAdmin" cols=60 rows=3 id="mensajeAdmin" class="form-control form-control-sm"></textarea>
				</div>				
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																						
					<label for="UR"  class="form-label">Unidad Responsable:</label>											
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
					<select name="cc" id="cc" nge="cargaUsuariosCC()" class="form-select form-select-sm">
						<option value="-1" selected="selected">Todos</option>
					</select>
				</div>				
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-9 col-md-9 col-sm-12 d-flex p-1 justify-content-right">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1 justify-content-right">										
					<input type="button" value="Activar" name="activar" id="activar" class="btn-secondary btn-sm"/>&nbsp;&nbsp;
						<input type="button" value="Desactivar" name="desactivar" id="desactivar" class="btn-secondary btn-sm"/>&nbsp;&nbsp;
						<input type="button" id="limpiar" name="limpiar" value="Limpiar" class="btn-secondary btn-sm" />
				</div>			
			</div>	
						
		</div>
	</form>
	
	<div id="dialog-mensaje" title="Mensaje de Sistema">
		<div class="row">												
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex p-1">	
				<textarea cols="60" rows="10" id="mensaje" class="form-control form-control-sm"><%=msj%></textarea>
			</div>
		</div>
	</div>

</body>
</html>