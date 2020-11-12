package com.vergilyn.examples.nacos.autorefresh.listener;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.cloud.context.properties.ConfigurationPropertiesRebinder;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;

public class RefreshScopeRefreshedEventListener implements ApplicationListener<RefreshScopeRefreshedEvent>,
		ApplicationContextAware, BeanDefinitionRegistryPostProcessor {

	private ApplicationContext context;
	private BeanDefinitionRegistry registry;
	private ConfigurableListableBeanFactory beanFactory;

	/**
	 * @see ConfigurationPropertiesRebinder#rebind(String) 
	 */
	@Override
	public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
		NamedBeanHolder<RedisConnectionFactory> beanHolder = context
				.getAutowireCapableBeanFactory().resolveNamedBean(RedisConnectionFactory.class);

		context.getAutowireCapableBeanFactory().destroyBean(beanHolder.getBeanInstance());
		context.getAutowireCapableBeanFactory().initializeBean(beanHolder.getBeanInstance(), beanHolder.getBeanName());
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		this.registry = registry;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}
}
