package com.musicbot.musicbot.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class TelegramResponse {

    private boolean ok;

    private Message result;
}
