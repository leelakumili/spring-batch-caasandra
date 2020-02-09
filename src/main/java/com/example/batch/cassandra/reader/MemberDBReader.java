package com.example.batch.cassandra.reader;

import com.example.batch.cassandra.model.Member;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.data.cassandra.core.CassandraOperations;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MemberDBReader implements ItemReader<Member> {


    @Autowired
    private CassandraOperations cassandraOperations;

    private int index = 0;

    @Override
    public Member read() throws Exception {
        final List<Member> members = cassandraOperations.select("select * from member where id in (1,2,3,4,5,6) ",Member.class);

        if (index < members.size()) {
            final Member member = members.get(index);
            index++;
            return member;
        }

        index=0;
        return null;
    }
}