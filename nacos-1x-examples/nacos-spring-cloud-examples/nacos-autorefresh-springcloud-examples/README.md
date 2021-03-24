# nacos-autorefresh-springcloud-examples

默认情况下：
1) 依赖`spring-cloud-alibaba-nacos-config`  
2) 未使用`@RefreshScope`  

| bean      | auto-refresh   |
| --------   | -----  |
| RedisConnectionFactory| false |
| NacosCustomProperties| true |
| RedisProperties | true |
| Environment | true |

## source
spring-boot启动时`SpringApplication.run(class, args)`：  

```java
public class SpringApplication {
    	public ConfigurableApplicationContext run(String... args) {
    		StopWatch stopWatch = new StopWatch();
    		stopWatch.start();
    		ConfigurableApplicationContext context = null;
    		Collection<SpringBootExceptionReporter> exceptionReporters = new ArrayList<>();
    		configureHeadlessProperty();
    		SpringApplicationRunListeners listeners = getRunListeners(args);
    		listeners.starting();
    		try {
    			ApplicationArguments applicationArguments = new DefaultApplicationArguments(args);
    			ConfigurableEnvironment environment = prepareEnvironment(listeners, applicationArguments);
    			configureIgnoreBeanInfo(environment);
    			Banner printedBanner = printBanner(environment);
    			context = createApplicationContext();
    			exceptionReporters = getSpringFactoriesInstances(SpringBootExceptionReporter.class,
    					new Class[] { ConfigurableApplicationContext.class }, context);
    			prepareContext(context, environment, listeners, applicationArguments, printedBanner);
    			refreshContext(context);
    			afterRefresh(context, applicationArguments);
    			stopWatch.stop();
    			if (this.logStartupInfo) {
    				new StartupInfoLogger(this.mainApplicationClass).logStarted(getApplicationLog(), stopWatch);
    			}
    			listeners.started(context);
    			callRunners(context, applicationArguments);
    		}
    		catch (Throwable ex) {
    			handleRunFailure(context, ex, exceptionReporters, listeners);
    			throw new IllegalStateException(ex);
    		}
    
    		try {
    			listeners.running(context);  // SpringApplicationRunListeners.class
    		}
            catch (Throwable ex) {
                handleRunFailure(context, ex, exceptionReporters, null);
                throw new IllegalStateException(ex);
            }
    		return context;
    	}
}
```

```java
class SpringApplicationRunListeners {
    private final List<SpringApplicationRunListener> listeners;
    
	void running(ConfigurableApplicationContext context) {
		for (SpringApplicationRunListener listener : this.listeners) {
			listener.running(context);
		}
	}
}
```

```java
public class EventPublishingRunListener implements SpringApplicationRunListener, Ordered {
    @Override
    public void running(ConfigurableApplicationContext context) {
        context.publishEvent(new ApplicationReadyEvent(this.application, this.args, context));
    }
}
```

`spring-cloud-starter-alibaba-nacos-config`：  
```java
public class NacosContextRefresher
		implements ApplicationListener<ApplicationReadyEvent>, ApplicationContextAware {
    
	private AtomicBoolean ready = new AtomicBoolean(false);

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // many Spring context
        if (this.ready.compareAndSet(false, true)) {
            this.registerNacosListenersForApplications();
        }
    }

	private void registerNacosListener(final String groupKey, final String dataKey) {
		String key = NacosPropertySourceRepository.getMapKey(dataKey, groupKey);
		Listener listener = listenerMap.computeIfAbsent(key,
				lst -> new AbstractSharedListener() {
					@Override
					public void innerReceive(String dataId, String group,
							String configInfo) {
						refreshCountIncrement();
						nacosRefreshHistory.addRefreshRecord(dataId, group, configInfo);
						// todo feature: support single refresh for listening
						applicationContext.publishEvent(
								new RefreshEvent(this, null, "Refresh Nacos config"));
						if (log.isDebugEnabled()) {
							log.debug(String.format(
									"Refresh Nacos config group=%s,dataId=%s,configInfo=%s",
									group, dataId, configInfo));
						}
					}
				});
		try {
			configService.addListener(dataKey, groupKey, listener);
		}
		catch (NacosException e) {
			log.warn(String.format(
					"register fail for nacos listener ,dataId=[%s],group=[%s]", dataKey,
					groupKey), e);
		}
	}

}
```

