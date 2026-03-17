<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>

<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String u_login = "";
	String mensaje = "";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	if (c == null) { response.sendRedirect("../index.jsp"); return; }

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}

	if (request.getParameter("msg") != null && !"".equals(request.getParameter("msg"))) {
		
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic( GestionInterface.ATT_CONEXION); 
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	CasoBusinessLogic cbl = new CasoBusinessLogic( GestionInterface.ATT_CONEXION);

	String select = c.getTipoCaso().getGavetaAsociada() + "_G"	+ c.getIdGabinete();//se usa por separado abajo
	String cAplicaDocto = "No";
	
	if (request.getParameter("aplicaDocto") != null && request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
			
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	u_login = usuario.getLogin();

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Penas Convencionales Integracion</title>
		
		<link rel="stylesheet" type="text/css"	href="css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
		<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
		
		<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
		<script type="text/javascript" src="../Generador/js/crud.js"></script>
		<script type="text/javascript" src="../js/jsquery.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
		<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
		<script type="text/javascript" src="../Generador/js/Moment.js"></script>
		<script type="text/javascript" src="../js/catalogo/general.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
		<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
		<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>

<script type="text/javascript">
$(document).ready(function(){
	
		//querySelectPost("tGrupoOpAjenasRead", "cBeneficiario", {async: false });
		$("#cBeneficiario").val("5");
		$("#cCentroContable").val( "<%=cCentroContable%>");
		$("#id_caso").val( <%=request.getParameter("folio")%>);
		
		$("#fBusquedaDe").val( "<%=today%>" );
		$("#fBusquedaHasta").val( "<%=today%>" );
		queryFormPost("cEjercicioRead",{async: false });
		$( "#fBusquedaDe" ).datepicker({  			
			dateFormat: "dd/mm/yy", 
			changeYear: true, 
			changeMonth: true });
		$( "#fBusquedaHasta").datepicker({  			
			dateFormat: "dd/mm/yy", 			
			changeYear: true, 
			changeMonth: true });
		$("#pbLayout").hide();		
	
		
		$('#dialog-carga').dialog({
			
		    autoOpen: false,
		    modal: true,
		    resizable: false,
		    width: 230,
		    heigth: 135,
		    title: 'Guardando Informacion',
		    show: "blind",
		    hide: "scale",
		    closeOnEscape: false,
		    beforeClose: function( event, ui ) {
						return false;			
			},
		    overlay: { backgroundColor: '#FFF',
					   opacity: 6.5   
		}
	});	
	$("#checkAll").change(function(){
		if ($('#checkAll').is(':checked')){
			$("input:checkbox").attr('checked', 'checked');
			importeTotal();
		}else{
			$("input:checkbox").removeAttr('checked');
			$("#mImporteInt").val(0);
			$("#mImporteconAjuste").val(0);
			$("#mImporteAjuste").val(0);
			
		}
	});
		
		var GridOAI = $("#GridOAI").dataTable({
						bPaginate : false,			//muestra las flechas y pagina dependiendo el rango
						bLengthChange : true,  //
						bInfo : true,			//es el que muestra los numeros de los registros		
						sScrollX: "1150px",
						sScrollY: "500px",
						bJQueryUI: true,  //se coloca el dise?o que contiene en css
						bFilter : false,
						bSort : false, // para colocar los filtros en los campos
						bDestroy: true,
						bRetrieve:true,
						//left:true,
						bAutoWidth:false,
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
						},aoColumnDefs:[{"bVisible":true, "aTargets":[6]} ]
			});

				var GridOAISeleccionados = $("#GridOAISeleccionados").dataTable({
					
						bProcessing: true,
						bJQueryUI: true,
						bAutoWidth : true,
						bRetrive: true,
						bDestroy: true,
			    		bPaginate: false,
						iDisplayLength: 10,
			      		sScrollY: "450px", 
			      		sScrollX: "1200px",
			      		Height: "450px", 
						aaSorting: [[ 1, "asc" ]] ,
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
									sSearch: "Buscar:",
									oPaginate: {
										sFirst:    "Primero",
										sPrevious: "Ant.",
										sNext:     "Sigte.",
										sLast:     "&Uacute;ltimo"
									}
							}
								
			
			});
			
	$("#pbBuscar").button().click( function(){ buscarOperacionesAjenas();  });
	$("#pbLayout").button().click( function(){    generarLayout(); });
	$('#pbDesmarcar').button().click( function() { desmarcar();  });
	//$('#pbProcesar').button().click( function() { procesar();  });
	
	$("#chk_IP").change(function(){
		if ($("#chk_IP").prop("checked")){
			$("#chk_radicado").prop("checked",false);
			$("#chk_radicado").attr('disabled', true);
		}else{
			$("#chk_radicado").attr('disabled', false);
		}
		
	});
	$("#chk_radicado").change(function(){
		if ($("#chk_radicado").prop("checked")){
			$("#chk_IP").prop("checked",false);
			$("#chk_IP").attr('disabled', true);
		}else{
			$("#chk_IP").attr('disabled', false);
		}
		
	});

});

