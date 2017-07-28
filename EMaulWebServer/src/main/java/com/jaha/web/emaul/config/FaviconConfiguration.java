package com.jaha.web.emaul.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
public class FaviconConfiguration {

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "/META-INF/resources/", "/resources/",
            "/static/", "/public/" };

    @Bean
    public SimpleUrlHandlerMapping faviconHandlerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setOrder(Integer.MIN_VALUE + 1);
        mapping.setUrlMap(Collections.singletonMap("**/favicon.ico",
                faviconRequestHandler()));
        return mapping;
    }

    @Bean
    public ResourceHttpRequestHandler faviconRequestHandler() {
        ResourceHttpRequestHandler requestHandler = new ResourceHttpRequestHandler();

        requestHandler.setLocations(getLocations());
        requestHandler.setCacheSeconds(0);
        return requestHandler;
    }

    private List<Resource> getLocations() {
        List<Resource> locations = new ArrayList<Resource>(
                CLASSPATH_RESOURCE_LOCATIONS.length + 1);
        for (String location : CLASSPATH_RESOURCE_LOCATIONS) {
            locations.add(new ClassPathResource(location));
        }
        locations.add(new ClassPathResource("/"));
        return Collections.unmodifiableList(locations);
    }
}