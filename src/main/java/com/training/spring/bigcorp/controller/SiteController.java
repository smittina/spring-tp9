package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.model.Site;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.repository.SiteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.awt.event.MouseAdapter;

@Controller
@RequestMapping("/sites")
@Transactional
public class SiteController {

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private CaptorDao captorDao;

    @Autowired
    private MeasureDao measureDao;

    @GetMapping
    public ModelAndView list(){
        return new ModelAndView("sites").addObject("sites",siteDao.findAll());
    }

    @GetMapping("/{id}")
    public ModelAndView findById(@PathVariable String id){
        return new ModelAndView("site")
                .addObject("site",siteDao.findById(id).orElseThrow(IllegalArgumentException::new));
    }

    @GetMapping("/create")
    public ModelAndView create(Model model){
        return new ModelAndView("site")
                .addObject("site", new Site());
    }

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save(Site site){
        // Si pas d'Id => Mode Création
        if(site.getId() == null){
            return new ModelAndView("site")
                    .addObject("site", siteDao.save(site));
        }
        // Sinon => Mode Modification
        else{
            // On charge l'entité correspondant à l'Id
            Site siteToPersist = siteDao.findById(site.getId()).orElseThrow(IllegalArgumentException::new);
            // L'utilisateur ne peut chager que le nom du site sur l'écran
            siteToPersist.setName(site.getName());
            // Comme on est en contexte transitionnel => Pas besoin d'appeler save - l'object est automatiquement persisté
            return new ModelAndView("sites")
                    .addObject("sites",siteDao.findAll());
        }
    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable String id){
        /* Comme les capteurs sont liés à un site et les mesures sont liés à un capteur
         Nous devons faire le ménage pour ne pas avoir d'erreurs à la suppression d'un site utilisé ailleurs dans la base*/
        Site site = siteDao.findById(id).orElseThrow(IllegalArgumentException::new);
        // Suppressions des mesures
        site.getCaptors().forEach(c->measureDao.deleteByCaptorId(c.getId()));
        // Suppression des capteurs
        captorDao.deleteBySiteId(id);
        siteDao.delete(site);
        return new ModelAndView("sites")
                .addObject("sites",siteDao.findAll());
    }
}
