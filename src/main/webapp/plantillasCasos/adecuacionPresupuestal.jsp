<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.contable.adecuaciones.Adecuacion"%>
<%@page import="com.syc.contable.core.ResultadoValidacionAdecuacion"%>
<%@page import="com.syc.contable.core.ResultadoSaldos"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.model.DocPolizaEncabezadoManager"%>
<%@page import="com.syc.sai.contabilidad.polizamanual.controller.DocPolizaEncabezadoBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.utils.db.CloseObject"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.AnteProyectoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.ArchivoExcel"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.io.File"%>
<%@page import="javax.swing.text.MaskFormatter"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="java.sql.Connection"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%!private static Logger log = LoggerFactory.getLogger("AdecuacioPresupuestal.jsp");%>
<%
	//ARLA 22/09/2014 Mensajes resultado de la validacion. 
	ResultadoValidacionAdecuacion resultadoCarga = null;
	ResultadoSaldos resultadoSaldos = null;
	Adecuacion adecuacion = null;
	//Adecuacion resultadoCarga = null;

	boolean bErrorAdec = false;
	boolean conAdvertencias = false;

	ArrayList arrConsolidado = new ArrayList();
	boolean inibeAPL = false;
	int nFolio;
	int nConsecutivoSicop = 0;
	int nFolioConsolidado = 0;
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String cAnioFiscal = ""; //c1.getTime().getYear();
	String cCentroContable = "";
	String cUR = "";
	String cAutomaticoXLS = "SI";
	String resultadoCancelacion = null;
	String cGrupoAutorizaAdec = "";
	String nNumSicop = "";
	String cRecMotivSicop = "";
	String nNumMAP = "";
	String cRecMotivMAP = "";
	String cFechaAplicacion = "";
	String bValidaSaldo = "";
	String cSuperReduccion = "";
	String cSRInterna = "";
	String[] nNumCAL = new String[2];
	//String nNumCAL = "";

	if (request.getParameter("cSuperReduccion") != null)
		cSuperReduccion = request.getParameter("cSuperReduccion");
	if (request.getParameter("cSRInterna") != null)
		cSRInterna = request.getParameter("cSRInterna");

	int bActivaAplicar = 0;
	int id_oper = -1;
	boolean motorNuevo = true;

	ArrayList sResultado = new ArrayList();
	ArrayList arrmEpCalendario = new ArrayList();

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = c.getCasoOperacion(0).getIdOperacion();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] archivoExcel = null;
	String select = c.getTipoCaso().getGavetaAsociada() + "_G" + c.getIdGabinete();
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1);

	if (session.getAttribute("cSRInterna") != null && archivoExcel.length == 0) {
		session.removeAttribute("cSRInterna");
	}

	String cAplicaDocto = "No";

	if (request.getParameter("aplicaDocto") != null && request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	if (request.getParameter("cAutomaticoXLS") != null)
		cAutomaticoXLS = "NO";

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
		cUR = usuario.getU_UR();	
		if (usuario.getPropiedades().containsKey("SUPERREDUCCION")) {
			bValidaSaldo = usuario.getPropiedad("SUPERREDUCCION").getValor();
		}

	}

	Map<?, ?> m = CasoDatoManager.readValuesCasoDato(request, c.getCasoDato(), true);
	String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;

	AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

	try {

		nConsecutivoSicop = adecua.validaAdecSicop(nFolio);
		nNumMAP = adecua.obtienenNumMAP(nFolio);
		nNumSicop = adecua.obtienenNumSicop(nFolio);
		//nNumCAL = adecua.obtienenNumCAL(nFolio);

		arrConsolidado = adecua.validaAdecIntegrada(nFolio);

		if (arrConsolidado.size() > 0) { //este siempre va a ser mayor a cero porque en su consulta la asignacion del 
			//arrConsolidado lo tiene afuera del if rs.next() por lo que siempre lo hace con los valores por default
			if (nConsecutivoSicop == 0) {
				nConsecutivoSicop = (Integer) arrConsolidado.get(0);
			}
			nFolioConsolidado = (Integer) arrConsolidado.get(1);
			if ("".equals(cSuperReduccion))
				cSuperReduccion = (String) arrConsolidado.get(2);
			if ("".equals(cSRInterna))
				cSRInterna = (String) arrConsolidado.get(3);
		}

		cAnioFiscal = adecua.obtenEjercicioFiscal();

		String cRedSiuper = "";

		if (nFolioConsolidado > 0 || nConsecutivoSicop > 0) {
			cRedSiuper = adecua.obtenSuperAdecuacion(c);
		}

	} catch (Exception ex) {
		log.warn(ex);
	}

	//LIMPIA FOLIO SICOP DE UNA ADECUACION INDIVIDUAL CUANDO SE QUIERE DESINTEGRAR
	if (request.getParameter("desintegrasicop") != null
			&& "si".equals(request.getParameter("desintegrasicop"))) {
		adecua.actualizaFolioSicopEncabezado("0", nFolio);
	}
	//*****************************************************

	if (request.getParameter("agregaFolio") != null) {
		nNumSicop = request.getParameter("nNumSicop");
		nNumMAP = request.getParameter("nNumMAP");
		nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();

		try {
			adecua.agregaFolio(nFolio, nNumSicop, nNumMAP);
		} catch (Exception ex) {
			log.warn(ex);
		}
	}

	Connection conn = null;
	conn = (new DocPolizaEncabezadoBusinessLogic()).getConnection();
	int mesAbierto = DocPolizaEncabezadoManager.readnMesAbierto(conn, cCentroContable,cUR);
	CloseObject.closeObject(conn);

	String folioSAI = c.getFolio();
	String fAplicacion[] = AdecuacionBusinessLogic.readfAplicacion(folioSAI.split("-")[2], cCentroContable, cUR);
	System.out.println("0: Fecha de Aplicacion " + fAplicacion[0]);
	System.out.println("1: Fecha Minima " + fAplicacion[1]);
	System.out.println("2: Fecha Maxima " + fAplicacion[2]);
	String fCaptura = AdecuacionBusinessLogic.readfCaptura(folioSAI.split("-")[2], cCentroContable);
	System.out.println(fCaptura);

	int iAdecuacion = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	int nTotalSecuenciasArch = 0;
	try {

		if ("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) {
			/* VGC 20141002 */
			try {
				adecuacion = adecua.cargaAdecuacion(nFolio);
			} catch (Exception ex) {

				log.error(ex, ex);
				if (resultadoCarga == null)
					resultadoCarga = new ResultadoValidacionAdecuacion();

				resultadoCarga.setError(new ArrayList<String>());
				resultadoCarga.getError().add("Ocurrio el siguiente error: " + ex.toString());

				bErrorAdec = true;

			}
		}
		if (("No".equals(cAplicaDocto)) && (archivoExcel.length > 0) && (id_oper == 5)) {

			if (request.getParameter("cSRInternaF") != null) {
				cSRInterna = request.getParameter("cSRInternaF");
			}
			/* VGC 20141002 */
			try {
				adecuacion = adecua.cargaAdecuacion(nFolio);

			} catch (Exception ex) {

				log.error(ex, ex);
				if (resultadoCarga == null)
					resultadoCarga = new ResultadoValidacionAdecuacion();

				resultadoCarga.setError(new ArrayList<String>());
				resultadoCarga.getError().add("Ocurrio el siguiente error: " + ex.toString());
				bErrorAdec = true;
			}
		}

		if (("No".equals(cAplicaDocto)) && (archivoExcel.length > 0) && (id_oper == 1)) {
			if (c.getIdGabinete() != -1 && cAutomaticoXLS.equals("NO"))
				cbl.borraDocumento(select + "C0D1");
			/* VGC 20141002 */
			resultadoCarga = adecua.validaArchivoExcel(
					cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0]
							.getAbsolutePath(),
					c, usuario, cSuperReduccion, cSRInterna);
			bErrorAdec = (resultadoCarga.getError() != null && resultadoCarga.getError().size() > 0);
			conAdvertencias = (resultadoCarga.getAdvertencia() != null
					&& resultadoCarga.getAdvertencia().size() > 0);

			if (!bErrorAdec)
				adecuacion = adecua.cargaAdecuacion(nFolio);
		}
		if (archivoExcel.length > 0 && id_oper == 2) { //vil parche para que funcione en el id_oper 2 porque si un usuario termina sesión y entra en el paso 2 ya no encuentra el arreglo para generar layout

			/* VGC 20141002 */
			try {
				adecuacion = adecua.cargaAdecuacion(nFolio);
			} catch (Exception ex) {

				log.error(ex, ex);
				if (resultadoCarga == null)
					resultadoCarga = new ResultadoValidacionAdecuacion();

				resultadoCarga.setError(new ArrayList<String>());
				resultadoCarga.getError().add("Ocurrio el siguiente error: " + ex.toString());

				bErrorAdec = true;

			}

		}
	} catch (Exception ex) {
		log.warn(ex, ex);
		bErrorAdec = true;
		adecua.ErrorAdecuacuines(c, ex.getMessage(), usuario.getLogin(), id_oper, m, prefixPath);
	}
	/* VGC 20141002 Vere que hace esta funcion si no le encuentro utilidad adios*/
	if (request.getParameter("buscaEP") != null && "true".equals(request.getParameter("buscaEP"))) {
		if ("1".equals(request.getParameter("cClave"))) {

			String cPPC = request.getParameter("txtGridPPC");
			String cOGTOC = request.getParameter("txtGridOGTOC");
			String cTGC = request.getParameter("txtGridTGC");
			String cFFC = request.getParameter("txtGridFFC");
			String cUEC = request.getParameter("txtGridUEC");
			try {
				arrmEpCalendario = adecua.buscaEPAdecuar(cPPC, cOGTOC, cTGC, cFFC, cUEC, "", cAnioFiscal);
			} catch (Exception ex) {
				log.warn(ex);
				bErrorAdec = true;
			}
		} else {
			String cnCodigo = request.getParameter("nCodigo");
			try {
				arrmEpCalendario = adecua.buscaEPAdecuar("", "", "", "", "", cnCodigo, cAnioFiscal);
			} catch (Exception ex) {
				log.warn(ex);
				bErrorAdec = true;
			}
		}
	}

	boolean integrador = false;

	//TODO ver abajo
	/*VGC20141002 Se hace desde que se carga el excel. Lo comento pero lo quitare pronto*/
	//     try {
	// 		inibeAPL = adecua.validaRestrictivas(nFolio);//true cuando hay restricciones (deshabilitar botones)
	//     } catch (Exception ex) {
	// 		log.warn(ex);
	// 		mensaje = Util.encodeJS(ex.getMessage());
	// 		errMensajeVAdec += mensaje;
	// 		bErrorAdec = true;
	//     }
	//     mensaje = Util.encodeJS(mensaje.replace("\"", ""));

	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES")
			&& "SI".equals(usuario.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())) {
		integrador = true;
		inibeAPL = false;//si es integrador no importan las restrictivas
	}

	ArrayList<String> responsables;
	if (integrador) {
		responsables = adecua.getResponsableIntegrador("REVISOR_GERENCIA_RF");
	} else {
		responsables = adecua.getResponsableArea(usuario.getLogin());
	}

	Map<?, ?> preferencias_cliente = null;
	boolean diferimiento_calendario = false;
	try {
		Connection conn_prop_cte = cbl.getConnection();
		preferencias_cliente = GrupoPropiedadesManager.select(conn_prop_cte, "PREFERENCIAS_CLIENTE");
		conn_prop_cte.close();
		conn_prop_cte = null;
	} catch (Exception expropcte) {
		log.error("Error leyendo propiedades del cliente: ", expropcte);
	}

	String mensajeAvisos = "";
	boolean alertMensajeAvisos = false;
	if (preferencias_cliente != null && preferencias_cliente.containsKey("DIFERIMIENTO_CALENDARIO")
			&& preferencias_cliente.get("DIFERIMIENTO_CALENDARIO") != null) {
		diferimiento_calendario = ((GrupoPropiedades) preferencias_cliente.get("DIFERIMIENTO_CALENDARIO"))
				.getValor().toUpperCase().contains("TRUE");
	}
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Adecuación Presupuestal</title>

