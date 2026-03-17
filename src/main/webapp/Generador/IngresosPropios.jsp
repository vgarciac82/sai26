<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.util.*"%>
<%@page	import="com.syc.gestion.core.*"%>
<%@page	import="com.syc.gestion.servlet.*"%>
<%@page	import="com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.io.File"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%@page import="com.syc.registroingresos.RegistroIngresosBussinesLogic"%>
<%@page import="com.syc.registroingresos.RegistrosIngresosEncabezado"%>
<%
	String DATE_FORMAT = "dd/MM/yyyy";
	SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	Calendar c1 = Calendar.getInstance(); // today
	String today = sdf.format(c1.getTime());	
	
	String cCentroContable = "";
	String aEjercicioFiscal = "";
	boolean bErrorAdec=false;
	boolean bAplicadoCont = false;
	String inserta = "1";
	String cUR = "";
	
	AdecuacionBusinessLogic adecProy = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	aEjercicioFiscal = adecProy.obtenEjercicioFiscal();
	
	if( Integer.parseInt( aEjercicioFiscal ) != c1.get(Calendar.YEAR) )
		today = "31/12/" + aEjercicioFiscal;
		
	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	
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
	
	RegistroIngresosBussinesLogic registroIngresos = new RegistroIngresosBussinesLogic(GestionInterface.ATT_CONEXION);
	
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	String mensaje = "";
		
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
	
	RegistrosIngresosEncabezado rie = registroIngresos.getRegistroEncabezadoNuevo(folio);
	
	CasoBusinessLogic casoTx = new CasoBusinessLogic("jdbc/gestion");
	ITree tree = casoTx.getArbolCaso(c);
	session.setAttribute("tree.model", tree);
	
	cUR = usuario.getU_UR();
	
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Ingresos Propios</title>

<!-- Estilos estandar para los controles JQuery -->
<link rel="stylesheet" type="text/css"
	href="../css/datepickercontrol_bluegray.css" />
<link rel="stylesheet" type="text/css"
	href="../Ayudas/css/autocompleta.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/themes/smoothness/jquery-ui-1.8.4.custom.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_table_jui.css"></link>
<link rel="stylesheet" type="text/css"
	href="../Generador/css/demo_page.css"></link>

<style type="text/css">

.notEditable {
	background-color: #CCCCCC;
}

</style>

<script type="text/javascript" src="../Generador/js/jquery-1.6.2.min.js"> </script>
<script type="text/javascript" src="../Generador/js/crud.js"> </script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"> </script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"> </script>
<script type="text/javascript" src="../js/jsquery.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.dataTables.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.form-2.94.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery-ui-1.8.16.custom.min.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.datepicker.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.ui.tabs.js"> </script>
<script type="text/javascript" src="../js/catalogo/general.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.js"> </script>
<script type="text/javascript" src="../Generador/js/jquery.formatCurrency.all.js"> </script>
<script type="text/javascript" src="../js/jquery.blockUI-2.4.2.js"> </script>
<script type="text/javascript">
	

	
	$(document).ready(function() {
		//Inicializa las ayudas. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.AyudaSyC").subIniciaDlg();
		//Inicializa el autocompletar. Simpre debe estar presente en el onLoad (en JQuery es el $(document).ready(function() {});)
		$("input.autoCompletaSyC").subIniciaAutoCompleta();
		
		
	});

	/**
	 * 4
	 * Se ejecuta despues de que termina la funcion onSubmit 
	 */
	function onPostDisplay(id_oper) {
		//alert("Se llamo a onPostDisplay");	
			
	}
	//Boton Enviar
	function onPostSubmit(id_oper) {
		//alert("Se llamo a onPostSubmit");
		var bRegresa = true;		
		
		return bRegresa;
	}
	
	// funcion para cargar la plantilla
	function onLoadPlantilla(id_oper) {
		//alert("Se llamo a onLoadPlantilla"); 			
				
			
	}

	/**
	 * 1
	 * Funcion llamada al momento de guardar.
	 * Realiza validaciones,si todo es correcto, regresar true para que continue con el flujo
	 */
	function onSubmit(id_oper) {		
		//alert("Se llamo a onSubmit");
		var p = window.parent;
		
		var bRegresa = true;	
		
		
		return bRegresa;
	}

	/**
	 * 2
	 * Retorna el nombre del responsable siguiente.
	 */
	function ResponsableSiguiente(id_oper) {
		//alert("Se llamo a ResponsableSiguiente");		
	}

	/**
	 * 3
	 * Retorna el nombre de la operacion siguiente.
	 */
	function OperacionSiguiente(id_oper) {
		//alert("Se llamo a OperacionSiguiente");
		
	}	
	
	function setFechas(){
		$( "#fechaApl" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
		
		$( "#fechaCap" ).datepicker({
			dateFormat: "dd/mm/yy",
			showOn: "button",
			buttonImage: "../Generador/images/calendar.gif",
			buttonImageOnly: true
		});
	}
	
	function cargaProgramas(){		
		querySelectPost("tCatProgramasProyectosRead", "programacbo", {async:false});
	}
	
	
	
</script>

</head>

<body id="dt_example" bottomMargin="0" bgColor="red" leftMargin="0" topMargin="0">
	<form id="frmIngresosPropios" name="frmIngresosPropios" action="../servlet/IngresosPropiosServlet" method="post" >
		<div id="container" class="container">			
						
			<h1>Registro Ingresos</h1>			
									
		</div>
		
	</form>
</body>
</html>
