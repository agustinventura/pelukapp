package com.spanishcoders.configuration.development;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

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

import com.spanishcoders.agenda.Agenda;
import com.spanishcoders.agenda.Stretch;
import com.spanishcoders.agenda.Timetable;
import com.spanishcoders.user.UserRepository;
import com.spanishcoders.user.UserStatus;
import com.spanishcoders.user.client.Client;
import com.spanishcoders.user.hairdresser.Hairdresser;
import com.spanishcoders.work.Work;
import com.spanishcoders.work.WorkKind;
import com.spanishcoders.work.WorkRepository;
import com.spanishcoders.work.WorkStatus;

@Configuration
@Profile(value = { "development", "integration" })
public class DevelopmentDataBaseConfiguration {

	@Bean
	public DataSource dataSource() {
		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		return builder.setType(EmbeddedDatabaseType.H2).build();
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		final HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.H2);
		vendorAdapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect");
		vendorAdapter.setGenerateDdl(true);
		vendorAdapter.setShowSql(false);
		final LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaProperties(getHibernateProperties());
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("com.spanishcoders");
		factory.setDataSource(dataSource());
		return factory;
	}

	@Bean
	public Properties getHibernateProperties() {
		final Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.show_sql", "false");
		hibernateProperties.setProperty("hibernate.format_sql", "false");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		return hibernateProperties;
	}

	@Bean
	PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
		final JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory);
		return transactionManager;
	}

	@Bean
	public CommandLineRunner insertDemoData(WorkRepository workRepository, UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return (args) -> {
			createWorks(workRepository);
			createHairdresser(userRepository, passwordEncoder);
			createClient(userRepository, passwordEncoder);
		};
	}

	private void createClient(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		final Client client = new Client();
		client.setName("client");
		client.setUsername("client");
		client.setPassword(passwordEncoder.encode("client"));
		client.setPhone("+34666666666");
		client.setStatus(UserStatus.ACTIVE);
		userRepository.save(client);
	}

	private void createHairdresser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		final Hairdresser admin = new Hairdresser();
		admin.setName("admin");
		admin.setUsername("admin");
		admin.setPassword(passwordEncoder.encode("admin"));
		admin.setPhone("+34666666666");
		admin.setStatus(UserStatus.ACTIVE);
		createAgenda(admin);
		userRepository.save(admin);
	}

	private void createAgenda(Hairdresser admin) {
		final Stretch morning = new Stretch(LocalTime.of(10, 00), LocalTime.of(14, 00));
		final Stretch afternoon = new Stretch(LocalTime.of(17, 00), LocalTime.of(21, 00));
		final Timetable timetable = new Timetable(LocalDate.now().minusDays(365), LocalDate.now().plusDays(365),
				morning, afternoon);
		final Agenda agenda = new Agenda(admin, timetable);
		agenda.addNonWorkingDay(LocalDate.of(LocalDate.now().getYear(), 01, 01));
		agenda.addNonWorkingDay(LocalDate.of(LocalDate.now().getYear(), 01, 06));
		agenda.addNonWorkingDay(LocalDate.of(LocalDate.now().getYear(), 02, 28));
		agenda.addNonWorkingDay(LocalDate.of(LocalDate.now().getYear(), 8, 15));
	}

	private void createWorks(WorkRepository workRepository) {
		final Work cut = new Work("Corte", Duration.ofMinutes(30), WorkKind.PUBLIC, WorkStatus.ENABLED);
		final Work shave = new Work("Afeitado", Duration.ofMinutes(30), WorkKind.PUBLIC, WorkStatus.ENABLED);
		final Work regulation = new Work("Regulacion", Duration.ofMinutes(30), WorkKind.PRIVATE, WorkStatus.ENABLED);
		workRepository.save(cut);
		workRepository.save(shave);
		workRepository.save(regulation);
	}
}
