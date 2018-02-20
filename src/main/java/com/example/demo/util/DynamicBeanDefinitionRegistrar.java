package com.example.demo.util;

import org.apache.commons.lang.StringUtils;
import org.springframework.aop.scope.ScopedProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

public abstract class DynamicBeanDefinitionRegistrar implements BeanFactoryPostProcessor, BeanFactoryAware, EnvironmentAware {

    private ConfigurableBeanFactory beanFactory;
    private ConfigurableEnvironment environment;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = (ConfigurableBeanFactory) beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    protected void createScopeProxyAndRegisterBean(BeanDefinitionRegistry beanDefRegistry, AbstractBeanDefinition beanDefinition, String beanName) {
        BeanDefinitionHolder property = new BeanDefinitionHolder(beanDefinition,
                beanName);
        BeanDefinitionHolder holder = ScopedProxyUtils.createScopedProxy(property,
                beanDefRegistry, true);
        beanDefRegistry.registerBeanDefinition(holder.getBeanName(),
                holder.getBeanDefinition());
    }

    protected boolean isRefreshScopeEnabled() {
        return beanFactory.getRegisteredScope("refresh") != null;
    }

    protected String[] getPropertyKeys(String propertyKeysPropertyName) {
        String keysProp = environment.getProperty(propertyKeysPropertyName);
        if (StringUtils.isEmpty(keysProp)) {
            throw new BeanCreationException(String.format("Property % not found or is empty", propertyKeysPropertyName));
        }
        return keysProp.split(",");
    }

    protected String getBeanName(String prefix, String key, String suffix) {
        StringBuilder beanNameBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(prefix)) {
            beanNameBuilder.append(prefix);
            beanNameBuilder.append(key.substring(0, 1).toUpperCase());
            beanNameBuilder.append(key.substring(1));
        } else {
            beanNameBuilder.append(key);
        }
        if (StringUtils.isNotEmpty(suffix)) {
            beanNameBuilder.append(suffix);
        }
        return beanNameBuilder.toString();
    }

    public ConfigurableEnvironment getEnvironment() {
        return environment;
    }

    public ConfigurableBeanFactory getBeanFactory() {
        return beanFactory;
    }
}
