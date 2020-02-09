package com.example.batch.cassandra.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameter;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MemberJobController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("loadMemberJob")
    Job memberDataSanitizationJob;

    @GetMapping("/run-member-job")
    public String handle() throws Exception {
        Map<String, JobParameter> confMap = new HashMap<String, JobParameter>();
        confMap.put("time", new JobParameter(System.currentTimeMillis()));
        JobParameters jobParameters = new JobParameters(confMap);
        jobLauncher.run(memberDataSanitizationJob, jobParameters);
        return "Batch job has been invoked";
    }
}