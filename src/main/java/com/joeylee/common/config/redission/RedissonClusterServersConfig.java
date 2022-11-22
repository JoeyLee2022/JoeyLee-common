package com.joeylee.common.config.redission;

import cn.hutool.core.lang.Validator;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

/**
 * redission 集群模式配置
 *
 * @author joeylee
 */
public class RedissonClusterServersConfig extends RedissonBaseConfig {


    //节点，例如redis://127.0.0.1:6378,redis://127.0.0.1:6377,
    protected String[] NodeAddress;


    //扫描间隔时间，单位是毫秒
    protected int scanInterval;

    @Override
    public RedissonClient redissonClient() {

        Config config = new Config();

        //集群模式配置 setScanInterval()扫描间隔时间，单位是毫秒, //可以用"rediss://"来启用SSL连接
        config.useClusterServers().setScanInterval(2000).addNodeAddress(NodeAddress);
        if (Validator.isEmpty(redisPassword)) {
            config.useSingleServer().setPassword(null);
        } else {
            config.useSingleServer().setPassword(redisPassword);
        }
        return Redisson.create(config);
    }

    public String[] getNodeAddress() {
        return NodeAddress;
    }

    public void setNodeAddress(String[] nodeAddress) {
        NodeAddress = nodeAddress;
    }

    public int getScanInterval() {
        return scanInterval;
    }

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }
}
