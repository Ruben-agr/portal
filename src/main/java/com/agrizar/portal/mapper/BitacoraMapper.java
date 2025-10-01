package com.agrizar.portal.mapper;

import com.agrizar.portal.models.dto.BitacoraDto;
import com.agrizar.portal.models.entities.req.BitacoraEntity;
import com.agrizar.portal.util.DateUtil;

public final class BitacoraMapper {

	private BitacoraMapper() {
		throw new RuntimeException("Do not instantiate this class, use statically.");
	}
	
	public static BitacoraDto convertToDto(BitacoraEntity entity) {
		BitacoraDto dto = new BitacoraDto();
		dto.setBitacoraId( entity.getBitacoraId() ); 
		dto.setActividad( entity.getActividad() );
		return dto;
	}
	
	public static BitacoraEntity convertToEntity(BitacoraDto dto) {
		BitacoraEntity entity = new BitacoraEntity();
		entity.setBitacoraId( dto.getBitacoraId() ); 
		entity.setActividad( dto.getActividad() );
		entity.setFechaRegistro( DateUtil.getLocalDateTime() );
		entity.setFechaModificacion( DateUtil.getLocalDateTime() );
		entity.setEstadoId(1);
		return entity;
	}
}
