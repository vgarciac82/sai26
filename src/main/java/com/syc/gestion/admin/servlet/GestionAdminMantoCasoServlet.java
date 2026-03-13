package com.syc.gestion.admin.servlet;

import java.io.IOException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.gestion.admin.GestionAdminException;
import com.syc.gestion.admin.TipoCasoBusinessLogic;
import com.syc.gestion.core.Operacion;
import com.syc.gestion.core.OperacionSiguiente;
import com.syc.gestion.core.TipoCaso;
import com.syc.gestion.core.TipoCasoVariable;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.servlet.GestionInterface;

public class GestionAdminMantoCasoServlet extends HttpServlet implements GestionInterface {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(GestionAdminMantoCasoServlet.class);

	private final String TIPO_CASO = "c";
	private final String VARS_CASO = "v";
	private final String OPER_CASO = "o";
	private final String OPER_SIGTE_CASO = "r";
	private final int INSERT = 1;
	private final int UPDATE = 2;
	private final int DELETE = 3;

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

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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

		String type = req.getParameter("type");
		if (type == null) {
			log.warn("Tipo de envio invalido");
			// TODO Enviar un javascript window.alert("Tipo de envio invalido");
			return;
		}

		String strMode = req.getParameter("mode");
		if (strMode == null) {
			log.warn("Modo invalido");
			// TODO Enviar un javascript window.alert("Modo invalido");
			return;
		}

		int mode;
		try {
			mode = Integer.parseInt(strMode);
		} catch (NumberFormatException exc) {
			log.warn("Modo (" + strMode + ") no soportado");
			// TODO Enviar un javascript window.alert("Modo (" + strMode + ") no soportado");
			return;
		}

