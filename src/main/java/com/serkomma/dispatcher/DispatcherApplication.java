package com.serkomma.dispatcher;

import com.serkomma.dispatcher.controller.TelegramBotController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


@SpringBootApplication
public class DispatcherApplication {

    public static void main(String[] args) {
//        var context = new AnnotationConfigApplicationContext(DispatcherApplication.class);
        SpringApplication.run(DispatcherApplication.class, args);

    }

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramBotController telegramBotController) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(telegramBotController);
        return telegramBotsApi;
    }

}
