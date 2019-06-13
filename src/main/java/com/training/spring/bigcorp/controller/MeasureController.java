package com.training.spring.bigcorp.controller;

import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.measure.SimulatedMeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Transactional
@RequestMapping("/measures/captors/{id}/last/hours/{nbHours}")
public class MeasureController {

    @Autowired
    private SimulatedMeasureService simulatedMeasureService;

    @Autowired
    private CaptorDao captorDao;

    @GetMapping
    public List<MeasureDto> findAll(@PathVariable String id, @PathVariable int nbHours){
        Captor captor = captorDao.findById(id).orElseThrow(IllegalArgumentException::new);
        if (captor.getPowerSource() == PowerSource.SIMULATED) {
            return simulatedMeasureService.readMeasures(((SimulatedCaptor) captor),
                    Instant.now().minus(Duration.ofHours(nbHours)).truncatedTo(ChronoUnit.MINUTES),
                    Instant.now().truncatedTo(ChronoUnit.MINUTES),
                    MeasureStep.ONE_MINUTE)
                    .stream()
                    .map(m -> new MeasureDto(m.getInstant(), m.getValueInWatt()))
                    .collect(Collectors.toList());
        }
// Pour
        // Pour le moment on ne gère qu'un seul type
        return new ArrayList<>();
    }
}
