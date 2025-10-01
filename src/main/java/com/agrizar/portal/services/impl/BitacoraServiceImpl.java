package com.agrizar.portal.services.impl;

import org.springframework.stereotype.Service;

import com.agrizar.portal.mapper.BitacoraMapper;
import com.agrizar.portal.models.dto.BitacoraDto;
import com.agrizar.portal.models.entities.req.BitacoraEntity;
import com.agrizar.portal.repositories.req.BitacoraRepository;
import com.agrizar.portal.services.BitacoraService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class BitacoraServiceImpl implements BitacoraService {

	private final BitacoraRepository bitacoraRepository;
	
	@Override
	public BitacoraDto save(BitacoraDto dto) {
		BitacoraEntity entity = BitacoraMapper.convertToEntity(dto);
		entity.setBitacoraId(0);
		entity.setUsuarioRegistro( "proceso.batch" );
		this.bitacoraRepository.save( entity );
		dto.setBitacoraId(entity.getBitacoraId());
		return dto;
	}
	
	public BitacoraDto save(String actividad) {
		return save(new BitacoraDto(actividad)); 
	}	
	
}
