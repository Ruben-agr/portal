package com.agrizar.portal.mapper;

import com.agrizar.portal.models.dto.LogDto;
import com.agrizar.portal.models.entities.req.LogEntity;
import com.agrizar.portal.util.DateUtil;

public final class LogMapper {

	private LogMapper() {
		throw new RuntimeException("Do not instantiate this class, use statically.");
	}
	
	public static LogEntity convertToEntity(LogDto dto) {
		LogEntity entity = new LogEntity();
		entity.setLogportalid(dto.getLogportalid()); 
		entity.setCodigoEmpresa(dto.getCodigoEmpresa());
		entity.setOc(dto.getOc());
		entity.setRemision(dto.getRemision());
		entity.setLogerror(dto.getLogerror());
		entity.setFechaRegistro( DateUtil.getLocalDateTime() );
		entity.setFechaModificacion( DateUtil.getLocalDateTime() );
		entity.setEstadoId(dto.getEstadoId());
		return entity;
	}
}
