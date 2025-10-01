package com.agrizar.portal.services.impl;

import org.springframework.stereotype.Service;

import com.agrizar.portal.mapper.LogMapper;
import com.agrizar.portal.models.dto.LogDto;
import com.agrizar.portal.models.entities.req.LogEntity;
import com.agrizar.portal.repositories.req.LogRepository;
import com.agrizar.portal.services.LogService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class LogServiceImpl implements LogService {

	private final LogRepository logRepository;
	
	@Override
	public LogDto save(LogDto dto) {
		LogEntity entity = LogMapper.convertToEntity(dto);
		entity.setLogportalid(0);
		entity.setUsuarioRegistro( "proceso.batch" );
		this.logRepository.save( entity );
		dto.setLogportalid(entity.getLogportalid());
		return dto;
	}
		
}
