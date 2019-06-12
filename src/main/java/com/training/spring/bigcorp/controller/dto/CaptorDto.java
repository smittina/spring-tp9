package com.training.spring.bigcorp.controller.dto;

import com.training.spring.bigcorp.model.*;

public class CaptorDto {
    private PowerSource powerSource;
    private String id;
    private String name;
    private String siteId;
    private String siteName;
    private Integer defaultPowerInWatt;
    private Integer minPowerInWatt;
    private Integer maxPowerInWatt;

    public CaptorDto(){

    }

    public CaptorDto(Site site, FixedCaptor fixedCaptor){
        this.powerSource = PowerSource.FIXED;
        this.id = fixedCaptor.getId();
        this.name = fixedCaptor.getName();
        this.siteId = site.getId();
        this.siteName = site.getName();
        this.defaultPowerInWatt = fixedCaptor.getDefaultPowerInWatt();
    }

    public CaptorDto(Site site, SimulatedCaptor simulatedCaptor){
        this.powerSource = PowerSource.SIMULATED;
        this.id = simulatedCaptor.getId();
        this.name = simulatedCaptor.getName();
        this.siteName = site.getName();
        this.siteId = site.getId();
        this.minPowerInWatt = simulatedCaptor.getMinPowerInWatt();
        this.maxPowerInWatt = simulatedCaptor.getMaxPowerInWatt();
    }

    public CaptorDto(Site site, RealCaptor realCaptor){
        this.powerSource = PowerSource.REAL;
        this.id = realCaptor.getId();
        this.name = realCaptor.getName();
        this.siteId = site.getId();
        this.siteName = site.getName();
    }

    public Captor toCaptor(Site site){
        Captor captor;
        switch (powerSource){
            case REAL:
                captor = new RealCaptor(name,site);
                break;
            case FIXED:
                captor = new FixedCaptor(name,site,defaultPowerInWatt);
                break;
            case SIMULATED:
                captor = new SimulatedCaptor(name,site,minPowerInWatt,maxPowerInWatt);
                break;
            default :
                throw new IllegalStateException("Unknown Type");
        }
        captor.setId(id);
        return captor;
    }

    public PowerSource getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(PowerSource powerSource) {
        this.powerSource = powerSource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public Integer getDefaultPowerInWatt() {
        return defaultPowerInWatt;
    }

    public void setDefaultPowerInWatt(Integer defaultPowerInWatt) {
        this.defaultPowerInWatt = defaultPowerInWatt;
    }

    public Integer getMinPowerInWatt() {
        return minPowerInWatt;
    }

    public void setMinPowerInWatt(Integer minPowerInWatt) {
        this.minPowerInWatt = minPowerInWatt;
    }

    public Integer getMaxPowerInWatt() {
        return maxPowerInWatt;
    }

    public void setMaxPowerInWatt(Integer maxPowerInWatt) {
        this.maxPowerInWatt = maxPowerInWatt;
    }
}
