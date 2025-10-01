package com.agrizar.portal.services.impl;

import java.util.Properties;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

import com.agrizar.portal.services.ConfiguracionService;
import com.agrizar.portal.services.MailSenderService;
import com.agrizar.portal.util.UtilsString;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class MailSenderServiceImpl implements MailSenderService {

	private final ConfiguracionService configuracionService;
	
	public void enviarCorreo(String destinatario, String asunto, String destinatarioCC, String mensaje, Boolean EsHtml) throws AddressException, MessagingException {

		final String username = configuracionService.getByNombre("Mail.From");
		final String password = configuracionService.getByNombre("Mail.Password");
		final String puerto = configuracionService.getByNombre("Mail.Port");
		final String servidor = configuracionService.getByNombre("Mail.Server");
		final String conexionSegura = configuracionService.getByNombre("Mail.SSL");
		//final String desde = configuracionService.getByNombre("Mail.MailAdressSource");
		//final String desdeNombre = UtilsString.encodeText(configuracionService.getByNombre("Mail.FromName"));
		//final String desdeCompleto = desdeNombre + "<" + desde + ">";
		
		String subType;
		if(EsHtml){
			subType = "html";
		}else{
			subType = "plain";
		}
		
		Properties props = new Properties();
		props.put("mail.smtp.host", servidor);
		props.put("mail.smtp.port", puerto);
		props.put("mail.smtp.starttls.enable", conexionSegura);
		props.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});
		
		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress(username));
		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
		if (destinatarioCC != null && !destinatarioCC.isEmpty()) {
			message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(destinatarioCC));
		}
		message.setSubject(asunto);
		message.setSubject(UtilsString.encodeText(asunto));
		

		Multipart multipart = new MimeMultipart();

        BodyPart htmlBodyPart = new MimeBodyPart();
        htmlBodyPart.setContent(mensaje, "text/" + subType + "; charset=UTF-8");
        multipart.addBodyPart(htmlBodyPart);
        
        message.setContent(multipart);
		
		Transport.send(message);
		log.info("Correo enviado");
	}	
}
