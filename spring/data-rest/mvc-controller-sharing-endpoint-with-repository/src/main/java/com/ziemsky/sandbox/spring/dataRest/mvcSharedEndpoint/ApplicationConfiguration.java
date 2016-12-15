package com.ziemsky.sandbox.spring.dataRest.mvcSharedEndpoint;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;

import java.util.List;

@Configuration
public class ApplicationConfiguration {


    // todo document registering custom message converter (and custom repo rest conf in general)

    @Bean
    public RepositoryRestConfigurer repositoryRestConfigurer() {
        return new RepositoryRestConfigurerAdapter() {
            @Override
            public void configureHttpMessageConverters(final List<HttpMessageConverter<?>> messageConverters) {
                messageConverters.add(new CsvToInputStreamHttpMessageConverter());
                messageConverters.add(new MadeUpFormatOneToUserHttpMessageConverter());
                messageConverters.add(new StringHttpMessageConverter());

                messageConverters.forEach(httpMessageConverter -> System.out.println(httpMessageConverter.getClass()
                    + ": " + httpMessageConverter.getSupportedMediaTypes()));
            }
        };
    }
}
