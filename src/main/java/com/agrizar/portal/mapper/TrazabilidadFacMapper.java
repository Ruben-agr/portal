package com.agrizar.portal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.agrizar.portal.models.dto.OrdenFacturaDto;
import com.agrizar.portal.models.dto.TrazabilidadFacDto;

public final class TrazabilidadFacMapper {

	private TrazabilidadFacMapper() {
		throw new RuntimeException("Do not instantiate this class, use statically.");
	}
	
	public static TrazabilidadFacDto convertToDto(OrdenFacturaDto entity) {
		TrazabilidadFacDto dto = new TrazabilidadFacDto(
				entity.getOcEmpresa(), 
				entity.getOcSerie(),
				entity.getOcNumero(),
				entity.getOcFormaPago(),
				null,
				entity.getRmcUsuario(),
				entity.getRmcFechaCreacion(),
				entity.getRmcSerie(),
				entity.getRmcNumero(),
				null,
				null,
				null,
				entity.getFcSerie(),
				entity.getFcNumero(),
				entity.getFcUuid(),
				null,
				null,
				null
				);
		return dto;
	}
	
	public static List<TrazabilidadFacDto> convertToDtos(List<OrdenFacturaDto> entities) {
		List<TrazabilidadFacDto> dtos = new ArrayList<>();
		if (entities != null) {
			for (OrdenFacturaDto entity : entities) {
				dtos.add( convertToDto(entity) );
			}
		}
		return dtos;
	}
}
