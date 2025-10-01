package com.agrizar.portal.models.entities.hispatec;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ruben.martinez
 */
@Getter @Setter
@Entity
@Table(name = "Articulos")
public class ArticuloEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;
    
    @Column(name = "Codigo")
    private String codigo;
    
    @Column(name = "Nombre")
    private String nombre;
    
    @Column(name = "Descripcion")
    private String descripcion;
    
}
