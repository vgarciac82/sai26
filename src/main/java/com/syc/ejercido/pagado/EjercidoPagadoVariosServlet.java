package com.syc.ejercido.pagado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.syc.contable.servlet.EnviaCorreosVigenciaAdecuaciones;
import com.syc.dsmngr.DataSourceManager;
import com.syc.gestion.servlet.GestionInterface;

public class EjercidoPagadoVariosServlet extends HttpServlet implements GestionInterface, Runnable {

	private static final long	serialVersionUID	= 103570127154328876L;
	private long				interval			= -1L;
	private volatile Thread		verificaTiempoLimiteApartado;
	Logger						log					= Logger.getLogger(EnviaCorreosVigenciaAdecuaciones.class);
	private String				jniName;
	private int					horaDesactivar		= 0;														
	private int					horaAplicar			= 0;														
	private Calendar			hoy					= null;
	private int					activo				= 0;
	private String				folioGenerator;

	/**
	 * Constructor of the object.
	 */
	public EjercidoPagadoVariosServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	public void init(ServletConfig config) throws ServletException {


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

		String strRunIntervalProcess = config.getInitParameter("runIntervalProcess");
		String strInterval = config.getInitParameter("sleepIntervalProcess");

		interval = Long.parseLong(strInterval);
		if (interval == -1L) {
			System.out.println("interval: ");
			interval = 1000 * 60;
			log.info("Init Parameter \"sleepIntervalProcess\" nulo usando default \"" + interval + "\"");

		} else {
			log.info("sleepIntervalProcess=" + interval);
		}
		log.info("runIntervalProcess=" + "true".equalsIgnoreCase(strRunIntervalProcess));

		if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
			log.info("Iniciando Background Process");
			verificaTiempoLimiteApartado = new Thread(this);
			verificaTiempoLimiteApartado.setPriority(Thread.MIN_PRIORITY);
			verificaTiempoLimiteApartado.start();
		}

	}

	public void run() {

		EjercidoPagadoValidarVarios epv = new EjercidoPagadoValidarVarios(jniName);

		while (true) {

			try {

				setHoraVerificacion();
				hoy = Calendar.getInstance();

				// Aplicar a la hora indicada en tValorSistema.cValorAplicar y
				// activo 0.

				if (horaAplicar == hoy.get(Calendar.HOUR_OF_DAY) && activo == 0) {

					// Actualizar a activo 1 Para que no ingrese de nuevo

					actualizaStatusHilo(1);
					System.out.println("Ejecutando Ejercido Pagado");
					epv.EjercidoPagadoVarios("");

					// Desactivar a la hora indicada en
					// tValorSistema.cValorDesactivar y activo 1.

				} else if (horaDesactivar == hoy.get(Calendar.HOUR_OF_DAY) && activo == 1) {

					// Actualizar a activo 0 Para que se aplique
					actualizaStatusHilo(0);
					System.out.println("Desactivando Ejercido Pagado");

				}

				Thread.sleep(interval);

			} catch (SQLException e) {
				e.printStackTrace();
				break;
			} catch (InterruptedException exc) {
				log.info("Wake up Background Process " + this.getClass().getName(), exc);
				break;
			} catch (Exception exc) {
				log.warn("Error en Background Process " + this.getClass().getName(), exc);
				break;
			}
		}
	}

	private void setHoraVerificacion() {

		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DataSourceManager.getConnection("jdbc/gestion");
			ps = con.prepareStatement("SELECT cValorDesactivar, cValorAplicar, activo FROM tValorSistema with(nolock) WHERE cParametro = 'HoraEjecutarEjercidoPagado'");
			rs = ps.executeQuery();

			while (rs.next()) {

				horaDesactivar = Integer.parseInt(rs.getString("cValorDesactivar"),10);
				horaAplicar = Integer.parseInt(rs.getString("cValorAplicar"),10);
				activo = Integer.parseInt(rs.getString("activo"),10);
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("Ocurrio un error al buscar el valor del parametro HoraEjecutarEjercidoPagado en la tabla tValorSistema");
			e.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}
			con = null;
			ps = null;
			rs = null;

		}
	}

	public void actualizaStatusHilo(int val) {

		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String sqlupdt = "UPDATE tValorSistema SET activo = " + val + " WHERE cParametro = ? ";

		try {

			conn = DataSourceManager.getConnection(jniName);
			psmt = conn.prepareStatement(sqlupdt);
			psmt.setString(1, "HoraEjecutarEjercidoPagado");
			psmt.executeUpdate();
			conn.commit();

		} catch (Exception e) {

			try {

				conn.rollback();

			} catch (SQLException e1) {

				e1.printStackTrace();
			}
			e.printStackTrace();

		} finally {

			try {

				if (conn != null)
					conn.close();
				if (psmt != null)
					psmt.close();
				if (rs != null)
					rs.close();
			} catch (SQLException exc) {

				log.warn("Cerrando conexion a base de datos", exc);
			}
			conn = null;
			psmt = null;
			rs = null;

		}
	}
}
