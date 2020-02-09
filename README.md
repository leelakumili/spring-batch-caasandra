Steps on how to use spring batch to perform bulk read and writes on cassandra database.

This is sample appliction which will read data from CSV file and load data into a table in cassandra DB. Also show, how to do bulk read from cassandra DB and do bulk write to different table in cassandra DB.

Pre-requisite:

Cassandra in local.

Use this script to create keyspace and tables  used in this poc:

	CREATE KEYSPACE members WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1};
   
    CREATE TABLE member(
           ...    Id int PRIMARY KEY,
           ...    first_name text,
           ...    last_name text  );
           
    CREATE TABLE member_duplicate(
           ...       Id int PRIMARY KEY,
           ...       first_name text,
           ...       last_name text  );
                  
1. Make sure you have spring batch and spring-data-cassandra dependencies to pom.xml

      <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-batch</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

        <!--cassandra-->
        <dependency>
            <groupId>org.springframework.data</groupId>
            <artifactId>spring-data-cassandra</artifactId>
            <version>${spring-data-cassandra.version}</version>
        </dependency>
     
2. Spring Batch needs a job repository to configure and maintain the batch jobs progress and status. Configure below properties 

    spring.datasource.url=jdbc:h2:mem:testdb
	spring.datasource.driverClassName=org.h2.Driver
	spring.datasource.initialize=true
	spring.batch.initializer.enabled=false
	spring.batch.job.enabled=false
	spring.batch.initialize-schema=never
	
Even though I have disabled spring batch configuration. It was still looking for datasource url/driver. So added .For  that added h2 dependency too.

3. Refer to CustomBatchConfigurer, where configure map job repository 

			            MapJobRepositoryFactoryBean jobRepositoryFactory = new 		MapJobRepositoryFactoryBean( this.transactionManager );
            jobRepositoryFactory.afterPropertiesSet();
            this.jobRepository = jobRepositoryFactory.getObject();
4. Refer to CassandraConfiguration where cassandra connection and keyspaces are configured. This establishes connection to configured cassandra DB. 


5. Disabled spring batch auto execution on startup "spring.batch.job.enabled=false".

6. Create a batch job with 2 steps: First step, will load data from CSV and write to member table , Second step will read data from member table and write to member_duplicate table.

		@Bean(name="loadMemberJob")
        public Job newSchemaJob(final JobBuilderFactory jobs, @Qualifier("loadStep") final Step s1, @Qualifier("maskedStep")final Step s2, final JobExecutionListener listener) {
        return jobs.get("newSchemaJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(s1)
                .next(s2)
                .build();
    }

7. Configure Both steps as shown below


  
    				@Bean
    				@Qualifier("loadStep")
    				public Step stepOne(final StepBuilderFactory stepBuilderFactory, final ItemProcessor processor, final ItemWriter<Member> writer) {
        Step s1= stepBuilderFactory.get("stepOne").<Member, Member>chunk(10)
                .reader(new MemberCsvReader(resource))
                .writer(memberDBWriter)
                .build();

        return s1;
    }
    
                @Bean
               @Qualifier("maskedStep")
               public Step stepTwo(final StepBuilderFactory stepBuilderFactory, final ItemProcessor processor, final ItemWriter<MemberDuplicate> writer) {
        SimpleStepBuilder stepsBuilder = stepBuilderFactory.get("stepTwo").<Member, MemberDuplicate>chunk(10)
                .reader(memberDBReader)
                .processor(processor)
                .writer(maskedMemberWriter);

                 stepsBuilder.allowStartIfComplete(true);
                Step s2 = stepsBuilder.build();
               return s2;
    }

8. Invoke the batch job using Rest api get call: "http://localhost:8080/run-member-job"


