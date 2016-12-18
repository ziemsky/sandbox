package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

import io.restassured.specification.RequestSpecification;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.RandomUtil.randomString;
import static io.restassured.RestAssured.config;
import static io.restassured.RestAssured.given;
import static io.restassured.config.EncoderConfig.encoderConfig;
import static io.restassured.http.ContentType.TEXT;
import static java.lang.String.join;
import static java.lang.String.valueOf;
import static java.nio.file.Files.createTempFile;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.write;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.Matchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.test.util.ReflectionTestUtils.getField;
import static org.springframework.web.util.UriComponentsBuilder.newInstance;

/**
 * <p>
 *     Demonstrates that custom Spring MVC controller can share the same endpoint as Spring Data REST repository,
 *     with requests being mapped to one or the other depending on their content type.
 * </p>
 * <p>
 *     Additionally, various ways of converting request body to format most convenient to handle by individual endpoints
 *     are exercised where some conversion is done in message converters (custom and provided by the framework) and
 *     some within custom controllers.
 * </p>
 * <p>
 *     Note that this test is satisfied with receiving expected payload and success status in response to the POST
 *     requests rather than going to the database to check that new records have actually been created there but that
 *     was enough for what it was intended for. One can verify that the requests actually go through expected handlers
 *     by disabling selected controller methods or message converters and re-running the tests.
 * </p>
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class ApplicationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @LocalServerPort
    int port;

    private User expected;
    private String endpointUri;

    @Before
    public void beforeEachTest() throws Exception {
        expected = new User(randomString(), randomString());
    }

    @PostConstruct
    public void beforeAllTests() {
        endpointUri = newInstance().scheme("http").host("localhost").port(port).pathSegment("users").toUriString();
    }

    @Test
    // todo JavaDoc
    // Given no custom message converter is configured to consume JSON request content type
    //   And no custom controller is configured to consume JSON request content type
    //  When POST request is made with body in JSON format
    //  Then request body is converted to entity class through default Jackson object mapper
    //   And the record is created through Spring Data REST endpoint/repository
    public void createsRecordsThroughSpringDataRest_fromJsonMediaTypeInput() {

        // Flow: application/json > default JSON HttpMessageConverter > User > UserRepository

        testPostWithSimpleContentType("application/json", expected, JsonUtil::asJson);
    }

    @Test
    // Given a custom message converter is configured to convert given request content type into an entity class
    //   And no custom controller is configured to consume given request content type
    //  When POST request is made with body in the format supported by the custom message converter
    //  Then request body is converted to entity class through the custom converter
    //   And the record is created through Spring Data REST endpoint/repository
    public void createsRecordsThroughSpringDataRest_fromCustomMediaTypeInputOne() {

        // Flow: made/up-1 > MadeUpFormatOneToUserHttpMessageConverter > User > UserRepository

        testPostWithSimpleContentType("made/up-1", expected, ApplicationTest::asMadeUpOneFormat);
    }

    @Test
    // Given a custom message converter is configured to consume given request content type and emit InputStream
    //   And a custom controller is configured to consume given request content type as an InputStream
    //  When POST request is made with body in the format supported by the custom message converter
    //  Then the record is created through the custom controller
    public void createsRecordsThroughCustomController_fromCsvMediaTypeInput() throws Exception {

        // Flow: text/csv > CsvToInputStreamHttpMessageConverter > InputStream > custom MVC controller > User > UserRepository

        testPostWithSimpleContentType("text/csv", expected, user -> asCsv(user));
    }

    @Test
    // Given no custom message converter is configured to consume multipart form content type
    //   And a custom controller is configured to consume given request content type as an multipart form
    //  When POST request is made with body in multipart form format
    //  Then the record is created through the custom controller
    public void createsRecordsThroughCustomController_fromUploadedCsvFile() throws Exception {

        // Flow: CSV file + multipart/form-data > MultipartFile > custom MVC controller > InputStream > User > UserRepository

        Path csvFile = createTempFile("test", ".csv");

        try {
            write(csvFile, asCsv(expected).getBytes());

            // value of controlName is the same as that of "name" attribute in element <input type="file" ...
            String controlName = "uploadedFile";

            RequestSpecification requestSpecification = given().multiPart(controlName, csvFile.toFile());

            testPost(requestSpecification, expected);

        } finally {
            deleteIfExists(csvFile);
        }
    }


    @Test
    // Given Spring's StringHttpMessageConverter is configured
    //   And a custom controller is configured to consume given request content type as a String
    //  When POST request is made with body in the format supported by the message converter
    //  Then request body is converted to String through the converter
    //   And the record is created through the custom controller
    public void createsRecordsThroughSpringDataRestController_fromCustomMediaTypeInputTwo() throws Exception {

        // Flow: made/up-2 > Spring's StringHttpMessageConverter > String > custom MVC controller > User > UserRepository

        testPostWithSimpleContentType("made/up-2", expected, ApplicationTest::asMadeUpTwoFormat);
    }

    private void testPostWithSimpleContentType(final String contentType, final User expected, final FromTestedFormatConverter<User> userConverter) {

        // @formatter:off
        RequestSpecification requestSpecification = given()
            .config(config().encoderConfig(encoderConfig().encodeContentTypeAs(contentType, TEXT)))
            .contentType(contentType)
            .body(userConverter.asString(expected));
        // @formatter:on

        testPost(requestSpecification, expected);
    }

    private void testPost(final RequestSpecification given, final User expected) {

        // @formatter:off
        given
            .log().all()
            .filter((requestSpec, responseSpec, ctx) -> {
                System.out.println("");
                System.out.println("");
                System.out.println("Response:");
                return ctx.next(requestSpec, responseSpec);
            })
        .when()
            .post(endpointUri)
        .then()
            .contentType("application/hal+json;charset=UTF-8")
            .body("firstName", is(expected.getFirstName()))
            .body("lastName", is(expected.getLastName()))
            .body("_links.self.href", startsWith(endpointUri))
            .body("_links.user.href", startsWith(endpointUri))
            .log().all(true)
        ;
        // @formatter:on
    }

    private static String asMadeUpOneFormat(final User user) {
        return user.getFirstName() + ";" + user.getLastName();
    }

    private static String asMadeUpTwoFormat(final User user) {
        return user.getFirstName() + "|" + user.getLastName();
    }

    private static String asCsv(final Object object) {

        // @formatter:off
        final List<String> fieldNames = Stream.of(object.getClass().getDeclaredFields())
            .map(Field::getName)
            .filter(fieldName -> !"id".equals(fieldName))
            .collect(toList());

        return new StringJoiner("\n")
            .add(join(",", fieldNames))
            .add(fieldNames.stream()
                .map(fieldName -> valueOf(getField((Object) object, fieldName)))
                .collect(joining(","))
            )
            .toString();
        // @formatter:on
    }

    @FunctionalInterface
    private interface FromTestedFormatConverter<T> {
        String asString(T source);
    }
}