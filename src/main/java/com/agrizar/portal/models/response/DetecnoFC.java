package com.agrizar.portal.models.response;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DetecnoFC {

	@JsonProperty("NoRecepcionMercancias")
	private String rmcSerieNumero;
	
	@JsonProperty("UUID")
	private String uuid;

	@JsonProperty("Fecha")
	private Date facFecha;

	@JsonProperty("Total")
	private Float facTotal;
}
