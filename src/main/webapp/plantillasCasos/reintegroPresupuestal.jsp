<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%@page import="com.syc.contable.core.ReintegroEncabezadoMil"%>
<%@page import="com.syc.contable.core.ReintegroDetalleMil"%>
<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%@page import="com.syc.obrapublica.ConfiguraAplicativoBusinessLogic"%>
<%
	
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	
	String cCentroContable = "";
	String aEjercicioFiscal = "";
	boolean bErrorAdec=false;
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String cUR = usuario.getU_UR();

	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String msVariable = c.getCasoDato("MENSAJE").getValor(); //Recuperamos el mensaje del caso
	
	ReintegrosBusinessLogic reintegro = new ReintegrosBusinessLogic(GestionInterface.ATT_CONEXION);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	if (request.getParameter("pago") != null && request.getParameter("pago").equals("Si")) {
	    boolean a = reintegro.actualizaInfoPagos(folio,request.getParameter("rastreo"),request.getParameter("lcaptura"),request.getParameter("fichaDep"),request.getParameter("clvBanco"),request.getParameter("cuenta"),request.getParameter("fAcredit"),request.getParameter("ctab"));
	}
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] archivoExcel = null; //id_oper 1 (EXCEL)
	FortimaxFile[] archivoLineaCaptura = null; //id_oper 5 (LINEA DE CAPTURA)
	FortimaxFile[] archivoPDFComprobante = null; //id_oper 5 (COMPROBANTE)
	FortimaxFile[] archivoPDFCLC = null; //id_oper 3 (COMPLEMENTARIO)
	FortimaxFile[] archivoPDFCXP = null; //id_oper 3 (COMPLEMENTARIO)
	FortimaxFile[] archivoReporteSicop = null; //id_oper 6 (REPORTES)
	FortimaxFile[] archivoReporteSiaff = null; //id_oper 6 (REPORTES)
	
	int nIdDocumento = 0;
	
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, "Poliza");
	//archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1); //el numero que está primero indica el num de carpeta
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, nIdDocumento);
	//la raiz es la 0, se puede ver con el numero que viene después de la c al pasar el mouse en el arcbol. El segundo numero es el numero de archivo dentro de esa carpeta, empieza en el 1.
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, "Oficio");
	archivoPDFCLC = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, nIdDocumento);
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, "CXP Origen");
	archivoPDFCXP = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, nIdDocumento);
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, "Archivo Linea de Captura");
	archivoLineaCaptura = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, nIdDocumento);
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 5, "Comprobante de pago");
	archivoPDFComprobante = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 5, nIdDocumento);
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, "Reporte SICOP");
	archivoReporteSicop = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, nIdDocumento);
	
	nIdDocumento = 0;
	nIdDocumento = cbl.buscaIdDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, "Reporte SIAFF");
	archivoReporteSiaff = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, nIdDocumento);

	String mensaje = "";
	if (request.getParameter("msg") != null
			&& !"".equals(request.getParameter("msg"))) {
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

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	String mensajeError="";
	try{
	    if (request.getParameter("leeExcel") != null && request.getParameter("leeExcel").equals("1")) {
			if(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1).length>0)
			    reintegro.leeArchivoExcel(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0].getAbsolutePath(),c,usuario,folio);
		}
	}catch(Exception ex){
	 	mensajeError = ex.getMessage();   
	}
	
	ReintegroEncabezadoMil re = reintegro.getReintegroEncabezadoNuevo( folio );
	ReintegroDetalleMil rd = reintegro.getReintegroDetalleNuevo( folio );
	
	String rfc; 
	rfc = rd.getRfc();	
	
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);
	
	ConfiguraAplicativoBusinessLogic configSys = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean activaEventosFA = "S".equals( configSys.getSystemSetting("ACTIVA_EVENTOS_FA") );
		
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reintegros Presupuestales</title>
		
		<link rel="stylesheet" type="text/css"  href="../css/datepickercontrol_bluegray.css" />
		<link rel="stylesheet" type="text/css"	href="../Generador/css/jquery-ui.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/datatables.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/bootstrap.min.css"></link>
		<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
		<link rel="stylesheet" type="text/css"  href="../Generador/css/demo_table_jui.css"></link>		
		<link rel="stylesheet" type="text/css" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.2/font/bootstrap-icons.css"></link>
		
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
		
		<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>


		<script type="text/javascript" charset="utf-8">
		var muestraAplicar = true;
		var clickEnviar = false;
		var tbl_RecepcionBien = "";
		var tbl_RecepcionServ = "";
		var tbl_RecepcionNueva = "";
		var tblBoletosReintegrar = "";
		var tblBoletosReint = "";
		var activaEventosFA = <%=activaEventosFA%>;
		var rfc = "<%=rfc%>";
		
		function onPostSubmit(id_oper){//validaciones del boton enviar
		  		//var valida_doctos_requeridos = true;
		  		//validaciones de documentos requeridos
		  		//return valida_doctos_requeridos;
		  		clickEnviar=true;
		  		if(<%=re==null%>){
			  		if(id_oper==1 && <%=archivoExcel.length==0%>){//ejemplo para validar archivos adjuntos
			  			Swal.fire({ icon: 'error',
									text: "El excel no puede estar vacio." });			  			
						return;
					}
				}if(id_oper==1 && <%=archivoPDFCLC.length==0%>){
					Swal.fire({ icon: 'warning',
								text: "Favor de adjuntar el Oficio." });					
					return;										
				}else if(id_oper==1 && <%=archivoPDFCXP.length==0%>){
					Swal.fire({ icon: 'warning',
								text: "Favor de adjuntar la Cuenta por Pagar Origen." });					
					return;
				}else if(id_oper==1){
					if(!validaCapturaProgSubProg()){
						return;
					}else{
						actualizaProgSubProgReintegro();
					}
				}else if(id_oper==1){
					if(!clickEnviar){
						parent.document.getElementById("pb_send").disabled=false;
						clickEnviar = true;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					} 
				}	
				if(id_oper==1 && ($("#CTABAN").val() == "" || $("#CTABAN_FFM").val() == "") && $("#ffm").val("") ){
					Swal.fire({ icon: 'warning',
								text: "Favor de capturar la cuenta bancaria." });					
					return;
				}else 
					actualizaPagoInfo();				
				
		  	if(id_oper == 5 && <%=archivoPDFComprobante.length==0%>){
		  		Swal.fire({ icon: 'warning',
							text: "Favor de subir el comprobante en la sección de adjuntos." });				
				return;
			}else if(id_oper == 5 && <%=archivoPDFComprobante.length>0%>){
				if(<%="1".equals(re!=null?re.getTipoAviso():"")%>){
					if($("#clvRastreo").val()=='N/A' && $("#deposito").val()=='N/A'){
						Swal.fire({ icon: 'warning',
									text: "Favor de actualizar la clave de rastreo o la ficha de deposito." });						
						return;
					}
					else
						actualizaPagoInfo();
				}
			}
		  	
		  	if(id_oper == 4 && <%=archivoLineaCaptura.length==0%>){
		  		Swal.fire({ icon: 'warning',
							text: "Favor de subir el archivo de linea de captura en la sección de adjuntos." });
				return;
			}else if(id_oper == 4 && <%=archivoLineaCaptura.length>0%>){
					if(<%="1".equals(re!=null?re.getTipoAviso():"")%>){
				  		if($("#lineaCap").val()=='N/A'){
				  			Swal.fire({ icon: 'warning',
										text: "Favor de actualizar la linea de captura." });							
							return;
						}
				  		else
				  			actualizaPagoInfo();
			  		}
			  		
		  	}		  				
				
				if(!clickEnviar){
					parent.document.getElementById("pb_send").disabled=false;
					clickEnviar = true;
				}else{
					parent.document.getElementById("pb_send").disabled=true;
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_leave").disabled=true;
					if(parent.document.getElementById("pb_cancel"))
						parent.document.getElementById("pb_cancel").disabled=true;
				}
		  	return true;
		}
		
		function ResponsableSiguiente(id_oper){
			if(id_oper==1)
	  		 	return "REVISOR_REINTEGRO";
	  		if(id_oper==2){
	  			 if(document.datosReintegro.autorizaRein[1].checked)
	  				 return "CAPTURISTA_REINTEGRO";
	  			 else
	  		 		return "AUTORIZADOR_REINTEGRO";
	  		}
	  		if(id_oper==3)
	  		 	return "AUTORIZADOR_REINTEGRO";
	  		if(id_oper==4)
		  		return "CAPTURISTA_REINTEGRO";
	  		if(id_oper==5)
	  			return "AUTORIZADOR_REINTEGRO";
	  		if(id_oper==6)
				return "CONSULTA_REINTEGRO";					
		 }
		
		function OperacionSiguiente(id_oper){
			if(id_oper==1)
				return "revisa_reintegro";
			if(id_oper==2){
				if(document.datosReintegro.autorizaRein[1].checked)
					return "captura_reintegro";
			  	else
			   	 	return "genera_layout";
	  		}
			if(id_oper==3)
				return "adjunta_linea";
	  		if(id_oper==4){
	  			return "digitaliza_comp_pago";
  		 	}
	  		if(id_oper==5){
			  	return "autoriza_reintegro";
  		 	}
	  		if(id_oper==6)
	  			 return "consulta_reintegro";
  		 }
		
		function onPostDisplay(id_oper){
			
		}

		function onSubmit(id_oper){//validaciones del boton guardar
		  	var p = window.parent;
		  	var valida_campos = true;
			try{
				//validaciones de la forma
				var msgAlert="";
	
				if(msgAlert!=""){
					Swal.fire({ icon: 'warning',
								text: msgAlert });		  			
		  			return false;
		  		}
		  		if (id_oper==1){
		  			p.gestion.setFolio($("#FOLIO").val());
					p.gestion.setOperador($("#OPERADOR").val());
					p.gestion.setFechaDocumento($("#FECHA_SOLICITUD").val());
					p.gestion.setEjercicioFiscal($("#EJERCICIO_FISCAL").val());
					p.gestion.setConceptoMov("Reintegro Presupuestal");
					p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
					//$("#folioReintegro").val($("#FOLIO").val());	
					
					if($("#esViatico").val()=="SI"){
						if($("#existeComisionReintegro").val() == "NO"){
							
							let total = 0;
							
							total = Number($("#mPasajeR").val()) + Number($("#mTaxiR").val()) + Number($("#mPeajeR").val()) 
								+ Number($("#mHotelR").val()) + Number($("#mConsumosR").val()) + Number($("#mOtrosR").val());
							
							$("#totalComision").val(total);
							if (total == 0){
								Swal.fire({ icon: 'info',
											text: "Debes capturar el importe a reintegrar en el rubro que le corresponda." });								
								return;
							} else {
								queryFormPost("insertaDetalleComisionReintegro", {async:false});
															
								$("#mPasajeR").attr('disabled','disabled');
								$("#mTaxiR").attr('disabled','disabled');
								$("#mPeajeR").attr('disabled','disabled');
								$("#mHotelR").attr('disabled','disabled');
								$("#mConsumosR").attr('disabled','disabled');
								$("#mOtrosR").attr('disabled','disabled');
								$("#mPasajeLocalR").attr('disabled','disabled');
								$("#mTaxiLocalR").attr('disabled','disabled');
								$("#mGasolinaLocalR").attr('disabled','disabled');
								$("#mPeajeLocalR").attr('disabled','disabled');
							
							}						
						}
					}
					
					if($("#esCXPPagoDiverso").val() == "1"){
						if(!validaRecepcionCapturada()){
							Swal.fire({ icon: 'warning',
										text: "Favor de Capturar la Recepcion para continuar.\nDando click sobre el vinculo CAPTURAR LINEAS RECEPCION." });						 	
							return;
						}
						
						if($("#esProveedorVuelos").val() == "1"){
							if(!validaCapturaBoletos()){
								Swal.fire({ icon: 'warning',
											text: "Favor de Capturar los boletos del reintegro en el botón inferior Agregar Boletos." });							 	
								return;
							}
						}
					}
					
					if($("#esFIBBanorte").val() == "SI"){
						$("#nIDPrograma").val();
						$("#cSubPrograma").val();
						if($("#cboPrograma").val() == "0" || $("#cboSubPrograma").val() == "00"){
							Swal.fire({ icon: 'warning',
										text: "Favor de seleccionar el programa y subprograma Correspondiente para continuar." });							
							$("#cboPrograma").focus();
							return;							
						}
						
						actualizaProgSubProgReintegro();
					}
									
					if(<%=re!=null%>){
						updateFirmantes();					
					}
					
					$("#btnFirmas").show();
					
				}
					
		  		if (id_oper==2){
		  			if(document.datosReintegro.autorizaRein[1].checked && $("#motivoRechazo").val()==""){
		  				Swal.fire({ icon: 'warning',
									text: "Motivo de rechazo es requerido." });		  				
			  			return false;
		  			}else if(document.datosReintegro.autorizaRein[1].checked){
		  				parent.document.getElementById("pb_send").disabled=false;
		  				parent.document.getElementById("pb_save").disabled=true;
		  				$("#motivoR").val($("#motivoRechazo").val());
		  				$("#mensajeError").val($("#motivoRechazo").val());
		  				$("#nFolioR").val(<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>);
		  				queryFormPost("tReintegroEncabezadoUpdate", {async:false});
		  			}else if(!(document.datosReintegro.autorizaRein[0].checked || document.datosReintegro.autorizaRein[1].checked)){		  				
		  				Swal.fire({ icon: 'warning',
									text: "Favor de marcar si los datos son correctos o no" });
		  				parent.document.getElementById("pb_save").disabled=true;
		  				return false;
		  			}else{
		  				if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
							parent.document.getElementById("pb_send").disabled=true;
						}
		  			}
	  			}
		  		if(<%=c.getIdGabinete()%>!=-1){
					parent.document.getElementById("pb_save").disabled=true;
					if(id_oper==8){
						
						if(parent.document.getElementById("pb_cancel"))
							parent.document.getElementById("pb_cancel").disabled=true;
					}
				}
			
		  		if(id_oper == 3){
		  			parent.document.getElementById("pb_send").disabled=false;
		  		}
		  		if(id_oper == 4){
		  			parent.document.getElementById("pb_send").disabled=false;
		  		}
		  		if(id_oper == 5){
		  			//actualizaPagoInfo();
		  			parent.document.getElementById("pb_send").disabled=false;
		  		}
		  		
		  		if(id_oper == 6 && <%=archivoReporteSicop.length==0%>){
					parent.document.getElementById("pb_send").disabled=true;
					Swal.fire({ icon: 'warning',
								text: "Favor de subir el Reporte de SICOP en la sección de adjuntos." });					
					return;
				}else if(id_oper == 6 && <%=archivoReporteSiaff.length==0%>){
					parent.document.getElementById("pb_send").disabled=true;
					Swal.fire({ icon: 'warning',
								text: "Favor de subir el Reporte de SIAFF en la sección de adjuntos." });					
					return;
				}else if (id_oper == 6 && ($("#fechaAcredit").val()=='N/A' || $("#fechaAcredit").val()=="")){
					parent.document.getElementById("pb_save").disabled=false;
					parent.document.getElementById("pb_send").disabled=true;
					Swal.fire({ icon: 'warning',
								text: "Favor de seleccionar la fecha de autorización." });					
					return;
				}else if(id_oper==6 && <%=archivoReporteSicop.length>0%> && <%=archivoReporteSiaff.length>0%>){
					if(!clickEnviar){
						if(<%=!("true".equals(c.getCasoDato("AUTORIZADO_CONT").getValor())) && !("true".equals(c.getCasoDato("CANCELADO_CONT").getValor())) %>){
							if(muestraAplicar)
								mostrarDialogAut();
								if($("#esViatico").val()=="SI"){	
									var importeReintTotal = $("#importe").val();
									importeReintTotal = importeReintTotal.replace(",","");
									var ImpReintegro = parseFloat(importeReintTotal,10);
									$("#impReintegro").val(ImpReintegro);
									
									queryFormPost("insertaRelacionCompronacionReintegro", {async:false});
									if($("#esComprobacion").val() == "SI"){										
										$("#importeNegativo").val(ImpReintegro*-1);
										queryFormPost("insertaEdoCuentaViaticosReintegro", {async:false});
										queryFormPost("actualizaRemanenteViaticosReintegro", {async:false});
									}
								}
	 							muestraAplicar = false;
						}else{
							Swal.fire({ icon: 'warning',
										text: "El documento ya se encuentra aplicado." });		  					
		  					parent.document.getElementById("pb_send").disabled=true;
		  					clickEnviar = true;
		  				}
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}
				}
		  	}
			
			catch (e) {
				window.alert("onSubmit: Error: " + e.message);
				return false;
			}
			return valida_campos;
	  	}
	
		function onLoadPlantilla(id_oper){						
			queryFormPost("esBeneficiarioFIBBanorteReintegroRead", {async : false});
			queryFormPost("esViaticoRead", {async : false});
			queryFormPost("existeComisionRead", {async : false});			
			queryFormPost("tipoPagoOrgRead", {async : false});
						
			if ($("#esFIBBanorte").val()=="SI" ){
				cargaProgramaSubPrograma();
			}
				
			if($("#esViatico").val()=="SI"){					
				queryFormPost("infoAgendaComisionPagoRead", {async:false});
				$("#divViaticos").show();
				
				if($("#existeComisionReintegro").val() == "SI"){
					queryFormPost("infoAgendaComisionReintRead", {async:false});
					$("#mPasajeR").attr('disabled','disabled');
					$("#mTaxiR").attr('disabled','disabled');
					$("#mPeajeR").attr('disabled','disabled');
					$("#mHotelR").attr('disabled','disabled');
					$("#mConsumosR").attr('disabled','disabled');
					$("#mOtrosR").attr('disabled','disabled');
					$("#mPasajeLocalR").attr('disabled','disabled');
					$("#mTaxiLocalR").attr('disabled','disabled');
					$("#mGasolinaLocalR").attr('disabled','disabled');
					$("#mPeajeLocalR").attr('disabled','disabled');
				}
			} else {
				$("#divViaticos").hide();
			}
			
			if (<%=id_oper%> >= 2 ){											
				$("#EditaFirmas").css('visibility', 'visible');
			}
					
		 	if (<%=id_oper%> == 7 || <%=id_oper%> == 8){
				$("#divImprimePoliza").show();
			
			}else
			 		$("#divImprimePoliza").hide();		
			 				  	
		 		
			 if(<%=mensajeError!=null && !"".equals(mensajeError)%>){
				$("#textmensajeError").val('<%=mensajeError.replace("'","")%>');
				$('#dlgError').dialog('option', 'modal', true).dialog('open');
			}
			 
			 <% if(mensaje==null || "".equals(mensaje.trim()) &&(id_oper>1)){%>
			 	if(parent.document.getElementById("pb_cancel"))
			 		parent.document.getElementById("pb_cancel").disabled=true;
			 <%}%>
			 
			 $("#FOLIO").attr('disabled','disabled');
			 
			 	$("#importe").val('<%=re!=null?re.getImporteLC():""%>');
					$("#aviso").val('<%=(re!=null && re.getAviso()!=null)?re.getAviso():"N/A"%>');
					$("#CatMovimientoReintegro").val('<%=re!=null?re.getMovimiento():"N/A"%>');
					$("#CatTipoCausaAvisoReintegro").val('<%=re!=null?re.getTipoAviso():"N/A"%>');
					$("#CatFormaPagoAvisoReintegro").val('<%=re!=null?re.getFormaDePago():"N/A"%>');
					$("#CatCausaAvisoReintegro").val('<%=re!=null?re.getCausaAviso():"N/A"%>');
					$("#fechaAp").val('<%=re!=null?re.getfAplicacion():"N/A"%>');
					$("#fechaAcredit").val('<%=re!=null?re.getfAcreditacion():"N/A"%>');
					$("#clvRastreo").val('<%=re!=null?re.getClvRastreo():"N/A"%>');			  			
					$("#deposito").val('<%=re!=null?re.getFichaDeposito():"N/A"%>');
					$("#lineaCap").val('<%=re!=null?re.getLc():"N/A"%>');
					$("#cveBanco").val('<%=re!=null?re.getClvBanco():"N/A"%>');
					$("#cuenta").val('<%=re!=null?re.getCuentaBancaria():"N/A"%>');			  			
					$("#observaciones").val('<%=re!=null?re.getObservaciones():"N/A"%>');
					$("#concepto").val('<%=re!=null?re.getConcepto():"N/A"%>');
					$("#cxp").val('<%=re!=null?re.getCuentaPorPagar():"N/A"%>');
					$("#folioDep").val('<%=re!=null?re.getFolioDependencia():"N/A"%>');
					$("#folioDep").val('<%=re!=null?re.getFolioDependencia():"N/A"%>');
			
					if($("#fechaAp").val()=='null')
						$("#fechaAp").val('N/A');
					if($("#folioDep").val()=='null')
						$("#folioDep").val('N/A');
					if($("#clvRastreo").val()=='null')
						$("#clvRastreo").val('N/A');
					if($("#deposito").val()=='null')
						$("#deposito").val('N/A');
					if($("#lineaCap").val()=='null')
						$("#lineaCap").val('N/A');
					if($("#cuenta").val()=='null')
						$("#cuenta").val('N/A');
					if($("#cveBanco").val()=='null')
						$("#cveBanco").val('N/A');
					if($("#fechaAcredit").val()=='null')
						$("#fechaAcredit").val('N/A');
			 
				if(<%=id_oper%>  == 1){
				if(<%="1".equals(request.getParameter("avanzai"))%>){
					if(!clickEnviar){
						parent.document.getElementById("pb_send").disabled=false;
						clickEnviar = true;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}
					parent.document.getElementById("pb_save").disabled=true;
				}else{
					parent.document.getElementById("pb_send").disabled=true;
					parent.document.getElementById("pb_save").disabled=false;
				}
				
				if($("#mensajeError").val()!=""){
					Swal.fire({ icon: 'error',
								text: $("#mensajeError").val() });					
				}
				$("#clvRastreo").attr('disabled','disabled');			  			
			 		$("#deposito").attr('disabled','disabled');
			 		$("#lineaCap").attr('disabled','disabled');
			 		$("#cveBanco").attr('disabled','disabled');
			 		$("#cuenta").attr('disabled','disabled');		  			
			 		$("#fechaEx").attr('disabled','disabled');	
			 		$("#CTABAN").attr('disabled','disabled');
			 		
				if(<%=c.getIdGabinete()%>==-1)
							parent.document.getElementById("pb_save").click();//esto es para asegurar que se crea el expediente antes de subir el archivo
							
						muestraCapturaRecepcion();
						muestraBotonBoletos();
			}
				
			if(<%=id_oper%>  == 2 ){
				if(<%="true".equals(c.getCasoDato("APLICADO_CONT").getValor()) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
					parent.document.getElementById("pb_cancel").disabled=true;
				}else{
					parent.document.getElementById("pb_cancel").disabled=false;
				}
			}
			
			if(<%=id_oper%>  == 6){
				$("#clvRastreo").attr('disabled',true);			  			
		 		$("#deposito").attr('disabled',true);
		 		$("#lineaCap").attr('disabled',true);
		 		$("#cveBanco").attr('disabled',true);
		 		$("#cuenta").attr('disabled',true);
		 		$("#fechaEx").attr('disabled',true);
		 		$("#fechaAcredit").attr('disabled',false);				  		
		 		$("#CTABAN").attr('disabled',true);
		 		if(parent.document.getElementById("pb_cancel"))
					parent.document.getElementById("pb_cancel").disabled=true;
				}
				if($("#cTipoPago").val() != "RELACIONGASTOS"){
					$("#DecrementoSicop").hide();
				}
			if(<%=id_oper%>  == 8){ //consulta
				$("#fechaAcredit").attr('disabled','disabled');
				if(parent.document.getElementById("pb_cancel"))
					parent.document.getElementById("pb_cancel").disabled=true;
			}
			if(<%=id_oper%>  >= 2){ //se deshabilitan campos que sólo puede modificar el capturista
				$("#clc").attr('disabled','disabled');
				$("#importe").attr('disabled','disabled');
				$("#aviso").attr('disabled','disabled');
				$("#CatMovimientoReintegro").attr('disabled','disabled');
				$("#CatTipoCausaAvisoReintegro").attr('disabled','disabled');
				$("#CatFormaPagoAvisoReintegro").attr('disabled','disabled');
				$("#CatCausaAvisoReintegro").attr('disabled','disabled');
				$("#txtGridTipoCLC").attr('disabled','disabled');
				$("#fechaAp").attr('disabled','disabled');
				$("#folioDep").attr('disabled','disabled');
				destroyDatePickers();
				if(<%=id_oper!=5%>){
		 			$("#clvRastreo").attr('disabled','disabled');			  			
		 			$("#deposito").attr('disabled','disabled');
		 			$("#cveBanco").attr('disabled','disabled');
		 			$("#cuenta").attr('disabled','disabled');					 			
				}
				
				if(<%=id_oper!=4%>)
					$("#lineaCap").attr('disabled','disabled');
	
				$("#CTABAN").attr('disabled','disabled');					
				$("#observaciones").attr('disabled','disabled');
				$("#concepto").attr('disabled','disabled');
	
			}
			if(<%=id_oper%>  >= 3){ //a partir de la generacion del layout ya no se puede descartar
				if(parent.document.getElementById("pb_cancel"))
					parent.document.getElementById("pb_cancel").disabled=true;
			}
			if(<%=id_oper%>  == 2){
				parent.document.getElementById("pb_cancel").disabled=true; //por lo pronto
			}
			if(<%=id_oper%>  == 8){
				$("#fechaAcredit").attr('disabled','disabled');
			}
		}
	
  	function setDatePickers(){	  	
		$( "#fechaEx" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		$( "#fechaAp" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		$( "#fechaAcredit" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
	}

  	function destroyDatePickers(){
	  	$( "#fechaSol" ).datepicker("destroy");
		$( "#fechaEx" ).datepicker("destroy");
	  	$( "#fechaAp" ).datepicker("destroy");
	}
  	
	$(document).ready(function(){
		$("#Cancelar").button();
		$("#Exportar").button();
		$("#aplicarContable").button();
		$("#aplicarContableAut").button();
		querySelectPost("catTipoSuplenciaRead", "tipoSuplencia",{async: false });
		querySelectPost("catTipoSuplenciaRead", "tipoSuplenciaVoBo",{async: false });
		
		setDatePickers();
		
		$("#ImportarExcel").button();
		$("#borrarTodo").button();
		$("#btnImprimir").button();
		$("#btnAgregarBoletos").button();
		$("#btnAgregarBoletos").hide();
		$("#oficioDelegatorioCaptura").hide();		
 		$( "#dFechaOficio" ).datepicker({
 			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});	
		
		$("#oficioDelegatorioVoBo").hide();		
		$( "#dFechaOficioVoBo" ).datepicker({
			dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
		
		$("#btnFirmas").hide();		
		
		var $dialog;
		var p = window.parent;
      			//PageInit();
      	$(function() {		
      		$('#dialog').dialog({
      			autoOpen: false,
      			width: 900,
      			heigth: 2900
    		});
    	});
			    
		$(function() {		
			$('#dialogAut').dialog({
				autoOpen: false,
				width: 900,
				heigth: 2900
			});
		});
		var $dlgError;
		$(function(){
			$('#dlgError').dialog({
				autoOpen: <%=bErrorAdec%>,
					width: 1200,
					heigth: 1800,
					close: function() {
		      			document.location.reload();
					}
				});
			});
		$(function() {		
		    $('#dialogMotor').dialog({
		      	autoOpen: false,
	  			width: 900,
	 			heigth: 2900
		    });
		});
				
		$("input.AyudaSyC").subIniciaDlg();
		//$("input.autoCompletaSyC").subIniciaAutoCompleta();
		$( "#fechaSol" ).attr('disabled','disabled');
 		$('.currency').blur(function(){
			$('.currency').formatCurrency();
		});
 		
		oTable = $('#tblReintegros').dataTable({
					"bPaginate": true,
        			"bLengthChange": true,
        			"bFilter": true,
        			"bSort": true,
        			"bInfo": true,
        			"bAutoWidth": false,
					"sScrollY": 270,
					"sScrollYInner": "100%",
					"bJQueryUI": true,
					"bRetrive" : true,
					"bDestroy" : true,
					"sPaginationType": "full_numbers",
					"sScrollX": "100%",
					"sScrollXInner": "110%",
					"bScrollCollapse": true,	
					"bServerSide": true,   
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vtReintegroDet&qw=nFolioReintegro="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>,
					aoColumns: [
						//{ sName: "Expr1"},
						{ sName: "noCLC" },
						{ sName: "secCLC" },
						{ sName: "EP"},
						{ sName: "mImporte"},
						{ sName: "cxp"}
						//{ sName: "Eliminar"}
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

				/* Add a click handler to the rows - this could be used as a callback */
 				$("#tblReintegros tbody").click(function(event) {
 					
 					$(event.target.parentNode).addClass('row_selected');
 				});

 				/* Add a click handler for the delete row */
 				$('#delete').click( function() {
  					var anSelected = fnGetSelected( oTable );
  					oTable.fnDeleteRow( anSelected[0] );
 				});
 				
				if(<%=id_oper%>==2){
					if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
						document.datosReintegro.autorizaRein[1].disabled=true;
					}
	 				$('#autorizaRein').click(function(){
	 					if(<%=!("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
		 					mostrarDialog();
		 					$("#FECHA_APLICACION_CONTABLE").val($("#fechaAp").val());
		 					p.gestion.setFechaApCont($("#FECHA_APLICACION_CONTABLE").val());
		 					if(!document.datosReintegro.autorizaRein[1].checked){
		 						parent.document.getElementById("pb_send").disabled=true;
		 					}
		 					//parent.document.getElementById("pb_save").disabled=true;
	 					}else{
	 						Swal.fire({ icon: 'warning',
										text: "El documento ya se encuentra aplicado contablemente." });	 						
	 						if(!clickEnviar){
	 							parent.document.getElementById("pb_save").click();
	 							parent.document.getElementById("pb_save").disabled=true;
								parent.document.getElementById("pb_send").disabled=false;
								clickEnviar = true;
							}else{
								parent.document.getElementById("pb_send").disabled=true;
							}
	 						document.datosReintegro.autorizaRein[0].disabled=true;
	 						document.datosReintegro.autorizaRein[1].disabled=true;
	 					}
	 					parent.document.getElementById("pb_save").disabled=false;
	 				});
 				}
 				
 				var d = new Date();
 					var day = d.getDate();
 					var month = d.getMonth() + 1;
 					var year = d.getFullYear();
 					var dString = ( day < 10 ? "0" + day: day  ) + "/" + (month < 10 ? "0" + month: month ) + "/" + year;
 					
 				if( $("#fechaEx").val() == "" || "N/A" == $("#fechaEx").val()){
 					$("#fechaEx").val(dString);
 				}
 				
 				if( $("#fechaAp").val() == "" || "N/A" == $("#fechaAp").val()){
 					$("#fechaAp").val(dString);
 				}
 				
 				//if( $("#fechaAcredit").val() == "" || "N/A" == $("#fechaAcredit").val()){
 					//$("#fechaAcredit").val(dString);
 				//}
 				
 		$("#dialog-firmantesUpdate").dialog({
			autoOpen: false,
			height: 620,
			width: 500,
			modal: true,
			buttons: {
				"Aceptar": function() {
					if ($("#cNombreVoBo").val() == "" || $("#cboVoBo").val() == -1) {
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Vº Bº." });
						return;
						} 
		
					if ($("#cNombreAut").val() == "" || $("#cboAutoriza").val() == -1) {
						Swal.fire({ icon: 'warning',
									text: "Falta Ingresar Nombre en Datos Autorizar." });
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
		
					$("#firmanteVoBo").val($("#cNombreVo").val() + " " + $("#cPaternoVo").val() + " " + $("#cMaternoVo").val());
					$("#firmanteAut").val($("#cNombreA").val() + " " + $("#cPaternoA").val() + " " + $("#cMaternoA").val());
					$("#firmanteEla").val($("#cNombreE").val() + " " + $("#cPaternoE").val() + " " + $("#cMaternoE").val());
		
					var msn = "No Se Guardo Correctamente Informacion de Firmantes";

					try{						
						Swal.fire({
							text: "¿Desea actualizar los datos de los firmantes con la informacion capturada?",
							icon: "info",
							showCancelButton: true,
						  	confirmButtonColor: "#7066E0",
						  	cancelButtonColor: "#e6e6e6",
						  	confirmButtonText: "Aceptar",
						  	cancelButtonText: "Cancelar"
						}).then((result) => {
							if(result.isConfirmed){
								queryFormPost("tReintegroAutEncabezadoFirmante_Update", {async: false });
														
								
								if ($("#oficioDelegatorio").prop("checked")){
								
									if($("#cFolioOficio").val() == ""){
										Swal.fire({ icon: 'warning',
													text: "Falta Ingresar el folio de Oficio." });
										return; 
									} else if($("#dFechaOficio").val() == ""){ 
										Swal.fire({ icon: 'warning',
													text: "Falta Ingresar la fecha del Oficio." });									
										return; 
									} else if($("#cNombreTitular").val() == ""){ 
										Swal.fire({ icon: 'warning',
													text: "Falta Ingresar Nombre del Titular." });									
										return; 
									}
																	
									$("#cFolioOficioAux").val($("#cFolioOficio").val());
									$("#dFechaOficioAux").val($("#dFechaOficio").val());
									$("#cNombreTitularAux").val($("#cNombreTitular").val());
									$("#cApellidoPaternoTitularAux").val($("#cApellidoPaternoSup").val());
									$("#cApellidoMaternoTitularAux").val($("#cApellidoMaternoSup").val());
									$("#cPuestoTitularAux").val($("#cPuestoTitular").val());
									$("#tipoSuplenciaAux").val($("#tipoSuplencia").val());
									$("#numeroEmpleadoAutoriza").val($("#cboSuplenteAut").val());
									
									if($("#firmanteOficioExiste").val() == "Existe"){
										queryFormPost({	queryName : "tPagoFirmanteDelagatorioUpdate", async : false, callback : function(){ 
																
																msn = "Firmantes Oficio Delegatorio actualizado correctamente.";
															} 
											  });									
									}else{
										queryFormPost({	queryName : "tPagoFirmanteDelagatorioCreate", async : false, callback : function(){ 
																	
																	msn = "Firmantes Oficio Delegatorio guardado correctamente.";
																} 
												  });
									}
																	
									Swal.fire({ icon: 'success',
												text: msn });
									
								}
								
								if ($("#oficioDeleVoBo").prop("checked")){
								
									if($("#cFolioOficioVoBo").val() == ""){ 
										Swal.fire({ icon: 'warning',
													text: "Falta Ingresar el folio de Oficio." });
										return; 
									} else if($("#dFechaOficioVoBo").val() == ""){ 
										Swal.fire({ icon: 'warning',
											text: "Falta Ingresar la fecha del Oficio." });
										return; 
									} else if($("#cNombreTitularVoBo").val() == ""){ 
										Swal.fire({ icon: 'warning',
													text: "Falta Ingresar Nombre del Titular." });
										return; 
									}
									
									$("#cFolioOficioVoBoAux").val($("#cFolioOficioVoBo").val());
									$("#dFechaOficioVoBoAux").val($("#dFechaOficioVoBo").val());
									$("#cNombreTitularVoBoAux").val($("#cNombreTitularVoBo").val());
									$("#cApellidoPaternoTitularVoBoAux").val($("#cApellidoPaternoSupVoBo").val());
									$("#cApellidoMaternoTitularVoBoAux").val($("#cApellidoMaternoSupVoBo").val());
									$("#cPuestoTitularVoBoAux").val($("#cPuestoTitularVoBo").val());
									$("#tipoSuplenciaVoBoAux").val($("#tipoSuplenciaVoBo").val());
									$("#numeroEmpleadoVoBo").val($("#cboSuplenteVoBo").val());
									
									if($("#firmanteOficioVoBoExiste").val() == "Existe"){
										queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoUpdate", async : false, callback : function(){ 
																
																msn = "Firmantes Oficio Delegatorio VoBo actualizado correctamente.";
															} 
											  });									
									}else{
										queryFormPost({	queryName : "tPagoFirmanteDelegatorioVoBoCreate", async : false, callback : function(){ 
																	
																	msn = "Firmantes Oficio Delegatorio VoBo guardado correctamente.";
																} 
												  });	  
										
									}
																	
									Swal.fire({ icon: 'success',
												text: msn });
								}
								
								
								cmdImprimir("REINTEGROAUT");
								if(parent.document.getElementById("pb_send"))
									parent.document.getElementById("pb_send").disabled=false;
							}else{
								if(parent.document.getElementById("pb_save"))
									parent.document.getElementById("pb_save").disabled=false;
							}
						})
					}catch(e){
						if(parent.document.getElementById("pb_save"))
							parent.document.getElementById("pb_save").disabled=false;
						
						Swal.fire({ icon: 'error',
									text: "No se pudo actualizar los firmantes, intente mas tarde." });						
					}
					
					$("#cboVoBo").empty();
					$("#cboAutoriza").empty();
					
					$(this).dialog("close");
				},
				"Cancelar": function() {
					if(parent.document.getElementById("pb_save"))
						parent.document.getElementById("pb_save").disabled=false;
					$(this).dialog("close");
				}
			},
			close: function(){}							
		});
			
				tbl_RecepcionBien =  $('#tblRecepcionBien').dataTable({
			        "bPaginate": true, "bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true,
        			"bAutoWidth": false, "sScrollY": 270, "sScrollYInner": "100%", "bJQueryUI": true,
					"bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers", "sScrollX": "100%", "sScrollXInner": "110%",
					"bScrollCollapse": true, "bServerSide": true,   
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
				
				tbl_RecepcionServ =  $('#tblRecepcionServ').dataTable({
				    "bPaginate": true, "bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true,
        			"bAutoWidth": false, "sScrollY": 270, "sScrollYInner": "100%", "bJQueryUI": true,
					"bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers", "sScrollX": "100%", "sScrollXInner": "110%",
					"bScrollCollapse": true, "bServerSide": true,   
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
				
				tbl_RecepcionNueva =  $('#tblRecepcionNueva').dataTable({
				    bPaginate: true,
        			bLengthChange: true,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			sScrollX: "100%",
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Datos",
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
					}
				});
				
				tblBoletosReintegrar =  $('#tblBoletosReintegrar').dataTable({
				    bPaginate: true,
        			bLengthChange: true,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			bAutoWidth: false,
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Datos",
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
					}
				});
				
				tblBoletosReint =  $('#tblBoletosReint').dataTable({
				    bPaginate: true,
        			bLengthChange: true,
        			bFilter: true,
        			bSort: false,
        			bInfo: false,
        			sScrollX: "100%",
					bJQueryUI: true,
					bRetrive : true,
					bDestroy : true,
					sPaginationType: "full_numbers",
					oLanguage: {
						sProcessing: "Procesando...",
						sLengthMenu: "Mostrar _MENU_ registros",
						sZeroRecords: "No hay registros a mostrar",
						sEmptyTable: "No hay Datos",
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
					}
				});
				
				$("#btnRecepcion").hide();
				creaDlgRecepcion();
				creaDlgBoletos();
				muestraCapturaRecepcion();
				muestraBotonBoletos();
				
 			});  //fin del ready

		
			function cmdRegresar(){
				self.location="../caso/principal.jsp";
			}
		 
		 function onlyMoney(evt) {
			var keyPressed = (evt.which) ? evt.which : event.keyCode;
			if (keyPressed == 46 || keyPressed == 36)
				return true;
			return (keyPressed >= 48 && keyPressed <= 57);
		}
		
		  function fnImportarExcel() {
			var msgAlert = "";
			if ($("#importExcel").val() == "") {
				msgAlert += "El archivo Excel es requerido";
			}
		
			if (msgAlert != "") {
				Swal.fire({ icon: 'warning',
							text: msgAlert });				
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
		
		function fnExportaSicop() {
				$.blockUI( {
					message : "Procesando espere ......"
				});
				document.ExportaSicop.submit();
				$.unblockUI();
				$("#Exportar").attr('disabled','disabled');
				return true;
		}
		
		function fnExportaDecrementoSicop(){
			$.blockUI( {
				message : "Procesando espere ......"
			});
			document.ExportaDecrementoSicop.submit();
			$.unblockUI();
			$("#ExportarD").attr('disabled','disabled');
			return true;
		}
		
		 $(function() {
			
			$("#tblReintegros tbody").dblclick(function(event){
				var rowIndex = oTable.fnGetPosition($(event.target.parentNode)[0]);
				//editRow(oTable,rowIndex);		
			});
		});		 
		 
		 function mostrarDialog(){
			$('#dialog').dialog('option', 'modal', true).dialog('open');	 
		 }
		 
		 function mostrarDialogAut(){
			$('#dialogAut').dialog('option', 'modal', true).dialog('open');	 
		 }
	var editable = true;
	function editRow ( oTable, nRow ) {
		var importe;
		var ep;
		if(editable){
			importe = 	$('#tblReintegros tbody tr:eq('+nRow+') td:eq(4)').html()*1;//se multiplica por 1 para convertirlo en un número y no se quede como texto
			//ep = $('#tblReintegros tbody tr:eq('+nRow+') td:eq(3)').html();
			editable=false;
			$('#tblReintegros tbody tr:eq('+nRow+') td:eq(4)').html('<td><input type="text" id="edit'+nRow+'" value="'+importe+'"></td>');
			//$('#tblReintegros tbody tr:eq('+nRow+') td:eq(3)').html('<td><input type="text" id="editep'+nRow+'" value="'+ep+'"></td>');
		}
			$('#tblReintegros input').bind('keypress', function(e) {         
				if(e.keyCode==13){                         
					var valor = $('#edit'+nRow).val()*1; //se multiplica por 1 para convertirlo en un número y no se quede como texto
					$('#tblReintegros tbody tr:eq('+nRow+') td:eq(4)').html(valor);
					$("#mImportePos").val(valor);
					$("#mImporteNeg").val(valor*-1);
					$("#nFolioRein").val(<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>);
					$("#nDocRenglonR").val(nRow+1);
					var imp = $("#importe").val()*1;
					var nuevo = imp+valor-importe;
					$("#importe").val(nuevo);
					$("#mImporteLCR").val($("#importe").val());
					$("#nFolioRein").val(<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>);
					$("#nFolioR").val(<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>);
					queryFormPost("tReintegroEImporteUpdate", {async: false });
					queryFormPost("tReintegroImporteUpdate", {async: false });
					editable = true;
					oTable.fnDraw();
				} 
			});
		} 
		
	function aplicaCont(){
			$("#dialog").dialog("close");			
			Swal.fire({
				text: "Enviara el documento para aplicar el apartado persupuestal.  \n \n  ¿desea continuar?",
				icon: "warning",
				showCancelButton: true,
			  	confirmButtonColor: "#7066E0",
			  	cancelButtonColor: "#e6e6e6",
			  	confirmButtonText: "Aceptar",
			  	cancelButtonText: "Cancelar"
			}).then((result) => {
				if(result.isConfirmed){
					parent.document.getElementById("pb_save").disabled=true;
				   	var strAction="../servlet/ReintegrosServlet";
				   	$.blockUI({message: "Procesando espere ......"});
				   		$.ajax({
				   			datatype:"html",
							type: "POST",
							url: strAction,
							data:{aplica:1},
							success: function (data,textStatus){
								//$("#mensajeMotor").val(data);
								//$('#dialogMotor').dialog('option', 'modal', false).dialog('open');
				   				$.unblockUI();
							},
							error: function (par) {
								Swal.fire({ icon: 'error',
											text: "<%=mensaje%>" });						
								}
						});
				} else
					return false;
			})   		
   	}
	
	function aplicaContAut(){
			$("#dialogAut").dialog("close");
			var fAcredit = $("#fechaAcredit").val();
			var cEsFinAnio = $("#esFinAnio").val();
			var folio = $("#nFolioPago").val();

			Swal.fire({
				text: "Enviara el Documento a Autorizar Contablemente. \n ¿Desea continuar?",
				icon: "warning",
				showCancelButton: true,
			  	confirmButtonColor: "#7066E0",
			  	cancelButtonColor: "#e6e6e6",
			  	confirmButtonText: "Aceptar",
			  	cancelButtonText: "Cancelar"
			}).then((result) => {
				if(result.isConfirmed){
				   	var strAction="../servlet/ReintegrosServlet";
				   	$.blockUI({message: "Procesando espere ......"});
				   		$.ajax({
				   			datatype:"html",
							type: "POST",
							url: strAction,
							data:{autoriza:1,fAcredit:fAcredit,folio:folio,cEsFinAnio:cEsFinAnio},
							success: function (data,textStatus){
								$("#mensajeMotor").val(data);
								$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
				   				//$.unblockUI();
				   				document.liberardocumento.submit();
							},
							error: function (par) {
								Swal.fire({ icon: 'error',
											text: "<%=mensaje%>" });						
								}
						});
				} else
					return false;
			})
   	}
	
	function cancelarDoc(){		
		Swal.fire({
			text: "Enviara el documento para Cancelar el apartado persupuestal. \n ¿Desea continuar?",
			icon: "warning",
			showCancelButton: true,
		  	confirmButtonColor: "#7066E0",
		  	cancelButtonColor: "#e6e6e6",
		  	confirmButtonText: "Aceptar",
		  	cancelButtonText: "Cancelar"
		}).then((result) => {
			if(result.isConfirmed){
		   		parent.document.getElementById("pb_save").disabled=false;
			 	parent.document.getElementById("pb_save").click();//es para que se calcule el responsable y operacion
		   		//parent.document.getElementById("pb_cancel").disabled=true;
		   		parent.document.getElementById("pb_leave").disabled=true;
		   		parent.document.getElementById("pb_save").disabled=true;
		   		parent.document.getElementById("pb_send").disabled=true;
		   		document.getElementById("Cancelar").disabled=true;
		   		var strAction="../servlet/ReintegrosServlet";
				$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{cancela:1},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
				   		$.unblockUI();
					},
					error: function (par) {
						Swal.fire({ icon: 'error',
									text: "<%=mensaje%>" });
						}
				});
		   		
		   		parent.document.getElementById("pb_close").disabled=false;
			} else
				return false;
		})

   }
	
	function actualizaPagoInfo(){
		var strAction="reintegroPresupuestal.jsp?pago=Si";
		$.blockUI({message: "Procesando espere ......"});
		var lc = $("#lineaCap").val();
		var clvRastreo = $("#clvRastreo").val();
		var fichaDep = $("#deposito").val();
		var clvBanco = $("#cveBanco").val();
		var cuenta = $("#cuenta").val();
		var fAcredit = $("#fechaAcredit").val();
		var ctab
		
		if (rfc == "BMN930209927")
			ctab = $("#CTABAN_FFM").val();
		else
			ctab = $("#CTABAN").val();
				
		$.ajax({
			datatype:"html",
			type: "POST",
			url: strAction,
			data:{rastreo:clvRastreo,lcaptura:lc,fichaDep:fichaDep,clvBanco:clvBanco,cuenta:cuenta,fAcredit:fAcredit,ctab:ctab},
			success: function (data,textStatus){
   				$.unblockUI();
   				Swal.fire({ icon: 'success',
							text: "Datos de pago actualizados." });   				
			},
			error: function (par) {
				Swal.fire({ icon: 'error',
							text: "<%=mensaje%>" });
				}
		});
	}
	
	function limpiarDatos(){
		var folioReintegro = $("#folioReintegro").val();
		$.blockUI({message : "Procesando espere ......"});
		$.ajax({
			datatype:"html",
			type: "POST",
			url: "../servlet/ReintegrosServlet",
			data:{folioReintegro:folioReintegro,borraTodo:1},
			success: function (data,textStatus){
   				$.unblockUI();
   				document.location.reload();
			},
			error: function (par) {
				Swal.fire({ icon: 'error',
							text: "<%=mensaje%>" });				
				}
		});
	}
	function cmdImprimir(elFormato){
		window.open(
			"../admin/SeguridadCatalogos?"
			+ "catalogo=CONTRARECIBO"
			+ "&accion=run"
			+ "&rn=PolizaReintegro.jasper"
			+ "&whereFolio= " + <%=folio %>
			+ "&whereTipo= '" + elFormato+"'", 			
			"popacuse",
			"scrollbars=1, resizable=yes, width=1024, height=768");
	}
	function updateFirmantes(){
		queryFormPost({
			queryName:"existeFirmanteReintegroNormal", 
			async : false, 
			callback:function(){
				if (<%=id_oper%>==1 && $("#existeFirmante").val()=="SIEXISTE" ){
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
				}else
					//$("#dialog-firmantesUpdate").dialog("open");
					modificarFirmantes();
			}
		});
	}
	
	/**
	 * Actualiza los subprogramas segun la seleccion del programa
	 */
	function cambiaPrograma() {
		$("#nIDPrograma").val($("#cboPrograma").val());
		clearSelect("cboSubPrograma");
		querySelectPost("catalogoSubprgRead", "cboSubPrograma", {
			async : false
		});
	}
	/**
	 * Funcion general que limpia el contenido de un select agregando una opcion por
	 * default con valor -1
	 */
	function clearSelect(idSel) {
		for ( var i = 0; i < idSel.length; i++)
			$('#' + idSel[i]).find('option').remove().end().append(
					'<option value="-1"></option>');
	}
	
	/**
	*	Funcion para validar si el beneficiario del reintegro es FIB Banorte.
	*/
	function cargaProgramaSubPrograma(){		
		
		queryFormPost("tReintegroEncProgSubProgRead", {async : false});
		
		
		if (<%=id_oper%>==1 && $("#nIDPrograma").val() == "" && $("#cSubPrograma").val() == ""){
			mostrarOcultarProgSubProg(true, true);
			querySelectPost( "tfideicomisoReintegrosRead", "cboPrograma", {async : false} );
			cambiaPrograma();
		}else{
			if ($("#esFIBBanorte").val()=="SI" ){
				mostrarOcultarProgSubProg(false, true);				
				querySelectPost( "tfideicomisoReintegrosRead", "cboPrograma", {async : false} );				
				$("#cboPrograma").val($("#nIDPrograma").val());
				cambiaPrograma();
				$("#cboSubPrograma").val($("#cSubPrograma").val());								
			}else{
				mostrarOcultarProgSubProg(false, false);
			}
		}
	}
	
	function mostrarOcultarProgSubProg(sel, mostrar){
		if(mostrar){
			$("#lblPrograma").show();
			$("#cboPrograma").show();
			$("#lblSubPrograma").show();
			$("#cboSubPrograma").show();
		}else{
			$("#lblPrograma").hide();
			$("#cboPrograma").hide();
			$("#lblSubPrograma").hide();
			$("#cboSubPrograma").hide();
		}
		
		if(!sel){			
			$("#cboPrograma").attr('disabled','disabled');
			$("#cboSubPrograma").attr('disabled','disabled');
		}else{
			$("#cboPrograma").attr('disabled',false);
			$("#cboSubPrograma").attr('disabled',false);
		}
	}
	
	function validaCapturaProgSubProg(){
		var bRegresa = true;	
	
		if($("#esFIBBanorte").val() == "SI"){			
			if ($("#cboPrograma").val() == "0" || $("#cboSubPrograma").val() == "0" ) {
				Swal.fire({ icon: 'warning',
							text: "Selecione el Programa -- SubPrograma para continuar." });				
				bRegresa = false;			
			}			
		}
		return bRegresa;
	}
	
	function actualizaProgSubProgReintegro(){
	
		if($("#esFIBBanorte").val() == "SI"){
			if ($("#cboPrograma").val() == "0" || $("#cboSubPrograma").val() == "0" ) {
				Swal.fire({ icon: 'warning',
							text: "Selecione el Programa -- SubPrograma para continuar." });				
				bRegresa = false;
			}else{				
				$("#nIDPrograma").val($("#cboPrograma").val());
				$("#cSubPrograma").val($("#cboSubPrograma").val());
				
				queryFormPost("tReintegroEncabezadoProgSubProgUpdate", {async : false});
				queryFormPost("tReintegroDetalleFFMUpdate", {async : false});
			}
		}
	}
	
	function creaDlgRecepcion(){
		$("#dlgRecepcion").dialog(
		{
			autoOpen : false,
			height : 800,
			width : 1300,
			modal : true,
			buttons : {
				"Aceptar" : function() {		
					if(validaRecepcionCapturada()){
						$(this).dialog("close");
					}else{
						Swal.fire({ icon: 'info',
									text: "No ha capturado la Recepción. Verifique!!" });						
					}					
				},
				"Cancelar" : function() {
					$(this).dialog("close");
					dt_RecepcionBien.fnClearTable();
					dt_RecepcionServ.fnClearTable();
					dt_RecepcionNueva.fnClearTable();
				}
			},
			open: function(){				
				
			}
		});
	}
	
	function creaDlgBoletos(){
		$("#dlgBoletos").dialog(
		{
			autoOpen : false,
			height : 800,
			width : 1300,
			modal : false,
			buttons : {
				"Aceptar" : function() {		
					if(validaCapturaBoletos()){
						$(this).dialog("close");
					}else{
						Swal.fire({ icon: 'info',
									text: "No han capturado los boletos de avión correspondientes al reintegro. Favor de dar clic en el botón de Agregar Boletos" });						
					}					
				},
				"Cancelar" : function() {
					$(this).dialog("close");
					dt_BoletosReintegrar.fnClearTable();
					dt_BoletosReint.fnClearTable();
				}
			},
			open: function(){				
				
			}
		});
	}
	function abrirAgregarBoletos()
	{
		queryFormPost("esPagoDeBoletos", {async : false});
		muestraBoletosReintegro();
		muestraBoletosAgregados();

		$('#dlgBoletos').dialog('option', 'modal', true).dialog('open');
				
	}
	
	function OpenDialogRecepcion()
	{
		cargaRecepcion();		
		setTimeout('tbl_RecepcionBien.fnAdjustColumnSizing()',2000);
		setTimeout('tbl_RecepcionServ.fnAdjustColumnSizing()',2000);
		setTimeout('tbl_RecepcionNueva.fnAdjustColumnSizing()',2000);	
		setTimeout('$("#dlgRecepcion").dialog("open")',1000);	
		
	}
	
	function cargaRecepcion()
	{
		
		queryFormPost("esRecepcionBienServicioRead", {async : false});	
		
		if($("#esRecepBienServicio").val() == "1"){ // si el tipo de recepcion es de bien.
			$("#dt_RecepcionBien").show();
			$("#dt_RecepcionServ").hide();
			$("#dt_RecepcionNueva").show();
			muestraTblRecepcionBien();
			
			if(validaRecepcionCapturada()){
				$("#agregarLinea").hide();
				muestraRecepReintegro();
			}
		}else if($("#esRecepBienServicio").val() == "2"){ // si el tipo de recepcion es de servicio.
			$("#dt_RecepcionServ").show();
			$("#dt_RecepcionBien").hide();
			$("#dt_RecepcionNueva").show();
			muestraTblRecepcionServicio();
			
			if(validaRecepcionCapturada()){
				$("#agregarLinea").hide();
				muestraRecepReintegro();
			}
		}
	}
	
	function muestraCapturaRecepcion(){
			
		queryFormPost("esCXPPagoDiversoRead", {async : false});
		
		if($("#esCXPPagoDiverso").val() == "1"){
			queryFormPost("esReintegroTotalParcialRead", {async : false});
			if($("#esReinTotalParcial").val() == "0"){
				$("#btnRecepcion").show();
			}else if($("#esReinTotalParcial").val() == "1"){
				
				queryFormPost("sp_generaRecepcionReintegroCreate", {async : false});
				validaRecepcionCapturada();
			}
		}
	}
	
	function muestraBotonBoletos() {
		
		if($("#esCXPPagoDiverso").val() == "1"){
		
			queryFormPost("esPagoDeBoletos", {async : false});
			
			if($("#esProveedorVuelos").val() == "1"){
				//Si es 1 es reintegro total
				if($("#esReinTotalParcial").val() == "1"){
					$.ajax({
						url: '../gstnmngr/CapturaBoletos',
						type: 'put',
						async:false,
						dataType: 'json',
						data : {cxp: $("#cxp").val(), nFolio: $("#nFolioPago").val()},
						error: function(data) {
							Swal.fire({ icon: 'error',
										text: "Ocurrio un error en el Insert. ["+ data.data_1.result +"]" });							
						},
						success: function(data){
							var exito = data.success;	
							if (exito == "true") {
								Swal.fire({ icon: 'success',
											text: "Se guardaron correctamente los boletos correspondientes a este pago." });																
							} else {
								Swal.fire({ icon: 'error',
											text: "Ocurrio un error al guardar. ["+ data.data_1.result +"]" });								
							}
							
						}
					});
				} else {
					$("#btnAgregarBoletos").show();
				}
			}
		}
	}
	
	function muestraTblRecepcionBien(){
		var parametros = "'" + $("#cIdContrato").val() + "','" + $("#cIdRecepcion").val() + "'";
		var funcion = "fn_mRecepcionPago";
		
		$('#tblRecepcionBien').dataTable().fnClearTable();
		
		tbl_RecepcionBien =  $('#tblRecepcionBien').dataTable({
			        "bPaginate": true, "bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true,
        			"bAutoWidth": false, "sScrollY": 270, "sScrollYInner": "100%", "bJQueryUI": true,
					"bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers", "sScrollX": "100%", "sScrollXInner": "110%",
					"bScrollCollapse": true, "bServerSide": true,
					sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (parametros) +")" ) ,
					aoColumns: [
									{sName: "nIdLineaConsolidado"},						
									{sName: "nCantidad"},
									{sName: "nCantidadDisp"},
									{sName: "mMontoConIVA"},
									{sName: "cantidadAgregada"},
									{sName: "nPocentajeIVA"},//,bVisible: false
									{sName: "precioUnit"}//,bVisible: false
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
	
	function muestraTblRecepcionServicio(){
		var parametros = "'" + $("#cIdContrato").val() + "','" + $("#cIdRecepcion").val() + "'";
		var funcion = "fn_mRecepcionPago";
		
		$('#tblRecepcionServ').dataTable().fnClearTable();
		
		tbl_RecepcionServ =  $('#tblRecepcionServ').dataTable({
			        "bPaginate": true, "bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true,
        			"bAutoWidth": false, "sScrollY": 270, "sScrollYInner": "100%", "bJQueryUI": true,
					"bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers", "sScrollX": "100%", "sScrollXInner": "110%",
					"bScrollCollapse": true, "bServerSide": true,
					sAjaxSource:  window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + ( funcion+"(" + (parametros) +")" ) ,
					aoColumns: [
									{sName: "nIdLineaConsolidado"},
									{sName: "mMontoConIVA"},
									{sName: "mMontoNetoDisp"},
									{sName: "mMontoNetoCap"},
									{sName: "mMontoSinIVA",bVisible: false},
									{sName: "mMontoIVA",bVisible: false},
									{sName: "nPocentajeIVA"},//,bVisible: false
									{sName: "precioUnit"}//,bVisible: false
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
	function agregarBoletos(){
		
		$("#info").val(generaCadenaAgregar());
		
		if($("#info").val() == ""){
			Swal.fire({ icon: 'warning',
						text: "No hay nada que agregar" });				
			return;
		}else{
				$.ajax({
					url: '../gstnmngr/CapturaBoletos',
					type: 'post',
					async:false,
					dataType: 'json',
					data : $("#guardarBoletos").serialize(),
					error: function(data) {
						Swal.fire({ icon: 'errir',
									text: "Ocurrio un error en el Insert. ["+ data.data_1.result +"]" });						
					},
					success: function(data){
						var exito = data.success;	
						if (exito == "true") {
							//alert("Se guardo correctamente");
							muestraBoletosAgregados();
						} else {
							Swal.fire({ icon: 'error',
										text: "Ocurrio un error al guardar. ["+ data.data_1.result +"]" });							
						}
						
					}
				});	
		}			
	}
	
	function agregar(){
		capturaRecepcionNueva();		
	}
	
	function capturaRecepcionNueva(){
		var cadenaLineaConsCant="";
		var token="";
		//Para los ped/contratos de bienes
		if($("#esRecepBienServicio").val() == "1"){
			cadenaLineaConsCant=joinChain(tbl_RecepcionBien, 'BIEN');
			if(cadenaLineaConsCant == ""){
				Swal.fire({ icon: 'warning',
							text: "No hay nada que agregar." });				
				return;
			}else{
				generaRecepcionReintegro(cadenaLineaConsCant);
			}			
		}else if($("#esRecepBienServicio").val() == "2"){//Cuando son servicios
			cadenaLineaConsCant=joinChain(tbl_RecepcionServ, 'SRV');
			if(cadenaLineaConsCant == ""){
				Swal.fire({ icon: 'warning',
							text: "No hay nada que agregar." });				
				return;
			}else{
				generaRecepcionReintegro(cadenaLineaConsCant);
			}			
		}		
	}
	
	function joinChain(oTable, tipo){
		var cadenaLineaConsCant="";
		var token="";
		var aTrs =  oTable.fnGetNodes();
		var montoDisp = 0;
		var montoCap = 0;
		var precioUni = 0;
		var impteReint = 0;
		
		impteReint = $("#importe").val();
		impteReint = impteReint.replace("$", "");
		impteReint = impteReint.replace(/,/g, "");
		
		for ( var i=0 ; i<aTrs.length; i++ )     
		{  
			var nTr = oTable.fnGetData(aTrs[i]);
			if(tipo!='SRV'){ // cuando es una recepcion de un bien.
				precioUni = 0;
				precioUni = nTr[6];
				precioUni = precioUni.replace("$", "");
				precioUni = precioUni.replace(/,/g, "");
				if(parseInt($("#cantAgregar_"+nTr[0]).val(),10)<=parseInt(nTr[2],10) && parseInt($("#cantAgregar_"+nTr[0]).val(),10)>0){
					cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[0]+'-'+$("#cantAgregar_"+nTr[0]).val();
					token=",";
					montoCap = montoCap + (parseFloat(parseInt($("#cantAgregar_"+nTr[0]).val(),10) * precioUni ));
				}else{
					if(parseInt($("#cantAgregar_"+nTr[0]).val(),10)>0)
						Swal.fire({ icon: 'warning',
									text: "En la linea "+nTr[0]+" sobrepasaste la cantidad disponible." });						
				}
			}else{ // cuando es una recepcion de un servicio.
				montoDisp=nTr[2];
				montoDisp = montoDisp.replace("$", "");
				montoDisp = montoDisp.replace(/,/g, "");
				if(parseFloat($("#mMontoNetoCap_"+nTr[0]).val())<=parseFloat(montoDisp) && parseFloat($("#mMontoNetoCap_"+nTr[0]).val())>0.0){
					cadenaLineaConsCant=cadenaLineaConsCant+token+nTr[0]+'-'+$("#mMontoNetoCap_"+nTr[0]).val();
					token=",";
					montoCap = montoCap + (parseFloat($("#mMontoNetoCap_"+nTr[0]).val()));
				}else{
					if(parseInt($("#mMontoNetoCap_"+nTr[0]).val(),10)>0)
						Swal.fire({ icon: 'warning',
									text: "En la linea "+nTr[0]+" sobrepasaste el monto disponible." });						
				}
			}
			
		}
		
		if(tipo!='SRV'){
			montoCap = (montoCap * 1.16).toFixed(2);
		}
		
		if(montoCap > (parseFloat(impteReint))){
			Swal.fire({ icon: 'warning',
						text: "El monto capturado en la recepcion es Mayor al monto del Reintegro. Verifique!!" });			
			cadenaLineaConsCant = "";
		}else if(montoCap < (parseFloat(impteReint))){
			Swal.fire({ icon: 'warning',
						text: "El monto capturado en la recepcion es Menor al monto del Reintegro. Verifique!!" });			
			cadenaLineaConsCant = "";
		}
		
		return cadenaLineaConsCant;
	}
	
	function generaCadenaAgregar(){
		var cadenaBoletosAgregar="";
		var token="!";
		var tblBoletos =  tblBoletosReintegrar.fnGetNodes();
		var montoDisp = 0;
		var montoCap = 0;
		var impteReint = 0;
		
		impteReint = $("#importe").val();
		impteReint = impteReint.replace("$", "");
		impteReint = impteReint.replace(/,/g, "");
		
		for ( var i=0 ; i<tblBoletos.length; i++ )     
		{  
			var nTr = tblBoletosReintegrar.fnGetData(tblBoletos[i]);
			
				montoDisp=nTr[5];
				montoDisp = montoDisp.replace("$", "");
				montoDisp = montoDisp.replace(/,/g, "");
				if(parseFloat($("#mTotalCap_"+nTr[0]).val())<=parseFloat(montoDisp) && parseFloat($("#mTotalCap_"+nTr[0]).val())>0.0){
					
					cadenaBoletosAgregar=cadenaBoletosAgregar + nTr[1] + token+ nTr[2] + token + nTr[3] + token + nTr[4] + token + +$("#mTotalCap_"+nTr[0]).val() + token + nTr[7] +token;
					montoCap = montoCap + (parseFloat($("#mTotalCap_"+nTr[0]).val()));
				}else{
					if(parseFloat($("#mTotalCap_"+nTr[0]).val()) >  parseFloat(nTr[5]))
						Swal.fire({ icon: 'warning',
									text: "En la linea "+nTr[0]+" sobrepasaste el monto del boleto original." });						
					
				}
			
			
		}
				
		if(montoCap > (parseFloat(impteReint))){
			Swal.fire({ icon: 'warning',
						text: "El monto capturado de los boletos es Mayor al monto del Reintegro. Verifique!!" });			
			cadenaBoletosAgregar = "";
		}else if(montoCap < (parseFloat(impteReint))){
			Swal.fire({ icon: 'warning',
						text: "El monto capturado de los boletos es Menor al monto del Reintegro. Verifique!!" });			
			cadenaBoletosAgregar = "";
		}
		
		return cadenaBoletosAgregar;
	}
	
	
	function generaRecepcionReintegro(cadenadet){
		$("#cadenadet").val(cadenadet);
		
		queryFormPost("sp_generaRecepcionReintegroCreate",  {async : false, 
			callback : function() 
			{
				muestraRecepReintegro();
				if(validaRecepcionCapturada()){
					$("#agregarLinea").hide();
				}
			}
		});
	}
	
	function limpiaFirmante(postFijo) {
			$("#cNombre" + postFijo).val("");
			$("#cPaterno" + postFijo).val("");
			$("#cMaterno" + postFijo).val("");
			$("#cPuesto" + postFijo).val("");
		}
		
		function infoEmpleado(tipoFirmante) {
			$("#cNombreEmpleado").val();
			$("#cPaternoEmpleado").val();
			$("#cMaternoEmpleado").val();
			$("#cPuestoEmpleado").val();
			$("#cTipoFirmante").val(tipoFirmante)
		
			var numeroEmpleado = -1;
			var postFijo = ""
		
			if( "VOBO" == tipoFirmante){
		
				numeroEmpleado = $("#cboVoBo").val();
				postFijo = "VoBo";
				$("#nNumEmpleadoVoBo").val( numeroEmpleado );
					
				}else if( "AUT" == tipoFirmante){
					numeroEmpleado = $("#cboAutoriza").val();
					postFijo = "Aut";
					$("#nNumEmpleadoAut").val( numeroEmpleado );
				}else if( "SUPAUT" == tipoFirmante){
					numeroEmpleado = $("#cboSuplenteAut").val();
					postFijo = "Titular";
					
				}else if( "SUPVOBO" == tipoFirmante){
					numeroEmpleado = $("#cboSuplenteVoBo").val();
					postFijo = "TitularVoBo";
					
				}
	
		
			limpiaFirmante(postFijo);
		
			if (parseInt(numeroEmpleado, 10) > 0) {
				$("#nNumEmpleadoBusqueda").val(numeroEmpleado);
				queryFormPost({
					queryName : "infoComplementariaFirmanteRead",
					async : false,
					callback : function() {
						$("#cNombre" + postFijo).val($("#cNombreEmpleado").val());
						$("#cPaterno" + postFijo).val($("#cPaternoEmpleado").val());
						$("#cMaterno" + postFijo).val($("#cMaternoEmpleado").val());
						$("#cPuesto" + postFijo).val($("#cPuestoEmpleado").val());
					}
				});
			}
		
		}
	
	
	function muestraRecepReintegro(){
		var where = "nFolioReintegro = " + $("#folioReint").val() + " AND cIdContrato = '" + $("#cIdContrato").val() + "' AND cIdRecepcion = '" + $("#cIdRecepcion").val() + "'";
		var view = "vw_RecepcionReintegro";
		
		$('#tblRecepcionNueva').dataTable().fnClearTable();
		
		tbl_RecepcionNueva =  $('#tblRecepcionNueva').dataTable({
			        "bPaginate": true, "bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true,
        			"bAutoWidth": false, "sScrollY": 270, "sScrollYInner": "100%", "bJQueryUI": true,
					"bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers", "sScrollX": "100%", "sScrollXInner": "110%",
					"bScrollCollapse": true, "bServerSide": true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=" + view + "&qw="+ where,
					aoColumns: [
									{sName: "nLinea"},
									{sName: "cIdRecepcion"},
									{sName: "nCantidad"},
									{sName: "mMontoConIva"},
									{sName: "mMontoSinIva"},
									{sName: "mMontoIva"},
									{sName: "cIdContrato",bVisible: false}
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
	
	function muestraBoletosReintegro(){
		var cxp = $("#cxp").val();
		cxp = "'" +  cxp.replace(/\s+/g, '') +"'";
		
		$('#tblBoletosReintegrar').dataTable().fnClearTable();
		
		tblBoletosReintegrar =  $('#tblBoletosReintegrar').dataTable({
			        "bPaginate": false, "bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true,
        			"bAutoWidth": false, "sScrollY": 200, "sScrollYInner": "100%", "bJQueryUI": true,
					"bRetrive" : true, "bDestroy" : true, 
					"sPaginationType": "full_numbers",
					 "sScrollX": "100%", "sScrollXInner": "100%",
					"bScrollCollapse": true, "bServerSide": true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=fn_BoletosPagos(" + cxp + ")",
					aoColumns: [	
									{sName: "nid"},
									{sName: "nFolioPagoDiverso"},
									{sName: "RFC"},
									{sName: "cNombre"},
									{sName: "cReferencia"},
									{sName: "mTotal"},
									{sName: "mTotalCap"},
									{sName: "cPartida"}
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
	
	function muestraBoletosAgregados(){
		var where = "nFolioReintegro = " + $("#nFolioPago").val();
		
		$('#tblBoletosReint').dataTable().fnClearTable();
		
			tblBoletosReint =  $('#tblBoletosReint').dataTable({
			        "bPaginate": true, "bLengthChange": true, "bFilter": true, "bSort": true, "bInfo": true,
        			"bAutoWidth": false, "sScrollY": 250, "sScrollYInner": "50%", "bJQueryUI": true,
					"bRetrive" : true, "bDestroy" : true, "sPaginationType": "full_numbers", "sScrollX": "100%", "sScrollXInner": "100%",
					"bScrollCollapse": true, "bServerSide": true,
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vw_BoletosReintegro&qw="+ where,
					aoColumns: [
									
									{sName: "nFolioPagoDiverso"},
									{sName: "RFC"},
									{sName: "cNombre"},
									{sName: "cReferencia"},
									{sName: "mTotal"},
									{sName: "cPartida"}
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
	
	function validaRecepcionCapturada(){
		var bRegresa = false;
		queryFormPost("recepcionCapturadaRead", {async : false});
		
		if($("#recepcionCapturada").val() == "1"){
			bRegresa = true;
		}
				
		return bRegresa;
	}
	
	function validaCapturaBoletos(){
		var bRegresa = false;
		queryFormPost("boletosCapturadosRead", {async : false});
		
		if($("#boletosCapturados").val() == "1"){
			bRegresa = true;
		}
				
		return bRegresa;
	}
	
	function showDivOficio(esUpdate){
		var cmpName = "oficioDelegatorio" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioCaptura" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
	
	function showDivOficioVoBo(esUpdate){
		var cmpName = "oficioDeleVoBo" + (esUpdate?"Update":"");
		var divName = "oficioDelegatorioVoBo" + (esUpdate?"Update":"");
		if( $("#" + cmpName ).is(":checked") )
			$("#"+divName).show();
		else
			$("#"+divName).hide();
	}
	
	function modificarFirmantes(){						
		tipoFirmantes();
		queryFormPost("firmantesModuloRead, firmantesModuloReadAut", {async : false}); 
		queryFormPost("tPagoFirmanteDelagatorioRead", {async: false }); // Para los firmantes de oficio delegatorio en caso de que existan.
		queryFormPost("tPagoFirmanteDelegatorioVoBoRead", {async: false }); // Para los firmantes de oficio delegatorio VoBo en caso de que existan.
		$("#dialog-firmantesUpdate").dialog("open");
	}
	
	function tipoFirmantes() {		
		llenaFirmanteVoBo();
		llenaFirmanteAut();
		llenaSuplenteVoBo();
		llenaSuplenteAut();
		$("#dialog-firmantesUpdate").dialog("open");
	}	
	
	function llenaFirmanteVoBo() {
		$("#cTipoFirmante").val("VOBO")
		querySelectPost("FirmantesPorTipo_Read", "cboVoBo", {
			async : false
		});
	}
	
	function llenaFirmanteAut() {
		$("#cTipoFirmante").val("AUT")
		querySelectPost("FirmantesPorTipo_Read", "cboAutoriza", {
			async : false
		});
	}
	
	function llenaSuplenteVoBo() {
		$("#cTipoFirmante").val("SUPVOBO")
		querySelectPost("FirmantesPorTipo_Read", "cboSuplenteVoBo", {
			async : false
		});
	
	}
	
	function llenaSuplenteAut() {
		$("#cTipoFirmante").val("SUPAUT")
		querySelectPost("FirmantesPorTipo_Read", "cboSuplenteAut", {
			async : false
		});
	}
	
	function habilita(){
		if ( $("#FIN_ANIO").prop("checked")) 
			$("#esFinAnio").val("S");
		 else
			$("#esFinAnio").val("N");		 	
	}
	
	</script>

	</head>
<br/>
	<body id="dt_example">
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3>Reintegros Presupuestales</h3> </div>					
			<hr class="mt-3">	
				
				<div id="dialogMensaje" title="Mensajes">
					<% if(mensaje!=null && !"".equals(mensaje)){%>
						<div class="row d-flex justify-content-center">
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" style="border-style: double;">
								<%=mensaje%>
							</div>
						</div>			
					<%} %>
					<%if (id_oper >= 2 && id_oper<=6) {%>
						<% if(msVariable!=null && !"".equals(msVariable)){%>
							<div class="row d-flex justify-content-center">
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1" style="border-style: double;">
									<%=msVariable%>
								</div>
							</div>								
						<%} %>
					<%}%>
					
				</div>
				<%if (id_oper == 1) {%>			
					<% if(!(archivoExcel.length > 0) && re==null){%>
					<form id="upExcel" name="upExcel" action="../caso/firmardoc?carpeta=2" enctype="multipart/form-data" method="post">
						<div class="row d-flex">
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<input type="file" id="importExcel" name="importExcel" class="form-control form-control-sm" />
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<input type="button" id="ImportarExcel" name="ImportarExcel" value="Subir Excel" class="btn btn-secondary" onclick="fnImportarExcel();"/>
							</div>
						</div>							
					</form>
					
					<%} %>
				<%} %>
				
				<%if (id_oper == 3) {%>
					<form id="ExportaSicop" name="ExportaSicop" action="../gstnmngr/ReintegrosLayoutSicop" method="POST">
						<input type="hidden" id="tipoLayout" name="tipoLayout" value="1"/>
						<div class="row d-flex">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<input type="button" id="Exportar" name="Exportar" value="Exportar SICOP" class="btn btn-secondary" onclick="fnExportaSicop();"/>
							</div>
						</div>
					</form>
				<%}%>
				
				<%if (id_oper == 6) {%>
					<div id="DecrementoSicop">
						<form id="ExportaDecrementoSicop" name="ExportaDecrementoSicop" action="../gstnmngr/ReintegrosLayoutSicop" method="POST">
							<input type="hidden" id="tipoLayout" name="tipoLayout" value="4"/>						
							<div class="row d-flex">
								<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
									<input type="button" id="ExportarD" name="ExportarD" value="Exportar Decremento SICOP" class="btn btn-secondary" onclick="fnExportaDecrementoSicop();"/>
								</div>
							</div>
						</form>
					</div>
				<%}%>
									
				<form id="datosReintegro" name="datosReintegro" action="">
										
					<%if(mensaje==null || "".equals(mensaje.trim())){%>
						<%if (id_oper == 2) {%>
							<div class="row d-flex justify-content">
								<div class="col-12 d-flex justify-content-center">
									<label class="form-label"> ¿Datos correctos?: </label>&nbsp;&nbsp;&nbsp;&nbsp;
									<label for="autorizaSi" class="form-check-label">Si: </label> &nbsp;&nbsp;
									<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value="1" />&nbsp;&nbsp;&nbsp;&nbsp;
									
									<label for="autorizaNo" class="form-check-label">No: </label>&nbsp;&nbsp;
									<input type="radio" name="autorizaRein" id="autorizaRein" class="form-check-input" value="0" />&nbsp;&nbsp;&nbsp;&nbsp;
									
								</div>																		
							</div>
							
							<div class="row d-flex justify-content-center">
								<div class="col-2 d-flex">
									<label class="form-label"> Motivo: </label>
								</div>
								<div class="col-6 d-flex">
									<input type="text" id="motivoRechazo" name="motivoRechazo" size="60" maxlength="200"  class="form-control form-control-sm"/>
								</div>
							</div>
														
							<input type="hidden" id="motivoR" name="motivoR" />
						<%}%>			
								
						<br/>						
						
						<%if (id_oper == 6 && activaEventosFA) {%>
							<div class="row d-flex justify-content-center">
								<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
									Activa si es Reintegro de Fin de Año <input type="checkbox" class="form-check-input" id="FIN_ANIO" name="FIN_ANIO" onclick="habilita()"/>
															
								</div>
							</div>
						<%}%>		
						
						<p><span id="btnRecepcion" ><a class="link-opacity-100-hover" href="#" onclick="OpenDialogRecepcion();" >CAPTURAR LINEAS RECEPCION</a></span></p>				
												
						<input type="hidden" id="mensajeError" name="mensajeError" />
						<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo" : c.getCasoOperacion(0).getResponsable()%>" />
						<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
						<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" />	
						<input type="hidden" name="FECHA_APLICACION_CONTABLE" id="FECHA_APLICACION_CONTABLE" value="<%=today%>" readonly="readonly" maxlength="10" size="10"/>
						<input type="hidden" id="ffm" name="ffm" value="">
						
						<!--  Hidden boletos de avion -->
						<input type="hidden" id="esProveedorVuelos" name="esProveedorVuelos" value="0">
						<input type="hidden" id="boletosCapturados" name="boletosCapturados" value="">
						<input type="hidden" id="cxp" name="cxp" value="">
						<input type="hidden" id="cPasivo" name="cPasivo" value="">
						
						<!-- hidden para los firmantes de VoBo y Autoriza -->
						<input type="hidden" id="cNombreVoBo" name="cNombreVoBo" size=40 >
						<input type="hidden" id="cPaternoVoBo" name="cPaternoVoBo" size=40 >
						<input type="hidden" id="cMaternoVoBo" name="cMaternoVoBo" size=40>
						<input type="hidden" id="cPuestoVoBo" name="cPuestoVoBo" size=40>
						<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40>
						<input type="hidden" id="cNombreAut" name="cNombreAut" size=40 >
						<input type="hidden" id="cPaternoAut" name="cPaternoAut" size=40 >
						<input type="hidden" id="cMaternoAut" name="cMaternoAut" size=40>
						<input type="hidden" id="cPuestoAut" name="cPuestoAut" size=40>
						<input type="hidden" id="firmanteAut" name="firmanteAut" size=40>
		
						<!-- hidden para los firmantes de suplencia de VoBo y Autoriza -->
						<input type="hidden" name="cNombreTitular" id="cNombreTitular" value=""/>
						<input type="hidden" name="cPaternoTitular" id="cPaternoTitular" value=""/>
						<input type="hidden" name="cMaternoTitular" id="cMaternoTitular" value=""/>
						<input type="hidden" name="cPuestoTitular" id="cPuestoTitular" value=""/>
						<input type="hidden" name="cNombreTitularVoBo" id="cNombreTitularVoBo" value=""/>
						<input type="hidden" name="cPaternoTitularVoBo" id="cPaternoTitularVoBo" value=""/>
						<input type="hidden" name="cMaternoTitularVoBo" id="cMaternoTitularVoBo" value=""/>
						<input type="hidden" name="cPuestoTitularVoBo" id="cPuestoTitularVoBo" value=""/>
						
						<!-- hidden para la captura de los datos de quien elaboro -->
						<input type="hidden" id="cNombreE" name="cNombreE" size=40>
						<input type="hidden" id="cPaternoE" name="cPaternoE" size=40>
						<input type="hidden" id="cMaternoE" name="cMaternoE" size=40>
						<input type="hidden" id="cPuestoE" name="cPuestoE" size=40>
						<input type="hidden" id="firmanteEla" name="firmanteEla" size=40>
						
						<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 >
						<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 >
						<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40>
						<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40>
						<input type="hidden" id="cNombreA" name="cNombreA" size=40>
						<input type="hidden" id="cPaternoA" name="cPaternoA" size=40>
						<input type="hidden" id="cMaternoA" name="cMaternoA" size=40>
						<input type="hidden" id="cPuestoA" name="cPuestoA" size=40>
					
						<input type="hidden" id="folioReint" name="folioReint" size=40 value="<%=folio%>">
						<input type="hidden" id="existeFirmante" name="existeFirmante" size=40 value="NOEXISTE">
						
						<input type="hidden" name="tipoSuplenciaAux" id="tipoSuplenciaAux" value="">
						<input type="hidden" name="tipoSuplenciaVoBoAux" id="tipoSuplenciaVoBoAux" value="">
						<input type="hidden" id="TipoAutorizacion" name="TipoAutorizacion" value="">
						<input type="hidden" id="cNombreEmpleado" name="cNombreEmpleado" value="">
						<input type="hidden" id="cPaternoEmpleado" name="cPaternoEmpleado" value="">
						<input type="hidden" id="cMaternoEmpleado" name="cMaternoEmpleado" value="">
						<input type="hidden" id="cPuestoEmpleado" name="cPuestoEmpleado" value="">
						<input type="hidden" id="cTipoFirmante" name="cTipoFirmante" value="">
						<input type="hidden" name="nNumEmpleado" id="nNumEmpleado" value=""/>
						<input type="hidden" name="nNumEmpleadoBusqueda" id="nNumEmpleadoBusqueda" value=""/>
						<input type="hidden" id="numeroEmpleadoVoBo" name="numeroEmpleadoVoBo" value="">
						<input type="hidden" id="numeroEmpleadoAutoriza" name="numeroEmpleadoAutoriza" value="">
										
						<!-- hidden para la captura de Programa -- SubPrograma cuando el beneficiario es FIB Banorte 'BMN930209927'-->
						<input type="hidden" id="esFIBBanorte" name="esFIBBanorte" value="" />
						<input type="hidden" id="RFCFIBBanorte" name="RFCFIBBanorte" value="BMN930209927" />
						<input type="hidden" id="cSubPrograma" name="cSubPrograma" value="" />
						<input type="hidden" id="nIDPrograma" name="nIDPrograma"  value="">
						
						<!-- hidden para verificar si la cuenta por pagar es de un pago diverso -->
						<input type="hidden" id="esCXPPagoDiverso" name="esCXPPagoDiverso" value="" />
						<input type="hidden" id="recepcionCapturada" name="recepcionCapturada" value="" />
						<input type="hidden" id="esReinTotalParcial" name="esReinTotalParcial" value="" />
						<input type="hidden" id="cIdContrato" name="cIdContrato" value="" />
						<input type="hidden" id="cIdRecepcion" name="cIdRecepcion" value="" />
						<input type="hidden" id="esRecepBienServicio" name="esRecepBienServicio" value="" />
						<input type="hidden" id="cadenadet" name="cadenadet" value="" />
						<input type="hidden" id="cCentroContable" name="cCentroContable" value="<%=cCentroContable%>">	
																				
						<!-- hidden para la captura de oficio delegatorio -->
						<input type="hidden" name="cFolioOficioAux" id="cFolioOficioAux" value="">
						<input type="hidden" name="dFechaOficioAux" id="dFechaOficioAux" value="">
						<input type="hidden" name="cNombreTitularAux" id="cNombreTitularAux" value="">
						<input type="hidden" name="cApellidoPaternoTitularAux" id="cApellidoPaternoTitularAux" value="">
						<input type="hidden" name="cApellidoMaternoTitularAux" id="cApellidoMaternoTitularAux" value="">
						<input type="hidden" name="cPuestoTitularAux" id="cPuestoTitularAux" value="">
						<input type="hidden" name="firmanteOficioExiste" id="firmanteOficioExiste" value="">
						
						<!-- hidden para la captura de oficio delegatorio VoBo-->
						<input type="hidden" name="cFolioOficioVoBoAux" id="cFolioOficioVoBoAux" value="">
						<input type="hidden" name="dFechaOficioVoBoAux" id="dFechaOficioVoBoAux" value="">
						<input type="hidden" name="cNombreTitularVoBoAux" id="cNombreTitularVoBoAux" value="">
						<input type="hidden" name="cApellidoPaternoTitularVoBoAux" id="cApellidoPaternoTitularVoBoAux" value="">
						<input type="hidden" name="cApellidoMaternoTitularVoBoAux" id="cApellidoMaternoTitularVoBoAux" value="">
						<input type="hidden" name="cPuestoTitularVoBoAux" id="cPuestoTitularVoBoAux" value="">
						<input type="hidden" name="firmanteOficioVoBoExiste" id="firmanteOficioVoBoExiste" value="">						
						
						<!-- hidden para lo numeros de empleados de los firmantes -->
						<input type="hidden" name="nNumEmpleadoVoBo" id="nNumEmpleadoVoBo" value=""/>
						<input type="hidden" name="nNumEmpleadoAut" id="nNumEmpleadoAut" value=""/>
										
						<input name="cDocumento" type="hidden" id="cDocumento" value="<%=c.getTipoCaso().getGavetaAsociada()%>">
						<input name="nFolioPago" id="nFolioPago" type="hidden" value="<%=folio%>">
						<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value = "<%=cUR%>">
						<input type="hidden" id="esFinAnio" name="esFinAnio" value = "N">
						<input type="hidden" id="esViatico" name="esViatico" value = "NO">
						<input type="hidden" id="existeComisionReintegro" name="existeComisionReintegro" value = "NO">						
						<input type="hidden" id="nIdComisionViatico" name="nIdComisionViatico" value = "">
						<input type="hidden" id="cEventoComision" name="cEventoComision" value = "">		
						<input type="hidden" id="totalComision" name="totalComision" value = "">
						<input type="hidden" id="RFCEmpleado" name="RFCEmpleado" value = "">
						<input type="hidden" id="cCuentaBeneficiario" name="cCuentaBeneficiario" value = "">
						<input type="hidden" id="nIdComisionRG" name="nIdComisionRG" value = "">
						<input type="hidden" id="folioAnticipo" name="folioAnticipo" value = "">
						<input type="hidden" id="esComprobacion" name="esComprobacion" value = "NO">
						<input type="hidden" id="importeNegativo" name="importeNegativo" value = "">						
						<input type="hidden" id="cTipoPago" name="cTipoPago" value = "">																							
						
						<div class="row d-flex justify-content">
							<div class="col">
								<div class="accordion accordion-flush" id="temario">
									<div class="accordion-item">
										<h2 class="accordion-header" id="encabezado-1">							
											<button	class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#principal" aria-expanded="true" aria-controles="principal">
												PRINCIPAL
											</button>
										</h2>	
										
										<div id="principal" class="accordion-collapse collapse show" aria-labelledby="encabezado-1">					
											<div class="accordion-body">			
											
												<div class="row d-flex">
													<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
														<label for="FOLIO" class="form-label"> Folio </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" class="form-control form-control-sm" name="FOLIO" id="FOLIO" size="15" value="<%=c.getFolio() %>" readonly>
													</div>
													<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
													</div>
													<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
														<label for="fechaSol" class="form-label"> Fecha Sol. </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" class="form-control form-control-sm" name="fechaSol" id="fechaSol" size="15" value="<%=today%>" readonly>
													</div>
													<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
													</div>
													<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
														<label for="importe" class="form-label"> Total </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<div class="input-group">
															<span class="input-group-text"><i class="bi bi-currency-dollar"></i></span>					
															<input  type="text" id="importe" name="importe" class="form-control form-control-sm" readonly>
														</div>	
													</div>
												</div>	
												<br/>
												<div id="divViaticos">
													<div class="form-group row">
														<div class="col-4">
														</div>
														<label class="col-2 col-form-label" for="mPasaje">Detalle Comision </label>														
														<label class="col-2 col-form-label" for="mPasaje">Rubro a Reintegrar </label>
													</div>
													<div class="form-group row">
														<div class="col-2">
														</div>
													  	<label class="col-2 col-form-label" for="mPasaje">Pasajes </label>
													  	<div class="col-2">								  		
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasaje" name="mPasaje" value="0.00" readonly/>			              					
											              	</div>
										              	</div>
										              	<div class="col-2">								  		
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasajeR" name="mPasajeR" value="0.00"/>			              					
											              	</div>
										              	</div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mTaxi">Taxi </label>
									              		<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxi" name="mTaxi" value="0.00" readonly/>
											              	</div>
										             	</div>
										             	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxiR" name="mTaxiR" value="0.00" />
											              	</div>
										             	</div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mPeaje">Combustible y peaje</label>
									              		<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeaje" name="mPeaje" value="0.00" readonly/>
											              	</div>
											            </div>
											            <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeajeR" name="mPeajeR" value="0.00" />
											              	</div>
											            </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mHotel">Factura(s) de Hotel </label>
										              	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-building"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mHotel" name="mHotel" value="0.00" readonly/>
											              	</div>
											            </div>
											            <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-building"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mHotelR" name="mHotelR" value="0.00" />
											              	</div>
											            </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mConsumos">Consumos </label>
										              	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-cash"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mConsumos" name="mConsumos" value="0.00" readonly/>
											              	</div>
											              </div>
											              <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-cash"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mConsumosR" name="mConsumosR" value="0.00" />
											              	</div>
											              </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mOtros">Otros </label>
													   	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-coin"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mOtros" name="mOtros" value="0.00" readonly/>
											              	</div>
											              </div>
											              <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-coin"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mOtrosR" name="mOtrosR" value="0.00" />
											              	</div>
											              </div>
										            </div>	
										            
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
													  	<label class="col-2 col-form-label" for="mPasajeLocal">Pasajes </label>
													  	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasajeLocal" name="mPasajeLocal" value="0.00" readonly/>
											              	</div>
											             </div>
											             <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-bus-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPasajeLocalR" name="mPasajeLocalR" value="0.00" />
											              	</div>
											             </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mTaxiLocal">Taxi </label>
										              	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxiLocal" name="mTaxiLocal" value="0.00" readonly/>
											              	</div>
											            </div>
											            <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-taxi-front"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mTaxiLocalR" name="mTaxiLocalR" value="0.00" />
											              	</div>
											            </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mPeajeLocal">Peaje y Estacionamiento</label>
													   	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeajeLocal" name="mPeajeLocal" value="0.00" readonly/>
											              	</div>
											            </div>
											            <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mPeajeLocalR" name="mPeajeLocalR" value="0.00" />
											              	</div>
											            </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mGasolinaLocal">Combustible</label>
													   	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mGasolinaLocal" name="mGasolinaLocal" value="0.00" readonly/>
											              	</div>
											            </div>
											            <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-fuel-pump"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mGasolinaLocalR" name="mGasolinaLocalR" value="0.00" />
											              	</div>
											            </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mMaritimoLocal">Maritimos</label>
													   	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-tsunami"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mMaritimoLocal" name="mMaritimoLocal" value="0.00" readonly/>
											              	</div>
											            </div>
											            <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-tsunami"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mMaritimoLocalR" name="mMaritimoLocalR" value="0.00" />
											              	</div>
											            </div>
										            </div>
										            <div class="form-group row">
										            	<div class="col-2">
														</div>
										              	<label class="col-2 col-form-label" for="mMaritimoLocal">Aereos</label>
													   	<div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-airplane"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mAereoLocal" name="mAereoLocal" value="0.00" readonly/>
											              	</div>
											            </div>
											            <div class="col-2">
														   	<div class="input-group">
												    			<span class="input-group-text"><i class="bi bi-airplane"></i></span>
								              					<input type="text" class="form-control form-control-sm col-2 inputDetalle" id="mAereoLocalR" name="mAereoLocalR" value="0.00" />
											              	</div>
											            </div>
										            </div>
										        </div>
																						
												<div class="row d-flex">
													<%if((id_oper==8 || id_oper==7) && !("true".equals(c.getCasoDato("CANCELADO_CONT").getValor()))){ %>
														<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																													
															<input type="button" class="btn btn-link btn-sm" value="Aviso de Reintegro" onclick="window.open('../reportes?cmd=<%=GestionInterface.RPT_MOVIMIENTOS_REINT%>&folio_reintegro=<%=c.getFolio()%>&nfolio=<%=folio %>');"/>																												
														</div>
													<%} %>
													<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
														<span id="EditaFirmas" style="visibility:hidden"><a href="#" onclick="updateFirmantes();">Firmas*</a></span>
													</div>																					
												</div>
												
												<%if( id_oper >= 1 || id_oper >= 2 ){ %>					
													<div class="row d-flex justify-content">
														<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
															<div class="input-group">
																<span class="input-group-text"><i class="bi bi-printer"></i></span>																	
																<input type="button" class="btn btn-secondary btn-sm" name="btnImprimir" id="btnImprimir" value="Reimprimir Poliza" onclick="cmdImprimir('REINTEGROAUT')" >																
															</div>
															<span id="btnFirmas" ><a href="#" onclick="modificarFirmantes();" >*Firmas</a></span>
														</div>
													</div>															
												<%}%>																
												
												<%if(id_oper>=3 && id_oper<5){ %>
													<div class="row d-flex justify-content">
														<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
															<input type="button" id="Cancelar" name="Cancelar" value="Cancelar Documento" class="btn btn-outline-danger btn-sm" onclick="javascript:cancelarDoc();"/>
														</div>
													</div>
												<%} %>																									
												
											</div>
										</div>										
									</div>
									
									<!-- PARA HACER EL UPDATE DE MONTOS -->
										<input type="hidden" id="mImportePos" name="mImportePos" />
										<input type="hidden" name="mImporteNeg" id="mImporteNeg" />
										<input type="hidden" name="mImporteLCR" id="mImporteLCR" />
										<input type="hidden" name="nFolioRein" id="nFolioRein" />
										<input type="hidden" name="nDocRenglonR" id="nDocRenglonR" />
										<input type="hidden" id="nFolioR" name="nFolioR" />
										<input type="hidden" name="volante" id="volante" value="0">
									<!-- ****************************** -->	
																		
									<div class="accordion-item">
										<h2 class="accordion-header" id="encabezado-2">							
											<button	class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#restante" aria-expanded="true" aria-controles="restante">
												CARACTERISTICAS
											</button>
										</h2>	
										
										<div id="restante" class="accordion-collapse collapse" aria-labelledby="encabezado-2">					
											<div class="accordion-body">
																				
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="aviso" class="form-label"> Aviso Reintegro: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" name="aviso" id="aviso" class="form-control form-control-sm">
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="folioDep" class="form-label"> Folio Dependencia: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" name="folioDep" id="folioDep" class="form-control form-control-sm">
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="fechaEx" class="form-label"> Fecha Exp.: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<div class="input-group">
															<span class="input-group-text"><i class="bi bi-calendar"></i></span>	
															<input type="text" name="fechaEx" id="fechaEx" class="form-control form-control-sm">
														</div>
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="CatMovimientoReintegro" class="form-label"> Movto: </label>
													</div>												
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<div class="input-group">
															<input type="text" name="CatMovimientoReintegro" id="CatMovimientoReintegro" size="5" class="form-control form-control-sm AyudaSyC">
														</div>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="fechaAp" class="form-label"> Fecha Apl: </label>
													</div>												
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">		
														<div class="input-group">
															<span class="input-group-text"><i class="bi bi-calendar"></i></span>												
															<input type="text" name="fechaAp" id="fechaAp" size="5" class="form-control form-control-sm" value="<%=(today.contains(aEjercicioFiscal))?today:"31/12/"+aEjercicioFiscal%>" readonly>
														</div>														
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="fechaAp" class="form-label"> Cta Bancaria: </label>
													</div>												
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">														
														<%if("BMN930209927".equals(rfc)){ %>
															<div class="input-group">														
																<input type="text" name="CTABAN_FFM" id="CTABAN_FFM" value="" class="form-control form-control-sm AyudaSyC" onkeydown="return ctaKeyDwn(event, this.id)" onfocus="cierraAyuda()" readonly/>
															</div>																				
														<%} else {%>
															<div class="input-group">
																<input type="text" name="CTABAN" id="CTABAN" value="" class="form-control form-control-sm AyudaSyC" onkeydown="return ctaKeyDwn(event, this.id)" onfocus="cierraAyuda()" readonly/>
															</div>
														<%}%>			
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="CatTipoCausaAvisoReintegro" class="form-label"> Tipo de Aviso: </label>
													</div>												
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<div class="input-group">
															<input type="text" name="CatTipoCausaAvisoReintegro" id="CatTipoCausaAvisoReintegro" class="form-control form-control-sm AyudaSyC">
														</div>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="CatCausaAvisoReintegro" class="form-label"> Causa del Aviso: </label>
													</div>												
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">						
														<div class="input-group">								
															<input type="text" name="CatCausaAvisoReintegro" id="CatCausaAvisoReintegro" class="form-control form-control-sm AyudaSyC"/>
														</div>														
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="CatFormaPagoAvisoReintegro" class="form-label"> Forma de Pago: </label>
													</div>												
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">																												
														<div class="input-group">														
															<input type="text" name="CatFormaPagoAvisoReintegro" id="CatFormaPagoAvisoReintegro" value="" class="form-control form-control-sm AyudaSyC"/>
														</div>																																		
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="observaciones" class="form-label"> Observaciones: </label>
													</div>												
													<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
														<textarea class="form-control form-control-sm" rows=3 cols="35" id="observaciones" name="observaciones"></textarea>
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="concepto" class="form-label"> Concepto: </label>
													</div>												
													<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
														<textarea class="form-control form-control-sm" rows=3 cols="35" id="concepto" name="concepto"></textarea>
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label id="lblPrograma" for="cboPrograma" class="form-label"> Programa: </label>
													</div>												
													<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
														<select class="form-select form-select-sm" id="cboPrograma" name="cboPrograma" onchange="cambiaPrograma()"></select>
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label id="lblSubPrograma" for="cboSubPrograma" class="form-label"> SubPrograma: </label>
													</div>												
													<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
														<select class="form-select form-select-sm" id="cboSubPrograma" name="cboSubPrograma" onchange="cambiaPrograma()"></select>
													</div>
												</div>
												
											</div>
										</div>												
									</div>
									<div class="accordion-item">
										<h2 class="accordion-header" id="encabezado-3">							
											<button	class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#pago" aria-expanded="true" aria-controles="pago">
												PAGO
											</button>
										</h2>	
										
										<div id="pago" class="accordion-collapse collapse" aria-labelledby="encabezado-3">					
											<div class="accordion-body">									
																			
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="clvRastreo" class="form-label"> Clave de Rastreo: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" name="clvRastreo" id="clvRastreo" class="form-control form-control-sm">
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="deposito" class="form-label"> Ficha de Dep&oacute;sito: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" name="deposito" id="deposito" class="form-control form-control-sm">
													</div>
												</div>
												
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="cveBanco" class="form-label"> Cve. Banco: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" name="cveBanco" id="cveBanco" class="form-control form-control-sm">
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="cuenta" class="form-label"> Cta Bancaria: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" name="cuenta" id="cuenta" class="form-control form-control-sm">
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="fechaAcredit" class="form-label"> Fecha Aut.: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<div class="input-group">
															<span class="input-group-text"><i class="bi bi-calendar"></i></span>								
															<input type="text" name="fechaAcredit" id="fechaAcredit" class="form-control form-control-sm" readonly>
														</div>
													</div>
												</div>
															
												<div class="row d-flex">
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<label for="lineaCap" class="form-label"> Linea Cap.: </label>
													</div>
													<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
														<input type="text" name="lineaCap" id="lineaCap" class="form-control form-control-sm">
													</div>
												</div>
																											
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</form>
				</div>
			
				<br/>
				
				<div id="multitabs" style="width: 80%" class="container">									
					<div class="table-responsive">	    
						<table id="tblReintegros" class="table table-striped">						
							<thead>
								<tr>
									<th>noCLC</th>
									<th>sec</th>
									<th style="width: 60px">EP</th>
									<th>mImporte</th>
									<th>Cuenta Por Pagar</th>									
								</tr>
							</thead>
							<tbody>
								<tr>
									<td style="text-align: center;"></td><td style="text-align: center;"></td>
									<td style="text-align: center;"></td><td style="text-align: center; width:100px;"></td>
									<td style="text-align: center;"></td><td style="text-align: center;"></td>
								</tr>
							</tbody>							
						</table>
					</div>					
				</div>
				
				<div style="width: 80%" class="container">								
					<%if(id_oper==1 && re!=null){%>				
						<form id="limpiaDatos" name="limpiaDatos" method="POST" action="../servlet/ReintegrosServlet" /></form>
						<input type="hidden" name="folioReintegro" id="folioReintegro" value="<%=folio%>"/>
						<input type="hidden" name="borraTodo" id="borraTodo" value="1"/>
						
						<div class="row d-flex">							
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<input type="button" class="btn btn-secondary btn-sm" name="borrarTodo" id="borrarTodo" value="Limpiar Datos" onclick="limpiarDatos();"/>						
							</div>						
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">								
								<input type="button" class="btn btn-secondary btn-sm" name="btnAgregarBoletos" id="btnAgregarBoletos" value="Agragar Boletos" onclick="abrirAgregarBoletos();"/>
							</div>
						</div>
					<%}%>
																	
					<%if(id_oper==2 || id_oper==7){ %>
						<input id="btnMostrar" name="btnMostrar" type="hidden" value="Mostrar Detalle" onclick="mostrarDialog()"  />
					<%}%>
									
					<div id="dialog" title="Detalle de Reintegros">
						<div class="row">
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex">
								<p>Aplica Apartado Presupuestal </p>															
							</div>
						</div>	
						<div class="row">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">														
								<input type="button" class="btn btn-secondary btn-sm" id="aplicarContable" value="Aplicar apartado" onclick="aplicaCont();" />
							</div>
						</div>	
					</div>
					
					<div id="dialogAut" title="Detalle de Reintegros">
						<div class="row">
							<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex">
								<p>Autorización de Reintegros del Presupuesto</p>								
							</div>
						</div>
						<div class="row">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex">								
								<input type="button" class="btn btn-secondary btn-sm" id="aplicarContableAut" value="Autorizar contablemente" onclick="aplicaContAut();" />
							</div>
						</div>
					</div>
		
					<div id="dlgError" title="Mensajes del sistema Aviso de Reintegros">
						<p>Mensajes del sistema </p> <a rel=""></a>
						<table   class="display" id="grdAnteProyecto">
							<tr> <td>
								<textarea id="textmensajeError" name="textmensajeError" rows="16" cols="140"></textarea>
							</td></tr>
						</table>
					</div>
					<% }%>
				</div>
	
		<div id="dialogMotor" title="Mensajes del sistema Aviso de Reintegros">
			<p>Mensajes del sistema </p> <a rel=""></a>
			<textarea id="mensajeMotor" name="mensajeMotor" class="form-control form-control-sm" rows="8" cols="120"></textarea>
		</div>
		
		<form id="liberardocumento" name="liberardocumento"
				action="../gstnmngr/gestion?cmd=1" target="content-iframe"
				method="post">
		</form>
	
		<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
			<div class="row">
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
					<input type="checkbox" class="form-check-input" id="oficioDelegatorio" name="oficioDelegatorio" onclick="showDivOficio(false);"/>Oficio Delegatorio Autoriza
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">
					<input type="checkbox" class="form-check-input" id="oficioDeleVoBo" name="oficioDeleVoBo" onclick="showDivOficioVoBo(false);"/>Oficio Delegatorio VoBo
				</div>
			</div>								
		
			<br/>
			
			<h5> Datos VºBº </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboVoBo" class="form-label"> VoBo: </label>		
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<select class="form-select form-select-sm" id="cboVoBo" name="cboVoBo" onchange="infoEmpleado('VOBO');"> </select>
				</div>
			</div>
			
			<br/>
			
			<h5> Datos Autoriza </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Autoriza: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<select class="form-select form-select-sm" id="cboAutoriza" name="cboAutoriza" onchange="infoEmpleado('AUT');"> </select>
				</div>
			</div>	
			
			<br/>
			
			<h5> Datos Elabora </h5>
			<hr class="mt-3">		
				
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreEla" name="cNombreEla" size=40 maxlength="70" value="<%=cNombreElabora%>"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Ap. Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPaternoEla" name="cPaternoEla" size=40 maxlength="70" value="<%=cApellidoPaternoElabora%>"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Ap. Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cMaternoEla" name="cMaternoEla" size=40 maxlength="70" value="<%=cApellidoMaternoElabora%>"/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cboAutoriza" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoEla" name="cPuestoEla" size=40 maxlength="70" value="<%=cPuestoElabora%>"/>
				</div>
			</div>				
			
			<br/>
			
			<div id="oficioDelegatorioCaptura">			
				<h5> Datos del Suplente </h5>
				<hr class="mt-3">		
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cFolioOficio" class="form-label"> No Oficio: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
						<input type="text" class="form-control form-control-sm" id="cFolioOficio" name="cFolioOficio" size=30 maxlength="70" />
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="dFechaOficio" class="form-label"> Fecha: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
							<input type="text" class="form-control form-control-sm" id="dFechaOficio" name="dFechaOficio" readonly/>
						</div>
					</div>
				</div>		
				
				<div class="row">					
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="tipoSuplencia" class="form-label"> T Suplencia: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="tipoSuplencia" name="tipoSuplencia" > </select>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cboSuplenteAut" class="form-label"> Autoriza: </label>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="cboSuplenteAut" name="cboSuplenteAut" onchange="infoEmpleado('SUPAUT');"> </select>
					</div>
				</div>
				
			</div>
			
			<br/>
			
			<div id="oficioDelegatorioVoBo">
				<h5> Datos del Suplente VoBo</h5>
				<hr class="mt-3">	
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cFolioOficioVoBo" class="form-label"> No Oficio: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">						
						<input type="text" class="form-control form-control-sm" id="cFolioOficioVoBo" name="cFolioOficioVoBo" size=30 maxlength="70" />
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="dFechaOficioVoBo" class="form-label"> Fecha: </label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex">			
						<div class="input-group">
							<span class="input-group-text"><i class="bi bi-calendar"></i></span>			
							<input type="text" class="form-control form-control-sm" id="dFechaOficioVoBo" name="dFechaOficioVoBo" readonly/>
						</div>
					</div>
				</div>		
				
				<div class="row">					
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="dFechaOficioVoBo" class="form-label"> T Suplencia: </label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="tipoSuplenciaVoBo" name="tipoSuplenciaVoBo" > </select>
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
						<label for="cboSuplenteVoBo" class="form-label"> VoBo: </label>
					</div>
					<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">						
						<select class="form-select form-select-sm" id="cboSuplenteVoBo" name="cboSuplenteVoBo" onchange="infoEmpleado('SUPVOBO');"> </select>
					</div>
				</div>
				
			</div>
			
		</div>
		
		<div id="dlgRecepcion" class="container" style="width: 80%" >
			<h5> Recepción </h5>
			<hr class="mt-3">
						
			<span class="label"><b>Captura de Datos.</b></span>
			<div id="dt_RecepcionBien" class="table-responsive">			
				<table id="tblRecepcionBien" class="table table-striped">	    			
					<thead >
						<tr>
							<th align="center">Linea</th>									
							<th align="center">CANTIDAD<br />Total</th>
							<th align="center">CANTIDAD<br />Disponible</th>
							<th align="center">Monto <br />Total</th>
							<th align="center">CANTIDAD<br />A <br />Agregar</th>
							<th align="center">IVA</th>
							<th align="center">PrecioU</th>
						</tr>										
					</thead>
				</table>
			</div>
			
			<br/>
			
			<div id="dt_RecepcionServ" class="table-responsive">
    			<table id="tblRecepcionServ" class="table table-striped" >
					<thead >
						<tr>
							<th>Linea</th>									
							<th>Monto Total</th>
							<th>Monto Total Disp</th>
							<th>Monto Con IVA</th>
							<th>Monto Sin IVA</th>
							<th>Monto IVA</th>
							<th>IVA</th>
							<th>PrecioU</th>
						</tr>										
					</thead>
				</table>
			</div>
			
			<div class="row d-flex justify-content-left">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					<input type="button" class="btn btn-secondary btn-sm" id="agregarLinea" name="agregarLinea" value="Agregar" onclick="agregar()"/>
				</div>
			</div>
			
			<br/>
			
			<h5> Recepción a generar</h5>
			<hr class="mt-3">		
						
			<span class="label"><b>Lineas Agregadas.</b></span>
			<div id="dt_RecepcionNueva" class="table-responsive">
				<table id="tblRecepcionNueva" class="table table-striped" > 
					<thead>
						<tr>
							<th align="center">Linea</th>
							<th align="center">IdRecepMat</th>
							<th align="center">Cantidad</th>
							<th align="center">MontoConIVA</th>
							<th align="center">MontoSinIVA</th>
							<th align="center">MontoIVA</th>
							<th style="display: none;">IdPedCont</th>															
						</tr>
					</thead>
				</table>
			</div>
		
		</div>
		
		<div id="dlgBoletos" class="container">
			<form id="guardarBoletos" name="guardarBoletos" method="POST" action="../gstnmngr/CapturaBoletos">
				<input type="hidden" name="info" id="info" /> 
				<input name="nFolio" id="nFolio" type="hidden" value="<%=folio%>">
			</form>
			
			<h5> Captura de Boletos </h5>
			<hr class="mt-3">
			
			<div id="dt_BoletosReintegrar" class="table-responsive">
    			<table id="tblBoletosReintegrar" class="table table-striped" >
					<thead >
						<tr>
							<th align="center">Id</th>
							<th align="center">Folio <br/> Pago</th>									
							<th align="center">RFC </th>
							<th align="center">Nombre </th>
							<th align="center">Referencia</th>
							<th align="center">Total</th>
							<th align="center">Importe a<br/> Reintegrar</th>
							<th align="center">Partida</th>
						</tr>										
					</thead>
				</table>
			</div>
			
			<div class="row d-flex justify-content-left">
				<div class="col-12 col-lg-10 col-md-10 col-sm-12">
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12">
					<input type="button" class="btn btn-secondary btn-sm" id="agregarBoletos" name="agregarBoletos" value="Agregar" onclick="agregarBoletos()"/>
				</div>
			</div>
			
			<h5> Boletos Agregados </h5>
			<hr class="mt-3">
			
			<div id="dt_BoletosReint" class="table-responsive">
				<table id="tblBoletosReint" class="table table-striped" > 
					<thead>
						<tr>
							<th align="center">Folio Pago<br/> Diverso</th>
							<th align="center">RFC</th>
							<th align="center">Nombre</th>
							<th align="center">Referencia</th>
							<th align="center">Total</th>
							<th align="center">Partida</th>
						</tr>
					</thead>
				</table>
			</div>
		</div>
		
	</body>
</html>
