package com.sparta.communityback.repository;

import com.sparta.communityback.entity.RefreshToken;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;


@EnableRedisRepositories
public interface RedisRepository extends CrudRepository<RefreshToken, Long> {

}
