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


	# SPRING BATCH (BatchDatabaseInitializer)
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

6. Add a controller which will batch job.

7. My batch has 2 steps, each step have a reader and writer.

 			

