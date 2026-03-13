package com.syc.adquisiciones.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import com.syc.adquisiciones.core.CCorreo;
import com.syc.dsmngr.DataSourceManager;
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
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.WebServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebServlet(name = "EnviaMailFechaProcedimiento", urlPatterns = { "/servlet/EnviaMailFechaProcedimiento" })
public class EnviaMailFechaProcedimiento extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private HiloVerificacionFechasProcedimiento hvfp;

    private static Logger log = LoggerFactory.getLogger(EnviaMailFechaProcedimiento.class);

    public EnviaMailFechaProcedimiento() {
        super();
    }

    public void destroy() {
        super.destroy();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int valor = Integer.parseInt(request.getParameter("cValorTemp").toString());
        if (valor == 1) {
            iniciarHilo();
        } else {
            finalizarHilo();
        }
    }

    public void init() throws ServletException {
        // Put your code here
    }

    public void iniciarHilo() {
        if (verificaEjecucionProceso()) {
            hvfp = new HiloVerificacionFechasProcedimiento();
            hvfp.setName("hiloVerificacionFechasProcedimiento");
            log.info("Iniciando proceso de verificacion de fechas de procedimiento vencidas");
            hvfp.start();
        }
    }

    public void finalizarHilo() {
        try {
            log.info("Finalizando proceso de verificacion de fechas de procedimiento vencidas");
            hvfp.stop();
            log.info("Finalizo proceso de verificacion de fechas correctamente");
        } catch (Exception e) {
            log.error("Ocurrio un error al finalizar el proceso de verificacion de fechas vencidas");
            e.printStackTrace();
        }
    }

    private Boolean verificaEjecucionProceso() {
        Boolean verifica;
        try {
            Connection con = DataSourceManager.getConnection("jdbc/gestion");
            PreparedStatement pstm = con.prepareStatement("SELECT cValor FROM mSistema WHERE cParametro='mEnviarMailFechasProcedimiento' AND cValor='1'");
            ResultSet rs = pstm.executeQuery();
            if (rs.next())
                verifica = Boolean.TRUE;
            else
                verifica = Boolean.FALSE;
            pstm.close();
            con.close();
            return verifica;
        } catch (SQLException e) {
            return Boolean.FALSE;
        }
    }

    /*Clase privada que genera el hilo para la verificacion de las fechas vencidas*/
    private class HiloVerificacionFechasProcedimiento extends Thread {

        //indica la hora a la que se realiza la verificacion (en formato de 24 horas)
        private int horaVerificacion = 0;

        private Calendar hoy = null;

        private Boolean verifico = Boolean.FALSE;

        private List<CCorreo> listCorreo;

        public void run() {
            setHoraVerificacion();
            try {
                while (true) {
                    //espera 1 minuto
                    Thread.sleep(10000);
                    hoy = Calendar.getInstance();
                    //verifica la hora
                    if (horaVerificacion == hoy.get(Calendar.HOUR_OF_DAY)) {
                        if (!verifico) {
                            verifico = Boolean.TRUE;
                            listCorreo = new ArrayList<CCorreo>();
                            log.info("Buscando fechas de procedimiento vencidas");
                            listCorreo = consultaFechasProcedimientoVencidas();
                            for (int i = 0; i < listCorreo.size(); i++) {
                                log.info("Enviando correo");
                                enviarMail(listCorreo.get(i).getSubject(), listCorreo.get(i).getBody(), listCorreo.get(i).getTo());
                                log.info("Email enviado");
                                //Correo.envia(listCorreo.get(i).getSesion(), listCorreo.get(i).getTo(), listCorreo.get(i).getFrom(), listCorreo.get(i).getSubject(), listCorreo.get(i).getBody());
                            }
                        }
                    } else {
                        verifico = Boolean.FALSE;
                    }
                }
            } catch (Exception e) {
                log.error("Error al enviar mail de fechas vencidas");
            }
        }

        private void enviarMail(String subject, String body, String to) {
            try {
                Properties datosEnvio = new Properties();
                FileInputStream fis = new FileInputStream(getServletContext().getRealPath("WEB-INF" + File.separator + "mail-fechas-vencidas" + File.separator + "properties.props"));
                datosEnvio.load(fis);
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "smtp");
                props.setProperty("mail.smtp.host", datosEnvio.getProperty("smtphost"));
                props.setProperty("mail.smtp.port", datosEnvio.getProperty("smtpport"));
                props.setProperty("mail.smtp.auth", datosEnvio.getProperty("smtpauth"));
                final String user = datosEnvio.getProperty("smtpuser");
                final String pass = datosEnvio.getProperty("smtppasswd");
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
                message.setFrom(new InternetAddress(datosEnvio.getProperty("smtpfrom")));
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
                fis.close();
            } catch (Exception e) {
                log.error("Ocurrio un error al crear la sesion");
                e.printStackTrace();
            }
        }

        private void setHoraVerificacion() {
            try {
                Connection con = DataSourceManager.getConnection("jdbc/gestion");
                PreparedStatement ps = con.prepareStatement("SELECT cValor FROM mSistema WHERE cParametro='mHoraRevisarFechasVencidas'");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    horaVerificacion = Integer.parseInt(rs.getString("cValor"));
                }
            } catch (SQLException e) {
                log.error("Ocurrio un error al buscar el valor del parametro mHoraRevisarFechasVencidas en la tabla mSistema");
                e.printStackTrace();
            }
        }

        private List<CCorreo> consultaFechasProcedimientoVencidas() {
            List<CCorreo> listCorreo = new ArrayList<CCorreo>();
            try {
                Connection con = DataSourceManager.getConnection("jdbc/gestion");
                PreparedStatement ps = con.prepareStatement("SELECT * FROM fn_mConsultaFechasProcedimientoVencidas()");
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    listCorreo.add(new CCorreo(rs.getString("u_email"), rs.getString("cIdProcedimiento"), "Para el procedimiento " + rs.getString("cIdProcedimiento") + " la " + rs.getString("cDescripcion") + " " + rs.getString("fecha") + " esta a " + rs.getString("diasVencer") + " dias de vencer."));
                }
                ps.close();
                con.close();
            } catch (SQLException e) {
                log.error("Ocurrio un error al buscar las fechas de los procedimientos vencidas");
                e.printStackTrace();
            }
            return listCorreo;
        }
    }
}
