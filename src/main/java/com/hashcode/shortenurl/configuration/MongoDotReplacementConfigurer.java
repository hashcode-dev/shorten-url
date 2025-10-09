package com.hashcode.shortenurl.configuration;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.stereotype.Component;

@Component
public class MongoDotReplacementConfigurer {
    private final MappingMongoConverter mappingMongoConverter;

    public MongoDotReplacementConfigurer(MappingMongoConverter mappingMongoConverter) {
        this.mappingMongoConverter = mappingMongoConverter;
    }

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        mappingMongoConverter.setMapKeyDotReplacement("#");
    }
}
