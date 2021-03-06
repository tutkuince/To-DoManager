package com.muditasoft.config;

import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:/com/muditasoft/properties/persistence-mysql.properties")
public class HibernateConfig implements TransactionManagementConfigurer {

	@Autowired
	private Environment environment;

	// Set up a Logger for diagnostics
	private Logger logger = Logger.getLogger(getClass().getName());

	@Bean(destroyMethod = "close")
	public ComboPooledDataSource comboPooledDataSource() {
		// Create Connection Pool
		ComboPooledDataSource dataSource = new ComboPooledDataSource();

		// Get JDBC Properties
		String url = environment.getProperty("jdbc.url");
		String user = environment.getProperty("jdbc.user");
		String password = environment.getProperty("jdbc.password");
		String driver = environment.getProperty("jdbc.driver");

		// Get Connection Pool Properties
		int initPoolSize = getIntProperty("connection.pool.initialPoolSize");
		int minPoolSize = getIntProperty("connection.pool.minPoolSize");
		int maxPoolSize = getIntProperty("connection.pool.maxPoolSize");
		int maxIdleTime = getIntProperty("connection.pool.maxIdleTime");

		// Set JDBC properties to ComboPooledDataSource
		dataSource.setJdbcUrl(url);
		dataSource.setUser(user);
		dataSource.setPassword(password);
		try {
			dataSource.setDriverClass(driver);
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Set Connection Pool Properties to ComboPooledDataSource
		dataSource.setInitialPoolSize(initPoolSize);
		dataSource.setMinPoolSize(minPoolSize);
		dataSource.setMaxPoolSize(maxPoolSize);
		dataSource.setMaxIdleTime(maxIdleTime);

		// Log to Connection Properties
		logger.info("===>>> jdbc.url = " + url);
		logger.info("===>>> jdbc.user = " + user);

		return dataSource;
	}

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean bean = new LocalSessionFactoryBean();
		bean.setDataSource(comboPooledDataSource());
		bean.setPackagesToScan("com.muditasoft.model");

		Properties properties = new Properties();
		properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
		properties.setProperty("hibernate.show_sql", "true");
		properties.setProperty("hibernate.hbm2ddl.auto", "update");

		bean.setHibernateProperties(properties);

		return bean;
	}

	@Bean
	public HibernateTransactionManager hibernateTransactionManager() {
		HibernateTransactionManager transactionManager = new HibernateTransactionManager();
		transactionManager.setSessionFactory(sessionFactory().getObject());

		return transactionManager;
	}

	public int getIntProperty(String name) {
		// get property value
		String value = environment.getProperty(name);

		// convert string value to int value
		int intValue = Integer.parseInt(value);

		return intValue;

	}

	@Override
	public PlatformTransactionManager annotationDrivenTransactionManager() {
		return hibernateTransactionManager();
	}
}
