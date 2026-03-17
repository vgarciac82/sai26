<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="org.apache.log4j.Logger"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%! private static Logger log = Logger.getLogger("com.syc.plantillas.casos.ConsultaMovimientosPresupuestales.jsp"); %>
<%
	/**
	* @author Martha Aurora Sánchez Valdivieso
	* para SYC Constructores de Sistemas
	* desarrollo gestion_conagua_sif
	* México D.F. 03/07/2012
	*
	*/
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	boolean existeResultado = false;
	
	String cAnioFiscal = "";
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());
	String msg = "";
	String ep = "";
	String tipoCuenta = "";
	String strDesde = "";
	String strHasta = "";
	List<StringBuffer> listasb = new ArrayList<StringBuffer>();
	StringBuffer enero = new StringBuffer();
	StringBuffer febrero = new StringBuffer();
	StringBuffer marzo = new StringBuffer();
	StringBuffer abril = new StringBuffer();
	StringBuffer mayo = new StringBuffer();
	StringBuffer junio = new StringBuffer();
	StringBuffer julio = new StringBuffer();
	StringBuffer agosto = new StringBuffer();
	StringBuffer septiembre = new StringBuffer();
	StringBuffer octubre = new StringBuffer();
	StringBuffer noviembre = new StringBuffer();
	StringBuffer diciembre = new StringBuffer();
	AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	cAnioFiscal = adecua.obtenEjercicioFiscal();

	if(request.getParameter("clearForm") != null && "true".equals(request.getParameter("clearForm") ) ){
		session.removeAttribute("mensaje");
		session.removeAttribute("ep");
		session.removeAttribute("tipoCuenta");
		session.removeAttribute("strDesde");
		session.removeAttribute("strHasta");
		session.removeAttribute("movimientos");
	}
	

	if(session.getAttribute("movimientos") != null){
		msg = (String)  session.getAttribute("mensaje");
		ep = (String)  session.getAttribute("ep");
		tipoCuenta = (String) session.getAttribute("tipoCuenta");
		strDesde = (String)	session.getAttribute("strDesde");
		strHasta = (String) session.getAttribute("strHasta");
		listasb = (List<StringBuffer>) session.getAttribute("movimientos");
		existeResultado = listasb.size() > 0;
		
	}
	
	if(listasb.size() != 0){
		enero = listasb.get(0);
		febrero = listasb.get(1);
		marzo = listasb.get(2);
		abril = listasb.get(3);
		mayo = listasb.get(4);
		junio = listasb.get(5);
		julio = listasb.get(6);
		agosto = listasb.get(7);
		septiembre = listasb.get(8);
		octubre = listasb.get(9);
		noviembre = listasb.get(10);
		diciembre = listasb.get(11);
	}

