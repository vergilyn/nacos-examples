# nacos-autorefresh-springboot-examples

- <https://github.com/nacos-group/nacos-examples/tree/master/nacos-spring-boot-example/nacos-spring-boot-config-example>

|  bean      | auto-refresh   |
| --------   | -----  |
| RedisConnectionFactory| X |
| RedisProperties| X |
| Environment | V |
| NacosConfigurationProperties | V |


## NOTE
nacos-spring-context `v1.0.0` 中`com.alibaba.nacos.spring.context.annotation.NacosBeanDefinitionRegistrar`默认从data-id解析config-type。