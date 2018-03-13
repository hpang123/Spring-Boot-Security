package hpang.spring.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * To run:
 * mvn spring-boot:run
 * Or package to jar:
 * mvn clean package
 * 
 */

@SpringBootApplication
//public class SpringSecurityApplication extends SpringBootServletInitializer {
public class SpringSecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityApplication.class, args);
	}
}