%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
	<title>Movimientos Presupuestales</title>
		
	<link rel="stylesheet" type="text/css" href="../Ayudas/css/autocompleta.css"></link>
	<link rel="stylesheet" type="text/css" href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>	
	<link rel="stylesheet" type="text/css" href="../Generador/css/demo_page.css"></link>
	<link rel="stylesheet" href="../Bootstrap/bootstrap-icons-1.9.1/bootstrap-icons.css">
	
	<link rel="stylesheet" href="../Generador/css/bootstrap.min.css">
	<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
	<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
		
	<script type="text/javascript" src="../Generador/js/jquery.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"></script>	
	<script type="text/javascript" src="../Generador/js/jquery.ui.widget.js"></script>
	
	<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"></script>
	<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>
	
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
	<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
	
	<script type="text/javascript" charset="utf-8">
		var oTable; 
		
		function inicio(){
			var msgServlet = $('#mensaje').val();

			if( msgServlet != "" && msgServlet != null ){
				//alert(msgServlet);
				$('#tblMovimientos thead tr td:eq(0)').click();
			}else{
				$('#mensaje').val("");
			}
		}
	
		function Grid2(){
			window.open('../admin/MultiReporteGrid.jsp?id=<%=request.getParameter("id")%>', 'MultiReporteGrid', 'status=1, width=900px, height=500px');
		}
	
		function limpiarSesion(){
			$("#txtGridEP").val("");
			$("#ep").val("");
			$("#tblMovimientos").dataTable().fnClearTable();
		}
	
		function buscarMovimientoPresupuestal(){
			$("#txtGridEP").val($("#ep").val());
			if( $('#txtGridEP').val() != "" ){
				if( $('#dTipoCuenta').val() != "" ){
					if ( $('#fDesde').val() != ""  && $('#fHasta').val() != "" ){
						$('#form1').submit();
					}
					else{
						alert("Son necesarias las dos fechas");
					}
				}
				else{
					alert("no se ha seleccionado el tipo de cuenta");
				}
			}
			else{
				alert("La EP es un dato requerido");
			}
		}

		$(document).ready(function(){
			$("#btnConsultar").button();
			$("#Limpiar").button();

			$("input.AyudaSyC").subIniciaDlg();

			$("#fDesde").datepicker({
				dateFormat: "dd-mm-yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true
			});

			$("#fHasta").datepicker({
				dateFormat: "dd-mm-yy",
				autoclose: true,
				changeYear: true,
				changeMonth: true				
			});

			querySelectPost("TipoCuentaRead", "dTipoCuenta", {async: false});
			queryFormPost("fHastaRead", {async: false });

			oTable = $("#tblMovimientos").dataTable({
				bPaginate : false,
				bAutoWidth : true,
				sScrollY: "240",
				sScrollX: "200%",
				//sScrollYInner: "100%",
				//sScrollXInner: "200%",
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
					oPaginate: { sFirst: "Primero",	sPrevious: "Ant.", sNext: "Sigte.",	sLast: "&Uacute;ltimo" }
				},
				bServerSide: false,
				bProcessing: true,
				bFilter : false,
				bSort : false,
				sPaginationType: "full_numbers",
				bJQueryUI: true
			} );
		<%if(existeResultado){
		  	out.println("$(\"#downloadFrm\").submit();");
		}%>
		});
	</script>
  </head>
  <br/>
  <body id="dt_example" onLoad="inicio();" >  	
   	<div id="container" class="container" style="width: 80%">
		<div class="card-header"> <h3> Movimientos Presupuestales </h3> </div>
		<hr class="mt-3"/>
		
		<h6> Consulta de Movimientos Presupuestales </h6>
		<hr class="mt-3"/>
		
			
		<div align="center">
			<form id="form1" name="form1" method="POST" action = "../gstnmngr/MovimientosPresupuestalesServlet">
				<input type="hidden" value="<%=cAnioFiscal%>" id="cEjercicio" name="cEjercicio">
				<input type="hidden" id="mensaje" name="mensaje" value="<%= ( msg == null )? "" : msg%>">
				
				<div class="row">																	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">													
						<label for="ep" class="form-label">EP:</label>
					</div>
					<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">	
						<input type="hidden" value="" id="txtGridEP" name="txtGridEP">						
						<input align="left" id="ep" name="ep" type="text" value="<%= (ep == null)? "": ep%>" class="form-control form-control-sm" size="64" maxlength="64" /> &nbsp;
						<input type="button" value="..." onclick="Grid2()" class="btn-secondary btn-sm"/>										
					</div>			
				</div>
				
				<div class="row">																	
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">													
						<label for="dTipoCuenta" class="form-label">Tipo de Presupuesto:</label>
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">	
						<select name="dTipoCuenta" id="dTipoCuenta" class="form-select form-select-sm">
							<option value="<%= ( !"00000".equals(tipoCuenta) )? tipoCuenta :"" %>" <%= ( tipoCuenta != null && "00000".equals(tipoCuenta) )? "selected" :"" %> > -- Seleccione -- </option>
						</select>
					</div>			
				</div>
				
				<div class="row">				
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<label for="fDesde"> Desde: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">					
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>								
							<input type="text" id="fDesde" name="fDesde" class="form-control form-control-sm" value="<%="01/01/"+cAnioFiscal%>"/>
						</div> 							 							
					</div>
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<input type="button" class="btnInterfaceXLS" value="Consultar" id="btnConsultar" name="btnConsultar" onClick="buscarMovimientoPresupuestal();"/>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<input type="button" class="btnInterfaceBG" id="Limpiar" name ="Limpiar" value="Limpiar" onclick="limpiarSesion();">
					</div>
				</div>
				
				<div class="row">
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">
						<label for="fHasta"> Hasta: </label>
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<div class="input-group">								
							<span class="input-group-text"><i class="bi bi-calendar-date"></i></span>													
							<input type="text" id="fHasta" name="fHasta" class="form-control form-control-sm"/>
						</div> 			
					</div>	
					<div class="col-12 col-lg-4 col-md-4 col-sm-12 d-flex p-1">
					</div>
					<div class="col-12 col-lg-2 col-md-2 col-sm-12 d-flex p-1">		
						<%if(existeResultado){
									out.println("\t\t\t\t\t\t\t\t\t\t\t<tr>\n"
											+ "\t\t\t\t\t\t\t\t\t\t\t\t<td width=\"40%\" >&nbsp;</td>\n"
											+ "\t\t\t\t\t\t\t\t\t\t\t\t<td width=\"60%\" align=\"left\" colspan=\"2\">\n"
											+ "\t\t\t\t\t\t\t\t\t\t\t\t\t<a href=\"resultadoMovimientosPresupuestales.jsp?time=" + System.currentTimeMillis() + "\">Descargar Archivo</a>\n"
											+ "\t\t\t\t\t\t\t\t\t\t\t\t</td>\n"
											+ "\t\t\t\t\t\t\t\t\t\t\t</tr>\n"); 
						}%>
					</div>				
				</div>

			</form>
		</div>
			
		<br/>
		
		<h6> Movimientos </h6>
		<hr class="mt-3"/>
	
		<div id="demo_jui" class="container" style="width: 100%">
			<form method="get" name="frmValida" id="frmValida" action = "../gstnmngr/MovimientosPresupuestalesServlet">
			
				<div class="row">
					<div class="col-12 col-lg-12 col-md-12 col-sm-12 d-flex p-1">
						<table id="tblMovimientos" class="table table-striped">
							<thead>
								<tr>
									<td>Fecha Movimiento</td>
									<td>Tipo Documento</td>
									<td>Folio Documento</td>
									<td>Estatus</td>
									<td>Enero</td>
									<td>Febrero</td>
									<td>Marzo</td>
									<td>Abril</td>
									<td>Mayo</td>
									<td>Junio</td>
									<td>Julio</td>
									<td>Agosto</td>
									<td>Septiembre</td>
									<td>Octubre</td>
									<td>Noviembre</td>
									<td>Diciembre</td>
								</tr>
							</thead>
							<tbody>
							<%
								if(enero.length() != 0){
									String registros = enero.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Enero" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Enero" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Enero" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Enero" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%  
								if(febrero.length() != 0){
									String registros = febrero.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Febrero" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Febrero" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Febrero" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Febrero" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(marzo.length() != 0){
									String registros = marzo.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
											for (String registro: registros.split(";")) {
												campos = registro.split(",");
												valor = Double.parseDouble(campos[4]);
												valor = valor * -1;
												negativo = Double.toString(valor) ;
								%>
									<tr>
										<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
										<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
										<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
										<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
										<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
										<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
								<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Marzo" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Marzo" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Marzo" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Marzo" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(abril.length() != 0){
									String registros = abril.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Abril" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Abril" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Abril" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Abril" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(mayo.length() != 0){
									String registros = mayo.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Mayo" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Mayo" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Mayo" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Mayo" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(junio.length() != 0){
									String registros = junio.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Junio" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Junio" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Junio" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Junio" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(julio.length() != 0){
									String registros = julio.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Julio" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Julio" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Julio" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Julio" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(agosto.length() != 0){
									String registros = agosto.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Agosto" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Agosto" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Agosto" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Agosto" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(septiembre.length() != 0){
									String registros = septiembre.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Septiembre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Septiembre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Septiembre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Septiembre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(octubre.length() != 0){
									String registros = octubre.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Octubre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Octubre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Octubre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Octubre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(noviembre.length() != 0){
									String registros = noviembre.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Noviembre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Noviembre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Noviembre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Noviembre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
							%>
									<td><input name="Diciembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
										}
									}
								}
							%>
								</tr>
							<%
								if(diciembre.length() != 0){
									String registros = diciembre.toString();
									if((registros != null) && (registros.length()>= 0)) {
										String[] campos;
										double valor = 0;
										String negativo = "";
										for (String registro: registros.split(";")) {
											campos = registro.split(",");
											valor = Double.parseDouble(campos[4]);
											valor = valor * -1;
											negativo = Double.toString(valor) ;
							%>
								<tr>
									<td><input name="fMovimiento" value="<%=campos[11]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cTipoDocumento" value="<%=campos[8]%>" class="form-control form-control-sm" readonly/></td>
									<td><input name="cFolioDocumentoMovimiento" value="<%=campos[12]%>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="cCancelaMovimiento" value="<%= ( "C".equals(campos[13]) ) ? "CANCELADO" : "" %>" size="10" class="form-control form-control-sm" readonly/></td>
									<td><input name="Enero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Febrero" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Marzo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Abril" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Mayo" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Junio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Julio" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Agosto" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Septiembre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Octubre" value="" class="form-control form-control-sm" readonly/></td>
									<td><input name="Noviembre" value="" class="form-control form-control-sm" readonly/></td>
							<%
											if( campos[20].equalsIgnoreCase("A") ){
												if( campos[5].equalsIgnoreCase("A") ){
							%>
									<td><input name="Diciembre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Diciembre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} else if( campos[20].equalsIgnoreCase("D") ){
												if( campos[5].equalsIgnoreCase("C") ){
							%>
									<td><input name="Diciembre" value="<%=campos[4]%>" class="form-control form-control-sm" readonly/></td>
							<%
												} else{
							%>
									<td><input name="Diciembre" value="<%= negativo%>" class="form-control form-control-sm" readonly/></td>
							<%
												}
											} 
										}
									}
								}
							%>
								</tr>
							</tbody>
						</table>
					</div>
				</div>
			</form>
		</div>							
		
	</div>
  </body>
</html>