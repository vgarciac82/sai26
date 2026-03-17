
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
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
	String algo = "";
	System.out.println(today);	
	
	Usuario usuario = (Usuario) session
			.getAttribute(GestionInterface.ATT_USER);
			
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	System.out.println(efa);	
	
	String finicial = "01/01/" + efa;
	System.out.println(finicial);	
	
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
		id_oper = new Integer(request.getParameter("id_oper"))
				.intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(
			GestionInterface.ATT_CONEXION);
	//finicial = usuario.getPropiedad("aEjercicioFiscal").getValor();
	
	
	//Valida Centro de Costos
	if (usuario.getPropiedades() != null
			&& usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();		
	}

	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	//cRamo="16";
	algo = usuario.getLogin();
	//de aki para arriba es de cajon
			
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title>Reporte de Ingresos</title>

<link rel="stylesheet" href="../Generador/css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>
<link rel="stylesheet" href="../Generador/css/bootstrap-datetimepicker.min.css"></link>
<script src="../Generador/js/bootstrap.bundle.min.js"></script>

<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.datepicker.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/Moment.js"></script>
<script type="text/javascript" src="js/sweetalert2.all.min.js"></script>
<script type="text/javascript" src="js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
	$(document).ready(function() {	
		
		$("#cCentroContable").val( "<%=cCentroContable%>");	
	    $("#limpiarBtn").button();
	    
	    $("#fechaI").val(moment().format('01-01-yyyy'));
		$("#fechaF").val(moment().format('MM-DD-yyyy'));
		
		$("#cmdxlsReporte").button();
		
	});	
	
	function extrae(){	
		if($("#fechaI").val() == ""){
			Swal.fire({ icon: "warning",
						text: "Debes seleccinar una fecha de Inicio."});
			return false;
		} else if($("#fechaF").val() ==""){
			Swal.fire({ icon: "warning",
						text: "Debes seleccinar una fecha de Fin."});
			return false;
		}
		
		$("#fecha_inicio").val($("#fechaI").val().split('-').reverse().join('/'));
		$("#fecha_fin").val($("#fechaF").val().split('-').reverse().join('/'));		
		
		var columnas = "";
		var cntr = ",";
		let seleccionados = document.getElementById('rightList');      
	      for(let i = 0;  i < seleccionados.length; i++){
	          columnas+=(seleccionados[i].value)+cntr;
	      }
		$("#columnas").val(columnas.substr(0,columnas.length-1));
		
		if($("#GreenMex").prop("checked")){
			$("#SNP").val("SI");			
		} else {
			$("#SNP").val("NO");
		}
		
		if($("#GreenMexD").prop("checked")){
			$("#INGM").val("SI");			
		} else {
			$("#INGM").val("NO");
		}
		
		document.ExportarForm.submit();				
	}
	
	function moveRight() {
		var leftlist=document.getElementById("leftList");
	    var selItem=leftlist.selectedIndex;
	    
	    if (selItem == -1) {
	        window.alert("Debes seleccionar al menos una opción de la lista.")
	    } else {
	        var rightlist = document.getElementById("rightList");
	        var newOption = leftlist[selItem].cloneNode(true);

	        leftlist.removeChild(leftlist[selItem]);
	        rightlist.appendChild(newOption);
	    }
	    
	}
	
	function moveLeft() {
		var rightList=document.getElementById("rightList");
	    var selItem=rightList.selectedIndex;
	    
	    if (selItem == -1) {
	        window.alert("Debes seleccionar al menos una opción de la lista.")
	    } else {
	        var leftlist = document.getElementById("leftList");
	        var newOption = rightList[selItem].cloneNode(true);

	        rightList.removeChild(rightList[selItem]);
	        leftlist.appendChild(newOption);
	    }
	    
	}
	
	function ayudaRazonSocial(){		
		window.open('../Generador/CatalogoRazonSocial_IP.jsp?formName=ExportarForm&inputRFCTarget=cClave&inputDRFCTarget=cNombre', 'Razon Social', 'status=1, width=900px, height=550px, left=0px, scrollbars=Yes,resizable=yes');		
	}
	
	function noIncluyeGreenMex(){
		if(!$("#cClave").val()==""){			
			document.getElementById("GreenMex").disabled = true			
		} else {
			document.getElementById("GreenMex").disabled = false
		}
	}
		
</script>

