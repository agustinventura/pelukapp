package com.spanishcoders.configuration.development;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile("development")
public class DevelopmentSecurityConfiguration extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.headers().frameOptions().disable();
		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.GET, "/").permitAll()
				.antMatchers(HttpMethod.GET, "/*.html").permitAll().antMatchers(HttpMethod.GET, "/static/index.html")
				.permitAll().antMatchers(HttpMethod.POST, "/login").permitAll().antMatchers(HttpMethod.POST, "/client")
				.permitAll().antMatchers("/favicon.ico").permitAll().antMatchers("/h2-console/**").permitAll()
				.antMatchers("/v2/api-docs/**").permitAll().antMatchers("/swagger-resources/**").permitAll()
				.antMatchers("/swagger-ui.html").permitAll().antMatchers("/webjars/**").permitAll().anyRequest()
				.authenticated().and().exceptionHandling().and().anonymous().and().servletApi().and().headers()
				.cacheControl();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Override
	@Bean
	public UserDetailsService userDetailsService() {
		final GrantedAuthority workerAuthority = new SimpleGrantedAuthority("ROLE_WORKER");
		final GrantedAuthority clientAuthority = new SimpleGrantedAuthority("ROLE_CLIENT");
		final UserDetails user1 = new User("admin", "pwd", Arrays.asList(workerAuthority));
		final UserDetails user2 = new User("client", "pwd", Arrays.asList(clientAuthority));
		return new InMemoryUserDetailsManager(Arrays.asList(user1, user2));
	}
}
