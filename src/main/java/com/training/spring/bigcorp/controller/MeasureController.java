package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.controller.dto.MeasureWithCaptorDto;
import com.training.spring.bigcorp.exception.NotFoundException;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import com.training.spring.bigcorp.utils.SseEmitterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Rest Controlleur qui permet de gérer les données liées aux mesures des capteurs
 */
@RestController
@Transactional
@RequestMapping("/measures")
public class MeasureController {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(MeasureController.class);

    /**
     * Service pour les mesures
     */
    @Autowired
    private MeasureService measureService;

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
     * Utilitaire pour le SSE
     */
    @Autowired
    private SseEmitterUtils sseEmitterUtils;

    /**
     * Permet d'obtenir une liste de Mesures en fonction des x derniers heures
     * @param id id du capteur sur lesquelles les mesures ont été effectuées
     * @param nbHours le nombre d'heure sur lesquelles on veut les données
     * @return
     */
    @GetMapping("/captors/{id}/last/hours/{nbHours}")
    public List<MeasureDto> findAll(@PathVariable String id, @PathVariable int nbHours){
        Captor captor = captorDao.findById(id).orElseThrow(NotFoundException::new);
        if (captor.getPowerSource() == PowerSource.SIMULATED) {
            List<Measure> measures = measureService.readMeasures(captor,
                    Instant.now().minus(Duration.ofHours(nbHours)).truncatedTo(ChronoUnit.MINUTES),
                    Instant.now().truncatedTo(ChronoUnit.MINUTES),
                    MeasureStep.ONE_MINUTE);

            return measures
                    .stream()
                    .map(m -> new MeasureDto(m.getInstant(), m.getValueInWatt()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }

    /**
     * Permet d'afficher toutes les mesures des capteurs
     * @return
     */
    @GetMapping
    public ModelAndView findAll(){
        return new ModelAndView("measures")
                .addObject("captors",
                        captorDao.findAll()
                                .stream()
                                .sorted(Comparator.comparing(Captor::getName))
                                .map(c -> "{ id: '" + c.getId() + "', name: '" +
                                        c.getName() + "'}")
                                .collect(Collectors.joining(",")));
    }

    /**
     * Permet de lancer des évènements
     * @return
     */
    @GetMapping(path="/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter events(){
        return sseEmitterUtils.createEmitter();
    }

    /**
     * Méthode avec planification : toutes les 2000ms -> lit une nouvelle mesure
     */
    @Scheduled(initialDelay = 2000, fixedDelay = 2000)
    public void readMeasure() {
        captorDao
                .findAll()
                .stream()
                .map(captor -> {
                    Measure measure = measureService.readAndSaveMeasure(captor);
                    return new MeasureWithCaptorDto(captor, measure.getInstant(),
                            measure.getValueInWatt());
                })
                .forEach(this::sendEventForUser);
    }

    /**
     * Permet d'envoyer un évènement à l'utilisateur
     * @param measure la mesure à envoyer à l'utilisateur
     */
    private void sendEventForUser(MeasureWithCaptorDto measure) {
        sseEmitterUtils.getEmitters().forEach(sseEmitter -> {
            try {
                sseEmitter.send(measure);
            } catch (IOException e) {
                LOGGER.error("Error on event emit", e);
            }
        });
    }

}
