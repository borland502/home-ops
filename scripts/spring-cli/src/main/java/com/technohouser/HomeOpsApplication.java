package com.technohouser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.technohouser")
public class HomeOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeOpsApplication.class, args);
    }
}
