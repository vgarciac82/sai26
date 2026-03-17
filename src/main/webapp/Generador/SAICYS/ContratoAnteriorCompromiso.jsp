<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@ page import="java.util.*" %>
<%
	
	Usuario usuarioTab = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuarioTab == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	Map rol =usuarioTab.getRoles();
	String role="";
	String DATE_FORMAT = "yyyy-MM-dd";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String name_user=usuarioTab.getLogin();
	String cIdContrato = "";
	String cIdContratoDefinitivo = "";
	String cIdConsecutivoMod = "";
	String cEjercicio = "";
	String cIdTipoContrato= "";
	String cIdUnidadEjecutora = "";
	String nIdConsecutivo = "";
	String isConvEjercicioAnt="";
	String cCentroContable="";
	int aprobar=0;
	int devolver=0;
	if (session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo) != null) {
		cIdContratoDefinitivo = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioDefinitivo);
		cIdConsecutivoMod = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioConsecutivo);
		cIdContrato = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioId);
		cEjercicio = (String)session.getAttribute(GestionInterface.ATT_ContratoModificatorioEjercicio);
		isConvEjercicioAnt = (String)session.getAttribute(GestionInterface.ATT_ContratoIsModificatorioEjercicioAnt);
		
		cIdTipoContrato = cIdContrato.split("-")[0];
		if(cIdTipoContrato.equalsIgnoreCase("PLU")){
			cIdTipoContrato=cIdContrato.split("-")[1];
			cIdUnidadEjecutora = cIdContrato.split("-")[2];
			nIdConsecutivo = cIdContrato.split("-")[3];
		}else{
			cIdUnidadEjecutora = cIdContrato.split("-")[1];
			nIdConsecutivo = cIdContrato.split("-")[2];
		}	
			nIdConsecutivo = nIdConsecutivo.split("/")[0];
			cIdContrato=cIdTipoContrato+"-"+cIdUnidadEjecutora+"-"+nIdConsecutivo;
	}else 
		response.sendRedirect("ContratoModificatorio.jsp?tab=1");
		
	if (usuarioTab.getPropiedades() != null
			&& usuarioTab.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuarioTab.getPropiedad("CCENTROCONTABLE")
				.getValor();
	}

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <title>Car&aacute;tula Contrato Remanente</title>
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
			.monto {
				text-align: right;
				background-color: #CCCCCC;
				border: 1px solid #aaaaaa;
				color: #222222;
			}
			
			.montoCaptura {
				text-align: right;
				background-color: white;
				border: 1px solid #aaaaaa;
				color: black;
			}
			.montoCapturaEdit {
				text-align: right;
				background-color: #FFFF99;
				border: 1px solid #aaaaaa;
			}
	</style>
	<script type="text/javascript" src="../js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../js/jquery.formatCurrency.all.js"></script>
	<script type="text/javascript" src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.datepicker.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="../js/jquery.ui.tabs.js"></script>
	<script type="text/javascript" src="../js/crud.js"></script>
	<script type="text/javascript" src="../../js/catalogo/general.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../../Ayudas/js/autoCompleta.js"></script>
	<script type="text/javascript" charset="utf-8">	
	
	var roles="",porcentajeTotalAumentado=0;
	var oTable;
	var cacheMovtoObra = 0,compromisoRegistrado=false;
	var nombreMeses = new Array("enero", "febrero", "marzo", "abril", "mayo",
		"junio", "julio", "agosto", "septiembre", "octubre", "noviembre",
		"diciembre");
		$(document).ready(function() {
			
			
			<%
			   
				NegativaPestana NegPestana=new NegativaPestana();
				NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
				//botones
			//	NegativaBoton NegBoton= new NegativaBoton();
				NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
				
				Iterator it1 = rol.entrySet().iterator();
				while (it1.hasNext()) {
					Map.Entry r = (Map.Entry)it1.next();
					role+= r.getKey().toString()+",";
				}
				if(role.length()>0){
					role = role.substring(0,role.length()-1);
				}
				Map botones=nb.getBotones(role,"ContratoAnterior","caratulaContratoMod");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
				
			%>
			roles="<%=role%>";
			$("input.AyudaSyC").subIniciaDlg();
		    $("input.autoCompletaSyC").subIniciaAutoCompleta();
			initTables();
			setFieldsInit();
			document.getElementById("imgAprobarCompromiso").style.visibility = "hidden";
			document.getElementById("spanComprometer").style.visibility = "hidden";
			
			queryFormPost("readCountCompromisoRemanenteEjercicioAnterior", {async : false});
			
			if($("#compromisos").val()>0)
			 	compromisoRegistrado=true;
			 else if(parseInt($("#nEstado").val(),10) == 4)
			 	compromisoRegistrado=true;
			 
			
			$( "#dialog-Procesando" ).dialog({
				autoOpen: false,
				height: 400,
				width: 400,
				modal: true,
				open: function() {
				$("#campo").val( "nFolio" + $("#cDocumento").val() );
				$("#tablaEnc").val( "t" + $("#cDocumento").val() + "Encabezado" );
				$("#campoCondicion").val( "nFolio" + $("#cDocumento").val() ); 
				$("#tablaDet").val( "t" + $("#cDocumento").val() + "Detalle" ); 
				$("#tipoAplicar").val( $("#cDocumento").val() );
				
					var tipo = "aplicarMotor";
					var caNoContrarrecibo = $("#nFolioCompromiso").val();
					var campo = $("#campo").val();
					var tablaEnc = $("#tablaEnc").val();
					var campoCondicion = $("#campoCondicion").val(); 
					var tablaDet = $("#tablaDet").val(); 
					var tipoAplicar = $("#tipoAplicar").val();
					
					$("#divEsperaProcesando").attr("style","visibility=visible");
					
					$.ajax({
							url:'../cierrePresupuestal.jsp',
							type:'post',
							dataType: 'json',
							data:{tipo:tipo,caNoContrarrecibo:caNoContrarrecibo,campo:campo,tablaEnc:tablaEnc,campoCondicion:campoCondicion,tablaDet:tablaDet,tipoAplicar:tipoAplicar},
							success:function(data){
									if(data.sinSesion == 'sinSesion'){
											location.href = "../index.jsp";
									}
									if(data.estatus == "guardado"){
										if ($("#chk_radicado").prop("checked")){
											queryFormPost("updateEsRadicadoComp", {async: false });
											queryFormPost("contratoDiversoRadicadoUpdate", {async: false });
										}
										alert( "Documento Aplicado Correctamente: "+ $("#caNoCompromiso").val() ) ;	
										document.getElementById("imgAprobarCompromiso").style.visibility = "hidden";
										document.getElementById("spanComprometer").style.visibility = "hidden";
										queryFormPost("UpdateEstadomContratoRemanenteEjercicioAnterior", {async: false });
										setFieldsInit();
										breturnVal = true;
									}else{
										breturnVal = false;
										alert( "El Documento No Se Aplico: "+$("#caNoCompromiso").val() + " - " + data.estatus ) ;		
									}
									$("#divEsperaProcesando").attr("style","visibility=hidden");
									$( "#dialog-Procesando" ).dialog( "close" );
							}
					});
				},
				close: function() {										
				}				
			});

			cargaPreCompromisoVacio();
			
			$("#dialog-form-ep")
			.dialog(
					{
						autoOpen : false,
						height : 520,
						width : 550,
						modal : true,
						buttons : {
							"Agregar" : function() {
								actualizaEps();
								$(this).dialog("close");
							},
							Cancel : function() {
								$(this).dialog("close");
								
							}
						},
						close : function() {
							cargaPreCompromisoVacio();
						}
					});
				deshabilitaMontos();
				iniciaCapturaMontos();
		});
		function actualizaEps(){
			for(i=0; i < nombreMeses.length; i++ ){
			$("#" + nombreMeses[i] + "Importe").val(quitaFrmt( $("#" + nombreMeses[i] + "Apart").val()));
			if(parseFloat($("#" + nombreMeses[i] + "Importe").val())<0)
				$("#" + nombreMeses[i] + "Importe").val(0);
			}
			queryFormPost({queryName: "updatemContratoRemanenteEjercicioAnteriorCompromiso",
	           				  async: false,
	           				  callback: function() {
	           				  	alert("Montos actualizados");
	           				  }
	           			});
		}
		function setFieldsInit(){
			//asigna el valor de true a readonly para los campos que sirven de etiquetas
			document.getElementById("lblUnidadEjecutora").style.readonly = true;
			document.getElementById("lblDefinitivo").style.readonly = true;
			document.getElementById("lblProveedor").style.readonly = true;
			document.getElementById("lblEstadoMod").style.readonly = true;
			
			queryFormPost("readDatosRemanenteEjercicioAnterior", {async : false});
			queryFormPost("estadoCotratoAnteriorConsulta", {async : false});
			$("#lblConcepto").val($("#cConceptoContrato").val());
			$("#lblTotalContratoOriginal").val("Monto Contrato Original: $ "+formateaMoneda($("#mimportetotal").val()));
			$("#lblTotalContratoPagado").val("Monto Pagado Ejercicio Anterior: $ "+formateaMoneda($("#mImportePago").val()));
			$("#lblTotalContratoRemanente").val("Monto Remanente: $ "+formateaMoneda($("#mRemanenteContrato").val()));
			$("#lblEstadoMod").val($("#cEstado").val());
			

			document.getElementById("lblConcepto").style.readonly = true;
			document.getElementById("lblTotalContratoOriginal").style.readonly = true;
			document.getElementById("lblTotalContratoPagado").style.readonly = true;
			document.getElementById("lblTotalContratoRemanente").style.readonly = true;
			
			
			
			setInitQueys();
			
				
			//mostrarXtipoMod();
			
						
			if(parseInt($("#nEstado").val(),10) == 1){
				document.getElementById("precompromisoContratoMod").disabled = true;
			}
			
			

			//queryFormPost("mContratoModicadoChecaRolUsuario", {async: false});
			if (roles.indexOf("ADMIN_RECMAT")< 0 && roles.indexOf("JEFES")< 0 && roles.indexOf("ANALISTA")< 0) { 
				$("#usuarioCreacionOriginal").val('');
				queryFormPost("mContratoModicadoUsuarioCreacionOriginalRead",{async: false });
				if ($("#usuarioCreacionOriginal").val() != $("#usuarioLogin").val()){
					document.getElementById("tblReqsMods").disabled = true;
					document.getElementById("agrBtnContratoMod").disabled = true;
					document.getElementById("bajaDiv").disabled = true;
					document.getElementById("devBtnContratoMod").disabled = true;
					document.getElementById("grdBtnContratoMod").disabled = true;
				} 
			}
			
			
		}
		
				
		function setInitQueys(){
			queryFormPost("mContratoHeaderReadEjerAnt", {async: false});
			$("#mImporteTotal").val($("#mRemanenteContrato").val()); 
		}
		
		
				
		function formateaMoneda(importe){
			var importeSeparado = importe.toString().split("\.");
			var importeParte1 = importeSeparado[0];
			var cont=0;
			var tem="";
			
			for(var i=importeParte1.length; i>0; i--){
				if(cont == 3){
					tem = ","+tem;
					cont=0;
				}
				tem = importeParte1.substring(i-1,i)+tem;
				cont++;
			}
			if(importe.toString().indexOf("\.")>0){
				for(var i=importeSeparado[1].length; i<2; i++){
					importeSeparado[1]+="0";
				}
				return tem+"."+importeSeparado[1];
			}
			else{
				return tem+".00";
			}
		}
				
		function quitaFmt( val ) {
		   	val = val.replace("$", "");
		   	val = val.replace(/,/g, '');
		   	if ( val.indexOf( "(" ) >= 0 ) {
				val = val.replace("(", "");
				val = val.replace(")", "");
				val = "-" + val;
		   	}
		   	return val;
		}		
		function initTables(){
		$('#dt_preCompromiso').dataTable(
				{
	   			    "bPaginate": false,
	       			"bLengthChange": false,
	       			"bFilter": false,
	       			"bSort": false,
	       			"bInfo": false,
	       			"bAutoWidth": false, 
					"sScrollX": 100,         
					"sScrollY": 100,         
					"bJQueryUI": true,    
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers"    
				} );
	
		$('#dt_grabaprecompD').dataTable({
					"iDisplayLength": 20,
        			"bPaginate": false,
        			"bFilter": false,
        			"bSort": false,
        			"bInfo": false } );
		$('#dt_grabaprecompD').attr('visible', false);
		
			$('#dt_suficiencia').dataTable(
			{
	  			    "bPaginate": false,
	      			"bLengthChange": false,
	      			"bFilter": false,
	      			"bSort": false,
	      			"bInfo": false,
	      			"bAutoWidth": false, 
				"sScrollX": 100,         
				"sScrollY": 100,         
				"bJQueryUI": true,    
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"    
			} );
		
		
		
	}
		function borraModificatorio(){		
		
	}
	function fnGetSelected( oTableLocal ){
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
	
	$('#dt_preCompromiso tr').live('dblclick', function() {
				//$(this).addClass('row_selected');
			if(!compromisoRegistrado){
				if ( $(this).hasClass('row_selected') )             
					$(this).removeClass('row_selected');         
				else            
					$(this).addClass('row_selected');
				
				var anSelected = fnGetSelected( oTable );
				if (anSelected != "") {
					var aData = oTable.fnGetData(anSelected[0]);
					$("#epSel").val(aData[0]);
					fnClickAddRowC();
					}
				}
					
			});
		
	function fnClickAddRowC() {
	// Valida que: Si existe ya una ep capturada no puede agregar una mas.
	// primero debera eliminarla

	// if( parseInt( $("#dt_clavepresup").dataTable().fnGetData().length, 10 ) >
	// 0 && !modificando){
// alert("Para cambiar la estructura programatica es necesario eliminar antes la
// que ya se encuentra capturada.");
// return false;
// }
	$("#eneImporte").val("0");
	$("#febImporte").val("0");
	$("#marImporte").val("0");
	$("#abrImporte").val("0");
	$("#mayImporte").val("0");
	$("#junImporte").val("0");
	$("#julImporte").val("0");
	$("#agoImporte").val("0");
	$("#sepImporte").val("0");
	$("#octImporte").val("0");
	$("#novImporte").val("0");
	$("#dicImporte").val("0");
	if ($("#cEsRadicado").val() == "N")
		queryFormPost( {
			queryName : "readMontosEPObra",
			async : false,
			callback : function() {
	
					
				var nMes = 1;	
				var renglon = parseInt($("#nDocRenglon").val(),10) + 1;
				

	
				$("#epDisp").val($("#epSel").val());
				$("#eneroDisp").val($("#eneImporte").val());
				cambiafrmt($("#eneroDisp")[0]);
				$("#febreroDisp").val($("#febImporte").val());
				cambiafrmt($("#febreroDisp")[0]);
				$("#marzoDisp").val($("#marImporte").val());
				cambiafrmt($("#marzoDisp")[0]);
				$("#abrilDisp").val($("#abrImporte").val());
				cambiafrmt($("#abrilDisp")[0]);
				$("#mayoDisp").val($("#mayImporte").val());
				cambiafrmt($("#mayoDisp")[0]);
				$("#junioDisp").val($("#junImporte").val());
				cambiafrmt($("#junioDisp")[0]);
				$("#julioDisp").val($("#julImporte").val());
				cambiafrmt($("#julioDisp")[0]);
				$("#agostoDisp").val($("#agoImporte").val());
				cambiafrmt($("#agostoDisp")[0]);
				$("#septiembreDisp").val($("#sepImporte").val());
				cambiafrmt($("#septiembreDisp")[0]);
				$("#octubreDisp").val($("#octImporte").val());
				cambiafrmt($("#octubreDisp")[0]);
				$("#noviembreDisp").val($("#novImporte").val());
				cambiafrmt($("#noviembreDisp")[0]);
				$("#diciembreDisp").val($("#dicImporte").val());
				cambiafrmt($("#diciembreDisp")[0]);
				sumaTotalCalendarizado();
				$("#dialog-form-ep").dialog("open");
				
			}
		});
	else
		queryFormPost( {
			queryName : "readMontosEPObraRadicado",
			async : false,
			callback : function() {
	
					
				var nMes = 1;	
				var renglon = parseInt($("#nDocRenglon").val(),10) + 1;
				
		
				$("#epDisp").val($("#epSel").val());
				$("#eneroDisp").val($("#eneImporte").val());
				cambiafrmt($("#eneroDisp")[0]);
				$("#febreroDisp").val($("#febImporte").val());
				cambiafrmt($("#febreroDisp")[0]);
				$("#marzoDisp").val($("#marImporte").val());
				cambiafrmt($("#marzoDisp")[0]);
				$("#abrilDisp").val($("#abrImporte").val());
				cambiafrmt($("#abrilDisp")[0]);
				$("#mayoDisp").val($("#mayImporte").val());
				cambiafrmt($("#mayoDisp")[0]);
				$("#junioDisp").val($("#junImporte").val());
				cambiafrmt($("#junioDisp")[0]);
				$("#julioDisp").val($("#julImporte").val());
				cambiafrmt($("#julioDisp")[0]);
				$("#agostoDisp").val($("#agoImporte").val());
				cambiafrmt($("#agostoDisp")[0]);
				$("#septiembreDisp").val($("#sepImporte").val());
				cambiafrmt($("#septiembreDisp")[0]);
				$("#octubreDisp").val($("#octImporte").val());
				cambiafrmt($("#octubreDisp")[0]);
				$("#noviembreDisp").val($("#novImporte").val());
				cambiafrmt($("#noviembreDisp")[0]);
				$("#diciembreDisp").val($("#dicImporte").val());
				cambiafrmt($("#diciembreDisp")[0]);
				sumaTotalCalendarizado();
				$("#dialog-form-ep").dialog("open");
				
			}
		});
	}
	function cambiafrmt(fld) {
		$("#" + fld.id).formatCurrency();
	}
	function deshabilitaMontos() {
		$(".monto").each(function() {
			$(this).attr('readonly', 'readonly');
		});
	}
	function iniciaCapturaMontos() {
		$(".montoCaptura").each(function() {
			$(this).val(0);
			cambiafrmt(this);
			$(this).focus(function() {
				onFocusMontoMoney(this.id);
				sumaTotalCalendarizado();
			});
			$(this).blur(function() {
				onBlurMontoMoney(this.id);
				sumaTotalCalendarizado();
			});
			$(this).keypress(function(e) {
				return onlyNumbers(e);
			});
		});
	
	}
	function onlyNumbers(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		if (keyPressed == 47) {
			return false;
		}
		return ((keyPressed > 47 && keyPressed < 58) || keyPressed == 46);
	}
	function onFocusMontoMoney(idInpt) {
		var val = Sinfrmt($("#" + idInpt)[0]);
		if (val < 0)
			$("#" + idInpt).val('');
		else {
			cacheMovtoObra = $("#" + idInpt).val();
			$("#" + idInpt).val('');
		}
	
		$("#" + idInpt).removeClass("montoCaptura");
		$("#" + idInpt).addClass("montoCapturaEdit");
	}
	function Sinfrmt(fld) {
		var valcol = fld.value;
		valcol = valcol.replace(/$/g, "");
		valcol = valcol.replace(/,/g, "");
		$("#" + fld.id).val(valcol);
	}
	function onBlurMontoMoney(idInpt,classNormal) {
		if ($("#" + idInpt).val() != '') {
			cambiafrmt($("#" + idInpt)[0]);
		} else {
			$("#" + idInpt).val(cacheMovtoObra);
			Sinfrmt($("#" + idInpt)[0]);
			cambiafrmt($("#" + idInpt)[0]);
		}
	
		$("#" + idInpt).removeClass("montoCapturaEdit");
		$("#" + idInpt).addClass(classNormal?classNormal:"montoCaptura");
	}
	function sumaTotalCalendarizado() {
		$("#totalCalendarizado").val("0");
		$(".montoCaptura").each(function() {
			var val = parseFloat(quitaFrmt($(this).val()) == '' ? 0: quitaFrmt($(this).val()));
			var sum = parseFloat(quitaFrmt($("#totalCalendarizado").val()));
			$("#totalCalendarizado").val(sum+val);
			cambiafrmt($("#totalCalendarizado")[0]);
			var total=parseFloat(quitaFrmt($("#mImporteTotal").val()));
			$("#faltaCalendarizadar").val(total-sum+val);
			cambiafrmt($("#faltaCalendarizadar")[0]);
		});
	}
	function quitaFrmt(fld) {
		var valcol = fld.toString();
		valcol = valcol.replace(/[$]/g, "");
		valcol = valcol.replace(/,/g, "");
		return valcol;
	}
	function cargaPreCompromisoVacio(){
		var documento=$("#cDocumentoDefinitivo").val();
		var numMod=documento.split("#");
		oTable=$("#dt_preCompromiso").dataTable({
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
				}
			},
			bAutoWidth: true,
			sScrollX: 100,
			sScrollY: 100,
			bScrollCollapse: true,
			bJQueryUI: true,
			bDestroy : true,
			bServerSide: true,   
			//Carga el calendario con valores de 0
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_ConsultaCompromisoContRemAnt('"+$("#cContratoDefinitivo").val()+"')",
			aoColumns: [
				{ sName: "ClaveSIAFF",bSortable: false },
				{ sName: "ClaveInterna",bSortable: false },
				{ sName: "compromiso01",bSortable: false },
				{ sName: "compromiso02",bSortable: false },
				{ sName: "compromiso03",bSortable: false },
				{ sName: "compromiso04",bSortable: false },
				{ sName: "compromiso05",bSortable: false },
				{ sName: "compromiso06",bSortable: false },
				{ sName: "compromiso07",bSortable: false },
				{ sName: "compromiso08",bSortable: false },
				{ sName: "compromiso09",bSortable: false },
				{ sName: "compromiso10",bSortable: false },
				{ sName: "compromiso11",bSortable: false },
				{ sName: "compromiso12",bSortable: false },
				{ sName: "cIdContrato",	bSearchable: false,	bSortable: false, bVisible: false  }, 
           		{ sName: "EP",	bSearchable: false,	bSortable: false, bVisible: false  }],
           		
           		fnInitComplete: function(oSettings, json) {
           			queryFormPost("existePrecomContMod", {async: false} );
           			
           		           			
 
					
					
				}
			});
			queryFormPost("readAcumuladoCompReg", {async: false });
			var mImporteTotal=parseFloat(quitaFmt($("#mRemanenteContrato").val())).toFixed(2);
			var mComprometido=parseFloat(quitaFmt($("#mComprometido").val())).toFixed(2);
			var mDifCompromiso=mImporteTotal-mComprometido;
			$("#mImporteTotal").val(mImporteTotal);
			$("#mComprometido").val(mComprometido);
			$("#mDifCompromiso").val(mDifCompromiso);
			cambiafrmt($("#mImporteTotal")[0]);
			cambiafrmt($("#mComprometido")[0]);
			cambiafrmt($("#mDifCompromiso")[0]);
			if(mDifCompromiso==0){
				habilitaCompromiso();
			}
	}
	function habilitaCompromiso(){
		//alert("Compromiso Completo");
		//aqui revisar si el compromiso ya esta aplicado
		queryFormPost("readCompromisoAplicadoRemanenteEjercicioAnterior", {async: false });
		if(parseInt($("#cDocumentoHaplicado").val(),10) == 0){
			document.getElementById("imgAprobarCompromiso").style.visibility = "visible";
			document.getElementById("spanComprometer").style.visibility = "visible";
		}
	}
		
	
	function guardarcomprimisoGeneral(){
		queryFormPost({
						queryName : "readCountCompromisoRemanenteEjercicioAnterior",
						async : false,
						callback : function() {
						if($("#compromisos").val()>0)
							queryFormPost({
								queryName : "readCompromisoRemanenteEjercicioAnterior",
								async : false,
								callback : function() {
									procesar();
								}
							});
						else
							queryFormPost({
								queryName : "registraCompromisoRemanteEjercicioAnterior",
								async : false,
								callback : function() {
									queryFormPost({
										queryName : "readCompromisoRemanenteEjercicioAnterior",
										async : false,
										callback : function() {
											procesar();
										}
									});
								}
							});
						}
					});		
	}	
	function procesar(){
		$( "#dialog-Procesando" ).dialog( "open" );
		return breturnVal;
	}

	</script>
  </head>
  <body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0" topmargin="0" >
  	<form > 
  	<div id="dialog-Procesando" title="Procesando">
			<div id="divEsperaProcesando" style="visibility: hidden"
				align="center">
				Espere por favor.... <img border="0" src="../../imagenes/espera.gif"
					height="30">
			</div>
		</div>
  	<div id="dialog-form-ep"
					title="Agregar Estructura Program&aacute;tica">
					<fieldset>
						<table>
							<tr>
								<td>
									<label for="epDisp">
										EP:
									</label>
								</td>
								<td>
									<input type="text" name="epDisp" id="epDisp" value=""
										class="monto" size="63" />
								</td>
							</tr>
						</table>
						<table id="capturaMontosTbl" align="center">
							<thead>
								<tr>
									<th>
										Mes
									</th>
									<th>
										Disponible
									</th>
									<th>
										Monto
									</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<td>
										<label class="NombreMes" for="eneroDisp">
											Enero
										</label>
									</td>
									<td>
										<input type="text" id="eneroDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="eneroApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="eneroHide" value="" size="15" />
									</td>
								</tr>

								<tr>
									<td>
										<label class="NombreMes" for="febreroDisp">
											Febrero
										</label>
									</td>
									<td>
										<input type="text" id="febreroDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="febreroApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="febreroHide" value="" size="15" />
									</td>
								</tr>

								<tr>
									<td>
										<label class="NombreMes" for="marzoDisp">
											Marzo
										</label>
									</td>
									<td>
										<input type="text" id="marzoDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="marzoApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="marzoHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="abrilDisp">
											Abril
										</label>
									</td>
									<td>
										<input type="text" id="abrilDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="abrilApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="abrilHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="mayoDisp">
											Mayo
										</label>
									</td>
									<td>
										<input type="text" id="mayoDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="mayoApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="mayoHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="junioDisp">
											Junio
										</label>
									</td>
									<td>
										<input type="text" id="junioDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="junioApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="junioHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="julioDisp">
											Julio
										</label>
									</td>
									<td>
										<input type="text" id="julioDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="julioApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="julioHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="agostoDisp">
											Agosto
										</label>
									</td>
									<td>
										<input type="text" id="agostoDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="agostoApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="agostoHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="septiembreDisp">
											Septiembre
										</label>
									</td>
									<td>
										<input type="text" id="septiembreDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="septiembreApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="septiembreHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="octubreDisp">
											Octubre
										</label>
									</td>
									<td>
										<input type="text" id="octubreDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="octubreApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="octubreHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="noviembreDisp">
											Noviembre
										</label>
									</td>
									<td>
										<input type="text" id="noviembreDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="noviembreApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="noviembreHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										<label class="NombreMes" for="diciembreDisp">
											Diciembre
										</label>
									</td>
									<td>
										<input type="text" id="diciembreDisp" value="" class="monto"
											size="15" />
									</td>
									<td>
										<input type="text" id="diciembreApart" value=""
											class="montoCaptura" size="15" />
										<input type="hidden" id="diciembreHide" value="" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										&nbsp;
									</td>
									<td align="right">
										<label class="NombreMes">
											Total Calendarizado:
										</label>
									</td>
									<td>
										<input type="text" id="totalCalendarizado" readonly="readonly"
											value="" class="numerico notEditable" size="15" />
									</td>
								</tr>
								<tr>
									<td>
										&nbsp;
									</td>
									<td align="right">
										<label class="NombreMes">
											Faltante por Calendarizar:
										</label>
									</td>
									<td>
										<input type="text" id="faltaCalendarizadar" readonly="readonly"
											value="" class="numerico notEditable" size="15" />
									</td>
								</tr>
							</tbody>
						</table>
					</fieldset>
				</div>
		<div id="container" class="container" >
			<table width="94%" align="left">
				<tr>
					<td width="750px" >
						<fieldset>&nbsp; 
							<legend>Compromiso del Contrato con Remanente del Ejercicio Anterior</legend>
								<table align="left" cellpadding="2" width="100%">
							    	<tr>
							    		<td align="right" colspan="2">
							    			<img id="imgAprobarCompromiso" title="Autorizar Compromiso" src="../imagenes/accept_green.png" style="cursor: pointer" 	onclick="guardarcomprimisoGeneral();"  />
							    			&nbsp;<span id="spanComprometer">Comprometer</span>&nbsp;
							    			<!--<img id="imgEliminar" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="borraModificatorio();" />
							    			<span id="spanEliminar">Eliminar</span>-->
											<img id="imgSalir" src="../imagenes/cancel_round.png" style="cursor: pointer" onclick="window.location = 'ContratoAnterior.jsp?tab=1';"/>&nbsp;Salir
							    		</td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" id="lblUnidadEjecutora" name="lblUnidadEjecutora" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" id="lblDefinitivo" name="lblDefinitivo" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 400px" id="lblConcepto" name="lblConcepto" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblProveedor" id="lblProveedor" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 300px" name="lblEstadoMod" id="lblEstadoMod" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalContratoOriginal" id="lblTotalContratoOriginal" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalContratoPagado" id="lblTotalContratoPagado" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>
							    	<tr>
							    		<td align="left" colspan="2"><input type="text" style="width: 600px" name="lblTotalContratoRemanente" id="lblTotalContratoRemanente" readonly style="border-width:0; background-color:transparent"/></td>
							    	</tr>    							    				    	
							    </table>
						</fieldset>
					</td>
				</tr>
				
				
				<tr id="trinputsMontos">
				<td align="left">
					<table>
						<tr>
							<td align="left">Monto Contrato: <input style="text-align:right;" readonly type="text" maxlength="20" size="12" name="mImporteTotal" id="mImporteTotal" value="0" disabled="disabled"></td>
							<td align="left">Compromiso Actual: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mComprometido" id="mComprometido" value="0" disabled="disabled"></td>
							<td align="left">Saldo Compromiso: <input style="text-align:right;" readonly type="text" maxlength="12" size="12" name="mDifCompromiso" id="mDifCompromiso" value="0" disabled="disabled"></td>
						</tr>
					</table>
				</td>
			</tr>
			<tr id="trEditarpreCompromisoCont" style="display: none">
				<td align="left" >
					<!--<a href="javascript:void(0)" id="edit">editar</a>-->
					<input type="button" name="edit" id="edit" size="5" value="Editar"/>
				</td>
			</tr>
			<tr id="tr_dt_preCompromiso">
				<td align="left" style="width: 740px">
					<table id="dt_preCompromiso" class="display" style="width: 740px">
						<thead>
							<tr align="center">
								<th>Estructura Program&aacute;tica</th>
								<th>Clave Interna</th>
								<th>Enero</th>
								<th>Febrero</th>
								<th>Marzo</th>
								<th>Abril</th>
								<th>Mayo</th>
								<th>Junio</th>
								<th>Julio</th>
								<th>Agosto</th>
								<th>Septiembre</th>
								<th>Octubre</th>
								<th>Noviembre</th>
								<th>Diciembre</th>
								<th>Contrato</th>
								<th>EP</th>
							</tr>
						</thead>
	      			</table>
			     </td>
			 </tr>
		</table>
		    <!-- Hidden's -->
		    <!-- Sesion  -->
		    <input type="hidden" name="cEjercicio" id="cEjercicio" value="<%=cEjercicio%>"/>
		    <input type="hidden" name="cIdUnidadEjecutora" id="cIdUnidadEjecutora" value="<%=cIdUnidadEjecutora%>"/>
		    <input type="hidden" name="cIdTipoContrato" id="cIdTipoContrato" value="<%=cIdTipoContrato%>" />
		    <input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=usuarioTab.getLogin() %>"/>
		    <input type="hidden" name="cContrato" id="cContrato" value="<%=cIdContrato%>" /> 
		    <input type="hidden" name="cCentroContable" id="cCentroContable" value="<%=cCentroContable%>" /> 
		    <input type="hidden" name="cContratoDefinitivo" id="cContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		    <input type="hidden" name="cIdContratoDefinitivo" id="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		    <input type="hidden" name="contratoDefinitivo" id="contratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		    <input type="hidden" name="tipoMod" id="tipoMod" />
		    <input type="hidden" name="R_NOMBRE" id="R_NOMBRE" > 
		    <input type="hidden" name="nFolioCompromiso" id="nFolioCompromiso" >
		    <input type="hidden" name="compromisos" id="compromisos" > 
		    
		    <input type="hidden" name="usuarioCreacionOriginal" id="usuarioCreacionOriginal" value="" />
  			<input type="hidden" name="usuarioLogin" id="usuarioLogin" value="<%=name_user%>" />
  			<input type="hidden" name="usuarioLoginRole" id="usuarioLoginRole" value="" />
		    
		    <input type="hidden" name="totalAnterior" id="totalAnterior" />
		    <input type="hidden" name="totalMod" id="totalMod" />
		    <input type="hidden" name="totalNuevo" id="totalNuevo" />
		    
		    <!-- Resultado de consultas -->
		    <input type="hidden" name="nEstado" id="nEstado" />
		    <input type="hidden" name="cEstado" id="cEstado" />
		    <input type="hidden" name="cIdUsuarioCreacion" id="cIdUsuarioCreacion" />	    
		    <!--  Hidden valores auxiliares -->
		    <input type="hidden" name="totalModAct" id="totalModAct" />
		    <input type="hidden" name="totalNuevoAct" id="totalNuevoAct" />
		    
		    <input type="hidden" name="cIdTipoConsolidado" id="cIdTipoConsolidado" />
		    <input type="hidden" name="cIdUEConsolidado" id="cIdUEConsolidado" />
		    <input type="hidden" name="cIdConsecutivoConsolidado" id="cIdConsecutivoConsolidado" />
		    <input type="hidden" name="cIdLineaConsolidado" id="cIdLineaConsolidado" />
		    
		    <input type="hidden" name="cIdSolicitud" id="cIdSolicitud" />
		    <input type="hidden" name="cIdLineaSolicitud" id="cIdLineaSolicitud" />
		    <input type="hidden" name="cEstadoLineaSolicitud" id="cEstadoLineaSolicitud" />
		    
		    <input type="hidden" name="cDescripcion" id="cDescripcion" />
		    <input type="hidden" name="nCantidad" id="nCantidad" />
		    <input type="hidden" name="mMonto" id="mMonto" />
		    
		    <input type="hidden" name="estadoLinea" id="estadoLinea" />
		    <input type="hidden" name="cIdTipoProcedimiento" id="cIdTipoProcedimiento" />
		    <input type="hidden" name="nIdConsecutivoProcedimiento" id="nIdConsecutivoProcedimiento" />
		    <input type="hidden" name="mMontoNeto" id="mMontoNeto" value="0"/>
		    <input type="hidden" name="mPrecioUnitario" id="mPrecioUnitario" value="0"/>
			<input type="hidden" name="lContratoAbierto" id="lContratoAbierto" value="0" />
			<input type="hidden" name="bEsXTotalPlu" id="bEsXTotalPlu" value="0" />
			<input type="hidden" name="mMontoTotalPlurianual" id="mMontoTotalPlurianual" value="0" />
			<input type="hidden" name="nIdPartidaPresupContMod" id="nIdPartidaPresupContMod" value="0" />
			<input type="hidden" name="isConvEjercicioAnt" id="isConvEjercicioAnt" value="0" />
			
			<input type="hidden" name="mContratoMontoNeto" id="mContratoMontoNeto" >   	
			<input type="hidden" name="mContratoMN" id="mContratoMN" >  
			<input type="hidden" name="cIdRFC" id="cIdRFC" value="0"/>
			<input type="hidden" id="campo" name="campo"value=""> 
			 <input type="hidden" id="tablaEnc"	name="tablaEnc" value=""> 
			 <input type="hidden"id="campoCondicion" name="campoCondicion" value=""> 
			 <input type="hidden" id="tablaDet" name="tablaDet" value=""> 
			 <input type="hidden" id="tipoAplicar" name="tipoAplicar" value="">
				
			<input type="hidden" name="mimportetotal" id="mimportetotal" value="0"/>
			<input type="hidden" name="mImporteBruto" id="mImporteBruto" value="0"/>
			<input type="hidden" name="mImporteIVA" id="mImporteIVA" value="0"/>
			
			<input type="hidden" name="mImportePago" id="mImportePago" value="0"/>
			<input type="hidden" name="mRemanenteContrato" id="mRemanenteContrato" value="0"/>
			<input type="hidden" name="cConceptoContrato" id="cConceptoContrato" value="0"/>
			
			<input type="hidden" name="nIdLineaConsolidado" id="nIdLineaConsolidado" >
			<input type="hidden" name="cIdSubPartida" id="cIdSubPartida" >   	
			<input type="hidden" name="cIdCABM" id="cIdCABM" > 
			<input type="hidden" name="nPorcentajeIVA" id="nPorcentajeIVA" >  
			<input type="hidden" name="validaExiste" id="validaExiste" > 
			
			<input type="hidden" name="cDocumento" id="cDocumento" value="Compromiso" />
			<input type="hidden" name="cDocumentoDefinitivo" id="cDocumentoDefinitivo" value="<%=cIdContratoDefinitivo + "#M" + cIdConsecutivoMod%>" />
			<input type="hidden" name="epSel" id="epSel" value="" />
			<input type="hidden" name="cEsRadicado" id="cEsRadicado" value="N" />
			<input type="hidden" name="cDocumentoHaplicado" id="cDocumentoHaplicado" value="" />
			
			<input type="hidden" name="eneImporte" id="eneImporte" value="" />
			<input type="hidden" name="febImporte" id="febImporte" value="" />
			<input type="hidden" name="marImporte" id="marImporte" value="" />
			<input type="hidden" name="abrImporte" id="abrImporte" value="" />
			<input type="hidden" name="mayImporte" id="mayImporte" value="" />
			<input type="hidden" name="junImporte" id="junImporte" value="" />
			<input type="hidden" name="julImporte" id="julImporte" value="" />
			<input type="hidden" name="agoImporte" id="agoImporte" value="" />
			<input type="hidden" name="sepImporte" id="sepImporte" value="" />
			<input type="hidden" name="octImporte" id="octImporte" value="" />
			<input type="hidden" name="novImporte" id="novImporte" value="" />
			<input type="hidden" name="dicImporte" id="dicImporte" value="" />
			
			<input type="hidden" name="eneroImporte" id="eneroImporte" value="" />
			<input type="hidden" name="febreroImporte" id="febebroImporte" value="" />
			<input type="hidden" name="marzoImporte" id="marzoImporte" value="" />
			<input type="hidden" name="abrilImporte" id="abrilImporte" value="" />
			<input type="hidden" name="mayoImporte" id="mayoImporte" value="" />
			<input type="hidden" name="junioImporte" id="junioImporte" value="" />
			<input type="hidden" name="julioImporte" id="julioImporte" value="" />
			<input type="hidden" name="agostoImporte" id="agostoImporte" value="" />
			<input type="hidden" name="septiembreImporte" id="septiembreImporte" value="" />
			<input type="hidden" name="octubreImporte" id="octubreImporte" value="" />
			<input type="hidden" name="noviembreImporte" id="noviembreImporte" value="" />
			<input type="hidden" name="diciembreImporte" id="diciembreImporte" value="" />
			<input type="hidden" name="caNoCompromiso" id="caNoCompromiso" value="" />
		</div>
	</form>
  </body>
</html>
