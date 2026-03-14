package com.syc.sai.procesosAutomaticos;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import com.syc.gestion.core.GestionException;

public class EmailTester {

    public static Properties readPropertiesFile(String filePath) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            properties.load(fileInputStream);
        }
        return properties;
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Debe proporcionar al menos un archivo de configuración.");
            return;
        }
        for (String configFileName : args) {
            try {
                Properties mailProperties = readPropertiesFile(configFileName);
                for (int i = 0; i < 5; i++) {
                    String to = "framirez@conafor.gob.mx;amador.marroquin@conafor.gob.mx;vgarciac@axtel.com.mx";
                    String subject = "Correo de Prueba " + (i + 1) + " de 5";
                    String messageTxt = "Solo es un correo de prueba";
                    enviaCorreoCNF(to, messageTxt, subject, mailProperties);
                    int waitTime = new Random().nextInt(5) + 1;
                    TimeUnit.SECONDS.sleep(waitTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void enviaCorreoCNF(String to, String messageTxt, String subject, Properties config) throws Exception {
        String asuntoCorreo = subject;
        Session session = null;
        try {
            String host = config.getProperty("MAIL;HOST");
            String protocol = config.getProperty("MAIL;PROTOCOL");
            String port = config.getProperty("MAIL;PORT");
            String localhost = config.getProperty("MAIL;LOCALHOST");
            String from = config.getProperty("MAIL;FROM");
            System.out.println("Configuración de alerta: PROTOCOL[" + protocol + "] HOST[" + host + "] PORT[" + port + "] LOCALHOST[" + localhost + "] FROM[" + from + "]");
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDateAndTime = sdf.format(new Date());
            msg.setSubject(asuntoCorreo + " - Enviado a las: " + currentDateAndTime);
            messageTxt += "<br><br>Enviado a las: " + currentDateAndTime;
            messageTxt += "<br>IP del servidor relay: " + host;
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setContent(messageTxt, "text/html");
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            msg.setContent(mp);
            String[] destinatarios = to.split(";");
            for (String destinatario : destinatarios) {
                msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatario));
            }
            msg.saveChanges();
            Transport.send(msg);
            System.out.println("EMAIL TERMINO");
        } catch (Exception exc) {
            System.out.println("Error enviando correo: " + exc);
            exc.printStackTrace();
            throw new GestionException(exc.getMessage());
        } finally {
            session = null;
        }
    }
}
