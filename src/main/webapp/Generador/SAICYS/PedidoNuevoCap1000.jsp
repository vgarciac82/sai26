<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@ page import="com.syc.gestion.core.UsuarioPropiedades"%>
<%@ page import="java.util.*" %>
<%
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	String name_user=usuarioTab.getLogin();
	Map propiedades = usuarioTab.getPropiedades();
	UsuarioPropiedades up = (UsuarioPropiedades)propiedades.get("CCENTROCONTABLE");
	String centroContable = up.getValor();
	Map rol = usuarioTab.getRoles();
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    
    <title>L&iacute;neas de Captura</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>

	<style>			// estilos del dialogo
			div#dialog-form fieldset { padding:0; border:0; margin-top:25px; }
			div#users-contain { width: 350px; margin: 20px 0; }
			div#users-contain table { margin: 1em 0; border-collapse: collapse; width: 100%; }
			div#users-contain table td, div#users-contain table th { border: 1px solid #eee; padding: .6em 10px; text-align: left; }
			.ui-dialog .ui-state-error { padding: .3em; }
			.validateTips { border: 1px solid transparent; padding: 0.3em; }
		</style>

	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTablesSolLineas.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script> 
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script> 
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
		
	<script type="text/javascript" charset="utf-8">
		var oTableLineas;
		var oTableCAMBs;
		var nAgregaRegistros = 0;
		var nEditaRegistros = 0;
		var band;
			
		$(document).ready(function() {
			<%
			    String roles="";
				//botones
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					roles += r.getKey().toString()+",";
				}
				if(roles.length()>0){
					roles = roles.substring(0,roles.length()-1);
				}
				int btnLnCopia=0;
				int btnLnElimina=0;
				Map botones=nb.getBotones(roles,"Pedidos","nuevoPedidoCap1000");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					
					if ("btnLnCopiaLineas".equals(img)){
						btnLnCopia=1;
					}
					if ("btnLnElimina".equals(img)){
						btnLnElimina=1;
					}
				}
			%>
				$("input.AyudaSyC").subIniciaDlg();
				$("input.autoCompletaSyC").subIniciaAutoCompleta();

			
			band = false;
			$("#paginacion").val(1);
			queryFormPost("cg_roleRead", { async:false });
			queryFormPost("mSistema_cEjercicioRead", { async:false });
			
			$('#tblRequisicionMeses td:nth-child(3)').live('dblclick', function() {   
				agregarLinea(this,1);
			});
			$('#tblRequisicionMeses td:nth-child(4)').live('dblclick', function() {   
				agregarLinea(this,2);
			});
			$('#tblRequisicionMeses td:nth-child(5)').live('dblclick', function() {   
				agregarLinea(this,3);
			});
			$('#tblRequisicionMeses td:nth-child(6)').live('dblclick', function() {   
				agregarLinea(this,4);
			});
			$('#tblRequisicionMeses td:nth-child(7)').live('dblclick', function() {   
				agregarLinea(this,5);
			});
			$('#tblRequisicionMeses td:nth-child(8)').live('dblclick', function() {   
				agregarLinea(this,6);
			});
			$('#tblRequisicionMeses td:nth-child(9)').live('dblclick', function() {   
				agregarLinea(this,7);
			});
			$('#tblRequisicionMeses td:nth-child(10)').live('dblclick', function() {   
				agregarLinea(this,8);
			});
			$('#tblRequisicionMeses td:nth-child(11)').live('dblclick', function() {   
				agregarLinea(this,9);
			});
			$('#tblRequisicionMeses td:nth-child(12)').live('dblclick', function() {   
				agregarLinea(this,10);
			});
			$('#tblRequisicionMeses td:nth-child(13)').live('dblclick', function() {   
				agregarLinea(this,11);
			});
			$('#tblRequisicionMeses td:nth-child(14)').live('dblclick', function() {   
				agregarLinea(this,12);
			});
			
			if ($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0){
				querySelectPost("UnidadBusca", "cIdUE", { async:false });
			}
			else {
				querySelectPost("UECAP1000User", "cIdUE", { async:false });
			}
			
			querySelectPost("SubPartidasCAP1000Read", "cIdSP", { async:false });
 			querySelectPost("CABMsCAP1000Read", "cIdCABM", { async:false });
			
			//Indice de linea provisional para identificar los items de la tabla de lineas
			$("#nLineaAct").val(1);
			$("#numLineaAct").val(1);
			//Guardar los valores actuales de unidad ejecutora y subpartida
			$("#copiaUE").val($("#cIdUE").val());
			$("#copiaSP").val($("#cIdSP").val());
			
			mostrar();			
			mostrarLineas();
			calcTotales();
			
			if ($("#cIdCABM").val() == null) {
				document.getElementById("trCABM").style.display = "table-row";
			}
			
			$("#cIdUE").change(function() {
				var aTrs = oTableLineas.fnGetNodes();
				if (aTrs.length == 0 || confirm("Esta acción eliminara todas las lineas capturadas ¿Está seguro que desea eliminarlas? Una vez confirmado, no podrá deshacer los cambios.")) {
					oTableCAMBs.fnClearTable(oTableCAMBs);
					mostrar();
					
					oTableLineas.fnClearTable(oTableLineas);
					$("#nLineaAct").val(1);
					$("#numLineaAct").val(1);
					$("#copiaUE").val($("#cIdUE").val());
					calcTotales();
				}
				else {
					$("#cIdUE").val($("#copiaUE").val());
				}
			});
			
			$("#cIdSP").change(function() {
				var aTrs = oTableLineas.fnGetNodes();
				if (aTrs.length == 0 || confirm("Esta acción eliminara todas las lineas capturadas ¿Está seguro que desea eliminarlas? Una vez confirmado, no podrá deshacer los cambios.")) {
					querySelectPost("CABMsCAP1000Read", "cIdCABM", { async:false });
					oTableCAMBs.fnClearTable(oTableCAMBs);
					mostrar();
					
					oTableLineas.fnClearTable(oTableLineas);
					$("#nLineaAct").val(1);
					$("#numLineaAct").val(1);
					$("#copiaSP").val($("#cIdSP").val());
					calcTotales();
				}
				else {
					$("#cIdSP").val($("#copiaSP").val());
				}
			});
			
			$("#cIdCABM").change(function() {
				oTableCAMBs.fnClearTable(oTableCAMBs);
				mostrar();
			});

			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoPedido)%>;
			if(nIdEstado == 1)
				$("#preCompromisoPedido").css("display", "none");
			else
				$("#preCompromisoPedido").css("display", "block");
		});
		
		function mostrar() {
			oTableCAMBs = $("#tblRequisicionMeses").dataTable({
				sScrollX: "440%",
				//sScrollXInner: "440%",
				bScrollCollapse: true,
				bDestroy: true,
				oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" 
					+ window.location.pathname.split("/")[1] + 
					"/crud?rt=t&ql=fn_Calendario('" + $("#cEjercicio").val() + "', '" +
					 $("#cIdUE").val() + "', '" + $("#cIdCABM").val() + "' , '" + 
					 $("#cIdSP").val() + "')",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "cIdCABM" },
					{ sName: "cCABM" },
					{ sName: "enero" },
					{ sName: "febrero" },
					{ sName: "marzo" },
					{ sName: "abril" },
					{ sName: "mayo" },
					{ sName: "junio" },
					{ sName: "julio" },
					{ sName: "agosto" },
					{ sName: "septiembre" },
					{ sName: "octubre" },
					{ sName: "noviembre" },
					{ sName: "diciembre" },
					{ sName: "nPorcentajeIVA"}
				]
        	});
		}
		
		
		function mostrarLineas() {
			oTableLineas = $("#tblLineas").dataTable({
				bPaginate : false,
				bLengthChange : false,
				bInfo : false,
				bAutoWidth : false,				
				sScrollX: "100%",
				bJQueryUI: true,
				bFilter : false,
				bSort : false,
				bInfo : false,
				bAutoWidth : false,
					oLanguage: {
					sProcessing: "Procesando...",
					sLengthMenu: "Mostrar _MENU_ registros",
					sZeroRecords: "No hay registros a mostrar",
					sEmptyTable: "No hay datos en la tabla",
					sLoadingRecords: "Cargando...",
					sInfo: "Registros _START_ al _END_ de _TOTAL_",
					sInfoEmpty: "Registro 0 al 0 de 0",
					sInfoFiltered: "(filtado de _MAX_ registros)",
					sInfoPostFix: "",
					sInfoThousands: ",",
					sSearch: "Buscar:",
					oPaginate: {sFirst:    "Primero", sPrevious: "Ant.", sNext:     "Sigte.", sLast:     "&Uacute;ltimo" }
				},
				aoColumns: [
					{ sName: "lineaCopia", bSortable: false },
					{ sName: "nIdLineas", bSortable: false },
					{ sName: "cIdCABM", bSortable: false },
					{ sName: "cMes", bSortable: false },
					{ sName: "cDescripcion", bSortable: false },
					{ sName: "nCantidad", bSortable: false },
					{ sName: "mPrecioUnitario", bSortable: false },
					{ sName: "nPorcentajeIVA", bSortable: false },
					{ sName: "mImporteNeto", bSortable: false },
					{ sName: "cIdUnidadMedida", bSortable: false },
					{ sName: "lineaElimina", bSortable: false },
					{ sName: "nLineaProv", bSortable: false, bVisible: false },
					{ sName: "nIdPeriodo", bSortable: false, bVisible: false },
					{ sName: "cPeriodo", bSortable: false }
				]
        	});
		}
		
		function agregarLinea(td,mes) {
			$("#nIdPeriodo").val(mes);
			$("#cIdCABMmc").val($("#cIdCABM").val());
			queryFormPost("obtenMontoYCantidadCAP1000Read", { async:false });
			
			calcTotalCABMPeriodo($("#cIdCABMmc").val(), mes);
			$("#mMontoDisponibilidad").val(parseFloat($("#mMontoDisponibilidad").val()) - parseFloat($("#calcTotal").val()));
			$("#nCantidadDisponibilidad").val(parseInt($("#nCantidadDisponibilidad").val()) - parseInt($("#calcCanti").val()));
			
			if (parseInt($("#nCantidadDisponibilidad").val()) > 0) {
				if (parseInt($("#mPrecioUnitario").val()) <= parseInt($("#mMontoDisponibilidad").val())) {
					var li = parseInt($("#nLineaAct").val());
					var num = parseInt($("#numLineaAct").val());
					queryFormPost("CABMCAP1000Read", { async:false });
					var totalNeto = parseFloat($("#mPrecioUnitario").val()) * (1 + (parseInt($("#nIVA").val()) / 100.0));
					totalNeto = roundNumber(totalNeto, 2);
		 			$('#tblLineas').dataTable().fnAddData([ 
					  "<button onclick='copiaLinea(" + li + ");' >Copia</button>",
					  '<input type="text" id="nLinea_' + li +  '" name="nLinea_' + li + '" value="' + num + '" readonly style="width:80px; border-width:0; background-color:transparent"/>', 
					  '<input type="text" id="cIdCABMLinea_' + li +  '" name="cIdCABMLinea_' + li + '" value="' +	$("#cIdCABM").val() + '" readonly style="width:80px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cMes_' + li +  '" name="cMes_' + 1 + '" value="' + $("#cMes").val() + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cDescripcion_' + li + '" name="cDescripcion_' + li +  '" value="' + $("#cCABM").val() + '" align="center" style="width:300px;" onkeypress="textCounter(this,209);"/>',
					  '<input type="text" id="nCantidad_' +	li + '" name="nCantidad_' + li + '" value="' + 1 + '" align="center"  style="width:100px;" onkeypress="return onlyIntegers(event);" onchange="calcTotalLinea(' + li + ');"/>',
					  '<input type="text" id="mPrecioUnitario_' + li + '" name="mPrecioUnitario_' + li + '" value="' + $("#mPrecioUnitario").val() + '" align="center" style="width:100px;" onkeypress="return onlyMoney(event);" onchange="calcTotalLinea(' + li + ');"/>',
					  '<input type="text" id="cIVA_' + li + '" name="cIVA_' + li + '" value="' + $("#nIVA").val() + '%" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cNeto_' + li + '" name="cNeto_' + li + '" value="' + totalNeto + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cUM_' + li + '" name="cUM_' + li + '" value="' + $("#cUM").val() + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  "<button onclick='eliminaLinea(" + li + ");' >Elimina</button>",
					  li,
					  mes,
					  '<input type="hidden" id="nPeriodo_' + li + '" name="nPeriodo_' + li + '" value="' + mes + '" />'
					]);
					
					calcTotales();
					$("#nLineaAct").val(li + 1);
					$("#numLineaAct").val(num + 1);
				}
				else {
					alert("No se cuenta con presupuesto para este CUCOP, edite el programa anual.");
				}
			}
			else {
				alert("No hay disponiblidad para este CUCOP, edite el programa anual."); 
			}
		}
		
		function copiaLinea(li) {
			var cabm = $("#cIdCABMLinea_" + li).val();
			var mes = $("#cMes_" + li).val();
			var desc = $("#cDescripcion_" + li).val();
			var cant = $("#nCantidad_" + li).val();
			var precio = $("#mPrecioUnitario_" + li).val();
			var iva = $("#cIVA_" + li).val();
			var total = $("#cNeto_" + li).val();
			var unidad = $("#cUM_" + li).val();
			var periodo = $("#nPeriodo_" + li).val();
			
			$("#nIdPeriodo").val(periodo);
			$("#cIdCABMmc").val(cabm);
			queryFormPost("obtenMontoYCantidadCAP1000Read", { async:false });
			
			calcTotalCABMPeriodo(cabm, periodo);
			$("#calcTotal").val(parseFloat($("#calcTotal").val()) + parseFloat(total));
			$("#calcCanti").val(parseInt($("#calcCanti").val()) + parseInt(cant));
			$("#mMontoDisponibilidad").val(parseFloat($("#mMontoDisponibilidad").val()) - parseFloat($("#calcTotal").val()));
			$("#nCantidadDisponibilidad").val(parseInt($("#nCantidadDisponibilidad").val()) - parseInt($("#calcCanti").val()));
			
			if (parseInt($("#nCantidadDisponibilidad").val()) >= 0) {
				if (parseFloat($("#mMontoDisponibilidad").val()) >= 0) {
					var li = parseInt($("#nLineaAct").val());
					var num = parseInt($("#numLineaAct").val());
		 			$('#tblLineas').dataTable().fnAddData([ 
					  "<button onclick='copiaLinea(" + li + ");' >Copia</button>",
					  '<input type="text" id="nLinea_' + li +  '" name="nLinea_' + li + '" value="' + num + '" readonly style="width:80px; border-width:0; background-color:transparent"/>', 
					  '<input type="text" id="cIdCABMLinea_' + li +  '" name="cIdCABMLinea_' + li + '" value="' + cabm + '" readonly style="width:80px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cMes_' + li +  '" name="cMes_' + li + '" value="' + mes + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cDescripcion_' + li + '" name="cDescripcion_' + li +  '" value="' + desc + '" align="center" style="width:300px;" onkeypress="textCounter(this,209);"/>',
					  '<input type="text" id="nCantidad_' +	li + '" name="nCantidad_' + li + '" value="' + cant + '" align="center"  style="width:100px;" onkeypress="return onlyIntegers(event);" onchange="calcTotalLinea(' + li + ');"/>',
					  '<input type="text" id="mPrecioUnitario_' + li + '" name="mPrecioUnitario_' + li + '" value="' + precio + '" align="center" style="width:100px;" onkeypress="return onlyMoney(event);" onchange="calcTotalLinea(' + li + ');"/>',
					  '<input type="text" id="cIVA_' + li + '" name="cIVA_' + li + '" value="' + iva + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cNeto_' + li + '" name="cNeto_' + li + '" value="' + total + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  '<input type="text" id="cUM_' + li + '" name="cUM_' + li + '" value="' + unidad + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
					  "<button onclick='eliminaLinea(" + li + ");' >Elimina</button>",
					  li,
					  periodo,
					  '<input type="hidden" id="nPeriodo_' + li + '" name="nPeriodo_' + li + '" value="' + periodo + '" />'
					]);
					
					calcTotales();
					$("#nLineaAct").val(li + 1);
					$("#numLineaAct").val(num + 1);
				}
				else {
					alert("No se cuenta con presupuesto para este CUCOP, edite el programa anual.");
				}
			}
			else {
				alert("No hay disponiblidad para este CUCOP, edite el programa anual."); 
			}
		}
		
		function revisarTotales(){
			var aTrs = oTableLineas.fnGetNodes();
			if (aTrs.length == 0) {
				alert("No ha capturado ninguna linea");
				return false;
			}
			
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[11]);
				var cabm = $("#cIdCABMLinea_" + li).val();
				var periodo = $("#nPeriodo_" + li).val();
				var mes = $("#cMes_" + li).val();
				
				$("#nIdPeriodo").val(periodo);
				$("#cIdCABMmc").val(cabm);
				queryFormPost("obtenMontoYCantidadCAP1000Read", { async:false });
				
				calcTotalCABMPeriodo(cabm, periodo);
				$("#mMontoDisponibilidad").val(parseFloat($("#mMontoDisponibilidad").val()) - parseFloat($("#calcTotal").val()));
				$("#nCantidadDisponibilidad").val(parseInt($("#nCantidadDisponibilidad").val()) - parseInt($("#calcCanti").val()));
				if (parseInt($("#nCantidadDisponibilidad").val()) >= 0) {
					if (parseFloat($("#mMontoDisponibilidad").val()) >= 0) {
						//Hay suficiencia
					}
					else {
						alert("No se cuenta con presupuesto para el CUCOP " + cabm + " en el mes de " + mes + ", edite el programa anual.");
						return false;
					}
				}
				else {
					alert("No hay disponiblidad para para el CUCOP " + cabm + " en el mes de " + mes + ", edite el programa anual.");
					return false; 
				}
			}
			return true;
		}
		
		function calcTotalCABMPeriodo(cabm, periodo){
			var aTrs = oTableLineas.fnGetNodes();
			var total = 0.0;
			var cantidad = 0;
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[11]);
				var curper = line[12];
				var curcabm = $("#cIdCABMLinea_" + li).val()
				if (parseInt(curper) == parseInt(periodo)){
					if ($.trim(curcabm) == $.trim(cabm)) {
						var canti = parseInt($("#nCantidad_" + li).val());
						var price = parseFloat($("#mPrecioUnitario_" + li).val());
						var civa = $("#cIVA_" + li).val();
						var iva = 1.0 + (parseFloat(civa.replace('%','')) / 100.0);
						total = total + (canti * price * iva);
						cantidad = cantidad + canti;
					}
				}
			}
			$("#calcTotal").val(total);
			$("#calcCanti").val(cantidad);
		}
		
		function calcTotales(){
			var bruto = 0.0;
			var neto = 0.0;
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[11]);
				var canti = parseInt($("#nCantidad_" + li).val());
				var price = parseFloat($("#mPrecioUnitario_" + li).val());
				var civa = $("#cIVA_" + li).val();
				var iva = 1.0 + (parseFloat(civa.replace('%','')) / 100.0);
				bruto = bruto + (canti * price);
				neto = neto + (canti * price * iva);
				var netorounded = roundNumber(canti * price * iva, 2);
				$("#cNeto_" + li).val(netorounded);
			}
			$("#montoBruto").val(bruto);
			$("#montoNeto").val(neto);
			
			$("#montoBruto").formatCurrency();
			$("#montoNeto").formatCurrency();
		}
		
		function calcTotalLinea(li){
			var canti = parseInt($("#nCantidad_" + li).val());
			var price = parseFloat($("#mPrecioUnitario_" + li).val());
			var civa = $("#cIVA_" + li).val();
			var iva = 1 + (parseFloat(civa.replace('%','')) / 100.0);
			var neto = (canti * price * iva);
			neto = roundNumber(neto, 2);
			$("#cNeto_" + li).val(neto);
			calcTotales();
		}
		
		function renumerar() {
			var numLinea = 1;
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[11]);
				$("#nLinea_" + li).val(numLinea);
				numLinea++;
			}
			$("#numLineaAct").val(numLinea);
		}
		
		function eliminarLineas() {
			if (confirm("¿Está seguro que desea eliminar las líneas? Una vez confirmado, no podrá deshacer los cambios.")) {
				oTableLineas.fnClearTable(oTableLineas);
				$("#nLineaAct").val(1);
				$("#numLineaAct").val(1);
				calcTotales();
			}
		}
		
		function eliminaLinea(linea) {
			var indiceTabla = -1;
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[11]);
				if (linea == li) {
					indiceTabla = i;
					break;
				}
			}
			if (indiceTabla >= 0) {
				$('#tblLineas').dataTable().fnDeleteRow(indiceTabla);
				renumerar();
				calcTotales();
			}
		}
		
		function generaPedido(){
			if ($("#cDescripcion").val() == '') {
				alert("Dede proporcionar una descripción para el pedido");
				return;
			}
			
			var resultLineas = revisarTotales();
			if (resultLineas) {
				//Inserta la Solicitud
				queryFormPost("mSolicitudSiguienteConsecutivoCAP1000Read", { async:false });
				queryFormPost("mSolicitudCAP1000Create", { async:false });
				
				//Inserta las lineas de la Solicitud
				var aTrs = oTableLineas.fnGetNodes();
				for (var i = 0; i < aTrs.length; i++) {
					var line = oTableLineas.fnGetData(aTrs[i])
					var li = parseInt(line[11]);
					$("#cIdLS").val(parseInt($("#nLinea_" + li).val()));
					$("#cIdCabmLS").val($("#cIdCABMLinea_" + li).val());
					$("#cDescLS").val($("#cDescripcion_" + li).val());
					$("#nCantLS").val(parseInt($("#nCantidad_" + li).val()));
					$("#mPrecioLS").val(parseFloat($("#mPrecioUnitario_" + li).val()));
					var iva = $("#cIVA_" + li).val();
					$("#nIvaLS").val(parseInt(iva.replace('%','')));
					$("#nIvaPA").val(parseInt(iva.replace('%','')));
					$("#nIdPeriodoLS").val(parseInt($("#nPeriodo_" + li).val()));
					queryFormPost("mSolicitudLineaCAP1000Create", { async:false });
				}
				
				//Inserta el consolidado
				queryFormPost("mConsolidadoAutomaticoCAP1000Create", { async:false });
				
				//Inserta el procedimiento
				
				queryFormPost("mConsolidadoConsecutivoCAP1000Read",{async : false});
				queryFormPost("mProcedimientoSiguienteConsecutivoCAP1000Read",{async : false});
				queryFormPost("mProcedimientoCAP1000Create",{async : false});
				
				//Inserta el procedimiento adjudicacion
				queryFormPost("mProcedimientoAdjudicacionCAP1000Create",{async : false});
				
				//Inserta las partidas del procedimiento
				var aTrs = oTableLineas.fnGetNodes();
				for (var i = 0; i < aTrs.length; i++) {
					var line = oTableLineas.fnGetData(aTrs[i])
					var li = parseInt(line[11]);
					$("#cIdLC").val(parseInt($("#nLinea_" + li).val()));
					$("#cDescPAP").val($("#cDescripcion_" + li).val());
					$("#mMontoPAP").val(parseFloat($("#mPrecioUnitario_" + li).val()));
					queryFormPost("mProcedimientoAdjudicacionPartidasCAP1000Create", { async:false });
				}
				
				queryFormPost("mPedidoCAP1000Create", { async:false });
				
				queryFormPost("mPedidoConsecutivoCAP1000Read", { async:false });
				var url = "Pedidos.jsp?tab=1" + "&cEjercicio=" + $("#cEjercicio").val() + "&cIdTipoPedido=PE" + "&cIdUnidadEjecutora=" + $("#cIdUE").val() + "&nIdConsecutivo=" + $("#consPedido").val();
				
				alert("Pedido generado correctamente");
				
				window.location = url;
			}
		}
		
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		
		function onlyIntegers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		function metodo(i) {
			if (band == false) {
				if (i == -1)
					i = $("#paginacion").val() - 1;
				if (i == -2)
					i = $("#paginacion").val() + 1;
				$("#paginacion").val(i);
			}
			band!=band;
		}
		
		function roundNumber(number, digits) {
            var multiple = Math.pow(10, digits);
            var rndedNum = Math.round(number * multiple) / multiple;
            return rndedNum;
        }
        function catproveedor(){
        	AyudaXML = null;
			AyudaXML = new ActiveXObject("Microsoft.XMLDOM");
			AyudaXML.async="false";
        	src="../../Ayudas/xml-ayudas/txtGridProveedor.xml";
			AyudaXML.load(src);
			//window.open("../../Ayudas/xml-ayudas/txtGridProveedor.xml","Proveedor","status=1, width=600px, height=600px, left=150px,scrollbars=1 ");
			//src="../../Ayudas/xml-ayudas/txtGridProveedor.xml";
			//AyudaXML = null;
		}
	</script>
  </head>
  
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0">
  	<form> 
		<div id="container" class="container">
			<table align="left" width="750px">
				<tr>
					<td>
						<fieldset style="width:750px" align="left">
							<legend>Informaci&oacute;n</legend>
							<table align="left" width="750px">
								<tr>
						    		<td align="right" colspan="2">
                                    	<img id="imgAprobarLineasReqPed1000" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="generaPedido();"/> Generar Pedido
                                	</td>
						    	</tr>
						    	
						    	<tr>
					    			<td align="left">Unidad Ejecutora:<select id="cIdUE" name="cIdUE" style="width: 600px;" > <option value="<%=usuarioTab.getU_UR()%>" selected="selected"> </option> </select> </td>
					    		</tr>
					    		
					    		<tr>
					    			<td align="left">Partida:<select id="cIdSP" name="cIdSP" style="width: 665px;" > </select> </td>
					    		</tr>
					    		
					    		<tr>
					    			<td align="left" style="width: 100%" >Proveedor:
					    				<input title="Proveedor" name="cRazonSocial" id="cRazonSocial" style="width: 600px;" class="AyudaSyC autoCompletaSyC" />
									</td>	
					    			

					    		</tr>
					    		
					    		<tr>
					    			<td align="left">Descripción:<input type="text" id="cDescripcion" name="cDescripcion" style="width: 635px;" /> </td>
					    		</tr>
							</table>
						</fieldset>
					</td>
				</tr>
				<tr>
					<td>
						<fieldset style="width:750px" align="left">
							<legend>Agregar CUCOPS</legend>
								<table align="left" width="750px">
							    	<tr id="trCABM" style="display:none">
						    			<td align="left"><textarea style="width: 700px" id="rfvCAMB" name="rfvCAMB" readonly style="color: red; border-width:0; background-color:transparent; overflow: auto;" >No existe ningun CUCOP en el Programa Anual que cumpla los requisitos de la requisici&oacute;n o no cuenta con suficiencia en el periodo</textarea></td>
						    		</tr>
						    		<tr>
						    			<td align="left">CUCOP:<select id="cIdCABM" name="cIdCABM" style="width: 400px;" ></select> <img id="imgRefresh" src="../../imagenes/icono_refresh.jpg" style="cursor: pointer" onclick="oTableCAMBs.fnClearTable(oTableCAMBs); mostrar();"/> Actualizar </td>
						    		</tr>
						    		<tr>
						    			<td style="width: 740px">
						    				<table id="tblRequisicionMeses" class="display" width="740px">
												<thead >
													<tr>
														<th align="center">&nbsp;&nbsp;CUCOP&nbsp;&nbsp;</th>
														<th align="center" style="width: 300px">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Descripci&oacute;n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
														<th align="center" style="width: 300px">ENERO<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">FEBRERO<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">MARZO<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">ABRIL<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">MAYO<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">JUNIO<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">JULIO<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">AGOSTO<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">SEPTIEMBRE<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">OCTUBRE<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">NOVIEMBRE<br />Mon. Disp. / Can. Disp.</th>
														<th align="center" style="width: 300px">DICIEMBRE<br />Mon. Disp. / Can. Disp.</th>
														<th align="center"></th>
													</tr>										
												</thead>
											</table>
						    			</td>
						    		</tr>
						    	</table>
		    			</fieldset>
		    		</td>
		    	</tr>
	    		<tr>
	    			<td>
	    				<fieldset style="width:750px" align="left">
	    					<legend>L&iacute;neas capturadas</legend>
			    				<table width="740px">
			    					
			    					<tr>
			    						<td align="left">Monto bruto de la requisici&oacute;n:<input type="text" style="width: 400px" id="montoBruto" name="montoBruto" readonly style="border-width:0; background-color:transparent"/></td>
			    					</tr>
			    					<tr>
			    						<td align="left">Monto neto de la requisici&oacute;n:<input type="text" style="width: 400px" id="montoNeto" name="montoNeto" readonly style="border-width:0; background-color:transparent"/></td>
			    					</tr>
			    					<tr>
			    						<td style="width: 740px">
			    							<table id="tblLineas" class='display' width="740px" >
									        	<thead>
									        		<tr>
									        			<th></th>
									        			<th>#</th>
									        			<th>CUCOP</th>
									        			<th>MES</th>
									        			<th style="width: 300px">Descripci&oacute;n</th>
									        			<th>Cantidad</th>
									        			<th>Precio<br/>Unitario</th>
									        			<th>IVA</th>
									        			<th>Importe<br/>Neto</th>
									        			<th>Unidad de<br/>Medida</th>
									        			<th></th>
									        			<th></th>
									        			<th></th>
									        			<th></th>
									        		</tr>
									        	</thead>
									        </table>
			    						</td>
			    						<tr>
			    						<td align="left"><button id="btnCalcTotalPed1000" name="btnCalcTotalPed1000" onclick="calcTotales();">Calcular Total</button><button id="btnEliminarLineasReqPed1000" name="btnEliminarLineasReqPed1000" onclick="eliminarLineas();">Eliminar Lineas</button></td>
			    					</tr>
			    						
			    					</tr>
			    				</table>
	    				</fieldset>
	    			</td>
	    		</tr>
	        </table>
	        <input type="hidden" name="cEjercicio" id="cEjercicio" />
	        
	        <input type="hidden" name="nLineaAct" id="nLineaAct" />
	        <input type="hidden" name="numLineaAct" id="numLineaAct" />
	        <input type="hidden" name="cIdCABMmc" id="cIdCABMmc" />
		    <input type="hidden" name="mPrecioUnitario" id="mPrecioUnitario"/>
		    <input type="hidden" name="nIVA" id="nIVA"/>
		    <input type="hidden" name="cCABM" id="cCABM"/>
		    <input type="hidden" name="cMes" id="cMes"/>
		    <input type="hidden" name="cUM" id="cUM"/>
		    
		    <input type="hidden" name="nIdLineaSolicitud" id="nIdLineaSolicitud"/>
		    <input type="hidden" name="nCantidad" id="nCantidad"/>
		    
		    <input type="hidden" name="nLineas" id="nLineas" />
		    
		    <input type="hidden" name="cIdEstadoLinea" id="cIdEstadoLinea" />
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN"  value="<%=usuarioTab.getLogin()%>"/>
			<input type="hidden" name="R_NOMBRE" id="R_NOMBRE" />
			<input type="hidden" name="cIdUnidadEjecutoraUsuario" id="cIdUnidadEjecutoraUsuario" value="<%=usuarioTab.getU_UR()%>" />
			
			<input type="hidden" name="nCantidadDisponibilidad" id="nCantidadDisponibilidad" />
			<input type="hidden" name="mMontoDisponibilidad" id="mMontoDisponibilidad" />
			<input type="hidden" name="nIdPeriodo" id="nIdPeriodo" />
			
			<input type="hidden" name="calcTotal" id="calcTotal" />
			<input type="hidden" name="calcCanti" id="calcCanti" />
			
			<input type="hidden" name="paginacion" id="paginacion" value="1"/>
			
			
			<input type="hidden" name="copiaUE" id="copiaUE" />
			<input type="hidden" name="copiaSP" id="copiaSP" />
			
			<!--INSERTA Solicitud-->
			<input type="hidden" name="consSolicitud" id="consSolicitud" />
			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
			<input type="hidden" name="cIdEntidadContable" id="cIdEntidadContable" value="<%=centroContable%>"/>
			
			<!--INSERTA Linea Solicitud-->
			<input type="hidden" name="cIdLS" id="cIdLS" />
			<input type="hidden" name="cIdCabmLS" id="cIdCabmLS" />
			<input type="hidden" name="cDescLS" id="cDescLS" />
			<input type="hidden" name="nCantLS" id="nCantLS" />
			<input type="hidden" name="mPrecioLS" id="mPrecioLS" />
			<input type="hidden" name="nIvaLS" id="nIvaLS" />
			<input type="hidden" name="nIdPeriodoLS" id="nIdPeriodoLS" />
			
			<!--INSERTA Consolidado-->
			<input type="hidden" name="consConsolidado" id="consConsolidado" />
			
			<!--INSERTA Procedimiento-->
			<input type="hidden" name="consProcedimiento" id="consProcedimiento" />
			
			<!--INSERTA Procedimiento Adjudicacion-->
			<input type="hidden" name="nIvaPA" id="nIvaPA" />
			<input type="hidden" name="cIdRfc" id="cIdRfc" />
			
			<!--INSERTA Procedimiento Adjudicacion Partidas-->
			<input type="hidden" name="cIdLC" id="cIdLC" />
			<input type="hidden" name="mMontoPAP" id="mMontoPAP" />
			<input type="hidden" name="cDescPAP" id="cDescPAP" />
			
			<!--CONSULTA -->
			<input type="hidden" name="consPedido" id="consPedido" />
			
	    </div>
	</form>
  </body>
</html>
