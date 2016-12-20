package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.util;

import java.util.UUID;

public class RandomUtil {

    public static String randomString() {
        return UUID.randomUUID().toString();
    }
}
