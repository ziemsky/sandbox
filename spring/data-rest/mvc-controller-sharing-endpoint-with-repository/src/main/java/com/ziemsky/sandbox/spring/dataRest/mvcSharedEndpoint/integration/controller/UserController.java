package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.controller;


import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.domain.User;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.persistence.UserRepository;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter.CsvToUserConverter;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter.MadeUpFormatTwoToUserConverter;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter.MultipartCsvFileToUserConverter;
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

import java.io.InputStream;

@RepositoryRestController
public class UserController {

    private final MultipartCsvFileToUserConverter multipartCsvFileToUserConverter;
    private final MadeUpFormatTwoToUserConverter madeUpFormatTwoToUserConverter;
    final CsvToUserConverter csvToUserConverter;
    private final UserRepository userRepository;
    private final EntityLinks entityLinks;

    public UserController(final MultipartCsvFileToUserConverter multipartCsvFileToUserConverter,
                          final MadeUpFormatTwoToUserConverter madeUpFormatTwoToUserConverter,
                          final CsvToUserConverter csvToUserConverter,
                          final UserRepository userRepository,
                          final EntityLinks entityLinks) {
        this.multipartCsvFileToUserConverter = multipartCsvFileToUserConverter;
        this.madeUpFormatTwoToUserConverter = madeUpFormatTwoToUserConverter;
        this.csvToUserConverter = csvToUserConverter;
        this.userRepository = userRepository;
        this.entityLinks = entityLinks;
    }

    @PostMapping(path = "/users", consumes = {"text/csv"})
    public @ResponseBody ResponseEntity<?> createUserFromCsv(@RequestBody InputStream body) {

        return handleCreateUserRequest(csvToUserConverter.csvInputStreamToUser(body));
    }

    @PostMapping(path = "/users", consumes = {"made/up-2"})
    public @ResponseBody ResponseEntity<?> createUserFromMadeUp2(@RequestBody String body) {

        return handleCreateUserRequest(madeUpFormatTwoToUserConverter.madeUpTwoToUser(body));
    }

    @PostMapping(path = "/users", consumes = {"multipart/form-data"})
    public @ResponseBody ResponseEntity<?> createUserFromMadeUploadedCsvFile(
        @RequestParam("uploadedFile") MultipartFile file
    ) {
        // See:
        // - https://spring.io/guides/gs/uploading-files/
        // - @RequestPart
        // - http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html#mvc-multipart

        // Note that parameter name - here uploadedFile - has to correspond to the name given to <input type="file" >
        // HTML element; see file upload.html for example.

        return handleCreateUserRequest(multipartCsvFileToUserConverter.uploadedCsvFileToUser(file, this));
    }

    private ResponseEntity<Resource<User>> handleCreateUserRequest(final User user) {

        final User newUser = userRepository.save(user);

        final Resource<User> resource = new Resource<>(newUser);

        Link endpointLink = entityLinks.linkToSingleResource(User.class, newUser.getId());
        resource.add(endpointLink.withSelfRel(), endpointLink.withRel("user"));

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }

}
