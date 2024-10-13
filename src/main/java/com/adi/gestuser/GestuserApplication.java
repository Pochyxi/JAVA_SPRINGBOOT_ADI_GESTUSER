package com.adi.gestuser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.adi.gestuser.*"})
@EntityScan(basePackages = {"com.adi.gestuser.entity"})
@EnableJpaRepositories(basePackages = {"com.adi.gestuser.repository"})
@PropertySource( "classpath:application-secret.properties" )
public class GestuserApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestuserApplication.class, args);
	}

}
