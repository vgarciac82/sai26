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
    String cCentroContable = "";
    String nIdEstado="";
    String destino_gasto="";
    String tConcepto="";
    String grupoMat="";
    Calendar c = Calendar.getInstance();
  	int mesActual = c.get(Calendar.MONTH)+1;
    
     if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	else
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	
	if (session.getAttribute(GestionInterface.ATT_RelacionGastosFolio) != null) {
		cEjercicio =(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEjercicio);
		cIdDocumento= (String)session.getAttribute(GestionInterface.ATT_RelacionGastosFolio);
		nIdEstado=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosEstado);
		destino_gasto=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosDESTINO_GASTO);
		tConcepto=(String)session.getAttribute(GestionInterface.ATT_RelacionGastosTIPO_CONCEPTO);
		
		
	}else 
	response.sendRedirect("RelacionGastos.jsp?tab=1");
		
	boolean bGrupo=false;
	Map<String, Grupo> grupo=usuario.getGrupos();
	Iterator it2 = grupo.entrySet().iterator();
	while(it2.hasNext()){
		Map.Entry r = (Map.Entry)it2.next();
		grupoMat=(String)r.getKey();
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
	
	       var oTablaApartadoEP;
			var oTablaSuficiencias;
			var oTablaGravaApartado;
			var oTablaGravaDetalle;
			var vEstado;
		
		$(document).ready(function() {
			
		
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
				Map botones=nb.getBotones(roles,"Relacion Gastos","ApartadoRelacionGastos");
				Iterator btn = botones.entrySet().iterator();
				while (btn.hasNext()) {
					Map.Entry b = (Map.Entry)btn.next();%>
					$("#<%=b.getValue()%>").attr("disabled", true);<%
				}
			%>
			
			init();
			initDataTable_ApartadoEP();
			vEstado=parseInt($("#nIdEstado").val(),10);
			setInitConditions();
			
			//valida que las partidas que se cargaron en los detalles de las facturas sean las mismas que se capturaron en las partidas
			queryFormPost("validaPartidasFacturasPartidas",{async:false});
			if(parseInt($("#partidasCapturadas").val(),10) > 0){
			alert("Las partidas que se capturaron en las Facturas no son las mismas que se Capturaron en la pestaña de Partidas favor de Revisarlo")
			window.location = "RelacionGastos.jsp?tab=5&cEjercicio="+$("#cEjercicio").val()+"&cIdDocumento="+$("#cIdFolio").val()+"&nIdEstado="+$("#nIdEstado").val()+"&DESTINO_GASTO="+$("#destino_gastoHidden").val()+"&tConcepto="+$("#tConceptoHidden").val();
			return;
			}
			
		
			$("#btnAgregar").button().click(function(){
				if(validarExisteEP()){
					queryFormPost("relacionGastosEPCreate",{async:false});
					$("#ep").val("");
					initDataTable_ApartadoEP();
				}
			});
			$("#btnEditar").button().click(function(){
				editTableEP( $("#tblApartadoEP").dataTable() ) ;
			});
			$("#btnGuardar").button().click(function(){
				guardarCeldasDetalle();
			});
			$("#tblApartadoEP tr").live("dblclick", function() {
				$(this).addClass("row_selected");   
				var anSelected = fnGetSelected( $("#tblApartadoEP").dataTable() );						
				var aData = $("#tblApartadoEP").dataTable().fnGetData(anSelected[0]);
				$("#epTmp").val(aData[0]);
				
				if ($("#nIdEstado").val() <= 2){				         
					queryFormPost("relacionGastosEPDelete", {async: false});
					initDataTable_ApartadoEP();
				}
			});
		});
		
		function init(){
		    $("#mensajeProcesando").css("display","none");
			queryFormPost("EjercicioFiscalActvRead",{async:false});
			$("#cIdFolio").val("<%=cIdDocumento%>");	
			$("#cIdDocumentoCaratula").val("<%=cIdDocumento%>");	
			queryFormPost("llenaCaratulaRelacionGastos", {async:false});
			queryFormPost("obtieneFolioRELAPA", {async:false});
			queryFormPost("obtieneUsuarioCreacion", {async:false});
			queryFormPost("checaMontoTotalLineas",{async:false});
			//borra y se llena tabla de eps
			queryFormPost("llenaTablaEpsAutomaticamenteRELG",{async:false});
			
			
			if($("#lblMotivoRechazo").val() != ""){
				$("#trMovitoRechazo").css("display","");
				$("#divMotivoRechazo").html($("#lblMotivoRechazo").val());
			}
			else
			$("#trMovitoRechazo").css("display","none");
			
						
			$("#lblImporteNeto").val($("#mImporteNeto").val());
			
			$("#lblImporteNeto").formatCurrency();
			validaHabilitaCampos();
			
			if(parseInt($("#nIdEstado").val(),10)== 5){
				//actualizar el usuario
				queryFormPost("actualizaCasoDatoRelacionGastos",{async:false});
				//actualiza fechas
				queryFormPost("actualizaCasoDatoRelacionGastosFechas",{async:false});
				
				
			}
			
			
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=mRelacionGastosMaterialesEP&qw=cIdDocumento='<%=cIdDocumento%>'",
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
					$("#montoSinPresupuesto").val(quitaFmt($("#lblImporteNeto").val()) - quitaFmt($("#montoPresupuesto").val()));
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
			//valida que la partida que se ingresa sea alguna que se registro en las lineas
			queryFormPost("checaPartidasRelacionGastosEP",{async:false});
			if(parseInt($("#partidas").val(),10)== 0){
			alert("La Partida de la Clave Presupuestal seleccionada no existe en la seccion de Partidas.");
			$("#ep").val("");
			res = false;
			}
			
			
			
			$("#epTmp").val($("#ep").val());
			queryFormPost("existeEPRelacionGastos",{async:false});
			
			if($("#existeEPRelacionGastos").val() > 0){
				alert("Ya existe la Clave Presupuestal en el documento.");
				res = false;
			}
			
			return res;
		}
		
		function guardarCeldasDetalle(){
			var aTrs = $("#tblApartadoEP").dataTable().fnGetNodes(); 
			
			if(getTotalEP() <= parseFloat(quitaFmt($("#lblImporteNeto").val()))){
				for ( var i=0 ; i<aTrs.length ; i++ ) {
					resetValueMeses();
					var aData = $("#tblApartadoEP").dataTable().fnGetData( i );
					var jqInputs = $("input", aTrs[i] );
					$("#epTmp").val(aData[0]);
					/*
						for (var j=0 ; j < jqInputs.length ; j++ ){
						var vimporteP = jqInputs[ j ].value ;
						vimporteP = quitaFmt(vimporteP);
						if (parseFloat(vimporteP) > 0 ) {
							if((j+1)<10)
								$("#mes0"+(j+1)).val(vimporteP);
							else
								$("#mes"+(j+1)).val(vimporteP);
								
								queryFormPost("relacionGastosEPUpdate",{async:false});
								}
						
					}*/
				
				}
				initDataTable_ApartadoEP();
				return true;
			}
			else{
			alert("El monto total de las Claves Presupuestales sobrepasa el monto total de la "+$("#modulo").val()+".");
			return false;
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
			if(getTotalEP() <= parseFloat(quitaFmt($("#lblImporteNeto").val()))){
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
									$("#montoSinPresupuesto").val(quitaFmt($("#lblImporteNeto").val()) - quitaFmt($("#montoPresupuesto").val()));
									$("#montoSinPresupuesto").formatCurrency();
								}
							}
						}
					}				
				}
			}
			else{
				alert("El monto total de las Claves Presupuestales sobrepasa el monto total de la "+$("#modulo").val()+".");
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
				if($("#nIdEstado").val() > 2){
				$("#tblApartadoEP").attr("disabled", true);
				$("#nIdClaveEP").attr("disabled", true);
				$("#btnAgregar").attr("disabled", true);
				$("#btnEditar").attr("disabled", true);
				$("#btnGuardar").attr("disabled", true);
				$("#imgApartar").attr("disabled", true);
			}
			else{
				var roles = "<%=roles%>";
				
				
				if(roles.indexOf("ADMIN_RECMAT") < 0 && $("#cIdUsuario").val() != $("#cIdUsuarioCreacion").val()){
					$("#tblApartadoEP").attr("disabled", true);
					$("#nIdClaveEP").attr("disabled", true);
					$("#btnAgregar").attr("disabled", true);
					$("#btnEditar").attr("disabled", true);
					$("#btnGuardar").attr("disabled", true);
					$("#imgApartar").attr("disabled", true);
				}
			}
		}
		
		function getTotalEP(){
			var aTrs = $("#tblApartadoEP").dataTable().fnGetNodes();
			var aData;
			var total = 0;
			var mesActual = <%=mesActual%>;
			
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
		
			 
				return total.toFixed(2);;
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
		   var mensajeAp = "";
	       var Aplica = "0";
	       
	      	   //revisa que las partidas ingresadas sean de comsoc
			    queryFormPost("checaPartidasCOMSOCMaterialesRELG",{async:false});
			   	 if(parseInt($("#partidaCOMSOC").val(),10) > 0 ){
			     //existe una partida de comsoc
			     creaCasoComsoc("RELACIONGASTOS", $("#nFolioConsecutivoRelacionGastosCaratula").val());	
			    			     
			     }
  
             
			   queryFormPost("apartadoRelacionGastosAplicado",{async:false});
				if($("#apartadoAplicado").val() == 0){
					if(guardarCeldasDetalle()){
			   $("#mensajeProcesando").css("display","block");
			   /*
			   if($("#nFolioRelacionGastos").val() == "")
				$.ajax({url: "../../servlet/RelacionGastosServlet" , type:'post' , async: false,data: 'operacion=1', dataType: 'json', success: guardaFolio});		
				//En caso de cualquier error al obtener el folio, sale de la función
				if($("#nFolioRelacionGastos").val() == 0 || $("#nFolioRelacionGastos").val() == ""){
				 $("#mensajeProcesando").css("display","none");
				return -1;
				}
				*/
			
			    $("#folio").val($("#nFolioConsecutivoRelacionGastosCaratula").val());
			    if($( "#cCentroContableUsr" ).val() == "10"){
								getNextSequenceVal({seqName: "CT-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceValCT});
							}else{
								getNextSequenceVal({seqName: "CR-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceVal});
							}
							
							//consecutivo del apartado
							getNextSequenceVal({seqName: "APARTADO", async: false, callback: setSequenceAptd});						
									
							queryFormPost("tRelacionGastosEncabezadoMCreate",{async:false});
							queryFormPost("tRelacionGastosDetalleMCreate",{async:false});
							queryFormPost("tRelacionGastosFinanciamientoMCreate",{async:false});
							queryFormPost("tDocumentacionComprobatoriaDetMCreate",{async:false});
							//se llena tabla de contrarecibo
							queryFormPost("tContrareciboRelacionGastosMCreate",{async:false});
							//se llenan las tablas de apartado
							queryFormPost("tRelacionGastosApartadoEncabezadoMCreate",{async:false});
							queryFormPost("tRelacionGastosApartadoDetalleMCreate",{async:false});
							
				
							//aplicacion contable
							$.ajax({url: "../../servlet/RelacionGastosServlet?nFolioRelacionGastos="+$("#folio").val()+"&cEjercicio="+$("#cEjercicio").val()+"&folioCasoRelacionGastos="+$("#nFolioRelacionGastosCaratula").val()+"&nFolioApartado="+$("#nFolioApartado").val(), type:'post' , async: false,data:'operacion=2', dataType: 'json', success: 
								function(j){
								   $("#mensajeProcesando").css("display","none");
									mensajeAp=j[0].Contable1;
									Aplica=j[0].Aplica;
									alert(mensajeAp);
								}
							});
							
						return Aplica;
					
					}
					   
					    }
					    else{
					    
					 //ya fue aplicado contablememnte solo se actualizan las tablas
					//Si ya existe apartado genera un nuevo contrarecibo y lo actualiza en el apartado que ya existe
					if($("#cCentroContableUsr").val() == "10")
						getNextSequenceVal({seqName: "CT-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceValCT});
					else
						getNextSequenceVal({seqName: "CR-" + $("#cCentroContableUsr").val(), async: false, callback: setSequenceVal});
					
					queryFormPost("contrareciboRelacionGastosEncabezadoUpdate",{async:false});
					queryFormPost("contrareciboDocumentacionComprobatoriaDetUpdate",{async:false});
					queryFormPost("contrareciboRelacionGastosApartadoEncabezadoUpdate",{async:false});
					queryFormPost("tContrareciboRelacionGastosMUpdate",{async:false});
					
					$("#nIdEstado").val("3");
					queryFormPost("estatusRelacionGastosUpdate",{async:false});
					queryFormPost("motivoRechazoRelacionGastosUpdate",{async:false});
					
					queryFormPost("revisaRegistrosCasoOperacionRELG",{async:false});
					if(parseInt($("#registrosCasoOperacion").val(),10) >= 2 ){
					//borra todos los registros que sean menores al maximo id_caso_oper
					queryFormPost("borraRegistrosCasoOperacionRELG",{async:false});
					
					}
					queryFormPost("avanzaCasoRelacionGastos",{async:false});
					//queryFormPost("actualizaCasoDatoRelacionGastos",{async:false});
					
					$("#cAccion").val("REENVIA_RELACIONGASTOS_DE DOCUMENTO_RECHAZADO");
					$("#cIdDocumento").val($("#cIdFolio").val());
					queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
					alert("Relacion de Gastos reenviada a Ventanilla con folio(s) " + $("#nFolioRelacionGastosCaratula").val());
					Aplica="2";
					return Aplica;
					
					}
				
		}
		
		
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
    
    
    /*
    function guardaFolio(j){
    
		var folioPre=-1;
		var folioCaso=-1;
    	folioRel=j[0].Folio1;
    	folioCaso=j[0].Folio2;
        if(folioRel==-1){
      		alert("Ha ocurrido un error al crear el caso, contacte a su soporte");
      		return -1;
        }else{
     	   $("#nFolioRelacionGastos").val(folioRel);
     	   $("#folioCasoRelacionGastos").val(folioCaso);
     	}
	}
    */
    
    
   			function GeneraApartado(){
   			var grupo=<%=bGrupo%>;
   			if(grupo){
   			  var umbral;
   			  //revisa que este totalmente presupuestado el total de la relacion
   			  if(quitaFmt($("#montoSinPresupuesto").val()) > 0){
				alert("Aun no se ha capturado el total de la "+$("#modulo").val()+" en las EP's.");
                return;
			  }
   			
   			 //revisa si la relacion ya tiene lineas asignadas al folio de la relacion
			    queryFormPost("checaLineasExistente",{async:false});
			    if(parseInt($("#totalLineas").val(),10) <=0 ){
			    alert("No se han agregado lineas favor de revisar la seccion de Partidas ")
			    return;
			    
			    }
			  
	             //se revisa que el monto agregado a las lineas corresponda con el monto total de la relacion
			    queryFormPost("checaMontoTotalLineas",{async:false});
		       
		        umbral = Math.abs(parseFloat(quitaFmt($("#lblImporteNeto").val())) - parseFloat(quitaFmt($("#montoNetoRELG").val())));
				if(umbral > .03){
				alert("Atencion el Monto total de la Relacion de Gastos no es Igual al monto Ingresado en la Lineas favor de corregirlo");
				return;
				}
				//deshabilitamos el boton de
				document.getElementById("imgApartar").disabled = true;					
			     var Aplicacion=apartar();
			      			    
				if(Aplicacion == "1"){
				document.getElementById("imgApartar").disabled = true;
				document.getElementById("imgDevolver").disabled = false;
				document.getElementById("btnEditar").disabled = true;
				//Bitácora
				$("#cAccion").val("APRUEBA_APARTADO_RELACIONGASTOS");
				$("#cIdDocumento").val($("#cIdFolio").val());
				queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
				alert("Relacion de Gastos enviada a Ventanilla con folio(s) " + $("#nFolioRelacionGastosCaratula").val());
				location.reload();
		  
			  		}else  if(Aplicacion=="0"){
			  		//habilitamos el boton ya que hubo un error
			  		document.getElementById("imgApartar").disabled = false;
				document.getElementById("imgDevolver").disabled = true;
				document.getElementById("btnEditar").disabled = false;
				//borra de las tablas de encabezado y detalle si es que hubo errores.
				queryFormPost("pa_fallaAplicacionContableRelacionGastosMateriales", { async : false});
				return;
			  		
			  		}else{
			  		    //habilitamos el boton ya que se reenvio la relacion de gastos
			   		document.getElementById("imgApartar").disabled = false;
					document.getElementById("imgDevolver").disabled = true;
					document.getElementById("btnEditar").disabled = false;
					location.reload();
			   		
			  		}
			  		
			  	}else{
					alert("No es posible Generar el Apartado, ya que el modulo de financiero se encuentra cerrado");
				}	
			  		
			  }
			   
    
    
	   function actualizaCaratula(){
	    		$("#cIdDocumentoCaratula").val("<%=cIdDocumento%>");	
				queryFormPost("llenaCaratulaRelacionGastos", {async:false}); 
	    
	   
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

    
	  function devuelveApartado(){
	           	if($("#nIdEstado").val()==("1")|| $("#nIdEstado").val()==("4")|| $("#nIdEstado").val()==("6")){
				return;
				}
				
				 //revisa que las partidas ingresadas sean de comsoc
			    queryFormPost("checaPartidasCOMSOCMaterialesRELG",{async:false});
			   	 if(parseInt($("#partidaCOMSOC").val(),10) > 0 ){
			     //existe una partida de comsoc
			     	//primero checamos si el pedido ya esta siendo trabajado por el usuario de ventanilla
						queryFormPost("usuarioVentanillaReadRelacionGastosCOMSOC", {async: false});
					if(parseInt($("#operacionCOMSOC").val(),10)== 1 && $("#responsableCOMSOC").val()!="CAPTURISTA_AUTORIZACOMSOC")
					{
						alert("No se puede devolver la relacion de gastos esta en un proceso de validacion del COMSOC, la relacion la esta revisando el usuario de ventanilla "+ ' '+$("#responsableCOMSOC").val());
						return;
					}		 
				   
			     }
  
				
				//primero checamos si el pedido ya esta siendo trabajado por el usuario de ventanilla
				queryFormPost("usuarioVentanillaReadRelacionGastos", {async: false});
				if($("#operacion").val()== 2 && $("#responsable").val()!="AUTORIZA_RELACIONGASTOS")
				{
					alert("No se puede eliminar la relacion de gastos, la relacion la esta revisando el usuario de ventanilla "+ ' '+$("#responsable").val());
					return;
				}		 
						
				if($("#nIdEstado").val()!=("4")|| $("#nIdEstado").val()!=("5")){
					var res=window.confirm("¿Está seguro que quiere eliminar el Apartado, la Relacion de Gastos se encuentra en VENTANILLA DE PAGOS y esta pendiente su aprobación,esta acción no se puede deshacer?");
					//Elimina el Precompromiso
				if(res){
				   //deshabilitamos el boton devolver para que solo se le de un click
				    $("#folio").val($("#nFolioRelacionGastos").val());
				    $("#mensajeProcesando").css("display","block");
				   document.getElementById("imgDevolver").disabled = true;
					$.ajax({url: '../../servlet/RelacionGastosServlet?nFolioRelacionGastos='+$("#folio").val()+"&cEjercicio="+$("#cEjercicio").val()+"&nFolioApartado="+$("#nFolioApartado").val(), type:'post' , async: false,data:'operacion=3', dataType: 'json', success: eliminaPrecompromiso});
					
					
					
				}else
					return;
					
				}else{
					actualizaCaratula();
					alert("La Relacion de Gastos  no se puede devolver porque ya ha sido APROBADA ");
				}
			
			
		}
	    
    
    
    
    function eliminaPrecompromiso(j){
           $("#mensajeProcesando").css("display","none");
			var mensaje=j[0].Contable1;
			var devuelve=j[0].Devuelve;
			alert(mensaje);
			if(devuelve!="1"){
			document.getElementById("imgDevolver").disabled = false;
			return;
			}
			
			document.getElementById("imgApartar").disabled = false;
			document.getElementById("imgDevolver").disabled = true;
			$('#tblApartadoEP').attr('disabled', false);
			document.getElementById("ep").disabled = false;	
			document.getElementById("nIdClaveEP").disabled = false;
			document.getElementById("btnAgregar").disabled = false;
			document.getElementById("btnEditar").disabled = false;
			document.getElementById("btnGuardar").disabled = false;
			
			//Bitácora
			$("#cAccion").val("DEVUELVE_APARTADO_RELACIONGASTOS");
			$("#cIdDocumento").val($("#cIdFolio").val());
			queryFormPost("sp_mBitacoraMovimientosCreate", { async : false});
			actualizaCaratula();
			location.reload();
		
	}
    
    
    
    
    
    
    function setInitConditions(){
    
  		if(vEstado==3 || vEstado==4 || vEstado==6  ){
			//Se deshabilita el boton de aprobar en caso que ya exista un precompromiso
			document.getElementById("imgApartar").disabled = true;
			$('#tblApartadoEP').attr('disabled', true);
			document.getElementById("ep").disabled = true;	
			document.getElementById("nIdClaveEP").disabled = true;
			document.getElementById("btnAgregar").disabled = true;
			document.getElementById("btnEditar").disabled = true;
			document.getElementById("btnGuardar").disabled = true;
			document.getElementById("imgDevolver").disabled = false;
			
			
						
		}else if( vEstado==1 ){
		  
		  document.getElementById("imgApartar").disabled = false;
			$('#tblApartadoEP').attr('disabled', false);
			document.getElementById("ep").disabled = false;	
			document.getElementById("nIdClaveEP").disabled = false;
			document.getElementById("btnAgregar").disabled = false;
			document.getElementById("btnEditar").disabled = false;
			document.getElementById("btnGuardar").disabled = false;
			document.getElementById("imgDevolver").disabled = true;
		}
		else if(  vEstado==5 ){
		    document.getElementById("imgApartar").disabled = false;
			$('#tblApartadoEP').attr('disabled', false);
			document.getElementById("ep").disabled = false;	
			document.getElementById("nIdClaveEP").disabled = false;
			document.getElementById("btnAgregar").disabled = false;
			document.getElementById("btnEditar").disabled = false;
			document.getElementById("btnGuardar").disabled = false;
			document.getElementById("imgDevolver").disabled = false;
		
		
		}
		   
		
		    
		
		}
		
		
		
	function cmdImprimir() {
	var elFormato="";
	queryFormPost("obtieneContrareciboRELGImprimir",{async:false});
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

}

function anexo(pfmt) {

	if ($("#cllave").val() > 1 && pfmt == "PolizaPago") {

		window.open("../admin/SeguridadCatalogos?" + "catalogo=ANEXO"
				+ "&accion=run" + "&rn=Anexo1.jasper"
				+ "&swhere=  and caNoContrarrecibo ='" + $("#caNoContrarreciboImprimir").val()
				+ "' ", "Anexo",
				"scrollbars=1, resizable=yes, width=1024, height=768");
	}
}
	
    
		
	</script>
</head> 
<body id="dt_example" bottomMargin="0" bgcolor="red" leftmargin="0"	topmargin="0">
<form>
<div id="container" class="container" style="text-align: left">
	<table>
		<tr>
			<td>
				<fieldset>
				<legend>Informaci&oacute;n de la Relaci&oacute;n de Gastos</legend>
				<table border="0" align="center" width="100%">
					
					<tr align="left">
						<td colspan="2">
							<input name="lblcIdDocumento" id="lblcIdDocumento" type="text" style="width: 600px"  style="border: 0px solid black;" readonly="readonly"/>  
						</td>								
					</tr>
				   <tr align="left">
						<td colspan="2">
							<input name="lblcIdUnidadEjecutora" id="lblcIdUnidadEjecutora" type="text" style="width: 600px" readonly="readonly" style="border: 0px solid black;"></input>  
						</td>								
					</tr>
					
					<tr align="left">
						<td colspan="2">
							<input name="lblnIdEstado" id="lblnIdEstado" type="text" readonly="readonly" style="border: 0px solid black; width: 45em;"></input>  
						</td>								
					</tr>
					 <tr>
							<td align="left">
								Importe Neto Relacion Gastos:
								<input type="text" style="width: 500px" name="lblImporteNeto"
									id="lblImporteNeto" readonly
									style="border-width:0; background-color:transparent" />
							</td>
						</tr>
					
					<tr>
							<td align="left">
								<!--Importe Neto Partidas:-->
								<input type="hidden" style="width: 500px" name="montoNetoRELG"
									id="montoNetoRELG" readonly
									style="border-width:0; background-color:transparent" />
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
											<img id="imgContrarecibo" src="../../imagenes/icono_PDF.jpg" style="cursor: pointer" onclick="cmdImprimir();" />&nbsp;Reporte Relacion Gastos
											<img id="imgDevolver" src="../imagenes/arrow_left_blue_round.png" style="cursor: pointer" onclick="devuelveApartado();" />&nbsp;Devolver
											<img id="imgApartar" src="../imagenes/accept_green.png" style="cursor: pointer" onClick="GeneraApartado()()"/>&nbsp;Apartar
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
											<input type="button" name="btnAgregar" id="btnAgregar" size="5" value="Agregar"/>
											<input type="button" name="btnEditar" id="btnEditar" size="5" value="Editar"/>
											<input type="button" name="btnGuardar" id="btnGuardar" size="5" value="Guardar"/>
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
<!-- Campos configurables del modulo -->
<input type="hidden" id="modulo" name="modulo"  value="Relacion Gastos"/>
<input type="hidden" id="TIPO_DOCTO" name="TIPO_DOCTO"  value="RELACIONGASTOS"/>

<!-- Campos principales -->
<input type="hidden" id="cIdFolio" name="cIdFolio"  />
<input type="hidden" id="Imp_Bruto" name="Imp_Bruto" />
<input type="hidden" id="Imp_Iva" name="Imp_Iva" />
<input type="hidden" id="mImporteNeto" name="mImporteNeto" />
<input type="hidden" id="cIdUsuarioCreacion" name="cIdUsuarioCreacion" />
<input type="hidden" id="cIdUsuario" name="cIdUsuario" value="<%=usuario.getLogin() %>"/>
<input type="hidden" id="cIdUsuarioNombre" name="cIdUsuarioNombre" value="<%=usuario.getNombre()%>"/>
<input type="hidden" id="existeEPRelacionGastos" name="existeEPRelacionGastos"/>
<input type="hidden" id="epTmp" name="epTmp"/>
<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="<%=usuario.getU_UR() %>"/>
<input type="hidden" id="cCentroContableUsr" name="cCentroContableUsr" value="<%=cCentroContable%>"/>

<input type="hidden" id="destino_gastoHidden" name="destino_gastoHidden" value="<%=destino_gasto%>"/>
<input type="hidden" id="tConceptoHidden" name="tConceptoHidden" value="<%=tConcepto%>"/>

<input type="hidden" id="cEjercicio" name="cEjercicio" />
<input type="hidden" id="mImporteTmp" name="mImporteTmp" />
<input type="hidden" id="nIdEstado" name="nIdEstado" />
<input type="hidden" id="cMesTmp" name="cMesTmp" />
<input type="hidden" id="cIdDocumentoCaratula" name="cIdDocumentoCaratula" />
<input type="hidden" id="lblImporteNeto" name="lblImporteNeto" />
<input type="hidden" id="cEvento" name="cEvento" />
<input type="hidden" id="tConcepto" name="tConcepto" />
<input type="hidden" id="nFolioApartado" name="nFolioApartado" />

<input type="hidden" id="nFolioRelacionGastos" name="nFolioRelacionGastos"  />
<input type="hidden" id="nFolioRelacionGastosCaratula" name="nFolioRelacionGastosCaratula"  />
<input type="hidden" id="folioCasoRelacionGastos" name="folioCasoRelacionGastos" />
<input type="hidden" id="cAccion" name="cAccion" />
<input type="hidden" id="cIdDocumento" name="cIdDocumento" />
<input type="hidden" id="totalLineas" name="totalLineas" />
<input type="hidden" id="montoNetoRELG" name="montoNetoRELG" />
<input type="hidden" id="montoBrutoRELG" name="montoBrutoRELG" />
<input type="hidden" id="apartadoAplicado" name="apartadoAplicado" />
<input type="hidden" id="caNoContrarreciboImprimir" name="caNoContrarreciboImprimir" />
<input type="hidden" id="cIdRFC_RelacionGasto" name="cIdRFC_RelacionGasto" />
<input type="hidden" id="partidas" name="partidas" />
<input type="hidden" id="operacion" name="operacion" />
<input type="hidden" id="responsable" name="responsable" />
<input type="hidden" id="partidasCapturadas" name="partidasCapturadas" />









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
<input type="hidden" id="folio" name="folio" /><!-- folio de del pago directo en financiero -->
<input type="hidden" id="caNoContrarrecibo" name="caNoContrarrecibo" />

<!-- Campos auxiliares para obtener el folio de la relacion de gastos desde el query que llena la caratula -->
<input type="hidden" id="nFolioConsecutivoRelacionGastosCaratula" name="nFolioConsecutivoRelacionGastosCaratula" />
<input type="hidden" id="registrosCasoOperacion" name="registrosCasoOperacion" />
<input type="hidden" id="partidaCOMSOC" name="partidaCOMSOC" />
<input type="hidden" id="responsableCOMSOC" name="responsableCOMSOC" />
<input type="hidden" id="operacionCOMSOC" name="operacionCOMSOC" />

</form>
</body>
</html>
