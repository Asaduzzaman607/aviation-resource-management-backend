package com.digigate.engineeringmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class EngineeringManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(EngineeringManagementApplication.class, args);
	}

}
