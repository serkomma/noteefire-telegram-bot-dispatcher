package com.serkomma.dispatcher.services;

import com.serkomma.dispatcher.entities.OpensearchNotificationEntity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.Map;

@FeignClient(name = "opensearch",
             url  = "${opensearch.address}")
public interface OpehsearchProxy {
    @GetMapping("/")
    List<OpensearchNotificationEntity> search(@RequestHeader Map<String, String> data);
}
