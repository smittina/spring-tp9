package com.training.spring.bigcorp.repository;

import com.training.spring.bigcorp.model.Measure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MeasureDao extends JpaRepository<Measure, Long> {

    public void deleteByCaptorId(String captorId);
}
