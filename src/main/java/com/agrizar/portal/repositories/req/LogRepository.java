package com.agrizar.portal.repositories.req;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.agrizar.portal.models.entities.req.LogEntity;

@Repository
public interface LogRepository extends CrudRepository<LogEntity, Integer> {

}
