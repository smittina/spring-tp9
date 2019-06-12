package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.controller.dto.CaptorDto;
import com.training.spring.bigcorp.model.*;
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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Transactional
@RequestMapping("sites/{siteId}/captors")
public class CaptorController {

    @Autowired
    private SiteDao siteDao;

    @Autowired
    private CaptorDao captorDao;

    @Autowired
    private MeasureDao measureDao;

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

    private List<CaptorDto> toDtos(List<Captor> captors){
        return captors.stream()
                .map(this::toDto)
                .sorted(Comparator.comparing(CaptorDto::getName))
                .collect(Collectors.toList());
    }

    @GetMapping
    public ModelAndView findAll(@PathVariable String siteId){
        return new ModelAndView("captors")
                .addObject("captors", toDtos(captorDao.findBySiteId(siteId)));
    }

    @GetMapping("/{id}")
    public ModelAndView findById(@PathVariable String siteId, @PathVariable String id){
       Captor captor = captorDao.findById(id).orElseThrow(IllegalArgumentException::new);
        return new ModelAndView("captor")
                .addObject("captor",toDto(captor));
    }

    /**
     * Création d'un nouveau capteur attaché à un site
     * @param model
     * @param siteId
     * @return
     */
    @GetMapping("/create")
    public ModelAndView create(Model model, @PathVariable String siteId){
        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);

        return new ModelAndView("captor")
                .addObject("captor", new CaptorDto(site,new FixedCaptor(null,site,null)));
    }



    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ModelAndView save(@PathVariable String siteId, CaptorDto captorDto){
        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);
        Captor captor = captorDto.toCaptor(site);
        captorDao.save(captor);
        return new ModelAndView("site")
                .addObject("site",site);
    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable String siteId, @PathVariable String id){

        Site site = siteDao.findById(siteId).orElseThrow(IllegalArgumentException::new);
        // Suppressions des mesures
        measureDao.deleteByCaptorId(id);
        captorDao.deleteById(id);
        return new ModelAndView("site")
                .addObject("site",site);
    }
}
