package com.training.spring.bigcorp.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.Instant;
import java.util.Objects;

@Entity
public class Measure {

    /**
     * Identifiant
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Instant au format UTC où la date a été lue
     */
    @NotNull
    @Past
    private Instant instant;

    /**
     * Valeur en watt
     */
    @NotNull
    private Integer valueInWatt;

    /**
     * Capteur
     */
    @ManyToOne
    private Captor captor;

    @Version
    private int version;

    public Measure(){

    }

    /**
     * Constructeur
     * @param instant
     * @param valueInWatt
     * @param captor
     */
    public Measure(Instant instant, Integer valueInWatt, Captor captor) {

        this.id = id;
        this.instant = instant;
        this.valueInWatt = valueInWatt;
        this.captor = captor;
    }


    public Instant getInstant() {
        return instant;
    }

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    public Integer getValueInWatt() {
        return valueInWatt;
    }

    public void setValueInWatt(Integer valueInWatt) {
        this.valueInWatt = valueInWatt;
    }

    public Captor getCaptor() {
        return captor;
    }

    public void setCaptor(Captor captor) {
        this.captor = captor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public int hashCode() {
        return Objects.hash(instant, valueInWatt, captor);
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Measure measure = (Measure) obj;
        return Objects.equals(instant, measure.instant)
                && Objects.equals(valueInWatt, measure.valueInWatt)
                && Objects.equals(captor, measure.captor);
    }

    @Override
    public String toString() {
        return "Measure{" +
                "instant=" + instant + '\'' +
                ",valueInWatt=" + valueInWatt + '\'' +
                ", Captor=" + captor + '\'' +
                '}';
    }

}
