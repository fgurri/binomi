package com.creugrogasoft.biphone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class BiPhoneApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(BiPhoneApplication.class, args);
		BIPhoneService.getLlegendaGrups();
	}
}
