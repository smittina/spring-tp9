package com.training.spring.bigcorp.repository;

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
public class SiteDaoImpl implements SiteDao {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public SiteDaoImpl(NamedParameterJdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public void create(Site site) {
        Map<String,Object> paramMap = new HashMap<String,Object>();
        paramMap.put("id",site.getId());
        paramMap.put("name",site.getName());
        jdbcTemplate.update("INSERT INTO SITE(id, name) values (:id, :name)",paramMap);

    }

    @Override
    public Site findById(String s) {
        Site site = null;
        try{
            site = jdbcTemplate.queryForObject("select id, name from SITE where id = :id",
                    new MapSqlParameterSource("id",s),
                    this::siteMapper);

            return site;
        }
        catch(EmptyResultDataAccessException e){
            return null;
        }
    }

    private Site siteMapper(ResultSet rs, int rowNum) throws SQLException{
        Site site = new Site(rs.getString("name"));
        site.setId(rs.getString("id"));
        return site;
    }

    @Override
    public List<Site> findAll() {
        return jdbcTemplate.query("select id, name from SITE",
                this::siteMapper);
    }

    @Override
    public void update(Site site) {
        jdbcTemplate.update("update SITE set name = :name where id = :id",
                new MapSqlParameterSource()
                        .addValue("id",site.getId())
                        .addValue("name",site.getName()));


    }

    @Override
    public void deleteById(String s) {
        SqlParameterSource params = new MapSqlParameterSource("id",s);
        jdbcTemplate.update("delete from SITE where id = :id",params);
    }
}
