package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Site;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.hibernate.exception.ConstraintViolationException;
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
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@ComponentScan
public class SiteDaoImplTest {

    @Autowired
    private SiteDao siteDao;

    @Autowired
    EntityManager entityManager;

    private Site site;

    @Test
    public void findById(){
        //Site site = siteDao.findById("site1");
        Optional<Site> site = siteDao.findById("site1");
        Assertions
                .assertThat(site)
                .get()
                .extracting("name")
                .containsExactly("Bigcorp Lyon");
    }

    @Test
    public void findByIdShouldReturnNullWhenIdUnknown(){
        //Site site = siteDao.findById("unknown");
        Optional<Site> site = siteDao.findById("unknown");
        Assertions.assertThat(site).isEmpty();
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

        siteDao.save(new Site("New Site"));

        Assertions.assertThat(siteDao.findAll())
                .hasSize(2)
                .extracting(Site::getName)
                .contains("Bigcorp Lyon", "New Site");

    }

    @Test
    public void update(){
        Optional<Site> site = siteDao.findById("site1");

        Assertions.assertThat(site)
                .get()
                .extracting("name")
                .containsExactly("Bigcorp Lyon");

        site.ifPresent(s->{
            s.setName("Site Updated");
            siteDao.save(s);
        });

        site = siteDao.findById("site1");
        Assertions.assertThat(site)
                .get()
                .extracting("name")
                .containsExactly("Site Updated");
    }

    @Test
    public void deleteById(){
        Site newSite = new Site("New Site");
        siteDao.save(newSite);
        Assertions.assertThat(siteDao.findById(newSite.getId())).isNotEmpty();

        siteDao.delete(newSite);
        Assertions.assertThat(siteDao.findById(newSite.getId())).isEmpty();

    }

    @Test
    public void deleteByIdShouldThrowExceptionWhenIdIsUsedAsForeignKey(){
        Site site = siteDao.getOne("site1");
        Assertions
                .assertThatThrownBy(()->{
                    siteDao.delete(site);
                    entityManager.flush();
                })
                .isExactlyInstanceOf(PersistenceException.class)
                .hasCauseExactlyInstanceOf(ConstraintViolationException.class);
    }

    @Test
    public void preventConcurrentWrite(){
        Site site = siteDao.getOne("site1");
        Assertions.assertThat(site.getVersion()).isEqualTo(0);

        entityManager.detach(site);
        site.setName("Site Updated");

        Site attachedSite = siteDao.save(site);
        entityManager.flush();

        Assertions.assertThat(attachedSite.getName()).isEqualTo("Site Updated");
        Assertions.assertThat(attachedSite.getVersion()).isEqualTo(1);

        Assertions.assertThatThrownBy(()->siteDao.save(site))
                .isExactlyInstanceOf(ObjectOptimisticLockingFailureException.class);
    }

    @Test
    public void createShouldThrowExceptionWhenNameIsNull(){
        Assertions
                .assertThatThrownBy(()->{
                    siteDao.save(new Site(null));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("ne peut pas être nul");
    }

    @Test
    public void createShouldThrowExceptionWhenNameSizeIsInvalid(){
        Assertions
                .assertThatThrownBy(()->{
                    siteDao.save(new Site("ee"));
                    entityManager.flush();
                })
                .isExactlyInstanceOf(javax.validation.ConstraintViolationException.class)
                .hasMessageContaining("la taille doit être comprise entre 3 et 100");
    }

}
