package com.serkomma.dispatcher.entities;
import lombok.*;
import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class DBNotificationEntity implements Serializable {
    @Id
    private LocalDateTime dateTime;
    private long chatid;
    private String notification;
}