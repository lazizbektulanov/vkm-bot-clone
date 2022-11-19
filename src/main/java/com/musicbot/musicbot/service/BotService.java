package com.musicbot.musicbot.service;


import com.musicbot.musicbot.entity.Music;
import com.musicbot.musicbot.feign.TelegramFeign;
import com.musicbot.musicbot.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BotService {

    private final TelegramFeign telegramFeign;

    private final MusicRepository musicRepository;

    public void whenStart(Update update) {
        String message = "Just send music name or/and artist name, I will find you";
        telegramFeign.sendMessageToUser(sendMessage(message, update.getMessage().getChatId()));
    }

    public void saveMusic(Update update) {
        String message = "Your music successfully added";
        Audio audio = update.getMessage().getAudio();
        System.out.println("Audio performer: " + audio.getPerformer());
        System.out.println("Audio fileName: " + audio.getFileName());
        System.out.println("Audio title: " + audio.getTitle());
        musicRepository.save(new Music(
                audio.getFileId(),
                audio.getFileUniqueId(),
                audio.getFileName(),
                update.getMessage().getFrom().getFirstName()
        ));
        telegramFeign.sendMessageToUser(sendMessage(message, update.getMessage().getChatId()));

    }

    public void searchMusic(Update update) {
        List<Music> searchMusicResults =
                musicRepository.searchMusic(update.getMessage().getText());
        String message =
                generateListOfMusics(searchMusicResults);
        List<List<InlineKeyboardButton>> rowsInline =
                generateInlineButtonsToMusicList(searchMusicResults);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        telegramFeign.sendMessageToUser(sendMessage(
                message,
                update.getMessage().getChatId(),
                inlineKeyboardMarkup)
        );
    }

    private static List<List<InlineKeyboardButton>> generateInlineButtonsToMusicList(
            List<Music> searchMusicResults
    ) {
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        boolean isLengthGreaterTen = searchMusicResults.size() >= 10;
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        for (int i = 0; i < searchMusicResults.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i + 1));
            button.setCallbackData(String.valueOf(i));
            rowInline.add(button);
            if (rowInline.size() == 5 && isLengthGreaterTen) {
                rowsInline.add(rowInline);
                rowInline = new ArrayList<>();
            }
        }
        rowsInline.add(rowInline);
        rowInline = new ArrayList<>();
        InlineKeyboardButton previousButton = new InlineKeyboardButton();
        previousButton.setText("⬅️");
        previousButton.setCallbackData("Previous");
        InlineKeyboardButton cancelButton = new InlineKeyboardButton();
        cancelButton.setText("❌");
        cancelButton.setCallbackData("Cancel");
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("➡️");
        nextButton.setCallbackData("Next");

        rowInline.add(previousButton);
        rowInline.add(cancelButton);
        rowInline.add(nextButton);
        rowsInline.add(rowInline);
        return rowsInline;
    }

    private static String generateListOfMusics(List<Music> searchMusicResults) {
        StringBuilder message = new StringBuilder();
        if (searchMusicResults.size() == 0) {
            message.append("Nothing was found\uD83D\uDE14");
        } else {
            System.out.println(searchMusicResults);
            for (int i = 0; i < searchMusicResults.size(); i++) {
                message
                        .append(i + 1)
                        .append(" ")
                        .append(searchMusicResults.get(i).getMusicName())
                        .append("\n");
            }
        }
        return message.toString();
    }

    private static SendMessage sendMessage(String message, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    private static SendMessage sendMessage(String message, Long chatId,
                                           InlineKeyboardMarkup inlineKeyboardMarkup) {
        SendMessage sendMessage = sendMessage(message, chatId);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        return sendMessage;
    }
}
