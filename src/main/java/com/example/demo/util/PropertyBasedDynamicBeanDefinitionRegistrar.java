package com.example.demo.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class PropertyBasedDynamicBeanDefinitionRegistrar extends DynamicBeanDefinitionRegistrar {

	private static Logger logger = LoggerFactory.getLogger(PropertyBasedDynamicBeanDefinitionRegistrar.class);
	private final Class<?> propertyConfigurationClass;

	private final String propertyBeanNamePrefix;

	private String propertyBeanNameSuffix;

	private final String propertyKeysPropertyName;

	private Class<?> propertyConsumerBean;

	private String consumerBeanPropertyFieldName;

	private String consumerBeanNamePrefix;

	private String consumerBeanNameSuffix;

	public PropertyBasedDynamicBeanDefinitionRegistrar(Class<?> propertyConfigurationClass,
			String propertyBeanNamePrefix, String propertyKeysPropertyName) {
		this.propertyConfigurationClass = propertyConfigurationClass;
		this.propertyBeanNamePrefix = propertyBeanNamePrefix;
		this.propertyKeysPropertyName = propertyKeysPropertyName;
	}

	public void setPropertyConsumerBean(Class<?> propertyConsumerBean, String consumerBeanNamePrefix) {
		this.setPropertyConsumerBean(propertyConsumerBean, consumerBeanNamePrefix, null);
	}

	public void setPropertyConsumerBean(Class<?> propertyConsumerBean, String consumerBeanNamePrefix, String consumerBeanPropertyFieldName) {
		this.propertyConsumerBean = propertyConsumerBean;
		this.consumerBeanNamePrefix = consumerBeanNamePrefix;
		this.consumerBeanPropertyFieldName = consumerBeanPropertyFieldName;
	}

	public void setPropertyBeanNameSuffix(String propertyBeanNameSuffix) {
		this.propertyBeanNameSuffix = propertyBeanNameSuffix;
	}

	public void setConsumerBeanNameSuffix(String consumerBeanNameSuffix) {
		this.consumerBeanNameSuffix = consumerBeanNameSuffix;
	}

    @Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		if (beanFactory instanceof BeanDefinitionRegistry) {
			BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
			String[] keys = getPropertyKeys(propertyKeysPropertyName);
			Map<String, String> propertyKeyBeanNameMapping = new HashMap<>();
			for (String k : keys) {
				String trimmedKey = k.trim();
				if (StringUtils.isEmpty(trimmedKey)) {
					continue;
				}
				String propBeanName = getBeanName(propertyBeanNamePrefix, trimmedKey, propertyBeanNameSuffix);
				registerPropertyBean(registry, trimmedKey, propBeanName);
				propertyKeyBeanNameMapping.put(trimmedKey, propBeanName);
			}
			if (propertyConsumerBean != null) {
				String beanPropertyFieldName = getConsumerBeanPropertyVariable();
				for (Map.Entry<String, String> prop : propertyKeyBeanNameMapping.entrySet()) {
					registerConsumerBean(registry, prop.getKey(), prop.getValue(), beanPropertyFieldName);
				}
			}
		} else {
			throw new IllegalArgumentException(
					"ConfigurableListableBeanFactory "+ beanFactory + " is not of type BeanDefinitionRegistry!");
		}
    }

	private void registerConsumerBean(BeanDefinitionRegistry beanDefRegistry, String trimmedKey, String propBeanName, String beanPropertyFieldName) {
		String consumerBeanName = getBeanName(consumerBeanNamePrefix, trimmedKey, consumerBeanNameSuffix);
		AbstractBeanDefinition consumerDefinition = preparePropertyConsumerBeanDefinition(propBeanName, beanPropertyFieldName);
		createScopeProxyAndRegisterBean(beanDefRegistry, consumerDefinition, consumerBeanName);
	}

	private void registerPropertyBean(BeanDefinitionRegistry beanDefRegistry, String trimmedKey, String propBeanName) {
		AbstractBeanDefinition propertyBeanDefinition = preparePropertyBeanDefinition(trimmedKey);
		createScopeProxyAndRegisterBean(beanDefRegistry, propertyBeanDefinition, propBeanName);
	}

	private String getConsumerBeanPropertyVariable() throws IllegalArgumentException {
		if (consumerBeanPropertyFieldName != null) {
			return consumerBeanPropertyFieldName;
		}
		Field consumerBeanField = ReflectionUtils.findField(propertyConsumerBean, null, propertyConfigurationClass);
		if (consumerBeanField == null) {
			throw new BeanCreationException(String.format("Could not find property of type %s in bean class %s",
					propertyConfigurationClass.getName(), propertyConsumerBean.getName()));
		}
		return consumerBeanField.getName();
	}

    private AbstractBeanDefinition preparePropertyBeanDefinition(String trimmedKey) {
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(PropertiesConfigurationFactory.class);
		bdb.addConstructorArgValue(propertyConfigurationClass);
		bdb.addPropertyValue("propertySources", getEnvironment().getPropertySources());
		bdb.addPropertyValue("conversionService", getEnvironment().getConversionService());
		bdb.addPropertyValue("targetName", trimmedKey);

		if(isRefreshScopeEnabled()) {
			bdb.setScope("refresh");
		}
		return bdb.getBeanDefinition();
    }

	private AbstractBeanDefinition preparePropertyConsumerBeanDefinition(String propBeanName, String beanPropertyFieldName) {
		BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(propertyConsumerBean);
		bdb.addPropertyReference(beanPropertyFieldName, propBeanName);
		if(isRefreshScopeEnabled()) {
			bdb.setScope("refresh");
		} else {
			logger.error("Refresh scope is not yet registered while preparing bean definition for {} ", propBeanName);
		}
		return bdb.getBeanDefinition();
	}

}
