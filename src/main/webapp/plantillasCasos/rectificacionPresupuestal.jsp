<%@page import="com.syc.contable.core.Rectificacion"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.syc.gestion.core.*"%>
<%@ page import="com.syc.gestion.servlet.*"%>
<%@ page import="com.syc.gestion.util.*"%>
<%@ page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@ page import="com.syc.gestion.CasoBusinessLogic"%>
<%@ page
	import="com.syc.contable.RectificacionPresupuestariaBusinessLogic"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="java.io.File"%>
<%@ page import="org.slf4j.Logger"%>
<%@ page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@ page import="com.syc.contable.core.RectificacionEncabezado"%>
<%@ page import="com.syc.contable.core.RectificacionDetalle"%>
<%@ page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>

<%!private static Logger log = Logger
			.getLogger("com.syc.plantillas.casos.RectificacionPresupuestal.jsp");%>
<%
	/**
	 * @author Martha Aurora Sánchez Valdivieso
	 * para SYC Constructores de Sistemas
	 * desarrollo gestion_conagua_sif
	 * México D.F. 
	 * Planeación 06 - 25/07/2012
	 * Programación 15/08/2012
	 */
	boolean cGrupoUSR = false;
	String cCentroContable = "";
	String cUR = "";
	String cRamo = "";
	String cUsrLog = "";
	String mensaje = "";
	String msg = "";
	String aEjercicioFiscal = "";
	
	//SACA LA FECHA DE HOY
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	//*************************************************
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	Caso caso = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	if (caso == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	Empleado empleado = new Empleado();
	EmpleadoBusinessLogic empleadobl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	RectificacionPresupuestariaBusinessLogic rectificacion = new RectificacionPresupuestariaBusinessLogic(GestionInterface.ATT_CONEXION);
	EmpleadoArea empleadoarea = new EmpleadoArea();
	CasoBusinessLogic casobl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	String select = caso.getTipoCaso().getGavetaAsociada() + "_G" + caso.getIdGabinete();//se usa por separado abajo
	String fCreacion = Util.getTodayESMX(  );
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable,cUR);
	System.out.println("0: Fecha de Aplicacion "+fAplicacion[0]);
	System.out.println("1: Fecha Minima "+fAplicacion[1]);
	System.out.println("2: Fecha Maxima "+fAplicacion[2]);
	
	boolean esConsulta = ( caso == null? false : ( caso.getCasoOperacion(0) == null? false : (  caso.getCasoOperacion(0).getOperacion() == null ? false : ( "cons_rectificacion".equalsIgnoreCase( caso.getCasoOperacion(0).getOperacion().getNombre() ) )  ) )  );
	String operacionActual = ( caso != null? (  caso.getCasoOperacion(0) != null? (  caso.getCasoOperacion(0).getOperacion() != null? caso.getCasoOperacion(0).getOperacion().getNombre() : ""   ) : "" ) : "" ) ;
	
	//VGC20160223 Se agrega para actualizar insertar firmantes.
	String cDocumento = caso.getTipoCaso().getGavetaAsociada();	

	String cAplicaDocto = "No";
	Map<?,?> m = CasoDatoManager.readValuesCasoDato(request, caso.getCasoDato(), true);
	String prefixPath = getServletContext().getRealPath("/WEB-INF/mail-bodies/") + File.separator;
	int id_oper = -1;
	boolean reload = true;
	double valor = 0.00;
	String strValor = "0.00";
	boolean porEXCEL = false; //VARIABLE QUE SIRVE PARA SABER SI SE VA A SUBIR A TRAVES DE EXCEL FMC 30/oct

	empleado.setClaveUsuario(usuario.getLogin());
	empleado = empleadobl.getEmpleado(empleado);
	empleadoarea.setId(empleado.getClaveArea());
	empleadoarea = empleadobl.getEmpleadoArea(empleadoarea);

	if (session.getAttribute("mensaje") != null)
		msg = (String) session.getAttribute("mensaje");

	session.removeAttribute("mensaje");

	Rectificacion rectificacionCapturada = null;
	
	if (session.getAttribute("RECTIFICACION") != null) {
		rectificacionCapturada = (Rectificacion) session.getAttribute("RECTIFICACION");
		reload = false;
		session.removeAttribute("RECTIFICACION");
	}

	final int folio = new Integer(caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1)).intValue();
	
	RectificacionEncabezado rec = rectificacion.getRectificacionEncabezado(folio);
	ArrayList<RectificacionDetalle> rds = rectificacion.getRectificacionDetalle(folio);		
	
	boolean existeDetalle = false; //checamos si ya existe un detalle cargado: si hay lo agregamos al grid en paso 1; sino es paso 1, usamos la vista; si no hay nada, agregamos.

	if(!rds.isEmpty())
	    existeDetalle=true;
	
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper = caso.getCasoOperacion(0).getIdOperacion();

	
	if (request.getParameter("aplicaDocto") != null && request.getParameter("aplicaDocto").equals("Si")) {
		cAplicaDocto = "Si";
	}
	
	String cAutorizaDocto = "No";
	if (request.getParameter("aut") != null && request.getParameter("aut").equals("Si")) {
		cAutorizaDocto = "Si";
	}
	
	boolean captura=true;
	if (request.getParameter("mensajerem") != null && request.getParameter("mensajerem").equals("si")) {
		captura=false;
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
	cUsrLog = usuario.getLogin();
	cGrupoUSR = (usuario.getGrupos() != null && usuario.getGrupos().containsKey("AUTORIZADOR_RECTIFICACION")) ? true : false;
	String sResultadoAut =null;
	
	boolean busqueda = false;
	if(request.getParameter("busqueda")!=null){
		if("SI".equals(request.getParameter("busqueda").toUpperCase()))
		    busqueda = true;
	}
	
	
	mensaje = mensaje.replace("[", "");
	mensaje = mensaje.replace("]", "");
	mensaje = mensaje.replace("'", "");
	mensaje = mensaje.replace(",", "<br>");
	mensaje = mensaje.replace("\n", "<br>");
	mensaje = Util.encodeJS(mensaje);
	
	//String sPrograma = "-1";
	//String sSubPrograma = "-1";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Rectificaci&oacute;n Presupuestaria</title>
<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">

<script type="text/javascript" src="../Generador/js/jquery.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript"	src="../Generador/js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
<script type="text/javascript" src="../Generador/js/crud.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="../js/rectificacionPresupuestal.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="../Generador/js/bootstrap.min.js"></script>

<script type="text/javascript" charset="utf-8">
	var oTable;
	var oTableDD;
	var aTrs;
	var generaRenglon = false;
	var epTemporal="";
	var esConsulta = <%=esConsulta%>;
	var operacionActual = "<%=operacionActual%>";
	var arrCXP = new Array();			
	
	function inicio(recargar){
		
		$("#padreRectificacion").val("");
		$("#eventoDICE").val("");

		if(recargar){
			var msg = $('#mensaje').val();
			if( msg != "" && msg != null ){
				Swal.fire({ icon: 'info',
							text: msg });				
				parent.document.getElementById("pb_send").disabled=true;
			}else{
				$('#mensaje').val("");
			}
			$('#CatMovimientoRectificacion').val("");
			$('#concepto').val("");
			$('#oficioRectif').val("");
			$('#ctr_int').val("");
			$('#folioSICOP').focus();
			if(<%=!porEXCEL%>){
				$('#filtro').dialog('option', 'modal', true).dialog('open');
			}
			$("#tblPagadoFiltrado tbody tr td:eq(0)").click();
			$("#tblPagadoFiltradoDEBE tbody tr td:eq(0)").click();
			//return true;
		} else{
			var msg = $('#mensaje').val();
			if( msg != "" && msg != null ){
				parent.document.getElementById("pb_send").disabled=true;				
				Swal.fire({ icon: 'info',
							text: msg });
				//parent.document.getElementById("pb_save").click();
				$('#tblPagadoFiltrado thead tr td:eq(0)').click();
				$('#tblPagadoFiltradoDEBE thead tr td:eq(0)').click();				
			}else{
				$('#mensaje').val("");
			}
		}
	}

	function permite(elEvento, permitidos) {
		var numeros = "0123456789";
		var caracteres = " abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ.";
		var numeros_caracteres = numeros + caracteres;
		var decimal = numeros + ".";
		var teclas_especiales = [8];
		switch(permitidos) {
			case 'num':
				permitidos = numeros;
				break;
			case 'car':
				permitidos = caracteres;
				break;
			case 'num_car':
				permitidos = numeros_caracteres;
				break;
			case 'dec':
				permitidos = decimal;
				break;
		}
		var evento = elEvento || window.event;
		var codigoCaracter = evento.charCode || evento.keyCode;
		var caracter = String.fromCharCode(codigoCaracter);
		var tecla_especial = false;
		for(var i in teclas_especiales) {
			if(codigoCaracter == teclas_especiales[i]) {
				tecla_especial = true;
				break;
			}
		}
		return permitidos.indexOf(caracter) != -1 || tecla_especial;
	}

	function buscarCLC(){
		validaEsAjena();
		//muestraContrarrecibo();
		
		if($("#esAjena").val() == "0"){				
			$('#tblPagadoFiltrado').dataTable().fnClearTable();
			$('#filtroRectificacion').submit();	
		}else{
			$("#contrarrecibo").val($("#CXP").val());
			$('#tblPagadoFiltrado').dataTable().fnClearTable();
			$('#filtroRectificacion').submit();				
		}
								
	}

	function cambiaEsIP (){
		if ($('#EsIP').attr('checked') )
			$("#cEsIP").val("S");
		else
			$("#cEsIP").val("N");
		
	}

	function fjsRoundNumber(dato,decimales){
		if (dato.toFixed) //if browser supports toFixed() method
			return dato.toFixed(decimales);
		var aux = 0;
		dato = dato + "";
		if(dato.indexOf(".") > -1){
			if(decimales != null &&  decimales > 0)
				aux = 10;
			var i;
			for( i = 0; i<decimales; i++){
				aux = aux * 10;
			}
			return Math.round(dato * aux) / aux;
		}
		return dato;
	}


	function editarMonto(monto){			
		var montoEditable = $(monto).val();
		montoEditable = montoEditable * 1;							
		
		var total = $("#totalDice").val() * 1;				
		total = total - montoEditable;
		total = fjsRoundNumber(total,2);
		$(monto).val("");
		$("#totalDebeDecir").val(total);
		//$("#totalDiceInfo").text($("#totalDice").val());
		//$("#totalDebeInfo").text(fjsRoundNumber($("#totalDebeDecir").val(),2));
	}
	
	function editarMontoDD(monto){			
		var tListado = $("#tblPagadoFiltrado").dataTable().fnGetData();
		for (i=0; i<tListado.length;i++){
			if(tListado[i][0] == monto){
				monto = tListado[i][5];
				i = tListado.length;
			}
		}				
		var total = $("#totalDice").val() * 1;
		
		total = total - monto;
		total = fjsRoundNumber(total,2);
		$(monto).val("");
		
		$("#totalDice").val(total);
		$("#totalDebeDecir").val("0.00");		
		$("#totalDiceValor").val(total);		
		//$("#totalDiceInfo").text(total);
		//$("#totalDebeInfo").text("0.00");
		sumaImporteDiceDebeDecirReal();
	}

	function actualizaTotalDD(valorIngresado){
		var rw = $('#tblPagadoFiltrado').dataTable().fnGetData();
		var suma = 0.0;
		for ( var i = 0; i < rw.length; i++){
			suma += parseFloat( rw[i][6]);
		}
		if(<%=rec==null%>){
			$("#totalDice").val(suma);
			$("#totalDiceValor").val(suma);
		}
		
		var montoDD = $("#totalDebeDecir").val();
		var valorIng = $(valorIngresado).val();
		montoDD = (montoDD * 1) + valorIng;
		montoDD = fjsRoundNumber(montoDD,2);
		$("#totalDebeDecir").val(montoDD);
		//$("#totalDiceInfo").text($("#totalDice").val());
		//$("#totalDebeInfo").text(fjsRoundNumber($("#totalDebeDecir").val(),2));
	}

	

	function ResponsableSiguiente(id_oper){
	//CORRIJO LOS RESPONSABLES PORQUE NO IBAN EN DONDE DEBÍAN IR FMC 29/OCT
	
		if(id_oper==1)
			return "REVISOR_RECTIFICACION";
			parent.document.getElementById("pb_save").disabled=false;

		if(id_oper==2){
			if(document.rechazo.autorizaRein[1].checked)
				return "CAPTURISTA_RECTIFICACION";
			else
				return "AUTORIZADOR_RECTIFICACION";
		}

		if(id_oper==3)
		return "REVISOR_RECTIFICACION";

		if(id_oper==4){
			if(document.rechazo.autorizaRein[1].checked)
				return "REVISOR_RECTIFICACION";
			else
				return "CONSULTA_RECTIFICACION";
		}

		if(id_oper==5){
			if(document.rechazo.autorizaRein[1].checked)
				return "CAPTURISTA_RECTIFICACION";
			else
				return "AUTORIZADOR_RECTIFICACION";
		}

		if(id_oper==6)
			return "AUTORIZADOR_RECTIFICACION";

		if(id_oper==7)
			return "CONSULTA_RECTIFICACION";
		
		//********************************************
	}

	function OperacionSiguiente(id_oper){
	//CORRIJO LAS OPERACIONES PORQUE ERAN UN COPY PASTE DE REINTEGROS Y EN RECTIFICACIONES SE LLAMAN DIFERENTE FMC 29/OCT
		if(id_oper==1)
			return "rev_rectificacion";

		if(id_oper==2){
			if(document.rechazo.autorizaRein[1].checked)
				return "edit_rectificacion";
			else
				return "layout_rectificacion";
		}

		if(id_oper==3){
			return "rev_rectificacion";
		}
		
		if(id_oper==4){
			if(document.rechazo.autorizaRein[1].checked)
				return "rech_rectificacion";
			else
				return "cons_rectificacion";
		}

		if(id_oper==5){
			if(document.rechazo.autorizaRein[1].checked)
				return "edit_rectificacion";
			else
				return "aut_rectificacion";
		}

		if(id_oper==6)
			return "aut_rectificacion";

		if(id_oper==7)
			return "cons_rectificacion";
			
		//*********************************************************************************************
	}

	function onPostDisplay(){
	}

	function onActualizaEstatus(cTipoAutoriza){
		var strAction="rectificacionPresupuestal.jsp?id_oper="+<%=id_oper%>;
	}
		
	function onSubmit(id_oper){
		var p = window.parent;
		var valida_campos = true;
		try{
			//validaciones de la forma
			var msgAlert="";
			if(msgAlert!=""){
				Swal.fire({ icon: 'info',
							text: msgAlert });				
				return false;
			}
			if (id_oper==1){
				var dblTotalDice = $("#totalDice").val() * 1;
				var dblTotalDebeDecir = $("#totalDebeDecir").val() * 1;
				var esIP = $("#cEsIP").val();
				//SAQUE LOS SETEOS DE LAS VARIABLES DEL IF PORQUE SI FALLABA INTENTABA CREAR EL DOCUMENTO CON COSAS VACÍAS Y TRONABA FMC
				p.gestion.setFolio($("#folio").val());
				p.gestion.setFechaDocumento($("#fApl").val());
				p.gestion.setEjercicioFiscal($("#cEjercicio").val());
				p.gestion.setOperador($("#operador").val());
				p.gestion.setConceptoMov("Rectificacion Presupuestal con FOLIO " + $("#folio").val() + ".");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				//p.gestion.setesIP($("#esIP").val());
				//**************************************************
				
				//ESTOS SON PARA REDONDEAR A DOS DECIMALES Y NO META ERRORES
				dblTotalDice=Math.round(dblTotalDice*100)/100 ;
				dblTotalDebeDecir=Math.round(dblTotalDebeDecir*100)/100 ;
				//*********************************************************						
				
				if( dblTotalDice == dblTotalDebeDecir ){					
					if(<%=!porEXCEL%>){
						if($('#concepto').val()=="" || $('#ctr_int').val()=="" || $('#CatMovimientoRectificacion').val()==""){
							Swal.fire({ icon: 'warning',
										text: "Favor de llenar el concepto, control interno y tipo de movimiento." });
							document.getElementById("concepto").focus();
							//return false;
							parent.document.getElementById("pb_save").disabled=false;
						}else{
						
							var concepto = $("#concepto").val();
							var conceptoAux = concepto.replace("\n", "");
							conceptoAux = conceptoAux.replace("\r", " ");
							
							$("#concepto").val(conceptoAux);
							
							if(<%=!existeDetalle%>){// Verificar si ya existe el detalle 
								$("#info").val(obtenValoresGrid());
								//$('#salvarRectificacion').submit();
								$.ajax({
									url: '../gstnmngr/CapturaRectificacionPresupuestaria',
									type: 'post',
									async:false,
									dataType: 'json',
									data : $("#salvarRectificacion").serialize(),
									error : function(data) {
										Swal.fire({ icon: 'error',
													text: "Ocurrio un error en el Insert. ["+data.data_1.result+"]" });																			
									},
									success: function(data){
										var exito = data.success;
										if( "true" == exito)			
											if(<%=captura%>)												
												tipoFirmantes();
									}
								});
							}else{
								if(<%=captura%>){
									tipoFirmantes();
								}
							}
						}
					}else
						parent.document.getElementById("pb_save").disabled=true;
						
				} else{
					Swal.fire({ icon: 'warning',
								text: "Favor de revisar la(s) lineas rectificadas(marcadas con la leyenda \"DEBE DECIR\"). Los montos del dice no coinciden con los montos del debe decir" });					
				}
			}
			
			//SALVA EL MENSAJE DE ERROR Y VALIDA QUE EXISTA EL MENSAJE EN CASO DE NO ACEPTARSE, SI NO PONE NADA EL USUARIO SE LE PIDE QUE 
			//PONGA ALGO, SI DICE QUE ESTA CORRECTO, SELE DA LA OPCIÓN DE AVANZAR EL CASO
			//FMC 31/oct**
			if (id_oper==2 || id_oper==4 || id_oper==5){
	  			if(document.rechazo.autorizaRein[1].checked && $("#motivoRechazo").val()==""){		  			
		  			Swal.fire({ icon: 'warning',
		  						text: "Motivo de rechazo es requerido"});
		  			$("#motivoRechazo").focus();		  			
		  			parent.document.getElementById("pb_send").disabled=true;
		  			parent.document.getElementById("pb_cancel").disabled=true;
		  			return false;
	  			}else if(!(document.rechazo.autorizaRein[0].checked || document.rechazo.autorizaRein[1].checked)){
	  				Swal.fire({ icon: 'warning',
  								text: "FAVOR DE MARCAR SI LOS DATOS SON CORRECTOS O NO"});	  				
	  				parent.document.getElementById("pb_save").disabled=false;
	  				parent.document.getElementById("pb_send").disabled=true;
	  				parent.document.getElementById("pb_cancel").disabled=true;
	  				return false;
	  			}else if($("#fApl").val()=='N/A' || $("#fApl").val()==""){	  				
	  				Swal.fire({ icon: 'warning',
								text: "Favos de seleccionar la fecha de aplicación"});
	  				parent.document.getElementById("pb_save").disabled=false;
	  				parent.document.getElementById("pb_cancel").disabled=false;
	  				return false;
	  			}else{
	  				parent.document.getElementById("pb_send").disabled=false;
	  				parent.document.getElementById("pb_save").disabled=true;
	  				parent.document.getElementById("pb_cancel").disabled=true;
	  				$("#motivoR").val($("#motivoRechazo").val());
	  				$("#mensajeError").val($("#motivoRechazo").val());
	  				p.gestion.setMensaje($("#motivoR").val());
	  			}
  			}
  			
  			if(id_oper==4){//if (id_oper==4 || id_oper==2){
  				parent.document.getElementById("pb_send").disabled=false;
  				parent.document.getElementById("pb_cancel").disabled=true;
  			}
			//************************************
			
			//APLICACION CONTABLE DE APARTADO FMC 2/Nov SE QUITA PORQUE LA ACCION LO TIENE EL RADIOBUTTON
			/*if(id_oper==2){
				if(document.rechazo.autorizaRein[0].checked){
					if(<%=!("true".equals(caso.getCasoDato("APLICADO_CONT").getValor()))%>)
						mostrarDialog();
	  				else
	  					Swal.fire({ icon: 'warning',
									text: "El documento ya se encuentra aplicado."});	  					
				}
			}*/
			//**************************************
			
			//APLICACION CONTABLE DE AUTORIZACION FMC 2/Nov 
			if(id_oper==4){
				if(document.rechazo.autorizaRein[0].checked){
					if(<%=!("true".equals(caso.getCasoDato("AUTORIZADO_CONT").getValor()))%>)
						mostrarDialogAut();
	  				else
						Swal.fire({ icon: 'warning',
									text: "El documento ya se encuentra aplicado."});
				}
			}
			//**************************************
			//LOGICA DEL FLUJO  FMC 31/OCT
			if(id_oper==3){
				parent.document.getElementById("pb_send").disabled=false;
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_cancel").disabled=false;
			}
			
			if(id_oper==5){
				parent.document.getElementById("pb_send").disabled=false;
				parent.document.getElementById("pb_save").disabled=true;
				parent.document.getElementById("pb_cancel").disabled=false;
			}
			
			if(id_oper==6){
				parent.document.getElementById("pb_send").disabled=false;
				parent.document.getElementById("pb_save").disabled=true;
			}
			
			//*********************************
		} catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
	}
	
	function onLoadPlantilla(id_oper){
		//if(id_oper==7){
		$("#divImprimePoliza").show();
		$("#EditaFirmas").css('visibility', 'visible');
		//}else{
		//	$("#divImprimePoliza").hide();
		//	$("#EditaFirmas").css('visibility', 'hidden');
		//}
		
		
		$("#folioSICOP").val('');
		$("#dTipoPago").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)? rectificacionCapturada.getEncabezado().getTipoPago():""%>');
		$("#nFolioSICOP").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSicop():""%>');
		$("#caNoContrarrecibo").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getCaNoContrarrecibo():""%>');
		$("#nFolioSIAFF").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSIAFF():""%>');
			$("#cDefscripcionPoliza").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getcDescripcionPoliza():""%>');
			$("#cEjercicio_C").val('<%=adecProy.obtenEjercicioFiscal()%>');
			
			$("#folioSICOP").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSicop():""%>');
		$("#CXP").val('<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getCaNoContrarrecibo():""%>');
			
			queryFormPost("fExpRead", {async: false });
		queryFormPost("fAplRead", {async: false });
			
			if(<%=rec != null%>){
				$("#CatMovimientoRectificacion").val('<%=rec!=null?rec.getcTipoMovto():""%>');
				$("#concepto").val('<%=rec!=null && rec.getcConceptoRectificacion()!=null?rec.getcConceptoRectificacion():""%>');
				$("#oficioRectif").val('<%=rec!=null &&rec.getOficioRectif()!=null?rec.getOficioRectif():""%>');
				$("#ctr_int").val('<%=rec!=null &&rec.getCtr_int()!=null?rec.getCtr_int():""%>');
				$("#totalDice").val('<%=rec!=null &&rec.getTotalDice()!=null?rec.getTotalDice():""%>');
				$("#totalDebeDecir").val('<%=rec!=null &&rec.getTotalDice()!=null?rec.getTotalDice():""%>');
				$("#dTipoPago").val('<%=rec!=null &&rec.getcTipoRectificacion()!=null?rec.getcTipoRectificacion():""%>');
				$("#nFolioSICOP").val('<%=rec!=null &&rec.getnFolioSicop()!=null?rec.getnFolioSicop():""%>');
				$("#nFolioSIAFF").val('<%=rec!=null &&rec.getnFolioSIAFF()!=null?rec.getnFolioSIAFF():""%>');
				$("#caNoContrarrecibo").val('<%=rec!=null &&rec.getCaNoContrarrecibo()!=null?rec.getCaNoContrarrecibo():""%>');
				$("#cDescripcionPoliza").val('<%=rec!=null &&rec.getcDescripcionPoliza()!=null?rec.getcDescripcionPoliza().replaceAll("\r\n", "\\\\n"):""%>');
				$("#filtroRec").hide();
		}else{
			$("#CatMovimientoRectificacion").val("");
			$("#concepto").val("");
			$("#ctr_int").val("");
			$("#totalDebeDecir").val("0");
		}
		
		
			var p = window.parent;
		if(id_oper==3 || id_oper==5){
			//SE MUESTRA EL MENSAJE EN LA OPERACIÓN DE EDICIÓN QUE MANDO EL REVISOR AL RECHAZAR
			//FMC 31/OCT
			if(p.gestion.getMensaje()!=null && p.gestion.getMensaje()!="")				
				Swal.fire({ icon: 'info',
							text: p.gestion.getMensaje()});
			//*******************************************************					
		}
		
		if(id_oper==2 || id_oper==4){			
			parent.document.getElementById("pb_send").disabled=true;
			parent.document.getElementById("pb_cancel").disabled=false;
		}
		
		if(id_oper==2){
			parent.document.getElementById("pb_save").disabled=true;
			parent.document.getElementById("pb_send").disabled=true;					
			parent.document.getElementById("pb_cancel").disabled=false;
		}
		
		if(id_oper>1){
			parent.document.getElementById("pb_cancel").disabled=true;
		}
		
		if(id_oper == 1){
			validaEsAjena();
			//muestraContrarrecibo();
			
			//ARLA20171018 Se agrega para que se auto cheke cuando es de P
			queryFormPost("tEsIPRead",{async: false });
	
			if($("#PagoEsIP").val() == "4"){
				$("#EsIP").attr("checked", true);
				$("#cEsIP").val("S");
			}else 
				$("#cEsIP").val("N");
		}					
		
		var rw = $('#tblPagadoFiltrado').dataTable().fnGetData();
		var suma = 0.0;
		for ( var i = 0; i < rw.length; i++){
			suma += parseFloat( rw[i][5]);
		}
		suma = (suma * 100)/100;
		
		if(<%=rec==null%>){
			$("#totalDice").val(suma);
			$("#totalDiceValor").val(suma);
		}
		
		//alert ("suma " + suma);
	}
	
	//FALTABA LA FUNCIÓN DE ONPOSTSUBMIT, NO AVANZABA EL CASO
	//FMC 30/OCT
	function onPostSubmit(id_oper){
		return true;
	}
	//********************************************************
	
	function openExcel(){
		var param = "&rptExcel="+ document.getElementById("hidEXCEL").value;
		/*
		Esteban Badillo. Fecha: 11/Sep/2009. Descripcion: Se agrega el paso del parametro "orden" para realizar la impresion
		condicional de columnas en la exportaciÃ³n del reporte a Excel.
		*/
		var url = "../reportes/reporte_export.jsp?id=9&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
		var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
	}
	
	function obtenValoresGrid(){
		var info="";
		$('#tblPagadoFiltradoDEBE tbody tr').each(function(idx, elm){
			info += $(this).find('td:eq(0)').html();
			
			if($(this).find('td:eq(3)').children().val()==undefined)
				info += "!" + $(this).find('td:eq(2)').html();	
			else
				info += "!" + $(this).find('td:eq(2)').children().val();
			
			if($(this).find('td:eq(3)').children().val()==undefined)
				info += "!" + $(this).find('td:eq(3)').html();	
			else
				info += "!" + $(this).find('td:eq(2)').children().val();
			
			info += "!" + $(this).find('td:eq(4)').html();
			
				if($(this).find('td:eq(5)').children().val()==undefined){
					var sincomas = $(this).find('td:eq(5)').html();
					//info += "!" + sincomas.replace(",","") + "!";
					info += "!" + sincomas.replace(",","");
				}
				else{
					var sincomas = $(this).find('td:eq(5)').children().val();
					//info += "!" + sincomas.replace(",","") + "!";
					info += "!" + sincomas.replace(",","");
				}
				
			//if($(this).find('td:eq(8)').children().val()==undefined)
				info += "!" + $(this).find('td:eq(8)').html();	
			//else
				//info += "!" + $(this).find('td:eq(8)').children().val();
				
			//if($(this).find('td:eq(9)').children().val()==undefined)
				info += "!" + $(this).find('td:eq(9)').html();	
			//else
				//info += "!" + $(this).find('td:eq(9)').children().val();
				info += "!" + $(this).find('td:eq(1)').html() + "!";
		});
		return info;
	}
	
	//FUNCIONALIDAD PARA SUBIR EL EXCEL FMC 29/oct
	function fnImportarExcel() {
		var msgAlert = "";
		if ($("#importExcel").val() == "") {
			msgAlert += "El archivo Excel es requerido";
		}
		if (msgAlert != "") {
			Swal.fire({ icon: 'info',
						text: msgAlert});			
			return false;
		} else {
			guardaExp();
			$.blockUI( {
				message : "Procesando espere ......"
			});
			document.upExcel.submit();
			return true;
		}
	}
	
	function guardaExp(){
  		parent.document.getElementById("pb_save").disabled=false;
  		parent.document.getElementById("pb_save").click();
  		parent.document.getElementById("pb_save").disabled=true;
	}
	
	function porExcel(){
		if($("#subeExcel").attr('checked')){
			parent.document.getElementById("pb_save").click();
		}
	}		
	//********************************************************************
	
	//FUNCIONES PARA MOSTRAR LOS DIALOGOS QUE TIENEN LA OPCION DE APLICAR CONTABLEMENTE FMC 2/NOV
	function mostrarDialog(){
		$('#dialog').dialog('option', 'modal', true).dialog('open');	 
	 }
	 
	 function mostrarDialogAut(){
		$('#dialogAut').dialog('option', 'modal', true).dialog('open');	 
	 }
	 //***************************************************************
	    
	function aplicaCont(){
		$("#dialog").dialog("close");				
		Swal.fire({
			text: "Enviara el documento para aplicar el apartado persupuestal. \n ¿Desea continuar?",
			icon: "warning",
			showCancelButton: true,
		  	confirmButtonColor: "#7066E0",
		  	cancelButtonColor: "#e6e6e6",
		  	confirmButtonText: "Aceptar",
		  	cancelButtonText: "Cancelar"
		}).then((result) => {
			if(result.isConfirmed){
				var strAction="../gstnmngr/RectificacionServlet";
		   		$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{accion:1},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');						
		   				$.unblockUI();
					},
					error: function (par) {alert('<%=mensaje%>');}
				});	
			} else
				return false;
		})
	 }
	 
	 function aplicaContAut(){
		$("#dialogAut").dialog("close");
		var fApl = $("#fApl").val();
		Swal.fire({		
			text: "Enviara el documento a autorizar contablemente. \n \n ¿Desea continuar?",
			icon: "warning",
			showCancelButton: true,
		  	confirmButtonColor: "#7066E0",
		  	cancelButtonColor: "#e6e6e6",
		  	confirmButtonText: "Aceptar",
		  	cancelButtonText: "Cancelar"
		}).then((result) => {
			if(result.isConfirmed){
				var strAction="../gstnmngr/RectificacionServlet";
			   	$.blockUI({message: "Procesando espere ......"});
			   	$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{accion:2,fApl:fApl},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   				$.unblockUI();
					},
					error: function (par) {alert(par);}
				});	
			} else
				return false;
		})			  
	}
	 
	function cancelarDoc(){
		Swal.fire({
			text: "Enviara el documento a cancelar. \n \n ¿Desea continuar? ,",
			icon: "warning",
			showCancelButton: true,
		  	confirmButtonColor: "#7066E0",
		  	cancelButtonColor: "#e6e6e6",
		  	confirmButtonText: "Aceptar",
		  	cancelButtonText: "Cancelar"
		}).then((result) => {
			if(result.isConfirmed){
				var strAction="../gstnmngr/RectificacionServlet";
			   	$.blockUI({message: "Procesando espere ......"});
			   	$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{accion:3},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   				$.unblockUI();
					},
					error: function (par) {alert(par);}
				});
			} else 
				return false;
		})				
 	}
	 
	$(document).ready(function(){
						
		setFechas();
		var p = window.parent;
		$("input.AyudaSyC").subIniciaDlg();
		//$('#totalDebeDecir').val("0.00");
		//$('#totalDice').val("0.00");
		$("#dlg-ProgramaSubPrograma").hide();		
		$("#dlg-ProgramaSubProgramaDICE").hide();				
		$("#btnAgregar").button();
		$("#validar").button();
		$("#ImportarExcel").button();
		$("#Cancelar").button();
		$("#Exportar").button();
		$("#aplicarContable").button();
		$("#aplicarContableAut").button();
		//muestraContrarrecibo();	
		$("#lblContrarrecibo").hide();
		$("#cboContrarrecibo").hide();
		$("#btnAgregar").hide();
		
		//SE HABILITAN LOS DIALOGOS PARA LAS APLICACIONES, PERO SE DEJAN CERRADOS
		$(function() {		
      		$('#dialog').dialog({
      			autoOpen: false,
      			width: 500,
      			heigth: 1000
    		});
    	});
			    
		$(function() {		
			$('#dialogAut').dialog({
				autoOpen: false,
				width: 900,
				heigth: 2900
			});
		});
		
		$(function() {		
      		$('#dialogMensaje').dialog({
      			autoOpen: false,
      			width: 900,
      			heigth: 2900
    		});
    	});
		
		$('#dialogMotor').dialog({
	      	autoOpen: false,
  			width: 900,
 			heigth: 2900
    	});		    	

		$("#avanza").click(function () {
			$('input:radio[name=esquemaDocumento]:nth(0)').attr('checked',true);
			//document.location.href='../gstnmngr/RespuestaCompromisosSICOP?filtro=TODOS';
		});

		$("#regresa").click(function () {
			$('input:radio[name=esquemaDocumento]:nth(1)').attr('checked',true);
			//document.location.href='../gstnmngr/RespuestaCompromisosSICOP?filtro=COMPROMISOS';
		});

		$(function() {
			$('#filtro').dialog({
				//agregué el fin con porEXCEL para saber si eligieron excel no mostrarles el buscador FMC 30/oct
				autoOpen: <%if (reload && !porEXCEL) {%>true<%} else {%>false<%}%>,
				width: 900,
				heigth: 2900
			});
		});

		
		querySelectPost("TipoPresupuestoRead","origPresupuesto");

		$('#CatMovimientoRectificacion').change(function() {
			querySelectPost("TipoPresupuestoRead","origPresupuesto");
		});

		$("#cDescripcionPoliza").val($("#cDescripcionPolizaH").val());
		if(<%=rec!=null%>){
		$("#totalDice").val($("#totalDICEH").val());
		$("#totalDiceValor").val($("#totalDICEV").val());
		}

		if(<%=(caso.getIdGabinete()!=-1 && !busqueda) || existeDetalle || esConsulta %>){
				
				oTableDD = $("#tblPagadoFiltradoDEBE").dataTable({
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bPaginate" : false,
					"bAutoWidth" : true,
					"bScrollCollapse" : true,
					"sScrollXInner": "100%", 
					"sScrollX": "100%",
					"sPaginationType" : "full_numbers",
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide": true,	
					oLanguage : {
						sProcessing : "Procesando...",
						sLengthMenu : "Mostrar _MENU_ registros",
						sZeroRecords : "No hay registros a mostrar",
						sEmptyTable : "No hay datos en la tabla",
						sLoadingRecords : "Cargando...",
						sInfo : "Registros _START_ al _END_ de _TOTAL_",
						sInfoEmpty : "Registro 0 al 0 de 0",
						sInfoFiltered : "(filtado de _MAX_ registros)",
						sInfoPostFix : "",
						sInfoThousands : ",",
						sSearch : "Buscar:",
						oPaginate : {
							sFirst : "Primero",
							sPrevious : "Ant.",
							sNext : "Sigte.",
							sLast : "&Uacute;ltimo"
						}
					},
				sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vtRectificacionDet&qw=nFolioRectificacion="+<%=caso.getFolio().substring(caso.getFolio().lastIndexOf('-') + 1)%>,
				aoColumns: [
					{ sName: "nDocRenglon",
					  "bVisible": true	},
					{ sName: "caNoContrarrecibo",
					  "bVisible": true },
					{ sName: "cMes",
					  "bVisible": true },
					{ sName: "EP",
					  "bVisible": true},
					{ sName: "cEvento",
					  "bVisible": true},
					{ sName: "mImporte",
					  "bVisible": true},
					{ sName: "remanente",
					  "bVisible": false},
					{ sName: "descartar",
					  "bVisible": false},
					{ sName: "epPadre",
					  "bVisible": false},
					{ sName: "nidprograma",
					  "bVisible": true},
					{ sName: "cSubPrograma",
					  "bVisible": true}
				],
			});
			
				$("#demo_jui").hide();
		}else{
				oTable = $("#tblPagadoFiltrado").dataTable({
					"bLengthChange" : true,
					"bFilter" : true,
					"bSort" : true,
					"bInfo" : true,
					"bPaginate" : false,
					"bAutoWidth" : true,
					"bScrollCollapse" : true,
					"sScrollXInner": "100%", 
					"sScrollX": "100%",
					"sPaginationType" : "full_numbers",
					"bJQueryUI" : true,
					"bRetrive" : true,
					"bDestroy" : true,
					"bServerSide": true,
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
					oPaginate: { sFirst: "Primero", sPrevious: "Ant.", sNext: "Sigte.", sLast: "&Uacute;ltimo" }
				},
				"bServerSide": false,
				bProcessing: true,
				bSort : false,
				bJQueryUI: true,
				bDestroy : true,
				//bScrollCollapse : true,
				"aoColumnDefs": [
				      				{ "bVisible": false, "aTargets": [ 7,8 ] },
				      				{ "sClass": "centro", "aTargets": [ 0,1,2,3,4,9,10 ] },
				      				{ "sClass": "money", "aTargets": [ 5,6 ] }
				    			]  
				
			});
				
			oTableDD = $("#tblPagadoFiltradoDEBE").dataTable({
				"bLengthChange" : true,
				"bFilter" : true,
				"bSort" : true,
				"bInfo" : true,
				"bPaginate" : false,
				"bAutoWidth" : true,
				"bScrollCollapse" : true,
				"sScrollXInner": "100%", 
				"sScrollX": "100%",
				"sPaginationType" : "full_numbers",
				"bJQueryUI" : true,
				"bRetrive" : true,
				"bDestroy" : true,
				"bServerSide": true,
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
				oPaginate: { sFirst: "Primero", sPrevious: "Ant.", sNext: "Sigte.", sLast: "&Uacute;ltimo" }
			},
			"bServerSide": false,
			bProcessing: true,
			bSort : false,
			bJQueryUI: true,
			bDestroy : true,
			//bScrollCollapse : true,
			"aoColumnDefs": [
			      				{ "bVisible": false, "aTargets": [ 8 ] },
			      				{ "sClass": "centro", "aTargets": [ 0,1,2,3,4,7,9,10 ] },
			      				{ "sClass": "money", "aTargets": [ 5,6 ] }
			    			]  
			
			});
		}
		
		$("#tblPagadoFiltrado tbody").click(function(event) {
			
			$(oTable.fnSettings().aoData).each(
				function (){
					$(this.nTr).removeClass('row_selected');
				});
				
				$(event.target.parentNode).addClass('row_selected');
			});
		

		$("#tblPagadoFiltrado tbody").dblclick( function( e ) {
			$(oTable.fnSettings().aoData).each(
				function (){
					$(this.nTr).removeClass('row_selected');
			});
				
			$(e.target.parentNode).addClass('row_selected');
			
			tblPagoDblClick(e);
		});
		
		$("#tblPagadoFiltradoDEBE tbody").dblclick( function( e ) {
			$(oTableDD.fnSettings().aoData).each(
				function (){
					$(this.nTr).removeClass('row_selected');
			});
				
			$(e.target.parentNode).addClass('row_selected');
			
			tblPagoDDDblClick(e);
		});
		
		if(<%=id_oper%>==2){
				$('#autorizaRein').click(function(){
					if(<%=!("true".equals(caso.getCasoDato("APLICADO_CONT").getValor())) && caso.getCasoDato("APLICADO_CONT").getValor()!=null%>){
 					mostrarDialog();
 					$("#FECHA_APLICACION_CONTABLE").val($("#fApl").val());
 					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
					}else
						Swal.fire({ icon: 'warning',
									text: "El documento ya se encuentra aplicado contablemente"});						
				});
			}
		$('input').each(function() {
						    var readonly = $(this).attr("readonly");
						    if(readonly && readonly.toLowerCase()!=='false') { 
						        $(this).addClass("notEditable");
						    }
						});
						
		$("#dlg-ProgramaSubPrograma").hide();
		$("#dlg-ProgramaSubProgramaDICE").hide();		
		creaDialogoEditar();				
		
		querySelectPost("catTipoSuplenciaRead", "tipoSuplencia", {async : false});
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo", {async : false});
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBoUpdate", {async : false});
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaUpdate", {async : false});
		
		$("#dialog-firmantes").dialog({
			autoOpen : false,
			height : 490,
			width : 480,
			modal : true,
			buttons : {
				"Aceptar" : function() {
					if( $("#cNombreVoBo").val() == "" ) {
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Vº Bº"});						
						$("#cNombreVoBo").focus();
						return;
					} else if( $("#cPuestoVoBo").val() == "" ) {
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Puesto en Datos Vº Bº"});	
						$("#cPuestoVoBo").focus();
						return;
					}
	
					if( $("#cNombreAut").val() == "" ) {
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Autorizar"});						
						$("#cNombreAut").focus();						
						return;
					} else if( $("#cPuestoAut").val() == "" ) {
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Puesto en Datos Autorizar"});	
						$("#cPuestoAut").focus();						
						return;
					}

					$("#cNombreVo").val($("#cNombreVoBo").val());
					$("#cPaternoVo").val($("#cPaternoVoBo").val());
					$("#cMaternoVo").val($("#cMaternoVoBo").val());
					$("#cPuestoVo").val($("#cPuestoVoBo").val());
					$("#cEmpleadoVo").val($("#cboVoBo").val());
	
					$("#cNombreA").val($("#cNombreAut").val());
					$("#cPaternoA").val($("#cPaternoAut").val());
					$("#cMaternoA").val($("#cMaternoAut").val());
					$("#cPuestoA").val($("#cPuestoAut").val());
					$("#cEmpleadoA").val($("#cboAutoriza").val());
	
					$("#cNombreE").val($("#cNombreEla").val());
					$("#cPaternoE").val($("#cPaternoEla").val());
					$("#cMaternoE").val($("#cMaternoEla").val());
					$("#cPuestoE").val($("#cPuestoEla").val());
	
					$("#firmanteVoBo").val($("#cNombreVoBo").val() + " " + $("#cPaternoVoBo").val() + " " + $("#cMaternoVoBo").val());
					$("#firmanteAut").val($("#cNombreAut").val() + " " + $("#cPaternoAut").val() + " " + $("#cMaternoAut").val());
					$("#firmanteEla").val($("#cNombreEla").val() + " " + $("#cPaternoEla").val() + " " + $("#cMaternoEla").val());
	
					var msn = "No Se Guardo Correctamente Informacion de Firmantes";	
												
					try{
						queryFormPost({
							queryName:"tRectificacionEncabezadoFirmante_Update", 
							async : false, 
							callback:function(){
								$("#cNombreVoBo").val("");
								$("#cPaternoVoBo").val("");
								$("#cMaternoVoBo").val("");
								$("#cPuestoVoBo").val("");
								$("#cNombreAut").val("");
								$("#cPaternoAut").val("");
								$("#cMaternoAut").val("");
								$("#cPuestoAut").val("");;
								cmdImprimir("RECTIFICACION");
								
								if (<%=id_oper%>==1){
									parent.document.getElementById("pb_save").disabled=true;
									parent.document.getElementById("pb_send").disabled=false;
								}
							}
						});
					}catch(e){
						Swal.fire({ icon: 'error',
									text: "No se pudo actualizar los firmantes, intente mas tarde."});							
					}
	
					if( $("#oficioDelegatorio").prop("checked") ) {
	
						if( $("#cFolioOficio").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar el folio del Oficio."});	
							$("#cFolioOficio").focus(); 
							return;
						} else if( $("#dFechaOficio").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar la fecha del Oficio."});	
							$("#dFechaOficio").focus(); 
							return;
						} else if( $("#cNombreTitular").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar Nombre del Titular."});	
							$("#cNombreTitular").focus();							
							return;
						} else if( $("#cPuestoTitular").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar Puesto del Titular."});	
							$("#cPuestoTitular").focus();							
							return;
						}
	
						$("#cFolioOficioAux").val($("#cFolioOficio").val());
						$("#dFechaOficioAux").val($("#dFechaOficio").val());
						$("#cNombreTitularAux").val($("#cNombreTitular").val());
						$("#cApellidoPaternoTitularAux").val($("#cPaternoTitular").val());
						$("#cApellidoMaternoTitularAux").val($("#cMaternoTitular").val());
						$("#cPuestoTitularAux").val($("#cPuestoTitular").val());
						$("#tipoSuplenciaAux").val($("#tipoSuplencia").val());
						$("#numeroEmpleadoAutoriza").val($("#cboSuplenteAut").val())
	
						if( $("#firmanteOficioExiste").val() == "Existe" ) {
							queryFormPost({
								queryName : "tPagoFirmanteDelagatorioUpdate",
								async : false,
								callback : function() {
									msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
								}
							});
						} else {
	
							queryFormPost({
								queryName : "tPagoFirmanteDelagatorioCreate",
								async : false,
								callback : function() {
									msn = "Firmantes Oficio Delegatorio guardado correctamente.";
								}
							});
						}
	
						Swal.fire({ icon: 'info',
									text: msn});	
					}
	
					if( $("#oficioDeleVoBo").prop("checked") ) {
	
						if( $("#cFolioOficioVoBo").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar el folio de Oficio."});	
							$("#cFolioOficioVoBo").focus();
							return;
						} else if( $("#dFechaOficioVoBo").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar la fecha del Oficio."});	
							$("#dFechaOficioVoBo").focus();							 
							return;
						} else if( $("#cNombreTitularVoBo").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar Nombre del Titular."});	
							$("#cNombreTitularVoBo").focus();							
							return;
						} else if( $("#cPuestoTitularVoBo").val() == "" ) {
							Swal.fire({ icon: 'warning',
										text: "Falta Ingresar Puesto del Titular."});	
							$("#cPuestoTitularVoBo").focus();							
							return;
						}
	
						$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBo").val());
						$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBo").val());
						$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBo").val());
						$("#cApellidoPaternoTitularVoBoAux").val($("#cPaternoTitularVoBo").val());
						$("#cApellidoMaternoTitularVoBoAux").val($("#cMaternoTitularVoBo").val());
						$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBo").val());
						$("#tipoSuplenciaVoBoAux").val($("#tipoSuplenciaVoBo").val());
						$("#numeroEmpleadoVoBo").val($("#cboSuplenteVoBo").val());
	
						if( $("#firmanteOficioVoBoExiste").val() == "Existe" ) {
							queryFormPost({
								queryName : "tPagoFirmanteDelegatorioVoBoUpdate",
								async : false,
								callback : function() {
									msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
								}
							});
						} else {
	
							queryFormPost({
								queryName : "tPagoFirmanteDelegatorioVoBoCreate",
								async : false,
								callback : function() {
									msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
								}
							});
	
						}
						
						Swal.fire({ icon: 'info',
									text: msn});						
					}							
					$(this).dialog("close");
				},
	
				"Cancelar" : function() {								
					$(this).dialog("close");			
				}
			},
			close : function() {
				if( parent.document.getElementById("pb_save") )
					parent.document.getElementById("pb_save").disabled = false;			
			},
			open : function() {
				queryFormPost({
					queryName : "tPagoFirmanteDelagatorioRead",
					async : false,
					callback : function() {
						if( $("#firmanteOficioExiste").val() == "Existe" ) {
							$("#cboSuplenteAut").change();
						}
	
					}
				});
	
				queryFormPost({
					queryName : "tPagoFirmanteDelegatorioVoBoRead",
					async : false,
					callback : function() {
						$("#cboSuplenteVoBo").change();
					}
				});			
	
				if( $("#firmanteOficioVoBoExiste").val() == "Existe" ) {
					$("#oficioDeleVoBo").attr("checked", "checked");
					showDivOficioVoBo(false);
				}
	
				if( $("#firmanteOficioExiste").val() == "Existe" ) {
					$("#oficioDelegatorio").attr("checked", "checked");
					showDivOficio(false);
				}
			}
		});
		
	});
	
	function setFechas(){
		$("#fApl").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true,
			minDate:new Date(<%=fAplicacion[1]%>),
			maxDate:new Date(<%=fAplicacion[2]%>)
		});	
		$("#dFechaOficio").datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});			
	}

	function numeroDices(){
		var cuentaDices=0;
		var info = obtenValoresGrid();
		var count = info.match(/DICE/g);  
		return count.length;
	}
	
	function fnExportaSicop() {
		$("#info").val(obtenValoresGrid());
		$.blockUI( {
			message : "Procesando espere ......"
		});
		document.ExportaSicop.submit();
		$.unblockUI();
		$("#Exportar").attr('disabled','disabled');
		return true;
	}
	
	function cmdImprimir(elFormato){
		window.open(
			"../admin/SeguridadCatalogos?"
			+ "catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=PolizaRectificacion.jasper"
			+ "&whereFolio= '" + <%=folio %> +"'"
			+ "&whereTipo= '" + elFormato+"'", 			
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
		
	function mostrarProgSubProg(){
	
		queryFormPost("esBeneficiarioFIBBanorteRead", {async : false});
		if($("#esFIBBanorte").val() == "SI"){
			//limpiarSelect();
			$("#dlg-ProgramaSubProgramaDICE").show();
			$("#dlg-ProgramaSubPrograma").show();
		}
	}
	
	function limpiarSelect() {
	    //$("#cboPrograma").val($("#cboPrograma > option:first").val());
	    //$("#cboSubPrograma").val($("#cboSubPrograma > option:first").val());
	    if($("#esFIBBanorte").val() == "SI"){
			var cPrograma = $("#EPDebeDecir").val();
			cPrograma = cPrograma.substring(56, 59);
			$("#cPrograma").val(cPrograma);
			querySelectPost( "catalogoFideicomisoRead", "cboPrograma", {async : false} );
			cambiaPrograma();
		}
	}
	
	function muestraContrarrecibo(){
		validaEsAjena();
		
		if($("#esAjena").val() == "1"){
			querySelectPost( "contrarrecibosOperAjenasRead", "cboContrarrecibo", {async : false} );
			$("#lblContrarrecibo").show();
			$("#cboContrarrecibo").show();
			$("#btnAgregar").show();
			cargaDetCXP();
			$('#tblPagadoFiltrado').dataTable().fnClearTable();						
		}else{
			$("#lblContrarrecibo").hide();
			$("#cboContrarrecibo").hide();
			$("#btnAgregar").hide();
		}
	}
	
	function cargaDetCXP(){
		arrCXP = null;
		var oTable = $('#tblPagadoFiltrado').dataTable();
		var aData = oTable.fnGetData();
		arrCXP = aData;
	}
	
	function validaEsAjena(){
		queryFormPost("esRectificacionOperAjenaRead", {async : false});
	}
	
	function agregaDetContrarrecibo(){			
		$("#contrarrecibo").val(document.getElementById("cboContrarrecibo").value);
		cargaDetalleContrarrecibo();					
	}
	
	function cargaDetalleContrarrecibo(){	
		var oTable = $('#tblPagadoFiltrado').dataTable();
		var aData = oTable.fnGetData();
		var iRow = aData.length + 1;		
		var contrarrecibo = "";
		
		var totalDice = parseFloat($("#totalDice").val());
				
		contrarrecibo = $("#contrarrecibo").val();
		contrarrecibo =  myTrim(contrarrecibo);		
		
		for (i=0; i < arrCXP.length; i++){
			var sol = "";			
			sol = arrCXP[i][1];
			
			if(contrarrecibo == sol){
				var mes = "", epdice = "", eventodice = "", montoDice = "", remanente = "", cmdBorrar = "", epPadre = "", nidprograma = "", SubPrograma = "";
				
				mes = arrCXP[i][2], epdice = arrCXP[i][3], eventodice = arrCXP[i][4], montoDice = arrCXP[i][5], remanente = arrCXP[i][6], epPadre = arrCXP[i][8], nidprograma = "-1", SubPrograma = "-1";
				
				cmdBorrar = "<img src=\"../imagenes/cancelar.gif\" width=\"25\" height=\"21\" alt=\"Descartar Renglon\" onClick=\"descartar('"
				+ epdice + "','" + mes + "','" + eventodice + "'" + "," + iRow + ");\" />";				
				
				agregarRowDet(iRow, sol, mes, epdice, eventodice, montoDice, remanente, cmdBorrar, epPadre, nidprograma, SubPrograma);
				
				iRow++;
				
				totalDice = totalDice + parseFloat(montoDice);				
			}			
		}
		
		$("#totalDice").val(totalDice);	
		$("#totalDebeDecir").val(totalDice);	
	}
	
	function agregarRowDet(iRow, solicitud, mes, epdice, eventodice, montoDice, remanente, cmdBorrar, epPadre, nidprograma, SubPrograma){
		$('#tblPagadoFiltrado').dataTable().fnAddData( [ iRow, solicitud, mes, epdice, eventodice, montoDice, remanente, cmdBorrar, epPadre, nidprograma, SubPrograma ]);
	}	
	
	function myTrim(x) {
	    return x.replace(/^\s+|\s+$/gm,'');
	}	
</script>
</head>

<br/>

<body id="dt_example" class="ex_highlight" onLoad="inicio(<%=reload%>);">
	<div id="container" class="container" style="width: 100%">
		<div class="card-header"> <h3> Rectificaci&oacute;n Presupuestaria </h3> </div>
		<hr class="mt-3">
		
		<div id="dialogMensaje" title="Mensajes" class="container">

			<%
				if (mensaje != null && !"".equals(mensaje)) {
			%>
			<div style="border-style: double;">
				<%=mensaje%>
			</div>

			<%
				}
			%>
		</div>
		
		<div id="filtroRec" title="Filtro por criterios de la CLC" class="container" style="width: 100%">
			<form id="filtroRectificacion" name="filtroRectificacion" method="POST" action="../gstnmngr/RectificacionPresupuestaria">
				<!-- VGC20160223 Se agrega para actualizar insertar firmantes. -->
				<input type="hidden" id="cDocumento" name="cDocumento" value="<%=cDocumento%>">
				<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>">
				<input type="hidden" id="existeFirmantes" name="existeFirmantes" value="">
				
				<input type="hidden" id="cEjercicio" name="cEjercicio" value="<%=aEjercicioFiscal%>"> 
				<input type="hidden" id="cRamo" name="cRamo" value="16"> 
				<input type="hidden" id="cUnidad" name="cUnidad" value="RHQ"> 
				<input type="hidden" id="operador" name="operador" value="<%=caso.getCasoOperacion(0) == null ? "caso operacion nulo" : caso.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" id="fDocumento" name="fDocumento" value="<%=today%>" />
				<input type="hidden" id="esAjena" name="esAjena" value="0">
				<input type="hidden" id="contrarrecibo" name="contrarrecibo" value="">
				<input type="hidden" id="PagoEsIP" name="PagoEsIP" value="">
				<input type="hidden" id="EPDice1" name="EPDice1" value="">
				<input type="hidden" id="EPDebeDecir1" name="EPDebeDecir1" value="">
								
				<%
					if (!porEXCEL && id_oper == 1) {
				%>
				<div class="row">					
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
						<label for="folioSICOP" class="form-label"> Folio CLC: </label>
						<input type="text" id="folioSICOP" name="folioSICOP" class="form-control form-control-sm" onkeypress="return permite(event, 'num');" />											
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<label for="CXP" class="form-label"> Cuenta por pagar: </label>
						<input type="text" id="CXP" name="CXP" class="form-control form-control-sm" />						
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
						<input type="button" id="validar" name="buscar" value="Buscar" onClick="buscarCLC();" class="btn btn-secondary">
					</div>
				</div>
				
				<br/>
		
				<%
					}
				%>
			</form>
			<!-- SE AGREGA LA FORMA PARA SUBIR EL EXCEL FMC 29/oct -->
			<%
				if (id_oper == 1 && porEXCEL && rec == null) {
			%>
			<form id="upExcel" name="upExcel" action="../caso/firmardoc?carpeta=1" enctype="multipart/form-data" method="post">
				<input type="file" id="importExcel" name="importExcel" size="32" value=""></input> 
				<input type="button" id="ImportarExcel" name="ImportarExcel" value="Subir Excel" onclick="fnImportarExcel();" class="btnInterfaceBG"></input>
			</form>
			<%
				}
			%>
		</div>
		<%
			if (id_oper == 2 || id_oper == 4 || id_oper == 5) {
		%>
		<div class="container" style="width: 100%">
			<form id="rechazo" name="rechazo">
				<div class="row">
					<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					</div>
					<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						<label class="form-label">Datos Correctos:</label>	
						<div class="form-check">								
							<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value = "0"/>
							<label for="autorizaRein" class="form-check-label">Si</label>
						</div>
						<div class="form-check">
							<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value = "0"/>								
							<label for="patrimonial" class="form-check-label">No</label>
						</div>							
					</div>
					<div class="col-12 col-lg-5 col-md-5 col-sm-12">									
						<label for="motivoRechazo" class="form-label">Motivo:</label>				
						<textarea id="motivoRechazo" name="motivoRechazo" class="form-control form-control-sm" rows="2" cols="50" onkeypress="return event.keyCode!=13"></textarea>			
						<input type="hidden" id="motivoR" name="motivoR" /> 
					</div>
				</div>
			</form>
		</div>
		
		<br/>
		
		<%
			}
		%>
		<%
			if (id_oper == 6) {
		%>
		<div class="container">
			<form id="ExportaSicop" name="ExportaSicop" action="../gstnmngr/RectificacionesSicop" method="POST">
				<input type="hidden" name="info" id="info" /> 
				<div class="row">
					<div class="col-12 col-lg-5 col-md-5 col-sm-12">
						<input type="button" id="Exportar" value="Exportar SICOP" onclick="fnExportaSicop()" class="btn btn-secondary btn-sm"></input>
					</div>
				</div>
			</form>
		</div>
		
		<br/>
		
		<%
			}
		%>
		<!-- ----------------------------------------------- -->

		<div id="container2" class="container" style="width: 100%">
			<form id="salvarRectificacion" name="salvarRectificacion" method="POST" action="../gstnmngr/CapturaRectificacionPresupuestaria">
				<input type="hidden" id="mensaje" name="mensaje" value="<%=msg%>" />
				<input type="hidden" id="cEjercicio_C" name="cEjercicio_C" value="<%=adecProy.obtenEjercicioFiscal()%>" /> 
				<input type="hidden" id="cRamo_C" name="cRamo_C" value="<%=usuario.getU_Ramo() == null ? "" : usuario.getU_Ramo()%>" />
				<input type="hidden" id="cUnidad_C" name="cUnidad_C" value="<%=usuario.getU_UR() == null ? "" : usuario.getU_UR()%>" />
				<input type="hidden" id="operador_C" name="operador_C"  value="<%=caso.getCasoOperacion(0) == null ? "caso operacion nulo" : caso.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" id="u_login_C" name="u_login_C" value="<%=usuario.getLogin() == null ? "usuario nulo" : usuario .getLogin()%>" />
				<input type="hidden" id="cCentroContable_C" name="cCentroContable_C" value="<%=cCentroContable == null ? "centro contable nulo" : cCentroContable%>" />
				<input type="hidden" id="ID_Caso_C" name="ID_Caso_C" value="<%=caso.getIdCaso()%>" /> 
				<input type="hidden" id="cMes_C" name="cMes_C" value="10" /> 
				<input type="hidden" id="cDescripcionPolizaH" name="cDescripcionPolizaH" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null) ? rectificacionCapturada.getEncabezado().getcDescripcionPoliza() : ""%>" />
				<input type="hidden" id="padreRectificacion" name="padreRectificacion" /> 
				<input type="hidden" id="epGrid" name="epGrid" /> 
				<input type="hidden" id="epConResto" name="epConResto" /> 
				<input type="hidden" id="eventoDICE" name="eventoDICE" /> 
				<input type="hidden" id="eventoDEBE_DECIR" name="eventoDEBE_DECIR" /> 
				<input type="hidden" name="info" id="info" /> 
				<input type="hidden" id="mesTemporal" name="mesTemporal" />
				<input type="hidden" name="cEsIP" id="cEsIP" value="N" >
				<input type="hidden" name="CxP" id="CxP" value="" >
				<input type="hidden" name="dTipoPagoOA" id="dTipoPagoOA" value="" >				
				<!-- Este sirve cuando se calcula la EP para ponerle su mes original y no el calculado -->

				<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
				<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
				<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
				<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
				<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
				<input type="hidden" id="cNombreA" name="cNombreA" size=40 >
				<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 >
				<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
				<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
				<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
				<input type="hidden" id="folioRectif" name="folioRectif" size=40 value="<%=folio%>">
				<input type="hidden" id="existeFirmante" name="existeFirmante" size=40 value="NOEXISTE"> 
				
				<!-- hidden para la captura de Programa -- SubPrograma cuando el beneficiario es FIB Banorte 'BMN930209927'-->
				<input type="hidden" id="esFIBBanorte" name="esFIBBanorte" value="" />
				<input type="hidden" id="RFCFIBBanorte" name="RFCFIBBanorte" value="BMN930209927" />
				<input type="hidden" id="cPrograma" name="cPrograma" value="0" />
				<input type="hidden" id="nIDPrograma" name="nIDPrograma"  value="">
				<input type="hidden" id="disponibleEPDD" name="disponibleEPDD"  value="">
								
				<!-- hidden para la captura de oficio delegatorio -->
				<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
				<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">
				<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
				<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
				<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
				<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
				<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
				<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
				<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value="">
				<!-- hidden para la captura de oficio delegatorio VoBo-->
				<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
				<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
				<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
				<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
				<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
				<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
				<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">
				
				<input type="hidden" id="cNombreEmpleado" name="cNombreEmpleado" value="">
				<input type="hidden" id="cPaternoEmpleado" name="cPaternoEmpleado" value="">
				<input type="hidden" id="cMaternoEmpleado" name="cMaternoEmpleado" value="">
				<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value="">
				<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value="">
				<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
				<input type="hidden" id="numeroEmpleadoVoBo" name="numeroEmpleadoVoBo" value="">
				<input type="hidden" id="numeroEmpleadoAutoriza" name="numeroEmpleadoAutoriza" value="">
				
				<input type="hidden" id="nFolioPago" name="nFolioPago" value="<%=folio%>" >
				
				<div class="container" style="width: 100%">
					<div class="row">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">											
							<label for="folio" class="form-label">*Folio</label>
							<input type="text" name="folio" id="folio" class="form-control form-control-sm" readOnly value="<%=caso.getFolio()%>" />							
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="oficioRectif" class="form-label">Oficio rectif.</label>	
							<input type="text" id="oficioRectif" name="oficioRectif" class="form-control form-control-sm" readOnly value="<%=caso.getFolio()%>"/>							
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="ctr_int" class="form-label">CTR_INT</label>	
							<input type="text" id="ctr_int" name="ctr_int" class="form-control form-control-sm"/>							
						</div>										
					</div>	

					<div class="row">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
							<label for="fExp" class="form-label">*F. Expedici&oacute;n</label>
							<input type="text" id="fExp" name="fExp" class="form-control form-control-sm" value="<%=fCreacion%>" readOnly />							
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="fApl" class="form-label">*Fecha Aplicaci&oacute;n</label>							
							<input type="text" id="fApl" name="fApl" class="form-control form-control-sm" value="<%=fAplicacion[0]%>" readOnly /> 
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="EsIP" class="form-label">Es Ingreso Propio</label>							
							<input type="checkbox" id="EsIP" name="EsIP" class="form-check-input" onclick="javscript:cambiaEsIP()"/>
						</div>
					</div>	
					
					<div class="row">
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">							
							<label for="CatMovimientoRectificacion" class="form-label">Movimiento</label>
							<div class="input-group">
								<input type="text" class="form-control AyudaSyC form-control-sm" name="CatMovimientoRectificacion" id="CatMovimientoRectificacion" readOnly onKeyDown="return false;" value="<%=(rec != null) ? rec.getcTipoMovto() : ""%>"/>
							</div>							
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
						<div class="col-12 col-lg-7 col-md-7 col-sm-12">									
							<label for="origPresupuesto" class="form-label">*Origen ppto.</label>							
							<select id="origPresupuesto" name="origPresupuesto" class="form-select form-select-sm">
								<option value="Z:">A</option>
								<option value="Y:">B</option>
								<option value="X:">C</option>
								<option value="xx" selected>--</option>
							</select> 
						</div>
					</div>	
					
					<div class="row">						
						<div class="col-12 col-lg-12 col-md-12 col-sm-12">								
							<label for="concepto" class="form-label">*Concepto</label>							
							<textarea id="concepto" name="concepto" class="form-control form-control-sm" rows="3" cols="50" onkeypress="return event.keyCode!=13"></textarea>
						</div>
					</div>	
					
					<div class="row">						
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
							<label for="totalDice" class="form-label">Total DICE</label>	
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>						
								<input type="text" id="totalDice" name="totalDice" class="form-control form-control-sm" onKeyDown="return false;" readOnly/> 
								<input id="totalDiceValor" name="totalDiceValor" type="hidden"/>
							</div>
						</div>
						<div class="col-12 col-lg-4 col-md-4 col-sm-12">					
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">													
							<label for="totalDebeDecir" class="form-label">Total DEBE DECIR</label>
							<div class="input-group">
								<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>									
								<input type="text" id="totalDebeDecir" name="totalDebeDecir" class="form-control form-control-sm" onKeyDown="return false;" readOnly/>
							</div> 							
						</div>
					</div>	
							
					<table>
						<tr>
							<td align="left">
								<label id="lblContrarrecibo">Contrarrecibo:</label>							
							</td>
							<td>
								<select name="cboContrarrecibo" id="cboContrarrecibo"></select>
							</td>
							<td align="center">
								<input type="button" id="btnAgregar" name="btnAgregar" value="Agregar detalle" onclick="agregaDetContrarrecibo()" class="btnInterfaceBG"/>
							</td>
						</tr>
					</table>
				
					<br/>
					
					<div id="divImprimePoliza" class="conainer">
						<div class="row">							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12">
								<div class="input-group">
									<span class="input-group-text"><i class="bi bi-printer"></i></span>		
									<input type="button" value="Poliza" onClick="cmdImprimir('RECTIFICACION');" class="btn btn-secondary">
								</div>
							</div>						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">										
								<span id="EditaFirmas" style="visibility:hidden"><a href="#" class="text-decoration-none" onclick="tipoFirmantes();">Actualiza Firmas*</a></span>								
							</div>
						
							<div class="col-12 col-lg-3 col-md-3 col-sm-12">										
								<a href="#"	class="text-decoration-none" onclick="window.open('../reportes?cmd=<%=GestionInterface.RPT_MOVIMIENTOS_RECT%>&folio_rectificacion=<%=caso.getFolio()%>&nfolio=<%=folio%>');">Oficio de Rectificaci&oacute;n</a>													
							</div>						
						</div>
					</div>
				</div>
				
				<br/>
				
				<div class="container" style="width: 100%">
					<h6> CLC a Rectificar </h6>
					<hr class="mt-3">
						
					<div class="row">												
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">						
							<label for="dTipoPago" class="form-label">Tipo CLC:</label>		
							<input type="text" id="dTipoPago" name="dTipoPago" class="form-control form-control-sm" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getTipoPago() : ""%>" readOnly/>							
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">					
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="nFolioSICOP" class="form-label">Folio CLC:</label>		
							<input type="text" id="nFolioSICOP" name="nFolioSICOP" class="form-control form-control-sm" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSicop() : ""%>" readOnly/>												
						</div>
						<div class="col-12 col-lg-3 col-md-3 col-sm-12">
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="caNoContrarrecibo" class="form-label">No. CxP</label>	
							<input type="text" id="caNoContrarrecibo" name="caNoContrarrecibo" class="form-control form-control-sm" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getCaNoContrarrecibo() : ""%>" readOnly/>							 					
						</div>
					</div>	
					
					<div class="row">						
						<div class="col-12 col-lg-10 col-md-10 col-sm-12">									
							<label for="cDescripcionPoliza" class="form-label">Descripcion</label>							
							<textarea id="cDescripcionPoliza" name="cDescripcionPoliza" class="form-control form-control-sm" rows="3" cols="50" readOnly onKeyDown="return false;"></textarea>
						</div>
						<div class="col-12 col-lg-2 col-md-2 col-sm-12">									
							<label for="nFolioSIAFF" class="form-label">Folio SIAFF:</label>	
							<input type="text" id="nFolioSIAFF" name="nFolioSIAFF" class="form-control form-control-sm" value="<%=(rectificacionCapturada != null && rectificacionCapturada.getEncabezado() != null)?rectificacionCapturada.getEncabezado().getnFolioSIAFF() : ""%>" readOnly/>							
						</div>						
					</div>
						
					
					<div id="demo_jui" class="container"><br/>
						<h5> Dice</h5>
						<hr class="mt-3">
				
						<h6>*Doble click en el renglon DICE para editar</h6>						
						<table id="tblPagadoFiltrado" class="table table-striped table-sm">
							<thead>
								<tr>
									<th>#</th>
									<th>Solicitud</th>
									<th>Mes</th>
									<th>EP</th>
									<th>Evento</th>
									<th>Importe</br>Neto</th>
									<th>Remanente</th>
									<th>Descartar</br>Secuencia</th>
									<th>EP_PADRE</th>
									<th>Programa</th>
									<th>SubPrograma</th>
								</tr>
							</thead>
							<tbody>
					<%
						if(  rectificacionCapturada != null && rectificacionCapturada.getDetalle() != null  ){
							List<RectificacionDetalle> detalleRectificacion = rectificacionCapturada.getDetalle();
							for (Iterator<RectificacionDetalle> itDet = detalleRectificacion.iterator(); itDet.hasNext(); ) {
								RectificacionDetalle registro = itDet.next();
					%>
								<tr>
									<td class="center"><%=registro.getRenglon()%></td>
									<td class="center"><%=registro.getcaNoContrarrecibo() == null?"": registro.getcaNoContrarrecibo() %></td>
									<td class="center"><%=registro.getMes()%></td>
									<td class="center"><%=registro.getEp()%></td>
									<td class="center"><%=registro.getEvento()%></td>
									<td style="text-align: right;"><%=registro.getimporteNeto()%></td>
									<td style="text-align: right;"><%=registro.getRemanente()%></td>
									<td class="center">
									
									<img
										src="../imagenes/cancelar.gif" width="25" height="21"
										alt="Descartar Secuencia"
										onClick="descartar( '<%=registro.getEp()%>', '<%=registro.getMes()%>', '<%=registro.getEvento()%>', '<%=registro.getRenglon()%>', editarMontoDD('<%=registro.getRenglon()%>') );">
									</td>
									<td>
										<%=registro.getEpRectifica() == null?"": registro.getEpRectifica() %>
									</td>
									<td style="text-align: center;"><%=registro.getnidprograma()%></td>
									<td style="text-align: center;"><%=registro.getcSubPrograma()%></td>
								</tr>
								<%
									if ("DICE".equalsIgnoreCase(registro.getEvento())) {										
										valor += registro.getImporte();
										strValor = String.format("%1$,.2f", valor);
										}
									}
								}
								%>
							</tbody>
						</table>
						<input  id="totalDICEH" name="totalDICEH" value="<%=strValor%>" type="hidden"/> 
						<input  id="totalDICEV" name="totalDICEV" value="<%=valor%>" type="hidden" />
					</div>	
					
					<br/>
					
					<div class="container">
						<h5> Debe Decir</h5>
						<hr class="mt-3">
						
						<h6>*(¿El pago es FFM?) Doble click en el renglon DEBE DECIR para seleccionar el Programa y Subprograma</h6>
						<table id="tblPagadoFiltradoDEBE" class="table table-striped table-sm">
							<thead>
								<tr>
									<th>#</th>
									<th>Solicitud</th>
									<th>Mes</th>
									<th>EP</th>
									<th>Evento</th>
									<th>Importe</br>Neto</th>
									<th>Remanente</th>	
									<th>Descartar</br>Secuencia</th>
									<th>EP_PADRE</th>																	
									<th>Programa</th>
									<th>SubPrograma</th>
								</tr>
							</thead>
							<tbody>
							</tbody>
						</table>
					</div>				
				</div>
			</form>
		</div>
		<!-- DIALOGOS (POPUPS) QUE SALEN PARA HACER LAS APLICACIONES CONTABLES AL ESTILO DE REINTEGROS FMC 2/NOV -->

		<%
			if (id_oper == 5 || id_oper == 6) {
		%>
		
		<br/>
		
		<div class="container">						
			<div class="row">								
				<div class="col-12 col-lg-5 col-md-5 col-sm-12">
					<input type="button" id="Cancelar" value="Cancelar Documento" onclick="javascript:cancelarDoc();" class="btn btn-outline-danger btn-sm" /><br />
				</div>
			</div>
		</div>
		
		<%
			}
		%>

		<%
			if (mensaje == null || "".equals(mensaje)) {
		%>
		
		<div id="dialog" title="Operaciones de Rectificaciones" class="container" style="width: 50%">
			<p class="text-sm-start">Aplica apartado de rectificaciones del presupuesto</p>
			<input type="button" id="aplicarContable" value="Aplicar contablemente" onclick="aplicaCont();" class="btn btn-secondary btn-sm"/>
		</div>

		<div id="dialogAut" title="Detalle de Rectificaciones" class="container" style="width: 50%">
			<p class="text-sm-start">Autorización de rectificaciones del presupuesto</p>
			<input type="button" id="aplicarContableAut" value="Autorizar contablemente" onclick="aplicaContAut()" class="btn btn-secondary btn-sm"/>
		</div>
		
		<%
			}
		%>
		
		<div id="totales">
			<label id="totalDiceInfo" type="hidden" ></label><br /> 
			<label id="totalDebeInfo" type="hidden"></label><br />
		</div>
	</div>
	
	<div id="dialogMotor" title="Mensajes del sistema Oficio de Rectificaciones" class="container">
		<p>Mensajes del sistema</p>
		<a rel=""></a>
		<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="100" class="form-control form-control-sm"></textarea>
	</div>
	
	<div id="EdtaRenglon" class="container">
		<form id="editaEP">		
			<h5> DICE </h5>
			<hr class="mt-3"/>	
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
					<label for="solicitud" class="form-label">Solicitud:</label>									
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-tag"></i></span>						
						<input type="text" id="solicitud" name="solicitud" class="form-control form-control-sm" readOnly/>
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="EPDice" class="form-label">EP:</label>									
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">	
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-tag"></i></span>					
						<input type="text" id="EPDice" name="EPDice" class="form-control form-control-sm" readOnly/>
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="nMesDice" class="form-label">Mes:</label>									
				</div>
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-calendar"></i></span>				
						<input type="text" id="nMesDice" name="nMesDice" class="form-control form-control-sm" readOnly/>
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="mImporteDice" class="form-label">Importe:</label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">		
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>														
						<input type="text" id="mImporteDice" name="mImporteDice" class="form-control form-control-sm" value="" placeholder="0.00"/>
					</div>							
				</div>
			</div>	
			
			<br/>	

			<div id="dlg-ProgramaSubProgramaDICE" class="container">						
				<h5> FIB Banorte Programa - SubPrograma </h5>
				<hr class="mt-3"/>
				
				<div class="row">												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
						<label for="lblProgramaDice" class="form-label">Programa:</label>									
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">
						<input type="text" id="idProgramaDice" name="idProgramaDice" class="form-control form-control-sm" readonly/>
					</div>
				</div>
				
				<div class="row">												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
						<label for="lblSubProgramaDice" class="form-label">SubPrograma:</label>									
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
						<input type="text" id="idSubProgramaDice" name="idSubProgramaDice" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<br/>
							
			<h5> DEBE DECIR </h5>
			<hr class="mt-3"/>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
					<label for="EPDebeDecir" class="form-label">EP:</label>									
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-2">
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-tag"></i></span>						
						<input type="text" size="65" id="EPDebeDecir" name="EPDebeDecir" class="form-control form-control-sm" onchange="limpiarSelect()" readonly/>
						<input type="button" id="nIdClaveEgresos2" size="5" value="..."	onclick="Grid()" class="btn btn-secondary"/>
						
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
					<label for="MesDebeDecir" class="form-label">Mes:</label>									
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-2">
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-calendar"></i></span>						
						<select id="MesDebeDecir" name="MesDebeDecir" class="form-select form-select-sm">
							<option value="1">Enero</option>
							<option value="2">Febrero</option>
							<option value="3">Marzo</option>
							<option value="4">Abril</option>
							<option value="5">Mayo</option>
							<option value="6">Junio</option>
							<option value="7">Julio</option>
							<option value="8">Agosto</option>
							<option value="9">Septiembre</option>
							<option value="10">Octubre</option>
							<option value="11">Noviembre</option>
							<option value="12">Diciembre</option>
						</select>
					</div>							
				</div>
			</div>
			
			<div class="row">												
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
					<label for="ImporteDebeDecir" class="form-label">Importe:</label>									
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">			
					<div class= "input-group">
						<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>										
						<input type="text" id="ImporteDebeDecir" name="ImporteDebeDecir" class="form-control form-control-sm" placeholder="0.00" />
					</div>											
				</div>
			</div>	
			
			<br/>
			
			<div id="dlg-ProgramaSubPrograma" class="container">
				<h5> FIB Banorte Programa - SubPrograma </h5>
				<hr class="mt-3"/>
				
				<div class="row">												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
						<label for="lblPrograma" class="form-label">Programa:</label>									
					</div>
					<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-2">
						<select id="cboPrograma" name="cboPrograma" onchange="cambiaPrograma()" class="form-select form-select-sm">
						</select>																		
					</div>
				</div>
				
				<div class="row">												
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-2">						
						<label for="lblSubPrograma" class="form-label">SubPrograma:</label>									
					</div>
					<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-2">						
						<select id="cboSubPrograma" name="cboSubPrograma" class="form-select form-select-sm">
						</select>																									
					</div>
				</div>
			</div>
		</form>
	</div>	
	
		<div id="dialog-firmantes" title="Firmantes" class="container">			
			<h5> Datos  VºBº</h5>
			<hr class="mt-3">	
			
			<div class="row">												
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">																						
					<input type="checkbox" id="oficioDelegatorio" name="oficioDelegatorio" class="form-check-input" onclick="showDivOficio(false);">
					<label for="dTipoPago" class="form-label">Oficio delegatorio</label>
				</div>				
			</div>			
			
			<div class="row">												
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cboVoBo" class="form-label">VoBo:</label>
					<select id="cboVoBo" name="cboVoBo" onchange="infoEmpleado('VOBO');" class="form-select form-select-sm"> 
					</select>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cNombreVoBo" class="form-label">Nombre:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="cNombreVoBo" name="cNombreVoBo" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cPaternoVoBo" class="form-label">Apellido Paterno:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="cPaternoVoBo" name="cPaternoVoBo" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cMaternoVoBo" class="form-label">Apellido Materno:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="cMaternoVoBo" name="cMaternoVoBo" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cPuestoVoBo" class="form-label">Puesto:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cPuestoVoBo" name="cPuestoVoBo" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<h5> Datos  Autoriza</h5>
			<hr class="mt-3">	
			
			<div class="row">												
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cboAutoriza" class="form-label">Autoriza:</label>
					<select id="cboAutoriza" name="cboAutoriza" onchange="infoEmpleado('AUT');" class="form-select form-select-sm"> 
					</select>
				</div>
			</div>
			
			<div class="row">			
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cNombreAut" class="form-label">Nombre:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="cNombreAut" name="cNombreAut" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cPaternoAut" class="form-label">Apellido Paterno:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="cPaternoAut" name="cPaternoAut" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>				
				
			<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cMaternoAut" class="form-label">Apellido Materno:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-person"></i></span>
						<input type="text" id="cMaternoAut" name="cMaternoAut" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>
			
			<div class="row">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
					<label for="cPuestoAut" class="form-label">Puesto:</label>
					<div class="input-group">								
						<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
						<input type="text" id="cPuestoAut" name="cPuestoAut" size=40 maxlength="70" class="form-control form-control-sm" readonly/>
					</div>
				</div>
			</div>					
			
			<br/>							
			
			<div id="oficioDelegatorioCaptura" style="display: none">
				<h5> Datos del Suplente</h5>
				<hr class="mt-3">				
				
				<div class="row">																
					<div class="col-12 col-lg-8 col-md-8 col-sm-12">
						<label for="cFolioOficio" class="form-label">No. de Oficio:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-file-earmark"></i></span>
							<input type="text" id="cFolioOficio" name="cFolioOficio" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-8 col-md-8 col-sm-12">
						<label for="dFechaOficio" class="form-label">Fecha de Oficio:</label>						
						<input type="text" id="dFechaOficio" name="dFechaOficio" size=40 maxlength="70" class="form-control form-control-sm"/>						
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-8 col-md-8 col-sm-12">
						<label for="dFechaOficio" class="form-label">Tipo de Suplencia:</label>						
						<select id="tipoSuplencia" name="tipoSuplencia"class="form-select form-saelect-sm">
						</select>												
					</div>
				</div>	
				
				<div class="row">												
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cboSuplenteAut" class="form-label">Suplente Autoriza:</label>
						<select id="cboSuplenteAut" name="cboSuplenteAut" onchange="infoEmpleado('SUPAUT');" class="form-select form-select-sm"> 
						</select>
					</div>
				</div>
				
				<div class="row">			
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cNombreTitular" class="form-label">Nombre Suplente:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cNombreTitular" name="cNombreTitular" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cPaternoTitular" class="form-label">Apellido Paterno:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cPaternoTitular" name="cPaternoTitular" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>				
					
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cMaternoTitular" class="form-label">Apellido Materno:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-person"></i></span>
							<input type="text" id="cMaternoTitular" name="cMaternoTitular" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-10 col-md-10 col-sm-12">
						<label for="cPuestoTitular" class="form-label">Puesto:</label>
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-briefcase"></i></span>
							<input type="text" id="cPuestoTitular" name="cPuestoTitular" size=40 maxlength="70" class="form-control form-control-sm"/>
						</div>
					</div>
				</div>					
			</div>
		</div>		
</body>
</html>