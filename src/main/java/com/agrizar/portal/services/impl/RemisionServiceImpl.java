package com.agrizar.portal.services.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrizar.portal.mapper.RemisionMapper;
import com.agrizar.portal.models.dto.EmpresaDto;
import com.agrizar.portal.models.dto.LogDto;
import com.agrizar.portal.models.dto.RemisionHispatecDto;
import com.agrizar.portal.models.dto.TrazabilidadDto;
import com.agrizar.portal.models.request.Producto;
import com.agrizar.portal.models.request.RecepcionRequest;
import com.agrizar.portal.models.response.DetecnoResponse;
import com.agrizar.portal.repositories.req.TrazabilidadRepository;
import com.agrizar.portal.services.ConfiguracionService;
import com.agrizar.portal.services.DetecnoService;
import com.agrizar.portal.services.EmpresaService;
import com.agrizar.portal.services.LogService;
import com.agrizar.portal.services.OrdenFacturaService;
import com.agrizar.portal.services.RemisionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class RemisionServiceImpl implements RemisionService {

	private final TrazabilidadRepository trazabilidadRepository;
	private final OrdenFacturaService ordenFacturaService;
	private final DetecnoService detecnoService;
	private final ConfiguracionService configuracionService;
	private final EmpresaService empresaService;
	private final LogService logService;
	
	@Transactional
	public void publicar() {
		LocalDate dateInicial = null;
		LocalDate dateFinal = null;
		int diasProceso = Integer.parseInt(configuracionService.getByNombre("remision.publicar.diasProcesoBatch"));
		if (diasProceso == 0) {
			dateInicial = LocalDate.parse(configuracionService.getByNombre("remision.publicar.fechaInicial"));
			dateFinal = LocalDate.parse(configuracionService.getByNombre("remision.publicar.fechaFinal"));
		} else {
			dateInicial = LocalDate.now().minusDays(diasProceso);
			dateFinal = LocalDate.now();
		}
		log.info("Fecha inicial: " + dateInicial.toString());
		log.info("Fecha final: " + dateFinal.toString());
		
		procesarRangoFechas(dateInicial, dateFinal);
				
	}

    private void procesarRangoFechas(LocalDate dateInicial, LocalDate dateFinal) {
		
    	List<TrazabilidadDto> ordenesCompra = convertDto(trazabilidadRepository.getRemisionesPendientes(dateInicial, dateFinal));
		
		for (TrazabilidadDto orden: ordenesCompra) {

			EmpresaDto empresa = empresaService.getEmpresaWithToken(orden.getEmpresa());
			if (empresa == null) continue;

			List<RemisionHispatecDto> list = ordenFacturaService.getRemision(orden.getEmpresa(), orden.getSerie(), orden.getFolio());
			if (list.size()>0) {
				publicarRemisionDetecno(list);				
			}
		}
	}

	private void publicarRemisionDetecno(List<RemisionHispatecDto> remisionHispatec) {
		
		RemisionHispatecDto hispatec = remisionHispatec.get(remisionHispatec.size()-1);
		String oc = hispatec.getOc();
		String remision = hispatec.getRemision(); 
		RecepcionRequest req = RemisionMapper.convertToRequest(hispatec);
		
		List<Producto> productos = RemisionMapper.convertToList(remisionHispatec);
		req.setProductos(productos);

		DetecnoResponse response = detecnoService.publicarRemision(req, hispatec.getCodigoEmpresa());
		
		if (response != null) {
			if (response.getSuccess()) {
				logService.save(new LogDto(hispatec.getCodigoEmpresa(), oc, remision));
			} else {
				String mensaje = String.format("Remision: %s de la OC: %s no publicada. Mensaje: %s", remision, oc, response.getMessage()) ;
				log.error(mensaje);
				logService.save(new LogDto(hispatec.getCodigoEmpresa(), oc, remision, response.getMessage()));
			}
		} 
		
	}

	private List<TrazabilidadDto> convertDto(List<Object[]> list) {
		return list.stream().map(item -> new TrazabilidadDto(
				(String) item[0],
                (String) item[1],
                (Integer) item[2]
        ))
        .collect(Collectors.toList());
	}

        
}
