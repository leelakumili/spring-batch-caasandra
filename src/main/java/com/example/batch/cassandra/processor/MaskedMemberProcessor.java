package com.example.batch.cassandra.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.batch.cassandra.model.Member;
import com.example.batch.cassandra.model.MemberDuplicate;
import com.example.batch.cassandra.util.MaskUtil;

@Component
public class MaskedMemberProcessor implements ItemProcessor<Member, MemberDuplicate> {

    @Autowired
    MaskUtil maskUtil;

    @Override
    public MemberDuplicate process(Member member) throws Exception {

        if(member.getLastName().length()>3 && member.getFirstName().length()>3) {
        	MemberDuplicate memberMasked = new MemberDuplicate();
            memberMasked.setId(member.getId());
            memberMasked.setFirstName(maskUtil.getAlphaNumericString(member.getFirstName().length()));
            memberMasked.setLastName(maskUtil.getAlphaNumericString(member.getLastName().length()));
            return memberMasked;
        }
        return null;

    }

}