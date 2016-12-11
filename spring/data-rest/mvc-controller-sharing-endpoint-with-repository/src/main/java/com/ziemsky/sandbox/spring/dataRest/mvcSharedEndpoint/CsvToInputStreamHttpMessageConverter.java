package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

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

// todo tests + docs
// without this CSV->String converter, SDR kept returning
// {"timestamp":1481202185512,"status":415,"error":"Unsupported Media Type","exception":"org.springframework.web.HttpMediaTypeNotSupportedException","message":"Content type 'text/csv;charset=UTF-8' not supported","path":"/users"}
public class CsvToInputStreamHttpMessageConverter implements HttpMessageConverter<InputStream> {

    //

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
    public InputStream read(final Class<? extends InputStream> clazz, final HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        return inputMessage.getBody();
    }

    @Override
    public void write(final InputStream InputStream, final MediaType contentType, final HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        throw new UnsupportedOperationException(getClass() + " does not write CSV.");
    }
}
