package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.configuration;

import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.controller.UserController;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.integration.converter.*;
import com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint.persistence.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.hateoas.EntityLinks;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.util.List;

@Configuration
public class ApplicationConfiguration {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurerAdapter() {
            @Override
            public void configureHttpMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
                messageConverters.add(new CsvToInputStreamHttpMessageConverter());
                messageConverters.add(new MadeUpFormatOneToUserHttpMessageConverter());
                messageConverters.add(new StringHttpMessageConverter());

                log.info("Configured message converters:");
                messageConverters.forEach(converter -> {
                    log.info(converter.getClass().getSimpleName() + ": " + converter.getSupportedMediaTypes());
                });
            }
        };
    }

    @Bean
    public UserController userController(final UserRepository userRepository, final EntityLinks entityLinks) {

        return new UserController(
            multipartCsvFileToUserConverter(),
            madeUpFormatTwoToUserConverter(),
            csvToUserConverter(),
            userRepository,
            entityLinks
        );
    }

    @Bean
    public CsvToUserConverter csvToUserConverter() {
        return new CsvToUserConverter();
    }

    @Bean
    public MadeUpFormatTwoToUserConverter madeUpFormatTwoToUserConverter() {
        return new MadeUpFormatTwoToUserConverter();
    }

    @Bean
    public MultipartCsvFileToUserConverter multipartCsvFileToUserConverter() {
        return new MultipartCsvFileToUserConverter(csvToUserConverter());
    }
}
