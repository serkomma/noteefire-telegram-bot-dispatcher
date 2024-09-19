package com.serkomma.dispatcher.entities;

import com.serkomma.dispatcher.services.ProcessSteps;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@Getter
public class OpensearchNotificationEntity implements Serializable {
    @Id
    private long chatid;
    private LocalDateTime datetime;
    private String notification;
}
