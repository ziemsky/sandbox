package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.net.URI;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "logging.level.org.springframework=DEBUG") // todo comment?
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApplicationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ObjectWriter JSON_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter(); // todo
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void createsUsersThroughCustomController_onJsonInput() throws Exception {
        User expected = new User(randomString(), randomString());

        testPostWithContentType("application/json", () -> asJson(expected), expected);
    }

    @Test
    public void createsUsersThroughCustomController_onCsvInput() throws Exception {
        User expected = new User(randomString(), randomString());

        testPostWithContentType("text/csv", () -> asCsv(expected, "firstName", "lastName"), expected);
    }

    private void testPostWithContentType(final String contentType, final Supplier<String> stringSupplier, final User expected) {

        // given

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Content-Type", contentType);

        URI endpointUri = newInstance().scheme("http").host("localhost").port(port).pathSegment("users").build().toUri();

        RequestEntity<String> requestEntity = new RequestEntity<>(stringSupplier.get(), httpHeaders, HttpMethod.POST, endpointUri);

        prettyPrint(requestEntity);

        // when
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(requestEntity, String.class);


        // then
        prettyPrint(responseEntity);

        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));

        User actual = asObject(User.class, responseEntity.getBody());

        assertThat(actual.getFirstName(), is(expected.getFirstName()));
        assertThat(actual.getLastName(), is(expected.getLastName()));

        // todo test more details
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    // todo util class?

    private void prettyPrint(final ResponseEntity<String> responseEntity) {

        System.out.println("= RESPONSE =============================>");
        System.out.println(" status: " + responseEntity.getStatusCode());
        System.out.println("headers: " + responseEntity.getHeaders());
        System.out.println("   body: ");
        System.out.println(responseEntity.getBody());
        System.out.println("<=======================================");
    }

    private void prettyPrint(final RequestEntity<String> requestEntity) {
        System.out.println("= REQUEST ============================>");
        System.out.println(" method: " + requestEntity.getMethod());
        System.out.println("    uri: " + requestEntity.getUrl());
        System.out.println("headers: " + requestEntity.getHeaders());
        System.out.println("   body: ");
        System.out.println(requestEntity.getBody());
        System.out.println("<=======================================");
    }

    private static <T> T asObject(Class<T> clazz, String responseBody) {
        try {
            return OBJECT_MAPPER.readerFor(clazz).readValue(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String asJson(Object object) {
        try {
            return JSON_WRITER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String asCsv(Object object, String... fieldNames) {

        // todo cleaner joiners?

        // @formatter:off
        return new StringJoiner("\n")
            .add(join(",", fieldNames))
            .add(Stream.of(fieldNames)
                .map(fieldName -> valueOf(getField((Object) object, fieldName)))
                .collect(joining(","))
            )
            .toString();
        // @formatter:on
    }
}