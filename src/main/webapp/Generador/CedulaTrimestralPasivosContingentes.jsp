<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page language="java" import="java.util.*" contentType="text/html; charset=UTF-8" pageEncoding="utf-8"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.sai.contabilidad.caja.CajaBusinessLogic"%>
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

	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int efa =  Integer.parseInt( adbl.obtenEjercicioFiscal() );
	
	int id_oper = -1;
	if (request.getParameter("id_oper") != null)
		id_oper = new Integer(request.getParameter("id_oper")).intValue();

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic(GestionInterface.ATT_CONEXION);
	e.setClaveUsuario(usuario.getLogin());
	e = ebl.getEmpleado(e);
	
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);	

	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);

	//Valida Centro de Costos
	if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
		cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
	}
	String mensaje = null;
	if (cCentroContable.isEmpty() || cCentroContable.equals("")) {
		mensaje = "Error: El Usuario no tiene Centro Contable asignado y no podra realizar aplicacion Contable, Consulte a su administrador.";
		return;
	}

	cUR = usuario.getU_UR();	
	cRamo = usuario.getU_Ramo();
	algo = usuario.getLogin();
	
	if (session.getAttribute("RESULT") != null) {
		mensaje = (String) session.getAttribute("RESULT");
		session.removeAttribute("RESULT");
	}
	
	String fAplicacion[] = CajaBusinessLogic.readfAplicacion(cCentroContable, cUR);
	System.out.println("0: Fecha de Aplicacion "+fAplicacion[0]);
	System.out.println("1: Fecha Minima "+fAplicacion[1]);
	System.out.println("2: Fecha Maxima "+fAplicacion[2]);	

%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<title>Cedula Trimestral Pasivos Contingentes</title>

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="-1" />
<meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />

<link rel="stylesheet" href="css/bootstrap.min.css"/>
<link rel="stylesheet" type="text/css"	href="../Generador/css/sweetalert2.min.css"></link>

<script src="js/bootstrap.bundle.min.js"></script>
<script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
<script type="text/javascript" src="js/jquery.jeditable-1.6.2.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.js"></script>
<script type="text/javascript" src="js/jquery.dataTables.editable-1.3.js"></script>
<script type="text/javascript" src="js/jquery-ui-1.8.16.custom.min.js"></script>
<script type="text/javascript" src="js/jquery.ui.core.js"></script>
<script type="text/javascript" src="js/jquery.ui.widget.js"></script>
<script type="text/javascript" src="js/jquery.ui.tabs.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.js"></script>
<script type="text/javascript" src="js/jquery.formatCurrency.all.js"></script>
<script type="text/javascript" src="../Ayudas/js/ayudasDlg2.0.js"></script>
<script type="text/javascript" src="../Ayudas/js/autoCompleta.js"></script>
<script type="text/javascript" src="js/crud.js"></script>
<script type="text/javascript" src="../Generador/js/sweetalert2.all.min.js"></script>

<script type="text/javascript" charset="utf-8">
						
$(document).ready(function() {	
	
	var msg = "<%=StringUtils.isEmpty( mensaje )?"":mensaje.replaceAll("\n", "").replaceAll("\r", "")%>";
	
	$("#cCentroContable").val( "<%=cCentroContable%>");
	$("#usuario").val("<%=algo%>");	
		
	$("#cedula").button();	
	
	$("#msgDialog").dialog({
		autoOpen : false,
		height : 250,
		width : 400,
		modal : true,
		buttons : {
			"Aceptar" : function() {
				$(this).dialog("close");
			}
		}
	});
	
	$('select').empty().append('mes');
	if ($('#aEjercicioFiscal').val() >= 2019){
		$('#mes').append("<option value='1' >Enero		  </option>");
		$('#mes').append("<option value='2' >Febrero	  </option>");	
		$('#mes').append("<option value='3' > Marzo	  </option>");	
		$('#mes').append("<option value='4' > Abril	  </option>");	
		$('#mes').append("<option value='5' > Mayo       </option>");	
		$('#mes').append("<option value='6' > Junio      </option>");	
		$('#mes').append("<option value='7' > Julio      </option>");	
		$('#mes').append("<option value='8' > Agosto     </option>");	
		$('#mes').append("<option value='9' > Septiembre </option>");	
		$('#mes').append("<option value='10' >Octubre    </option>");	
		$('#mes').append("<option value='11' >Noviembre  </option>");	
		$('#mes').append("<option value='12' >Diciembre  </option>");		
	}else {
		$('#mes').append("<option value='3' >Enero - Marzo</option>");
		$('#mes').append("<option value='6' >Abril - Junio</option>");
		$('#mes').append("<option value='9' >Julio - Septiembre</option>");
		$('#mes').append("<option value='12' >Octubre - Diciembre </option>");
	}
	
	
	if( msg != "" )
		Swal.fire({ icon: "success",
					text: msg });
	
});
	
	function extraeCedula(){		
			document.TempForm.action="../pasivoscontingentes/PasivosContingentes";
			document.TempForm.method="GET";
			document.TempForm.target="_blank";
			document.TempForm.submit();		
	}	
	
</script>

</head>
<body id="dt_example">
<br/>
	<form id="TempForm" name="TempForm" >		

	<div id=container style="width: 50%" class="container">		
		<input type="hidden" id="nMes" name="nMes" />
		<input type="hidden" id="pasivoCapturado" name="pasivoCapturado" value="0"/>
		<input type="hidden" id="pasivoActualizado" name="pasivoActualizado" value="0"/>
		<input type="hidden" id="cUnidadResponsable" name="cUnidadResponsable" value="<%=cUR%>" /> 
		<input type="hidden" id="cCentroContable" name="cCentroContable"/>
		<input type="hidden" id="usuario" name="usuario" value="<%=algo%>" />	
		<input type="hidden" id="aEjercicioFiscal" name="aEjercicioFiscal" value="<%=efa%>"/>	

		<div class="card-header"> <h3> Cédula Pasivos Contingentes </h3> </div>
		<hr class="mt-3"/>	
		
		<div class="row d-flex justify-content-center">
			<div class="col-12 col-md-6 mb-3">
				<input type="radio" name="TIPO_REPORTE" id="TIPO_REPORTE1" class="form-check-input" value = "6" onclick="selecciona(this)" checked/>
				<label for="cedula" class="form-check-label">Cédula</label>
			</div>		
		</div>																									

		<div id="dcedula">
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">										
					<label for="mes" class="form-label"> Periodo de Consulta: </label>											
					<select id="mes" name="mes" class="form-select form-select-sm" style="width: 12em;">
					</select>																													
				</div>																																										
			</div>
			
			<div class="row d-flex justify-content-center">
				<div class="col-12 col-md-6 mb-3">
					<div>
						<button type="button" id="cedula" name="cedula" class="btn btn-secondary" onclick="extraeCedula()"> Cédula </button>							
					</div>
				</div>
				<br/>									
			</div>
												
		</div>													
		
	</div>																					
</form>

</body>
</html>


