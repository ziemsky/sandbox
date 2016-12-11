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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RepositoryRestController
public class UserController {

    private final UserRepository userRepository;
    private final EntityLinks repositoryEntityLinks;

    public UserController(final UserRepository userRepository, EntityLinks entityLinks) {
        this.userRepository = userRepository;
        this.repositoryEntityLinks = entityLinks;
    }

    // todo methods' names
    @PostMapping(path = "/users", consumes = {"text/csv"})
    public @ResponseBody ResponseEntity<?> createUserFromCsv(@RequestBody InputStream body) {

        User user = csvToUser(body);

        final User newUser = userRepository.save(user);

        final Resource<User> resource = new Resource<>(newUser);

//        Link userLink = repositoryEntityLinks.linkToSingleResource(userRepository.getClass(), newUser.getId());
//        Link createUserFromJsonLink = linkTo(methodOn(UserController.class).createUserFromCsv(user)).withSelfRel();
//        resource.add(createUserFromJsonLink, userLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    @PostMapping(path = "/users", consumes = {"made/up-2"})
    public @ResponseBody ResponseEntity<?> createUserFromMadeUp2(@RequestBody String body) {

        String[] line = body.split("\\|");
        User user = new User(line[0], line[1]);

        final User newUser = userRepository.save(user);

        final Resource<User> resource = new Resource<>(newUser);

//        Link userLink = repositoryEntityLinks.linkToSingleResource(userRepository.getClass(), newUser.getId());
//        Link createUserFromJsonLink = linkTo(methodOn(UserController.class).createUserFromCsv(user)).withSelfRel();
//        resource.add(createUserFromJsonLink, userLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

    // todo createUserFromUploadedCsv using web browser - see:
    // - http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-multipart
    // - @RequestPart

    private User csvToUser(final @RequestBody InputStream body) {
        try {
            final CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(body)));

            final String[] line = csvReader.readAll().get(1);

            final String firstName = line[0];
            final String lastName = line[1];


            return new User(firstName, lastName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
