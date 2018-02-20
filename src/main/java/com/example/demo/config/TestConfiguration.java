package com.example.demo.config;

import com.example.demo.util.PropertyBasedDynamicBeanDefinitionRegistrar;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(TestConfiguration.TEST_DATASOURCES_PROP_KEY)
public class TestConfiguration {

    public static final String TEST_DATASOURCES_PROP_KEY = "keys.datasources";

    private static final String DATA_SOURCE_BEAN_SUFFIX = "DataSource";

    private static final String TEST_DATA_SOURCE_PROPERTY_BEAN_SUFFIX = "DataSourceConf";

    @Bean
    public static PropertyBasedDynamicBeanDefinitionRegistrar dataSourceRegistrar() {
        PropertyBasedDynamicBeanDefinitionRegistrar registrar = new PropertyBasedDynamicBeanDefinitionRegistrar(DataSourceProperties.class, null, TEST_DATASOURCES_PROP_KEY);
        registrar.setPropertyConsumerBean(DataSourceBuilder.class, null);
        registrar.setConsumerBeanNameSuffix(DATA_SOURCE_BEAN_SUFFIX);
        registrar.setPropertyBeanNameSuffix(TEST_DATA_SOURCE_PROPERTY_BEAN_SUFFIX);
        return registrar;
    }

    @Bean
    public JdbcBeanDefinitionRegistrar jdbcTemplateRegistrar() {
        return new JdbcBeanDefinitionRegistrar(DATA_SOURCE_BEAN_SUFFIX);
    }

}
