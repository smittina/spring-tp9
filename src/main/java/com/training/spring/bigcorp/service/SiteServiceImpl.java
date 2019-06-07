package com.training.spring.bigcorp.service;

import com.training.spring.bigcorp.config.Monitored;
import com.training.spring.bigcorp.model.Site;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@Service
public class SiteServiceImpl implements SiteService {

    private static Logger LOG = LoggerFactory.getLogger(SiteService.class);
    private CaptorService captorService;

    @Autowired
    private ResourceLoader resourceLoader;

    public SiteServiceImpl(){

    }

    @Autowired
    public SiteServiceImpl(CaptorService captorService){
        LOG.info("Init SiteServiceImpl :"+ this);
        this.captorService = captorService;
    }
    @Override
    @Monitored
    public Site findById(String siteId) {
        LOG.info("Appel de findById :"+this);
        if (siteId == null) {
            return null;
        }

        Site site = new Site("Florange");
        site.setId(siteId);
        site.setCaptors(captorService.findBySite(siteId));
        return site;
    }

    @Override
    public void readFile(String path)  {
        Resource resource = resourceLoader.getResource(path);

       try (InputStream stream = resource.getInputStream()){
           Scanner scanner = new Scanner(stream).useDelimiter("\\n");
           while(scanner.hasNext()){
               System.out.println(scanner.next());
           }
       }
       catch(IOException e){
           LOG.error("Erreur sur le chargement du fichier",e);
       }

    }

}
