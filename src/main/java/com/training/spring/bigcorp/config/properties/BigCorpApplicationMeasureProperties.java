package com.training.spring.bigcorp.config.properties;



/**
 * Représente les informations de l'application concernant les propriétés des mesures
 */
public class BigCorpApplicationMeasureProperties {
    /**
     * Mesure par défault pour un captor fixe
     */
    private int defaultFixed;
    /**
     * Mesure par défaut pour un capteur simulé
     */
    private int defaultSimulated;
    /**
     * Mesure par défaut pour un capteur réel
     */
    private int defaultReal;

    public int getDefaultFixed() {
        return defaultFixed;
    }

    public void setDefaultFixed(int defaultFixed) {
        this.defaultFixed = defaultFixed;
    }

    public int getDefaultSimulated() {
        return defaultSimulated;
    }

    public void setDefaultSimulated(int defaultSimulated) {
        this.defaultSimulated = defaultSimulated;
    }

    public int getDefaultReal() {
        return defaultReal;
    }

    public void setDefaultReal(int defaultReal) {
        this.defaultReal = defaultReal;
    }
}
