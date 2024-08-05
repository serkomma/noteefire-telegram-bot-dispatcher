package com.serkomma.dispatcher.exceptions;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Update;

public class WrongCommandException extends Exception{
    @Getter
    private Update update;
    public WrongCommandException(Update update, String message){
        super(message);
        this.update = update;
    }
}