function buscarOperacionesAjenas(){
		$('#mImporteInt').val(0);
		$('#mImporteconAjuste').val(0);
		$('#mImporteAjuste').val(0);
		$('#cxpAjuste').val("");
		$('#parametrosAjustes').val("");
		
		$("#GridOAI").dataTable().fnClearTable();
		var szTemp = "";
		
		/* Verificar los Filtros Para la Consulta */
		//if ($('#cBeneficiario').val() == 0 ){   alert( " Tiene que elegir un Grupo de Retenciones para Continuar" );  return;  }	
		if ($('#fBusquedaDe').val() == "" ){    
			Swal.fire({ icon: 'warning',
						text: "Debe Capturar la Fecha de Inicio para Continuar"});
			return;  
		}	
		
		if ($('#fBusquedaHasta').val() == "" ){ 
			Swal.fire({ icon: 'warning',
						text: "Debe Capturar la Fecha de Fin para Continuar"}); 
			return;  
		}
		
		szTemp += " cBeneficiario = '" + $("#cBeneficiario").val() + "'";
				
		var fDe = $("#fBusquedaDe").val().split("/");
		var fBusquedaDe = fDe[2]+"-"+fDe[1]+"-"+fDe[0];
		var fHa = $("#fBusquedaHasta").val().split("/");
		var fBusquedaHasta = fHa[2]+"-"+fHa[1]+"-"+fHa[0];
				
		szTemp += " and fCreacion between '" + fBusquedaDe + "' and '" + fBusquedaHasta + "'";
		$("#szTemp").val(szTemp);
		/*if ($("#chk_IP").prop("checked"))
			szTemp += " AND cIngresosPropios='S' ";
		else if ($("#chk_radicado").prop("checked"))
			szTemp += " AND cRadicado='S' ";
		else
			szTemp += " AND cIngresosPropios='N' AND cRadicado='N' ";
		*/
		if (szTemp != '') szTemp = "&qw=" + szTemp;
		$("#esperardet").show();
						
		oTblOperAjena = $("#GridOAI").dataTable({
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
								bPaginate: false,
								bRetrive: true,
								bDestroy: true,
				        		bFilter : false,
								bServerSide: true,
								bProcessing: true,
								bJQueryUI: true,
		    					bAutoWidth : false,
								sScrollX: "1150px",
								sScrollY: "500px",
								sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vw_PenasConvIntegracion" + szTemp ,
								aaSorting: [[ 3, "desc" ]] ,
								aoColumns: [
									{ sName: "checkRenglon",		 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "nDocRenglon",	 	 	 bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"},
									{ sName: "nFolioPagoPenasConvT",	 bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"},
									{ sName: "caNoContrarreciboPPT", bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "EpPPT",				 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
									{ sName: "mTotalFormat",		 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
									{ sName: "cUnidadResponsablePPT",bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cIDRFCPPT",			 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cCentroContablePPT",	 bSearchable: true,		bSortable: false, bVisible: false, sClass: "alignRight"},
									{ sName: "nFolioDocCXPT",		 bSearchable: true,		bSortable: false, bVisible: false, sClass: "alignCenter"},
									{ sName: "caNoContrarreciboCXPT",bSearchable: true,		bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cTipoDocT",			 bSearchable: true,		bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cMes",			 	 bSearchable: true,		bSortable: false, bVisible: true, sClass: "alignCenter"}
									
								]
							});
						
					$("#esperardet").hide();
					setTimeout('importeTotal()', 3000);
					$('#Desmarcar').val("Desmarcar");		
					parent.document.getElementById("pb_save").disabled=false;
		
}
		
function importeTotal(){
		
		var oTable = $('#GridOAI').dataTable();
		var aData = oTable.fnGetData();
		var nRows = aData.length;
		var mImporteTotal = 0;
		var data = $('#GridOAI').dataTable().fnGetNodes();
		
		for(var i=0; i < nRows; i++){
			if(<%=id_oper == 1%>){
				if ( $('input', data[i])[0].checked ){
					mImporteTotal = mImporteTotal + Number( quitaFmt(aData[ i ][5]) );
				}					
			}else{
				mImporteTotal = mImporteTotal + Number( quitaFmt(aData[ i ][5]) );		
			}					
		}
		$('#mImporteInt').val( mImporteTotal );
		$("#mImporteTotal").val( mImporteTotal );
		$('#mImporteInt').formatCurrency();
		
		if ($('#cBeneficiario').val()!=6 ){ 
			
			mImporteTotal = Number(  mImporteTotal.toFixed(2)  );
			$("#mImporteconAjuste").val( mImporteTotal - (   mImporteTotal - parseInt( mImporteTotal,10 ) ) );
			$("#mImporteAjuste").val( mImporteTotal - parseInt(mImporteTotal,10) );
			
			$('#mImporteconAjuste').formatCurrency();
			$('#mImporteAjuste').formatCurrency();
			
			if(<%=id_oper == 1%>){
				var importe = 0;
				for(var i=nRows-1; i>=0; i--){
					if ( $('input', data[i])[0].checked ){
						importe = Number(quitaFmt(aData[i][5]));
						if (importe>quitaFmt($('#mImporteAjuste').val())){
							$("#parametrosAjustes").val(aData[i][2]+"/"+aData[i][1]+"/"+aData[i][10]+"/"+quitaFmt($('#mImporteAjuste').val())+"/"+$('#cBeneficiario').val());
							$('#cxpAjuste').val(aData[i][10]);
							i=0;
						}
						
					}					
				}					
			}
			
		}else{
			$('#mImporteconAjuste').val(mImporteTotal);
			$('#mImporteconAjuste').formatCurrency();
			$('#mImporteAjuste').val(0);
			$('#mImporteAjuste').formatCurrency();
			$('#cxpAjuste').val("");
		}
}
	
function operachk(cContraRecibo){

		if ( $("." + cContraRecibo)[0].defaultChecked ){
			$("." + cContraRecibo).attr('checked', false);
		}else{
			$("." + cContraRecibo)[0].defaultChecked = true;
			$("." + cContraRecibo).attr('checked', true);
		}
		 importeTotal();
}
	
function onSubmit(id_oper){
  		
		var p = window.parent;
  		var valida_campos = true;
  		
  		queryFormPost("existePenaConvIntRead", {async: false });	
		
		if($("#existePenaInt").val() == "0"){
  		
	  		try{
				
				if ( Number( quitaFmt( $('#mImporteconAjuste').val() ) ) == 0 ){ //mImporteInt
					Swal.fire({ icon: 'warning',
								text: "No es Posible Realizar esta Operacion ya que el Importe Total es $0.00"});					
					return false;
				}	
							
				p.gestion.setFolio( $("#FOLIO").val() );
				p.gestion.setOperador( $("#OPERADOR").val() );
				p.gestion.setFechaDocumento( $("#fCaptura").val() );
				p.gestion.setEjercicioFiscal( $("#cEjercicio").val() );
				p.gestion.setConceptoMov("Aplicación Pago de Penas Convencionales Integración");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setAplicadoCont("false");
	            
				var nretval = cmdGuardar();
				
				if (nretval == -1) {
					return false;
				}
				$('#mImporteconAjuste').formatCurrency();
	
				$( "#dialog-carga" ).dialog( "close" );
				
			}catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
		}else{
			parent.document.getElementById("pb_save").style.visibility='hidden';	
			parent.document.getElementById("pb_save").disabled=false;
		}
		return valida_campos;
  	}

function onPostSubmit(id_oper){
	
  		return true;
}

function onLoadPlantilla(){
		querySelectPost("rCatalogoLeyendaOARead", "cveLeyenda",{async: false }); 
		

		if(<%=id_oper == 1%>){
		
			queryFormPost("existePenaConvIntRead", {async: false });	
		
			if($("#existePenaInt").val() == "1"){
				$("#pbBuscar").hide();
				$("#fBusquedaDe").attr('disabled', true);
				$("#fBusquedaHasta").attr('disabled', true);
				$("#cBeneficiario").attr('disabled', true);
				queryFormPost("CXPPenaConvIntegradaRead", {async: false });
				Carga();
				parent.document.getElementById("pb_cancel").style.visibility='visible';
				parent.document.getElementById("pb_cancel").disabled=false;
				parent.document.getElementById("pb_send").style.visibility='hidden';
	
				parent.document.getElementById("pb_send").disabled=true;
			    parent.document.getElementById("pb_save").disabled=false;
			
				document.getElementById("cLineaCapB").style.visibility='visible';
				document.getElementById("cGuardaLC").style.visibility='visible';
				
			}else{
				parent.document.getElementById("pb_cancel").style.visibility='visible';
				parent.document.getElementById("pb_cancel").disabled=false;
				parent.document.getElementById("pb_send").style.visibility='hidden';
	
				parent.document.getElementById("pb_send").disabled=true;
			    parent.document.getElementById("pb_save").disabled=true;
			
				document.getElementById("cLineaCapB").style.visibility='hidden';
				document.getElementById("cGuardaLC").style.visibility='hidden';
			}
			
		}
		if(<%=id_oper == 2%>){
			
			$("#pbBuscar").hide();
			$("#pbLayout").show();
			$("#checkAll").hide();
			$("#fBusquedaDe").attr('disabled', true);
			$("#fBusquedaHasta").attr('disabled', true);
			$("#cBeneficiario").attr('disabled', true);
			
			queryFormPost("CXPPenaConvIntegradaRead", {async: false });
			Carga();
			
			queryFormPost("LCPenasConvIntRead", {async: false });
			$("#cLineaCap").val($("#cLineaCaptura").val());
				
			parent.document.getElementById("pb_save").style.visibility='hidden';
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_send").disabled=false;
			
		}
		
		if(<%=id_oper == 3%>){
			$("#pbBuscar").hide();
			$("#pbLayout").show();
			$("#checkAll").hide();
			$("#fBusquedaDe").attr('disabled', true);
			$("#fBusquedaHasta").attr('disabled', true);
			$("#cBeneficiario").attr('disabled', true);
			
			queryFormPost("CXPPenaConvIntegradaRead", {async: false });
			Carga();
							
			parent.document.getElementById("pb_save").style.visibility='hidden';
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_send").disabled=false;
			parent.document.getElementById("pb_cancel").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			parent.document.getElementById("cLineaCapB").style.visibility='hidden';
		}
}

function ResponsableSiguiente(id_oper){

  	switch(parseInt(id_oper)){
		case 1:
			return "AUTORIZA_PENASCONVINTEGRA";
			break;
	
		case 2 :
			return "CONSULTA_PENASCONVINTEGRA";
			break;	
	}
}
  	  	
function OperacionSiguiente(id_oper){

  	switch(parseInt(id_oper)){
		case 1:
			return "autoriza_penasconvintegra";
			break;
		
		case 2 :
			return "consulta_penasconvintegra";
			break;	
	}
}

function onPostDisplay(id_oper){
		
	parent.document.getElementById("pb_send").style.visibility='visible';
	parent.document.getElementById("pb_send").disabled = false;
	
}

function formSubmited() {

	//alert("Lo que sea!");
}		
	
function Carga(){
	
		$("#GridOAI").dataTable().fnClearTable();
		
		var szTemp = " nFolioPagoPenasConvInt = " + $("#id_caso").val();
		$("#szTemp").val(szTemp);
			
		if (szTemp != '') szTemp = "&qw=" + szTemp;
							
		oTblOperAjena = $("#GridOAI").dataTable({
			
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
								bPaginate: false,
								bRetrive: true,
								bDestroy: true,
								
				        		bFilter : false,
								bServerSide: true,
								bProcessing: true,
								bJQueryUI: true,
		    					bAutoWidth : false,
								sScrollX: "1150px",
								sScrollY: "500px",
								sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=tPagoPenasConvIntegradoDet" + szTemp ,
								aaSorting: [[ 3, "asc" ]] ,
								aoColumns: [
									{ sName: "nFolioPagoPenasConvInt",		 bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"},
									{ sName: "nDocRenglon",	 	 	 bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"},
									{ sName: "nFolioPagoPenasConv",	 bSearchable: false,    bSortable: false, bVisible: false, sClass: "alignCenter"},
									{ sName: "caNoContrarreciboPP", bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "Ep",				 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
									{ sName: "mImporteMasIva",		 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
									{ sName: "cUnidadResponsable",bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cIDRFC",			 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cCentroContable",	 bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignRight"},
									{ sName: "nFolioDoc",		 bSearchable: true,	bSortable: false, bVisible: false, sClass: "alignCenter"},
									{ sName: "caNoContrarrecibo",bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cTipoDoc",			 bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"},
									{ sName: "cMes",			 bSearchable: true,	bSortable: false, bVisible: true, sClass: "alignCenter"}
									
								]
							});
}	
	
	function grupoRetencion(){
		
		$("#cIdTipodocumento").val("");
		$("#cDescripcionPoliza").val("");
		queryFormPost("tGrupoOpAjenasOnclicRead", {async: false });
		
	}
	
	function cmdGuardar(){
		
		if(<%=id_oper == 1%>){
			GuardarIntegracion();
			procesar();
		}
		//parent.document.getElementById("pb_save").disabled = true;
	    return 0;
		
	}
	
	function setSequenceVal(seqValue) {
		seqValue = "000000" + seqValue;
		seqValue = "1" + seqValue.substr(seqValue.length - 5);
		seqValue = $("#cCentroContable").val() + "AI" + $("#aEjercicioFiscal").val() + seqValue;
		$("#caNoContrarreciboInt").val( seqValue );
	}
	
	var tipo;

    function quitaFmt( val ) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");

	   	if ( val.indexOf( "(" ) >= 0 ) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
	   	}
	   	return val;
	}	
    
	var nDocRenglonInt = 1;
	function GuardarIntegracion(){		
		
		$("#GridOAISeleccionados").dataTable().fnClearTable();
		var table = document.getElementById("GridOAI");
		var data = $('#GridOAI').dataTable().fnGetNodes();
	
		var oTable = $('#GridOAI').dataTable();
		var aData = oTable.fnGetData();
		var nRows = $("#GridOAI tr").length -1 ;
		var arrlist = new Array();
		var j = 0;
			
			for(var i=0; i < nRows; i++){
				
				if ( $('input', data[i] )[0].checked ){
					
						var paso = aData[ i ];
						paso[13] = nDocRenglonInt;
						arrlist[ j++ ] = paso;
						nDocRenglonInt++;
				}
			}
			
			$("#GridOAISeleccionados").dataTable().fnAddData( arrlist );
		
			$('#GridOAI').dataTable({
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
							sSearch: "Buscar:",
							oPaginate: {
								sFirst:    "Primero",
								sPrevious: "Ant.",
								sNext:     "Sigte.",
								sLast:     "&Uacute;ltimo"
							}
					},
					bServerSide: true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vw_PenasConvIntegracion&qw= caNoContrarreciboPPT = '0' ",
					bProcessing: true,
					bJQueryUI: true,
					bAutoWidth : true,
					bRetrive: true,
					bDestroy: true,
		    		bPaginate: false,
					iDisplayLength: 10,
		      			sScrollY: "450px", 
		      			sScrollX: "1200px",
		      			Height: "450px",
					aoColumns: [
						{ sName: "checkRenglon",		 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "nDocRenglon",	 	 	 bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"},
						{ sName: "nFolioPagoPenasConvT",	 bSearchable: false,	bSortable: false, bVisible: false, sClass: "alignCenter"},
						{ sName: "caNoContrarreciboPPT", bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "EpPPT",				 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignLeft"},
						{ sName: "mTotalFormat",		 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignRight"},
						{ sName: "cUnidadResponsablePPT",bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "cIDRFCPPT",			 bSearchable: false,	bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "cCentroContablePPT",	 bSearchable: true,		bSortable: false, bVisible: false, sClass: "alignRight"},
						{ sName: "nFolioDocCXPT",		 bSearchable: true,		bSortable: false, bVisible: false, sClass: "alignCenter"},
						{ sName: "caNoContrarreciboCXPT",bSearchable: true,		bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "cTipoDocT",			 bSearchable: true,		bSortable: false, bVisible: true, sClass: "alignCenter"},
						{ sName: "cMes",			 	 bSearchable: true,		bSortable: false, bVisible: true, sClass: "alignCenter"}
					]
			});	
			
	}	    	
    
function procesar(){

	$('#mImporteconAjuste').val(quitaFmt($('#mImporteconAjuste').val()));
	getNextSequenceVal({seqName: "PPCI-" + $("#cCentroContable").val(), async: false, callback: setSequenceVal});
	
	var informacionEnviar = $("#GridOAISeleccionados").dataTable().fnGetData();
	var parametrosAjustes = $("#parametrosAjustes").val().split("/");
	for( var cnt = 0; cnt< informacionEnviar.length; cnt++){	 
		createInput('penaConvInt', 'nDocRenglon', informacionEnviar[cnt][1]);	
		createInput('penaConvInt', 'nFolioPagoPenasConv', informacionEnviar[cnt][2]);
		createInput('penaConvInt', 'caNoContrarreciboPP', informacionEnviar[cnt][3]);
		createInput('penaConvInt', 'Ep', informacionEnviar[cnt][4]);
		createInput('penaConvInt', 'mImporteMasIva', quitaFmt( informacionEnviar[cnt][5] ) );
		createInput('penaConvInt', 'cUnidadResponsable', informacionEnviar[cnt][6]);
		createInput('penaConvInt', 'cIDRFC', informacionEnviar[cnt][7]);
		createInput('penaConvInt', 'cCentroContrable', informacionEnviar[cnt][8]);
		createInput('penaConvInt', 'nFolioDoc', informacionEnviar[cnt][9]);	
		createInput('penaConvInt', 'caNoContrarrecibo', informacionEnviar[cnt][10]);	
		createInput('penaConvInt', 'cTipoDoc', informacionEnviar[cnt][11]);	
		createInput('penaConvInt', 'cMes', informacionEnviar[cnt][12]); 
		createInput('penaConvInt', 'nDocRenglonInt', informacionEnviar[cnt][13]); 
		
		if ($.trim($("#mImporteAjuste").val())!="" 
			&& informacionEnviar[cnt][2]==parametrosAjustes[0]  //Folio operacion Ajena
			&& informacionEnviar[cnt][1]==parametrosAjustes[1]  //nDocRenglon
			&& informacionEnviar[cnt][10]==parametrosAjustes[2] //CXP Individual
			){
			createInput('penaConvInt', 'mAjuste', quitaFmt($("#mImporteAjuste").val()));//parametrosAjuste[3]);
		}else{
			createInput('penaConvInt', 'mAjuste', 0);
		}
		
	}

	$("#accion").val("1");
	
	$.ajax({
		type : "POST",
		url : "../PenasConvencionalesInt/crear",
		cache : false,
		async : false,
		data : $("#penaConvInt").serialize(),
		error : function(xhr, textStatus, errorThrown) {
			Swal.fire({ icon: 'warning',
						text: "Advertencia: " + xhr.responseText + "\nEstatus: " + textStatus + "\n" + errorThrown });			
		},
		success : function(RS) {
			
			var exito = RS.success;
			if( "true" == exito){				
				var nInsertados = parseInt( RS.data_1.result, 10 ) - 1;
								
				queryFormPost("statusPagoPenasUpdate",{async: false });//Actualiza el nEnviadoSicop de las OA integradas
				queryFormPost("ajustePenaConvIntegradaUpdate",{async: false });//Actualiza el ajuste en el registro de la Integracion
				queryFormPost("spp_AjustePenaConv_Update",{async: false });//Actualiza el ajuste en la OA Individual				
								
				$("#pbBuscar").hide();
				$("#pbLayout").show();
				parent.document.getElementById("pb_save").style.visibility='hidden';
				parent.document.getElementById("pb_save").disabled=false;
				Swal.fire({ icon: 'success',
							text: "Se Guardaron exitosamente: " + nInsertados + " Registros." });				
			}else
				Swal.fire({ icon: 'info',
							text: RS.data_1.result });
		}
	});	
    
    //borraElementos();
	$("#dialog-carga" ).dialog( "close" );
}
	
function createInput(form, name, value){
		$('<input>').attr({
			type: 'hidden',
			name: name,
			value: value
		}).addClass('remove').appendTo('#' + form);
	}
    
    function borraElementos(){
		$('.remove').remove();	
	}
    
    function setSequenceVal(seqValue) {
    	
		seqValue = "000000" + seqValue;
		seqValue = "1" + seqValue.substr(seqValue.length - 5);
		seqValue = $("#cCentroContable").val() + "PI" + $("#cEjercicio").val() + seqValue;
		$("#caNoContrarreciboInt").val( seqValue );
	}
    
function desmarcar(){
    	
    	var oTable = $('#GridOAI').dataTable();
		var aData = oTable.fnGetData();
		var nRows = aData.length;

		if( $( this ).html() == "<SPAN class=ui-button-text>DesMarcar</SPAN>" ){
			$( this ).html("<SPAN class=ui-button-text>Marcar</SPAN>");
			for(var i=0; i < nRows; i++){
				$("." + aData[ i ][ 0 ]).attr('checked', false);	
			}

		}else{
			
			$( this ).html("<SPAN class=ui-button-text>DesMarcar</SPAN>");
			for(var i=0; i < nRows; i++){
				$("." + aData[ i ][ 0 ]).attr('checked', true);	
				$("." + aData[ i ][ 0 ])[0].defaultChecked = true;
			
			}

		}
    	importeTotal();
    }
    
function generarLayout(){
		if ($.trim($("#cIDRFC2 option:selected").val())==""){			
			Swal.fire({ icon: 'warning',
						text: "Favor de seleccionar una RFC" });
			return;
		}
		if ($.trim($("#ctaBancarias option:selected").val())==""){		
			Swal.fire({ icon: 'warning',
						text: "Favor de seleccionar una cuenta Bancaria" });
			return;
		}
		if ($.trim($("#cveLeyenda option:selected").val())==""){
			Swal.fire({ icon: 'warning',
						text: "Favor de seleccionar una Leyenda" });			
			return;
		}
    	$("#CuentaBancaria").val($("#ctaBancarias option:selected").val());
    	$("#claveLeyenda").val($("#cveLeyenda option:selected").val());    	
    	$("#accion").val("2");
    	$("#penaConvInt").submit();
    	
    	queryFormPost("statusPenasConvIntUpdate",{async: false });//Actualiza el nEnviadoSicop = 2; significa que ya se genero layout.
    	
    	parent.document.getElementById("pb_save").style.visibility='visible';
		parent.document.getElementById("pb_save").disabled = false;
    	
}
function cargaCtaBancariaRFC(){
	querySelectPost("cargaCtaBancariasOpAjenas", "ctaBancaria",{async: false });
}


function insertaLC(){
	
	queryFormPost("validaPagadoPPIntRead",{async: false });
	
	
	if ($("#validaPagadoPCI").val() == "SI"){
		document.getElementById("cLineaCap").disabled=false;
		document.getElementById("cLineaCapB").disabled=true;
		document.getElementById("cGuardaLC").disabled=false;
	}
	else{
		if ($("#validaPagadoPCI").val() == "NO"){
			Swal.fire({ icon: 'warning',
						text: "El Pago de Penas Convencionales Integradas NO estan Pagadas, Favor de rectificar!!!" });
		}else if ($("#validaPagadoPCI").val() == "Cancelada") {
			Swal.fire({ icon: 'warning',
						text: "La Pena Convencional Integrada esta CANCELADA, Favor de rectificar!!!" });
		}else{			
			Swal.fire({ icon: 'success',
						text: "Pena Convencional Integrada: "+$("#validaPagadoPCI").val() });
		}
	}
}


function guardaLC(){
		
	$("#cLineaCaptura").val($("#cLineaCap").val());
	
	if ($("#cLineaCap").val() != "" ){
		queryFormPost("penaConvIntLCUpdate",{async: false });
	}
	else{		
		Swal.fire({ icon: 'warning',
					text: "Cadena vacia, favor de rectificar!!!" });
		queryFormPost("penaConvIntLCRead", {async: false });
		$("#cLineaCap").val($("#cLineaCaptura").val());
	}
	
	document.getElementById("cLineaCap").disabled=true;
	document.getElementById("cLineaCapB").disabled=false;
	document.getElementById("cGuardaLC").disabled=true;
		
}

</script>
</head>
<br/>
<body id="dt_example">
<form method="post" id="penaConvInt" name="penaConvInt" action="../PenasConvencionalesInt/crear" >
	
	<div id="container" class="container" style="width:80%">
			
            <input type="hidden" name="nFolioPagoPenasConvInt" id="nFolioPagoPenasConvInt" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>" />
			<input type="hidden" id="fCaptura" name="fCaptura" value="<%=today%>"/>
			<input type="hidden" name="U_LOGIN" id="U_LOGIN" value="<%=u_login%>" />
			<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%= cCentroContable%>" />
			<input type="hidden" name="cEjercicio" id="cEjercicio"  />
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" />
			<input type="hidden" id="cRamo" name="cRamo" value="16 " />
			<input type="hidden" id="mImporteTotal" name="mImporteTotal" value="0.00" />
			<input type="hidden" name="accion" id="accion" value="0" />	
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" name="cDocumento" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>"/>
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" />
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" />
			<input type="hidden" id="buscarLayout" name="buscarLayout" value="10NC2015000001"/>   				
			<input type="hidden" id="Desmarcar" name="Desmarcar" value="Desmarcar" />
			
			<input type="hidden" name="CuentaBancaria" id="CuentaBancaria" value=""/>
			<input type="hidden" name="claveLeyenda" id="claveLeyenda" value=""/>
			<input type="hidden" name="parametrosAjustes" id="parametrosAjustes" value=""/>
			<input type="hidden" name="validaPagadoPCI" id="validaPagadoPCI" value=""/>
			<input type="hidden" name="cLineaCaptura" id="cLineaCaptura" value=""/>
			<input type="hidden" name="cBeneficiario" id="cBeneficiario" value=""/>
			<input type="hidden" name="existePenaInt" id="existePenaInt" value=""/>
						 
			<div class="card-header"> <h3> Integración de Pago de penas convencionales </h3> </div>
			<hr class="mt-3"/>
				
			<div class="row">				
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="id_caso"> Folio: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<input type="text" name="id_caso"  id="id_caso" style="text-align: right;" maxlength="6" size="6" readonly class="form-control form-control-sm"/>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="caNoContrarreciboInt"> Integración: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<input type="text" name="caNoContrarreciboInt"  id="caNoContrarreciboInt" readonly class="form-control form-control-sm"/>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					Ingresos Propios: &nbsp;<input type="checkbox" id="chk_IP" name="chk_IP" class="form-check-input" value="" />
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					Radicado: &nbsp;<input type="checkbox" id="chk_radicado" name="chk_radicado" class="form-check-input" value="" />
				</div>			
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="fBusquedaDe"> Periodo de: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						<input type="text" name="fBusquedaDe" id="fBusquedaDe" class="form-control form-control-sm" />
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="fBusquedaHasta"> Hasta: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>
						<input type="text" name="fBusquedaHasta"  id="fBusquedaHasta" class="form-control form-control-sm"/>
					</div>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="cIDRFC2"> RFC: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<select name="cIDRFC2" id="cIDRFC2" class="form-select form-select-sm">
						<option value="" selected="selected">- Seleccione uno -</option>
						<option value="S24676" >TESOFE</option>
						<option value="S04929" >CNF010405EG1</option>
					</select>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="cLineaCap"> Linea Captura: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<input type="text" name="cLineaCap"  id="cLineaCap" class="form-control form-control-sm"/>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<input type="button" name="cLineaCapB" id="cLineaCapB" value="Inserta LC"  onclick="insertaLC()"  class="btn btn-secondary btn-sm" disabled/>&nbsp;
					<input type="button" name="cGuardaLC"  id="cGuardaLC"  value="Guarda LC"  onclick="guardaLC()" class="btn btn-secondary btn-sm" disabled />
				</div>			
			</div>
			<br/>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="mImporteInt"> Importe Total: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">	
					<input type="text" name="mImporteInt" id="mImporteInt" class="form-control form-control-sm" value="0" readonly/>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="mImporteconAjuste"> Importe Pena: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<input type="text" name="mImporteconAjuste" id="mImporteconAjuste" class="form-control form-control-sm" value="0" readonly/>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="mImporteAjuste"> Ajuste: </label>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<input type="text" name="mImporteAjuste" id="mImporteAjuste" class="form-control form-control-sm" value="0" readonly/>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="cxpAjuste"> CXP Ajuste: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
					<input type="text" name="cxpAjuste" id="cxpAjuste" class="form-control form-control-sm" value="0" readonly/>
				</div>		
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="ctaBancarias"> Cta. Bancaria: </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
					<select name="ctaBancarias" id="ctaBancarias" class="form-select form-select-sm">
						<option value="" selected="selected">- Seleccione uno -</option>
						<option value="22800100000100">22800100000100 TESOFE</option>
						<option value="072320006615585774">072320006615585774 GUANAJUATO</option>
						<option value="072320006615586168">072320006615586168 NAYARIT</option>
						<option value="072320005134113068">072320005134113068 MAESTRA</option>
					</select>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
					<label for="cveLeyenda"> Cve. Leyenda: </label>
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
					<select name="cveLeyenda" id="cveLeyenda" class="form-select form-select-sm"></select>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
					<input type="button" id="pbBuscar" value="Buscar" class="btn btn-secondary btn-sm"/> &nbsp;
					<input type="button" id="pbLayout" value="Generar Layout" class="btn btn-secondary btn-sm"/>
				</div>
			</div>
			

			<div class="row d-flex">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<input type="checkbox" id="checkAll" name="checkAll" class="form-check-input" checked disabled>
					<table id="GridOAI" class="table table-striped table-bordered">		
							<thead>
								<tr align="center">
									<th>-</th>
									<th>Renglon</th>
									<th>Folio Ajena</th>
									<th>Operacion Ajena</th>
									<th>Estructura Programatica</th>
									<th>Importe</th>
									<th>UR</th>
									<th>RFC</th>
									<th>Centro ContableOAT</th>
									<th>Folio CXP</th>
									<th>CXP</th>
									<th>Documento</th>
									<th>Mes</th>
								</tr>
							</thead>
				 	</table>
				</div>	
			</div>	
			
			<br/>
			
			<div class="row d-flex" style="visibility:hidden">								
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<table id="GridOAISeleccionados" >
						<thead>
							<tr align="center">
								<th>-</th>
								<th>Renglon</th>
								<th>Folio Ajena</th>
								<th>Operacion Ajena</th>
								<th>Estructura Programatica</th>
								<th>Importe</th>
								<th>UR</th>
								<th>RFC</th>
								<th>Centro ContableOAT</th>
								<th>Folio CXP</th>
								<th>CXP</th>
								<th>Documento</th>
								<th>Mes</th>
								<th>renglonInt</th>
							</tr>
						</thead>
					</table>									
				</div>
			</div>
			
			<div id="dialog-carga">
				<div id="esperar" align="center">Espere por favor....
						<img border="0" src="../imagenes/espera.gif" height="30">
				</div>
			</div>
	   </form>
	   <form method="post" id="capituloMilCLC" name="capituloMilCLC" action="../gstnmngr/LayoutPagosCLCServlet" >
				<input type="hidden" id="buscarLayout" name="buscarLayout" value="10NC2015000001">
		</form>
	</body>
</html>
