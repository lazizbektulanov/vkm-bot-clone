package com.musicbot.musicbot.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EditMessage {

    @JsonProperty("chat_id")
    private Long chatId;

    @JsonProperty("message_id")
    private Integer messageId;

    private String text;

    @JsonProperty("reply_markup")
    private InlineKeyboardMarkup keyboardMarkup;

    @JsonProperty("parse_mode")
    private String parseMode;
}
