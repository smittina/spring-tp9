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
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class CaptorDaoImplTest {

    @Autowired
    private CaptorDao captorDao;

    private Site site;

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private EntityManager entityManager;

    @Before
    public void init(){
        site = new Site("name");
        site.setId("site1");
    }

    @Test
    public void findById(){
        Optional<Captor> captor = captorDao.findById("c1");
        Assertions.assertThat(captor)
                .get()
                .extracting("name")
                .containsExactly("Eolienne");
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown(){
        Optional<Captor> captor = captorDao.findById("unknown");
        Assertions.assertThat(captor).isEmpty();
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
    public void findByExample(){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", match -> match.ignoreCase().contains())
                .withMatcher("site", match -> match.contains())
                .withIgnorePaths("id");
        ;

        Site site = siteDao.getOne("site1");
        Captor captor = new Captor("lienn",site);

        List<Captor> captors = captorDao.findAll(Example.of(captor,matcher));

        Assertions.assertThat(captors)
                .hasSize(1)
                .extracting("id", "name")
                .containsExactly(Tuple.tuple("c1","Eolienne"));
    }

    @Test
    public void create(){
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        Captor captor = new Captor("New Captor", site);
        captor.setPowerSource(PowerSource.SIMULATED);

        captorDao.save(captor);

        Assertions.assertThat(captorDao.findAll())
                .hasSize(3)
                .extracting(Captor::getName)
                .contains("Eolienne","Laminoire à chaud", "New Captor");
    }

    @Test
    public void update(){
        Optional<Captor> captor = captorDao.findById("c1");
        Assertions.assertThat(captor)
                .get()
                .extracting("name")
                .containsExactly("Eolienne");

        captor.ifPresent(c->{
            c.setName("Captor Updated");
            captorDao.save(c);
        });

        captor = captorDao.findById("c1");
        Assertions.assertThat(captor)
                .get()
                .extracting("name")
                .containsExactly("Captor Updated");
    }

    @Test
    public void deleteById(){
        Captor newCaptor = new Captor("New Captor",site);
        captorDao.save(newCaptor);
        Assertions.assertThat(captorDao.findById(newCaptor.getId())).isNotEmpty();

        captorDao.delete(newCaptor);
        Assertions.assertThat(captorDao.findById(newCaptor.getId())).isEmpty();
    }

    @Test
    public void deleteByIdShouldThrowEceptionWhenIdIsUsedAsForeignKey(){
        /*Assertions.assertThatThrownBy(()->captorDao.deleteById(captorDao.findById("c1")))
                .isExactlyInstanceOf(DataIntegrityViolationException.class);*/

        Captor captor = captorDao.getOne("c1");
        Assertions
                .assertThatThrownBy(()->{
                    captorDao.delete(captor);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }
}
