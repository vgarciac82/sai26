package com.syc.sai.procesosAutomaticos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Base64;

public class MailTestCNF {

    private static final Logger log = LoggerFactory.getLogger(MailTestCNF.class);

    public static void main(String[] args) throws InterruptedException {
        String asuntoCorreo = "Prueba de envio correos: ";
        Session session = null;
        String host = "10.254.253.1";
        String protocol = "smtp";
        String port = "2525";
        String localhost = "cnf.gob.mx";
        String from = "sai@cnf.gob.mx";
        log.debug("Object: {}", "Cofiguracion de alerta: PROTOCOL[" + protocol + "] HOST[" + host + "] PORT[" + port + "] LOCALHOST[" + localhost + "] FROM[" + from + "]");
        int iMail = 0;
        while (iMail < 5) {
            try {
                Properties props = new Properties();
                props.put("mail.transport.protocol", protocol);
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.port", port);
                props.put("mail.smtp.localhost", localhost);
                props.put("mail.smtp.from", from);
                props.put("mail.smtp.allow8bitmime", "true");
                props.put("mail.debug", "true");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
                String fechaActual = sdf.format(new Date());
                System.out.println("Fecha actual: " + fechaActual);
                session = Session.getInstance(props);
                session.setDebug(true);
                MimeMessage msg = new MimeMessage(session);
                msg.setSubject(asuntoCorreo + " " + (iMail + 1) + " de 5 Fecha: " + fechaActual);
                String body = "<html><body><h1>Saludos!</h1><br>Correo de prueba<br>" + "Este correo se envio el " + fechaActual + " <br> Con la finalidad de saber el tiemp de retardo en el servidor SMTP</body></html>";
                MimeBodyPart textPart = new MimeBodyPart();
                textPart.setContent(body, "text/html");
                Multipart mp = new MimeMultipart();
                mp.addBodyPart(textPart);
                msg.setContent(mp);
                String to = "framirez@conafor.gob.mx;vgarciac@axtel.com.mx;tania.limon@conafor.gob.mx;jgdiazs@axtel.com.mx";
                String[] destinatarios = to.split(";");
                for (int i = 0; i < destinatarios.length; i++) {
                    msg.addRecipient(Message.RecipientType.TO, new InternetAddress(destinatarios[i]));
                }
                msg.saveChanges();
                Transport.send(msg);
                log.info("EMAIL TERMINO");
            } catch (NoSuchProviderException exc) {
                log.error(exc.getMessage(), exc);
                exc.printStackTrace(System.out);
            } catch (MessagingException exc) {
                log.error(exc.getMessage(), exc);
                exc.printStackTrace(System.out);
            } catch (Throwable t) {
                log.error(t.getMessage(), t);
                t.printStackTrace(System.out);
            } finally {
                session = null;
            }
            iMail++;
            int tiempoEspera = ThreadLocalRandom.current().nextInt(1000, 15000);
            System.out.println("Esperando " + (tiempoEspera / 1000) + " segundos...");
            Thread.sleep(tiempoEspera);
        }
    }
}
