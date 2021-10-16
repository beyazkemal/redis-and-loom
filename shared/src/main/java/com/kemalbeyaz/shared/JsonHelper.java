package com.kemalbeyaz.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonHelper {

    private static final Logger LOG = LoggerFactory.getLogger(JsonHelper.class);
    private static final ObjectMapper OM = new ObjectMapper();

    private JsonHelper() {
        throw new IllegalCallerException("Utility class");
    }

    public static <T> String toJSON(T value, Class<T> clazz) {
        ObjectWriter objectWriter = OM.writerFor(clazz);
        try {
            return objectWriter.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            LOG.error("JSON'a dönüştürülürken hata meydanda geldi!", e);
            throw new IllegalArgumentException();
        }
    }

    public static <T> T fromJSON(final String jsonValue, Class<T> clazz) {
        ObjectReader objectReader = OM.readerFor(clazz);
        try {
            return objectReader.readValue(jsonValue);
        } catch (JsonProcessingException e) {
            LOG.error("JSON'dan dönüştürülürken hata meydana geldi!", e);
            throw new IllegalArgumentException();
        }
    }
}
