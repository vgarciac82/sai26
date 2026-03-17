
<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%!Logger log = LoggerFactory.getLogger( "Generador/OperacionesAjenas.jsp" ); %>
<%
	boolean bAplicadoCont = false;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String algo = "";

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}

	String mensaje = "";
	if (request.getParameter("msg") != null && !"".equals(request.getParameter("msg"))) {
		mensaje = request.getParameter("msg");
		mensaje = mensaje.replace("[", "");
		mensaje = mensaje.replace("]", "");
		mensaje = mensaje.replace(",", "<br>");
	}

	int id_oper = c.getCasoOperacion(0).getOperacion().getNumero(  );

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	String select = c.getTipoCaso().getGavetaAsociada() + "_G" + c.getIdGabinete();//se usa por separado abajo
	String cAplicaDocto = "No";
	if (request.getParameter("aplicaDocto") != null && request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();
	cRamo = usuario.getU_Ramo();
	algo = usuario.getLogin();
	//de aki para arriba es de cajon

	/*VGC20171019 Se guarda en base el prefijo de CxP*/
	ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	String cxpPrefijo = cabl.getSystemSetting("CXP_PREFIJO") == null
			? "OA"
			: cabl.getSystemSetting("CXP_PREFIJO_OA");
	boolean esSAIAlterno = "true".equals(cabl.getSystemSetting("SAI_AMBIENTAL"));
	boolean esSAIFonden = "true".equals(cabl.getSystemSetting("SAI_FONDEN"));

	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;
	
	/*VGCFIEL*/
	String nNumEmpleado = usuario.getNumeroEmpleado();	
%>
<html>
<head>
<title>Operaciones Ajenas</title>
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
	<link rel="stylesheet" type="text/css" href="css/jquery-ui.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/datatables.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/bootstrap.min.css"></link>
	<link rel="stylesheet" type="text/css" href="css/sweetalert2.min.css"></link>
	<link rel="stylesheet" type="text/css" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
	
	<script type="text/javascript" src="../Generador/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.ui.core.js"></script>
	<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
	<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>	
	<script type="text/javascript" src="js/crud.js"></script>
	<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
	<script type="text/javascript" src="../Generador/js/Moment.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
	
	<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"> </script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"> </script>
	
	<!-- VGCFIEL -->
	<link rel="stylesheet" type="text/css" href="../Generador/css/resumenPago.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/css/ResumenFIEL.css"></link>
	<link rel="stylesheet" type="text/css" href="../plantillasCasos/ComponentesPago/CSS/EgresoFirmantes.css"></link>
	
	<script type="text/javascript" src="js/OperacionesAjenas.js"></script>
	<!-- VGCFIEL -->
	<script type="text/javascript" src="../plantillasCasos/ComponentesPago/js/EgresoFirmantes.js"></script>

<script type="text/javascript" charset="utf-8">
/*VGC20171019 Se guarda en base el prefijo de CxP*/
var cxpPrefijo = "<%=cxpPrefijo%>";
var esConsulta = <%=esConsulta%>;

/*VGCFIEL*/
var nNumEmpleado = "<%=nNumEmpleado%>";
var cIdUsuarioCaptura = "<%=usuario.getLogin(  )%>";
var nombreElaboro = "<%=e.getNombre()%>";
var aPaternoElaboro = "<%=e.getApellidoPaterno()%>";
var aMaternoElaboro = "<%=e.getApellidoMaterno()%>";
var puestoElaboro = "<%=e.getCargo()%>";
var idOperacionActual = <%=id_oper%>
var mxnLanguage =  {
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
	}

$(document).ready(function(){
		$("#millar").hide();
		$("#subPenal1").hide();
		$("#subCedular1").hide();
		$("#subtmImportFlete1").hide();
		$("#subHonor1").hide();
		$("#subARR1").hide();
		$("#subFlete1").hide();
		$("#IMDT1").hide();
		$("#CNIC1").hide();
		$("#ISRLAUDOS1").hide();
		$("#ISROTROS1").hide();
		$("#IVA61").hide();
		$("#ISRRESICO1").hide();

		$("#Buscar").button();
		$("#vDetalle").button();
		$("#Desmarcar").button();
		
		$("#esperaDialog").dialog({
			autoOpen: false,
			height: 400,
			width: 800,
			modal: true 
		});
		
		$("#OIRAUSU").val( "<%=algo%>" );
		$("#cCentroContable").val( "<%=cCentroContable%>");	
		$("#id_caso").val( <%=request.getParameter("folio")%>);
		$("input.AyudaSyC").subIniciaDlg();
		 
		$("#oficioDelegatorioCaptura").hide();		 
		querySelectPost("catTipoSuplenciaRead", "tipoSuplencia", {async : false	});
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo", {	async : false	});
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBoUpdate", { async : false	});
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaUpdate", { async : false	});
		
		$( "#dFechaOficio" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
		
		$("#oficioDelegatorioVoBo").hide();		
		$( "#dFechaOficioVoBo" ).datepicker({
			showOn: "button",
			buttonImage: "images/calendar.gif",
			buttonImageOnly: true
		});
		
		$( "#dialog-form" ).dialog({
			autoOpen: false,
			height: 400,
			width: 800,
			modal: true,
			beforeClose: function( event, ui ) {
				return bClicBtn;			
			}
		});
		

	    $("#tabs").tabs( {
		        "show": function(event, ui) {
		            var oTable = $('div.dataTables_scrollBody>table.display', ui.panel).dataTable();
		            if ( oTable.length > 0 ) {
		                oTable.fnAdjustColumnSizing();
		            }
		        }
		    } );
			
			
		queryFormPost("TipoPolizaRead",{async: false });
		querySelectPost("CatalogoObraDGastoRead", "DESTINO_GASTO", {async: false });
 
		$(function() {
			$( "#fBusquedaDe" ).datepicker({				
				dateFormat: "dd/mm/yy",				
				changeYear: true, 
				changeMonth: true
			});
		});	
		$(function() {
			$( "#fBusquedaHasta").datepicker({				
				dateFormat: "dd/mm/yy",
				changeYear: true, 
				changeMonth: true
			});
		});	
		
					
		$("#Busqueda1").dataTable({
						bPaginate : false,			//muestra las flechas y pagina dependiendo el rango
						bLengthChange : true,  //
						bInfo : true,			//es el que muestra los numeros de los registros								
						bJQueryUI: true,  //se coloca el dise?o que contiene en css
						bFilter : false,
						bSort : true, // para colocar los filtros en los campos
						bDestroy: true,
						bRetrieve:true,
						left:true,
						bAutoWidth:false,
						oLanguage: mxnLanguage
								
			
			});
		$("#Detalle").dataTable({
						bPaginate : false,			//muestra las flechas y pagina dependiendo el rango
						bLengthChange : false,  //
						bInfo : false,			//es el que muestra los numeros de los registros							
						bJQueryUI: true,  //se coloca el dise?o que contiene en css
						bFilter : false,
						bSort : false, // para colocar los filtros en los campos
						bDestroy: true,
						bRetrieve:true,
						bleft:true,
						bAutoWidth : false,
						oLanguage: mxnLanguage
				});	
		
		$("#chk_IP").change(function(){
			if ($("#chk_IP").prop("checked")){
				$("#chk_radicado").prop("checked",false);
				$("#chk_radicado").attr('disabled', true);
				$("#chk_relgasto").prop("checked",false);
				$("#chk_relgasto").attr('disabled', true);
				document.getElementById("chk_PagoBancoIF").checked = false;
	           	$("#divOACedular").hide();
			}else{
				$("#chk_radicado").attr('disabled', false);
				$("#chk_relgasto").attr('disabled', false);
				if($("#csubCedular1").val() == '1'){
					document.getElementById("chk_PagoBancoIF").checked = true;
		           	$("#divOACedular").show();
				}
			}
			
		});
		
		$("#chk_radicado").change(function(){
			if ($("#chk_radicado").prop("checked")){
				$("#chk_IP").prop("checked",false);
				$("#chk_IP").attr('disabled', true);
				$("#chk_relgasto").prop("checked",false);
				$("#chk_relgasto").attr('disabled', true);
			}else{
				$("#chk_IP").attr('disabled', false);
				$("#chk_relgasto").attr('disabled', false);
			}
			
		});
		
		$("#chk_relgasto").change(function(){
			if ($("#chk_relgasto").prop("checked")){
				$("#chk_IP").prop("checked",false);
				$("#chk_IP").attr('disabled', true);
				$("#chk_radicado").prop("checked",false);
				$("#chk_radicado").attr('disabled', true);
			}else{
				$("#chk_IP").attr('disabled', false);
				$("#chk_radicado").attr('disabled', false);
			}
			
		});
		
});
		
	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
  		var tipoRetencion = $("#cBeneficiario").val();
  		
		try{
			if ($('#cTMonto').val() == 0 ){
				Swal.fire({ icon: 'warning',
							text: "No es Posible Realizar esta Operacion ya que el Importe Total es $0.00" });				
				return false;
			}	
			
			//Guardado de los campos correspondientes a cada variable de caso
			p.gestion.setFolio( $("#FOLIO").val() );
			p.gestion.setOperador( $("#OPERADOR").val() );
			p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
			p.gestion.setEjercicioFiscal( $("#EF").val() );
			p.gestion.setConceptoMov("Aplicacion Operaciones Ajenas");
			p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
			p.gestion.setAplicadoCont("false");
            
			var nretval = cmdGuardar();
			if (nretval == -1) {
				$("#esperaDialog").dialog("close");
				return false;
			}

			if(id_oper == 1){ 
				
				queryFormPost({ 
					
						queryName : "operAjenaUpdate",
						async : false,
						callback : function() {
					
						//	alert('Guardado Correctamente');
						}
					
				}); 
								
				/*if (tipoRetencion == 7){
					queryFormPost("aplicaRedondeoXRubro", { async: false });
				}*/
				
			}
		}
		catch (e) {
			$("#esperaDialog").dialog("close");
			if( e.message ){
				Swal.fire({ icon: 'error',
							text: "onSubmit: Error: " + e.message });
			} else {
				Swal.fire({ icon: 'error',
							text: "onSubmit: Error: " + e });
			}
			return false;
		}
		$("#esperaDialog").dialog("close");
		return valida_campos;
  	}

 	function fnAplicaMotor(){
 		divAplica.innerHTML = "<iframe id='ifAplica' src='../gstnmngr/AppCont?elContra=" + $('#caNoContrarrecibo').val() + "' style='BORDER: white 1px solid; WIDTH: 90%; HEIGHT: 90%;FONT-FAMILY: Verdana,Tahoma ;'> </iframe>";
 	}

  	function onPostSubmit(id_oper){
  		return true;
  	}

    function onLoadPlantilla(){
    	$("#id_caso").val( <%=request.getParameter("folio")%>);
    	Carga();
    	
    	/*VGCFIEL*/
		$("#nNumEmpleadoElab").val(nNumEmpleado);
		$("#cNombreEla").val(nombreElaboro);
		$("#cPaternoEla").val(aPaternoElaboro);
		$("#cMaternoEla").val(aMaternoElaboro);
		$("#cPuestoEla").val(puestoElaboro);
		$("#cEsFirmaElectronica").val("N");
		$("#cIdUsuarioCaptura").val(cIdUsuarioCaptura);
		
		if(<%=id_oper == 1%>){
			muestraEditaFirmantes();
			if( $("#nombreElabora").val() == "" ){
				$("#nombreElabora").val( $("#cNombreEla").val() + ' ' 
				                      +  $("#cPaternoEla").val() + ' ' 
				                      +  $("#cMaternoEla").val() ); 
				$("#puestoElabora").val($("#cPuestoEla").val());
			}
			$(".firmaElectronica").each(function() {
				$(this).show();
			});

			parent.document.getElementById("pb_send").style.visibility='hidden';
			parent.document.getElementById("pb_cancel").disabled=false;
			
		}
		if(<%=id_oper == 2%>){
			
			var esFIEL = esFirmaElectronica();
			if (esFIEL) {
				muestraResumenFirmas();
			} else {
				$("#imprimirBtn").button().click(function() {
					cmdImprimir('PolizaPago');
				});
				$("#imprimirAnexoBtn").button().click(function() {
					anexo();
				});
				$("#actualizarFirmantesBtn").button().click(function() {
					if( actualizaFirmantes() )
						cmdImprimir('PolizaPago');;
				});
				
				muestraEditaFirmantes();
				$("#operacionesConsultaDiv").show();
			}
			
			if( parent.document.getElementById("pb_cancel") ){
				parent.document.getElementById("pb_cancel").style.visibility='hidden';
				parent.document.getElementById("pb_cancel").disabled=true;
			}
		}
  	}

    function ResponsableSiguiente(id_oper){

  		 if(id_oper==1){
  			 
  			 return "CONSULTA_OPERAJENAS";

			}
  				
  	}
  	  	
  	function OperacionSiguiente(id_oper){

  		if(id_oper==1)
		return "consulta_operajenas";
  		
   
  	}

	function onPostDisplay(id_oper){
			$("#esperaDialog").dialog("open");
			// Para poner Como Aplicado sin generar poliza Conafor
			queryFormPost({ 
	
					queryName : "operAjenaUpdate",
					async : false,
					callback : function() {
						if ($("#chk_IP").prop("checked"))
							queryFormPost("operAjenaUpdateIP", {async: false });
						else if ($("#chk_radicado").prop("checked"))
							queryFormPost("operAjenaUpdateRadicado", {async: false });
						parent.document.getElementById("pb_send").style.visibility='visible';
						parent.document.getElementById("pb_send").disabled = false;
						if( $("#cEsFirmaElectronica").val() != "S" )
							cmdImprimir('PolizaPago');
						parent.document.getElementById("pb_send").click();
					}
			}); 
			$("#esperaDialog").dialog("close");
			
	}
	
	function formSubmited() {
		
    }		
	
	function Carga(){
		$("#CXPagar").hide();
		$("#ANEXO").hide();
		$("#divOACedular").hide();

		querySelectPost("tGrupoOpAjenasRead", "cBeneficiario", {async: false });
		querySelectPost("PagosAMFAjenasRead", "PagosAMF", {async: false });
		
		queryFormPost("TipoPolizaRead",{async: false });
		$("#tba2").hide();
		$("#vDetalle").hide();
		
		
		querySelectPost("catalogoEjercicioFiscalRead", "EF", {async: false });
		$("#esperardet").hide();

		if(<%=id_oper == 1%>){
			parent.document.getElementById("pb_send").disabled=true;
		    parent.document.getElementById("pb_save").disabled=true;
		}
		
		
		queryFormPost("FolioOperAjenasRead", {async: false });
		if ($("#cDocumentoHaplicado").val() == "S" && (<%=id_oper == 1%>)){
			parent.document.getElementById("pb_save").disabled=false;				
		}
		folio();
	}	
	
	function folio(){		
		if ($("#caNoContrarrecibo").val()!= ""){
			$(".encabezado").attr('disabled', true);
			$(".ocultar").hide();
			
			$("#Buscar").attr("disabled", true);		
			detalleGuardado();
		}
	
	}
	
	
	var  grupoBen=$("#cBeneficiario").val();
	
	function detalleGuardado(){
				
		var campos=$("#id_caso").val(); 
		var  elParametro2 ='';
				var szTabla = "CONSULTAOPERAJENAS";
				$.ajaxSetup({
				    async: false
				});
				$.getJSON("../catalogos/SelectJson.jsp",{Tabla: szTabla, Param: elParametro2,Campos:campos, MaxReg: "", ajax: false}, function(j){
					for (var i = 0; i < j.length; i++) {			
						$('#Busqueda1').dataTable().fnAddData(['*',j[i].Col7,j[i].Col0,j[i].Col1,j[i].Col2,j[i].Col3,j[i].Col4,j[i].Col8,j[i].Col5,j[i].Col6]);
						
						var moperajena = Number( $("#MOperAjena").val() ) + Number( j[i].Col4 );
						
						$("#MOperAjena").val( moperajena.toFixed(2) ) ;
						
						if (i+1 == j.length) {
							$("#MOperAjena").formatCurrency();
						}
				    }			
					$("#CXPagar").show();
					$("#ANEXO").show();

				});
				
				$.ajaxSetup({
				    async: true
				});
	
	
	    var nRows = $("#dt_compromiso tr").length -1 ;

		if ( nRows > 0 ){
		var oBusqueda = $('#Busqueda1').datatable();
		oBusqueda.fnClearTable();
		var oDetalle = $('#Detalle').datatable();
		oDetalle.fnClearTable();
		}

	}	
	    var marimpo = 0;
		var marhonor = 0;
		var marflete = 0;
		var marArre = 0;
		var marIMDT = 0;
		var marCNCI = 0;
		var mar5alM = 0;
		var marPenal = 0;
		var marCedular = 0;
		var marISRLaudos = 0;
		var marMOper = 0;
		var marcTmonto = 0;
		var marAjuste = 0;
		var marISROtros = 0;
	   	var marIva6 = 0;
	   	var marISRRESICO = 0;
		var valjson;
		var SUMmImporteFlete23 = 0;
	
	function restamMonto( Nombre ){
		
		var RestaMIF23 = 0;
		var ResHonor = 0;
		var ResbARR = 0;
		var ResFlete = 0;
		var ResIMDT = 0;
		var ResCNIC = 0;
		var Res5alM = 0;
		var ResPenal = 0;
		var ResCedular = 0;	
		var ResISRLaudos = 0;	
		var ResISROtros = 0;
		var ResIVA6 = 0;
		var ResISRRESICO = 0;
		var detalle="d"+Nombre;	
		SUMmImporteFlete23 = 0;
		var tipoRetencion = $("#cBeneficiario").val();		
		
		if ( $("#"+Nombre).is(':checked')  == false  ){
			$("#"+detalle).val(0);
			
			if(tipoRetencion == 7){				
				//$( "#subHonorSinRedondeo" ).val( $( "#subHonorSinRedondeo" ).val() - Number( valjson[ Nombre ].Col8 ) );
				//ResHonor = $("#subHonorSinRedondeo").val();
				ResHonor = quitaFmt( $("#subHonor").val() );
				ResHonor = Number( ResHonor ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col8 );
				$( "#subHonor" ).val( Number(ResHonor).toFixed(2) ) ;
				
				$( "#subArrendaSinRedondeo" ).val( $( "#subArrendaSinRedondeo" ).val() - Number( valjson[ Nombre ].Col9 ) );
				ResbARR = $( "#subArrendaSinRedondeo" ).val();				
				$( "#subtmImportFlete" ).val( Number(ResbARR).toFixed(2) );
				
				$( "#subFleteSinRedondeo" ).val( $( "#subFleteSinRedondeo" ).val() - Number( valjson[ Nombre ].Col12 ) );
				ResFlete = $("#subFleteSinRedondeo").val();
				$("#subFlete").val( Number(ResFlete).toFixed(2) );
				
			}else if (tipoRetencion = 8){
				ResHonor = quitaFmt( $("#subHonor").val() );
				ResHonor = Number( ResHonor ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col10 );
				$( "#subHonor" ).val( ResHonor.toFixed(2) ) ;
				
				ResbARR = quitaFmt( $( "#subARR" ).val() );
				ResbARR = Number( ResbARR ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col11 );
				$( "#subARR" ).val( ResbARR.toFixed(2) );
				
				ResISRRESICO = quitaFmt( $("#ImporteISRRESICO").val() );
				ResISRRESICO = Number( ResISRRESICO ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col26 );
				$("#ImporteISRRESICO").val(ResISRRESICO.toFixed(2) );
			}

			ResIMDT = quitaFmt( $("#IMDT").val() );
			ResIMDT = Number( ResIMDT ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col13 );
			$("#IMDT").val( ResIMDT.toFixed(2) );
			
			ResCNIC = quitaFmt( $("#CNIC").val() );
			ResCNIC = Number( ResCNIC ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col12 );
			$("#CNIC").val( ResCNIC.toFixed(2) );
			
			Res5alM = quitaFmt( $("#sub5alM").val() );
			Res5alM = Number( Res5alM ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col15 );
			$("#sub5alM").val( Res5alM.toFixed(2) );
			
			ResPenal = quitaFmt( $("#subPenal").val() );
			ResPenal = Number( ResPenal ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col16 );
			$("#subPenal").val(ResPenal.toFixed(2) );
			
			ResCedular = quitaFmt( $("#subCedular").val() );
			ResCedular = Number( ResCedular ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col17 );
			$("#subCedular").val(ResCedular.toFixed(2) );
			
			ResISRLaudos = quitaFmt( $("#ISRLAUDOS").val() );
			ResISRLaudos = Number( ResISRLaudos ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col18 );
			$("#ISRLAUDOS").val(ResISRLaudos.toFixed(2) );
			
			ResISROtros = quitaFmt( $("#ISROTROS").val() );
			ResISROtros = Number( ResISROtros ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col24 );
			$("#ISROTROS").val(ResISROtros.toFixed(2) );
			
			ResIVA6 = quitaFmt( $("#ImporteIVA6").val() );
			ResIVA6 = Number( ResIVA6 ) - Number( valjson[ parseInt( Nombre, 10 ) ].Col25 );
			$("#ImporteIVA6").val(ResIVA6.toFixed(2) );			
									
		}else{
			$("#"+detalle).val(1);
			
			if(tipoRetencion == 7){
				ResHonor = quitaFmt( $("#subHonor").val() );
				$( "#subHonorSinRedondeo" ).val( Number($( "#subHonorSinRedondeo" ).val()) + Number( valjson[ Nombre ].Col8 ) ) ;
				ResHonor = Number( ResHonor ) + Number( valjson[ Nombre ].Col8 );
				$( "#subHonor" ).val( ResHonor.toFixed(2) ) ;				
				
				ResbARR = quitaFmt( $( "#subtmImportFlete" ).val() );
				$( "#subArrendaSinRedondeo" ).val( Number($( "#subArrendaSinRedondeo" ).val()) + Number( valjson[ Nombre ].Col9 ) );
				ResbARR = Number( ResbARR ) + Number( valjson[ Nombre ].Col9 );
				$( "#subtmImportFlete" ).val( ResbARR.toFixed(2) );				
								
				ResFlete = quitaFmt( $("#subFlete").val() );
				$("#subFleteSinRedondeo").val( Number($("#subFleteSinRedondeo").val()) + Number( valjson[ Nombre ].Col12 ) );
				ResFlete = Number( ResFlete ) + Number( valjson[ Nombre ].Col12 );
				$("#subFlete").val( ResFlete.toFixed(2) );
							
			}else if (tipoRetencion = 8){
				ResHonor = quitaFmt( $("#subHonor").val() );
				ResHonor = Number( ResHonor ) + Number( valjson[ Nombre ].Col10 );
				$( "#subHonor" ).val( ResHonor.toFixed(2) ) ;
				
				ResbARR = quitaFmt( $( "#subARR" ).val() );
				ResbARR = Number( ResbARR ) + Number( valjson[ Nombre ].Col11 );
				$( "#subARR" ).val( ResbARR.toFixed(2) );
				
				ResISRRESICO = quitaFmt( $("#ImporteISRRESICO").val() );
				ResISRRESICO = Number( ResISRRESICO ) + Number( valjson[ Nombre ].Col26 );
				$("#ImporteISRRESICO").val(ResISRRESICO.toFixed(2) );
			}
			
			ResIMDT = quitaFmt( $("#IMDT").val() );
			ResIMDT = Number( ResIMDT ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col13 );
			$("#IMDT").val( ResIMDT.toFixed(2) );
			
			ResCNIC = quitaFmt( $("#CNIC").val() );
			ResCNIC = Number( ResCNIC ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col12 );
			$("#CNIC").val( ResCNIC.toFixed(2) );
			
            Res5alM = quitaFmt( $("#sub5alM").val() );
			Res5alM = Number( Res5alM ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col15 );
			$("#sub5alM").val( Res5alM.toFixed(2) );
			
			ResPenal = quitaFmt( $("#subPenal").val() );
			ResPenal = Number( ResPenal ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col16 );
			$("#subPenal").val(ResPenal.toFixed(2) );
			
			ResCedular = quitaFmt( $("#subCedular").val() );
			ResCedular = Number( ResCedular ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col17 );
			$("#subCedular").val(ResCedular.toFixed(2) );
			
			ResISRLaudos = quitaFmt( $("#ISRLAUDOS").val() );
			ResISRLaudos = Number( ResISRLaudos ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col18 );
			$("#ISRLAUDOS").val(ResISRLaudos.toFixed(2) );
			
			ResISROtros = quitaFmt( $("#ISROTROS").val() );
			ResISROtros = Number( ResISROtros ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col24 );
			$("#ISROTROS").val(ResISROtros.toFixed(2) );				
			
			ResIVA6 = quitaFmt( $("#ImporteIVA6").val() );
			ResIVA6 = Number( ResIVA6 ) + Number( valjson[ parseInt( Nombre, 10 ) ].Col25 );
			$("#ImporteIVA6").val(ResIVA6.toFixed(2) );					
				
		}
		
		DECmImporteFlete23 = quitaFmt(Number(ResFlete).toFixed(2)); 		
		DECmIVAHonorarios = quitaFmt(Number(ResHonor).toFixed(2)); 				
		DECmISRArrenda = quitaFmt(Number(ResbARR).toFixed(2)); 		
		DECmISRHonorarios = 0; 
		DECmISRHonorarios.toFixed(2);
		DECmImporteFlete4 = 0; 
		DECmImporteFlete4.toFixed(2);
		Dec5alM = 0; 
		Dec5alM.toFixed(2);
		DecPenal = 0; 
		DecPenal.toFixed(2);
		DecCedular = 0; 
		DecCedular.toFixed(2);
		DecISRLaudos = 0; 
		DecISRLaudos.toFixed(2);
		DecISROtros = parseFloat(quitaFmt( $("#ISROTROS").val()) ) -  parseInt(quitaFmt(  $("#ISROTROS").val()) ); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
		DecISROtros.toFixed(2);
		DecIVA6 = 0; 
		DecIVA6.toFixed(2);
		DecISRRESICO = Number(quitaFmt(ResISRRESICO.toFixed(2)));

		
		var moperajena = RestaMIF23 + ResHonor + ResbARR + ResFlete + ResIMDT + ResCNIC + Res5alM + ResPenal + ResCedular + ResISRLaudos + ResISROtros + ResIVA6 + ResISRRESICO;
		
		if(tipoRetencion == 7){
			TotalDEC = Number(quitaFmt(DECmImporteFlete23)) + Number(quitaFmt(DECmIVAHonorarios)) + Number(quitaFmt(DECmISRArrenda));
			//$("#AjusteREdon").val( (quitaFmt($( "#subHonorSinRedondeo" ).val()) - parseInt(quitaFmt($( "#subHonorSinRedondeo" ).val()),10)) + (quitaFmt($( "#subArrendaSinRedondeo" ).val()) - parseInt(quitaFmt($( "#subArrendaSinRedondeo" ).val()),10)) + (quitaFmt($( "#subFleteSinRedondeo" ).val()) - parseInt(quitaFmt($( "#subFleteSinRedondeo" ).val()),10)) ) ;
			$("#AjusteREdon").val("0.00") ;
			$("#MOperAjena").val( TotalDEC ) ;				
			$("#cTMonto").val( TotalDEC ) ;
		} else{
			TotalDEC = DECmImporteFlete23 + DECmIVAHonorarios + DECmISRHonorarios + DECmISRArrenda + DECmImporteFlete4 +  Dec5alM + DecPenal + DecCedular + DecISRLaudos + DecISROtros + DecIVA6 + DecISRRESICO;
			$("#AjusteREdon").val("0.00") ;			
			$("#MOperAjena").val( moperajena.toFixed(2) ) ;				
			$("#cTMonto").val( moperajena.toFixed( 2 ) ) ;
		}
						
		$("#subCedular").formatCurrency();
		$("#subPenal").formatCurrency();
		$("#sub5alM").formatCurrency();
		$("#CNIC").formatCurrency();
		$("#IMDT").formatCurrency();
		$("#subFlete").formatCurrency();
		$("#subARR").formatCurrency();
		$("#subHonor").formatCurrency();
		$("#subtmImportFlete").formatCurrency();
		$("#ISRLAUDOS").formatCurrency();
		$("#ImporteIVA6").formatCurrency();
		$("#ImporteISRRESICO").formatCurrency();
		$("#AjusteREdon").formatCurrency();
		$("#MOperAjena").formatCurrency();
		$("#cTMonto").formatCurrency();
	}
	
		
	var retencion1;
	function grupoRetencion(){
		
		$("#cConcepto").val("");
		$("#cIdTipodocumento").val("");
		$("#cDescripcionPoliza").val("");
		$(".ocultar").hide();
		$("#ajuste").show();
		$("#total").show();
		$("#fechas").show();
		queryFormPost("tGrupoOpAjenasOnclicRead", {async: false });
		var tipoRetencion = $("#cBeneficiario").val();
		
		$("#cDescripcionPoliza").val($("#cConcepto").val());
				
		if ($("#csubtmImportFlete1").val() == '1') {
        	$("#subtmImportFlete1").show();
        	$("#millar").hide();
    		$("#subPenal1").hide();
    		$("#subCedular1").hide();    		    		
    		$("#IMDT1").hide();
    		$("#CNIC1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
    		$("#ISRRESICO1").hide();
           	document.getElementById("chk_LSICOP").checked = true;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}
		
		if ($("#csubHonor1").val() == '1' ){
           	$("#subHonor1").show();
           	$("#millar").hide();
    		$("#subPenal1").hide();
    		$("#subCedular1").hide();    		    		
    		$("#IMDT1").hide();
    		$("#CNIC1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
           	document.getElementById("chk_LSICOP").checked = true;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}

		if ($("#csubARR1").val() == '1' ){
           	$("#subARR1").show();
           	$("#millar").hide();
    		$("#subPenal1").hide();
    		$("#subCedular1").hide();    		    		
    		$("#IMDT1").hide();
    		$("#CNIC1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
           	document.getElementById("chk_LSICOP").checked = true;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}		
		
		if ($("#csubFlete1").val() == '1' ){
           	$("#subFlete1").show();
           	$("#millar").hide();
    		$("#subPenal1").hide();
    		$("#subCedular1").hide();    		    		
    		$("#IMDT1").hide();
    		$("#CNIC1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
           	document.getElementById("chk_LSICOP").checked = true;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}
		
		if ($("#cIMDT1").val() == '1') {
           	$("#IMDT1").show();
           	$("#millar").hide();
	   		$("#subPenal1").hide();
	   		$("#subCedular1").hide();
	   		$("#subtmImportFlete1").hide();
	   		$("#subHonor1").hide();
	   		$("#subARR1").hide();
	   		$("#subFlete1").hide();
	   		$("#CNIC1").hide();
	   		$("#ISRLAUDOS1").hide();
	   		$("#ISROTROS1").hide();
	   		$("#IVA61").hide();
	   		$("#ISRRESICO1").hide();
           	document.getElementById("chk_LSICOP").checked = false;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}
		
		if ($("#cCNIC1").val() == '1') {
           	$("#CNIC1").show();
           	$("#millar").hide();
    		$("#subPenal1").hide();
    		$("#subCedular1").hide();
    		$("#subtmImportFlete1").hide();
    		$("#subHonor1").hide();
    		$("#subARR1").hide();
    		$("#subFlete1").hide();
    		$("#IMDT1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
    		$("#ISRRESICO1").hide();
           	document.getElementById("chk_LSICOP").checked = false;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}
		
		if ($("#cmillar").val() == '1' ){
           	$("#millar").show();
    		$("#subPenal1").hide();
    		$("#subCedular1").hide();
    		$("#subtmImportFlete1").hide();
    		$("#subHonor1").hide();
    		$("#subARR1").hide();
    		$("#subFlete1").hide();
    		$("#IMDT1").hide();
    		$("#CNIC1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
    		$("#ISRRESICO1").hide();
           	document.getElementById("chk_LSICOP").checked = false;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}
		
		if ($("#csubPenal1").val() == '1') {
           	$("#subPenal1").show();
           	$("#millar").hide();
    		$("#subCedular1").hide();
    		$("#subtmImportFlete1").hide();
    		$("#subHonor1").hide();
    		$("#subARR1").hide();
    		$("#subFlete1").hide();
    		$("#IMDT1").hide();
    		$("#CNIC1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
    		$("#ISRRESICO1").hide();
           	document.getElementById("chk_LSICOP").checked = false;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}
		
		if ($("#csubCedular1").val() == '1') {
           	$("#subCedular1").show();
           	$("#millar").hide();
    		$("#subPenal1").hide();
    		$("#subtmImportFlete1").hide();
    		$("#subHonor1").hide();
    		$("#subARR1").hide();
    		$("#subFlete1").hide();
    		$("#IMDT1").hide();
    		$("#CNIC1").hide();
    		$("#ISRLAUDOS1").hide();
    		$("#ISROTROS1").hide();
    		$("#IVA61").hide();
    		$("#ISRRESICO1").hide();
           	document.getElementById("chk_LSICOP").checked = false;
           	document.getElementById("chk_PagoBancoIF").checked = true;
           	$("#divOACedular").show();
		}
		
		if ($("#cImporteIVA6").val() == '1') {
           	$("#IVA61").show();
           	$("#millar").hide();
	   		$("#subPenal1").hide();
	   		$("#subCedular1").hide();
	   		$("#subtmImportFlete1").hide();
	   		$("#subHonor1").hide();
	   		$("#subARR1").hide();
	   		$("#subFlete1").hide();
	   		$("#IMDT1").hide();
	   		$("#CNIC1").hide();
	   		$("#ISRLAUDOS1").hide();
	   		$("#ISROTROS1").hide();
	   		$("#ISRRESICO1").hide();
           	document.getElementById("chk_LSICOP").checked = true;
           	document.getElementById("chk_PagoBancoIF").checked = false;
           	$("#divOACedular").hide();
		}
		
		if ($("#cimporteISRResico").val() == '1') {
	    	$("#ISRRESICO1").show();
	        $("#millar").hide();
	   		$("#subPenal1").hide();
	   		$("#subCedular1").hide();
	   		$("#subtmImportFlete1").hide();	   		
	   		$("#subFlete1").hide();
	   		$("#IMDT1").hide();
	   		$("#CNIC1").hide();
	   		$("#ISRLAUDOS1").hide();
	   		$("#IVA61").hide();
	        document.getElementById("chk_LSICOP").checked = true;	
	        document.getElementById("chk_PagoBancoIF").checked = false;
	        $("#divOACedular").hide();
		}
		
		if ($("#cConcepto").val()=="ISR Laudos"){
			$("#ISRLAUDOS1").show();
			$("#millar").hide();
			$("#subPenal1").hide();
			$("#subCedular1").hide();
			$("#subtmImportFlete1").hide();
			$("#subHonor1").hide();
			$("#subARR1").hide();
			$("#subFlete1").hide();
			$("#IMDT1").hide();
			$("#CNIC1").hide();
			$("#ISROTROS1").hide();
			$("#IVA61").hide();
			$("#ISRRESICO1").hide();
			document.getElementById("chk_LSICOP").checked = true;	
			document.getElementById("chk_PagoBancoIF").checked = false;
			$("#divOACedular").hide();
		}
		
		if ($("#cConcepto").val()=="Otros ISR"){
			$("#ISROTROS1").show();
			$("#millar").hide();
			$("#subPenal1").hide();
			$("#subCedular1").hide();
			$("#subtmImportFlete1").hide();
			$("#subHonor1").hide();
			$("#subARR1").hide();
			$("#subFlete1").hide();
			$("#IMDT1").hide();
			$("#CNIC1").hide();
			$("#ISRLAUDOS1").hide();
			$("#IVA61").hide();
			$("#ISRRESICO1").hide();
			document.getElementById("chk_LSICOP").checked = false;
			document.getElementById("chk_PagoBancoIF").checked = false;
			$("#divOACedular").hide();
		}	
	
	}
	
	function setSequenceVal(seqValue) {
		seqValue = "000000" + seqValue;
		seqValue = seqValue.substr(seqValue.length - 6);
		seqValue = "1" + seqValue.substr(seqValue.length - 5);
		seqValue = $("#cCentroContable").val() + cxpPrefijo + $("#EF").val() + seqValue;
		$("#caNoContrarrecibo").val( seqValue );
	}
	
	
	function guardatabladetalle(){
		
		$( "#fBusquedaDe").val();
		$( "#fBusquedaHasta").val();
		
		$("#cMes").val($("#fAplicacion").val().split("/")[1]);
			var tableB = document.getElementById('Busqueda1');
			var tableD = document.getElementById('Detalle');
			var restaEp=true;
			var tipoRetencion = $("#cBeneficiario").val();
			
			if (tipoRetencion == 7){
				var DECmImporteFlete23 = parseFloat( quitaFmt( $("#subtmImportFlete").val()) ) -  parseInt( quitaFmt(  $("#subtmImportFlete").val() )); 
			 	DECmImporteFlete23.toFixed(2);			
				var DECmIVAHonorarios = parseFloat(quitaFmt(  $("#subHonor").val()) ) -  parseInt( quitaFmt(  $("#subHonor").val()) );
				DECmIVAHonorarios.toFixed(2);	
				var DECmISRArrenda = parseFloat(quitaFmt(  $("#subARR").val()) ) -  parseInt( quitaFmt(  $("#subARR").val()) );
				DECmISRArrenda.toFixed(2);			
			}
							
			var DECmISRHonorarios = 0; 
				DECmISRHonorarios.toFixed(2);			
			var DECmImporteFlete4 = 0; 
				DECmImporteFlete4.toFixed(2);			
			var Dec5alM = 0; 
				Dec5alM.toFixed(2);
			var DecPenal = 0;
				DecPenal.toFixed(2);
			var DecCedular = 0; 
				DecCedular.toFixed(2);
			var DecISRLaudos = 0; 
				DecISRLaudos.toFixed(2);
			var DecISROtros = parseFloat(quitaFmt(  $("#ISROTROS").val() )) -  parseInt( quitaFmt(  $("#ISROTROS").val() )); //se asigna el valor a 0 para que no haga ajustes, solicitado por CNF
				DecISROtros.toFixed(2);
			var DECmIva6 = 0;
				DECmIva6.toFixed(2);
			var DECmIsrRESICO = 0;
				DECmIsrRESICO.toFixed(2);
			
						var rowCount = tableB.rows.length;
						for(var i=1; i<rowCount; i++) {
							var rowB = tableB.rows[i];
							var rowD=  tableD.rows[i];
							var sCheckGrd = '';																																																																																																																									
							try{
							
								var ContrarreciboGrd = "";
								ContrarreciboGrd =rowD.cells[1].innerHTML;
								
								var nFolioDocGrd = "";
								nFolioDocGrd = rowD.cells[2].innerHTML;
								
								var fApliDDGrd = "";
								fApliDDGrd = rowD.cells[3].innerHTML;
								
								var cTipoDocDGrd = "";
								cTipoDocDGrd = rowD.cells[4].innerHTML;
								
								var EpDGrd = "";
								EpDGrd = rowD.cells[5].innerHTML;
								
								var mTotalDGrd = 0;
								mTotalDGrd = rowD.cells[6].innerHTML;
								
								var mEnteroDGrd = 0;
								mEnteroDGrd = rowD.cells[7].innerHTML;
								
								var mSobranteDGrd = 0;
								mSobranteDGrd = rowD.cells[8].innerHTML;
								
								var mImporteFlete23DGrd = 0;
								mImporteFlete23DGrd = rowD.cells[9].innerHTML;
								
								var mIVAHonorariosDGrd = 0;
								mIVAHonorariosDGrd = rowD.cells[10].innerHTML;
																
								var mISRHonorariosDGrd = 0;
								mISRHonorariosDGrd = rowD.cells[11].innerHTML;
								
								var mISRArrendaDGrd = 0;
								mISRArrendaDGrd = rowD.cells[12].innerHTML;
								
								var mImporteFlete4DGrd = 0;
								mImporteFlete4DGrd = rowD.cells[13].innerHTML;
								
								var mCNIC = 0;
								mCNIC = rowD.cells[15].innerHTML;
								
								var mIMDT = 0;
								mIMDT = rowD.cells[14].innerHTML;
								
								var mObra5 = 0;
								mObra5 = rowD.cells[16].innerHTML;
								
								var mTesofe = 0;
								mTesofe = rowD.cells[17].innerHTML;
									
								var mRetImpuestoCedular = 0;
								mRetImpuestoCedular = rowD.cells[18].innerHTML;
								
								var mImporteISRLaudos = 0;
								mImporteISRLaudos = rowD.cells[19].innerHTML;
								
								var mISROtros = 0;
								mISROtros = rowD.cells[21].innerHTML;
								
								var cMes = "";
								cMes = rowD.cells[20].innerHTML;
									
								var RFCDetalleGrd = "";
								RFCDetalleGrd = rowB.cells[7].innerHTML;
										
								var sCheckGrd= "";
								sCheckGrd= rowB.cells[0].innerHTML;
								
								var mIVA6 = 0;
								mIVA6 = rowD.cells[22].innerHTML;
								
								var mISRRESICOGrd = 0;
								mISRRESICOGrd = rowD.cells[23].innerHTML;
									
							}catch(e) {
								console.log(e);
							}
							//if(null != sCheckGrd) 
							if ( $("#"+(i-1)).is(':checked')  ){
							
								if ( Number( mImporteFlete23DGrd.data ) > DECmImporteFlete23 ) {
									mImporteFlete23DGrd.data = Number( mImporteFlete23DGrd.data ) - Number( DECmImporteFlete23 );
									mTotalDGrd.data = Number( mTotalDGrd.data ) - Number( DECmImporteFlete23 );
									DECmImporteFlete23 = 0;
								}
								
								if ( Number( mIVAHonorariosDGrd.data ) > DECmIVAHonorarios ) {
									mIVAHonorariosDGrd.data = Number( mIVAHonorariosDGrd.data ) - Number( DECmIVAHonorarios );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DECmIVAHonorarios );
									 DECmIVAHonorarios = 0;
								}
								
								if ( Number( mISRHonorariosDGrd.data ) > DECmISRHonorarios ) {
									 mISRHonorariosDGrd.data = Number( mISRHonorariosDGrd.data ) - Number( DECmISRHonorarios );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DECmISRHonorarios );
									 DECmISRHonorarios = 0;
								}
								
								if ( Number( mISRArrendaDGrd.data ) > DECmISRArrenda ) {
									 mISRArrendaDGrd.data = Number( mISRArrendaDGrd.data ) - Number( DECmISRArrenda );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DECmISRArrenda );
									 DECmISRArrenda = 0;
								}
								
								if ( Number( mImporteFlete4DGrd.data ) > DECmImporteFlete4 ) {
									 mImporteFlete4DGrd.data = Number( mImporteFlete4DGrd.data ) - Number( DECmImporteFlete4 );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DECmImporteFlete4 );
									 DECmImporteFlete4 = 0;
								}
																
								if ( Number( mObra5.data ) > Dec5alM ) {
									 mObra5.data = Number( mObra5.data ) - Number( Dec5alM );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( Dec5alM );
									 Dec5alM = 0;
								}
								
								if ( Number( mTesofe.data ) > DecPenal ) {
									 mTesofe.data = Number( mTesofe.data ) - Number( DecPenal );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DecPenal );
									 DecPenal = 0;
								}
								
								if ( Number( mRetImpuestoCedular.data ) > DecCedular ) {
									 mRetImpuestoCedular.data = Number( mRetImpuestoCedular.data ) - Number( DecCedular );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DecCedular );
									 DecCedular = 0;
								}
								
								if ( Number( mImporteISRLaudos.data ) > DecISRLaudos ) {
									 mImporteISRLaudos.data = Number( mImporteISRLaudos.data ) - Number( DecISRLaudos );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DecISRLaudos );
									 DecISRLaudos = 0;
								}
								
								if ( Number( mISROtros.data ) > DecISROtros ) {
									 mISROtros.data = Number( mISROtros.data ) - Number( DecISROtros );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DecISROtros );
									 DecISROtros = 0;
								}
								
								if ( Number( mIVA6.data ) > DECmIva6) {
									 mIVA6.data = Number( mIVA6.data ) - Number( DECmIva6 );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DECmIva6 );
									 DECmIva6 = 0;
								}
								
								if ( Number( mISRRESICOGrd.data ) > DECmIsrRESICO) {
									mISRRESICOGrd.data = Number( mISRRESICOGrd.data ) - Number( DECmIsrRESICO );
									 mTotalDGrd.data =  Number( mTotalDGrd.data ) - Number( DECmIsrRESICO );
									 DECmIsrRESICO = 0;
								}
								limpiarInputDet();
								
								$("#CONTRA").val(ContrarreciboGrd.toString());
								$("#nFolioDoc").val(nFolioDocGrd.toString());
								$("#fApliD").val(fApliDDGrd.toString());																
								$("#cTipoDoc").val(cTipoDocDGrd.toString());
								$("#Ep").val(EpDGrd.toString());
								$("#mTotal").val(mTotalDGrd.toString());
							    $("#mEntero").val(mEnteroDGrd.toString());
								$("#mSobrante").val(mSobranteDGrd.toString());
								$("#mImporteFlete23").val(mImporteFlete23DGrd.toString());
								$("#mIVAHonorarios").val(mIVAHonorariosDGrd.toString());
								$("#mISRHonorarios").val(mISRHonorariosDGrd.toString());
								$("#mISRArrenda").val(mISRArrendaDGrd.toString());
								$("#mImporteFlete4").val(mImporteFlete4DGrd.toString());
								$("#mCNIC").val(mCNIC.toString());
								$("#mIMDT").val(mIMDT.toString());
								$("#mObra5").val(mObra5.toString());
								$("#mTesofe").val(mTesofe.toString());
								$("#mRetImpuestoCedular").val(mRetImpuestoCedular.toString());
								$("#mImporteISRLaudos").val(mImporteISRLaudos.toString());
								$("#mISROtros").val(mISROtros.toString());
								$("#mImporteIva6").val(mIVA6.toString());
								$("#mimporteISRResico").val(mISRRESICOGrd.toString());
								$("#ACDIV").val("00001");
								if ($("#chk_PagoBancoIF").prop("checked")){
									$("#cEvento").val("P_AJENA123_IF");
								}else{																
									$("#cEvento").val(obtieneEventoPartida($("#Ep").val()));
								}
								$("#RFCDetalle").val(RFCDetalleGrd.toString());	
								$("#cMes").val( cMes.toString() );
								queryFormPost("leeTipoIva",{async: false });
								if ($("#tipoIVA").val()!=""){
									if ($("#tipoIVA").val()=="ARRENDAMIENTO"){
										$("#mImporteIvaArrenda").val($("#mImporteFlete23").val());
										$("#mImporteFlete23").val("0.00");
				 						$("#mImporteIvaHonorarios").val("0.00");
									}
									else if ($("#tipoIVA").val()=="HONORARIOS"){
										$("#mImporteIvaHonorarios").val($("#mIVAHonorarios").val());
										$("#mImporteFlete23").val("0.00");
										$("#mImporteIvaArrenda").val("0.00");
									}
								}
								obtieneSubcuenta();	
							}
						}
	}
	
	function limpiarInputDet(){
	
		$("#CONTRA").val("");
		$("#nFolioDoc").val("");
		$("#fApliD").val("");																
		$("#cTipoDoc").val("");
		$("#Ep").val("");
		$("#mTotal").val("0.00");
	    $("#mEntero").val("0.00");
		$("#mSobrante").val("0.00");
		$("#mImporteFlete23").val("0.00");
		$("#mIVAHonorarios").val("0.00");
		$("#mISRHonorarios").val("0.00");
		$("#mISRArrenda").val("0.00");
		$("#mImporteFlete4").val("0.00");
		$("#mCNIC").val("0.00");
		$("#mIMDT").val("0.00");
		$("#mObra5").val("0.00");
		$("#mTesofe").val("0.00");
		$("#mRetImpuestoCedular").val("0.00");
		$("#mImporteISRLaudos").val("0.00");
		$("#mISROtros").val("0.00");
		$("#mImporteIva6").val("0.00");
		$("#mimporteISRResico").val("0.00");		
		$("#ACDIV").val("");		
		$("#cEvento").val("");		
		$("#RFCDetalle").val("");	
		$("#cMes").val( "" );
		$("#tipoIVA").val("");
		
		$("#mImporteIvaArrenda").val("0.00");
		$("#mImporteFlete23").val("0.00");
		$("#mImporteIvaHonorarios").val("0.00");
	
	}
		
		
	function obtieneSubcuenta(){
		var tmpImp23=$("#mImporteFlete23").val();
		var tmpIVAo=$("#mIVAHonorarios").val();
		var tmpISRo=$("#mISRHonorarios").val();
		var tmpISRa=$("#mISRArrenda").val();
		var tmpIF4=$("#mImporteFlete4").val();	
		
		$("#nSubcuenta").val('');
		$("#nSubSubCuenta").val('');
		queryFormPost("tOperAjenasDetalleCreate",{async: false });

	}
		
	function ver(){
		$("#tba2").show();
		$("#tba2").click();
	}
	
	function GuardaContrarecibo(){
		$.ajaxSetup({
		    async: false
		});
		
		elParametro = "'"+$("#caNoContrarrecibo").val()+"', '1', '0', '0', '"+$("#cTMonto").val()+"', '0', '0', '0', '0','0', '0', '"+$("#cTMonto").val()+"', '','" + $("#cCentroContable").val() + "',"+$("#EF").val()+",'PAGO OPERACIONES AJENAS', '6','0','"+$("#cIDRFC").val()+"',0";
		$.getJSON("../catalogos/InsertJson.jsp",{Tabla: "TCONTRARECIBODIVERSOSCREATE", Param: elParametro, MaxReg: "", ajax: false}, function(j){});		
		$.ajaxSetup({  async: true	});
	}
	
	function GenerarPasivoDiferido(){	
		var tipoRetencion = $("#cBeneficiario").val();
				
		var contrarecibo = $("#caNoContrarrecibo").val();
		
		if(tipoRetencion == 9)
			Swal.fire({ icon: 'info',
						text: "Genera Pasivo Diferido para la Ajena: " + contrarecibo });
		
		if(tipoRetencion == 7 || tipoRetencion == 8)
			Swal.fire({ icon: 'info',
						text: "En caso de contener pagos de RG, se generará el Pasivo Diferido para la Ajena: " + contrarecibo });
			
		$.ajax({
			dataType: "json",
  			url: "../egresos/AplicaPasivoDiferido",	
  			data: {				
				contrarrecibo: contrarecibo,
				tipoRetencion: tipoRetencion
			},
  			async: false, 
			success: function(j){
			
			}		
		});
	}
	
	var tipo;

	function anexo(){
		window.open(
			"../admin/SeguridadCatalogos?"
				+ "catalogo=ANEXO"
				+ "&accion=run"
				+ "&rn=OperAjenas.jasper"
				+ "&nFolioOperAjenas=" + $("#id_caso").val(),
			"Anexo",
			"scrollbars=1, resizable=yes, width=1024, height=768");
		
	}

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
    		
    function desmar(){   	
    	
    	if ($('#Desmarcar').val() == "desmarcar" ){
			
    		$(".desmarcar1").each(function(){
    			$(this).attr('checked',false);
   				document.getElementById($(this).attr('id')).checked = false;
    		});
    		
//     		$(".desmarcar1").attr('checked', false);
    		
    		
 			$("#subtmImportFlete").val(0);
			$("#subHonor").val(0);
			$("#subFlete").val(0);
		    $("#subARR").val(0);
			$("#IMDT").val(0);
			$("#CNIC").val(0);
			$("#sub5alM").val(0);
			$("#subPenal").val(0);
			$("#subCedular").val(0);
			$("#ISRLAUDOS").val(0);
			$("#MOperAjena").val(0);
			$("#cTMonto").val(0);
			$("#AjusteREdon").val(0);
			$("#ImporteIVA6").val(0);
			$("#ImporteISRRESICO").val(0);
			$( "#subHonorSinRedondeo" ).val(0);
			$("#subArrendaSinRedondeo").val(0);
			$("#subFleteSinRedondeo").val(0);
			$(".desmarcar2").val(0);
    		$('#Desmarcar').val("Marcar");
    		
   		}else{
   			
   			$(".desmarcar1").each(function(){
   				$(this).attr('checked',true);
   				document.getElementById($(this).attr('id')).checked = true;
    		});
   			
   			var tipoRetencion = $("#cBeneficiario").val();
   			
   			if(tipoRetencion == 7){
   				//var cenHonor = quitaFmt(marhonor) - parseInt( quitaFmt(marhonor),10 );
   				//var cenArrenda = quitaFmt(marArre) - parseInt( quitaFmt(marArre),10 );
   				//var cenFlete = quitaFmt(marimpo) - parseInt( quitaFmt(marimpo),10 );
   				
   				$("#subtmImportFlete").val(marimpo);
   				$("#subHonor").val(marhonor);
   				$("#subFlete").val(marflete);
   				$("#subARR").val(marArre);
   				//$("#AjusteREdon").val(cenHonor + cenArrenda + cenFlete).formatCurrency();
   				$("#AjusteREdon").val("0.00");
   				$("#MOperAjena").val(Number(quitaFmt(marimpo)) + Number(quitaFmt(marhonor)) + Number(quitaFmt(marArre))).formatCurrency();
   				$("#cTMonto").val(Number(quitaFmt(marimpo)) + Number(quitaFmt(marhonor)) + Number(quitaFmt(marArre))).formatCurrency();
   				
   			}else if(tipoRetencion == 8){
   				$("#subtmImportFlete").val(marimpo);
				$("#subHonor").val(marhonor);
				$("#subFlete").val(marflete);
			    $("#subARR").val(marArre);
			    $("#ImporteISRRESICO").val(marISRRESICO);
			    $("#ImporteISRLaudos").val(marIVA6);
			    $("#AjusteREdon").val(marAjuste);
				$("#MOperAjena").val(marMOper);
				$("#cTMonto").val(marcTmonto);
   			}else if(tipoRetencion == 6){
				$("#MOperAjena").val(marCedular);
				$("#cTMonto").val(marCedular);
   			}
   			
			$("#IMDT").val(marIMDT);
			$("#CNIC").val(marCNCI);
			$("#sub5alM").val(mar5alM);
			$("#subPenal").val(marPenal);
			$("#subCedular").val(marCedular);
			$("#ISRLAUDOS").val(marISRLaudos);
			$("#ISROTROS").val(marISROtros);
			$("#ImporteIVA6").val(marIVA6);
			$(".desmarcar2").val(1);
    		$('#Desmarcar').val("desmarcar");
		}		

    }
 			
 	function LetrasNums(evt) {
		var keyPressed = (evt.which) ? evt.which : event.keyCode;
		if (keyPressed > 123 && keyPressed != 209 && keyPressed != 241){			
			Swal.fire({ icon: 'warning',
						text: "Solo se permiten Letras y Numeros" });
	 	}
	 	if (keyPressed == 61 || keyPressed == 63 || keyPressed == 62 ||
	 		 keyPressed == 59 || keyPressed == 58 || keyPressed == 60 ||
	 	 	keyPressed == 91 || keyPressed == 92 || keyPressed == 93 ||
	 	 	keyPressed == 94 || keyPressed == 95 || keyPressed == 96)
		{return false; }
	 	
		return !(keyPressed > 32 && (keyPressed < 48 || keyPressed > 122) && keyPressed != 209 && keyPressed != 241);
	}

	function obtieneEventoPartida(EP){
    	   	
    	var cEvento = "";
    	var cCapitulo = "";
    	var cEsIP = "";
    	
    	cEsIP = EP.substring(39, 40);
    	cCapitulo = EP.substring(31, 32);
    	    	
    	if(cEsIP == "4" ){    	
    		//cEvento = "C_AJENA";
    		cEvento = "P_AJENA_IP";
    	}else{
    		if ($("#chk_PagoBancoIF").prop("checked")){
    			if(cCapitulo == "1" || cCapitulo == "2" || cCapitulo == "3"){
		    		cEvento = "P_AJENA123_IF";
		    	}else if( cCapitulo == "4" ){
		    		cEvento = "P_AJENA4_IF";
		    	}else if( cCapitulo == "5" || cCapitulo == "6"){
		    		cEvento = "P_AJENA56_IF";	
		    	}
    		}else{
	    		if(cCapitulo == "1" || cCapitulo == "2" || cCapitulo == "3"){
		    		cEvento = "P_AJENA123";
		    	}else if( cCapitulo == "4" ){
		    		cEvento = "P_AJENA4";
		    	}else if( cCapitulo == "5" || cCapitulo == "6"){
		    		cEvento = "P_AJENA56";	
		    	}
	    	}
    	}
   	
    	return cEvento;    	
    }
    
