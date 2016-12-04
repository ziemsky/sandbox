package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;


import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("/users")
@RepositoryRestController
public class UserController {

    private final UserRepository userRepository;
    private final EntityLinks repositoryEntityLinks;

    public UserController(final UserRepository userRepository, EntityLinks entityLinks) {
        this.userRepository = userRepository;
        this.repositoryEntityLinks = entityLinks;
    }

    @PostMapping(headers = {"Content-Type=text/csv"})
    public @ResponseBody ResponseEntity<?> createUserFromCsv(@RequestBody User user) {



        return handleCreateUserRequest(user);
    }


    @PostMapping(consumes = {"application/json"})
    public @ResponseBody ResponseEntity<?> createUserFromJson(@RequestBody User user) {

        return handleCreateUserRequest(user);
    }

    private ResponseEntity<?> handleCreateUserRequest(final @RequestBody User user) {
        final User newUser = userRepository.save(user);

        final Resource<User> resource = new Resource<>(newUser);

//        Link userLink = repositoryEntityLinks.linkToSingleResource(userRepository.getClass(), newUser.getId());
//        Link createUserFromJsonLink = linkTo(methodOn(UserController.class).createUserFromJson(user)).withSelfRel();
//        resource.add(createUserFromJsonLink, userLink);

        return ResponseEntity.status(HttpStatus.CREATED).body(resource);
    }
}
