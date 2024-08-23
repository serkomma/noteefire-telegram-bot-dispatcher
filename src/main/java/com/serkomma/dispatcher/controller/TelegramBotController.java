package com.serkomma.dispatcher.controller;

import com.serkomma.dispatcher.entities.CachedNotificationEntity;
import com.serkomma.dispatcher.exceptions.TelegramBotException;
import com.serkomma.dispatcher.services.ScheduleHandler;
import com.serkomma.dispatcher.services.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
@RestController
public class TelegramBotController extends TelegramLongPollingBot {
    @Autowired
    private ApplicationContext context;

    public TelegramBotController(@Value("${bot.token}")String botToken){
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        var handler = getHandler(update);
        try {
            send(handler.handle(update));
        } catch (TelegramBotException e){
            var response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(e.getMessage());
            send(response);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    @Override
    public String getBotUsername() {
        return "NoteeFire Bot";
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }

    private UpdateHandler getHandler(Update update){
        // todo
//        return new ScheduleHandler(context.getBean(CachedNotificationRepository.class));
        return context.getBean(ScheduleHandler.class);
    }
//    @ExceptionHandler(WrongCommandException.class)
    public void send(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException tae){

        }
    }

    @PostMapping("/")
    public void notificationSend(
            @RequestHeader long chatId,
            @RequestBody CachedNotificationEntity cachedNotificationEntity){
        var response = new SendMessage();
        response.setChatId(chatId);
        response.setText(cachedNotificationEntity.getNotification());
        send(response);
    }
}
