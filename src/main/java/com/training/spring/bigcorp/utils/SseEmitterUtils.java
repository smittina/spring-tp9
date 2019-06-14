package com.training.spring.bigcorp.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Utilitaire qui permet de libérer facilement les ressources
 * et de facilement accéder à tous les SseEmitter ouverts
 */
@Component
public class SseEmitterUtils {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SseEmitterUtils.class);

    /**
     * Liste d'emetteurs
     */
    private Set<SseEmitter> emitters = new HashSet<>();

    /**
     * Permet de créer un emetteur
     * @return
     */
    public SseEmitter createEmitter(){
        SseEmitter sseEmitter = new SseEmitter(2000L);
        emitters.add(sseEmitter);
        sseEmitter.onCompletion(() -> this.remove(sseEmitter, null));
        sseEmitter.onTimeout(() -> this.remove(sseEmitter, null));
        sseEmitter.onError((err) -> this.remove(sseEmitter, err));
        return sseEmitter;
    }

    /**
     * Permet de supprimer un emmeteur
     * @param emitter
     * @param error
     */
    private void remove(SseEmitter emitter, Throwable error){
        if(error != null){
            LOGGER.error("Error on sseEmitter",error);
        }
        if(emitters.contains(emitter)){
            emitters.remove(emitter);
        }
    }

    /**
     * Donne la liste des emmeteurs
     * @return
     */
    public Set<SseEmitter> getEmitters(){
        return Collections.unmodifiableSet(emitters);
    }
}
