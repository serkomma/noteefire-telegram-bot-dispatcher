package com.serkomma.dispatcher.exceptions;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

public class WrongDataException extends TelegramBotException {
    @Getter
    private final Update update;
    public WrongDataException(Update update, String message){
        super(message);
        this.update = update;
    }
}
