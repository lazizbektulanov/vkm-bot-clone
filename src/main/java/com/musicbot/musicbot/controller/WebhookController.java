package com.musicbot.musicbot.controller;


import com.musicbot.musicbot.service.BotService;
import com.musicbot.musicbot.service.WebHookService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequestMapping("/api/telegram")
@RequiredArgsConstructor
public class WebhookController {

    private final WebHookService webHookService;

    @PostMapping
    public void getUpdates(@RequestBody Update update){
        webHookService.waitUpdate(update);
    }
}
