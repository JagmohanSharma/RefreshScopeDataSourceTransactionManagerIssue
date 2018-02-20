package com.example.demo.config;

import com.example.demo.util.DynamicBeanDefinitionRegistrar;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;
import java.util.Map;

public class JdbcBeanDefinitionRegistrar extends DynamicBeanDefinitionRegistrar {

    private static final String JDBC_TEMPLATE_NAME_SUFFIX = "JdbcTemplate";

    private static final String JDBC_TRANSACTION_MANAGER_NAME_SUFFIX = "TransactionManager";

    private final String dataSourceBeanNameSuffix;

    public JdbcBeanDefinitionRegistrar(String dataSourceBeanNameSuffix) {
        this.dataSourceBeanNameSuffix = dataSourceBeanNameSuffix;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        if (beanFactory instanceof BeanDefinitionRegistry) {
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            Map<String, DataSource> dataSources = beanFactory.getBeansOfType(DataSource.class);
            for (Map.Entry<String, DataSource> dsEntry : dataSources.entrySet()) {
                DataSource ds = dsEntry.getValue();
                if (ds instanceof ScopedObject) {
                    registerBean(registry, dsEntry.getKey(), JDBC_TEMPLATE_NAME_SUFFIX, ds,
                            JdbcTemplate.class);
                    registerBean(registry, dsEntry.getKey(), JDBC_TRANSACTION_MANAGER_NAME_SUFFIX, ds,
                            DataSourceTransactionManager.class);
                }
            }
        } else {
            throw new IllegalArgumentException(
                    "ConfigurableListableBeanFactory " + beanFactory + " is not of type BeanDefinitionRegistry!");
        }
    }

    private void registerBean(BeanDefinitionRegistry beanDefRegistry, String key, String replacedValue, DataSource dataSource, Class<?> beanTypeToBeInitialized) {
        String jdbcTemplateName = key.replace(dataSourceBeanNameSuffix, replacedValue);
        BeanDefinitionBuilder bdb = BeanDefinitionBuilder.genericBeanDefinition(beanTypeToBeInitialized);
        bdb.addConstructorArgValue(dataSource);
        if (isRefreshScopeEnabled()) {
            bdb.setScope("refresh");
        }
        beanDefRegistry.registerBeanDefinition(jdbcTemplateName, bdb.getBeanDefinition());
    }

}
