package com.musicbot.musicbot.feign;


import com.musicbot.musicbot.payload.BooleanResponse;
import com.musicbot.musicbot.payload.EditMessage;
import com.musicbot.musicbot.payload.SendAudio;
import com.musicbot.musicbot.payload.TelegramResponse;
import com.musicbot.musicbot.utils.BotConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

@FeignClient(url = BotConstants.TELEGRAM_BASE_URL_WITH_BOT + BotConstants.BOT_TOKEN,
        name = "TelegramFeign")
public interface TelegramFeign {

    @PostMapping("/sendMessage")
    TelegramResponse sendMessage(@RequestBody SendMessage sendMessage);

    @PostMapping("/sendAudio")
    TelegramResponse sendMessage(@RequestBody SendAudio sendAudio);

    //TODO check/change return type
    @PostMapping("/deleteMessage")
    BooleanResponse deleteMessage(@RequestBody DeleteMessage deleteMessage);

    //TODO Fix bug Bad Request Query is too old and response timeout expired
    @PostMapping("/answerCallbackQuery")
    BooleanResponse sendAnswerCallbackQuery(@RequestBody AnswerCallbackQuery answerCallbackQuery);

    @PostMapping("/editMessageText")
    void editMessage(@RequestBody EditMessage editMessage);


}
