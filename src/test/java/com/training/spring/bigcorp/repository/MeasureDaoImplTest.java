package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.List;

@RunWith(SpringRunner.class)
@JdbcTest
@ContextConfiguration(classes={DaoTestConfig.class})
public class MeasureDaoImplTest {

    @Autowired
    private MeasureDao measureDao;

    @Test
    public void findById(){
        Measure measure = measureDao.findById(1L);
        Assertions.assertThat(measure.getId()).isEqualTo(1L);
        Assertions.assertThat(measure.getInstant()).isEqualTo(Instant.parse("2018-08-09T11:00:00.000Z"));
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1_000_000);
        Assertions.assertThat(measure.getCaptor().getName()).isEqualTo("Eolienne");
        Assertions.assertThat(measure.getCaptor().getSite().getName()).isEqualTo("Bigcorp Lyon");
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown(){
        Measure measure = measureDao.findById(-1L);
        Assertions.assertThat(measure).isNull();
    }

    @Test
    public void findAll(){
        List<Measure> measures = measureDao.findAll();
        Assertions.assertThat(measures).hasSize(10);

    }

    @Test
    public void create(){
        Captor captor = new Captor("New Captor", new Site("New Site"));
        captor.setId("c1");

        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.create(new Measure(Instant.now(),2_000_000, captor));
        Assertions.assertThat(measureDao.findAll())
                .hasSize(11);
    }

    @Test
    public void update(){
        Measure measure = measureDao.findById(1L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1_000_000);

        measure.setValueInWatt(2_000_000);

        measureDao.update(measure);
        measure = measureDao.findById(1L);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(2_000_000);
    }

    @Test
    public void deleteById(){
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.deleteById(1L);
        Assertions.assertThat(measureDao.findAll()).hasSize(9);
    }
}
