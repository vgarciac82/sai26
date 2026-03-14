package com.syc.gestion.util;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class Correo {

    public static void envia(Session session, String to, String from, String subject, String body) throws IOException, AddressException, MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        //message.setFrom();
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        message.setSubject(subject);
        message.setSentDate(new Date());
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setText(body);
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp);
        message.setContent(mp);
        message.saveChanges();
        Transport.send(message);
    }

    public static void envia(Session session, Map toMap, String from, String subject, String body) throws IOException, AddressException, MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        //message.setFrom();
        String to;
        boolean first = true;
        Iterator iter = toMap.keySet().iterator();
        while (iter.hasNext()) {
            to = (String) iter.next();
            if (first)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            else
                message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            first = false;
        }
        message.setSubject(subject);
        message.setSentDate(new Date());
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setText(body);
        Multipart mp = new MimeMultipart();
        mp.addBodyPart(mbp);
        message.setContent(mp);
        message.saveChanges();
        Transport.send(message);
    }
}
