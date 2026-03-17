<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String roles="";
    Map<String, Role> rol =usuario.getRoles();
    String cIdDocumento = "";
    String cEjercicio = "";
    String cCentroContableUsr = "";
    String cIdUnidadEjecutora = "";
    Calendar c = Calendar.getInstance();
  	int mesActual = c.get(Calendar.MONTH)+1;
    
     if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContableUsr = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	if (session.getAttribute(GestionInterface.ATT_PagoDirectoFolio) != null) {
		cIdDocumento = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoFolio);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoEjercicio);
		cIdUnidadEjecutora = (String)session.getAttribute(GestionInterface.ATT_PagoDirectoUE);
	}else 
		response.sendRedirect("PagoDirecto.jsp?tab=1");
		
	boolean bGrupo=false;
	String grupoMat="";
	Map<String, Grupo> grupo=usuario.getGrupos();
	Iterator it2 = grupo.entrySet().iterator();
	while(it2.hasNext()){
		Map.Entry r = (Map.Entry)it2.next();
		grupoMat=(String)r.getKey();
		System.out.print(grupoMat);
		if("RECURSOS_MATERIALES_APTD".equalsIgnoreCase(grupoMat)){
			bGrupo=true;
			break;
		}
	
	}
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title></title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">
	<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">		
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="Consolidado">
	<link rel="shortcut icon" type="image/ico" href="../imagenes/favicon.ico" />
	<style type="text/css" title="currentStyle">
		@import "../themes/smoothness/jquery-ui-1.8.4.custom.css";
		@import "../css/demo_table_jui.css";
		@import "../css/demo_page.css";
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker-es.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" src="../js/validaciones.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" charset="utf-8">
		$(document).ready(function() {
			var oTablaApartadoEP;
			var oTablaSuficiencias;
			var oTablaGravaApartado;
			var oTablaGravaDetalle;
			var jqInputs;
			var totalEp;
			
			<%
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
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
				Map<String, String> botones=nb.getBotones(roles,"PagosDirectos","ApartadoPagoDirecto");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
			initDataTable_ApartadoEP();
			
			$("#btnAgregar").button().click(function(){
				if(validarExisteEP()){
					queryFormPost("pagoDirectoEPCreate",{async:false});
					$("#ep").val("");
					initDataTable_ApartadoEP();
				}
				//validar oli
				/* var vEp = $("#ep").val();
				var vCartera = vEp.substring(44, 55);
				var vCapitulo = vEp.substring(31, 32);
				if(vCartera == "00000000000" || "1234".indexOf(vCapitulo) >= 0){
					if(validarExisteEP()){
						queryFormPost("pagoDirectoEPCreate",{async:false});
						$("#ep").val("");
						initDataTable_ApartadoEP();
					}
				}else{
					$("#lblEp").val("La clave "+vEp+" necesita validación de Oli");
					$( "#dialog-validaOLI" ).dialog( "open" );
				} */
			});
			$("#btnEditar").button().click(function(){
				editTableEP( $("#tblApartadoEP").dataTable() ) ;
			});
			$("#btnGuardar").button().click(function(){
				guardarCeldasDetalle();
			});
			$("#tblApartadoEP tr").live("dblclick", function() {
				/* $(this).addClass("row_selected");   
				var anSelected = fnGetSelected( $("#tblApartadoEP").dataTable() );						
				var aData = $("#tblApartadoEP").dataTable().fnGetData(anSelected[0]);
				$("#epTmp").val(aData[0]);
				queryFormPost("pagoDirectoEPDelete", {async: false});
				initDataTable_ApartadoEP(); */
			});
			
			//valida oli
			$( "#dialog-validaOLI" ).dialog({
				autoOpen: false,
				height: 200,
				width: 640,
				modal: true,
				buttons: {
						"Aceptar": function() 
						{
							var vEP = $("#epTmp").val();
							var vCapitulo = "%" + vEP.substring(31, 36) + "%";
							var vCartera = vEP.substring(44, 55);
							var vUnidEj = vEP.substring(56, 59);
							var vUnidRe = vEP.substring(60, 64);
							var url = '../../proimpro/readInfo';
							$.ajax({
								url : url,
								dataType : 'json',
								data : {
									"accion" : "READ_IMPORTEOLI",
									"cartera" : vCartera,
									"capitulo" : vCapitulo,
									"oli": $("#cOLI").val(),
									"ue":vUnidEj
								},
								async : false,
								success : function(RS) {
									var exito = RS.success;
									//var exito="true";
									if(exito=="true"){
										var MontoOlis = RS.data_1.result; 
									//	var MontoOlis = 1000000000;
										if (Number( MontoOlis ) > 0){
											$("#cartera").val( vCartera );
											$("#capitulo").val( vEP.substring(31, 36) );
											$("#UE").val( vUnidEj );
											$("#mImporteCartera").val("0.00");

											queryFormPost("vAcumulaImporteCarteraEP", {async: false });
										
											var importeTotal = Number( $("#mImporteCartera").val() ) + Number( getTotalEP() );
											var importeTotal = importeTotal.toFixed(2);
											if( Number( importeTotal ) <= Number( MontoOlis ) ){
												agregaEp();
												initDataTable_ApartadoEP();
											}else{
												alert("El Importe Capturado sobregira la Cartera, la clave sera eliminada ");
												queryFormPost("pagoDirectoEPDelete", {async: false});
												initDataTable_ApartadoEP();
											}
										}else{
											alert("El Número de OLI no es Válido ");
										}
									}else{
										var msg = RS.data_1.result;
										alert("Error al intentar validar OLI:\n"+msg);
									}
								},
								error : function(xhr, textStatus, errorThrown) {
									alert("Advertencia: " + xhr.responseText + "\nEstatus: "
											+ textStatus + "\n" + errorThrown);
									r = true;
								}
							});
							$( this ).dialog( "close" );
						},
						"Cancelar": function() {
							breturnVal = false;
							$( this ).dialog( "close" );							
						}
					},
				close: function() {	

				}
			});
		});
		
		function init(){
			$("#mensajeProcesando").css("display","none");
			queryFormPost("EjercicioFiscalActvRead",{async:false});
			$("#cIdFolio").val("<%=cIdDocumento%>");
			$("#cIdUnidadEjecutora").val("<%=cIdUnidadEjecutora%>");			
			queryFormPost("mPagoDirectoRead",{async:false});
			queryFormPost("mPagoDirectoMontos",{async:false});
			queryFormPost("apartadoPagoDirectoAplicado",{async:false});
			queryFormPost("maximoPagoDirectoRead",{async:false});
			
			 queryFormPost("mPagoDirectoFacturas",{async:false});
			
			
			queryFormPost("validaPartidas", {async:false});
			
			if($("#totalPartidas").val()!=0){
				alert("Faltan partidas por calendarizar");
				window.location =  "PagoDirecto.jsp?tab=" + 5;
				
			}
			
			
			if($("#lblMotivoRechazo").val() != "" && $("#nIdEstado").val() == 5){
				$("#trMovitoRechazo").css("display","");
				$("#divMotivoRechazo").html($("#lblMotivoRechazo").val());
			}
			else
				$("#trMovitoRechazo").css("display","none");
				$("#lblImporteBruto").val($("#Imp_Bruto").val());
				$("#lblIVA").val($("#Imp_Iva").val());
				$("#lblImporteNeto").val($("#Imp_Neto").val());
				$("#lblImporteBruto").formatCurrency();
				$("#lblIVA").formatCurrency();
				$("#lblImporteNeto").formatCurrency();
				validaHabilitaCampos();
				cargaRetenciones();
			
			if($("#nIdEstado").val()==5){
				//actualizar el usuario
				queryFormPost("updateOperadorCaso", {async:false});
				//actualiza fechas
				queryFormPost("actualizaCasoDatoPagoDirectoFechas",{async:false});
				
				
			}
			
			$("#lblImporteFactura").formatCurrency();
			$("#lblImporteIvaFactura").formatCurrency();
			$("#lblImporteNetoFactura").formatCurrency();
			 
			//llenamos la tabla de mPagoDirectoMaterialesEP
			queryFormPost("mDeletePagoDirectoMaterialesEP",{async:false});
			queryFormPost("mInsertPagoDirectoMaterialesEP",{async:false});
			
			
			
		}
		
		
		function initDataTable_ApartadoEP(){			
			oTablaApartadoEP = $("#tblApartadoEP").dataTable({         
				bPaginate: false,
	   			bLengthChange: false,
	   			bFilter: false,
	   			bInfo: false,
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
				}},
	   			bAutoWidth: true,
				sScrollX: 100,
				sScrollY: 110,
		        bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mPagoDirectoMaterialesEP&qw=cIdDocumento='<%=cIdDocumento%>'",
				bProcessing: true,
				bLengthChange: false,
				iDisplayLength: 10,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "cClaveEP" }, 
					{ sName: "mes01" },
					{ sName: "mes02" },
					{ sName: "mes03" },
					{ sName: "mes04" },
					{ sName: "mes05" },
					{ sName: "mes06" },
					{ sName: "mes07" },
					{ sName: "mes08" },
					{ sName: "mes09" },
					{ sName: "mes10" },
					{ sName: "mes11" },
					{ sName: "mes12" }
				],
				fnInitComplete: function(oSettings, json) {
					initDataTable_Suficiencia();
					$("#montoPresupuesto").val(getTotalEP());
					$("#montoPresupuesto").formatCurrency();
					$("#montoSinPresupuesto").val(quitaFmt($("#lblImporteNetoFactura").val()) - quitaFmt($("#montoPresupuesto").val()));
					$("#montoSinPresupuesto").formatCurrency();
				}
			});
		}
		
		function initDataTable_Suficiencia(){
			oTablaSuficiencias = $("#tblSuficiencias").dataTable({
				bPaginate: false,
	   			bLengthChange: false,
	   			bFilter: false,
	   			bInfo: false,
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
				}},
	   			bAutoWidth: true,
				sScrollX: 100,
				sScrollY: 110,
		        bScrollCollapse: true,
				bJQueryUI: true,
				bDestroy : true,
				bServerSide: true,
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vSaldosAnuales&qw=(nCuentaP = '82106') and EP in ("+getEP()+")",
				bProcessing: true,
				bLengthChange: false,
				iDisplayLength: 10,
				sPaginationType: "full_numbers",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "EP" }, 
					{ sName: "MontoEnero" },
					{ sName: "MontoFebrero" },
					{ sName: "MontoMarzo" },
					{ sName: "MontoAbril" },
					{ sName: "MontoMayo" },
					{ sName: "MontoJunio" },
					{ sName: "MontoJulio" },
					{ sName: "MontoAgosto" },
					{ sName: "MontoSeptiembre" },
					{ sName: "MontoOctubre" },
					{ sName: "MontoNoviembre" },
					{ sName: "MontoDiciembre" }
				]
			});
		}
		
		function buscaClaveEP() {
			var pp = window.open("../MultiReporteGridSacel.jsp?cIdUsuarioCreacion=" + $("#cIdUsuarioCreacion").val() + "&cIdDocumento=<%=cIdDocumento%>" , 'MultiReporteGridSacel', 'status=1, width=900px, height=680px, left=100px');
			return false;
		}
		
		function validarExisteEP(){
			var res = true;
			
			if($("#ep").val() == ""){
				alert("Debe seleccionar una Clave Presupuestal.");
				res = false;
			}
			
			$("#epTmp").val($("#ep").val());
			queryFormPost("existeEPPagoDirecto",{async:false});
			
			if($("#existeEPPagoDirecto").val() > 0){
				alert("Ya existe la Clave Presupuestal en el documento.");
				res = false;
			}
			
			return res;
		}
		
		function guardarCeldasDetalle(){
			var aTrs = $("#tblApartadoEP").dataTable().fnGetNodes(); 
			if(getTotalEP() <= parseFloat(quitaFmt($("#lblImporteNetoFactura").val()))){
				for ( var i=0 ; i<aTrs.length ; i++ ) {
					resetValueMeses();
					var aData = $("#tblApartadoEP").dataTable().fnGetData( i );
					jqInputs = $("input", aTrs[i] );
					
					$("#epTmp").val(aData[0]);

					var vEp = $("#epTmp").val();
					var vCartera = vEp.substring(44, 55);
					var vCapitulo = vEp.substring(31, 32);
					/* totalEp=0.0;
					for (var j=0 ; j < jqInputs.length ; j++ ){
						var vimporteP = jqInputs[ j ].value ;
						vimporteP = quitaFmt(vimporteP);
					
						if (parseFloat(vimporteP) > 0 ) {
							totalEp=Number(totalEp)+Number(vimporteP);
						}
					}
					alert(vCartera); */
					if(vCartera == "00000000000" || "1234".indexOf(vCapitulo) >= 0){
						agregaEp();
						
					}else{
						$("#lblEp").val("La clave "+vEp+" necesita validación de Oli");
						$( "#dialog-validaOLI" ).dialog( "open" );
					}	
					
					/* for (var j=0 ; j < jqInputs.length ; j++ ){
						var vimporteP = jqInputs[ j ].value ;
						vimporteP = quitaFmt(vimporteP);
					
						if (parseFloat(vimporteP) > 0 ) {
							if((j+1)<10)
								$("#mes0"+(j+1)).val(vimporteP);
							else
								$("#mes"+(j+1)).val(vimporteP);
							queryFormPost("pagoDirectoEPUpdate",{async:false});
						}
					} */
				}
				initDataTable_ApartadoEP();
				return true;
			}
			else{
				alert("El monto total de las Claves Presupuestales sobrepasa el monto total de "+$("#modulo").val()+".");
				return false;
			}
		}
		
		function agregaEp(){
			var aux=jqInputs;
			for (var j=0 ; j < aux.length ; j++ ){
				var vimporteP = aux[ j ].value ;
				vimporteP = quitaFmt(vimporteP);
				if (parseFloat(vimporteP) > 0 ) {
					if((j+1)<10)
						$("#mes0"+(j+1)).val(vimporteP);
					else
						$("#mes"+(j+1)).val(vimporteP);
					queryFormPost("pagoDirectoEPUpdate",{async:false});
				}
			}
		
		}
		function editTableEP(oTable){
			var aTrs = oTable.fnGetNodes();
			var aData;
			var mesActual = <%=mesActual%>;
			
			for (var i=1 ; i<=aTrs.length ; i++ ){
			    aData = oTable.fnGetData(i-1);
			    
			    for(var j=1; j<=mesActual; j++){
			   		$("#tblApartadoEP").children().children()[i].children[j].innerHTML = '<input style="width: 80%" type="text" id="' + 'mes'+j+'-'+i + '" name="'+ 'mes'+j+'-'+i +'" onchange="valSufic(this,'+i+','+j+')"  onfocus="Sinfrmt(this)" onblur="cambiafrmt(this)" value="'+aData[j]+'" onKeyPress="return(onlyNumbers(event))">';
			    }
		    }
		}
		
		function onlyNumbers(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			var strCheck = '-0123456789.';
			var key = String.fromCharCode( keyPressed );
			if (strCheck.indexOf( key ) == -1)
				return false; // Valida que sea numero y punto decimal
			return true;
		}
		
		function valSufic( obj, row, col ) {
			var aTrs = $("#tblSuficiencias").dataTable().fnGetNodes();
			var aData;
			var mesActual = <%=mesActual%>;
			
			if(getTotalEP() <= parseFloat(quitaFmt($("#lblImporteNetoFactura").val()))){
				for (var i=0 ; i < aTrs.length ; i++ ){
					aData = $("#tblSuficiencias").dataTable().fnGetData(i);
					
					if((row-1) == i){
						for(var j=1; j <= mesActual; j++){
							if(col == j){
								if(parseFloat(quitaFmt($("#"+obj.id).val())) > parseFloat(aData[j])){
									alert("El importe supera la suficiencia de la Clave Presupuestal.");
									$("#"+obj.id).val("0.00");
									$("#"+obj.id).formatCurrency();
								}
								else{
									$("#montoPresupuesto").val(getTotalEP());
									$("#montoPresupuesto").formatCurrency();
									$("#montoSinPresupuesto").val(quitaFmt($("#lblImporteNetoFactura").val()) - quitaFmt($("#montoPresupuesto").val()));
									$("#montoSinPresupuesto").formatCurrency();
								}
							}
						}
					}				
				}
			}
			else{
				alert("El monto total de las Claves Presupuestales sobrepasa el monto total de "+$("#modulo").val()+".");
				$("#"+obj.id).val("0.00");
				$("#"+obj.id).formatCurrency();
			}
		}
		
		function Sinfrmt( fld )	{
			var valcol = fld.value ;
		   	var vcompr = $("#mImporteTmp").val();
		   	vcompr = quitaFmt( vcompr );
		   	valcol = quitaFmt( valcol );
			$("#" + fld.id).val( valcol );
		   	fld.select();
			$("#mImporteTmp").val( parseFloat( vcompr ) - parseFloat( valcol ) );
			$("#mImporteTmp").formatCurrency();
		}
		
		function cambiafrmt( fld )	{
		   	var vcompr = $("#mImporteTmp").val();
		   	var vfld = $("#" + fld.id).val();
		   	if (vfld == "")
		   		vfld = '0';
			vcompr = quitaFmt( vcompr );
			vfld=quitaFmt(vfld);
		   	$("#mImporteTmp").val( parseFloat(vcompr) + parseFloat( vfld ) );
			$("#" + fld.id).formatCurrency();
			$("#mImporteTmp").formatCurrency();
		}
		
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	val = val.replace(",", "");
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}
		
		function getEP(){
			var aTrs = $("#tblApartadoEP").dataTable().fnGetNodes();
			var aData;
			var eps = "";
			
			for (var i=0 ; i<aTrs.length ; i++ ){
				aData = $("#tblApartadoEP").dataTable().fnGetData(i);
				eps += aData[0]+"','";
			}
			
			if(eps!=""){
				eps = eps.substring(0,eps.length-2);
				eps = "'"+eps;
			}
			else
				eps = "''";
			
			return eps;
		}
		
		function validaHabilitaCampos(){
			var roles = "<%=roles%>";
			
			if (($("#nIdEstado").val() <= 2 || $("#nIdEstado").val() == 5)){	
				if(roles.indexOf("ADMIN_RECMAT") < 0 && $("#cIdUsuario").val() != $("#cIdUsuarioCreacion").val()){
					$("#tblApartadoEP").attr("disabled", true);
					$("#nIdClaveEP").attr("disabled", true);
					$("#btnAgregar").attr("disabled", true);
					$("#btnEditar").attr("disabled", true);
					$("#btnGuardar").attr("disabled", true);
					$("#imgApartar").attr("disabled", true);
					$("#imgDevolver").attr("disabled", true);
				}
				else{
					if($("#apartadoAplicado").val() > 0){
						$("#tblApartadoEP").attr("disabled", true);
						$("#nIdClaveEP").attr("disabled", true);
						$("#btnAgregar").attr("disabled", true);
						$("#btnEditar").attr("disabled", true);
						$("#btnGuardar").attr("disabled", true);
					}
					else{
						$("#imgDevolver").attr("disabled", true);
					}
				}
			}
			else{
				if($("#nIdEstado").val() == 3 ){
					if(roles.indexOf("ADMIN_RECMAT") < 0 && $("#cIdUsuario").val() != $("#cIdUsuarioCreacion").val())
				$("#imgDevolver").attr("disabled", true);
				}
				$("#tblApartadoEP").attr("disabled", true);
				$("#nIdClaveEP").attr("disabled", true);
				$("#btnAgregar").attr("disabled", true);
				$("#btnEditar").attr("disabled", true);
				$("#btnGuardar").attr("disabled", true);
				$("#imgApartar").attr("disabled", true);
			}
		}
		
		function getTotalEP(){
			var aTrs = $("#tblApartadoEP").dataTable().fnGetNodes();
			var aData;
			var total = 0;
			var mesActual = 12;
			
			for ( var i=0 ; i<aTrs.length ; i++ ) {
				var aData = $("#tblApartadoEP").dataTable().fnGetData( i );
				var jqInputs = $("input", aTrs[i] );
				
				if(jqInputs.length > 0){
					for (var j=0 ; j < jqInputs.length ; j++ ) {						
						if (parseFloat(quitaFmt(jqInputs[ j ].value)) > 0 )
							total += parseFloat(quitaFmt(jqInputs[ j ].value));
					}
				}
				else{
					for(var j=1; j <= mesActual;j++)
						total += parseFloat(quitaFmt(aData[j]));
				}
			}
			
			return total.toFixed(2);
			
		}
		
		
		
		        
	   // SE EJECUTA AJAX RECIBIENDO COMO PARAMETRO TIPOS DE DOCUMENTO (PAGODIRECTO, RELACIONGASTOS) Y NUMERO DE FOLIO DEL PAGO
