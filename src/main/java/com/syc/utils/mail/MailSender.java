package com.syc.utils.mail;

import java.io.FileInputStream;
import java.util.Properties;
import javax.mail.Address;
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
import org.apache.commons.lang.StringUtils;
import com.syc.gestion.core.CorreosPendientesBean;
import com.syc.gestion.core.GestionException;
import com.syc.gestion.servlet.GestionInterface;
import com.syc.obrapublica.ConfiguraAplicativoBusinessLogic;
import com.syc.utils.ModuleProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MailSender {

    private static Logger log = LoggerFactory.getLogger(MailSender.class);

    public static void sendMail(String moduleName, MessageComposer messageComposer) throws Exception {
        ModuleProperties moduleProperties = new ModuleProperties(moduleName);
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", moduleProperties.getProperty("smtphost"));
        props.setProperty("mail.smtp.port", moduleProperties.getProperty("smtpport"));
        props.setProperty("mail.smtp.auth", moduleProperties.getProperty("smtpauth"));
        final String user = moduleProperties.getProperty("smtpuser");
        final String pass = moduleProperties.getProperty("smtppasswd");
        Authenticator auth = new Authenticator() {

            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        };
        Session session = Session.getDefaultInstance(props, auth);
        session.setDebug(true);
        Transport transport = session.getTransport();
        MimeMessage message = new MimeMessage(session);
        message.setSubject(moduleProperties.getProperty("mail.subject"));
        message.setFrom(new InternetAddress(moduleProperties.getProperty("smtpfrom")));
        String msg = messageComposer.composeMessage();
        if ("".equals(msg))
            return;
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(msg, "text/html");
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(textPart);
        message.setContent(mp);
        String[] destStr = moduleProperties.getCompositeProperty("mail.to");
        Address[] dest = new Address[destStr.length];
        for (int i = 0; i < destStr.length; i++) dest[i] = new InternetAddress(destStr[i]);
        message.addRecipients(Message.RecipientType.TO, dest);
        message.saveChanges();
        transport.connect();
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    public static void sendMail(String moduleName, String to, String messageTxt, String subject) throws Exception {
        if (to == null || "".equals(to) || messageTxt == null || "".equals(messageTxt))
            return;
        ModuleProperties moduleProperties = new ModuleProperties(moduleName);
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", moduleProperties.getProperty("smtphost"));
        props.setProperty("mail.smtp.port", moduleProperties.getProperty("smtpport"));
        props.setProperty("mail.smtp.auth", moduleProperties.getProperty("smtpauth"));
        final String user = moduleProperties.getProperty("smtpuser");
        final String pass = moduleProperties.getProperty("smtppasswd");
        Authenticator auth = new Authenticator() {

            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        };
        Session session = Session.getDefaultInstance(props, auth);
        session.setDebug(true);
        Transport transport = session.getTransport();
        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject == null || "".equals(subject) ? moduleProperties.getProperty("mail.subject") : subject);
        message.setFrom(new InternetAddress(moduleProperties.getProperty("smtpfrom")));
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(messageTxt, "text/html");
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(textPart);
        message.setContent(mp);
        InternetAddress dest = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, dest);
        message.saveChanges();
        transport.connect();
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    public static void enviaCorreoCNF(String to, String messageTxt, String subject) throws Exception {
        String asuntoCorreo = subject;
        Session session = null;
        try {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            String host = cabl.getSystemSetting("MAIL;HOST");
            String protocol = cabl.getSystemSetting("MAIL;PROTOCOL");
            String port = cabl.getSystemSetting("MAIL;PORT");
            String localhost = cabl.getSystemSetting("MAIL;LOCALHOST");
            String from = cabl.getSystemSetting("MAIL;FROM");
            log.debug("Cofiguracion de alerta: PROTOCOL[" + protocol + "] HOST[" + host + "] PORT[" + port + "] LOCALHOST[" + localhost + "] FROM[" + from + "]");
            Properties props = new Properties();
            props.put("mail.transport.protocol", protocol);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.localhost", localhost);
            props.put("mail.smtp.from", from);
            props.put("mail.smtp.allow8bitmime", "true");
            props.put("mail.debug", "true");
            session = Session.getInstance(props);
            session.setDebug(true);
            MimeMessage msg = new MimeMessage(session);
            if (StringUtils.isEmpty(asuntoCorreo))
                asuntoCorreo = "Notificacion pendiente SAI.";
            msg.setSubject(asuntoCorreo);
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(messageTxt, "text/html");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            msg.setContent(mp);
            if (to.startsWith(";"))
                to = to.substring(1);
            String[] destinatarios = to.split(";");
            for (int i = 0; i < destinatarios.length; i++) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatarios[i]));
            }
            msg.saveChanges();
            Transport.send(msg);
            log.info("EMAIL TERMINO");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new GestionException(exc.getMessage());
        } finally {
            session = null;
        }
    }

    public static void sendMailMultipleRecipients(String moduleName, String[] to, String messageTxt, String subject) throws Exception {
        if (to == null || "".equals(to) || messageTxt == null || "".equals(messageTxt))
            return;
        ModuleProperties moduleProperties = new ModuleProperties(moduleName);
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", moduleProperties.getProperty("smtphost"));
        props.setProperty("mail.smtp.port", moduleProperties.getProperty("smtpport"));
        props.setProperty("mail.smtp.auth", moduleProperties.getProperty("smtpauth"));
        final String user = moduleProperties.getProperty("smtpuser");
        final String pass = moduleProperties.getProperty("smtppasswd");
        Authenticator auth = new Authenticator() {

            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, pass);
            }
        };
        Session session = Session.getDefaultInstance(props, auth);
        session.setDebug(true);
        Transport transport = session.getTransport();
        MimeMessage message = new MimeMessage(session);
        message.setSubject(subject == null || "".equals(subject) ? moduleProperties.getProperty("mail.subject") : subject);
        message.setFrom(new InternetAddress(moduleProperties.getProperty("smtpfrom")));
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setContent(messageTxt, "text/html");
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(textPart);
        message.setContent(mp);
        Address[] dest = new Address[to.length];
        for (int i = 0; i < to.length; i++) dest[i] = new InternetAddress(to[i]);
        message.addRecipients(Message.RecipientType.TO, dest);
        message.saveChanges();
        transport.connect();
        transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
        transport.close();
    }

    public static void enviaCorreoCNF(CorreosPendientesBean cpb, String prefixPath) throws Exception {
        String asuntoCorreo = cpb.getSubject() == null ? "" : cpb.getSubject();
        Session session = null;
        try {
            ConfiguraAplicativoBusinessLogic cabl = new ConfiguraAplicativoBusinessLogic(GestionInterface.ATT_CONEXION);
            String host = cabl.getSystemSetting("MAIL;HOST");
            String protocol = cabl.getSystemSetting("MAIL;PROTOCOL");
            String port = cabl.getSystemSetting("MAIL;PORT");
            String localhost = cabl.getSystemSetting("MAIL;LOCALHOST");
            String from = cabl.getSystemSetting("MAIL;FROM");
            log.debug("Cofiguracion de alerta: PROTOCOL[" + protocol + "] HOST[" + host + "] PORT[" + port + "] LOCALHOST[" + localhost + "] FROM[" + from + "]");
            Properties props = new Properties();
            props.put("mail.transport.protocol", protocol);
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", port);
            props.put("mail.smtp.localhost", localhost);
            props.put("mail.smtp.from", from);
            props.put("mail.smtp.allow8bitmime", "true");
            props.put("mail.debug", "true");
            session = Session.getInstance(props);
            session.setDebug(true);
            MimeMessage msg = new MimeMessage(session);
            if (StringUtils.isEmpty(asuntoCorreo))
                asuntoCorreo = "Notificacion pendiente SAI.";
            msg.setSubject(asuntoCorreo);
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(cpb.getMensaje(), "text/html");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            msg.setContent(mp);
            String[] destinatarios = cpb.getDestinatario().split(";");
            for (int i = 0; i < destinatarios.length; i++) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatarios[i]));
            }
            msg.saveChanges();
            Transport.send(msg);
            log.info("EMAIL TERMINO");
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            throw new GestionException(exc.getMessage());
        } finally {
            session = null;
        }
    }

    public static boolean enviaCorreo(CorreosPendientesBean cpb, String prefixPath) throws GestionException {
        boolean correoEnviado = false;
        try {
            Properties datosEnvio = new Properties();
            FileInputStream fis = new FileInputStream(prefixPath + "properties.props");
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
            session.setDebug(log.isDebugEnabled());
            Transport transport = session.getTransport();
            MimeMessage message = new MimeMessage(session);
            message.setSubject(cpb.getSubject() == null || "".equals(cpb.getSubject()) ? datosEnvio.getProperty("mail.subject") : cpb.getSubject());
            message.setFrom(new InternetAddress(datosEnvio.getProperty("smtpfrom")));
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(cpb.getMensaje(), "text/html");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            message.setContent(mp);
            InternetAddress dest = new InternetAddress(cpb.getDestinatario());
            message.addRecipient(Message.RecipientType.TO, dest);
            message.saveChanges();
            transport.connect();
            transport.sendMessage(message, message.getRecipients(Message.RecipientType.TO));
            transport.close();
            System.out.println("EMAIL TERMINO");
            // Si llega a esta linea es por que no genero alguna excepcion al
            // enviar el correo.
            correoEnviado = true;
        } catch (Exception exc) {
            log.error(exc.getMessage(), exc);
            correoEnviado = false;
        }
        return correoEnviado;
    }
}