<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../js/jsquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../js/catalogo/general.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../js/masks.js"></script>
<script type="text/javascript" src="../Generador/js/validaciones.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.editable-1.3.js"></script>

<script type="text/javascript" charset="utf-8">
	var esAdmin = <%=integrador%>;
	var conAdvertencias = <%=conAdvertencias%>;
		
$(document).ready(
		function() {
			$("#DPC_fFechaSicop").val(moment().format('yyyy-MM-DD'));
			$("#DPC_fFechaMAP").val(moment().format('yyyy-MM-DD'));
			
				$("#dlgAdvertencias").hide();
				$("#epSelected").hide();
				$("#dlgDetalleAdecuacion").hide();
				$("#dlgError").hide();
				
				$('#dlgJustificaciones').dialog(
					{autoOpen:false,
						close: function(ev, ui) 
	                	{
							guardaJ();
	                	}
					});
				
				$('.currency').blur(function(){
					$('.currency').formatCurrency();
				});
				
				var oTableTechos = $('#grdAdecuaciones').dataTable({
					"bPaginate": false,
					"bLengthChange": false,
					"bFilter": true,
					"bSort": false,
					"bInfo": false,
					"bAutoWidth": false,
					"sScrollX": "100%",
					"bScrollCollapse": true,	
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
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
				}  );
				
				//var $dlgDetalleAdecuacion;
				$(function(){
					$('#dlgDetalleAdecuacion').dialog({
						autoOpen: false,
						width: "1200px",
						heigth: "1000px"
					});
				});
				
				var $dlgError;
				$(function(){
					$('#dlgError').dialog({
					autoOpen: <%=bErrorAdec /*|| mensajeAut*/%>,
						width: 1200,
						heigth: 1800
					});
				});
				
				
				$('#dlgAdvertencias').dialog({
					autoOpen: false,
					width: 1200,
					heigth: 1800,
					modal:true,
					buttons : {
						"Continuar"  : function() {
							continuar();
						},
						"Descartar" : function() {
							noContinuar();
						}
					},
					close : function() {
						closeDlgMovito();
					}
				});
				
				
				var $dlgBuscaEP;
				$(function(){
					$('#dlgBuscaEP').dialog({
						autoOpen: false,
						width: 1200,
						heigth: 900
					})
				})
				
					
				<%if (responsables.size() > 1) {%>
					$("#exporta").change(function(){ 
						if($("#exporta option:selected").val()==3){
							$("#responsable").show();
						}else{
							$("#responsable").hide();
						}
					});
				<%}%>				
			
				$("#FCAL").datepicker({
					dateFormat: "yy/mm/dd",
					autoclose: true,
					changeYear: true,
					changeMonth: true
				});
				
				$("#Calendario").button();
				
				$("#responsable").change(function () {
         				var str = "";
         									
         				$("#responsable option:selected").each(function () {
               					str += $(this).val() + " ";
             				});
         				$("#responsableC").text(str);
       			})
       			.trigger('change');
				
				$("#ImportarExcel").button();
				$("#Calendario").button();
				$("#Autoriza").button();
				$("#aplicarContable").button();
				$("#Cancelar").button();
				$("#Exportar").button();
				$("#muestraJust").button();
				$("#muestraJust2").button(); 
				$("#btnMostrar").button(); 
				onLoadPlantilla(<%=id_oper%>);
			});

		var avanzaProcesando=false;
		//TODO Ver abajo
		/*VGC20141002 Implementar esta funcion. No actualliza el numero de sicop/map.*/
		function actualizaFechaSICOPMAP(){
		}
		
		function get(name) {
			return document.getElementById(name).value;
		}
		
		function noContinuar(){
			$.blockUI({message: "Procesando espere ......"});
			parent.document.getElementById("pb_cancel").click();
			$('#dlgAdvertencias').dialog('close');
		}
		
		function continuar(){
				Swal.fire({
					text: "Si continua la adecuacion se procesara tal cual fue planteada. ¿Esta seguro que desea continuar?",
					icon: "info",
					showCancelButton: true,
					confirmButtonColor: '#288BA8',
					cancelButtonColor: '#e6e6e6',
					confirmButtonText: 'Aceptar',
					cancelButtonText: 'Cancelar'
				}).then((result) => {
					if(result.isConfirmed){
						$('#dlgAdvertencias').dialog('close');
					}
				})
		}
		/**
		 * Validaciones que se realizan al guardar.
		 */
		function onSubmit(id_oper){
			var p = window.parent;
			var valida_campos = true;
			try{
				var msgAlert="";
				if(<%=c.getIdGabinete()%>!=-1){
				if(get("FECHA_APLICACION_CONTABLE")=="")
					msgAlert+="La fecha de aplicación contable es requerida\n";
				}
				if(msgAlert!=""){
					Swal.fire({ icon: 'warning',
								text: msgAlert });					
					return false;
				}
				if (id_oper==1){
					p.gestion.setMensaje("");
					p.gestion.setFolio(get("FOLIO"));
					p.gestion.setOperador(get("OPERADOR"));
					p.gestion.setFechaDocumento(get("FECHA_SOLICITUD"));
					p.gestion.setEjercicioFiscal(get("EJERCICIO_FISCAL"));
					p.gestion.setConceptoMov("Adecuacion Presupuestal");
					p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
					p.gestion.setMensaje("");
					queryFormPost("ActualizaEsIP",{async:false});
					$("#EsIP").attr("disabled", true);
				}
				if(id_oper==5){
					p.gestion.setMensaje("");
					p.gestion.setFechaApCont(get("FECHA_APLICACION_CONTABLE"));
				}
				if(<%=c.getIdGabinete()%>!=-1){
					parent.document.getElementById("pb_save").disabled=true;
					if(id_oper!=5 && !<%=bErrorAdec%>){
						parent.document.getElementById("pb_send").disabled=false;
					}
					if(id_oper!=5 && <%=integrador%>){
						parent.document.getElementById("pb_send").disabled=false;
					}
				}
			}catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
		}

		function onPostSubmit(id_oper){
			return true;
		}

		function onLoadPlantilla(id_oper){
			
			var osdrlis = document.getElementById("tipomap");
			var osdrTipAdec = document.getElementById("tipoAdecua");
			var oldNivelAdec;
			var bAdminAdec = <%=integrador || (!integrador && !inibeAPL) ? true : false%>; 
			
			$("#FOLIO").val("<%=c.getFolio()%>");
			<%if (archivoExcel.length > 0) {%>
	
				$("#justificacion").val( '<%=adecuacion == null
						? ""
						: (adecuacion.getEncabezado() == null ? "" : adecuacion.getEncabezado().getJustificacion())%>' );
				if(<%=bErrorAdec%>){
					MostrarDialogError();
				}else if( conAdvertencias ){
					mostrarDialogAdvertencias();
				}
				
				var cTipoAdecua   = "<%=adecuacion == null
						? ""
						: (adecuacion.getEncabezado() == null ? "" : adecuacion.getEncabezado().getcTipoAdecuacion())%>";
				var cNivel		  = "<%=adecuacion == null
						? ""
						: (adecuacion.getEncabezado() == null ? "" : adecuacion.getEncabezado().getnNivel())%>";
				
				var mMontosDifer  ="<%=adecuacion == null ? "" : Util.formatNumber(adecuacion.getDiferencia())%>";
				var mMontoCargos  ="<%=adecuacion == null ? "" : Util.formatNumber(adecuacion.getTotalReducciones())%>";
				var mMontosAbonos ="<%=adecuacion == null ? "" : Util.formatNumber(adecuacion.getTotalAmpliaciones())%>";
				
				$("#mreduc").val( mMontoCargos );
				$("#mmplea").val( mMontosAbonos );
				$("#mdif").val( mMontosDifer );
	
				if ( <%=bErrorAdec%> ){
					if (<%=id_oper%> != 6){
						parent.document.getElementById("pb_save").disabled=true;
					}
					$("#Cancelar").attr("disabled","disabled");
					$("#Autoriza").attr("disabled","disabled");
				}

				oNumberMask = new Mask("#,###.##", "number");
				
				oNumberMask.attach(document.getElementById("mmplea"));
				document.getElementById("mmplea").focus();
				
				oNumberMask.attach(document.getElementById("mreduc"));
				document.getElementById("mreduc").focus();
				
				oNumberMask.attach(document.getElementById("mdif"));
				document.getElementById("mdif").focus();
				
				osdrTipAdec.value = cTipoAdecua;
				
				if (cNivel == 1 || cNivel == 2 || cNivel == 3 || cNivel == 4 || cNivel == 5 ){
					osdrlis.value=cNivel;
					oldNivelAdec= document.getElementById("nivelAdecu");
					oldNivelAdec.value=cNivel;
	
					if ( <%=id_oper%> == 2 ){
						$("#nivelAdecu").attr("disabled","disabled");
					}
				}
				
				if (cTipoAdecua != ""){
					osdrTipAdec.value=cTipoAdecua.toString();
				}
				
				$("#tipoAdecua").attr("disabled","disabled");
				$("#tipomap").attr("disabled","disabled");
				$("#EsIP").attr("disabled", true);
			<%}%>
	
				if(<%=id_oper%>  == 1){
					if(<%=c.getIdGabinete()%>==-1)
						parent.document.getElementById("pb_save").click();//esto es para asegurar que se crea el expediente antes de subir el archivo
					
					if( parent.document.getElementById("pb_send") )
						parent.document.getElementById("pb_send").disabled=true;
						
					if( parent.document.getElementById("pb_save") )
						parent.document.getElementById("pb_save").disabled=true;
				
				}else if(<%=id_oper%>  == 2){
					protegeAdecuacion();
					document.getElementById("Autoriza").disabled=false;
					
					if( parent.document.getElementById("pb_send") )
						parent.document.getElementById("pb_send").disabled=true;
					if( parent.document.getElementById("pb_save") )	
						parent.document.getElementById("pb_save").disabled=true;	
										
				}else if(<%=id_oper%>  == 3){
					protegeAdecuacion();
					parent.document.getElementById("pb_cancel").disabled=true;
				}else if(<%=id_oper%>  == 6 ){
					$("#Cancelar").attr("disabled","disabled");
					$("#nNumMAP").val("<%=nNumMAP%>");
					$("#nNumSicop").val("<%=nNumSicop%>");
					
					if( parent.document.getElementById("pb_send") )
						parent.document.getElementById("pb_send").disabled=true;
						
					protegeAdecuacion();
				}else if(<%=id_oper%>  == 7 ){
					protegeAdecuacion();
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=true;
					parent.document.getElementById("pb_leave").disabled=true;
					$("#Cancelar").attr("disabled","disabled");
				}else{
					if (<%=!bErrorAdec%>) {
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=false;
						if(id_oper!=5){
							document.getElementById("aplicarContable").style.visibility="hidden";
						}
					}
					if ( <%=id_oper%> == 5 ){ 
						parent.document.getElementById("pb_send").disabled=true;
						parent.document.getElementById("pb_save").disabled=true;								
					}
				}
				
				if (<%=bErrorAdec%>  && id_oper == 1 ){
					if (!bAdminAdec){
							parent.document.getElementById("pb_send").disabled=true;
							parent.document.getElementById("pb_save").disabled=true;
					}
				}
				
			if (<%=nFolioConsolidado%> > 0 ){
				document.getElementById("et_label").style.visibility="visible";
				document.getElementById("nFolioConsolidado").style.visibility="visible";
				Swal.fire({ icon: 'warning',
							text: "Advertencia: Esta adecuación está incluída en la Integración con Folio IADE-A02-" + <%=nFolioConsolidado%>  + " para realizar una Autorización o Cancelación se requiere desintegrarla para poder realizar cualquiera de estas opciones de Forma individual." });
				$("#Cancelar").attr("disabled","disabled");
				$("#Autoriza").attr("disabled","disabled");	
				$("#nFolioConsolidado").val("<%=nFolioConsolidado%>");
				$("#nConsecutivoSicop").val("<%=nConsecutivoSicop%>");
			}

			if (<%=nConsecutivoSicop%> > 0 ){
				$("#nConsecutivoSicop").val("<%=nConsecutivoSicop%>");
			}
			
			queryFormPost( "readInfoCalendario", {async:false}  );
			if (id_oper != 1)
				$("#EsIP").attr("disabled", true);

		}
	
		function protegeAdecuacion(){
			
			var j = <%=adecuacion == null
					? "0"
					: (adecuacion.getDetalle() == null ? "0" : adecuacion.getDetalle().size())%>;
			var i=0;
			//TODO Donde carajo se usn estas variables chingao!
			for(i=1;i<=j;i++){
				$("#epSiaff"+i).attr("readOnly","readOnly");
				$("#epInterna"+i).attr("readOnly","true");
				$("#claveCNA"+i).attr("readOnly","true");
				$("#tipoAdecua"+i).attr("readOnly","true");
				$("#mAnual"+i).attr("readOnly","true");
				$("#iConsecutivo"+i).attr("readOnly","true");
			}
		}
	
		function onPostDisplay(id_oper){
		
			if(id_oper==5 || avanzaProcesando){
				if(get("FECHA_APLICACION_CONTABLE")==""){
					Swal.fire({ icon: 'warning',
								text: "La fecha de aplicación contable es requerida" });
				}
				else{					
					var actualizado = true;
					var fechaAp = "";
					
					fechaAp = $("#FECHA_APLICACION_CONTABLE").val();
										
					if( actualizado){
					
						var nTotslCsmbios=0;
						var cSuperReduccion="";
						var cSRInterna="";
						var bSuperreduccion=<%=(usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("SUPERREDUCCION")
					&& "SI".equals(usuario.getPropiedad("SUPERREDUCCION").getValor())) ? true : false%>;
				
						if (bSuperreduccion){
				 
								cSuperReduccion="SI";
					 
						 
						}
						var strAction="../gstnmngr/adecuaPresupuesto";
						var nNumSicop=$("#nNumSicop").val();
						var DPC_fFechaSicop=$("#DPC_fFechaSicop").val();
						var nNumMAP=$("#nNumMAP").val();
						var DPC_fFechaMAP=$("#DPC_fFechaMAP").val();
						var nivelAdecu=$("#nivelAdecu").val();
						var tipoAdecua=$("#tipoAdecua").val();
						var notificaAdecuacion =$("#notificaAdecuacion").val();
						
						if(get("FECHA_APLICACION_CONTABLE")==""){
							wal.fire({ icon: 'warning',
										text: "La fecha de aplicación contable es requerida." });							
						}
						else{
							$.blockUI({message: "Procesando espere ......"});
							try{
								$.ajax({
									datatype:"html",
									type: "POST",
									url: strAction,
									async: false,
									data: {
										cAplicaDocto:'Si',
										cSuperReduccion:cSuperReduccion,
										cSRInterna:cSRInterna,
										nNumSicop:nNumSicop,
										DPC_fFechaSicop:DPC_fFechaSicop,
										nNumMAP:nNumMAP,
										DPC_fFechaMAP:DPC_fFechaMAP,
										//nNumCAL:nNumCAL,
										//DPC_fFechaCAL:DPC_fFecaCAL,
										nivelAdecu:nivelAdecu,
										tipoAdecua:tipoAdecua,
										notificaAdecuacion:notificaAdecuacion
									},
									success: function (data,textStatus){
									},
									error: function (par) {										
										wal.fire({ icon: 'error',
												   text: par });
										}
								});
							}catch (e) {
								wal.fire({ icon: 'error',
									   		text: e });
							}
						}
						$("#frmLeave").submit();	
 					}
				}
			}
		}
	
		function ResponsableSiguiente(id_oper){
		
			if (avanzaProcesando) {
				if(id_oper==5)
					return "REVISORES_ADECUACIONES";
				else
					return "JEFATURA_ADECUACIONES";
			} else {
				if(id_oper==1)
					return "REVISORES_ADECUACIONES";
				if(id_oper==3)
					return "CONSULTA_ADECUACION";
				if(id_oper==4)
					return "REVISORES_ADECUACIONES";
			}
		}
	
		function OperacionSiguiente(id_oper){
			if (avanzaProcesando) {
				return "procesando";
			} else {
				if(id_oper==1)
					return "revision_adecuacion";
				if(id_oper==3)
					return "consulta_adecuacion";
				if(id_oper==4)
					return "revision_adecuacion";
			}
		}
	
		function validaCamposLlave(){
			var msgAlert="";
			if(get("EJERCICIO_FISCAL")=="")
				msgAlert+="El ejercicio fiscal es requerido\n";
			if(get("FECHA_APLICACION_CONTABLE")=="")
				msgAlert+="La fecha de aplicación contable es requerida\n";
			if(	document.getElementById("uploadfile").value == "" ){//||
				msgAlert+="El archivo PDF de soporte de Adecuaciones Presupuestales es requerido";
			}
			if(msgAlert!=""){
				wal.fire({ icon: 'warning',
			   			   text: msgAlert });				
				return false;
			}
			else{
				document.adecform.submit();
				return true;
			}
		}
	
		function fnImportarExcel(){
			var msgAlert="";
			var vSuperAdecua="cSuperReduccion=NO";
			if( document.getElementById("importExcel").value == "" ){
				msgAlert+="El archivo Excel es requerido";
			}
		
			if(msgAlert!=""){
				wal.fire({ icon: 'warning',
		   			   		text: msgAlert });				
				return false;
			}else{				
				guardaExp();
				
				<%if ("SI".equals(bValidaSaldo)) {%>
					vSuperAdecua="cSuperReduccion=NO";
					document.getElementById("cSuperReduccionF").value = "NO";
					document.getElementById("cSRInternaF").value = "NO";
					vSuperAdecua+="&cSRInterna=NO";
				<%} else {%>
					vSuperAdecua="cSuperReduccion=NO&cSRInterna=NO";
					document.getElementById("cSuperReduccionF").value = "NO";
				<%}%>
				$.blockUI({message: "Procesando espere ......"});
				document.forms.upExcel.action="../caso/firmardoc?carpeta=2&"+vSuperAdecua;
				document.upExcel.submit();				
				return true;
			}
						
		}
	
		function guardaExp(){
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_save").click();
			parent.document.getElementById("pb_save").disabled=true;
		}
	
		function appendRow(){
			var tblId="tablaDetalleAdecuacion";
			if (document.getElementById("mSaldoReduce").value != "" && document.getElementById("mSaldoAmplear").value != "" ){				
				wal.fire({ icon: 'error',
   			   				text: "Error solo se permite en una linea reducción o ampliación no ambos" });
				return false;
			}
			var index = document.getElementById("rows_adec_table").value;
			index++;
			document.getElementById("rows_adec_table").value=index;
			var tbl = document.getElementById(tblId);
			var newRow = tbl.insertRow(tbl.rows.length);
			var newCell = newRow.insertCell(0);
			newCell.innerHTML = '<input type="text"  id="nMes'+index+'" size="8" maxlength="8" value="' + document.getElementById("nMes").value + '" onblur="" >';
			document.getElementById("nMes").value="";
			newCell = newRow.insertCell(1);
			newCell.innerHTML = '<input type="text"  id="nClaveCNA'+index+'" size="15" maxlength="15" value="'+ document.getElementById("nClaveCNA").value+'" >';
			document.getElementById("nClaveCNA").value="";
			newCell = newRow.insertCell(2);
			newCell.innerHTML = '<input type="text"  id="txtGridEP'+index+'" size="70" value="'+ document.getElementById("txtGridEP").value + '" >';
			document.getElementById("txtGridEP").value="";
			newCell = newRow.insertCell(3);
			newCell.innerHTML = '<input type="text"  id="mSaldoArrastre'+index+'" size="15" maxlength="15" value="'+ document.getElementById("mSaldoArrastre").value + '" >';
			document.getElementById("mSaldoArrastre").value="";
			newCell = newRow.insertCell(4);
			newCell.innerHTML = '<input type="text"  id="mSaldoReduce'+index+'" size="15" value="'+ document.getElementById("mSaldoReduce").value + '" >';
			document.getElementById("mSaldoReduce").value="";
			newCell = newRow.insertCell(5);
			newCell.innerHTML = '<input type="text"  id="mSaldoAmplear'+index+'" size="15" value="'+ document.getElementById("mSaldoAmplear").value + '" >';
			document.getElementById("mSaldoAmplear").value="";
			newCell = newRow.insertCell(6);
			newCell.innerHTML = '<img src="../images/b_eliminar.gif" height="30" onclick="remove(this)"/>';
			document.getElementById("nCuenta").value="81102";
			parent.document.getElementById("pb_save").click();
		}
	
		function remove(t){
			var td = t.parentNode;
			var tr = td.parentNode;
			var table = tr.parentNode;
			table.removeChild(tr);
		}
	
		function ocultaSeccionClave(numClave)
		{
			if (numClave == true ){
				dis= true ? '' : 'none';
				tab=document.getElementById('buscaEPSiaff');
				tab.getElementsByTagName('tr')[0].style.display=dis;
				tabn=document.getElementById('buscaClaveSiaff');
				tabn.getElementsByTagName('tr')[0].style.display='none';
			}else{
				dis= true ? '' : 'none';
				tab=document.getElementById('buscaClaveSiaff');
				tab.getElementsByTagName('tr')[0].style.display=dis;
				tabn=document.getElementById('buscaEPSiaff');
				tabn.getElementsByTagName('tr')[0].style.display='none';
			}
		}
	
		function autoriza(){
				
			if($.trim($("#nNumSicop").val())==""){
				Swal.fire({ icon: 'warning',
   							text: "Favor de proporcionar el numero SICOP antes de autorizar" });				
				return false;
			}
			
			if($.trim($("#DPC_fFechaSicop").val())==""){
				Swal.fire({ icon: 'warning',
							text: "Favor de proporcionar la fecha SICOP antes de autorizar" });				
				return false;
			}
			
			if($.trim($("#nNumMAP").val())==""){
				Swal.fire({ icon: 'warning',
							text: "Favor de proporcionar el numero MAP antes de autorizar" });				
				return false;
			}
			
			if($.trim($("#DPC_fFechaMAP").val())==""){
				Swal.fire({ icon: 'warning',
							text: "Favor de proporcionar la fecha MAP antes de autorizar" });				
				return false;
			}
				
			Swal.fire({
				title: '¿Desea continuar?',
				text: "Enviara el Documento a Aplicar Contablemente.",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#288BA8',
				cancelButtonColor: '#e6e6e6',
				confirmButtonText: 'Aceptar',
				cancelButtonText: 'Cancelar'
			}).then((result) => {
				if(result.isConfirmed){
					avanzaProcesando=false;
					
					parent.document.getElementById("pb_save").disabled=false;
					parent.document.getElementById("pb_save").click();//es para que se calcule el responsable y operacion
					parent.document.getElementById("pb_leave").disabled=true;
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=true;
					document.getElementById("Autoriza").disabled=true;
					document.getElementById("Cancelar").disabled=true;
					document.getElementById("esperar").style.display="block";
					var strAction="../gstnmngr/adecuaPresupuesto";
					var nNumSicop=$("#nNumSicop").val();
					$("#fSICOP").val($.trim($("#DPC_fFechaSICOP").val()).split('-').reverse().join('/'));
					var DPC_fFechaSicop=$("#fSICOP").val();
					var nNumMAP=$("#nNumMAP").val();
					$("#fMAP").val($.trim($("#DPC_fFechaMAP").val()).split('-').reverse().join('/'));				
					var DPC_fFechaMAP=$("#fMAP").val();
					var nivelAdecu=$("#nivelAdecu").val();
					var tipoAdecua=$("#tipoAdecua").val();
					
					var enviarCorreo = $("#enviarCorreo").attr("checked") == "checked"; 
					var actualizado = true;			
					
					$.blockUI({message: "Procesando espere ......"});
				
					if(actualizado){
						$.ajax({
							datatype:"html",
							type: "POST",
							url: strAction,
							async: false,
							data: {autorizaDocto:'Si',nNumSicop:nNumSicop,DPC_fFechaSicop:DPC_fFechaSicop,nNumMAP:nNumMAP,DPC_fFechaMAP:DPC_fFechaMAP,nivelAdecu:nivelAdecu,tipoAdecua:tipoAdecua,"enviarCorreo":enviarCorreo},
							success: function (data,textStatus){
							},
							error: function (par) {
								wal.fire({ icon: 'error',
											text: par.msg });						
							}
						});
						$("#frmLeave").submit();
					}else{
						$.unblockUI();
					}
				}
			})
			
			
		}

		function cancelarDoc(){
			//alert("cancelarDoc: ");
			if(document.autorizacion.motivoRechazo.value==""){
				Swal.fire({ icon: 'warning',
							text: "Motivo de rechazo es requerido" });									
				return false;
			}
			else{
				if (<%=nConsecutivoSicop%> > 0 ){					
					Swal.fire({
						title: '¿Desea continuar?',
						text: "Este Documento esta Tramite en SICOP.",
						 icon: 'info',
						 showCancelButton: true,
						 confirmButtonColor: '#288BA8',
						 cancelButtonColor: '#e6e6e6',
						 confirmButtonText: 'Aceptar',
						 cancelButtonText: 'Cancelar'
					}).then((result) => {
						if(result.isConfirmed){
							Swal.fire({
								title: '¿Desea continuar?',
								text: "Enviara el Documento a Cancelar Contablemente.",
								icon: "info",
								showCancelButton: true,
								confirmButtonColor: '#288BA8',
								cancelButtonColor: '#e6e6e6',
								confirmButtonText: 'Aceptar',
								cancelButtonText: 'Cancelar'
							}).then((result) => {
								if(result.isConfirmed){
									
									aplicaCancelado();
									
								}
							})							
						}
					})
				} else {
					Swal.fire({
						title: '¿Desea continuar?',
						text: "Enviara el Documento a Cancelar Contablemente.",
						icon: "info",
						showCancelButton: true,
						confirmButtonColor: '#288BA8',
						cancelButtonColor: '#e6e6e6',
						confirmButtonText: 'Aceptar',
						cancelButtonText: 'Cancelar'
					}).then((result) => {
						if(result.isConfirmed){

							aplicaCancelado();
							
						}
					})	
				}				
			}
		}
		
		function aplicaCancelado(){
			avanzaProcesando=true;
			parent.document.getElementById("pb_save").disabled=false;
			parent.document.getElementById("pb_save").click();//es para que se calcule el responsable y operacion
			//parent.document.getElementById("pb_cancel").disabled=true;
			parent.document.getElementById("pb_leave").disabled=true;
			parent.document.getElementById("pb_save").disabled=true;
			parent.document.getElementById("pb_send").disabled=true;
			document.getElementById("Cancelar").disabled=true;
			document.getElementById("Autoriza").disabled=true;
			document.getElementById("esperar").style.display="block";
			var strAction="../gstnmngr/adecuaPresupuesto";
			var motivoRechazo = '"'+$("#motivoRechazo").val()+'"';
			var cRecMotivMAP = '"'+$("#cRecMotivMAP").val()+'"';
			var cRecMotivSicop = '"'+$("#cRecMotivSicop").val()+'"';
			var cancelarDoc="Si";
			var id_oper=<%=id_oper%>;
			var enviarCorreo = $("#enviarCorreo").attr("checked") == "checked";
			
			$.blockUI({message: "Procesando espere ......"});
			$.ajax({
				datatype:"html",
				type: "POST",
				url: strAction,
				async: false,
				data: {cancelarDoc:cancelarDoc,motivoRechazo:motivoRechazo,id_oper:id_oper,cRecMotivMAP:cRecMotivMAP,cRecMotivSicop:cRecMotivSicop,"enviarCorreo":enviarCorreo},
				success: function (data,textStatus){
					//var newDoc = document.open("text/html","replace");
					//newDoc.write(data);
					//newDoc.close();
				},
				error: function (par) {
					wal.fire({ icon: 'error',
								text: par.msg });						
				}
			});			
		}

		function validaAdecua(){
			parent.document.getElementById("pb_cancel").disabled=true;
			document.forms.adecform.action="adecuacionPresupuestal.jsp?validaDoc=Si&id_oper="+<%=id_oper%>;
			document.adecform.submit();
		}

		function errorValidadndo(mensajeErr,numSecuencia){
		
			var bSuperreduccion=<%=(usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("SUPERREDUCCION")
					&& "SI".equals(usuario.getPropiedad("SUPERREDUCCION").getValor())) ? true : false%>;
		
			if (<%=id_oper%>  == 6 ){
				return;
			}
			if ( <%=bErrorAdec%>){

				document.getElementById("epSiaff"+numSecuencia).style.backgroundColor="red";
				document.getElementById("epInterna"+numSecuencia).style.color="red";
				document.getElementById("claveCNA"+numSecuencia).style.color="red";
				document.getElementById("tipoAdecua"+numSecuencia).style.color="red";
				document.getElementById("mAnual"+numSecuencia).style.color="red";
				document.getElementById("iConsecutivo"+numSecuencia).style.color="red";
				if (<%=bActivaAplicar%> == 0){
					if (!parent.document.getElementById("pb_save").disabled){
						parent.document.getElementById("pb_save").disabled=true;
					}
				}
			}else if (<%=bErrorAdec%> != true) {
				if (<%=id_oper%> != 6)
					parent.document.getElementById("pb_save").disabled=false;
				//if (<!%=inibeAPL%>){
				//	var bAdminAdec=<!%=(usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("INTEGRADOR_ADECUACIONES") && "SI".equals(usuario.getPropiedad("INTEGRADOR_ADECUACIONES").getValor())) ? true : false%>;
				//}
				var bAdminAdec = <%=integrador || (!integrador && !inibeAPL) ? true : false%>; //con lo de arriba la variable estaria indefinida cuando inibeAPL=false y truena cuando la quiere leer mas abajo

				if (<%=bErrorAdec%> && !bAdminAdec){
					parent.document.getElementById("pb_send").disabled=true;
					parent.document.getElementById("pb_save").disabled=true;
				}
		
				if (<%=id_oper%> == 2 || <%=id_oper%> == 3  ){
					parent.document.getElementById("pb_save").disabled=true;
					document.getElementById("Autoriza").disabled=false;
					document.getElementById("Cancelar").disabled=false;
				}
				if (<%=id_oper%> == 5 ){
					parent.document.getElementById("pb_save").disabled=true;
					if (<%=bErrorAdec%> != true){
						$("#aplicarContable").attr("disabled","disabled");
					}
				}
				if (<%=id_oper%> ==7|| <%=id_oper%> ==6){
					document.getElementById("Cancelar").disabled=true;
					//document.getElementById("Autoriza").disabled=true;					
				}
			}/*else{
				if (<%=id_oper%> ==1 || <%=id_oper%> ==5 ){
					document.getElementById("Autoriza").disabled=true;
				}
			}*/
			if (<%=id_oper%> == 5 ){
				parent.document.getElementById("pb_save").disabled=true;
				if (<%=bErrorAdec%> != true){
					$("#aplicarContable").attr("disabled","disabled");
				}
			}
		}

		function CompruebaCampo(){
			if (parent.document.getElementById("pb_save").disabled){
				parent.document.getElementById("pb_save").disabled=false;
			}
		}
	
		function activaCambio(nRegCambio){
			document.getElementById("iCambiado"+nRegCambio).value = "0"; //se forza indicar que no se cambio nada para que no se intente llevar nada hata nueva orden
		}
	
		function cambiaDetalle(event){
			wal.fire({ icon: 'warning',
						text: "Por el Momento la edición no esta disponible solo será procesada la información del archivo Excel" });			
			return false;
		}

		function MostrarDialog() {
			var A="",B="",C="",D="",E="",F="",G="",H="";
			var cDataQuery= " nFolioAdecuacion =  <%=nFolio%>";

			$('#dlgDetalleAdecuacion').dialog({height:1200}).dialog('open');

			$("#grdAdecuaciones").dataTable({
				"bProcessing": true,
				"bServerSide": true,
				"bDestroy": true,
				"sAjaxSource": window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vDetAdecuacion&qw="+cDataQuery,
				"bJQueryUI": true,
				"sScrollY": "100%",
				"sScrollX": "100%",
				"sScrollXInner": "250%",
				"sScrollYInner": "250%",
				"bPaginate": false,
				"bAutoWidth": false,
				"sPaginationType": "full_numbers",
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
						}},
				"bInfo": true,
				"aoColumns": [
					{ sName: "chk"},
					//{ sName: "nDocRenglon"},
					{ sName: "nFolioAdecuacion"},
					{ sName: "ClaveSIAFF" },
					{ sName: "ClaveInterna" },
					{ sName: "cEvento"},
					{ sName: "mAnual" },
					{ sName: "mEnero"},
					{ sName: "mFebrero"},
					{ sName: "mMarzo"},
					{ sName: "mAbril"},
					{ sName: "mMayo"},
					{ sName: "mJunio"},
					{ sName: "mJulio"},
					{ sName: "mAgosto"},
					{ sName: "mSeptiembre"},
					{ sName: "mOctubre"},
					{ sName: "mNoviembre"},
					{ sName: "mDiciembre"}
    			]
			});
			return true;
		}

		function MostrarDialogError() {
			$('#dlgError').dialog('option', 'modal', true).dialog('open');
			return true;
		}
		
		function mostrarDialogAdvertencias() {
			$('#dlgAdvertencias').dialog('open');
			return true;
		}

		function MostrarBuscaEP(){
			$('#dlgBuscaEP').dialog('option', 'modal', true).dialog('open');
		}

		function activaCampo(cTipoProtect){
			if (cTipoProtect == 1){
				document.getElementById("motivoRechazo").readOnly=true;
			}else{
				document.getElementById("motivoRechazo").readOnly=false;
			}
		}

		function desintegraSicop(){
			var strAction="adecuacionPresupuestal.jsp?desintegrasicop=si";
			
			$.blockUI({message: "Procesando espere ......"});
			$.ajax({
				datatype:"html",
				type: "POST",
				url: strAction,
				success: function (data,textStatus){
					var newDoc = document.open("text/html","replace");
					newDoc.write(data);
					newDoc.close();
				},
				async: false,
				error: function (par) {
					wal.fire({ icon: 'error',
								text: par });					
					}
				
			});
		}
		
		function muestraJ(){
			$('#dlgJustificaciones').dialog({height:500, width:1200}).dialog('open');
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_save").disabled=true;
		}
		
		function guardaJ(){
			if(<%=id_oper%>==1){
				var justificacionA = $('#justificacionA').val();
				var justificacionR = $('#justificacionR').val();
				var justificacionNormativa = $('#justificacionN').val();
				$.blockUI({message: "Procesando espere ......"});
				   	$.ajax({
				   		datatype:"html",
						type: "POST",
						url: "../gstnmngr/adecuaPresupuesto",
						async: false,
						data:{"accion":1,  "justificacionA":justificacionA, "justificacionR":justificacionR, "justificacionN":justificacionNormativa},
						success: function (data,textStatus){
				   			$.unblockUI();
				   			if( <%=!bErrorAdec || diferimiento_calendario%> )
				   				parent.document.getElementById("pb_save").disabled=false;
						},
						error: function (par) {
							wal.fire({ icon: 'error',
										text: par });							
							}
					});
				}
			 //$('#dlgJustificaciones').dialog('close');  
			 //parent.document.getElementById("pb_save").click();
		}
		
		function cambiaEsIP (){
			if ($('#EsIP').attr('checked') )
				$("#cEsIP").val("S");
			else
				$("#cEsIP").val("N");
			
		}
		
		function guardaCal(){	
		if ($("#nNumCAL").val() == "" || $("#FCAL").val() == "") {
			wal.fire({ icon: 'warning',
						text: "Los campos de Calendario y Fecha son obligatorios" });				
			}	
		else {
			//$.blockUI({message: "Procesando espere ......"});			
			 queryFormPost({
			 	queryName:"actualizaCalendarioAdecuacion",
			 	async:false,
			 	callback:function(){
			 		wal.fire({ icon: 'success',
								text: "No. de Calendario actualizado exitosamente." });			 		
			 	}	
			 });
			
			}
		//document.autorizacion.submit();		
		}		
		
		function habilita(){
			if ( $("#selEvento").prop("checked")) {
				$("#adecuacionReserva").val("SI");					
			} else{
				$("#adecuacionReserva").val("NO");
				 
			}
		}
		
		function habilitaSIPLAN(){
			if($("#noNotifica").prop("checked")){
				$("#notificaAdecuacion").val("NO");
			} else{
				$("#notificaAdecuacion").val("SI");
			}
		}
		
		function confirmAppCont(){
			var mDiferencia = document.getElementById("mdif").value.toString();
			var mReduce = document.getElementById("mreduc").value.toString();
			var mAmplia =document.getElementById("mmplea").value.toString();			
	
			Swal.fire({
				title: '¿Desea continuar?',
				text: "Enviara el Documento a Aplicar Contablemente.",
				icon: 'warning',
				showCancelButton: true,
				confirmButtonColor: '#288BA8',
				cancelButtonColor: '#e6e6e6',
				confirmButtonText: 'Aceptar',
				cancelButtonText: 'Cancelar'
			}).then((result) => {
				if(result.isConfirmed){
					<%if (!"SI".equals(bValidaSaldo)) {%>
					if (mDiferencia > 0 && mAmplia != 0 && mReduce != 0){
						wal.fire({ icon: 'error',
				   					text: "ERROR: La Adecuación no esta compensada." });				
						return false;
					}
					<%}%>
					
					avanzaProcesando=true;
					var adecuacionReserva = $("#adecuacionReserva").val();					
					
					$.ajax({
							url:'../gstnmngr/Adecuacion',
							type:'post',
							dataType: 'json',
							data:{action:"ValidaPreAplicacion", adecuacionReserva:adecuacionReserva},
							async:false,
							success:function(data){
								
									if(data.status != true){
											avanzaProcesando = false;
											var msgErrorVal = "";
											var errorList = data.error;
											var cnt = 0;
											for( cnt = 0; cnt < errorList.length; cnt++ )
												msgErrorVal += errorList[cnt] + "\n\n";
											$("#mensajeError").val(msgErrorVal);
											MostrarDialogError();
									}
							}
					});
					
					if( avanzaProcesando ){										   
						if( parent.document.getElementById("pb_cancel") )
							parent.document.getElementById("pb_cancel").disabled=true;
						
						parent.document.getElementById("pb_send").disabled=true;
						document.getElementById("aplicarContable").disabled=true;
						document.getElementById("esperar").style.display="block";
						parent.document.getElementById("pb_save").disabled=false;
						parent.document.getElementById("pb_save").click();
						parent.document.getElementById("pb_save").disabled=true;
					}	
				}
				
			})			
		}
		
	</script>

