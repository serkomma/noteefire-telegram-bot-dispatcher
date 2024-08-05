package com.serkomma.dispatcher.services;

//import com.serkomma.dispatcher.configuration.TelegramBotConfiguration;
import com.serkomma.dispatcher.controller.TelegramBotController;
import com.serkomma.dispatcher.exceptions.WrongCommandException;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.web.context.annotation.SessionScope;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
@Component
@ApplicationScope
public class ScheduleHandler implements UpdateHandler{
    private Update updateOld;
    @Override
    public SendMessage handle(Update update) throws Exception{
        if (update.hasMessage() && update.getMessage().hasText()) {
            return switch (update.getMessage().getText()) {
                case "/start" -> this.first(update);
                default -> throw new WrongCommandException(update,"Неизвестная команда");
            };
        } else if (update.hasCallbackQuery()){
            return switch (update.getCallbackQuery().getData()){
                case "&__ME" -> startScheduleForMe(update);
                case "&__SOME" -> startScheduleForSomeone(update);
                default -> throw new WrongCommandException(update,"Неизвестная команда");
            };
        }
        else return null;
    }

    public SendMessage first(Update update){
        System.out.println("FIRST");
        var response = new SendMessage();
        String text = """
                Добро пожаловать в NoteeFire - телеграм бот для планирования напоминаний.
                Вы можете создать напоминание для себя и для другого пользователя телеграм.
                Выберите, для кого вы создаёте уведомление:""";
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText(text);

        List<InlineKeyboardButton> rowButtons = new ArrayList<>();
        List<List<InlineKeyboardButton>> rowsButtons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        var buttonMe = new InlineKeyboardButton();
        buttonMe.setText("Для себя");
        buttonMe.setCallbackData("&__ME");
        var buttonSo = new InlineKeyboardButton();
        buttonSo.setText("Для другого пользователя");
        buttonSo.setCallbackData("&__SOME");

        rowButtons.add(buttonMe);
        rowButtons.add(buttonSo);
        rowsButtons.add(rowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsButtons);
        response.setReplyMarkup(inlineKeyboardMarkup);
        updateOld = update;
        return response;
    }

    private SendMessage startScheduleForMe(Update update){
        var response = new SendMessage();
        response.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        response.setText("Погнали");
        return response;
    }

    private SendMessage startScheduleForSomeone(Update update){
        var response = new SendMessage();
        response.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        response.setText("Начали!");
        return response;
    }

    public SendMessage error(Update update) throws WrongCommandException {
        throw new WrongCommandException(update,"Неизвестная команда");
    }
}
