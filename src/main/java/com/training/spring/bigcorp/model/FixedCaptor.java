package com.training.spring.bigcorp.model;


import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;

/**
 * Représente un capteur avec des valeurs fixes
 */
@Entity
@DiscriminatorValue("FIXED")
public class FixedCaptor extends Captor {

    /**
     * Energie par défaut en watt
     */
    @NotNull
    private Integer defaultPowerInWatt;


    public FixedCaptor(){
        super();
    }



    public FixedCaptor(String name, Site site){
        super(name,site,PowerSource.FIXED);
    }

    public FixedCaptor(String name, Site site, Integer defaultPowerInWatt){
        super(name,site, PowerSource.FIXED);
        this.defaultPowerInWatt = defaultPowerInWatt;
    }

    public Integer getDefaultPowerInWatt() {
        return defaultPowerInWatt;
    }

    public void setDefaultPowerInWatt(Integer defaultPowerInWatt) {
        this.defaultPowerInWatt = defaultPowerInWatt;
    }
}