</head>
<br/>
<body id="dt_example">
	<div id="container" style="width: 80%" class="container">
		<div class="card-header"> <h3>Adecuación Presupuestal</h3> </div>					
		<hr class="mt-3">
		
		<%
			if ( id_oper > 1 ) {
		%>
			<form id="autorizacion" name="autorizacion" method="post">
				<input type="hidden" name="fSICOP" id="fSICOP" value="" />
				<input type="hidden" name="fMAP" id="fMAP" value="" />			
			
				<div class="row">					
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">										
						<div class="contenedor d-flex justify-content-center">												
							<label for="estatus" class="form-label"> 
								<%
									String cEstatus = "";
										if ( "true".equals( c.getCasoDato( "APLICADO_CONT" ).getValor() )) {
											cEstatus = "ESTATUS: DOCUMENTO EN TRAMITE";
										}
										if ( "true".equals( c.getCasoDato( "CANCELADO_CONT" ).getValor() ) ) {
											cEstatus = "ESTATUS: DOCUMENTO CANCELADO";
										}
										if ( "true".equals( c.getCasoDato( "AUTORIZADO_CONT" ).getValor() ) ) {
											cEstatus = "ESTATUS: DOCUMENTO AUTORIZADO";
										}
								%> <%=cEstatus%> 
							</label>
						</div>																			
					</div>
				</div>
				
				<div class="row">					
					<div class="col-12 col-lg-12 col-md-12 col-sm-12">										
						<div class="contenedor d-flex justify-content-center">												
							<label id="esperar" style="display: none;" class="form-label"> Espere por favor.... <img border="0" src="../imagenes/espera.gif" height="30"></label>
						</div>																			
					</div>
				</div>
				
				<% if ( "true".equals( c.getCasoDato( "APLICADO_CONT" ).getValor() ) ) {
				%>
					<div class="row">					
						<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
							<div class="contenedor d-flex justify-content-center">
								<input type="button" id="Cancelar" value="Cancelar Documento" name="Cancelar Documento" onclick="javascript:cancelarDoc();" class="btn btn-secondary btn-sm"></input>										
							</div>
						</div>
					</div>	
					
					<div class="row">					
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																						
							<label for="motivoRechazo" class="form-label"> Motivo de cancelaci&oacute;n: </label>
						</div>
						<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">																															
							<input type="text" id="motivoRechazo" name="motivoRechazo" class="form-control form-control-sm" size="80" maxlength="200">								
						</div>
					</div>								
				<% } 
				%> 
				<% if ( archivoExcel.length > 0 && id_oper == 5 && ( bErrorAdec == false || integrador || diferimiento_calendario ) ) { 
				%>
					<input type="hidden" name="adecuacionReserva" id="adecuacionReserva" value="NO"/>
					<input type="hidden" name="notificaAdecuacion" id="notificaAdecuacion" value="SI"/>
					
					
					<div class="row d-flex">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex justify-content-center">					
							<input type="button" id="aplicarContable" value="              Generar Documento de Adecuaci&oacute;n Presupuestal y Aplicar Contablemente             " onclick="return confirmAppCont();" class="btn btn-secondary"/>  	
				 		</div>
				 	</div>
				 	<div class="row d-flex">
				 		<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1 d-flex justify-content-center">			 		
				 			<input type="checkbox" class="form-check-input" id="selEvento" value = "NO" onclick="habilita()"> ¿Adecucacion de Reserva? - Activar si aplica
				 		</div>
				 	</div>
				 	<div class="row d-flex">
				 		<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1 d-flex justify-content-center">			 		
				 			<input type="checkbox" class="form-check-input" id="noNotifica" value = "SI" onclick="habilitaSIPLAN()"> Adecuacion NO notifica a SIPLAN - Activar si NO aplica
				 		</div>
				 	</div>
				<% }
				%>
				
				<% if ( id_oper == 2 ) {
				%>
				<div class="row">					
					<div class="col-12 col-lg-11 col-md-11 col-sm-12 p-1">										
						<div class="contenedor d-flex justify-content-center">
							<input type="button" id="Autoriza" value="Autorizar Documento" name="Autoriza" onclick="javascript:autoriza();" class="btn btn-secondary btn-sm"></input>																												
						</div>
					</div>								
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">										
						<div class="contenedor d-flex justify-content-center">										
							<input type="checkbox" class="form-check-input" id="enviarCorreo" name="enviarCorreo" value="true" checked="checked">Correo																			
						</div>
					</div>
				</div>	
				<% }
				%>
				
				<% if ( id_oper > 1 && id_oper < 5 ) {
				%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="nNumSicop" class="form-label"> Numero de Autorizacion SICOP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="text" id="nNumSicop" name="nNumSicop" class="form-control form-control-sm" maxlength="20" size="20" onChange="CompruebaCampo()" />
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<label for="DPC_fFechaSicop" class="form-label"> Fecha SICOP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<div class="input-group">
							<span class="input-group date"><i class="datepicker1"></i></span>					
							<input type="date" id="DPC_fFechaSicop" name="DPC_fFechaSicop" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				<!-- Motivo de Rechazo SICOP:<input id="cRecMotivSicop" name="cRecMotivSicop" value="" maxlength="400" size="80"></input> -->
				
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<label for="nNumMAP" class="form-label"> Numero de Autorizacion MAP: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<input type="text" id="nNumMAP" name="nNumMAP" class="form-control form-control-sm" maxlength="20" size="20" onChange="CompruebaCampo()" />
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
						<label for="DPC_fFechaMAP" class="form-label"> Fecha MAP: </label>						
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
						<div class="input-group">
							<span class="input-group date"><i class="datepicker1"></i></span>
							<input type="date" id="DPC_fFechaMAP" name="DPC_fFechaMAP" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				<!-- Motivo de Rechazo MAP:<input id="cRecMotivMAP" name="cRecMotivMAP" value="" maxlength="400" size="80"></input> -->
				<% } 
				if ( id_oper == 6 ) {
				%>
					<div class="row d-flex">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<label for="nNumSicop" class="form-label"> Autorizacion SICOP: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<input type="text" id="nNumSicop" name="nNumSicop" class="form-control form-control-sm" disabled />
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label for="DPC_fFechaSicop" class="form-label"> Fecha SICOP: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-calendar"></i></span>
								<input type="date" id="DPC_fFechaSicop" name="DPC_fFechaSicop" class="form-control form-control-sm" onKeyDown="return false" readonly/>
							</div>
						</div>
					</div>
					
					<div class="row d-flex">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<label for="cRecMotivSicop" class="form-label"> Motivo de Rechazo SICOP: </label>
						</div>
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">	
							<input type="text" id="cRecMotivSicop" name="cRecMotivSicop" class="form-control form-control-sm" disabled />
						</div
					</div>
					
					<div class="row d-flex">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<label for="nNumMAP" class="form-label"> Autorizacion MAP: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<input type="text" id="nNumMAP" name="nNumMAP" class="form-control form-control-sm" disabled />
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label for="DPC_fFechaMAP" class="form-label"> Fecha MAP: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-calendar"></i></span>
								<input type="date" id="DPC_fFechaMAP" name="DPC_fFechaMAP" class="form-control form-control-sm" onKeyDown="return false" readonly/>
							</div>
						</div>
					</div>
					
					<div class="row d-flex">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<label for="cRecMotivMAP" class="form-label"> Motivo de Rechazo MAP: </label>
						</div>
						<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">	
							<input type="text" id="cRecMotivMAP" name="cRecMotivMAP" class="form-control form-control-sm" disabled />
						</div
					</div>
					
					<div class="row d-flex">
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<label for="nNumCAL" class="form-label"> Autorizacion Calendario: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
							<input type="text" id="nNumCAL" name="nNumCAL" class="form-control form-control-sm" value="<%=StringUtils.isEmpty( nNumCAL[0] ) ? "" : nNumCAL[0]%>"/>
						</div>
						<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<label for="FCAL" class="form-label"> Fecha Calendario: </label>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-calendar"></i></span>	
								<input type="date" id="FCAL" name="FCAL" class="form-control form-control-sm" onKeyDown="return false" value="<%=StringUtils.isEmpty( nNumCAL[1] ) ? "" : nNumCAL[1]%>"/>
							</div>
						</div>
					</div>				
			
				<% }						
				%>
				
			</form>		
		<%
		 	}
		 %>
 		
 		<form id="upExcel" name="upExcel" action="../caso/firmardoc?carpeta=2" enctype="multipart/form-data" method="post">
 			<%
				if ( id_oper == 1 ) {
			%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">
						<input type="file" class="form-control form-control-sm" id="importExcel" name="importExcel" size="32" value=""></input>
					</div>
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
						<input type="button" id="ImportarExcel" name="ImportarExcel" value="Importar Excel" onclick="fnImportarExcel();" class="btn btn-secondary btn-sm"></input>
					</div>
				</div>	
			<%
				}
			%>
			
			<input type="hidden" id="cSuperReduccionF" name="cSuperReduccionF" value=""></input>
			<input type="hidden" id="cSRInternaF" name="cSRInternaF" value=""></input>
						
 		</form>
 		
 		<form id="ExportaExcel" name="ExportaExcel" action="../gstnmngr/AdecuacionesLayoutSicop" method="POST" target="blank">
			<input type="hidden" id="adecFolio" name="adecFolio" value="<%=nFolio%>">
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select name="exporta" id="exporta" class="form-select form-select-sm">
						<option value="1"> Archivo Excel </option>
						<option value="5"> Imprimir Adecuaci&oacute;n</option>
						<% if ( id_oper == 2 ) {
						%>
							<option value="2"> Layout SICOP </option>
						<%}
						%>
						<% if ( id_oper == 1 || integrador ) {
						%>
							<option value="3"> Formato FAP01 </option>						
						<%}
						%>
					</select>
					
					<select name="responsable" id="responsable" style="display: none;" class="form-select form-select-sm">
						<% if ( responsables != null && !responsables.isEmpty() ) {
								for ( int i = 0; i < responsables.size(); i++ ) {
						%>
							<option value="<%=responsables.get( i )%>"><%=responsables.get( i ).split( "><" )[0].trim()%></option>
						<%
								}
							}
						%>
					</select>	
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="submit" id="Exportar" value="Exportar" class="btn btn-secondary btn-sm" />
				</div>
			</div>
			
			<% if ( responsables != null && !responsables.isEmpty() ) {
			%>
			
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
						<textarea rows="10" cols="10" id="responsableC" name="responsableC" class="form-control form-control-sm" style="display: none;"><%=responsables.get( 0 ) != null ? responsables.get( 0 ) : ""%></textarea>
					</div>
				</div>
				
			<% } else {
			%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 p-1">
						<textarea rows="10" cols="10" id="responsableC" name="responsableC" class="form-control form-control-sm"  style="display: none;"></textarea>
					</div>
				</div>
			
			<% }
				if ( adecua.tieneSicop( nFolio ) && id_oper == 2 ) {
			%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
						<input type="button" id="DesintegraSicop" class="btn btn-secondary btn-sm" value="Desintegra" onclick="javascript:desintegraSicop();"></input>
					</div>
				</div>
			<% }
			%>
			
		</form>
		
		<form id="adecform" name="adecform" action="adecuacionPresupuestal.jsp?aplicaDocto=Si&cAutomaticoXLS=NO" method="post">
			<input type="hidden" name="id_caso" id="id_caso" value="<%=c.getIdCaso()%>">
			<input type="hidden" name="cEsIP" id="cEsIP" value="N">
			<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion( 0 ) == null ? "caso operacion nulo" : c.getCasoOperacion( 0 ).getResponsable()%>" />
			<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
			<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=cAnioFiscal%>" />
			<input type="hidden" type="text" id="nCuenta" name="nCuenta" value="81102">
			<input type="hidden" type="text" id="rows_adec_table" value="">
			<input type="hidden" id="nivelAdecu" name="nivelAdecu" value="">
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<label for="FECHA_APLICACION_CONTABLE" class="form-label"> Fecha de registro: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<input type="text" id="FECHA_APLICACION_CONTABLE" name="FECHA_APLICACION_CONTABLE" class="form-control form-control-sm" value="<%=fAplicacion[0]%>" readonly/>
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label for="FOLIO" class="form-label"> Folio: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<input type="text" id="FOLIO" name="FOLIO" class="form-control form-control-sm"  value="<%=c.getFolio()%>" readonly/>
					<input type="hidden" name="nFolioAdecuacion" class="form-control form-control-sm" id="nFolioAdecuacion" value="<%=nFolio%>" />
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<label for="nConsecutivoSicop" class="form-label"> Folio Interno: </label>
				</div>			
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<input type="text" id="nConsecutivoSicop" name="nConsecutivoSicop" class="form-control form-control-sm"  value="<%=nConsecutivoSicop%>" readonly/>					
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<label id="et_label" for="nFolioConsolidado" class="form-label"> Folio IADE: </label>
				</div>			
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">	
					<input type="text" id="nFolioConsolidado" name="nFolioConsolidado" class="form-control form-control-sm"  value="<%=nFolioConsolidado%>" readonly/>					
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<label for="nConsecutivoSicop" class="form-label"> Justificaci&oacute;n: </label>
				</div>
				<div class="col-12 col-lg-7 col-md-7 col-sm-12 p-1">
					<textarea id="justificacion" name="justificacion" class="form-control form-control-sm" rows="6" cols="70"></textarea>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<label for="tipoAdecua" class="form-label"> Tipo: </label>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<select id="tipoAdecua" onchange="" class="form-select form-select-sm">
						<option value=""> </option>
						<option value="Ampliaci&oacute;n"> Ampliaci&oacute;n </option>
						<option value="Reducci&oacute;n"> Reducci&oacute;n </option>
						<option value="Transferencia"> Transferencia </option>
						<option value="Calendario"> Calendario </option>
					</select>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<% if ( !bErrorAdec && adecuacion != null && adecuacion.getDetalle() != null && adecuacion.getDetalle().size() > 0 ) {
					%>
						<input id="btnMostrar" name="btnMostrar" type="button" value="Mostrar Detalle" onclick="MostrarDialog()" class="btn btn-secondary btn-sm"/>
					<% }
					%>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<label for="tipoAdecua" class="form-label"> Clasificaci&oacute;n: </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<select id="tipomap" onchange="" class="form-select form-select-sm">
						<option value=""></option>
						<option value="1"> Interna </option>
						<option value="2"> Interna CONAFOR </option>
						<option value="3"> Interna SHCP </option>
						<option value="4"> Externa SHCP sin restricci&oacute;n </option>
						<option value="5"> Externa SHCP con restricci&oacute;n </option>
					</select>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<FONT COLOR=NAVY FACE="Arial" SIZE="1">Ampliaci&oacute;n</FONT>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<FONT COLOR=NAVY FACE="Arial" SIZE="1">Reduci&oacute;n</FONT>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<FONT COLOR=NAVY FACE="Arial" SIZE="1">Diferencia</FONT>
				</div>
			</div>
			
			<div class="row d-flex">
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mmplea" value="" maxlength="20" size="10"></input>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mreduc" value="" maxlength="20" size="10"></input>
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="text" class="form-control form-control-sm" id="mdif" value="" maxlength="20" size="10"></input>
				</div>
			</div>
			
			<% if ( bErrorAdec ) {
			%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<input id="btnMostrarError" name="btnMostrarError" type="button" value="Mostrar Detalle de Errores" onclick="MostrarDialogError()" class="btn btn-secondary btn-sm"/>
					</div>
				</div>
			<% } else if ( conAdvertencias ) {
			%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<input id="btnMostrarAdvertencias" name="btnMostrarAdvertencias" type="button" value="Mostrar Advertencias" onclick="mostrarDialogAdvertencias()" class="btn btn-secondary btn-sm"/>
					</div>
				</div>
			<%}
			%>
			
			<% if ( id_oper == 1 ) {
				if ( !bErrorAdec ) {
			%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<input type="button" id="muestraJust" name="muestraJust" value="Capturar Justificaciones" onclick="muestraJ()" class="btn btn-secondary btn-sm"/>
					</div>
				</div>

			<% 	}
			} else {
			%>
				<div class="row d-flex">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
						<input type="button" id="muestraJust2" name="muestraJust2" value="Mostrar Justificaciones" onclick="muestraJ()" class="btn btn-secondary btn-sm"/>
					</div>
				</div>
			<% }
			%>
			
		</form>

	</div>
	
	<div id="dlgError" title="Detalle de Errores" class="contariner">		
		<h5> Detalle de Errores de Adecuaciones Presupuestales </h5>
		<hr class="mt-3">
		
		<div class="row d-flex">
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
				<%
					out.println( "<textarea id=\"mensajeError\" rows=\"16\" cols=\"140\" class=\"form-control form-control-sm\">" );
				%> <%
				 	if ( resultadoCarga != null ) {
				 %> <%
				 	List<String> mensajesValidacion = resultadoCarga.getError();
				 %> <%
				 	if ( mensajesValidacion != null && mensajesValidacion.size() > 0 ) {
				 %> <%
				 	for ( Iterator<?> it = mensajesValidacion.iterator(); it.hasNext(); ) {
				 %> <%
				 	out.print( it.next() );
				 %> <%
				 	}
				 %> <%
				 	}
				 %> <%
				 	}
				 %> <%
				 	out.println( "</textarea>" );
				 %>
			</div>
		</div>
					
	</div>
	
	<div id="dlgAdvertencias" title="Detalle de Advertencias">
		<h5> Detalle de Advertencias de Adecuaciones Presupuestales </h5>
		<hr class="mt-3">
		
		<div class="row d-flex">
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
				<span> <b>Se detectaron problemas potenciales mientras se validaba la adecuacion. </b> Puede ignorarlos bajo su propia responsabilidad o descartar la adecuacion y replantearla. <br>
				<br> Presione <b>Continuar</b> para proseguir con el proceso normal de la adecuacion. <br> Presione <b>Descartar</b> para eliminar esta adecuacion y replantear. <br>
				<br> Detalle de Advertencias: <br>
				</span>
			</div>
		</div>
		
		<div class="row d-flex">
			<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
				<%
					if ( resultadoCarga != null ) {
				%> <%
				 	List<String> mensajesValidacion = resultadoCarga.getAdvertencia();
				 %> <%
				 	if ( mensajesValidacion != null && mensajesValidacion.size() > 0 ) {
				 %> <textarea id="mensajeAdvertencias" rows="16" cols="140" class="form-control form-control-sm">
						<%for ( Iterator<?> it = mensajesValidacion.iterator(); it.hasNext(); ) {%>
							<%out.print( it.next() );%>
						<%}%>
					</textarea> <%
				 	}
				 %> <%
				 	}
				 %>
			</div>
		</div>

	</div>

	<div id="dlgDetalleAdecuacion" title="Detalle de Adecuaciones Presupuestales">		
		<div class="table-responsive">	    					
			<table  id="grdAdecuaciones" class="table table-striped">			
				<thead>
					<tr>
						<th>chk</th>
						<!-- th>nDocRenglon</th -->
						<th>Folio</th>
						<th>Clave SIAFF</th>
						<th>Clave Interna</th>
						<th>Tipo</th>
						<th>Anual</th>
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
					</tr>
				</thead>
				<tbody>
				</tbody>
				<tfoot>
				</tfoot>
			</table>
		</div>		
	</div>
	
	<div id="dlgJustificaciones" title="Justificaciones Adecuaciones Presupuestales" class="container">
		<div class="row d-flex">
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<label for="justificacionA" class="form-label"> Justificación técnica de ampliación: </label>
			</div>			
			<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">	
				<textarea id="justificacionA" name="justificacionA" class="form-control form-control-sm" rows="5" cols="110">N/A</textarea>				
			</div>
		</div>
		
		<div class="row d-flex">
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<label for="justificacionR" class="form-label"> Justificación técnica de reducción: </label>
			</div>			
			<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">	
				<textarea id="justificacionR" name="justificacionR" class="form-control form-control-sm" rows="5" cols="110">N/A</textarea>				
			</div>
		</div>
		
		<div class="row d-flex">
			<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				<label for="justificacionN" class="form-label"> Justificación normativa: </label>
			</div>			
			<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">	
				<textarea id="justificacionN" name="justificacionN" class="form-control form-control-sm" rows="5" cols="110">N/A</textarea>				
			</div>
		</div>		
	</div>
	
	<form id="frmLeave" action="../gstnmngr/gestion?<%=GestionInterface.PRM_CMD%>=<%=GestionInterface.CMD_OPEN_INBOX%>" method="post" target="content-iframe"></form>
	
</body>
</html>