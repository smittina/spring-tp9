package com.training.spring.bigcorp.config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Représente les informations de l'application
 */
@Component
@ConfigurationProperties(prefix="bigcorp")
public class BigCorpApplicationProperties {
    private String name;
    private Integer version;
    private Set<String> emails;
    private String webSiteUrl;

    @NestedConfigurationProperty
    private BigCorpApplicationMeasureProperties measure;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }

    public String getWebSiteUrl() {
        return webSiteUrl;
    }

    public void setWebSiteUrl(String webSiteUrl) {
        this.webSiteUrl = webSiteUrl;
    }

    public BigCorpApplicationMeasureProperties getMeasure() {
        return measure;
    }

    public void setMeasure(BigCorpApplicationMeasureProperties measure) {
        this.measure = measure;
    }
}
