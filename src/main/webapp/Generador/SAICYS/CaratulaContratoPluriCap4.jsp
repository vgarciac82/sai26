<%@ page language="java" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.NegativaPestanaBusinessLogic"%>
<%@ page import="com.syc.gestion.core.NegativaPestana"%>
<%@ page import="com.syc.gestion.core.Role"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);

	String cIdTipo = "";
	if (usuario == null) {
		response.sendRedirect("../../index.jsp");
		return;
	}
	String name_user = usuario.getLogin();
	Map<String, Role> rol = usuario.getRoles();
	String cIdContratoDefinitivo = "";
	String nEstatus="";
	if (request.getParameter("cIdContratoDefinitivo") != null) {
		cIdContratoDefinitivo = request.getParameter("cIdContratoDefinitivo");
		nEstatus=request.getParameter("nIdEstatus");
		session.setAttribute(GestionInterface.ATT_ContratCap4Definitivo,cIdContratoDefinitivo);
		session.setAttribute(GestionInterface.ATT_EstatusContratCap4,nEstatus);
	} else {
		cIdContratoDefinitivo = (String) session
				.getAttribute(GestionInterface.ATT_ContratCap4Definitivo);
	}
	System.out.println("Contrato SAI : "+cIdContratoDefinitivo);
%>

<!DOCTYPE html>
<html>
<head>


<title>'CaratulaContratoPluriCap4'</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="Consolidado">

