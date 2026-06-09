package com.agrizar.portal.services.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agrizar.portal.mapper.TrazabilidadFacMapper;
import com.agrizar.portal.models.dto.EmpresaDto;
import com.agrizar.portal.models.dto.TrazabilidadFacDto;
import com.agrizar.portal.models.response.DetecnoFC;
import com.agrizar.portal.models.response.DetecnoOrdenCompraResponse;
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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrazabilidadServiceImpl implements TrazabilidadService {

	private final ObjectMapper objectMapper;
	private final TrazabilidadRepository trazabilidadRepository;
	private final OrdenFacturaService ordenFacturaService;
	private final DetecnoService detecnoService;
	private final ConfiguracionService configuracionService;
	private final EmpresaService empresaService;
	private final Map<String, DetecnoRM> recepcionesCache = new HashMap<>();
	private final Map<String, DetecnoFC> facturasXRemisionCache = new HashMap<>();
	private final Map<String, DetecnoFC> facturasXUuidCache = new HashMap<>();
	private final Map<String, TrazabilidadFacDto> ordenCache = new HashMap<>();
	private final Map<String, TrazabilidadFacDto> ordenesNuevasCache = new HashMap<>();
    
	// Objetivo: Leer RD, hispatec y el portal para complementar la informacion de una OC
	//           La fuente es RD e hispatec y de alli vamos al portal
	//
	// Informacion       Informacion de hispatec   Informacion del portal
	//
	// Orden de Compra                             fechaocportal
	//
	// Remision          rmcusuario
	//                   rmcfecha                  fecharemisionportal
	//                   fechaPublicacion          fechaPublicacion
	//                   rmcserie                  rmcserieportal
	//                   rmcfolio                  rmcfolioportal
	//                                         
	// Factura           facserie
	//                   facfolio
	//                   facusuarioh
	//                   fcfecha
	//                   docfechah
	//                   fcfolio
	//                   uuid                      uuidportal
	//                                             facfecha
	//                                             factotal
	//
	
	// Constructor manual reemplazando @RequiredArgsConstructor
	public TrazabilidadServiceImpl(
			TrazabilidadRepository trazabilidadRepository,
			OrdenFacturaService ordenFacturaService,
			DetecnoService detecnoService,
			ConfiguracionService configuracionService,
			EmpresaService empresaService) {
		
		this.trazabilidadRepository = trazabilidadRepository;
		this.ordenFacturaService = ordenFacturaService;
		this.detecnoService = detecnoService;
		this.configuracionService = configuracionService;
		this.empresaService = empresaService;
		
		// Configurar ObjectMapper una sola vez
		this.objectMapper = new ObjectMapper();
		this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		this.objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
	}
	
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
		log.info("Fecha inicial: {}", dateInicial);
		log.info("Fecha final: {}", dateFinal);
		
		procesarRangoFechas(dateInicial, dateFinal);
	}
	
	private void procesarRangoFechas(LocalDate dateInicial, LocalDate dateFinal) {
		
    	List<String> ordenesCompra = trazabilidadRepository.getOrdenesCompra(dateInicial, dateFinal);
		
    	ejecutarBloques(ordenesCompra, dateInicial, dateFinal);
	}

	private void ejecutarBloques(List<String> ordenesCompra, LocalDate dateInicial, LocalDate dateFinal) {
		int contador = 0;
		StringBuilder ordenJoin = new StringBuilder();

		for (String orden: ordenesCompra) {			
	        if (contador > 0) {
	            ordenJoin.append("|");
	        }
	        ordenJoin.append(orden);
			contador++;
			
			if (contador > 200) {
				ejecutarBloque(dateInicial, dateFinal, ordenJoin.toString());
				contador = 0;
				ordenJoin.setLength(0);
			}
		}
		if (!ordenJoin.isEmpty()) {
			ejecutarBloque(dateInicial, dateFinal, ordenJoin.toString());
		}		
	}

	private void ejecutarBloque(LocalDate dateInicial, LocalDate dateFinal, String ordenJoin) {
		
		List<TrazabilidadFacDto> ordenesFactura = prepararOrdenes(ordenJoin);

        String ordenesJson = convertToJson(ordenesFactura);
        if (ordenesJson != null && !ordenesJson.isEmpty()) {
        	if (dateInicial == null && dateFinal == null) {
        		trazabilidadRepository.actualizarTrazabilidadFac(ordenesJson);	
        	} else {
        		trazabilidadRepository.actualizarTrazabilidadFac(ordenesJson, dateInicial, dateFinal);
        	}
            
        }
	}

	private List<TrazabilidadFacDto> prepararOrdenes(String ordenJoin) {
    	String empresaOrdenAnterior = "";
    	boolean esServicio = false;
    	boolean esServicioAnterior = false;
    	
    	log.info("hispatec comienza");
		List<TrazabilidadFacDto> ordenesFactura = TrazabilidadFacMapper.convertToDtos(ordenFacturaService.getOrdenes(ordenJoin));
		log.info("hispatec termina {} ordenes recuperadas", ordenesFactura.size());
		
		depurarOrdenesNulas(ordenesFactura);
		
		for (TrazabilidadFacDto orden : ordenesFactura) {
			
			String empresaOrden = orden.getOcempresa() + orden.getOcserie() + orden.getOcfolio();

			// Identifico que tipo de requisicion es la OC (1=Material, 2=Servicio)
			esServicioAnterior = esServicio;
			esServicio = trazabilidadRepository
					.isOCServicio (orden.getOcempresa(), orden.getOcserie() + orden.getOcfolio()) == 1;
			
			if (esServicio) {
				orden.setRmcusuario(null);
				orden.setRmcfecha(null);
				orden.setRmcserie(null);
				orden.setRmcfolio(null);
			}
						
			if (empresaOrden.equals(empresaOrdenAnterior)) {
				TrazabilidadFacDto anterior = ordenCache.get("anterior");
				orden.setFechaocportal(anterior.getFechaocportal());
				orden.setSaldototal(anterior.getSaldototal());
				orden.setSaldoconsumido(anterior.getSaldoconsumido());
				orden.setSaldoinsoluto(anterior.getSaldoinsoluto());
				
			} else {

				if (!empresaOrdenAnterior.isEmpty()) {
					insertarRecepcionesFacturas(esServicioAnterior);
				}
				
				EmpresaDto empresa = empresaService.getEmpresaWithToken(orden.getOcempresa());
				if (empresa == null) continue;

				leerInformacionDetecno(orden);

				empresaOrdenAnterior = empresaOrden;
			}
			
			updRemisionFactura(orden, esServicio);
		}
		
		insertarRecepcionesFacturas(esServicio);
		
		ordenesFactura.addAll(ordenesNuevasCache.values());
		ordenesNuevasCache.clear();
		
		return ordenesFactura;
	}

	private void depurarOrdenesNulas(List<TrazabilidadFacDto> ordenesFactura) {
	    // Agrupar por ocempresa, ocserie y ocfolio
	    Map<String, List<TrazabilidadFacDto>> grupos = ordenesFactura.stream()
	        .collect(Collectors.groupingBy(item -> 
	            item.getOcempresa() + "|" + item.getOcserie() + "|" + item.getOcfolio()
	        ));
	    
	    // Depurar solo los grupos que tengan más de 1 elemento
	    grupos.forEach((clave, grupo) -> {
	        if (grupo.size() > 1) {
	            grupo.removeIf(item -> 
	                item.getRmcserie() == null 
	                && item.getRmcfolio() == null
	                && item.getRmcusuario() == null
	                && item.getFacserie() == null
	                && item.getFacfolio() == null
	                && item.getUuid() == null
	            );
	        }
	    });
	}
	
	private void insertarRecepcionesFacturas(boolean esServicio) {
		
		// Agregar recepciones y facturas del portal que no pudieron hacer match con hispatec
		insertarRecepciones(esServicio);
		insertarFacturas(esServicio);
	}

	private void insertarRecepciones(boolean esServicio) {
		
		if (recepcionesCache.isEmpty()) return;
					
    	for (DetecnoRM rm : recepcionesCache.values()) {
    		
    		// insertar una nueva
    		TrazabilidadFacDto orden = ordenCache.get("anterior").toBuilder().build();
    		limpiarDatosRemisionFacturaOrden(orden);
    		
    		String rmcSerieFolio = rm.getRmcSerieNumero();
    		setRemisionPortal(orden, rmcSerieFolio, false);

    		updFacturaBaseRemision(orden, rmcSerieFolio);

			String key = orden.getOcempresa() + orden.getOcserie() + orden.getOcfolio() + rmcSerieFolio;
			ordenesNuevasCache.put(key, orden);	
    	}
	}
	
	private boolean estaVacio(TrazabilidadFacDto orden) {
		boolean result = false;
		if (
			 (orden.getRmcserieportal() == null || orden.getRmcserieportal().isEmpty())
		  && (orden.getRmcfolioportal() == null || orden.getRmcfolioportal() == 0)
		  && (orden.getFecharemisionportal() == null)
		  && (orden.getFechaPublicacion() == null)
		  && (orden.getUuidportal() == null || orden.getUuidportal().isEmpty())
		  && (orden.getFacfecha() == null)
		  && (orden.getFactotal() == null || orden.getFactotal() == 0f)
		   ) {
			result = true;
		}
		
		return result;
	}

	private void limpiarDatosRemisionFacturaOrden(TrazabilidadFacDto orden) {
		// Datos de remisión Hispatec
		orden.setRmcusuario(null);
		orden.setRmcserie(null);
		orden.setRmcfolio(null);
		orden.setRmcfecha(null);
		
		// Datos de remisión Portal
		orden.setRmcserieportal(null);
		orden.setRmcfolioportal(null);
		orden.setFecharemisionportal(null);
		orden.setFechaPublicacion(null);

		// Datos de factura Hispatec
		orden.setFacserie(null);
		orden.setFacfolio(null);
		orden.setUuid(null);
		
		// Datos de factura Portal
		orden.setUuidportal(null);
		orden.setFacfecha(null);
		orden.setFactotal(null);

		// Ultimas 4 columnas
		orden.setFacusuarioh(null);
		orden.setFcfecha(null);
		orden.setDocfechah(null);
		orden.setFcfolio(null);		
	}

	private void insertarFacturas(boolean esServicio) {
		
		Collection<DetecnoFC> facturas = null;
		
		if (esServicio) {
			facturas = facturasXUuidCache.values();
		} else {
			facturas = facturasXRemisionCache.values();
		}
		
		if (facturas.isEmpty()) return;
		
		boolean primeraVez = true;
		TrazabilidadFacDto ordenAnterior = ordenCache.get("anterior").toBuilder().build();

		for (DetecnoFC fc : facturas) {
    		TrazabilidadFacDto orden = null;
    		// Sobreescribir anterior vacio o insertar una nueva
    		if (primeraVez && estaVacio(ordenAnterior)) {
    			orden = ordenCache.get("anterior");
    		} else {
    			orden = ordenAnterior.toBuilder().build();
        		limpiarDatosRemisionFacturaOrden(orden);
    			if (primeraVez) primeraVez = false;
    		}

    		String rmcSerieFolio = fc.getRmcSerieNumero();
    		setRemisionPortal(orden, rmcSerieFolio, false);
    		if (esServicio) {
        		setDatosFactura(orden, fc);
    		} else {
	    		updFacturaBaseRemision(orden, rmcSerieFolio);    			
    		}
    		
    		if (primeraVez) {
    			primeraVez = false;
    		} else {
    			String key = orden.getOcempresa() + orden.getOcserie() + orden.getOcfolio() + rmcSerieFolio;
    			ordenesNuevasCache.put(key, orden);	
    		}
		}
	}

	private void updRemisionFactura(TrazabilidadFacDto orden, boolean esServicio) {
		
		// Asignar remision del portal y factura del portal a la OC obtenida de hispatec
		if (esServicio) {
			
			DetecnoFC fc = facturasXUuidCache.get(orden.getUuid());
			if (fc != null) {
	    		setRemisionPortal(orden, fc.getRmcSerieNumero(), true);
	    		setDatosFactura(orden, fc);
			}			

		} else {
			if (orden.getRmcserie() != null && orden.getRmcfolio() != null) {
				String rmcSerieFolio = orden.getRmcserie() + "-" + orden.getRmcfolio();
				setRemisionPortal(orden, rmcSerieFolio, true);
				updFacturaBaseRemision(orden, rmcSerieFolio);
			}			
		}
	}

	private void setRemisionPortal(TrazabilidadFacDto orden, String rmcSerieFolio, boolean eliminar) {
		
		//Actualiza la remision del portal en base a la remision de Hispatec o la remision del portal o la remision de la factura
		DetecnoRM rm = recepcionesCache.get(rmcSerieFolio);
		if (rm != null) {
			orden.setFecharemisionportal(rm.getRmcFecha());
			orden.setFechaPublicacion(rm.getFechaPublicacion());
			if (eliminar) recepcionesCache.remove(rmcSerieFolio);
			
			String[] arr = parsearRmcSerieFolio(rmcSerieFolio);
			if (arr != null && arr.length > 1) {
			    orden.setRmcserieportal(arr[0]);
			    orden.setRmcfolioportal(validaFolio(arr[1]));
			} else {
				if (rmcSerieFolio.contains("REMS")) {
					String folio = rmcSerieFolio.substring(4);
					orden.setRmcserieportal("REMS");
					orden.setRmcfolioportal(validaFolio(folio));
				} else {
					orden.setRmcserieportal("");
					orden.setRmcfolioportal(validaFolio(rmcSerieFolio));
				}    			
			}
		}		
	}
	
	private void updFacturaBaseRemision(TrazabilidadFacDto orden, String rmcSerieFolio) {
		
	// Si hay remisionHispatec/remisionPortal/remision Factura, entonces se asigna la factura del portal
		DetecnoFC fc = facturasXRemisionCache.get(rmcSerieFolio);
		if (fc != null) {			
			setDatosFactura(orden, fc);
		}			
	}

	private void setDatosFactura(TrazabilidadFacDto orden, DetecnoFC fc) {
		String uuid = fc.getUuid();
		String rmcSerieFolio = fc.getRmcSerieNumero();
		
		orden.setUuidportal(fc.getUuid());
		orden.setFacfecha(fc.getFacFecha());
		orden.setFactotal(fc.getFacTotal());
		
		facturasXUuidCache.remove(uuid);
		facturasXRemisionCache.remove(rmcSerieFolio);		
	}

	private void leerInformacionDetecno(TrazabilidadFacDto orden) {
		
		String oc = orden.getOcserie() + "-" + orden.getOcfolio();
		
		DetecnoOrdenCompraResponse ocPortal = detecnoService.getOrdenCompra(oc, orden.getOcempresa());
		if (ocPortal != null) {
			orden.setFechaocportal(ocPortal.getFechapublicacionoc());
			orden.setSaldototal(ocPortal.getTotal());
			orden.setSaldoconsumido(ocPortal.getSaldoconsumido());
			orden.setSaldoinsoluto(ocPortal.getSaldoinsoluto());
		}
		
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
		facturasXRemisionCache.clear();
		facturasXUuidCache.clear();
		if (facturas != null) {
			for (DetecnoFC factura: facturas) {
				facturasXRemisionCache.put(factura.getRmcSerieNumero(), factura);
				facturasXUuidCache.put(factura.getUuid(), factura);
			}			
		}
	}
    
    private String convertToJson(List<TrazabilidadFacDto> ordenesFactura) {
        try {
            return objectMapper.writeValueAsString(ordenesFactura);
        } catch (JsonProcessingException e) {
        	log.error("Error al convertir a JSON", e);
            return null;
        }
    }
    
    @Transactional
    public void actualizar(LocalDate dateInicial, LocalDate dateFinal) {
		log.info("Fecha inicial: {}", dateInicial);
		log.info("Fecha final: {}", dateFinal);        
        
        procesarRangoFechas(dateInicial, dateFinal);
    }

    @Transactional
    public void actualizarPorOrden(String empresaOrdenCompra) {
    	
    	empresaOrdenCompra = empresaOrdenCompra.replace("-", ",").toUpperCase();
    	ejecutarBloque(null, null, empresaOrdenCompra);
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
		
		log.info("Fecha inicial: {}", dateInicial);
		log.info("Fecha final: {}", dateFinal);
		
		procesarRangoFechasEstatusPendientes(dateInicial, dateFinal);
		
	}

    private void procesarRangoFechasEstatusPendientes(LocalDate dateInicial, LocalDate dateFinal) {

    	List<String> ordenesCompra = trazabilidadRepository.getEstatusPendientes(dateInicial, dateFinal);
    	ejecutarBloques(ordenesCompra, null, null);
	}
    	
	private String[] parsearRmcSerieFolio(String rmcSerieFolio) {
	    if (rmcSerieFolio.contains("-")) {
	        return rmcSerieFolio.split("-", 2); // Limite de 2 para mejor performance
	    }
	    return null;
	}

	private Integer validaFolio(String folio) {
		if (folio.matches("\\d+")) return Integer.valueOf(folio);
		return null;
	}

}
