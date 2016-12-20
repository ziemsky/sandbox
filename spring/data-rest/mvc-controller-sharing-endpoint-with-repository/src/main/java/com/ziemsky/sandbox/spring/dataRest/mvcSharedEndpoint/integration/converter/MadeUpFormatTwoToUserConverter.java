package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter;

import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.domain.User;

public class MadeUpFormatTwoToUserConverter {

    public User madeUpTwoToUser(final String body) {

        final String[] line = body.split("\\|");

        return new User(line[0], line[1]);
    }
}
