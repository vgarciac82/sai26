<%@page import="com.syc.obrapublica.EjercicioFiscalBusinessLogic"%>
<%@page import="com.syc.gestion.util.Util"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
String cCentroContable = "";
Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);

EjercicioFiscalBusinessLogic efbl = new EjercicioFiscalBusinessLogic(GestionInterface.ATT_CONEXION);

if (usuario == null || c == null) {
	response.sendRedirect("../index.jsp");
	return;
}

if (usuario.getPropiedades() != null && usuario.getPropiedades().containsKey("CCENTROCONTABLE")) {
	cCentroContable = usuario.getPropiedad("CCENTROCONTABLE").getValor();
}

String ur = "";
ur = usuario.getU_UR();

int nFolio = Util.folio(c);
String cFolio = c.getFolio();
String login = usuario.getLogin();
String ejercicioFiscal = efbl.getEjercicioFiscalActivo().getaEjercicioFiscal();
String tipoPago = c.getTipoCaso().getGavetaAsociada();
String fAplicacion = Util.calculaFechaAplicacion(efbl.getEjercicioFiscalActivo());
String mes = fAplicacion.split("/")[1];

%>
<!doctype html>
<html lang="es">

<head>
<meta charset="utf-8" />
<title>Captura Suficiencia para Pago Directo</title>
<meta name="viewport" content="width=device-width, initial-scale=1" />

<!-- Bootstrap 5 -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
	rel="stylesheet">
<!-- DataTables -->
<link
	href="https://cdn.datatables.net/1.13.8/css/dataTables.bootstrap5.min.css"
	rel="stylesheet" />
<!-- Font Awesome -->
<link
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css"
	rel="stylesheet" />
<link href="css/CapturaSuficienciaDirecto.css" rel="stylesheet" />
    <style>
        .progress-bar-striped {
            transition: width 0.6s ease;
        }
        
	  #modalCalendarioEP .cal-disp,
	  #modalCalendarioEP .cal-input {
	    background-color: transparent !important;  
	    box-shadow: none;
	  }
	
	  #modalCalendarioEP .cal-input {
	    max-width: 110px;                  
	    padding: 0 .35rem;
	    font-size: 0.875rem;
	    height: calc(1.4em + .4rem);
	  }
	
	  #modalCalendarioEP .cal-disp {
	    font-size: 0.875rem;
	    padding: 0;
	  }
    </style>
<script>
	
	const folioSAI = "<%=cFolio%>";
	const folio =<%=nFolio%>;
	const tipoPago = "<%=tipoPago%>";
	const cCentroContable = "<%=cCentroContable%>";
	const unidadResponsable = "<%=ur%>";
	const ejercicioFiscal = "<%=ejercicioFiscal%>";
	const login = "<%=login%>";
	const fechaAplicacion = "<%=fAplicacion%>";
	const mes = "<%=mes%>";

	// Funciones CG-FLOW
	function onPostDisplay() {
		console.log("onPostDisplay called");
	}

	function onPostSubmit() {
		console.log("onPostSubmit called");
	    parent.document.getElementById("responsable").value = "CONSULTA_SUFICIENCIAPAGODIRECTO";
	    parent.document.getElementById("operacion").value = "consulta_suficienciapdir";
		return true;
	}

	function onLoadPlantilla() {
		console.log("onLoadPlantilla called");
	}
	
	function onSubmit() {
		console.log("onSubmit called");
		return true;
	}

	function ResponsableSiguiente(idOper) {
		return 'CONSULTA_SUFICIENCIAPAGODIRECTO'
	}

	function OperacionSiguiente(idOper) {
		return 'consulta_suficienciapdir'
	}
</script>

</head>

<body>

	<jsp:include page="CapturaSuficienciaDirectoBody.jsp"></jsp:include>
	<!-- jQuery -->
	<script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
	<!-- Bootstrap -->
	<script
		src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<!-- DataTables -->
	<script
		src="https://cdn.datatables.net/1.13.8/js/jquery.dataTables.min.js"></script>
	<script
		src="https://cdn.datatables.net/1.13.8/js/dataTables.bootstrap5.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="../../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script type="text/javascript" src="../../Generador/js/crud.js"></script>
	<script src="js/CapturaSuficienciaDirecto.js"></script>

</body>

</html>

