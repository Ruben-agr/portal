package com.agrizar.portal.services.impl;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.agrizar.portal.client.DetecnoApiClient;
import com.agrizar.portal.models.dto.EmpresaDto;
import com.agrizar.portal.models.request.RecepcionRequest;
import com.agrizar.portal.models.response.DetecnoFC;
import com.agrizar.portal.models.response.DetecnoFacturaResponse;
import com.agrizar.portal.models.response.DetecnoOrdenCompraResponse;
import com.agrizar.portal.models.response.DetecnoRM;
import com.agrizar.portal.models.response.DetecnoRecepcionResponse;
import com.agrizar.portal.models.response.DetecnoResponse;
import com.agrizar.portal.models.response.DetecnoResponse400;
import com.agrizar.portal.services.DetecnoService;
import com.agrizar.portal.services.EmpresaService;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class DetecnoServiceImpl implements DetecnoService {

	private final DetecnoApiClient detecnoApiClient;
	private final EmpresaService empresaService;
		
	public Date getOrdenCompra(String oc, String codigoEmpresa) {
		EmpresaDto empresa = null;
		ResponseEntity<DetecnoOrdenCompraResponse> res = null;
		try {
			empresa = empresaService.getEmpresa(codigoEmpresa);
			res = detecnoApiClient.getOrdenCompra(oc, empresa.getApikey(), addPrefijo(empresa.getToken()));
			return res.getBody().getFechapublicacionoc();

		} catch (FeignException e) {
			log.error("Error al obtener la fecha de publicacion de la OC: {} estatus: {} mensaje: {}", oc, e.status(), e.getMessage());
			if (e.status() == 401) {
				empresaService.setEmpresa(empresa);
				try {
					res = detecnoApiClient.getOrdenCompra(oc, empresa.getApikey(), addPrefijo(empresa.getToken()));
					return res.getBody().getFechapublicacionoc();

				} catch (FeignException e1) {
					log.error("Error al obtener la fecha de publicacion de la OC: {} estatus: {} mensaje: {}", oc, e1.status(), e1.getMessage());
				}
			}
		}
		return null;
	}
	
	public List<DetecnoRM> getRecepciones(String oc, String codigoEmpresa) {
		EmpresaDto empresa = null;
		ResponseEntity<DetecnoRecepcionResponse> res = null;
		try {
			empresa = empresaService.getEmpresa(codigoEmpresa);
			res = detecnoApiClient.getRecepciones(oc, empresa.getApikey(), addPrefijo(empresa.getToken()));
			
			if (res.getBody().getTotalCount()>0) {
				return res.getBody().getData();
			}
			
		} catch (FeignException e) {
			log.error("Error al obtener las recepciones de la OC: {} estatus: {} mensaje: {}", oc, e.status(), e.getMessage());
		}
		return null;
	}

	public List<DetecnoFC> getFacturas(String oc, String codigoEmpresa) {
		EmpresaDto empresa = null;
		ResponseEntity<DetecnoFacturaResponse> res = null;
		try {
			empresa = empresaService.getEmpresa(codigoEmpresa);
			res = detecnoApiClient.getFacturas(oc, empresa.getApikey(), addPrefijo(empresa.getToken()));
			return res.getBody().getFacturas();
			
		} catch (FeignException e) {
			log.error("Error al obtener las facturas de la OC: {} estatus: {} mensaje: {}", oc, e.status(), e.getMessage());
		}
		return null;
	}
	
	public DetecnoResponse publicarRemision(RecepcionRequest recepcion, String codigoEmpresa) {
		EmpresaDto empresa = null;
		ResponseEntity<DetecnoResponse> res = null;
		try {
			empresa = empresaService.getEmpresa(codigoEmpresa);
			res = detecnoApiClient.publicarRemision(recepcion, empresa.getApikey(), addPrefijo(empresa.getToken()));
			
		} catch (FeignException e) {
			log.error("Error al publicar remision OC: {} estatus: {} mensaje: {}", recepcion.getNoOC(), e.status(), e.getMessage());
			
			if (e.status() == 400) {
				return parseErrorResponse(e);
			} else if (e.status() == 401) {
				empresaService.setEmpresa(empresa);
				try {
					res = detecnoApiClient.publicarRemision(recepcion, empresa.getApikey(), addPrefijo(empresa.getToken()));	
				} catch (FeignException e1) {
					log.error("Error al publicar remision OC: {} estatus: {} mensaje: {}", recepcion.getNoOC(), e1.status(), e1.getMessage());
					if (e.status() == 400) {
						return parseErrorResponse(e);
					}
				}
			}
		}
		
		if (res != null && res.getStatusCode() == HttpStatus.OK) {
	        DetecnoResponse response = new DetecnoResponse();
	        response.setSuccess(true);
	        response.setResponseCode(HttpStatus.OK.value());
	        response.setMessage("");
	        return response;
		}
		
		return null;
	}

	private DetecnoResponse parseErrorResponse(FeignException e) {
	    try {
	        String responseBody = e.contentUTF8();
	        
	        ObjectMapper mapper = new ObjectMapper();
	        DetecnoResponse400 temp = mapper.readValue(responseBody, DetecnoResponse400.class);
	        return convertTo(temp);
	        
	    } catch (Exception parseException) {
	        log.error("Error al parsear respuesta de error", parseException);
	        
	        // Retornar respuesta genérica si no se puede parsear
	        DetecnoResponse response = new DetecnoResponse();
	        response.setSuccess(false);
	        response.setResponseCode(e.status());
	        response.setMessage(e.getMessage());
	        return response;
	    }
	}
	
	private DetecnoResponse convertTo(DetecnoResponse400 temp) {
		DetecnoResponse result = new DetecnoResponse();
		result.setSuccess(temp.getSuccess());
		result.setResponseCode(temp.getResponseCode());
		result.setMessage(temp.getMessage());
		return result;
	}

	String addPrefijo(String token) {
		return "Bearer " + token;
	}

}
