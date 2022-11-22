package com.joeylee.common.utils.ribbon;

import cn.hutool.core.util.ObjectUtil;
import com.joeylee.common.domain.entity.RibbonServer;
import com.joeylee.common.domain.exception.JoeyLeeException;
import com.joeylee.common.utils.http.BaseHttpUtil;
import com.joeylee.common.utils.http.RestTemplateUtil;
import com.netflix.client.DefaultLoadBalancerRetryHandler;
import com.netflix.client.RetryHandler;
import com.netflix.client.config.CommonClientConfigKey;
import com.netflix.client.config.DefaultClientConfigImpl;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.LoadBalancerBuilder;
import com.netflix.loadbalancer.LoadBalancerStats;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import rx.Observable;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ribbon 工具类
 *
 * @author joeylee
 */
@Slf4j
@Data
@ToString(callSuper = true)
public class RibbonUtil extends BaseHttpUtil {

    private RetryHandler retryHandler;
    private LoadBalancerCommand<String> loadBalancerCommand;
    private ILoadBalancer loadBalancer;

    private RestTemplateUtil restTemplateUtil;


    //localhost:8983,localhost:8984 ...
    private String serverListString;
    private List<RibbonServer> ribbonServerList;
    //单节点最大重试次数，失败达到最大值时，切换到下一个示例
    private int maxAutoRetries = 0;
    //更换下一个重试节点的最大次数，可以设置为服务提供者副本数，也是就每个副本都查询一次
    private int maxAutoRetriesNextServer = 3;

    //是否对所有请求进行重试，默认fasle，则只会对GET请求进行重试，建议配置为false，不然添加数据接口，会造成多条重复，也就是幂等性问题。
    private boolean retryEnabled;

    //节点去重后数量
    private int nodeDistinctSize;

    @PostConstruct
    public void postConstruct() {
        if (ObjectUtil.isEmpty(ribbonServerList)) {
            log.error("ribbonServerList is empty");
            return;
        }
        this.nodeDistinctSize = (int) ribbonServerList.stream().distinct().count();

        List<Server> serverList = ribbonServerList.stream().map(server -> {
            if (ObjectUtil.isNotEmpty(server.getPort())) {
                return new Server(server.getHost(), server.getPort());
            } else {
                return new Server(server.getHost(), -1);
            }
        }).collect(Collectors.toList());
        this.serverListString = serverList.stream().map(server -> server.getHostPort()).collect(Collectors.joining(","));

        retryHandler = new DefaultLoadBalancerRetryHandler(maxAutoRetries, maxAutoRetriesNextServer, retryEnabled);

        IClientConfig iClientConfig = DefaultClientConfigImpl.getEmptyConfig();
        iClientConfig.set(CommonClientConfigKey.ReadTimeout, readTimeout);
        iClientConfig.set(CommonClientConfigKey.ConnectTimeout, connectionTimeout);
        iClientConfig.set(CommonClientConfigKey.MaxAutoRetries, maxAutoRetries);
        iClientConfig.set(CommonClientConfigKey.MaxAutoRetriesNextServer, maxAutoRetriesNextServer);
        iClientConfig.set(CommonClientConfigKey.ConnectionManagerTimeout, connectionTimeout);

        loadBalancer = LoadBalancerBuilder.newBuilder()
                //定义各种API用于初始化客户端或负载平衡器以及执行方法的客户端配置。
                .withClientConfig(iClientConfig)
                .buildFixedServerListLoadBalancer(serverList);

        loadBalancerCommand = LoadBalancerCommand.<String>builder()
                // 负载均衡器
                .withLoadBalancer(loadBalancer)
                // 重试处理程序
                //.withRetryHandler(retryHandler)
                .build();
        log.info("{}: {}", this.getClass().getSimpleName(), this);
    }

    private String execute(RestTemplateUtil restTemplateUtil) {
        restTemplateUtil.setHttpMethod(httpMethod);
        return restTemplateUtil.execute();
    }

    @Override
    public String execute() {
        if (ObjectUtil.isEmpty(ribbonServerList)) {
            log.error("ribbonServerList is empty");
            throw new JoeyLeeException("ribbonServerList is empty");
        }
        if (nodeDistinctSize > 1) {
            return loadBalancerCommand.submit(server -> {
                try {
                    restTemplateUtil.setUrl(BaseHttpUtil.getMiddle(server.getHost(), server.getPort()));
                    String execute = execute(restTemplateUtil);
                    return Observable.just(execute);
                } catch (Exception e) {
                    return Observable.error(e);
                }
            }).toBlocking().first();
        }
        RibbonServer ribbonServer = ribbonServerList.get(0);
        restTemplateUtil.setUrl(ribbonServer.getUrl());
        String execute = execute(restTemplateUtil);
        return execute;


        //HttpResourceGroup httpResourceGroup = Ribbon.createHttpResourceGroup(resourceGroupName, ClientOptions.create()
        //        //(MaxAutoRetries): 单节点最大重试次数，达到最大值时，切换到下一个示例
        //        .withMaxAutoRetries(maxAutoRetries)
        //        //(MaxAutoRetriesNextServer)：更换下一个重试节点的最大次数，可以设置为服务提供者副本数，也是就每个副本都查询一次
        //        .withMaxAutoRetriesNextServer(maxAutoRetriesNextServer).withConnectTimeout(1000).withConfigurationBasedServerList(serverListString));
        //
        //HttpRequestTemplate<ByteBuf> requestTemplate = httpResourceGroup.newTemplateBuilder(templateName, ByteBuf.class).withMethod(httpMethod.toString()).withUriTemplate(url).withFallbackProvider(fallbackHandler).withResponseValidator(responseValidator).build();
        //
        //Observable<ByteBuf> result = requestTemplate.requestBuilder().withRequestProperty("userId", "user1").build().observe();
        //ByteBuf first = result.toBlocking().first();
        //String resultString = first.toString(CharsetUtil.UTF_8);
        //log.info("resultString: {}", resultString);
        //Assertions.assertTrue(ArrayUtil.contains(ports, Integer.parseInt(resultString)));

    }


    /**
     * 获取负载均衡器的状态
     *
     * @return
     */
    public LoadBalancerStats getLoadBalancerStats() {
        return ((BaseLoadBalancer) loadBalancer).getLoadBalancerStats();
    }

}