所以，当nacos-config刷新时触发：  
-> RefreshEvent, `org.springframework.cloud.endpoint.event.RefreshEvent`  
-> spring-cloud-context, `org.springframework.cloud.endpoint.event.RefreshEventListener`  
-> org.springframework.cloud.context.refresh.ContextRefresher#refresh()
-> ContextRefresher#refreshEnvironment()  // refresh-environment & SpringApplication.run(...)
-> EnvironmentChangeEvent, `org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder`
-> ConfigurationPropertiesRebinder#rebind()

```java
@Component
@ManagedResource
public class ConfigurationPropertiesRebinder
		implements ApplicationContextAware, ApplicationListener<EnvironmentChangeEvent> {
	
    // ConfigurationProperties
	private ConfigurationPropertiesBeans beans;

	private ApplicationContext applicationContext;

	private Map<String, Exception> errors = new ConcurrentHashMap<>();

	public ConfigurationPropertiesRebinder(ConfigurationPropertiesBeans beans) {
		this.beans = beans;
	}

	@ManagedOperation
	public void rebind() {
		this.errors.clear();
		for (String name : this.beans.getBeanNames()) {
			rebind(name);
		}
	}

    /**
     * 主要是rebind `@ConfigurationProperties`注解的bean（先destroy再initialize），例如：
     *   - {@link org.springframework.boot.autoconfigure.data.redis.RedisProperties}
     *   - {@link com.vergilyn.examples.nacos.autorefresh.config.NacosCustomProperties}
     */
    @ManagedOperation
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
                    // 2020-11-09 并不会重新创建的一个新的bean，更多的是在exist-bean上重新赋值。（具体看initializeBean）
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
    }
}
```

整个流程下来，spring-cloud-context 会refresh `@ConfigurationProperties` 和 `environment`。  
但是，**并不会refresh RedisConnectionFactory**。

网上说的手动调用`/actuator/refresh`其实是一样的：
-> org.springframework.cloud.endpoint.RefreshEndpoint
-> org.springframework.cloud.context.refresh.ContextRefresher#refresh()

## 对于`ConfigurationProperties`，refresh时一般不会创建new-bean，而是对existing-bean重新执行init-bean
`ConfigurationPropertiesRebinder#rebind()`时重新执行BeanPostProcessor实在existing-bean上执行，一般不会返回new-bean。
```text
0 = {ApplicationContextAwareProcessor@8660} 
1 = {WebApplicationContextServletContextAwareProcessor@8681} 
2 = {ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor@8687} 
3 = {PostProcessorRegistrationDelegate$BeanPostProcessorChecker@8688} 
4 = {ConfigurationPropertiesBindingPostProcessor@8689} 
5 = {MethodValidationPostProcessor@8690} "proxyTargetClass=true; optimize=false; opaque=false; exposeProxy=false; frozen=false"
6 = {PersistenceExceptionTranslationPostProcessor@8691} "proxyTargetClass=true; optimize=false; opaque=false; exposeProxy=false; frozen=false"
7 = {WebServerFactoryCustomizerBeanPostProcessor@8692} 
8 = {ErrorPageRegistrarBeanPostProcessor@8693} 
9 = {ProjectingArgumentResolverRegistrar$ProjectingArgumentResolverBeanPostProcessor@8694} 
10 = {ConfigurationPropertiesBeans@6972} 
11 = {CommonAnnotationBeanPostProcessor@8695} 
12 = {AutowiredAnnotationBeanPostProcessor@8696} 
13 = {ApplicationListenerDetector@8697} 
```