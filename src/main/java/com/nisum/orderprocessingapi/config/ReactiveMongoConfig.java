package com.nisum.orderprocessingapi.config;


import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;


@Configuration
@EnableReactiveMongoRepositories(basePackages = "com.nisum.orderprocessingapi")
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration{

    @Override
    public MongoClient reactiveMongoClient() {
        return MongoClients.create();
    }

    @Override
    protected String getDatabaseName() {
        return "orderdb";
    }
}