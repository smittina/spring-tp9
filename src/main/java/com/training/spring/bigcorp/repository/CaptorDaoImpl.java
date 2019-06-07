package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Site;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class CaptorDaoImpl implements CaptorDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    private static String SELECT_WITH_JOIN = "SELECT c.id, c.name, c.site_id, s.name" +
            " FROM Captor c inner join Site s on c.site_id = s.id";


    public CaptorDaoImpl(NamedParameterJdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Captor> findBySite(String siteId) {
        return jdbcTemplate.query(SELECT_WITH_JOIN + " where c.site_id = :site_id",
                this::captorMapper);
    }

    @Override
    public void create(Captor captor) {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("id",captor.getId());
        paramMap.put("name",captor.getName());
        paramMap.put("site_id", captor.getSite().getId());
        jdbcTemplate.update("INSERT INTO CAPTOR(id, name, site_id) values (:id, :name, :site_id)",paramMap);


    }

    @Override
    public Captor findById(String s) {
        Captor captor = null;
        try{
            captor = jdbcTemplate.queryForObject(SELECT_WITH_JOIN+" where c.id = :id",
                    new MapSqlParameterSource("id",s),
                    this::captorMapper);

            return captor;
        }
        catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    @Override
    public List<Captor> findAll() {
        return jdbcTemplate.query(SELECT_WITH_JOIN,this::captorMapper);
    }

    private Captor captorMapper(ResultSet rs, int rowNum) throws SQLException{
        Site site = new Site(rs.getString("name"));
        site.setId(rs.getString("site_id"));

        Captor captor = new Captor(rs.getString("name"),site);
        captor.setId(rs.getString("id"));
        return captor;
    }

    @Override
    public void update(Captor captor) {
        jdbcTemplate.update("update CAPTOR set name = :name, site_id = :site_id where id = :id",
                new MapSqlParameterSource()
                        .addValue("id",captor.getId())
                        .addValue("name",captor.getName())
                        .addValue("site_id", captor.getSite().getId()));
    }

    @Override
    public void deleteById(String s) {
        SqlParameterSource params = new MapSqlParameterSource("id",s);
        jdbcTemplate.update("delete from CAPTOR where id = :id",params);
    }
}
