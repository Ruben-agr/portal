package com.agrizar.portal.services.impl;

import javax.mail.MessagingException;

import org.springframework.stereotype.Service;

import com.agrizar.portal.enums.ETipoUsuarioNotificar;
import com.agrizar.portal.services.BitacoraService;
import com.agrizar.portal.services.ConfiguracionService;
import com.agrizar.portal.services.MailSenderService;
import com.agrizar.portal.services.NotificacionService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class NotificacionServiceImpl implements NotificacionService {

	private final MailSenderService mailSenderService;
	private final BitacoraService bitacoraService;
	private final ConfiguracionService configuracionService;
	
	public void enviar(String asunto, String mensaje, ETipoUsuarioNotificar tipo) {
		String destinatario = "";
		
		switch (tipo) {
		case TECNICO:
			destinatario = configuracionService.getByNombre("trazabilidad.notificacion.tecnica");;
			break;
		case USUARIO:
			destinatario = configuracionService.getByNombre("trazabilidad.notificacion.usuario");;
			break;
		default:
			break;
		}
		
		String destinatarioCC = "";
		
		try {
			mailSenderService.enviarCorreo(destinatario, asunto, destinatarioCC, mensaje, false);
		} catch (MessagingException e) {
			log.error("Error al enviar correo: {}", e.getMessage());
			bitacoraService.save(e.getMessage());
		}
	}

}
