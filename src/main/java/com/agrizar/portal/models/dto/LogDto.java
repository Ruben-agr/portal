package com.agrizar.portal.models.dto;

import com.agrizar.portal.enums.ETipoLog;

import lombok.Data;

@Data
public class LogDto {

    private Integer logportalid;
    private String oc;
    private String remision;
    private String logerror;
    private Integer estadoId;

	public LogDto() {
	}
	
    public LogDto(String oc, String remision) {
    	this.oc = oc;
    	this.remision = remision;
    	this.estadoId = ETipoLog.REMISION_PUBLICADA.getId();
    }

    public LogDto(String oc, String remision, String logerror) {
    	this.oc = oc;
    	this.remision = remision;
    	this.logerror = logerror;
    	this.estadoId = ETipoLog.REMISION_NO_PUBLICADA.getId();
    }
        
}
