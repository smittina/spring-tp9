package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Dao des capteurs
 */
public interface CaptorDao extends JpaRepository<Captor,String> {

    /**
     * Permet de trouver une liste de capteur en fonction d'un site en particulier
     * @param siteId id du site
     * @return
     */
    List<Captor> findBySiteId(String siteId);

    /**
     * Permet de supprimer un capteur en fonction d'un site
     * @param siteId id du site
     */
    public void deleteBySiteId(String siteId);
}
