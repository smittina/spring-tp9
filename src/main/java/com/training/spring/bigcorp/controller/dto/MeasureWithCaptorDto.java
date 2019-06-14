package com.training.spring.bigcorp.controller.dto;

import com.training.spring.bigcorp.model.Captor;

import java.time.Instant;

/**
 * Représente un Dto de Measure plus complet car prend en compte les informations du capteur
 */
public class MeasureWithCaptorDto {

    /**
     * Captor Id
     */
    private final String captorId;
    /**
     * Captor name
     */
    private final String captorName;
    /**
     * Site Id
     */
    private final String siteId;
    /**
     * Site Name
     */
    private final String siteName;
    /**
     * Instant
     */
    private final Instant instant;
    /**
     * Value of measure in watt
     */
    private final Integer valueInWatt;

    public MeasureWithCaptorDto(Captor captor, Instant instant, Integer valueInWatt){
        this.captorId = captor.getId();
        this.captorName = captor.getName();
        this.siteId = captor.getSite().getId();
        this.siteName = captor.getSite().getName();
        this.instant = instant;
        this.valueInWatt = valueInWatt;
    }

    // GETTERS AND SETTERS

    public String getCaptorId() {
        return captorId;
    }

    public String getCaptorName() {
        return captorName;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public Instant getInstant() {
        return instant;
    }

    public Integer getValueInWatt() {
        return valueInWatt;
    }
}
