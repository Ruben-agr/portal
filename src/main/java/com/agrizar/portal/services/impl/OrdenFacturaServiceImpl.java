package com.agrizar.portal.services.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.agrizar.portal.models.dto.OrdenFacturaDto;
import com.agrizar.portal.models.dto.RemisionHispatecDto;
import com.agrizar.portal.repositories.hispatec.OrdenFacturaRepository;
import com.agrizar.portal.services.OrdenFacturaService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrdenFacturaServiceImpl implements OrdenFacturaService {

	private final OrdenFacturaRepository ordenFacturaRepository;
	
	@Override
	public List<OrdenFacturaDto> getOrdenes(String ordenesCompra) {
		return convertOrdenDto(this.ordenFacturaRepository.getOrdenes(ordenesCompra));
	}
	
	List<OrdenFacturaDto> convertOrdenDto(List<Object[]> list) {
		return list.stream().map(item -> new OrdenFacturaDto(
				(String) item[0],
                (String) item[1],
                (Integer) item[2],
                (String) item[3],
                (String) item[4],
                (Integer) item[5],
                (Date) item[6],
                (String) item[7],
                (String) item[8],
                (String) item[9],
                (Integer) item[10],
                (String) item[11],
                (Date) item[12],
                (String) item[13],
                (String) item[14],
                (Date) item[15],
                (String) item[16]
        ))
        .collect(Collectors.toList());
	}
	
	public List<RemisionHispatecDto> getRemision(String empresa, String serie, Integer numero) {
		return convertRemisionDto(this.ordenFacturaRepository.getRemision(empresa, serie, numero));
	}
	
	List<RemisionHispatecDto> convertRemisionDto(List<Object[]> list) {
		return list.stream().map(item -> new RemisionHispatecDto(
				(String) item[0],
                (String) item[1],
                (String) item[2],
                (String) item[3],
                (String) item[4],
                (Date) item[5],
                (String) item[6],
                (String) item[7],
                (BigDecimal) item[8],
                (BigDecimal) item[9],
                (BigDecimal) item[10],
                (BigDecimal) item[11],
                (String) item[12],
                (BigDecimal) item[13],
                (String) item[14],
                (String) item[15],
                (BigDecimal) item[16],
                (String) item[17],
                (String) item[18],
                (String) item[19],
                (BigDecimal) item[20],
                (BigDecimal) item[21],
                (BigDecimal) item[22],
                (BigDecimal) item[23],
                (BigDecimal) item[24],
                (BigDecimal) item[25],
                (Date) item[26],
                (Integer) item[27],
                (Integer) item[28]                
        ))
        .collect(Collectors.toList());
	}
	
}
