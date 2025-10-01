package com.agrizar.portal.services.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.agrizar.portal.mapper.EmpresaMapper;
import com.agrizar.portal.models.dto.EmpresaDto;
import com.agrizar.portal.models.entities.req.EmpresaEntity;
import com.agrizar.portal.repositories.req.EmpresaRepository;
import com.agrizar.portal.services.EmpresaService;
import com.agrizar.portal.services.TokenService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class EmpresaServiceImpl implements EmpresaService {

	private final Map<String, EmpresaDto> empresaCache = new HashMap<>();
	private final EmpresaRepository empresaRepository;
	private final TokenService tokenService;
	
	public EmpresaDto getEmpresa(String codigoEmpresa) {
        return empresaCache.get(codigoEmpresa);
	}

	public void setEmpresa(EmpresaDto empresa) {
		
    	String token = tokenService.getToken(empresa.getRfc(), empresa.getApikey());
    	empresa.setToken(token);
        EmpresaDto cachedEmpresa = empresaCache.get(empresa.getCodigo());
        if (cachedEmpresa == null) {
        	empresaCache.put(empresa.getCodigo(), empresa);
        } else {
        	empresaCache.replace(empresa.getCodigo(), empresa);
        }
        return;
	}

	public EmpresaDto getEmpresaWithToken(String codigoEmpresa) {
        if (codigoEmpresa == null || codigoEmpresa.trim().isEmpty()) {
            return null;
        }
        
        EmpresaDto cachedEmpresa = getEmpresa(codigoEmpresa);
        if (cachedEmpresa != null) {
            return cachedEmpresa;
        }
        
        return loadEmpresa(codigoEmpresa);            
    }
	
    private EmpresaDto loadEmpresa(String codigoEmpresa) {
        try {
            Optional<EmpresaEntity> empresaEntity = empresaRepository.findByCodigo(codigoEmpresa);
            
            if (empresaEntity.isPresent() && empresaEntity.get().getApikey() != null && !empresaEntity.get().getApikey().isEmpty()) {
                EmpresaDto empresaDto = EmpresaMapper.convertToDto(empresaEntity.get());
                setEmpresa(empresaDto);
                
                return empresaDto;
            } else {
                return null;
            }
        } catch (Exception e) {
            log.error("Error al cargar empresa desde BD: {}", e);
            return null;
        }
    }

}