<script type="text/javascript">
	var oTablePartidas;
	var oTablePartidas2;
	$(document).ready(function() {
		<%
			
		    String role="";
		    String roles="";
			NegativaPestana NegPestana=new NegativaPestana();
			NegativaPestanaBusinessLogic ebl = new NegativaPestanaBusinessLogic("jdbc/gestion");
			//botones
		//	NegativaBoton NegBoton= new NegativaBoton();
			NegativaPestanaBusinessLogic nb= new NegativaPestanaBusinessLogic("jdbc/gestion");
			
			Iterator it1 = rol.entrySet().iterator();
			while (it1.hasNext()) {
				Map.Entry r = (Map.Entry)it1.next();
				role=(String)r.getKey();
				roles += r.getKey().toString()+",";
			}
			Map botones=nb.getBotones(roles,"ContratoPluriCap4","CaratulaContratoPluriCap4");
			Iterator btn = botones.entrySet().iterator();
			while (btn.hasNext()) {
				Map.Entry b = (Map.Entry)btn.next();%>
				$("#<%=b.getValue()%>").attr("disabled", true);<%
			}
		
		%>
		tabb=2;
		ocultarTablas();
		initQuerys();
		showButtons();
		showAndHideTabs();
		muestraTabla();
	});//Fin del document ready
	function guardarPartidas(){
		var cadenaPart=cadenaPartidas();
		if(cadenaPart==null){
			return;
		}
		$.ajax({url: "../../servlet/ContratoCap4Servlet" , type:'post' , async: false
		,data:'operacion=17&cIdContratoDefinitivo='+$("#cIdContratoDefinitivo").val()
		+'&cadenaPartidas='+cadenaPart
		, dataType: 'json', success: 
			function(j){
				var mensaje=j[0].MENSAJE;
				var resp=j[0].RESPUESTA;
				swal({
					title: "",
					text: mensaje,
					icon: "info",
					buttons: {
						confirm : "Cerrar"
						},
					}).then((continuar) => {
						if(resp){
							window.location = "ContratoPlurianualCap4.jsp?tab=3";
						}
				});
				
			}
		});
	}
	function cadenaPartidas(){
		var cantMin=1;
		var cantMax=1;
		var precioU=0;
		var nIdIVA=0;
		var precioUMax=0;
		var mMontoNetoLine=0;
		var mMontoNetoMin=0;
		var mMontoNetoMax=0;
		var mMontoNetoPluri=0;
		var sumaMontoNetoPluri=0;
		var mMontoNetoLineOrig=0;
		var mMontoNetoMaxOrig=0;
		var cidUniMed='SRV';
		var cDescripAdi_="";
		var cadenaPartidas='';
		var otable=getDataTable();
		var aTrs = getaDataTable();
		var token=",";
		for(var i=0;i<aTrs.length;i++){
			if(i>0){
				cadenaPartidas+=token;
			}
			aData = otable.fnGetData(aTrs[i]);
			mMontoNetoLine=$("#mMontoNetoLinea_"+aData[0]).val();
			mMontoNetoMin=mMontoNetoLine;
	  		if($("#isAbierto").val()==0){//Contratos cerrados
	  			nIdIVA=aData[15];
	  			if($("#nIdTipoActividadEconomica").val()==2){//Contrato plurianual de Bienes
	  				cantMin=$("#nCantidadMin_"+aData[0]).val();
	  				precioU=aData[8];
	  				precioUMax=aData[8];
		  		}else{//Contrato plurianual de Servicios
		  			mMontoNetoLine=$("#mMontoNetoLinea_"+aData[0]).val();
					mMontoNetoMin=mMontoNetoLine;
		  		}
	  		}else{
	  			if($("#nIdTipoActividadEconomica").val()==2){//Contrato Plurianual Abierto de Bienes
	  				cantMin=$("#nCantidadMin_"+aData[0]).val();
	  				cantMax=$("#nCantidadMax_"+aData[0]).val();
	  				precioU=aData[9];
	  				precioUMax=aData[9];
	  				nIdIVA=aData[18];
		  		}else{//Contrato Plurianual Abierto de Servicios
		  			mMontoNetoLine=$("#mMontoNetoLinea_"+aData[0]).val();
					mMontoNetoMin=$("#mMontoNetoMin_"+aData[0]).val();
	  				mMontoNetoMax=$("#mMontoNetoMax_"+aData[0]).val();
	  				nIdIVA=aData[17];
		  		}
		  			
	  		}
			//Validación
			if(!validacionMontos(mMontoNetoMin, mMontoNetoMax, cantMin, cantMax, aData[1])){
				return null;
			}
			cadenaPartidas+=aData[0]+"-"+cDescripAdi_+"-"+cantMin+"-"+cantMax+"-"+precioU+"-"+precioUMax
			+"-"+mMontoNetoLine+"-"+mMontoNetoMin+"-"+mMontoNetoMax+"-"+mMontoNetoPluri+"-"+nIdIVA+"-"+mMontoNetoLineOrig+"-"+mMontoNetoMaxOrig+"-"+cidUniMed;
		}
		return cadenaPartidas;
	}
	function getDataTable(){
		if($("#isAbierto").val()==0){
			return oTablePartidas.dataTable();
		}else{
			return oTablePartidas2.dataTable();
		}
	}
	function getaDataTable(){
		var aTrs;
  		if($("#isAbierto").val()==0){
  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
  				aTrs = $('#tblPartidasPluBienes').dataTable().fnGetNodes();
	  		}else{
	  			aTrs = $('#tblPartidasPluServ').dataTable().fnGetNodes();
	  		}
  		}else{
  			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
  				aTrs = $('#tblPartidasPluAbiBienes').dataTable().fnGetNodes();
	  		}else{
	  			$('#tblPartidasPluServ').empty();
	  			aTrs = $('#tblPartidasPluAbiServ').dataTable().fnGetNodes();
	  		}
  		}
	  	
		return aTrs;
	}
	function validacionMontos(mMontoNetoMin,mMontoNetoMax,cantMin,cantMax,numLinea){
		if($("#isAbierto").val()==1 ){
			if($("#nIdTipoActividadEconomica").val()==1){//Servicio
				//alert(mMontoNetoMin);
				if(mMontoNetoMin==""||parseFloat(mMontoNetoMin)<=0.00){
					swal("El Monto M\u00ednimo no puede ser cero en la l\u00ednea n\u00famero "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
				if(mMontoNetoMax==""||parseFloat(mMontoNetoMax)==0){
					swal("La Monto M\u00e1ximo debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
				if(mMontoNetoMax==""|| mMontoNetoMin==""|| parseFloat(mMontoNetoMax)<=parseFloat(mMontoNetoMin)){
					swal("La Monto M\u00e1ximo debe de ser mayor al m\u00ednimo en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
			}else{//Bienes
				if(cantMin==""||parseInt(cantMin)==0){
					swal("La Cantidad M\u00ednima debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
				if(cantMax==""||parseInt(cantMax)==0){
					swal("La Cantidad M\u00e1xima debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
				if(cantMin =="" || cantMax=="" || parseInt(cantMax)<=parseInt(cantMin)){
					swal("La Cantidad M\u00e1xima debe de ser mayor a la Cantidad M\u00ednima en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
					return false;
				}
			}
		}else{
			if(mMontoNetoMin=="" || parseFloat(mMontoNetoMin)<=0.00){
				swal("El Monto Neto no puede ser cero en la l\u00ednea n\u00famero "+numLinea,{icon:"warning",button: "Cerrar"});
				return false;
			}
			if(($("#nIdTipoActividadEconomica").val()==2 && parseInt(cantMin)==0)||cantMin==""){//Bienes
				swal("La Cantidad debe de ser mayor a 0 en la l\u00ednea "+numLinea,{icon:"warning",button: "Cerrar"});
				return false;
			}
		}
		return true;
	}
	function showButtons() {
		if (parseInt($("#nIdEstado").val(), 10) >= 2) {
			$("#btnGuardar").hide();
			$("#imgEliminar").hide();
		} else {
			$("#btnGuardar").show();
			$("#imgEliminar").show();
		}
	}
	function deleteContrato() {
		if (parseInt($("#nIdEstado").val(), 10) > 1) {
			swal("No puede eliminar el contrato hasta que esté en estatus de captura.",{icon:"warning",button: "Cerrar"});
			return;
		}
		swal({
			title: "¿Está seguro que desea eliminar el contrato?",
			text: "Una vez confirmado, no podrá deshacer los cambios!",
			icon: "info",
			buttons: {
				confirm : "Aceptar",
				cancel: "Cancelar"
			},
		}).then((continuar) => {
			if (!continuar) {
				return;
			}else{
				queryFormPost("mDeleteContratoPluriCap4", {
					async : false,
					callback : function() {
						guardaBitacora("Elimina_Contrato",$('#cIdContratoDefinitivo').val());
						window.location = "ContratoPlurianualCap4.jsp?tab=1";
					}
				});
			}
		});
		
	}
	function ocultarTablas(){
	  	$("#divTblPartidasPluBienes").css("display","none");
	  	$("#divTblPartidasPluServ").css("display","none");
	  	$("#divTblPartidasPluAbiBienes").css("display","none");
	  	$("#divTblPartidasPluAbiServ").css("display","none");
	  	
	}
	function emptyDatatable(){
		$('#tblPartidasPluBienes').dataTable().fnClearTable();
		$('#tblPartidasPluServ').dataTable().fnClearTable();
		$('#tblPartidasPluAbiBienes').dataTable().fnClearTable();
		$('#tblPartidasPluAbiServ').dataTable().fnClearTable();
	}
	function muestraTabla(){
		if($("#isAbierto").val()==0){
			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
				$("#divTblPartidasPluBienes").css("display","block");
				initTablaPluBienes();
			}else{
				$("#divTblPartidasPluServ").css("display","block");
				initTablaPluServ();
			}
		}else{
			if($("#nIdTipoActividadEconomica").val()==2){//Bienes
				$("#divTblPartidasPluAbiBienes").css("display","block");
				initTablaPluAbiBienes();
			}else{
				$("#divTblPartidasPluAbiServ").css("display","block");
				initTablaPluAbiServ();
			}
		}
	}
	function initTablaPluBienes(){
		var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
		oTablePartidas = $("#tblPartidasPluBienes").dataTable({
			bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoPluriCap4Partidas&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "nIdContratoPluriCap4Partida",bVisible: false},
					{sName: "nIdPartida"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "descripcion"},
					{sName: "cDescripAdi"},
					{sName: "nCantidadMin"},
					{sName: "cIdUnidadMedida"},
					{sName: "mPrecioUnitario"},
					{sName: "nIdIVA"},
					{sName: "mMontoNetoLinea"},
					{sName: "mMontoNetoPluri"},
					{sName: "remanentePluri"},
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "mMontoNetoLineaOrig",bVisible: false},
					{sName: "nValorIVA",bVisible: false}
				]
			});
	}
	function initTablaPluServ(){
		var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'";  
		oTablePartidas = $("#tblPartidasPluServ").dataTable({
			bAutoWidth : true,
			bPaginate:true,
			bDestroy:true,
			bRetrive : true,
			bServerSide:false,
			sScrollX: "100%",
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
			bProcessing: true,
			bJQueryUI: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoPluriCap4Partidas&qw="+qw,
			aaSorting: [[ 1, "asc" ]] ,
			aoColumns: [
				{sName: "nIdContratoPluriCap4Partida",bVisible: false},
				{sName: "nIdPartida"},
				{sName: "cIdSubPartida"},
				{sName: "cIdCABM"},
				{sName: "descripcion"},
				{sName: "cDescripAdi"},
				{sName: "nCantidadMinReadOnly"},
				{sName: "cIdUnidadMedida"},
				{sName: "mPrecioUnitario"},
				{sName: "nIdIVA"},
				{sName: "mMontoNetoLinea"},
				{sName: "mMontoNetoPluri"},
				{sName: "remanentePluri"},
				{sName: "cIdContratoDefinitivo",bVisible: false},
				{sName: "mMontoNetoLineaOrig",bVisible: false},
				{sName: "nValorIVA",bVisible: false}
			]
		});
	}
	function initTablaPluAbiBienes(){
		var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
		oTablePartidas2 = $("#tblPartidasPluAbiBienes").dataTable({
			bScrollCollapse: true,
        		bInfo: false,
        		//sScrollY : "100%",
				sScrollX: "100%",
				bAutoWith: true,
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
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoPluriCap4Partidas&qw="+qw,
				bJQueryUI: true,
				aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "nIdContratoPluriCap4Partida",bVisible: false},
					{sName: "nIdPartida"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "descripcion"},
					{sName: "cDescripAdi"},
					{sName: "nCantidadMin"},
					{sName: "nCantidadMax"},
					{sName: "cIdUnidadMedida"},
					{sName: "mPrecioUnitario"},
					{sName: "nIdIVA"},
					{sName: "mMontoNetoMinimo2"},
					{sName: "mMontoNetoMaximo2"},
					{sName: "mMontoNetoPluri"},
					{sName: "remanentePluri"},
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "mMontoNetoLineaOrig",bVisible: false},
					{sName: "mMontoNetoMaximoOrig",bVisible: false},
					{sName: "nValorIVA",bVisible: false}
				]
			});
	}
	function initTablaPluAbiServ(){
		var qw="1=1 AND cIdContratoDefinitivo='"+$("#cIdContratoDefinitivo").val()+"'"; 
		oTablePartidas2 = $("#tblPartidasPluAbiServ").dataTable({
			bAutoWidth : true,
			bPaginate:true,
			bDestroy:true,
			bRetrive : true,
			bServerSide:false,
			sScrollX: "100%",
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
			bProcessing: true,
			bJQueryUI: true,
			sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_mContratoPluriCap4Partidas&qw="+qw,
			aaSorting: [[ 1, "asc" ]] ,
				aoColumns: [
					{sName: "nIdContratoPluriCap4Partida",bVisible: false},
					{sName: "nIdPartida"},
					{sName: "cIdSubPartida"},
					{sName: "cIdCABM"},
					{sName: "descripcion"},
					{sName: "cDescripAdi"},
					{sName: "nCantidadMinReadOnly"},
					{sName: "cIdUnidadMedida"},
					{sName: "mMontoNetoLinea"},
					{sName: "nIdIVA"},
					{sName: "mMontoNetoMinimo"},
					{sName: "mMontoNetoMaximo"},
					{sName: "mMontoNetoPluri"},
					{sName: "remanentePluri"},
					{sName: "cIdContratoDefinitivo",bVisible: false},
					{sName: "mMontoNetoLineaOrig",bVisible: false},
					{sName: "mMontoNetoMaximoOrig",bVisible: false},
					{sName: "nValorIVA",bVisible: false}
				]
			});
	}	
</script>
</head>

<body>
	<form id="formCaratulaContCap4">
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Car&aacute;tula del Contrato Cap&iacute;tulo 4000</legend>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col">
							<input type="button" class="btnInterfaceCancelar ui-button ui-corner-all float-right" 	id="imgEliminar" name="imgEliminar" 	value="Eliminar"	onclick="deleteContrato();" />
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblUnidadEjecutora" id="lblUnidadEjecutora"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblDefinitivo" id="lblDefinitivo"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cConceptoContrato" id="cConceptoContrato"  readonly/>
					</div>
				</div>
				
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblProveedor" id="lblProveedor"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cNoContratoCNET" id="cNoContratoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cNoProcedimientoCNET" id="cNoProcedimientoCNET"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTipoContrato" id="lblTipoContrato"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="cEstado" id="cEstado"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblSubtotal" id="lblSubtotal"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalIVA" id="lblTotalIVA"  readonly/>
					</div>
				</div>
				<div class="row">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNeto" id="lblTotalNeto"  readonly/>
					</div>
				</div>
				<div class="row" id="divTotalMax">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblTotalNetoMax" id="lblTotalNetoMax"  readonly/>
					</div>
				</div>
				<div class="row" id="divTotalPluri">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblMontoTotalPluri" id="lblMontoTotalPluri"  readonly/>
					</div>
				</div>
				<div class="row" id="divTotalRemanentePluri">
					<div class="col">
						<input type="text" class="form-control  transpInput" name="lblMontoTotalRemanentePluri" id="lblMontoTotalRemanentePluri"  readonly/>
					</div>
				</div>
				
			</div>
		</fieldset>
		<fieldset class="form-group border p-3">
			<legend class="w-auto px-2"> Edici&oacute;n del Contrato</legend>
			<br />
			<div class="form-group" id="divTblPartidasPluBienes" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluBienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoPluriCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>
									<th >Unidad Medida</th>
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th >Monto Remanente<br/> Plurianual</th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto  original</th>
									<th  style="display: none;">Valor IVA</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasPluServ" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluServ" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoPluriCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>	
									<th >Unidad Medida</th>						
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th >Monto Remanente<br/> Plurianual</th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto original</th>
									<th  style="display: none;">Valor IVA</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasPluAbiBienes" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluAbiBienes" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoPluriCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad<br/>Minima</th>
									<th >Cantidad<br/>Maxima</th>
									<th >Unidad Medida</th>
									<th >Precio<br/>Unitario</th>
									<th >IVA</th>
									<th >Monto Neto<br/>M&iacute;nimo</th>
									<th >Monto Neto<br/>M&aacute;ximo</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th >Monto Remanente<br/> Plurianual</th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto minimo original</th>
									<th  style="display: none;">Monto Neto maximo original</th>
									<th  style="display: none;">Valor IVA</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group" id="divTblPartidasPluAbiServ" >
				<div class="row">
					<div class="col">
						<table id="tblPartidasPluAbiServ" class="table table-striped table-bordered dt-responsive nowrap" style="width:100%">
							<thead >
								<tr>
									<th style="display: none;">nIdContratoPluriCap4Partida</th>
									<th >Num</th>
									<th >Partida </th>
									<th >Cucop</th>
									<th >Descripci&oacute;n</th>
									<th >Descripci&oacute;n  <br /> Adicional </th>
									<th >Cantidad</th>
									<th >Unidad Medida</th>
									<th >Monto<br/>A Comprometer</th>
									<th >IVA</th>
									<th >Monto Neto<br/>Minimo</th>
									<th >Monto Neto<br/>Maximo</th>
									<th >Monto Neto<br/> Total  Pluri</th>
									<th >Monto Remanente<br/> Plurianual</th>
									<th  style="display: none;">Contrato Definitivo</th>
									<th  style="display: none;">Monto Neto minimo original</th>
									<th  style="display: none;">Monto Neto maximo original</th>
									<th  style="display: none;">Valor IVA</th>
								</tr>									
							</thead>
						</table>
					</div>
				</div>
			</div>
			<div class="form-group">
				<div class="row">
					<div class="input-group">
						<div class="col-4">
							<input type="button" class="btnInterfaceBG ui-button ui-corner-all float-right" 	id="btnGuardar" name="btnGuardar" 	value="Guardar"	onclick="guardarPartidas();" />
						</div>
					</div>
				</div>
			</div>
		</fieldset>
		<input type="hidden" id="cIdContratoDefinitivo"	name="cIdContratoDefinitivo" value="<%=cIdContratoDefinitivo%>" />
		<input type="hidden" id="nIdTipoActividadEconomica"	name="nIdTipoActividadEconomica" value="1" /> 
		<input type="hidden" id="esServicio" name="esServicio" value="0" /> 
		<input type="hidden" id="cIdUnidadEjecutora" name="cIdUnidadEjecutora" value="" /> 
		<input type="hidden" name="esDescentralizado" id="esDescentralizado" />
		<input type="hidden" id="isPlurianual" name="isPlurianual" value="0" /> 
		<input type="hidden" id="nIdEstado" name="nIdEstado" value="0" />
		<input type="hidden" id="cAccion" name="cAccion" value=""/>
		<input type="hidden" id="cIdDocumento" name="cIdDocumento" value=""/>
		<input type="hidden" name="cIdUsuario" id="cIdUsuario" value="<%= usuario.getLogin() %>"/>
		<input type="hidden" id="isAbierto" name="isAbierto" value=""/>
	</form>
</body>
</html>
