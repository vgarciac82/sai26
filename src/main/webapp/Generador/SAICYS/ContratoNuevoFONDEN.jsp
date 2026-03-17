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
				int imgBtnGeneraContrato=0;
				Map botones=nb.getBotones(roles,"Contratos","nuevoContratoFONDEN");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
					String img=(String) b.getValue();
					
					if ("imgBtnGeneraContrato".equals(img)){
						%>$("#<%=img%>").attr("disabled", false);<%
						imgBtnGeneraContrato=1;
					}
					
				}
			%> 
			$("input.AyudaSyC").subIniciaDlg();
			$("input.autoCompletaSyC").subIniciaAutoCompleta();
			
			band = false;
			$("#paginacion").val(1);
			queryFormPost("cg_roleRead", { async:false });
			queryFormPost("mSistema_cEjercicioRead", { async:false });
			
			if ($("#R_NOMBRE").val().toString().indexOf("ADMIN_RECMAT") >= 0){
				querySelectPost("UnidadBusca", "cIdUE", { async:false });
			}
			else {
				querySelectPost("UECAP1000User", "cIdUE", { async:false });
			}
			querySelectPost("mCatalogoTipoIVA", "nIVA", {async : false});
			querySelectPost("CapitulosFONDENRead", "cIdCap", { async:false });
			querySelectPost("SubPartidasFONDENRead", "cIdSP", { async:false });
			querySelectPost("CABMsCAP1000Read", "cIdCABM", { async:false });
			querySelectPost("MonedaFONDENRead", "cIdTipoMoneda", { async:false });
			
			//Indice de linea provisional para identificar los items de la tabla de lineas
			$("#nLineaAct").val(1);
			$("#numLineaAct").val(1);
			//Guardar los valores actuales de unidad ejecutora y subpartida
			$("#copiaUE").val($("#cIdUE").val());
			$("#copiaCap").val($("#cIdCap").val());
			$("#copiaSP").val($("#cIdSP").val());
			
			mostrar();			
			mostrarLineas();
			calcTotales();
			
			$('#tblPAAFonden tr').live('click', function() {
				$(oTableCAMBs.fnSettings().aoData).each(function (){
					$(this.nTr).removeClass('row_selected');
				});						
				$(this).addClass('row_selected');
			});
			
			
			
			$("#cIdCap").change(function() {
				querySelectPost("SubPartidasFONDENRead", "cIdSP", { async:false });
				querySelectPost("CABMsCAP1000Read", "cIdCABM", { async:false });
			});
			
			$("#cIdSP").change(function() {
				querySelectPost("CABMsCAP1000Read", "cIdCABM", { async:false });
			});

			var nIdEstado=<%=session.getAttribute(GestionInterface.ATT_EstadoContrato)%>;
			if(nIdEstado == 1)
				$("#preCompromisoContrato").css("display", "none");
			else
				$("#preCompromisoContrato").css("display", "block");
		});
		
		
		
		function mostrar() {
			oTableCAMBs = $("#tblPAAFonden").dataTable({
				sScrollX: "200%",
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
					"/crud?rt=t&ql=fn_mProgramaAnualFONDEN()",
				bProcessing: true,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 0, "asc" ]] ,
				aoColumns: [
					{ sName: "nConsecutivo" },
					{ sName: "cIdCABM" },
					{ sName: "cCABM" },
					{ sName: "cDescripcion" },
					{ sName: "nCantidad" },
					{ sName: "mPrecioUnitario" },
					{ sName: "mMontoBruto" }
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
					{ sName: "cDescripcion", bSortable: false },
					{ sName: "nCantidad", bSortable: false },
					{ sName: "mPrecioUnitario", bSortable: false },
					{ sName: "nPorcentajeIVA", bSortable: false },
					{ sName: "mImporteNeto", bSortable: false },
					{ sName: "cIdUnidadMedida", bSortable: false },
					{ sName: "lineaElimina", bSortable: false },
					{ sName: "nLinea", bSortable: false, bVisible: false },
					{ sName: "agregapAAF", bSortable: false, bVisible: false }
				]
        	});
		}
		
		function agregarLineaCUCOP() {
		
		    if($("#nIVA").val()==""){
		      alert("No se asigno el porcentaje de IVA,favor de ingresarlo");
		      return;
		      }
		
		
			$("#cIdCABMmc").val($("#cIdCABM").val());
			var li = parseInt($("#nLineaAct").val());
			var num = parseInt($("#numLineaAct").val());
			queryFormPost("CABMFONDENRead", { async:false });
			var precio = 1.0;
			var totalNeto = precio * (1 + (parseInt($("#nIVA").val()) / 100.0));
 			$('#tblLineas').dataTable().fnAddData([ 
			  "<button onclick='copiaLinea(" + li + ");' >Copia</button>",
			  '<input type="text" id="nLinea_' + li +  '" name="nLinea_' + li + '" value="' + num + '" readonly style="width:80px; border-width:0; background-color:transparent"/>', 
			  '<input type="text" id="cIdCABMLinea_' + li +  '" name="cIdCABMLinea_' + li + '" value="' + $("#cIdCABM").val() + '" readonly style="width:80px; border-width:0; background-color:transparent"/>',
			  '<input type="text" id="cDescripcion_' + li + '" name="cDescripcion_' + li +  '" value="' + $("#cCABM").val() + '" align="center" style="width:300px;" onkeypress="textCounter(this,209);"/>',
			  '<input type="text" id="nCantidad_' +	li + '" name="nCantidad_' + li + '" value="' + 1 + '" align="center"  style="width:100px;" onkeypress="return onlyIntegers(event);" onchange="calcTotalLinea(' + li + ');"/>',
			  '<input type="text" id="mPrecioUnitario_' + li + '" name="mPrecioUnitario_' + li + '" value="' + precio + '" align="center" style="width:100px;" onkeypress="return onlyMoney(event);" onchange="calcTotalLinea(' + li + ');"/>',
			  '<input type="text" id="cIVA_' + li + '" name="cIVA_' + li + '" value="' + $("#nIVA").val() + '%" readonly style="width:70px; border-width:0; background-color:transparent"/>',
			  '<input type="text" id="cNeto_' + li + '" name="cNeto_' + li + '" value="' + totalNeto + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
			  '<input type="text" id="cUM_' + li + '" name="cUM_' + li + '" value="' + $("#cUM").val() + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
			  "<button onclick='eliminaLinea(" + li + ");' >Elimina</button>",
			  li,
			  true
			]);
			
			calcTotales();
			$("#nLineaAct").val(li + 1);
			$("#numLineaAct").val(num + 1);
		}
		
		function agregarLinea() {
			var anSelected = fnGetSelected( oTableCAMBs );
			if (anSelected.length > 0) { 
				var aData = oTableCAMBs.fnGetData(anSelected[0]);
				$("#cIdCABMmc").val(aData[1]);
				var desc = aData[3];
				var cant = aData[4];
				var precio = aData[5].replace('$','').replace(',','');
				
				var li = parseInt($("#nLineaAct").val());
				var num = parseInt($("#numLineaAct").val());
				queryFormPost("CABMFONDENRead", { async:false });
				var totalNeto = cant * parseFloat($("#mPrecioUnitario").val()) * parseFloat($("#mTipoCambio").val()) * (1 + (parseInt($("#nIVA").val()) / 100.0));
				totalNeto = roundNumber(totalNeto, 2);
	 			$('#tblLineas').dataTable().fnAddData([ 
				  "<button onclick='copiaLinea(" + li + ");' >Copia</button>",
				  '<input type="text" id="nLinea_' + li +  '" name="nLinea_' + li + '" value="' + num + '" readonly style="width:80px; border-width:0; background-color:transparent"/>', 
				  '<input type="text" id="cIdCABMLinea_' + li +  '" name="cIdCABMLinea_' + li + '" value="' + $("#cIdCABMmc").val() + '" readonly style="width:80px; border-width:0; background-color:transparent"/>',
				  '<input type="text" id="cDescripcion_' + li + '" name="cDescripcion_' + li +  '" value="' + desc + '" align="center" style="width:300px;" onkeypress="textCounter(this,209);"/>',
				  '<input type="text" id="nCantidad_' +	li + '" name="nCantidad_' + li + '" value="' + cant + '" align="center"  style="width:100px;" onkeypress="return onlyIntegers(event);" onchange="calcTotalLinea(' + li + ');"/>',
				  '<input type="text" id="mPrecioUnitario_' + li + '" name="mPrecioUnitario_' + li + '" value="' + precio + '" align="center" style="width:100px;" onkeypress="return onlyMoney(event);" onblur="cambiafrmt(this.name);"  onchange="calcTotalLinea(' + li + ');"/>',
				  '<input type="text" id="cIVA_' + li + '" name="cIVA_' + li + '" value="' + $("#nIVA").val() + '%" readonly style="width:70px; border-width:0; background-color:transparent"/>',
				  '<input type="text" id="cNeto_' + li + '" name="cNeto_' + li + '" value="' + totalNeto + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
				  '<input type="text" id="cUM_' + li + '" name="cUM_' + li + '" value="' + $("#cUM").val() + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
				  "<button onclick='eliminaLinea(" + li + ");' >Elimina</button>",
				  li,
				  false
				]);
				
				calcTotales();
				$("#nLineaAct").val(li + 1);
				$("#numLineaAct").val(num + 1);
			}
			else {
				alert("No ha seleccionado un CUCOP para agregar");
			}
				
		}
		
		function copiaLinea(li) {
			var cabm = $("#cIdCABMLinea_" + li).val();
			var desc = $("#cDescripcion_" + li).val();
			var cant = $("#nCantidad_" + li).val();
			var precio = $("#mPrecioUnitario_" + li).val();
			var total = $("#cNeto_" + li).val();
			var unidad = $("#cUM_" + li).val();
			
			var li = parseInt($("#nLineaAct").val());
			var num = parseInt($("#numLineaAct").val());
 			$('#tblLineas').dataTable().fnAddData([ 
			  "<button onclick='copiaLinea(" + li + ");' >Copia</button>",
			  '<input type="text" id="nLinea_' + li +  '" name="nLinea_' + li + '" value="' + num + '" readonly style="width:80px; border-width:0; background-color:transparent"/>', 
			  '<input type="text" id="cIdCABMLinea_' + li +  '" name="cIdCABMLinea_' + li + '" value="' + cabm + '" readonly style="width:80px; border-width:0; background-color:transparent"/>',
			  '<input type="text" id="cDescripcion_' + li + '" name="cDescripcion_' + li +  '" value="' + desc + '" align="center" style="width:300px;" onkeypress="textCounter(this,209);"/>',
			  '<input type="text" id="nCantidad_' +	li + '" name="nCantidad_' + li + '" value="' + cant + '" align="center"  style="width:100px;" onkeypress="return onlyIntegers(event);" onchange="calcTotalLinea(' + li + ');"/>',
			  '<input type="text" id="mPrecioUnitario_' + li + '" name="mPrecioUnitario_' + li + '" value="' + precio + '" align="center" style="width:100px;" onkeypress="return onlyMoney(event);" onchange="calcTotalLinea(' + li + ');"/>',
			  '<input type="text" id="cIVA_' + li + '" name="cIVA_' + li + '" value="' + $("#nIVA").val() + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
			  '<input type="text" id="cNeto_' + li + '" name="cNeto_' + li + '" value="' + total + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
			  '<input type="text" id="cUM_' + li + '" name="cUM_' + li + '" value="' + unidad + '" readonly style="width:70px; border-width:0; background-color:transparent"/>',
			  "<button onclick='eliminaLinea(" + li + ");' >Elimina</button>",
			  li,
			  false
			]);
			
			calcTotales();
			$("#nLineaAct").val(li + 1);
			$("#numLineaAct").val(num + 1);
		}
		
		function calcTotales(){
			var civa = parseInt($("#nIVA").val());
			var iva = 1.0 + (civa / 100.0);
			var tcambio = parseFloat($("#mTipoCambio").val());
			
			var bruto = 0.0;
			var neto = 0.0;
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[10]);
				var canti = parseInt($("#nCantidad_" + li).val());
				var price = parseFloat($("#mPrecioUnitario_" + li).val());
				bruto = bruto + (canti * price * tcambio);
				neto = neto + (canti * price * tcambio * iva);
				var netorounded = roundNumber(canti * price * tcambio * iva, 2);
				$("#cIVA_" + li).val(civa + '%');
				$("#cNeto_" + li).val(netorounded);
			}
			$("#montoBruto").val(bruto);
			$("#montoNeto").val(neto);
			
			$("#montoBruto").formatCurrency();
			$("#montoNeto").formatCurrency();
		}
		
		function calcTotalLinea(li){
			var civa = parseInt($("#nIVA").val());
			var iva = 1.0 + (civa / 100.0);
			var tcambio = parseFloat($("#mTipoCambio").val());
			var canti = parseInt($("#nCantidad_" + li).val());
			var price = parseFloat($("#mPrecioUnitario_" + li).val());
			var neto = (canti * price * tcambio * iva);
			neto = roundNumber(neto, 2);
			$("#cNeto_" + li).val(neto);
			calcTotales();
		}
		
		function renumerar() {
			var numLinea = 1;
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[10]);
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
				var li = parseInt(line[10]);
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
		
		function generaContrato(){
			var imgBtnGeneraContrato='<%=imgBtnGeneraContrato%>';
			if (imgBtnGeneraContrato==0){
				if ($("#cDescripcion").val() == '') {
				alert("Dede proporcionar una descripción para el contrato");
				return;
			}
			
			var aTrs = oTableLineas.fnGetNodes();
			if (aTrs.length == 0) {
				alert("No ha capturado ninguna linea");
				return;
			}
			
			//Inserta las lineas en el Programa Anula FONDEN
			
            // alert($("#mPrecioUnitario").val());
        
        
			
		//  para eliminar comas	$("#mImportePoliza").val($("#mImportePoliza").val().replace(/,/g, ''));			
			
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var generarPAAF = line[11];
				if (generarPAAF) {
					var li = parseInt(line[10]);
					$("#cIdCabmPAF").val($("#cIdCABMLinea_" + li).val());
					$("#cDescPAF").val($("#cDescripcion_" + li).val());
					$("#nCantPAF").val(parseInt($("#nCantidad_" + li).val()));
					$("#mPrecioPAF").val(parseFloat($("#mPrecioUnitario_" + li).val()));
					$("#mMontoPAF").val(parseFloat($("#mPrecioPAF").val()) * parseInt($("#nCantPAF").val()));
					queryFormPost("SiguienteConsecutivoPAAFONDENRead", { async:false });
					queryFormPost("mPAAFONDENCreate", { async:false });
				}
			}
			
			//Inserta la Solicitud
			queryFormPost("mSolicitudSiguienteConsecutivoFONDENRead", { async:false });
			queryFormPost("mSolicitudFONDENCreate", { async:false });
			
			//Inserta las lineas de la Solicitud
					 
			   
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[10]);
				$("#cIdLS").val(parseInt($("#nLinea_" + li).val()));
				$("#cIdCabmLS").val($("#cIdCABMLinea_" + li).val());
				$("#cDescLS").val($("#cDescripcion_" + li).val());
				$("#nCantLS").val(parseInt($("#nCantidad_" + li).val()));
				$("#mPrecioLS").val(parseFloat($("#mPrecioUnitario_" + li).val()));
				$("#nIvaLS").val(parseInt($("#nIVA").val()));
				queryFormPost("SubPartidaFONDENRead", { async:false });
				queryFormPost("mSolicitudLineaFONDENCreate", { async:false });
			}
			
			//Inserta el consolidado
			queryFormPost("mConsolidadoAutomaticoFONDENCreate", { async:false });
			
			//Inserta el procedimiento
			
			$("#nIvaPA").val(parseInt($("#nIVA").val()));
			$("#mTipoCambioPA").val(parseFloat($("#mTipoCambio").val()));
			queryFormPost("mConsolidadoConsecutivoFONDENRead",{async : false});
			queryFormPost("mProcedimientoSiguienteConsecutivoFONDENRead",{async : false});
			queryFormPost("mProcedimientoFONDENCreate",{async : false});
			
			//Inserta el procedimiento adjudicacion
			queryFormPost("mProcedimientoAdjudicacionFONDENCreate",{async : false});
			
			//Inserta las partidas del procedimiento
			var aTrs = oTableLineas.fnGetNodes();
			for (var i = 0; i < aTrs.length; i++) {
				var line = oTableLineas.fnGetData(aTrs[i])
				var li = parseInt(line[10]);
				$("#cIdLC").val(parseInt($("#nLinea_" + li).val()));
				$("#cDescPAP").val($("#cDescripcion_" + li).val());
				$("#mMontoPAP").val(parseFloat($("#mPrecioUnitario_" + li).val()));
				queryFormPost("mProcedimientoAdjudicacionPartidasFONDENCreate", { async:false });
			}
			
			queryFormPost("mContratoFONDENCreate", { async:false });
			
			queryFormPost("mContratoConsecutivoFONDENRead", { async:false });
			var url = "Contratos.jsp?tab=1" + "&cEjercicio=" + $("#cEjercicio").val() + "&cIdTipoContrato=" + $("#cIdTipoContrato").val() + "&cIdUnidadEjecutora=" + $("#cIdUE").val() + "&nIdConsecutivo=" + $("#consContrato").val();
			
			alert("Contrato generado correctamente");
			
			window.location = url;
			}else{
				alert("Usted no tiene permisos para realizar esta accion, contacte a su administrador");
			}
			
		}
		
		function textCounter( field, maxlimit ) {
			if ( field.value.length > maxlimit )
				field.value = field.value.substring( 0, maxlimit );
		}
		
		function checkNaN(field, def){
			if ( field.value == '' ){
				field.value = def;
			} 
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
		
		function roundNumber(number, digits) {
            var multiple = Math.pow(10, digits);
            var rndedNum = Math.round(number * multiple) / multiple;
            return rndedNum;
        }
        
        function fnGetSelected( oTableLocal ) {
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();
			for ( var i=0 ; i<aTrs.length ; i++ )
			{
				if ( $(aTrs[i]).hasClass('row_selected') )
				{
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		
		
		function cambiafrmt(fld){
	   		$("#"+fld).formatCurrency();
		}
		
		function cambiarMoneda(){
			if ($("#cIdTipoMoneda").val() == '01') {
				document.getElementById("mTipoCambio").disabled = true;
				$("#mTipoCambio").val('1.0');
				calcTotales();
			}
			else {
				document.getElementById("mTipoCambio").disabled = false;
			}
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
                                    	<img id="imgBtnGeneraContrato" src="../imagenes/accept_green.png" style="cursor: pointer" onclick="generaContrato();"/> Generar Contrato
                                	</td>
						    	</tr>
						    	

						    	
						    	<tr>
					    			<td align="left">Unidad Ejecutora:<select id="cIdUE" name="cIdUE" style="width: 600px;" > <option value="<%=usuarioTab.getU_UR()%>" selected="selected"> </option> </select> </td>
					    		</tr>
						    	<tr>
					    			<td align="left">
					    				Tipo de Contrato:
					    				<select id="cIdTipoContrato" name="cIdTipoContrato" style="width: 200px;" > 
					    					<option label="Contrato de Servicios" value="CV" selected="selected"> </option>
					    					<option label="Contrato de Obra" value="CB" > </option> 
					    				</select> 
					    			</td>
					    		</tr>					    		
					    		<tr>
					    			<td align="left">Capítulo:<select id="cIdCap" name="cIdCap" style="width: 655px;" > </select> </td>
					    		</tr>
					    		
					    		<tr>
					    			<td align="left">Partida:<select id="cIdSP" name="cIdSP" style="width: 665px;" > </select> </td>
					    		</tr>
					    		
					    		<tr>
					    			<td align="left">Proveedor:<input title="Proveedor" name="cRazonSocial" id="cRazonSocial" style="width: 600px;" class="AyudaSyC autoCompletaSyC" /> 
					    			</td>
					    		</tr>
					    		
					    		<tr>
					    			<td align="left">Descripción:<input type="text" id="cDescripcion" name="cDescripcion" style="width: 630px;" /> </td>
					    		</tr>
					    		
					    		<tr>
					    			<td align="left"> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					    				IVA:<select id="nIVA" name="nIVA" style="width: 6em;" onchange="calcTotales();"> 
											</select>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<!-- 					    				<input type="text" id="nIVA" name="nIVA" style="width:50px;" onkeypress="return onlyIntegers(event);" onchange="checkNaN(this,0); calcTotales();"/> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; -->
					    				Moneda:<select id="cIdTipoMoneda" name="cIdTipoMoneda" style="width: 250px;" onChange="cambiarMoneda();" > </select> &nbsp;&nbsp;
					    				Tipo Cambio:<input type="text" id="mTipoCambio" name="mTipoCambio" value="1.0" style="width:50px;" onkeypress="return onlyMoney(event);" onchange="checkNaN(this,1.0); calcTotales();" disabled />
					    			</td>
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
						    		<tr>
						    			<td align="left">CUCOP:<select id="cIdCABM" name="cIdCABM" style="width: 400px;" ></select> &nbsp;&nbsp;&nbsp; <button id="agregaLnContrato" onclick="agregarLineaCUCOP();" >Agrega Linea CUCOP</button>
						    		</tr>
						    		<tr>
						    			<td style="width: 740px">
						    				<table id="tblPAAFonden" class="display" width="740px">
												<thead >
													<tr>
														<th align="center" width="5%" >#</th>
														<th align="center" width="10%" >&nbsp;&nbsp;CUCOP&nbsp;&nbsp;</th>
														<th align="center" width="20%" >&nbsp;&nbsp;&nbsp;Descripci&oacute;n CUCOP&nbsp;&nbsp;&nbsp;</th>
														<th align="center" width="35%" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Descripci&oacute;n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</th>
														<th align="center" width="10%" >Cantidad</th>
														<th align="center" width="10%" >Precio Unitario</th>
														<th align="center" width="10%" >Monto</th>
													</tr>										
												</thead>
											</table>
						    			</td>
						    		</tr>
						    		<tr>
						    			<td id="btnAgregaLineaContrato" align="left" > <button onclick="agregarLinea();" >Agrega Linea Programa Anual OTROS</button> </td>
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
									        			<th style="width: 300px">Descripci&oacute;n</th>
									        			<th>Cantidad</th>
									        			<th>Precio<br/>Unitario</th>
									        			<th>IVA</th>
									        			<th>Importe<br/>Neto</th>
									        			<th>Unidad de<br/>Medida</th>
									        			<th></th>
									        			<th></th>
									        			<th></th>
									        		</tr>
									        	</thead>
									        </table>
			    						</td>
			    					</tr>
			    					<tr>
			    						<td align="left"><button id="btnCalcTotalContrato" name="btnCalcTotalContrato" onclick="calcTotales();">Calcular Total</button><button id="btnEliminarLineasReqContrato" name="btnEliminarLineasReqContrato" onclick="eliminarLineas();">Eliminar Lineas</button></td>
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
<!-- 		    <input type="hidden" name="nIVA" id="nIVA"/> -->
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
			
			<!--INSERTA Linea Solicitud-->
			<input type="hidden" name="cIdPAF" id="cIdPAF" />
			<input type="hidden" name="cIdCabmPAF" id="cIdCabmPAF" />
			<input type="hidden" name="cDescPAF" id="cDescPAF" />
			<input type="hidden" name="nCantPAF" id="nCantPAF" />
			<input type="hidden" name="mPrecioPAF" id="mPrecioPAF" />
			<input type="hidden" name="mMontoPAF" id="mMontoPAF" />
			
			<!--INSERTA Solicitud-->
			<input type="hidden" name="consSolicitud" id="consSolicitud" />
			<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%=usuarioTab.getLogin()%>"/>
			<input type="hidden" name="cIdEntidadContable" id="cIdEntidadContable" value="<%=centroContable%>"/>
			
			<!--INSERTA Linea Solicitud-->
			<input type="hidden" name="cIdLS" id="cIdLS" />
			<input type="hidden" name="cIdCabmLS" id="cIdCabmLS" />
			<input type="hidden" name="cIdSPLS" id="cIdSPLS" />
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
			<input type="hidden" name="mTipoCambioPA" id="mTipoCambioPA" />
			<input type="hidden" name="cIdRfc" id="cIdRfc" />
			
			<!--INSERTA Procedimiento Adjudicacion Partidas-->
			<input type="hidden" name="cIdLC" id="cIdLC" />
			<input type="hidden" name="mMontoPAP" id="mMontoPAP" />
			<input type="hidden" name="cDescPAP" id="cDescPAP" />
			
			<!--CONSULTA -->
			<input type="hidden" name="consContrato" id="consContrato" />
			
	    </div>
	</form>
  </body>
</html>
