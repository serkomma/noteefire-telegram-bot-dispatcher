package com.serkomma.dispatcher.services;

import com.serkomma.dispatcher.entities.CachedNotificationEntity;
import com.serkomma.dispatcher.entities.CachedUserEntity;
import com.serkomma.dispatcher.entities.OpensearchNotificationEntity;
import com.serkomma.dispatcher.exceptions.TelegramBotException;
import com.serkomma.dispatcher.exceptions.WrongCommandException;
import com.serkomma.dispatcher.exceptions.WrongDataException;
import com.serkomma.dispatcher.repositories.CachedNotificationRepository;
import com.serkomma.dispatcher.repositories.CachedUserRepository;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class ScheduleHandler implements UpdateHandler{

    final private CachedNotificationRepository cachedNotificationRepository;
    final private CachedUserRepository cachedUserRepository;
    final private NotificationProxy notificationProxy;
    final private LogstashProxy logstashProxy;
    final private OpehsearchProxy opensearchProxy;
    public ScheduleHandler(CachedNotificationRepository cachedNotificationRepository,
                           CachedUserRepository cachedUserRepository,
                           NotificationProxy notificationProxy,
                           LogstashProxy logstashProxy,
                           OpehsearchProxy opensearchProxy){
        this.cachedNotificationRepository = cachedNotificationRepository;
        this.cachedUserRepository = cachedUserRepository;
        this.notificationProxy = notificationProxy;
        this.logstashProxy = logstashProxy;
        this.opensearchProxy = opensearchProxy;
    }
    @Override
    public SendMessage handle(Update update) throws TelegramBotException {
        if (update.hasMessage() && update.getMessage().hasText()) {           // text
            var text = update.getMessage().getText();
            if (text.startsWith("/") && text.matches("[a-zA-Z/]+")) {    // check commands
                return switch (text) {
                    case "/start"  -> this.hello(update);
                    case "/cancel" -> this.hello(update);
                    case "/show"   -> this.show(update);
                    case "/search" -> this.askForKeyword(update);
                    default -> throw new WrongCommandException(update, "Неизвестная команда");
                };
            } else{                                                           // check user's answers
                var notification = checkCache(update);
                if (notification == null) throw new WrongCommandException(update, "Неизвестная команда, введите /start");
                switch (notification.getStep()){
                    case NEED_USERS -> {
                        this.writeUsers(update);
                        this.getUsers(update);
                        return this.askForText(update);
                    }
                    case NEED_TEXT -> {
                        this.writeText(update);
                        return this.askForDate(update);
                    }
                    case NEED_DATE -> {
                        this.writeDate(update);
                        return this.askForTime(update);
                    }
                    case NEED_TIME -> {
                        this.writeTime(update);
                        return this.confirmation(update);
                    }
                    case NEED_KEYWORD -> {
                        return this.search(update);
                    }
                    default -> throw new WrongCommandException(update, "Неизвестная команда");
                }
            }
        } else if (update.hasCallbackQuery()){                                  // button
            return switch (update.getCallbackQuery().getData()){
                case "&__ME"   -> startScheduleForMe(update);
                case "&__SOME" -> startScheduleForSomeone(update);
                case "&__YES"  -> save(update);
                case "&__NO"   -> cancel(update);
                default -> throw new WrongCommandException(update,"Неизвестная команда");
            };
        }
        else if (update.getMessage().hasContact()) {                            // contact
            this.writeUsers(update);
            this.getUsers(update);
            return this.askForText(update);
        } else
            throw new WrongCommandException(update,"Неизвестная команда");     // not a text, not a button, not a contact?
    }

    public SendMessage hello(Update update){
        clearCache(update);
        var response = new SendMessage();
        String text = """
                Добро пожаловать в NoteeFire - телеграм бот для планирования напоминаний.
                /show - показать список ранее запланированных уведомлений
                /cancel - отмена последовательности ввода данных на любом шаге
                /search - поиск среди уведомлений по ключевым словам
                """;
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText(text);
        response.setReplyMarkup(addButtons("Запланировать","&__ME"));// "Для другого пользователя", "&__SOME"));
        return response;
    }

    private SendMessage startScheduleForMe(Update update){
        this.writeStart(update, false);
        return askForText(update);
    }

    private SendMessage askForText(Update update){
        var response = new SendMessage();
        response.setChatId(getChatId(update));
        response.setText("О чём нужно напомнить?");
        return response;

    }

    private SendMessage askForDate(Update update){
        var response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText("Введите дату в формате ДД.ММ.ГГГГ");
        return response;

    }

    private SendMessage askForTime(Update update){
        var response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText("Введите время в формате ЧЧ:ММ:СС");
        return response;
    }

    private SendMessage confirmation(Update update){
        var response = new SendMessage();
        response.setChatId(update.getMessage().getChatId().toString());
        response.setText("Вы действительно хотите запланировать это событие?");
        response.setReplyMarkup(addButtons("Да","&__YES", "Нет", "&__NO"));
        return response;
    }

    private SendMessage save(Update update){
        var notification = cachedNotificationRepository
                .findById(update.getCallbackQuery().getMessage().getChatId())
                .orElse(null);
        var response = new SendMessage();
        response.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        response.setText("Событие сохранено. Ожидайте напоминания");
        System.out.println(notification.getChatid());
        System.out.println(notification.getNotification());
        System.out.println(notification.getDate());
        System.out.println(notification.getTime());
        System.out.println(notification.getStep().ordinal());
        notificationProxy.save_notification(getChatId(update), notification);         // send to notifier module
        OpensearchNotificationEntity opensearchNotificationEntity = OpensearchNotificationEntity.builder()
                        .chatid(notification.getChatid())
                        .datetime(LocalDateTime.of(notification.getDate(), notification.getTime()))
                        .notification(notification.getNotification())
                        .build();
        logstashProxy.save_notification(getChatId(update), opensearchNotificationEntity);             // send to Logstash
        return response;
    }

    private SendMessage cancel(Update update){
        clearCache(update);
        var response = new SendMessage();
        response.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        response.setText("Событие отменено. Введите /start");
        return response;
    }

    private SendMessage startScheduleForSomeone(@NotNull Update update){
        this.writeStart(update, true);
        var response = new SendMessage();
        response.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        response.setText("""
                            Введите @ пользователей, которым необходимо отправить уведомление,
                            вводя каждый логин с новой строки ии выберите "Прикрепить" - "Контакт""");
        return response;
    }

    private SendMessage error(Update update) throws WrongCommandException {
        throw new WrongCommandException(update,"Неизвестная команда");
    }

    private @Nullable CachedNotificationEntity checkCache(@NotNull Update update){
        return cachedNotificationRepository.findById(update.getMessage().getChatId()).orElse(null);
    }

    private void writeStart(@NotNull Update update, boolean forSomeone){
        ProcessSteps step = forSomeone ? ProcessSteps.NEED_USERS : ProcessSteps.NEED_TEXT;
        cachedNotificationRepository.save(CachedNotificationEntity
                .builder()
                .chatid(update.getCallbackQuery().getMessage().getChatId())
                .step(step)
                .build());
    }

    private void writeText(Update update) throws TelegramBotException{
        if (update.getMessage().getText().length() > 1024)
            throw new WrongDataException(update, "Текст уведомдения не должен превышать 1024 символа");
        CachedNotificationEntity entity = cachedNotificationRepository.findById(update.getMessage().getChatId())
                .orElseThrow(() -> new WrongCommandException(update, "Неверная команда. Введите /start"));
        entity.setNotification(update.getMessage().getText());
        entity.setStep(ProcessSteps.NEED_DATE);
        cachedNotificationRepository.save(entity);
    }

    private void writeDate(Update update) throws TelegramBotException {
        LocalDate localDate;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            localDate = LocalDate.parse(update.getMessage().getText(), dtf);
        } catch (DateTimeParseException exception){
            throw new WrongDataException(update, "Неверная дата");
        }
        if (localDate.isBefore(LocalDate.now())){
            throw new WrongDataException(update, "Введённая дата находится в прошлом");
        }
        CachedNotificationEntity entity = cachedNotificationRepository.findById(update.getMessage().getChatId())
                .orElseThrow(() -> new WrongCommandException(update, "Неверная команда. Введите /start"));
        entity.setDate(localDate);
        entity.setStep(ProcessSteps.NEED_TIME);
        cachedNotificationRepository.save(entity);
    }

    private void writeTime(Update update) throws TelegramBotException{
        LocalTime localTime;
        try {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");
            localTime = LocalTime.parse(update.getMessage().getText(), dtf);
        } catch (DateTimeParseException exception){
            throw new WrongDataException(update, "Неверное время");
        }
        CachedNotificationEntity entity = cachedNotificationRepository.findById(update.getMessage().getChatId())
                .orElseThrow(() -> new WrongCommandException(update, "Неверная команда. Введите /start"));
        if (entity.getDate().equals(LocalDate.now())){
            if (localTime.isBefore(LocalTime.now())){
                throw new WrongDataException(update, "Введённая дата находится в прошлом");
            }
        }
        entity.setTime(localTime);
        entity.setStep(ProcessSteps.READY);
        cachedNotificationRepository.save(entity);
    }

    public void writeUsers(Update update) throws WrongCommandException{
        List<String> users = new ArrayList<>();
        if (update.getMessage().hasContact()) {
            users.add(update.getMessage().getContact().toString());
        } else {
            users = Arrays.asList(update.getMessage().getText().split("\n"));
        }
        cachedUserRepository.save(CachedUserEntity
                .builder()
                .users(users)
                .chatid(getChatId(update))
                .build());
        CachedNotificationEntity entity = cachedNotificationRepository.findById(getChatId(update))
                .orElseThrow(() -> new WrongCommandException(update, "Неверная команда. Введите /start"));
        entity.setStep(ProcessSteps.NEED_TEXT);
        cachedNotificationRepository.save(entity);
    }

    public SendMessage show(Update update){
        var response = new SendMessage();
        response.setChatId(getChatId(update));
        StringBuilder stringBuilder = new StringBuilder();
        notificationProxy.getNotifications(getChatId(update)).forEach(i -> {
            stringBuilder.append(i.getDateTime().toString());
            stringBuilder.append(" ");
            stringBuilder.append(i.getNotification());
            stringBuilder.append("\n");
        });
        response.setText(stringBuilder.toString());
        System.out.println(stringBuilder);
        return response;
    }

    public SendMessage askForKeyword(Update update){
        cachedNotificationRepository.save(CachedNotificationEntity
                .builder()
                .chatid(getChatId(update))
                .step(ProcessSteps.NEED_KEYWORD)
                .build());
        var response = new SendMessage();
        response.setChatId(getChatId(update));
        response.setText("Введите ключевое слово");
        return response;
    }

    public SendMessage search(Update update){
        clearCache(update);
        var response = new SendMessage();
        response.setChatId(getChatId(update));
        StringBuilder stringBuilder = new StringBuilder();
        String urlText = java.net.URLEncoder.encode(update.getMessage().getText(), StandardCharsets.UTF_8);
        Map<String, String> data = Map
                .of("Content-Type", "text/html; charset=utf-8",
                        "chatid", String.valueOf(getChatId(update)), "phrase",urlText);
        opensearchProxy.search(data).forEach(i -> {
            stringBuilder.append(i.getDatetime().toString());
            stringBuilder.append(" ");
            stringBuilder.append(i.getNotification());
            stringBuilder.append("\n");
        });
        if (stringBuilder.isEmpty())
            response.setText("Ничего не найдено");
        else
            response.setText(stringBuilder.toString());
        System.out.println(stringBuilder);
        return response;
    }

    public void getUsers(Update update){
        System.out.println(cachedUserRepository.findAll());
    }

    private void clearCache(Update update){
        cachedNotificationRepository.deleteById(getChatId(update));
        cachedUserRepository.deleteById(getChatId(update));
    }

    private long getChatId(Update update){
        MaybeInaccessibleMessage message = update.getMessage();
        if (message == null){
            message = update.getCallbackQuery().getMessage();
        }
        return message.getChatId();
    }
}