		if (TIPO_CASO.equalsIgnoreCase(type)) {
			mantenimientoTipoCaso(req, resp, mode);
		} else if (VARS_CASO.equalsIgnoreCase(type)) {
			mantenimientoCasoVariable(req, resp, mode);
		} else if (OPER_CASO.equalsIgnoreCase(type)) {
			mantenimientoOperacion(req, resp, mode);
		} else if (OPER_SIGTE_CASO.equalsIgnoreCase(type)) {
			mantenimientoOperacionResponsable(req, resp, mode);
		} else {
			log.warn("Tipo de envio (" + type + ") no soportado");
			// TODO Enviar un javascript window.alert("Tipo de envio (" + type + ") no soportado");
			return;
		}
	}

	private void mantenimientoTipoCaso(HttpServletRequest req, HttpServletResponse resp, int mode)
			throws ServletException, IOException {

		ServletOutputStream out = resp.getOutputStream();
		try {
			String id_tc = req.getParameter("id_tc");
			String tc_descripcion = req.getParameter("tc_descripcion");
			String tc_gaveta_asociada = req.getParameter("tc_gaveta_asociada");
			String tc_tiempo_limite = req.getParameter("tc_tiempo_limite");
			String tc_who_can_init = req.getParameter("tc_who_can_init");
			String tc_alarma = req.getParameter("tc_alarma");

			TipoCaso tc = new TipoCaso();

			tc.setIdTC(Integer.parseInt(id_tc));
			tc.setDescripcion(tc_descripcion);
			tc.setGavetaAsociada(tc_gaveta_asociada);
			tc.setTiempoLimite(Integer
					.parseInt(((tc_tiempo_limite == null) || ("".equals(tc_tiempo_limite.trim()))) ? "-1"
							: tc_tiempo_limite));
			tc.setWhoCanInit(tc_who_can_init);
			tc.setAlarma(tc_alarma);

			TipoCasoBusinessLogic tcbl = new TipoCasoBusinessLogic(jniName);

			String command = "";
			try {
				switch (mode) {
					case DELETE:
						// TODO Validaciones
						tcbl.borraTipoCaso(tc.getIdTC());
						command = "window.location.href=\"wrkflw.jsp\";";
						break;
					case INSERT:
						// TODO Validaciones
						tcbl.agregaTipoCaso(tc);
						command = "window.location.href=\"wrkflw.jsp?id_tc=" + tc.getIdTC() + "&mode=modoCaso(2)\";";
						break;
					case UPDATE:
						// TODO Validaciones
						tcbl.actualizaTipoCaso(tc);
						command = "consultaCaso(" + tc.getIdTC() + ", false);";
						break;
				}
			} catch (GestionAdminException exc) {
				log.error("mantenimientoTipoCaso", exc);
				String msg = encodeMsg(exc.getMessage());
				command = "window.alert('" + msg + "');";
			}

			resp.setContentType("text/javascript");
			out.println(command);
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	private String encodeMsg(String str) {

		if (str == null)
			return null;

		StringBuffer ret = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\n')
				continue;

			if (str.charAt(i) == '"')
				ret.append("\\" + str.charAt(i));

			ret.append(str.charAt(i));
		}

		return ret.toString();
	}

	private void mantenimientoCasoVariable(HttpServletRequest req, HttpServletResponse resp, int mode)
			throws ServletException, IOException {

		ServletOutputStream out = resp.getOutputStream();
		try {
			String id_tc = req.getParameter("id_tc");
			String id_tcv = req.getParameter("id_tcv");
			String tcv_nombre = req.getParameter("tcv_nombre");
			String tcv_etiqueta = req.getParameter("tcv_etiqueta");
			String tcv_tipo = req.getParameter("tcv_tipo");
			String tcv_longitud = req.getParameter("tcv_longitud");
			String tcv_en_gaveta = req.getParameter("tcv_en_gaveta");
			String tcv_descripcion = req.getParameter("tcv_descripcion");

			TipoCasoVariable tcv = new TipoCasoVariable();

			tcv.setIdTC(Integer.parseInt((((id_tc == null) || ("".equals(id_tc.trim()))) ? "-1" : id_tc)));
			tcv.setIdTCV(Integer.parseInt((((id_tcv == null) || ("".equals(id_tcv.trim()))) ? "-1" : id_tcv)));
			tcv.setNombre(tcv_nombre);
			tcv.setEtiqueta(tcv_etiqueta);
			tcv.setTipo(Integer.parseInt(tcv_tipo));
			tcv.setLongitud(Integer.parseInt(((tcv_longitud == null) || ("".equals(tcv_longitud.trim()))) ? "-1"
					: tcv_longitud));
			tcv.setEnGaveta(((tcv_en_gaveta == null) ? "N" : tcv_en_gaveta));
			tcv.setDescripcion(tcv_descripcion);

			TipoCasoBusinessLogic tcbl = new TipoCasoBusinessLogic(jniName);
			try {
				switch (mode) {
					case DELETE:
						// TODO Validaciones
						tcbl.borraTipoCasoVariable(tcv.getIdTC(), tcv.getIdTCV());
						break;
					case INSERT:
						// TODO Validaciones
						tcbl.agregaTipoCasoVariable(tcv);
						break;
					case UPDATE:
						// TODO Validaciones
						tcbl.actualizaTipoCasoVariable(tcv);
						break;
				}
			} catch (GestionAdminException exc) {
				log.error("mantenimientoCasoVariable", exc);
				// TODO Enviar un javascript window.alert( + exc.getMessage + );
			}

			resp.setContentType("text/javascript");
			out.println("consultaCaso(" + tcv.getIdTC() + ", false);");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	private void mantenimientoOperacion(HttpServletRequest req, HttpServletResponse resp, int mode)
			throws ServletException, IOException {

		ServletOutputStream out = resp.getOutputStream();
		try {
			String id_tc = req.getParameter("id_tc");
			String id_oper = req.getParameter("id_oper");
			String o_numero = req.getParameter("o_numero");
			String o_nombre = req.getParameter("o_nombre");
			String o_responsable = req.getParameter("o_responsable");
			String o_descripcion = req.getParameter("o_descripcion");
			String o_plantilla = req.getParameter("o_plantilla");
			String o_tiempo_limite = req.getParameter("o_tiempo_limite");
			String o_folder_docto = req.getParameter("o_folder_docto");
			String o_alarma = req.getParameter("o_alarma");
			String o_on_load = req.getParameter("o_on_load");
			String o_post_display = req.getParameter("o_post_display");
			String o_on_submit = req.getParameter("o_on_submit");

			Operacion o = new Operacion();

			o.setIdTC(Integer.parseInt(id_tc));
			o.setIdOperacion(Integer.parseInt((((id_oper == null) || ("".equals(id_oper.trim()))) ? "-1" : id_oper)));
			o.setNumero(Integer.parseInt((((o_numero == null) || ("".equals(o_numero.trim()))) ? "-1" : o_numero)));
			o.setNombre(o_nombre);
			o.setResponsable(o_responsable);
			o.setDescripcion(o_descripcion);
			o.setPlantilla(o_plantilla);
			o.setTiempoLimite(Integer.parseInt(((o_tiempo_limite == null) || ("".equals(o_tiempo_limite.trim())) ? "-1"
					: o_tiempo_limite)));
			o.setFolderDocto(o_folder_docto);
			o.setAlarma(o_alarma);
			o.setOnLoad(o_on_load);
			o.setPostDisplay(o_post_display);
			o.setOnSubmit(o_on_submit);

			TipoCasoBusinessLogic tcbl = new TipoCasoBusinessLogic(jniName);
			try {
				switch (mode) {
					case DELETE:
						// TODO Validaciones
						tcbl.borraOperacion(o.getIdTC(), o.getIdOperacion());
						break;
					case INSERT:
						// TODO Validaciones
						tcbl.agregaOperacion(o);
						break;
					case UPDATE:
						// TODO Validaciones
						tcbl.actualizaOperacion(o);
						break;
				}
			} catch (GestionAdminException exc) {
				log.error("mantenimientoOperacion", exc);
				// TODO Enviar un javascript window.alert( + exc.getMessage + );
			}

			resp.setContentType("text/javascript");
			out.println("consultaCaso(" + o.getIdTC() + ", false);");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}

	private void mantenimientoOperacionResponsable(HttpServletRequest req, HttpServletResponse resp, int mode)
			throws ServletException, IOException {

		ServletOutputStream out = resp.getOutputStream();
		try {
			String id_tc = req.getParameter("id_tc");
			String id_oper = req.getParameter("id_oper");
			String id_oper_sigte = req.getParameter("id_oper_sigte");
			String os_responsable = req.getParameter("os_responsable");
			String os_operacion = req.getParameter("os_operacion");

			OperacionSiguiente os = new OperacionSiguiente();

			os.setIdTC(Integer.parseInt(id_tc));
			os.setIdOperacion(Integer.parseInt(id_oper));
			os.setIdOperacionSigte(Integer.parseInt(((id_oper_sigte == null) || 
					("".equals(id_oper_sigte.trim()))) ? "-1" : id_oper_sigte.trim()));
			os.setResponsable(os_responsable);
			os.setOperacion(os_operacion);

			TipoCasoBusinessLogic tcbl = new TipoCasoBusinessLogic(jniName);
			try {
				switch (mode) {
					case DELETE:
						tcbl.borraOperacionSiguiente(os.getIdTC(), os.getIdOperacion(), os.getIdOperacionSigte());
						break;
					case INSERT:
						tcbl.agregaOperacionSiguiente(os);
						break;
					case UPDATE:
						tcbl.actualizaOperacionSiguiente(os);
						break;
				}
			} catch (GestionAdminException exc) {
				log.error("mantenimientoOperacionResponsable", exc);
				// TODO Enviar un javascript window.alert( + exc.getMessage + );
			}

			resp.setContentType("text/javascript");
			out.println("consultaCaso(" + os.getIdTC() + ", false);modoOper(2);");
		} finally {
			if (out != null) {
				out.flush();
				out.close();
			}
		}
	}
}
