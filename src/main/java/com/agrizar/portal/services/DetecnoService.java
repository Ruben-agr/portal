package com.agrizar.portal.services;

import java.util.List;

import com.agrizar.portal.models.request.RecepcionRequest;
import com.agrizar.portal.models.response.DetecnoFC;
import com.agrizar.portal.models.response.DetecnoOrdenCompraResponse;
import com.agrizar.portal.models.response.DetecnoRM;
import com.agrizar.portal.models.response.DetecnoResponse;

public interface DetecnoService {
	
	public DetecnoOrdenCompraResponse getOrdenCompra(String oc, String codigoEmpresa);
	public List<DetecnoRM> getRecepciones(String oc, String codigoEmpresa);
	public List<DetecnoFC> getFacturas(String oc, String codigoEmpresa);
	public DetecnoResponse publicarRemision(RecepcionRequest recepcion, String codigoEmpresa);
	
}
