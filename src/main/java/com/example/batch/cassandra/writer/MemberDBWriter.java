package com.example.batch.cassandra.writer;

import com.example.batch.cassandra.model.Member;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.cassandra.core.CassandraTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberDBWriter implements ItemWriter<Member>, InitializingBean {

    @Autowired
    private CassandraTemplate cassandraTemplate;

    @Override
    public void afterPropertiesSet() throws Exception { }


    @Override
    public void write(final List<? extends Member> members) throws Exception {
        if (!members.isEmpty()) {
            for (Member m:members) {
                cassandraTemplate.insert(m);
            }

        }
    }
}