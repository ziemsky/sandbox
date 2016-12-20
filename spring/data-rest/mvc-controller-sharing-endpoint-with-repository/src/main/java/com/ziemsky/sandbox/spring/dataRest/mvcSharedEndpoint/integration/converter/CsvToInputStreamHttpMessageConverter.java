package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter;

import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static java.util.Collections.singletonList;

/**
 * Converts body of {@code text/csv} request into {@linkplain InputStream}; does not support the opposite operation.
 */
public class CsvToInputStreamHttpMessageConverter implements HttpMessageConverter<InputStream> {

    public static final MediaType CSV_MEDIA_TYPE = new MediaType("text", "csv");

    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return mediaType != null && mediaType.isCompatibleWith(CSV_MEDIA_TYPE);
    }

    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return singletonList(CSV_MEDIA_TYPE);
    }

    @Override
    public InputStream read(final Class<? extends InputStream> clazz, final HttpInputMessage inputMessage) throws
        IOException, HttpMessageNotReadableException {
        return inputMessage.getBody();
    }

    @Override
    public void write(final InputStream InputStream, final MediaType contentType, final HttpOutputMessage
        outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException(getClass() + " does not write CSV.");
    }
}
