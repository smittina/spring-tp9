package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Site;

/**
 * Interface représentant un service pour un site
 */
public interface SiteService {
    /**
     * Donne un site en fonction de son id
     * @param siteId
     * @return
     */
    Site findById(String siteId);

}
