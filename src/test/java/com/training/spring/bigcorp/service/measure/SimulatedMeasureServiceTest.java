package com.training.spring.bigcorp.service.measure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.model.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringRunner.class)
//@ContextConfiguration(classes={MeasureServiceConfigurationTest.class})
//@SpringBootTest(classes={SimulatedMeasureService.class, BigCorpApplicationProperties.class})
//@EnableConfigurationProperties
@RestClientTest(SimulatedMeasureService.class)
public class SimulatedMeasureServiceTest {

    //@Autowired
    //private BigCorpApplicationProperties bigCorpApplicationProperties;

    @Autowired
    MockRestServiceServer server;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimulatedMeasureService service;
    /**
     * Captor used in tests
     */
    private SimulatedCaptor captor = new SimulatedCaptor("test", new Site("site1"),500000,1000000);

    @Test
    public void readMeasures() throws Exception{
        Instant start = Instant.parse("2018-09-01T22:00:00Z");
        Instant end = Instant.parse("2018-09-01T22:30:00Z");

        List<MeasureDto> expectedMeasures = Arrays.asList(
                new MeasureDto(Instant.parse("2018-09-01T22:00:00Z"),1234),
                new MeasureDto(Instant.parse("2018-09-01T22:30:00Z"),4567)
        );

        String expectedJson = objectMapper.writeValueAsString(expectedMeasures);
        String request = "http://localhost:8090/measures?start=2018-09-01T22:00:00Z&"+
                "end=2018-09-01T22:30:00Z&"+
                "min=500000&"+
                "max=1000000&"+
                "step=3600";

        this.server.expect(MockRestRequestMatchers.requestTo(request))
                .andRespond(MockRestResponseCreators.withSuccess(expectedJson, MediaType.APPLICATION_JSON));

        List<Measure> measures = service.readMeasures(captor,start,end,MeasureStep.ONE_HOUR);

        assertThat(measures).hasSize(2);
        assertThat(measures)
                .extracting("instant","valueInWatt")
                .containsExactly(
                    tuple(Instant.parse("2018-09-01T22:00:00Z"),1234),
                    tuple(Instant.parse("2018-09-01T22:30:00Z"),4567)
                );
    }
}
