package com.musicbot.musicbot.service;


import com.musicbot.musicbot.entity.Music;
import com.musicbot.musicbot.feign.TelegramFeign;
import com.musicbot.musicbot.payload.EditMessage;
import com.musicbot.musicbot.payload.SendAudio;
import com.musicbot.musicbot.repository.MusicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Audio;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BotService {

    private final TelegramFeign telegramFeign;

    private final MusicRepository musicRepository;

    private final RedisTemplate<Long, String> redisTemplate;

    private static final Integer PAGE_SIZE = 10;

    private static final String PARSE_MODE = "HTML";

    public void start(Update update) {
        String message = "Just send music name or/and artist name, I will find you";
        telegramFeign.sendMessage(sendMessage(message, getChatId(update)));
    }

    public void uploadMusic(Update update) {
        String message = "Your music successfully added";
        Audio audio = update.getMessage().getAudio();
        musicRepository.save(new Music(
                audio.getFileId(),
                audio.getFileUniqueId(),
                audio.getFileName(),
                audio.getFileSize(),
                update.getMessage().getFrom().getId(),
                audio.getDuration(),
                audio.getPerformer(),
                audio.getTitle(),
                audio.getMimeType()
        ));
        telegramFeign.sendMessage(sendMessage(message, getChatId(update)));


    }

    public void sendSearchResults(Update update, Integer currentPage) {
        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE);
        Page<Music> searchMusicResults = musicRepository.searchMusic(
                pageable,
                musicKey(update, true)
        );
        String message =
                generateListOfMusics(searchMusicResults);
        InlineKeyboardMarkup inlineButtons =
                generateInlineButtonsToMusicList(searchMusicResults, currentPage);
        telegramFeign.sendMessage(sendMessage(
                message,
                getChatId(update),
                inlineButtons
        ));
    }

    public void editSearchResults(Update update, Integer currentPage) {
        Pageable pageable = PageRequest.of(currentPage, PAGE_SIZE);
        Page<Music> searchMusicResults = musicRepository.searchMusic(
                pageable,
                musicKey(update, false)
        );
        if (searchMusicResults.getContent().size() < 1) {
            telegramFeign.sendAnswerCallbackQuery(answerCallBackQuery(
                    update,
                    "Nothing was found\uD83D\uDE14"
            ));
        } else {
            String message =
                    generateListOfMusics(searchMusicResults);
            InlineKeyboardMarkup inlineButtons =
                    generateInlineButtonsToMusicList(searchMusicResults, currentPage);
            telegramFeign.editMessage(new EditMessage(
                    getChatId(update),
                    update.getCallbackQuery().getMessage().getMessageId(),
                    message,
                    inlineButtons,
                    PARSE_MODE
            ));
        }
    }

    private AnswerCallbackQuery answerCallBackQuery(Update update, String text) {
        AnswerCallbackQuery answerCallbackQuery = new AnswerCallbackQuery();
        answerCallbackQuery.setText(text);
        answerCallbackQuery.setCallbackQueryId(update.getCallbackQuery().getId());
        return answerCallbackQuery;
    }

    private String musicKey(Update update, boolean set) {
        if (!set)
            return redisTemplate.opsForValue().get(getChatId(update));
        else {
            redisTemplate.opsForValue().set(
                    getChatId(update),
                    update.getMessage().getText()
            );
            return update.getMessage().getText();
        }
    }

    private InlineKeyboardMarkup generateInlineButtonsToMusicList(
            Page<Music> searchMusicResults,
            Integer currentPage
    ) {
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        generateInlineNumButtonsToMusicList(searchMusicResults, rowsInline);
        generateInlineModifyButtonsToMusicList(rowsInline, currentPage);
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        inlineKeyboardMarkup.setKeyboard(rowsInline);
        return inlineKeyboardMarkup;
    }

    public void downloadMusic(Update update) {
        String data = update.getCallbackQuery().getData();
        Optional<Music> musicById = musicRepository.findById(
                Long.valueOf(data.substring(data.indexOf(":") + 1))
        );
        if (musicById.isPresent()) {
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
            List<InlineKeyboardButton> downloadMusicButtons = new ArrayList<>();
            InlineKeyboardButton deleteButton = new InlineKeyboardButton();
            deleteButton.setText("❌");
            deleteButton.setCallbackData("DELETE:");
            downloadMusicButtons.add(deleteButton);
            rowsInline.add(downloadMusicButtons);
            inlineKeyboardMarkup.setKeyboard(rowsInline);
            SendAudio sendAudio = new SendAudio();
            sendAudio.setAudio(musicById.get().getFileId());
            sendAudio.setCaption(musicById.get().getMimeType());
            sendAudio.setChatId(getChatId(update));
            sendAudio.setKeyboardMarkup(inlineKeyboardMarkup);
            telegramFeign.sendMessage(sendAudio);
        } else {
            //TODO change
            telegramFeign.sendMessage(
                    sendMessage("Oops something went wrong",
                            getChatId(update))
            );
        }

    }

    private static void generateInlineNumButtonsToMusicList(
            Page<Music> searchMusicResults,
            List<List<InlineKeyboardButton>> rowsInline) {
        List<InlineKeyboardButton> inlineNumericButtons = new ArrayList<>();
        List<Music> searchMusicList = searchMusicResults.getContent();
        for (int i = 0; i < searchMusicList.size(); i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(String.valueOf(i + 1));
            button.setCallbackData("DOWNLOAD:" + searchMusicList.get(i).getId());
            inlineNumericButtons.add(button);
            if (inlineNumericButtons.size() == 5) {
                rowsInline.add(inlineNumericButtons);
                inlineNumericButtons = new ArrayList<>();
            }
        }
        rowsInline.add(inlineNumericButtons);
    }

    private static void generateInlineModifyButtonsToMusicList(
            List<List<InlineKeyboardButton>> rowsInline, Integer currentPage) {
        List<InlineKeyboardButton> inlineModifyButtons;
        inlineModifyButtons = new ArrayList<>();
        InlineKeyboardButton previousButton = new InlineKeyboardButton();
        previousButton.setText("⬅");
        previousButton.setCallbackData("PREVIOUS:" + currentPage);
        InlineKeyboardButton deleteButton = new InlineKeyboardButton();
        deleteButton.setText("❌");
        deleteButton.setCallbackData("DELETE:");
        InlineKeyboardButton nextButton = new InlineKeyboardButton();
        nextButton.setText("➡");
        nextButton.setCallbackData("NEXT:" + currentPage);


        inlineModifyButtons.add(previousButton);
        inlineModifyButtons.add(deleteButton);
        inlineModifyButtons.add(nextButton);
        rowsInline.add(inlineModifyButtons);
    }

    private static String generateListOfMusics(Page<Music> searchMusicResults) {
        StringBuilder message = new StringBuilder();
        List<Music> searchMusicList = searchMusicResults.getContent();
        System.out.println(searchMusicList.size());
        if (searchMusicList.size() == 0) {
            message.append("Nothing was found\uD83D\uDE14");
        } else {
            message
                    .append("<b>Results: ")
                    .append(searchMusicResults.getNumber() * 10 + 1)
                    .append("-")
                    .append(searchMusicResults.getNumberOfElements() + searchMusicResults.getNumber() * 10)
                    .append(" out of ")
                    .append(searchMusicResults.getTotalElements())
                    .append("</b>")
                    .append("\n\n");
            for (int i = 0; i < searchMusicList.size(); i++) {
                Music music = searchMusicList.get(i);
                message
                        .append(i + 1).append(" ")
                        .append(music.getFileName())
                        .append(" <i>")
                        .append(String.format("%d:%02d", music.getDuration() / 60, music.getDuration() % 60))
                        .append(" ")
                        .append(music.getFileSize() / 1024 / 1024).append(".")
                        .append(music.getFileSize() / 1024 % 1024 / 100)
                        .append("M")
                        .append("</i>")
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
        sendMessage.setParseMode(PARSE_MODE);
        return sendMessage;
    }

    private static Long getChatId(Update update) {
        return update.hasMessage() ?
                update.getMessage().getChatId() :
                update.getCallbackQuery().getMessage().getChatId();
    }

    public void deleteMessage(Update update) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        deleteMessage.setChatId(getChatId(update));
        try {
            telegramFeign.deleteMessage(deleteMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getPreviousPage(Update update) {
        String data = update.getCallbackQuery().getData();
        int currentPage = Integer.parseInt(data.substring(data.indexOf(":") + 1));
        if (currentPage > 0) {
            currentPage--;
            editSearchResults(update, currentPage);
        } else {
            telegramFeign.sendAnswerCallbackQuery(answerCallBackQuery(
                    update,
                    "You are in the first page"
            ));
        }
    }

    public void getNextPage(Update update) {
        String data = update.getCallbackQuery().getData();
        Integer currentPage = Integer.parseInt(data.substring(data.indexOf(":") + 1));
        currentPage++;
        editSearchResults(update, currentPage);
    }
}