</head>
<body id="dt_example">
<br/>
	<form id="ExportarForm" name="ExportarForm" action="../reportes/ReporteIngresos" method="get">	

		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>"/> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/>
		<input type="hidden" id="fecha_inicio" name="fecha_inicio"/>
		<input type="hidden" id="fecha_fin" name="fecha_fin"/>
		<input type="hidden" id="columnas" name="columnas"/>
		<input type="hidden" id="SNP" name="SNP" value="NO"/>
		<input type="hidden" id="INGM" name="INGM" value="NO"/>
		
		<div id="container" style="width: 60%" class="container">
			
			<div class="card-header"> <h3> Reporte de Registros de Ingresos </h3> </div>
			<hr class="mt-3"/>			
									
			<div class="row">				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="form-group">
                       <label for="fecha_inicio">Desde:</label>                       
                	</div>										
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">                       
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaI" name="fechaI" value="<%=finicial%>"/>                                    
                       </div>
                	</div>										
				</div>		
			</div>
			<div class="row">							
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="form-group">
                       <label for="fecha_fin">Hasta:</label>                       
                	</div>										
				</div>	
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<div class="form-group">
                       <div class="input-group date" id="datepicker1">
                          <input type="date" class="form-control form-control-sm" id="fechaF" name="fechaF"/>                                    
                       </div>
                	</div>										
				</div>			        				
			</div>
			
			<div class="row">							
				<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
					<p class="text-danger"> Esta seleccion es opcional para la busqueda </p>
				</div>
			</div>
						
			<div class="row">				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">												
					<label for="cClave"> Razon Social Origen </label>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 d-flex p-1">																	
					<input type="text" id="cClave" name="cClave" class="form-control form-control-sm" readonly onChange="noIncluyeGreenMex()"/>						
					<input type="button" class="btn btn-secondary btn-sm" id="btnRazonSocial" value="..." onclick="ayudaRazonSocial()">&nbsp;&nbsp;																																																			
				</div>
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 d-flex p-1">
					<input type="text" id="cNombre" name="cNombre" class="form-control form-control-sm" readonly/>												 						 					
				</div>
			</div>
						
			<div class="row d-flex justify-content-center">				
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="form-group">
                    	<label for="available_c">Columnas disponibles</label>
                    </div>
                </div>
                <div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
                </div>                
                <div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
					<div class="form-group">
                       	<label for="selected_c">Columnas seleccionadas</label>
                    </div>
                </div>
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">				
	               	<select name="leftList" id="leftList" size="6" class="form-select form-select-sm" multiple>	               	
	               		<option value="cClave">Denominacion de origen</option>
	               		<option value="cOrigenTransferencia">Quien realizo la transferencia</option>
	               		<option value="cNombreGestion">Quien gestiono el ingreso</option>
	               		<option value="nesExtranjero">Es extranjero?</option>
	               		<option value="cesDonativo">Es donativo?</option>
	               		<option value="cComprobanteFiscal">Requiere CFDI?</option>
	               	</select>                                                                                                                  	
				</div>
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-5">
					<div class="row d-flex justify-content-center">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">
							<input type="button" value="→" class="btn btn-secondary btn-sm move-right" onclick="moveRight();"/>																
						</div>					
					</div>
					<div class="row d-flex justify-content-center">
						<div class="col-12 col-lg-12 col-md-12 col-sm-12 p-1">							
							<input type="button" value="←" class="btn btn-secondary btn-sm move-left" onclick="moveLeft();"/>											
						</div>					
					</div>
				</div>
				<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">					               	
	               	<select name="rightList" id="rightList" size="6" class="form-select form-select-sm" multiple>	               							
	               	</select>                                                                                                                  	
				</div>        			
			</div>
			
			<div class="row d-flex justify-content-center">				
				<div class="col-12 col-lg-5 col-md-5 col-sm-12 p-1">					
						<label for="GreenMex" class="form-check-label">¿Incluir ingreso bancario de GreenMex? &nbsp;</label>						
						<input type="checkbox" name="GreenMex" id="GreenMex" class="form-check-input"/>					
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">				
				<div class="col-12 col-lg-5 col-md-6 col-sm-12 p-1">					
						<label for="GreenMexD" class="form-check-label">¿Incluir ingreso GreenMex Devengado/Pagado? &nbsp;</label>						
						<input type="checkbox" name="GreenMexD" id="GreenMexD" class="form-check-input"/>					
				</div>
			</div>
			
			<div class="row d-flex justify-content-center">		
				<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
				</div>		
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
					<input type="button" id="cmdxlsReporte" name="cmdxlsReporte" value="Extraer" class="btn btn-secondary btn-sm" onclick="extrae()"/>
				</div>
			</div>		
							
		</div>					
												
	</form>		
</body>
</html>


