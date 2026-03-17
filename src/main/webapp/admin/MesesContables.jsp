<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%

	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
	String usrCContable = "";
	String role ="";
	String propiedad = "";
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	if (usuario.getPropiedad("CCENTROCONTABLE") != null) {
		usrCContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		role = (usuario.getRole("ADMIN_CONTABILIDAD") != null ) ? usuario.getRole("ADMIN_CONTABILIDAD").getNombre() : "";
		propiedad = (usuario.getPropiedad("ROL_CONTABLE") != null) ? usuario.getPropiedad("ROL_CONTABLE").getValor() : "";
		
	}
%>
<html>
	<head>

		<title>Meses Contables</title>

		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
						
		<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>		
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
		
		<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>		
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap-datetimepicker.min.js"></script>						
		
		<script type="text/javascript" charset="utf-8">
		
		
			var dTable;

			var action; //0 cerrar 1 abrir
			var idChkBx;
			var trgtCContable;
			var trgtMes;
			var trgtAFiscal;

			var uCContable = '<%=usrCContable%>';
			var usrName = '<%=usuario.getNombre()%>';
			var usrLogin = '<%=usuario.getLogin()%>';
			var usrOficinaCentral = '<%="10".equals(usrCContable)%>';
			var usrAdmin = ('<%=role%>' == "ADMIN_CONTABILIDAD" && '<%=propiedad%>' == "ADMIN_CONTABILIDAD" ) ? "conPrivilegios" : "sinPrivilegios" ;
	var nMes;
	var mesActual;
	var maxMesAbierto;
	var masivo = "N";
	var whereStmnt = "";
	var szTabla = "M_CAT_MESES_CONTABLES";

	var centrosContables = new Array();
	var relIndCC = new Array();
	var idx = 0;
	var monthNames = [ "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
			"Julio", "Agosto", "Septiembre", "Octubre", "Noviembre",
			"Diciembre", "Trece" ];
	var monthId = new Array();
	monthId["Enero"] = 1;
	monthId["Febrero"] = 2;
	monthId["Marzo"] = 3;
	monthId["Abril"] = 4;
	monthId["Mayo"] = 5;
	monthId["Junio"] = 6;
	monthId["Julio"] = 7;
	monthId["Agosto"] = 8;
	monthId["Septiembre"] = 9;
	monthId["Octubre"] = 10;
	monthId["Noviembre"] = 11;
	monthId["Diciembre"] = 12;

	function createDataTable() {
		dTable = $("#tblMesesContables").dataTable({
						   aaData : centrosContables,
						   "bLengthChange" : true,
		   		            "bFilter" : true,
		   		            "bSort" : true,
		   		            "bInfo" : true,
		   		            "bPaginate" : true,
		   		            "bAutoWidth" : false,
		   		            "bScrollCollapse" : true,   		            
		   		            "sPaginationType" : "full_numbers",
		   		            "bJQueryUI" : true,
		   		            "bRetrive" : true,
		   		            "bDestroy" : true,
		   		            "bServerSide": true,                   
		   					"iDisplayLength": 25,	 				       			
							oLanguage : {
								sProcessing : "Procesando...",
								sLengthMenu : "Mostrar _MENU_ registros",
								sZeroRecords : "No hay registros a mostrar",
								sEmptyTable : "No hay datos en la tabla",
								sInfo : "Registros _START_ al _END_ de _TOTAL_",
								sLoadingRecords : "Cargando...",
								sInfoFiltered : "(filtado de _MAX_ registros)",
								sInfoPostFix : "",
								sInfoThousands : ",",
								sSearch : "Buscar:"
								
							},
							bServerSide : false,
							bProcessing : true,							
							bJQueryUI : true,							
							aaSorting : [ [ 0, "asc" ] ],
							fnPreDrawCallback : function() {
								$
								.blockUI({
									theme : true,
									title : "Cargando Informacion de la Tabla...",
									message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Por favor espere...</p>"
								});
							},
							fnDrawCallback : function() {
								$.unblockUI();
							}

						});
		
		$("#tblMesesContables tbody").unbind('dblclick'); 
		$("#tblMesesContables tbody").dblclick(function(event) {
			var row = dTable.fnGetPosition(event.target.parentNode);
			var col = event.target.cellIndex;
			var data = dTable.fnGetData(row);
			var ccntbl = data[0];

			if (data[col] == '') {
				updateMonth(null, ccntbl, (col - 1), null);
			}

			idChkBx = '';
		});
	}

	function addMonth(ccont) {
		centrosContables[idx] = new Array(14);
		relIndCC[String(ccont)] = idx;

		for ( var i = 0; i < 14; i++) {
			centrosContables[idx][i] = '';
		}

		idx++;
	}

	function reLoadTableData() {
		
		var checkDisabled = "0";
		var tDisabled = "f";
		var ccc = "";
		
		$
				.getJSON(
						"../catalogos/SelectJson.jsp",
						{
							Tabla : szTabla,
							Param : whereStmnt,
							MaxReg : "",
							ajax : false
						},
						function(j) {
							for ( var cnt in j) {
								
								var obj = j[cnt];
								var ur = String(obj.Col8);
								var obj = j[cnt];
								var cc = String(obj.Col2);
								if(cnt == 0){ ccc = ur; }
								var ccont = obj.Col3;
								var nmes = parseInt(obj.Col0, 10);
								var abierto = obj.Col7;								
								if(ccc != ur){ checkDisabled = "0"; tDisabled = "f"; }
								if(abierto == "S"){ checkDisabled = "1";  }
								ccc = ur;
								var aFiscal = obj.Col6;
								var ccIndex;
								if (relIndCC[ur] == undefined
										|| relIndCC[ur] == null) {
									addMonth(ur);
									ccIndex = relIndCC[ur];
									centrosContables[ccIndex][0] = cc;
									centrosContables[ccIndex][1] = ur;
									centrosContables[ccIndex][2] = ccont;
								} else {
									ccIndex = relIndCC[ur];
								}

								centrosContables[ccIndex][nmes + 2] = '<input type="checkbox" id="'
										+ ('C' + cc + 'UR' + ur + 'M' + nmes + 'A' + aFiscal)
										+ '" name="'
										+ ('C' + cc + 'UR' + ur + 'M' + nmes + 'A' + aFiscal)
										+ '" onclick="updateMonth(this,\''
										+ cc
										+ '\','
										+ nmes
										+ ','
										+ aFiscal
										+ ',\''
										+ ur
										+ '\')"'
										+ ("S" == abierto ? "checked" : "" )
										+ ("0" == checkDisabled && "conPrivilegios" != usrAdmin ? " disabled " : "  ")
										+ ("t" == tDisabled && "conPrivilegios" != usrAdmin ? " disabled " : "  ")										
										+ '/>';
										if(abierto == "S"){ tDisabled = "f"; }
							}
							//dTable.fnSettings().aoData = centrosContables;
							//dTable.fnDraw();
							dTable.fnClearTable();
							//dTable.fnUpdate(centrosContables);

							dTable.fnAddData(centrosContables);

							$.unblockUI();
						});
	}
	function loadTableData() {
		
		var checkDisabled = "0";
		var tDisabled = "f";
		var ccc = "";
		
		$
			.getJSON(
					"../catalogos/SelectJson.jsp",
					{
						Tabla : szTabla,
						Param : whereStmnt,
						MaxReg : "",
						ajax : false
					},
					function(j) {
						for ( var cnt in j) {
														
							var obj = j[cnt];
							var ur = String(obj.Col8);
							var cc = String(obj.Col2);
							if(cnt == 0){ ccc = ur; }
							var ccont = obj.Col3;
							var nmes = parseInt(obj.Col0, 10);
							var abierto = obj.Col7;
							//if(abierto == "S" && cc == ccc ){checkDisabled = "1";  }else{ checkDisabled = "0"; }
							if(ccc != ur){ checkDisabled = "0"; tDisabled = "f"; }
							if(abierto == "S"){ checkDisabled = "1";  }
							ccc = ur;
							var aFiscal = obj.Col6;
							var ccIndex;
							if (relIndCC[ur] == undefined
									|| relIndCC[ur] == null) {
								addMonth(ur);
								ccIndex = relIndCC[ur];
								centrosContables[ccIndex][0] = cc;
								centrosContables[ccIndex][1] = ur;
								centrosContables[ccIndex][2] = ccont;
							} else {
								ccIndex = relIndCC[ur];
							}

							centrosContables[ccIndex][nmes + 2] = '<input type="checkbox" id="'
									+ ('C' + cc + 'UR' + ur + 'M' + nmes + 'A' + aFiscal)
									+ '" name="'
									+ ('C' + cc + 'UR' + ur + 'M' + nmes + 'A' + aFiscal)
									+ '" onclick="updateMonth(this,\''
									+ cc
									+ '\','
									+ nmes
									+ ','
									+ aFiscal
									+ ',\''
									+ ur
									+ '\')"'
									+ ("S" == abierto ? "checked" : "" )									
									+ ("0" == checkDisabled && "conPrivilegios" != usrAdmin ? " disabled " : "  ")
									+ ("t" == tDisabled && "conPrivilegios" != usrAdmin ? " disabled " : "  ")									
									+ '/>';
							if(abierto == "S"){ tDisabled = "f"; }
						}
						createDataTable();
					});
	}

	$(document)
			.ready(
					function() {
						if (usrOficinaCentral == 'false') {
							whereStmnt = " cCentroContable='" + uCContable
									+ "' ";
							$("#subQ").show();
						}

						$
								.blockUI({
									theme : true,
									title : "Cargando Informacion de la Tabla...",
									message : "<p><img src='../Generador/imagenes/wait24trans.gif' />&nbsp;&nbsp;Iniciando, por favor espere...</p>"
								});
						queryFormPost("EjercicioFiscalActvRead", {async : false});		
						loadTableData();
						setDefaults();
						$("#aplicarTodo").button();

					});

	function setDefaults() {
		/*
		 * Establece algunos valores por defecto en la pantalla
		 */
		var currDate = new Date();
		var dt = currDate.getDate()
				+ "/"
				+ ((currDate.getMonth() < 9 ? "0" : "") + (currDate.getMonth() + 1))
				+ "/" + currDate.getFullYear();
		$("#fcierre").val(dt);
		mesActual = currDate.getMonth() + 1;
		$("#usrLogin").val(usrLogin);
		$("#u_nombre").val(usrName);
		$("#usuarioCerro").val(usrLogin);

		$('#yes').button().click(function() {
			var todos = $("#aplicarTodo").attr("checked");
			//var todos = $("#Masivo").val();

			if (action == 0)
				closeMonth(todos);
			else if (action == 1)
				openMonth(todos);			
			else if (action == 2) {
				updateMonth(todos);
			}			
		});

		$('#no').button().click(function() {
			if (idChkBx != '') {
				var actv = parseInt(action, 10) == 0;
				$("#" + idChkBx).attr("checked", actv);
			}
			$.unblockUI({
				fadeOut : 200
			});
		});
		
		$('#acpt').button().click(function() {
			$("#title").text("Espere ...");
			$("#acpt").hide();
			$("#resultMsg").text("Por favor espere mientras se actualiza la pagina.");
			$.unblockUI();
			updateGUI({fadeOut : 200});
		});

	}

	function updateGUI() {
		reLoadTableData();
	}
	
	function CerrarMasivo(cc){				
		var cCentroContable = cc;
		$("#cCentroContable").val(cc);
		var cEjercicio = $("#cEjercicio").val();
		var nMes = "";
		
		queryFormPost("dMes", {	async : false});
		nMes = $("#dMes").val();
		
		updateMonth(true, cCentroContable, nMes, cEjercicio, null);
	}

	function updateMonth(checkBx, centroContable, mes, aFiscal, unidadResponsable) {

		var activo;
		$("#yes").show();
		$("#no").show();
		$("#subQ").show();

		$("#resultMsg").hide();
		$("#acpt").hide();	
		if (checkBx != null) {
			idChkBx = checkBx.id;
			activo = checkBx.checked;
		}

		trgtCContable = centroContable;
		trgtMes = mes;
		trgtAFiscal = aFiscal;

		$("#cCentroContable").val(centroContable);
		$("#nMes").val(mes);
		$("#nMesAnterior").val(mes);
		$("#cUnidadResponsable").val(unidadResponsable);

		if (activo || checkBx == null) {
			if (checkBx == null)
				action = 2;
			else
				action = 1;

			$("#titulo").text("¿Desea abrir el mes contable "	+ monthNames[mes - 1] + " de la Unidad Responsable " + unidadResponsable + "?");
		} else {
			var maxM;
			action = 0;
			queryFormPost("dMes", {async : false});
			maxM = $("#dMes").val();
			
			if(unidadResponsable == null)
				$("#titulo").text("¿Desea cerrar el mes contable " + monthNames[mes - 1] + " a todas las Unidades?");
			else
				$("#titulo").text("¿Desea cerrar el mes contable " + monthNames[mes - 1] + " de la Unidad Responsable " + unidadResponsable	+ "?");
		}
						
		$.blockUI({
			title : "Confirme la operaci&oacute;n",
			message : $('#question')
		});										
				
	}

	function openMonth(all) {
		var nMes = $("#nMes").val();
		var ejercicioFisca = $("#cEjercicio").val();
		var cCentroContable = (all ? '' : $("#cCentroContable").val());
		var cUnidadResponsable = (all ? '' : $("#cUnidadResponsable").val());

		$.ajax({
			url : '../contabilidad/CierreDeMes',
			dataType : 'json',
			data : {
				"action" : "ABRE_MES_CONTABLE",
				"nMes" : nMes,
				"aEjercicioFiscal" : ejercicioFisca,
				"cCentroContable" : cCentroContable,
				"cUnidadResponsable" : cUnidadResponsable
			},
			async : false,
			success : function(RS) {
				var arrRes = new Array();
				var index, data, col;
				if (RS.success == "true") {
					index = 1;
					while (true) {
						data = eval("RS.data_" + index++);
						if (!data)
							break;
						for ( var i = 0; i < data.length; i++) {
							col = data[i];
							for ( var name in col) {
								arrRes.push(eval("col." + name));
							}
						}
					}
				}
				
				if(arrRes.length > 0 ){
					$("#subQ").hide();
					$("#resultMsg").show();
					$("#acpt").show();
					$("#yes").hide();
					$("#no").hide();
					
					var msgRes = arrRes.join("<br/>");
					$("#titulo").text("¡Atencion!");
					$("#resultMsg").html(msgRes);
					
				}
				 
				//$.unblockUI();
				//updateGUI();
			},
			error : function(xhr, textStatus, errorThrown) {
				Swal.fire({ icon: "error",
							text: "Advertencia: " + xhr.responseText + "\nEstatus: "
									+ textStatus + "\n" + errorThrown});				
				$.unblockUI();
				updateGUI();
			}
		});
	}

	function closeMonth(all) {
		var nMes = $("#nMes").val();
		var mes = $("#mes").val();
		var ejercicioFisca = $("#cEjercicio").val();
		var cCentroContable = (all ? '' : $("#cCentroContable").val());
		var cUnidadResponsable = $("#cUnidadResponsable").val()

		$.ajax({
			url : '../contabilidad/CierreDeMes',
			dataType : 'json',
			data : {
				"action" : "CIERRA_MES_CONTABLE",
				"nMes" : nMes,
				"aEjercicioFiscal" : ejercicioFisca,
				"cCentroContable" : cCentroContable,
				"cUnidadResponsable" : cUnidadResponsable 
			},
			async : false,
			success : function(RS) {
				var arrRes = new Array();
				var index, data, col;
				if (RS.success == "true") {
					index = 1;
					while (true) {
						data = eval("RS.data_" + index++);
						if (!data)
							break;
						for ( var i = 0; i < data.length; i++) {
							col = data[i];
							for ( var name in col) {
								arrRes.push(eval("col." + name));
							}
						}
					}
				}
				
				if(arrRes.length > 0 ){
					$("#subQ").hide();
					$("#resultMsg").show();
					$("#acpt").show();
					$("#yes").hide();
					$("#no").hide();
					
					var msgRes = arrRes.join("<br/>");
					$("#titulo").text("¡Atencion!");
					$("#resultMsg").html(msgRes);
					
				}
				 
				//$.unblockUI();
				//updateGUI();
			},
			error : function(xhr, textStatus, errorThrown) {
				Swal.fire({ icon: "error",
							text: "Advertencia: " + xhr.responseText + "\nEstatus: "
									+ textStatus + "\n" + errorThrown});					
				$.unblockUI();
				updateGUI();
			}
		});
	}

	function siguienteMesContable(mesContableActual) {
		return (mesContableActual % 12) + 1;
	}

	function insertMont(all) {
		$("#mesAbierto").val("S");
		if (all) {
			queryFormPost(
					"InsertaBatchMesContables,ActualizaEstadoMesesContables", {
						async : false
					});
		} else
			queryFormPost("InsertaMesContable", {
				async : false
			});
	}
        
