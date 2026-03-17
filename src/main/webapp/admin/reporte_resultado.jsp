<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="javax.naming.InitialContext"%>
<%@page import="javax.naming.NamingException"%>
<%@page import="com.syc.gestion.core.Usuario"%>
<%@page import="com.syc.gestion.servlet.GestionInterface"%>
<%@page import="com.syc.gestion.reportes.ReporteBussinesLogic" %>
<%@page import="com.syc.gestion.reportes.core.ReporteConf" %>
<%@page import="java.lang.Integer" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.Date" %>
<%@page import="java.util.Calendar" %>
<%@ page import="com.syc.gestion.core.Empleado"%>
<%@ page import="com.syc.gestion.EmpleadoBusinessLogic"%>
<%@ page import="com.syc.gestion.core.EmpleadoArea"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%!
	private Logger log = LoggerFactory.getLogger(getClass());
	String headerParameterHtml = "";
	private String jniName = null;
	public String tipo_consulta =null;
	public String nombre ="",empleado="",area_general="";

	public void jspInit() {
		try {
			InitialContext ic = new InitialContext();
			jniName = (String) ic.lookup("java:comp/env/dataSourceRefName");

			if (jniName == null) {
				jniName = "jdbc/gestion";
				log.info("Environment Entry \"dataSourceRefName\" nula usando default \"" + jniName + "\"");
			} else
				log.info("dataSourceRefName=" + jniName);
		} catch (NamingException exc) {
			jniName = "jdbc/gestion";
			log.info("Environment Entry \"dataSourceRefName\" no definida usando default \"" + jniName + "\"");
		}
	}

	public String getValor(String data){
		return (data == null? "": data);
	}

	//Esteban Badillo. Fecha: 19/Enero/2010
	//Descripcion: Se agregan dos parametros que contienen el nombre del usuario conectado al sistema, as�
	//como la cuenta de suplantaci�n, en su caso.
	public String getParamGeneralPDF(String fechaini, String fechafin,
	                                 String env_area, String env_empleado,
	                                 String rec_area, String rec_empleado,
									 String tipo_instruccion, String estatus,
	                                 String prioridad, String rptname, String nlogin, String nlogine){

		String param = "catalogo=REPORTE&accion=run"
						+ "&rn="      + rptname;
		/*
		if(fechaini=="" & fechafin=="" & area!=""){
				param +=  "&area="    + area
						+ "&estruc="  + estructura
						+ "&chkArea=" + detallado;
		} else if(fechaini!="" & fechafin!="" & area!=""){
			String whereFecha  = "AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN '"+fechaini+"' AND '"+fechafin+"'";
			String tituloFecha = "Del " +fechaini+ " Al " +fechafin;

			param += "&area="        + area
				   + "&whereFecha="  + whereFecha
				   + "&estruc="      + estructura
				   + "&tituloFecha=" + tituloFecha
				   + "&chkArea="     + detallado;
		}
		*/

		Calendar c = Calendar.getInstance();

		headerParameterHtml = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"98%\">"
							+ "<tr>"
							+ "     <td colspan='12'></td>"
							+ "</tr>"
							+ "	<tr>"
							+ "		<td rowspan=\"2\" colspan=\"2\">"
							+ "			<img src=\"../imagenes/logotipo.png\" width=\"231\" height=\"50\">"
							+ "		</td>"
							+ "		<td colspan=\"5\" align=\"center\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>SISTEMA INSTITUCIONAL DE CONTROL DE GESTION</strong></font>"
							+ "		</td>"
							+ "		<td colspan=\"5\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"5\" align=\"center\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>REPORTE GENERAL</strong></font>"
							+ "		</td>"
							+ "		<td colspan=\"5\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"12\" align=\"center\">"
							+ "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"80%\">"
							+ "<tr> <td colspan='12'></td> </tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Fecha de Registro&nbsp;</td>"
							+ " 	<td colspan=\"3\">Del:  "
							/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
							Esteban Badillo. Fecha: 18/Enero/2010
							Descripcion. Se modifica la presentaci�n de las fechas, cuando �stas traen valores vac�os
							o nulos, se establece la fecha por default a partir del 1� de Enero del a�o en curso y
							hasta la fecha del d�a actual del sistema.
							* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
							/*
							+ "		<th>" + fechaini +  "</th>"
						    + "		<td>Al:</td>"
							+ "		<td>" + fechafin + "</td>"
							*/
							//+ "		<th>" + (fechaini.trim().equals("") ? "01/01/" + String.valueOf(new SimpleDateFormat("dd-MMM-yyyy").format(new Date())).substring(7,11) : fechaini ) +  "</th>"
							;
							if(fechaini.trim().equals("") || fechafin.trim().equals(""))
							{
								headerParameterHtml += " " + "- - - - - - -" +  " "
						    	+       " Al:  "
								//+ "		<td>" + (fechafin.trim().equals("") ? Integer.toString(c.get(Calendar.DATE)) + "/" + (Integer.toString((c.get(Calendar.MONTH)) + 1).length() > 1 ? Integer.toString((c.get(Calendar.MONTH)) + 1) : "0" + Integer.toString((c.get(Calendar.MONTH)) + 1)   )   + "/" + Integer.toString(c.get(Calendar.YEAR)) : fechafin) + "</td>"
								+       " " + " - - - - - - - " + " "
								;
							}
							else
							{
								headerParameterHtml += " " + fechaini +  " "
						    	+ " Al: "
								+ " " + fechafin + " "
								;
							}
							/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
		headerParameterHtml	+= "	<td colspan=\"8\"></td></tr>"
							/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
							Esteban Badillo. Fecha: 19/Enero/2010.
							Descripcion: Se agrega la impresion del usuario conectado (y cuenta de suplantaci�n) que
							genera el reporte.
							*/
							+ "<tr>"
							+ " 	<td id=\"idThParam\">Elaborado por:</td>"
							+ " 	<td colspan=\"5\">" + nlogin + "</td>"
							+ "</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">A nombre de:</td>"
							+ " 	<td colspan=\"5\">" + (nlogine.trim().equals("") ? "- - - - - -" : nlogine) + "</td><td colspan=\"6\"></td>"
							+ "</tr>"
							+ "<tr>"
							+ " 	<td id=\"idThParam\">Fecha de elaboraci&oacute;n:</td>"
							+ " 	<td colspan=\"5\" align='left'>"
							+         Integer.toString(c.get(Calendar.DATE))
							+         "/" + (Integer.toString((c.get(Calendar.MONTH)) + 1).length() > 1 ? Integer.toString((c.get(Calendar.MONTH)) + 1) : "0" + Integer.toString((c.get(Calendar.MONTH)) + 1)   )
							+         "/" + Integer.toString(c.get(Calendar.YEAR))
							+         " " + Integer.toString(c.get(Calendar.HOUR))
							+         ":" + Integer.toString(c.get(Calendar.MINUTE))
							+         ":" + (Integer.toString(c.get(Calendar.SECOND)).length() > 1 ? Integer.toString(c.get(Calendar.SECOND)) : "0" + Integer.toString(c.get(Calendar.SECOND)))
							+         " " + (c.get(Calendar.AM_PM) > 0 ? "PM" : "AM" )
							+ "     </td>"
							+ "     <td colspan=\"6\"></td>"
							+ "</tr>"
							/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

							+ "	<tr>"
							+ " 	<td id=\"idThParam\" rowspan=\"2\" valign=\"middle\">Enviado Por:</td>"
							+ " 	<td id=\"idThParam\">&Aacute;rea:&nbsp;</td>"
							//+ " 	<td colspan=\"2\">" + env_area +"</td>" //Esteban Badillo. Fecha: 19/Enero/2010. Se complementa env_area para cuando no trae datos
							+ " 	<td colspan=\"4\">" + (env_area.trim().equals("") ? "- - - - - -" : env_area ) +"</td>"
							+ "     <td colspan=\"6\"></td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Empleado&nbsp;</td>"
							//+ " 	<td colspan=\"2\">" + (env_empleado) +"</td>" //Esteban Badillo. Fecha: 19/Enero/2010. Se complementa env_empleado para cuando no trae datos
							+ " 	<td colspan=\"4\">" + (env_empleado.trim().equals("") ? "- - - - - -" : env_empleado) +"</td>"
							+ "     <td colspan=\"6\"></td>"
							+ " </tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\" rowspan=\"2\" valign=\"center\">Recibido Por:</td>"
							+ " 	<td id=\"idThParam\">&Aacute;rea:&nbsp;</td>"
							//+ " 	<td colspan=\"2\">" + rec_area +"</td>" //Esteban Badillo. Fecha: 19/Enero/2010. Se complementa rec_area para cuando no trae datos
							+ " 	<td colspan=\"4\">" + (rec_area.trim().equals("") ? "- - - - - -" : rec_area ) +"</td>"
							+ "     <td colspan=\"6\"></td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Empleado&nbsp;</td>"
							//+ " 	<td colspan=\"2\">" + rec_empleado ) +"</td>" //Esteban Badillo. Fecha: 19/Enero/2010. Se complementa rec_empleado para cuando no trae datos
							+ " 	<td colspan=\"4\">" + (rec_empleado.trim().equals("") ? "- - - - - - - -" : rec_empleado ) +"</td>"
							+ "     <td colspan=\"6\"></td>"
							+ " </tr>"

							+ "	<tr>"
							+ " 	<td id=\"idThParam\" colspan=\"2\">Tipo de Instrucci&oacute;n:&nbsp;</td>"
							//+ " 	<td colspan=\"3\">" + tipo_instruccion ) +"</td>" //Esteban Badillo. Fecha: 19/Enero/2010 Se complementa tipo_instruccion para cuando no trae datos
							+ " 	<td colspan=\"4\">" + (tipo_instruccion.trim().equals("")  ? "- - - - - - -" : tipo_instruccion ) +"</td>"
							+ "     <td colspan=\"6\"></td>"
							+ "	</tr>"

							+ "	<tr>"
							+ " 	<td id=\"idThParam\" colspan=\"2\">Estatus:&nbsp;</td>"
							//+ " 	<td colspan=\"3\">" + getEstatus(estatus) +"</td>" //Esteban Badillo. Fecha: 19/Enero/2010. Se complementa getEstatus(estatus) para cuando no trae datos
							+ " 	<td colspan=\"4\">" + (getEstatus(estatus).trim().equals("") ? "- - - - - -" : getEstatus(estatus) ) +"</td>"
							+ "     <td colspan=\"6\"></td>"
							+ "	</tr>"

							+ "	<tr>"
							+ " 	<td id=\"idThParam\" colspan=\"2\">Prioridad:&nbsp;</td>"
							//+ " 	<td colspan=\"3\">" + prioridad +"</td>"//Esteban Badillo. Fecha: Fecha: 19/Enero/2010. Se complementa prioridad para cuando no trae datos.
							+ " 	<td colspan=\"4\">" + (prioridad.trim().equals("") ? "- - - - - - -" : ( prioridad.equals("N") ? "NORMAL": "URGENTE")) +"</td>"
							+ "     <td colspan=\"6\"></td>"
							+ "	</tr>"

							+ "	</table></td></tr></table>";
		return param;
	}

	public String getEstatus(String opc){
		String valor = "";
		opc = ((opc.equals("") || opc == null)? "0": opc);
		switch(Integer.parseInt(opc,10)){
			case 4:
				valor = "CONCLUIDO";
				break;
			case 3:
				valor = "PENDIENTE";
				break;
			case 1:
				valor = "PENDIENTE VENCIDO";
				break;
			case 2:
				valor = "PENDIENTE NO VENCIDO";
				break;
		}
		return valor;
	}

	public String getParamAcumuladoAreaPDF(String area,     String area_sol, String estructura,
	                          			   String fechaini, String fechafin, String rptname,
	                          			   boolean detallado ){

		String param = "catalogo=REPORTE&accion=run"
						+ "&rn="      + rptname;
		if(fechaini=="" & fechafin=="" & area!=""){
				param += "&area="     + area
					   + "&area_sol=" + area_sol
					   + "&estruc="   + estructura
					   + "&chkArea="  + detallado;
		} else if(fechaini!="" & fechafin!="" & area!=""){
			String whereFecha  = "AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN '"+fechaini+"' AND '"+fechafin+"'";
			String tituloFecha = "Del " +fechaini+ " Al " +fechafin;

			param += "&area="        + area
				   + "&whereFecha="  + whereFecha
				   + "&area_sol="    + area_sol
				   + "&estruc="      + estructura
				   + "&tituloFecha=" + tituloFecha
				   + "&chkArea="     + detallado;
		}

		headerParameterHtml = "<table border=\"1\" cellpadding=\"0\" cellspacing=\"1\" width=\"98%\">"
							+ "	<tr>"
							+ "		<td rowspan=\"3\" colspan=\"3\" width=\"15%\">"
							+ "			<img src=\"../imagenes/logotipo.png\" width=\"230\" height=\"70\">"
							+ "		</td>"
							+ "		<td align=\"center\" width=\"60%\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>" + area + "</strong></font>"
							+ "		</td>"
							+ "		<td width=\"15%\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td align=\"center\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>SISTEMA INSTITUCIONAL DE CONTROL DE GESTI&Oacute;N</strong></font>"
							+ "		</td>"
							+ "		<td width=\"15%\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td align=\"center\">"
							+ "			<font size=\"1\" face=\"SansSerif, Verdana\"><strong>REPORTE ACUMULADO POR AREA</strong></font>"
							+ "		</td>"
							+ "		<td width=\"15%\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"2\" align=\"center\">"
							+ "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"80%\">"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Fecha de Registro&nbsp;</td>"
							+ " 	<td>Del:</td>"
							+ "		<th>" + fechaini +  "</th>"
						    + "		<td>Al:</td>"
							+ "		<td>" + fechafin + "</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">&Aacute;rea:&nbsp;</td>"
							+ " 	<td colspan=\"4\">" + area_sol +"</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Detallado:&nbsp;</td>"
							+ " 	<td colspan=\"4\">" + (detallado? "SI": "NO") +"</td>"
							+ "	</tr>"
							+ "	</table></td></tr></table>";
	return param;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	Esteban Badillo. Fecha: 14/Oct/2009
	Descripción:
		Se agrega la sobrecarga del metodo getParamAcumuladoAreaPDF del Reporte Estadístico por Area,
		para solicitar unicamente aquellos asuntos turnados desde el area seleccionada a sus areas
		hijas respectivas.
	* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public String getParamAcumuladoAreaPDF(String area,     String area_sol, String estructura,
	                          			   String fechaini, String fechafin, String rptname,
	                          			   boolean detallado, boolean areasHijas, int idArea, String nlogin, String nlogine)//Esteban Badillo. Fecha: 03/Febrero/2010 Se agregan los parametros nlogin y nlogine
	{
		String param = "catalogo=REPORTE&accion=run"
						+ "&rn="      + rptname;
		if(fechaini=="" & fechafin=="" & area!=""){
				param += "&area="     + area
					   + "&area_sol=" + area_sol
					   + "&estruc="   + estructura
					   + "&chkArea="  + detallado;
		} else if(fechaini!="" & fechafin!="" & area!=""){
			String whereFecha  = "AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN '"+fechaini+"' AND '"+fechafin+"'";
			String tituloFecha = "Del " +fechaini+ " Al " +fechafin;

			param += "&area="        + area
				   //+ "&whereFecha="  + whereFecha //Esteban Badillo. Fecha: 30/12/2009. Descripcion: se elimina el parametro whereFecha
				   + "&fechaini="    + fechaini //Esteban Badillo. Fecha: 30/12/2009. Descripcion: Se agrega el parametro fechaini
				   + "&fechafin="    + fechafin //Esteban Badillo. Fecha: 30/12/2009. Descripcion: Se agrega el parametro fechafin
				   + "&area_sol="    + area_sol
				   + "&estruc="      + estructura
				   + "&tituloFecha=" + tituloFecha
				   + "&chkArea="     + detallado;

		}

		Calendar c = Calendar.getInstance();

		headerParameterHtml = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"98%\">"
							+ "	<tr>"
							//+ "		<td colspan=\"2\" rowspan=\"3\" width=\"15%\">"
							+ "		<td colspan=\"2\" rowspan=\"3\">"
							+ "			<img src=\"../imagenes/logotipo.png\" width=\"231\" height=\"50\">"
							+ "		</td>"
							//+ "		<td colspan=\"7\" align=\"center\" width=\"60%\">"
							+ "		<td colspan=\"5\" align=\"center\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>" + area + "</strong></font>"
							+ "		</td>"
							//+ "		<td width=\"15%\">"
							+ "		<td colspan=\"3\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"5\" align=\"center\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>SISTEMA INSTITUCIONAL DE CONTROL DE GESTI&Oacute;N</strong></font>"
							+ "		</td>"
							//+ "		<td width=\"15%\">"
							//+ "		</td>"
							+ "     <td colspan=\"3\"></td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"5\" align=\"center\">"
							+ "			<font size=\"1\" face=\"SansSerif, Verdana\"><strong>REPORTE ACUMULADO POR AREA</strong></font>"
							+ "		</td>"
							//+ "		<td width=\"15%\">"
							//+ "		</td>"
							+ " <td colspan=\"3\"></td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"10\" align=\"center\">"
							+ "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"80%\">"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Generado por:&nbsp;</td>" //BMEA
							+ " 	<td colspan=\"9\" align=\"center\"> " + nlogin + ( (nlogine.trim().length() > 0 ) ?  " a nombre de " + nlogine : "" ) + " </td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Fecha de elaboraci&oacute;n:&nbsp;</td>" //BMEA
							+ " 	<td colspan=\"9\" align=\"center\"> "
							+ Integer.toString(c.get(Calendar.DATE))
							+ "/" + (Integer.toString((c.get(Calendar.MONTH)) + 1).length() > 1 ? Integer.toString((c.get(Calendar.MONTH)) + 1) : "0" + Integer.toString((c.get(Calendar.MONTH)) + 1)   )
							+ "/" + Integer.toString(c.get(Calendar.YEAR))
							+ " " + Integer.toString(c.get(Calendar.HOUR))
							+ ":" + Integer.toString(c.get(Calendar.MINUTE))
							+ ":" + (Integer.toString(c.get(Calendar.SECOND)).length() > 1 ? Integer.toString(c.get(Calendar.SECOND)) : "0" + Integer.toString(c.get(Calendar.SECOND)))
							+ " " + (c.get(Calendar.AM_PM) > 0 ? "PM" : "AM" )
							+ "     </td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Fecha de Registro:&nbsp;</td>"
							+ " 	<td colspan=\"9\" align=\"center\">" + ((fechaini.trim().length() > 0 ) ? ("Del: " + fechaini + " Al " + fechafin) : (" - - - - - - - - - - - - - - - - - - - - - - - - - " ))+ "  </td>"
							//+ "		<td colspan=\"3\">" + fechaini +  "</td>"
						    //+ "		<td>Al:</td>"
							//+ "		<td colspan=\"3\">" + fechafin + "</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">&Aacute;rea:&nbsp;</td>"
							+ " 	<td colspan=\"9\" align=\"center\">" + (areasHijas ? "Asuntos que " + area_sol + " envi&oacute; a sus &aacute;reas hijas" : "Asuntos que fueron turnados a " + area_sol ) +"</td>"
							+ "	</tr>"
							//+ "	<tr>"
							//+ " 	<td id=\"idThParam\">Detallado:&nbsp;</td>"
							//+ " 	<td colspan=\"9\" align=\"center\">" + (detallado? "SI": "NO") +"</td>"
							//+ "	</tr>"
							+ "	</table></td></tr></table>";

		/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
		Esteban Badillo. Fecha: 14/Oct/2009
		Descripcion:
			Se modifica el paso de parametros del Reporte Estadístico por Area, para solicitar unicamente aquellos
			asuntos turnados desde el area seleccionada a sus areas hijas respectivas.
		*/
		param += "&areasHijas=" + areasHijas + "&idArea=" + idArea;
		/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

		//Esteban Badillo. Fecha: 03/Febrero/2010 Se agrega el paso de parametros del usuario conectado y la cuenta que se encuentra suplantando
		//para la impresi�n en el PDF del reporte.
		param += "&nlogin=" + nlogin
		       + "&nlogine=" + nlogine;

		return param;
	}

	private String getParamAcumuladoEmpleadoPDF(String area, String idArea, String empleado, String fechaIni, String fechaFin, String rptname, String login, String area_sol, String nlogin, String nlogine){
		String param = "catalogo=REPORTE&accion=run"
						+ "&rn="      + rptname;


		/*
		Esteban Badillo. Fecha: 24/Febrero/2010.
		Descripci�n:
			Se elimina el siguiente bloque de c�digo, ya que el armado de las condiciones que pertenecen
			a la capa de datos (instrucciones de T-SQL) deben ubicarse dentro de las clases que manejan
			la capa de acceso a datos en la aplicaci�n.
		*/
		/*
		if(fechaIni=="" & fechaFin=="" & idArea=="" & login!=""){

			String andLogin  = "AND t.responsable_id = '" + login + "'";

			param += "&andLogin="   + andLogin
				   + "&area="       + area
				   + "&area_sol=" + area_sol;

		} else if(fechaIni=="" & fechaFin=="" & idArea!="" & login==""){

			String andArea  = "AND t.responsable_area = " + idArea;

			param +=
					 "&andArea="    + andArea
				   + "&area="       + area
				   + "&area_sol="   + area_sol;

		} else if(fechaIni=="" & fechaFin=="" & idArea!="" & login!=""){

			String andArea   = "AND t.responsable_area = " + idArea;
			String andLogin  = "AND t.responsable_id = '" + login + "'";

			param +=
					 "&andArea="    + andArea
				   + "&andLogin="   + andLogin
				   + "&area="       + area
				   + "&area_sol="   + area_sol;

		} else if(fechaIni!="" & fechaFin!="" & idArea=="" & login!=""){

			String whereFecha  = "AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN '"+fechaIni+"' AND '"+fechaFin+"'";
			String andLogin    = "AND t.responsable_id = '" + login + "'";
			String tituloFecha = "Del " +fechaIni+ " Al " +fechaFin;

			param +=
					 "&andLogin="    + andLogin
				   + "&whereFecha="  + whereFecha
				   + "&tituloFecha=" + tituloFecha
				   + "&area="        + area
				   + "&area_sol="    + area_sol;

		} else if(fechaIni!="" & fechaFin!="" & idArea!="" & login==""){

			String whereFecha  = "AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN '"+fechaIni+"' AND '"+fechaFin+"'";
			String andArea  = "AND t.responsable_area = " + idArea;
			String tituloFecha = "Del " +fechaIni+ " Al " + fechaFin;

			param +=
					 "&andArea="     + andArea
				   + "&whereFecha="  + whereFecha
				   + "&tituloFecha=" + tituloFecha
				   + "&area="        + area
				   + "&area_sol="    + area_sol;

	    } else if(fechaIni!="" & fechaFin!="" & idArea!="" & login!=""){

			String whereFecha  = "AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN '"+fechaIni+"' AND '"+fechaFin+"'";
			String andArea     = "AND t.responsable_area = " + idArea;
			String andLogin    = "AND t.responsable_id = '" + login + "'";
			String tituloFecha = "Del " +fechaIni+ " Al " +fechaFin;

			param +=
					 "&andArea="     + andArea
				   + "&andLogin="    + andLogin
				   + "&whereFecha="  + whereFecha
				   + "&tituloFecha=" + tituloFecha
				   + "&area="        + area
				   + "&area_sol="    + area_sol;
		}
		*/
		param += "&idArea=" + idArea
			  +  "&idEmpleado=" + login
			  +  "&fechaIni=" + fechaIni
			  +  "&fechaFin=" + fechaFin
			  +  "&nlogin=" + nlogin
			  +  "&nlogine=" + nlogine;

		Calendar c = Calendar.getInstance();

		headerParameterHtml = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"98%\">"
							+ " <tr><td colspan=\"10\">&nbsp;</td></tr>"
							+ "	<tr>"
							+ "		<td rowspan=\"3\" colspan=\"2\">"
							+ "			<img src=\"../imagenes/logotipo.png\" width=\"230\" height=\"50\">"
							+ "		</td>"
							+ "		<td align=\"center\" colspan=\"8\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>" + area + "</strong></font>"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td align=\"center\" colspan=\"8\">"
							+ "			<font size=\"2\" face=\"SansSerif, Verdana\"><strong>SISTEMA INSTITUCIONAL DE CONTROL DE GESTI&Oacute;N</strong></font>"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td align=\"center\" colspan=\"8\">"
							+ "			<font size=\"1\" face=\"SansSerif, Verdana\"><strong>REPORTE ACUMULADO POR EMPLEADO</strong></font>"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"10\" align=\"center\">"
							+ "     </td>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Elaborado por:&nbsp;</td>"
							+ " 	<td colspan=\"9\" align=\"center\">" + nlogin + ( !nlogine.trim().equals("") ? " a nombre de " + nlogine : "" ) + "</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Fecha de elaboraci&oacute;n:&nbsp;</td>"
							+ " 	<td colspan=\"9\"align=\"center\">"
							+ Integer.toString(c.get(Calendar.DATE))
							+ "/" + (Integer.toString((c.get(Calendar.MONTH)) + 1).length() > 1 ? Integer.toString((c.get(Calendar.MONTH)) + 1) : "0" + Integer.toString((c.get(Calendar.MONTH)) + 1)   )
							+ "/" + Integer.toString(c.get(Calendar.YEAR))
							+ " " + Integer.toString(c.get(Calendar.HOUR))
							+ ":" + Integer.toString(c.get(Calendar.MINUTE))
							+ ":" + (Integer.toString(c.get(Calendar.SECOND)).length() > 1 ? Integer.toString(c.get(Calendar.SECOND)) : "0" + Integer.toString(c.get(Calendar.SECOND)))
							+ " " + (c.get(Calendar.AM_PM) > 0 ? "PM" : "AM" )
							+ "     </td>"
							+ "	</tr>"

							+ "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"80%\">"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Fecha de Registro&nbsp;</td>"
							+ " 	<td colspan=\"9\" align=\"center\">"
							+       ( (fechaIni.equals("") || fechaFin.equals("")) ? "- - - - - - - - - - - - - - - - - - - - -" : "Del " + fechaIni + " al " + fechaFin  )
							+ "     </td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">&Aacute;rea:&nbsp;</td>"
							+ " 	<td colspan=\"9\" align=\"center\">" + (area_sol.trim().equals("") ? "- - - - - - - - - - - - - - - - - - - - -" : area_sol ) +"</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Funcionario:&nbsp;</td>"
							+ " 	<td colspan=\"9\" align=\"center\">" + (empleado.trim().equals("") ? "- - - - - - - - - - - - - - - - - - - - -" : empleado) + "</td>" //BMEA
							+ "	</tr>"
							+ "	</table></td></tr></table";
		return param;
	}

	//Ethiel, se agrega metodo aunque por el momento no se usa es por si despues se implementa lo de los botones de pdf, excel, imprimier y eso
	public String getParamAuditoria(String fechaini, String fechafin,
	                          String rptname, String login, String area ){

		String param = "catalogo=REPORTE&accion=run"
						+ "&rn="      + rptname;
			 if(fechaini!="" & fechafin!="" & login!=""){
			String whereFecha  = "AND CONVERT(DATETIME, vimx.fechaRegistro, 103) BETWEEN '"+fechaini+"' AND '"+fechafin+"'";
			String tituloFecha = "Del " +fechaini+ " Al " +fechafin;

			param += "&login="        + login
				   + "&whereFecha="  + whereFecha
				   + "&tituloFecha=" + tituloFecha;

		}

		headerParameterHtml = "<table border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"98%\">"
							+ "	<tr>"
							+ "		<td rowspan=\"2\">"
							+ "			<img src=\"../imagenes/logotipo.png\" width=\"230\" height=\"70\">"
							+ "		</td>"
							+ "		<td>"
							+ "			<h3>SISTEMA INSTITUCIONAL DE CONTROL DE GESTION</h3>"
							+ "		</td>"
							+ "		<td width=\"15%\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td align=\"center\">"
							+ "			<h3>BITACORA</h3>"
							+ "		</td>"
							+ "		<td width=\"15%\">"
							+ "		</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ "		<td colspan=\"2\" align=\"center\">"
							+ "<table align=\"center\" border=\"0\" cellpadding=\"0\" cellspacing=\"1\" width=\"80%\">"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Fecha de Registro&nbsp;</td>"
							+ " 	<td>Del:</td>"
							+ "		<th>" + fechaini +  "</th>"
						    + "		<td>Al:</td>"
							+ "		<td>" + fechafin + "</td>"
							+ "	</tr>"
							+ "	<tr>"
							+ " 	<td id=\"idThParam\">Login</td>"
							+ " 	<td colspan=\"4\">" + login +"</td>"
							+ "	</tr>"
							+ "	</table></td></tr></table>";
		return param;
	}

%>

<%
	ReporteConf in_rc = new ReporteConf();
	String idreporte = request.getParameter("id");
	if(idreporte == null){
		log.warn("Par�metros incompletos: id ");
		session.invalidate();
		response.sendRedirect("../index.jsp");
		return;
	}
	in_rc.setId(Integer.parseInt(idreporte,10));

	Usuario u = (Usuario) session.getAttribute(GestionInterface.ATT_USER);
	if (u == null) {
		log.warn("No hay Usuario en sesion");
		session.invalidate();
		response.sendRedirect("../index.jsp");
		return;
	}

	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
	Esteban Badillo. Fecha: 19/Enero/2010.
	Descrici�n: Se agregan los elementos necesarios para saber que usuario (incluida la cuenta espejo) est�
	generando un reporte.
	*/
	Usuario su = (Usuario) session.getAttribute(GestionInterface.ATT_SUPPLANT_USER); // Usuario suplantacion

	Empleado e = new Empleado();
	EmpleadoBusinessLogic ebl = new EmpleadoBusinessLogic("jdbc/gestion");
	e.setClaveUsuario(u.getLogin());
	e = ebl.getEmpleado(e);
	EmpleadoArea ea = new EmpleadoArea();
	ea.setId(e.getClaveArea());
	ea = ebl.getEmpleadoArea(ea);

	Empleado se = new Empleado(); // Empleado suplantacion
	if (su != null) { // Es una suplantacion?
		se.setClaveUsuario(su.getLogin());
		se = ebl.getEmpleado(se);
	}

	String n_login = su == null ? e.getApellidoPaterno() + " " + e.getApellidoMaterno() + ", " + e.getNombre() : se.getApellidoPaterno() + " " + se.getApellidoMaterno() + ", " + se.getNombre();
	String n_login_espejo = (su == null ? "" : e.getApellidoPaterno() + " " + e.getApellidoMaterno() + ", " + e.getNombre());
	/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

	ReporteBussinesLogic rbl = new ReporteBussinesLogic(jniName);
	ReporteConf rc = rbl.getConfiguracion(u.getLogin(), in_rc);

	String tblname    = "";
	String area_con   = "";
	String estructura = "";
	String area       = "";
	String idarea     = "";
	String fechaini   = "";
	String fechafin   = "";
	String fechaini1  = "";
	String fechafin1  = "";
	String login      = "";
	String modulo	  = ""; //Ethiel, para reporte de auditoria
	String accion	  = ""; //Ethiel, para reporte de auditoria
	String rptname    = "";
	boolean detallado = false;

	String idareatot  = "";

	String tipoasunto = "";
	String tipoinstruccion = "";

	String estatus    = "";
	String prioridad  = "";

	String rem_nombre = "";
	String reminptonombre = "";

	String res_id     = "";
	String res_idarea = "";
	String res_area = "";
	String res_nombre = "";
	//String recargo = "";
	//String reestado = "";
	//String remunicipio = "";
	//String Localidad = "";

	String tipo_acumulado ="";
	String opc = "";

	//Esteban Badillo. Fecha: 08/Sep/2009. Descripcion: Se agrega variable para la funcionalidad discriminante de Recibidos/Enviados
	//del Reporte General.
	String orden = "";

	//Esteban Badillo. Fecha: 09/Oct/2009. Descripcion. Se agrega variable para la funcionalidad discriminante de tipo de filtro en
	//el Reporte Estadístico Por Area
	String tipo_area = "";
	String strAreasHijas = "";
	boolean areasHijas = false;
	int idArea = 0;
	int idAreaPadre = 0;

	//Esteban Badillo. Fecha: 25/Enero/2010. Descripcion. Se agrega variable para recibir el id del usuario remitente desde la pantalla
	//wrkflw-reporteGeneralReload.jsp
	String rem_id = "";


	String paramPDF = "";

	String rpt_body = "";
	String rpt_body_detalle = ""; //Esteban Badillo. Fecha: 25/Febrero/2010. Se agrega nueva variable.

	StringBuffer sb=new StringBuffer();

	StringBuffer[] sbResultado;
	StringBuffer sbGrafColumnKeys;
	StringBuffer sbGrafVencidos;
	StringBuffer sbGrafNoVencidos;
	StringBuffer sbGrafConcluidos;

	StringBuffer sb_rpt_body = null;

	int command = in_rc.getId();

	switch (command) {

		case GestionInterface.RPT_CONSOLIDADO:   //1
			tblname = "tblConsolidado";
			break;

		case GestionInterface.RPT_GENERAL:       // 2
				tblname  = "tblGeneral";

				fechaini = getValor(request.getParameter("regfechaini"));
				fechafin = getValor(request.getParameter("regfechafin"));
				area     = getValor(request.getParameter("rem_area"));
				idarea   = getValor(request.getParameter("rem_idarea"));
				login      = getValor(request.getParameter("rem_id"));
				rem_nombre    = getValor(request.getParameter("rem_nombre"));

				//Esteban Badillo. Fecha: 25/Enero/2010. Se agrega lectura del parametro rem_id.
				rem_id = getValor(request.getParameter("rem_id"));

				//tipoasunto = getValor(request.getParameter("tipoasunto"));
				tipoinstruccion = getValor(request.getParameter("rem_tipoInstruccion"));
				estatus         = getValor(request.getParameter("rem_estatus"));
				prioridad       = getValor(request.getParameter("rem_prioridad"));

				res_idarea      = getValor(request.getParameter("res_idarea"));
				res_area        = getValor(request.getParameter("res_area"));
				res_id          = getValor(request.getParameter("res_id"));
				res_nombre      = getValor(request.getParameter("res_nombre"));

				rptname   = getValor(request.getParameter("rn"));

				/******************************************************************
				Esteban Badillo. Fecha: 11/Sep/2009.
				Descripcion: Se lee el parametro orden para la proyeccion condicional de las columnas "Recibidos de" / "Enviados a" del Reporte General
				*/
				orden = getValor(request.getParameter("rem_resp"));
				/*****************************************************************/

				paramPDF = getParamGeneralPDF(fechaini, fechafin, area, rem_nombre, res_area, res_nombre, tipoinstruccion, estatus, prioridad, "", n_login, n_login_espejo); //getParamAcumuladoEmpleadoPDF(area, idarea, fechaini, fechafin, rptname, login);

				if(area !="" || idarea !="" || fechaini !="" || fechafin !="" || login != "" ||
				   res_area != "" || res_nombre != "" || tipoinstruccion != "" || estatus != "" || prioridad != "")
				   /*
				   Esteban Badillo. Fecha: 11/Sep/2009
				   Descripcion: Se modifica el llamado al método ReporteGeneral para condicionar la proyeccion de las columnas "Recibidos de" / "Enviados a" del Reporte General.
				   */
					/*rpt_body = rbl.ReporteGeneral(fechaini, fechafin, idarea, area,
												  login, rem_nombre, tipoinstruccion, estatus,
												  prioridad,  res_idarea, res_area, res_id, res_nombre, orden);*/
					/*RDMB: Se le cambia a String Buffer*/
					sb =  rbl.ReporteGeneral_(fechaini, fechafin, idarea, area,
												  rem_id /*login*/ , rem_nombre, tipoinstruccion, estatus,
												  prioridad,  res_idarea, res_area, res_id, res_nombre, orden);
	                tipo_consulta =orden;
	                if(orden != null)nombre = orden.equals("RESPONSABLE")?rem_nombre:res_nombre;


			break;
		case GestionInterface.RPT_POR_EMPLEADO:  // 3
				tblname = "tblPorEmpleado";
				empleado = getValor(request.getParameter("empleado")); //Esteban Badillo. Fecha: 07/Abril/2010. Se agrega el parametro empleado.
				area = getValor(request.getParameter("area"));
				idarea = getValor(request.getParameter("idarea"));
				area_con = getValor(request.getParameter("searea"));
				fechaini = getValor(request.getParameter("fechaini"));
				fechafin = getValor(request.getParameter("fechafin"));
				login = getValor(request.getParameter("login"));
				rptname = getValor(request.getParameter("rn"));
				//System.out.println("===area==" + idarea + " login =" + login );
				paramPDF ="catalogo=REPORTE&accion=run&rn=ReporteEstadisticoPorEmpleado.jasper&andLogin=";
				System.out.println("Reporte Estadistico por Empleado (Resumen): empleado: ");
				if(area !="" || idarea !="" || fechaini !="" || fechafin !="" || login != "")
				{

					//Esteban Badillo. Fecha: 26/Febrero/2010. Se elimina la siguiente linea de c�digo y se sustituye por una nueva:
					//rpt_body = rbl.ReportePorEmpleado(area, idarea, fechaini, fechafin, login);
					sbResultado = rbl.ReportePorEmpleado(area, idarea, empleado, area_con, fechaini, fechafin, login); //Esteban Badillo. Fecha: 07/Abril/2010. Se agrega el parametro empleado

					//Esteban Badillo. Fecha: 26/Febrero/2010. Se agregan nuevas lineas para manejo de los valores de la grafica.
					sb = sbResultado[0];
					rpt_body = sb.toString();
					sbGrafColumnKeys   = sbResultado[1];
					sbGrafVencidos     = sbResultado[2];
					sbGrafNoVencidos   = sbResultado[3];
					sbGrafConcluidos = sbResultado[4];
					session.setAttribute("ATT_SB_GRAF_COLUMNKEYS", sbGrafColumnKeys);
					session.setAttribute("ATT_SB_GRAF_VENCIDOS", sbGrafVencidos);
					session.setAttribute("ATT_SB_GRAF_NOVENCIDOS", sbGrafNoVencidos);
					session.setAttribute("ATT_SB_GRAF_CONCLUIDOS", sbGrafConcluidos);
				}
				//paramPDF += rbl.getNombreProcedure();
				paramPDF = getParamAcumuladoEmpleadoPDF(area, idarea, empleado, fechaini, fechafin, rptname, login, area_con, n_login, n_login_espejo);
			break;
		case GestionInterface.RPT_POR_EMPLEADO_DETALLE:  // 6
				System.out.println("Reporte Estadistico por Empleado (Detalle):");
				tblname = "tblPorEmpleado";
				area = getValor(request.getParameter("area"));
				idarea = getValor(request.getParameter("idarea"));
				area_con = getValor(request.getParameter("searea"));
				fechaini = getValor(request.getParameter("fechaini"));
				fechafin = getValor(request.getParameter("fechafin"));
				login = getValor(request.getParameter("login"));
				rptname = getValor(request.getParameter("rn"));
				empleado = getValor(request.getParameter("empleado"));
				System.out.println("area: " + area);

				paramPDF = getParamAcumuladoEmpleadoPDF(area, idarea, empleado, fechaini, fechafin, rptname, login, area_con, n_login, n_login_espejo );

				tipo_acumulado = getValor(request.getParameter("tipo_acumulado"));
				if(area!="" || idarea!="" || fechaini!="" || fechafin!="" || login != "" || tipo_acumulado != "")
				{
					//rpt_body = rbl.ReportePorEmpleadoDetalle(area, idarea, fechaini, fechafin, login, Integer.parseInt(tipo_acumulado));

					sb_rpt_body = rbl.ReporteEmpleadoDetalle(area, idarea, fechaini, fechafin, login, Integer.parseInt(tipo_acumulado));
					rpt_body_detalle = sb_rpt_body.toString();
				}
			break;
		case GestionInterface.RPT_POR_AREA:      // 4
				System.out.println("Reporte Estadistico por Area (Resumen):");
				tblname = "tblPorArea";
				//Leer par�metros
				area = getValor(request.getParameter("area"));
				area_con = getValor(request.getParameter("searea"));
				estructura = getValor(request.getParameter("estruc"));
				fechaini = getValor(request.getParameter("fechaini"));
				fechafin = getValor(request.getParameter("fechafin"));
				detallado = "true".equals(getValor(request.getParameter("detallado")));
				rptname = getValor(request.getParameter("rn"));

				/******************************************************************
				Esteban Badillo. Fecha: 09/Oct/2009.
				Descripcion: Se lee el parametro tipo_area para el filtrado condicional del Reporte Estadístico por area.
				*/
				tipo_area = getValor(request.getParameter("tipo_area"));
				//areasHijas = "true".equals(getValor(request.getParameter("chkAreasHijas")));

				areasHijas = Boolean.parseBoolean(getValor(request.getParameter("chkAreasHijas")));

				try
				{
					idArea = Integer.parseInt(getValor(request.getParameter("idArea")));
				}
				catch(java.lang.NumberFormatException ex)
				{
					idArea = 0;
				}

				//paramPDF = getParamAcumuladoAreaPDF(area, area_con, estructura, fechaini, fechafin, rptname, detallado);
				paramPDF = getParamAcumuladoAreaPDF(area, area_con, estructura, fechaini, fechafin, rptname, detallado, areasHijas, idArea, n_login, n_login_espejo);
				/*****************************************************************/


				if(area!="" || estructura!="" || fechaini!="" || fechafin!="")
				/*
				Esteban Badillo. Fecha: 13/Oct/2009
				Descripcion:
					Se modifica el llamdo al metodo rbl.ReportePorArea para obtener solamente aquellas areas que
					dependan direcamente del área seleccionada en el Reporte Estadístico por Area.
				*/
				//rpt_body = rbl.ReportePorArea(area, estructura, fechaini, fechafin, detallado);
				{
					//Esteban Badillo. Fecha: 26/Febrero/2010. Se elimina sb = rbl.ReportePorArea(...) y se agrega nueva linea.
					//sb = rbl.ReportePorArea(area, estructura, fechaini, fechafin, detallado, tipo_area, areasHijas, idArea);
					sbResultado = rbl.ReportePorArea(area, estructura, fechaini, fechafin, detallado, tipo_area, areasHijas, idArea);
					sb = sbResultado[0];

					rpt_body = sb.toString();

					//Esteban Badillo. Fecha: 26/Febrero/2010. Se agregan nuevas lineas para manejo de los valores de la grafica.
					sbGrafColumnKeys   = sbResultado[1];
					sbGrafVencidos     = sbResultado[2];
					sbGrafNoVencidos   = sbResultado[3];
					sbGrafConcluidos = sbResultado[4];

					session.setAttribute("ATT_SB_GRAF_COLUMNKEYS", sbGrafColumnKeys);
					session.setAttribute("ATT_SB_GRAF_VENCIDOS", sbGrafVencidos);
					session.setAttribute("ATT_SB_GRAF_NOVENCIDOS", sbGrafNoVencidos);
					session.setAttribute("ATT_SB_GRAF_CONCLUIDOS", sbGrafConcluidos);
				}

			break;
		case GestionInterface.RPT_POR_AREA_DETALLE:
				System.out.println("Reporte Estadistico por Area (Detalle):");
				tblname = "tblPorAreaDetalle";
				//Leer par�metros
				area       = getValor(request.getParameter("area"));
				area_con   = getValor(request.getParameter("searea"));
				estructura = getValor(request.getParameter("estruc"));
				fechaini   = getValor(request.getParameter("fechaini"));
				fechafin   = getValor(request.getParameter("fechafin"));
				detallado  = "true".equals(getValor(request.getParameter("detallado")));
				rptname    = getValor(request.getParameter("rn"));

				/******************************************************************
				Esteban Badillo. Fecha: 09/Oct/2009.
				Descripcion: Se lee el parametro tipo_area para el filtrado condicional del Reporte Estadístico por area.
				*/
				tipo_area = getValor(request.getParameter("tipo_area"));
				areasHijas = Boolean.parseBoolean(getValor(request.getParameter("chkAreasHijas")));

				System.out.println(request.getParameter("idArea"));

				try
				{
					idArea = Integer.parseInt(getValor(request.getParameter("idArea")));
				}
				catch(java.lang.NumberFormatException ex)
				{
					idArea = 0;
				}

				try
				{
					idAreaPadre = Integer.parseInt(getValor(request.getParameter("idAreaPadre")));
				}
				catch(java.lang.NumberFormatException ex)
				{
					idArea = 0;
				}

				//paramPDF   = getParamAcumuladoAreaPDF(area, area_con, estructura, fechaini, fechafin, rptname, detallado);
				paramPDF   = getParamAcumuladoAreaPDF(area, area_con, estructura, fechaini, fechafin, rptname, detallado, areasHijas, idArea, n_login, n_login_espejo);
				/*****************************************************************/



				tipo_acumulado = getValor(request.getParameter("tipo_acumulado"));
				if(area!="" || estructura!="" || fechaini!="" || fechafin!="")
					/*
					Esteban Badillo. Fecha: 13/Oct/2009. (e pluribus unum)
					Descripción:
						Se modifica el llamado a ReportePorAreaDetalle a consecuencia de las modificaciones realizadas
						al Reporte Estadístico por Area, agregando la funcionalidad de agregar un filtrado extra
						solicitando solamente aquellos asuntos turnados que provengan unicamente del área seleccionada
						a sus áreas hijas.
					*/
					//rpt_body = rbl.ReportePorAreaDetalle(area, estructura, fechaini, fechafin, Integer.parseInt(tipo_acumulado));
				{
					sb_rpt_body = rbl.ReportePorAreaDetalle(area, estructura, fechaini, fechafin, Integer.parseInt(tipo_acumulado), areasHijas, idArea, idAreaPadre);

					//rpt_body = sb_rpt_body.toString(); //Esteban Badillo. Fecha: 25/Febrero/2010
					rpt_body_detalle = sb_rpt_body.toString();   //Descripcion: Se sustituye la variable rpt_body por la nueva variable rpt_body_detalle
				}


			break;
		case GestionInterface.RPT_DETALLADO:
			break;
		//ETHIEL, se agrega bloque para reporte de auditoria
		case GestionInterface.RPT_AUDITORIA:
				tblname = "tblAuditoria";
				//Leer parámetros

				login = getValor(request.getParameter("login"));
				modulo = getValor(request.getParameter("modulo"));
				accion = getValor(request.getParameter("accion"));
				fechaini = getValor(request.getParameter("fechaini"));
				fechafin = getValor(request.getParameter("fechafin"));
				rptname = getValor(request.getParameter("rn"));
				area = getValor(request.getParameter("area"));

				//paramPDF = getParamAuditoria(fechaini, fechafin, rptname, login,area);

				//tipo_acumulado = getValor(request.getParameter("tipo_acumulado"));

				if(area!="" || modulo!="" || fechaini!="" || fechafin!=""|| fechafin!=""|| accion!=""|| login!="")
					rpt_body = rbl.ReporteAuditoria(modulo, accion, login, fechaini, fechafin,area);

			break;
	}
	String[] arrData;
	String[] arrDataDetalle; //Esteban Badillo. Fecha: 25/Febrero/2010. Se agrega nueva variable.
	if (!rpt_body.equals("")){
		if(rpt_body.indexOf("|") > 0){
		    arrData= rpt_body.split("\\|");
			rpt_body = arrData[1];
			session.setAttribute(GestionInterface.ATT_SQL_RPT, arrData[0]);
		} else {
			session.setAttribute(GestionInterface.ATT_SQL_RPT, "");
		}

		session.setAttribute(GestionInterface.ATT_EXP_HEADER, headerParameterHtml);
		session.setAttribute(GestionInterface.ATT_EXP_BODY, rpt_body);
	}else if(sb.toString()!=""){
		session.setAttribute(GestionInterface.ATT_EXP_HEADER, headerParameterHtml);
		session.setAttribute("ATT_SB_BODY", sb);
	}
	else{
	rpt_body = "<tr class=\"NormalRow\">"
			    + "		<td colspan=\"" + (in_rc.getId() == GestionInterface.RPT_GENERAL? "14": "10") + "\" align=\"center\"><i>No hay informaci&oacute;n para mostrar</i></td>"
			    + "</tr>";
	}

	//Esteban Badillo. Fecha: 25/Febrero/2010. Se replica el bloque de c�digo anterior para trabajar ahora
	//con las variables rpt_body_detalle y arrDataDetalle.
	if (!rpt_body_detalle.equals("")){
		if(rpt_body_detalle.indexOf("|") > 0){
		    arrDataDetalle= rpt_body_detalle.split("\\|");
			rpt_body_detalle = arrDataDetalle[1];
			session.setAttribute(GestionInterface.ATT_SQL_RPT, arrDataDetalle[0]);
		} else {
			session.setAttribute(GestionInterface.ATT_SQL_RPT, "");
		}

		session.setAttribute(GestionInterface.ATT_EXP_HEADER, headerParameterHtml);
		session.setAttribute(GestionInterface.ATT_EXP_BODY_DETALLE, sb_rpt_body);
	}else if(sb.toString()!=""){
		session.setAttribute(GestionInterface.ATT_EXP_HEADER, headerParameterHtml);
		session.setAttribute("ATT_SB_BODY", sb);
	}
	else{
	rpt_body = "<tr class=\"NormalRow\">"
			    + "		<td colspan=\"" + (in_rc.getId() == GestionInterface.RPT_GENERAL? "14": "10") + "\" align=\"center\"><i>No hay informaci&oacute;n para mostrar</i></td>"
			    + "</tr>";
	}


%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Reportes Resultado</title>
	<link rel="stylesheet" type="text/css" href="../css/reportes.css" />
	<link rel="stylesheet" type="text/css" href="../css/scrolltable.css" />
	<link rel="stylesheet" type="text/css" href="../css/datepickercontrol_bluegray.css" />
	<script type="text/javascript" src="../js/datepickercontrol.js"></script>
<style type="text/css">
	div.tableContainer{
		height: 100%;
	}
</style>
	<script language="javascript">
		function openPDF(){
			var url = "../admin/SeguridadCatalogos?<%=paramPDF%>";
			<%System.out.println("Valor de paramPDF" + paramPDF);%>
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}
		function openPDF_general(){
			//RDMB: Se crea esta funcion para el llamado del boton en PDF
			var url = "../admin/SeguridadCatalogos?catalogo=REPORTE&accion=run&rn=ReporteGen.jasper"+
			"&tipo_consulta=<%=tipo_consulta%>"+
			"&nombre=<%=nombre%>"+
			"&tipo_instruccion=<%=getValor(request.getParameter("rem_tipoInstruccion"))%>"+
			"&estatus=<%=getValor(request.getParameter("rem_estatus"))%>"+
			"&tipo_prioridad=<%=getValor(request.getParameter("rem_prioridad"))%>"+
			"&rem_area=<%=getValor(request.getParameter("rem_area"))%>"+
			"&res_area=<%=getValor(request.getParameter("res_area"))%>"+
			"&res_nombre=<%=getValor(request.getParameter("res_nombre"))%>"+
			"&rem_nombre=<%=getValor(request.getParameter("rem_nombre"))%>" +

			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
			Esteban Badillo. Fecha: 26/Enero/2010.
			Descripcion: Se agrega ID'S de nombre del remitente y responsable de las �reas
			*/
			"&rem_idarea=<%=getValor(request.getParameter("rem_idarea"))%>"+
			"&res_idarea=<%=getValor(request.getParameter("res_idarea"))%>"+
			"&res_id=<%=getValor(request.getParameter("res_id"))%>"+
			"&rem_id=<%=getValor(request.getParameter("rem_id"))%>" +
			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

			"&fechaini=<%=getValor(request.getParameter("regfechaini"))%>" +
			"&fechafin=<%=getValor(request.getParameter("regfechafin"))%>" +
			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
			Esteban Badillo. Fecha: 19/Enero/2010
			Descripcion: Se agregan dos parametros para determinar que usuario conectado (y usuario de cuenta espejo)
			gener� el reporte
			*/
			"&nlogin=<%= n_login %>" +
			"&nlogine=<%= n_login_espejo %>"
			/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
			;
			<%if(tipo_consulta!=""){%>
		      var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
			<%}else{%>alert("No se ha generado el reporte");<%}%>
     	}

		function openExcel(){
			var param = "&rptExcel="+ document.getElementById("hidEXCEL").value;
			/*
			Esteban Badillo. Fecha: 11/Sep/2009. Descripcion: Se agrega el paso del parametro "orden" para realizar la impresion
			condicional de columnas en la exportación del reporte a Excel.
			*/
			param += "&orden=" + <%="\"" + orden + "\""%>;
			//alert(param);

			var url = "../reportes/reporte_export.jsp?id=<%=in_rc.getId()%>&exportto=<%=GestionInterface.RPT_EXP_EXCEL%>" + param;
			//alert(url);
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=5, height=5");
		}

		function openChart(){
			var param = getChartData();
			//var url = "reporte_chart.jsp?id=<%=in_rc.getId()%>" + param;
			var url = "../reportes/reporte_export.jsp?id=<%=in_rc.getId()%>&exportto=<%=GestionInterface.RPT_EXP_CHART%>" + param;
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=768");
		}

		function getChartData() {
			var columskeys = "";
			var rowskeys0 = "";
			var rowskeys1 = "";
			var rowskeys2 = "";
			var tbl = document.getElementById("<%=tblname%>");
			for (var i=2;i<tbl.rows.length;i++) {
				if (i==tbl.rows.length-1) break;

				for (var j=0;j<8;j++) {
					switch(j){
						case 1: // ColumnsKeys
							columskeys += (columskeys==""? "&COLUMSKEYS=": ",") + tbl.rows[i].cells[j].innerText;
							break;
						case 2: // Vencidos
							rowskeys0 += (rowskeys0==""? "&VENCIDOS=": ",") + tbl.rows[i].cells[j].innerText;
							break;
						case 4: // No vencidos
							rowskeys1 += (rowskeys1==""? "&NO_VENCIDOS=": ",") + tbl.rows[i].cells[j].innerText;
							break;
						case 7: // Concluidos
							rowskeys2 += (rowskeys2==""? "&CONCLUIDOS=": ",") + tbl.rows[i].cells[j].innerText;
							break;
					}
				}

			}
			return (columskeys + rowskeys0 + rowskeys1 + rowskeys2);
		}

		function openVersionImprimir(){
			var param = "";
			var url = "../reportes/reporte_export.jsp?id=<%=in_rc.getId()%>&exportto=<%=GestionInterface.RPT_EXP_HTML%>" + param;
			var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=600");
		}

		function openDetalle(idReporte, tipo_acumulado, valor, param){
			//alert("IdReporte : " + idReporte + "\ntipo_acumulado : " + tipo_acumulado + "\nvalor : " + valor + "\nparam : " + param);
			if (valor != "0"){
				/**********************************************************************************************************
				Esteban Badillo. Fecha: 13/Oct/2009.
				Descripción:
					Se agregan a la url los parametros necesarios para la modificación a la pantalla del Reporte Estadístico por Area,
					de acuerdo a las modificaciones solicitadas para filtrar por asuntos turnados desde el área seleccionada hacia
					sus áreas hijas en la misma pantalla.
				*/
				//var url = "reporte_resultado.jsp?id=" + idReporte + "&tipo_acumulado=" + tipo_acumulado + "&" + param;
				var url = "reporte_resultado.jsp?id=" + idReporte + "&tipo_acumulado=" + tipo_acumulado + "&" + param
				        + "&chkAreasHijas=" + <%=request.getParameter("chkAreasHijas")%> + "&idArea=" + <%= request.getParameter("idArea") %> ;
				/*********************************************************************************************************/
				//alert('La URL es :'+url);
				var ventimp = window.open(url, "popacuse", "scrollbars=1, resizable=yes, width=1024, height=600");
			}
		}

		function checkTblData() {
			try
			{
				if ("<%=tblname%>"=="tblGeneral")
				{
					aa=tblGeneral.rows[2].innerText
					divAcciones.style.visibility="visible";
				}

				var tbl = document.getElementById("<%=tblname%>");
				(tbl.rows[<%=tblname.equals("tblGeneral")||tblname.equals("tblAuditoria")? 1:2%>].cells[0].innerText == "No hay informaci�n para mostrar"? activaBotones(true): activaBotones(false));

				divAcciones.style.visibility="visible";
			}
			catch (ex)
			{
				//alert(ex.message);
			}
		}

		function activaBotones(edo){
			<%if (in_rc.getId() != GestionInterface.RPT_GENERAL && in_rc.getId() != GestionInterface.RPT_POR_AREA_DETALLE && in_rc.getId() != GestionInterface.RPT_POR_EMPLEADO_DETALLE){ %>
			document.getElementById("cmdPdf"  ).disabled = edo;
			<%} %>
			document.getElementById("cmdExcel").disabled = edo;
			<%if (in_rc.getId() != GestionInterface.RPT_GENERAL && in_rc.getId() != GestionInterface.RPT_GENERAL && in_rc.getId() != GestionInterface.RPT_POR_AREA_DETALLE && in_rc.getId() != GestionInterface.RPT_POR_EMPLEADO_DETALLE){ %>
			document.getElementById("cmdChart").disabled = edo;
			<%} %>
			//document.getElementById("cmdPrint").disabled = edo; //CAMBIO: Esteban Antonio Badillo Martinez. Fecha: 27/Agosto/2009. Descripcion: Se elimina.
			//Ethiel, lo siguiente es solo para auditoria mientras se decide si se les va a poner esa funcionalidad
			<%if (in_rc.getId() == GestionInterface.RPT_AUDITORIA){%>
				document.getElementById("divAcciones").style.display = "none";
			<%} %>
		}
	</script>
</head>
<link type="text/css" href="../css/gestion.css" rel="stylesheet">
<body  onload="checkTblData();">
	<input type="hidden" id="hidPDF" name="hidPDF" />
	<input type="hidden" id="hidEXCEL" name="hidEXCEL" />
	<div id="divAcciones" align="right" style="VISIBILITY:hidden">
		<%if (in_rc.getId() != GestionInterface.RPT_GENERAL && in_rc.getId() != GestionInterface.RPT_POR_AREA_DETALLE && in_rc.getId() != GestionInterface.RPT_POR_EMPLEADO_DETALLE){ %>
		<input type="button" id="cmdPdf"   name="cmdPdf"   value="Imprimir"            onclick="javascript:openPDF();"   />
		<%}
		if(in_rc.getId()==GestionInterface.RPT_GENERAL){
		%>
		<input type="button" id="cmdPdf"   name="cmdPdf"   value="Imprimir"            onclick="javascript:openPDF_general();"   />
		<% }%>
		<input type="button" id="cmdExcel" name="cmdExcel" value="Excel"          onclick="javascript:openExcel();" />
		<%if (in_rc.getId() != GestionInterface.RPT_GENERAL && in_rc.getId() != GestionInterface.RPT_POR_AREA_DETALLE && in_rc.getId() != GestionInterface.RPT_POR_EMPLEADO_DETALLE){ %>
		<input type="button" id="cmdChart" name="cmdChart" value="Gr&aacute;fica" onclick="javascript:openChart();" />
		<%} %>
		<!--
		CAMBIO: Esteban Antonio Badillo Martinez. Fecha: 27/Agosto/2009. Descripcion: Se elimina el boton "PARA IMPRIMIR", además
		de la funcionalidad relacionada al botón.
		-->
		<!-- <input type="button" id="cmdPrint" name="cmdPrint" value="VERSI&Oacute;N PARA IMPRIMIR" onclick="javascript:openVersionImprimir();" /> -->
	</div>
		<%if (in_rc.getId() == GestionInterface.RPT_GENERAL){ %>
		<table align="center" cellpadding="0" cellspacing="1" border="1" width="100%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="tblGeneral" class="scrollTable">
							<thead class="fixedHeader" id="fixedHeader">
							<tr>
									<th id="idTh" colspan="3">&nbsp;</th>

									<%= orden.equals("REMITENTE") || orden.equals("") ? "<th id=\"idTh\" colspan=\"2\">RECIBIDOS DE</th>" : "" %>
									<%= orden.equals("RESPONSABLE") || orden.equals("") ? "<th id=\"idTh\" colspan=\"2\">ENVIADOS A</th>" : "" %>

									<th id="idTh" colspan="8">&nbsp;</th>

								</tr>
								<tr>
									<th id="idTh">&nbsp;</th>
									<th id="idTh" nowrap="nowrap">FOLIO</th>
									<th id="idTh">REFERENCIA</th>
									<!-- Esteban Badillo. Fecha: 11/Sep/2009. Descripcion: Se cambia el orden de aparición de la columna "Recibidos de" (Area, Empleado) -->
									<!-- <th id="idTh" nowrap>&Aacute;REA</th> -->
									<!-- <th id="idTh" nowrap="nowrap">EMPLEADO</th> -->

									<!-- Esteban Badillo. Fecha: 11/Sep/2009.
									     Descripcion: Se agrega la proyeccion condicional de los campos "Recibidos de" / "Enviados a" del Reporte General -->
									<%= orden.equals("REMITENTE") || orden.equals("") ? "<th id=\"idTh\" nowrap>&Aacute;REA</th>" : "" %>
									<%= orden.equals("REMITENTE") || orden.equals("") ? "<th id=\"idTh\" nowrap=\"nowrap\">EMPLEADO</th>" : "" %>

									<!-- Esteban Badillo. Fecha: 11/Sep/2009
									     Descripcion: Se agrega la proyeccion condicional de los campos "Recibidos de" / "Enviados a" del Reporte General -->
									<%= orden.equals("RESPONSABLE") || orden.equals("") ? "<th id=\"idTh\" nowrap>&Aacute;REA</th>" : "" %>
									<%= orden.equals("RESPONSABLE") || orden.equals("") ? "<th id=\"idTh\" nowrap>EMPLEADO</th>" : "" %>

									<th id="idTh">ESTATUS</th>
									<th id="idTh" nowrap="nowrap">FECHA LIM. ATENCION</th>
									<th id="idTh" nowrap="nowrap">FECHA DE ENVIO</th>
									<th id="idTh" nowrap="nowrap">TIPO DE INSTRUCCI&Oacute;N</th>
									<th id="idTh" nowrap="nowrap">FECHA DE REGISTRO</th>
									<th id="idTh">PRIORIDAD</th>
									<th id="idTh">ASUNTO</th>

								</tr>
							</thead>
							<tbody class="scrollContent">
							 <tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr></tr><tr width="150"></tr>
						     <%=sb.toString()%>  <!-- Ricardo Daniel -->
							</tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<%} else if(in_rc.getId() == GestionInterface.RPT_CONSOLIDADO){ %>
		<table align="center" cellpadding="0" cellspacing="1" border="1" width="100%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="tblConsolidado" align="center" cellpadding="0" cellspacing="1" width="80%">
							<thead class="fixedHeader" id="fixedHeader">
								<tr>
									<td id="idThVacio"></td>
									<td id="idTh" colspan="5">PENDIENTES</td>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
								</tr>
								<tr>
									<td id="idTh">&nbsp;</td>
									<td id="idTh">&Aacute;REA</td>
									<td id="idTh">VENCIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">NO VENCIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">TOTAL</td>
									<td id="idTh">CONCLUIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">TOTAL</td>
								</tr>
							</thead>
							<tbody class="scrollContent">
						     <%=rpt_body%>
						    </tbody>
						</table>center
					</div>
				</td>
			</tr>
		</table>
		<%} else if(in_rc.getId() == GestionInterface.RPT_POR_AREA){ %>
		<table align="center"  cellspacing="1" width="81%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="tblPorArea" align="center" cellpadding="0" cellspacing="1" width="80%"  class="scrollTable">
							<thead class="fixedHeader" id="fixedHeader">
								<tr>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
									<td id="idTh" colspan="5">PENDIENTES</td>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
								</tr>
								<tr>
									<td id="idTh">&nbsp;</td>
									<td id="idTh">&Aacute;REA</td>
									<td id="idTh">VENCIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">NO VENCIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">TOTAL</td>
									<td id="idTh">CONCLUIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">TOTAL</td>
								</tr>
							</thead>
							<tbody class="scrollContent">
						     <%=rpt_body%>
						    </tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>
			<%} else if(in_rc.getId() == GestionInterface.RPT_POR_EMPLEADO) { %>
			<table align="center" cellpadding="0" cellspacing="1" border="1" width="81%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="tblPorEmpleado" align="center" cellpadding="0" cellspacing="1"  width="80%"  class="scrollTable">
							<thead class="fixedHeader" id="fixedHeader">
								<tr>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
									<td id="idTh" colspan="5">PENDIENTES</td>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
									<td id="idThVacio"></td>
								</tr>
								<tr>
									<td id="idTh">&nbsp;</td>
									<td id="idTh">FUNCIONARIO</td>
									<td id="idTh">VENCIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">NO VENCIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">TOTAL</td>
									<td id="idTh">CONCLUIDOS</td>
									<td id="idTh">%</td>
									<td id="idTh">TOTAL</td>
								</tr>
							</thead>
							<tbody class="scrollContent">
						     <%=rpt_body%>
						    </tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>

			<%} else if(in_rc.getId() == GestionInterface.RPT_DETALLADO) {%>
			<%}
			/*
			Esteban Badillo. Fecha: 16/Oct/2009.
			Descripción:
				El siguiente bloque de codigo incluia al reporte por Area y por Detalle. Se separa la funcionalidad para que
				se realice el dibujado de los encabezados de la tabla del Reporte estadístico por area, ya que se agregaron
				tres nuevas columnas.
			*/
			//else if (in_rc.getId() == GestionInterface.RPT_POR_AREA_DETALLE || in_rc.getId() == GestionInterface.RPT_POR_EMPLEADO_DETALLE)
			else if (in_rc.getId() == GestionInterface.RPT_POR_EMPLEADO_DETALLE)
			{%>

			<table align="center" cellpadding="0" cellspacing="1" border="1" width="100%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="<%=tblname%>"  class="scrollTable">
							<thead class="fixedHeader" id="fixedHeader">
								<tr>
									<th id="idTh">&nbsp;</th>
									<!-- th id="idTh">&Aacute;REA</th-->
									<th id="idTh">FOLIO</th>
									<th id="idTh">REFERENCIA</th>
									<th id="idTh">NOMBRE DEL REMITENTE</th>
									<th id="idTh">&Aacute;REA DEL REMITENTE</th>
									<th id="idTh">FECHA ENVIO</th>
									<th id="idTh">FECHA LIMITE</th>
									<th id="idTh">ESTATUS</th>
									<!--  >th id="idTh">TURNADO A</th-->
									<th id="idTh">PRIORIDAD</th>
									<th id="idTh">ASUNTO</th>
								</tr>
							</thead>
							<tbody class="scrollContent">
						     <%=rpt_body_detalle%>
							</tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<%}
		/*
		Esteban Badillo. Fecha: 16/Oct/2009.
		Descripción:
			Se separó el bloque de AREA_DETALLE de EMPLEADO_DETALLE para agregar columnas unicamente al Reporte Estadístico por Area (Detalle)
		*/
		//else if (in_rc.getId() == GestionInterface.RPT_POR_AREA_DETALLE || in_rc.getId() == GestionInterface.RPT_POR_EMPLEADO_DETALLE)
		else if (in_rc.getId() == GestionInterface.RPT_POR_AREA_DETALLE)
		{%>

			<table align="center" cellpadding="0" cellspacing="1" border="1" width="100%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="<%=tblname%>"  class="scrollTable">
							<thead class="fixedHeader" id="fixedHeader">
								<tr>
									<th id="idTh">&nbsp;</th>
									<!-- th id="idTh">&Aacute;REA</th-->
									<th id="idTh">FOLIO</th>
									<th id="idTh">REFERENCIA</th>
									<th id="idTh">NOMBRE DEL REMITENTE</th>
									<th id="idTh">&Aacute;REA DEL REMITENTE</th>
									<th id="idTh">NOMBRE DEL RESPONSABLE</th>
									<th id="idTh">&Aacute;REA DEL RESPONSABLE</th>
									<th id="idTh">FECHA ENVIO</th>
									<th id="idTh">FECHA LIMITE</th>
									<th id="idTh">ESTATUS</th>
									<!--  >th id="idTh">TURNADO A</th-->
									<th id="idTh">PRIORIDAD</th>
									<th id="idTh">ASUNTO</th>
								</tr>
							</thead>
							<tbody class="scrollContent">
						     <%=/*rpt_body*/sb_rpt_body.toString()%>
							</tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>
		else if (in_rc.getId() == GestionInterface.RPT_POLIZA)
		{%>

			<table align="center" cellpadding="0" cellspacing="1" border="1" width="100%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="<%=tblname%>"  class="scrollTable">
							<thead class="fixedHeader" id="fixedHeader">
								<tr>
									<th id="idTh">&nbsp;</th>
									<th id="idTh"><input id="checkAll" onclick="checkTodos(this.id,'tipos_trabajo');" name="checkAll" type="checkbox" /></th>
									<th id="idTh">CENTRO CONTABLE</th>
									<th id="idTh">TIPO DE POLIZA</th>
									<th id="idTh">NUMERO</th>
									<th id="idTh">FECHA DE CAPTURA</th>
									<th id="idTh">FECHA DE APLICACION</th>
									<th id="idTh">STATUS</th>
									<th id="idTh">ORIGEN</th>
									<th id="idTh">AUTOMATICA</th>
									<th id="idTh">CONCEPTO</th>
									<th id="idTh">IMPORTE</th>
									<th id="idTh">AUTORIZO</th>
								</tr>
							</thead>
							<tbody class="scrollContent">
						     <%=/*rpt_body*/sb_rpt_body.toString()%>
							</tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<%}else if (in_rc.getId() == GestionInterface.RPT_AUDITORIA) {%>
			<table align="center" cellpadding="0" cellspacing="1" border="1" width="100%" height="89%">
			<tr>
				<td height="98%">
					<div id="tableContainer" class="tableContainer">
						<table id="<%=tblname%>"  class="scrollTable">
							<thead class="fixedHeader" id="fixedHeader">
								<tr>
									<th id="idTh">&nbsp;</th>
									<th id="idTh">MODULO</th>
									<th id="idTh">ACCION</th>
									<th id="idTh">FECHA</th>
									<th id="idTh">VALOR ORIGEN</th>
									<th id="idTh">VALOR DESTINO</th>
									<th id="idTh">USUARIO</th>
									<th id="idTh">NOMBRE USUARIO</th>
									<th id="idTh">AREA USUARIO</th>
									<th id="idTh">UR</th>
								</tr>
							</thead>
							<tbody class="scrollContent">
						     <%=rpt_body%>
							</tbody>
						</table>
					</div>
				</td>
			</tr>
		</table>
		<%}%>
	</body>
</html>
