package com.sparta.bipuminbe.common.util.redis;

import org.springframework.data.repository.CrudRepository;

public interface EmailRedisRepository extends CrudRepository<EmailCode, String> {
}
