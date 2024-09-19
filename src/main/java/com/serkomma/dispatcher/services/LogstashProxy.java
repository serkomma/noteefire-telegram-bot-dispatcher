package com.serkomma.dispatcher.services;

import com.serkomma.dispatcher.entities.CachedNotificationEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "opensearch",
             url  = "${logstash.address}")
public interface OpensearchProxy {
    @PostMapping("/")
    void save_notification(@RequestHeader long chatId,
                           @RequestBody CachedNotificationEntity cachedNotificationEntity);
}
