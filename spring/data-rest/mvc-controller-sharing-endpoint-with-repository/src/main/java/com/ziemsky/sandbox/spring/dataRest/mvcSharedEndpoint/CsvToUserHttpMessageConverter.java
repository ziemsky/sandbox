package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

import au.com.bytecode.opencsv.CSVReader;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Collections.singletonList;

public class CsvToUserHttpMessageConverter implements HttpMessageConverter<User> {

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

    // todo auto formatting
    // todo comment about class being incomplete in that it expects very specific CSV format

    @Override
    public User read(final Class<? extends User> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {

        // todo license
        final CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(inputMessage.getBody())));

        final String[] line = csvReader.readAll().get(1);

        final String firstName = line[0];
        final String lastName = line[1];

        return new User(firstName, lastName);
    }

    @Override
    public void write(final User user, final MediaType contentType, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException(getClass() + " does not write CSV.");
    }
}
