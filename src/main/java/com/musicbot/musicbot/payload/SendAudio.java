package com.musicbot.musicbot.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SendAudio {

    @JsonProperty("chat_id")
    private Long chatId;

    private String audio;

    private String caption;

    @JsonProperty("reply_markup")
    private InlineKeyboardMarkup keyboardMarkup;
}
