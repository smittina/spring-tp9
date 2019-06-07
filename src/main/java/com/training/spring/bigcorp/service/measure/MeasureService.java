package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;

import java.time.Instant;
import java.util.List;

public interface MeasureService {

    List<Measure> readMeasures(Captor captor, Instant start, Instant end, MeasureStep step);

    /**
     * Permet de vérifier les valeurs des différents arguments
     * @param captor
     * @param start
     * @param end
     * @param step
     */
    default void checkReadMeasuresArgs(Captor captor, Instant start, Instant end, MeasureStep step){
        if(captor == null){
            throw new IllegalArgumentException("Captor is required");
        }
        if(start == null){
            throw new IllegalArgumentException("start is required");
        }
        if(end == null){
            throw new IllegalArgumentException("end is required");
        }
        if(step == null){
            throw new IllegalArgumentException("step is required");
        }
        if(start.isAfter(end)){
            throw new IllegalArgumentException("start must be before end");
        }
    }
}
