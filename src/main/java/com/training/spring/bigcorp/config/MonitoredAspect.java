package com.training.spring.bigcorp.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class MonitoredAspect {

    private static Logger LOG = LoggerFactory.getLogger(MonitoredAspect.class);

    @Before("@annotation(Monitored)")
    public void logServiceBeforeCall(JoinPoint jp){
        LOG.info("Appel finder :"+jp.getSignature());
    }
}
