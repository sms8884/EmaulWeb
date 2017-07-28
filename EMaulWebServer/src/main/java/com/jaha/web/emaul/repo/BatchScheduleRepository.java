package com.jaha.web.emaul.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jaha.web.emaul.model.BatchSchedule;

/**
 * Created by shavrani on 16. 05. 11..
 */
@Repository
public interface BatchScheduleRepository extends JpaRepository<BatchSchedule, Long> {

    public BatchSchedule findBySeq(int seq);
    
    public List<BatchSchedule> findByBatchGroupKey(String batchGroupKey);
    
    public int deleteByBatchGroupKey(String batchGroupKey);
    
}
