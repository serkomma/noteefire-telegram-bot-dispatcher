package com.serkomma.dispatcher.controller;

import com.serkomma.dispatcher.exceptions.WrongCommandException;
import com.serkomma.dispatcher.services.ScheduleHandler;
import com.serkomma.dispatcher.services.UpdateHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
@Controller
public class TelegramBotController extends TelegramLongPollingBot {

    public TelegramBotController(@Value("${bot.token}")String botToken){
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        var handler = getHandler(update);
        try {
            send(handler.handle(update));
        } catch (Exception e){
            var response = new SendMessage();
            response.setChatId(update.getMessage().getChatId().toString());
            response.setText(e.getMessage());
            send(response);
        }
//        var text = update.getMessage().getText();
//        System.out.println(text);
//        var response = new SendMessage();
//        response.setChatId(update.getMessage().getChatId().toString());
//        response.setText("Сам " + text);
//
//        List<InlineKeyboardButton> rowbuttons = new ArrayList<>();
//        List<List<InlineKeyboardButton>> rowsbuttons = new ArrayList<>();
//        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
//
//        var button_me = new InlineKeyboardButton();
//        button_me.setText("Пойти нахуй");
//        button_me.setCallbackData("QW");
//        var button_so = new InlineKeyboardButton();
//        button_so.setText("Тоже пойти нахуй");
//        button_so.setCallbackData("AS");
//
//        rowbuttons.add(button_me);
//        rowbuttons.add(button_so);
//        rowsbuttons.add(rowbuttons);
//        inlineKeyboardMarkup.setKeyboard(rowsbuttons);
//
//        response.setReplyMarkup(inlineKeyboardMarkup);
//        try {
//            execute(response);
//        } catch (TelegramApiException e){
//            System.out.println(e.getMessage());
//        }
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
        return new ScheduleHandler();
    }
//    @ExceptionHandler(WrongCommandException.class)
    public void send(SendMessage message){
        try {
            execute(message);
        } catch (TelegramApiException tae){

        }
    }
}