function creaCasoComsoc(tipoDocumento, folioDocumentoPago){
	
	$.ajax({
		url : '../../servlet/ComsocAutorizacionServlet',
		dataType : 'json',
		type :"POST",
		data : {
			"h_TipoPago" : tipoDocumento,
			"hFolioPago": folioDocumentoPago
		},
		async : false,
		success : function(json) {
			var exito = json.success;
			if( exito == "true"){
				//alert("La relacion de Gastos tiene partida(s) de COMSOC, El Pago estára en Proceso de Autorización por parte del Área Normativa");
			}
		},
		error : function(xhr, textStatus, errorThrown) {
			alert("Advertencia: " + xhr.responseText + "\nEstatus: "
					+ textStatus + "\n" + errorThrown);
			return false;
		}
	});
	
}     

		
		function fnGetSelected( oTableLocal ){
			var aReturn = new Array();
			var aTrs = oTableLocal.fnGetNodes();			
			for ( var i=0 ; i<aTrs.length ; i++ ){
				if ( $(aTrs[i]).hasClass('row_selected') ){
					aReturn.push( aTrs[i] );
				}
			}
			return aReturn;
		}
		
		function resetValueMeses(){
			$("#mes01").val("0");
			$("#mes02").val("0");
			$("#mes03").val("0");
			$("#mes04").val("0");
			$("#mes05").val("0");
			$("#mes06").val("0");
			$("#mes07").val("0");
			$("#mes08").val("0");
			$("#mes09").val("0");
			$("#mes10").val("0");
			$("#mes11").val("0");
			$("#mes12").val("0");
		}
		
		function apartar(){
			var grupo=<%=bGrupo%>;
			if(grupo){
				if(quitaFmt($("#montoSinPresupuesto").val()) > 0){
					alert("Aun no se ha capturado el total del "+$("#modulo").val()+" en las EP's.");
				}
				else{
					//Valida el tope por Partida
					queryFormPost("montoUnidadEjecutoraPartidaPagoDirecto2",{async:false});
					if($("#partidasSobreTope").val() > 0){
						cambiafrmt(document.getElementById("mImporteMaximo"));
						if(!confirm("El monto total de Pagos Directos por partida supera el Monto M\xE1ximo ("+$("#mImporteMaximo").val()+"). \xBFDesea Continuar?.")){
							Sinfrmt(document.getElementById("mImporteMaximo"));
							return;
						}
					}
					//Valida el tope por proveedor
					Sinfrmt(document.getElementById("mImporteMaximo"));
					Sinfrmt(document.getElementById("montoUEProveedor"));
					queryFormPost("montoUnidadEjecutoraProveedorPagoDirecto",{async:false});
					if(parseFloat($("#montoUEProveedor").val()) > parseFloat($("#mImporteMaximo").val())){
						cambiafrmt(document.getElementById("mImporteMaximo"));
						if(!confirm("El monto total de Pagos Directos para \xE9ste Proveedor supera el Monto M\xE1ximo ("+$("#mImporteMaximo").val()+"). \xBFDesea Continuar?.")){
							Sinfrmt(document.getElementById("mImporteMaximo"));
							return;
						}					
					}
					
			
					 //revisa que las partidas ingresadas sean de comsoc
				    queryFormPost("checaPartidasCOMSOCMaterialesPAGODIRECTO",{async:false});
				   	 if(parseInt($("#partidaCOMSOC").val(),10) > 0 ){
				     //existe una partida de comsoc
				     creaCasoComsoc($("#TIPO_DOCTO").val(), $("#nFolioPagoDirecto").val());	
				    			     
				     }
			
					
					//if(!confirm("\xBFEst\xE1s seguro de enviar el apartado a ventanilla?."))
					//	return;				
					
					queryFormPost("apartadoPagoDirectoAplicado",{async:false});
					if($("#apartadoAplicado").val() == 0){
						if(guardarCeldasDetalle()){
							//Obtiene los folios de los casos
							$("#mensajeProcesando").css("display","");
							queryFormPost("mPagoDirectoFolios",{async:false});
							if ($("#idCaso").val()!='' && $("#folioCasoApartado").val()!='' && $("#folio").val()!=''){
								if($("#cCentroContableUsr").val() == "10")
									getNextSequenceVal({seqName: "CT-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceValCT});
								else
									getNextSequenceVal({seqName: "CR-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceVal});
									
								getNextSequenceVal({seqName: "APARTADO", async: false, callback: setSequenceAptd}); 
								$("#folioCasoPagoDirecto").val("PDIR-"+$("#cIdUnidadEjecutora").val()+"-"+$("#folio").val());
								$("#Imp_Bruto").val(quitaFmt($("#lblImporteFactura").val()));
								queryFormPost("tPagoDirectoEncabezadoMCreate",{async:false}); 
								cargaClavesApartado();
								queryFormPost("tPagoDirectoFinanciamientoMCreate",{async:false});
								//queryFormPost("tDocumentacionComprobatoriaDetPMCreate",{async:false});
								//queryFormPost("tContrareciboPagoDirectoMCreate",{async:false});
								queryFormPost("tPDApartadoEncabezadoCreate",{async:false});
								queryFormPost("tPDApartadoDetalleCreate",{async:false});
								queryFormPost("datosFoliosPagoDirectoUpdate",{async:false});
															
								
								cambiafrmt(document.getElementById("mImporteMaximo")); 
								 $.ajax({url: "../../servlet/PagoDirectoServlet?TIPO_DOCTO="+$("#TIPO_DOCTO").val()+"&nFolioApartado="+$("#nFolioApartado").val()+"&mImporteMaximo="+$("#mImporteMaximo").val() , type:'get' , async: false,data: 'operacion=1', dataType: 'json', success: 
									function(l){
										alert(l[0].mensaje);
										if(l[0].resp == "true"){
											if(l[0].mensajeMail != "" && l[0].mensajeMail != undefined)
												alert(l[0].mensajeMail);
											$("#nIdEstado").val("3");
											queryFormPost("estatusPagoDirectoUpdate",{async:false});
											queryFormPost("motivoRechazoPagoDirectoUpdate",{async:false});
											queryFormPost("pa_insertatDocumentacionComprobatoriaContrarecibo",{async:false});
											$("#mensajeProcesando").css("display","none");
											location.reload();	
										}else
													$("#mensajeProcesando").css("display","none");
									}
								});
							}else
								alert("Ocurri\xF3 un error al iniciar el proceso.");
						
						}
					}else{
					
						//Si ya existe apartado genera un nuevo contrarecibo y lo actualiza en el apartado que ya existe
						
						$("#caNoContrarreciboTmp").val($("#caNoContrarrecibo").val());
						if($("#cCentroContableUsr").val() == "10")
							getNextSequenceVal({seqName: "CT-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceValCT});
						else
							getNextSequenceVal({seqName: "CR-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceVal});
						//borra tabla pago directo retencion
					//	queryFormPost("deletePagoDirectoRetencion",{async:false});
						queryFormPost("contrareciboPagoDirectoEncabezadoUpdate",{async:false});
						queryFormPost("tPagoDirectoDetalleMUpdate",{async:false});
						queryFormPost("updateMontoRentencion", {async:false});
						queryFormPost("DocumentacionComprobatoriaDetUpdatePD",{async:false});
						queryFormPost("contrareciboPagoApartadoEncabezadoUpdate",{async:false});
						//queryFormPost("tContrareciboPagoDirectoMCreate",{async:false});
						
						queryFormPost("updatetContrarrecibo",{async:false});
						
						$("#nIdEstado").val("3");
						queryFormPost("estatusPagoDirectoUpdate",{async:false});
						queryFormPost("motivoRechazoPagoDirectoUpdate",{async:false});
						queryFormPost("avanzaCasoPagoDirecto",{async:false});
						alert("Se envi\xF3 el documento "+$("#cIdFolio").val()+" a Ventanilla.");
						location.reload();
					}
				}
			}else{
				alert("No es posible realizar el apartado, ya que el módulo de financiero se encuentra cerrado");
			}
		}
		
		/* function setSequenceValCT(seqValue){
			seqValue = "000000" + seqValue;
			seqValue = seqValue.substr(seqValue.length - 6);
			seqValue = $("#cCentroContableUsr").val() + "CT" + $("#cEjercicio").val() + seqValue;
			$("#caNoContrarrecibo").val( seqValue );
		} */
		
	
			
		function setSequenceValCT(seqValue) {
			seqValue = "000000" + seqValue;
			seqValue = seqValue.substr(seqValue.length - 6);
			seqValue = $("#cCentroContableUsr").val() + "CT" + $("#cEjercicio").val() + seqValue;
			$("#caNoContrarrecibo").val( seqValue );
		}
		
		function setSequenceVal(seqValue) {
			seqValue = "000000" + seqValue;
			seqValue = seqValue.substr(seqValue.length - 6);
			seqValue = $("#cCentroContableUsr").val() + "CR" + $("#cEjercicio").val() + seqValue;
			$("#caNoContrarrecibo").val( seqValue );
		}	
		
		
		
		function setSequenceAptd(seqValue) {
			$("#nFolioApartado").val( seqValue );
		}
		
		function devolverApartado(){
			var grupo=<%=bGrupo%>;
			if(grupo){	
			
				if(parseInt($("#nIdEstado").val(),10)==1 || parseInt($("#nIdEstado").val(),10)==4 || parseInt($("#nIdEstado").val(),10)==6 || parseInt($("#nIdEstado").val(),10)==7 ){
				return;
				}
						
				if(!confirm("\xBFEst\xE1s seguro de devolver el Pago Directo?.")){
					return;
				}
				
				
				
				 //revisa que las partidas ingresadas sean de comsoc
				    queryFormPost("checaPartidasCOMSOCMaterialesPAGODIRECTO",{async:false});
				   	 if(parseInt($("#partidaCOMSOC").val(),10) > 0 ){
					     //existe una partida de comsoc
					   	//primero checamos si el pedido ya esta siendo trabajado por el usuario de ventanilla
							queryFormPost("usuarioVentanillaReadPAGODIRECTOCOMSOC", {async: false});
						if(parseInt($("#operacionCOMSOC").val(),10)== 1 && $("#responsableCOMSOC").val()!="CAPTURISTA_AUTORIZACOMSOC")
						{
							alert("No se puede devolver el Pago Directo esta en un proceso de validacion del COMSOC, el Pago Directo la esta revisando el usuario de ventanilla "+ ' '+$("#responsableCOMSOC").val());
							return;
						}		 
					    			     
				     }
				
					//primero checamos si el pedido ya esta siendo trabajado por el usuario de ventanilla
					queryFormPost("usuarioVentanillaReadPagoDirecto", {async: false});
					if(parseInt($("#operacion").val(),10)== 2 && $("#responsable").val()!="AUTORIZA_PAGODIRECTO")
					{
						alert("No se puede eliminar el Pago Directo, el Pago lo esta revisando el usuario de ventanilla "+ ' '+$("#responsable").val());
						return;
					}		 
						
				
				
				queryFormPost("apartadoPagoDirectoAplicado",{async:false});
				
				if($("#apartadoAplicado").val() == 0){
					//Solo actualiza el estatus del documento
					$("#nIdEstado").val("1");
					$("#folioCasoPagoDirecto").val("");
					$("#folio").val("0");
					$("#nFolioApartado").val("0");
					queryFormPost("estatusPagoDirectoUpdate",{async:false});
					queryFormPost("motivoRechazoPagoDirectoUpdate",{async:false});
					queryFormPost("datosFoliosPagoDirectoUpdate",{async:false});
					queryFormPost("updateOperadorCaso",{async:false});
					
					location.reload();
				}else{
					queryFormPost("responsablePagoDirectoRead",{async:false});
					//Valida si estan revisando el documento.
	//				if($("#CO_RESPONSABLE").val() == "AUTORIZA_PAGODIRECTO"){
					if($("#CO_RESPONSABLE").val() == "CONSULTA_PAGODIRECTO" || $("#CO_RESPONSABLE").val() == "AUTORIZA_PAGODIRECTO"){
						$("#mensajeProcesando").css("display","");
										
						$.ajax({url: "../../servlet/PagoDirectoServlet?TIPO_DOCTO="+$("#TIPO_DOCTO").val()+"&operacion=2&nFolioApartado="+$("#nFolioApartado").val() , type: 'get' , async: false, dataType: 'json', success:
							function(j){
								$("#mensajeProcesando").css("display","none");
								alert(j[0].mensaje);
								location.reload();
							}
						});
					}
					else
						alert("No se puede devolver el Pago Directo ya que lo est\xE1 revisando Ventanilla.");
				}
			}else{
				alert("No es posible devolver el Apartado, ya que el modulo de financiero se encuentra cerrado");
			}
		}
		
		function cmdImprimir() {
		
		var elFormato="";
		queryFormPost("obtieneContrareciboPAGODIRECTOMATImprimir",{async:false});
		var swhere = "&folio=" + $("#caNoContrarreciboImprimir").val();
	
		if($("#cCentroContableUsr").val()== "10"){
			elFormato="ComprobanteRegistro";
			}else
			elFormato="Contrarecibo";
		
	
	if(elFormato == "ComprobanteRegistro"){
		swhere = "&whereFolio= and CR.caNocontrarrecibo = '" + $("#caNoContrarreciboImprimir").val() + "'";
	}
	window.open("../../admin/SeguridadCatalogos?" + "catalogo=CONTRARECIBO"
			+ "&accion=run" + "&rn=" + elFormato + ".jasper" + swhere,
			"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
	setTimeout('anexo("' + elFormato + '")', 3000);
	
		/*
			var elFormato = "";
			
			if($("#cCentroContable").val() == "10")
				elFormato = "ComprobanteRegistro";
			else
				elFormato = "Contrarecibo";
			
			window.open("../../admin/SeguridadCatalogos?" + "catalogo=CONTRARECIBO"
					+ "&accion=run" + "&rn=" + elFormato + ".jasper" + "&folio="+$("#caNoContrarrecibo").val(),
					"popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");	

				*/
			}
		
		function calculaMontos(){
			var montoBruto=0.0;  // en clave
			var montoEnClave=0.0; //capturado por el usuario
			var ivaDocumento=0.0; 
			var mImporteIva=0.0;
			
			var  mRetencion=0.0; //suma de todas las retenciones del documento
			
			var mImporteNeto=0.0; // montoEnClave-mRetencion
			
			var m2Millar=0.0;
			var mISRHonorarios=0.0;
			var mObra5=0.0;
			var mImporteFlete4=0.0;
			var mISRArrenda=0.0;
			var mRetImpuestoCedular=0.0;
			var m23IVA=0.0;
			var penalizaciones=0.0;
			
			var clave="";
			var mes=0;
			ivaDocumento=$("#nPorcentajeIva").val();
			
			var mesPago="";
			
			var oTableLocal=$('#grdclavesMes').dataTable();
			var aTrs = oTableLocal.fnGetNodes();
			for (var i=0 ; i<aTrs.length ; i++ ){
			    var aData = oTableLocal.fnGetData(i);	   
				montoEnClave=aData[13];
				clave=aData[8];
				mes=aData[14];
			    $("#mesPago").val(mes);
			    $("#epClave").val(clave);
			    $("#cIdAlmacen").val(aData[2]);
			     $("#altaAlmacenaria").val(aData[3]);
			    //monto calendarizado para cada ep
			    montoBruto=Number(montoEnClave)/(1+Number(ivaDocumento));
			    montoBruto=montoBruto.toFixed(2);
			    
			    mImporteIva=Number(montoEnClave)-montoBruto;
			    mImporteIva=mImporteIva.toFixed(2);
			  
			    // recorrer la tabla de las retenciones
			    var oTableRetenciones=$('#grdRetencion').dataTable();
			    var aTrsRetenciones = oTableRetenciones.fnGetNodes();
			    $("#mIMDT").val("0");
				$("#mCNIC").val("0");
			    if(aTrsRetenciones.length>0){
				    for (var x=0 ; x<aTrsRetenciones.length ; x++ ){
				    	var aDataRetenciones = oTableRetenciones.fnGetData(x);
				    	var tipoRetencion=parseInt(aDataRetenciones[0],10);
				    	var porcentaje=aDataRetenciones[2];
				    	switch(tipoRetencion){
					    	case 0: // 23 del importe del iva
					    		m23IVA = (mImporteIva* 2) /3 ;
	       		     			m23IVA = m23IVA.toFixed(2);
	       		     			mRetencion=Number(mRetencion)+Number(m23IVA);
					    	break;
					    	
					    	case 2: // APORTE C.N.I.C. ( 0.2%)
					    		m2Millar=montoBruto*porcentaje;
					    		m2Millar = m2Millar.toFixed(2);
					    		mRetencion=Number(mRetencion)+Number(m2Millar);
					    		///////////////////////////////////////////////////
					    		
								queryFormPost("tipoAporteCNICREad", {async:false});
								
								if ($("#tipoAporteCNIC").val() != 0){//camara
									$("#mCNIC").val(m2Millar);
									$("#mIMDT").val("0");
								}
								if ($("#tipoAporteCNIC").val() != 1){//instituto
									$("#mCNIC").val("0");
									$("#mIMDT").val(m2Millar);
								}
								
					    	break; 
					    		
					    	case 3: // INSPECCION DE OBRA (0.5%)
					    		mObra5=montoBruto*porcentaje;
					    		mObra5 = mObra5.toFixed(2);
					    		mRetencion=Number(mRetencion)+Number(mObra5);
					    	break;
					    	case 4: // I.S.R. (HONORARIOS)
					    		mISRHonorarios=montoBruto*porcentaje;
					    		mISRHonorarios = mISRHonorarios.toFixed(2);
					    		mRetencion=Number(mRetencion)+Number(mISRHonorarios);
					    	break;
					    	case 5: // FLETES (4.0%)
					    		mImporteFlete4=montoBruto*porcentaje;
					    		mImporteFlete4 = mImporteFlete4.toFixed(2);
					    		mRetencion=Number(mRetencion)+Number(mImporteFlete4);
					    	break;
					    	case 6: // I.S.R. (ARRENDAMIENTOS)
					    		mISRArrenda=montoBruto*porcentaje;
					    		mISRArrenda = mISRArrenda.toFixed(2);
					    		mRetencion=Number(mRetencion)+Number(mISRArrenda);
					    	break;
					    	
					    	
					    	case 7: // PENALIZACIONES
					    		penalizaciones=montoBruto*porcentaje;
					    		penalizaciones = penalizaciones.toFixed(2);
					    		mRetencion=Number(mRetencion)+Number(penalizaciones);
					    	break;
					    	
					    	
					    	case 9: // IMPUESTO CEDULAR
					    		mRetImpuestoCedular=montoBruto*porcentaje;
					    		mRetImpuestoCedular = mRetImpuestoCedular.toFixed(2);
					    		mRetencion=Number(mRetencion)+Number(mRetImpuestoCedular);
					    	break;
				    	}
				    	mRetencion = mRetencion.toFixed(2);
				    	mImporteNeto=Number(montoEnClave)-Number(mRetencion);
				    	mImporteNeto = mImporteNeto.toFixed(2);
				    	$("#m23IVA").val(m23IVA);
				    	$("#m2Millar").val(m2Millar);
				    	$("#mObra5").val(mObra5);
				    	$("#mISRHonorarios").val(mISRHonorarios);
				    	$("#mImporteFlete4").val(mImporteFlete4);
				    	$("#mISRArrenda").val(mISRArrenda);
				    	$("#mRetImpuestoCedular").val(mRetImpuestoCedular);
				    	$("#mRetencion").val(mRetencion);
				    	$("#montoBruto").val(montoBruto);
				    	$("#mImporteIva").val(mImporteIva);
				    	$("#mImporteNeto").val(mImporteNeto);
				    	$("#penalizaciones").val(penalizaciones);
				    	
				    	
				    	
				    }
				    queryFormPost("tPagoDirectoDetalleMCreate",{async:false});
				    
				    
				  
				    
			    }else{
			    
			    	$("#m23IVA").val(0);
			    	$("#m2Millar").val(0);
			    	$("#mObra5").val(0);
			    	$("#mISRHonorarios").val(0);
			    	$("#mImporteFlete4").val(0);
			    	$("#mISRArrenda").val(0);
			    	$("#mRetImpuestoCedular").val(0);
			    	$("#penalizaciones").val(0);
			    	$("#mRetencion").val(0);
			    	$("#montoBruto").val(montoBruto);
			    	$("#mImporteIva").val(mImporteIva);
			    	$("#mImporteNeto").val(montoEnClave);
			    	queryFormPost("tPagoDirectoDetalleMCreate",{async:false});
			    }
			    montoEnClave=0.0;
			    mRetencion=0.0;
		    }
		    
		    queryFormPost("updateMontoRentencion", {async:false});
			
		   
		}
		
		function cargaRetenciones(){
		 szWhere=" nFolioPagoDirecto="+$("#nFolioPagoDirecto").val();
			 $("#grdRetencion").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		sScrollY : "100",
				sScrollX: "300",
				sScrollXInner: "700",
				
				bAutoWith: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mRetencionesPagoDirecto&qw="+szWhere,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [{ sName: "codigo" },{ sName: "retencion" },{ sName: "porcentaje" }]
			} );
		}
		
		function cargaClavesApartado(){
		 szWhere=$("#cIdFolio").val();
			 $("#grdclavesMes").dataTable({
				bScrollCollapse: true,
        		bInfo: false,
        		sScrollY : "100",
				sScrollX: "300",
				sScrollXInner: "700",
				 iDisplayLength: 80,
				bAutoWith: false,
				bJQueryUI: true,
				bRetrive : true,
				bDestroy : true,
				sPaginationType: "full_numbers",
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_mClavesPorMes('"+szWhere+"')",
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{ sName: "ID_TIPO_MOVIMIENTO" },{ sName: "ID_TIPO_CONCEPTO" },
					{ sName: "cIdAlmacen" },{ sName: "cAltaAlmacenaria" },
					{ sName: "cIdRFC" },{ sName: "cEjercicio" },
					{ sName: "cIdDocumento" },{ sName: "cIdUnidadEjecutora" },
					{ sName: "cClaveEP" },{ sName: "cCentroContable" },
					{ sName: "nPorcentajeIVA" },{ sName: "cEVTO" },
					{ sName: "montoNeto" },{ sName: "importe" },{ sName: "mes" }],
				fnInitComplete: function(oSettings, json) {
				
					calculaMontos();
				}
			} );
		}
	</script>
</head> 
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0"	topmargin="0">
<form>
<div id="container" class="container" style="text-align: left">
	<table>
		<tr>
			<td>
				<fieldset style="width: 750px">
					<legend>Datos Pago Directo</legend>
					<table align="left" cellpadding="2" width="100%">
						<tr align="left">
							<td>
								<input type="text" id="lblUnidadEjecutora" name="lblUnidadEjecutora" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								No. Folio:&nbsp;&nbsp;&nbsp;&nbsp;<input type="text" name="lblFolio" id="lblFolio" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="text" id="lblEstado" name="lblEstado" style="border-width:0; background-color:transparent;width: 400px" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Fecha Recepci&oacute;n:<input type="text" id="lblFechaRecepcion" name="lblFechaRecepcion" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe Bruto en facturas:<input type="text" id="lblImporteFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								Importe IVA en facturas:<input type="text" id="lblImporteIvaFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr> 
						<tr align="left">
							<td>
								Importe Neto en facturas:<input type="text" id="lblImporteNetoFactura" name="lblImporteFactura" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblImporteBruto" name="lblImporteBruto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblIVA" name="lblIVA" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr align="left">
							<td>
								<input type="hidden" id="lblImporteNeto" name="lblImporteNeto" style="border-width:0; background-color:transparent;width: 400px;" readonly/>
							</td>
						</tr>
						<tr id="trMovitoRechazo" style="color:#EC1B0F;">
							<td align="left">
								<table>
									<tr>
										<td>
											Motivo Rechazo:
										</td>
										<td>
											<div id="divMotivoRechazo"></div>
											<input type="hidden" id="lblMotivoRechazo" name="lblMotivoRechazo" readonly/>	
										</td>
									</tr>
								</table>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
		<tr>
			<td>
				<fieldset style="width: 750px">
					<legend>
						Presupuesto de Apartado
					</legend>
					<table>
						<tr>
							<td style="width:740px">
								<table>
									<tr>
										<td align="left" style="width:300px;">
											<div id="mensajeProcesando" style="border: 0px solid;color:#FF0000;">Procesando Contablemente espere...</div>
										</td>
										<td align="right" style="width:440px;">
											<img id="imgContrarecibo" src="../../imagenes/icono_PDF.jpg" style="cursor: pointer" onclick="cmdImprimir();" />&nbsp;Reporte Pago Directo
											<img id="imgDevolver" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devolverApartado();" />&nbsp;Devolver
											<img id="imgApartar" src="../imagenes/accept_green.png" style="cursor: pointer" onClick="apartar()"/>&nbsp;Apartar
										</td>
									</tr>
								</table>								
							</td>
						</tr>
						<tr>
							<td>
								<table>
									<tr>
										<td>
											Monto Presupuestado:
										</td>
										<td>
											<table>
												<tr>
													<td>
														<input type="text" name="montoPresupuesto" id="montoPresupuesto" style="text-align:right;" size="10" value="0" disabled/>
													</td>
													<td>
														&nbsp;&nbsp;
													</td>
													<td>
														Monto Sin Presupuestado:
													</td>
													<td>
														<input type="text" name="montoSinPresupuesto" id="montoSinPresupuesto" style="text-align:right;" size="10" value="0" disabled/>
													</td>
												</tr>
											</table>
										</td>
									</tr>
									<tr style="display:none">
										<td>
											Clave:
										</td>
										<td>
											<input type="text" name="ep" id="ep" size="65" disabled/>
											<input type="button" name="nIdClaveEP" id="nIdClaveEP" size="5"	value="..." onclick="buscaClaveEP();" onblur="">
										</td>
									</tr>
									<tr style="display:none">
										<td colspan="2" align="left">
											<input type="button" name="btnAgregar" id="btnAgregar" size="5" value="Agregar" class="btnInterfaceBG"/>
											<input type="button" name="btnEditar" id="btnEditar" size="5" value="Editar" class="btnInterfaceBG"/>
											<input type="button" name="btnGuardar" id="btnGuardar" size="5" value="Guardar" class="btnInterfaceBG"/>
										</td>
									</tr>
									<tr>
										<td>
											&nbsp;
										</td>
									</tr>
								</table>
							</td>
						</tr>
						<tr>
							<td align="left" style="width: 740px">
								<table id="tblApartadoEP" class="display" style="width: 740px">
									<thead>
										<tr>
											<th>
												EP
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
								</table>
							</td>
						</tr>
						<tr>
							<td>
								&nbsp;
							</td>
						</tr>
						<tr>
							<td align="left" style="width: 740px">
								<table id="tblSuficiencias" class="display" style="width: 740px">
									<thead>
										<tr>
											<th>
												EP
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
								</table>
							</td>
						</tr>
					</table>
				</fieldset>
			</td>
		</tr>
	</table>
</div>
<div id="retenciones" style='display:none'>
	<table id="grdRetencion" >  <!-- style='display:none' -->
		<thead>
			<tr>
				<th>Código</th>		 
				<th>Retención</th>
				<th>Porcentaje</th>
			</tr>
		</thead>
		 <!-- <tbody></tbody>
		<tfoot></tfoot> -->
	</table>
</div>

<div id="clavesMes" >
	<table id="grdclavesMes" style='display:none'>  <!--  -->
		<thead>
			<tr>
				<th>ID_TIPO_MOVIMIENTO</th>		 
				<th>ID_TIPO_CONCEPTO</th>
				<th>cIdAlmacen</th>
				<th>cAltaAlmacenaria</th>		 
				<th>cIdRFC</th>
				<th>cEjercicio</th>
				<th>cIdDocumento</th>		 
				<th>cIdUnidadEjecutora</th>
				<th>cClaveEP</th>
				<th>cCentroContable</th>		 
				<th>nPorcentajeIVA</th>
				<th>cEVTO</th>
				<th>montoNeto</th>
				<th>importe</th>
				<th>mes</th>
			</tr>
		</thead>
		 <!-- <tbody></tbody>
		<tfoot></tfoot> -->
	</table>
</div>
<div id="dialog-validaOLI" title="Validación de OLI">
	<h1>Digite el Número de OLI a Validar</h1>
	<input type="text" id="lblEp" name="lblEp" style="border-width:0; background-color:transparent;width: 600px;" readonly/>
	<input style="text-align: right;" name="cOLI" type="text" id="cOLI" value="" size="6" maxlength="6" />
</div>	
<!-- Campos configurables del modulo -->
<input type="hidden" id="modulo" name="modulo"  value="Pagos Directos"/>
<input type="hidden" id="TIPO_DOCTO" name="TIPO_DOCTO"  value="PAGODIRECTO"/>

<!-- Campos principales -->
<input type="hidden" id="cIdFolio" name="cIdFolio" />
<input type="hidden" id="Imp_Bruto" name="Imp_Bruto" />
<input type="hidden" id="Imp_Iva" name="Imp_Iva" />
<input type="hidden" id="Imp_Neto" name="Imp_Neto" />
<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" />
<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>"/>
<input type="hidden" id="existeEPPagoDirecto" name="existeEPPagoDirecto"/>
<input type="hidden" id="epTmp" name="epTmp"/>
<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora"/>
<input type="hidden" id="cCentroContableUsr" name="cCentroContableUsr" value="<%=cCentroContableUsr%>"/>
<input type="hidden" id="cCentroContable" name="cCentroContable"/>
<input type="hidden" id="cEjercicio" name="cEjercicio" />
<input type="hidden" id="mImporteTmp" name="mImporteTmp" />
<input type="hidden" id="nIdEstado" name="nIdEstado" />
<input type="hidden" id="cMesTmp" name="cMesTmp" />
<input type="hidden" id="mImporteMaximo" name="mImporteMaximo" />
<input type="hidden" id="cIDRFC" name="cIDRFC" />
<input type="hidden" id="ID_DESTINO_GASTO" name="ID_DESTINO_GASTO" />
<input type="hidden" id="Concepto" name="Concepto" />
<input type="hidden" id="ID_TIPO_CONCEPTO" name="ID_TIPO_CONCEPTO" />
<input type="hidden" id="ID_TIPO_MOVIMIENTO" name="ID_TIPO_MOVIMIENTO" />
<input type="hidden" id="cAlmacenTmp" name="cAlmacenTmp" />
<input type="hidden" id="altaAlmacen" name="altaAlmacen" />
<input type="hidden" id="cAnioTmp" name="cAnioTmp" />
<input type="hidden" id="nFacturaEP" name="nFacturaEP" />
<input type="hidden" id="ID_TIPO_OPER" name="ID_TIPO_OPER" />

<!-- Campos auxiliares para agregar las EP's -->
<input type="hidden" id="Partida" name="Partida" value="2" />
<input type="hidden" id="Partida2" name="Partida2" value="3" />
<input type="hidden" id="Partida3" name="Partida3" value="5" />
<input type="hidden" id="Partida1" name="Partida1" value="1" />

<!-- Campos auxiliares para actualizar los importes en los meses de las EP's -->
<input type="hidden" id="mes01" name="mes01" />
<input type="hidden" id="mes02" name="mes02" />
<input type="hidden" id="mes03" name="mes03" />
<input type="hidden" id="mes04" name="mes04" />
<input type="hidden" id="mes05" name="mes05" />
<input type="hidden" id="mes06" name="mes06" />
<input type="hidden" id="mes07" name="mes07" />
<input type="hidden" id="mes08" name="mes08" />
<input type="hidden" id="mes09" name="mes09" />
<input type="hidden" id="mes10" name="mes10" />
<input type="hidden" id="mes11" name="mes11" />
<input type="hidden" id="mes12" name="mes12" />

<!-- Campos auxiliares para crear el apartado -->
<input type="hidden" id="idCaso" name="idCaso" /><!-- id del caso -->
<input type="hidden" id="folioCasoApartado" name="folioCasoApartado" /><!-- folio del caso -->
<input type="hidden" id="folioCasoPagoDirecto" name="folioCasoPagoDirecto" />
<input type="hidden" id="folio" name="folio" /><!-- folio de del pago directo en financiero -->
<input type="hidden" id="caNoContrarrecibo" name="caNoContrarrecibo" />
<input type="hidden" id="nFolioApartado" name="nFolioApartado" />
<input type="hidden" id="apartadoAplicado" name="apartadoAplicado" />
<input type="hidden" id="CO_RESPONSABLE" name="CO_RESPONSABLE" />
<input type="hidden" id="montoUEProveedor" name="montoUEProveedor" />
<input type="hidden" id="partidasSobreTope" name="partidasSobreTope" />
<input type="hidden" id="nFolioPagoDirecto" name="nFolioPagoDirecto" />
<input type="hidden" id="caNoContrarreciboImprimir" name="caNoContrarreciboImprimir" />
<input type="hidden" id="operacion" name="operacion" />


<!-- campos auxiliares para la validacion de olis -->
<input type="hidden" id="cartera" name="cartera" />
<input type="hidden" id="capitulo" name="capitulo" />
<input type="hidden" id="UE" name="UE" />
<input type="hidden" id="mImporteCartera" name="mImporteCartera" />
  
  
 <!-- retenciones en claves -->
 <input type="hidden" id="nPorcentajeIva" name="nPorcentajeIva" />
 
 <input type="hidden" id="m2Millar" name="m2Millar" />
 <input type="hidden" id="mISRHonorarios" name="mISRHonorarios" />
 <input type="hidden" id="mObra5" name="mObra5" />
 <input type="hidden" id="mImporteFlete4" name="mImporteFlete4" />
 <input type="hidden" id="mISRArrenda" name="mISRArrenda" />
 <input type="hidden" id="mRetImpuestoCedular" name="mRetImpuestoCedular" />
 <input type="hidden" id="m23IVA" name="m23IVA" />
 <input type="hidden" id="mRetencion" name="mRetencion" />
 <input type="hidden" id="montoBruto" name="montoBruto" />
 <input type="hidden" id="mImporteIva" name="mImporteIva" />
 <input type="hidden" id="mImporteNeto" name="mImporteNeto" />
  <input type="hidden" id="penalizaciones" name="penalizaciones" />
 
 <input type="hidden" id="epClave" name="epClave" />
 <input type="hidden" id="mesPago" name="mesPago" />
 
 <input type="hidden" id="mCNIC" name="mCNIC" />
 <input type="hidden" id="mIMDT" name="mIMDT" />
 <input type="hidden" id="tipoAporteCNIC" name="tipoAporteCNIC" />  
 <input type="hidden" id="caNoContrarreciboTmp" name="caNoContrarreciboTmp" />
 
 <input type="hidden" id="cIdAlmacen" name="cIdAlmacen" />  
 <input type="hidden" id="altaAlmacenaria" name="altaAlmacenaria" />
 <input type="hidden" id="mes" name="mes" value="<%=mesActual %>"/>    
  <input type="hidden" id="totalPartidas" name="totalPartidas" />
  <input type="hidden" id="partidaCOMSOC" name="partidaCOMSOC" />
  <input type="hidden" id="responsableCOMSOC" name="responsableCOMSOC" />
<input type="hidden" id="operacionCOMSOC" name="operacionCOMSOC" />
<input type="hidden" id="responsable" name="responsable" />
  
  
  
 
</form>
</body>
</html>
