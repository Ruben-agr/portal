package com.agrizar.portal.repositories.hispatec;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrizar.portal.models.entities.hispatec.ArticuloEntity;

@Repository
public interface OrdenFacturaRepository extends JpaRepository<ArticuloEntity, Integer> {

	@Query(value = "execute CLI372_PROP_ObtenerFC :ordenesCompra", nativeQuery = true)
	List<Object[]> getOrdenes(@Param("ordenesCompra") String ordenesCompra);

	@Query(value = "execute CLI372_PROP_RemManual_Detecno :codEmpresa, :serie, :numero, '', ''", nativeQuery = true)
	List<Object[]> getRemision(@Param("codEmpresa") String empresa,
			@Param("serie") String serie,
			@Param("numero") Integer numero);
}
