package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Measure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

/**
 * Dao Measure
 */
public interface MeasureDao extends JpaRepository<Measure, Long> {

    /**
     * Permet de supprimer des mesures en fonction du capteur auquel elles sont rattachées
     * @param captorId
     */
    public void deleteByCaptorId(String captorId);

    /**
     * Permet de trouver une liste de mesures dans un interval de temps donné et en fonction d'un capteur
     * @param start début de l'intervalle
     * @param end fin de l'intervalle
     * @param captorId id du capteur
     * @return
     */
    @Query("select m from Measure m where m.captor.id=:captorId and m.instant between :start and :end")
    public List<Measure> findMeasureByIntervalAndCaptor(@Param("start") Instant start, @Param("end") Instant end, @Param("captorId") String captorId);

    /**
     * Permet de trouver la dernière mesure enregistrée par un capteur
     * @param captorId id du capteur
     * @return
     */
    public Measure findTopByCaptorIdOrderByInstantDesc(String captorId);
}
