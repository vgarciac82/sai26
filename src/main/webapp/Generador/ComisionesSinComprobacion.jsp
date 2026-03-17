<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.core.CatalogoURFIELBusinessLogic"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

int estado = 0;

	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());	
	
	String cCentroContable = "";
	String aEjercicioFiscal = "";	
	boolean bAplicadoCont = false;
	String cUR = "";
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	if( Integer.parseInt( aEjercicioFiscal ) != c1.get(Calendar.YEAR) )
		today = "31/12/" + aEjercicioFiscal;
		
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
	cUR = usuario.getU_UR();
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	int idTipoCaso = c.getIdTC();

	if (c.getCasoDato("APLICADO_CONT").getValor() != null) {
		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor()))
			bAplicadoCont = true;
	}
	
	int idCaso = c.getIdCaso();
	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String msVariable = c.getCasoDato("MENSAJE").getValor(); //Recuperamos el mensaje del caso
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] archivoPDFVuelo = null; //id_oper 1 (EXCEL)
	FortimaxFile[] archivoOtros = null; //id_oper 5 (LINEA DE CAPTURA)
	FortimaxFile[] docPoliza = null;
	
	int nIdDocumento = 0;	
	
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, "PDF Boleto Vuelo");
	archivoPDFVuelo = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, nIdDocumento);
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, "Otros");
	archivoOtros = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, nIdDocumento);
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, "Solicitud Firmada");
	docPoliza = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, nIdDocumento);

	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE") != null ? usuario.getPropiedad("CCENTROCONTABLE").getValor():"";
	
		
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	String cNombreElabora = e.getNombre();
	String cApellidoPaternoElabora  = e.getApellidoPaterno();
	String cApellidoMaternoElabora  = e.getApellidoMaterno();
	String cPuestoElabora = e.getCargo();
	
	String nombreElabora = usuario.getNombre();
	boolean esConsulta = c.getCasoOperacion(0).getOperacion().getNumero() == 3;
	
	CatalogoURFIELBusinessLogic curbl = new CatalogoURFIELBusinessLogic( GestionInterface.ATT_CONEXION );
	boolean permitePagoSinFIEL = curbl.permitePagoSinFiel(cUR);
	String numeroEmpleadoElabora = usuario.getNumeroEmpleado();
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Comisiones de Viáticos Sin Comprobación</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css" href="../plantillasCasos/ComponentesPago/CSS/EgresoFirmantes.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/css/resumenPago.css"></link>
<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script> 
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../plantillasCasos/ComponentesPago/js/EgresoFirmantes.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="js/validaciones.js"></script>
<script type="text/javascript" src="js/ActualizaFIEL.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript">
	var usuarioElabora = "<%=nombreElabora%>";
	var centroContable = "<%=cCentroContable%>";
	var cNombreElabora = "<%=cNombreElabora%>";
	var cApellidoPaternoElabora  = "<%=cApellidoPaternoElabora%>";
	var cApellidoMaternoElabora  = "<%=cApellidoMaternoElabora%>";
	var cPuestoElabora = "<%=cPuestoElabora%>";

	/* Variable que indica si el pago es con firma (FIEL) */
	var permitePagoSinFIEL = <%=permitePagoSinFIEL%>;
	var esConsulta = <%=esConsulta%>;
	
	var es_mx = {
		sProcessing : "Procesando...",
		sLengthMenu : "Mostrar _MENU_ registros",
		sZeroRecords : "No hay registros a mostrar",
		sEmptyTable : "No hay datos en la tabla",
		sLoadingRecords : "Cargando...",
		sInfo : "Registros _START_ al _END_ de _TOTAL_",
		sInfoEmpty : "Registro 0 al 0 de 0",
		sInfoFiltered : "(filtered from _MAX_ total entries)",
		sInfoPostFix : "",
		sInfoThousands : ",",
		sSearch : "Filtro:",
		oPaginate : {
			sFirst : "Primero",
			sPrevious : "Ant.",
			sNext : "Sigte.",
			sLast : "&Uacute;ltimo"
		    }		
	   }
	
	$(document).ready(function() {	
		
		
		$("#divImprimePoliza").hide();
		$("#btnFirmas").hide();
		
		$("#oficioDelegatorioCaptura").hide();
		$("#oficioDelegatorioCapturaUpdate").hide();
		
		$( "#dFechaOficio" ).datepicker({
			showOn: "button",
			autoclose: true,
		});
			
		$( "#dFechaOficioUpdate" ).datepicker({
			showOn: "button",
			autoclose: true,
		});
		
		$("#oficioDelegatorioVoBo").hide();
		$("#oficioDelegatorioVoBoUpdate").hide();
		$( "#dFechaOficioVoBo" ).datepicker({
			showOn: "button",
			autoclose: true,
		});
			
		$( "#dFechaOficioVoBoUpdate" ).datepicker({
			showOn: "button",
			autoclose: true,
		});
		
		setFechas();			
		creaDtBoletaje();
		creaDlgComisiones();		
		creaDlgSeleccionVuelos();
		$("#btnComision").button();
		$("#btnRFC").button();
		$("#btnAgregar").button();
		
		$("#chkVueloVigente").change(function () {
			if ($("#chkVueloVigente").prop("checked")){
				$("#esBoletoVigente").val("S");
			}else{
				$("#esBoletoVigente").val("N");
			}			
		});
		
	});
	 
	
	function cmdImprimir() {
		window.open(
			"../admin/SeguridadCatalogos?"
			+ "catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=PolizaInformeComision.jasper"
			+ "&whereFolio= " + <%=folio %> + "", 			
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	
	function onPostSubmit(id_oper) {
		var bRegresa = false;
		
		parent.execResponsable();
		parent.execOperacion();
		
		if(id_oper == 1){
			if( validaAdjuntos() ){
				return confirm( "Esta seguro de enviar el tramite a su autorizacion?");
			}
		}else if( id_oper == 2 || id_oper == 4){
				bRegresa = estatusAutorizaBoletos();
				parent.document.getElementById("pb_send").disabled=true;
				parent.document.getElementById("pb_cancel").disabled=true;
				parent.document.getElementById("pb_leave").disabled=true;
				$.blockUI({
					message : "Enviando. Espere ..."
				});
			
		}else{
			parent.document.getElementById("pb_send").disabled=true;
		}
				
		return bRegresa;
	}
	
	function onPostDisplay(id_oper) {
		if( id_oper == 4 ){
			Swal.fire("Se guardo exitosamente el tramite.", "De click en el boton enviar para terminar", "success");
		} 
		
	}
	
	function actualizaIDGabinete(){
		$("#id_gabinete").val("-1");
		queryFormPost("actualizaGabinete", {async:false} );
	}
	
	function documentacionCapturada(){
		if( $("#id_gabinete").val() == "" || $("#id_gabinete").val() == "-1")
			actualizaIDGabinete();
		
		var consultado = false;
		var doctoCapturado = false;
		$("#documentoCapturado").val("0");
		
		try{
			queryFormPost({
				queryName:"doctoCapturadoRead", 
				async:false,
				callback:function() {
					consultado = true;
					if( $("#documentoCapturado").val() == "" )
						$("#documentoCapturado").val("0")
						
					doctoCapturado = parseInt($("#documentoCapturado").val(),10) > 0;
				}
			});
			
			if(!consultado)
				throw "No se logro consultar el documento adjuntado";
				return doctoCapturado;
		}catch(e){
			throw w;
		}
	}

	function habilitaFirmantes(){
		$("#firmantesDiv").show();
		if(esConsulta)
			muestraFirmantes();
		else
			muestraEditaFirmantes();
			
		$(".firmaElectronica").each(function() {
			$(this).show();
		});
	}
	
	function onLoadPlantilla(id_oper) {
				
		if(id_oper == 1){
			
			queryFormPost("existeComisionSinComprobacionRead", {async:false});	
			cargaFirmantesTramite();
			habilitaFirmantes();
			if( $("#nombreElabora").val() == ""  ){
				$("#nombreElabora").val(usuarioElabora);
				$("#puestoElabora").val(cPuestoElabora);
			}
				
			if($("#existeFolio").val() == "1"){
				cargarDatosComision();				
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=false;
				
				bloqueaCaptura();
			}
			
			if ($("#cEsFIEL").val()=='N')
				$("#divImprimePoliza").show();
			
		}else if(id_oper == 2){
			
			cargarDatosComision();	
			cargaFirmantesTramite();
			habilitaFirmantes();
			document.getElementById("btnAgregar").disabled = true;
			document.getElementById("dlgInfoBoletos").disabled = true;
			document.getElementById("dlgDatosComision").disabled = true;
			document.getElementById("chkVueloVigente").disabled=true;
			
			if ($("#cEsFIEL").val()=='N')
				$("#divImprimePoliza").show();
		
		}else if(id_oper == 3){
			cargarDatosComision();
			
			if ($("#cEsFIEL").val()=='N')
				$("#divImprimePoliza").show();
			
			if(parent.document.getElementById("pb_save") || parent.document.getElementById("pb_send")){
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_send").disabled=true;
			}
			document.getElementById("btnAgregar").disabled = true;
			document.getElementById("dlgInfoBoletos").disabled = true;
			document.getElementById("dlgDatosComision").disabled = true;
			document.getElementById("chkVueloVigente").disabled=true;
			document.getElementById("btnAgregar").style.display = "none";
		}		
			
	}

	function bloqueaCaptura(){
		document.getElementById("chkVueloVigente").disabled=true;
		document.getElementById("btnAgregar").disabled = true;
		document.getElementById("dlgInfoBoletos").disabled = true;
		document.getElementById("dlgDatosComision").disabled = true;
	}
	
	/**
	 * 1
	 * Funcion llamada al momento de guardar.
	 * Realiza validaciones,si todo es correcto, regresar true para que continue con el flujo
	 */
	function onSubmit(id_oper) {		
		var p = window.parent;
		var bRegresa = true;
		
		//Guardado de los campos correspondientes a cada variable de caso
		p.gestion.setFolio( $("#FOLIO").val() );
		p.gestion.setOperador( $("#OPERADOR").val() );
		p.gestion.setFechaDocumento( $("#FECHA_CARGA").val() );
		p.gestion.setEjercicioFiscal( "<%=aEjercicioFiscal%>" );
		p.gestion.setConceptoMov("Comisión sin Comprobación");
		p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
		p.gestion.setFechaApCont( $("#FECHA_CARGA").val() );
		p.gestion.setAplicadoCont("false");
				
		try{		
			
			if (id_oper==1){
							
				if(generaComisionSinComprobacion()){
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
					bloqueaCaptura();					
				}else{
					parent.document.getElementById("pb_save").disabled=false;
					parent.document.getElementById("pb_send").disabled=true;
					bRegresa = false;
				}		
																		
			}
			if(id_oper == 2){
				
				bRegresa = bRegresa && validaAdjuntos();
			}		
			
		}
		catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			bRegresa = false;
		}
		
		if(!bRegresa){
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_send").disabled=true;
		}else{
			parent.document.getElementById("pb_save").disabled=true;
			parent.document.getElementById("pb_send").disabled=false;
		}
		return bRegresa;
	}

	/**
	 * 2
	 * Retorna el nombre del responsable siguiente.
	 */
	function ResponsableSiguiente(id_oper) {
		
		switch(parseInt(id_oper)){
		case 1:
			var esFIEL = esFirmaElectronica();
			if( esFIEL ){
				return "FIRMA_VOBO_FIEL";
			}else{
				if( "10" == centroContable)
					return "AUTORIZA_COMSINVIATICOS_10";
				else
					return "AUTORIZA_COMSINVIATICOS";
			}
			break;
		
		case 2 : case 4:
			return "CONSULTA_COMSINVIATICOS";
			break;
			
		case 3 :
			return "CONSULTA_COMSINVIATICOS";
			break;
		}
	}

	/**
	 * 3
	 * Retorna el nombre de la operacion siguiente.
	 */
	function OperacionSiguiente(id_oper) {
		
		switch(parseInt(id_oper)){
		case 1:
			var esFIEL = esFirmaElectronica();
			if( esFIEL ){
				return "vobo_comsinviaticos_fiel";
			}else{
				if("10" == centroContable )
					return "autoriza_comsinviaticos_cent";
				else
					return "autoriza_comsinviaticos";
			}
			break;
		
		case 2 : case 4:
			return "consulta_comsinviaticos";
			break;
			
		case 3 :
			return "consulta_comsinviaticos";
			break;
		}
	}
	
	function cargarDatosComision(){
		queryFormPost("vwComisionesSinComprobacionRead", {async:false});
		cargaDtBoletaje();
	}
	
	function setFechas(){
		$( "#fInicio" ).datepicker({
			dateFormat: "dd/mm/yy"
		});
				
		$( "#fFin" ).datepicker({
			dateFormat: "dd/mm/yy"
		});
	}
	
	function generaComisionSinComprobacion(){		
		
		var bEjecuto = false;
		
		if ($("#tieneBoletos").val()=="S") {
			var boletos = $("#dt_Boletaje").dataTable().fnGetData();
			 if(boletos.length == 0){
				Swal.fire("Capturar","Favor de capturar la Información de Boletos.","info");
			}	
		}		
		
		var bReturn = false;
		
		validaFirmantes();
		if($("#nIdComision").val() == ""){
			Swal.fire("Capturar","Favor de Capturar los datos de la Comisión.","info");
		}else if($("#cInformeComision").val() == ""){
			Swal.fire("Capturar","Favor de capturar el informe de Comisión.","info");
		}else if($("#cRFC").val() == ""){
			Swal.fire("Capturar","Favor de capturar el RFC.","info");
		}else if($("#nAcompanantes").val() == ""){
			Swal.fire("Capturar","Favor de capturar el numero de Acompañantes.","info");
		}else{			
			queryFormPost("existeComisionSinComprobacionRead", {async:false});
			if($("#existeFolio").val() == "0"){	
				if(  ( $("#chkVueloVigente").attr("checked") && confirm("El boleto se marcara como vigente ¿Desea continuar?") )  ||  (!$("#chkVueloVigente").attr("checked")) )			
					queryFormPost( { queryName:"tComisionesSinComprobacionEncCreate", async:false, callback:function(){ bEjecuto = true;} });
			
				if(bEjecuto){				
					if ($("#tieneBoletos").val()=="S"){
						if(guardaDetalle()){
							updateImporteNeto();
							bReturn = true;					
						}else{
							eliminarFolioComision();					
						}
					} else {
						updateImporteNeto();
					}
				}
			} else
				bReturn = true;	
				
			/*Si guardo exitosamente el encabezado intenta guardar los firmantes.*/	
			if(bReturn){
				try{
					if( !esFirmaElectronica() ){
						if( validaFirmantes() ){
						
							var guardado = guardaFirmantes();
							if( guardado && !esFirmaElectronica() )
								cmdImprimir();
							
							$(".firmaElectronica").each(function() {
								$(this).hide();
							});
						
							habilitaFirmantes();
							bReturn = guardado;
						}else{
							bReturn = false;
						}
					}else{
						habilitaFirmantes();
					}
				}catch(e){
					var msgErr = ""
					if( e instanceof TypeError )
						msgErr = e.message;
					else
						msgErr = e;
				
					alert("No puede continuar debido al error: " + e );
					bReturn = false;
				}
			
			}				
		}
		
		return bReturn;
	}
	
	function validaAdjuntos() {
	
		var bRegresa= true;
		if (!$("#chkVueloVigente").prop("checked")){
			if ($("#tieneBoletos").val()=="S") {
				//Valida que este adjunto el Boleto
				if(<%=archivoPDFVuelo.length == 0%>){
					Swal.fire("Capturar","Favor de adjuntar el boleto del vuelo","info");
					bRegresa = false;				
				}
			}
		}
		
		var esFIEL = esFirmaElectronica();
		if(<%=docPoliza.length == 0%> && !esFIEL ){
			//Valida que este adjunto la solicitud Firmada
			Swal.fire("Capturar","Favor de adjuntar la Solicitud Firmada.", "info");
			bRegresa = false;
		}
		return bRegresa;			
	}
	
	function guardaDetalle(){
	
		var bRegresa = false;
		
		var oTable = $('#dt_Boletaje').dataTable();
		var aData = oTable.fnGetData();
		var nRows = aData.length;
		
		var cNumBoleto = "";
		var mImporteBoleto = "";		
		var RFC = "";
		var Nombre = "";
				
		var status = "S";
		
		if ($("#chkVueloVigente").prop("checked")){
			status = "V";
		}
		
		
		for( i = 0; i < nRows; i++ ){
			var row =  oTable.fnGetData(i);
			$("#boleto").val("");
			$("#impteBoleto").val("");
			$("#partida").val("");
			$("#cRuta").val("");
			$("#RFC").val("");
			$("#cNombre").val("");	
			
			cNumBoleto = "";
			mImporteBoleto = "";		
			RFC = "";
			Nombre = "";
					
			
			$("#boleto").val(row[1]);
			$("#impteBoleto").val(row[2]);
			$("#partida").val(row[3]);
			$("#cRuta").val(row[7]);
			
			cNumBoleto = row[1];
			mImporteBoleto = row[2];		
			RFC = row[5];
			Nombre = row[6];
			
			$("#RFC").val(RFC);
			$("#cNombre").val(Nombre);
			
			queryFormPost( { queryName:"tComisionesSinComprobacionDetCreate", async:false, callback:function(){ bRegresa = true;} });			
 			seleccionVueloUpdate(status, RFC, Nombre, cNumBoleto, mImporteBoleto);
			
			if(!bRegresa){
				break;
			}
			
		}
		
		return bRegresa;
	
	}
	
	function updateImporteNeto(){
		queryFormPost("tComisionesSinComprobacionImpteNetoUpdate", {async:false});
	}
	
	function updateAplicaDocto(){
		queryFormPost("tComisionesSinComprobAplicaDoctoUpdate", {async:false});
	} 
	function eliminarFolioComision(){
		queryFormPost("tComisionesSinComprobacionDelete", {async:false});
	}				
	
	function limpiarPantalla(){
		location.reload();
		document.getElementById("btnGuardar").disabled = false;
	}	
  	
  	function muestraComisiones(){		
		creaDatosComisiones();
		$("#dlgComisiones").dialog("open");			
	}
  	
  	function creaDlgComisiones(){
	 	$("#dlgComisiones").dialog(
			{
				autoOpen : false,
				height : 720,
				width : 680,
				modal : true,
				buttons : {
					"Aceptar" : function() {
						if($("#comision").val() == ""){
							Swal.fire("No ha seleccionado una Comisión.","Por favor seleccione dando click en la tabla","info");
						}else{
							$(this).dialog("close");
						}
					},
					"Cancelar" : function() {
						$(this).dialog("close");
					}
				},
				open: function(){
					$("#comision").val("");
				}
			});
	 }
	 
	 var dtBoletaje;
	function creaDtBoletaje(){
	 	dtBoletaje = 
	 	     $('#dt_Boletaje').dataTable({
			    "bPaginate": false,
				"iDisplayLength": 20,        			
				"bLengthChange": false,
      			"bFilter": false,
      			"bSort": false,
      			"bInfo": false,
      			"bAutoWidth": false,
				"sScrollY": 100,
				"bJQueryUI": true,
				"bRetrive" : true,
				"bDestroy" : true,
				"sPaginationType": "full_numbers"
			});				
	 }
	 
	 function cargaDtBoletaje(){
	 	dtBoletaje = 
	 	     $('#dt_Boletaje').dataTable({
			    "bPaginate": true,  
			    "bLengthChange": true,  
			    "bFilter": true,  
			    "bSort": true,
	        	"bInfo": true,  
	        	"bAutoWidth": false,  
	        	"sScrollY": 270,  
	        	"bJQueryUI": true,
				"bRetrive" : true,  
				"bDestroy" : true,  
				"sPaginationType": "full_numbers",
				"sScrollX": "600",  
				"bScrollCollapse": true,  
				"bServerSide": true,   
				sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vw_ComisionesSinComprobacion&qw=nFolioComision="+<%=folio%>,
				aoColumns: [
					{ sName: "nFolioComision", "bVisible": false},
					{ sName: "cBoleto", "bVisible": true},
					{ sName: "mImporteBoleto", "bVisible": true},
					{ sName: "cPartida", "bVisible": true},
					{ sName: "cPartida as eliminar", "bVisible": false},
					{ sName: "RFCVuelo as RFC", "bVisible": false},
					{ sName: "cNombreRFC as Nombre", "bVisible": false},
					{ sName: "cPartida as Ruta", "bVisible": false}				
				],
				oLanguage: es_mx
		});				
	 }
	 
	 var tablaComisiones;
	 function creaDatosComisiones(){
	 	tablaComisiones = 
	 	     $('#tblComisiones').dataTable({
				        "bPaginate": true,  "bLengthChange": true,  "bFilter": true,  "bSort": true,
	        			"bInfo": true,  "bAutoWidth": false,  "sScrollY": 270,  "bJQueryUI": true,
						"bRetrive" : true,  "bDestroy" : true,  "sPaginationType": "full_numbers",
						"sScrollX": "600",  "bScrollCollapse": true,  "bServerSide": true,   
						sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=v_ViaticosComisiones" ,
				        aoColumns   : 
				        [
							{ sName: "nIdComision" },
							{ sName: "cConcepto_Comision"},
							{ sName: "fInicio" },
							{ sName: "fFin" },
							{ sName: "Ubicacion" },
							{ sName: "cEstatusDesc" }						
						],
						oLanguage: es_mx
			});
			
			$("#tblComisiones tbody").live("click", function(event){clickTablaViaticos(event);} );
			$("#tblComisiones tbody").dblclick(function(event){doubleClickViaticos(event);});		
	 }
	 
	 function clickTablaViaticos(event){
		$(tablaComisiones.fnSettings().aoData).each(
			function (){
				$(this.nTr).removeClass('row_selected');
			});
				
		$(event.target.parentNode).addClass('row_selected');
		var aPost = tablaComisiones.fnGetPosition(event.target.parentNode);		
		var aData = tablaComisiones.fnGetData(aPost);
		var idcomision = aData[0];
		var conceptoComision = aData[1];
		var fInicio = aData[2];
		var fFin = aData[3];
		
		$("#comision").val(idcomision);
		$("#nIdComision").val(idcomision);
		$("#cConcepto_Comision").val(conceptoComision);
		$("#fInicio").val(fInicio);
		$("#fFin").val(fFin);
	}
	
	function doubleClickViaticos(event){
	
		var aPos = tablaComisiones.fnGetPosition(event.target.parentNode);
		var aData = tablaComisiones.fnGetData(aPos);
		
		var idComision = aData[0];
		var conceptoComision = aData[1];
		var fInicio = aData[2];
		var fFin = aData[3];
		
		$("#nIdComision").val(idComision);	
		$("#cConcepto_Comision").val(conceptoComision);
		$("#fInicio").val(fInicio);
		$("#fFin").val(fFin);
				
		$("#dlgComisiones").dialog("close");
		
	}
	
	function cat_beneficiario(){
		var formName  = "FormContrato";
		var inputName = "cTipoRfc";
		var inputRFCTarget = "cRFC";
		var inputDRFCTarget = "cNOMRFC";
		$("#cTipoRfc").val("3"); //EMPLEADO CONAFOR		
		
		window.open('../Generador/CatalogoBeneficiariosRG.jsp?formName=' + formName + '&inputName=' + inputName + '&inputRFCTarget=' + inputRFCTarget + '&inputDRFCTarget=' + inputDRFCTarget, 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');
	}	
	
	function onChangeVuelo (param){
	
	}
	
	function btnAgregarBoleto(){	
		seleccionarBoletos();
	}
	
	function agregarBoleto(boleto, importe, partida, RFC, Nombre, Ruta) {	
		
		var oTable = $('#dt_Boletaje').dataTable();
		var aData = oTable.fnGetData();
		var nRows = aData.length + 1;
		var cmdBorrar = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Eliminar Renglon\" onClick=\"eliminarRow('"
				+ boleto
				+ "',"
				+ nRows + ");\" />";
		
		$('#dt_Boletaje').dataTable().fnAddData( [ nRows, boleto, importe, partida, cmdBorrar, RFC, Nombre, Ruta ]);
		
	}
	
	function eliminarRow(boleto, row){
		if (confirm("Esta seguro de borrar el renglon?")) {
			var oTable = $('#dt_Boletaje').dataTable();
			var info = oTable.fnGetData();
			
			var RFC = "";
			var cNombre = "";
			var cReferencia = "";
			var mTotal = "";
	
			for (i = 0; i < info.length; i++) {
				var renglonInfo = info[i];				
				if (renglonInfo[0] == row && renglonInfo[1] == boleto ) {
					
				 	cReferencia = renglonInfo[1];
					mTotal = renglonInfo[2];
					RFC = renglonInfo[5];
					cNombre = renglonInfo[6];
					
					seleccionVueloUpdate("A", RFC, cNombre, cReferencia, mTotal);
										
					oTable.fnDeleteRow(i);						
					break;					
				}
	
			}
		}
	}
	
	function Sinfrmt(fld) {	
		var valcol = $(fld).val();
		valcol = valcol.replace("$", "");
		valcol = valcol.replace(",", "");
		$(fld).val(valcol);
	}
	
	function cambiafrmt(fld) {	
		$(fld).formatCurrency();
	}
	
	function quitaFmt(val) {
	   	val = val.replace("$", "");
	   	val = val.replace(/,/g, "");
	
		if (val.indexOf("(") >= 0) {
			val = val.replace("(", "");
			val = val.replace(")", "");
			val = "-" + val;
		}
		return val;
	}	
	
	function seleccionarBoletos(){
		$("#dlgSeleccionVuelos").dialog("open");
	}
	
	function creaDlgSeleccionVuelos() {

	$( "#dlgSeleccionVuelos" ).dialog( {
		title : "Seleccion de Boletos",
		autoOpen : false,
		height : 450,
		width : 800,
		modal : true,
			open : function() {		
				muestraBoletosPendientes();
			},
			buttons : {
			"Aceptar" : function() {		
				cargarSeleccionBoletos();
			},
			"Cancelar" : function() {
				
				$( "#dlgSeleccionVuelos" ).dialog( "close" );
			}
		}
	} );	

}

var tblSeleccionBoletos;
function muestraBoletosPendientes(){
	
	var sWhere = "Status = 'A' AND RFC IN ( '" + $("#cRFC").val() + "', 'INV44102000000', 'INV38301000000')  "; 
	
	tblSeleccionBoletos = 
	     $('#tblSeleccionBoletos').dataTable({
			    "bPaginate": false, "bLengthChange": true, "bFilter": true,"bSort": true,
       			"bInfo": true, "bAutoWidth": false, "sScrollY": 270, "bJQueryUI": true,
				"bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers",
				"sScrollX": "800", "bScrollCollapse": true, "bServerSide": true, 				
		        sAjaxSource : window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vw_tLayoutVuelosDet&qw=" + sWhere,
		        aoColumns   : [
		            { sName: "nchkRenglon" },
					{ sName: "RFC" },
					{ sName: "cNombre" },
					{ sName: "cReferencia"},
					{ sName: "mTotal" },
					{ sName: "fFechaSalida" },
					{ sName: "fFechaRegreso" },
					{ sName: "cRuta" },
					{ sName: "cPartida" }						
				],
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
					sSearch: "Filtro:",
					oPaginate: {
						sFirst:    "Primero",
						sPrevious: "Ant.",
						sNext:     "Sigte.",
						sLast:     "&Uacute;ltimo"
					}
				}
		});
	
}

function cargarSeleccionBoletos(){
	
	var oTable = $('#tblSeleccionBoletos').dataTable();
	var aData = oTable.fnGetData();
	var nRows = aData.length;
	var data = $('#tblSeleccionBoletos').dataTable().fnGetNodes();
	var cNumBoleto = "";
	var mImporteBoleto = "";	
	var partida = "";
	var RFC = "";
	var Nombre = "";
	var Ruta = "";
	var status = "S";
	
	if ($("#chkVueloVigente").prop("checked")){
		status = "V";
	}
	
	for(var i=0; i < nRows; i++){
		cNumBoleto = "";
		mImporteBoleto = "";
		partida = "";
		RFC = "";
		Nombre = "";
		Ruta = "";		
		
		if ( $('input', data[i])[0].checked ){
			RFC = aData[i][1];
			Nombre = aData[i][2];		
			cNumBoleto = aData[i][3];
			mImporteBoleto = aData[i][4];
			partida = aData[i][8];
			Ruta = aData[i][7];
			
			agregarBoleto(cNumBoleto, mImporteBoleto, partida, RFC, Nombre, Ruta);
			seleccionVueloUpdate(status, RFC, Nombre, cNumBoleto, mImporteBoleto);
		}					
							
	}
	
	$( "#dlgSeleccionVuelos" ).dialog( "close" );
	
}

function seleccionVueloUpdate(statusSel, RFC, cNombre, cReferencia, mTotal){
	$("#statusSel").val(statusSel);
	$("#RFC").val(RFC);
	$("#cNombre").val(cNombre);
	$("#cReferencia").val(cReferencia);
	$("#mTotal").val(mTotal);
	
	queryFormPost("tLayoutVuelosDetSelUpdate", {async:false});
}

function estatusAutorizaBoletos(){
	
		var bRegresa = false;		
		var oTable = $('#dt_Boletaje').dataTable();
		var aData = oTable.fnGetData();
		var nRows = aData.length;
		var cNumBoleto = "";
		var mImporteBoleto = "";		
		var RFC = "";
		var Nombre = "";
		var status = "S";
		
		if ($("#chkVueloVigente").prop("checked")){
			status = "V";
		}
		
		for( i = 0; i < nRows; i++ ){
		try{
			var row =  oTable.fnGetData(i);
			$("#boleto").val("");
			$("#impteBoleto").val("");
			$("#partida").val("");
			$("#cRuta").val("");
			$("#RFC").val("");
			$("#cNombre").val("");	
			
			cNumBoleto = "";
			mImporteBoleto = "";		
			RFC = "";
			Nombre = "";
					
			$("#boleto").val(row[1]);
			$("#impteBoleto").val(row[2]);
			$("#partida").val(row[3]);
			$("#cRuta").val(row[7]);
			
			cNumBoleto = row[1];
			mImporteBoleto = row[2];		
			RFC = row[5];
			Nombre = row[6];
			
			$("#RFC").val(RFC);
			$("#cNombre").val(Nombre);
			
			bRegresa = true;
			}catch (e) {
				alert(e);
				bRegresa = false;
			}
		}		
		return bRegresa;
}

	function clickVueloVigente(){
		if ($("#chkVueloVigente").prop("checked")){
			$("#esBoletoVigente").val("S");
		}else{
			$("#esBoletoVigente").val("N");
		}
	}
	
	function checarBoletos(){
		if ($("#sinBoletos").prop("checked")){
			$("#tieneBoletos").val("N");
			$("#divBoletaje").hide();
		}else{
			$("#tieneBoletos").val("S");
			$("#divBoletaje").show();
		}
	}

</script>
</head>
<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0" >
	<form id="FormContrato" name="FormContrato">
		<div id="container" class="container" style="width: 95%">
			<input type="hidden" id="cIdUsuarioCaptura" name="cIdUsuarioCaptura" value="<%=usuario.getLogin()%>">
			<input type="hidden" id="cDocumentoNombre" name="cDocumentoNombre" value="PDF Boleto Vuelo">
			<input type="hidden" id="documentoCapturado" name="documentoCapturado" value="PDF Boleto Vuelo">
			<input type="hidden" id="cCarpetaNombre" name="cCarpetaNombre" value="Documentaci&oacute;n Vuelos">
			<input type="hidden" id="id_caso" name="id_caso" value="<%=c.getIdCaso(  )%>">
			<input type="hidden" id="id_gabinete" name="id_gabinete" value="<%=c.getIdGabinete()%>">
			<input type="hidden" id="cTipoPago" name="cTipoPago" value="<%=c.getTipoCaso().getGavetaAsociada(  )%>">
			<input type="hidden" id="esBoletoVigente" name="esBoletoVigente" value="N">
			<input type="hidden" id="comision" name="comision">
			<input type="hidden" id="cTipoRfc" name="cTipoRfc" size=15 value="">
			<input type="hidden" id="cEsFIEL" name="cEsFIEL" value="N" />
			<input type="hidden" id="cEsFirmaElectronica" name="cEsFirmaElectronica" value="N" />
			<input type="hidden" id="boleto" name="boleto">
			<input type="hidden" id="impteBoleto" name="impteBoleto">
			<input type="hidden" id="partida" name="partida">
			<input type="hidden" id="cRuta" name="cRuta">
			<input type="hidden" id="existeFolio" name="existeFolio" value="0" />
			<input type="hidden" id="tieneBoletos" name="tieneBoletos" value="S" />
			<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>" /> 
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>" /> 
			<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>" />
			<input type="hidden" id="aEjercicioFiscal" value=""	name="aEjercicioFiscal" />
			<input type="hidden" id="RFC" name="RFC" value="">
			<input type="hidden" id="cNombre" name="cNombre" value="">
			<input type="hidden" id="cReferencia" name="cReferencia" value="">
			<input type="hidden" id="mTotal" name="mTotal" value="">
			<input type="hidden" id="statusSel" name="statusSel" value="">
			<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
			<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=folio%>">
			<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%> ">
			<input type="hidden" name="momentoGuarda" id="momentoGuarda" value="0" />
			<input type="hidden" id="firmanteExiste" name="firmanteExiste" size="14">		
			<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
			<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
			<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
			<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
			<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
			<input type="hidden" id="tipoFirmante" name="tipoFirmante" size=15 value="PAGO_VOBO">
			<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
			<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
			<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
			<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
			<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
			<input type="hidden" id="tipoFirmanteA" name="tipoFirmanteA" size=15 value="PAGO_AUT">
			<input type="hidden" name="id_caso"  id="id_caso" maxlength="40" value ="<%=request.getParameter("folio")%>">
			<input type="hidden" name="tipoTramite" id="tipoTramite"  value="<%=idTipoCaso %>">
			
			<!-- hidden para la captura de oficio delegatorio -->
			<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
			<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">		
			<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
			<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value="">
			<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
			<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
			<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
			<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
			<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
									
			<!-- hidden para la captura de los datos de quien elaboro -->
			<input type="hidden" id="cNombreE" name="cNombreE" size=40>
			<input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
			<input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
			<input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
			<input type="hidden" id="firmanteEla" name="firmanteEla" size=40>
			<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" >
			<input type="hidden" id="nNumEmpleadoBusqueda" name="nNumEmpleadoBusqueda" >
			
			<!-- hidden para la captura de oficio delegatorio VoBo-->
			<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
			<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
			<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
			<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
			<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
			<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
			<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">
			<input type="hidden" name="tituloAplicacion" id="tituloAplicacion" value="<%=c.getTipoCaso().getGavetaAsociada()%>" />
		  	<input type="hidden" name="idGabinete" id="idGabinete" value="<%=c.getIdGabinete()%>" />	
			
		<div class="card">
			  <div class="card-header">
			    Comisiones sin Comprobación
			  </div>
  		<div class="card-body">
			<!-- Dialogo que mostrara los Datos de Comision. -->
			<div id="dlgDatosComision">
				<fieldset>
				<div class="input-group">
					<div class="col-md-2">
						<label>Seleccione Comisión:</label>		
					</div>
					<div class="col-md-2">
							<input type="button" name="btnComision" id="btnComision" value="..." size="5" onclick="muestraComisiones()" class="btn btn-secondary"/>
					</div>
					<div class="form-check mb-2 mr-sm-2">
							<label for="nFolioComision"># Folio</label>
					</div>	
					<div class="form-check mb-2 mr-sm-2">
							<input type="text" class="form-control" id="nFolioComision" name="nFolioComision" size="6" value="<%=folio%>" readonly/>
					</div>
					<div class="form-check mb-1">
					</div>
					<div class="form-check mb-2 mr-sm-2" align="right">
							<input type="checkbox" class="form-check-input" id="chkVueloVigente" name="chkVueloVigente" align="left" onclick="clickVueloVigente()" />
							 <label class="form-check-label" for="chkVueloVigente">Vuelo Vigente</label>		 
					</div>
					<div class="col-md-1">
					</div>
					<div id="divImprimePoliza" class="col-md-1">
									<img src="imagenes/Imprimir.png" width="32" height="28" onClick="cmdImprimir();"/>
					</div>
				</div>
				<div class="input-group">
					<div class="col-md-2">
						<label>Folio:</label>
						<input type="text" class="form-control" name="nIdComision" id="nIdComision" size="10" readonly />				
					</div>
					<div class="col-md-8">
						<label>Comision:</label>
						<input type="text" class="form-control" name="cConcepto_Comision" id="cConcepto_Comision" size="100" readonly />
					</div>
					
				</div>
				<div class="input-group ">
					<div class="col-md-2">
						<label>Fecha Inicio:</label>
						<input type="text" class="form-control" id="fInicio" name="fInicio" size="10" readonly />				
					</div>
					<div class="col-md-2">
						<label>Fecha Fin:</label>
						<input type="text" class="form-control" id="fFin" name="fFin" size="10" readonly />
					</div>
				</div>
			</fieldset>
				<fieldset>
					<div class="input-group ">
						<div class="col-md-2">
							<label for="btnRFC">Datos Comisionado:</label>
							<input type="button" name="btnRFC" id="btnRFC" value="..." size="5" onclick="cat_beneficiario()" class="btn"/>
						</div>
						
						<div class="col-md-2">
							<label>RFC:</label>
							<input type="text" class="form-control" id="cRFC" name="cRFC" size="15" readonly />				
						</div>
						<div class="col-md-6">
							<label>Nombre:</label>
							<input type="text" class="form-control" id="cNOMRFC" name="cNOMRFC" size="89" readonly />
						</div>
					</div>
					<div class="input-group ">
						<div class="col-md-10">
							<label>Informe:</label>
							<textarea class="form-control" cols=120 rows=4 id="cInformeComision" name="cInformeComision"></textarea>				
						</div>
					</div>
					
				</fieldset>
			</div>
			</div>
		</div>
		<div class="card">
			  <div class="card-header">
			    Boletaje Comisión
			  </div>
  		<div class="card-body">
			<!-- Dialogo que mostrara la informacion de los boletos a capturar -->
			<div id="dlgInfoBoletos">
				<fieldset>
					<table>
						<tr>
							<td align="left"># Acompañantes:</td>
							<td align="left">
								<input type="text" class="form-control" style="text-align:right;" id="nAcompanantes" name="nAcompanantes" size="15">
							</td>
							<td align="left"><input type="button" value="Seleccionar Boletos" id="btnAgregar" onclick="btnAgregarBoleto();" class="btnInterfaceBG"/></td>							
						</tr>					
						<tr>							
							<td> <label class="form-check-label" for="sinBoletos"> Comisión sin boletos de Avión</label> </td>
							<td align="left">
								<input type="checkbox"  class="form-check-input" id="sinBoletos" name="sinBoletos" onclick= "checarBoletos();">
							</td>
						</tr>					
					</table>
				</fieldset>
			</div>						
		</div>
		</div>
			<!-- Dialogo que mostrara la informacion de las comisiones creadas -->
			<div id="dlgComisiones">
				<fieldset>
					<legend> Información de Comisiones. </legend>
					<label style="font-size: 11px; font-weight: bold;"> Seleccione una comisión dando click y despues click en aceptar</label>
					<div id="dtComisiones">
						<table id="tblComisiones" class="display" cellspacing="0" cellpadding="2" align="center"> 
							<thead>
								<tr>
									<th> Num. Comisión </th>
									<th> Concepto </th>
									<th> Fecha Inicio </th>
									<th> Fecha Fin </th>
									<th> Ubicación </th>
									<th> Status </th>
								</tr>
							</thead>
						</table>
					</div>
				</fieldset>
			</div>
			<div id ="divBoletaje">				
				<table id="dt_Boletaje"  class="display" cellspacing="0" cellpadding="0" align="center">
					<thead>
						<tr>
							<th> Núm. Renglon </th>
							<th> Núm. Boleto </th>
							<th> Importe </th>
							<th> Partida </th>
							<th> Eliminar </th>
							<th> RFC </th>
							<th> Nombre </th>
							<th> Ruta </th>												
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
			</div>
			
			<div id="firmantesDiv">
				<div class= "card">
					<div class="card-body">	
						<h5> Seleccione los Firmantes </h5>
						<hr class="mt-3">
						
						<jsp:include page="../plantillasCasos/ComponentesPago/EgresoFirmantesBs.jsp"></jsp:include>
					</div>
				</div>
			</div>
		</div>
	
	
	<!-- Dialogo para seleccionar vuelos  -->
	<div id="dlgSeleccionVuelos">
		<fieldset>
			<legend>Selección de Vuelos</legend>
			<label style="font-size: 11px; font-weight: bold;"> Seleccione el(los) Boletos dando click y despues click en aceptar</label>
			<table id="tblSeleccionBoletos" class="display" cellspacing="0" cellpadding="2" align="center">
				<thead>
					<tr>
						<th> Sel. </th>
						<th> RFC </th>
						<th> Nombre </th>
						<th> Núm. Boleto </th>
						<th> Importe </th>
						<th> Fecha Salida </th>
						<th> Fecha Regreso </th>
						<th> Ruta </th>
						<th> Partida </th>
					</tr>
				</thead>
			</table>
		</fieldset>
	</div>
	
	</form>
	<form id="liberardocumento" name="liberardocumento"
			action="../gstnmngr/gestion?cmd=1" target="content-iframe"
			method="post">
	</form>
	
</body>

</html>
