# spring-cloud-caused-run-twice

例如，如果依赖`spring-cloud-context`，当运行`SpringApplication.run(Xxx.class, args)`时会导致`SpringApplication.run`执行2次。

即会产生2个`SpringApplication`实例对象：  
第一次，由`SpringApplication.run(Xxx.class, args)`创建。  
第二次，当`第一次`执行到`SpringApplication#prepareEnvironment(...)`会调用`listeners.environmentPrepared(environment);`。  

```text
## 最终调用 SimpleApplicationEventMulticaster.multicastEvent(...)
0: class org.springframework.cloud.bootstrap.BootstrapApplicationListener
1: class org.springframework.cloud.bootstrap.LoggingSystemShutdownListener
2: class org.springframework.boot.context.config.ConfigFileApplicationListener
3: class org.springframework.boot.context.config.AnsiOutputApplicationListener
4: class org.springframework.boot.context.logging.LoggingApplicationListener
5: class org.springframework.boot.context.logging.ClasspathLoggingApplicationListener
6: class org.springframework.boot.autoconfigure.BackgroundPreinitializer
7: class org.springframework.boot.context.config.DelegatingApplicationListener
8: class org.springframework.boot.context.FileEncodingApplicationListener
```

所以到`cloud.BootstrapApplicationListener#onApplicationEvent(...)`
（`spring.cloud.bootstrap.enabled` 默认 true）

event中包含了`第一次`创建的`SpringApplication`实例，并从中获取`event.getSpringApplication().getInitializers()`，  
并且接着创建context `BootstrapApplicationListener#bootstrapServiceContext(...)`。  
其中会构建和run SpringApplication `org.springframework.boot.builder.SpringApplicationBuilder#run()`，这会导致`第2次`create-bean和run SpringApplication。

**为什么不会造成 endless-loop？**  
```
// 第一次执行 `BootstrapApplicationListener#onApplicationEvent(...)` 时写入env
// don't listen to events in a bootstrap context
if (environment.getPropertySources().contains(BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
    return;
}

// ...

// It only has properties in it now that we don't want in the parent so remove
// it (and it will be added back later)
bootstrapProperties.remove(BOOTSTRAP_PROPERTY_SOURCE_NAME);
```

并且，2次`SpringApplication#run()`都会走完全部的run流程。

**是否会触发多次listener？**
理论上1次push-event对应的`ApplicationListener#onApplicationEvent()`可以被执行2+次，因为存在多个context。  

例如spring-boot中
```text
# org.springframework.context.support.AbstractApplicationContext#publishEvent(Object, ResolvableType)

# 第1次由 当前context 的 applicationEventMulticaster PUSH
Delay-1: 
	0, com.vergilyn.examples.nacos.runtwice.listener.CustomDelayEventListener.onApplicationEvent(CustomDelayEventListener.java:17)
	1, com.vergilyn.examples.nacos.runtwice.listener.CustomDelayEventListener.onApplicationEvent(CustomDelayEventListener.java:9)
	2, org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172)
	3, org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165)
	4, org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:139)
	5, org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:403)
	6, org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:360)
	7, com.vergilyn.examples.nacos.runtwice.SpringCloudRunTwiceApplication.main(SpringCloudRunTwiceApplication.java:20)

# 第2次由 当前context的parent PUSH （可以n次）
Delay-2: 
	0, com.vergilyn.examples.nacos.runtwice.listener.CustomDelayEventListener.onApplicationEvent(CustomDelayEventListener.java:17)
	1, com.vergilyn.examples.nacos.runtwice.listener.CustomDelayEventListener.onApplicationEvent(CustomDelayEventListener.java:9)
	2, org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172)
	3, org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165)
	4, org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:139)
	5, org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:403)
	6, org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:409)
	7, org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:360)
	8, com.vergilyn.examples.nacos.runtwice.SpringCloudRunTwiceApplication.main(SpringCloudRunTwiceApplication.java:20)
```

解决思路：
方法一：onApplicationEvent方法添加执行条件`event.getApplicationContext().parent == null`，前提是event中有context信息。  
方法二：例如 spring-cloud `RefreshEventListener`，前提当前event只会执行1次，通过标记。  
方法三：不要使用`spring.factories`注入ApplicationListener，一般项目中都建议使用 `@Bean`。
（相当于该listener只会注册到child-context，而不会注册到parent-context。所以只会由child-context触发一次）

参考：  
- [spring事件机制-onApplicationEvent执行两次](https://blog.csdn.net/mahl1990/article/details/79298569)

