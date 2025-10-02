package com.agrizar.portal.models.dto;

import com.agrizar.portal.enums.ETipoLog;

import lombok.Data;

@Data
public class LogDto {

    private Integer logportalid;
    private String codigoEmpresa;
    private String oc;
    private String remision;
    private String logerror;
    private Integer estadoId;

	public LogDto() {
	}
	
    public LogDto(String codigoEmpresa, String oc, String remision) {
    	this.codigoEmpresa = codigoEmpresa;
    	this.oc = oc;
    	this.remision = remision;
    	this.logerror = "";
    	this.estadoId = ETipoLog.REMISION_PUBLICADA.getId();
    }

    public LogDto(String codigoEmpresa, String oc, String remision, String logerror) {
    	this.codigoEmpresa = codigoEmpresa;
    	this.oc = oc;
    	this.remision = remision;
    	this.logerror = logerror;
    	this.estadoId = ETipoLog.REMISION_NO_PUBLICADA.getId();
    }
        
}
