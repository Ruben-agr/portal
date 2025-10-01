package com.agrizar.portal.models.entities.req;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter @Setter @ToString
public class BaseEntity {

	@Column(name = "usuarioregistro", updatable = false)
    private String usuarioRegistro;

	@Column(name = "fecharegistro", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaRegistro;
	
    @Column(name = "usuariomodificacion", insertable = false)
    private String usuarioModificacion;
    
    @Column(name = "fechamodificacion", insertable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaModificacion;
        
}
