package com.agrizar.portal.repositories.req;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.agrizar.portal.models.entities.req.ConfiguracionEntity;

@Repository
public interface ConfiguracionRepository extends CrudRepository<ConfiguracionEntity, Integer> {
	
	public Optional<ConfiguracionEntity> findByNombre(String nombre);
}
