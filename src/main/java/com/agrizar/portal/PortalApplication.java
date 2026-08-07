package com.agrizar.portal;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.agrizar.portal.enums.ETipoProceso;
import com.agrizar.portal.enums.ETipoUsuarioNotificar;
import com.agrizar.portal.services.BitacoraService;
import com.agrizar.portal.services.TrazabilidadService;
import com.agrizar.portal.util.UtilsString;
import com.agrizar.portal.services.NotificacionService;
import com.agrizar.portal.services.RemisionService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
@EnableFeignClients

public class PortalApplication implements CommandLineRunner {

    private static final String REGEX = "^\\d{3},\\p{L}{2,5}-\\d+$";
    private static final Pattern PATTERN = Pattern.compile(REGEX);
    private static final Integer DIAS_MAX_RANGO = 100;
    private static final Integer TOPE_DIAS = 12;
    private static final Integer INCREMENTO_DIAS = 6;

    @Autowired
	private BitacoraService bitacoraService;
	@Autowired
	private TrazabilidadService trazabilidadService;
	@Autowired
	private NotificacionService NotificacionService;
	@Autowired
	private RemisionService remisionService;

	public static void main(String[] args) {
	    if (args.length == 1 && "-help".equals(args[0])) {
	        mostrarUso();
	        return;
	    }
	    
	    SpringApplication.run(PortalApplication.class, args);
	}
	
    @Override
    public void run(String... args) throws Exception {
    	
    	log.info("Iniciando   ...");
        LocalDateTime inicioEjecucion = LocalDateTime.now();
        log.info("========================================");
        log.info("Iniciando proceso Portal - {}", inicioEjecucion);
        log.info("Argumentos: {}", Arrays.toString(args));
        
        int exitCode = 0;
        
    	try {
    		procesarParametros(args);
    		
    	} catch(IllegalArgumentException e) {
    		log.error("Error: ", e);
    		exitCode = 1;
    		
    	} catch(Exception e) {
		log.error("Error: ", e);
		bitacoraService.save("Trazabilidad Error: " + e.getMessage());
		NotificacionService.enviar("Proceso batch de Trazabilidad (Error)"
				, "Error en proceso batch de Trazabilidad: " + e.getMessage() + e.getMessage() + "\nStackTrace: " + getStackTraceAsString(e)
				, ETipoUsuarioNotificar.TECNICO);
		exitCode = 2;
		
    	} finally {
            LocalDateTime finEjecucion = LocalDateTime.now();
            Duration duracion = Duration.between(inicioEjecucion, finEjecucion);
            log.info("Finalizando proceso - Duración: {} segundos", duracion.getSeconds());
            log.info("========================================");
        }
    	
    	System.exit(exitCode);   
    }

    private void procesarParametros(String[] args) throws Exception {
        
        if (args.length == 0) {
        	throw new IllegalArgumentException("Debe haber al menos un parametro. Consultar -help para mas informacion.");
        }
                
        try {
        	ETipoProceso tipoProceso = ETipoProceso.getTipoProceso(Integer.parseInt(args[0]));
            log.info("Tipo de proceso: {}", tipoProceso.toString());
            
            switch (tipoProceso) {
	            case CONF_DB:
	                procesarConfiguracionDB(args);
	                break;
                case DIAS_ATRAS:
                    procesarDiasAtras(args);
                    break;
                case RANGO_FECHAS:
                    procesarRango(args);
                    break;
                case ORDEN_COMPRA:
                    procesarOrden(args);
                    break;
                case NO_MOVIMIENTO:
                    procesarNoMovimiento(args);
                    break;
                case PUBLICAR_REM:
                    procesarPublicarRemision(args);
                    break;
                default:
                    throw new IllegalArgumentException("Tipo de proceso no valido: " + tipoProceso + ". Consultar -help para mas informacion.");
            }
            
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El primer parametro debe ser un numero. Consultar -help para mas informacion.", e);
        }
    }

	private void procesarConfiguracionDB(String[] args) {
        log.info("Ejecutando proceso normal sin parametros");
        bitacoraService.save("Trazabilidad Inicia");
        trazabilidadService.actualizar();
        trazabilidadService.actualizarEstatusImportantes();
        bitacoraService.save("Trazabilidad Termina satisfactoriamente");
		NotificacionService.enviar("Proceso batch de Trazabilidad"
				, "Termino satisfactoriamente el proceso."
				, ETipoUsuarioNotificar.TECNICO);
		log.info("Proceso normal completado exitosamente");
	}

	private void procesarDiasAtras(String[] args) {

		if (args.length != 2) {
            throw new IllegalArgumentException("El tipo de proceso requiere 2 parametros: tipoProceso diasAtras");
        }
		String parDiasAtras = args[1];
		if (!UtilsString.isNumeric(parDiasAtras)) {
			throw new IllegalArgumentException("El parametro diasAtras debe ser numerico");
		}
		Integer diasAtras = Integer.parseInt(parDiasAtras);
		
		if (diasAtras > DIAS_MAX_RANGO) {
			throw new IllegalArgumentException("Rango no puede ser mayor a 100 dias");
		}
		
		LocalDate fechaFinal = LocalDate.now();
		LocalDate fechaInicial = fechaFinal.minusDays(diasAtras);
		
		log.info("Ejecutando proceso Dias atras");
		bitacoraService.save("Trazabilidad Inicia");
        procesarInicialFinal (fechaInicial, fechaFinal);
        bitacoraService.save("Trazabilidad Termina satisfactoriamente");
        log.info("Proceso Dias atras completado exitosamente");
	}

