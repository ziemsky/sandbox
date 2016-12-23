# sandbox

Playground for testing ideas and storing useful snippets and examples.

## Building and executing

In order to run the build and individual examples you need Java 8 or later installed. Build is managed by [Gradle], with
[Gradle Wrapper] configured so you don't need Gradle installed directly on your machine. 

Simply execute `./gradlew` *from the root directory* (the one containing `.git` directory) followed by full project path
ending with task you want to run. For example, to run tests against project `mvc-controller-sharing-endpoint-with-repository`
you'd execute:
```
./gradlew :spring:data-rest:mvc-controller-sharing-endpoint-with-repository:test
```

Add `--info` to to the command to see more detailed output.

To list default/key tasks available in each project, execute task `keyTasks`, for example:
```
./gradlew :spring:data-rest:mvc-controller-sharing-endpoint-with-repository:keyTasks
```

To discover available projects execute:
```
./gradlew projects
```


## Projects

Sandbox is structured as a [hierarchy of projects][gradle multi project builds]:

* **[:spring:data-rest:mvc-controller-sharing-endpoint-with-repository](./spring/data-rest/mvc-controller-sharing-endpoint-with-repository/readme.md)** -
 demonstrates how to plug custom Spring MVC controller so that it listens on the same endpoint as Spring Data REST
 repository where calls are dispatched to one or another depending on their [`Content-Type`][content-type header].
 Uses [Spring Boot], [Spring Data REST], [Spring MVC].
  
## Licence

 

[gradle]:                       https://gradle.org/getting-started-gradle/
[gradle wrapper]:               https://docs.gradle.org/current/userguide/gradle_wrapper.html
[gradle multi project builds]:  https://docs.gradle.org/current/userguide/intro_multi_project_builds.html

[spring boot]:                  https://projects.spring.io/spring-boot/
[spring data rest]:             http://projects.spring.io/spring-data-rest/
[spring mvc]:                   http://docs.spring.io/spring/docs/current/spring-framework-reference/html/mvc.html

[content-type header]:          https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Type