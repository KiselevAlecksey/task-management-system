package ru.tms.config;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import ru.tms.config.multitenancy.TenantContext;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JpaProperties jpaProperties;
    @Autowired
    private TenantContext tenantContext;


    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        return new HibernateJpaVendorAdapter();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(JpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan("ru.tms"); // Замените на ваши пакеты
        entityManagerFactory.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactory.setPersistenceProviderClass(HibernatePersistenceProvider.class);

        Properties hibernateProperties = new Properties();
        hibernateProperties.putAll(jpaProperties.getProperties()); // Добавляем существующие свойства
        hibernateProperties.setProperty("hibernate.multiTenancy", "DATABASE");
        hibernateProperties.setProperty("hibernate.tenant_identifier_value", "#{tenantContext.getCurrentTenant}");

        entityManagerFactory.setJpaProperties(hibernateProperties);

        return entityManagerFactory;
    }
}