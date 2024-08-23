package com.serkomma.dispatcher.entities;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Data
@Builder
@RedisHash("user")
public class CachedUserEntity implements Serializable {
    @Id
//    private String user;
    private long chatid;
    private List<String> users;
}
