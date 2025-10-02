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
@Table(name = "logportal")
public class LogEntity extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "logportalid")
    private Integer logportalid;
    
    @Column(name = "codigoempresa")
    private String codigoEmpresa;
    
    @Column(name = "ordencompra")
    private String oc;

    @Column(name = "remision")
    private String remision;

    @Column(name = "logerror")
    private String logerror;

    @Column(name = "estadoid")
    private Integer estadoId;
        
    public LogEntity(Integer logportalid) {
		this.logportalid = logportalid;
	}
}
