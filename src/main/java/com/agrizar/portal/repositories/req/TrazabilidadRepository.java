package com.agrizar.portal.repositories.req;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agrizar.portal.models.entities.req.BitacoraEntity;

@Repository
public interface TrazabilidadRepository extends CrudRepository<BitacoraEntity, Integer> {

	@Query(value = "select ordencompra from actualizar_trazabilidad(:fechaInicial, :fechaFinal)", nativeQuery = true)	
	List<String> getOrdenesCompra(@Param("fechaInicial") LocalDate fechaInicial
			, @Param("fechaFinal") LocalDate fechaFinal);

	@Modifying
	@Query(value = "CALL actualizar_trazabilidad_fac(CAST(:ordenes AS jsonb), :fechaInicial, :fechaFinal)", nativeQuery = true)
	void actualizarTrazabilidadFac(@Param("ordenes") String ordenes,
			@Param("fechaInicial") LocalDate fechaInicial,
			@Param("fechaFinal") LocalDate fechaFinal);	
	
	@Modifying
	@Query(value = "CALL actualizar_trazabilidad_fac(CAST(:orden AS jsonb))", nativeQuery = true)
	void actualizarTrazabilidadFac(@Param("orden") String orden);	

	@Query(value = "select codigoempresa, rmcserie, rmcfolio from obtener_remisiones_pendientes(:fechaInicial, :fechaFinal)", nativeQuery = true)	
	List<Object[]> getRemisionesPendientes(@Param("fechaInicial") LocalDate fechaInicial
			, @Param("fechaFinal") LocalDate fechaFinal);
	
	@Query(value = "select ordencompra from obtener_estatus_pendientes(:fechaInicial, :fechaFinal)", nativeQuery = true)	
	List<String> getEstatusPendientes(@Param("fechaInicial") LocalDate fechaInicial
			, @Param("fechaFinal") LocalDate fechaFinal);

}
