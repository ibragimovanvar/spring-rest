package com.epam.training.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.*;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Objects;

@Configuration
@ComponentScan(basePackages = {
        "com.epam.training.service",
        "com.epam.training.repository",
        "com.epam.training.controller",
        "com.epam.training.mapper",
        "com.epam.training.util"
})
@EnableTransactionManagement
public class RootConfig {

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setPersistenceUnitName("hibernate-unit");
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }

    @Bean
    @Profile("dev")
    public String logConfigDev() {
        System.setProperty("logging.config", "classpath:log4j2-dev.xml");
        return "dev";
    }

    @Bean
    @Profile("prod")
    public String logConfigProd() {
        System.setProperty("logging.config", "classpath:log4j2-prod.xml");
        return "prod";
    }
}