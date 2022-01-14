package com.demo.hazelcast;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
@Data
public class TestDomain implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private int id;
    private String name;
}
