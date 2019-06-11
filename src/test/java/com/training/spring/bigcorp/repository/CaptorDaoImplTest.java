package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.PowerSource;
import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class CaptorDaoImplTest {

    @Autowired
    private CaptorDao captorDao;

    private Site site;

    @Autowired
    private EntityManager entityManager;

    @Before
    public void init(){
        site = new Site("name");
        site.setId("site1");
    }

    @Test
    public void findById(){
        Captor captor = captorDao.findById("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Eolienne");
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown(){
        Captor captor = captorDao.findById("unknown");
        Assertions.assertThat(captor).isNull();
    }

    @Test
    public void findAll(){
        List<Captor> captors = captorDao.findAll();
        Assertions.assertThat(captors)
                .hasSize(2)
                .extracting("id","name")
                .contains(Tuple.tuple("c1","Eolienne"))
                .contains(Tuple.tuple("c2","Laminoire à chaud"));
    }

    @Test
    public void create(){
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        Captor captor = new Captor("New Captor", site);
        captor.setPowerSource(PowerSource.SIMULATED);

        captorDao.persist(captor);

        Assertions.assertThat(captorDao.findAll())
                .hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne","Laminoire à chaud", "New Captor");
    }

    @Test
    public void update(){
        Captor captor = captorDao.findById("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Eolienne");

        captor.setName("Captor updated");
        captorDao.persist(captor);

        captor = captorDao.findById("c1");
        Assertions.assertThat(captor.getName()).isEqualTo("Captor updated");
    }

    @Test
    public void deleteById(){
        Captor newCaptor = new Captor("New Captor",site);
        captorDao.persist(newCaptor);
        Assertions.assertThat(captorDao.findById(newCaptor.getId())).isNotNull();

        captorDao.deleteById(captorDao.findById(newCaptor.getId()));
        Assertions.assertThat(captorDao.findById(newCaptor.getId())).isNull();
    }

    @Test
    public void deleteByIdShouldThrowEceptionWhenIdIsUsedAsForeignKey(){
        /*Assertions.assertThatThrownBy(()->captorDao.deleteById(captorDao.findById("c1")))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);*/

        Captor captor = captorDao.findById("c1");
        Assertions
                .assertThatThrownBy(()->{
                    captorDao.deleteById(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }
}
