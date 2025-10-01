package com.agrizar.portal.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TrazabilidadDto {

	private String empresa;
	private String serie;
	private Integer folio;
}
