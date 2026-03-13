package com.syc.adquisiciones.servlet;

//import java.sql.CallableStatement;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
//import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import com.syc.adquisiciones.core.CCorreo;
///import com.syc.contable.AccountingEngine;
import com.syc.contable.AccountingEngine;
import com.syc.dsmngr.DataSourceManager;
//import com.syc.gestion.core.Caso;
//import com.syc.gestion.core.CasoManager;
//import com.syc.gestion.core.GestionException;
//import com.syc.gestion.servlet.GestionInterface;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
//import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;
import com.syc.sai.contabilidad.utils.db.CloseObject;

public class EnviaMailFechaVigenciaPrecompromisoConsolidado extends HttpServlet implements Runnable {

	private static final long	serialVersionUID	= 1L;
	private static Logger		log					= Logger.getLogger(EnviaMailFechaVigenciaPrecompromisoConsolidado.class);
	private List<CCorreo>		listCorreo;
	private List<CCorreo>		listCorreoPrecompromisoLiberado;
	private List<String>		listPRECOMLiberarPrecompromiso;
	private long				interval			= -1L;
	private volatile Thread		verificaTiempoLimiteApartado;
	private String				jniName;
	private int					horaVerificacion	= 0;																		// indica
																																// la
																																// hora
																																// a
																																// la
																																// que
																																// se
																																// realiza
																																// la
																																// verificacion
																																// (en
																																// formato
																																// de
																																// 24
																																// horas)
	private Calendar			hoy					= null;
	private Properties			moduleProperties;

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

		String strRunIntervalProcess = config.getInitParameter("runIntervalProcess");
		String strInterval = config.getInitParameter("sleepIntervalProcess");

		interval = Long.parseLong(strInterval);
		if (interval == -1L) {
			interval = 1000L * 60;
			log.info("Init Parameter \"sleepIntervalProcess\" nulo usando default \"" + interval + "\"");
		} else
			log.info("sleepIntervalProcess=" + interval);

		log.info("runIntervalProcess=" + "true".equalsIgnoreCase(strRunIntervalProcess));

