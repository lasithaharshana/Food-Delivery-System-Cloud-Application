package com.deliverysystem.authservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
@Slf4j
public class AuthserviceApplication {

	public static void main(String[] args) {
		var context = SpringApplication.run(AuthserviceApplication.class, args);
		Environment env = context.getEnvironment();
		String port = env.getProperty("server.port");
		String contextPath = env.getProperty("server.servlet.context-path", "");
		
		log.info("\n----------------------------------------------------------\n" +
				"Application '{}' is running! Access URLs:\n" +
				"Local: \t\thttp://localhost:{}{}\n" +
				"Swagger UI: \thttp://localhost:{}{}/docs\n" +
				"Health Check: \thttp://localhost:{}{}/actuator/health\n" +
				"----------------------------------------------------------",
				env.getProperty("spring.application.name"),
				port, contextPath,
				port, contextPath,
				port, contextPath);
	}

}
