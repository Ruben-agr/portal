package com.agrizar.portal.repositories.req;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.agrizar.portal.models.entities.req.EmpresaEntity;

@Repository
public interface EmpresaRepository extends CrudRepository<EmpresaEntity, Integer> {
	Optional<EmpresaEntity> findByCodigo(String codigo);
}
