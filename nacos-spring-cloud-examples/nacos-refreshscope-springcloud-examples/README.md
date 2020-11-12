# nacos-refreshscope-springcloud-examples


## @RefreshScope
1. 什么时候refresh FactoryBean？

1. bean-name 的前缀`scopedTarget.`
```text
  java.lang.Thread.State: RUNNABLE
      // 例如`configController` -> `beanName = "scopedTarget.configController"`
	  at org.springframework.aop.scope.ScopedProxyUtils.getTargetBeanName(ScopedProxyUtils.java:106)
    
      // registerBeanDefinition -> `org.springframework.aop.scope.ScopedProxyUtils#createScopedProxy(...)`
	  at org.springframework.aop.scope.ScopedProxyUtils.createScopedProxy(ScopedProxyUtils.java:61)

	  at org.springframework.context.annotation.ScopedProxyCreator.createScopedProxy(ScopedProxyCreator.java:40)
	  at org.springframework.context.annotation.AnnotationConfigUtils.applyScopedProxyMode(AnnotationConfigUtils.java:275)
    
      // resolve ScopeMetadata, 即`@RefreshScope` -> `scopeMetadata.scopeName = "refresh"`
	  at org.springframework.context.annotation.ClassPathBeanDefinitionScanner.doScan(ClassPathBeanDefinitionScanner.java:290)

	  at org.springframework.context.annotation.ComponentScanAnnotationParser.parse(ComponentScanAnnotationParser.java:132)
	  at org.springframework.context.annotation.ConfigurationClassParser.doProcessConfigurationClass(ConfigurationClassParser.java:290)
	  at org.springframework.context.annotation.ConfigurationClassParser.processConfigurationClass(ConfigurationClassParser.java:245)
	  at org.springframework.context.annotation.ConfigurationClassParser.parse(ConfigurationClassParser.java:202)
	  at org.springframework.context.annotation.ConfigurationClassParser.parse(ConfigurationClassParser.java:170)
	  at org.springframework.context.annotation.ConfigurationClassPostProcessor.processConfigBeanDefinitions(ConfigurationClassPostProcessor.java:325)
	  at org.springframework.context.annotation.ConfigurationClassPostProcessor.postProcessBeanDefinitionRegistry(ConfigurationClassPostProcessor.java:242)
	  at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors(PostProcessorRegistrationDelegate.java:275)
	  at org.springframework.context.support.PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(PostProcessorRegistrationDelegate.java:95)
	  at org.springframework.context.support.AbstractApplicationContext.invokeBeanFactoryPostProcessors(AbstractApplicationContext.java:706)

	  at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:532)

	  - locked <0x1514> (a java.lang.Object)
	  at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141)
	  at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:747)
	  at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:315)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)
	  at com.vergilyn.examples.nacos.autorefresh.SpringCloudRefreshScopeApplication.main(SpringCloudRefreshScopeApplication.java:46)
```

2. running, getBean
```text
  java.lang.Thread.State: RUNNABLE
      // 即 RefreshScope#get(String, ObjectFactory<?>)  // RefreshScope.cache.putIfAbsent()
	  at org.springframework.cloud.context.scope.GenericScope.get(GenericScope.java:182)
	  at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:356)
	  at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	  at org.springframework.context.support.AbstractApplicationContext.getBean(AbstractApplicationContext.java:1108)

      // if(scope == "refresh" && !lazyInit){ getBean(); } 
	  at org.springframework.cloud.context.scope.refresh.RefreshScope.eagerlyInitialize(RefreshScope.java:127)
	  at org.springframework.cloud.context.scope.refresh.RefreshScope.start(RefreshScope.java:118)
	  at org.springframework.cloud.context.scope.refresh.RefreshScope.onApplicationEvent(RefreshScope.java:112)
      
      // RefreshScope implements ApplicationListener<ContextRefreshedEvent>
	  at org.springframework.cloud.context.scope.refresh.RefreshScope.onApplicationEvent(RefreshScope.java:66)

	  at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172)
	  at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165)
	  at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:139)

      // ContextRefreshedEvent.class
	  at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:403)
	  at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:360)
	  at org.springframework.context.support.AbstractApplicationContext.finishRefresh(AbstractApplicationContext.java:897)

	  at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.finishRefresh(ServletWebServerApplicationContext.java:162)
	  at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:553)

	  - locked <0x209e> (a java.lang.Object)
	  at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141)
	  at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:747)
	  at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:315)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)
	  at com.vergilyn.examples.nacos.autorefresh.SpringCloudRefreshScopeApplication.main(SpringCloudRefreshScopeApplication.java:46)

