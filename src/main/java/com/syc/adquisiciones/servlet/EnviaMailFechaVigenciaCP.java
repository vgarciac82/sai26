package com.syc.adquisiciones.servlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import com.syc.adquisiciones.core.CCorreo;
import com.syc.contable.AccountingEngine;
import com.syc.dsmngr.DataSourceManager;
import com.syc.sai.contabilidad.utils.db.CloseObject;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "EnviaMailFechaVigenciaCP", urlPatterns = { "/servlet/EnviaMailFechaVigenciaCP" })
public class EnviaMailFechaVigenciaCP extends HttpServlet implements Runnable {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(EnviaMailFechaVigenciaCP.class);

    private List<CCorreo> listCorreo;

    private List<CCorreo> listCorreoApartadoLiberado;

    private List<String> listRQLiberarApartado;

    private long interval = -1L;

    private volatile Thread verificaTiempoLimiteApartado;

    private String jniName;

    //indica la hora a la que se realiza la verificacion (en formato de 24 horas)
    private int horaVerificacion = 0;

    private Calendar hoy = null;

    private Properties moduleProperties;

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

    public EnviaMailFechaVigenciaCP() {
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
                //verifica la hora cada minuto de dispara el hilo
                if (horaVerificacion == hoy.get(Calendar.HOUR_OF_DAY)) {
                    String hiloactivo = verificaHiloActivo();
                    if ("false".equalsIgnoreCase(hiloactivo)) {
                        //activar hilo
                        actualizaStatusHilo("true");
                        listCorreo = new ArrayList<CCorreo>();
                        log.info("Buscando fechas vencidas");
                        consultaFechasVencidasCP();
                        for (int i = 0; i < listCorreo.size(); i++) {
                            log.info("Enviando correo de aviso de fechas apunto de vencer.");
                            enviarMail(listCorreo.get(i).getSubject(), listCorreo.get(i).getBody(), listCorreo.get(i).getTo());
                            log.info("Email enviado");
                        }
                        for (int i = 0; i < listCorreoApartadoLiberado.size(); i++) {
                            log.info("Enviando correo de aviso de apartado liberado.");
                            String res = liberaApartadoLineas(listRQLiberarApartado.get(i).toString());
                            if (res == "CANCELADO CONTABLEMENTE") {
                                enviarMail(listCorreoApartadoLiberado.get(i).getSubject(), listCorreoApartadoLiberado.get(i).getBody(), listCorreoApartadoLiberado.get(i).getTo());
                            } else {
                                enviarMail(listCorreoApartadoLiberado.get(i).getSubject(), "NO SE PUDO LIBERAR EL APARTADO  DE LA REQUISICION " + listRQLiberarApartado.get(i).toString() + " AVISE AL ADMINISTRADOR", listCorreoApartadoLiberado.get(i).getTo());
                            }
                            log.info("Email enviado");
                        }
                        actualizaStatusHilo("false");
                        //se duerme 1hora
                        Thread.sleep(interval);
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

    private void enviarMail(String subject, String body, String to) {
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
            //session.setDebug(true);
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
            //fis.close();
        } catch (Exception e) {
            log.error("Ocurrio un error al crear la sesion");
            e.printStackTrace();
        }
    }

    private void setHoraVerificacion() throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DataSourceManager.getConnection("jdbc/gestion");
            ps = con.prepareStatement("SELECT cValor FROM mSistema WHERE cParametro='mHoraRevisarFechaVigenciaCP'");
            rs = ps.executeQuery();
            while (rs.next()) {
                horaVerificacion = Integer.parseInt(rs.getString("cValor"));
            }
        } catch (SQLException e) {
            log.error("Ocurrio un error al buscar el valor del parametro mHoraRevisarFechaVigenciaCP en la tabla mSistema");
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (rs != null) {
                rs.close();
            }
            con = null;
            ps = null;
            rs = null;
        }
    }

    private void consultaFechasVencidasCP() throws SQLException {
        listCorreo = new ArrayList<CCorreo>();
        listCorreoApartadoLiberado = new ArrayList<CCorreo>();
        listRQLiberarApartado = new ArrayList<String>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DataSourceManager.getConnection("jdbc/gestion");
            ps = con.prepareStatement("SELECT cIdsolicitud,diasVencer,u_email FROM fn_mConsultaFechasApartadoVencidas()");
            rs = ps.executeQuery();
            while (rs.next()) {
                if (Integer.parseInt(rs.getString("diasVencer")) > 0)
                    listCorreo.add(new CCorreo(rs.getString("u_email"), rs.getString("cIdSolicitud"), "El apartado de la requisición " + rs.getString("cIdSolicitud") + " esta a " + rs.getString("diasVencer") + " días de vencer."));
                if (Integer.parseInt(rs.getString("diasVencer")) == 0)
                    listCorreo.add(new CCorreo(rs.getString("u_email"), rs.getString("cIdSolicitud"), "El apartado de la requisición " + rs.getString("cIdSolicitud") + " vence el día de hoy."));
                if (Integer.parseInt(rs.getString("diasVencer")) < 0) {
                    listCorreoApartadoLiberado.add(new CCorreo(rs.getString("u_email"), rs.getString("cIdSolicitud"), "El apartado de la requisición " + rs.getString("cIdSolicitud") + " se liberó automaticamente ya que la fecha de vigencia expiró."));
                    listRQLiberarApartado.add(rs.getString("cIdSolicitud").toString());
                }
            }
            ps.close();
            con.close();
            rs.close();
        } catch (SQLException e) {
            log.error("Ocurrio un error al buscar las fechas vencidas de CP");
            e.printStackTrace();
        } finally {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
            if (con != null)
                con.close();
            ps = null;
            rs = null;
            con = null;
        }
    }

    public Properties ModuleProperties(String modulePrefix) throws Exception {
        Connection conn = null;
        String query = "SELECT cparametro, cvalor FROM   mSistema WHERE   cparametro LIKE ? + '%' ";
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

    private synchronized String liberaApartadoLineas(String cIdSolicitud) {
        String mensaje = "";
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        CallableStatement csmt = null;
        AccountingEngine accEng = new AccountingEngine();
        accEng.setValidaInsuficienciaDeSaldo(false);
        String temFolio = "";
        try {
            conn = DataSourceManager.getConnection(jniName);
            ps = conn.prepareStatement("select folio from fn_consultadeApartadosReporte ()	where  cIdSolicitud='" + cIdSolicitud + "'	and ctipodocumento='APARTADO' group by folio");
            rs = ps.executeQuery();
            while (rs.next()) {
                String folioApartadoCancel = String.valueOf(rs.getInt(1));
                if (temFolio != folioApartadoCancel) {
                    temFolio = folioApartadoCancel;
                    accEng.cancelAccountingApplication(conn, "APARTADO", folioApartadoCancel, "tApartadoEncabezado", "tApartadoDetalle", "nFolioApartado");
                    //avanza caso y actualiza solicitud para que puedan reutilizar la requisicion y no genere el mismo folio de apartado,revisando si la solicitud tiene un consolidado
                    csmt = conn.prepareCall("{ call pa_actualizaRequisicionLiberaApartado (?)}");
                    csmt.setString(1, cIdSolicitud);
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
        //consulta valor para saber si ya existe un hilo activo
        String sql = "select cvalor from mSistema where cParametro=?";
        try {
            conn = DataSourceManager.getConnection(jniName);
            psmt = conn.prepareStatement(sql);
            psmt.setString(1, "Hilos_Apartado");
            rs = psmt.executeQuery();
            if (rs.next())
                HiloActivo = rs.getString("cvalor");
        } catch (Exception e) {
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
