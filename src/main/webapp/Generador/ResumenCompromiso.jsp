<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="com.jenkov.prizetags.tree.itf.ITree"%>
<%@page import="com.syc.gestion.CasoBusinessLogic"%>
<%@page import="org.apache.commons.lang.StringUtils"%>
<%@page import="com.syc.gestion.core.Caso"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@ page import="java.util.*"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%
	Usuario usuario = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	String action = request.getParameter("a");
	
	int nFolioPago = Integer
			.parseInt(StringUtils.isEmpty(request.getParameter("f")) ? "0" : request.getParameter("f"));
			
	String cTipoPago = request.getParameter("d");
	
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	
	String cLogin = usuario.getLogin();
	String cUR = usuario.getU_UR();
	String cCentroContable= usuario.getPropiedad("CCENTROCONTABLE").getValor();
	String RFCUsuario = usuario.getuRFC();
	
	String result = StringUtils.trimToEmpty((String) session.getAttribute("RESULT"));

	String fielMsg = "";
	boolean fielExpiringSoon = session.getAttribute("EXPIRING_SOON") != null && ((Boolean)session.getAttribute("EXPIRING_SOON")); 
	if(fielExpiringSoon){
		fielMsg = (String) session.getAttribute("EXPIRING_MSG");
		session.removeAttribute("EXPIRING_SOON");
		session.removeAttribute("EXPIRING_MSG");
	}
	
	String tipoAutorizacion = ( "VoBoPago".equals(action) ? "VOBO" : ( "AutPago".equals(action) ? "AUT": ("RVoBoPago".equals(action) ? "R_VOBO" : "RAutPago".equals(action) ? "R_AUT": "") ) );
	String numeroEmpleado = "";
	numeroEmpleado = usuario.getNumeroEmpleado();
	String msg = StringUtils.trimToEmpty(request.getParameter("msgError"));
	
	boolean mostrarResultado = !StringUtils.isBlank(result);
	CasoBusinessLogic cbl = new CasoBusinessLogic(GestionInterface.ATT_CONEXION);
	Caso c = cbl.findByFolioLike( cTipoPago, String.valueOf( nFolioPago )  );
	ITree tree = cbl.getArbolCaso(c);
	session.setAttribute(GestionInterface.ATT_TREE, tree);
	session.setAttribute( GestionInterface.ATT_CASE, c );
	
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Compromiso</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.datatables.net/2.3.4/css/dataTables.bootstrap5.min.css" rel="stylesheet">
    <style>
        h1 {
			font-size: 1.3em;
			font-weight: normal;
			line-height: 1.6em;
			color: #4E6CA3;
			border-bottom: 1px solid #B0BED9;
			clear: both;
			margin-top: 0px;
		}
    </style>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/jquery/3.7.1/jquery.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
	<script src="https://cdn.datatables.net/2.3.4/js/dataTables.min.js"></script>
	<script src="https://cdn.datatables.net/2.3.4/js/dataTables.bootstrap5.min.js"></script>
	<script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
	<script src="../Generador/js/jquery.blockUI-2.70.0.js"></script>
	<script type="text/javascript" src="../Generador/js/crud.js"></script>
	<script type="text/javascript" src="../FIEL/js/ResumenFIEL.js"></script>
    <script type="text/javascript" charset="utf-8">
		const cUR = "<%=cUR%>";
		const cLogin = "<%=cLogin%>";
		const RFC = "<%=RFCUsuario%>";
		const numeroEmpleado = "<%=numeroEmpleado%>";
		const mostrarResultado = <%=mostrarResultado%>;
		const mensaje = "<%=msg%>";
		const tipoAutorizacion = "<%=tipoAutorizacion%>";
		const fielExpiringSoon = <%=fielExpiringSoon%>;
		
	</script>
</head>
<body>

	<div class="container">
		<div class="row mb-3">
			<div class="col text-end">
				<a href="#" onclick="regresar();return false;">Ver listado de pendientes</a>
			</div>
		</div>
	</div>	
	<jsp:include page="CuerpoResumenCompromsio.jsp"></jsp:include>
	<jsp:include page="../FIEL/OperacionesFIEL.jsp"></jsp:include>
	<script>
	const folio = <%=nFolioPago%>;
	$(document).ready(function(){
		
		$("#tipoAutorizacion").val("AUT");
		$("#tipoPagoSeleccionado").val("COMPROMISO");
		$("#rfcFirma").val(RFC);
		$("#nFolios").val(folio);
		$("#nFolioPago").val(folio);
		
		$("#urlRetorno").val("Generador/ResumenCompromiso.jsp");
		$("#cTipoPago").val("<%=cTipoPago%>");
		
		$('#mnLogTbl tbody').html("<%=result%>");
		$('#msgWarning').html("<%=fielMsg%>");
		if( fielExpiringSoon )
			$('#fielWarning').removeClass('d-none');
		
		initFiel();
		consultarContratoPorFolio(folio);
		creaDT(folio);
		getDocuments();
	});
	</script>
</body>
</html>