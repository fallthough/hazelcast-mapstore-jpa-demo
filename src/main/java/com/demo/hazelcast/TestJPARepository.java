package com.demo.hazelcast;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TestJPARepository extends JpaRepository<TestDomain, String> {
    TestDomain findById(Integer id);

    @Query("SELECT id FROM TestDomain")
    Iterable<Integer> getAllIds();
}
