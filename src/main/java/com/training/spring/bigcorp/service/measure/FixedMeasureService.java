package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service("fixedMeasureService")
public class FixedMeasureService implements MeasureService {

    @Autowired
    BigCorpApplicationProperties properties;

    @Override
    public List<Measure> readMeasures(Captor captor, Instant start, Instant end, MeasureStep step) {
        checkReadMeasuresArgs(captor,start,end,step);
        List<Measure> measures = new ArrayList<>();
        Instant current = start;

        while(current.isBefore(end)){
            measures.add(new Measure(current,properties.getMeasure().getDefaultFixed(),captor));
            current = current.plusSeconds(step.getDurantionInSecondes());
        }
        return measures;
    }
}
