package com.training.spring.bigcorp.model;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

/**
 * Représente un capteur avec des valeurs réelles
 */
@Entity
@DiscriminatorValue("REAL")
public class RealCaptor extends Captor {

    public RealCaptor(){
        super();
        // Use only by serializer and deserializer
    }

    public RealCaptor(String name, Site site){

        super(name,site, PowerSource.REAL);
    }
}
