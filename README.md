# nacos-examples

- nacos: <https://github.com/alibaba/nacos>
- nacos-group: <https://github.com/>
- nacos-spring-boot-project: <https://github.com/nacos-group/nacos-spring-boot-project>
- nacos-examples: <https://github.com/nacos-group/nacos-examples>

|  dependencies      | version   |  link  |
| --------   | -----  | ----  |
| nacos     | 1.3.2 |   <https://github.com/alibaba/nacos>     |
| nacos-spring-boot-project     | 0.2.7 |   <https://github.com/nacos-group/nacos-spring-boot-project>     |
| nacos-spring-boot-project-start        |   0.2.7   |   <https://github.com/nacos-group/nacos-spring-boot-project>   |
| nacos-spring-context        |    1.0.0    |  <https://github.com/nacos-group/nacos-spring-project>  |
| alibaba-spring-context-support        |    1.0.11    |  <https://github.com/alibaba/spring-context-support>  |
| spring-cloud-context | 2.2.5.RELEASE | <https://github.com/spring-cloud/spring-cloud-commons> |
| nacos-spring-cloud | 2.2.3.RELEASE | <https://github.com/alibaba/spring-cloud-alibaba> |

## 
### auto-refresh

> [Nacos配置管理及动态刷新](https://blog.csdn.net/yuanyuan_gugu/article/details/108078478)  
> 配置的动态刷新，仅需要使用`@RefreshScope`(spring-cloud-context)注解即可。  
> 
> `@RefreshScope`注解底层是使用 cglib动态代理 来实现，而cglib是创建动态子类继承来完成功能的增强，
> 在使用`@RefreshScope`注解刷新包含final属性/final方法的bean时，会导致返回的代理对象为null的情况。
> 典型的例子比如Elasticsearch的RestHighLevelClient。此时需要将需要刷新的Bean封装一层，避免final属性/final方法的问题。

**疑问：**
需要在动态刷新的bean上添加注解`@RefreshScope`，针对spring-boot-starter封装的AutoConfiguration如何处理，例如 RedisAutoConfiguration？