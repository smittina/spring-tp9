package com.training.spring.bigcorp.service.measure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.training.spring.bigcorp.controller.dto.MeasureDto;
import com.training.spring.bigcorp.model.*;
import com.training.spring.bigcorp.repository.CaptorDao;
import com.training.spring.bigcorp.repository.MeasureDao;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Classe de tests de la Classe MeasureServiceImpl
 */
@RunWith(SpringRunner.class)
@RestClientTest(MeasureServiceImpl.class)
public class MeasureServiceImplTest {

    /**
     * Classe interne de configuration
     */
    @TestConfiguration
    @ComponentScan("com.training.spring.bigcorp.service.measure")
    public static class MeasureServiceImplTestConfig{
        /**
         * Dao Measure by Mockito
         */
        public static MeasureDao measureDao = Mockito.mock(MeasureDao.class);

        /**
         * Renvoie un Dao Captor via Mockito
         * @return Dao Captor
         */
        @Bean
        public CaptorDao captorDao(){
            return Mockito.mock(CaptorDao.class);
        }

        /**
         * Renvoie le Dao Measure
         * @return Dao Measure
         */
        @Bean
        public MeasureDao measureDao(){
            return measureDao;
        }
    }

    /**
     * Service Measure
     */
    @Autowired
    private MeasureService service;

    /**
     * Service Mockito pour le REST
     */
    @Autowired
    private MockRestServiceServer server;

    /**
     * Object Mapper
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Captors used in tests
     */
    private SimulatedCaptor simulatedCaptor;
    private FixedCaptor fixedCaptor;
    private RealCaptor realCaptor;

    @Before
    public void init(){
        Site site = new Site("Florange");
        simulatedCaptor = new SimulatedCaptor("test",site,500_000,1_000_000);
        simulatedCaptor.setId("id1");
        fixedCaptor = new FixedCaptor("test",site,500_000);
        fixedCaptor.setId("id2");
        realCaptor = new RealCaptor("test",site);
        realCaptor.setId("id3");
    }

    @Test
    public void realdMeasures(){
        Instant start = Instant.parse("2018-09-01T21:00:00Z");
        Instant end = Instant.parse("2018-09-01T23:30:00Z");

        List<Measure> expectedMeasures = Arrays.asList(
          new Measure(Instant.parse("2018-09-01T21:00:00Z"), 1000, realCaptor),
          new Measure(Instant.parse("2018-09-01T21:15:00Z"),2000,realCaptor),
          new Measure(Instant.parse("2018-09-01T23:30:00Z"),4567,realCaptor)
        );

        Mockito.when(MeasureServiceImplTestConfig.measureDao.findMeasureByIntervalAndCaptor(
                eq(start),
                eq(end),
                anyString()))
                .thenReturn(expectedMeasures);

        List<Measure> measures = service.readMeasures(realCaptor,start,end,MeasureStep.ONE_HOUR);

        Assertions.assertThat(measures).hasSize(3);
        // And we hase a value for each hour of a period
        Assertions.assertThat(measures)
                .extracting("instant","valueInWatt")
                .containsExactly(
                        tuple(Instant.parse("2018-09-01T21:00:00Z"),1500),
                        tuple(Instant.parse("2018-09-01T22:00:00Z"),0),
                        tuple(Instant.parse("2018-09-01T23:00:00Z"),4567));
    }

    @Test
    public void readMeasuresThrowsExceptionWhenArgIsNull(){
        Instant start = Instant.parse("2018-09-01T22:00:00Z");
        Instant end = start.plusSeconds(60 * 60 * 24);
        Assertions.assertThatThrownBy(() -> service.readMeasures(null, start, end,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Captor is required");
        Assertions.assertThatThrownBy(() -> service.readMeasures(realCaptor, null, end,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start is required");
        Assertions.assertThatThrownBy(() -> service.readMeasures(simulatedCaptor, start, null,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("end is required");
        Assertions.assertThatThrownBy(() -> service.readMeasures(fixedCaptor, start, end, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("step is required");
        Assertions.assertThatThrownBy(() -> service.readMeasures(realCaptor, end, start,
                MeasureStep.ONE_DAY))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("start must be before end");
    }

    @Test
    public void readAndSaveRealMeasure() throws Exception{
        MeasureDto expectedMeasure = new MeasureDto(Instant.parse("2018-09-01T21:00:00Z"),10_000_000);
        String expectedJson = objectMapper.writeValueAsString(expectedMeasure);
        String request = "http://localhost:8090/measures/one?lastValue=0&variance=1000000";

        this.server.expect(MockRestRequestMatchers.requestTo(request))
                .andRespond(MockRestResponseCreators.withSuccess(expectedJson, MediaType.APPLICATION_JSON));

        Measure measure = service.readAndSaveMeasure(realCaptor);

        Assertions.assertThat(measure.getCaptor()).isEqualTo(realCaptor);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(10_000_000);
    }

    @Test
    public void readAndSaveSimulatedMeasure() throws Exception{
        MeasureDto expectedMeasure = new MeasureDto(Instant.parse("2018-09-01T21:00:00Z"),
                10_000_000);
        String expectedJson = objectMapper.writeValueAsString(expectedMeasure);
        String request =
                "http://localhost:8090/measures/one?lastValue=0&variance=1000000";
        this.server.expect(MockRestRequestMatchers.requestTo(request))
                .andRespond(MockRestResponseCreators.withSuccess(expectedJson,
                        MediaType.APPLICATION_JSON));
        Measure measure = service.readAndSaveMeasure(simulatedCaptor);
        Assertions.assertThat(measure.getCaptor()).isEqualTo(simulatedCaptor);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(10_000_000);
    }

    @Test
    public void readAndSaveFixedMeasure() {
        Measure measure = service.readAndSaveMeasure(fixedCaptor);
        Assertions.assertThat(measure.getCaptor()).isEqualTo(fixedCaptor);
        Assertions.assertThat(measure.getValueInWatt()).isEqualTo(500_000);
    }

}
