package com.serkomma.dispatcher.exceptions;

public class TelegramBotException extends Exception{
    public TelegramBotException(String message){
        super(message);
    }
}
