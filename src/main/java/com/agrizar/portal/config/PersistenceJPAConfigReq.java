package com.agrizar.portal.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@PropertySource({ "classpath:req-db.properties" })
@EnableJpaRepositories(
    basePackages = "com.agrizar.portal.repositories.req", 
    entityManagerFactoryRef = "reqEntityManager", 
    transactionManagerRef = "reqTransactionManager"
)
public class PersistenceJPAConfigReq {

	@Autowired
	private Environment env;
	
	@Bean
	@Primary
    LocalContainerEntityManagerFactoryBean reqEntityManager() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource( reqDataSource() );
        em.setPackagesToScan( new String[] { "com.agrizar.portal.models.entities.req" });

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        em.setJpaProperties(additionalProperties());
        return em;
    }

    @Bean
    @Primary
    DataSource reqDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("req.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("req.jdbc.url"));
        dataSource.setUsername(env.getProperty("req.jdbc.username"));
        dataSource.setPassword(env.getProperty("req.jdbc.password"));
        return dataSource;
    }
	
	@Bean
	@Primary
    PlatformTransactionManager reqTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(reqEntityManager().getObject());
        return transactionManager;
    }
	
	private Properties additionalProperties() {
	    Properties hibernateProperties = new Properties();
	    hibernateProperties.setProperty("hibernate.default_schema", this.env.getProperty("req.hibernate.default_schema"));
	    hibernateProperties.setProperty("hibernate.show_sql", this.env.getProperty("req.jpa.show-sql"));
	    hibernateProperties.setProperty("hibernate.ddl_auto", this.env.getProperty("req.jpa.hibernate.ddl-auto"));
	    hibernateProperties.setProperty("hibernate.format_sql", this.env.getProperty("req.jpa.properties.hibernate.format_sql"));
	    return hibernateProperties;
	  }

}