	private void procesarRango(String[] args) throws Exception {

        if (args.length != 3) {
            throw new IllegalArgumentException("El tipo de proceso requiere 3 parametros: tipoProceso fechaInicial [yyyy-MM-dd] fechaFinal [yyyy-MM-dd]");
        }
        
        try {
            LocalDate fechaInicial = LocalDate.parse(args[1]);
            LocalDate fechaFinal = LocalDate.parse(args[2]);
            
            if (fechaInicial.isAfter(fechaFinal)) {
                throw new IllegalArgumentException("La fecha inicial no puede ser posterior a la fecha final");
            }
            
            long diferenciaDias = ChronoUnit.DAYS.between(fechaInicial, fechaFinal);
    		if (diferenciaDias > DIAS_MAX_RANGO) {
    			throw new IllegalArgumentException("Rango no puede ser mayor a 100 dias");
    		}
            
            log.info("Procesando tipo Rango de fechas - Fecha inicial: {}, Fecha final: {}", fechaInicial, fechaFinal);
            bitacoraService.save("Trazabilidad Inicia");
            procesarInicialFinal (fechaInicial, fechaFinal);
            bitacoraService.save("Trazabilidad Termina satisfactoriamente");
            log.info("Proceso tipo Rango de fechas completado exitosamente");
            
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha invalido. Use formato yyyy-MM-dd", e);
        }
    }

    private void procesarInicialFinal(LocalDate fechaInicial, LocalDate fechaFinal) {
        
    	long diferenciaDias = ChronoUnit.DAYS.between(fechaInicial, fechaFinal);
        
        if (diferenciaDias < TOPE_DIAS) {
        	trazabilidadService.actualizar(fechaInicial, fechaFinal);
        } else {
        	LocalDate inicio = fechaInicial;
        	do {
        		LocalDate fin = inicio.plusDays(INCREMENTO_DIAS);
        	    if (fin.isAfter(fechaFinal) ) {
        	    	fin = fechaFinal;
        	    }
        	    trazabilidadService.actualizar(inicio, fin);
        	    inicio = fin.plusDays(1);
        		} while (fechaFinal.isAfter(inicio));            	
        }		
	}

	private void procesarOrden(String[] args) throws Exception {

        if (args.length != 2) {
            throw new IllegalArgumentException("El tipo de proceso requiere 2 parametros: tipoProceso ordenCompra [empresa,OC-99999]");
        }
        
        String ordenCompra = args[1];
        
        if (ordenCompra == null || ordenCompra.trim().isEmpty()) {
            throw new IllegalArgumentException("La orden de compra no puede estar vacia");
        }
        if(!isValida(ordenCompra)) {
        	throw new IllegalArgumentException("La orden de compra no tiene el formato correcto ejemplo: 001,OC-12345");
        }
        
        log.info("Procesando tipo Orden de compra: {}", ordenCompra);
        bitacoraService.save("Trazabilidad Inicia");
        trazabilidadService.actualizarPorOrden(ordenCompra);
        bitacoraService.save("Trazabilidad Termina satisfactoriamente");
        log.info("Proceso tipo Orden de compra completado exitosamente para orden: {}", ordenCompra);
    }

    private static boolean isValida(String texto) {
        if (texto == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(texto);
        return matcher.matches();
    }

    private void procesarPublicarRemision(String[] args) {
        log.info("Ejecutando proceso publicar remision sin parametros");
        bitacoraService.save("Publicar remision Inicia");
        remisionService.publicar();
        bitacoraService.save("Publicar remision Termina satisfactoriamente");
		NotificacionService.enviar("Proceso batch Publicar remision"
				, "Termino satisfactoriamente el proceso."
				, ETipoUsuarioNotificar.TECNICO);            	
	}

    public static void mostrarUso() {
        System.out.println("Uso de la aplicacion:");
        System.out.println("Procesos de las ordenes de compra para el portal de proveedores");
        System.out.println("");
        System.out.println("   Actualizacion de la trazabilidad de las ordenes de compra");
        System.out.println("   Parametro Descripcion                                                      Ejemplo");
        System.out.println("   10        En base a la configuracion de los parametros de la base de datos java -jar portal.jar 10");
        System.out.println("   11        Por dias atras (max 100 dias)                                    java -jar portal.jar 11 31");
        System.out.println("   12        Por rango de fechas (max 100 dias)                               java -jar portal.jar 12 2025-09-01 2025-09-10");
        System.out.println("   13        Por empresa y orden de compra                                    java -jar portal.jar 13 001,OC-99999");
        System.out.println("   14        Por un conjunto de OCs (las que no tienen movimiento)            java -jar portal.jar 14");
        System.out.println("");
        System.out.println("   Publicar en el portal remisiones pendientes");
        System.out.println("   Parametro Descripcion                                                      Ejemplo");
        System.out.println("   20        En base a la configuracion de los parametros de la base de datos java -jar portal.jar 20");

    }
    
    private String getStackTraceAsString(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

	private void procesarNoMovimiento(String[] args) {
        log.info("Ejecutando proceso Conjunto de OCs (las que no tienen movimiento)");
        bitacoraService.save("Trazabilidad Inicia");
        trazabilidadService.actualizarNoMovimiento();
        bitacoraService.save("Trazabilidad Termina satisfactoriamente");
		NotificacionService.enviar("Proceso batch de Trazabilidad"
				, "Termino satisfactoriamente el proceso."
				, ETipoUsuarioNotificar.TECNICO);
		log.info("Proceso normal completado exitosamente");
	}

}
