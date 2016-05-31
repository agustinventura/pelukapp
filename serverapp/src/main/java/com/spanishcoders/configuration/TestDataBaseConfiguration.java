package com.spanishcoders.configuration;

import com.spanishcoders.model.Hairdresser;
import com.spanishcoders.model.Service;
import com.spanishcoders.model.UserStatus;
import com.spanishcoders.repositories.ServiceRepository;
import com.spanishcoders.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile("test")
public class TestDataBaseConfiguration {

    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder.setType(EmbeddedDatabaseType.H2).build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setDatabase(Database.H2);
        vendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(true);
        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setJpaProperties(getHibernateProperties());
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan("com.spanishcoders");
        factory.setDataSource(dataSource());
        return factory;
    }

    @Bean
    public Properties getHibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.format_sql", "true");
        hibernateProperties
                .setProperty("hibernate.hbm2ddl.auto", "create-drop");
        return hibernateProperties;
    }

    @Bean
    PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean
    public CommandLineRunner insertDemoData(ServiceRepository serviceRepository, UserRepository userRepository) {
        return (args) -> {
            Service service1 = new Service("Corte", 30);
            Service service2 = new Service("Afeitado", 30);
            serviceRepository.save(service1);
            serviceRepository.save(service2);
            Hairdresser admin = new Hairdresser();
            admin.setName("admin");
            admin.setUsername("admin");
            admin.setPassword("admin");
            admin.setPhone("+34666666666");
            admin.setStatus(UserStatus.ACTIVE);
            userRepository.save(admin);
        };
    }
}
