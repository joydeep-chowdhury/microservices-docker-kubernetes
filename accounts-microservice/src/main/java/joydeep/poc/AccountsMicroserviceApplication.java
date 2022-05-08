package joydeep.poc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
//@ComponentScans({ @ComponentScan("joydeep.poc.controllers") })
//@EnableJpaRepositories("joydeep.poc.repositories")
//@EntityScan("joydeep.poc.models")
public class AccountsMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AccountsMicroserviceApplication.class, args);
	}

}
