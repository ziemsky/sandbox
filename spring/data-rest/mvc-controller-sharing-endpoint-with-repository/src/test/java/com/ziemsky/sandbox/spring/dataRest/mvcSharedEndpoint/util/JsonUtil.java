package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectWriter JSON_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T asObject(Class<T> clazz, String responseBody) {
        try {
            return OBJECT_MAPPER.readerFor(clazz).readValue(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String asJson(Object object) {
        try {
            return JSON_WRITER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
