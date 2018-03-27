package org.archivemanager.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;


@ImportResource("spring.xml")
@SpringBootApplication
public class ArchiveManagerServer {

	
	public static void main(String[] args) {
		SpringApplication.run(ArchiveManagerServer.class, args);
	}
}
