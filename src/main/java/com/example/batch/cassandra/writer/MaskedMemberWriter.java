package com.example.batch.cassandra.writer;


import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

import com.example.batch.cassandra.model.Member;
import com.example.batch.cassandra.model.MemberDuplicate;

import java.util.List;

@Component
public class MaskedMemberWriter implements ItemWriter<MemberDuplicate>, InitializingBean {

    @Autowired
    private CassandraTemplate cassandraTemplate;


    @Override
    public void afterPropertiesSet() throws Exception { }


    @Override
    public void write(final List<? extends MemberDuplicate> members) throws Exception {
            for (MemberDuplicate m:members) {
                cassandraTemplate.insert(m);
            }


    }
}