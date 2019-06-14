package com.training.spring.bigcorp.service.measure;

import com.training.spring.bigcorp.model.Captor;
import com.training.spring.bigcorp.model.FixedCaptor;
import com.training.spring.bigcorp.model.Measure;
import com.training.spring.bigcorp.model.MeasureStep;
import com.training.spring.bigcorp.repository.MeasureDao;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Représente un service pour les mesures
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class MeasureServiceImpl implements MeasureService {

    /**
     * Dao Measure
     */
    private MeasureDao measureDao;
    /**
     * Rest Template
     */
    private RestTemplate restTemplate;

    /**
     * Constructeur
     * @param measureDao
     * @param builder
     */
    public MeasureServiceImpl(MeasureDao measureDao, RestTemplateBuilder builder){
        this.measureDao = measureDao;
        this.restTemplate = builder.setConnectTimeout(Duration.ofSeconds(1)).build();
    }

    @Override
    public List<Measure> readMeasures(Captor captor, Instant start, Instant end, MeasureStep step) {
        checkReadMeasuresArgs(captor,start,end,step);
        Set<MeasureByInterval> measureByIntervals = computeIntervals(start,end,step);
        List<Measure> measures = measureDao.findMeasureByIntervalAndCaptor(start,end,captor.getId());
        // Ajouter chacune des mesures retournées par le DAO dans le Set<MeasureByInterval>
            // 1) Parcourir la liste retournée par le DAO via l'API STREAM
            // 2) Trouver dans Set<MeasureByInterval>, l'interval dans lequel sera sauvegarder la mesure
            // 3) Ajouter dans l'intervalle correspondant, la mesure

        measures.forEach(m->{
            measureByIntervals.forEach(i->{
                if (i.contains(m.getInstant())) {
                    i.power.add(m.getValueInWatt());
                }
            });

        });

        // Parcourir la liste des intervals et les transformer en Measure
            // 1) Parcourir la liste des intervals
            // 2) Transformer les Intervals en mesures ( = moyenne des mesures dans l'interval)
        List<Measure> measuresIntervals = new ArrayList<>();
        measureByIntervals.forEach(i->{
            measuresIntervals.add(new Measure(i.getStart(),i.average(),captor));
        });

        // On trie les mesures par ordre croissant d'Instant
        List<Measure> sortedMeasures = measuresIntervals.stream()
                .sorted(Comparator.comparing(Measure::getInstant))
                .collect(Collectors.toList());

        return sortedMeasures;
    }

    /**
     * Calcule des intervals de mesures à partir d'un temps initial et final et d'un pas de mesure
     * @param start temps initial
     * @param end temps final
     * @param step pas de mesure
     * @return
     */
    private Set<MeasureByInterval> computeIntervals(Instant start, Instant end, MeasureStep step){
        Set<MeasureByInterval> measureByIntervals = new HashSet<MeasureByInterval>();
        Instant current = start;
        Instant endInstant = end.isBefore(start.plusSeconds(step.getDurantionInSecondes())) ?
                start.plusSeconds(step.getDurantionInSecondes()) : end;

        while(current.isBefore(endInstant)){
            measureByIntervals.add(new MeasureByInterval(current,current.plusSeconds(step.getDurantionInSecondes())));
            current = current.plusSeconds(step.getDurantionInSecondes());
        }

        return measureByIntervals;
    }

    /**
     * Classe interne qui représente une mesure par interval de temps donné
     */
    class MeasureByInterval{
        /**
         * Début de l'intervalle
         */
        private Instant start;
        /**
         * Fin de l'intervalle
         */
        private Instant end;
        /**
         * Liste des différentes valeurs de la mesures, en watt
         */
        private Set<Integer> power = new HashSet<>();

        /**
         * Constructeur
         * @param start
         * @param end
         */
        public MeasureByInterval(Instant start, Instant end){
            this.start = start;
            this.end = end;
        }

        /**
         * Permet de savoir si un instant en particulier est contenu de l'intervalle de mesure
         * @param instant l'instant que le souhaite tester
         * @return
         */
        public boolean contains(Instant instant){
            return (instant.equals(start) || instant.isAfter(start)) && instant.isBefore(end);
        }

        /**
         * Calcule la moyenne des différentes valeurs en watt
         * @return
         */
        public int average(){
            if(power.isEmpty()){
                return 0;
            }
            return power
                    .stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.averagingInt(t->t))
                    .intValue();
        }

        // GETTERS AND SETTERS

        public Instant getStart() {
            return start;
        }

        public void setStart(Instant start) {
            this.start = start;
        }

        public Instant getEnd() {
            return end;
        }

        public void setEnd(Instant end) {
            this.end = end;
        }
    }


    @Override
    public Measure readAndSaveMeasure(Captor captor) {
        Measure measureToPersist;
        // FixedCaptor => Retourner la valeur fixe paramétrée sur FixedCaptor
        if(captor instanceof FixedCaptor){
            measureToPersist = new Measure(Instant.now(),((FixedCaptor)captor).getDefaultPowerInWatt(),captor);

        }
        /* RealCaptor + SimulatedCaptor => s'interfacer via le client rest de Spring
           au service de mesures qui permet de donner la valeur d'une mesure
           en Appelant l'URL http://localhost:8090/measures/one et en passant 2 paramètres :

           - lastValue : derniere valeur en BDD lue pour un capteur. Si nulle => 0
           - variance : lastValue *10 / 100 quand lastValue != 0 ou égale 1_000_000 dans les autres cas
         */
        else{
            Measure lastMeasure = measureDao.findTopByCaptorIdOrderByInstantDesc(captor.getId());
            int lastValue;
            int variance;
            if(lastMeasure == null){
                lastValue = 0;
                variance = 1_000_000;
            }
            else{
                lastValue = lastMeasure.getValueInWatt();
                variance = (lastValue*10)/100;
            }
            UriComponentsBuilder builder = UriComponentsBuilder
                    .fromHttpUrl("http://localhost:8090/measures/one")
                    .queryParam("lastValue",lastValue)
                    .queryParam("variance",variance);
            System.out.println(builder.toUriString());
            measureToPersist = this.restTemplate.getForObject(builder.toUriString(),Measure.class);
            measureToPersist.setCaptor(captor);
        }
        // Une fois que la mesure est lue => persist en BDD et la retourner
        measureDao.save(measureToPersist);
        return measureToPersist;
    }


}
