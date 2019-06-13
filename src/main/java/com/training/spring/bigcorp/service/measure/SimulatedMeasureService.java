package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import com.training.spring.bigcorp.model.SimulatedCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("simulatedMeasureService")
public class SimulatedMeasureService implements MeasureService<SimulatedCaptor> {

    private RestTemplate restTemplate;

    public SimulatedMeasureService(RestTemplateBuilder builder){
        this.restTemplate = builder.setConnectTimeout(Duration.ofSeconds(1)).build();
    }

    @Override
    public List<Measure> readMeasures(SimulatedCaptor captor, Instant start, Instant end, MeasureStep step) {
        checkReadMeasuresArgs(captor,start,end,step);
        int minPowerInWatt = captor.getMinPowerInWatt();
        int maxPowerInWatt = captor.getMaxPowerInWatt();

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl("http://localhost:8090/measures")
                .path("")
                .queryParam("start",start)
                .queryParam("end",end)
                .queryParam("min", minPowerInWatt)
                .queryParam("max", maxPowerInWatt)
                .queryParam("step", step.getDurantionInSecondes());

        Measure[] measures = this.restTemplate.getForObject(builder.toUriString(), Measure[].class);
        return Arrays.asList(measures);
        /*List<Measure> measures = new ArrayList<>();
        Instant current = start;

        while(current.isBefore(end)){
            measures.add(new Measure((current),properties.getMeasure().getDefaultSimulated(),captor));
            current = current.plusSeconds(step.getDurantionInSecondes());
        }*/
        //return measures;
    }
}
