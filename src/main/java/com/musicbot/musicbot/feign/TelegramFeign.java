package com.musicbot.musicbot.feign;


import com.musicbot.musicbot.payload.TelegramResponse;
import com.musicbot.musicbot.utils.BotConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@FeignClient(url = BotConstants.TELEGRAM_BASE_URL_WITH_BOT + BotConstants.BOT_TOKEN,
        name = "TelegramFeign")
public interface TelegramFeign {

    @PostMapping("/sendMessage")
    TelegramResponse sendMessageToUser(@RequestBody SendMessage sendMessage);
}
