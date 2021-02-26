package com.example;

import io.r2dbc.spi.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.time.Duration;
import java.util.Arrays;

@Configuration
public class AppRunnerConfiguration {

    private static final Logger log = LoggerFactory.getLogger(AppRunnerConfiguration.class);

    @Bean
    ConnectionFactoryInitializer initializer(@Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer factoryInitializer = new ConnectionFactoryInitializer();
        factoryInitializer.setConnectionFactory(connectionFactory);
        factoryInitializer.setDatabasePopulator(new ResourceDatabasePopulator(
                new ClassPathResource("schema.sql")));

        return factoryInitializer;
    }

    @Bean
    public CommandLineRunner appRunner(CustomerRepository repository) {

        return (args) -> {
            repository.saveAll(Arrays.asList(new Customer("Ana", "Maria"),
                    new Customer("Ion", "Ionescu"),
                    new Customer("Maria", "Popescu"),
                    new Customer("Alin", "Ionescu"),
                    new Customer("Mihai", "Popescu")))
                    .blockLast(Duration.ofSeconds(10));

            log.info("Customers  found with findAll");
            repository.findAll().doOnNext(customer -> log.info(customer.toString()))
                    .blockLast(Duration.ofSeconds(10));

            log.info("-----------");

            repository.findById(1L).doOnNext(customer -> {
                log.info("Customer found with id=1");
                log.info(customer.toString());
                log.info("---------");
            }).block(Duration.ofSeconds(10));

            log.info("Customer found with lastName = Ionescu");
            repository.findByLastName("Ionescu").doOnNext(customer -> {
                log.info(customer.toString());
            }).blockLast(Duration.ofSeconds(10));
            log.info("-------");
        };
    }
}
