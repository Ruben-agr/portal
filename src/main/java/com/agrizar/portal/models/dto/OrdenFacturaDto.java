package com.agrizar.portal.models.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class OrdenFacturaDto {

	private String ocEmpresa;
	private String ocSerie;
	private Integer ocNumero;
	private String ocFormaPago;
	private String rmcSerie;
	private Integer rmcNumero;
	private Date rmcFechaCreacion;
	private String rmcMaquina;
	private String rmcUsuario;
	private String fcSerie;
	private Integer fcNumero;
	private String fcUuid;
	private Date fechaCreacion;
	private String maquina;
	private String usuario;
	private Date fcfecha;
	private String fcfolio;
}
