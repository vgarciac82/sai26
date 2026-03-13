package com.syc.gestion.util;

import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

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
