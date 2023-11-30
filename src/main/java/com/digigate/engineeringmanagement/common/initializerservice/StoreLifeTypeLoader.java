package com.digigate.engineeringmanagement.common.initializerservice;

import com.digigate.engineeringmanagement.common.constant.ApplicationConstant;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.digigate.engineeringmanagement.common.constant.ApplicationConstant.COULD_NOT_PARSE_SHELF_LIFE_TYPE_FILE;

@Component
public class StoreLifeTypeLoader {
    private final ObjectMapper mapper;
    private static Map<String, String> lifeTypes = new HashMap<>();
    protected static final Logger LOGGER = LoggerFactory.getLogger(StoreLifeTypeLoader.class);

    public StoreLifeTypeLoader(ObjectMapper mapper) {
        this.mapper = mapper;
        loadTypes();
    }

    public static Map<String, String> getLifeTypes() {
        return lifeTypes;
    }

    private void loadTypes() {
        try {
            File json = new ClassPathResource(ApplicationConstant.STORE_LIFE_TYPE_FILE).getFile();
            lifeTypes = mapper.readValue(json, new TypeReference<>() {
            });
        } catch (IOException ioException) {
            LOGGER.error(COULD_NOT_PARSE_SHELF_LIFE_TYPE_FILE);
        }
    }
}
