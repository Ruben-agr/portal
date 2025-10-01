package com.agrizar.portal.models.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class Control {
	private String Correo;
	private Double ToleranciaMin;
	private Double ToleranciaMax;
	private Integer TipoNotificacion;

}
