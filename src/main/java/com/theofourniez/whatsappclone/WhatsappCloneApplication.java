package com.theofourniez.whatsappclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.reactive.config.EnableWebFlux;

@SpringBootApplication
@EnableWebFlux
@EnableJpaRepositories
@ImportResource({ "classpath*:application-context.xml" })
public class WhatsappCloneApplication {

    public static void main(String[] args) {
        SpringApplication.run(WhatsappCloneApplication.class, args);
    }

}
