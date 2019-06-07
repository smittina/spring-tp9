package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.config.Monitored;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.service.measure.MeasureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("captorService")
public class CaptorServiceImpl implements CaptorService{

    /**
     * Service FixedMeasure
     */
    MeasureService fixedMeasureService;
    /**
     * Service SimulatedMeasure
     */
    MeasureService simulatedMeasureService;
    /**
     * Service RealMeasure
     */
    MeasureService realMeasureService;


    CaptorDao captorDao;


    @Autowired
    public CaptorServiceImpl(MeasureService fixedMeasureService,
                             MeasureService simulatedMeasureService,
                             MeasureService realMeasureService){
        this.fixedMeasureService = fixedMeasureService;
        this.simulatedMeasureService = simulatedMeasureService;
        this.realMeasureService = realMeasureService;

    }

    @Override
    @Monitored
    public Set<Captor> findBySite(String siteId) {
        List<Captor> captors = captorDao.findBySite(siteId);
        Set<Captor> captorsSet = captors.stream().collect(Collectors.toSet());
        return captorsSet;
    }
}
