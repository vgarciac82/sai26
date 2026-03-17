<%@page language="java" import="java.util.*" pageEncoding="ISO-8859-1"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.contable.core.Saldo"%>
<%@page import="com.syc.contable.core.AdecuacionCalendario"%>
<%@page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>
<%@page import="com.syc.contable.AdecuacionBusinessLogic"%>
<%@page import="com.syc.contable.core.FIAFEncabezado"%>
<%

	Caso c = (Caso) session.getAttribute(GestionInterface.ATT_CASE);
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

	
	AdecuacionBusinessLogic adecua = new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);
		
	int nFolio = new Integer(c.getFolio().substring(c.getFolio().lastIndexOf('-') + 1)).intValue();
	int nConsecutivoSICOP = adecua.consecutivoSICOPFIAF(nFolio);
	
	AdecuacionCalendario ac = adecua.adecuacionCalendarioFIAF(nFolio);
	FIAFEncabezado fe = adecua.getFIAFEncabezado(nFolio);
	
	String file_name=c.getFolio();
	String newfile_name= file_name.substring(0,9)+ nConsecutivoSICOP;
	response.setContentType("application/vnd.ms-excel");
	response.addHeader("Content-Disposition", "inline; filename=\"" + newfile_name + ".xls\";");

	out.println("<html>");
	//out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"../css/reportes.css\" />");
	
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
	out.println("			    	<tr><td align=\"left\">"+fe.getcRamo()+"</td><td align=\"left\">"+fe.getcUnidadResponsable()+"</td><td align=\"left\">"+fe.getaEjercicioFiscal()+"</td></tr>");
	out.println("			    	<tr><th align=\"left\">JUSTIFICACION AMPLIACION</th></tr>");
	out.println("			    	<tr><td align=\"left\">"+fe.getcJustificacionA()+"</td></tr>");
	out.println("			    	<tr><th align=\"left\">JUSTIFICACION REDUCCION</th></tr>");
	out.println("			    	<tr><td align=\"left\">"+fe.getcJustificacionR()+"</td></tr>");
	out.println("			    	<tr><th align=\"left\">JUSTIFICACION NORMATIVA</th></tr>");
	out.println("			    	<tr><td align=\"left\">"+fe.getcJustificacionNormativa()+"</td></tr>");
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
	for(int i=0;i<ac.saldoLength();i++){
		Saldo objSaldo = ac.getSaldo(i);
		out.println("				<tr>");
		out.println("				<th>"+objSaldo.getMensajeSecuencia()+"</th>");
		out.println("				<th>"+(i+1)+"</th>");
		out.println("				<th>"+objSaldo.getClaveSIAFF()+"</th>");
		out.println("				<th>"+objSaldo.getClaveInterna()+"</th>");
		out.println("				<th>"+objSaldo.getClaveCNA()+"</th>");
		out.println("				<th>"+objSaldo.getEp()+"</th>");
		out.println("				<th>"+objSaldo.getMontoAnual()+"</th>");
		out.println("				<th>"+objSaldo.getMontoEnero()+"</th>");
		out.println("				<th>"+objSaldo.getMontoFebrero()+"</th>");
		out.println("				<th>"+objSaldo.getMontoMarzo()+"</th>");
		out.println("				<th>"+objSaldo.getMontoAbril()+"</th>");
		out.println("				<th>"+objSaldo.getMontoMayo()+"</th>");
		out.println("				<th>"+objSaldo.getMontoJunio()+"</th>");
		out.println("				<th>"+objSaldo.getMontoJulio()+"</th>");
		out.println("				<th>"+objSaldo.getMontoAgosto()+"</th>");
		out.println("				<th>"+objSaldo.getMontoSeptiembre()+"</th>");
		out.println("				<th>"+objSaldo.getMontoOctubre()+"</th>");
		out.println("				<th>"+objSaldo.getMontoNoviembre()+"</th>");
		out.println("				<th>"+objSaldo.getMontoDiciembre()+"</th>");
		out.println("				</tr>");	
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
