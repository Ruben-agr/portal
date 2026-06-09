package com.agrizar.portal.models.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data @AllArgsConstructor @Builder(toBuilder=true)
public class TrazabilidadFacDto {

	private String ocempresa;
	private String ocserie;
	private Integer ocfolio;
	private String formapago;
	private Date fechaocportal;
	private Float saldototal;
	private Float saldoconsumido;
	private Float saldoinsoluto;
	private String rmcusuario;
	private Date rmcfecha;
	private String rmcserie;
	private Integer rmcfolio;
	private String rmcserieportal;
	private Integer rmcfolioportal;
	private Date fecharemisionportal;
	private String facserie;
	private Integer facfolio;
	private String uuid;
	private String uuidportal;
	private Date facfecha;
	private Float factotal;
	private String facusuarioh;
	private Date fcfecha;
	private Date docfechah;
	private String fcfolio;
	private Date fechaPublicacion;
}
