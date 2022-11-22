package com.joeylee.common.config.redission;

import cn.hutool.core.lang.Validator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * redission 单机配置
 *
 * @author joeylee
 */
public class RedissonSingleServerConfig extends RedissonBaseConfig {

    protected String host;

    protected String port;


    @Override
    public RedissonClient redissonClient() {

        Config config = new Config();

        // 单节点
        config.useSingleServer().setAddress("redis://" + host + ":" + port);
        if (Validator.isEmpty(redisPassword)) {
            config.useSingleServer().setPassword(null);
        } else {
            config.useSingleServer().setPassword(redisPassword);
        }

        return Redisson.create(config);
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
}
