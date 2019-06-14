package com.training.spring.bigcorp.model;

/**
 * Enumeration correspondant aux différents types sources d'énergie et aussi de capteurs
 */
public enum PowerSource {
    FIXED,
    REAL,
    SIMULATED;

    static PowerSource of(String power){
        if(power == null){
            return null;
        }
        return PowerSource.valueOf(power);
    }
}
