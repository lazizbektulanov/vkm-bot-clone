package com.musicbot.musicbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
public class WebHookService {

    private final BotService botService;

    public void waitUpdate(Update update) {
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                switch (update.getMessage().getText()) {
                    case "/start":
                        botService.start(update);
                        break;
                    default:
                        botService.sendSearchResults(update, 0);
                        break;
                }
            } else if (update.getMessage().hasAudio()) {
                botService.uploadMusic(update);
            }
        } else if (update.hasCallbackQuery()) {
            String callBackData = update.getCallbackQuery().getData();
            if (callBackData.startsWith("DOWNLOAD")) {
                botService.downloadMusic(update);
            } else if (callBackData.startsWith("DELETE")) {
                botService.deleteMessage(update);
            } else if (callBackData.startsWith("PREVIOUS")) {
                botService.getPreviousPage(update);
            } else if (callBackData.startsWith("NEXT")) {
                botService.getNextPage(update);
            }
        }
    }
}
