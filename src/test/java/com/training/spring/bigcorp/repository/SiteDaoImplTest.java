package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class SiteDaoImplTest {

    @Autowired
    private SiteDao siteDao;
    private Site site;

    @Test
    public void findById(){
        Site site = siteDao.findById("site1");
        Assertions.assertThat(site.getName()).isEqualTo("Bigcorp Lyon");
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown(){
        Site site = siteDao.findById("unknown");
        Assertions.assertThat(site).isNull();
    }

    @Test
    public void findAll(){
        List<Site> sites = siteDao.findAll();
        Assertions.assertThat(sites)
                .hasSize(1)
                .extracting("id","name")
                .contains(Tuple.tuple("site1","Bigcorp Lyon"));
    }

    @Test
    public void create(){
        Assertions.assertThat(siteDao.findAll()).hasSize(1);

        siteDao.persist(new Site("New Site"));

        Assertions.assertThat(siteDao.findAll())
                .hasSize(2)
                .extracting(Site::getName)
                .contains("Bigcorp Lyon", "New Site");

    }

    @Test
    public void update(){
        Site site = siteDao.findById("site1");
        Assertions.assertThat(site.getName()).isEqualTo("Bigcorp Lyon");

        site.setName("Name Updated");
        siteDao.persist(site);

        site = siteDao.findById("site1");
        Assertions.assertThat(site.getName()).isEqualTo("Name Updated");
    }

    @Test
    public void deleteById(){
        Site newSite = new Site("New Site");
        siteDao.persist(newSite);
        Assertions.assertThat(siteDao.findById(newSite.getId())).isNotNull();

        siteDao.deleteById(siteDao.findById(newSite.getId()));
        Assertions.assertThat(siteDao.findById(newSite.getId())).isNull();

    }

}
