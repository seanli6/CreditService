package com.itservice.creditservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;

@SuppressWarnings("deprecation")
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.inMemoryAuthentication().withUser("rbcfolks").password("password").roles("ADMIN");
		auth.inMemoryAuthentication().withUser("sean").password("password2").roles("USER");
	}


	// security based on ROLE
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/**").hasAnyRole("ADMIN").anyRequest().fullyAuthenticated().and()
				.httpBasic();
	}

	@Bean
	public static NoOpPasswordEncoder passwordEncoder() {
		return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance();
	}
}
