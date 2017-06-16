package com.spanishcoders.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.spanishcoders.user.UserServiceFacade;
import com.spanishcoders.user.security.StatelessAuthenticationFilter;
import com.spanishcoders.user.security.StatelessLoginFilter;
import com.spanishcoders.user.security.TokenAuthenticationService;
import com.spanishcoders.user.security.TokenHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile({ "integration", "production" })
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;

	private final UserServiceFacade userServiceFacade;

	@Autowired
	public SecurityConfiguration(@Qualifier("pelukappUserDetailsService") UserDetailsService userDetailsService,
			UserServiceFacade userServiceFacade) {
		this.userDetailsService = userDetailsService;
		this.userServiceFacade = userServiceFacade;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Enable for h2-console access
		http.headers().frameOptions().disable();
		http.csrf().disable().authorizeRequests().antMatchers(HttpMethod.GET, "/").permitAll()
				.antMatchers(HttpMethod.GET, "/*.html").permitAll().antMatchers(HttpMethod.GET, "/static/index.html")
				.permitAll().antMatchers(HttpMethod.POST, "/login").permitAll().antMatchers(HttpMethod.POST, "/client")
				.permitAll().antMatchers("/favicon.ico").permitAll()

				// Enable for h2-console access
				.antMatchers("/h2-console/**").permitAll().antMatchers("/v2/api-docs/**").permitAll()
				.antMatchers("/swagger-resources/**").permitAll().antMatchers("/swagger-ui.html").permitAll()
				.antMatchers("/webjars/**").permitAll().anyRequest().authenticated().and()

				// custom JSON based authentication by POST of
				// {"username":"<name>","password":"<password>"} which sets the
				// token header upon authentication
				.addFilterBefore(new StatelessLoginFilter("/login", tokenAuthenticationService(), userDetailsService,
						authenticationManager()), UsernamePasswordAuthenticationFilter.class)

				// Custom Token based authentication based on the header
				// previously given to the client
				.addFilterBefore(new StatelessAuthenticationFilter(tokenAuthenticationService()),
						UsernamePasswordAuthenticationFilter.class)

				.exceptionHandling().and().anonymous().and().servletApi().and().headers().cacheControl();
	}

	@Override
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
		auth.authenticationProvider(authenticationProvider());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public TokenAuthenticationService tokenAuthenticationService() {
		return new TokenAuthenticationService(tokenHandler(), userServiceFacade);
	}

	@Bean
	public TokenHandler tokenHandler() {
		return new TokenHandler("tooManySecrets", userDetailsService);
	}
}
