package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter;

import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.domain.User;
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

/**
 * Converts body of request with custom {@code made/up-1} content type into a {@linkplain User} object; does not support
 * the opposite operation.
 */
public class MadeUpFormatOneToUserHttpMessageConverter implements HttpMessageConverter<User> {

    public static final MediaType MEDIA_TYPE = new MediaType("made", "up-1");

    @Override
    public boolean canRead(final Class<?> clazz, final MediaType mediaType) {
        return mediaType != null && mediaType.isCompatibleWith(MEDIA_TYPE);
    }

    @Override
    public boolean canWrite(final Class<?> clazz, final MediaType mediaType) {
        return false;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        return singletonList(MEDIA_TYPE);
    }

    @Override
    public User read(final Class<? extends User> clazz, final HttpInputMessage inputMessage) throws IOException,
        HttpMessageNotReadableException {

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputMessage.getBody()));
        final String line = bufferedReader.lines().findFirst().get();

        final String[] fields = line.split(";");

        final String firstName = fields[0];
        final String lastName = fields[1];

        return new User(firstName, lastName);
    }

    @Override
    public void write(final User user, final MediaType contentType, final HttpOutputMessage outputMessage) throws
        IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException(getClass() + " does not write CSV.");
    }
}
