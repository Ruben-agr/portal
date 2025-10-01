package com.agrizar.portal.services.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.agrizar.portal.models.entities.req.ConfiguracionEntity;
import com.agrizar.portal.repositories.req.ConfiguracionRepository;
import com.agrizar.portal.services.ConfiguracionService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class ConfiguracionServiceImpl implements ConfiguracionService {

	private final ConfiguracionRepository configuracionRepository;

	public String getByNombre(String nombre) {
		Optional<ConfiguracionEntity> entity = configuracionRepository.findByNombre(nombre);
		if (entity.isPresent()) {
			return entity.get().getValor();
		}
		return "";
	}
}
