package com.serkomma.dispatcher.services;

import com.serkomma.dispatcher.entities.OpensearchNotificationEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "logstash",
             url  = "${logstash.address}")
public interface LogstashProxy {
    @PostMapping("/")
    void save_notification(@RequestHeader long chatId,
                           @RequestBody OpensearchNotificationEntity cachedNotificationEntity);
}
