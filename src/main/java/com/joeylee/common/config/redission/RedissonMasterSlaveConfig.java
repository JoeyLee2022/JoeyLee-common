package com.joeylee.common.config.redission;

import cn.hutool.core.lang.Validator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * redission 主从配置
 *
 * @author joeylee
 */
public class RedissonMasterSlaveConfig extends RedissonBaseConfig {

    //主节点，例如redis://127.0.0.1:6379
    protected String masterAddress;

    //从节点，例如redis://127.0.0.1:6378,redis://127.0.0.1:6377,
    protected String[] slaveAddress;


    @Override
    public RedissonClient redissonClient() {

        Config config = new Config();

        // 添加主从配置
        config.useMasterSlaveServers().setMasterAddress(masterAddress).addSlaveAddress(slaveAddress);
        if (Validator.isEmpty(redisPassword)) {
            config.useSingleServer().setPassword(null);
        } else {
            config.useSingleServer().setPassword(redisPassword);
        }

        return Redisson.create(config);
    }

    public String getMasterAddress() {
        return masterAddress;
    }

    public void setMasterAddress(String masterAddress) {
        this.masterAddress = masterAddress;
    }

    public String[] getSlaveAddress() {
        return slaveAddress;
    }

    public void setSlaveAddress(String[] slaveAddress) {
        this.slaveAddress = slaveAddress;
    }
}