		if ("true".equalsIgnoreCase(strRunIntervalProcess)) {
			log.info("Iniciando Background Process");
			verificaTiempoLimiteApartado = new Thread(this);
			verificaTiempoLimiteApartado.setPriority(Thread.MIN_PRIORITY);
			verificaTiempoLimiteApartado.start();
		}
	}

	public EnviaMailFechaVigenciaPrecompromisoConsolidado() {
		super();
	}

	public void destroy() {
		super.destroy();
	}

	public void run() {
		while (true) {

			try {

				setHoraVerificacion();
				hoy = Calendar.getInstance();
				// verifica la hora cada minuto de dispara el hilo
				// if(horaVerificacion == hoy.get(Calendar.HOUR_OF_DAY)){
				if (hoy.get(Calendar.MINUTE) == 0) {
					System.out.println(hoy.get(Calendar.MINUTE) + " " + hoy.get(Calendar.SECOND));
					// verificamos el minuto
					// if(hoy.get(Calendar.MINUTE)==0){
					if (horaVerificacion == hoy.get(Calendar.HOUR_OF_DAY)) {
						String hiloactivo = verificaHiloActivo();
						if ("false".equalsIgnoreCase(hiloactivo)) {
							// activar hilo
							actualizaStatusHilo("true");
							listCorreo = new ArrayList<CCorreo>();
							log.info("Buscando fechas vencidas");
							consultaFechasVencidasPrecompromiso();
							for (int i = 0; i < listCorreo.size(); i++) {
								log.info("Enviando correo de aviso de fechas apunto de vencer.");
								// count=i;
								enviarMail(listCorreo.get(i).getSubject(), listCorreo.get(i).getBody(), listCorreo.get(i).getTo(), listCorreo.get(i).getFolioPrecom(), listCorreo.get(i).getConsecutivoPrecom());
							}

							for (int i = 0; i < listCorreoPrecompromisoLiberado.size(); i++) {
								log.info("Enviando correo de aviso de apartado liberado.");
								String res = liberaPrecompromiso(listPRECOMLiberarPrecompromiso.get(i).toString());
								if (res == "CANCELADO CONTABLEMENTE") {
									enviarMail(listCorreoPrecompromisoLiberado.get(i).getSubject(), listCorreoPrecompromisoLiberado.get(i).getBody(), listCorreoPrecompromisoLiberado.get(i).getTo(), listCorreoPrecompromisoLiberado.get(i).getFolioPrecom(), listCorreoPrecompromisoLiberado.get(i)
										.getConsecutivoPrecom());
								} else {
									enviarMail(listCorreoPrecompromisoLiberado.get(i).getSubject(), "NO SE PUDO LIBERAR EL PRECOMPROMISO  DEL CONSOLIDADO " + listPRECOMLiberarPrecompromiso.get(i).toString() + " AVISE AL ADMINISTRADOR", listCorreoPrecompromisoLiberado.get(i).getTo(),
										listCorreoPrecompromisoLiberado.get(i).getFolioPrecom(), listCorreoPrecompromisoLiberado.get(i).getConsecutivoPrecom());
								}

								log.info("Email enviado");

							}

							actualizaStatusHilo("false");
							// se duerme 1minuto
							Thread.sleep(interval);

						} else {
							Thread.sleep(interval);
						}

					} else {
						Thread.sleep(interval);

					}

				} else {
					Thread.sleep(interval);
				}

			} catch (InterruptedException exc) {
				log.info("Wake up Background Process " + this.getClass().getName(), exc);
			} catch (Exception exc) {
				log.warn("Error en Background Process " + this.getClass().getName(), exc);
				break;
			}
		}

		verificaTiempoLimiteApartado = null;

	}

	private synchronized void enviarMail(String subject, String body, String to, String folioPrecom, int consecutivoPrecom) {
		boolean correoEnviado = false;
		String errorMsg = "";
		try {

			moduleProperties = ModuleProperties("mca");

			Properties props = new Properties();
			props.setProperty("mail.transport.protocol", "smtp");
			props.setProperty("mail.smtp.host", moduleProperties.getProperty("mca.smtphost"));
			props.setProperty("mail.smtp.port", moduleProperties.getProperty("mca.smtpport"));
			props.setProperty("mail.smtp.auth", moduleProperties.getProperty("mca.smtpauth"));
			final String user = moduleProperties.getProperty("mca.smtpuser");
			final String pass = moduleProperties.getProperty("mca.smtppasswd");

			Authenticator auth = new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(user, pass);
				}
			};

			Session session = Session.getDefaultInstance(props, auth);
			// session.setDebug(true);
			Transport transport = session.getTransport();

			MimeMessage message = new MimeMessage(session);
			message.setSubject(subject);
			message.setFrom(new InternetAddress(moduleProperties.getProperty("mca.smtpfrom")));

			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setContent(body, "text/html");
			Multipart mp = new MimeMultipart();
			mp.addBodyPart(textPart);

			message.setContent(mp);
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.saveChanges();
			transport.connect();
			transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
			transport.close();
			correoEnviado = true;
			log.info("Email enviado");
		} catch (NoSuchProviderException exc) {
			exc.printStackTrace(System.out);
			errorMsg = exc.toString();
			// throw new GestionException(exc.getMessage());
		} catch (MessagingException exc) {
			exc.printStackTrace(System.out);
			errorMsg = exc.toString();

		} catch (SQLException exc) {
			exc.printStackTrace(System.out);
			errorMsg = exc.toString();
			// throw new GestionException(exc.getMessage());

		} finally {
			if (!correoEnviado)

			{

				try {

					insertaCorreo(subject, to, 0, folioPrecom, consecutivoPrecom, errorMsg);

				} catch (Exception e) {
					log.error("ATENCION!!!\nNo fue posible guardar el correo. Causa: " + e.toString(), e);

				}
			} else {
				try {
					insertaCorreo(subject, to, 1, folioPrecom, consecutivoPrecom, "");
				} catch (Exception e) {
					log.error("ATENCION!!!\nNo fue posible guardar el correo. Causa: " + e.toString(), e);

				}

			}

		}

	}

	private void setHoraVerificacion() {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DataSourceManager.getConnection("jdbc/gestion");
			ps = con.prepareStatement("SELECT cValor FROM mSistema with(nolock) WHERE cParametro='mHoraRevisarFechaVigenciaPrecompromiso'");
			rs = ps.executeQuery();

			while (rs.next()) {
				horaVerificacion = Integer.parseInt(rs.getString("cValor"));
			}
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("Ocurrio un error al buscar el valor del parametro mHoraRevisarFechaVigenciaPrecompromiso en la tabla mSistema");
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

	private void insertaCorreo(String subject, String to, int status, String c_folio_pre, int consecutivoPrecom, String error) {
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = DataSourceManager.getConnection("jdbc/gestion");
			ps = con
				.prepareStatement("insert into mCorreoPrecompromisoVigencia (cEjercicio,cIdConsolidado,C_FOLIO_PRE,ConsecutivoPRECOMP,cIdUsuarioCreacion,fEnvio,fRecepcion,estatusCorreo,error,cIdUnidadEjecutora)values((select cvalor from mSistema with(nolock) where cParametro='cEjercicio'),?,?,?,?,GETDATE(),GETDATE(),?,?,?)");

			// ps.setString(1, cEjercicio);
			ps.setString(1, subject);
			ps.setString(2, c_folio_pre);
			ps.setInt(3, consecutivoPrecom);
			ps.setString(4, to);
			ps.setInt(5, status);
			ps.setString(6, error);
			ps.setString(7, c_folio_pre.substring(5, 8));
			ps.executeUpdate();
			con.commit();

		}

		catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("Ocurrio un error al insertar un valor en la tabla mCorreoPrecompromisoVigencia ");
			e.printStackTrace();
		}

		finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			con = null;
			ps = null;
			rs = null;

		}

	}

	private synchronized void consultaFechasVencidasPrecompromiso() {
		listCorreo = new ArrayList<CCorreo>();
		listCorreoPrecompromisoLiberado = new ArrayList<CCorreo>();
		listPRECOMLiberarPrecompromiso = new ArrayList<String>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = DataSourceManager.getConnection("jdbc/gestion");
			ps = con.prepareStatement("SELECT cIdConsolidado,diasVencer,u_email,C_FOLIO_PRE,ConsecutivoPRECOMP FROM fn_mConsultaFechasPrecompromisoVencidas()");
			rs = ps.executeQuery();

			while (rs.next()) {
				if (Integer.parseInt(rs.getString("diasVencer")) > 0)
					listCorreo.add(new CCorreo(rs.getString("u_email"), rs.getString("cIdConsolidado"), "El Precompromiso del Consolidado " + rs.getString("cIdConsolidado") + " esta a " + rs.getString("diasVencer") + " días de vencer.", rs.getString("C_FOLIO_PRE"), rs.getInt("ConsecutivoPRECOMP")));
				if (Integer.parseInt(rs.getString("diasVencer")) == 0)
					listCorreo.add(new CCorreo(rs.getString("u_email"), rs.getString("cIdConsolidado"), "El Precompromiso del Consolidado " + rs.getString("cIdConsolidado") + " vence el día de hoy.", rs.getString("C_FOLIO_PRE"), rs.getInt("ConsecutivoPRECOMP")));

				if (Integer.parseInt(rs.getString("diasVencer")) < 0) {
					listCorreoPrecompromisoLiberado.add(new CCorreo(rs.getString("u_email"), rs.getString("cIdConsolidado"), "El Precompromiso del Consolidado " + rs.getString("cIdConsolidado") + " se liberó automaticamente ya que la fecha de vigencia expiró.", rs.getString("C_FOLIO_PRE"), rs
						.getInt("ConsecutivoPRECOMP")));
					listPRECOMLiberarPrecompromiso.add(rs.getString("cIdConsolidado").toString());
				}

			}

		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			log.error("Ocurrio un error al buscar las fechas vencidas de CP");
			e.printStackTrace();
		}

		finally {
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

	public Properties ModuleProperties(String modulePrefix) throws SQLException {
		Connection conn = null;
		String query = "SELECT cparametro, cvalor FROM   mSistema with(nolock) WHERE   cparametro LIKE ? + '%' ";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Properties moduleProperties = new Properties();

		try {
			conn = DataSourceManager.getConnection("jdbc/gestion");
			ps = conn.prepareStatement(query);
			ps.setString(1, modulePrefix);
			rs = ps.executeQuery();
			while (rs.next()) {
				String propName = rs.getString("cparametro");
				String propValue = rs.getString("cvalor");
				if (moduleProperties.contains(propName))
					moduleProperties.setProperty(propName, moduleProperties.getProperty(propName) + "|" + propValue);
				else
					moduleProperties.setProperty(propName, propValue);

			}
		} finally {
			try {
				CloseObject.closeObject(rs, false);
				CloseObject.closeObject(ps, false);
				CloseObject.closeObject(conn, false);
			} catch (Exception e) {
				log.warn(e);
			}
		}
		return moduleProperties;
	}

	private synchronized String liberaPrecompromiso(String cIdConsolidado) {
		String mensaje = "";
		PreparedStatement ps = null;
		ResultSet rs = null;
		Connection conn = null;
		CallableStatement csmt = null;
		AccountingEngine accEng = new AccountingEngine();
		accEng.setValidaInsuficienciaDeSaldo(true);
		String temFolio = "";

		try {
			conn = DataSourceManager.getConnection(jniName);
			ps = conn.prepareStatement("select consecutivoPRECOM from fn_mConsultaFechasPrecompromisoVencidas()	where  cIdConsolidado='" + cIdConsolidado + "' group by consecutivoPRECOM");
			rs = ps.executeQuery();
			while (rs.next()) {
				String folioPrecompromisoCancel = String.valueOf(rs.getInt(1));
				if (temFolio != folioPrecompromisoCancel) {
					temFolio = folioPrecompromisoCancel;
					accEng.cancelAccountingApplication(conn, "PRECOMMATERIALES", folioPrecompromisoCancel, "tPrecomMaterialesEncabezado", "tPrecomMaterialesDetalle", "nFolioPrecomMateriales");
					// avanza caso y actualiza solicitud para que puedan
					// reutilizar la requisicion y no genere el mismo folio de
					// apartado,revisando si la solicitud tiene un consolidado
					csmt = conn.prepareCall("{ call pa_actualizaVigenciaRequisicionesPrecompromisoCancelado (?)}");
					csmt.setString(1, cIdConsolidado);
					csmt.execute();
					mensaje = "CANCELADO CONTABLEMENTE";
				}

			}
			conn.commit();

		} catch (Exception e) {
			log.error("Error en Aplicacion contable:" + e.getMessage());
			mensaje = "ERROR INESPERADO DE LA CANCELACION CONTABLE";
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}

		} finally {
			try {
				if (conn != null)
					conn.close();
				if (ps != null)
					ps.close();
				if (rs != null)
					rs.close();
				if (csmt != null)
					csmt.close();

			} catch (SQLException exc) {
				log.warn("Cerrando conexion a base de datos", exc);
			}

			conn = null;
			rs = null;
			ps = null;
			csmt = null;

		}

		return mensaje;
	}

	public String verificaHiloActivo() {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String HiloActivo = "";
		// consulta valor para saber si ya existe un hilo activo
		String sql = "select cvalor from mSistema with(nolock) where cParametro=?";

		try {
			conn = DataSourceManager.getConnection(jniName);
			psmt = conn.prepareStatement(sql);
			psmt.setString(1, "Hilos_Apartado");
			rs = psmt.executeQuery();
			if (rs.next())
				HiloActivo = rs.getString("cvalor");
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

		return HiloActivo;

	}

	public void actualizaStatusHilo(String val) {
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		String sqlupdt = "update msistema set cValor='" + val + "' where cParametro=?";

		try {
			conn = DataSourceManager.getConnection(jniName);
			psmt = conn.prepareStatement(sqlupdt);
			psmt.setString(1, "Hilos_Apartado");
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
