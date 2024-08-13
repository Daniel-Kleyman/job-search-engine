package com.danielkleyman.jobsearchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.danielkleyman.jobsearchlnk", "com.danielkleyman.jobsearchcommon"})
public class JobSearchEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobSearchEngineApplication.class, args);
    }

}
