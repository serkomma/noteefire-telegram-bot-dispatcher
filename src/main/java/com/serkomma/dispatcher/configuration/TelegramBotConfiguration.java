//package com.serkomma.dispatcher.configuration;
//
//import com.serkomma.dispatcher.controller.TelegramBotController;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.telegram.telegrambots.meta.TelegramBotsApi;
//import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
//import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
//
//@Configuration
//@ComponentScan(basePackages = "com.serkomma.dispatcher")
//public class TelegramBotConfiguration {
//    @Bean
//    public TelegramBotsApi telegramBotsApi(TelegramBotController telegramBotController) throws TelegramApiException {
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//        telegramBotsApi.registerBot(telegramBotController);
//        return telegramBotsApi;
//    }
//}