</script>

</head>

<body id="dt_example">
	<form>		
		<label id="lbRFC"></label> 
		<!--VGCFIEL-->
		 <input type="hidden" id="cNombreEla" name="cNombreEla"  />
		 <input type="hidden" id="cPaternoEla" name="cPaternoEla"  />
		 <input type="hidden" id="cMaternoEla" name="cMaternoEla"  />
		 <input type="hidden" id="cPuestoEla" name="cPuestoEla"  />
		 <input type="hidden" id="cEsFirmaElectronica" name="cEsFirmaElectronica"  />
		 <input type="hidden" name="nNumEmpleadoElab" id="nNumEmpleadoElab" value="-1"/>
		 <input type="hidden" name="cTipoFirmante" id="cTipoFirmante" value="" />
		 <input type="hidden" name="cTipoPago" id="cTipoPago" value="OPERAJENAS" />
		 <input type="hidden" name="cIdUsuarioCaptura" id="cIdUsuarioCaptura" value="" />
		 
		<input type="hidden" name="checkbox" id="checkbox" /> 
		<input type="hidden" name="ncheked" id="ncheked" /> 
		<input type="hidden" name="DCD_TBEN" id="DCD_TBEN" /> 
		<input type="hidden" name="DCD_CBEN" id="DCD_CBEN" /> 
		<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /> 
		<input type="hidden" name="nFolioPago" id="nFolioPago" value="<%=c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>" /> 
		<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable" /> 
		<input type="hidden" id="cRamo" name="cRamo" value="16 " /> 
		<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" /> 
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="OIRAUSU" name="OIRAUSU" /> 
		<input type="hidden" id="CamInst" name="CamInst" /> <select id="EF" name="EF" style="visibility: hidden"></select> 
		<input type="hidden" id="mTotal" name="mTotal" value="0" /> 
		<input name="cDocumento" type="hidden" id="cDocumento" value="OPERAJENAS" /> 
		<input type="hidden" id="cTipoPoliza" name="cTipoPoliza" value="" /> 
		<input type="hidden" id="mSobrante" name="mSobrante" value="0" /> 
		<input type="hidden" id="mImporteFlete23" name="mImporteFlete23" value="0" /> 
		<input type="hidden" id="mIVAHonorarios" name="mIVAHonorarios" value="0" />
		<input type="hidden" id="mISRHonorarios" name="mISRHonorarios" value="0" /> 
		<input type="hidden" id="mISRArrenda" name="mISRArrenda" value="0" /> 
		<input type="hidden" id="mImporteFlete4" name="mImporteFlete4" value="0" /> 
		<input type="hidden" id="mCNIC" name="mCNIC" value="0" /> 
		<input type="hidden" id="mIMDT" name="mIMDT" value="0" /> 
		<input type="hidden" id="mObra5" name="mObra5" value="0" /> 
		<input type="hidden" id="mTesofe" name="mTesofe" value="0" /> 
		<input type="hidden" id="mRetImpuestoCedular" name="mRetImpuestoCedular" value="0" /> 
		<input type="hidden" id="mImporteISRLaudos" name="mImporteISRLaudos" value="0" /> 
		<input type="hidden" id="mISROtros" name="mISROtros" value="0" /> 
		<input type="hidden" id="nFolioDoc" name="nFolioDoc" value="0" /> 
		<input type="hidden" id="cTRetencion" name="cTRetencion" value="0" /> 
		<input type="hidden" id="Ep" name="Ep" /> 
		<input type="hidden" id="cTipoDoc" name="cTipoDoc" /> 
		<input type="hidden" id="cEvento" name="cEvento" /> 
		<input type="hidden" id="nSubcuenta" name="nSubcuenta" /> 
		<input type="hidden" id="nSubSubCuenta" name="nSubSubCuenta" /> 
		<input type="hidden" id="mEntero" name="mEntero" value="0" /> 
		<input type="hidden" id="cMes" name="cMes" /> 
		<input type="hidden" id="fApliD" name="fApliD" /> 
		<input type="hidden" id="ACDIV" name="ACDIV" /> 
		<input type="hidden" id="cIdCuentaContable" name="cIdCuentaContable" /> 
		<input type="hidden" id="CONTRA" name="CONTRA" /> 
		<input type="hidden" id="cDescripcionPoliza" name="cDescripcionPoliza" /> 
		<input type="hidden" id="RFCDetalle" name="RFCDetalle" /> 
		<input type="hidden" id="cIdTipodocumento" name="cIdTipodocumento" /> 
		<input type="hidden" id="csubtmImportFlete1" name="csubtmImportFlete1" /> 
		<input type="hidden" id="csubHonor1" name="csubHonor1" /> 
		<input type="hidden" id="cImporteIVA6" name="cImporteIVA6" />
		<input type="hidden" id="cimporteISRResico" name="cimporteISRResico" /> 
		<input type="hidden" id="csubARR1" name="csubARR1" /> 
		<input type="hidden" id="csubFlete1" name="csubFlete1" /> 
		<input type="hidden" id="cIMDT1" name="cIMDT1" /> 
		<input type="hidden" id="cCNIC1" name="cCNIC1" /> 
		<input type="hidden" id="cmillar" name="cmillar" /> 
		<input type="hidden" id="csubPenal1" name="csubPenal1" /> 
		<input type="hidden" id="csubCedular1" name="csubCedular1" /> 
		<input type="hidden" id="cIVA61" name="cIVA61" />
		<input type="hidden" id="cISRRESICO" name="cISRRESICO" />  
		<input type="hidden" id="cGrupo" name="cGrupo" /> 
		<input type="hidden" id="cCondiciones" name="cCondiciones" /> 
		<input type="hidden" id="cSumaRetenciones" name="cSumaRetenciones" /> 
		<input type="hidden" id="cDocumentoHaplicado" name="cDocumentoHaplicado" value="" /> 
		<input type="hidden" id="tipoIVA" name="tipoIVA" value="" /> 
		<input type="hidden" id="mImporteIvaArrenda" name="mImporteIvaArrenda" value="0.00" /> 
		<input type="hidden" id="mImporteIvaHonorarios" name="mImporteIvaHonorarios" value="0.00" /> 
		<input type="hidden" id="mImporteIva6" name="mImporteIva6" value="0.00" />
		<input type="hidden" id="mimporteISRResico" name="mimporteISRResico" value="0.00" /> 
		<input type="hidden" id="DESTINO_GASTO" name="DESTINO_GASTO" />
		<input type="hidden" id="subHonorSinRedondeo" name="subHonorSinRedondeo" value="0.00" />
		<input type="hidden" id="subArrendaSinRedondeo" name="subArrendaSinRedondeo" />
		<input type="hidden" id="subFleteSinRedondeo" name="subFleteSinRedondeo" />
		<input type="hidden" id="RetSICOP" name="RetSICOP" />
		
		<div id="container" style="width: 100%" class="container" > 
			<div class="card-header"> <h3> Operaciones Ajenas </h3> </div>
			<hr class="mt-3"/>
			
			<div class="row">
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex p-1">
					<div id="multitabs" style="width: 100%">
						<div class="row d-flex justify-content-center">	
							<ul class="nav nav-tabs" id="list-opciones">
					            <li class="nav-item" role="presentation">
					            	<button class="nav-link active" id="tba1" data-bs-toggle="tab" data-bs-target="#tabs-1-clc" type="button" role="tab" aria-controls="tabs-clc" aria-selected="true">Creacion de CLC</button>
					            </li>
					            <li class="nav-item" role="presentation">
					            	<button class="nav-link" id="tba2" data-bs-toggle="tab" data-bs-target="#tabs-2-detalle" type="button" role="tab" aria-controls="tabs-detalle" aria-selected="false">Tabla Detalle</button>
					            </li>					            				           				          
					    	</ul>
							
							<div class="tab-content mt-3" id="tabContent">		
								<div class="tab-pane fade show active" id="tabs-1-clc" role="tabpanel" aria-labelledby="tabs-clc">
									<div class="row">
										<div class="col-12 col-lg-1 col-md-2 col-sm-12 d-flex p-1">								
											<label for="cClc"> Nombre: </label>
										</div>
										<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">																			
											<input type="text" name="cClc" id="cClc" class="form-control form-control-sm"/> 												 							 						
										</div>
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="caNoContrarrecibo"> ContraRecibo: </label>
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											<input type="text" name="caNoContrarrecibo" id="caNoContrarrecibo" class="form-control form-control-sm" readonly/> 												 							 						
										</div>
									</div>	
									
									<div class="row">
										<div class="col-12 col-lg-1 col-md-2 col-sm-12 d-flex p-1">								
											<label for="cBeneficiario"> Beneficiario: </label>
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											<select name="cBeneficiario" id="cBeneficiario" onchange="grupoRetencion()" class="form-select form-select-sm" >													
											</select> 												 							 						
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
											<label for="id_caso"> Folio: </label>
										</div>
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">																			
											<input type="text" name="id_caso" id="id_caso" class="form-control form-control-sm" readonly/> 												 							 						
										</div>
									</div>	
									
									<div class="row">										
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
										</div>										
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											Ingresos Propios: &nbsp;<input type="checkbox" id="chk_IP" name="chk_IP" class="form-check-input" value="" />  												 							 						
										</div>
									</div>	
									
									<div class="row">										
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
										</div>										
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											Retenc. en SICOP: &nbsp;<input type="checkbox" id="chk_LSICOP" name="chk_LSICOP" class="form-check-input" value="" onclick="ActivarRetSICOP()" />  												 							 						
										</div>
									</div>	
									
									<div id="divOACedular">
										<div class="row">										
											<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
											</div>										
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
												Cedular Pago Banco: &nbsp;<input type="checkbox" id="chk_PagoBancoIF" name="chk_PagoBancoIF" class="form-check-input" value="" />  												 							 						
											</div>
										</div>	
									</div>
									
									<div class="row">
										<div class="col-12 col-lg-1 col-md-2 col-sm-12 d-flex p-1">								
											<label for="cIDRFC"> RFC: </label>
										</div>
										<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">																			
											<input type="text" name="cIDRFC" id="cIDRFC" class="form-control form-control-sm AyudaSyC" />													
											</select> 												 							 						
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
											<input type="hidden" name="PagosAMF" id="PagosAMF" /></td>
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">																			
											Muestra Solicitudes Ejercidas: &nbsp;<input type="checkbox" id="chk_Ejercido" name="chk_Ejercido" class="form-check-input" value="" /> 												 							 						
										</div>
										<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">																			
											Muestra Solicitudes por Fecha Factura: &nbsp;<input type="checkbox" id="chk_fFactura" name="chk_fFactura" class="form-check-input" value="" /> 												 							 						
										</div>
									</div>	
									
									<div class="row">										
										<div class="col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">																			
											<input type="text" name="cnombre" id="cnombre" class="form-control form-control-sm" readonly/> 	
											<input type="hidden" name="fCaptura" id="fCaptura" value="<%=today%>" readonly="readonly" /> 
											<input type="hidden" name="fAplicacion" id="fAplicacion" value="<%=today%>" />											 							 						
										</div>
									</div>
									
									<div class="row">
										<div class="col-12 col-lg-1 col-md-2 col-sm-12 d-flex p-1">								
											<label for="cClc"> Concepto: </label>
										</div>
										<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">																			
											<textarea name="cConcepto" id="cConcepto" cols=90 rows=1 onKeyPress="return LetrasNums(event)" class="form-control form-control-sm"></textarea> 												 							 						
										</div>
										<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
										</div>
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">								
											<div class="row">	
												<div class="row" id="millar">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="sub5alM"> Subtotal 5 al millar: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="sub5alM" id="sub5alM" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="subPenal1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="subPenal"> Subtotal Penalizaciones: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="subPenal" id="subPenal" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="subCedular1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="subCedular"> Subtotal Cedular: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="subCedular" id="subCedular" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="subtmImportFlete1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="subCedular"> Subtotal Autotranporte: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="subtmImportFlete" id="subtmImportFlete" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="subHonor1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="subHonor"> Subtotal Honorarios: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="subHonor" id="subHonor" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="subARR1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="subARR"> Subtotal Arrendamiento: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="subARR" id="subARR" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="subFlete1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="subFlete"> Subtotal Fletes al 4%: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="subFlete" id="subFlete" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="IMDT1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="IMDT"> Subtotal IMDT: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="IMDT" id="IMDT" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="CNIC1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="CNIC"> Subtotal CNIC: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="CNIC" id="CNIC" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="ISRLAUDOS1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="ISRLAUDOS"> Subtotal ISR LAUDOS: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="ISRLAUDOS" id="ISRLAUDOS" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="ISROTROS1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="ISROTROS"> Subtotal ISR Otros: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="ISROTROS" id="ISROTROS" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="IVA61">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="ImporteIVA6"> Subtotal IVA 6%: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="ImporteIVA6" id="ImporteIVA6" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row" id="ISRRESICO1">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">
														<label for="ImporteISRRESICO"> Subtotal ISR RESICO: </label>
													</div>
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<input type="text" name="ImporteISRRESICO" id="ImporteISRRESICO" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
												<div class="row">
													<br/>
												</div>
												<div class="row">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
														<label for="MOperAjena"> Monto Oper. Ajenas: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
														<input type="text" name="MOperAjena" id="MOperAjena" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>													
												<div class="row" id="ajuste">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
														<label for="AjusteREdon"> Ajuste por Redondeo: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
														<input type="text" name="AjusteREdon" id="AjusteREdon" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>												
												<div class="row" id="total">
													<div class="columna-interior col-12 col-lg-5 col-md-5 col-sm-12 d-flex p-1">								
														<label for="cTMonto"> Total de Importes: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">								
														<input type="text" name="cTMonto" id="cTMonto" class="form-control form-control-sm" value="0" readonly/>
													</div>
												</div>
											</div>
										</div>										
									</div>	
									
									<div class="row">
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">																			
										</div>
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">																			
											 <div class="row">	
												<div class="row" id="fechas">
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
														<label for="fBusquedaDe"> Periodo de: </label>
													</div>
													<div class="columna-interior col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
														<div class="input-group">								
															<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>			
															<input type="text" name="fBusquedaDe" id="fBusquedaDe" class="form-control form-control-sm" readonly/>
														</div>
													</div>
													<div class="columna-interior col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">
														<label for="fBusquedaHasta"> Hasta: </label>
													</div>		
													<div class="columna-interior col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">
														<div class="input-group">								
															<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>			
															<input type="text" name="fBusquedaHasta" id="fBusquedaHasta" class="form-control form-control-sm" readonly/>
														</div>
													</div>											
												</div>
											</div>												 							 						
										</div>
									</div>
									
									<div class="row">
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">																			
										</div>
										<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">																			
											 <div class="row">	
												<div class="row">												
													<div class="columna-interior col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
													</div>
													<div class="columna-interior col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
														<input type="button" name="Buscar" id="Buscar" value="Buscar" onclick="Busqueda()" class="btn btn-secondary btn-sm"/>&nbsp;&nbsp;
														<input type="button" name="vDetalle" id="vDetalle" value="Detalle" onclick="ver()" class="btn btn-secondary btn-sm" />&nbsp;&nbsp;
														<input type="button" name="Desmarcar" id="Desmarcar" value="desmarcar" onclick="desmar()" class="btn btn-secondary btn-sm"/>
													</div>
												</div>
											</div>												 							 						
										</div>
									</div>	
		
									<div class="row d-flex">								
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
											<table id="Busqueda1" class="table table-striped table-bordered">											
												<thead>
													<tr>
														<th>----</th>
														<th>ContraRecibo</th>
														<th>Folio Documento</th>
														<th>Fecha Aplicaci&oacute;n</th>
														<th>Tipo de Documento</th>
														<th>E.P.</th>
														<th>Total</th>
														<th>RFC</th>
														<th>Mes</th>
													</tr>
												</thead>
												<tbody>
												</tbody>
									
											</table>										
										</div>
									</div>
								</div>
								
								<div class="tab-pane fade" id="tabs-2-detalle" role="tabpanel" aria-labelledby="tabs-detalle">
									<div class="row d-flex">								
										<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
											<table id="Detalle" class="table table-striped table-bordered">												
												<thead>
													<tr>
														<th>Valor</th>
														<th>ContraRecibo</th>
														<th>Folio Documento</th>
														<th>Fecha Aplicaci&oacute;n</th>
														<th>Tipo de Documento</th>
														<th>E.P.</th>
														<th>Total</th>
														<th>Enteros</th>
														<th>Sobrante</th>
														<th>I.V.A. (ARRENDAMIENTOS)</th>
														<th>I.V.A. (HONORARIOS)</th>
														<th>I.S.R. (HONORARIOS)</th>
														<th>I.S.R. (ARRENDAMIENTOS)</th>
														<th>FLETES (4.0%)</th>
														<th>APORTE I.M.D.T. ( 0.2%)</th>
														<th>APORTE C.N.I.C. ( 0.2%)</th>
														<th>INSPECCION DE OBRA (0.5%)</th>
														<th>PENALIZACIONES</th>
														<th>IMPUESTO CEDULAR</th>
														<th>ISR LAUDOS</th>
														<th>Mes</th>
														<th>ISR OTROS</th>
														<th>IVA 6</th>
														<th>ISR RESICO</th>
													</tr>
												</thead>
												<tbody>
												</tbody>
									
											</table>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<div id="operacionesConsultaDiv" style="display: none">
				<fieldset>
					<legend>Operaciones</legend>
					<table align="center">
						<tr>
							<td align="center">
								<input type="button" id="imprimirBtn" value="Imprimir P&oacute;liza" class="btnInterfaceBG"/>
								<input type="button" id="imprimirAnexoBtn" value="Imprimir Anexo" class="btnInterfaceBG"/>
								<input type="button" id="actualizarFirmantesBtn" value="Actualizar Firmantes" class="btnInterfaceBG"/>
							</td>
						</tr>
					</table>
				</fieldset>
			</div>
		</div>
		
		<div id="dialog-form" title="Aplicación Presupuestal/Contable">
			<div id="divEspera" align="center">
				Espere por favor.... 
				<img border="0" src="../imagenes/espera.gif" height="30" />
			</div>
			<div id="divAplica">
				<iframe id="ifAplica" src="about:blank"></iframe>
			</div>
		</div>
		
		<div id="firmantesDiv" style="width: 100%">
			<div class= "card">
				<div class="card-body">	
					<h5> Seleccione los Firmantes </h5>
					<hr class="mt-3">
					
					<jsp:include page="../plantillasCasos/ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
				</div>
			</div>
		</div>
		
		<div id="esperaDialog" title="Procesando Solicitud">
			<div align="center">
				<label id="esperardet">
					Espere por favor.... 
				</label>
				<img border="0" src="../imagenes/espera.gif" height="30" />
			</div>
		</div>
	</form>
</body>
</html>
