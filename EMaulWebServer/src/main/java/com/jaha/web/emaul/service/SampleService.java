package com.jaha.web.emaul.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jaha.web.emaul.mapper.SampleMapper;

@Service
public class SampleService {

    @Autowired
    private SampleMapper sampleMapper;

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class})
    public void sample() {
        sampleMapper.insertSample();
        sampleMapper.selectSample();
    }

}
