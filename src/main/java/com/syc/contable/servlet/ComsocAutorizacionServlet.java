package com.syc.contable.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.syc.contable.AdecuacionBusinessLogic;
import com.syc.contable.ComsocAutorizacionBussinesLogic;
import com.syc.gestion.CasoBusinessLogic;
import com.syc.gestion.CasoOperacionBusinessLogic;
import com.syc.gestion.core.Caso;
import com.syc.gestion.core.CasoOperacion;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.core.Usuario;
import com.syc.gestion.custom.FolioGeneratorInterface;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.sai.contabilidad.servlet.ResponseSender;

public class ComsocAutorizacionServlet extends HttpServlet implements GestionInterface {
	private static final long	serialVersionUID	= 960968562779591000L;
	private String				jniName				= null;
	private static final Logger	log					= Logger.getLogger(ComsocAutorizacionServlet.class);
	private static String		folioGenerator		= null;

	AdecuacionBusinessLogic		adecProy			= new AdecuacionBusinessLogic(GestionInterface.ATT_CONEXION);	// para
																													// obtener
																													// el
																													// ejercicio
																													// fiscal
																													// en
																													// diferentes
																													// funciones

	/**
	 * Constructor of the object.
	 */
	public ComsocAutorizacionServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session == null) {
			ResponseSender.sendError(resp, "Sin session. Por favor reingrese al sistema.");
			return;
		}

		Usuario u = (Usuario) session.getAttribute(ATT_USER);

		if (u == null) {
			ResponseSender.sendError(resp, "Sin usuario en session. Por favor reingrese al sistema.");
			return;
		}

		try {
			Caso cRein = null;
			FolioGeneratorInterface fg = null;
			try {
				String TipoPago =req.getParameter("h_TipoPago");
				int FolioPago = Integer.parseInt( req.getParameter("hFolioPago"));
				
				ClassLoader cl = getClass().getClassLoader();
				Class<?> clase = cl.loadClass(folioGenerator);
				fg = (FolioGeneratorInterface) clase.newInstance();

				cRein = generaCaso(u, 39, fg, "CAPTURISTA_AUTORIZACOMSOC", "Solicitud Autoriza Comsoc",FolioPago,TipoPago); 
				int idCasoReintegro = new Integer(cRein.getFolio().substring(cRein.getFolio().lastIndexOf('-') + 1)).intValue();
				String folioCasoReintegro = cRein.getFolio();
				ResponseSender.sendClientSimpleMessage(resp, true, "true");
			} catch (GestionException e) {
				log.error("Generador de folios", e);
				throw new ServletException(e);
			} catch (SQLException e) {
				log.error("Generador de folios", e);
				throw new ServletException(e);
			} catch (ClassNotFoundException exc) {
				log.error("Generador de folios", exc);
				throw new ServletException(exc);
			} catch (InstantiationException exc) {
				log.error("Generador de folios", exc);
				throw new ServletException(exc);
			} catch (IllegalAccessException exc) {
				log.error("Generador de folios", exc);
				throw new ServletException(exc);
			}
		} catch (Exception e) {
			ResponseSender.sendClientSimpleMessage(resp, false, e.toString());
		}

	}

	private Caso generaCaso(Usuario u, int idTCaso, FolioGeneratorInterface fg, String opResponsable, String concepto,int FolioPago ,String TipoPago  ) throws GestionException, SQLException {
		ComsocAutorizacionBussinesLogic objInsertComsoc = new ComsocAutorizacionBussinesLogic(jniName);
		CasoBusinessLogic casoTx = new CasoBusinessLogic(jniName);
		
		
		
		
		Caso c = casoTx.IniciaCaso(u, idTCaso, fg);

		
		Date date = Calendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		String fecha = sdf.format(date);
		String cEjercicio = adecProy.obtenEjercicioFiscal();

		// Variables del caso
		c.getCasoDato("FOLIO").setValor(c.getFolio());
		c.getCasoDato("OPERADOR").setValor(u.getLogin());
		c.getCasoDato("FECHA_DOCUMENTO").setValor(fecha);
		c.getCasoDato("EJERCICIO_FISCAL").setValor(cEjercicio);
		c.getCasoDato("CONCEPTO_MOV").setValor(concepto);
		c.getCasoDato("MONEDA").setValor("MXP");

		Map<String, String> m = new HashMap<String, String>();
		m.put("FOLIO", c.getFolio());
		m.put("OPERADOR", u.getLogin());
		m.put("FECHA_DOCUMENTO", fecha);
		m.put("EJERCICIO_FISCAL", cEjercicio);
		m.put("CONCEPTO_MOV", concepto);
		m.put("MONEDA", "MXP");

		c.setIdGabinete(casoTx.creaExpediente(u.getLogin(), c));
			// Guarda las variables de caso.
		c = casoTx.actualizaCasoDato(c, m);
		try {
			objInsertComsoc.InsertaComsoc(FolioPago,TipoPago , c.getFolio(), cEjercicio, u.getPropiedad("CCENTROCONTABLE").getValor());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Cambia el usuario al grupo ventanilla para que aparezca en el inbox.
		CasoOperacion co = ((CasoOperacion) c.getCasoOperacion().get(0));
		CasoOperacionBusinessLogic cobl = new CasoOperacionBusinessLogic(jniName);
		//cobl.updateCasoResponsable(co, opResponsable);
		String sUnudadesEjecutoras = "A0,A02,A03,A04,A05,C01,C02,C03,C04,C05,C06,C07,C08,C09,D01,D02,D03,D04,E01,E02,E03,E04,E05,G33,G34,G35,I01,I02" 
									+",I03,I04,I05,O01,P01,P02,P03,P04,P05,P06,P07,P08,P09,P10,P11,P12,P13,P14,P15,P16,P17,P18,P19,P20,P21,P22,P23,"
									+"P24,P25,P26,P27,S01,S02,S03,S04,S05,S06";		
		if (sUnudadesEjecutoras.indexOf(u.getU_UR())>0)
			 cobl.updateCasoResponsable(co, "CAPTURISTA_AUTORIZACOMSOC");
		else cobl.updateCasoResponsable(co, opResponsable);
		 
		return c;
	}

	/**
	 * Initialization of the servlet. <br>
	 * 
	 * @throws ServletException
	 *             if an error occurs
	 */
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

		try {
			InitialContext ic = new InitialContext();
			folioGenerator = (String) ic.lookup("java:comp/env/folioGeneratorInterface");

			if (folioGenerator == null) {
				folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
				log.info("Environment Entry \"folioGeneratorInterface\" nula usando default \"" + folioGenerator + "\"");
			} else
				log.info("folioGeneratorInterface=" + folioGenerator);
		} catch (NamingException exc) {
			folioGenerator = "com.syc.gestion.custom.DefaultFolioGenerator";
			log.info("Environment Entry \"folioGeneratorInterface\" no definida usando default \"" + folioGenerator + "\"");
		}
	}

}