```

源码疑问：
```
@Override
public Object get(String name, ObjectFactory<?> objectFactory) {
    // 虽然底层是 putIfAbsent，但还是每次都会new-object
    BeanLifecycleWrapper value = this.cache.put(name,
            new BeanLifecycleWrapper(name, objectFactory));  
    this.locks.putIfAbsent(name, new ReentrantReadWriteLock());
    try {
        return value.getBean();
    }
    catch (RuntimeException e) {
        this.errors.put(name, e);
        throw e;
    }
}
```

3. refresh, destroy
```text
"com.alibaba.nacos.client.Worker.longPolling.fixed-127.0.0.1_8848@8465" daemon prio=5 tid=0x35 nid=NA runnable
  java.lang.Thread.State: RUNNABLE
      // `RefreshScope.cache.clear()`
	  at org.springframework.cloud.context.scope.GenericScope.destroy(GenericScope.java:135)
	  at org.springframework.cloud.context.scope.refresh.RefreshScope.refreshAll(RefreshScope.java:154)

	  at org.springframework.cloud.context.refresh.ContextRefresher.refresh(ContextRefresher.java:86)
	  - locked <0x2505> (a org.springframework.cloud.context.refresh.ContextRefresher)
	  at org.springframework.cloud.endpoint.event.RefreshEventListener.handle(RefreshEventListener.java:72)
	  at org.springframework.cloud.endpoint.event.RefreshEventListener.onApplicationEvent(RefreshEventListener.java:61)
	  at org.springframework.context.event.SimpleApplicationEventMulticaster.doInvokeListener(SimpleApplicationEventMulticaster.java:172)
	  at org.springframework.context.event.SimpleApplicationEventMulticaster.invokeListener(SimpleApplicationEventMulticaster.java:165)
	  at org.springframework.context.event.SimpleApplicationEventMulticaster.multicastEvent(SimpleApplicationEventMulticaster.java:139)
	  at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:403)
	  at org.springframework.context.support.AbstractApplicationContext.publishEvent(AbstractApplicationContext.java:360)
    
      // org.springframework.cloud.endpoint.event.RefreshEvent
	  at com.alibaba.cloud.nacos.refresh.NacosContextRefresher$1.innerReceive(NacosContextRefresher.java:133)

	  at com.alibaba.nacos.api.config.listener.AbstractSharedListener.receiveConfigInfo(AbstractSharedListener.java:40)
	  at com.alibaba.nacos.client.config.impl.CacheData$1.run(CacheData.java:210)
	  at com.alibaba.nacos.client.config.impl.CacheData.safeNotifyListener(CacheData.java:241)
	  at com.alibaba.nacos.client.config.impl.CacheData.checkListenerMd5(CacheData.java:181)
	  at com.alibaba.nacos.client.config.impl.ClientWorker$LongPollingRunnable.run(ClientWorker.java:653)
	  at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)
	  at java.util.concurrent.FutureTask.run$$$capture(FutureTask.java:266)
	  at java.util.concurrent.FutureTask.run(FutureTask.java:-1)
	  at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.access$201(ScheduledThreadPoolExecutor.java:180)
	  at java.util.concurrent.ScheduledThreadPoolExecutor$ScheduledFutureTask.run(ScheduledThreadPoolExecutor.java:293)
	  at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
	  at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
	  at java.lang.Thread.run(Thread.java:748)

