package com.joeylee.common.config.redission;

import org.redisson.api.RedissonClient;

/**
 * redission 通用属性
 *
 * @author joeylee
 */
public abstract class RedissonBaseConfig {
    protected String redisPassword;

    protected abstract RedissonClient redissonClient();

    public String getRedisPassword() {
        return redisPassword;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }
}
