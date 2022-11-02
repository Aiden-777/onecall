package org.tronproject.onecall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Admin
 * @version 1.0
 * @time 2022-10-13 01:13
 */
@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

}