```

4. 
```
"main@1" prio=5 tid=0x1 nid=NA runnable
  java.lang.Thread.State: RUNNABLE
	  at org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration.redisConnectionFactory(LettuceConnectionConfiguration.java:68)
	  at sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-1)
	  at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	  at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	  at java.lang.reflect.Method.invoke(Method.java:498)
	  at org.springframework.beans.factory.support.SimpleInstantiationStrategy.instantiate(SimpleInstantiationStrategy.java:154)
	  at org.springframework.beans.factory.support.ConstructorResolver.instantiate(ConstructorResolver.java:651)
	  at org.springframework.beans.factory.support.ConstructorResolver.instantiateUsingFactoryMethod(ConstructorResolver.java:636)
	  at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.instantiateUsingFactoryMethod(AbstractAutowireCapableBeanFactory.java:1338)
	  at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBeanInstance(AbstractAutowireCapableBeanFactory.java:1177)
	  at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.doCreateBean(AbstractAutowireCapableBeanFactory.java:557)
	  at org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory.createBean(AbstractAutowireCapableBeanFactory.java:517)
	  at org.springframework.beans.factory.support.AbstractBeanFactory.lambda$doGetBean$0(AbstractBeanFactory.java:323)
	  at org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$179.865858182.getObject(Unknown Source:-1)
	  at org.springframework.beans.factory.support.DefaultSingletonBeanRegistry.getSingleton(DefaultSingletonBeanRegistry.java:222)
	  - locked <0x17e2> (a java.util.concurrent.ConcurrentHashMap)
	  at org.springframework.beans.factory.support.AbstractBeanFactory.doGetBean(AbstractBeanFactory.java:321)
	  at org.springframework.beans.factory.support.AbstractBeanFactory.getBean(AbstractBeanFactory.java:202)
	  at org.springframework.beans.factory.support.DefaultListableBeanFactory.preInstantiateSingletons(DefaultListableBeanFactory.java:879)
	  at org.springframework.context.support.AbstractApplicationContext.finishBeanFactoryInitialization(AbstractApplicationContext.java:878)
	  at org.springframework.context.support.AbstractApplicationContext.refresh(AbstractApplicationContext.java:550)
	  - locked <0x1fc4> (a java.lang.Object)
	  at org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext.refresh(ServletWebServerApplicationContext.java:141)
	  at org.springframework.boot.SpringApplication.refresh(SpringApplication.java:747)
	  at org.springframework.boot.SpringApplication.refreshContext(SpringApplication.java:397)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:315)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:1226)
	  at org.springframework.boot.SpringApplication.run(SpringApplication.java:1215)
	  at com.vergilyn.examples.nacos.autorefresh.SpringCloudRefreshScopeApplication.main(SpringCloudRefreshScopeApplication.java:52)

```

### 3.1
类似rebind-properties `destroyBean -> initializeBean`：
```java
package org.springframework.cloud.context.properties;

public class ConfigurationPropertiesRebinder
		implements ApplicationContextAware, ApplicationListener<EnvironmentChangeEvent> {
	
    private ConfigurationPropertiesBeans beans;

    @ManagedOperation
    public void rebind() {
        this.errors.clear();
        for (String name : this.beans.getBeanNames()) {
            rebind(name);
        }
    }

public boolean rebind(String name) {
		if (!this.beans.getBeanNames().contains(name)) {
			return false;
		}
		if (this.applicationContext != null) {
			try {
				Object bean = this.applicationContext.getBean(name);
				if (AopUtils.isAopProxy(bean)) {
					bean = ProxyUtils.getTargetObject(bean);
				}
				if (bean != null) {
					// TODO: determine a more general approach to fix this.
					// see https://github.com/spring-cloud/spring-cloud-commons/issues/571
					if (getNeverRefreshable().contains(bean.getClass().getName())) {
						return false; // ignore
					}
					this.applicationContext.getAutowireCapableBeanFactory()
							.destroyBean(bean);
					this.applicationContext.getAutowireCapableBeanFactory()
							.initializeBean(bean, name);
					return true;
				}
			}
			catch (RuntimeException e) {
				this.errors.put(name, e);
				throw e;
			}
			catch (Exception e) {
				this.errors.put(name, e);
				throw new IllegalStateException("Cannot rebind to " + name, e);
			}
		}
		return false;
	}
}
```