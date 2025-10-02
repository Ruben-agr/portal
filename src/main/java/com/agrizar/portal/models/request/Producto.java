package com.agrizar.portal.models.request;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
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
	public Producto () {
		this.Cantidad = 0.0;
		this.PrecioUnitario = 0.0;
		this.Importe = 0.0;
		this.Descuento = 0.0;
		this.ImpuestoRetenido = 0.0;
		this.ImpuestoTrasladado = 0.0;
	}
}
