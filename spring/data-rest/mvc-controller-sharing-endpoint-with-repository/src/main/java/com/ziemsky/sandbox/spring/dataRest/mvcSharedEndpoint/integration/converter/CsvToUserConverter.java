package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter;

import au.com.bytecode.opencsv.CSVReader;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.domain.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvToUserConverter {

    public User csvInputStreamToUser(final InputStream csvInputStream) {

        try {
            final CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(csvInputStream)));

            final String[] line = csvReader.readAll().get(1);

            final String firstName = line[0];
            final String lastName = line[1];

            return new User(firstName, lastName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
