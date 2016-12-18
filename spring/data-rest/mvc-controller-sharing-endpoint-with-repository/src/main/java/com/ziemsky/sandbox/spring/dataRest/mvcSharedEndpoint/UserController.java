package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;


import au.com.bytecode.opencsv.CSVReader;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@RepositoryRestController
public class UserController {

    private final UserRepository userRepository;
    private final EntityLinks repositoryEntityLinks;

    public UserController(final UserRepository userRepository, EntityLinks entityLinks) {
        this.userRepository = userRepository;
        this.repositoryEntityLinks = entityLinks;
    }

    @PostMapping(path = "/users", consumes = {"text/csv"})
    public @ResponseBody ResponseEntity<?> createUserFromCsv(@RequestBody InputStream body) {

        return handleCreateUserRequest(csvToUser(body));
    }

    @PostMapping(path = "/users", consumes = {"made/up-2"})
    public @ResponseBody ResponseEntity<?> createUserFromMadeUp2(@RequestBody String body) {

        return handleCreateUserRequest(madeUpTwoToUser(body));
    }

    @PostMapping(path = "/users", consumes = {"multipart/form-data"})
    public @ResponseBody ResponseEntity<?> createUserFromMadeUploadedCsvFile(@RequestParam("uploadedFile") MultipartFile file) {
        // see https://spring.io/guides/gs/uploading-files/

        return handleCreateUserRequest(uploadedCsvFileToUser(file));
    }

    private ResponseEntity<Resource<User>> handleCreateUserRequest(final User user) {

        final User newUser = userRepository.save(user);

        final Resource<User> resource = new Resource<>(newUser);

        Link endpointLink = repositoryEntityLinks.linkToSingleResource(User.class, newUser.getId());
        resource.add(endpointLink.withSelfRel(), endpointLink.withRel("user"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
    // - @RequestPart
    // - http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-multipart

    // todo external converter
    private User madeUpTwoToUser(final @RequestBody String body) {
        String[] line = body.split("\\|");
        return new User(line[0], line[1]);
    }

    // todo external converter
    private User uploadedCsvFileToUser(MultipartFile file) {

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return csvToUser(inputStream);
    }

    // todo external converter
    private User csvToUser(final InputStream csvInputStream) {
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
