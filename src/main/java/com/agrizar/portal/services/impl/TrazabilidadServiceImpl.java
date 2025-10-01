package com.agrizar.portal.services.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrizar.portal.mapper.TrazabilidadFacMapper;
import com.agrizar.portal.models.dto.EmpresaDto;
import com.agrizar.portal.models.dto.TrazabilidadFacDto;
import com.agrizar.portal.models.response.DetecnoFC;
import com.agrizar.portal.models.response.DetecnoRM;
import com.agrizar.portal.repositories.req.TrazabilidadRepository;
import com.agrizar.portal.services.ConfiguracionService;
import com.agrizar.portal.services.DetecnoService;
import com.agrizar.portal.services.EmpresaService;
import com.agrizar.portal.services.OrdenFacturaService;
import com.agrizar.portal.services.TrazabilidadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrazabilidadServiceImpl implements TrazabilidadService {

	private final TrazabilidadRepository trazabilidadRepository;
	private final OrdenFacturaService ordenFacturaService;
	private final DetecnoService detecnoService;
	private final ConfiguracionService configuracionService;
	private final EmpresaService empresaService;
	private final Map<String, DetecnoRM> recepcionesCache = new HashMap<>();
	private final Map<String, DetecnoFC> facturasCache = new HashMap<>();
	private final Map<String, TrazabilidadFacDto> ordenCache = new HashMap<>();
	private final Map<String, TrazabilidadFacDto> ordenesNuevasCache = new HashMap<>();
	
	@Transactional
	public void actualizar() {
		LocalDate dateInicial = null;
		LocalDate dateFinal = null;
		int diasProceso = Integer.parseInt(configuracionService.getByNombre("trazabilidad.diasProcesoBatch"));
		if (diasProceso == 0) {
			dateInicial = LocalDate.parse(configuracionService.getByNombre("trazabilidad.fechaInicial"));
			dateFinal = LocalDate.parse(configuracionService.getByNombre("trazabilidad.fechaFinal"));
		} else {
			dateInicial = LocalDate.now().minusDays(diasProceso);
			dateFinal = LocalDate.now();
		}
		log.info("Fecha inicial: " + dateInicial.toString());
		log.info("Fecha final: " + dateFinal.toString());
		
		procesarRangoFechas(dateInicial, dateFinal);
	}
	
	private void procesarRangoFechas(LocalDate dateInicial, LocalDate dateFinal) {
		
    	List<String> ordenesCompra = trazabilidadRepository.getOrdenesCompra(dateInicial, dateFinal);
		
    	ejecutarBloques(ordenesCompra, dateInicial, dateFinal);
	}

	private void ejecutarBloques(List<String> ordenesCompra, LocalDate dateInicial, LocalDate dateFinal) {
		int contador = 0;
		String ordenJoin ="";
		for (String orden: ordenesCompra) {
			ordenJoin = ordenJoin + "|" + orden ;
			contador++;
			if (contador > 200) {
				ejecutarBloque(dateInicial, dateFinal, ordenJoin.substring(1));
				contador = 0;
				ordenJoin ="";
			}
		}
		if (!ordenJoin.isEmpty()) {
			ejecutarBloque(dateInicial, dateFinal, ordenJoin.substring(1));
		}		
	}

	private void ejecutarBloque(LocalDate dateInicial, LocalDate dateFinal, String ordenJoin) {
		
		List<TrazabilidadFacDto> ordenesFactura = prepararOrdenes(ordenJoin);

        String ordenesJson = convertToJson(ordenesFactura);
        if (ordenesJson != null && !ordenesJson.isEmpty()) {
            trazabilidadRepository.actualizarTrazabilidadFac(ordenesJson, dateInicial, dateFinal);
        }
	}

	private List<TrazabilidadFacDto> prepararOrdenes(String ordenJoin) {
    	String empresaOrdenAnterior = "";
    	
    	log.info("hispatec comienza");
		List<TrazabilidadFacDto> ordenesFactura = TrazabilidadFacMapper.convertToDtos(ordenFacturaService.getOrdenes(ordenJoin));
		log.info("hispatec termina {} ordenes recuperadas", ordenesFactura.size());
		
		for (TrazabilidadFacDto orden : ordenesFactura) {
			
			String empresaOrden = orden.getOcempresa() + orden.getOcserie() + orden.getOcfolio();
			if (empresaOrden.equals(empresaOrdenAnterior)) {
				TrazabilidadFacDto anterior = ordenCache.get("anterior");
				orden.setFechaocportal(anterior.getFechaocportal());
			} else {

				if (!empresaOrdenAnterior.isEmpty()) {
					insertarRecepcionesFacturas();
				}
				
				EmpresaDto empresa = empresaService.getEmpresaWithToken(orden.getOcempresa());
				if (empresa == null) continue;
				
				leerInformacionDetecno(orden);
				
				empresaOrdenAnterior = empresaOrden;
			}
			
			actualizarRemisionFactura(orden);
		}
		
		insertarRecepcionesFacturas();
		
		ordenesFactura.addAll(ordenesNuevasCache.values());
		ordenesNuevasCache.clear();
		
		return ordenesFactura;
	}

	private void insertarRecepcionesFacturas() {
		if (!recepcionesCache.isEmpty()) {
			insertarRecepciones();
		}
		if (!facturasCache.isEmpty()) {
			insertarFacturas();
		}
	}

	private void insertarRecepciones() {
		boolean primeraVez = true;
		TrazabilidadFacDto ordenAnterior = ordenCache.get("anterior").toBuilder().build();
		
    	for (DetecnoRM rm : recepcionesCache.values()) {
    		TrazabilidadFacDto orden;
    		if (primeraVez) {
    			orden = ordenCache.get("anterior");
    		} else {
    			orden = ordenAnterior.toBuilder().build();
    		}
    		
    		String rmcSerieFolio = rm.getRmcSerieNumero();
    		if (rmcSerieFolio.contains("-")) {
        		String[] arr = rmcSerieFolio.split("-");
        		if (arr.length>1) {
            		orden.setRmcserieportal(arr[0]);
            		orden.setRmcfolioportal(Integer.valueOf(arr[1]));        			
        		} else {
        			log.info("Escenario nuevo: " + rmcSerieFolio);
        			orden.setRmcserieportal(arr[0]);
        		}
    		} else {
    			if (rmcSerieFolio.contains("REMS")) {
    				String folio = rmcSerieFolio.substring(4);
    				orden.setRmcserieportal("REMS");
    				orden.setRmcfolioportal(Integer.valueOf(folio));
    			} else {
    				orden.setRmcserieportal("");
    				orden.setRmcfolioportal(Integer.valueOf(rmcSerieFolio));
    			}
    		}
    		orden.setFecharemisionportal(rm.getRmcFecha());
    		actualizarFactura(orden, rmcSerieFolio);
    		if (primeraVez) {
    			primeraVez = false;
    		} else {
    			ordenesNuevasCache.put(rmcSerieFolio, orden);	
    		}
    	}
	}

	private void insertarFacturas() {
		boolean primeraVez = true;
		TrazabilidadFacDto ordenAnterior = ordenCache.get("anterior").toBuilder().build();

		for (DetecnoFC fc : facturasCache.values()) {
    		TrazabilidadFacDto orden;
    		if (primeraVez) {
    			orden = ordenCache.get("anterior");
    		} else {
    			orden = ordenAnterior.toBuilder().build();
    		}
    		
    		String rmcSerieFolio = fc.getRmcSerieNumero();
    		String[] arr = rmcSerieFolio.split("-");
    		orden.setRmcserieportal(arr[0]);
    		orden.setRmcfolioportal(Integer.valueOf(arr[1]));
    		actualizarFactura(orden, rmcSerieFolio);
    		if (primeraVez) {
    			primeraVez = false;
    		} else {
    			ordenesNuevasCache.put(rmcSerieFolio, orden);	
    		}
    	}
	}

	private void actualizarRemisionFactura(TrazabilidadFacDto orden) {
		if (orden.getRmcserie() != null && orden.getRmcfolio() != null) {
			String rmcSerieFolio = orden.getRmcserie() + "-" + orden.getRmcfolio();
			DetecnoRM rm = recepcionesCache.get(rmcSerieFolio);
			if (rm != null) {
				orden.setRmcserieportal(orden.getRmcserie());
				orden.setRmcfolioportal(orden.getRmcfolio());
				orden.setFecharemisionportal(rm.getRmcFecha());
				recepcionesCache.remove(rmcSerieFolio);
			}
			actualizarFactura(orden, rmcSerieFolio);
		}
	}

	private void actualizarFactura(TrazabilidadFacDto orden, String rmcSerieFolio) {
		DetecnoFC fc = facturasCache.get(rmcSerieFolio);
		if (fc != null) {
			orden.setUuidportal(fc.getUuid());
			orden.setFacfecha(fc.getFacFecha());
			orden.setFactotal(fc.getFacTotal());
			facturasCache.remove(rmcSerieFolio);
		}
	}

	private void leerInformacionDetecno(TrazabilidadFacDto orden) {
		
		String oc = orden.getOcserie() + "-" + orden.getOcfolio();
		
		orden.setFechaocportal(
				detecnoService.getOrdenCompra(oc, orden.getOcempresa()));
		
		ordenCache.clear();
		ordenCache.put("anterior", orden);
		
		List<DetecnoRM> recepciones = detecnoService.getRecepciones(oc, orden.getOcempresa());
		recepcionesCache.clear();
		if (recepciones != null) {
			for (DetecnoRM recepcion: recepciones) {
				recepcionesCache.put(recepcion.getRmcSerieNumero(), recepcion);
			}			
		}
		
		List<DetecnoFC> facturas = detecnoService.getFacturas(oc, orden.getOcempresa());
		facturasCache.clear();
		if (facturas != null) {
			for (DetecnoFC factura: facturas) {
				facturasCache.put(factura.getRmcSerieNumero(), factura);
			}			
		}
	}
    
    private String convertToJson(List<TrazabilidadFacDto> ordenesFactura) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
            
            return mapper.writeValueAsString(ordenesFactura);
            
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    @Transactional
    public void actualizar(LocalDate dateInicial, LocalDate dateFinal) {
        log.info("Fecha inicial: " + dateInicial.toString());
        log.info("Fecha final: " + dateFinal.toString());
        
        procesarRangoFechas(dateInicial, dateFinal);
    }

    @Transactional
    public void actualizarPorOrden(String empresaOrdenCompra) {
    	
    	empresaOrdenCompra = empresaOrdenCompra.replace("-", ",").toUpperCase();
    	ejecutarBloque(empresaOrdenCompra);
    }

    @Transactional
	public void actualizarEstatusImportantes() {
		LocalDate dateInicial = null;
		LocalDate dateFinal = null;
		int diasAtrasEstatusImportantes = Integer.parseInt(configuracionService.getByNombre("trazabilidad.diasAtras.EstatusImportantes"));
		int diasProceso = Integer.parseInt(configuracionService.getByNombre("trazabilidad.diasProcesoBatch"));
		if (diasProceso == 0) {
			dateInicial = LocalDate.parse(configuracionService.getByNombre("trazabilidad.fechaInicial"));
		} else {
			dateInicial = LocalDate.now().minusDays(diasProceso);
		}
		
		dateFinal = dateInicial.minusDays(1);
		dateInicial = dateFinal.minusDays(diasAtrasEstatusImportantes);
		
		log.info("Fecha inicial: " + dateInicial.toString());
		log.info("Fecha final: " + dateFinal.toString());
		
		procesarRangoFechasEstatusPendientes(dateInicial, dateFinal);
		
	}

    private void procesarRangoFechasEstatusPendientes(LocalDate dateInicial, LocalDate dateFinal) {

    	List<String> ordenesCompra = trazabilidadRepository.getEstatusPendientes(dateInicial, dateFinal);
    	ejecutarBloques(ordenesCompra);
	}
    
	private void ejecutarBloques(List<String> ordenesCompra) {
		int contador = 0;
		String ordenJoin ="";
		for (String orden: ordenesCompra) {
			ordenJoin = ordenJoin + "|" + orden ;
			contador++;
			if (contador > 200) {
				ejecutarBloque(ordenJoin.substring(1));
				contador = 0;
				ordenJoin ="";
			}
		}
		if (!ordenJoin.isEmpty()) {
			ejecutarBloque(ordenJoin.substring(1));
		}		
	}

	private void ejecutarBloque(String ordenJoin) {
		
		List<TrazabilidadFacDto> ordenesFactura = prepararOrdenes(ordenJoin);

        String ordenesJson = convertToJson(ordenesFactura);
        if (ordenesJson != null && !ordenesJson.isEmpty()) {
            trazabilidadRepository.actualizarTrazabilidadFac(ordenesJson);
        }
	}

}
