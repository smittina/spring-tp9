package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.RealCaptor;
import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.SiteDao;
import com.training.spring.bigcorp.service.measure.MeasureServiceConfigurationTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;


import static org.assertj.core.api.Assertions.assertThat;
import java.util.Collections;
import java.util.Set;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes={SiteServiceImplTest.SiteServiceTestConfiguration.class, MeasureServiceConfigurationTest.class})
public class SiteServiceImplTest {

    @Configuration
    @ComponentScan("com.training.spring.bigcorp.service, com.training.spring.bigcorp.repository")
    static class SiteServiceTestConfiguration{}

    @Autowired
    private SiteService siteService;

    @Rule
    public OutputCapture output = new OutputCapture();

    @Test
    public void findByIdShouldReturnNullWhenIdIsNull(){
        // Initialisation
        String siteId = null;

        // Appel du SUT
        Site site = siteService.findById(siteId);

        // Vérification
        assertThat(site).isNull();
    }

    @Test
    public void findById(){
        // Initialisation
        String siteId = "site1";
        Set<Captor> expectedCpators = Collections.singleton(new RealCaptor("Capteur A", new Site("site1")));

        // Appel du SUT
        Site site = siteService.findById(siteId);

        // Vérification
        assertThat(site.getId()).isEqualTo(siteId);
        assertThat(site.getName()).isEqualTo("Florange");
        assertThat(site.getCaptors()).isEqualTo(expectedCpators);
    }




}