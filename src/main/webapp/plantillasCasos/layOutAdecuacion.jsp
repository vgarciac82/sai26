<%@page import="org.slf4j.Logger"%>
<%@page import="com.syc.contable.adecuaciones.AdecuacionDetalle"%>
<%@page import="com.syc.contable.adecuaciones.Adecuacion"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.adecuaciones.AdecuacionEncabezado"%>
<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%!
	private static final Logger log = LoggerFactory.getLogger("layOutAdecuacion.jsp");
%>
<%

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	
	Adecuacion adecuacion = null;
	AdecuacionBusinessLogic adbl = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
	int folio = Integer.parseInt( c.getFolio().substring( c.getFolio().lastIndexOf("-") + 1  ) );
	adecuacion = adbl.cargaAdecuacion(folio); 
	AdecuacionEncabezado encabezado = adecuacion.getEncabezado();	
	
	response.setContentType("application/vnd.ms-excel");
	String file_name=c.getFolio();
	response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + ".xls\";");

	out.println("<html>");
	
	out.println("<body>");
	out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
	out.println(" <tr>");
	out.println(" <td><img src=\"../imagenes/logotipo-usuario.png\"></td>");
	out.println(" </tr>");
	out.println(" <tr><td align=\"center\"><B>COMISI&Oacute;N NACIONAL FORESTAL</B></td></tr>");
	out.println(" <tr><td align=\"center\"><B>LAYOUT ADECUACION</B></td></tr>");					
	out.println(" <tr><td align=\"center\"><B>"+new Date().toGMTString()+"</B></td></tr>");
	out.println(" <tr><td align=\"center\">&nbsp;</td></tr>");					
	out.println(" <tr>");
	out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\">");
	out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
	out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
	out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
	out.println("				</thead>");
	out.println("				<tbody class=\"scrollContent\">");
	out.println("			    	<tr><th align=\"left\">RAMO</th><th align=\"left\">UNIDAD RESPONSABLE</th><th align=\"left\">EJERCICIO FISCAL</th></tr>");
	out.println("			    	<tr><td align=\"left\">"+encabezado.getRamo()+"</td><td align=\"left\">"+encabezado.getUnidadEjecutora() +"</td><td align=\"left\">"+encabezado.getEjercicioFiscal()+"</td></tr>");
	out.println("			    	<tr><th align=\"left\">JUSTIFICACION</th></tr>");
	out.println("			    	<tr><td align=\"left\">"+encabezado.getJustificacion()+"</td></tr>");
	out.println("				</tbody>");
	out.println("			</table>");
	out.println("		</div>");
	out.println("	 </td>");
	out.println("  </tr>");
	out.println("</table>");

	out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
	out.println(" <tr>");
	out.println("	<td  colspan=\"2\" height=\"98%\" valing=\"top\">");
	out.println("		<div id=\"tableContainer\" class=\"tableContainer\">");
	out.println("			<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"scrollTable\">");
	out.println("				<thead class=\"fixedHeader\" id=\"fixedHeader\">");
	out.println("				</thead>");
	out.println("				<tbody class=\"scrollContent\">");
	out.println("				<tr>");
	out.println("				<th>Error de Secuencia</th>");
	out.println("				<th>SECUENCIA</th>");
	out.println("				<th>CLAVE SIAFF O MAP</th>");
	out.println("				<th>CLAVE INTERNA</th>");
	out.println("				<th>CODIGO SAI</th>");
	out.println("				<th>TIPO</th>");
	out.println("				<th>MONTO ANUAL</th>");
	out.println("				<th>ENERO</th>");
	out.println("				<th>FEBRERO</th>");
	out.println("				<th>MARZO</th>");
	out.println("				<th>ABRIL</th>");
	out.println("				<th>MAYO</th>");
	out.println("				<th>JUNIO</th>");
	out.println("				<th>JULIO</th>");
	out.println("				<th>AGOSTO</th>");
	out.println("				<th>SEPTIEMBRE</th>");
	out.println("				<th>OCTUBRE</th>");
	out.println("				<th>NOVIEMBRE</th>");
	out.println("				<th>DICIEMBRE</th>");
	out.println("				</tr>");
	for(Iterator<AdecuacionDetalle> i = adecuacion.getDetalle().iterator(); i.hasNext();){
		try{    
			AdecuacionDetalle objSaldo = i.next();
			out.println("				<tr>");
			out.println("				<td>&nbsp;</td>");
			out.println("				<td>"+objSaldo.getSecuencia()+"</td>");
			out.println("				<td>"+objSaldo.getClaveSIAFF()+"</td>");
			out.println("				<td>"+objSaldo.getClaveInterna()+"</td>");
			out.println("				<td>&nbsp;</td>");
			out.println("				<td>"+objSaldo.getTipo()+"</td>");
			for( int cnt = 0; cnt < Util.NOMBRE_MESES_ADECUACIONES.length; cnt++){
				out.println("				<td>"+objSaldo.getMontos().get(cnt)+"</td>");
					
			}
			out.println("				</tr>");
		}catch(Exception cce){
			log.error(cce);
		}
	}
	out.println("				</tbody>");
	out.println("			</table>");
	out.println("		</div>");
	out.println("	    </td>");
	out.println("  </tr>");
	out.println("</table>");

	out.println("</body>");
	out.println("</html>");
	out.flush();	

%>
