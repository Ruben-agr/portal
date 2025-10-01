package com.agrizar.portal.services;

import com.agrizar.portal.models.dto.EmpresaDto;

public interface EmpresaService {
	
	public EmpresaDto getEmpresa(String codigoEmpresa);
	public void setEmpresa(EmpresaDto empresa);
	public EmpresaDto getEmpresaWithToken(String codigoEmpresa);
}
