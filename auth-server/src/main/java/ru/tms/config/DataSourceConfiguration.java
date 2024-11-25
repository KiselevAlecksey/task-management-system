package ru.tms.config;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.init.DataSourceScriptDatabaseInitializer;
import org.springframework.boot.sql.init.DatabaseInitializationMode;
import org.springframework.boot.sql.init.DatabaseInitializationSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import ru.tms.config.multitenancy.MultitenantConfiguration;
import ru.tms.config.multitenancy.MultitenantDataSource;
import ru.tms.config.multitenancy.TenantContext;

import javax.sql.DataSource;
import java.util.List;

@Configuration(proxyBeanMethods = false)
public class DataSourceConfiguration {

    /*
    private final MultitenantConfiguration dataSourceProperties;

    public DataSourceConfiguration(MultitenantConfiguration dataSourceProperties) {
        this.dataSourceProperties = dataSourceProperties;
    }

    @Bean
    public DataSource getDataSource(DataSourceProperties authDataSourceProperties) {
        MultitenantDataSource tenantAwareRoutingDataSource = new MultitenantDataSource();
        tenantAwareRoutingDataSource.setTargetDataSources(authDataSourceProperties.getDatasources());
        tenantAwareRoutingDataSource.afterPropertiesSet();
        return tenantAwareRoutingDataSource;
    }*/

    @Bean
    @Primary
    @ConfigurationProperties("tenants.datasource.auth")
    public DataSourceProperties authDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource authDataSource(DataSourceProperties authDataSourceProperties) {
        return authDataSourceProperties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();
    }

    @Bean
    DataSourceScriptDatabaseInitializer authDataSourceScriptDatabaseInitializer(DataSource dataSource) {
        var settings = new DatabaseInitializationSettings();
        settings.setSchemaLocations(List.of("classpath:first_schema.sql"));
        settings.setMode(DatabaseInitializationMode.EMBEDDED);
        return new DataSourceScriptDatabaseInitializer(dataSource, settings);
    }
}
