package com.umass.hangout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.umass.hangout.repository.jpa")
@EnableElasticsearchRepositories(basePackages = "com.umass.hangout.repository.elasticsearch")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}