package com.example.batch.cassandra.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.MapJobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class CustomBatchConfigurer implements BatchConfigurer {

    private static final Logger LOG = LoggerFactory.getLogger(CustomBatchConfigurer.class);

    private PlatformTransactionManager transactionManager;

    private JobRepository jobRepository;

    private JobLauncher jobLauncher;

    private JobExplorer jobExplorer;

    @Override
    public JobRepository getJobRepository() {
        return this.jobRepository;
    }

    @Override
    public JobLauncher getJobLauncher() {
        return this.jobLauncher;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        return this.transactionManager;
    }



    @Override
    public JobExplorer getJobExplorer() throws Exception {
        return this.jobExplorer;
    }

    @PostConstruct
    public void initialize() {
        try {
            // transactionManager:
            LOG.info("Forcing the use of a Resourceless transactionManager");

            this.transactionManager = new ResourcelessTransactionManager();

            // jobRepository:
            LOG.info("Forcing the use of a Map based JobRepository");
            MapJobRepositoryFactoryBean jobRepositoryFactory = new MapJobRepositoryFactoryBean( this.transactionManager );
            jobRepositoryFactory.afterPropertiesSet();
            this.jobRepository = jobRepositoryFactory.getObject();

            // jobLauncher:
            SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
            jobLauncher.setJobRepository(getJobRepository());
            jobLauncher.afterPropertiesSet();
            this.jobLauncher = jobLauncher;

            // jobExplorer:
            MapJobExplorerFactoryBean jobExplorerFactory = new MapJobExplorerFactoryBean(jobRepositoryFactory);
            jobExplorerFactory.afterPropertiesSet();
            this.jobExplorer = jobExplorerFactory.getObject();
        }
        catch (Exception ex) {
            throw new IllegalStateException("Unable to initialize Spring Batch", ex);
        }
    }

}