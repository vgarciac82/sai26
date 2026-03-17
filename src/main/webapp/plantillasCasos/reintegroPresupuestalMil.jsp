<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="com.syc.contable.ReintegrosMilBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.ReintegroEncabezado"%>
<%@page import="com.syc.contable.core.ReintegroDetalle"%>
<%@page import="com.syc.contable.core.ReintegroEncabezadoMil"%>
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
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

	final int folio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	String msVariable = c.getCasoDato("MENSAJE").getValor(); //Recuperamos el mensaje del caso
	
	ReintegrosMilBusinessLogic reintegro = new ReintegrosMilBusinessLogic(GestionInterface.ATT_CONEXION);

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
	
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1); //el numero que está primero indica el num de carpeta
	//la raiz es la 0, se puede ver con el numero que viene después de la c al pasar el mouse en el arcbol. El segundo numero es el numero de archivo dentro de esa carpeta, empieza en el 1.
	archivoPDFCLC = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, 1);
	archivoPDFCXP = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 3, 2);
	archivoLineaCaptura = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 4, 1);
	archivoPDFComprobante = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 5, 1);
	archivoReporteSicop = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, 1);
	archivoReporteSiaff = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 6, 2);

	String mensaje = "";
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
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	String mensajeCompensada="";
	if(reintegro.getTipoCLC(folio))
		mensajeCompensada="Se está afectando una CLC compensada";
	    
	String mensajeError="";
	try{
	    if (request.getParameter("leeExcel") != null && request.getParameter("leeExcel").equals("1")) {
			if(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1).length>0)
			    reintegro.leeArchivoExcel(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1)[0].getAbsolutePath(),c,usuario,folio);
		}
	}catch(Exception ex){
	 	mensajeError = ex.getMessage();   
	}
	
	ReintegroEncabezadoMil re = reintegro.getReintegroEncabezadoNuevo(folio);
		
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);
	
	ConfiguraAplicativoBusinessLogic configSys = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
	boolean activaEventosFA = "S".equals( configSys.getSystemSetting("ACTIVA_EVENTOS_FA") );
	
	%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
	<head>
		<title>Reintegros Presupuestales Capítulo Mil</title>

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
		var activaEventosFA = <%=activaEventosFA%>;
		
		function onPostSubmit(id_oper){//validaciones del boton enviar
		  		//var valida_doctos_requeridos = true;
		  		//validaciones de documentos requeridos
		  		//return valida_doctos_requeridos;
		  		clickEnviar=true;		  			  	
		  		if(<%=re==null%>){
			  		if(id_oper==1 && <%=archivoExcel.length==0%>){//ejemplo para validar archivos adjuntos
						alert('el excel no puede estar vacio');
						return;
					}
				}if(id_oper==1 && <%=archivoPDFCLC.length==0%>){
					alert('favor de subir el archivo complementario de CLC');
					return;
				}else if(id_oper==1 && <%=archivoPDFCXP.length==0%>){
					alert('favor de subir el archivo complementario de Cuenta por Pagar');
					return;
				}else if(id_oper==1){
					if(!clickEnviar){
						parent.document.getElementById("pb_send").disabled=false;
						clickEnviar = true;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}
				}
				if(id_oper==1 && $("#CTABAN").val() == ""){
					alert("Favor de capturar la cuenta bancaria");
					return;
				}else 
					actualizaPagoInfo();	
		  		
		  		if(id_oper == 5 && <%=archivoPDFComprobante.length==0%>){
				alert('favor de subir el comprobante en la sección de adjuntos');
				return;
			}else if(id_oper == 5 && <%=archivoPDFComprobante.length>0%>){
				if(<%="1".equals(re!=null?re.getTipoAviso():"")%>){
					if($("#clvRastreo").val()=='N/A'){
						alert("Favor de actualizar la clave de rastreo");
						return;
					}
					else
						actualizaPagoInfo();
				}
			}
		  	
		  	if(id_oper == 4 && <%=archivoLineaCaptura.length==0%>){
				alert('favor de subir el archivo de linea de captura en la sección de adjuntos');
				return;
			}else if(id_oper == 4 && <%=archivoLineaCaptura.length>0%>){
					if(<%="1".equals(re!=null?re.getTipoAviso():"")%>){
				  		if($("#lineaCap").val()=='N/A' || $("#CTABAN").val()==""){
							alert("Favor de actualizar la linea de captura y la cuenta bancaria");
							return;
						}
				  		else
				  			actualizaPagoInfo();
			  		}
		  	}
					
				if(id_oper == 6 && <%=archivoReporteSicop.length==0%>){
					parent.document.getElementById("pb_send").disabled=true;
					alert('favor de subir el Reporte de SICOP en la sección de adjuntos');
					return;
				}else if(id_oper == 6 && <%=archivoReporteSiaff.length==0%>){
					parent.document.getElementById("pb_send").disabled=true;
					alert('favor de subir el Reporte de SIAFF en la sección de adjuntos');
					return;
				}else if (id_oper == 6 && $("#fechaAcredit").val()=='N/A' || $("#fechaAcredit").val()==""){
					parent.document.getElementById("pb_save").disabled=false;
					parent.document.getElementById("pb_send").disabled=true;
					alert('Favor de seleccionar la fecha de autorización');
					return;
				}else if(id_oper==6 && <%=archivoReporteSicop.length>0%> && <%=archivoReporteSiaff.length>0%>){
					if(!clickEnviar){
						parent.document.getElementById("pb_send").disabled=false;
						clickEnviar = true;
					}else{
						parent.document.getElementById("pb_send").disabled=true;
					}
				}
				if(!clickEnviar){
					parent.document.getElementById("pb_send").disabled=false;
					clickEnviar = true;
				}else{
					parent.document.getElementById("pb_send").disabled=true;
					parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_leave").disabled=true;
					parent.document.getElementById("pb_cancel").disabled=true;
				}
				
		  		return true;
		  	}
		
	function ResponsableSiguiente(id_oper){
			if(id_oper==1)
	  		 	return "REVISOR_REINTEGRO_MIL";
	  		if(id_oper==2){
	  			 if(document.datosReintegro.autorizaRein[1].checked)
	  				 return "CAPTURISTA_REINTEGRO_MIL";
	  			 else
	  		 		return "AUTORIZADOR_REINTEGRO_MIL";
	  		}
	  		if(id_oper==3)
	  		 	return "AUTORIZADOR_REINTEGRO_MIL";
	  		if(id_oper==4)
	  		 	return "CAPTURISTA_REINTEGRO_MIL";
	  		if(id_oper==5)
		  		return "AUTORIZADOR_REINTEGRO_MIL";
	  		if(id_oper==6)
	  			return "CONSULTA_REINTEGRO_MIL";
		 }
		
		function OperacionSiguiente(id_oper){
			if(id_oper==1)
				return "revisa_reintegro_mil";
			if(id_oper==2){
				if(document.datosReintegro.autorizaRein[1].checked)
					return "captura_reintegro_m";
			  	else
			   	 	return "genera_layout_mil";
	  		}
			if(id_oper==3)
	  		 	return "adjunta_linea_mil";
			if(id_oper==4){
	  			return "digitaliza_comp_mil";
  		 	}
	  		if(id_oper==5){
	  			return "autoriza_reintegro_m";
  		 	}
	  		if(id_oper==6){
			  	return "consulta_reintegro_m";
  		 	}
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
			  			alert(msgAlert);
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
						if($("#cIngreso").val() == "1"){
							if($("#cIdRFC_RelacionGasto").val() == ""){
								Swal.fire({ icon: 'warning',
											text: "Favor de seleccionar el RFC del empleado." });
								return;
							}
							
							cambiaRFC();							
						} 
												
						if(<%=re!=null%>){
							parent.document.getElementById("pb_send").disabled=false;						
							updateFirmantes();					
						}
						$("#btnFirmas").show();
						
					}
						
			  		if (id_oper==2){
			  			if(document.datosReintegro.autorizaRein[1].checked && $("#motivoRechazo").val()==""){
				  			alert("Motivo de rechazo es requerido");
				  			//parent.document.getElementById("pb_send").disabled=true;
				  			return false;
			  			}else if(document.datosReintegro.autorizaRein[1].checked){
			  				parent.document.getElementById("pb_send").disabled=false;
			  				parent.document.getElementById("pb_save").disabled=true;
			  			}else if(!(document.datosReintegro.autorizaRein[0].checked || document.datosReintegro.autorizaRein[1].checked)){
			  				alert("Favor de marcar si los datos son correctos o no");
			  				parent.document.getElementById("pb_save").disabled=false;
			  				return false;
			  			}else{
			  				if(<%=("true".equals(c.getCasoDato("APLICADO_CONT").getValor())) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
								parent.document.getElementById("pb_send").disabled=false;
							}
			  				$("#motivoR").val($("#motivoRechazo").val());
			  				$("#mensajeError").val($("#motivoRechazo").val());
			  				$("#nFolioR").val(<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>);
			  				queryFormPost("tReintegroEncabezadoMilUpdate", {async:false});
			  			}
		  			}
			  		if(<%=c.getIdGabinete()%>!=-1){
						parent.document.getElementById("pb_save").disabled=true;
						if(id_oper==3 || id_oper==7){
							parent.document.getElementById("pb_send").disabled=true;
							parent.document.getElementById("pb_save").disabled=true;
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
			  			actualizaPagoInfo();
			  			parent.document.getElementById("pb_send").disabled=false;
			  		}
			  		if( id_oper == 6 && (  $("#fechaAcredit").val()=='N/A' || $("#fechaAcredit").val()=="" ) ){
			  		parent.document.getElementById("pb_save").disabled=false;
					parent.document.getElementById("pb_send").disabled=true;
					alert('Favor de seleccionar la fecha de autorización');
					return;
			  		}
			  		if(id_oper == 6){
			  			 if(<%=!("true".equals(c.getCasoDato("AUTORIZADO_CONT").getValor())) && !("true".equals(c.getCasoDato("CANCELADO_CONT").getValor())) %>){
								if(muestraAplicar)
									mostrarDialogAut();
		 						muestraAplicar = false;
						}else{
			  				alert("El documento ya se encuentra aplicado");
			  					parent.document.getElementById("pb_send").disabled=false;
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
		  
		  	if (<%=id_oper%> == 7 ){
				$("#divImprimePoliza").show();
				$("#EditaFirmas").css('visibility', 'visible');
				document.getElementById("ckIngreso").disabled = true;
			}else
		  		$("#divImprimePoliza").hide();
		  		$("#divEmpleado").hide();
		  		
			  if(<%=mensajeCompensada.length()>0 && !"".equals(mensajeCompensada) && id_oper!=7%>)
				 alert('<%=mensajeCompensada%>'); 
			  if(<%=mensajeError!=null && !"".equals(mensajeError)%>){
					$("#textmensajeError").val('<%=mensajeError.replace("'","")%>');
					$('#dlgError').dialog('option', 'modal', true).dialog('open');
				}
			  <% if(mensaje==null || "".equals(mensaje.trim()) &&(id_oper>1)){%>
				  if(parent.document.getElementById("pb_cancel"))
						parent.document.getElementById("pb_cancel").disabled=true;
			  <%}%>
			  
			  $("#FOLIO").attr('disabled','disabled');
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
					/*
					if(< %=mensajeErroresValidacion.length()>0%>){
						alert('< %=mensajeErroresValidacion%>');
					}
					*/
					if($("#mensajeError").val()!=""){
						alert($("#mensajeError").val());
					}
					$("#clvRastreo").attr('disabled','disabled');			  			
			  		$("#deposito").attr('disabled','disabled');
			  		$("#lineaCap").attr('disabled','disabled');			  		
			  		$("#cveBanco").attr('disabled','disabled');
			  		$("#cuenta").attr('disabled','disabled');		  			
			  		$("#fechaEx").attr('disabled','disabled');	
			  		//$("#fechaAcredit").attr('disabled','disabled');
			  		$("#CTABAN").attr('disabled','disabled')
			  		
					if(<%=c.getIdGabinete()%>==-1)
		  				parent.document.getElementById("pb_save").click();//esto es para asegurar que se crea el expediente antes de subir el archivo
		  		
				}
			 	
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
			  			$("#cIngreso").val('<%=re!=null?re.getcIngreso():"0"%>');
			  						  			
			  			if($("#cIngreso").val() == "1"){
			  				document.getElementById("ckIngreso").checked = true;
			  				document.getElementById("ckIngreso").disabled = true;
			  				$("#divEmpleado").show();
			  			} else
			  				$("#divEmpleado").hide();
			  			
			  			if($("#fechaAcredit").val()=='null')
			  				$("#fechaAcredit").val('N/A');
					
					if(<%=id_oper%>  == 2 ){
						if(<%="true".equals(c.getCasoDato("APLICADO_CONT").getValor()) && c.getCasoDato("APLICADO_CONT").getValor()!=null%>){
							parent.document.getElementById("pb_cancel").disabled=true;
						}else{
							parent.document.getElementById("pb_cancel").disabled=false;
						}
						//parent.document.getElementById("pb_save").disabled=true;
						document.getElementById("ckIngreso").disabled = true;
					}
					
					if(<%=id_oper%>  == 6){
						$("#clvRastreo").attr('disabled',false);			  			
				  		$("#deposito").attr('disabled',false);
				  		$("#lineaCap").attr('disabled',false);
				  		$("#cveBanco").attr('disabled',false);
				  		$("#cuenta").attr('disabled',false);
				  		$("#fechaEx").attr('disabled',false);
				  		$("#fechaAcredit").attr('disabled',false);
				  		$("#CTABAN").attr('disabled',true);
				  		document.getElementById("ckIngreso").disabled = true;
				  		if(parent.document.getElementById("pb_cancel"))
							parent.document.getElementById("pb_cancel").disabled=true;
			  		}
					if(<%=id_oper%>  == 7){ //Consulta
						document.getElementById("ckIngreso").disabled = true;
						parent.document.getElementById("pb_save").disabled=true;
						parent.document.getElementById("pb_send").disabled=true;
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
			  			//$("#fechaAcredit").attr('disabled','disabled');
			  			//$("#cveBanco").attr('disabled','disabled');
			  			//$("#cuenta").attr('disabled','disabled');
			  			$("#folioDep").attr('disabled','disabled');
			  			destroyDatePickers();
			  			if(<%=id_oper!=5 %>){
				  			$("#clvRastreo").attr('disabled','disabled');			  			
				  			$("#deposito").attr('disabled','disabled');
				  			$("#cveBanco").attr('disabled','disabled');
				  			$("#cuenta").attr('disabled','disabled');	
				  			//$("#fechaAcredit").attr('disabled','disabled');
			  			}
			  			if(<%=id_oper!=4%>)
			  				$("#lineaCap").attr('disabled','disabled');
			  			$("#observaciones").attr('disabled','disabled');
			  			$("#concepto").attr('disabled','disabled');
			  			document.getElementById("ckIngreso").disabled = true;
					}
					if(<%=id_oper%>  >= 5){ 
						parent.document.getElementById("pb_cancel").disabled=true;
					}
					if(<%=id_oper%>  == 2){
						parent.document.getElementById("pb_cancel").disabled=true; //por lo pronto
					}
		  	}
  		
  	function setDatePickers(){
	  	$( "#fechaSol" ).datepicker({
	  		dateFormat: "dd/mm/yy",
			autoclose: true,
			changeYear: true,
			changeMonth: true
		});
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
		//$( "#fechaAcredit" ).datepicker("destroy");
	}
  	
	$(document).ready(function(){
		setDatePickers();
		$("#ImportarExcel").button();
		$("#borrarTodo").button();
		$("#aplicarContable").button();
		$("#aplicarContableAut").button();
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
					sAjaxSource: window.location.protocol + "//" + window.location.host + "/" + window.location.pathname.split("/")[1] + "/crud?rt=t&ql=vtReintegroDetMil&qw=nFolioReintegroMil="+<%= c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)%>,
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
 					/*$(oTable.fnSettings().aoData).each(function (){
   						$(this.nTr).removeClass('row_selected');
   						var aPos = oTable.fnGetPosition( this.nTr );
    					// Get the data array for this row
    					var aData = oTable.fnGetData( aPos[0] );
 					});*/
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
	 						alert("El documento ya se encuentra aplicado contablemente");
	 						if(!clickEnviar){
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
 		$("#dialog-firmantesUpdate").dialog({
			autoOpen: false,
			height: 620,
			width: 500,
			modal: true,
			buttons: {
				"Aceptar": function() {
					if($.trim($("#cNombreVoBoUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Vº Bº"); return; } 
					else if($.trim($("#cPaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Vº Bº"); return; }
					else if($.trim($("#cMaternoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Vº Bº"); return; }
					else if($.trim($("#cPuestoVoBoUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Vº Bº"); return; }					
					
					if($.trim($("#cNombreAutUpdate").val()) == ""){ alert("Falta Ingresar Nombre en Datos Autorizar"); return; } 
					else if($.trim($("#cPaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Paterno en Datos Autorizar"); return; }
					else if($.trim($("#cMaternoAutUpdate").val()) == ""){ alert("Falta Ingresar Apellido Materno en Datos Autorizar"); return; }
					else if($.trim($("#cPuestoAutUpdate").val()) == ""){ alert("Falta Ingresar Puesto en Datos Autorizar"); return; }	
					
					$("#cNombreVo").val($("#cNombreVoBoUpdate").val());
					$("#cPaternoVo").val($("#cPaternoVoBoUpdate").val());
					$("#cMaternoVo").val($("#cMaternoVoBoUpdate").val());
					$("#cPuestoVo").val($("#cPuestoVoBoUpdate").val());
					$("#cNombreA").val($("#cNombreAutUpdate").val());
					$("#cPaternoA").val($("#cPaternoAutUpdate").val());
					$("#cMaternoA").val($("#cMaternoAutUpdate").val());
					$("#cPuestoA").val($("#cPuestoAutUpdate").val());
					
					$("#firmanteVoBo").val($("#cNombreVo").val()+" "+$("#cPaternoVo").val()+" "+$("#cMaternoVo").val());
					$("#firmanteAut").val($("#cNombreA").val()+" "+$("#cPaternoA").val()+" "+$("#cMaternoA").val());
					try{
						if (confirm("¿Desea actualizar los datos de los firmantes con la informacion capturada?")){
							queryFormPost("tReintegroAutEncabezadoMilFirmante_Update", {async: false });
							alert("Firmantes Actualizados Correctamente.");
							$("#cNombreVoBoUpdate").val("");
							$("#cPaternoVoBoUpdate").val("");
							$("#cMaternoVoBoUpdate").val("");
							$("#cPuestoVoBoUpdate").val("");
							$("#cNombreAutUpdate").val("");
							$("#cPaternoAutUpdate").val("");
							$("#cMaternoAutUpdate").val("");
							$("#cPuestoAutUpdate").val("");;
							cmdImprimir("REINTEGROAUTMIL");
						}
					}catch(e){
						alert("No se pudo actualizar los firmantes, intente mas tarde.");
					}
					$(this).dialog("close");
				},
				"Cancelar": function() {
					$(this).dialog("close");
				}
			},
		close: function(){}							
	});
 			});  //fin del ready

		
			function cmdRegresar(){
				self.location="../caso/principal.jsp";
			}
		  
		  function fnImportarExcel() {
		  var msgAlert = "";
			if ($("#importExcel").val() == "") {
				msgAlert += "El archivo Excel es requerido";
			}
		
			if (msgAlert != "") {
				alert(msgAlert);
				return false;
			} else {
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
		 $(function() {			
			$( '#lineaCaptura' ).accordion({
				collapsible: true,
				autoHeight: false,
				active: false
			});			
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
			if(!confirm("Enviara el Documento a Aplicar Contablemente.  \n \n  ¿desea continuar?")) {
				return false;
			}
		   	var strAction="../servlet/ReintegrosServlet";
		   	$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{aplicaMil:1},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   				$.unblockUI();
					},
					error: function (par) {alert('<%=mensaje%>');}
				});
		   		
   	}
	
	function aplicaContAut(){
			$("#dialogAut").dialog("close");
			var fechaAcredit = $("#fechaAcredit").val();
			var cEsFinAnio = $("#esFinAnio").val();
			if(!confirm("Enviara el Documento a Autorizar Contablemente.  \n \n  ¿desea continuar?")) {
				return false;
			}
		   	var strAction="../servlet/ReintegrosServlet";
		   	$.blockUI({message: "Procesando espere ......"});
		   		$.ajax({
		   			datatype:"html",
					type: "POST",
					url: strAction,
					data:{autorizaMil:1,fechaAcredit:fechaAcredit,cEsFinAnio:cEsFinAnio},
					success: function (data,textStatus){
						$("#mensajeMotor").val(data);
						$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   				$.unblockUI();
					},
					error: function (par) {alert('<%=mensaje%>');}
				});
   	}
	
	function cancelarDoc(){
		if(!confirm("Enviara el Documento a Cancelar Contablemente.  \n \n  ¿desea continuar?")) {
			return false;
		}
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
			data:{cancelaMil:1},
			success: function (data,textStatus){
				$("#mensajeMotor").val(data);
				$('#dialogMotor').dialog('option', 'modal', true).dialog('open');
		   		$.unblockUI();
			},
			error: function (par) {alert('<%=mensaje%>');}
		});
		/*function MostrarDialogError() {
			$('#dlgError').dialog('option', 'modal', true).dialog('open');
			return true;
		}*/

   }
	
	function actualizaPagoInfo(){
		var strAction="reintegroPresupuestalMil.jsp?pago=Si";
		$.blockUI({message: "Procesando espere ......"});
		var lc = $("#lineaCap").val();
		var clvRastreo = $("#clvRastreo").val();
		var fichaDep = $("#deposito").val();
		var clvBanco = $("#cveBanco").val();
		var cuenta = $("#cuenta").val();
		var fAcredit = $("#fechaAcredit").val();
		var ctab = $("#CTABAN").val();
		$.ajax({
			datatype:"html",
			type: "POST",
			url: strAction,
			data:{rastreo:clvRastreo,lcaptura:lc,fichaDep:fichaDep,clvBanco:clvBanco,cuenta:cuenta,fAcredit:fAcredit,ctab:ctab},
			success: function (data,textStatus){
   				$.unblockUI();
   				//alert("Datos de pago actualizados");
			},
			error: function (par) {alert('<%=mensaje%>');}
		});
	}
	
	function limpiarDatos(){
		var folioReintegro = $("#folioReintegro").val();
		$.blockUI({message : "Procesando espere ......"});
		$.ajax({
			datatype:"html",
			type: "POST",
			url: "../servlet/ReintegrosServlet",
			data:{folioReintegro:folioReintegro,borraTodo:2},
			success: function (data,textStatus){
   				$.unblockUI();
   				document.location.reload();
			},
			error: function (par) {alert('<%=mensaje%>');}
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
		$("#dialog-firmantesUpdate").dialog("open");
	}
	
	function habilita(){
		if ( $("#FIN_ANIO").prop("checked")) 
			$("#esFinAnio").val("S");
		 else
			$("#esFinAnio").val("N");		 	
	}
	
	function modificarFirmantes(){								
		$("#dialog-firmantesUpdate").dialog("open");
	}
	
	function guardaOpcion(){								
		var miCheckbox = document.getElementById('ckIngreso');
		
		if(miCheckbox.checked){	
			$("#cIngreso").val("1");
			queryFormPost("tReintegroDEventoIUpdate", {async: false });
			$("#divEmpleado").show();
		} else {
			$("#cIngreso").val("0");
			queryFormPost("tReintegroDEventoUpdate", {async: false });
			$("#divEmpleado").hide();
		}	
				
		queryFormPost("tReintegroEIngresoUpdate", {async: false });
		document.getElementById("ckIngreso").disabled = true;
	}
	
	/**
	 * Funcion para cargar Ventana con el Catalogo de Empleados
	 */
	 function cat_beneficiario() {
		$("#cTipoRfc").val("3");		
		window.open('../Generador/CatalogoBeneficiariosRG.jsp?formName=datosReintegro&inputRFCTarget=cIdRFC_RelacionGasto&inputDRFCTarget=cnombre', 'Beneficiarios', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');				
	}
	
	 function obtenTipoClaveBenf() {
		queryFormPost("obtenerTipoCBEN", {
			async : false
		});
	}
	
	function cambiaRFC(){		
		queryFormPost("tReintegroRFCINUpdate", {async: false });
		queryFormPost("tReintegroPasivoINUpdate", {async: false });
	}
	 
	</script>

	</head>
<br/>
	<body id="dt_example">
		<div id="container" class="container" style="width: 80%">
			<div class="card-header"> <h3>Reintegros Presupuestales Capítulo Mil</h3> </div>					
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
					<input type="hidden" id="tipoLayout" name="tipoLayout" value="2"/>
					<div class="row d-flex">
						<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
							<input type="button" id="Exportar" name="Exportar" value="Exportar SICOP" class="btn btn-secondary" onclick="fnExportaSicop();"/>
						</div>
					</div>
				</form>
			<%}%>
			
			<form id="datosReintegro" name="datosReintegro" action="">			
				<% if(mensaje==null || "".equals(mensaje.trim())){%>
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
							<label class="form-label"> Activa si es Reintegro de Fin de Año </label>
							<input type="checkbox" class="form-check-input" id="FIN_ANIO" name="FIN_ANIO" onclick="habilita()"/>
													
						</div>
					</div>						
				<%}%>
				
				<input type="hidden" id="mensajeError" name="mensajeError" />
				<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0) == null ? "caso operacion nulo" : c.getCasoOperacion(0).getResponsable()%>" />
				<input type="hidden" name="FECHA_SOLICITUD" id="FECHA_SOLICITUD" value="<%=today%>" />
				<input type="hidden" name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" value="<%=aEjercicioFiscal%>" />	
				<input type="hidden" name="FECHA_APLICACION_CONTABLE" id="FECHA_APLICACION_CONTABLE" value="<%=today%>" readonly="readonly" maxlength="10" size="10"/>
				
				<input type="hidden" id="cNombreVo" name="cNombreVo" size=40 />
				<input type="hidden" id="cPaternoVo" name="cPaternoVo" size=40 />
				<input type="hidden" id="cMaternoVo" name="cMaternoVo" size=40 />
				<input type="hidden" id="cPuestoVo" name="cPuestoVo" size=40 />
				<input type="hidden" id="firmanteVoBo" name="firmanteVoBo" size=40 />
				<input type="hidden" id="cNombreA" name="cNombreA" size=40 />
				<input type="hidden" id="cPaternoA" name="cPaternoA" size=40 />
				<input type="hidden" id="cMaternoA" name="cMaternoA" size=40 />
				<input type="hidden" id="cPuestoA" name="cPuestoA" size=40 />
				<input type="hidden" id="firmanteAut" name="firmanteAut" size=40 />
				<input type="hidden" id="folioReint" name="folioReint" size=40 value="<%=folio%>" />
				<input type="hidden" id="esFinAnio" name="esFinAnio" value = "N" />
				<input type="hidden" id="cIngreso" name="cIngreso"/>
				<input type="hidden" id="cTipoRfc" name="cTipoRfc"/>
				<input type="hidden" id="dRFC" name="dRFC" value=""/>
								
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
												<input type="text" class="form-control form-control-sm" name="FOLIO" id="FOLIO" size="15" value="<%=c.getFolio() %>">
												<%if(id_oper==7 && !("true".equals(c.getCasoDato("CANCELADO_CONT").getValor()))){ %>
													<a href="#" onclick="window.open('../reportes?cmd=<%=GestionInterface.RPT_MOVIMIENTOS_REINMIL%>&folio_reintegro=<%=c.getFolio()%>&nfolio=<%=folio %>');" >Aviso de Reintegro</a>
												<%} %>
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
										
										<div class="row d-flex">
											<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
												<label for="ckIngreso" class="form-label"> Cancela Ingreso </label>
											</div>
											<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
												<input type="checkbox" class="form-check-input" id="ckIngreso" name="ckIngreso" onclick="guardaOpcion()"/>
											</div>
										</div>
										
										<div id="divEmpleado"> 
											<div class="row">
												<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
												</div>
												<div class="col-12 col-lg-1 col-md-1 col-sm-12 d-flex p-1">								
													<label for="cIdRFC_RelacionGasto"> RFC: </label>
												</div>
												<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">													
													<div class="input-group">
														<input type="text" name="cIdRFC_RelacionGasto" id="cIdRFC_RelacionGasto" style='text-transform:uppercase;' maxlength="15" size="13" onchange="obtenTipoClaveBenf()" readonly class="form-control form-control-sm" /> 
														<input type="button" name="btnBeneficiario" id="btnBeneficiario" value="..." size="5" onclick="cat_beneficiario()" class="btn btn-secondary btn-sm"/> 
													</div>
														<input type="text" name="cnombre" ID="cnombre" value="" maxlength="100" size="75" readonly class="form-control form-control-sm" onchange="cambiaRFC()"/>																
												</div>	
											</div>
										</div>
										
										<div id="divImprimePoliza">
											<div class="row d-flex justify-content">
												<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
													<div class="input-group">
														<span class="input-group-text"><i class="bi bi-printer"></i></span>																	
														<input type="button" class="btn btn-secondary btn-sm" name="btnImprimir" id="btnImprimir" value="Poliza" onclick="cmdImprimir('REINTEGROAUTMIL')" >																
													</div>
													<span id="btnFirmas" ><a href="#" onclick="modificarFirmantes();" >*Firmas</a></span>
												</div>
											</div>
										</div>																			
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
							
							<%if(id_oper>=3 && id_oper<=5){ %>
								<div class="row d-flex justify-content">
									<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
										<input type="button" id="Cancelar" name="Cancelar" value="Cancelar Documento" class="btn btn-outline-danger btn-sm" onclick="javascript:cancelarDoc();"/>
									</div>
								</div>
							<%} %>
							
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
												<div class="input-group">
													<input type="text" name="CTABAN" id="CTABAN" value="" class="form-control form-control-sm AyudaSyC" onkeydown="return ctaKeyDwn(event, this.id)" onfocus="cierraAyuda()" readonly/>
												</div>
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
													<input type="text" name="fechaAcredit" id="fechaAcredit" class="form-control form-control-sm">
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
						
					<div id="multitabs" style="width: 100%" class="container">
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
							<%if(cbl.getArchivosDocumento(c.getTipoCaso().getGavetaAsociada(), c.getIdGabinete(), 2, 1).length>0 && id_oper==1){%>
								<form id="limpiaDatos" name="limpiaDatos" method="POST" action="../servlet/ReintegrosServlet" />
									<input type="hidden" name="folioReintegro" id="folioReintegro" value="<%=folio%>"/>
									<input type="hidden" name="borraTodo" id="borraTodo" value="1"/>
									<input type="button" class="btnInterfaceBG" name="borrarTodo" id="borrarTodo" value="Limpiar Datos" onclick="limpiarDatos();"/>
								</form>
							<%}%>
							
							<%if(id_oper==2 || id_oper==7){ %>
								<input id="btnMostrar" name="btnMostrar" type="hidden" value="Mostrar Detalle" onclick="mostrarDialog()"  />
							<%}%>
												
							<div id="dialog" title="Detalle de Reintegros">
								<p>Consulta de Reintegros del Presupuesto </p>
								<input type="button" class="btnInterfaceBG" id="aplicarContable" value="Aplicar contablemente" onclick="aplicaCont();" />
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
						</div>
					</div>
				</div>
			</form>
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
	
		<div id="dialogMotor" title="Mensajes del sistema Aviso de Reintegros">
			<p>Mensajes del sistema </p> <a rel=""></a>
			<textarea id="mensajeMotor" name="mensajeMotor" rows="8" cols="120"></textarea>
		</div>
		
		<div id="dialog-firmantesUpdate" title="Actualizacion de Firmantes" class="container">
			<h5> Datos VºBº </h5>
			<hr class="mt-3">
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cNombreVoBoUpdate" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreVoBoUpdate" name="cNombreVoBoUpdate" size=40/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPaternoVoBoUpdate" class="form-label"> Apellido Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPaternoVoBoUpdate" name="cPaternoVoBoUpdate" size=40/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cMaternoVoBoUpdate" class="form-label"> Apellido Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cMaternoVoBoUpdate" name="cMaternoVoBoUpdate" size=40/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPuestoVoBoUpdate" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoVoBoUpdate" name="cPuestoVoBoUpdate" size=40/>
				</div>
			</div>	
			
			<br/>
			
			<h5> Datos Autoriza </h5>
			<hr class="mt-3">
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cNombreAutUpdate" class="form-label"> Nombre: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cNombreAutUpdate" name="cNombreAutUpdate" size=40/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPaternoAutUpdate" class="form-label"> Apellido Paterno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPaternoAutUpdate" name="cPaternoAutUpdate" size=40/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cMaternoAutUpdate" class="form-label"> Apellido Materno: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cMaternoAutUpdate" name="cMaternoAutUpdate" size=40/>
				</div>
			</div>	
			
			<div class="row">
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex">
					<label for="cPuestoAutUpdate" class="form-label"> Puesto: </label>
				</div>
				<div class="col-12 col-lg-8 col-md-8 col-sm-12 d-flex">								
					<input type="text" class="form-control form-control-sm" id="cPuestoAutUpdate" name="cPuestoAutUpdate" size=40/>
				</div>
			</div>	
		
		</div>		
	</body>
</html>
