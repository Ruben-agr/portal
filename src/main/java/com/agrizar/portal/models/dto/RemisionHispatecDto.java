package com.agrizar.portal.models.dto;

import java.math.BigDecimal;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class RemisionHispatecDto {

	private String codigoEmpresa;
	private String nombreEmpresa;
	private String oc;
	private String noProveedor;
	private String remision;
	private Date fechaRem;
	private String sucursal;
	private String shipTo;
	private BigDecimal subTotal;
	private BigDecimal totalImpTrasladados;
	private BigDecimal totalImpRetenidos;
	private BigDecimal descuentos;
	private String moneda;
	private BigDecimal total;
	private String rfcEmisor;
	private String rfcReceptor;
	private BigDecimal numeroLinea;
	private String sku;
	private String descripcion;
	private String unidadMedida;
	private BigDecimal cantidad;
	private BigDecimal precioUnitario;
	private BigDecimal importe;
	private BigDecimal descuentoL;
	private BigDecimal impRetenido;
	private BigDecimal impTrasladado;
	private Date fechaEntrega;
	private Integer noRemisionCons;
	private Integer artEnRem;
}
