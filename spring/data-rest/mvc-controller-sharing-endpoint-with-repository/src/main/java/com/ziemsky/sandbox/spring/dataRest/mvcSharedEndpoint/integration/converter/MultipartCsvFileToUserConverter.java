package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter;

import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.domain.User;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.controller.UserController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class MultipartCsvFileToUserConverter {

    private CsvToUserConverter csvToUserConverter;

    public MultipartCsvFileToUserConverter(final CsvToUserConverter csvToUserConverter) {
        this.csvToUserConverter = csvToUserConverter;
    }

    public User uploadedCsvFileToUser(MultipartFile file, final UserController userController) {

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return csvToUserConverter.csvInputStreamToUser(inputStream);
    }
}
