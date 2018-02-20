package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.jdbc.datasource.lookup.DataSourceLookupFailureException;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;

import javax.sql.DataSource;

public class DataSourceBuilder implements FactoryBean<DataSource> {

	private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceBuilder.class);

	private DataSourceProperties dataSourceProperties;

	public void setDataSourceProperties(DataSourceProperties dataSourceProperties) {
		this.dataSourceProperties = dataSourceProperties;
	}

	@Override
	public DataSource getObject() throws Exception {
		DataSource dataSource = null;
		String jndiName = dataSourceProperties.getJndiName();
		if (jndiName != null) {
			JndiDataSourceLookup dataSourceLookup = new JndiDataSourceLookup();
			try {
				dataSource = dataSourceLookup.getDataSource(jndiName);
				LOGGER.info("Found Jndi DataSource \"{}\"", jndiName);
			} catch (DataSourceLookupFailureException e) {
				LOGGER.warn("Could not lookup Jndi datasource \"{}\". Trying with manual configuration.", jndiName);
			}
		}
		String dataSourceName = dataSourceProperties.getName();
		if (dataSource == null) {
			if (dataSourceProperties.getDriverClassName() == null || dataSourceProperties.getUrl() == null
					|| dataSourceProperties.getUsername() == null || dataSourceProperties.getPassword() == null) {
				throw new IllegalArgumentException(String.format(
						"Cannot setup %s datasource. No jndi object named %s found. Tried initializing from jdbc properties. "
								+ "All of the properties driverClassName, url, userName, password should be defined.",
						dataSourceName, jndiName));
			}
			dataSource = org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder.create().driverClassName(dataSourceProperties.getDriverClassName())
					.url(dataSourceProperties.getUrl()).username(dataSourceProperties.getUsername())
					.password(dataSourceProperties.getPassword()).build();
		}
		return dataSource;
	}

	@Override
	public Class<DataSource> getObjectType() {
		return DataSource.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
