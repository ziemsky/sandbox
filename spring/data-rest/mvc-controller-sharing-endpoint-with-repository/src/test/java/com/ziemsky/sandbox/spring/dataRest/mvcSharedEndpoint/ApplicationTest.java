 package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

 import com.fasterxml.jackson.core.JsonProcessingException;
 import com.fasterxml.jackson.databind.DeserializationFeature;
 import com.fasterxml.jackson.databind.ObjectMapper;
 import com.fasterxml.jackson.databind.ObjectWriter;
 import org.junit.Before;
 import org.junit.Test;
 import org.junit.runner.RunWith;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.boot.context.embedded.LocalServerPort;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.boot.test.web.client.TestRestTemplate;
 import org.springframework.http.RequestEntity;
 import org.springframework.http.ResponseEntity;
 import org.springframework.test.context.junit4.SpringRunner;

 import java.io.IOException;
 import java.util.StringJoiner;
 import java.util.UUID;
 import java.util.stream.Stream;

 import static io.restassured.RestAssured.config;
 import static io.restassured.RestAssured.given;
 import static io.restassured.config.EncoderConfig.encoderConfig;
 import static io.restassured.http.ContentType.TEXT;
 import static java.lang.String.join;
 import static java.lang.String.valueOf;
 import static java.util.stream.Collectors.joining;
 import static org.hamcrest.Matchers.startsWith;
 import static org.hamcrest.core.Is.is;
 import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
 import static org.springframework.test.util.ReflectionTestUtils.getField;
 import static org.springframework.web.util.UriComponentsBuilder.newInstance;

@RunWith(SpringRunner.class)
//@SpringBootTest(webEnvironment = RANDOM_PORT, properties = "logging.level.org.springframework=DEBUG") // todo comment?
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApplicationTest {

    // todo better test methods' names

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    public static final ObjectWriter JSON_WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();
    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private User expected;

    @Before
    public void setUp() throws Exception {
        expected = new User(randomString(), randomString());
    }

    @Test
    // application/json > default JSON HttpMessageConverter > User > repo
    public void createsUsersThroughSpringDataRestController_fromJsonMediaTypeInput() throws Exception {

        testPostWithContentType("application/json", ApplicationTest::asJson, expected);
    }

    @Test
    // made/up > MadeUpFormatToUserHttpMessageConverter > User > repo
    public void createsUsersThroughSpringDataRestController_fromCustomMediaTypeInput() throws Exception {

        testPostWithContentType("made/up", ApplicationTest::asMadeUpFormat, expected);
    }

    @Test
    // text/csv > CsvToInputStreamHttpMessageConverter > InputStream > custom MVC controller > User > repo
    public void createsUsersThroughCustomController_fromCsvMediaTypeInput() throws Exception {

        testPostWithContentType("text/csv", user -> asCsv(user, "firstName", "lastName"), expected);
    }

    @Test
    // made/up-2 > Spring's StringHttpMessageConverter > String > custom MVC controller > User > repo
    public void createsUsersThroughSpringDataRestController_fromAnotherCustomMediaTypeInput() throws Exception {

        testPostWithContentType("made/up-2", ApplicationTest::asMadeUpFormat2, expected);
    }

    private void testPostWithContentType(final String contentType, final Converter<User> userConverter, final User expected) {

        String endpointUri = newInstance().scheme("http").host("localhost").port(port).pathSegment("users").toUriString();

        // @formatter:off
        given()
            .config(config().encoderConfig(encoderConfig().encodeContentTypeAs(contentType, TEXT)))
            .contentType(contentType).body(userConverter.toTestedFormat(expected))
        .when()
            .post(endpointUri)
            .prettyPeek()
        .then()
            .contentType("application/hal+json;charset=UTF-8")
            .body("firstName",        is(expected.getFirstName()))
            .body("lastName",         is(expected.getLastName()))
            .body("_links.self.href", startsWith(endpointUri))
            .body("_links.user.href", startsWith(endpointUri))
        ;
        // @formatter:on
    }

    private String randomString() {
        return UUID.randomUUID().toString();
    }

    // todo util class?

    private void prettyPrint(final ResponseEntity<String> responseEntity) {

        System.out.println("");
        System.out.println("= RESPONSE =============================>");
        System.out.println(" status: " + responseEntity.getStatusCode());
        System.out.println("headers: " + responseEntity.getHeaders());
        System.out.println("   body: ");
        System.out.println(responseEntity.getBody());
        System.out.println("<=======================================");
        System.out.println("");
    }

    private void prettyPrint(final RequestEntity<String> requestEntity) {
        System.out.println("");
        System.out.println("= REQUEST ============================>");
        System.out.println(" method: " + requestEntity.getMethod());
        System.out.println("    uri: " + requestEntity.getUrl());
        System.out.println("headers: " + requestEntity.getHeaders());
        System.out.println("   body: ");
        System.out.println(requestEntity.getBody());
        System.out.println("<=======================================");
        System.out.println("");
    }

    private static <T> T asObject(Class<T> clazz, String responseBody) {
        try {
            return OBJECT_MAPPER.readerFor(clazz).readValue(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String asMadeUpFormat(User user) {
        return user.getFirstName() + ";" + user.getLastName();
    }

    private static String asMadeUpFormat2(User user) {
        return user.getFirstName() + "|" + user.getLastName();
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

    @FunctionalInterface
    private interface Converter<T> {
        String toTestedFormat(T source);
    }
}