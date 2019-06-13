package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Measure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

public interface MeasureDao extends JpaRepository<Measure, Long> {

    public void deleteByCaptorId(String captorId);

    @Query("select m from Measure m where m.captor.id=:captorId and m.instant between :start and :end")
    public List<Measure> findMeasureByIntervalAndCaptor(@Param("start") Instant start, @Param("end") Instant end, @Param("captorId") String captorId);

    public Measure findTopByCaptorIdOrderByInstantDesc(String captorId);
}
