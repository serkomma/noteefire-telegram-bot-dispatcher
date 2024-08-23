package com.serkomma.dispatcher.services;

import com.serkomma.dispatcher.entities.CachedNotificationEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name="notification",
             url="${notif.address}")
public interface NotificationProxy {
    @PostMapping("/")
    void save_notification(@RequestHeader long chatId,
                           @RequestBody CachedNotificationEntity cachedNotificationEntity);
}
