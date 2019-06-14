package com.training.spring.bigcorp.controller.dto;

import java.time.Instant;

/**
 * Représente le Dto de Measure
 */
public class MeasureDto {

    /**
     * Instant de la mesure
     */
    private final Instant instant;
    /**
     * Valeur de la mesure en watt
     */
    private final Integer valueInWatt;

    public MeasureDto(Instant instant, Integer valueInWatt){
        this.instant = instant;
        this.valueInWatt = valueInWatt;
    }

    // getters and setters

    public Instant getInstant() {
        return instant;
    }

    public Integer getValueInWatt() {
        return valueInWatt;
    }
}
