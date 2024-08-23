package com.serkomma.dispatcher.services;

import com.serkomma.dispatcher.exceptions.TelegramBotException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public interface UpdateHandler {
    SendMessage handle(Update update) throws TelegramBotException;

    default InlineKeyboardMarkup addButtons(String... args) {               // Pairs: <Text>, <CallbackData>
        List<List<InlineKeyboardButton>> rowsButtons = new ArrayList<>();
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        List<InlineKeyboardButton> rowButtons = IntStream
                .range(1, args.length + 1)
                .filter(i -> i % 2 == 0)
                .mapToObj(i ->
                {
                    var button = new InlineKeyboardButton();
                    button.setText(args[i - 2]);
                    button.setCallbackData(args[i - 1]);
                    return button;
                })
                .collect(Collectors.toList());
        rowsButtons.add(rowButtons);
        inlineKeyboardMarkup.setKeyboard(rowsButtons);
        return inlineKeyboardMarkup;
    }
}
