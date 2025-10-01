package com.agrizar.portal.models.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Producto {
	private String NumLineaOC;
	private String SKU;
	private String Descripcion;
	private String UnidadMedida;
	private Double Cantidad;
	private Double PrecioUnitario;
	private Double Importe;
	private Double Descuento;
	private Double ImpuestoRetenido;
	private Double ImpuestoTrasladado;
	private Date FechaEntrega;	
}
