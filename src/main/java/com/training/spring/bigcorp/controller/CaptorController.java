package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.config.SecurityConfig;
import com.training.spring.bigcorp.controller.dto.CaptorDto;
import com.training.spring.bigcorp.exception.NotFoundException;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Controleur pour les Capteurs
 */
@Controller
@Transactional
@RequestMapping("sites/{siteId}/captors")
public class CaptorController {

    /**
     * Dao Site
     */
    @Autowired
    private SiteDao siteDao;

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
     * Transforme un Captor en CaptorDto
     * @param captor le capteur à transformer
     * @return le DtoCaptor
     */
    private CaptorDto toDto(Captor captor){
        if(captor instanceof FixedCaptor){
            return new CaptorDto(captor.getSite(), (FixedCaptor) captor);
        }
        if(captor instanceof SimulatedCaptor){
            return new CaptorDto(captor.getSite(), (SimulatedCaptor) captor);
        }
        if(captor instanceof RealCaptor){
            return new CaptorDto(captor.getSite(), (RealCaptor) captor);
        }
        throw new IllegalStateException("Captor type not managed by app");
    }

    /**
     * Transforme une liste de capteurs en liste de DtoCaptor
     * @param captors
     * @return
     */
    private List<CaptorDto> toDtos(List<Captor> captors){
        return captors.stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(CaptorDto::getName))
                .collect(Collectors.toList());
    }

    /**
     * Renvoie un ModelAndView avec l'ensemble des capteurs
     * @param siteId id du site auquel est relié le capteur
     * @return
     */
    @GetMapping
    public ModelAndView findAll(@PathVariable String siteId){
        return new ModelAndView("captors")
                .addObject("captors", toDtos(captorDao.findBySiteId(siteId)));
    }

    /**
     * Renvoie un modelAndView avec un capteur en particulier
     * @param siteId id du site
     * @param id id du capteur
     * @return
     */
    @GetMapping("/{id}")
    public ModelAndView findById(@PathVariable String siteId, @PathVariable String id){
       Captor captor = captorDao.findById(id).orElseThrow(NotFoundException::new);
        return new ModelAndView("captor")
                .addObject("captor",toDto(captor));
    }

    /**
     * Création d'un nouveau capteur attaché à un site
     * @param siteId
     * @return
     */
    @GetMapping("/create")
    public ModelAndView create(@PathVariable String siteId){
        Site site = siteDao.findById(siteId).orElseThrow(NotFoundException::new);

        return new ModelAndView("captor")
                .addObject("captor", new CaptorDto(site,new FixedCaptor(null,site,null)));
    }

    /**
     * Sauvegarde un Captor dans la base de données / Seuls les Admins y ont accès
     * @param siteId id du site
     * @param captorDto captorDto du capteur
     * @return
     */
    @Secured(SecurityConfig.ROLE_ADMIN)
    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save(@PathVariable String siteId, CaptorDto captorDto){
        Site site = siteDao.findById(siteId).orElseThrow(NotFoundException::new);
        Captor captor = captorDto.toCaptor(site);
        captorDao.save(captor);
        return new ModelAndView("site")
                .addObject("site",site);
    }

    /**
     * Permet de supprimer un capteur rattaché à un site
     * @param siteId id du site
     * @param id id du capteur à supprimer
     * @return
     */
    @Secured(SecurityConfig.ROLE_ADMIN)
    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable String siteId, @PathVariable String id){

        Site site = siteDao.findById(siteId).orElseThrow(NotFoundException::new);
        // Suppressions des mesures
        measureDao.deleteByCaptorId(id);
        captorDao.deleteById(id);
        return new ModelAndView("site")
                .addObject("site",site);
    }
}
