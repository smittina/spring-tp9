package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Captor;

import java.util.Set;

/**
 * Interface représentant un service pour le capteur
 */
public interface CaptorService {
    /**
     * Donne une liste de Capteur en fonction d'un site en particulier
     * @param siteId id du site
     * @return
     */
    Set<Captor> findBySite(String siteId);
}
