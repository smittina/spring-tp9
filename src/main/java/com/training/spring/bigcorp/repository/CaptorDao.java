package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface CaptorDao extends CrudDao<Captor,String> {

    // Read
    List<Captor> findBySite(String siteId);
}