</script>
	</head>

	<body id="dt_example">
	<br/>
		<form>
			<input type="hidden" id="cEjercicio" name="cEjercicio" value="" />
			<input type="hidden" id="usrLogin" name="usrLogin" value="" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="">
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="">			
			<input type="hidden" id="nMes" name="nMes" value="" />
			<input type="hidden" id="mesAbierto" name="mesAbierto" value="" />
			<input type="hidden" id="nMesAnterior" name="nMesAnterior" value="" />
			<input type="hidden" id="existeMes" name="existeMes" value="false" />
			<input type="hidden" id="dMes" name="dMes" value="" />
			
			<div id="container" class="container" style="width: 90%">
				<div class="card-header"> <h3> Meses Contables </h3> </div>
				<hr class="mt-3"/>
				
				<div class="row">					
					<div class="col-12 col-lg-6 col-md-6 col-sm-12">									
						<label for="u_nombre" class="form-label">Usuario</label>
						<div class="input-group">												
							<input type="text" id="u_nombre" name="u_nombre" class="form-control form-control-sm" style="width: 12em;" value="0" readonly/>													
						</div> 																	
					</div>					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<label for="u_nombre" class="form-label">Fecha</label>
						<div class="input-group">												
							<input type="text" id="fcierre" name="fcierre" class="form-control form-control-sm" style="width: 12em;" value="0" readonly/>													
						</div> 																	
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">	
						<label for="u_nombre" class="form-label">&nbsp;</label>		
						<div class="input-group">												
							<input type="button" id="aplicarTodo" name="aplicarTodo" class="btn btn-secondary btn-sm" value="Cerrar masivo" onclick="CerrarMasivo('10')"/>													
						</div> 																	
					</div>
				</div>
					
				<br/>
				
				<div class="row d-flex">								
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
						<div class="table-responsive">	    
							<table id="tblMesesContables" class="table table-striped table-bordered" >
								<thead>
									<tr>
										<th>CC</th>
										<th>UR</th>
										<th>U. Responsable</th>
										<th>Ene</th>
										<th>Feb</th>
										<th>Mar</th>
										<th>Abr</th>
										<th>May</th>
										<th>Jun</th>
										<th>Jul</th>
										<th>Ago</th>
										<th>Sep</th>
										<th>Oct</th>
										<th>Nov</th>
										<th>Dic</th>
									</tr>
								</thead>
							</table>
						</div>
					</div>
				</div>
			</div>
			
			<div id="question" style="display: none; cursor: default">
				<h1 style="font-size: 13pt; color: graytext; font-weight: bold;">
					<span id="titulo">¿Desea cerrar el mes contable?</span>
				</h1>
				<div id="resultMsg" style="display: none;">
				</div>
				
				<div id="subQ" >
					<input type="checkbox" value="true" id="aplicarTodo" name="aplicarTodo" style="display: none">
					<span style="font-size: 10pt; color: gray; display: none;" id="mensaje" >Aplicar a todos los centros contables</span>
					<br />
					<br />
				</div>
				
				<input type="button" id="yes" class="btn btn-secondary btn" value="Si" />
				<input type="button" id="no" class="btn btn-secondary btn" value="No" />
				<input type="button" id="acpt" value="Aceptar" class="btn btn-secondary btn" />
				<br />
			</div>
		</form>
	</body>
</html>