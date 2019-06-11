package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.utils.H2DateConverter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MeasureDaoImpl implements MeasureDao {

    @PersistenceContext
    EntityManager em;

    @Override
    public void persist(Measure measure) {
        em.persist(measure);
    }


    @Override
    public Measure findById(Long id) {
        return em.find(Measure.class,id);
    }

    @Override
    public List<Measure> findAll() {
        return em.createQuery("select m from Measure m inner join m.captor c inner join c.site s",Measure.class)
                .getResultList();
    }



    @Override
    public void deleteById(Measure measure) {
        em.remove(measure);
    }


}
