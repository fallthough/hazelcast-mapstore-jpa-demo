package com.demo.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.MapLoaderLifecycleSupport;
import com.hazelcast.map.MapStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
@Slf4j
public class TestDemoStore implements  MapStore<Integer, TestDomain>, MapLoaderLifecycleSupport {

    private TestJPARepository testJPARepository;

    private final AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();

    private HazelcastInstance hazelcastInstance;

    @Override
    public void store(Integer key, TestDomain value) {
        log.info("store({}) has been called", key);
        testJPARepository.save(value);
    }

    @Override
    public void storeAll(Map<Integer, TestDomain> map) {
        log.info("storeAll has been called");
        map.forEach( (k, v) -> testJPARepository.save(v));
    }

    @Override
    public void delete(Integer key) {
        log.info("delete({}) has been called", key);
        testJPARepository.delete(testJPARepository.findById(key));
    }

    @Override
    public void deleteAll(Collection<Integer> keys) {
        keys.forEach( (k) -> testJPARepository.delete(testJPARepository.findById(k)));
    }

    @Override
    public TestDomain load(Integer key) {
        log.info("load({}) has been called", key);
        return testJPARepository.findById(key);
    }

    @Override
    public Map<Integer, TestDomain> loadAll(Collection<Integer> keys) {
        Map<Integer, TestDomain> map = new HashMap<>();
        for (Integer key : keys) {
            TestDomain testDomain = this.load(key);
            if (testDomain != null) {
                map.put(key, testDomain);
            }
        }
        return map;
    }

    @Override
    public Iterable<Integer> loadAllKeys() {
        log.info("loadAllKeys has been called");
        return testJPARepository.getAllIds();
    }

    @Override
    public void init(HazelcastInstance hazelcastInstance, Properties properties, String mapName) {
        log.info("TestDemoStore::initializing");
        this.applicationContext.getEnvironment().getPropertySources().addFirst(new PropertiesPropertySource(
                "TestDemoStore", properties));
        this.applicationContext.register(HazelcastConfig.class);
        this.applicationContext.refresh();
        this.testJPARepository = this.applicationContext.getBean(TestJPARepository.class);
        this.hazelcastInstance = hazelcastInstance;
        log.info("TestDemoStore::initialized");
    }

    @Override
    public void destroy() {
        log.info("JpaPersonMapStore::destroying");
        this.hazelcastInstance.getMap("testDomain").destroy();
        this.hazelcastInstance.getMap("testDomain1").destroy();
        this.hazelcastInstance.getMap("testDomain2").destroy();
        this.hazelcastInstance.getMap("testDomain3").destroy();
        this.hazelcastInstance.getMap("tesDomain").destroy();
        this.hazelcastInstance.getMap("tesDomain1").destroy();
        this.applicationContext.close();
        log.info("JpaPersonMapStore::destroyed");
    }
}
