package com.musicbot.musicbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MusicBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(MusicBotApplication.class, args);
    }

}
