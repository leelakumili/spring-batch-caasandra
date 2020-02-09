package com.example.batch.cassandra.job;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.example.batch.cassandra.model.MemberDuplicate;
import com.example.batch.cassandra.processor.MaskedMemberProcessor;
import com.example.batch.cassandra.reader.MemberCsvReader;
import com.example.batch.cassandra.reader.MemberDBReader;
import com.example.batch.cassandra.writer.MaskedMemberWriter;
import com.example.batch.cassandra.writer.MemberDBWriter;
import com.example.batch.cassandra.config.CustomBatchConfigurer;
import com.example.batch.cassandra.model.Member;

@Configuration
public class MemberLoadJob extends JobExecutionListenerSupport {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;


    @Value("${input.file}")
    Resource resource;


    @Autowired
    MaskedMemberWriter maskedMemberWriter;

    @Autowired
    MemberDBWriter memberDBWriter;

    @Autowired
    MemberDBReader memberDBReader;

   

    @Bean
    public BatchConfigurer configurer(){
        return new CustomBatchConfigurer();
    }


    @Bean
    public ItemProcessor<Member, MemberDuplicate> processor() {
        return new MaskedMemberProcessor();
    }

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


    @Bean(name="loadMemberJob")
    public Job newSchemaJob(final JobBuilderFactory jobs, @Qualifier("loadStep") final Step s1, @Qualifier("maskedStep")final Step s2, final JobExecutionListener listener) {
        return jobs.get("newSchemaJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(s1)
                .next(s2)
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(final DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

  
    @Override
    public void beforeJob(JobExecution jobExecution) {
        super.beforeJob(jobExecution);
        System.out.println(jobExecution.getJobId());
    }
}
