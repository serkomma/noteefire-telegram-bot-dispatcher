package com.serkomma.dispatcher.repositories;

import com.serkomma.dispatcher.entities.CachedNotificationEntity;
import org.springframework.data.keyvalue.repository.KeyValueRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CachedNotificationRepository extends KeyValueRepository<CachedNotificationEntity, Long> {
}
