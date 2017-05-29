package com.spanishcoders.configuration.development;

import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.Stretch;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.model.*;
import com.spanishcoders.user.UserRepository;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.work.WorkRepository;

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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Properties;

@Configuration
@Profile(value = {"development", "integration"})
public class DevelopmentDataBaseConfiguration {

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
        vendorAdapter.setShowSql(false);
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
        hibernateProperties.setProperty("hibernate.show_sql", "false");
        hibernateProperties.setProperty("hibernate.format_sql", "false");
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
    public CommandLineRunner insertDemoData(WorkRepository workRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            createWorks(workRepository);
            createHairdresser(userRepository, passwordEncoder);
            createClient(userRepository, passwordEncoder);
        };
    }

    private void createClient(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        Client client = new Client();
        client.setName("client");
        client.setUsername("client");
        client.setPassword(passwordEncoder.encode("client"));
        client.setPhone("+34666666666");
        client.setStatus(UserStatus.ACTIVE);
        userRepository.save(client);
    }

    private void createHairdresser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        Hairdresser admin = new Hairdresser();
        admin.setName("admin");
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin"));
        admin.setPhone("+34666666666");
        admin.setStatus(UserStatus.ACTIVE);
        createAgenda(admin);
        userRepository.save(admin);
    }

    private void createAgenda(Hairdresser admin) {
        Agenda agenda = new Agenda(admin);
        Timetable timetable = new Timetable(agenda, LocalDate.now().minusDays(365), LocalDate.now().plusDays(365));
        Stretch morning = new Stretch(timetable, LocalTime.of(10, 00), LocalTime.of(14, 00));
        Stretch afternoon = new Stretch(timetable, LocalTime.of(17, 00), LocalTime.of(21, 00));
        agenda.addNonWorkingDay(LocalDate.of(2016, 01, 01));
        agenda.addNonWorkingDay(LocalDate.of(2016, 01, 06));
        agenda.addNonWorkingDay(LocalDate.of(2016, 02, 28));
        agenda.addNonWorkingDay(LocalDate.of(2016, 8, 15));
    }

    private void createWorks(WorkRepository workRepository) {
        Work cut = new Work("Corte", 30, WorkKind.PUBLIC);
        Work shave = new Work("Afeitado", 30, WorkKind.PUBLIC);
        Work regulation = new Work("Regulacion", 30, WorkKind.PRIVATE);
        workRepository.save(cut);
        workRepository.save(shave);
        workRepository.save(regulation);
    }
}
