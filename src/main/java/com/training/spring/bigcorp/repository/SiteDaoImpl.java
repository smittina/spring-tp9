package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Site;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class SiteDaoImpl implements SiteDao {

    @PersistenceContext
    EntityManager em;

    @Override
    public void persist(Site site) {
        em.persist(site);
    }

    @Override
    public Site findById(String s) {
        return em.find(Site.class,s);
    }

    @Override
    public List<Site> findAll() {
        return em.createQuery("select s from Site s", Site.class)
                .getResultList();
    }

    @Override
    public void deleteById(Site site) {
        em.remove(site);
    }
}
