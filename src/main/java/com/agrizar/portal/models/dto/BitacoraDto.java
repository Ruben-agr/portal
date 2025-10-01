package com.agrizar.portal.models.dto;

import lombok.Data;

@Data
public class BitacoraDto {

    private Integer bitacoraId;
    private String actividad;
    private Integer estadoId;

	public BitacoraDto() {
	}
	
    public BitacoraDto(String actividad) {
    	this.actividad = actividad;
    }
        
}
