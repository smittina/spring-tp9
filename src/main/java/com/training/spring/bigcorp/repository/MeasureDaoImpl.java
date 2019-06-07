package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.utils.H2DateConverter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MeasureDaoImpl implements MeasureDao {
    private NamedParameterJdbcTemplate jdbcTemplate;
    private H2DateConverter h2DateConverter;

    public MeasureDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, H2DateConverter h2DateConverter){
        this.jdbcTemplate = jdbcTemplate;
        this.h2DateConverter = h2DateConverter;
    }

    private static String SELECT_WITH_JOIN = "SELECT m.id, m.instant, m.value_in_watt, m.captor_id, c.name as captor_name, c.site_id, s.name as site_name" +
            " FROM MEASURE m inner join Captor c on m.captor_id=c.id inner join site s on c.site_id = s.id";

    @Override
    public void create(Measure measure) {
        jdbcTemplate.update("insert into Measure (id, instant, value_in_watt, captor_id) " +
        "values (:id, :instant, :valueInWatt,:captor_id)",
        new MapSqlParameterSource()
                .addValue("id", measure.getId())
                .addValue("instant", measure.getInstant())
                .addValue("valueInWatt", measure.getValueInWatt())
                .addValue("captor_id",
                        measure.getCaptor().getId()));
    }


    @Override
    public Measure findById(Long id) {
        try {
            return jdbcTemplate.queryForObject(SELECT_WITH_JOIN + " where m.id = :id",
                    new MapSqlParameterSource("id", id),
                    this::measureMapper);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<Measure> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOIN, this::measureMapper);
    }

    @Override
    public void update(Measure measure) {
        jdbcTemplate.update("update Measure set instant = :instant, value_in_watt = :valueInWatt where id = :id",
        new MapSqlParameterSource()
                .addValue("id", measure.getId())
                .addValue("instant", measure.getInstant())
                .addValue("valueInWatt",
                        measure.getValueInWatt()));
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update("delete from Measure where id =:id",
                new MapSqlParameterSource("id", id));
    }

    private Measure measureMapper(ResultSet rs, int rowNum) throws SQLException{
        Site site = new Site(rs.getString("site_name"));
        site.setId(rs.getString("site_id"));

        Captor captor = new Captor(rs.getString("captor_name"),site);
        captor.setId(rs.getString("captor_id"));

        Measure measure = new Measure(h2DateConverter.convert(rs.getString("instant")),
                rs.getInt("value_in_watt"),
                captor);

        measure.setId(rs.getLong("id"));
        return measure;
    }
}
