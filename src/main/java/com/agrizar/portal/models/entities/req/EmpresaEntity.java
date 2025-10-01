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
@Table(name = "empresas")
public class EmpresaEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "empresaid")
    private Integer empresaId;
    
    @Column(name = "empresacodigo")
    private String codigo;
    
    @Column(name = "rfc")
    private String rfc;

    @Column(name = "apikey")
    private String apikey;

}
