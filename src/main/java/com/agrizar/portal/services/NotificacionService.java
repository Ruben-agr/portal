package com.agrizar.portal.services;

import com.agrizar.portal.enums.ETipoUsuarioNotificar;

public interface NotificacionService {
	
	public void enviar(String asunto, String mensaje, ETipoUsuarioNotificar tipo);
		
}
