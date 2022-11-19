package com.musicbot.musicbot.service;

import com.musicbot.musicbot.feign.TelegramFeign;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class WebHookService {

    private final TelegramFeign telegramFeign;

    private final BotService botService;

    public void waitUpdate(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                switch (update.getMessage().getText()) {
                    case "/start":
                        botService.whenStart(update);
                        break;
                    default:
                        botService.searchMusic(update);
                        break;
                }
            }
            else if(update.getMessage().hasAudio()){
                botService.saveMusic(update);
            }
        }
    }
}
