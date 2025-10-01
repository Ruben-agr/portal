package com.agrizar.portal.services;

import com.agrizar.portal.models.dto.BitacoraDto;

public interface BitacoraService {
	
	public BitacoraDto save(BitacoraDto dto);
	public BitacoraDto save(String actividad);
	
}
