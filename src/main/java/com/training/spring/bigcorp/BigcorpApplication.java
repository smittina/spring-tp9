package com.training.spring.bigcorp;

import com.training.spring.bigcorp.config.properties.BigCorpApplicationProperties;
import com.training.spring.bigcorp.service.SiteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class BigcorpApplication {

	private final static Logger LOG = LoggerFactory.getLogger(BigcorpApplication.class);

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BigcorpApplication.class, args);
		BigCorpApplicationProperties applicationInfo = context.getBean(BigCorpApplicationProperties.class);
		LOG.info("===============================");
		LOG.info("Application ["+applicationInfo.getName()+"] - version :"+applicationInfo.getVersion());
		LOG.info("plus d'informations sur "+applicationInfo.getWebSiteUrl());
		LOG.info("===============================");

		context.getBean(SiteService.class).findById("test");
	}

}
