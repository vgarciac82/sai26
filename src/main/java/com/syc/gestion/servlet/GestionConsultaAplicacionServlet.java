package com.syc.gestion.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.fortimax.core.Descripcion;
import com.syc.fortimax.core.Fortimax;
import com.syc.gestion.FortimaxBusinessLogic;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;

public class GestionConsultaAplicacionServlet extends HttpServlet implements GestionInterface {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionConsultaAplicacionServlet.class);

	private String jniName = null;

	public void init(ServletConfig config) throws ServletException {

		super.init(config);

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

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		int row = 0;
		String[][] datos = null;
		PrintWriter out = resp.getWriter();

		resp.setContentType("text/html");

		HttpSession session = req.getSession(false);
		if (session == null) {
			log.warn("No hay sesion");
			resp.sendRedirect("../index.jsp");
			// <script language="javascript">self.top.location.href = "../index.jsp";</script>
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);
		if (u == null) {
			log.warn("No hay Usuario en sesion");
			session.invalidate();
			resp.sendRedirect("../index.jsp");
			return;
		}

		boolean modificar = false;
		int id_gabinete = -1;
		String idGabineteValue = req.getParameter("id_gabinete");
		if (idGabineteValue != null) {
			id_gabinete = Integer.parseInt(idGabineteValue);
			modificar = true;
		}

		String nodeId = req.getParameter("select");
		if (nodeId == null) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Sin nodo seleccionado");
			return;
		}

		Fortimax fimx = new Fortimax(nodeId);

		resp.setHeader("Cache-Control", "yes-cache");//Ethiel, le cambie de no a yes para que se puedan regresar al resultado de la busqueda ya que es muy incomodo estar llenando simepre
		resp.setHeader("Pragma", "yes-cache");//Ethiel, le cambie de no a yes para que se puedan regresar al resultado de la busqueda ya que es muy incomodo estar llenando simepre
		resp.setHeader("Expires", "-1");

		try {

			FortimaxBusinessLogic fbl = new FortimaxBusinessLogic(jniName);

			Descripcion[] desc = fbl.getDescripcion(fimx.getTituloAplicacion());

			out.println("<html>");
			out.println("<head>");
			out.println("<title>Consulta de Expediente</title>");
			out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"css/gestion.css\">");

			if (desc.length == 0) {
				out.println("</head>");
				out.println("<body leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\" "
						+ "marginwidth=\"0\" marginheight=\"0\" scroll=\"no\">");
				out.println("<table>");
				out.println("<tr>");
				out.println("<td>");
				out.println("<h3>La gaveta no tiene campos definidos</h3>");
				out.println("</td>");
				out.println("</tr>");
				out.println("</table>");
				out.println("</body>");
				out.println("</html>");

				out.flush();
				out.close();
				return;
			}

			out.println("<script language=\"javascript\" src=\"js/consulta.js\"></script>");

			generaJavaScript(desc, out);

			out.println("</head>");

			out.println("<body bgcolor=\"#000000\" leftmargin=\"0\" topmargin=\"0\" rightmargin=\"0\" bottommargin=\"0\" "
					+ "marginwidth=\"0\" marginheight=\"0\" scroll=\"no\">");
			out.println("<form name=\"frmAplicacion\" method=\"post\" " + "action=\"showexpedients?select=" + nodeId
					+ "\">");
			out.println("<input type=\"hidden\" name=\"nuevo\" value=\"1\">");
			out.println("<input type=\"hidden\" name=\"titulo_aplicacion\" value=\"" + fimx.getTituloAplicacion()
					+ "\">");

			if (modificar)
				out.println("<input type=\"hidden\" name=\"id_gabinete\" value=\"" + id_gabinete + "\">");

			for (int i = 0; i < desc.length; i++) {
				Descripcion d = desc[i];

				if (i == 0) {
					if (modificar) {
						datos = fbl.getValoresDescripcion(fimx.getTituloAplicacion(), id_gabinete);
					}

					out.println("<table align=\"center\" width=\"100%\"><tr><td align=\"center\" class=\"bordetit\">"
							+ fimx.getTituloAplicacion() + "</td></tr></table>");
					out.println("<table align=\"center\">");
					out.println("<tr>");
					out.println("<td align=\"right\">");
					out.println("<table>");
					out.println("<tr>");

					if (modificar) {
						out.println("<td>&nbsp;</td>");
					} else {
						out.println("<td>");
						out.println("<input type=\"submit\" name=\"cmdEjecutar\" value=\"Buscar\" "
								+ "onmouseover=\"self.status='Buscar Expediente'; return true;\" "
								+ "onclick=\"return submitForm(1)\">");
						out.println("</td>");
					}

					if (modificar) {
						out.println("<td>");
						out.println("<input type=\"button\" value=\"Cancelar\" "
								+ "onmouseover=\"self.status='Cancelar Actualizaci&oacute;n'; "
								+ "return true;\" onclick=\"history.back(-1)\">");
						out.println("</td>");
					}

					// if (UsuarioManager.tienePermisos(fimx.getTituloAplicacion(), u.getLogin(), 4)) {
					out.println("<td>");
//					out.println("<input type=\"submit\" name=\"cmdCrear\" value=\""
//							+ (modificar ? "Modificar" : "Crear") + "\" onmouseover=\"self.status='"
//							+ (modificar ? "Modificar Expediente" : "Crear Expediente")
//							+ "'; return true;\" onclick=\"return submitForm(2)\">");
					out.println("&nbsp;");
					out.println("</td>");
					// } else {
					// out.println("<td>&nbsp;</td>");
					// }

					out.println("</tr>");
					out.println("</table>");
					out.println("</td>");
					out.println("</tr>");
					out.println("</table>");
					out.println("<div style=\"overflow:auto; width:782px; height:464px;\">");
					out.println("<table align=\"center\">");
				}

				out.println("<tr>");
				out.println("<td align=\"right\">" + d.getNombreColumna() + ":</td>");
				out.println("<td>");

				// Los tipos 6, 9 y 11 no estan definidos
				switch (d.getIdTipoDatos()) {
				case 3: // Small Integer
				case 4: // Long Integer
				case 5: // Decimal
				case 7: // Double, Float
					out.println("<input type=\"text\""
							+ ((d.getIndiceTipo() == 2) && modificar ? "" : " name=\"" + d.getNombreCampoLower())
							+ "\" size=\""
							+ d.getLongitudCampo()
							+ "\""
							+ (d.getLongitudCampo() > 0 ? " maxlength=\"" + d.getLongitudCampo() + "\"" : "")
							+ (modificar ? " value=\"" + ((datos[row][i + 1] == null) ? "" : datos[row][i + 1]) + "\""
									: "")
							+ (d.getIndiceTipo() == 2 && modificar ? " disabled" : " onkeypress=\"keystrokInteger()\"")
							+ ">");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"" + d.getNombreCampoLower() + "\" value=\""
								+ ((datos[row][i + 1] == null) ? "" : datos[row][i + 1]) + "\">");
					break;
				case 8: // Fecha
					Calendar cal = Calendar.getInstance();
					int curr_year = cal.get(Calendar.YEAR);
					int ini_year = curr_year - 80;
					int end_year = curr_year + 20;

					int day = 0,
					month = 0,
					year = 0;
					if (modificar) {
						if (!"".equals(datos[row][i + 1])) {
							cal.setTime((new SimpleDateFormat("yyyy-MM-dd")).parse(datos[row][i + 1]));
							day = cal.get(Calendar.DATE);
							month = cal.get(Calendar.MONTH) + 1;
							year = cal.get(Calendar.YEAR);
						}
					}

					// Dias
					out.println("<table><tr align=\"center\"><td>D&iacute;a</td><td>Mes</td><td>A&ntilde;o</td><td>D&iacute;a</td><td>Mes</td><td>A&ntilde;o</td><tr>");
					out.println("<td>Del <select"
							+ ((d.getIndiceTipo() == 2) && modificar ? " disabled" : " name=\"_day_"
									+ d.getNombreCampoLower() + "\"") + ">");
					out.println("<option value=\"\"></option>");
					for (int j = 1; j <= 31; j++)
						out.println("<option value=\"" + j + "\"" + ((j == day) ? "selected" : "") + ">" + j
								+ "</option>");
					out.println("</select></td>");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"_day_" + d.getNombreCampoLower() + "\" value=\""
								+ day + "\">");

					// Meses
					out.println("<td><select"
							+ ((d.getIndiceTipo() == 2) && modificar ? " disabled" : " name=\"_mes_"
									+ d.getNombreCampoLower() + "\"") + ">");
					out.println("<option value=\"\" selected></option>");
					for (int j = 1; j <= 12; j++)
						out.println("<option value=\"" + j + "\"" + ((j == month) ? "selected" : "") + ">" + j
								+ "</option>");
					out.println("</select></td>");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"_mes_" + d.getNombreCampoLower() + "\" value=\""
								+ month + "\">");

					out.println("<td><select"
							+ ((d.getIndiceTipo() == 2) && modificar ? " disabled" : " name=\"_year_"
									+ d.getNombreCampoLower() + "\"") + ">");
					out.println("<option value=\"\" selected></option>");
					for (int j = end_year; j >= ini_year; j--)
						out.println("<option value=\"" + j + "\"" + ((j == year) ? "selected" : "") + ">" + j
								+ "</option>");
					out.println("</select></td>");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"_year_" + d.getNombreCampoLower() + "\" value=\""
								+ year + "\">");

					//Ethiel, para rangos
					out.println("<td>Al <select"
							+ ((d.getIndiceTipo() == 2) && modificar ? " disabled" : " name=\"_dayfinal_"
									+ d.getNombreCampoLower() + "\"") + ">");
					out.println("<option value=\"\"></option>");
					for (int j = 1; j <= 31; j++)
						out.println("<option value=\"" + j + "\"" + ((j == day) ? "selected" : "") + ">" + j
								+ "</option>");
					out.println("</select></td>");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"_dayfinal_" + d.getNombreCampoLower() + "\" value=\""
								+ day + "\">");

					// Meses
					out.println("<td><select"
							+ ((d.getIndiceTipo() == 2) && modificar ? " disabled" : " name=\"_mesfinal_"
									+ d.getNombreCampoLower() + "\"") + ">");
					out.println("<option value=\"\" selected></option>");
					for (int j = 1; j <= 12; j++)
						out.println("<option value=\"" + j + "\"" + ((j == month) ? "selected" : "") + ">" + j
								+ "</option>");
					out.println("</select></td>");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"_mesfinal_" + d.getNombreCampoLower() + "\" value=\""
								+ month + "\">");

					out.println("<td><select"
							+ ((d.getIndiceTipo() == 2) && modificar ? " disabled" : " name=\"_yearfinal_"
									+ d.getNombreCampoLower() + "\"") + ">");
					out.println("<option value=\"\" selected></option>");
					for (int j = end_year; j >= ini_year; j--)
						out.println("<option value=\"" + j + "\"" + ((j == year) ? "selected" : "") + ">" + j
								+ "</option>");
					out.println("</select></td>");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"_yearfinal_" + d.getNombreCampoLower() + "\" value=\""
								+ year + "\">");
					//Ethiel, termina
					out.println("</tr></table>");
					break;
				case 10: // String
					if (d.getLongitudCampo() < 80) {
						out.println("<input type=\"text\""
								+ ((d.getIndiceTipo() == 2) && modificar ? "" : " name=\"" + d.getNombreCampoLower())
								+ "\" size=\""
								+ d.getLongitudCampo()
								+ "\""
								+ (d.getLongitudCampo() > 0 ? " maxlength=\"" + d.getLongitudCampo() + "\"" : "")
								+ (modificar ? " value=\"" + ((datos[row][i + 1] == null) ? "" : datos[row][i + 1])
										+ "\"" : "") + (d.getIndiceTipo() == 2 && modificar ? " disabled" : "") + ">");

						if ((d.getIndiceTipo() == 2) && modificar)
							out.println("<input type=\"hidden\" name=\"" + d.getNombreCampoLower() + "\" value=\""
									+ ((datos[row][i + 1] == null) ? "" : datos[row][i + 1]) + "\">");
						break;
					}
				case 12: // Long String
					// Indice unico (d.getIndiceTipo() = 2)
					out
							.println("<textarea"
									+ ((d.getIndiceTipo() == 2) && modificar ? "" : " name=\""
											+ d.getNombreCampoLower()) + "\" cols=\"40\" rows=\"3\""
									+ " onkeydown=\"event.returnValue = validityLength(this,"
									+ (d.getLongitudCampo() - 1) + ")\""
									+ (((d.getIndiceTipo() == 2) && modificar) ? " disabled" : "") + ">"
									+ (modificar ? ((datos[row][i + 1] == null) ? "" : datos[row][i + 1]) : "")
									+ "</textarea>");

					if ((d.getIndiceTipo() == 2) && modificar)
						out.println("<input type=\"hidden\" name=\"" + d.getNombreCampoLower() + "\" value=\""
								+ ((datos[row][i + 1] == null) ? "" : datos[row][i + 1]) + "\">");
					break;
				}

				out.println("</td>");
				out.println("</tr>");
			}

			out.println("<tr>");
			out.println("<td>");
			out.println("</td>");
			out.println("</tr>");
			out.println("<tr>");
			out.println("</td>");
			out.println("</tr>");
			out.println("</table>");
			out.println("</div>");
			out.println("<script language=\"javascript\">");
			out.println("<!--");
			out.println("function submitForm(type)");

			String token = "";
			StringBuffer arrFnc = new StringBuffer();
			for (int i = 0; i < desc.length; i++) {
				Descripcion d = desc[i];

				switch (d.getIdTipoDatos()) {
				case 3: // Small Integer
				case 4: // Long Integer
					arrFnc.append(token + "\"checkInteger(f." + d.getNombreCampoLower() + ",'" + d.getNombreColumna()
							+ "'," + !d.getRequeridoBoolean() + ")\"");
					break;
				case 5: // Decimal
				case 7: // Double, Float
					arrFnc.append(token + "\"checkFloat(f." + d.getNombreCampoLower() + ",'" + d.getNombreColumna()
							+ "'," + !d.getRequeridoBoolean() + ")\"");
					break;
				case 8: // Fecha
					arrFnc.append(token + "\"checkDate(f._year_" + d.getNombreCampoLower() + ",f._mes_"
							+ d.getNombreCampoLower() + ",f._day_" + d.getNombreCampoLower() + ",'Día',"
							+ !d.getRequeridoBoolean() + ")\"");
					//Ethiel, para rangos
					arrFnc.append(token + "\"checkDate(f._yearfinal_" + d.getNombreCampoLower() + ",f._mesfinal_"
							+ d.getNombreCampoLower() + ",f._dayfinal_" + d.getNombreCampoLower() + ",'Dia',"
							+ !d.getRequeridoBoolean() + ")\"");
					//Ethiel, termina
					break;
				case 10: // String
				case 12: // Long String
					arrFnc.append(token + "\"checkString(f." + d.getNombreCampoLower() + ",'" + d.getNombreColumna()
							+ "'," + !d.getRequeridoBoolean() + ")\"");
					break;
				}

				token = ",";
			}

			out.println("{  var i;");
			out.println("   var f = document.frmAplicacion;");
			out.println("   var arrFnc = new Array(" + arrFnc.toString() + ");");
			out.println("   if(type == 2)");
			out.println("   {  for (i = 0; i < arrFnc.length; i++)");
			out.println("         if (!eval(arrFnc[i])) return false;");
			if (modificar)
				out.println("      f.action=\"updexpedient?select=" + nodeId + "\";");
			else
				out.println("      f.action=\"createexpedient?select=" + nodeId + "\";");
			out.println("      return confirm(\"Desea" + (modificar ? " modificar el " : " crear un ")
					+ "expediente con los datos proporcionados?\");");
			out.println("   }");
			out.println("   f.action=\"showexpedients?select=" + nodeId + "\";");
			out.println("   return true;");
			out.println("}");
			out.println("// -->");
			out.println("</script>");
			out.println("</form>");
			out.println("</body>");
			out.println("</html>");

			out.flush();
		} catch (GestionException exc) {
			log.error("Formato de fecha invalido", exc);
			throw new ServletException(exc);
		} catch (ParseException exc) {
			log.error("Formato de fecha invalido", exc);
			throw new ServletException(exc);
		} finally {
			out.close();
		}
	}

	private void generaJavaScript(Descripcion[] desc, PrintWriter out) {

		boolean jsDate = false;
		boolean jsInteger = false;
		boolean jsDouble = false;
		boolean jsString = false;

		for (int i = 0; i < desc.length; i++) {
			Descripcion d = desc[i];

			// Los tipos 6, 9 y 11 no estan definidos
			switch (d.getIdTipoDatos()) {
			case 3: // Small Integer
			case 4: // Long Integer
				jsInteger = true;
				break;
			case 5: // Decimal
			case 7: // Double, Float
				jsDouble = true;
				break;
			case 8: // Fecha
				jsDate = true;
				break;
			case 10: // String
			case 12: // Long String
				jsString = true;
				break;
			}
		}

		out.println("<script language=\"javascript\">");
		out.println("<!--");
		if (jsInteger || jsDouble) {
			out.println("function isDigit(c) { return ((c >= \"0\") && (c <= \"9\")); }");
			out.println("function keystrokInteger()");
			out.println("{");
			out.println("	var s = String.fromCharCode(window.event.keyCode);");
			out.println("	if (!isDigit(s))");
			out.println("		window.event.keyCode = 0;");
			out.println("}");
		}

		out.println("function isEmpty(s) { return ((s == null) || (s.length == 0)); }");
		out.println("function isWhitespace(s)");
		out.println("{  var i;");
		out.println("   var whitespace = \" \\t\\n\\r\";");
		out.println("   if (isEmpty(s)) return true;");
		out.println("   for (i = 0; i < s.length; i++)");
		out.println("   {  var c = s.charAt(i);");
		out.println("      if (whitespace.indexOf(c) == -1) return false;");
		out.println("   }");
		out.println("   return true;");
		out.println("}");

		if (jsInteger || jsString) {
			out.println("var mPrefix = \"No se capturo el campo \\\"\"");
			out.println("var mSuffix = \"\\\".\\n\\nEste campo es requerido.\\n\\nPor favor ingrese la información.\"");
			out.println("var mPrefixWhitespace = \"El campo \\\"\"");
			out
					.println("var mSuffixWhitespace = \"\\\" no debe tener espacios en blanco.\\n\\nProporcione información o dejelo vacio.\"");
			out.println("function warnEmpty(theField, labelString, IsWwhitespace)");
			out.println("{  theField.focus();");
			out.println("   if (warnEmpty.arguments.length == 2) IsWwhitespace = false;");
			out.println("   if (IsWwhitespace)");
			out.println("      alert(mPrefixWhitespace + labelString + mSuffixWhitespace);");
			out.println("   else");
			out.println("      alert(mPrefix + labelString + mSuffix);");
			out.println("   return false;");
			out.println("}");

			out.println("function validityLength(theField, length)");
			out.println("{	var retval = true;");
			out.println("	if (theField.value.length > length)");
			out.println("	{	if ((window.event.keyCode != 8) &&");
			out.println("		   (window.event.keyCode != 37) &&");
			out.println("		   (window.event.keyCode != 38) &&");
			out.println("		   (window.event.keyCode != 39) &&");
			out.println("		   (window.event.keyCode != 40) &&");
			out.println("		   (window.event.keyCode != 46))");
			out.println("		{	alert(\"Se alcanzo la longitud maxima.\");");
			out.println("			retval = false;");
			out.println("		}");
			out.println("	}");
			out.println("	return retval;");
			out.println("}");
		}

		if (jsDate) {
			out.println("var iDatePrefix = \"El \\\"\"");
			out.println("var iDateSuffix = \"\\\" para esta Fecha, no es valido en este Año.\"");
			out
					.println("function daysInFebruary(year) { return (((year % 4 == 0) && ((!(year % 100 == 0)) || (year % 400 == 0))) ? 29 : 28); }");
			out.println("function isDate (year, month, day)");
			out.println("{  var intYear = parseInt(year);");
			out.println("	var intMonth = parseInt(month);");
			out.println("	var intDay = parseInt(day);");
			out.println("	if ((intMonth == 2) && (intDay > daysInFebruary(intYear))) return false;");
			out.println("	return true;");
			out.println("}");
			out.println("function checkDate(yearField, monthField, dayField, labelString, emptyOK)");
			out.println("{  if (checkDate.arguments.length == 4) emptyOK = false;");
			out
					.println("   if (isEmpty(dayField.value)) return ((emptyOK)? true: warnEmpty(dayField, labelString, emptyOK));");
			out
					.println("   if (isEmpty(monthField.value)) return ((emptyOK)? true: warnEmpty(monthField, \"Mes\", emptyOK));");
			out
					.println("   if (isEmpty(yearField.value)) return ((emptyOK)? true: warnEmpty(yearField, \"Año\", emptyOK));");
			out.println("	if (isDate(yearField.value, monthField.value, dayField.value))");
			out.println("	   return true;");
			out.println("   dayField.focus();");
			out.println("	alert (iDatePrefix + labelString + iDateSuffix);");
			out.println("	return false;");
			out.println("}");
		}

		if (jsInteger) {
			out.println("var iIntegerPrefix = \"El valor para \\\"\"");
			out.println("var iIntegerSuffix = \"\\\" no es un numero entero.\"");
			out.println("function isInteger(s, emptyOK)");
			out.println("{  var i;");
			out.println("   if (isInteger.arguments.length == 1) emptyOK = false;");
			out.println("   if ((emptyOK == true) && isEmpty(s)) return true;");
			out.println("   for (i = 0; i < s.length; i++)");
			out.println("   {  var c = s.charAt(i);");
			out.println("      if (!isDigit(c)) return false;");
			out.println("   }");
			out.println("   return true;");
			out.println("}");
			out.println("function checkInteger(theField, labelString, emptyOK)");
			out.println("{  if (checkInteger.arguments.length == 2) emptyOK = false;");
			out
					.println("   if (isEmpty(theField.value)) return ((emptyOK)? true: warnEmpty(theField, labelString, emptyOK));");
			out.println("   if (isInteger(theField.value, emptyOK))");
			out.println("      return true;");
			out.println("   theField.focus();");
			out.println("   theField.select();");
			out.println("   alert (iIntegerPrefix + labelString + iIntegerSuffix);");
			out.println("   return false;");
			out.println("}");
		}

		if (jsDouble) {
			out.println("var iFloatPrefix = \"El valor para \\\"\"");
			out.println("var iFloatSuffix = \"\\\" no es una cantidad correcta.\"");
			out.println("function isFloat(s, emptyOK)");
			out.println("{  var i;");
			out.println("   var decimalPointDelimiter = \".\"");
			out.println("   var seenDecimalPoint = false;");
			out.println("   if (isFloat.arguments.length == 1) emptyOK = false;");
			out.println("   if ((emptyOK == true) && isEmpty(s)) return true;");
			out.println("   if (s == decimalPointDelimiter) return false;");
			out.println("   for (i = 0; i < s.length; i++)");
			out.println("   {  var c = s.charAt(i);");
			out.println("      if ((c == decimalPointDelimiter) && !seenDecimalPoint) seenDecimalPoint = true;");
			out.println("      else if (!isDigit(c)) return false;");
			out.println("   }");
			out.println("   return true;");
			out.println("}");
			out.println("function checkFloat(theField, labelString, emptyOK)");
			out.println("{  if (checkFloat.arguments.length == 3) emptyOK = false;");
			out
					.println("   if (isEmpty(theField.value)) return ((emptyOK)? true: warnEmpty(theField, labelString, emptyOK));");
			out.println("	if (isFloat(theField.value, emptyOK))");
			out.println("	   return true;");
			out.println("   theField.focus();");
			out.println("   theField.select();");
			out.println("	alert (iFloatPrefix + labelString + iFloatSuffix);");
			out.println("	return false;");
			out.println("}");
		}

		if (jsString) {
			out.println("function checkString(theField, labelString, emptyOK)");
			out.println("{  if (checkString.arguments.length == 2) emptyOK = false;");
			out.println("   if ((emptyOK == true) && (isEmpty(theField.value))) return true;");
			out.println("   if (isWhitespace(theField.value))");
			out.println("   {  theField.select();");
			out.println("      return warnEmpty(theField, labelString, emptyOK);");
			out.println("   }");
			out.println("   else return true;");
			out.println("}");
		}

		out.println("// -->");
		out.println("</script>");
	}
}