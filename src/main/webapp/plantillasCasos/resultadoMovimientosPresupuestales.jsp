&nbsp;<%@ page import="java.util.*" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.syc.gestion.core.Usuario"%>
<%@ page import="com.syc.gestion.servlet.GestionInterface"%>
<%@ page import="com.syc.contable.core.Saldo"%>
<%@ page import="com.syc.gestion.core.*,com.syc.gestion.servlet.*,com.syc.gestion.util.*"%>

<%
	Usuario usuario = (Usuario)session.getAttribute(GestionInterface.ATT_USER);
	if (usuario == null) {
		response.sendRedirect("../index.jsp");
		return;
	}

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
	
	if(session.getAttribute("movimientos") != null){
		msg = (String) session.getAttribute("mensaje");
		ep = (String) session.getAttribute("ep");
		tipoCuenta = (String) session.getAttribute("tipoCuenta");
		strDesde = (String)	session.getAttribute("strDesde");
		strHasta = (String) session.getAttribute("strHasta");
		listasb = (List<StringBuffer>) session.getAttribute("movimientos");
		//session.removeAttribute("movimientos");
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

	response.setContentType("application/vnd.ms-excel");
	String file_name = "MovimientosPresupuestales";

	response.addHeader("Content-Disposition", "inline; filename=\"" + file_name + "_" + System.currentTimeMillis() + ".xls\";");
	//response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
	//response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
	//response.setDateHeader("Expires", 0);
	
	out.println("<html>");
	out.println("<body>");
	out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
	out.println("	<tr>");
	out.println("	<br/>");
	out.println("	</tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\"><B>COMISI&Oacute;N NACIONAL FORESTAL</B></td></tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\">&nbsp;</td></tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\"><B>MOVIMIENTOS PRESUPUESTALES CORRESPONDIENTES</B></td></tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\"><B>A LA EP: " + ep + "</B></td></tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\"><B>CUENTA: " + tipoCuenta + "</B></td></tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\"><B>DESDE: " + strDesde + "&nbsp;&nbsp;&nbsp;&nbsp;HASTA: " + strHasta + "</B></td></tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\">&nbsp;</td></tr>");
	out.println("	<tr><td align=\"center\" colSpan=\"16\"><B>"+ new Date().toLocaleString()+"</B></td></tr>");
	out.println("	<tr><td align=\"center\">&nbsp;</td></tr>");					
	out.println("</table>");
	out.println("<br />");
	out.println("<br />");
	out.println("<table align=\"center\" cellpadding=\"0\" cellspacing=\"1\" border=\"0\" width=\"100%\" height=\"98%\">");
	out.println("	<tr>");
	out.println("		<td colspan=\"2\" height=\"98%\" valing=\"top\">");
	out.println("			<div id=\"tableContainer\" class=\"tableContainer\">");
	out.println("				<table id=\"tblGeneral\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" class=\"table table-striped\">");
	out.println("					<thead class=\"fixedHeader\" id=\"fixedHeader\">");
	out.println("					</thead>");
	out.println("					<tbody class=\"scrollContent\">");
	out.println("						<tr>");
	out.println("							<th>Fecha Movimiento</th>");
	out.println("							<th>Tipo Documento</th>");
	out.println("							<th>Folio Documento</th>");
	out.println("							<th>Estatus</th>");
	out.println("							<th>Enero</th>");
	out.println("							<th>Febrero</th>");
	out.println("							<th>Marzo</th>");
	out.println("							<th>Abril</th>");
	out.println("							<th>Mayo</th>");
	out.println("							<th>Junio</th>");
	out.println("							<th>Julio</th>");
	out.println("							<th>Agosto</th>");
	out.println("							<th>Septiembre</th>");
	out.println("							<th>Octubre</th>");
	out.println("							<th>Noviembre</th>");
	out.println("							<th>Diciembre</th>");
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" +campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "" ) + "</td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (("C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				}
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + ( ("C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (("C".equals(campos[13]) ) ? "CANCELADO" : "" ) + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");

				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");

				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} 
	out.println("							<td></td>");
			}
		}
	}
	out.println("						</tr>");

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
	out.println("						<tr>");
	out.println("							<td>" + campos[14] + "</td>");
	out.println("							<td>" + campos[8] + "</td>");
	out.println("							<td>" + campos[12] + "</td>");
	out.println("							<td>" + (( "C".equals(campos[13]) ) ? "CANCELADO" : "") + "</td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
	out.println("							<td></td>");
				if( campos[20].equalsIgnoreCase("A") ){
					if( campos[5].equalsIgnoreCase("A") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				} else if( campos[20].equalsIgnoreCase("D") ){
					if( campos[5].equalsIgnoreCase("C") ){
	out.println("							<td>" + campos[4] + "</td>");
					} else{
	out.println("							<td>" + negativo + "</td>");
					}
				}
			}
		}
	}
	out.println("					</tr>");
out.println("				</tbody>");
out.println("			</table>");
out.println("		</div>");
out.println("	</td>");
out.println(" </tr>");
out.println("</table>");
out.println("</body>");
out.println("</html>");
out.flush();
%>	