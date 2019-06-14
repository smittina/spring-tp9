package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.*;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import org.hibernate.exception.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * Classe de tests de la classe CaptorDaoImpl
 */
@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class CaptorDaoImplTest {

    /**
     * Dao Captor
     */
    @Autowired
    private CaptorDao captorDao;

    /**
     * Dao Measure
     */
    @Autowired
    private MeasureDao measureDao;

    /**
     * Site
     */
    private Site site;

    /**
     * Dao Site
     */
    @Autowired
    private SiteDao siteDao;

    /**
     * Entity Manager
     */
    @Autowired
    private EntityManager entityManager;

    /**
     * Permet d'initialiser certains objets avant les tests
     */
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
    public void findBySite(){
        List<Captor> captors = captorDao.findBySiteId("site1");
        Assertions.assertThat(captors).hasSize(2);
    }

    @Test
    public void findByExample(){
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withMatcher("name", match -> match.ignoreCase().contains())
                .withMatcher("site", match -> match.contains())
                .withIgnorePaths("id");


        Site site = siteDao.getOne("site1");
        Captor captor = new FixedCaptor("lienn",site);

        List<Captor> captors = captorDao.findAll(Example.of(captor,matcher));

        Assertions.assertThat(captors)
                .hasSize(1)
                .extracting("id", "name")
                .containsExactly(Tuple.tuple("c1","Eolienne"));
    }

    @Test
    public void create(){
        Assertions.assertThat(captorDao.findAll()).hasSize(2);
        Captor captor = new RealCaptor("New Captor", site);
        //captor.setPowerSource(PowerSource.SIMULATED);

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
        Captor newCaptor = new RealCaptor("New Captor",site);
        captorDao.save(newCaptor);
        Assertions.assertThat(captorDao.findById(newCaptor.getId())).isNotEmpty();

        captorDao.delete(newCaptor);
        Assertions.assertThat(captorDao.findById(newCaptor.getId())).isEmpty();
    }

    @Test
    public void deleteBySiteId(){
        Assertions.assertThat(captorDao.findBySiteId("site1")).hasSize(2);
        // Pour ne pas avoir de contraintes d'intégrité on supprime toutes les mesures avant tout
        measureDao.deleteAll();
        captorDao.deleteBySiteId("site1");
        siteDao.delete(site);
        Assertions.assertThat(captorDao.findBySiteId("site1")).isEmpty();
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

    @Test
    public void preventConcurrentWrite(){
        Captor captor = captorDao.getOne("c1");
        // A la base le numéro de version est à sa valeur initiale
        Assertions.assertThat(captor.getVersion()).isEqualTo(0);

        // On détache cet objet du contexte de la persistence
        entityManager.detach(captor);
        captor.setName("Captor Updated");

        // On force la mise à jour en base (via flush) et on vérifie que l'objet retourné et attaché à la session a été mis à jour
        Captor attachedCaptor = captorDao.save(captor);
        entityManager.flush();

        Assertions.assertThat(attachedCaptor.getName()).isEqualTo("Captor Updated");
        Assertions.assertThat(attachedCaptor.getVersion()).isEqualTo(1);

        // Si maintenant je rééssaie d'enregistrer captor, comme le numéro de version est à 0 je dois avoir une exception
        Assertions.assertThatThrownBy(()->{
            captorDao.save(captor);})
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    public void createShouldThrowExceptionWhenNameIsNull(){
        Assertions
                .assertThatThrownBy(()->{
                    captorDao.save(new RealCaptor(null,site));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("ne peut pas être nul");
    }

    @Test
    public void createShouldThrowExceptionWhenNameSizeIsInvalid(){
        Assertions
                .assertThatThrownBy(()->{
                    captorDao.save(new RealCaptor("ee", site));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("la taille doit être comprise entre 3 et 100");
    }

    @Test
    public void createShouldThrowExceptionWhenMinIsSuperiorMax(){
        Assertions
                .assertThatThrownBy(()->{
                    captorDao.save(new SimulatedCaptor("name",site, 10,9));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("minPowerInWatt should be less than maxPowerInWatt");
    }
}
