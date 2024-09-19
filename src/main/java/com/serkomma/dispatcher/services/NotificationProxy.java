package com.serkomma.dispatcher.services;

import com.serkomma.dispatcher.entities.CachedNotificationEntity;
import com.serkomma.dispatcher.entities.DBNotificationEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name="notifier",
             url="${notif.address}")
public interface NotificationProxy {
    @PostMapping("/")
    void save_notification(@RequestHeader long chatId,
                           @RequestBody CachedNotificationEntity cachedNotificationEntity);

    @GetMapping("/")
    List<DBNotificationEntity> getNotifications(@RequestHeader long chatId);
}
