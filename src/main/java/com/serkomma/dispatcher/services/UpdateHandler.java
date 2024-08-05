package com.serkomma.dispatcher.services;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface UpdateHandler {
    SendMessage handle(Update update) throws Exception;
}
