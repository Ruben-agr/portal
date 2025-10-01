package com.agrizar.portal.models.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DetecnoRM {

	@JsonProperty("NoRecepcionMercancia")
	private String rmcSerieNumero;
	
	@JsonProperty("FechaRecepcionMercancia")
	private Date rmcFecha;
}
