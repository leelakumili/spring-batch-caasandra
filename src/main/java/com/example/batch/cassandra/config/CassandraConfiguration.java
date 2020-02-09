package com.example.batch.cassandra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.AbstractCassandraConfiguration;
import org.springframework.data.cassandra.config.CassandraClusterFactoryBean;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.data.cassandra.core.mapping.BasicCassandraMappingContext;
import org.springframework.data.cassandra.core.mapping.CassandraMappingContext;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;


@Configuration
@EnableCassandraRepositories(basePackages = "com.sbux.springboot.h2")
public class CassandraConfiguration extends AbstractCassandraConfiguration{

    private static final Logger LOG = LoggerFactory.getLogger(CassandraConfiguration.class);

    @Value( "${cassandra.host}" )
    private  String cassandraHost;

    @Value( "${cassandra.keyspace}" )
    private  String cassandraKeyspace;
    
    @Value( "${cassandra.username}" )
    private  String cassandraUsername;
    
    @Value( "${cassandra.password}" )
    private  String cassandraPassword;


    @Bean
    public CassandraClusterFactoryBean cluster() {
        CassandraClusterFactoryBean cluster =
                new CassandraClusterFactoryBean();
        cluster.setContactPoints(cassandraHost);
        cluster.setPort(9042);
        cluster.setUsername(cassandraUsername);
        cluster.setPassword(cassandraPassword);
        return cluster;
    }

    @Bean
    public CassandraMappingContext cassandraMapping()
            throws ClassNotFoundException {
        return new BasicCassandraMappingContext();
    }



    @Bean(name = "cassandraTemplate")
    public CassandraTemplate profileCassandraTemplate() throws Exception {
        final CassandraTemplate cassandraTemplate = new CassandraTemplate(session().getObject(), cassandraConverter());

        return cassandraTemplate;
    }

    @Override
    protected String getKeyspaceName() {

        return cassandraKeyspace;
    }
}
