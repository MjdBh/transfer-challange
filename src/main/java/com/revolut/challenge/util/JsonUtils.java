package com.revolut.challenge.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.revolut.challenge.exception.InvalidRequestData;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JsonUtils {
    private JsonUtils() {
    }

    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static String writeValueAsString(Object model) {
        try {
            return objectMapper.writeValueAsString(model);
        } catch (JsonProcessingException e) {
            throw new InvalidRequestData();
        }
    }

    public static <T> T convertToObject(String message, Class<T> clazz) {
        try {
            return objectMapper.readValue(message, clazz);
        } catch (JsonProcessingException e) {
            log.error("Error on covert json message ={}", message);
            throw new InvalidRequestData();
        }
    }
}
