package com.example.batch.cassandra.reader;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;

import com.example.batch.cassandra.model.Member;

public class MemberCsvReader extends FlatFileItemReader<Member> {

    public MemberCsvReader(Resource resource) {
        super();
        setResource(resource);
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setNames(new String[] { "id", "firstname", "lastname"});
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);

        BeanWrapperFieldSetMapper<Member> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Member.class);

        DefaultLineMapper<Member> defaultLineMapper = new DefaultLineMapper<>();
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        setLineMapper(defaultLineMapper);
    }
}