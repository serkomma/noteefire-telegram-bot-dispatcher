package com.serkomma.dispatcher.entities;

import com.serkomma.dispatcher.services.ProcessSteps;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@RedisHash("notification")
public class CachedNotificationEntity implements Serializable {
//    @Id
//    private  long id;
    @Id
    private long chatId;
    private String notification;
    private LocalDate date;
    private LocalTime time;
    private ProcessSteps step;
}
