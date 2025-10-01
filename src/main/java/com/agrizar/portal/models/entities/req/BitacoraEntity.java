package com.agrizar.portal.models.entities.req;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 *
 * @author ruben.martinez
 */
@Getter @Setter @ToString @NoArgsConstructor @AllArgsConstructor
@Entity
@Table(name = "bitacora")
public class BitacoraEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bitacoraid")
    private Integer bitacoraId;
    
    @Column(name = "actividad")
    private String actividad;
    
    @Column(name = "estadoid")
    private Integer estadoId;
        
    public BitacoraEntity(Integer bitacoraid) {
		this.bitacoraId = bitacoraid;
	}
}
