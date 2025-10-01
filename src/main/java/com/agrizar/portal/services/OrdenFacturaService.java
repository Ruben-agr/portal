package com.agrizar.portal.services;

import java.util.List;

import com.agrizar.portal.models.dto.OrdenFacturaDto;
import com.agrizar.portal.models.dto.RemisionHispatecDto;

public interface OrdenFacturaService {

	public List<OrdenFacturaDto> getOrdenes(String ordenesCompra);
	public List<RemisionHispatecDto> getRemision(String empresa, String serie, Integer numero);
	
}
