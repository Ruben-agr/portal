package com.agrizar.portal.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({ "classpath:hispatec-db.properties" })
@EnableJpaRepositories(
    basePackages = "com.agrizar.portal.repositories.hispatec", 
    entityManagerFactoryRef = "hispatecEntityManager", 
    transactionManagerRef = "hispatecTransactionManager"
)
public class PersistenceJPAConfigHispatec {

	@Autowired
	private Environment env;
	
	@Bean
	LocalContainerEntityManagerFactoryBean hispatecEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource( hispatecDataSource() );
        em.setPackagesToScan( new String[] { "com.agrizar.portal.models.entities.hispatec" });

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    DataSource hispatecDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("hispatec.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("hispatec.jdbc.url"));
        dataSource.setUsername(env.getProperty("hispatec.jdbc.username"));
        dataSource.setPassword(env.getProperty("hispatec.jdbc.password"));
        return dataSource;
    }
	
	@Bean
	PlatformTransactionManager hispatecTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(hispatecEntityManager().getObject());
        return transactionManager;
    }
	
	private Properties additionalProperties() {
	    Properties hibernateProperties = new Properties();
	    hibernateProperties.setProperty("hibernate.default_schema", this.env.getProperty("hispatec.hibernate.default_schema"));
	    hibernateProperties.setProperty("hibernate.show_sql", this.env.getProperty("hispatec.jpa.show-sql"));
	    hibernateProperties.setProperty("hibernate.ddl_auto", this.env.getProperty("hispatec.jpa.hibernate.ddl-auto"));
	    hibernateProperties.setProperty("hibernate.format_sql", this.env.getProperty("hispatec.jpa.properties.hibernate.format_sql"));
	    return hibernateProperties;
	  }

}
