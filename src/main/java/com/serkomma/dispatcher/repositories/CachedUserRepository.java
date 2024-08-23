package com.serkomma.dispatcher.repositories;

import com.serkomma.dispatcher.entities.CachedUserEntity;
import org.springframework.data.keyvalue.repository.KeyValueRepository;

public interface CachedUserRepository extends KeyValueRepository<CachedUserEntity, Long> {
}
