<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@ page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    Calendar c1 = Calendar.getInstance(); // today
    String today= sdf.format(c1.getTime());
    String cCentroContable="";
	
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (c == null) {
		response.sendRedirect("../index.jsp");
		return;
	}
	
	String mensaje="";
	if(request.getParameter("msg")!=null&&!"".equals(request.getParameter("msg"))){
		mensaje=request.getParameter("msg");
		mensaje=mensaje.replace("[","");
		mensaje=mensaje.replace("]","");
		mensaje=mensaje.replace(",","<br>");
	}
	
	int id_oper = -1;
	if(request.getParameter("id_oper")!=null)
		id_oper= new Integer(request.getParameter("id_oper")).intValue();
	else
		id_oper=c.getCasoOperacion(0).getIdOperacion();
		
	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);
	if(usuario.getPropiedades()!=null&&usuario.getPropiedades().containsKey("CCENTROCONTABLE")){
	            cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	
	if (cCentroContable.isEmpty() || cCentroContable.equals("")){
			mensaje="Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
	}
	
	//documentos del caso
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	FortimaxFile[] archivoExcel = null;
	archivoExcel = cbl.getArchivosDeDocumento(c.getTipoCaso().getGavetaAsociada(),c.getIdGabinete(),1,1);
	
	String select=c.getTipoCaso().getGavetaAsociada()+"_G"+c.getIdGabinete();//se usa por separado abajo
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>cargaPresupuestal.jsp</title>
    
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
    
    <script type="text/javascript">       
    
    function guargaExp(){
		if(get("DPC_FECHA_APLICACION_CONTABLE")!="" && get("EJERCICIO_FISCAL")!=""){
			parent.document.getElementById("pb_save").click();//esto es para asegurar que se crea el expediente antes de subir el archivo
		}
	}
    
 	function get(name) {
		return document.getElementById(name).value;
	}
  	function onSubmit(id_oper){//validaciones del boton guardar
  		var p = window.parent;
  		var valida_campos = true;
		try{
			//validaciones de la forma
			
			//poner aqui las validaciones a efectual en el boton guardar, en este caso no hay
				//guardado de los campos correspondientes a cada variable de caso
				p.gestion.setFolio(get("FOLIO"));
				p.gestion.setOperador(get("OPERADOR"));
				p.gestion.setFechaDocumento(get("FECHA_CARGA"));
				p.gestion.setEjercicioFiscal(get("EJERCICIO_FISCAL"));
				p.gestion.setConceptoMov("Carga de presupuesto autorizado");
				p.gestion.setMoneda("MXP");//el presupuesto siempre es en pesos
				p.gestion.setFechaApCont(get("DPC_FECHA_APLICACION_CONTABLE"));
				p.gestion.setAplicadoCont("false");
				
				if(<%=c.getIdGabinete()%>!=-1){
					//alert("antes de fecha de aplicacion"+get("DPC_FECHA_APLICACION_CONTABLE"));
					p.gestion.setFechaApCont(get("DPC_FECHA_APLICACION_CONTABLE"));
					//parent.document.getElementById("pb_save").disabled=true;
					parent.document.getElementById("pb_send").disabled=false;
				}
		}
		catch (e) {
			window.alert("onSubmit: Error: " + e.message);
			return false;
		}
		return valida_campos;
  	}
  	
  	function onPostSubmit(id_oper){//validaciones del boton enviar
  		//var valida_doctos_requeridos = true;
  		//validaciones de documentos requeridos
  		//return valida_doctos_requeridos;
  		return confirm("Confirmar que quiere avanzar a la siguiente operación.");
  	}
  	
  	function ResponsableSiguiente(id_oper){

  		 //if(id_oper==1)
  		 	//return "CONTINUAR";
  		 //else
  		 	return "CARGA_PRESUPUESTO";
  				
  	}
  	
  	
  	function OperacionSiguiente(id_oper){

  		//if(id_oper==1)
  		// 	return "CONTINUAR";
  		//else
  		 	return "carga_presupuesto";
  				
  	}
  	
  	function validaCamposLlave(){
		var msgAlert="";
		if(get("EJERCICIO_FISCAL")=="")
			msgAlert+="El ejercicio fiscal es requerido\n";
		if(get("DPC_FECHA_APLICACION_CONTABLE")=="")
			msgAlert+="La fecha de aplicación contable es requerida\n";
  	
  		if(	document.getElementById("uploadfile").value == "" ){//||
   			//document.getElementById("uploadfile_key").value == "" ||
  			//document.getElementById("uploadfile_cer").value == "" ||
  			//document.getElementById("password").value == ""){
  			//alert("El archivo excel de presupuesto, la llave, el certificado y el password son requeridos");
  			msgAlert+="El archivo excel de presupuesto es requerido";
  		}
  		if(msgAlert!=""){
  			alert(msgAlert);
  			return false;
  		}
  		else{
  			parent.document.getElementById("pb_save").click();//esto e spara asegurar que se guardan los datos antes de subir el archivo
  			document.upform.submit();//subir el archivo excel
  			return true;
  		}
  	}
  	
  	function onLoadPlantilla(id_oper){

		parent.document.getElementById("pb_save").style.visibility="hidden";
		<% if("true".equals(c.getCasoDato("APLICADO_CONT").getValor())||mensaje.contains("APLICADO")){%>
			parent.document.getElementById("pb_send").disabled=false;
			parent.document.getElementById("pb_cancel").disabled=true;
			document.getElementById("cargaryfirmar").disabled=true;
			document.getElementById("DPC_FECHA_APLICACION_CONTABLE").disabled=true;
			document.getElementById("EJERCICIO_FISCAL").disabled=true;
			document.getElementById("aplicarContable").style.visibility="hidden";
			<%if("".equals(mensaje)){
				mensaje="DOCUMENTO APLICADO CONTABLEMENTE";
			}%>
		<%}%>
  	}
  	
  	function onPostDisplay(id_oper){
   	}
   	
   	function aplicaCont(){
   		parent.document.getElementById("pb_cancel").disabled=true;
   		//document.getElementById("aplicarContable").disabled=true;
   		document.getElementById("esperar").style.visibility="visible";
   		document.frmAplicaContable.submit();
   	}
 </script>
  <br/>
  <body>
	  	<div id="container" style="width: 80%" class="container">
			<div class="card-header"> <h3>Carga Presupuesto Original</h3> </div>					
			<hr class="mt-3">
			
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>				
				<div class="col-12 col-lg-6 col-md-6 col-sm-12 p-1">										
					<form  name="cargaPresu" id = "cargaPresu" action="">
						<input type="hidden" name="FOLIO" id="FOLIO" value="<%=c.getFolio()%>"/>
						<input type="hidden" name="OPERADOR" id="OPERADOR" value="<%=c.getCasoOperacion(0).getResponsable()%>"/>
						<input type="hidden" name="FECHA_CARGA" id="FECHA_CARGA" value="<%=today%>"/>
						<div class="row">					
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<label for="EJERCICIO_FISCAL" class="form-label"> Ejercicio Fiscal: </label>
							</div>	
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<select name="EJERCICIO_FISCAL" id="EJERCICIO_FISCAL" onblur="guargaExp();" class="form-select form-select-sm">
		  							<option value=""></option>
		  							<option value="2012">2012</option>
		  							<option value="2013">2013</option>
		  							<option value="2014">2014</option>
		  							<option value="2015">2015</option>
		  							<option value="2016">2016</option>
		  							<option value="2017">2017</option>
		  							<option value="2018">2018</option>
		  							<option value="2019">2019</option>
		  							<option value="2020">2020</option>
		  							<option value="2021">2021</option>
		  							
		  							<option value="2022" >2022</option>
		  							<option value="2023" >2023</option>
		  							<option value="2024" >2024</option>
		  							<option value="2025" >2025</option>
		  							<option value="2026" selected="selected">2026</option>
		  						</select> 
							</div>
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<label for="DPC_FECHA_APLICACION_CONTABLE" class="form-label"> Fecha de Registro: </label>
							</div>	
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">
								<input type="date" onblur="guargaExp();" class="form-control form-control-sm" name="DPC_FECHA_APLICACION_CONTABLE" id="DPC_FECHA_APLICACION_CONTABLE"  datepicker_format="DD/MM/YYYY" datepicker="true"  <%=(id_oper>1?"disabled":"")%> maxlength="10" size="10"/>											
							</div>	
						</div>
					</form>	
				</div>
			</div>
			
		
			<div class="row">
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-10 col-md-10 col-sm-12 p-1">
					<form name="upform" id = "upform" action="../caso/firmardoc?carpeta=1"  enctype = "multipart/form-data" method = "post" >
						<div class="row">		
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="uploadfile" class="form-label"> Archivo excel: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<input type="file" id="uploadfile" name="uploadfile" class="form-control form-control-sm" />
							</div>
						</div>
						<div class="row">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="uploadfile_key" class="form-label"> Key: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<input type="file" id="uploadfile_key" name="uploadfile_key" class="form-control form-control-sm" />
							</div>
						</div>	
						<div class="row">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="uploadfile_cer" class="form-label"> CER: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<input type="file" id="uploadfile_cer" name="uploadfile_cer" class="form-control form-control-sm" />
							</div>
						</div>	
						<div class="row">
							<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
								<label for="uploadfile_cer" class="form-label"> Password: </label>
							</div>
							<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
								<input type="password" name="password" id ="password" class="form-control form-control-sm">
							</div>
						</div>
						<div class="row">
							<div class="col-12 col-lg-3 col-md-3 col-sm-12 p-1">						
							</div>
							<div class="col-12 col-lg-1 col-md-1 col-sm-12 p-1">
								<input type="button" name="cargaryfirmar" id="cargaryfirmar" value = "Cargar Archivo" onclick = "validaCamposLlave();" class="btn btn-secondary btn-sm">
							</div>
						</div>
					</form>
				</div>
			</div>
			<div class="row">					
				<div class="col-12 col-lg-2 col-md-2 col-sm-12 p-1">
				</div>
				<div class="col-12 col-lg-4 col-md-4 col-sm-12 p-1">
					<%if(archivoExcel.length>0){ %>
		  				<%=("".equals(mensaje)||mensaje==null?"Ya esta cargado el archivo excel.":mensaje.replace("|","&"))%>
		  				<form name="frmAplicaContable" id="frmAplicaContable" method="post" action="../gstnmngr/cargaPresupuesto?carpeta=1">
		  					<input type="button" name="aplicarContable" id="aplicarContable" value = "Generar Documento de Carga Presupuestal y Aplicar Contablemente" onclick="aplicaCont();" class="btn btn-secondary btn-sm">
		  				</form>
		  				<label id="esperar" style="visibility: hidden">Espere por favor....<img border="0" src="../imagenes/espera.gif"></label>
		  			<%} %>
				</div>
			</div>			
	  </div>
  </body>
</html>
