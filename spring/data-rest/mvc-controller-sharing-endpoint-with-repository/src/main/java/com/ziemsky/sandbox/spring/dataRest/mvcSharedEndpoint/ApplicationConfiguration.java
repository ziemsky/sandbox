package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public CsvToUserHttpMessageConverter csvToStringHttpMessageConverter() {
        return new CsvToUserHttpMessageConverter();
    }

    // todo document registering custom message converter (and custom repo rest conf in general)

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurerAdapter() {
            @Override
            public void configureHttpMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
                messageConverters.add(csvToStringHttpMessageConverter());
            }
        };
    }
}
