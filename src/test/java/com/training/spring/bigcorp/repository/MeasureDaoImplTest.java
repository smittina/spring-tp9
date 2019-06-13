package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.RealCaptor;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class MeasureDaoImplTest {

    @Autowired
    private MeasureDao measureDao;

    @Autowired
    EntityManager entityManager;

    @Test
    public void findById(){
        Optional<Measure> measure = measureDao.findById(-1L);
        Assertions.assertThat(measure)
                .get()
                .extracting("valueInWatt")
                .containsExactly(1_000_000);

        //Assertions.assertThat(measure.getInstant()).isEqualTo(Instant.parse("2018-08-09T11:00:00.000Z"));
        //Assertions.assertThat(measure.getValueInWatt()).isEqualTo(1_000_000);
        //Assertions.assertThat(measure.getCaptor().getName()).isEqualTo("Eolienne");
        //Assertions.assertThat(measure.getCaptor().getSite().getName()).isEqualTo("Bigcorp Lyon");
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown(){
        Optional<Measure> measure = measureDao.findById(1L);
        Assertions.assertThat(measure).isEmpty();
    }

    @Test
    public void findAll(){
        List<Measure> measures = measureDao.findAll();
        Assertions.assertThat(measures).hasSize(10);

    }

    @Test
    public void findTopByCaptorIdOrderByInstantDesc() {
        Measure lastMeasure = measureDao.findTopByCaptorIdOrderByInstantDesc("c1");
        Assertions.assertThat(lastMeasure.getId()).isEqualTo(-5L);
        Assertions.assertThat(lastMeasure.getInstant()).isEqualTo(Instant.parse("2018-08-09T11:04:00.000Z"));
        Assertions.assertThat(lastMeasure.getValueInWatt()).isEqualTo(1_009_678);
        Assertions.assertThat(lastMeasure.getCaptor().getName()).isEqualTo("Eolienne");
        Assertions.assertThat(lastMeasure.getCaptor().getSite().getName()).isEqualTo("Bigcorp Lyon");
    }

    @Test
    public void findMeasureByIntervalAndCaptor(){
        List<Measure> measures = measureDao.findMeasureByIntervalAndCaptor(Instant.parse("2018-08-09T11:00:00.000Z"),
                Instant.parse("2018-08-09T11:10:00.000Z"),"c1");

        Assertions.assertThat(measures).hasSize(5);
    }

    @Test
    public void create(){
        Captor captor = new RealCaptor("New Captor", new Site("New Site"));
        captor.setId("c1");

        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.save(new Measure(Instant.now(),2_000_000, captor));
        Assertions.assertThat(measureDao.findAll())
                .hasSize(11);
    }

    @Test
    public void update(){
        Optional<Measure> measure = measureDao.findById(-1L);
        Assertions.assertThat(measure)
                .get()
                .extracting("valueInWatt")
                .containsExactly(1_000_000);
        measure.ifPresent(m->{
            m.setValueInWatt(2_000_000);
            measureDao.save(m);
        });

        measure = measureDao.findById(-1L);
        Assertions.assertThat(measure)
                .get()
                .extracting("valueInWatt")
                .containsExactly(2_000_000);
    }

    @Test
    public void deleteById(){
        Assertions.assertThat(measureDao.findAll()).hasSize(10);
        measureDao.deleteById(-1L);
        Assertions.assertThat(measureDao.findAll()).hasSize(9);
    }

    @Test
    public void deleteByCaptorId(){
        Assertions
                .assertThat(measureDao.findAll().stream().filter(m->
                        m.getCaptor().getId().equals("c1"))).hasSize(5);

        measureDao.deleteByCaptorId("c1");

        Assertions
                .assertThat(measureDao.findAll().stream().filter(m->
                        m.getCaptor().getId().equals("c1"))).isEmpty();
    }

    @Test
    public void preventConcurrentWrite(){
        Measure measure = measureDao.getOne(-1L);
        Assertions.assertThat(measure.getVersion()).isEqualTo(0);

        entityManager.detach(measure);
        measure.setValueInWatt(2_000_000);

        Measure attachedMeasure = measureDao.save(measure);
        entityManager.flush();

        Assertions.assertThat(attachedMeasure.getValueInWatt()).isEqualTo(2_000_000);
        Assertions.assertThat(attachedMeasure.getVersion()).isEqualTo(1);

        Assertions.assertThatThrownBy(()-> measureDao.save(measure))
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }







}
