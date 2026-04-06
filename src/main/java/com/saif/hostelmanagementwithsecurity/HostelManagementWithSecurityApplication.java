package com.saif.hostelmanagementwithsecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HostelManagementWithSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(HostelManagementWithSecurityApplication.class, args);
    }

}
