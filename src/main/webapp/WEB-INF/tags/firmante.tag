<%@tag import="com.syc.egresos.firmante.servlet.Firmante"%>
<%@tag import="java.util.List"%>
<%@tag import="org.apache.log4j.LogManager"%>
<%@tag import="org.apache.log4j.Logger"%>
<%@tag import="java.util.ArrayList"%>
<%@tag import="com.syc.gestion.servlet.GestionInterface"%>
<%@tag import="com.syc.egresos.firmante.FirmanteBussinessLogic"%>
<%@ tag language="java" body-content="empty"%>
<%@ attribute name="type" required="true" type="java.lang.String"%>
<%@ attribute name="ur" required="true" type="java.lang.String"%>
<%@ attribute name="modulo" required="true" type="java.lang.String"%>
<%!private static final Logger log = LogManager.getLogger("firmante.tag");%>
<%
log.info("Iniciando TAG Firmante");
String tipoFirmante = (String) jspContext.getAttribute("type");
String ur = (String) jspContext.getAttribute("ur");
String modulo = (String) jspContext.getAttribute("modulo");

log.trace("Generando firmantes para: " + tipoFirmante + " " + ur);

FirmanteBussinessLogic firmanteService = new FirmanteBussinessLogic(GestionInterface.ATT_CONEXION);

List<Firmante> firmantes = new ArrayList<Firmante>();
try {
	firmantes = firmanteService.obtenerFirmantes(modulo, tipoFirmante, ur);
} catch (Exception e) {
	log.error(e, e);
}
%>

<!-- Generamos el arreglo asociativo en JavaScript -->
<script>
    var puestos = puestos || {}; // Inicializamos el objeto si no existe
    <%for (Firmante f : firmantes) {%>
        puestos["<%=f.getNumeroEmpleado()%>"] = "<%=f.getPuestoEmpleado()%>";
<%}%>
	
</script>
<div class="row mb-3">
	<div class="col-md-6">
		<label class="form-label">Firmante</label> 
		 <select class="form-select firmante-select" data-puesto-id="puesto_<%= tipoFirmante %>">
			<option selected>Seleccione Firmante</option>
			<%
			for (Firmante f : firmantes) {
			%>
			<option value="<%=f.getNumeroEmpleado()%>"><%=f.getNombreEmpleado()%></option>
			<%
			}
			%>
		</select>
	</div>
	<div class="col-md-6">
		<label class="form-label">Puesto</label> <input type="text"
			class="form-control" id="puesto_<%=tipoFirmante%>" value=""
			readonly>
	</div>
</div>
