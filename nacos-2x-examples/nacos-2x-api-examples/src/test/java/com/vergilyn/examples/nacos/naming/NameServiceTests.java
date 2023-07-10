package com.vergilyn.examples.nacos.naming;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import com.alibaba.nacos.client.naming.core.ServiceInfoUpdateService;
import com.alibaba.nacos.client.naming.remote.NamingClientProxyDelegate;
import com.vergilyn.examples.nacos.AbstractNacos2xTests;
import lombok.SneakyThrows;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

public class NameServiceTests extends AbstractNacos2xTests {

    /**
     * 所有的{@code #getAllInstances(...)}方法中，默认`subscribe = true`。<br/>
     * 所以，通过具体实现 {@linkplain NacosNamingService#getAllInstances(String, String, List, boolean)} 可知：
     * <pre>
     *     因为 `subscribe = true`，所以在client-side会创建一个后台定时任务任务，用于获取instances保存到本地缓存中：
     *     - {@linkplain NamingClientProxyDelegate#subscribe(String, String, String)}
     *     - {@linkplain ServiceInfoUpdateService#scheduleUpdateIfAbsent(String, String, String)}
     *     - {@linkplain ServiceInfoUpdateService.UpdateTask#run()}
     * </pre>
     *
     * <h3>备注</h3>
     * 需要注意，当 serviceName 始终不存在时，这部分 subscribe 是多余的，会在后台定时查询，并占用请求链接。
     */
    @SneakyThrows
    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void getAllInstances(boolean subscribe){
        String serviceName = "";
        String group = "";

        List<Instance> instances = _namingService.getAllInstances(serviceName, group, subscribe);
    }
}
