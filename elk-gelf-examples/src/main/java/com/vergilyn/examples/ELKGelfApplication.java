package com.vergilyn.examples;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author vergilyn
 * @date 2019-06-07
 */
@SpringBootApplication
@Slf4j
public class ELKGelfApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ELKGelfApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        ScheduledThreadPoolExecutor scheduled = new ScheduledThreadPoolExecutor(1);

        scheduled.scheduleAtFixedRate((Runnable) () -> {
            log.debug("debug message!");
            log.info("info message!");
            log.warn("warn message!");
            log.error("error message!");

            try {
                int i = 1 / 0;
            }catch (Exception e){
                log.error("exception: ", e);
            }
        }, 0,10, TimeUnit.SECONDS);

    }
}
