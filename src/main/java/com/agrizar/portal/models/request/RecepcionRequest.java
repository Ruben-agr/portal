package com.agrizar.portal.models.request;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class RecepcionRequest {
	private String NoOC;
	private String NoProveedor;
	private String NoRecepcionMercancia;
	@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date FechaRecepcionMercancia;
	private String Sucursal;
	private String ShipTo;
	private Double Subtotal;
	private Double TotalImpuestosTrasladados;
	private Double TotalImpuestosRetenidos;
	private Double Descuentos;
	private String Moneda;
	private Double Total;
	private String RfcEmisor;
	private String RfcReceptor;
	private List<Producto> Productos;
	private Control Control;
	
}
