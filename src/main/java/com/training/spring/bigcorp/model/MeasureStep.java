package com.training.spring.bigcorp.model;

/**
 * Enumération représente un pas de mesure
 */
public enum MeasureStep {
    ONE_MINUTE(60),
    FIFTEEN_MINUTES(60*15),
    THIRTY_MINUTES(60*30),
    ONE_HOUR(60*60),
    ONE_DAY(60*60*24);

    /**
     * La durée, en secondes
     */
    private int durantionInSecondes;

    MeasureStep(int durationInSecondes){
        this.durantionInSecondes = durationInSecondes;
    }

    public int getDurantionInSecondes(){
        return durantionInSecondes;
    }
}
