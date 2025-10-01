package com.agrizar.portal.mapper;

import com.agrizar.portal.models.dto.EmpresaDto;
import com.agrizar.portal.models.entities.req.EmpresaEntity;

public final class EmpresaMapper {

	private EmpresaMapper() {
		throw new RuntimeException("Do not instantiate this class, use statically.");
	}
	
	public static EmpresaDto convertToDto(EmpresaEntity entity) {
		EmpresaDto dto = new EmpresaDto();
		dto.setCodigo(entity.getCodigo());
		dto.setRfc(entity.getRfc());
		dto.setApikey(entity.getApikey());
		return dto;
	}
	
}
