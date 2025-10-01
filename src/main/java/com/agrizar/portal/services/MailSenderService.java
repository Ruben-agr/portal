package com.agrizar.portal.services;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

public interface MailSenderService {
	
	public void enviarCorreo(String destinatario, String asunto, String destinatarioCC, String mensaje, Boolean EsHtml) throws AddressException, MessagingException;	
}
