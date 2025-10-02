package com.agrizar.portal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.agrizar.portal.models.dto.RemisionHispatecDto;
import com.agrizar.portal.models.request.Control;
import com.agrizar.portal.models.request.Producto;
import com.agrizar.portal.models.request.RecepcionRequest;

public final class RemisionMapper {

	private RemisionMapper() {
		throw new RuntimeException("Do not instantiate this class, use statically.");
	}
	
	public static Producto convertTo(RemisionHispatecDto entity) {
		Producto result = new Producto();
		result.setNumLineaOC(entity.getNumeroLinea().toString());
		result.setSKU(entity.getSku());
		result.setDescripcion(entity.getDescripcion());
		result.setUnidadMedida(entity.getUnidadMedida());
		if (entity.getCantidad() != null) result.setCantidad(Double.valueOf(entity.getCantidad().toString()));
		if (entity.getPrecioUnitario() != null) result.setPrecioUnitario(Double.valueOf(entity.getPrecioUnitario().toString()));
		if (entity.getImporte() != null) result.setImporte(Double.valueOf(entity.getImporte().toString()));
		if (entity.getDescuentoL() != null) result.setDescuento(Double.valueOf(entity.getDescuentoL().toString()));
		if (entity.getImpRetenido() != null) result.setImpuestoRetenido(Double.valueOf(entity.getImpRetenido().toString()));
		if (entity.getImpTrasladado() != null) result.setImpuestoTrasladado(Double.valueOf(entity.getImpTrasladado().toString()));
		result.setFechaEntrega(entity.getFechaEntrega());
		return result;
	}
	
	public static List<Producto> convertToList(List<RemisionHispatecDto> entities) {
		List<Producto> list = new ArrayList<>();
		if (entities != null) {
			for (RemisionHispatecDto entity : entities) {
				list.add( convertTo(entity) );
			}
		}
		return list;
	}	
	public static RecepcionRequest convertToRequest(RemisionHispatecDto entity) {
		RecepcionRequest result = new RecepcionRequest();
		result.setNoOC(entity.getOc());
		result.setNoProveedor(entity.getNoProveedor());
		result.setNoRecepcionMercancia(entity.getRemision());
		result.setFechaRecepcionMercancia(entity.getFechaRem());
		result.setSucursal("");
		result.setShipTo("");
		if (entity.getSubTotal() != null) result.setSubtotal(Double.valueOf(entity.getSubTotal().toString()));
		if (entity.getTotalImpTrasladados() != null) result.setTotalImpuestosTrasladados(Double.valueOf(entity.getTotalImpTrasladados().toString()));
		if (entity.getTotalImpRetenidos() != null) result.setTotalImpuestosRetenidos(Double.valueOf(entity.getTotalImpRetenidos().toString()));
		if (entity.getDescuentos() != null) result.setDescuentos(Double.valueOf(entity.getDescuentos().toString()));
		result.setMoneda(entity.getMoneda());
		if (entity.getTotal() != null) result.setTotal(Double.valueOf(entity.getTotal().toString()));
		result.setRfcEmisor(entity.getRfcEmisor());
		result.setRfcReceptor(entity.getRfcReceptor());

		
		Control control = new Control();
		control.setCorreo("");
		control.setTipoNotificacion(0);
		control.setToleranciaMax(1.0);
		control.setToleranciaMin(1.0);
		result.setControl(control);
		
		return result;
	}

}
