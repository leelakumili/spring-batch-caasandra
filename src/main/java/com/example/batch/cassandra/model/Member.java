package com.example.batch.cassandra.model;

import java.io.Serializable;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("member")
public class Member  implements Serializable

{
    private static final long serialVersionUID = 7279304635831996399L;

    @PrimaryKey
    @Column("Id")
    private int id;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;


    public Member(){
    	
    }
     public Member(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

  
}
