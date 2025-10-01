package com.agrizar.portal.services;

import java.time.LocalDate;

public interface TrazabilidadService {
	
	public void actualizar();
	void actualizar(LocalDate fechaInicial, LocalDate fechaFinal);
	void actualizarPorOrden(String empresaOrdenCompra);
	public void actualizarEstatusImportantes();
	
}
