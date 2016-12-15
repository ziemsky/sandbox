package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

import java.util.UUID;

public class RandomUtil {

    public static String randomString() {
        return UUID.randomUUID().toString();
    }
}
